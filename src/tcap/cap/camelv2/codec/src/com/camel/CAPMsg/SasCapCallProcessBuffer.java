package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.TcapConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import asnGenerated.AudibleIndicator;
import asnGenerated.BCSMEvent;
import asnGenerated.BothwayThroughConnectionInd;
import asnGenerated.CAMEL_CallResult;
import asnGenerated.CallInformationRequestArg;
import asnGenerated.CallSegmentID;
import asnGenerated.EventTypeBCSM;
import asnGenerated.MiscCallInfo;
import asnGenerated.Tone;

import com.camel.dataTypes.AdrsStringDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.CallingPartyNum;
import com.camel.dataTypes.CauseDataType;
import com.camel.dataTypes.CellIdFixedLenDataType;
import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.dataTypes.GenericNumDataType;
import com.camel.dataTypes.IPSSCapabilitiesDataType;
import com.camel.dataTypes.ImsiDataType;
import com.camel.dataTypes.LAIFixedLenDataType;
import com.camel.dataTypes.LocationNum;
import com.camel.dataTypes.PartyId;
import com.camel.enumData.CalgPartyCatgEnum;


/**
 * This class contains parameters required
 * for CAP v2 operations
 * The instance of this class will float between application and Sbb
 * @author nkumar
 *
 */
public class SasCapCallProcessBuffer {

	/** This is the Called Party Number and will be set by Sbb after decoding the IDP */
	public CalledPartyNum calledPartyNum ;
	/** This is the Calling Party Number and will be set by Sbb after decoding the IDP */
	public CallingPartyNum callingPartyNum ;
	/** This is the Service key and will be set by Sbb after decoding the IDP */
	public Integer serviceKey ;
	/** This is the IMSI and will be set by Sbb after decoding the IDP */
	public ImsiDataType imsi ;
	/** This is the location number used in location information and will be set by Sbb after decoding the IDP */
	public LocationNum locationNum ;
	
	/** This is the location information used by service for CDR. Format is AgeOfLocationInformation,MCC,MNC,LAC,cellIdentity,VLR Adrs Signal,location number adrs signal*/
	public String locationInfoForCDR ;
	/** This is the Call reference number used by service for CDR.*/
	public String callRefNum ;
	/** This is the IMSI used by service for CDR.Format is MCC,MNC,LAC*/
	public String imsiForCDR ;
	
	/** This param defines the subscriber is roamed or not. Library will set this param after decoding the Idp. Value 1- Roaming, 0-No Roaming -1-Can't determine */
	public int roamingVal = -1 ;
	
	/** This parameter represent the total no of DP's occurred in a call and used by service for CDR */
	public int dpCount = 0 ;
	/**
	 * This parameter indicates which gsmSRF resources supported within the VMSC or GMSC the gsmSSF resides in
	*  are attached and available and will be set by Sbb after decoding the IDP.
	 */
	public IPSSCapabilitiesDataType ipSSPCapabilitiesDataType ;
	/** This is the VLR number used in location information and will be set by Sbb after decoding the IDP */
	public AdrsStringDataType vlrNumber ;
	/** This is the age of location used in location information and will be set by Sbb after decoding the IDP */
	public Integer ageOfLocationInfo ;
	/** This is the LAIFixedLenDataType used in location information and will be set by Sbb after decoding the IDP */
	public LAIFixedLenDataType laiFixedLenDataType ;
	/** This is the CellIdFixedLenDataType used in location information and will be set by Sbb after decoding the IDP */
	public CellIdFixedLenDataType cellIdFixedLenDataType ;
	/** This is the calgPartyCatg Indicates the type of calling party (e.g. operator, pay phone, ordinary subscriber)and will be set by Sbb after decoding the IDP */
	public CalgPartyCatgEnum calgPartyCatg ;
	/** This is the mscAdrs contains the mscId assigned to the MSC and will be set by Sbb after decoding the IDP */
	public AdrsStringDataType mscAdrs ;
	/** This is the eventTypeBCSM indicates the armed BCSM DP event, resulting in the "InitialDP" operation and will be set by Sbb after decoding the IDP */
	public EventTypeBCSM eventTypeBCSM_Idp ;
	/** This is the calledPartyBCDNumber and will be set by Sbb after decoding the IDP */
	public AdrsStringDataType calledPartyBCDNumber ;
	/** This parameter indicates the type of Call either MO or MT and will be set by Sbb after decoding the IDP */
	public SasCapCallTypeEnum callType ;
	/** This parameter indicates the translated calledPartyNumber based on nature of the number and can be used by prepaid service for charging */
	public String cldNumRTCH ;
	/** This parameter indicates the translated callingPartyNumber based on nature of the number and can be used by prepaid service for charging */
	public String clgNumRTCH ;
	
	
	/** This parameter contains the called party numbers towards which the call shall be routed 
	 * and will be set by Application to connect. This is mandatory parameter.
	 **/
	public List<CalledPartyNum> destRoutingAdd ;	
	/** If the call is forwarded by the gsmSCF, then this parameter carries the dialled digits. This will be set by Application to connect. This is optional parameter. */
	public PartyId orignalCaldPartyId ;
	
	/** Application will set this flag if want to send requestReportBCSM in the connect api or play. playAndCollect api. */
	public Boolean requestReportBcsm ;
	/** This is the list of bcsmEvent which application want to armed. Application will set this mandatory param before calling connect api or armEvents api */ 
	public List<BCSMEvent> bcsmEventList ;
	
	/** This is the EventTypeBCSM used in EventReportBCSM and will be set by Sbb after decoding the EventReportBCSM */
	public EventTypeBCSM eventType ;
	/** This is the cause for particular event type in EventTypeBCSM and will be set by Sbb after decoding the EventReportBCSM */
	public CauseDataType cause ;
	/** The legType will be either "0x01" or "0x02" and will be set by Sbb after decoding the EventReportBCSM */
	public String legType ;
	/** This parameter indicates Detection Point (DP) related information and will be set by Sbb after decoding the EventReportBCSM */
	public MiscCallInfo miscCallInfo ;
	
	
	/** Application will set this flag if want to send Apply Charging operation in connect api. **/
	public boolean applyChargingReq = false;
	
	/** This mandatory parameter will be set by application using the Charging Engine for Apply Charging Request
	 * This parameter specifies the total period of time as per the subscriber balance.
	 **/
	public Integer totalTimeDuration ;
	/** This mandatory parameter will be set by application using the Charging Engine for Apply Charging Request
	 * This parameter specifies the period of time for which a call can progress before an ApplyChargingReport
	 *	shall be sent to the gsmSCF.*/
	public Integer maxCallPeriodDuration ;
	/** This mandatory parameter will be set by application using the Charging Engine for Apply Charging Request
	 *  This parameter specifies the action to be taken at the IM-SSF when the duration specified above has been
	 *	reached.*/
	public Boolean releaseIfDurationExced ;
	
	/** This mandatory parameter indicates that a warning tone shall be played when the pre-defined warning tone timer
	 *	expires.Application will set this optional param before calling ApplyCharging API. 
	 */
	public Boolean audibleIndicator ;
	/** This optional parameter will be set by application using the Charging Engine for Apply Charging Request 
	 *   This parameter indicates to the IM-SSF the time duration until the next tariff switch. The measurement of the
	 *	elapsed tariff switch period commences immediately upon successful execution of this operation.*/
	public Integer tariifSwitchInterval ;
	/** This mandatory parameter will be set by application using the Charging Engine for Apply Charging Request.
	 * Values will be either 01 or 02.This parameter indicates the party in the call.Default value is 01.
	 **/
	public String partyToCharge = "01";
	
	/**
	 * This parameter will be set by application before calling furnishCharging api.This is mandatory parameter.
	 * This parameter contains free-format billing and/or charging characteristics;
	 */
	public byte[] freeFormatData ;
	
	/** This parameter will be set by Sbb after decoding the Apply Charging Report */
	public CAMEL_CallResult camelCallResult ;
	
	/**
	 * This optional parameter will be set by Application for ContinueWithArg operation.
	 * This parameter indicates the Call Segment.Either set this param or set legIdForContinueWithArg. If both of these params will
	 * be absent then Continue operation will be sent.
	 */
	public CallSegmentID calSegmentIDForContinueWithArg ;
	/**
	 * This optional parameter will be set by Application for ContinueWithArg operation.
	 * This parameter indicates the legID.Value will be either 01 or 02.Either set this param or set calSegmentIDForContinueWithArg.
	 * If both of these params will be absent then Continue operation will be sent.
	 */
	public String legIdForContinueWithArg ;
	
	/**
	 * This optional parameter will be set by Application for DFCWithArg operation in discconectIVR api.If this parameter will not set 
	 * then Sbb will send DFC message.
	 * This parameter indicates the Call Segment.
	 */
	public CallSegmentID calSegmentIDForDFCWithArg ;
	
	/** This is the primitive value of last dialogue and will be set by Sbb */ 
	public int lastDialoguePrimitive ;
	
	/** This will contain state information of event and will be set by Sbb */
	public SasCapCallStateInfo stateInfo = new SasCapCallStateInfo();
	
	
	/** This parameter contains information of CollectedDigits used to encode CollectedInfo parameter
	 * of PC. Application will set this mandatory parameter before calling PlayAndCollect API.
	 */
	public SasCapClctdDigitsDataType collectedDigits = new SasCapClctdDigitsDataType();
	/** 
	 * Application will set this parameter before calling PlayAndCollect API or Play API.
	 * This is mandatory parameter.Default value is TONE.This parameter indicates an announcement or tone to be sent to the end-user by the gsmSRF. 
	 * If set the value INBAND_INFO then set the parameter inbandInfoDataType
	 * If set the value TONE then set the parameter tone.
	 */
	public SasCapInformationtoSendEnum informationtoSendEnum = SasCapInformationtoSendEnum.TONE ;
	
	/** 
	 * Application will set this parameter before calling PlayAndCollect API or Play API.
	 * This is an optional field.This parameter indicates whether the gsmSRF may initiate a disconnection from the gsmSSF after the interaction has been completed. 
	 */
	public boolean disconnectFromIPForbidden = true ;
	/** 
	 * Application will set this parameter before calling PlayAndCollect API or Play API.
	 * This is an optional field.This parameter indicates whether or not a "SpecializedResourceReport" shall be sent to the gsmSCF when all
	 *  information has been sent.
	 */
	public Boolean requestAnnouncementComplete  ;
	
	/** 
	 * Application will set this parameter before calling PlayAndCollect API or Play API.
	 * This is an optional field.This parameter indicates whether or not a "SpecializedResourceReport" shall be sent to the gsmSCF when the first
	 *	announcement or tone has started.
	 */
	public Boolean requestAnnouncementStarted  ;
	/** 
	 * Application will set this parameter before calling PlayAndCollect API or Play API.
	 * This is an optional field.This parameter indicates the Call Segment to which the user interaction shall apply.
	 */
	public CallSegmentID callSegmentID ;
	/** 
	 * Application will set this parameter if select the INBAND_INFO value 
	 * of SasCapInformationtoSendEnum before calling the PlayAndCollect API or Play API.
	 * This parameter specifies the inband information to be sent.
	 */
	public SasCapInbandInfoDataType inbandInfoDataType ;
	/** 
	 * Application will set this parameter if select the TONE value 
	 * of SasCapInformationtoSendEnum before calling the PlayAndCollect API or Play API.
	 * This parameter specifies a tone to be sent to the end-user.
	 */
	public Tone tone ;
	/**
	 * Application will set this parameter if select the ELEMENTRY_MSG_ID value of SasCapMsgIdEnum of
	 * inbandInfoDataType before calling the PlayAndCollect API or Play API.
	 * This parameter indicates a single announcement.
	 */
	public Integer elementryMsgId ;
	/**
	 * Application will set this parameter if select the ELEMENTRY_MSG_ID_LIST value of SasCapMsgIdEnum of
	 * inbandInfoDataType before calling the PlayAndCollect API or Play API.
	 * This parameter specifies a sequence of announcements.
	 */
	public List<Integer> elementryMsgIdList ;
	/**
	 * Application will set this parameter if select the VARIABLEMSG value of SasCapMsgIdEnum of
	 * inbandInfoDataType before calling the PlayAndCollect API or Play API.
	 * This parameter specifies an announcement with one or more variable parts.
	 */
	public SasCapVariableMsgDataType variableMsg ;
				
	/**
	 * Application will set this parameter for ConnectToResource operation before calling 
	 * PlayAndCollect API or Play API.This is mandatory parameter for ConnectToResource operation.
	 * This parameter indicates the routing address to set up a connection towards the gsmSRF.
	 */
	public CalledPartyNum ipRoutingAdrs ;
	
	/**
	 * Application will set this parameter for ConnectToResource operation before calling 
	 * PlayAndCollect API or Play API or CTR.This is optional parameter for ConnectToResource operation.
	 * This parameter indicates the call segment to which the Connect To Resource procedure applies.
	 */
	public CallSegmentID calSegmentIDForCTR ;
	
	/**
	 * Application will set this parameter for ConnectToResource operation before calling 
	 * PlayAndCollect API or Play API.This is optional parameter for ConnectToResource operation.
	 * This parameter contains an indicator that is used for the control of the through connection to the Calling Party.
	 */
	public BothwayThroughConnectionInd bothwayThroughConnectionInd ;
	
	/**
	 * This parameter contains information collected from end-user. Sbb will
	 * set this param.
	 */
	public GenericDigitsDataType promptCollectUserInfo ;
	
	/** Application will set this flag if want to send Call Information Request operation and set the callInfoRequestData **/
	public boolean callInfoRequest = false;
	
	/** 
	 * Application will set callInfoRequestData for leg1 if callInfoRequest is true. 
	 * If callInfoRequest is true but callInfoRequestDataForLeg1 is not set then Sbb will use 
	 * default value.  
	 */
	public CallInformationRequestArg callInfoRequestDataForLeg1 ;
	
	/** 
	 * Application will set callInfoRequestData for leg2 if callInfoRequest is true. 
	 * If callInfoRequest is true but callInfoRequestDataForLeg2 is not set then Sbb will use 
	 * default value.  
	 */
	public CallInformationRequestArg callInfoRequestDataForLeg2 ;
	
	/**
	 * This parameter will be set by Sbb after decoding the Call Information Report
	 *  received from the network.
	 */
	public SasCapCallInfoRptDataType callInfoRptDataType ;
	
	
	/**
	 * This param will be set by application before calling CoonectToIVR api. This  is mandatory parameter.
	 * This parameter indicates the destination address of the gsmSRF for assist procedure.
	 */
	public GenericNumDataType assistSSPIPRoutingAdrs ;
	/**
	 * This param will be set by application before calling CoonectToIVR api. This  is optional parameter.
	 * This parameter is used by the gsmSCF to associate the "AssistRequestInstructions" from the assisting gsmSSF
	 *	(or the gsmSRF) with the Request from the initiating gsmSSF
	 */
	public GenericDigitsDataType correlationId ;
	/**
	 * This param will be set by application before calling CoonectToIVR api. This  is optional parameter.
	 * This parameter contains the gsmSCF identifier and enables the assisting gsmSSF to identify which gsmSCF the
	 * AssistRequestInstructions shall be sent to.
	 */
	public String scfId ;
	/**
	 * This param will be set by application before calling CoonectToIVR api. This  is optional parameter and default value is false.
	 * This parameter contains an indicator that is used for the control of the through connection to the Calling Party.
	 */
	public boolean bothwayPathRequired = false ;
	
	/** This parameter indicates whether Reset Timer msg should be sent in play api or playAndCollect api or not. Application will set this param before calling the same api's.
	 * Default value is false.
	 **/ 
	public Boolean resetTimer = false ;
	
	/** Application have to set this mandatory param for resetTimer. This parameter specifies the value in seconds to which the timer shall be set.Default value is 100000 sec.*/
	public Integer resetTimerDuration = 100000 ;
	
	/** This param specifies the problem type.Application have to set this mandatory param before calling for rejectRequest api. 
	 *	Use JainTcap Class jain.protocol.ss7.tcap.component.ComponentConstants for possible values.
	 **/
	public Integer problemType = null ;
	
	/** This param specifies the problem belongs to the problem type.Application have to set this mandatory param before calling for rejectRequest api. 
	 * Use JainTcap Class jain.protocol.ss7.tcap.component.ComponentConstants for possible values.
	 * */
	public Integer problemCode = null ;
	
	/** This timer will be used for sending messages after timeout. */
	public Timer timer = null ;
	
	/** This timer will be used for sending messages after timeout . */
	public Timer timerAT = null ;
	
	/** This timer value in seconds will be used by Application to send ACTIVITY_TEST message when timeout occurs. */
	public Integer sendActivitytestTimerVal = 60 ;
	
	/** This timer value in seconds will be used by Application to send ABORT message when timeout occurs after sending ACTIVITY_TEST message. */
	public Integer waitTimeActivityTestResult = 5 ;
	
	public Boolean activityTestresultReceived = false;
	
	/**
	 * Sbb will use this invokeId.This Parameter will be incremented
	 * corresponding to each call.It should be in sync so it is put in buffer.
	 */
	public int invokeId = 0 ;
	
	//used for call load
	public String CDR = "";
	/**
	 * Sbb will put invokeId corresponding to opcode for which return result expected.
	 * This parameter will be used when Return result of operation will be received.
	 */
	public HashMap<Integer,Byte> invokeId_Opcode = new HashMap<Integer, Byte>();
	
	
	
	
	/** This flag represent that dialoguePortion is present in the dialogue or not */
	public boolean dialoguePortionPresent ;
	
	public Integer appContextIdentifier ;
	
	public byte[] appContextName ;
	
	public Integer protocolVersion ;
	
	public Integer securityContextIdentifier ;
	
	public byte[] securityContextInfo ;
	
	public byte[] userInfo ;
	
	/** This is the dialogue ID */
	public int dlgId ;
	
	/** This can be same as dialogue id or can have some other unique value identifying the call */
	public String callId ;
	
	/** This is the originating point code */
	public int opc ;
	
	/** This is the destination point code */
	public int dpc ;
	
	/** This is the originating sub system number */
	public int ossn ;
	
	/** This is the originating sub system number */
	public int dssn ;
	
	
	public boolean isActivityTestresultReceivedPresent(){
		return activityTestresultReceived != null ;
	}
	
	public boolean isCalledPartyBCDNumberPresent(){
		return calledPartyBCDNumber != null ;
	}
	
	public boolean isTimerPresent(){
		return timer != null ;
	}
	
	public boolean isProblemTypePresent(){
		return problemType != null ;
	}
	
	public boolean isProblemCodePresent(){
		return problemCode != null ;
	}
	
	public boolean isCalSegmentIDForContinueWithArgPresent(){
		return calSegmentIDForContinueWithArg != null ;
	}
	
	public boolean islegIdForContinueWithArgPresent(){
		return legIdForContinueWithArg != null ;
	}
	
	public boolean isCalSegmentIDForDFCPresent(){
		return calSegmentIDForDFCWithArg != null ;
	}
	
	public boolean isAssistSSPIPRoutingAdrsPresent(){
		return assistSSPIPRoutingAdrs != null ;
	}
	
	public boolean isCorrelationIdPresent(){
		return correlationId != null ;
	}
	
	public boolean isCallInfoRequestDataForLeg1Present(){
		return callInfoRequestDataForLeg1 != null ;
	}
	
	public boolean isCallInfoRequestDataForLeg2Present(){
		return callInfoRequestDataForLeg2 != null ;
	}
	
	public boolean isIpRoutingAdrsPresent(){
		return ipRoutingAdrs != null ;
	}
	
	public boolean isCallSegmentIDPresent(){
		return callSegmentID != null ;
	}
	
	public boolean isRequestAnnouncementCompletePresent(){
		return requestAnnouncementComplete != null ;
	}
	
	public boolean isRequestAnnouncementStartedPresent(){
		return requestAnnouncementStarted != null ;
	}
	
	public boolean isCalSegmentIDForCTRPresent(){
		return calSegmentIDForCTR != null ;
	}
	
	public boolean isbothwayThroughConnectionIndPresent(){
		return bothwayThroughConnectionInd != null ;
	}
	
	public boolean isElementryMsgIdPresent(){
		return elementryMsgId != null ;
	}
	
	public boolean isCollectedDigitsPresent(){
		return collectedDigits != null ;
	}
	
	public boolean isInformationtoSendEnumPresent(){
		return informationtoSendEnum != null ;
	}
	
	public boolean isInbandInfoDataTypePresent(){
		return inbandInfoDataType != null ;
	}
	
	public boolean isTonePresent(){
		return tone != null ;
	}
	
	public boolean isElementryMsgIdListPresent(){
		return elementryMsgIdList != null ;
	}
	
	public boolean isVariableMsgPresent(){
		return variableMsg != null ;
	}
	
	public boolean iscamelCallResultPresent(){
		return camelCallResult != null ;
	}
	public boolean isCalledPartyNumPresent() {
		return calledPartyNum != null ;
	}
	public boolean isCallingPartyNumPresent() {
		return callingPartyNum != null ;
	}
	
	public boolean isCallTypePresent() {
		return callType != null ;
	}
	
	public boolean isCldNumRTCHPresent() {
		return cldNumRTCH != null ;
	}
	
	public boolean isClgNumRTCHPresent() {
		return clgNumRTCH != null ;
	}
	
	public boolean isServiceKeyPresent() {
		return serviceKey != null ;
	}
	
	public boolean isipSSPCapabilitiesDataTypePresent() {
		return ipSSPCapabilitiesDataType != null ;
	}
	
	public boolean isAgeOfLocationInfoPresent() {
		return ageOfLocationInfo != null ;
	}
	
	public boolean isCalgPartyCatgPresent() {
		return calgPartyCatg != null ;
	}
	
	public boolean isMscAdrsPresent() {
		return mscAdrs != null ;
	}
	
	public boolean isEventTypeBCSM_IdpPresent() {
		return eventTypeBCSM_Idp != null ;
	}
	
	public boolean isBcsmEventListPresent() {
		return bcsmEventList != null ;
	}
	
	public boolean isDestRoutingAddPresent() {
		return destRoutingAdd != null ;
	}
	public boolean isOrignalCaldPartyIdPresent() {
		return orignalCaldPartyId != null ;
	}
	public boolean isLocationNumPresent() {
		return locationNum != null ;
	}
	public boolean isVlrNumberPresent() {
		return vlrNumber != null ;
	}
	
	public boolean isLaiFixedLenDataTypePresent() {
		return laiFixedLenDataType != null ;
	}
	public boolean isCellIdFixedLenDataTypePresent() {
		return cellIdFixedLenDataType != null ;
	}
	public boolean isEventTypePresent() {
		return eventType != null ;
	}
	
	public boolean isCausePresent() {
		return cause != null ;
	}
	public boolean isLegTypePresent() {
		return legType != null ;
	}
	public boolean isImsiPresent() {
		return imsi != null ;
	}
	public boolean isStateInfoPresent() {
		return stateInfo != null ;
	}
	public boolean isCallIdPresent() {
		return callId != null ;
	}
	
	public boolean isTotalTimeDurationPresent() {
		return totalTimeDuration != null ;
	}
	
	public boolean ismaxCallPeriodDurationPresent() {
		return maxCallPeriodDuration != null ;
	}
	
	public boolean isReleaseIfDurationExcedPresent() {
		return releaseIfDurationExced != null ;
	}
	
	public boolean isAudibleIndicatorPresent() {
		return audibleIndicator != null ;
	}
	
	public boolean isTariifSwitchIntervalPresent() {
		return tariifSwitchInterval != null ;
	}
	
	public boolean isMiscCallInfoPresent() {
		return miscCallInfo != null ;
	}
	
	public boolean isLocationInfoForCDRPresent() {
		return locationInfoForCDR != null ;
	}
	
	public boolean isCallRefNumPresent() {
		return callRefNum != null ;
	}
	
	public boolean isimsiForCDRPresent() {
		return imsiForCDR != null ;
	}
	
	public boolean isDlgActive(){
		return (lastDialoguePrimitive!= TcapConstants.PRIMITIVE_END 
				 || lastDialoguePrimitive != TcapConstants.PRIMITIVE_PROVIDER_ABORT || lastDialoguePrimitive != TcapConstants.PRIMITIVE_USER_ABORT );
	}
}
