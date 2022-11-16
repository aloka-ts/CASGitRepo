package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectReqEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueReqEvent;
import jain.protocol.ss7.tcap.dialogue.DialogueConstants;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortReqEvent;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import asnGenerated.Cause;
import asnGenerated.ReleaseCallArg;

import com.camel.dataTypes.CauseDataType;
import com.camel.exceptions.InvalidInputException;
import com.camel.exceptions.OperationNotAllowedException;
import com.camel.util.Util;




/**
 * This class contains encoding and decoding methods 
 * for CAP messages
 * @author nkumar
 *
 */
public class CAPSbb {


	private static CAPSbb capSbb = null ;


	private CAPSbb(){		
	}

	/**
	 * This method will return the single instance of CAPSbb.
	 * @return instance of CAPSbb
	 * @throws Exception
	 */
	public static CAPSbb getInstance() throws Exception{		
		logger.info("getInstance:Enter");		
		if(capSbb == null){
			logger.info("getInstance:creating instance of CapSbb");
			synchronized (CAPSbb.class) {
				if(capSbb == null){
					logger.info("getInstance:New Instance");
					capSbb = new CAPSbb();
					SasCapFsm.loadOpAllwdMap();
				}
			}		
		}
		return capSbb ;
	}

	//Instance of logger
	private static Logger logger = Logger.getLogger(CAPSbb.class);	 


	/**
	 * This function will decode the eventObject based on the operation code and dialgORComp
	 * and update the CPB object. Application will set the dialgORComp parameter.cpb is mandatory parameter.
	 * @param eventObject this event object will contain dialogue indication event or component indication event 
	 * @param dialgORComp this is the enum of type DialgORComp. Value will be dialogue or component.
	 * @param cpb			this is the object of SasCapCallProcessBuffer.
	 * @throws Exception    If some problem occurs in decoding of component or dialogue.
	 */
	public void updateCAPObj(EventObject eventObject,SS7IndicationType dialgORComp, SasCapCallProcessBuffer cpb) throws Exception{
		if(cpb == null){
			throw new Exception("cpb is null");
		}
		int dlgId = cpb.dlgId ;
		logger.info(Util.toString(dlgId) + "::: updateCAPObj:Enter");

		if(dialgORComp == SS7IndicationType.COMPONENT){
			logger.info(Util.toString(dlgId) + "::: updateCAPObj:Calling processComponent");
			SasCapMsg.processComponent(eventObject, cpb);
		}
		else if(dialgORComp == SS7IndicationType.Dialogue){
			logger.info(Util.toString(dlgId) + "::: updateCAPObj:Calling processDialogue");
			SasCapMsg.processDialogue(eventObject, cpb);
		}
		logger.info(Util.toString(dlgId) + "::: updateCAPObj:Exit");

	}

	/**
	 * This function will use cpb parameters to encode RequestReportBCSM message
	 * and set the corresponding invokeReq events in msgs.Application have to set bcsmEventList of cpb. 
	 */
	public void armEvents(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: armEvents:Enter");
		SasCapRequestRpt.setComponentReqEvent(source, cpb, msgs, ++cpb.invokeId);
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the continue dialogue event");
				
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, continueReq);
			cpb.dialoguePortionPresent = false ;
		}
		msgs.setDlgReqEvent(continueReq);	
		cpb.CDR += new Date()+"<--- snd RRBCSM with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: armEvents:Exit");
	}

	/**
	 * This function will use cpb parameters to encode Continue message or ContinueWithArg message
	 * and set the corresponding invokeReq events in msgs.Application have to set callSegmentIdForContinueWithArg
	 * or legIdForContinueWithArg of cpb for ContinueWithArg message to be sent.
	 */
	public void continueCall(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Enter");
		msgs.getCompReqEvents().clear();
		if(cpb.isCalSegmentIDForContinueWithArgPresent() || cpb.islegIdForContinueWithArgPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Making the ContinueWithArg msg");
			SasCapContinue.encodeContinueWithArg(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd continueWithArg with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Successfully ContinueWithArg msg added in the msgs");
		}else{
			logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Making the Continue msg");
			SasCapContinue.encodeContinue(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd continue with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Successfully Continue msg added in the msgs");
		}
		logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		msgs.setDlgReqEvent(continueReq);	
		logger.info(Util.toString(cpb.dlgId) + "::: continueCall:Exit");
	}

	/**
	 * This function will use cpb parameters to encode ActivityTest message 
	 * and set the corresponding invokeReq events in msgs.Application have to set activityTestTimer
	 * of cpb for ActivityTest message to be sent.
	 */
	public void activityTest(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: activityTest:Enter");

		logger.info(Util.toString(cpb.dlgId) + "::: activityTest:Making the ActivityTest msg");
		SasCapContinue.encodeActivityTest(source, cpb, msgs, ++cpb.invokeId);
		logger.info(Util.toString(cpb.dlgId) + "::: activityTest:Successfully ActivityTest msg added in the msgs");
		
		logger.debug(Util.toString(cpb.dlgId) + "::: activityTest:Putting invokeId:" + cpb.invokeId + " in map for opcode:" + CAPOpcode.ACTIVITY_TEST);
		cpb.invokeId_Opcode.put(cpb.invokeId,CAPOpcode.ACTIVITY_TEST );
		
		logger.info(Util.toString(cpb.dlgId) + "::: activityTest:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		msgs.setDlgReqEvent(continueReq);	
		cpb.CDR += new Date()+"<--- snd AT with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: activityTest:Exit");
	}

	/**
	 * This function will use cpb parameters to encode RequestReportBCSM,APPLY_CHARGING, callInfoRequest,
	 * connect or continue messages and set the corresponding invokeReq events in msgs. 
	 * For RequestReportBcsm, application have to set bcsmEventList of cpb otherwise It will send requestReportBcsm for hard coded bcsm events.
	 * For APPLY_CHARGING, application have to set the following params of cpb:maxCallPeriodDuration,tariifSwitchInterval,
	 * releaseIfDurationExced and partyToCharge. These all params have default values in cpb.
	 * For CallInformationRequest, application have to set the flag callInfoRequest of cpb.Default value is true and need to set the 
	 * following params callInfoRequestDataForLeg1,callInfoRequestDataForLeg2.If these two parameters will not be provided then
	 * will send default values. 
	 * For connect, application have to set the mandatory parameter destRoutingAdd of cpb.Application can also set 
	 * other parameter orignalCaldPartyId of cpb which is optional param.
	 * @param source source of the event
	 * @param msgs	This object will contain array of events send to the network.
	 * @param cpb 	this is the object of SasCapCallProcessBuffer
	 * @throws Exception if mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 */
	public void connect(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.CONNECT);
		//int invokeId = 0 ;
		msgs.getCompReqEvents().clear();
		// encoding of RequestReprotBcsm and set the invokeRequestEvent
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.REQUEST_REPORT) && cpb.requestReportBcsm){
			//armEvents(source, cpb, msgs);
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Setting the REQUEST_REPORT operation");
			SasCapRequestRpt.setComponentReqEvent(source, cpb, msgs, ++cpb.invokeId);
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Setting the REQUEST_REPORT operation added in the msgs");
		}
		// encoding of APPLY_CHARGING and set the invokeRequestEvent
		if(cpb.applyChargingReq && alwdObj.getAllwdOpCode().contains(CAPOpcode.APPLY_CHARGING)){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Setting the CAP ACR operation");
			SasCapACR.encodeAC(source, cpb, msgs,++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd ACH with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: connect: Successfully CAP ACR operation added in the msgs");
		}
		//callInfoRequest is true
		if(cpb.callInfoRequest){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Setting the CAP CIRq operation");
			SasCapCIR.encodeCIRq(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd CIRq with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			++cpb.invokeId ;
			logger.info(Util.toString(cpb.dlgId) + "::: connect: Successfully CAP CIRq operation added in the msgs");
		}

		// encoding of Connect and set the invokeRequestEvent
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.CONNECT) && cpb.isDestRoutingAddPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Encoding the CONNECT");
			SasCapConnect.encodeConnect(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd connect with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully encoded the CONNECT");
		}else if(alwdObj.getAllwdOpCode().contains(CAPOpcode.CONTINUE)){
			if(cpb.isCalSegmentIDForContinueWithArgPresent() || cpb.islegIdForContinueWithArgPresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the ContinueWithArg msg");
				SasCapContinue.encodeContinueWithArg(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd continueWithArg with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully ContinueWithArg msg added in the msgs");
			}else{
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the Continue msg");
				SasCapContinue.encodeContinue(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd continue with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully Continue msg added in the msgs");
			}
		}
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, continueReq);
		}
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Exit");		
	}

	/**
	 * This function will use cpb parameters to encode Furnish Charging Information messages and set the corresponding invokeReq events in msgs
	 * Application will need to set the legIdForFCI and freeFormatdata of cpb. 
	 * @param source source of the event
	 * @param msgs 	this object will contain array of events send to the network.
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 */
	public void  furnishCharging(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: furnishCharging:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.FURNISH_CHARGING);
		msgs.getCompReqEvents().clear();
		// encoding of RequestReprotBcsm and set the invokeRequestEvent
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.FURNISH_CHARGING_INFORMATION)){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Encoding the FURNISH_CHARGING_INFORMATION operation");
			SasCapACR.encodeFCI(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd FCI with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully encoded FURNISH_CHARGING_INFORMATION operation");
		}
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: furnishCharging:Exit");
	}

	/**
	 * This function will use cpb parameters to encode ETC messages and set the corresponding invokeReq events in msgs
	 * Application will need to set the assistingSSPIPRoutingAddress,correlationID and scfId parameters. 
	 * @param source source of the event
	 * @param msgs 	this object will contain array of events send to the network.
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 */
	public void  connectToIVR(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: connectToIVR:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.CONNECT_IVR);
		msgs.getCompReqEvents().clear();
		// encoding of RequestReprotBcsm and set the invokeRequestEvent
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.ESTABLISH_TEMP_CONNECTION)){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Encoding the ESTABLISH_TEMP_CONNECTION operation");
			SasCapETC.encodeETC(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd ETC with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully encoded ESTABLISH_TEMP_CONNECTION operation");
		}
		logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, continueReq);
		}
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: connectToIVR:Exit");
	}

	/**
	 * This function will use cpb parameters to encode Reject Request messages and set the corresponding invokeReq events in msgs
	 * Application will need to set the problemCode and problemType parameters of cpb. 
	 * @param source source of the event
	 * @param msgs 	this object will contain array of events send to the network.
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 */
	public void  rejectRequest(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: rejectRequest:Enter");
		if(!cpb.isProblemCodePresent() && !cpb.isProblemTypePresent()){
			logger.error(Util.toString(cpb.dlgId) + "::: rejectRequest:ProblemType or ProblemCode of cpb is null.");
			throw new InvalidInputException("ProblemType or ProblemCode of cpb is null.");
		}
		//SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.CONNECT_IVR);
		msgs.getCompReqEvents().clear();
		RejectReqEvent rejectReqEvent = new RejectReqEvent(source, cpb.dlgId, cpb.problemType, cpb.problemCode);
		msgs.getCompReqEvents().add(rejectReqEvent);

		logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the continue dialogue event");
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		msgs.setDlgReqEvent(continueReq);	
		cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
		cpb.stateInfo.setCurrState(null);
		cpb.CDR += new Date()+"<--- snd reject with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: rejectRequest:Exit");
	}

	/**
	 * This function will use cpb parameters to encode release call messages and set the corresponding invokeReq events in msgs.
	 * Application will need to set the cause param of cpb.If not provided then send the
	 * default value Normal_UNSPECIFIED.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events and dialogue send to the network.
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 * 
	 */
	public void releaseCall(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.RELEASE_CALL);
		msgs.getCompReqEvents().clear();
		ReleaseCallArg releaseCallArg = new ReleaseCallArg() ;
		byte[] data ;
		if(! cpb.isCausePresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Cause is not set. So setting default value  CAUSE_VAL_NORMAL_CLEAR");
			data = CauseDataType.encodeCauseVal(SasCapDefaultValue.LOCTION_USER, SasCapDefaultValue.CODING_STD_ITU_T, SasCapDefaultValue.CAUSE_VAL_NORMAL_CLEAR);
		}else{
			logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Cause:" + cpb.cause);
			CauseDataType cause = cpb.cause ;
			data = CauseDataType.encodeCauseVal(cause.getLocEnum(), cause.getCodingStndEnum(), cause.getCauseValEnum());
			//for next time it should be reset
			cpb.cause = null ;
		}
		releaseCallArg.setValue(new Cause(data));
		IEncoder<ReleaseCallArg> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Encoding the ReleaseCallArg");
		encoder.encode(releaseCallArg, outputStream);
		logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Successfully Encoded the ReleaseCallArg");
		byte[] encodedReleaseCall = outputStream.toByteArray();
		//byte[] encodedReleaseCall = {04 ,02, 80,(byte)2F};
		int len = encodedReleaseCall.length ;

		if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: releaseCall:Encoded encodedReleaseCall: " + Util.formatBytes(encodedReleaseCall));
			logger.debug(Util.toString(cpb.dlgId) + "::: releaseCall:length of encoded encodedReleaseCall: " + len);
		}

		byte[] releaseCallOpCode =  { CAPOpcode.RELEASE_CALL };
		Operation releaseOp = new Operation(Operation.OPERATIONTYPE_LOCAL, releaseCallOpCode);
		InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, releaseOp);
		ire.setInvokeId(++cpb.invokeId);
		logger.debug(Util.toString(cpb.dlgId) + "::: releaseCall:Sending param");
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SINGLE, encodedReleaseCall));
		ire.setClassType(CAPOpcode.RELEASE_CALL_CLASS);
		//ire.setLastInvokeEvent(true);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ire);
		msgs.setCompReqEvents(list);
		logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Making the end request dialogue event");
		EndReqEvent endReq = new EndReqEvent(source, cpb.dlgId);
		endReq.setTermination(DialogueConstants.TC_BASIC_END);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, endReq);
			cpb.dialoguePortionPresent = false ;
		}
		setDialogueEventAndState(endReq, cpb, msgs, alwdObj);
		cpb.CDR += new Date()+"<--- snd RC with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: releaseCall:Exit");

	}

	/**
	 * This function will use cpb parameters to encode ConnectToResource message(Conditional)
	 * and playAnnouncment message and set the corresponding invokeReq events in msgs.
	 * For ConnectToResource, application will need to set the mandatory param ipRoutingAdrs and optional param 
	 * bothwayThroughConnectionInd of cpb. 
	 * For play, application can set the following params:disconnectFromIPForbidden,requestAnnouncementComplete
	 * informationtoSendEnum,inbandInfoDataType,variableMsg,tone,elementryMsgId,elementryMsgIdList of cpb.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 * 
	 */
	public void play(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: play:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.PLAY);
		msgs.getCompReqEvents().clear();
		/*if(alwdObj.getAllwdOpCode().contains(CAPOpcode.REQUEST_REPORT) && cpb.requestReportBcsm){
			armEvents(source, cpb, msgs);
		}*/

		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.CONNECT_TO_RESOURCE) && ((cpb.stateInfo.currState != SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS) && 
				(cpb.stateInfo.currState != SasCapCallStateEnum.USER_INTERACTION_COMPLETED))){
			logger.info(Util.toString(cpb.dlgId) + "::: play:Encoding the CONNECT_TO_RESOURCE");
			SasCapPC.encodeCTR(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd CTR with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: play: Successfully encoded the CONNECT_TO_RESOURCE");
		}

		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.PLAY_ANNOUNCEMENT)){
			logger.info(Util.toString(cpb.dlgId) + "::: play:Encoding the PLAY_ANNOUNCEMENT");
			SasCapPC.encodePA(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd play with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: play: Successfully encoded the PLAY_ANNOUNCEMENT");
		}
		//sending the reset Timer event
		if(cpb.resetTimer){
			SasCapResetTimer.encodeRT(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd RT with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
		}

		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, continueReq);
		}
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: play:Exit");
	}

	/**
	 * This function will use cpb parameters to encode ConnectToResource message(Conditional)
	 * and prompt and collect information message and set the corresponding invokeReq events in msgs.
	 * For ConnectToResource, application will need to set the mandatory param ipRoutingAdrs and optional param 
	 * bothwayThroughConnectionInd of cpb. 
	 * For prompt and collect, application can set the following params:collectedDigits,disconnectFromIPForbidden,
	 * informationtoSendEnum,inbandInfoDataType,variableMsg,tone,elementryMsgId,elementryMsgIdList of cpb.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 * 
	 */
	public void playAndCollect(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.PLAYANDCOLLECT);
		msgs.getCompReqEvents().clear();

		/*if(alwdObj.getAllwdOpCode().contains(CAPOpcode.REQUEST_REPORT) && cpb.requestReportBcsm){
			armEvents(source, cpb, msgs);
		}*/

		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.CONNECT_TO_RESOURCE)&& ( !(cpb.stateInfo.currState == SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS) && 
				! (cpb.stateInfo.currState == SasCapCallStateEnum.USER_INTERACTION_COMPLETED))){
			logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect:Encoding the CONNECT_TO_RESOURCE");
			SasCapPC.encodeCTR(source, cpb, msgs,++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd CTR with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect: Successfully encoded the CONNECT_TO_RESOURCE");
		}

		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.PROMPT_COLLECT)){
			logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect:Encoding the PROMPT_COLLECT");
			SasCapPC.encodePC(source, cpb, msgs, ++cpb.invokeId);
			logger.debug(Util.toString(cpb.dlgId) + "::: playAndCollect:Putting invokeId:" + cpb.invokeId + " in map for opcode:" + CAPOpcode.PROMPT_COLLECT);
			cpb.invokeId_Opcode.put(cpb.invokeId,CAPOpcode.PROMPT_COLLECT );
			cpb.CDR += new Date()+"<--- snd PC with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect: Successfully encoded the PROMPT_COLLECT");
		}
		//sending the reset Timer event
		if(cpb.resetTimer)
			SasCapResetTimer.encodeRT(source, cpb, msgs, ++cpb.invokeId);

		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		if(cpb.dialoguePortionPresent){
			setDialoguePortion(cpb, source, continueReq);
		}
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: playAndCollect:Exit");
	}

	/**
	 * This function will use cpb parameters to encode disconnect forward connection operation
	 * and and set the corresponding invokeReq events in msgs.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 * 
	 */
	public void disconnectIvr(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Enter");
		SasCapAlwdOp alwdOp = checkOperationAllowed(cpb, SasCapApiEnum.DISCONNECT_IVR);
		msgs.getCompReqEvents().clear();
		if(alwdOp.getAllwdOpCode().contains(CAPOpcode.DISCONNECT_FORWARD_CONNECTION)){
			if(cpb.isCalSegmentIDForDFCPresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Making the DFCWithArg msg");
				SasCapDFC.encodeDFCWithArg(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd DFCWithArg with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Successfully added DFCWithArg msg in msgs");
			}else {
				logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Making the DFC msg");
				SasCapDFC.encodeDFC(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd DFC with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Successfully added DFC msg in msgs");
			}
		}
		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		setDialogueEventAndState(continueReq, cpb, msgs, alwdOp);
		logger.info(Util.toString(cpb.dlgId) + "::: disconnectIvr:Exit");
	}

	/**
	 * This function will use cpb parameters to encode Apply Charging message
	 * and set the corresponding invokeReq events in msgs.
	 * For APPLY_CHARGING, application have to set the following params of cpb:maxCallPeriodDuration,tariifSwitchInterval,
	 * releaseIfDurationExced and partyToCharge. These all params have default values in cpb.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception If mandatory parameters are not set for any operation or any problem occurs in encoding of messages.
	 * 
	 */
	public void applyCharging(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: applyCharging:Enter");
		SasCapAlwdOp alwdObj = checkOperationAllowed(cpb, SasCapApiEnum.APPLY_CHARGING);
		msgs.getCompReqEvents().clear();
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.APPLY_CHARGING)){
			logger.info(Util.toString(cpb.dlgId) + "::: applyCharging:Making the APPLY_CHARGING msg");
			SasCapACR.encodeAC(source, cpb, msgs, ++cpb.invokeId);
			cpb.CDR += new Date()+"<--- snd ACH with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
			logger.info(Util.toString(cpb.dlgId) + "::: applyCharging: Successfully added the APPLY_CHARGING msg in msgs");
		}
		if(alwdObj.getAllwdOpCode().contains(CAPOpcode.CONTINUE)){//do changes in fsm
			if(cpb.isCalSegmentIDForContinueWithArgPresent() || cpb.islegIdForContinueWithArgPresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the ContinueWithArg msg");
				SasCapContinue.encodeContinueWithArg(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd continueWithArg with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully ContinueWithArg msg added in the msgs");
			}else{
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Making the Continue msg");
				SasCapContinue.encodeContinue(source, cpb, msgs, ++cpb.invokeId);
				cpb.CDR += new Date()+"<--- snd continue with invkId:" + cpb.invokeId+ " and dlgId:" + cpb.dlgId + "\n";
				logger.info(Util.toString(cpb.dlgId) + "::: connect:Successfully Continue msg added in the msgs");
			}
		}

		ContinueReqEvent continueReq = new ContinueReqEvent(source, cpb.dlgId);
		setDialogueEventAndState(continueReq, cpb, msgs, alwdObj);
		logger.info(Util.toString(cpb.dlgId) + "::: applyCharging:Exit");
	}

	/**
	 * This function will set userAbortEvent in DialogueReqEvent
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception 
	 */
	public void abort(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: abort:Enter");
		msgs.getCompReqEvents().clear();
		UserAbortReqEvent userAbort = new UserAbortReqEvent(source, cpb.dlgId);
		setDialoguePortion(cpb, source, userAbort);

		msgs.setDlgReqEvent(userAbort);
		cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
		cpb.stateInfo.setCurrState(null);
		cpb.CDR += new Date()+"<--- snd abort and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: abort:Exit");
	}
	
	/**
	 * This function will set TC_END in DialogueReqEvent
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * @param msgs	this object will contain array of events send to the network.
	 * @throws Exception 
	 */
	public void tcEnd(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: tcEnd:Enter");
		msgs.getCompReqEvents().clear();
		EndReqEvent endReqEvent = new EndReqEvent(source, cpb.dlgId);
		endReqEvent.setTermination(DialogueConstants.TC_BASIC_END);
		msgs.setDlgReqEvent(endReqEvent);
		cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
		cpb.stateInfo.setCurrState(null);
		cpb.CDR += new Date()+"<--- snd tcEnd and dlgId:" + cpb.dlgId + "\n";
		logger.info(Util.toString(cpb.dlgId) + "::: tcEnd:Exit");
	}

	/**
	 * This function will clean up all the occupied resources.
	 * and set the events in InvokeReqEvent.
	 * @param source source of the event
	 * @param cpb	this is the object of SasCapCallProcessBuffer
	 * 
	 */
	public void callCleanup(Object source, SasCapCallProcessBuffer cpb)  {
		logger.info("callCleanup:Enter");
		cpb.CDR += new Date()+"Cleanup the call \n";
		cpb = null ;	
		logger.info("callCleanup:Exit");
	}

	/**
	 * This function checks whether operation is allowed or not.
	 * @param cpb
	 * @param apiEnum
	 * @return instance of SasCapAlwdOp
	 * @throws OperationNotAllowedException
	 */
	private SasCapAlwdOp checkOperationAllowed(SasCapCallProcessBuffer cpb, SasCapApiEnum apiEnum) throws OperationNotAllowedException{
		logger.info(Util.toString(cpb.dlgId) + "::: checkOperationAllowed:Enter");
		String key = cpb.stateInfo.getCurrState().getCode() + "," + apiEnum.getCode() ;
		SasCapAlwdOp allwdOp = SasCapFsm.getOpAllwdMap().get(key);
		logger.debug(Util.toString(cpb.dlgId) + "::: checkOperationAllowed:key:" + key);
		logger.debug(Util.toString(cpb.dlgId) + "::: checkOperationAllowed:object found for this key and value is:" + allwdOp);
		if(allwdOp == null){
			logger.error(Util.toString(cpb.dlgId) + "::: checkOperationAllowed: " + apiEnum + " operation not allowed in the state:" + cpb.stateInfo.getCurrState());
			throw new OperationNotAllowedException( apiEnum + " operation not allowed in the state:" + cpb.stateInfo.getCurrState());
		}
		logger.info(Util.toString(cpb.dlgId) + "::: checkOperationAllowed:Exit");
		return allwdOp ;
	}


	private void setDialoguePortion(SasCapCallProcessBuffer cpb, Object source,DialogueReqEvent dlgEvent){
		logger.info(Util.toString(cpb.dlgId) + "::: setDialoguePortion:Making the DialoguePortion");
		DialoguePortion dlgPortion = new DialoguePortion(source);
		if(cpb.appContextIdentifier != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:appContextIdentifier in the DialoguePortion:"+ cpb.appContextIdentifier);
			dlgPortion.setAppContextIdentifier(cpb.appContextIdentifier);
		}
		if(cpb.appContextName != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:appContextName in the DialoguePortion:"+ cpb.appContextName);
			dlgPortion.setAppContextName(cpb.appContextName);
		}
		if(cpb.userInfo != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:userInfo in the DialoguePortion:"+ cpb.userInfo);
			dlgPortion.setUserInformation(cpb.userInfo);
		}
		if(cpb.securityContextIdentifier != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:securityContextIdentifier in the DialoguePortion:"+ cpb.securityContextIdentifier);
			dlgPortion.setSecurityContextIdentifier(cpb.securityContextIdentifier);
		}
		if(cpb.securityContextInfo != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:securityContextInfo in the DialoguePortion:"+ cpb.securityContextInfo);
			dlgPortion.setSecurityContextInformation(cpb.securityContextInfo);
		}
		if(cpb.protocolVersion != null){
			logger.info(Util.toString(cpb.dlgId) + "::: connect:protocolVersion in the DialoguePortion:"+ cpb.protocolVersion);
			dlgPortion.setProtocolVersion(cpb.protocolVersion);
		}
		dlgEvent.setDialoguePortion(dlgPortion);
		logger.info(Util.toString(cpb.dlgId) + "::: setDialoguePortion:Exit");
	}

	/**
	 * This function will make a dialogue type of either continue or End and set the same in msgs.
	 * @param dlgEvent
	 * @param cpb
	 * @param msgs
	 * @param alwdObj
	 */
	private void setDialogueEventAndState(DialogueReqEvent dlgEvent , SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, SasCapAlwdOp alwdObj ) {
		logger.info(Util.toString(cpb.dlgId) + "::: setDialogueEventAndState:Enter");
		msgs.setDlgReqEvent(dlgEvent);		
		logger.debug(Util.toString(cpb.dlgId) + "::: setDialogueEventAndState:Setting the Call state and current state was:" + cpb.stateInfo.getCurrState());
		cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
		logger.debug(Util.toString(cpb.dlgId) + "::: setDialogueEventAndState:Setting the Call state and next state is:" + alwdObj.getNextState());
		cpb.stateInfo.setCurrState(alwdObj.getNextState());
		logger.info(Util.toString(cpb.dlgId) + "::: setDialogueEventAndState:Exit");
	}
}
