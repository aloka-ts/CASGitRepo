package com.baypackets.ase.router.customize.servicenode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;
import javax.servlet.sip.URI;

/**
 * Created by ankitsinghal on 20/05/16.
 */
public class SnApplicationRouterUtil {
	private static final String ORIGNUM_SIG_INFO = "P-SIG-INFO";
	private static final String ORIGNUM_ASSETED_IDENTITY = "P-Asserted-Identity";
	private static final String  DIVERSION_HDR="Diversion";
	private static SnApplicationRouterConfigData configData = null;
	private static Logger logger = Logger.getLogger(SnApplicationRouterUtil.class);

	static final String getRouteInfo(SipServletRequest initialRequest) throws Throwable {
		String ipAddress = initialRequest.getRemoteAddr();
		int port = initialRequest.getRemotePort();
		String routeInfo = ipAddress + "|" + port;
		if (logger.isDebugEnabled()) {
			logger.debug("getRouteInfo()->Normal SIP/SIP-T call Route Info is ::" + routeInfo);
		}
		return routeInfo;
	}

	static void setConfigData(SnApplicationRouterConfigData configurationData) {
		configData = configurationData;
	}

	static String getTerminatingNumber(SipServletRequest initialRequest, SnApplicationRouterConfigData configData) throws Throwable {
		String terminatingNumber = null;
		URI uri = null;
		if(configData.isReadTermFromRURI()){
			if (logger.isDebugEnabled()) {
				logger.debug("getTerminatingNumber()->Reading terminating number form RURI::");
			}
			uri=initialRequest.getRequestURI();
		}else{

			if (logger.isDebugEnabled()) {
				logger.debug("getTerminatingNumber()->Reading terminating number form To header::");
			}
			uri= initialRequest.getTo().getURI();
		}
		SipURI sipUri;
		TelURL telUrl;
		if (uri != null && uri.isSipURI()) {
			sipUri = (SipURI) uri;
			terminatingNumber = sipUri.getUser();

			// check if NPA needs to be applied to terminating number if it is in 
			// NXX-XXXX format.
			if(configData.isNpaDefined() && StringUtils.isNotBlank(terminatingNumber) 
					&& StringUtils.length(terminatingNumber) == 7 ){
				terminatingNumber = configData.getNpaToAppend() + terminatingNumber;
			}
		}else {
			telUrl=(TelURL)uri;
			terminatingNumber=telUrl.getPhoneNumber();
			
			// check if NPA needs to be applied to terminating number if it is in 
						// NXX-XXXX format.
			if(configData.isNpaDefined() && StringUtils.isNotBlank(terminatingNumber) 
					&& StringUtils.length(terminatingNumber) == 7 ){
				terminatingNumber = configData.getNpaToAppend() + terminatingNumber;
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getTerminatingNumber()->Terminating number fetched is "+terminatingNumber);
		}
		//check if TN is null fetch same from request URI
		if (terminatingNumber == null && !configData.getAllowURIWithoutUser()) {
			logger.error("Terminating number is mandatory. Not found. returning null");
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getTerminatingNumber()->Normal SIP/SIP-T call terminating number is:::" + terminatingNumber);
		}
		return terminatingNumber;
	}

	static String getOriginatingNumber(SipServletRequest initialRequest) throws Throwable {
		String originatingNumber = initialRequest.getHeader(ORIGNUM_SIG_INFO);
		//if orig num is null in SIG-INFO then chk in ASSERTED-IDENTITY
		if (originatingNumber == null) {
			Address address = initialRequest.getAddressHeader(ORIGNUM_ASSETED_IDENTITY);

			if (address != null) {
				if (address.getURI().isSipURI()) {
					SipURI paiUri = (SipURI) address.getURI();
					originatingNumber = paiUri.getUser();
				} else {
					String tmp = ((TelURL) address.getURI()).getPhoneNumber();
					tmp = (tmp == null) ? "" : tmp;
					originatingNumber = tmp.split(";")[0];
				}

				if (logger.isDebugEnabled()) {
					logger.debug("getOriginatingNumber()->Pick originating number from PAI ::" + originatingNumber);
				}
			}
			//if orig is still null check from header
			if (originatingNumber == null) {
				URI uri = initialRequest.getFrom().getURI();
				if (uri != null && uri.isSipURI()) {
					SipURI sipUri = (SipURI) uri;
					originatingNumber = sipUri.getUser();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("getOriginatingNumber()->Pick originating number from FROM Header ::" + originatingNumber);
				}
			}
		} else {
			originatingNumber = originatingNumber.substring(originatingNumber.indexOf("=") + 1);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getOriginatingNumber()->Normal SIP/SIP-T call Orig num is ::" + originatingNumber);
		}
		return originatingNumber;
	}
	
	static String getDiversionHeader(SipServletRequest initialRequest) throws Throwable {
		Address address = initialRequest.getAddressHeader(DIVERSION_HDR);
		String divHdr=null;
		if (address != null) {
			if (address.getURI().isSipURI()) {
				SipURI divUri = (SipURI) address.getURI();
				divHdr = divUri.getUser();
			} else {
				String tmp = ((TelURL) address.getURI()).getPhoneNumber();
				tmp = (tmp == null) ? "" : tmp;
				divHdr = tmp.split(";")[0];
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getDiversionHeader()-> ::" + divHdr);
		}
		return divHdr;
	}

	static String preProcessIncomingCLI(String incomingPhoneNumber, SnApplicationRouterConfigData configData) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside preProcessIncomingCLI.. Input incomingPhoneNumber: " + incomingPhoneNumber);
		}
		String processedPhoneNumber = null;
		if (StringUtils.isNotBlank(configData.getDefaultCountryCode())) {
			if (!incomingPhoneNumber.startsWith("+")) {
				//Adding "+" before because default country code always starts with "+"
				if (("+" + incomingPhoneNumber).startsWith(configData.getDefaultCountryCode())) {
					//Incoming phone number is in CCNN format where CC is Default CC. Just preprend + to it
					//to make it +CCNN format
					processedPhoneNumber = "+" + incomingPhoneNumber;
				} else {
					//Add Default CC to the phone number to make it +CCNN format.
					processedPhoneNumber = configData.getDefaultCountryCode() + incomingPhoneNumber;
				}
			}
			if (null == processedPhoneNumber) {
				processedPhoneNumber = incomingPhoneNumber;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Retunring with processedPhoneNumber: " + processedPhoneNumber);
			}
		} else {
			processedPhoneNumber = incomingPhoneNumber;
		}
		return processedPhoneNumber;
	}

	/**
	 * Returns the final application that should be triggered based on appPriorityMap returns the
	 * first service whichever has the highest priority
	 *
	 * @param appPriorityMap     Mapping of ServiceId to ServiceName in the order of Priority
	 *                           defined in ApprouterConfig.xml
	 * @param interestedAppNames All the appId's which are eligible for triggering
	 * @param defaultAppId       Default app that needs to be triggered
	 * @return AppId which should be triggered for the request
	 */
	static final String getPriorityTriggeringApplication(Map<String, TriggerDetails> appPriorityMap,
			Set<String> interestedAppNames,
			String defaultAppId) {
		String triggeringAppId = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Inside getTriggeringApplication() with " +
					"interestedAppNames: " + interestedAppNames +
					", appPriorityMap: " + appPriorityMap);
		}

		if (CollectionUtils.isEmpty(interestedAppNames)) {
			if (StringUtils.isNotBlank(defaultAppId)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Triggering the default app: " + defaultAppId);
				}
				triggeringAppId = defaultAppId;
			} else {
				logger.error("No apps are interested for this message!! ");
			}
		} else if (interestedAppNames.size() == 1) {
			triggeringAppId = (interestedAppNames.toArray()[0]).toString();
			if (logger.isDebugEnabled()) {
				logger.debug("Returning the only interested app as: " + triggeringAppId);
			}
		} else {
			if (MapUtils.isNotEmpty(appPriorityMap)) {
				//Iterate on appPriorityMap, and get the first interestedAppNames matching app
				for (Map.Entry<String, TriggerDetails> entry : appPriorityMap.entrySet()) {
					String appId = entry.getKey();
					String appName = entry.getValue().getApplicationName();
					if (interestedAppNames.contains(appId)) {
						triggeringAppId = appId;
						if (logger.isDebugEnabled()) {
							logger.debug("Found the matching application: " + appId + ":" + appName);
						}
						break;
					}
				}
			} else {
				logger.error("App Priority Map is null in config! " +
						"Multiple Matching applications..." +
						"Can't decide which application to route the call to.. Returning Null!");
				return null;
			}
		}
		return triggeringAppId;
	}

	static final String getDeployedServicesTriggerMapping(Set<String> deployedApplications,
			Map<String, TriggerDetails> appPriorityMap) {
		StringBuilder mappingString = new StringBuilder("");
		if (CollectionUtils.isEmpty(deployedApplications)) {
			logger.error("No Deployed Apps!! Returning null");
			return null;
		}
		if (MapUtils.isEmpty(appPriorityMap)) {
			logger.error("Triggering Mapping is Empty in configuration.. Returning null..");
			return null;
		}
		for (String applicationId : deployedApplications) {
			TriggerDetails appTriggerDetails = appPriorityMap.get(applicationId);

			if (logger.isDebugEnabled()) {
				logger.debug("Find apptriggering details for : " + applicationId);
			}
			if (null == appTriggerDetails) {	
				if(!applicationId.equals("tcap-provider")){
					logger.error("AppTrigger configuration not found for appId: " + applicationId);
				}
			} else {
				String appId = appTriggerDetails.getApplicationId();
				String triggerCriteriaCode = appTriggerDetails.getTriggerCriteriaType().getCriteriaCode();
				mappingString.append(appId).append(":").append(triggerCriteriaCode).append(",");
			}
		}
		//No appropriate mapping found
		if (StringUtils.isBlank(mappingString)) {
			return null;
		} else {
			return StringUtils.removeEnd(mappingString.toString(), ",");
		}
	}

	public static String getDiversionReason(SipServletRequest initialRequest)
			throws Throwable {

		String reason = null;
		Address address = initialRequest.getAddressHeader(DIVERSION_HDR);
		if (address != null) {
			reason = address.getParameter("reason");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getDiversionReason()-> ::" + reason);
		}
		return reason;
	}

}
