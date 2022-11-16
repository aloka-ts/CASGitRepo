package com.agnity.sasapp.common;

import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.http.HttpServletResponse;
import com.agnity.inapitutcs2.datatypes.CarrierInformation;
import com.agnity.inapitutcs2.enumdata.ISDNAccessIndEnum;
import com.agnity.inapitutcs2.enumdata.ISDNUserPartIndEnum;
import com.agnity.inapitutcs2.enumdata.InterNwIndEnum;
import com.agnity.inapitutcs2.enumdata.TransitCarrierIndEnum;
import com.genband.isup.datatypes.RedirectionInformation;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.CallingPartySubAddrEncodingEnum;
import com.genband.isup.enumdata.TypeOfSubaddress;
import com.genband.isup.enumdata.RedirectingReasonEnum;

public class CallData implements Serializable {

	private static final transient long		serialVersionUID	= 6941430008330108767L;
	public static final transient String	CALL_DATA			= "CALL-DATA";
	public static final transient int		CAUSE_CODE_BUSY		= 486;
	public static final transient int		CAUSE_CODE_NOANSWER	= 408;
	public static final transient int		CAUSE_CODE_NOTAVAIL	= 480;

	private PhoneNumber						callingNumber;
	private PhoneNumber						calledNumber;								//CalledIN if present otherwise Called party number

	private PhoneNumber						idpCalledPartyNumber;						//IDP.calledPartyNumber

	private PhoneNumber						contractorNumber;
	private PhoneNumber						dialedDigit;

	private PhoneNumber						modifiedCallingNumber;
	private PhoneNumber						modifiedCalledNumber;

	private PhoneNumber						destinationNumber;
	
	private PhoneNumber                   translatedCallingNumber;               

	private String							dialogAndCallId;
	
	private String							origLegCallId;
	private String							ivrLegCallId;
	private String							termLegCallId;
	private String 							controlDigit;
	private int								inapDialogueId;

	private int								reasonForRelease = 999;
	
	private String							appSessionId;
	private Date							callStartDate;

	private SccpUserAddress					sccpUserAddress;

	/* Error response received from orig/term/ivr */
	private transient int					causeCode;
	private int								releaseCauseValue;

	private boolean							auditFlag;
	private transient boolean				chargeAnnFlag		= true;
	private transient StringBuilder			serviceFlowMsg;
	private String							originIPAddress;
	private int								originPort;
	private int								originPointCode;
	
	private String							termIPAddress;
	private int								termPort;
	
	private transient int					apiCounter			= 1;
	private transient int					actionCounter		= 1;
	private transient String				apiResource;
	private transient String				actionResource;
	private transient StringBuilder			apiActionFlow		= new StringBuilder();
	 
	private Action				lastAction;
	private transient boolean				calledInNumberExist;
	private transient boolean 				pathSwitchInprogress;
	private long 							lastINFOTimestamp;
	private boolean							externalCdrReqd		= true;
	private int								intermediateCdrCount;
	private boolean							finalCdrWritten;
	private int								ivrConnectionCount;
	private boolean							traceFlag;
	private StringBuilder					traceMsg			= new StringBuilder();	;
	private List<Integer>								constraintId = new ArrayList<Integer>();
	private boolean							flexChargingApplied;
	private transient int					relLocationField	= -1;

	private transient Map<Object, Object>	transientAttribute; 		
	private String tcapSessionId;

	private int  deleteDigitNum;

    private int featureApplied;

    private boolean   errorNotifyToService;
    
    private int callType;
    
    private int callFeature;
	
	private transient HttpServletResponse	asrHttpResponse;
	
	private int cfnrFeature;
	
	private int tlec1;
	
	private int tlec2;
	
	private boolean isRequestTerminatedForCfb;

	public static enum DESTINATION_TYPE {
		ENDPOINT, VOICE_MAIL
	}

	public static enum SIGNALING_TYPE {
		SIP_T, SIP, INAP
	}

	public static enum INGRESS_NETWORK_TYPE {
		INTER_NETWORK, INTRA_NETWORK
	}

	public static enum SERVICEINDICATOR {
		SUBADDRESS_DIAL_IN_NSAP, PBX_DIAL_IN, SUBADDRESS_DIAL_IN_USER, PSTN_CALL
	}

	public static enum INAP_CALL_STATES {
		INIT, SERVICE_LOGIC, CONNECTION_IN_PROGRESS, CONNECTED, TERMINATION_IN_PROGRESS, TERMINATED, ASSIST, HANDOFF
	}

	public static enum SIP_CALL_STATES {
		INIT, SERVICE_LOGIC, CONNECTION_IN_PROGRESS, CONNECTED, TERMINATION_IN_PROGRESS, TERMINATED, REDIRECTED, MS_CONNECTION_IN_PROGRESS, MS_EARLY_MEDIA_CONNECTED, MS_CONNECTED, MS_PLAY_INVOKED, MS_PLAY_COLLECT_INVOKED, MS_PLAY_RECORD_INVOKED, MS_DISCONNECTION_IN_PROGRESS, CANCELLED
	}

	private INAP_CALL_STATES				inapCallState		= INAP_CALL_STATES.INIT;
	private SIP_CALL_STATES					origSipCallState	= SIP_CALL_STATES.INIT;
	private SIP_CALL_STATES					termSipCallState	= SIP_CALL_STATES.INIT;


	private DialoguePortion					inapDialoguePortion;

	// 2-PSTN 1-PBX Dial-In 3-SubAddress Dial-In
	private SERVICEINDICATOR				serviceIndicator;
	private transient String				pbtSubaddress;
	private INGRESS_NETWORK_TYPE			ingressNetworkType;
	private transient short					activityContextVersion;
	private String							chargingArea		= "";
	/*
	 * PH would set value of ttcSspChargingArea field with ttcSspChargingArea or
	 * ttcChargingArea from IDP.
	 * Service should use this field for Handoff Analysis and if this field is absent then service
	 * should fetch orig charging area from DB and should use that charging area as originating CA
	 * for handoff analysis.
	 */
	private String							ttcSspChargingArea	= "";
	private String							termChargingArea	= "";
	private int								cpc;
	//Additional CPC for Mobile phone
	private int								additionalCpc1 = -1;
	private int								additionalCpc2 = -1;
	private int								additionalCpc3 = -1;
	//Additional CPC for fixed/PSTN phone
	private int								fixedPhAdditionalCpc1 = -1;
	private transient PhoneNumber			genericNumber;
	private int								tmr;
	private int								fci;
	private int								invokeId;

	private transient int					serviceKey;
	private int								serviceType;										//1=FreeCall,2=Ad-Call,3=FreeCallTui, 4=AdCallTui
	private transient String				dpci;
	private transient TransitCarrierIndEnum	transitCarrierIndEnum;
	private InterNwIndEnum					termInterEncounterInd;
	private ISDNUserPartIndEnum				termIsupLinkInd;
	private ISDNAccessIndEnum				termIsdnAccessInd;
	private LinkedList<CarrierInformation>	carrierInfoList;
	private LinkedList<CarrierInformation>	FwdCarrierInfoList;
	private Date							callConnectDateTime;
	private transient Date					callDisconnectDateTime;
	private transient String				recipientScpCic;
	private boolean							scpDonor;											//True if SCP is a donor
	//Please don't make correlationId transient
	private int								correlationId;
	private transient float[]					chargingInterval;

	private Map<String, Object>				serviceState;
	private transient List<String>			lsResultData;
	private List<String>			cvList;
	private transient int					lsId;
	private transient String				executionCommand;

	private transient CallData				nextCallData;
	private volatile boolean				callDequeued;
	private volatile boolean				dequeuedCallProc;
	private volatile boolean				callEnqueued;
	private long							msTimeToTimeout;
	private long							enqueueTimeMillis;
	private long							dequeueTimeMillis;

	private SIGNALING_TYPE					termSignaling		= SIGNALING_TYPE.SIP_T;
	private transient DESTINATION_TYPE		termType			= DESTINATION_TYPE.ENDPOINT;
	private transient String				diversionReason;
	private transient String				userOriginatingArea	= "";
	private transient int					userAnnType;
	private transient int					activityTestInvokeId;
	private transient String				httpResponse;
	private transient String				httpResponseHeader;
	private transient boolean				notAnFtCall;
	
	private int redirectionCounter;
	private PhoneNumber redirectingNumber;

	//charging
	public static enum CHARGING_TYPE {
		CALLED_PARTY, CALLING_PARTY, SETUP, SPLIT
	}

	public static enum FORWARD_CALL_TYPE {
		NOT_SUBSCRIBED, CONDITIONAL_TF, CONDITIONAL_PARTIAL_TF,UNCONDITIONAL,BUSY,NO_ANSWER
	}

	private int					failedCallInd;
	private InterNwIndEnum		origInterEncounterInd;
	private ISDNUserPartIndEnum	origIsupLinkInd;
	private String				chargeId				= "";
	private int					assistInd;
	private int					dirServiceMultilevel;
	private int					attemptedInd			= 4;
	private String				audioFileType			= "";
	private String				annInfoNumber			= "";
	private int					codingStandard;
	private int					generationSource		= 3;
	private int					reconnectionCount		= -1;
	private int					interconnectionCount;
	private int					vpnCallType;
	private int					vpnConnectionType;
	private int					vpnAccessType;
	private String				origExtensionNumber;
	private String				termExtensionNumber;
	private String				origCGNumber;
	private String				termCGNumber;
	private int					npaLength;

	private int					tariffType;
	private int					hourOfDay;
	private String				distanceLevel			= "";
	private int					remoteControlIndicator;
	private String				calledPartyType;
	private boolean				callForwarded;
	private CHARGING_TYPE		chargingType;
	private FORWARD_CALL_TYPE	forwardCallType;

	/* The string is used to store the path for recorded file in TUI flow */
	private transient String	recAnnPath;
	private transient String	vxmlPath;

	private String				cccChargeId				= "";
	private String				cccReceivedInfo			= "";
	private String				cccOption				= "";

	private String				cdrCalledId				= "";
	private String				cdrCalledInNumber		= "";
	private String				scpRecipientPrefix;

	// Flag to determine to set cause value from the oAbondon or oDisconnect
	private boolean				causeValueFromMsg;

	//JTI parameters
	private String				calledLsSubInd;
	private String				termBypassInd;
	private String				termIGSInd;
	private String				cldMemStsInfo;
	private String				cldMemStsInd;

	//saneja @bug 10406[
	private int					rxDialoguePrimitiveType;
	private int					disconnectConnType;
	private int					lastRxInvokeId;

	private int					lastInvokeIdRangeStart	= 0;
	private int					lastInvokeIdRangeEnd	= 0;

	private String				switchCode;

	private boolean				isDfcRequired			= true;

	private Date				intermediateCdrStartTime;
	
	/**
	 * Added following fields for cdiv service
	 */
	private String  origCarrierId;
	
	private boolean sendHistoryInfo=false;
	
	private String historyInfo=null;
	
	private boolean  sendContactorNumberInSIP=false;

	private String termCarrierId;
	
	private boolean regenerateFwdCallInd=false;
	
	private boolean overrideFwdCallInd=false;
	
	private ChargeIndEnum chargeIndication;
	
	private boolean sendDummySuccessResponse;
	
	private PhoneNumber originatingCalled;
	
	private boolean sendCPGAlternateRouting =false;
	
	private boolean sendSdpAlternateRouting =false;
	
	private boolean isSendIsupContractorNumber=false;
	
	private RedirectingReasonEnum redirectionReason=null;
	
	private boolean sendChargingInfo=true;
	
	private boolean sendAdditionalCalledUser=true;
	
	private RedirectionInformation redirectionInfo=null;

	private boolean isSendChargeMsg=false;
	
	private boolean isSendRedirectionNum=true;
	
	private boolean isSendRedirectionInfo=true;
	
	private boolean isSendOriginalCalled=true;
	
	private String dummySdp;
	
	private boolean cvReceivedInAcmCpg=false;
	private boolean cancelOnCvInAcmCpg=false;
	
	private boolean sendIsupPartTransparenly =false;
	
	private int relCodingStnd=-1;
	
	private boolean interSvcCall=false;
	
	private String callingPartySubAddress=null;
	
	private boolean sendCallingPartyInfo=false;
	
	private AnnSpec origAnnSpec=null;
	
    private AnnSpec termAnnSpec=null;
	
	private String origCollectedDigits=null;
	
	private String termCollectedDigits=null;
	
	private CallingPartySubAddrEncodingEnum callingPartysubAddrEncoding;
	
	private TypeOfSubaddress callingPartySubaddressType;
	
	private boolean psEligible = false; 
	
	private boolean psRedirectionAnswer = false;
	
	private boolean psExecuted = false;
	
	private int disconnectPauseTimer ;
	
	private int invokePauseTimer;
	
	private String faxToneSignal;
	
	private int busyFeature ;
	
	private int notReachableFeature;
	
	private boolean cvReceivedForPS;
	
	private boolean cvForUnavailable;

	private String	recievedInfoTone;
	
	public AnnSpec getOrigAnnSpec() {
		return origAnnSpec;
	}

	public void setOrigAnnSpec(AnnSpec origAnnSpec) {
		this.origAnnSpec = origAnnSpec;
	}

	public AnnSpec getTermAnnSpec() {
		return termAnnSpec;
	}

	public void setTermAnnSpec(AnnSpec termAnnSpec) {
		this.termAnnSpec = termAnnSpec;
	}

	public String getOrigCollectedDigits() {
		return origCollectedDigits;
	}

	public void setOrigCollectedDigits(String origCollectedDigits) {
		this.origCollectedDigits = origCollectedDigits;
	}

	public String getTermCollectedDigits() {
		return termCollectedDigits;
	}

	public void setTermCollectedDigits(String termCollectedDigits) {
		this.termCollectedDigits = termCollectedDigits;
	}
	
	/*
	 * Ends here
	 */

	//added to support only valid ERB events which are armed in RRBE are processed
	public static enum ERB_TYPE {
		ERB_ORIGATTEMPTAUTH, ERB_ANALYZED, ERB_BUSY, ERB_NO_ANSWER, ERB_ANSWER, ERB_DISCONNECT, ERB_ABANDON,
	}

	private Set<ERB_TYPE>	erbTypeSet	= null;
	private String msResultStatus;
	private boolean playTermAnnOnConnect;


	/**
	 * @param isDfcRequired
	 *            the isDfcRequired to set
	 */
	public void setDfcRequired(boolean isDfcRequired) {
		this.isDfcRequired = isDfcRequired;
	}

	/**
	 * @param intermediateCdrStartTime
	 *            the intermediateCdrStartTime to set
	 */
	public void setIntermediateCdrStartTime(Date intermediateCdrStartTime) {
		this.intermediateCdrStartTime = intermediateCdrStartTime;
	}

	/**
	 * @return the intermediateCdrStartTime
	 */
	public Date getIntermediateCdrStartTime() {
		return intermediateCdrStartTime;
	}

	/**
	 * @return the isDfcRequired
	 */
	public boolean isDfcRequired() {
		return isDfcRequired;
	}

	/**
	 * @param switchCode
	 *            the switchCode to set
	 */
	public void setSwitchCode(String switchCode) {
		this.switchCode = switchCode;
	}

	/**
	 * @return the switchCode
	 */
	public String getSwitchCode() {
		return switchCode;
	}

	/**
	 * @return the lastInvokeIdRangeend
	 */
	public int getLastSentInvokeId() {
		return invokeId;
	}

	/**
	 * @param lastInvokeIdRangeend
	 *            the lastInvokeIdRangeend to set
	 */
	public void setLastInvokeIdRangeEnd(int lastInvokeIdRangeEnd) {
		this.lastInvokeIdRangeEnd = lastInvokeIdRangeEnd;
	}

	/**
	 * @return the lastInvokeIdRangeend
	 */
	public int getLastInvokeIdRangeEnd() {
		return lastInvokeIdRangeEnd;
	}

	/**
	 * @param lastInvokeIdRangeStart
	 *            the lastInvokeIdRangeStart to set
	 */
	public void setLastInvokeIdRangeStart(int lastInvokeIdRangeStart) {
		this.lastInvokeIdRangeStart = lastInvokeIdRangeStart;
	}

	/**
	 * @return the lastInvokeIdRangeStart
	 */
	public int getLastInvokeIdRangeStart() {
		return lastInvokeIdRangeStart;
	}

	/**
	 * @param lastInvokeId
	 *            the lastInvokeId to set
	 */
	public void setLastRxInvokeId(int lastInvokeId) {
		this.lastRxInvokeId = lastInvokeId;
	}

	/**
	 * @return the lastInvokeId
	 */
	public int getLastRxInvokeId() {
		return lastRxInvokeId;
	}

	/**
	 * @param erbSetType
	 *            the erbSetType to set
	 */
	public void setErbTypeSet(Set<ERB_TYPE> erbTypeSet) {
		this.erbTypeSet = erbTypeSet;
	}

	/**
	 * @return the erbSetType
	 */
	public Set<ERB_TYPE> getErbTypeSet() {
		return erbTypeSet;
	}

	//]closed saneja @bug 10406

	public CallData(PhoneNumber callingNumber, PhoneNumber calledNumber, String appSessionId,
					String origCallId) {
		this.callingNumber = callingNumber;
		this.calledNumber = calledNumber;
		this.origLegCallId = origCallId;
		this.appSessionId = appSessionId;
		this.callStartDate = new Date();
		this.serviceFlowMsg = new StringBuilder();
		this.ingressNetworkType = INGRESS_NETWORK_TYPE.INTRA_NETWORK;

		serviceState = new HashMap<String, Object>();
		transientAttribute = new HashMap<Object, Object>();
		externalCdrReqd = false;

		dialogAndCallId = inapDialogueId + "-" + origLegCallId;
	}

	public CallData(int dialogueId) {
		this.callStartDate = new Date();
		this.serviceFlowMsg = new StringBuilder();
		this.ingressNetworkType = INGRESS_NETWORK_TYPE.INTER_NETWORK;
		this.inapDialogueId = dialogueId;

		serviceState = new HashMap<String, Object>();
		transientAttribute = new HashMap<Object, Object>();
		externalCdrReqd = true;

		//saneja @bug10406 [
		rxDialoguePrimitiveType = -1;
		//]saneja @bug 10406
		
		dialogAndCallId = inapDialogueId + "-" + origLegCallId;
	}

	public int getOriginPointCode() {
		return originPointCode;
	}

	public void setOriginPointCode(int originPointCode) {
		this.originPointCode = originPointCode;
	}
	
	public String getOriginIPAddress() {
		return originIPAddress;
	}

	public void setOriginIPAddress(String originIPAddress) {
		this.originIPAddress = originIPAddress;
	}

	public int getOriginPort() {
		return originPort;
	}

	public void setOriginPort(int originPort) {
		this.originPort = originPort;
	}

	public StringBuilder getServiceFlowMsg() {
		return serviceFlowMsg;
	}

	public void appendServiceFlowMsg(String serviceFlowMsg) {
		this.serviceFlowMsg.append(serviceFlowMsg);
	}

	public void setServiceFlowMsg(StringBuilder serviceFlowMsg) {
		this.serviceFlowMsg = serviceFlowMsg;
	}

	public boolean isAuditFlag() {
		return auditFlag;
	}

	public void setAuditFlag(boolean auditFlag) {
		this.auditFlag = auditFlag;
	}

	public PhoneNumber getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(PhoneNumber callingPhoneNumber) {
		this.callingNumber = callingPhoneNumber;
	}

	public PhoneNumber getCalledNumber() {
		return calledNumber;
	}

	public void setCalledNumber(PhoneNumber calledPhoneNumber) {
		this.calledNumber = calledPhoneNumber;
	}

	public PhoneNumber getModifiedCallingNumber() {
		return modifiedCallingNumber;
	}

	public void setModifiedCallingNumber(PhoneNumber modifiedCallingNumber) {
		this.modifiedCallingNumber = modifiedCallingNumber;
	}

	public PhoneNumber getDestinationNumber() {
		return destinationNumber;
	}

	public void setDestinationNumber(PhoneNumber destinationNumber) {
		this.destinationNumber = destinationNumber;
	}

	public void setServiceStateData(String key, Object value) {
		serviceState.put(key, value);
	}

	public Object getServiceStateData(String key) {
		return serviceState.get(key);
	}

	public void removeServiceStateData(String key) {
		serviceState.remove(key);
	}

	public String getTermLegCallId() {
		return termLegCallId;
	}

	public void setTermLegCallId(String outLegCallId) {
		this.termLegCallId = outLegCallId;
	}
	
	public String getControlDigit(){
		return controlDigit;
	}
	
	public void setControlDigit(String controlDigit){
		this.controlDigit=controlDigit;
	}

	public String getOrigLegCallId() {
		return dialogAndCallId;
	}

	public void setOrigLegCallId(String inLegCallId) {
		origLegCallId = inLegCallId;
		dialogAndCallId = inapDialogueId + "-" + origLegCallId;
	}

	public Date getCallStartDate() {
		return callStartDate;
	}

	public String getAppSessionId() {
		return appSessionId;
	}

	public void setAppSessionId(String appSession) {
		appSessionId = appSession;
	}

	public int getCauseCode() {
		return causeCode;
	}

	public void setCauseCode(int causeCode) {
		this.causeCode = causeCode;
	}

	public String getIvrLegCallId() {
		return ivrLegCallId;
	}

	public void setIvrLegCallId(String ivrLegCallId) {
		this.ivrLegCallId = ivrLegCallId;
	}

	public short getActivityContextVersion() {
		return activityContextVersion;
	}

	public void setActivityContextVersion(short activityContextVersion) {
		this.activityContextVersion = activityContextVersion;
	}

	public int getCpc() {
		return cpc;
	}

	public void setCpc(int cpc) {
		this.cpc = cpc;
	}

	public int getAdditionalCpc1() {
		return additionalCpc1;
	}

	public void setAdditionalCpc1(int additionalCpc1) {
		this.additionalCpc1 = additionalCpc1;
	}

	public int getAdditionalCpc2() {
		return additionalCpc2;
	}
	
	public int getAdditionalCpc3() {
		return additionalCpc3;
	}

	public void setAdditionalCpc3(int additionalCpc3) {
		this.additionalCpc3 = additionalCpc3;
	}

	public String getChargingArea() {
		return chargingArea;
	}

	public void setChargingArea(String chargingArea) {
		this.chargingArea = chargingArea;
	}
	
	public String getTtcSspChargingArea() {
		return ttcSspChargingArea;
	}

	public void setTtcSspChargingArea(String ttcSspChargingArea) {
		this.ttcSspChargingArea = ttcSspChargingArea;
	}

	public void setAdditionalCpc2(int additionalCpc2) {
		this.additionalCpc2 = additionalCpc2;
	}

	public INGRESS_NETWORK_TYPE getIngressNetworkType() {
		return ingressNetworkType;
	}

	public void setIngressNetworkType(INGRESS_NETWORK_TYPE ingressNetworkType) {
		this.ingressNetworkType = ingressNetworkType;
	}

	public int getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(int serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getDpci() {
		return dpci;
	}

	public void setDpci(String dpci) {
		this.dpci = dpci;
	}

	public PhoneNumber getGenericNumber() {
		return genericNumber;
	}

	public void setGenericNumber(PhoneNumber genericNumber) {
		this.genericNumber = genericNumber;
	}

	public int getTmr() {
		return tmr;
	}

	public void setTmr(int tmr) {
		this.tmr = tmr;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public PhoneNumber getModifiedCalledNumber() {
		return modifiedCalledNumber;
	}

	public void setModifiedCalledNumber(PhoneNumber modifiedCalledNumber) {
		this.modifiedCalledNumber = modifiedCalledNumber;
	}

		/**
		 * @return the apiCounter
		 */
		public int getApiCounter() {
			return apiCounter++;
		}
	
		/**
		 * @return the actionCounter
		 */
		public int getActionCounter() {
			return actionCounter++;
		}
	
		/**
		 * @param apiResource
		 *            the apiResource to set
		 */
		public void setApiResource(String apiResource) {
			this.apiResource = apiResource;
		}
	
		/**
		 * @return the apiResource
		 */
		public String getApiResource() {
			return apiResource;
		}
	
		/**
		 * @param actionResource
		 *            the actionResource to set
		 */
		public void setActionResource(String actionResource) {
			this.actionResource = actionResource;
		}
	
		/**
		 * @return the actionResource
		 */
		public String getActionResource() {
			return actionResource;
		}
	
		/**
		 * @param apiActionFlow
		 *            the apiActionFlow to set
		 */
		public void appendApiActionFlow(String apiActionFlow) {
			this.apiActionFlow.append(apiActionFlow);
		}
	
		/**
		 * @param apiActionFlow
		 *            the apiActionFlow to set
		 */
		public void setApiActionFlow(StringBuilder apiActionFlow) {
			this.apiActionFlow = apiActionFlow;
		}
	
		/**
		 * @return the apiActionFlow
		 */
		public StringBuilder getApiActionFlow() {
			return apiActionFlow;
		}

	/**
	 * @param contractorNumber
	 *            the contractorNumber to set
	 */
	public void setContractorNumber(PhoneNumber contractorNumber) {
		this.contractorNumber = contractorNumber;
	}

	/**
	 * @return the contractorNumber
	 */
	public PhoneNumber getContractorNumber() {
		return contractorNumber;
	}

	/**
	 * @param dialedDigit
	 *            the dialedDigit to set
	 */
	public void setDialedDigit(PhoneNumber dialedDigit) {
		this.dialedDigit = dialedDigit;
	}

	/**
	 * @return the dialedDigit
	 */
	public PhoneNumber getDialedDigit() {
		return dialedDigit;
	}

	/**
	 * @param serviceIndicator
	 *            the serviceIndicator to set
	 */
	public void setServiceIndicator(SERVICEINDICATOR serviceIndicator) {
		this.serviceIndicator = serviceIndicator;
	}

	/**
	 * @return the serviceIndicator
	 */
	public SERVICEINDICATOR getServiceIndicator() {
		return serviceIndicator;
	}

	/**
	 * @param pbtSubaddress
	 *            the pbtSubaddress to set
	 */
	public void setPbtSubaddress(String pbtSubaddress) {
		this.pbtSubaddress = pbtSubaddress;
	}

	/**
	 * @return the pbtSubaddress
	 */
	public String getPbtSubaddress() {
		return pbtSubaddress;
	}

	/**
	 * @param fci
	 *            the fci to set
	 */
	public void setFci(int fci) {
		this.fci = fci;
	}

	/**
	 * @return the fci
	 */
	public int getFci() {
		return fci;
	}

	public void setInapCallState(INAP_CALL_STATES inapCs) {
		this.inapCallState = inapCs;
	}

	public INAP_CALL_STATES getInapCallState() {
		return this.inapCallState;
	}

	public void setOrigSipCallState(SIP_CALL_STATES sipCs) {
		this.origSipCallState = sipCs;
	}

	public SIP_CALL_STATES getOrigSipCallState() {
		return this.origSipCallState;
	}

	public void setTermSipCallState(SIP_CALL_STATES sipCs) {
		this.termSipCallState = sipCs;
	}

	public SIP_CALL_STATES getTermSipCallState() {
		return this.termSipCallState;
	}

	public int getDialogueId() {
		return inapDialogueId;
	}

	public void setDialoguePortion(DialoguePortion dialoguePortion) {
		inapDialoguePortion = dialoguePortion;
	}

	public DialoguePortion getDialoguePortion() {
		return inapDialoguePortion;
	}

	public int getNextInvokeId() {
		return ++invokeId;
	}

	public TransitCarrierIndEnum getTransitCarrierIndEnum() {
		return transitCarrierIndEnum;
	}

	public void setTransitCarrierIndEnum(TransitCarrierIndEnum value) {
		transitCarrierIndEnum = value;
	}

	public String getTermChargingArea() {
		return termChargingArea;
	}

	public void setTermChargingArea(String chargingArea) {
		this.termChargingArea = chargingArea;
	}

	public InterNwIndEnum getTermInterEncounterInd() {
		return termInterEncounterInd;
	}

	public void setTermInterEncounterInd(InterNwIndEnum value) {
		termInterEncounterInd = value;
	}

	public ISDNUserPartIndEnum getTermIsupLinkInd() {
		return termIsupLinkInd;
	}

	public void setTermIsupLinkInd(ISDNUserPartIndEnum value) {
		termIsupLinkInd = value;
	}

	public ISDNAccessIndEnum getTermIsdnAccessInd() {
		return termIsdnAccessInd;
	}

	public void setTermIsdnAccessInd(ISDNAccessIndEnum value) {
		termIsdnAccessInd = value;
	}

	public LinkedList<CarrierInformation> getCarrierInformationList() {
		return carrierInfoList;
	}

	public void setCarrierInformationList(LinkedList<CarrierInformation> value) {
		carrierInfoList = value;
	}

	public LinkedList<CarrierInformation> getFwdCarrierInformationList() {
		return FwdCarrierInfoList;
	}

	public void setFwdCarrierInformationList(LinkedList<CarrierInformation> value) {
		FwdCarrierInfoList = value;
	}

	public void addToFwdCarrierInformationList(LinkedList<CarrierInformation> value) {
		if (FwdCarrierInfoList == null) {
			setFwdCarrierInformationList(value);
		} else {
			FwdCarrierInfoList.addAll(value);
		}
	}

	public void setCallConnectDateTime(Date value) {
		callConnectDateTime = value;
	}

	public Date getCallConnectDateTime() {
		return callConnectDateTime;
	}

	public void setCallDisconnectDateTime(Date value) {
		callDisconnectDateTime = value;
	}

	public Date getCallDisconnectDateTime() {
		return callDisconnectDateTime;
	}

	public void setRecipientScpCic(String value) {
		recipientScpCic = value;
	}

	public String getRecipientScpCic() {
		return recipientScpCic;
	}

	public int getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(int value) {
		correlationId = value;
	}

	public int getUserAnnType() {
		return userAnnType;
	}

	public void setUserAnnType(int value) {
		userAnnType = value;
	}

	public void setLsResultData(List<String> lsResultData) {
		this.lsResultData = lsResultData;
	}

	public List<String> getLsResultData() {
		return lsResultData;
	}
	
	public void setCvList(List<String> cvList) {
		this.cvList = cvList;
	}

	public List<String> getCvList() {
		return cvList;
	}

	public int getLsId() {
		return lsId;
	}

	public void setLsId(int lsId) {
		this.lsId = lsId;
	}

	public String getExecutionCommand() {
		return executionCommand;
	}

	public void setExecutionCommand(String executionCommand) {
		this.executionCommand = executionCommand;
	}

	public boolean getChargeAnnFlag() {
		return chargeAnnFlag;
	}

	public void setChargeAnnFlag(boolean value) {
		chargeAnnFlag = value;
	}

	public Action getLastAction() {
		return lastAction;
	}

	public void setLastAction(Action value) {
		lastAction = value;
	}

	public void setChargingInterval(float[] value) {
		chargingInterval = value;
	}

	public float[] getChargingInterval() {
		return chargingInterval;
	}

	public int getReleaseCauseValue() {
		return releaseCauseValue;
	}

	public void setReleaseCauseValue(int value) {
		this.releaseCauseValue = value;
	}

	public int getFixedPhAdditionalCpc1() {
		return fixedPhAdditionalCpc1;
	}

	public void setFixedPhAdditionalCpc1(int fixedPhAdditionalCpc1) {
		this.fixedPhAdditionalCpc1 = fixedPhAdditionalCpc1;
	}

	public boolean isCalledInNumberExist() {
		return calledInNumberExist;
	}

	public void setCalledInNumberExist(boolean calledInNumberExist) {
		this.calledInNumberExist = calledInNumberExist;
	}
		
	public long getLastINFOTimestamp() {
		return lastINFOTimestamp;
	}

	public void setLastINFOTimestamp(long lastINFOTimestamp) {
		this.lastINFOTimestamp = lastINFOTimestamp;
	}
	
	public boolean isPathSwitchInprogress() {
		return pathSwitchInprogress;
	}

	public void setPathSwitchInprogress(boolean pathSwitchInprogress) {
		this.pathSwitchInprogress = pathSwitchInprogress;
	}

	public void setNextCallData(CallData nextCallData) {
		this.nextCallData = nextCallData;
	}

	public CallData getNextCallData() {
		return nextCallData;
	}

	public String getTcapSessionId() {
		return tcapSessionId;
	}
	
	public void setTcapSessionId(String sessionId) {
		this.tcapSessionId = sessionId;
	}
	
	public void setFailedCallInd(int failedCallInd) {
		this.failedCallInd = failedCallInd;
	}

	public int getFailedCallInd() {
		return failedCallInd;
	}

	public InterNwIndEnum getOrigInterEncounterInd() {
		return origInterEncounterInd;
	}

	public void setOrigInterEncounterInd(InterNwIndEnum value) {
		origInterEncounterInd = value;
	}

	public ISDNUserPartIndEnum getOrigIsupLinkInd() {
		return origIsupLinkInd;
	}

	public void setOrigIsupLinkInd(ISDNUserPartIndEnum value) {
		origIsupLinkInd = value;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setAssistInd(int assistInd) {
		this.assistInd = assistInd;
	}

	public int getAssistInd() {
		return assistInd;
	}

	public void setDirServiceMultilevel(int dirServiceMultilevel) {
		this.dirServiceMultilevel = dirServiceMultilevel;
	}

	public int getDirServiceMultilevel() {
		return dirServiceMultilevel;
	}

	public void setAttemptedInd(int attemptedInd) {
		this.attemptedInd = attemptedInd;
	}

	public int getAttemptedInd() {
		return attemptedInd;
	}

	public void setAudioFileType(String audioFileType) {
		this.audioFileType = audioFileType;
	}

	public String getAudioFileType() {
		return audioFileType;
	}

	public void setAnnInfoNumber(String annInfoNumber) {
		this.annInfoNumber = annInfoNumber;
	}

	public String getAnnInfoNumber() {
		return annInfoNumber;
	}

	public void setCodingStandard(int codingStandard) {
		this.codingStandard = codingStandard;
	}

	public int getCodingStandard() {
		return codingStandard;
	}

	public void setGenerationSource(int generationSource) {
		this.generationSource = generationSource;
	}

	public int getGenerationSource() {
		return generationSource;
	}

	public void incrementReconnectionCount() {
		reconnectionCount++;
	}

	public int getReconnectionCount() {
		return reconnectionCount;
	}

	public void incrementInterconnectionCount() {
		interconnectionCount++;
	}

	public int getInterconnectionCount() {
		return interconnectionCount;
	}

	public void setVpnCallType(int vpnCallType) {
		this.vpnCallType = vpnCallType;
	}

	public int getVpnCallType() {
		return vpnCallType;
	}

	public void setVpnConnectionType(int vpnConnectionType) {
		this.vpnConnectionType = vpnConnectionType;
	}

	public int getVpnConnectionType() {
		return vpnConnectionType;
	}

	public void setVpnAccessType(int vpnAccessType) {
		this.vpnAccessType = vpnAccessType;
	}

	public int getVpnAccessType() {
		return vpnAccessType;
	}

	public void setOrigExtensionNumber(String origExtensionNumber) {
		this.origExtensionNumber = origExtensionNumber;
	}

	public String getOrigExtensionNumber() {
		return origExtensionNumber;
	}

	public void setTermExtensionNumber(String termExtensionNumber) {
		this.termExtensionNumber = termExtensionNumber;
	}

	public String getTermExtensionNumber() {
		return termExtensionNumber;
	}

	public void setOrigCGNumber(String origCGNumber) {
		this.origCGNumber = origCGNumber;
	}

	public String getOrigCGNumber() {
		return origCGNumber;
	}

	public void setTermCGNumber(String termCGNumber) {
		this.termCGNumber = termCGNumber;
	}

	public String getTermCGNumber() {
		return termCGNumber;
	}

	public void setNpaLength(int npaLength) {
		this.npaLength = npaLength;
	}

	public int getNpaLength() {
		return npaLength;
	}

	public void setTariffType(int tariffType) {
		this.tariffType = tariffType;
	}

	public int getTariffType() {
		return tariffType;
	}

	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}

	public void setDistanceLevel(String distanceLevel) {
		this.distanceLevel = distanceLevel;
	}

	public String getDistanceLevel() {
		return distanceLevel;
	}

	public void setRemoteControlIndicator(int remoteControlIndicator) {
		this.remoteControlIndicator = remoteControlIndicator;
	}

	public int getRemoteControlIndicator() {
		return remoteControlIndicator;
	}

	public void setCalledPartyType(String calledPartyType) {
		this.calledPartyType = calledPartyType;
	}

	public String getCalledPartyType() {
		return calledPartyType;
	}

	public String getCccChargeId() {
		return cccChargeId;
	}

	public void setCccChargeId(String cccChargeId) {
		this.cccChargeId = cccChargeId;
	}

	public String getCccReceivedInfo() {
		return cccReceivedInfo;
	}

	public void setCccReceivedInfo(String cccRecievedInfo) {
		this.cccReceivedInfo = cccRecievedInfo;
	}

	public void setCccOption(String cccOption) {
		this.cccOption = cccOption;
	}

	public String getCccOption() {
		return cccOption;
	}

	public void setCallForwarded(boolean callForwarded) {
		this.callForwarded = callForwarded;
	}

	public boolean getCallForwarded() {
		return callForwarded;
	}

	public CHARGING_TYPE getChargingType() {
		return chargingType;
	}

	public void setChargingType(CHARGING_TYPE value) {
		chargingType = value;
	}

	public FORWARD_CALL_TYPE getForwardCallType() {
		return forwardCallType;
	}

	public void setForwardCallType(FORWARD_CALL_TYPE value) {
		forwardCallType = value;
	}

	public void setExternalCdrRequired(boolean value) {
		externalCdrReqd = value;
	}

	public boolean getExternalCdrRequired() {
		return externalCdrReqd;
	}

	public void setCallDequeued(boolean value) {
		callDequeued = value;
	}

	public boolean isCallDequeued() {
		return callDequeued;
	}

	public void incrementIntermediateCdr() {
		intermediateCdrCount++;
	}

	public int getIntermediateCdrCount() {
		return intermediateCdrCount;
	}

	public SIGNALING_TYPE getTermSignaling() {
		return termSignaling;
	}

	public void setTermSignaling(SIGNALING_TYPE value) {
		termSignaling = value;
	}

	public DESTINATION_TYPE getTermType() {
		return termType;
	}

	public void setTermType(DESTINATION_TYPE value) {
		termType = value;
	}

	public String getDiversionReason() {
		return diversionReason;
	}

	public void setDiversionReason(String value) {
		diversionReason = value;
	}

	public String getUserOrignatingArea() {
		return userOriginatingArea;
	}

	public void setUserOrignatingArea(String value) {
		userOriginatingArea = value;
	}

	public boolean getFinalCdrWritten() {
		return finalCdrWritten;
	}

	public void setFinalCdrWritten(boolean value) {
		finalCdrWritten = value;
	}

	public int getActivityTestInvokeId() {
		return activityTestInvokeId;
	}

	public void setActivityTestInvokeId(int value) {
		activityTestInvokeId = value;;
	}

	public int getIvrConnectionCount() {
		return ivrConnectionCount;
	}

	public void incrementIvrConnectionCount() {
		ivrConnectionCount++;
	}

	public String getRecAnnPath() {
		return recAnnPath;
	}

	public void setRecAnnPath(String recAnnPath) {
		this.recAnnPath = recAnnPath;
	}

	public String getVxmlPath() {
		return vxmlPath;
	}

	public void setVxmlPath(String value) {
		this.vxmlPath = value;
	}

	public String getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(String value) {
		httpResponse = value;
	}

	public boolean isScpDonor() {
		return scpDonor;

	}

	public void setScpDonor(boolean scrDonor) {
		this.scpDonor = scrDonor;
	}

	public void setTraceFlag(boolean traceFlag) {
		this.traceFlag = traceFlag;
	}

	public boolean isTraceFlag() {
		return traceFlag;
	}

	public void setTraceMsg(StringBuilder traceMsg) {
		this.traceMsg = traceMsg;
	}

	public void clearTraceMsg() {
		this.traceMsg.delete(0, traceMsg.length());
	}

	public StringBuilder getTraceMsg() {
		return traceMsg;
	}

	public void appendTraceMsg(String traceMsg) {
		this.traceMsg.append(traceMsg);
	}

	public String getCdrCalledId() {
		return cdrCalledId;
	}

	public void setCdrCalledId(String cdrCalledId) {
		this.cdrCalledId = cdrCalledId;
	}

	public boolean isFlexChargingApplied() {
		return flexChargingApplied;
	}

	public void setFlexChargingApplied(boolean flexChargingApplied) {
		this.flexChargingApplied = flexChargingApplied;
	}

	public int getRelLocationField() {
		return relLocationField;
	}

	public void setRelLocationField(int relLocationField) {
		this.relLocationField = relLocationField;
	}

	/**
	 * @param scpRecipientPrefix
	 *            the scpRecipientPrefix to set
	 */
	public void setScpRecipientPrefix(String scpRecipientPrefix) {
		this.scpRecipientPrefix = scpRecipientPrefix;
	}

	/**
	 * @return the scpRecipientPrefix
	 */
	public String getScpRecipientPrefix() {
		return scpRecipientPrefix;
	}

	/**
	 * @return the httpResponseHeader
	 */
	public String getHttpResponseHeader() {
		return httpResponseHeader;
	}

	/**
	 * @param httpResponseHeader
	 *            the httpResponseHeader to set
	 */
	public void setHttpResponseHeader(String httpResponseHeader) {
		this.httpResponseHeader = httpResponseHeader;
	}

	public boolean isCauseValueFromMsg() {
		return causeValueFromMsg;
	}

	public void setCauseValueFromMsg(boolean causeValueFromMsg) {
		this.causeValueFromMsg = causeValueFromMsg;
	}

	public boolean isDequeuedCallProc() {
		return dequeuedCallProc;
	}

	public void setDequeuedCallProc(boolean dequeuedCallProc) {
		this.dequeuedCallProc = dequeuedCallProc;
	}

	public boolean isCallEnqueued() {
		return callEnqueued;
	}

	public void setCallEnqueued(boolean callEnqueued) {
		this.callEnqueued = callEnqueued;
	}

	public long getMsTimeToTimeout() {
		return msTimeToTimeout;
	}

	public void setMsTimeToTimeout(long msTimeToTimeout) {
		this.msTimeToTimeout = msTimeToTimeout;
	}

	public long getEnqueueTimeMillis() {
		return enqueueTimeMillis;
	}

	public void setEnqueueTimeMillis(long enqueueTimeMillis) {
		this.enqueueTimeMillis = enqueueTimeMillis;
	}

	public long getDequeueTimeMillis() {
		return dequeueTimeMillis;
	}

	public void setDequeueTimeMillis(long dequeueTimeMillis) {
		this.dequeueTimeMillis = dequeueTimeMillis;
	}

	/**
	 * @param calledLsSubInd
	 *            the calledLsSubInd to set
	 */
	public void setCalledLsSubInd(String calledLsSubInd) {
		this.calledLsSubInd = calledLsSubInd;
	}

	/**
	 * @return the calledLsSubInd
	 */
	public String getCalledLsSubInd() {
		return calledLsSubInd;
	}

	/**
	 * @param termBypassInd
	 *            the termBypassInd to set
	 */
	public void setTermBypassInd(String termBypassInd) {
		this.termBypassInd = termBypassInd;
	}

	/**
	 * @return the termBypassInd
	 */
	public String getTermBypassInd() {
		return termBypassInd;
	}

	/**
	 * @param termIGSInd
	 *            the termIGSInd to set
	 */
	public void setTermIGSInd(String termIGSInd) {
		this.termIGSInd = termIGSInd;
	}

	/**
	 * @return the termIGSInd
	 */
	public String getTermIGSInd() {
		return termIGSInd;
	}

	/**
	 * @param cldMemStsInfo
	 *            the cldMemStsInfo to set
	 */
	public void setCldMemStsInfo(String cldMemStsInfo) {
		this.cldMemStsInfo = cldMemStsInfo;
	}

	/**
	 * @return the cldMemStsInfo
	 */
	public String getCldMemStsInfo() {
		return cldMemStsInfo;
	}

	/**
	 * @param cldMemStsInd
	 *            the cldMemStsInd to set
	 */
	public void setCldMemStsInd(String cldMemStsInd) {
		this.cldMemStsInd = cldMemStsInd;
	}

	/**
	 * @return the cldMemStsInd
	 */
	public String getCldMemStsInd() {
		return cldMemStsInd;
	}

	public HttpServletResponse getAsrHttpResponse() {
		return asrHttpResponse;
	}

	public void setAsrHttpResponse(HttpServletResponse asrHttpResponse) {
		this.asrHttpResponse = asrHttpResponse;
	}

	public SccpUserAddress getSccpUserAddress() {
		return sccpUserAddress;
	}

	public void setSccpUserAddress(SccpUserAddress sccpUserAddress) {
		this.sccpUserAddress = sccpUserAddress;
	}

	public PhoneNumber getIdpCalledPartyNumber() {
		return idpCalledPartyNumber;
	}

	public void setIdpCalledPartyNumber(PhoneNumber idpCalledPartyNumber) {
		this.idpCalledPartyNumber = idpCalledPartyNumber;
	}

	//saneja @bug 10406[

	/**
	 * @param rxDialoguePrimitiveType
	 *            the rxDialoguePrimitiveType to set
	 */
	public void setRxDialoguePrimitiveType(int rxDialoguePrimitiveType) {
		this.rxDialoguePrimitiveType = rxDialoguePrimitiveType;
	}

	/**
	 * @return the rxDialoguePrimitiveType
	 */
	public int getRxDialoguePrimitiveType() {
		return rxDialoguePrimitiveType;
	}

	/**
	 * @param disconnectConnType
	 *            the disconnectConnType to set
	 */
	public void setDisconnectConnType(int disconnectConnType) {
		this.disconnectConnType = disconnectConnType;
	}

	/**
	 * @return the disconnectConnType
	 */
	public int getDisconnectConnType() {
		return disconnectConnType;
	}

	/**
	 * @param cdrCalledInNumber
	 *            the cdrCalledInNumber to set
	 */
	public void setCdrCalledInNumber(String cdrCalledInNumber) {
		this.cdrCalledInNumber = cdrCalledInNumber;
	}

	/**
	 * @return the cdrCalledInNumber
	 */
	public String getCdrCalledInNumber() {
		return cdrCalledInNumber;
	}

	public boolean isNotAnFtCall() {
		return notAnFtCall;
	}

	public void setNotAnFtCall(boolean notAnFtCall) {
		this.notAnFtCall = notAnFtCall;
	}

	public int getReasonForRelease() {
		return reasonForRelease;
	}

	public void setReasonForRelease(int reasonForRelease) {
		this.reasonForRelease = reasonForRelease;
	}

	public String getTermIPAddress() {
		return termIPAddress;
	}

	public void setTermIPAddress(String termIPAddress) {
		this.termIPAddress = termIPAddress;
	}

	public int getTermPort() {
		return termPort;
	}

	public void setTermPort(int termPort) {
		this.termPort = termPort;
	}
	
	//]closed saneja @bug 10406

	public Object getTransientAttribute(Object key) {
		return transientAttribute.get(key);
	}

	public void setTransientAttribute(Object key, Object value) {
		transientAttribute.put(key, value);
	}
	
	public List<Integer> getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(List<Integer> constraintId) {
		this.constraintId = constraintId;
	}
	
	public void setMsResultStatus(String msResultStatus) {
		// TODO Auto-generated method stub
		this.msResultStatus=msResultStatus;
		
	}
	
	public String getMsResultStatus() {
		return msResultStatus;
	}
	
	public boolean isSendCPGAlternateRouting() {
		return sendCPGAlternateRouting;
	}

	public void setSendCPGAlternateRouting(boolean sendCPGForAlternateRouting) {
		this.sendCPGAlternateRouting = sendCPGForAlternateRouting;
	}
	
	public boolean isSendHistoryInfo() {
		// TODO Auto-generated method stub
		return this.sendHistoryInfo;
	}

	public void setSendHistoryInfo(boolean sendHistoryInfo) {
		this.sendHistoryInfo = sendHistoryInfo;
	}
	
	
	public String getOrigCarrierId() {
		return origCarrierId;
	}

	public void setOrigCarrierId(String origCarrierId) {
		this.origCarrierId = origCarrierId;
	}

	public String getTermCarrierId() {
		return termCarrierId;
	}

	public void setTermCarrierId(String termCarrierId) {
		this.termCarrierId = termCarrierId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CallData [FwdCarrierInfoList=");
		builder.append(FwdCarrierInfoList);
		builder.append(", actionCounter=");
		builder.append(actionCounter);
		builder.append(", actionResource=");
		builder.append(actionResource);
		builder.append(", activityContextVersion=");
		builder.append(activityContextVersion);
		builder.append(", activityTestInvokeId=");
		builder.append(activityTestInvokeId);
		builder.append(", additionalCpc1=");
		builder.append(additionalCpc1);
		builder.append(", additionalCpc2=");
		builder.append(additionalCpc2);
		builder.append(", annInfoNumber=");
		builder.append(annInfoNumber);
		builder.append(", apiActionFlow=");
		builder.append(apiActionFlow);
		builder.append(", apiCounter=");
		builder.append(apiCounter);
		builder.append(", apiResource=");
		builder.append(apiResource);
		builder.append(", appSessionId=");
		builder.append(appSessionId);
		builder.append(", asrHttpResponse=");
		builder.append(asrHttpResponse);
		builder.append(", assistInd=");
		builder.append(assistInd);
		builder.append(", attemptedInd=");
		builder.append(attemptedInd);
		builder.append(", audioFileType=");
		builder.append(audioFileType);
		builder.append(", auditFlag=");
		builder.append(auditFlag);
		builder.append(", callConnectDateTime=");
		builder.append(callConnectDateTime);
		builder.append(", callDequeued=");
		builder.append(callDequeued);
		builder.append(", callDisconnectDateTime=");
		builder.append(callDisconnectDateTime);
		builder.append(", callEnqueued=");
		builder.append(callEnqueued);
		builder.append(", callForwarded=");
		builder.append(callForwarded);
		builder.append(", callStartDate=");
		builder.append(callStartDate);
		builder.append(", calledInNumberExist=");
		builder.append(calledInNumberExist);
		builder.append(", lastINFOTimestamp=");
		builder.append(lastINFOTimestamp);
		builder.append(", pathSwitchInprogress=");
		builder.append(pathSwitchInprogress);
		builder.append(", calledLsSubInd=");
		builder.append(calledLsSubInd);
		builder.append(", calledNumber=");
		builder.append(calledNumber);
		builder.append(", calledPartyType=");
		builder.append(calledPartyType);
		builder.append(", callingNumber=");
		builder.append(callingNumber);
		builder.append(", carrierInfoList=");
		builder.append(carrierInfoList);
		builder.append(", causeCode=");
		builder.append(causeCode);
		builder.append(", causeValueFromMsg=");
		builder.append(causeValueFromMsg);
		builder.append(", cccChargeId=");
		builder.append(cccChargeId);
		builder.append(", cccOption=");
		builder.append(cccOption);
		builder.append(", cccReceivedInfo=");
		builder.append(cccReceivedInfo);
		builder.append(", cdrCalledId=");
		builder.append(cdrCalledId);
		builder.append(", cdrCalledInNumber=");
		builder.append(cdrCalledInNumber);
		builder.append(", chargeAnnFlag=");
		builder.append(chargeAnnFlag);
		builder.append(", chargeId=");
		builder.append(chargeId);
		builder.append(", chargingArea=");
		builder.append(chargingArea);
		builder.append(", chargingInterval=");
		builder.append(Arrays.toString(chargingInterval));
		builder.append(", chargingType=");
		builder.append(chargingType);
		builder.append(", cldMemStsInd=");
		builder.append(cldMemStsInd);
		builder.append(", cldMemStsInfo=");
		builder.append(cldMemStsInfo);
		builder.append(", codingStandard=");
		builder.append(codingStandard);
		builder.append(", constraintId=");
		builder.append(constraintId);
		builder.append(", contractorNumber=");
		builder.append(contractorNumber);
		builder.append(", correlationId=");
		builder.append(correlationId);
		builder.append(", cpc=");
		builder.append(cpc);
		builder.append(", cvList=");
		builder.append(cvList);
		builder.append(", dequeueTimeMillis=");
		builder.append(dequeueTimeMillis);
		builder.append(", dequeuedCallProc=");
		builder.append(dequeuedCallProc);
		builder.append(", destinationNumber=");
		builder.append(destinationNumber);
		builder.append(", dialedDigit=");
		builder.append(dialedDigit);
		builder.append(", dirServiceMultilevel=");
		builder.append(dirServiceMultilevel);
		builder.append(", disconnectConnType=");
		builder.append(disconnectConnType);
		builder.append(", distanceLevel=");
		builder.append(distanceLevel);
		builder.append(", diversionReason=");
		builder.append(diversionReason);
		builder.append(", dpci=");
		builder.append(dpci);
		builder.append(", enqueueTimeMillis=");
		builder.append(enqueueTimeMillis);
		builder.append(", erbTypeSet=");
		builder.append(erbTypeSet);
		builder.append(", executionCommand=");
		builder.append(executionCommand);
		builder.append(", externalCdrReqd=");
		builder.append(externalCdrReqd);
		builder.append(", failedCallInd=");
		builder.append(failedCallInd);
		builder.append(", fci=");
		builder.append(fci);
		builder.append(", finalCdrWritten=");
		builder.append(finalCdrWritten);
		builder.append(", fixedPhAdditionalCpc1=");
		builder.append(fixedPhAdditionalCpc1);
		builder.append(", flexChargingApplied=");
		builder.append(flexChargingApplied);
		builder.append(", forwardCallType=");
		builder.append(forwardCallType);
		builder.append(", generationSource=");
		builder.append(generationSource);
		builder.append(", genericNumber=");
		builder.append(genericNumber);
		builder.append(", hourOfDay=");
		builder.append(hourOfDay);
		builder.append(", httpResponse=");
		builder.append(httpResponse);
		builder.append(", httpResponseHeader=");
		builder.append(httpResponseHeader);
		builder.append(", idpCalledPartyNumber=");
		builder.append(idpCalledPartyNumber);
		builder.append(", inapCallState=");
		builder.append(inapCallState);
		builder.append(", inapDialogueId=");
		builder.append(inapDialogueId);
		builder.append(", inapDialoguePortion=");
		builder.append(inapDialoguePortion);
		builder.append(", ingressNetworkType=");
		builder.append(ingressNetworkType);
		builder.append(", interconnectionCount=");
		builder.append(interconnectionCount);
		builder.append(", intermediateCdrCount=");
		builder.append(intermediateCdrCount);
		builder.append(", intermediateCdrStartTime=");
		builder.append(intermediateCdrStartTime);
		builder.append(", invokeId=");
		builder.append(invokeId);
		builder.append(", isDfcRequired=");
		builder.append(isDfcRequired);
		builder.append(", ivrConnectionCount=");
		builder.append(ivrConnectionCount);
		builder.append(", ivrLegCallId=");
		builder.append(ivrLegCallId);
		builder.append(", lastAction=");
		builder.append(lastAction);
		builder.append(", lastInvokeIdRangeEnd=");
		builder.append(lastInvokeIdRangeEnd);
		builder.append(", lastInvokeIdRangeStart=");
		builder.append(lastInvokeIdRangeStart);
		builder.append(", lastRxInvokeId=");
		builder.append(lastRxInvokeId);
		builder.append(", lsId=");
		builder.append(lsId);
		builder.append(", lsResultData=");
		builder.append(lsResultData);
		builder.append(", modifiedCalledNumber=");
		builder.append(modifiedCalledNumber);
		builder.append(", modifiedCallingNumber=");
		builder.append(modifiedCallingNumber);
		builder.append(", msTimeToTimeout=");
		builder.append(msTimeToTimeout);
		builder.append(", nextCallData=");
		builder.append(nextCallData);
		builder.append(", notAnFtCall=");
		builder.append(notAnFtCall);
		builder.append(", npaLength=");
		builder.append(npaLength);
		builder.append(", origCGNumber=");
		builder.append(origCGNumber);
		builder.append(", origExtensionNumber=");
		builder.append(origExtensionNumber);
		builder.append(", origInterEncounterInd=");
		builder.append(origInterEncounterInd);
		builder.append(", origIsupLinkInd=");
		builder.append(origIsupLinkInd);
		builder.append(", origLegCallId=");
		builder.append(origLegCallId);
		builder.append(", origSipCallState=");
		builder.append(origSipCallState);
		builder.append(", originIPAddress=");
		builder.append(originIPAddress);
		builder.append(", originPointCode=");
		builder.append(originPointCode);
		builder.append(", originPort=");
		builder.append(originPort);
		builder.append(", pbtSubaddress=");
		builder.append(pbtSubaddress);
		builder.append(", reasonForRelease=");
		builder.append(reasonForRelease);
		builder.append(", recAnnPath=");
		builder.append(recAnnPath);
		builder.append(", recipientScpCic=");
		builder.append(recipientScpCic);
		builder.append(", reconnectionCount=");
		builder.append(reconnectionCount);
		builder.append(", relLocationField=");
		builder.append(relLocationField);
		builder.append(", releaseCauseValue=");
		builder.append(releaseCauseValue);
		builder.append(", remoteControlIndicator=");
		builder.append(remoteControlIndicator);
		builder.append(", rxDialoguePrimitiveType=");
		builder.append(rxDialoguePrimitiveType);
		builder.append(", sccpUserAddress=");
		builder.append(sccpUserAddress);
		builder.append(", scpDonor=");
		builder.append(scpDonor);
		builder.append(", scpRecipientPrefix=");
		builder.append(scpRecipientPrefix);
		builder.append(", serviceFlowMsg=");
		builder.append(serviceFlowMsg);
		builder.append(", serviceIndicator=");
		builder.append(serviceIndicator);
		builder.append(", serviceKey=");
		builder.append(serviceKey);
		builder.append(", serviceState=");
		builder.append(serviceState);
		builder.append(", serviceType=");
		builder.append(serviceType);
		builder.append(", switchCode=");
		builder.append(switchCode);
		builder.append(", tariffType=");
		builder.append(tariffType);
		builder.append(", termBypassInd=");
		builder.append(termBypassInd);
		builder.append(", termCGNumber=");
		builder.append(termCGNumber);
		builder.append(", termChargingArea=");
		builder.append(termChargingArea);
		builder.append(", termExtensionNumber=");
		builder.append(termExtensionNumber);
		builder.append(", termIGSInd=");
		builder.append(termIGSInd);
		builder.append(", termIPAddress=");
		builder.append(termIPAddress);
		builder.append(", termInterEncounterInd=");
		builder.append(termInterEncounterInd);
		builder.append(", termIsdnAccessInd=");
		builder.append(termIsdnAccessInd);
		builder.append(", termIsupLinkInd=");
		builder.append(termIsupLinkInd);
		builder.append(", termLegCallId=");
		builder.append(termLegCallId);
		builder.append(", controlDigit=");
		builder.append(controlDigit);
		builder.append(", termPort=");
		builder.append(termPort);
		builder.append(", termSignaling=");
		builder.append(termSignaling);
		builder.append(", termSipCallState=");
		builder.append(termSipCallState);
		builder.append(", termType=");
		builder.append(termType);
		builder.append(", tmr=");
		builder.append(tmr);
		builder.append(", traceFlag=");
		builder.append(traceFlag);
		builder.append(", traceMsg=");
		builder.append(traceMsg);
		builder.append(", transitCarrierIndEnum=");
		builder.append(transitCarrierIndEnum);
		builder.append(", ttcSspChargingArea=");
		builder.append(ttcSspChargingArea);
		builder.append(", userAnnType=");
		builder.append(userAnnType);
		builder.append(", userOriginatingArea=");
		builder.append(userOriginatingArea);
		builder.append(", vpnAccessType=");
		builder.append(vpnAccessType);
		builder.append(", vpnCallType=");
		builder.append(vpnCallType);
		builder.append(", vpnConnectionType=");
		builder.append(vpnConnectionType);
		builder.append(", vxmlPath=");
		builder.append(vxmlPath);
		builder.append("]");
		return builder.toString();
	}

	public boolean isSendContactorNumberInSIP() {
		return sendContactorNumberInSIP;
	}

	public void setSendContactorNumberInSIP(boolean sendContactorNumberInSIP) {
		this.sendContactorNumberInSIP = sendContactorNumberInSIP;
	}

	public String getHistoryInfo() {
		return historyInfo;
	}

	public void setHistoryInfo(String historyInfo) {
		this.historyInfo = historyInfo;
	}

	public boolean isRegenerateFwdCallInd() {
		return regenerateFwdCallInd;
	}

	public void setRegenerateFwdCallInd(boolean regenerateFwdCallInd) {
		this.regenerateFwdCallInd = regenerateFwdCallInd;
	}

	public boolean isOverrideFwdCallInd() {
		return overrideFwdCallInd;
	}

	public void setOverrideFwdCallInd(boolean overrideFwdCallInd) {
		this.overrideFwdCallInd = overrideFwdCallInd;
	}

	public ChargeIndEnum getChargeIndication() {
		return chargeIndication;
	}

	public void setChargeIndication(ChargeIndEnum chargeIndication) {
		this.chargeIndication = chargeIndication;
	}

	public PhoneNumber getTranslatedCallingNumber() {
		return translatedCallingNumber;
	}

	public void setTranslatedCallingNumber(PhoneNumber translatedCallingNumber) {
		this.translatedCallingNumber = translatedCallingNumber;
	}

	public boolean isSendDummySuccessResponse() {
		return sendDummySuccessResponse;
	}

	public void setSendDummySuccessResponse(boolean sendDummySuccessResponse) {
		this.sendDummySuccessResponse = sendDummySuccessResponse;
	}

	public int getRedirectionCounter() {
		return redirectionCounter;
	}

	public void setRedirectionCounter(int redirectionCounter) {
		this.redirectionCounter = redirectionCounter;
	}

	public PhoneNumber getRedirectingNumber() {
		return redirectingNumber;
	}

	public void setRedirectingNumber(PhoneNumber redirectingNumber) {
		this.redirectingNumber = redirectingNumber;
	}

	public boolean isSendSdpAlternateRouting() {
		return sendSdpAlternateRouting;
	}

	public void setSendSdpAlternateRouting(boolean sendSdpAlternateRouting) {
		this.sendSdpAlternateRouting = sendSdpAlternateRouting;
	}

	public PhoneNumber getOriginatingCalled() {
		return originatingCalled;
	}

	public void setOriginatingCalled(PhoneNumber originatingCalled) {
		this.originatingCalled = originatingCalled;
	}

	public boolean isPlayTermAnnOnConnect() {
		return playTermAnnOnConnect;
	}

	public void setPlayTermAnnOnConnect(boolean playTermAnnOnConnect) {
		this.playTermAnnOnConnect = playTermAnnOnConnect;
	}

	public boolean isSendIsupContractorNumber() {
		return isSendIsupContractorNumber;
	}

	public void setSendIsupContractorNumber(boolean isSendIsupContractorNumber) {
		this.isSendIsupContractorNumber = isSendIsupContractorNumber;
	}

	public RedirectingReasonEnum getRedirectionReason() {
		return redirectionReason;
	}

	public void setRedirectionReason(RedirectingReasonEnum redirectionReason) {
		this.redirectionReason = redirectionReason;
	}

	public boolean isSendChargingInfo() {
		return sendChargingInfo;
	}

	public void setSendChargingInfo(boolean sendChargingInfo) {
		this.sendChargingInfo = sendChargingInfo;
	}

	public boolean isSendAdditionalCalledUser() {
		return sendAdditionalCalledUser;
	}

	public void setSendAdditionalCalledUser(boolean sendAdditionalCalledUser) {
		this.sendAdditionalCalledUser = sendAdditionalCalledUser;
	}

	public RedirectionInformation getRedirectionInfo() {
		return redirectionInfo;
	}

	public void setRedirectionInfo(RedirectionInformation redirectionInfo) {
		this.redirectionInfo = redirectionInfo;
	}

	public boolean isSendChargingMsg(){
		return isSendChargeMsg;
	}

	public void setSendChargingMsg(boolean isSendChargeMsg) {
		this.isSendChargeMsg = isSendChargeMsg;
	}

	public boolean isSendRedirectionNum() {
		return isSendRedirectionNum;
	}

	public void setSendRedirectionNum(boolean isSendRedirectinNum) {
		this.isSendRedirectionNum = isSendRedirectinNum;
	}

	public boolean isSendRedirectionInfo() {
		return isSendRedirectionInfo;
	}

	public void setSendRedirectionInfo(boolean isSendRedirectionInfo) {
		this.isSendRedirectionInfo = isSendRedirectionInfo;
	}

	public boolean isSendOriginalCalled() {
		return isSendOriginalCalled;
	}

	public void setSendOriginalCalled(boolean isSendOriginalCalled) {
		this.isSendOriginalCalled = isSendOriginalCalled;
	}

	public String getDummySdp() {
		return dummySdp;
	}

	public void setDummySdp(String dummySdp) {
		this.dummySdp = dummySdp;
	}

	public boolean isCvReceivedInAcmCpg() {
		return cvReceivedInAcmCpg;
	}

	public void setCvReceivedInAcmCpg(boolean cvReceivedInAcmCpg) {
		this.cvReceivedInAcmCpg = cvReceivedInAcmCpg;
	}

	public boolean isCancelOnCvInAcmCpg() {
		return cancelOnCvInAcmCpg;
	}

	public void setCancelOnCvInAcmCpg(boolean cancelOnCvInAcmCpg) {
		this.cancelOnCvInAcmCpg = cancelOnCvInAcmCpg;
	}

	public boolean isSendIsupPartTransparenly() {
		return sendIsupPartTransparenly;
	}

	public void setSendIsupPartTransparenly(boolean sendIsupPartTransparenly) {
		this.sendIsupPartTransparenly = sendIsupPartTransparenly;
	}

	public int getRelCodingStnd() {
		return relCodingStnd;
	}

	public void setRelCodingStnd(int relCodingStnd) {
		this.relCodingStnd = relCodingStnd;
	}

	public boolean isInterSvcCall() {
		return interSvcCall;
	}

	public void setInterSvcCall(boolean interSvcCall) {
		this.interSvcCall = interSvcCall;
	}

	public String getCallingPartySubAddress() {
		return callingPartySubAddress;
	}

	public void setCallingPartySubAddress(String callingPartySubAddress) {
		this.callingPartySubAddress = callingPartySubAddress;
	}

	public boolean isSendCallingPartyInfo() {
		return sendCallingPartyInfo;
	}

	public void setSendCallingPartyInfo(boolean sendCallingPartyInfo) {
		this.sendCallingPartyInfo = sendCallingPartyInfo;
	}

	public CallingPartySubAddrEncodingEnum getCallingPartySubAddrEncoding() {
		return callingPartysubAddrEncoding;
	}

	public void setCallingPartySubAddrEncoding(CallingPartySubAddrEncodingEnum callingPartysubAddrEncoding) {
		this.callingPartysubAddrEncoding = callingPartysubAddrEncoding;
	}
	
	public TypeOfSubaddress getCallingPartySubAddressType() {
		return callingPartySubaddressType;
	}

	public void setCallingPartySubAddressType(TypeOfSubaddress callingPartySubaddressType) {
		this.callingPartySubaddressType = callingPartySubaddressType;
	}

	public int getDeleteDigitNum(){
		return deleteDigitNum;
    }
	
	public void setDeleteDigitNum(int deleteDigitNum){
		this.deleteDigitNum = deleteDigitNum;
	}
        

    public int getFeatureApplied(){
        return featureApplied;
    }

    public void setFeatureApplied(int featureApplied){
        this.featureApplied = featureApplied;
    }

    public boolean isErrorNotifyToService() {
        return errorNotifyToService;
    }

    public void setErrorNotifyToService(boolean errorNotifyToService) {
        this.errorNotifyToService = errorNotifyToService;
    }
    
    public int getCallType(){
        return callType;
    }

    public void setCallType(int callType){
        this.callType = callType;
    }
    
    public int getCallFeature(){
        return callFeature;
    }

    public void setCallFeature(int callFeature){
        this.callFeature = callFeature;
    }

	public boolean isPsEligible() {
		return psEligible;
	}
	public void setPsEligible(boolean psEligible) {
		this.psEligible = psEligible;
	}
	public boolean isPsRedirectionAnswer() {
		return psRedirectionAnswer;
	}
	public void setPsRedirectionAnswer(boolean psRedirectionAnswer) {
		this.psRedirectionAnswer = psRedirectionAnswer;
	}
	public boolean isPsExecuted() {
		return psExecuted;
	}
	public void setPsExecuted(boolean psExecuted) {
		this.psExecuted = psExecuted;
	}
	public int getDisconnectPauseTimer() {
		return disconnectPauseTimer;
	}
	public void setDisconnectPauseTimer(int disconnectPauseTimer) {
		this.disconnectPauseTimer = disconnectPauseTimer;
	}
	public int getInvokePauseTimer() {
		return invokePauseTimer;
	}
	public void setInvokePauseTimer(int invokePauseTimer) {
		this.invokePauseTimer = invokePauseTimer;
	}
	public String getFaxToneSignal() {
		return faxToneSignal;
	}
	public void setFaxToneSignal(String faxToneSignal) {
		this.faxToneSignal = faxToneSignal;
	}
    public int getBusyFeature() {
		return busyFeature;
	}
	public void setBusyFeature(int busyFeature) {
		this.busyFeature = busyFeature;
	}
	public int getNotReachableFeature() {
		return notReachableFeature;
	}
	public void setNotReachableFeature(int notReachableFeature) {
		this.notReachableFeature = notReachableFeature;
	}
	public boolean isCvReceivedForPS() {
		return cvReceivedForPS;
	}

	public void setCvReceivedForPS(boolean cvReceivedForPS) {
		this.cvReceivedForPS = cvReceivedForPS;
	}

	public boolean isCvForUnavailable() {
		return cvForUnavailable;
	}

	public void setCvForUnavailable(boolean cvForUnavailable) {
		this.cvForUnavailable = cvForUnavailable;
	}

	public String getRecievedInfoTone() {
		return recievedInfoTone;
	}

	public void setRecievedInfoTone(String recievedInfoTone) {
		this.recievedInfoTone = recievedInfoTone;
	}
	
	public int getCfnrFeature() {
		return cfnrFeature;
	}

	public void setCfnrFeature(int cfnrFeature) {
		this.cfnrFeature = cfnrFeature;
	}

	public int getTlec1() {
		return tlec1;
	}

	public void setTlec1(int tlec1) {
		this.tlec1 = tlec1;
	}

	public int getTlec2() {
		return tlec2;
	}

	public void setTlec2(int tlec2) {
		this.tlec2 = tlec2;
	}

	public boolean isRequestTerminatedForCfb() {
		return isRequestTerminatedForCfb;
	}

	public void setRequestTerminatedForCfb(boolean isRequestTerminatedForCfb) {
		this.isRequestTerminatedForCfb = isRequestTerminatedForCfb;
	}

}
