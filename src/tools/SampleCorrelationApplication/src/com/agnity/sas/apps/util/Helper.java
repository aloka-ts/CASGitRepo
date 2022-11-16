package com.agnity.sas.apps.util;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.dialogue.BeginIndEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueReqEvent;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;
import jain.protocol.ss7.tcap.dialogue.NoticeIndEvent;
import jain.protocol.ss7.tcap.dialogue.ProviderAbortIndEvent;

import java.util.Date;
import java.util.EventObject;

import org.apache.log4j.Logger;

import com.agnity.sas.apps.domainobjects.ParsedIdp;
import com.agnity.sas.apps.domainobjects.SampleAppCallProcessBuffer;
import com.agnity.sas.apps.exceptions.MessageCreationFailedException;
import com.agnity.sas.apps.exceptions.MessageDecodeFailedException;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;


/**
 * This class have methods to process component and
 * dialogue.
 * @author saneja
 *
 */
public class Helper {

	//Instance of logger
	private static Logger logger = Logger.getLogger(Helper.class);	 

	public static void processDialogue(EventObject eventObject, SampleAppCallProcessBuffer cpb) throws ParameterNotSetException{
		int dlgId = cpb.getDlgId() ;
		logger.info(Util.toString(dlgId)+ "::: processDialogue:indication is Dialogue");
		DialogueIndEvent dilgEvent = (DialogueIndEvent)eventObject ;
		cpb.setLastDialoguePrimitive(dilgEvent.getPrimitiveType());
		switch (dilgEvent.getPrimitiveType()) {
		case TcapConstants.PRIMITIVE_BEGIN : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue begin indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_BEGIN) ;
			BeginIndEvent begin = (BeginIndEvent)eventObject ;
			cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.BEGIN_RECIEVED);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Next state:" + cpb.getStateInfo().getCurrState());
			cpb.setOriginatingAddress(begin.getOriginatingAddress());
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Origin Address: " + begin.getOriginatingAddress());
			cpb.setDestinationAddress(begin.getDestinationAddress());
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Destination Address: " + begin.getDestinationAddress());
			cpb.getCdr().append(new Date() + "--->Begin Rcvd:dlgId:" + dlgId);; 
			break ;
		}
		case TcapConstants.PRIMITIVE_CONTINUE : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue continue indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_CONTINUE) ;
			cpb.getCdr().append(new Date() + "--->CNTE Rcvd:dlgId:" + dlgId); 
			break ;
		}
		case TcapConstants.PRIMITIVE_END : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue end indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_END) ;
			EndIndEvent endIndEvent = (EndIndEvent)eventObject ;
			if(! endIndEvent.isComponentsPresent()){
				logger.debug(Util.toString(dlgId) + "::: processDialogue: NO component in TC_END:"+endIndEvent.isComponentsPresent());
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
				cpb.getStateInfo().setCurrState(null);
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			}
			cpb.getCdr().append(new Date() + "--->END Rcvd:dlgId:" + dlgId);
			break ;
		}
		case TcapConstants.PRIMITIVE_PROVIDER_ABORT : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue provider abort indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_PROVIDER_ABORT) ;
			ProviderAbortIndEvent provdAbort = (ProviderAbortIndEvent)eventObject ;
			try {
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Provider Abort Reason:" + provdAbort.getPAbort());
			} catch (MandatoryParameterNotSetException e) {
				logger.error(Util.toString(dlgId) + "::: processDialogue: MandatoryParameterNotSetException ", e);
				throw e ;
			}
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getStateInfo().setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getCdr().append(new Date() + "--->PABORT Rcvd:dlgId:" + dlgId);
			break ;
		}
		case TcapConstants.PRIMITIVE_USER_ABORT : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue user abort indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_USER_ABORT) ;
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getStateInfo().setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getCdr().append(new Date() + "--->UABORT Rcvd:dlgId:" + dlgId);
			break ;
		}
		case TcapConstants.PRIMITIVE_NOTICE : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue notice indication recieved");
			cpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_NOTICE) ;
			NoticeIndEvent noticeIndEvent = (NoticeIndEvent)eventObject;
			if(noticeIndEvent.isReportCausePresent()){
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + Util.formatBytes(noticeIndEvent.getReportCause()));
			}
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getStateInfo().setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.getStateInfo().getCurrState());
			cpb.getCdr().append(new Date() + "--->NOTICE Recvd:dlgId:" + dlgId);
			break ;
		}
		default : {
			logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue NOT RECOGNIZED recieved: "+ dilgEvent.getPrimitiveType());
		}
		}

	}

	public static void processComponent(EventObject eventObject, SampleAppCallProcessBuffer cpb) 
	throws MandatoryParameterNotSetException,ParameterNotSetException,InvalidInputException,Exception {
		int dlgId = cpb.getDlgId() ;
		logger.info(Util.toString(dlgId) + "::: processComponent:Enter");

		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		logger.debug(Util.toString(dlgId) + "::: processComponent:Received component primitive:" + cmpReqEvent.getPrimitiveType());
		switch (cmpReqEvent.getPrimitiveType()) {
		case TcapConstants.PRIMITIVE_INVOKE : {				
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_INVOKE");
			// cast to an Invoke Indication Event
			InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
			Operation opr;
			byte[] opCode ;
			byte[] parms ;
			String opCodeStr ;
			try {
				opr = receivedInvoke.getOperation();
				opCode = opr.getOperationCode();
				opCodeStr = Util.formatBytes(opCode);
				logger.debug(Util.toString(dlgId) + "::: processComponent:opcode:"+ opCodeStr);
				parms = receivedInvoke.getParameters().getParameter();
				int paramIdentifier = (receivedInvoke.getParameters()).getParameterIdentifier();
				logger.debug(Util.toString(dlgId) + "::: processComponent:params identifier from network: "+ paramIdentifier);
				logger.debug(Util.toString(dlgId) + "::: processComponent:params received from network: "+ Util.formatBytes(parms));

			} catch (MandatoryParameterNotSetException e) {
				logger.error(Util.toString(dlgId) + "::: processComponent:" , e);
				throw e ;
			} catch (ParameterNotSetException pe) {
				logger.error(Util.toString(dlgId) + "::: processComponent:" , pe);
				throw pe ;
			}
			// Operation code is IDP
			if(opCodeStr.equals(InapOpCodes.IDP)){
				logger.info(Util.toString(dlgId) + "::: processComponent: processing Idp");
				cpb.setDpCount(cpb.getDpCount() + 1) ;
				logger.info(Util.toString(dlgId) + "::: processComponent: dp counter value is: " + cpb.getDpCount());
				cpb.getCdr().append(" with IDP \n"); 
				processIdp(dlgId, cpb, parms,receivedInvoke);	
				logger.info(Util.toString(dlgId) + "::: processComponent: processed Idp");
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.getStateInfo().getCurrState());
				cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.ANALYZED_INFORMATION);
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.getStateInfo().getCurrState());
			}	
			//XXX add handle for other opcodes here

			break ;
		}//end of invoke component
		case TcapConstants.PRIMITIVE_RESULT : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT");

			//casting to result indication event
			ResultIndEvent resultIndEvent = (ResultIndEvent)eventObject ;
			if(resultIndEvent.isLastResultEvent()){
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT last result component");
				//TODO Add logic

			}else {
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT Not a last result component");
			}
			break ;
		}
		case TcapConstants.PRIMITIVE_ERROR : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_ERROR");
			//casting to ErrorIndEvent for future purpose
			ErrorIndEvent errorInd = (ErrorIndEvent)cmpReqEvent;
			logger.debug(Util.toString(dlgId) + "::: processComponent: Error Type :" + errorInd.getErrorType());
			byte[] error = errorInd.getErrorCode();
			logger.debug(Util.toString(dlgId) + "::: processComponent: Error :" + Util.formatBytes(error));

			//TODO add logic

			break ;
		}
		case TcapConstants.PRIMITIVE_REJECT : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_REJECT");
			//casting to RejectIndEvent for future purpose
			RejectIndEvent rejectInd = (RejectIndEvent)cmpReqEvent ;
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem Type :" + rejectInd.getProblemType());
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem :" + rejectInd.getProblem());
			//TODO add logic
			break ;
		}
		default : {
			logger.debug(Util.toString(dlgId) + "::: processComponent: Component NOT RECOGNIZED recieved: " + cmpReqEvent.getPrimitiveType());
		}
		}

	}

	private static void processIdp(int dlgId, SampleAppCallProcessBuffer cpb,
			byte[] parms, InvokeIndEvent invokeIndEvent) throws MessageDecodeFailedException {
		ParsedIdp idp=InapIsupParser.parseIDP(parms,invokeIndEvent);
		logger.info(Util.toString(dlgId)+ " IDP parsed");
		cpb.setIdpContent(idp);

	}

	public static InvokeReqEvent createEtc(Object source,SampleAppCallProcessBuffer buffer, int corrId,
			String assistingSspIp) throws MessageCreationFailedException {
		logger.info(Util.toString(buffer.getDlgId())+ " creating ETC");
		
		buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.SENDING_ETC);
		
		buffer.setAssistingSspIp(assistingSspIp);
		buffer.setCorrId(corrId);
		buffer.incrementInvokeId();
		int invokeId=buffer.getInvokeId();
				
		byte[] etc=InapIsupParser.createEtc(assistingSspIp,Integer.toString(corrId));
		
		
		byte[] etcOpCode = {0x11} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, etcOpCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(source, buffer.getDlgId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, etc));
		ire.setClassType(Constants.ETC_CLASS);
		
		logger.info(Util.toString(buffer.getDlgId())+ " ETC req event created");
		return ire;
		
	}
	
	public static InvokeReqEvent createCon(Object source,SampleAppCallProcessBuffer buffer, int corrId,
			String destinationRoutingAddress) throws MessageCreationFailedException {
		logger.info(Util.toString(buffer.getDlgId())+ " creating CON");
		
		buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.SENDING_CON);
		
		buffer.setDestinationRoutingAddress(destinationRoutingAddress);
		buffer.setSipTcorrId(corrId);
		buffer.incrementInvokeId();
		int invokeId=buffer.getInvokeId();
				
		byte[] con=InapIsupParser.createCon(destinationRoutingAddress,Integer.toString(corrId));
		
		
		byte[] conOpCode = {0x14} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, conOpCode);
				
		InvokeReqEvent ire = new InvokeReqEvent(source, buffer.getDlgId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, con));
		ire.setClassType(Constants.CON_CLASS);
		
		logger.info(Util.toString(buffer.getDlgId())+ " CON req event created");
		return ire;
		
	}
	
	public static InvokeReqEvent createDfc(Object source,SampleAppCallProcessBuffer buffer ) throws MessageCreationFailedException {
		logger.info(Util.toString(buffer.getDlgId())+ " creating DFC");
		
		buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.SENDING_DFC);
		
		buffer.incrementInvokeId();
		int invokeId=buffer.getInvokeId();
				
		byte[] dfcOpCode = {0x12} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, dfcOpCode);
				
		InvokeReqEvent ire = new InvokeReqEvent(source, buffer.getDlgId(), requestOp);
		ire.setInvokeId(invokeId);
		//ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, null));
		ire.setClassType(Constants.DFC_CLASS);
		
		logger.info(Util.toString(buffer.getDlgId())+ " DFC req event created");
		return ire;
		
	}

	public static ContinueReqEvent createContinueDialogReqEvent(Object source,SampleAppCallProcessBuffer buffer) {
		logger.info(Util.toString(buffer.getDlgId())+ " creating Continue dialog");
		ContinueReqEvent continueReq=new ContinueReqEvent(source, buffer.getDlgId());
		if(buffer.isDialoguePortionPresent()){
			setDialoguePortion(buffer, source, continueReq);
			buffer.setDialoguePortionPresent(false) ;
		}
		continueReq.setOriginatingAddress(buffer.getOriginatingAddress());
//		continueReq.setOriginatingAddress(SampleTestApp.localAddr);
		return continueReq;
	}
	
	public static EndReqEvent createEndDialogReqEvent(Object source,SampleAppCallProcessBuffer buffer) {
		logger.info(Util.toString(buffer.getDlgId())+ " creating end dialog");
		EndReqEvent endReq=new EndReqEvent(source, buffer.getDlgId());
		if(buffer.isDialoguePortionPresent()){
			setDialoguePortion(buffer, source, endReq);
			buffer.setDialoguePortionPresent(false) ;
		}
		
		return endReq;
	}

	private static void setDialoguePortion(SampleAppCallProcessBuffer cpb, Object source,DialogueReqEvent dlgEvent){
		logger.info(Util.toString(cpb.getDlgId()) + "::: setDialoguePortion:Making the DialoguePortion");
		DialoguePortion dlgPortion = new DialoguePortion(source);
		if(cpb.getAppContextIdentifier() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:appContextIdentifier in the DialoguePortion:"+ cpb.getAppContextIdentifier());
			dlgPortion.setAppContextIdentifier(cpb.getAppContextIdentifier());
		}
		if(cpb.getAppContextName() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:appContextName in the DialoguePortion:"+ cpb.getAppContextName());
			dlgPortion.setAppContextName(cpb.getAppContextName());
		}
		if(cpb.getUserInfo() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:userInfo in the DialoguePortion:"+ cpb.getUserInfo());
			dlgPortion.setUserInformation(cpb.getUserInfo());
		}
		if(cpb.getSecurityContextIdentifier() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:securityContextIdentifier in the DialoguePortion:"+ cpb.getSecurityContextIdentifier());
			dlgPortion.setSecurityContextIdentifier(cpb.getSecurityContextIdentifier());
		}
		if(cpb.getSecurityContextInfo() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:securityContextInfo in the DialoguePortion:"+ cpb.getSecurityContextInfo());
			dlgPortion.setSecurityContextInformation(cpb.getSecurityContextInfo());
		}
		if(cpb.getProtocolVersion() != null){
			logger.info(Util.toString(cpb.getDlgId()) + "::: connect:protocolVersion in the DialoguePortion:"+ cpb.getProtocolVersion());
			dlgPortion.setProtocolVersion(cpb.getProtocolVersion());
		}
		dlgEvent.setDialoguePortion(dlgPortion);
		logger.info(Util.toString(cpb.getDlgId()) + "::: setDialoguePortion:Exit");
	}
}
