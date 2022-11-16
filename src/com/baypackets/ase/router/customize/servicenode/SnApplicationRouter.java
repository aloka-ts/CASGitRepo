package com.baypackets.ase.router.customize.servicenode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRouter;
import javax.servlet.sip.ar.SipApplicationRouterInfo;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;
import javax.servlet.sip.ar.SipRouteModifier;
import javax.servlet.sip.ar.SipTargetedRequestInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.agnity.tc.FeeServiceConnector;
import com.agnity.tc.TriggeringData;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipConstants;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.stpool.AseSharedTokenPool;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class SnApplicationRouter implements SipApplicationRouter {
	private static final String ISC_HEADER = "X-ISC-SVC";
	private static Logger logger = Logger.getLogger(SnApplicationRouter.class);
	private Set<String> deployedApplicationNames = null;
	private Set<String> allowedOptionsApplicationNames=null;

	private SnApplicationRouterDao snApplicationRouterDao = null;
	private static SnApplicationRouterConfigData configData = null;
	private AtomicInteger warmupCount = null;
	AseSharedTokenPool aseStp=null;

	String serveletName = null;
	String serviceName = null;
	String serviceId = null;
	String version =null;
	String adeRoutingEnabled=null;
	private static final int NXX_LENGTH = 7;

	public SnApplicationRouter() {
		super();
	}

	public void destroy() {
	}

	/**
	 * Container notifies application router that new applications are deployed
	 *
	 * @param newlyDeployedApplicationNames - A list of names of the newly added applications
	 */
	public void applicationDeployed(List<String> newlyDeployedApplicationNames) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside: applicationDeployed(). Deploying Apps:::" + newlyDeployedApplicationNames.toString());
		}
		if (newlyDeployedApplicationNames != null) {
			synchronized (deployedApplicationNames) {
				deployedApplicationNames.addAll(newlyDeployedApplicationNames);
			}
		}
	}

	/**
	 * Container notifies application router that some applications are undeployed
	 *
	 * @param undeployedApplicationNames - A list of names of the undeployed applications
	 */
	public void applicationUndeployed(List<String> undeployedApplicationNames) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside: applicationUndeployed(). UnDeploying Apps:::" + undeployedApplicationNames.toString());
		}
		if (undeployedApplicationNames != null) {
			synchronized (deployedApplicationNames) {
				deployedApplicationNames.removeAll(undeployedApplicationNames);
			}

		}
	}

	/**
	 * Returns deployed application
	 * @return
	 */
	public String getAnyDeployedApplication() {
		if (logger.isInfoEnabled()) {
			logger.info("Inside: getAnyDeployedApplication()");
		}
		String appId = null;
// deployedApplicationNames  should be in list of allowedOptionsApplicationNames then only it should be allowed 
		if(deployedApplicationNames != null ) {
			for (String applicationId : deployedApplicationNames) {	 
				if(!applicationId.equals("tcap-provider")  && allowedOptionsApplicationNames.contains(applicationId)){
					appId = applicationId;
					break;
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Inside getAnyDeployedApplication: appId retured: " + appId);
		}
		return appId;
	}

	/**
	 * Get next application method called by dispatcher to get the next application information.It
	 * will return null is there is no next application defined in Router's repository
	 *
	 * if incoming message 1. checks if tcap call then return tcap-provider name(configured in XML)
	 * in SipApplicationRouterInfo 2. checks if correlation call then return correlated app
	 * name(configured in XML)in SipApplicationRouterInfo 3. else return application name returned
	 * form dao in SipApplicationRouterInfo else if outgoing message 1. check if ISC(ISC header in
	 * invite) then returns app name(present in ISC header) in SipApplicationRouterInfo 2. else
	 * returns null;
	 *
	 * @param initialRequest - The initial request for which the container is asking for application
	 *                       selection. The request must not be modified by the AR. It is
	 *                       recommended that the implementations explicitly disallow any mutation
	 *                       action by throwing appropriate RuntimeException like
	 *                       IllegalStateException.
	 * @param region         - Which region the application selection process is in
	 * @param directive      - The routing directive used in creating this request. If this is a
	 *                       request received externally, directive is NEW.
	 * @param stateInfo      - If this request is relayed from a previous request by an application,
	 *                       this is the stored state the application router returned earlier when
	 *                       invoked to handle the previous request.
	 * @return Next application routing info.
	 */
	public SipApplicationRouterInfo getNextApplication(
			SipServletRequest initialRequest,
			SipApplicationRoutingRegion region,
			SipApplicationRoutingDirective directive,
			SipTargetedRequestInfo targettedRequest, Serializable stateInfo) {
		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isInfoEnabled) {
			logger.info("Inside SnApplicationRouter getNextApplication() ");
		}
		String appName = null;
		try {
			if (initialRequest != null) {
				if (isDebugEnabled) {
					logger.debug("getNextApplication()->Initial request not null Method::" + initialRequest.getMethod());
				}
				int reqSrc = ((AseSipServletRequest) initialRequest).getSource();
				if (reqSrc != AseSipConstants.SRC_SERVLET) {
					if (isDebugEnabled) {
						logger.debug("getNextApplication()->Initial request from Network");
					}
					URI reqUri = null;
					SipURI sipReqUri = null;
					String sipReqUser = null;
					TelURL telurl = null;
					reqUri = initialRequest.getRequestURI();
					if (reqUri != null) {
						if(reqUri.isSipURI()) {
							sipReqUri = (SipURI) reqUri;
	                        sipReqUser = sipReqUri.getUser();
	                    }else {
	                    	telurl =(TelURL)reqUri;
	                    	sipReqUser=telurl.getPhoneNumber();
	                    }
						
						try {
							if (isDebugEnabled) {
								logger.debug("getNextApplication()->Initial request from Network : sipReqUser "+sipReqUser);
							}
							if(sipReqUser != null){
								StringTokenizer userTokens = new StringTokenizer(sipReqUser, AseStrings.SEMI_COLON);
								sipReqUser = userTokens.nextToken();
								
								if (isDebugEnabled) {
									logger.debug("getNextApplication()->Initial request from Network : sipReqUser after token "+sipReqUser);
								}
							}
							
						} catch (Exception e) {
							logger.error("Exception in getNextApplication()->Tokenizer error", e);
						}    
					}

					// apply NPA check 
					if(sipReqUser != null && StringUtils.length(sipReqUser) == NXX_LENGTH && configData.isNpaDefined()){
						sipReqUser = configData.getNpaToAppend() + sipReqUser;

						((SipURI)initialRequest.getRequestURI()).setUser(sipReqUser);
						if(logger.isDebugEnabled()){
							logger.debug("SipRequestUser after appending NPA: " + sipReqUser + ", NPA:" + configData.getNpaToAppend() );
							logger.debug("modified sipReqUser : " + ((SipURI)initialRequest.getRequestURI()).getUser());
						}
					}

					// handling of SIP Options. sipReqUser can be null in case of Option. 
					if(initialRequest.getMethod().equals(AseStrings.OPTIONS) && configData.isSipOptionHbHandlingEnabled() ){
						// check if any application will handle SIP Option. if not then use the defined 
						// app name
						logger.debug("Options request encountered  in SnApplicationRouter");
						if(!configData.isAnyAppWillHandleOption()){
							appName = configData.getAppIdToHandleSipOption();
						}else{
							// it means any application will handle SIP Option. 
							// fetch any deployed application, set its id
							appName = getAnyDeployedApplication();
						}

						if(logger.isDebugEnabled()){
							logger.debug("Handling SIP Option, passing to appId:" + appName);
						}
						return prepareSipAppRouterInfo(appName);
					}

					if(sipReqUser==null && !configData.getAllowURIWithoutUser()){
						logger.error("Invalid request. req User not found");
						return null;
					}

					appName = checkWarmup(initialRequest);
					if (appName != null) {
						logger.error("This is warmup request for application:::" + appName);
						return prepareSipAppRouterInfo(appName);
					}

					//check if Tcap/Inap call
					if( (sipReqUser!=null && sipReqUser.toLowerCase().startsWith(configData.getTcapCallIdentifier().toLowerCase())) &&
							(initialRequest.getMethod().equals(AseStrings.NOTIFY))) {
						//TCAP call for tpg
						if (isDebugEnabled) {
							logger.debug("getNextApplication()->INAP call");
						}
						appName = configData.getTcapProviderName();
						if (isDebugEnabled) {
							logger.debug("getNextApplication()->INAP AppName is ::[" + appName + "]");
						}
					} else if(sipReqUser!=null && (appName=checkForCorrelatedCall(sipReqUser.toLowerCase()))!=null){

						if (isDebugEnabled) {
							logger.debug("getNextApplication()->Handoff/assist AppName is ::[" + appName + "]");
						}
						initialRequest.setAttribute(Constants.CORRELATION_ID_ATTRIBUTE, sipReqUser.toLowerCase());

					} else if (sipReqUser!=null && sipReqUser.toLowerCase().startsWith(configData.getCorrIdUrlStart().toLowerCase())) { 

						//handoff allowed only on INvites..

						Map<String, String> handOffAssistMap = configData.getHandOffAssistMap();

						if (initialRequest.getMethod().equals(AseStrings.INVITE)) {
							//hand off scenario
							if (isDebugEnabled) {
								logger.debug("getNextApplication()->HandOff or Assist");
							}
							//Changes to comply the request URI as per RFC 3261 for relation to tel uri
							//For instance, tel:+358-555-1234567;postd=pp22 becomes
							//sip:+358-555-1234567;postd=pp22@foo.com;user=phone
							String corrId = null;
							if (!(configData.getCorrIdUrlStart().trim().isEmpty())) {
								StringTokenizer userTokens = new StringTokenizer(sipReqUser, AseStrings.SEMI_COLON);
								String firstToken = userTokens.nextToken();
								int startIndex = (firstToken.length() - configData.getCorrLength());
								int endIndex = startIndex + (configData.getCorrLength());
								if (handOffAssistMap == null || handOffAssistMap.isEmpty() ||
										startIndex < 0 || endIndex <= startIndex) {
									logger.error("Error in HandOff/assist details configuration return null " +
											"reqUser::[" + sipReqUser + "] startIndex::[" + startIndex + "] endIndex::[" +
											endIndex + "]  HandOFffMAp::+[" + handOffAssistMap + AseStrings.SQUARE_BRACKET_CLOSE);
									return null;
								}
								corrId = sipReqUser.substring(startIndex, endIndex);
							} else {
								corrId = sipReqUser;
							}
							//if corrID is not found it cannot be hand off
							if (corrId == null) {
								logger.error("Hand off/assist but corrId is null:: return null");
								return null;
							}
							if (isDebugEnabled) {
								logger.debug("getNextApplication()->Corrid is::[" + corrId + AseStrings.SQUARE_BRACKET_CLOSE);
							}
							Set<String> corrIdBeginSet = handOffAssistMap.keySet();
							Iterator<String> corrIdBeginIterator = corrIdBeginSet.iterator();
							String corrIdBegin = null;
							boolean notFound = true;
							//iterating begin string to check if app matches. exit on first match
							while (notFound && (corrIdBeginIterator.hasNext())) {
								corrIdBegin = corrIdBeginIterator.next();
								if (corrId.startsWith(corrIdBegin)) {
									appName = handOffAssistMap.get(corrIdBegin);
									//Adding proprietry headers
									initialRequest.setAttribute(Constants.CORRELATION_ID_ATTRIBUTE, corrId);
									//exit while loop
									notFound = false;
								}
							}
							if (!notFound) {
								if (isDebugEnabled) {
									logger.debug("getNextApplication()->Handoff/assist AppName is ::[" + appName + AseStrings.SQUARE_BRACKET_CLOSE);
								}
							} else if (configData.getCorrIdUrlStart().trim().isEmpty()) {
								if (isDebugEnabled) {
									logger.debug("Call Not Matched any HandOff Assist Rule so treating as normal sip call flow.");
								}
								appName = findApplicationForNormalRequest(initialRequest);
							} else {
								//if found appname its already set here else it's null
								if (isDebugEnabled) {
									logger.debug("Call Not Matched any HandOff Assist Rule so returning appName:" + appName);
								}
							}
						}
					} else {
						appName = findApplicationForNormalRequest(initialRequest);
					}
				} else {
					if (isDebugEnabled) {
						logger.debug("getNextApplication()->Initial request from Servlet");
					}
					//check if INvite or notify. ISC only for INVITE
					if (initialRequest.getMethod().equals(AseStrings.INVITE)) {
						if (isDebugEnabled) {
							logger.debug("getNextApplication()->INVite request from Servlet checking ISC");
						}
						//invite check ISC
						String iscHeader = initialRequest.getHeader(ISC_HEADER);
						//checking if isc needed
						if (iscHeader != null) {
							if (isDebugEnabled) {
								logger.debug("getNextApplication()->ISC call");
							}
							//changed as per new ISC logic DD v0.4
							appName = iscHeader;
							if (isDebugEnabled) {
								logger.debug("getNextApplication()->ISC AppName is ::[" + appName + "]");
							}
						}
					} else {
						if (isDebugEnabled) {
							logger.debug("getNextApplication()->Notify request from Servlet");
						}
						return null;
					}
				}
				//return SipApprouterInfo if found app else return null
				if (appName != null) {
					// If application name returned is equal to application that
					// initiated this session select another entry
					// This is done to remove looping
					SipSession session = initialRequest.getSession(false);
					String previousApp = null;
					if (session != null) {
						previousApp = session.getApplicationSession().getApplicationName();
						if (previousApp.equals(appName)) {
							if (isInfoEnabled) {
								logger.info("getNextApplication()--> Loop detected,Previous App is same as new app, returning null");
							}
							return null;
						}
					}
					if (isInfoEnabled) {
						logger.info("Leave getNextApplication()->SUccess returning Approuter INfo ");
					}
					return prepareSipAppRouterInfo(appName);
				} else {
					if (reqSrc != AseSipConstants.SRC_SERVLET){
						logger.error("getNextApplication()->AppName Not found. When Request received from Network");
					}
					return null;
				}
			}
		} catch (Throwable e) {
			logger.error("Exception raised in AppRouter:", e);
		}

		//this line will be exexcuted if request is null or excpetion is caught
		logger.warn("Leave getNextApplication() null initial request or exception ");
		return null;

	}

	private String findApplicationForNormalRequest(SipServletRequest initialRequest) throws Throwable {
		boolean isDebugEnabled = logger.isDebugEnabled();
		String triggerAppName = null;
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->Normal SIP/SIP-T call Handling");
		}
		//if dao is null retry
		if (snApplicationRouterDao == null || !snApplicationRouterDao.isInitialized()) {
			if (isDebugEnabled) {
				logger.debug("dao ref is null retrying");
			}
			this.loadDao();
			//if dao still null then return
			if (snApplicationRouterDao == null) {
				logger.error("Unable to find DAO ref");
				return null;
			}
		}

		String terminatingNumber = SnApplicationRouterUtil.getTerminatingNumber(initialRequest,configData);
		if(terminatingNumber == null){
			if (isDebugEnabled) {
				logger.debug("terminatingNumber is null so making it empty");
			}
			terminatingNumber = "";
		}
		String originatingNumber = SnApplicationRouterUtil.getOriginatingNumber(initialRequest);

		String divHdr = SnApplicationRouterUtil.getDiversionHeader(initialRequest);
		
		String divReason = SnApplicationRouterUtil.getDiversionReason(initialRequest);

		if(originatingNumber == null){
			if (isDebugEnabled) {
				logger.debug("originatingNumber is null so making it empty");
			}
			originatingNumber = "";
		}
		
		//Update the originatingNumber with default CC for matching in DB table "ADDRESS"
		originatingNumber = SnApplicationRouterUtil.preProcessIncomingCLI(originatingNumber, configData);
		String routeInfo = SnApplicationRouterUtil.getRouteInfo(initialRequest);
		String serviceTriggerMapping = SnApplicationRouterUtil.getDeployedServicesTriggerMapping(
				deployedApplicationNames,
				configData.getAppTriggerPriorityMap());

		if (null != serviceTriggerMapping) {
			Set<String> interestedAppNames =null;
//			if(divHdr!=null){
//			interestedAppNames = snApplicationRouterDao.findInterestedApplicationNames(
//					terminatingNumber,
//					originatingNumber,
//					divHdr,
//					divReason,
//					routeInfo,
//					serviceTriggerMapping);
//			}else{
			interestedAppNames = snApplicationRouterDao.findInterestedApplicationNames(
						terminatingNumber,
						originatingNumber,
						routeInfo,
						serviceTriggerMapping);
			//}
			triggerAppName = SnApplicationRouterUtil.getPriorityTriggeringApplication(
					configData.getAppTriggerPriorityMap(),
					interestedAppNames,
					configData.getDefaultApp());
		}
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->SIP-T AppName is ::[" + triggerAppName + "]");
		}
		return triggerAppName;
	}

	
	/**
	 * This method is used  by appchain manager to get next interested service as per priority of service
	 * @param terminatingNumber
	 * @param originatingNumber
	 * @param originInfo
	 * @return
	 * @throws Throwable
	 */
	public String findApplicationForNormalRequest(String terminatingNumber, String originatingNumber, String originInfo, Set<String> excludeServices, String triggerCriteria) throws Throwable {
		boolean isDebugEnabled = logger.isDebugEnabled();
		String triggerAppName = null;
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->Normal SIP/SIP-T call Handling exclude services provided "+excludeServices+ " :triggerCriteria: "+triggerCriteria);
		}
		//if dao is null retry
		if (snApplicationRouterDao == null || !snApplicationRouterDao.isInitialized()) {
			if (isDebugEnabled) {
				logger.debug("dao ref is null retrying");
			}
			this.loadDao();
			//if dao still null then return
			if (snApplicationRouterDao == null) {
				logger.error("Unable to find DAO ref");
				return null;
			}
		}

		//Update the originatingNumber with default CC for matching in DB table "ADDRESS"
		originatingNumber = SnApplicationRouterUtil.preProcessIncomingCLI(originatingNumber, configData);
		String routeInfo = originInfo;//SnApplicationRouterUtil.getRouteInfo(initialRequest);
		String serviceTriggerMapping = SnApplicationRouterUtil.getDeployedServicesTriggerMapping(
				deployedApplicationNames,
				configData.getAppTriggerPriorityMap());

		if (null != serviceTriggerMapping) {
			Set<String> interestedAppNames = snApplicationRouterDao.findInterestedApplicationNamesWithTC(
					terminatingNumber,
					originatingNumber,
					routeInfo,
					serviceTriggerMapping, triggerCriteria);

			/**
			 * remove the exclude list from interested apps
			 */
			if (excludeServices != null) {
				if (isDebugEnabled) {
					logger.debug("findApplicationForNormalRequest()-> remove already triggred services..."+excludeServices);
				}
				interestedAppNames.removeAll(excludeServices);
			}

			triggerAppName = SnApplicationRouterUtil.getPriorityTriggeringApplication(
					configData.getAppTriggerPriorityMap(),
					interestedAppNames,
					configData.getDefaultApp());
		}
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->SIP-T AppName is ::[" + triggerAppName + "]");
		}
		return triggerAppName;
	}
	
	/**
	 * This method is used  by appchain manager to get next interested service as per priority of service
	 * @param terminatingNumber
	 * @param originatingNumber
	 * @param originInfo
	 * @return
	 * @throws Throwable
	 */
	public String findApplicationForNormalRequest(String terminatingNumber, String originatingNumber, String originInfo, Set<String> excludeServices) throws Throwable {
		boolean isDebugEnabled = logger.isDebugEnabled();
		String triggerAppName = null;
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->Normal SIP/SIP-T call Handling exclude services provided "+excludeServices);
		}
		//if dao is null retry
		if (snApplicationRouterDao == null || !snApplicationRouterDao.isInitialized()) {
			if (isDebugEnabled) {
				logger.debug("dao ref is null retrying");
			}
			this.loadDao();
			//if dao still null then return
			if (snApplicationRouterDao == null) {
				logger.error("Unable to find DAO ref");
				return null;
			}
		}

		//Update the originatingNumber with default CC for matching in DB table "ADDRESS"
		originatingNumber = SnApplicationRouterUtil.preProcessIncomingCLI(originatingNumber, configData);
		String routeInfo = originInfo;//SnApplicationRouterUtil.getRouteInfo(initialRequest);
		String serviceTriggerMapping = SnApplicationRouterUtil.getDeployedServicesTriggerMapping(
				deployedApplicationNames,
				configData.getAppTriggerPriorityMap());

		if (null != serviceTriggerMapping) {
			Set<String> interestedAppNames = snApplicationRouterDao.findInterestedApplicationNames(
					terminatingNumber,
					originatingNumber,
					routeInfo,
					serviceTriggerMapping);

			/**
			 * remove the exclude list from interested apps
			 */
			if (excludeServices != null) {
				if (isDebugEnabled) {
					logger.debug("findApplicationForNormalRequest()-> remove already triggred services..."+excludeServices);
				}
				interestedAppNames.removeAll(excludeServices);
			}

			triggerAppName = SnApplicationRouterUtil.getPriorityTriggeringApplication(
					configData.getAppTriggerPriorityMap(),
					interestedAppNames,
					configData.getDefaultApp());
		}
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequest()->SIP-T AppName is ::[" + triggerAppName + "]");
		}
		return triggerAppName;
	}
	
	


	private String checkWarmup(SipServletRequest initialRequest) {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter Warmup");
		}
		if (warmupCount == null || warmupCount.get() == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Warmup Message Limit Reached");
			}
			return null;
		}
		String app = initialRequest.getHeader("WARMUP");
		if (app != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Warmup app found as::" + app);
			}
			if (warmupCount.decrementAndGet() == 0) {
				warmupCount = null;
			}
			return app;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Warmup app No app found");
			}
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see javax.servlet.sip.ar.SipApplicationRouter#init(java.util.Properties)
	 */
	@Override
	public void init(Properties paramProperties) {
		logger.error("insisde init with properties()" + paramProperties);
		String reInitProp = (String) paramProperties.get("WARMUP");
		if (reInitProp != null && reInitProp.equals(AseStrings.TRUE_SMALL)) {
			//get warmup Msg Count
			Properties prop = new Properties();
			String fileName = System.getProperty("ase.home") + "/conf/msgbuffer.dat";
			try {
				InputStream is = new FileInputStream(fileName);
				prop.load(is);
			} catch (FileNotFoundException e) {
				logger.warn(e);
			} catch (IOException e) {
				logger.warn(e);
			}
			//reading properties
			String messageString = prop.getProperty("warmup.messages.hexMessageString");
			if (messageString != null) {
				warmupCount = new AtomicInteger(messageString.split(",").length);
			}
			this.loadDao();
			snApplicationRouterDao.warmUp();

			aseStp=(AseSharedTokenPool) Registry.lookup(Constants.NAME_SHARED_TOKEN_POOL);
		}
		//do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.sip.ar.SipApplicationRouter#init()
	 */
	@Override
	public void init() {
		logger.error("insisde init()");
		if (logger.isInfoEnabled()) {
			logger.info("Initializing SnApplicationRouter()");
		}
		this.deployedApplicationNames = new HashSet<String>();
		this.allowedOptionsApplicationNames= new HashSet<String>();
		this.loadConfiguration();

		if (logger.isInfoEnabled()) {
			logger.info("Initialization Complete for approuter ");
		}
	}

	private SipApplicationRouterInfo prepareSipAppRouterInfo(String appName) {

		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isInfoEnabled) {
			logger.info("Inside SnApplicationRouter prepareSipAppRouterInfo() ");
		}
		boolean isApplicationPresentInContainer = false;

		if (appName != null) {
			synchronized (deployedApplicationNames) {
				// check if application is deployed in the container
				if (deployedApplicationNames.contains(appName)) {
					isApplicationPresentInContainer = true;
					if (isDebugEnabled) {
						logger.debug("Application " + appName + " is deployed in the container");
					}
				} else {
					if (isInfoEnabled) {
						logger.info("Application " + appName + " is not deployed in the container");
					}
				}
			}
		}
		SipRouteModifier routeModifier = SipRouteModifier.valueOf(SipRouteModifier.class, "NO_ROUTE");
		if (isApplicationPresentInContainer) {
			return new SipApplicationRouterInfo(appName, null, null, null, routeModifier, null);
		} else {
			logger.error("Application " + appName + " is not deployed in the container.Sending 503 response.");
			return null;
		}
	}


	public void loadDao() {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled) {
			logger.debug("Inside SnApplicationRouter loadDao()");
		}
		String procName = configData.getDbProcedureName();
		if (procName == null || procName.trim().equals(AseStrings.BLANK_STRING)) {
			logger.error("Proc name is null or empty for AR::[" + procName + "]");
			return;
		}
		try {
			snApplicationRouterDao = SnApplicationRouterDaoImpl.getInstance();
			snApplicationRouterDao.init(procName);
		} catch (Exception e) {
			logger.error("Error loading Dao config for Approuter", e);
			snApplicationRouterDao = null;
		}

		if (isDebugEnabled) {
			logger.debug("Leaving SnApplicationRouter loadDao()");
		}
	}

	/**
	 * reads config XML reads SIPT proc name freom config repository
	 */
	private void loadConfiguration() {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled) {
			logger.debug("Inside SnApplicationRouter loadConfiguration()->loading XML file");
		}
		//loading xml
		SnApplicationRouterConfigReader configReader = new SnApplicationRouterConfigReader();
		configData = configReader.readConfigXml();
		SnApplicationRouterUtil.setConfigData(configData);
		if (isDebugEnabled) {
			logger.debug("Inside SnApplicationRouter loadConfiguration()->getting values for apps allowed for options");
		}

		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String allowedApps = configRep.getValue(Constants.APP_ROUTER_OPTIONS_HANDLING_APPS);
		logger.debug("Allowed apps in options method :- "+ allowedApps);
		if(StringUtils.isNotBlank(allowedApps)) {
			if(allowedApps.contains(AseStrings.COMMA)) {
				StringTokenizer tokenizer = new StringTokenizer(allowedApps, AseStrings.COMMA);
				while(tokenizer.hasMoreTokens()) {
					allowedOptionsApplicationNames.add(tokenizer.nextToken());
				}
			}else {
				allowedOptionsApplicationNames.add(allowedApps);
			}
		}


		//UAT-1435 The INAP call was failed in Main lab
		//This code is introduced to have inject the necessary configuration for
		//correlation id, so that it can be used while routing the SIP call on
		//the same thread as of INAP call.
		AseUtils.corrIdUrlStart = configData.getCorrIdUrlStart().toLowerCase();
		AseUtils.corrLength = configData.getCorrLength();
		if (isDebugEnabled) {
			logger.debug("Inside SnApplicationRouter loadConfiguration()->load Complete");
		}
		
		
		logger.info("starting load  properties file" + Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES);
		if (logger.isDebugEnabled()) {
			logger.debug("findApplicationForNormalRequestV2 : ");
		}

		String filepath = Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES;
	//	Properties redisP = AseUtils.getProperties(filepath);
		InputStream in = null;
		try {
			 in = Files.newInputStream(Paths.get(filepath));
		
		Yaml yaml = new Yaml();
		TreeMap<String, String> oemsAgentProperties = yaml.loadAs(in, TreeMap.class);

		adeRoutingEnabled = oemsAgentProperties.get("app.routing.ade.enabled");
	

		logger.info("adeRoutingEnabled:"+ adeRoutingEnabled);
		
		String triggerAppName = null;
		if (isDebugEnabled) {
			logger.debug(
					"loadConfiguration");
		}

			if(adeRoutingEnabled.equals("true")) {
				serveletName = oemsAgentProperties.get("app.routing.ade.servlet.name");
				serviceName = oemsAgentProperties.get("app.routing.ade.service.name");
				serviceId = oemsAgentProperties.get("app.routing.ade.service.id");
				version = oemsAgentProperties.get("app.routing.ade.service.version");
			}
		} catch (Exception e) {
			logger.error("exception occured:" + e);
		}finally{
			try{
				if (isDebugEnabled) {
					logger.debug(
							"close the input steam");
				}
			in.close();
			}catch(IOException io){
				logger.error(" eror while closing input stream");
			}
		}
	}

	/**
	 * @param SnApplicationRouterDao the SnApplicationRouterDao to set
	 */
	protected void setSnApplicationRouterDao(SnApplicationRouterDao snApplicationRouterDao) {
		this.snApplicationRouterDao = snApplicationRouterDao;
	}

	public static SnApplicationRouterConfigData getConfigData() {
		return configData;
	}

	/**
	 * @param configData the configData to set
	 */
	protected void setConfigData(SnApplicationRouterConfigData configData) {
		this.configData = configData;
	}

	protected String checkForCorrelatedCall(String sipReqUser) {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside checkForCorrelatedCall for sipReqUser "
					+ sipReqUser);
		}
		boolean useSTC = configData.IsUseSharedTokenCorrelation();

		String serviceId=null;
		if (useSTC) {
			if (aseStp != null) {
				serviceId= aseStp.getServiceIdForTokenValue(sipReqUser);
				if(serviceId!=null){
					if (logger.isDebugEnabled()) {
						logger.debug("Inside checkForCorrelatedCall serviceId  found  from AseStp so this is a correlated call..");
					}
				}else{
					if (logger.isDebugEnabled()) {
						logger.debug("Inside checkForCorrelatedCall serviceId not found  from AseStp so not a correlated call..");
					}
				}
				return serviceId;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Inside checkForCorrelatedCall for sipReqUser-> token pool not enabled");
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Not a shared token pool correlated call");
		}
		return null;
	}
	
	public String findApplicationForNormalRequestV2(String terminatingNumber, String originatingNumber,
			String originInfo, Set<String> excludeServices, String triggerCriteria, TriggeringData triggeringData)
			throws Throwable {
		
//		logger.info("starting load  properties file" + Constants.ASE_HOME + "/"
//				+ Constants.FILE_CAS_STARUP_PROPERTIES);
//		if (logger.isDebugEnabled()) {
//			logger.debug("findApplicationForNormalRequestV2 : ");
//		}
//
//		String filepath = Constants.ASE_HOME + "/"
//				+ Constants.FILE_CAS_STARUP_PROPERTIES;
//	//	Properties redisP = AseUtils.getProperties(filepath);
//		InputStream in = null;
//		try {
//			 in = Files.newInputStream(Paths.get(filepath));
//		} catch (Exception e) {
//			logger.error("error while readinf file");
//		}
//		
//		Yaml yaml = new Yaml();
//		TreeMap<String, String> oemsAgentProperties = yaml.loadAs(in, TreeMap.class);
//
//		String adeRoutingEnabled = oemsAgentProperties.get("app.routing.ade.enabled");
//	
//
//		logger.info("adeRoutingEnabled:"+ adeRoutingEnabled);
//		
		boolean isDebugEnabled = logger.isDebugEnabled();
		String triggerAppName = null;
//		if (isDebugEnabled) {
//			logger.debug(
//					"findApplicationForNormalRequestV2()->Normal SIP/SIP-T call Handling exclude services provided "
//							+ excludeServices + " :triggerCriteria: " + " triggerCriteria :" + triggeringData);
//		}
//
//		try {
			if(adeRoutingEnabled.equals("true")) {
				FeeServiceConnector feeServiceConnector = FeeServiceConnector.getInstance();
				String appName = feeServiceConnector.initFeeService(serveletName,
						serviceName, serviceId,
						version, triggeringData);
				if (isDebugEnabled) {
					logger.debug("appName by findApplicationForNormalRequestV2:" + appName);
				}
				if (appName !=null ) {
					return appName;
				}
			}
			
			
//		} catch (Exception e) {
//			logger.error("exception occured:" + e);
//		}finally{
//			in.close();
//		}

		if (isDebugEnabled) {
			logger.debug("loading dao");
		}

		// if dao is null retry
		if (snApplicationRouterDao == null || !snApplicationRouterDao.isInitialized()) {
			if (isDebugEnabled) {
				logger.debug("dao ref is null retrying");
			}
			this.loadDao();
			// if dao still null then return
			if (snApplicationRouterDao == null) {
				logger.error("Unable to find DAO ref");
				return null;
			}
		}

		// Update the originatingNumber with default CC for matching in DB table
		// "ADDRESS"
		originatingNumber = SnApplicationRouterUtil.preProcessIncomingCLI(originatingNumber, configData);
		String routeInfo = originInfo;
		String serviceTriggerMapping = SnApplicationRouterUtil
				.getDeployedServicesTriggerMapping(deployedApplicationNames, configData.getAppTriggerPriorityMap());

		if (null != serviceTriggerMapping) {
			Set<String> interestedAppNames = snApplicationRouterDao
					.findInterestedApplicationNamesWithTC(terminatingNumber, originatingNumber,
							routeInfo, serviceTriggerMapping, triggerCriteria);

			/**
			 * remove the exclude list from interested apps
			 */
			if (excludeServices != null) {
				if (isDebugEnabled) {
					logger.debug("findApplicationForNormalRequestV2()-> remove already triggred services..."
							+ excludeServices);
				}
				interestedAppNames.removeAll(excludeServices);
			}

			triggerAppName = SnApplicationRouterUtil.getPriorityTriggeringApplication(
					configData.getAppTriggerPriorityMap(), interestedAppNames, configData.getDefaultApp());
		}
		if (isDebugEnabled) {
			logger.debug("findApplicationForNormalRequestV2()->SIP-T AppName is ::[" + triggerAppName + "]");
		}
		return triggerAppName;
	}

}