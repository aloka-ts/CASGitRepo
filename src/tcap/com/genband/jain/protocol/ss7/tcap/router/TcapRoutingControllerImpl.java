package com.genband.jain.protocol.ss7.tcap.router;


import java.util.Arrays;

import javax.servlet.sip.ar.SipApplicationRouter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bn.exceptions.EnumParamOutOfRangeException;

import com.agnity.ain.asngenerated.Carrier;
import com.agnity.ain.asngenerated.InfoAnalyzedArg;
import com.agnity.ain.asngenerated.InfoCollectedArg;
import com.agnity.ain.asngenerated.NetworkBusyArg;
import com.agnity.ain.asngenerated.TerminationAttemptArg;
import com.agnity.ain.asngenerated.TriggerCriteriaType.TriggerCriteriaTypeEnumType;
import com.agnity.ain.asngenerated.TrunkGroupID;
import com.agnity.ain.asngenerated.UserID;
import com.agnity.ain.datatypes.AinDigits;
import com.agnity.ain.datatypes.CarrierFormat;
import com.agnity.ain.enumdata.CalgNatOfNumEnum;
import com.agnity.ain.enumdata.CalledNatOfNumEnum;
import com.agnity.ain.enumdata.NumPlanEnum;
import com.agnity.ain.operations.AinOperationsCoding;
import com.agnity.ain.util.Constant;
import com.agnity.inapitutcs2.asngenerated.InitialDPArg;
import com.agnity.inapitutcs2.datatypes.CalledPartyNum;
import com.agnity.inapitutcs2.datatypes.CallingPartyNum;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.operations.InapOperationsCoding;
import com.agnity.mphdata.common.CarrierInfo;
import com.agnity.mphdata.common.PhoneNumber;
import com.agnity.tc.TriggeringData;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.router.AseSipApplicationRouterManager;
import com.baypackets.ase.router.customize.servicenode.SnApplicationRouter;
import com.baypackets.ase.router.customize.servicenode.SnApplicationRouterConfigData;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

import jain.ASNParsingException;
import jain.CriticalityTypeException;
import jain.MandatoryParamMissingException;
import jain.MandatoryParamMissingException.MISSINGPARAM;
import jain.ParameterNotSetException;
import jain.ParameterOutOfRangeException;
import jain.ParameterOutOfRangeException.PARAM_NAME;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

public class TcapRoutingControllerImpl implements TcapRoutingController, MComponent {

	private static Logger						logger						= Logger
			.getLogger(TcapRoutingControllerImpl.class);

	private static TcapRoutingControllerImpl	tcapRoutingControllerImpl;

	private TcapRoutingControllerDao			tcapRoutingControllerDao	= null;

	private boolean								parseFullIdp				= false;

	private String								procName					= null;

	private static final String					OID_TCAP_AR_PARSE_FULL_IDP	= new String("30.1.74");

	public static final String					TCAP_ROUTING_PROC_NAME		= "routing.proc.name";

	private ConfigRepository					configRep					= null;

	private static boolean isInitialized =false; 

	private class ParsedExtensionResult {
		private String	originatingNumber;
		private String	terminatingNumber;

		public ParsedExtensionResult(String originatingNumber, String terminatingNumber) {
			setOriginatingNumber(originatingNumber);
			setTerminatingNumber(terminatingNumber);
		}

		/**
		 * @param originatingNumber
		 *            the originatingNumber to set
		 */
		public void setOriginatingNumber(String originatingNumber) {
			this.originatingNumber = originatingNumber;
		}

		/**
		 * @return the originatingNumber
		 */
		public String getOriginatingNumber() {
			return originatingNumber;
		}

		/**
		 * @param terminatingNumber
		 *            the terminatingNumber to set
		 */
		public void setTerminatingNumber(String terminatingNumber) {
			this.terminatingNumber = terminatingNumber;
		}

		/**
		 * @return the terminatingNumber
		 */
		public String getTerminatingNumber() {
			return terminatingNumber;
		}
	}

	/**
	 * used for mocking
	 * 
	 * @param tcapRoutingControllerDao
	 *            the tcapRoutingControllerDao to set
	 */
	protected void setTcapRoutingControllerDao(TcapRoutingControllerDao tcapRoutingControllerDao) {
		this.tcapRoutingControllerDao = tcapRoutingControllerDao;
	}

	/**
	 * Instantiates a TCAPRoutingControllerImpl.
	 */
	public TcapRoutingControllerImpl() {
		if (logger.isInfoEnabled())
			logger.info("Inside TCAPRoutingControllerImpl");
		tcapRoutingControllerImpl = this;

	}

	/**
	 * Instantiates a TCAPRoutingControllerImpl.
	 */
	public void init() {
		if (logger.isInfoEnabled())
			logger.info("Inside init()");
		tcapRoutingControllerImpl = this;
		configRep = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		this.readParsingRule();
		this.readProcedureName();
		if (procName == null || procName.trim().equals("")) {
			logger.error("Proc name is null or empty for Tcap Utility::[" + procName + "]");
			return;
		}

		if (parseFullIdp) {
			try {
				tcapRoutingControllerDao = TcapRoutingControllerDaoImpl.getInstance();
				tcapRoutingControllerDao.init(procName);
			} catch (Exception e) {
				logger.error("Exception getting Dao Object", e);
				throw new RuntimeException("Unable to get DAO", e);
			}
		}
		if (logger.isInfoEnabled())
			logger.info("leaving init() with parseFullIdp::[" + parseFullIdp
					+ "]  and procNAme::[" + procName + "]");
	}


	/**
	 * Gets the single instance of TCAPRoutingControllerImpl.
	 * 
	 * @return single instance of TCAPRoutingControllerImpl
	 */
	public static TcapRoutingControllerImpl getInstance() {
		if (tcapRoutingControllerImpl == null){
			synchronized (TcapRoutingControllerImpl.class) {
				if (tcapRoutingControllerImpl == null){
					tcapRoutingControllerImpl = new TcapRoutingControllerImpl();
				}//end inner if
			}//end synchronized

		}//end outer if

		//is initialized is indepndent of tcaprouting conreoller impl as slee component creates its instance
		if(!isInitialized){
			synchronized (TcapRoutingControllerImpl.class) {
				if(!isInitialized){
					tcapRoutingControllerImpl.init();
					isInitialized=true;
				}//end inner if
			}//end synchronized
		}//end outer if

		return tcapRoutingControllerImpl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	/**
	 * Reads service key from byte array
	 * checks parsing criteria
	 * If parse full IDP extracts
	 * terminating and originating
	 * number from IDP and performs DB lookup
	 * and return appname in TcapnextAppInfo
	 * If not returns service key in tcapNextAppInfo
	 * (non-Javadoc)
	 * 
	 * @throws ASNParsingException
	 * @throws CriticalityTypeException
	 * @throws EnumParamOutOfRangeException
	 * @throws MandatoryParamMissingException
	 * @throws ParameterOutOfRangeException
	 * @see com.genband.jain.protocol.ss7.tcap.router.TCAPRoutingController#getNextAppListener(javax.servlet.sip.SipServletRequest)
	 */
	@Override
	public TcapNextAppInfo getNextAppListener(byte[] initialRequest,SccpUserAddress origSUA) throws ASNParsingException,
	EnumParamOutOfRangeException, MandatoryParamMissingException,
	ParameterOutOfRangeException, CriticalityTypeException {
		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isInfoEnabled){
			logger.info("Inside getNextAppListener");
		}

		int serviceKey = -1;
		serviceKey = getServiceKeyFromByteArray(initialRequest);

		if (isInfoEnabled){
			logger.info("getNextAppListener serviceKey is "+serviceKey);
		}

		//chk service key
		if (serviceKey == -1) {
			logger.error("Service key not found in message returning NUll");
			return null;
		}
		if (parseFullIdp) {
			if (isDebugEnabled)
				logger.debug("Parse Full IDP");
			String appName = null;
			String originatingNumber = null;
			String terminatingNumber = null;
			InvokeIndEvent iie = new InvokeIndEvent(new Object());
			//parsing byte array
			byte[] opCode = { (byte) 0x00 };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
			iie.setOperation(op);
			InitialDPArg idp = null;

			try {
				idp = (InitialDPArg) InapOperationsCoding.decodeOperation(initialRequest, iie);
			} catch (EnumParamOutOfRangeException epore) {
				logger.error("parseIdp  : InapOperationsCoding.decodeOperation", epore);
				throw epore;
			} catch (Exception e) {
				logger.error("ASN parse failed on IDP ARG", e);
				logger.error("initialRequest content : " + Arrays.toString(initialRequest));
				throw new ASNParsingException("ASN Parsing Failure: IDP parsing failure occured.");
			}

			if (idp != null) {
				//get Orig&term from extensions
				//ParsedExtensionResult parseExtensions = getParamsFromExtensions(idp);
				//originatingNumber = parseExtensions.getOriginatingNumber();
				//terminatingNumber = parseExtensions.getTerminatingNumber();
				if (originatingNumber == null) {
					if (isDebugEnabled) {
						logger.debug("origNum null in extension try idp message");
					}
					originatingNumber = getOriginatingNumber(idp);
				}
				if (terminatingNumber == null) {
					if (isDebugEnabled) {
						logger.debug("termNum null in extension try idp message");
					}
					terminatingNumber = getTerminatingNumber(idp);
				}

				//fail call if orig is still null
				if (originatingNumber == null) {

					originatingNumber="";
					if (isDebugEnabled) {
						logger.debug("Orig num is still null " + "dont throw param not set exception as it is optional");
					}
					//					throw new MandatoryParamMissingException("Orig is null in IDP.",
					//									MISSINGPARAM.ORIG_NUM);
				}

				//FIXME
				//fail call if term is still null
				/*
				 * commented as VPN calls can have terminating number missing.
				 * if (terminatingNumber == null) {
				 * if (isDebugEnabled) {
				 * logger.debug("term num is still null " + "throw param not set exception");
				 * }
				 * throw new MandatoryParamMissingException("term is null in IDP.",
				 * MISSINGPARAM.TERM_NUM);
				 * }
				 */

			} else {
				if (isDebugEnabled)
					logger.debug("IDP not found");
				throw new ASNParsingException(
						"ASN Parsing Failure: IDP parsing failure occured wit idp as null.");
			}

			try {
				/*
				 * commented below SBTM code and replaced with calling findapplictaion on SN  router
				 */
				//				appName = tcapRoutingControllerDao.findApplicationName(serviceKey,
				//								originatingNumber, terminatingNumber);


				SipApplicationRouter  sar=AseSipApplicationRouterManager.getSysAppRouter();

				if (sar != null && sar instanceof SnApplicationRouter) {
					String origInfo=getPCFromSUA(origSUA)+"|" +serviceKey; 

					if (logger.isDebugEnabled()) {
						logger.debug("getNextInterestedService(): get Applictaion name from SNRouter: for origin info :"+ origInfo + " terminatingNumber : "+terminatingNumber
								+" originatingNumber "+ originatingNumber);
					}

					SnApplicationRouter snar = (SnApplicationRouter) sar;
					appName = snar.findApplicationForNormalRequest(
							terminatingNumber,
							originatingNumber,origInfo,null);
				}

				if (isDebugEnabled) {
					logger.debug("Appname returned by SNRouter is ::[" + appName + "]");
				}
				if (appName != null){

					SnApplicationRouterConfigData configData=SnApplicationRouter.getConfigData();
					if(appName.equals(configData.getDefaultApp())&& configData.isUseServiceKeyOnNoMatch()){

						if (isDebugEnabled) {
							logger.debug("Appname returned by SNRouter is ::[" + appName + "] which is default app but service key also exists so return that");
						}
						return new TcapNextAppInfo(Integer.toString(serviceKey), true);
					}else{

						if (isDebugEnabled) {
							logger.debug("Return appName SNRouter is ::[" + appName + "]");
						}
						return new TcapNextAppInfo(appName, !(parseFullIdp));
					}
				}else{

					if (isDebugEnabled) {
						logger.debug("Return appName using service key now ");
					}
					//	return null;// if no interesetd app found above then we will try to find using service key
					return new TcapNextAppInfo(Integer.toString(serviceKey), true);
				}
			} catch (Throwable e) {
				logger.error("Error reading App Name from Database return null", e);
				return null;
			}
		} else {
			if (isDebugEnabled)
				logger.debug("Parse Only Service Key key is::[" + serviceKey + "]");
			return new TcapNextAppInfo(Integer.toString(serviceKey), !(parseFullIdp));
		}

	}

	/**
	 * Method is used to fetch next application based on received digits and trigger criteria. 
	 * @param initialRequest
	 * @param receivedDigits
	 * @param origSUA
	 * @return
	 */
	public TcapNextAppInfo getNextAppListener(byte[] initialRequest, String opCode, String receivedDigits, SccpUserAddress sua) 
			throws ASNParsingException,
			EnumParamOutOfRangeException, MandatoryParamMissingException,
			ParameterOutOfRangeException, CriticalityTypeException{

		if (logger.isInfoEnabled()){
			logger.info("Inside getNextAppListener(byte[] initialRequest, String opCode, String receivedDigits, SccpUserAddress sua)");
		}

		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		String appName = null;
		String originatingNumber = "";
		String terminatingNumber = null;
		String origInfo = "";
		int triggerCriteria = 0; 
		TcapNextAppInfo nextAppInfo = null;

		try {
			Object ainObjMsg = (Object) AinOperationsCoding.decodeOperationsForOpCode(initialRequest, opCode);
			if(ainObjMsg instanceof InfoAnalyzedArg ){
				InfoAnalyzedArg  infoAnalyzeArg = (InfoAnalyzedArg)ainObjMsg; 

				if(infoAnalyzeArg.isTriggerCriteriaTypePresent()) {
					TriggerCriteriaTypeEnumType triggerCriteriaType = infoAnalyzeArg.getTriggerCriteriaType().getValue();
					triggerCriteria =  triggerCriteriaType.getValue().ordinal();
				}

			}else if(ainObjMsg instanceof TerminationAttemptArg ){
				TerminationAttemptArg  termAttemptArg = (TerminationAttemptArg)ainObjMsg;

				if(termAttemptArg.isTriggerCriteriaTypePresent()) {
					TriggerCriteriaTypeEnumType triggerCriteriaType = termAttemptArg.getTriggerCriteriaType().getValue();
					triggerCriteria =  triggerCriteriaType.getValue().ordinal();
				}

			}

			if (logger.isInfoEnabled()){
				logger.info("parsed triggered critera is : "+triggerCriteria);
			}

		} catch (EnumParamOutOfRangeException epore) {
			logger.error("getNextAppListener  : AinOperationsCoding.decodeOperation", epore);
			throw epore;
		} catch (Exception e) {
			logger.error("ASN parse failed on IDP ARG", e);
			throw new ASNParsingException("ASN Parsing Failure: IDP parsing failure occured.");
		}



		if (isInfoEnabled){
			logger.info("Inside getNextAppListener with received Digits" + receivedDigits);
		}

		try{
			SipApplicationRouter  sar=AseSipApplicationRouterManager.getSysAppRouter();

			if (sar != null && sar instanceof SnApplicationRouter) {
				if (logger.isDebugEnabled()) {
					logger.debug("getNextInterestedService(): get Applictaion name from SNRouter: for origin info :"+ 
							origInfo + " terminatingNumber : "+ receivedDigits
							+" originatingNumber "+ originatingNumber + ": triggerCriteria :"+triggerCriteria);
				}

				SnApplicationRouter snar = (SnApplicationRouter) sar;
				appName = snar.findApplicationForNormalRequest(receivedDigits, originatingNumber, origInfo, null,triggerCriteria+"");
			}

			if (isDebugEnabled) {
				logger.debug("Appname returned by SNRouter is ::[" + appName + "]");
			}
			if (appName != null){
				nextAppInfo = new TcapNextAppInfo(appName, false);
			}
		} catch (Throwable e) {
			logger.error("Error reading App Name for number " +receivedDigits + " from Database return null", e);
			return null;
		}

		if(isDebugEnabled){
			logger.debug("NextApp returned:" + nextAppInfo);
		}

		return nextAppInfo;
	}


	/*
	 * get application by using new implementtaion
	 */
	public TcapNextAppInfo getNextAppListenerV2(byte[] initialRequest,
			String opCode,
			String receivedDigits,
			String ssn,
			int protocol,
			int pointCode,
			String serviceKey,
			SccpUserAddress sua , int dialogueId) throws ASNParsingException, EnumParamOutOfRangeException,
	MandatoryParamMissingException, ParameterOutOfRangeException, CriticalityTypeException {

		if (logger.isInfoEnabled()) {
			logger.info(
					"Inside getNextAppListenerV2(byte[] initialRequest, String opCode, String receivedDigits, SccpUserAddress sua)");
		}


		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		String appName = null;
		String originatingNumber = "";
		String terminatingNumber = null;
		String origInfo = "";
		int triggerCriteria = 0;
		TcapNextAppInfo nextAppInfo = null;
		CarrierInfo carrierInfo = null;
		PhoneNumber calledPartyID = null;
		PhoneNumber callingPartyID = null;
		Integer trunkGroup = 0;
		TriggeringData triggeringData = new TriggeringData();

		try {
			Object ainObjMsg = (Object) AinOperationsCoding.decodeOperationsForOpCode(initialRequest, opCode);
			if (ainObjMsg instanceof InfoAnalyzedArg) {
				logger.info("Inside InfoAnalyzedArg");
				InfoAnalyzedArg infoAnalyzeArg = (InfoAnalyzedArg) ainObjMsg;

				if (infoAnalyzeArg.isTriggerCriteriaTypePresent()) {
					TriggerCriteriaTypeEnumType triggerCriteriaType = infoAnalyzeArg.getTriggerCriteriaType()
							.getValue();
					triggerCriteria = triggerCriteriaType.getValue().ordinal();
				}

				// to get trunkGroup
				UserID uId = infoAnalyzeArg.getUserID();
				if (uId != null && uId.getValue().getTrunkGroupID() != null) {
					trunkGroup = getTrunkGroup(uId).getValue();
				}

				// To get carrier info
				if (infoAnalyzeArg.isCarrierPresent()) {
					Carrier carrier = infoAnalyzeArg.getCarrier();
					carrierInfo = getCarrierInfo(carrier);
				}

				// get calledParty Id
				if (infoAnalyzeArg.isCalledPartyIDPresent()) {
					byte[] value = infoAnalyzeArg.getCalledPartyID().getValue().getValue();
					calledPartyID = getCalledPartyPhoneNumber(value);
				}

				// get Calling party Id
				if (infoAnalyzeArg.isCallingPartyIDPresent()) {
					byte[] value = infoAnalyzeArg.getCallingPartyID().getValue().getValue();
					callingPartyID = getCallingPartyPhoneNumber(value);
				}

			} else if (ainObjMsg instanceof TerminationAttemptArg) {
				logger.info("Inside TerminationAttemptArg");
				TerminationAttemptArg termAttemptArg = (TerminationAttemptArg) ainObjMsg;

				if (termAttemptArg.isTriggerCriteriaTypePresent()) {
					TriggerCriteriaTypeEnumType triggerCriteriaType = termAttemptArg.getTriggerCriteriaType()
							.getValue();
					triggerCriteria = triggerCriteriaType.getValue().ordinal();
				}

				// to get trunkGroup
				UserID uId = termAttemptArg.getUserID();
				if (uId != null && uId.getValue().getTrunkGroupID() != null) {
					trunkGroup = getTrunkGroup(uId).getValue();
				}

				// To get carrier info
				if (termAttemptArg.isCarrierPresent()) {
					Carrier carrier = termAttemptArg.getCarrier();
					carrierInfo = getCarrierInfo(carrier);
				}

				// get calledParty Id
				if (termAttemptArg.isCalledPartyIDPresent()) {
					byte[] value = termAttemptArg.getCalledPartyID().getValue().getValue();
					calledPartyID = getCalledPartyPhoneNumber(value);
				}

				// get Calling party Id
				if (termAttemptArg.isCallingPartyIDPresent()) {
					byte[] value = termAttemptArg.getCallingPartyID().getValue().getValue();
					callingPartyID = getCallingPartyPhoneNumber(value);
				}

			} else if (ainObjMsg instanceof InfoCollectedArg) {
				logger.info("Inside InfoCollectedArg");
				InfoCollectedArg infoCollectedArg = (InfoCollectedArg) ainObjMsg;
				TriggerCriteriaTypeEnumType triggerCriteriaType = infoCollectedArg.getTriggerCriteriaType().getValue();
				triggerCriteria = triggerCriteriaType.getValue().ordinal();

				// to get trunkGroup
				UserID uId = infoCollectedArg.getUserID();
				if (uId != null && uId.getValue().getTrunkGroupID() != null) {
					trunkGroup = getTrunkGroup(uId).getValue();
				}

				// To get carrier info
				if (infoCollectedArg.isCarrierPresent()) {
					Carrier carrier = infoCollectedArg.getCarrier();
					carrierInfo = getCarrierInfo(carrier);
				}

				// get calledParty Id
				if (infoCollectedArg.isCalledPartyIDPresent()) {
					byte[] value = infoCollectedArg.getCalledPartyID().getValue().getValue();
					calledPartyID = getCalledPartyPhoneNumber(value);
				}

				// get Calling party Id
				if (infoCollectedArg.isCallingPartyIDPresent()) {
					byte[] value = infoCollectedArg.getCallingPartyID().getValue().getValue();
					callingPartyID = getCallingPartyPhoneNumber(value);
				}

			} else if (ainObjMsg instanceof NetworkBusyArg) {
				logger.info("Inside NetworkBusyArg");
				NetworkBusyArg networkBusyArg = (NetworkBusyArg) ainObjMsg;
				TriggerCriteriaTypeEnumType triggerCriteriaType = networkBusyArg.getTriggerCriteriaType().getValue();
				triggerCriteria = triggerCriteriaType.getValue().ordinal();

				// to get trunkGroup
				UserID uId = networkBusyArg.getUserID();
				if (uId != null && uId.getValue().getTrunkGroupID() != null) {
					trunkGroup = getTrunkGroup(uId).getValue();
				}

				// To get carrier info
				if (networkBusyArg.isCarrierPresent()) {
					Carrier carrier = networkBusyArg.getCarrier();
					carrierInfo = getCarrierInfo(carrier);
				}

				// get calledParty Id
				if (networkBusyArg.isCalledPartyIDPresent()) {
					byte[] value = networkBusyArg.getCalledPartyID().getValue().getValue();
					calledPartyID = getCalledPartyPhoneNumber(value);
				}

				// get Calling party Id
				if (networkBusyArg.isCallingPartyIDPresent()) {
					byte[] value = networkBusyArg.getCallingPartyID().getValue().getValue();
					callingPartyID = getCallingPartyPhoneNumber(value);
				}

			}

			// this need to be controlled through configuration flag
			if(StringUtils.isNotBlank(receivedDigits)){
				if(logger.isDebugEnabled()){
					logger.debug(dialogueId + "GT digits : " + receivedDigits 
							+ ", is replaced in called party ID extracted from incoming buffer:" + 
							calledPartyID);
				}
				if(calledPartyID != null){
					calledPartyID.setAddress(receivedDigits);
				}
			}else{
				if(calledPartyID != null){
					receivedDigits = calledPartyID.getAddress();
				}

				if(logger.isDebugEnabled()){
					logger.debug(dialogueId + ": receovedDigits copied from parsed digits:" + receivedDigits);
				}
			}

			triggeringData.setCalledPartyId(calledPartyID);			
			triggeringData.setCallingPartyId(callingPartyID);
			triggeringData.setSsn(ssn);
			triggeringData.setServiceKey(serviceKey);
			triggeringData.setOpCode(opCode);
			triggeringData.setPointCode(String.valueOf(pointCode));

			if(carrierInfo!=null)
				triggeringData.setCic(carrierInfo.getAddress());

			triggeringData.setTrunkGroup(trunkGroup);
			triggeringData.setTriggeringCriteria(triggerCriteria);
			triggeringData.setProtocol(String.valueOf(protocol));
			triggeringData.setDialogueId(dialogueId);	

			if (logger.isInfoEnabled()) {
				logger.info("parsed triggeringData : "+ triggeringData);
			}

		} catch (EnumParamOutOfRangeException epore) {
			logger.error("getNextAppListener  : AinOperationsCoding.decodeOperation", epore);
			throw epore;
		} catch (Exception e) {
			logger.error("ASN parse failed on IDP ARG", e);
			throw new ASNParsingException("ASN Parsing Failure: IDP parsing failure occured.");
		}

		if (isInfoEnabled) {
			logger.info("Inside getNextAppListener with received Digits" + receivedDigits);
		}

		try {
			SipApplicationRouter sar = AseSipApplicationRouterManager.getSysAppRouter();

			logger.info("callingPartyID  :"+ callingPartyID);
			if(callingPartyID!= null && callingPartyID.getAddress() != null) {
				originatingNumber = callingPartyID.getAddress();
			}
			if (sar != null && sar instanceof SnApplicationRouter) {
				if (logger.isDebugEnabled()) {
					logger.debug("getNextInterestedService(): get Applictaion name from SNRouter: for origin info :"
							+ origInfo + " terminatingNumber : " + receivedDigits + " originatingNumber "
							+ originatingNumber + ": triggerCriteria :" + triggerCriteria);
				}

				SnApplicationRouter snar = (SnApplicationRouter) sar;

				appName = snar.findApplicationForNormalRequestV2(receivedDigits, originatingNumber,
						origInfo, null, triggerCriteria + "", triggeringData);
			}

			if (isDebugEnabled) {
				logger.debug("Appname returned by SNRouter is ::[" + appName + "]");
			}
			if (appName != null) {
				nextAppInfo = new TcapNextAppInfo(appName, false);
			}
		} catch (Throwable e) {
			logger.error("Error reading App Name for number " + receivedDigits + " from Database return null", e);
			return null;
		}

		if (isDebugEnabled) {
			logger.debug("NextApp returned:" + nextAppInfo);
		}

		return nextAppInfo;
	}



	/**
	 * To get Carrier info
	 * 
	 * @param carrier
	 * @return
	 */
	public CarrierInfo getCarrierInfo(Carrier carrier) throws ASNParsingException {
		CarrierFormat cf = new CarrierFormat();
		try {
			cf.decodeCarrierFormat(carrier.getValue().getValue());
		} catch (Exception e) {
			logger.error("::[PH] Error in getCarrierInfo " + e.getMessage());
			throw new ASNParsingException("carrier Parsing Failure: Parsing TTC contractor num");
		}

		CarrierInfo ci = new CarrierInfo();
		ci.setAddress(cf.getAddrSignal());
		ci.setCarrierSelection(cf.getCarrierFormatSelectionEnum().getCode());
		ci.setNatureOfCarrier(cf.getCarrierFormatNatEnum().getCode());
		return ci;
	}

	/**
	 * Get trunk group Id
	 * 
	 * @param uId
	 * @return
	 */
	public TrunkGroupID getTrunkGroup(UserID uId) {
		TrunkGroupID trunkGroupId = null;
		if (uId.getValue().getTrunkGroupID() != null) {
			trunkGroupId = uId.getValue().getTrunkGroupID();
		}
		return trunkGroupId;

	}

	public PhoneNumber getCalledPartyPhoneNumber(byte[] value) throws ASNParsingException {
		try {
			AinDigits calliedPartyNumdigit = new AinDigits();
			AinDigits calledPartyNum = calliedPartyNumdigit.decodeAinDigits(value, Constant.CALLED);
			if (logger.isDebugEnabled()) {
				logger.debug(":: [PH] Extracted called number:" + calledPartyNum);
			}

			PhoneNumber calledNumber = parseCalledPartyNum(calledPartyNum);
			if (logger.isDebugEnabled()) {
				logger.debug("::[PH] Exit parseCalledPartyNum, calledNumber:" + calledNumber);
			}
			return calledNumber;
		} catch (Exception e) {
			throw new ASNParsingException(":: [PH] Calling party num address signal missing");
		}
	}


	public PhoneNumber getCallingPartyPhoneNumber(byte[] value) throws ASNParsingException {

		try {
			AinDigits callingPartyNumdigit = new AinDigits();

			AinDigits callingPartyNum = callingPartyNumdigit.decodeAinDigits(value, Constant.CALLING);

			if (logger.isDebugEnabled()) {
				logger.debug(":: [PH] Extracted Calling party number  " + callingPartyNum);
			}
			if (callingPartyNum == null || callingPartyNum.getAddrSignal() == null
					|| "".equals(callingPartyNum.getAddrSignal().trim())) {
				/*
				 * call should be handled as ASn parse failure as address signal is missing
				 */
				logger.error(":: [PH] Calling party num address signal missing");
				throw new ASNParsingException(":: [PH] Calling party num address signal missing");
			}
			PhoneNumber callingNumber = parseCallingPartyNum(callingPartyNum);
			if (logger.isDebugEnabled()) {
				logger.debug(":: [PH] Exit parseCallingPartyNum, CallingNumber:" + callingNumber);
			}

			return callingNumber;
		} catch (Exception e) {
			throw new ASNParsingException(":: [PH] Calling party num address signal missing");
		}
	}

	/**
	 * 
	 * @param calledPartyNum
	 * @return
	 */
	private static PhoneNumber parseCalledPartyNum(AinDigits calledPartyNum) {
		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Inside parseCalledPartyNum");
		}

		PhoneNumber calledNumber = new PhoneNumber();

		CalledNatOfNumEnum natureOfAddrEnum = calledPartyNum.getCalledNatOfNumEnum();
		if ((natureOfAddrEnum == CalledNatOfNumEnum.SPARE) || (natureOfAddrEnum == CalledNatOfNumEnum.NAT_NUM)
				|| (natureOfAddrEnum == CalledNatOfNumEnum.CALL_LOCAL_EXCHANGE)
				// || (natureOfAddrEnum == CalledNatOfNumEnum.NAT_NUM_OPERTR_REQ)
				|| (natureOfAddrEnum == CalledNatOfNumEnum.SUBS_NUM)
				|| (natureOfAddrEnum == CalledNatOfNumEnum.SUBS_NUM_OPERTR_REQ)) {
			calledNumber.setNatureOfAddress(PhoneNumber.NOA_NATIONAL);
		} else {
			calledNumber.setNatureOfAddress(PhoneNumber.NOA_UNKNOWN);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Nature of Address is " + natureOfAddrEnum.getCode());
		}

		/*
		 * Numbering Plan Indicator
		 */
		NumPlanEnum numPlanIndEnum = calledPartyNum.getNumPlanEnum();
		if (numPlanIndEnum == NumPlanEnum.PRIVATE_NP) {
			calledNumber.setNumberingPlan(PhoneNumber.NP_PRIVATE);
		} else if (numPlanIndEnum == NumPlanEnum.ISDN_NP) {
			calledNumber.setNumberingPlan(PhoneNumber.NP_ISDN);
		} else {
			calledNumber.setNumberingPlan(PhoneNumber.NP_UNKNOWN);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Numbering Plan is " + numPlanIndEnum.getCode());
		}

		/*
		 * Address
		 */
		String addrSignal = calledPartyNum.getAddrSignal();
		calledNumber.setAddress(addrSignal);
		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Address Signal is " + addrSignal);
		}

		return calledNumber;
	}

	private static PhoneNumber parseCallingPartyNum(AinDigits callingPartyNum) {
		if (logger.isDebugEnabled()) {
			logger.debug("[PH]:: Inside parseCallingPartyNum");
		}

		PhoneNumber callingNumber = new PhoneNumber();

		/*
		 * Nature Of Address
		 */
		CalgNatOfNumEnum natureOfAddrEnum = callingPartyNum.getCalgNatOfNumEnum();
		if ((natureOfAddrEnum == CalgNatOfNumEnum.SPARE) || (natureOfAddrEnum == CalgNatOfNumEnum.UNIQUE_NAT_NUM)
				|| (natureOfAddrEnum == CalgNatOfNumEnum.NON_UNIQUE_NAT_NUM)
				|| (natureOfAddrEnum == CalgNatOfNumEnum.UNIQUE_SUBS_NUM)
				|| (natureOfAddrEnum == CalgNatOfNumEnum.NON_UNIQUE_SUBS_NUM)) {
			callingNumber.setNatureOfAddress(PhoneNumber.NOA_NATIONAL);
		} else if (natureOfAddrEnum == CalgNatOfNumEnum.NOT_APPLICABLE) {
			callingNumber.setNatureOfAddress(PhoneNumber.NOA_UNKNOWN);
		} else if ((natureOfAddrEnum == CalgNatOfNumEnum.NON_UNIQUE_INTER_NAT_NUM)
				|| natureOfAddrEnum == CalgNatOfNumEnum.UNIQUE_INTER_NAT_NUM) {
			callingNumber.setNatureOfAddress(PhoneNumber.NOA_INTERNATIONAL);
		} else {
			callingNumber.setNatureOfAddress(PhoneNumber.NOA_UNKNOWN);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Nature of Address is " + natureOfAddrEnum.getCode());
		}

		/*
		 * Numbering Plan Indicator
		 */
		com.agnity.ain.enumdata.NumPlanEnum numPlanIndEnum = callingPartyNum.getNumPlanEnum();
		if (numPlanIndEnum == NumPlanEnum.PRIVATE_NP) {
			callingNumber.setNumberingPlan(PhoneNumber.NP_PRIVATE);
		} else if (numPlanIndEnum == NumPlanEnum.ISDN_NP) {
			callingNumber.setNumberingPlan(PhoneNumber.NP_ISDN);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Numbering Plan is " + numPlanIndEnum.getCode());
		}

		// Presentation and screening indicator
		callingNumber.setPresentationIndicator(callingPartyNum.getClgPrsntRestIndEnum().getCode());
		// callingNumber.setScreeningIndicator(callingPartyNum.getScreeningIndEnum().getCode());

		/*
		 * Address
		 */
		String addrSignal = callingPartyNum.getAddrSignal();
		callingNumber.setAddress(addrSignal);
		if (logger.isDebugEnabled()) {
			logger.debug("::[PH] Extracted Address Signal is " + addrSignal);
		}

		return callingNumber;
	}


	/**
	 * Method is used to fetch next application based on received digits. 
	 * @param receivedDigits
	 * @param origSUA
	 * @return
	 */
	public TcapNextAppInfo getNextAppListener(String receivedDigits, SccpUserAddress sua) {

		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isDebugEnabled = logger.isDebugEnabled();
		String appName = null;
		String originatingNumber = "";
		String terminatingNumber = null;
		String origInfo = "";
		TcapNextAppInfo nextAppInfo = null;


		if (isInfoEnabled){
			logger.info("Inside getNextAppListener with received Digits" + receivedDigits);
		}

		try{
			SipApplicationRouter  sar=AseSipApplicationRouterManager.getSysAppRouter();

			if (sar != null && sar instanceof SnApplicationRouter) {
				if (logger.isDebugEnabled()) {
					logger.debug("getNextInterestedService(): get Applictaion name from SNRouter: for origin info :"+ 
							origInfo + " terminatingNumber : "+ receivedDigits
							+" originatingNumber "+ originatingNumber);
				}

				SnApplicationRouter snar = (SnApplicationRouter) sar;
				appName = snar.findApplicationForNormalRequest(receivedDigits, originatingNumber, origInfo, null);
			}

			if (isDebugEnabled) {
				logger.debug("Appname returned by SNRouter is ::[" + appName + "]");
			}
			if (appName != null){
				nextAppInfo = new TcapNextAppInfo(appName, false);
			}
		} catch (Throwable e) {
			logger.error("Error reading App Name for number " +receivedDigits + " from Database return null", e);
			return null;
		}

		if(isDebugEnabled){
			logger.debug("NextApp returned:" + nextAppInfo);
		}

		return nextAppInfo;
	}


	/**
	 * This method is used to get pc form SUA
	 * @param origSUA
	 * @return
	 */
	private int getPCFromSUA(SccpUserAddress sua){
		if (logger.isDebugEnabled()) {
			logger.debug("getPCFromSUA "+ sua);
		}
		int pc = -1;
		try {
			SignalingPointCode signalingPointCode = sua.getSubSystemAddress().getSignalingPointCode();
			int zone = signalingPointCode.getZone();
			int cluster = signalingPointCode.getCluster();
			int member = signalingPointCode.getMember();

			logger.debug("[PH]:: Signal Point Code :: " + signalingPointCode);
			if (logger.isInfoEnabled()) {
				logger.info("[PH]:: Origin zone= " + zone + " cluster=" + cluster + " member=" + member);
			}
			String pcBitStr = lPad(Integer.toBinaryString(zone), 3) + lPad(Integer.toBinaryString(cluster), 8) + lPad(Integer.toBinaryString(member), 3);
			if (logger.isInfoEnabled()) {
				logger.info("[PH]:: pcBitStr =" + pcBitStr);
			}
			pc = Integer.parseInt(pcBitStr, 2);
			if (logger.isDebugEnabled()) {
				logger.debug("getPCFromSUA returns "+ pc);
			}
		} catch (ParameterNotSetException e1) {
			logger.error("[PH]:: Failed to get origin point code from SUA " + e1.getMessage());
		}
		return pc;
	}


	/**
	 * This method is used to do processing of input string
	 *
	 * @param input
	 * @param resultSize
	 */
	private static String lPad(String input, int resultSize) {
		if (input == null) {
			return input;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (resultSize - input.length()); i++) {
			result.append("0");
		}
		result.append(input);
		return result.toString();
	}
	/**
	 * This method parses extension param in IDP and return orig and term as per Standard
	 * 
	 * @param idp
	 * @return
	 * @throws CriticalityTypeException
	 * @throws ASNParsingException
	 */
	/*
	 * private ParsedExtensionResult getParamsFromExtensions(InitialDPArg idp)
	 * 				throws ASNParsingException, CriticalityTypeException {
	 * 	if (logger.isDebugEnabled()) {
	 * 		logger.debug("Inside get extensions: fetch orig and term from extension");
	 * 	}
	 * 	String originatingNumber = null;
	 * 	String terminatingNumber = null;
	 * 	if (idp.isExtensionsPresent()) {
	 * 		Collection<ExtensionField> extensionList = idp.getExtensions();
	 * 		Iterator<ExtensionField> extensionIter = extensionList.iterator();
	 * 		while (extensionIter.hasNext()) {
	 * 			ExtensionField extensionField = extensionIter.next();

	 * 			//do error if crtitcality type and extension type 
	 * 			if (extensionField.getType() != 254 && extensionField.getType() != -2) {
	 * 				if (extensionField.getCriticality() == null
	 * 								|| extensionField.getCriticality().getValue() == EnumType.ignore) {
	 * 					//no need to throw exception for ignore or null
	 * 					//throw new CriticalityTypeException(CRITICALITY.IGNORE);
	 * 					if (logger.isDebugEnabled()) {
	 * 						logger.warn("Invalid extension type::"
	 * 										+ extensionField.getType()
	 * 										+ " criticality is ignore or null so continue");
	 * 					}
	 * 				} else {
	 * 					logger.warn("Invalid extension type::"
	 * 									+ extensionField.getType() + " criticality is Abort");
	 * 					throw new CriticalityTypeException(CRITICALITY.ABORT);
	 * 				}
	 * 			}

	 * 			try {
	 * 				InitialDPExtension initialDpExt = InapOperationsCoding
	 * 					.decodeInitialDPExt(extensionField.getValue());

	 * 				if (logger.isDebugEnabled()) {
	 * 					logger.debug("Decoded IDP Extension is " + initialDpExt);
	 * 				}

	 * 				if (initialDpExt.isTtcContractorNumberPresent()) {
	 * 					try {
	 * 						TtcContractorNumber generatedTtcContractor = initialDpExt
	 * 							.getTtcContractorNumber();

	 * 						originatingNumber = TtcContractorNum.decodeTtcContractorNum(
	 * 										generatedTtcContractor.getValue()).getAddrSignal();
	 * 					} catch (InvalidInputException e) {
	 * 						if (logger.isDebugEnabled()) {
	 * 							logger.debug("InputException in decoding ContractorNum", e);
	 * 						}
	 * 						originatingNumber = null;
	 * 						throw new ASNParsingException(
	 * 										"ASN Parsing Failure: Parsing TTC contractor num");
	 * 					}
	 * 				}

	 * 				if (initialDpExt.isTtcCalledINNumberPresent()) {
	 * 					try {
	 * 						com.genband.inap.asngenerated.TtcCalledINNumber genertatedTtcCalledINNumber = initialDpExt
	 * 							.getTtcCalledINNumber();

	 * 						terminatingNumber = TtcCalledINNumber.decodeTtcCalledINNum(
	 * 										genertatedTtcCalledINNumber.getValue()).getAddrSignal();
	 * 					} catch (InvalidInputException e) {
	 * 						if (logger.isDebugEnabled()) {
	 * 							logger.debug("InputException in decoding TtcCAlledNum", e);
	 * 						}
	 * 						terminatingNumber = null;
	 * 						throw new ASNParsingException(
	 * 										"ASN Parsing Failure: Parsing TTC called num");
	 * 					}
	 * 				}

	 * 			} catch (Exception e) {
	 * 				if (logger.isDebugEnabled()) {
	 * 					logger.debug("Exception in decoding Extensions", e);
	 * 				}
	 * 				originatingNumber = null;
	 * 				terminatingNumber = null;
	 * 				throw new ASNParsingException("ASN Parsing Failure: Parsing Extensions");
	 * 			}

	 * 		}
	 * 	}
	 * 	if (logger.isDebugEnabled()) {
	 * 		logger.debug("return pasre extensions with origniating number:" + originatingNumber
	 * 						+ " terminatingNumber:" + terminatingNumber);
	 * 	}
	 * 	return new ParsedExtensionResult(originatingNumber, terminatingNumber);

	 * }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.baypackets.bayprocessor.agent.MComponent#changeState(com.baypackets.bayprocessor.agent
	 * .MComponentState)
	 */
	@Override
	public void changeState(MComponentState arg0) throws UnableToChangeStateException {
		//XXX No impl reqd

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.baypackets.bayprocessor.agent.MComponent#updateConfiguration(com.baypackets.bayprocessor
	 * .slee.common.Pair[], com.baypackets.bayprocessor.agent.OperationType)
	 */
	@Override
	public void updateConfiguration(Pair[] pairs, OperationType opType)
			throws UnableToUpdateConfigException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Enter Update Configuration::EMS OIDs Changed");
		try {
			if (pairs == null) {
				if (isDebugEnabled)
					logger.debug("Changed pairs array is null");
				return;
			}
			if (OperationType.MODIFY != opType.getValue()) {
				if (isDebugEnabled)
					logger.debug("OperationType is not MODIFY");
				return;
			}
			for (int i = 0; i < pairs.length; i++) {
				String paramName = pairs[i].getFirst().toString();
				String paramValue = pairs[i].getSecond().toString();
				if (OID_TCAP_AR_PARSE_FULL_IDP.equals(paramName)) {
					parseFullIdp = Boolean.parseBoolean(paramValue);
					if (parseFullIdp) {
						try {
							tcapRoutingControllerDao = TcapRoutingControllerDaoImpl.getInstance();
						} catch (Exception e) {
							logger.error("Exception getting Dao Object", e);
							throw new RuntimeException("Unable to get DAO", e);
						}
					}
				}
			}
		} catch (Exception e) {
			String msg = "Error occurred while updating component configuration: " + e.toString();
			logger.error(msg, e);
			throw new UnableToUpdateConfigException(msg);
		}
		if (isDebugEnabled)
			logger.debug("leave Update Configuration");
	}

	private void readParsingRule() {
		if (logger.isDebugEnabled())
			logger.debug("Enter readParsingRule");
		parseFullIdp = Boolean.parseBoolean(configRep.getValue(OID_TCAP_AR_PARSE_FULL_IDP));
		if (logger.isDebugEnabled())
			logger.debug("leave readParsingRule parsefullIDp::[" + parseFullIdp + "]");

	}

	private void readProcedureName() {
		if (logger.isDebugEnabled())
			logger.debug("Enter readProcedureName");
		procName = configRep.getValue(TCAP_ROUTING_PROC_NAME);
		if (logger.isDebugEnabled())
			logger.debug("leave readProcedureName procName::[" + procName + "]");
	}

	/**
	 * @return the parseFullIdp
	 */
	public boolean isParseFullIdp() {
		return parseFullIdp;
	}

	/**
	 * used in mocking
	 * 
	 * @param parseFullIdp
	 */
	protected void setParseFullIdp(boolean parseFullIdp) {
		this.parseFullIdp = parseFullIdp;
	}

	protected String getOriginatingNumber(InitialDPArg idp) throws ASNParsingException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Enter getOriginatingNumber");
		String originatingNumber = null;
		//check contarctor first

		/*
		 * //move to getExtensions
		 * try {
		 * InitialDPExtension idpExt = InapOperationsCoding.decodeInitialDPExt(extensionList
		 * .iterator().next().getValue());
		 * TtcContractorNumber generatedTtcContractor = idpExt.getTtcContractorNumber();
		 * if (generatedTtcContractor != null)
		 * originatingNumber = TtcContractorNum.decodeTtcContractorNum(
		 * generatedTtcContractor.getValue()).getAddrSignal();
		 * } catch (Exception e) {
		 * if (isDebugEnabled)
		 * logger.debug("Exception in decoding TtcContractorNumber", e);
		 * 
		 * originatingNumber = null;
		 * }
		 * }
		 */

		//if contractor is null

		//checking Calling party num
		if (idp.getCallingPartyNumber() != null) {
			try {
				originatingNumber = (CallingPartyNum.decodeCalgParty(idp.getCallingPartyNumber()
						.getValue())).getAddrSignal();
			} catch (InvalidInputException e) {
				if (isDebugEnabled)
					logger.debug("InputException in decoding CallingPartyNum", e);
				originatingNumber = null;
				throw new ASNParsingException(
						"ASN Parsing Failure: fetchin originatingNumber from calling party number");
			}
		}

		if (isDebugEnabled)
			logger.debug("Leave getOriginatingNumber with ON::[" + originatingNumber + "]");
		return originatingNumber;
	}

	protected String getTerminatingNumber(InitialDPArg idp) throws ASNParsingException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Enter getTerminatingNumber");
		String terminatingNumber = null;
		//check called IN first
		/*
		 * Moved to extensions
		 * Collection<ExtensionField> extensionList = idp.getExtensions();
		 * //ty check
		 * if (extensionList != null) {
		 * /
		 * try {
		 * InitialDPExtension idpExt = InapOperationsCoding.decodeInitialDPExt(extensionList
		 * .iterator().next().getValue());
		 * com.genband.inap.asngenerated.TtcCalledINNumber genertatedTtcCalledINNumber = idpExt
		 * .getTtcCalledINNumber();
		 * if (genertatedTtcCalledINNumber != null)
		 * terminatingNumber = TtcCalledINNumber.decodeTtcCalledINNum(
		 * genertatedTtcCalledINNumber.getValue()).getAddrSignal();
		 * } catch (Exception e) {
		 * if (isDebugEnabled)
		 * logger.debug("Exception in decoding TtcCalledINNumber", e);
		 * terminatingNumber = null;
		 * 
		 * }
		 * }
		 */

		//if ttc called IN is null

		if (isDebugEnabled)
			logger.debug("Called IN is null");
		//checking Called party num
		if (idp.getCalledPartyNumber() != null) {
			try {
				terminatingNumber = (CalledPartyNum.decodeCaldParty(idp.getCalledPartyNumber()
						.getValue())).getAddrSignal();
			} catch (InvalidInputException e) {
				if (isDebugEnabled)
					logger.debug("InvalidInputException in decoding CalledPartyNum", e);
				terminatingNumber = null;
				throw new ASNParsingException(
						"ASN Parsing Failure: fetchin terminatingNumber from calledParty Number");
			}
		}


		//if called Party is null
		if (terminatingNumber == null) {
			if (isDebugEnabled)
				logger.debug("Called Party is null");
			//checking dialed digits num
			if (idp.getDialledDigits() != null) {
				try {
					terminatingNumber = (CalledPartyNum.decodeCaldParty(idp.getDialledDigits()
							.getValue())).getAddrSignal();
				} catch (InvalidInputException e) {
					if (isDebugEnabled)
						logger.debug("InvalidInputException in decoding Dialed digits", e);
					terminatingNumber = null;
					throw new ASNParsingException(
							"ASN Parsing Failure: fetchin terminatingNumber from dialed digits");
				}
			}
		}
		if (isDebugEnabled)
			logger.debug("return get terminating num with [" + terminatingNumber + "]");
		return terminatingNumber;
	}

	protected int getServiceKeyFromByteArray(byte[] b) throws MandatoryParamMissingException,
	ParameterOutOfRangeException {

		boolean isDebugEnabled = logger.isDebugEnabled();
		int serviceKey = -1;

		if ((b[1] & 0x80) >=1){
			int lengthOffset = b[1] & 0x7f;
			int servKeyTag = lengthOffset + 2;
			if ((b[servKeyTag] & 0xff) == 128){
				if (b[servKeyTag+1] == 1) {
					serviceKey = b[servKeyTag+2];
				} else if (b[servKeyTag+1] == 2) {
					serviceKey = ((b[servKeyTag+2] & 0xff) << 8);
					serviceKey = (serviceKey | (b[servKeyTag+3] & 0xff));
				}
			}else {
				throw new MandatoryParamMissingException("Service key is not present in IDP array",
						MISSINGPARAM.SERVICE_KEY);
			}
		}else if ((b[2] & 0xff) == 128) {
			if (b[3] == 1) {
				serviceKey = b[4] & 0xFF;
			} else if (b[3] == 2) {
				serviceKey = ((b[4] & 0xff) << 8);
				serviceKey = (serviceKey | (b[5] & 0xff));
			}
		}else {
			throw new MandatoryParamMissingException("Service key is not present in IDP array",
					MISSINGPARAM.SERVICE_KEY);
		}

		//		if ((b[2] & 0xff) == 128) {
		//			if (b[3] == 1) {
		//				serviceKey = b[4];
		//			} else if (b[3] == 2) {
		//				serviceKey = ((b[4] & 0xff) << 8);
		//				serviceKey = (serviceKey | (b[5] & 0xff));
		//			}
		//		} else {
		//			throw new MandatoryParamMissingException("Service key is not present in IDP array",
		//							MISSINGPARAM.SERVICE_KEY);
		//		}

		if ((serviceKey < 0) ){//|| (serviceKey > 999)) {
			throw new ParameterOutOfRangeException(
					"Service key is out of range or invalid length value::" + serviceKey,
					PARAM_NAME.SERVICE_KEY);
		}

		if (isDebugEnabled)
			logger.debug("serviceKey [" + serviceKey + "]");
		return serviceKey;
	}

	protected int getServiceKeyFromIDP(InitialDPArg idp) throws MandatoryParamMissingException,
	ParameterOutOfRangeException {
		int serviceKey = -1;
		if (idp.isServiceKeyPresent()) {
			serviceKey = idp.getServiceKey().getValue().getValue().intValue();
		} else {
			throw new MandatoryParamMissingException("Service key is not present in IDP Arg",
					MISSINGPARAM.SERVICE_KEY);
		}

		if ((serviceKey < 0) || (serviceKey > 999)) {
			throw new ParameterOutOfRangeException(
					"Service key is out of range or invalid length value::" + serviceKey,
					PARAM_NAME.SERVICE_KEY);
		}
		return serviceKey;
	}
}
