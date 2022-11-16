package com.camel.CAPMsg;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.dialogue.BeginIndEvent;
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;
import jain.protocol.ss7.tcap.dialogue.NoticeIndEvent;
import jain.protocol.ss7.tcap.dialogue.ProviderAbortIndEvent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.EventObject;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.ReceivedInformationArg;

import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have methods to process component and
 * dialogue.
 * @author nkumar
 *
 */
public class SasCapMsg {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapMsg.class);	 

	public static void processComponent(EventObject eventObject, SasCapCallProcessBuffer cpb) 
	throws MandatoryParameterNotSetException,ParameterNotSetException,InvalidInputException,Exception {
		int dlgId = cpb.dlgId ;
		logger.info(Util.toString(dlgId) + "::: processComponent:Enter");
		IDecoder decoder;
		try {
			decoder = CoderFactory.getInstance().newDecoder("BER");
			logger.debug(Util.toString(dlgId) + "::: processComponent:decoder" + decoder);
		} catch (Exception e) {
			logger.error(Util.toString(dlgId) + "::: processComponent:" , e);
			throw e;
		}
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
			if(opCodeStr.equals(CAPOpcode.IDP)){
				logger.info(Util.toString(dlgId) + "::: processComponent: processing Idp");
				cpb.dpCount++ ;
				logger.info(Util.toString(dlgId) + "::: processComponent: dp counter value is: " + cpb.dpCount);
				cpb.CDR += " with IDP \n"; 
				logger.debug(Util.toString(dlgId) + "::: processComponent:decoder" + decoder);
				SasCapIdp.processIdp(dlgId, cpb, parms, decoder);	
				logger.info(Util.toString(dlgId) + "::: processComponent: processed Idp");
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
				cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.ANALYZED_INFORMATION);
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			}	
			// Operation Code is EventReportBCSM
			if(opCodeStr.equals(CAPOpcode.EVENT_REPORT_BCSM)){
				logger.info(Util.toString(dlgId) + "::: processComponent: processing EVENT_REPORT_BCSM");
				cpb.dpCount++ ;
				logger.info(Util.toString(dlgId) + "::: processComponent: dp counter value is: "+ cpb.dpCount);
				cpb.CDR += " with ERBCSM  \n"; 
				SasCapEventReport.setEventReprtParamsOfCpb(dlgId, parms, cpb, decoder);
				logger.info(Util.toString(dlgId) + "::: processComponent: processed EVENT_REPORT_BCSM");
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getPrevState());
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			}
			// OperationCode is ApplyCharging Report
			if(opCodeStr.equals(CAPOpcode.APPLY_CHARGING_REPORT)) {
				logger.info(Util.toString(dlgId) + "::: processComponent: processing the ACR");
				cpb.CDR += " with ACR \n"; 
				logger.info(Util.toString(dlgId) + "::: processComponent: Sending no modified params..");
				SasCapACR.processACR(dlgId, cpb, parms, decoder);
				logger.info(Util.toString(dlgId) + "::: processComponent: Successfully processed the ACR");
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
				cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.CONNECTED_ACHRPT);
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			}
			// Operation code is SPECIALIZED_RSOURCE_RPRT
			if(opCodeStr.equalsIgnoreCase(CAPOpcode.SPECIALIZED_RSOURCE_RPRT)){
				logger.info(Util.toString(dlgId) + "::: processComponent: SPECIALIZED_RSOURCE_RPRT");
				cpb.CDR += " with SRR \n"; 
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
				cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			}
			// Operation code is CALL_INFORMATION_RPT
			if(opCodeStr.equalsIgnoreCase(CAPOpcode.CALL_INFORMATION_RPT)){
				logger.info(Util.toString(dlgId) + "::: processComponent: processing CALL_INFORMATION_RPT");
				cpb.CDR += " with CIR \n"; 
				SasCapCIR.processCIRp(dlgId, cpb, parms, decoder);
				logger.info(Util.toString(dlgId) + "::: processComponent: processed CALL_INFORMATION_RPT");
				logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
				cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.CALL_INFO_RPT);
				logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			}
			// Operation code is ENTITY_RELEASED
			if(opCodeStr.equalsIgnoreCase(CAPOpcode.ENTITY_RELEASED)){
				logger.info(Util.toString(dlgId) + "::: processComponent: processing ENTITY_RELEASED");
				cpb.CDR += " with ER \n"; 
				SasCapCIR.processEntityReleased(dlgId, cpb, parms, decoder);
				logger.info(Util.toString(dlgId) + "::: processComponent: processed ENTITY_RELEASED");
				// change in call state
			}

			break ;
		}//end of invoke component
		case TcapConstants.PRIMITIVE_RESULT : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT");
			cpb.CDR += " with RESULT \n"; 
			//casting to result indication event
			ResultIndEvent resultIndEvent = (ResultIndEvent)eventObject ;
			if(resultIndEvent.isLastResultEvent()){
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT last result component");
				Operation op = null;
				byte[] opCode = null;
				if(resultIndEvent.isOperationPresent()){
					logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT operation present");
					op = resultIndEvent.getOperation();
					opCode = op.getOperationCode();
				}else{
					if(resultIndEvent.isInvokeIdPresent()){
						int invokeId = resultIndEvent.getInvokeId();
						logger.info(Util.toString(dlgId) + "::: processComponent:invoke Id present:" + invokeId);
						Byte opCd = cpb.invokeId_Opcode.get(invokeId);
						if(opCd != null){
							opCode = new byte[1];
							opCode[0] = opCd ;
						}
					}						
				}
				if(opCode != null){
					String opCodeStr = Util.formatBytes(opCode);
					if(logger.isDebugEnabled())		
						logger.debug(Util.toString(dlgId) + "::: processComponent:opcode:"+ opCodeStr);
					if(opCodeStr.equalsIgnoreCase(CAPOpcode.PROMPT_COLLECT_USER_INFO)){
						if(resultIndEvent.isParametersPresent()){
							logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT parameters present");
							byte[] params = resultIndEvent.getParameters().getParameter();
							InputStream ins = new ByteArrayInputStream(params);
							ReceivedInformationArg recvdArg = null;
							try {
								logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT decoding the ReceivedInformationArg");
								recvdArg = decoder.decode(ins, ReceivedInformationArg.class);
								logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT successfully decoded the ReceivedInformationArg");
								cpb.promptCollectUserInfo = GenericDigitsDataType.decodeGenericDigits(recvdArg.getDigitsResponse().getValue());
							} catch (Exception e) {
								logger.error(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT:" , e);
								throw e ;
							}
						}
						logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
						cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
						cpb.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
						logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
					} else if(opCodeStr.equalsIgnoreCase(CAPOpcode.ACTIVITY_TEST_RESULT)){
						logger.info(Util.toString(dlgId) + "::: processComponent:Result of ACTIVITY_TEST got it.");
						cpb.activityTestresultReceived = true ;
					}
				}
			}else {
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT Not a last result component");
			}
			break ;
		}
		case TcapConstants.PRIMITIVE_ERROR : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_ERROR");
			cpb.CDR += " with ERROR \n"; 
			//casting to ErrorIndEvent for future purpose
			ErrorIndEvent errorInd = (ErrorIndEvent)cmpReqEvent;
			logger.debug(Util.toString(dlgId) + "::: processComponent: Error Type :" + errorInd.getErrorType());
			byte[] error = errorInd.getErrorCode();
			logger.debug(Util.toString(dlgId) + "::: processComponent: Error :" + Util.formatBytes(error));
			logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.ERROR_STATE);
			logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			break ;
		}
		case TcapConstants.PRIMITIVE_REJECT : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_REJECT");
			cpb.CDR += " with REJECT  \n"; 
			//casting to RejectIndEvent for future purpose
			RejectIndEvent rejectInd = (RejectIndEvent)cmpReqEvent ;
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem Type :" + rejectInd.getProblemType());
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem :" + rejectInd.getProblem());
			logger.debug(Util.toString(dlgId) + "::: processComponent: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.ERROR_STATE);
			logger.debug(Util.toString(dlgId) + "::: processComponent: Next state:" + cpb.stateInfo.getCurrState());
			break ;
		}
		default : {
			logger.debug(Util.toString(dlgId) + "::: processComponent: Component NOT RECOGNIZED recieved: " + cmpReqEvent.getPrimitiveType());
		}
		}

	}

	public static void processDialogue(EventObject eventObject, SasCapCallProcessBuffer cpb) throws ParameterNotSetException{
		int dlgId = cpb.dlgId ;
		logger.info(Util.toString(dlgId) + "::: processDialogue:indication is Dialogue");
		DialogueIndEvent dilgEvent = (DialogueIndEvent)eventObject ;
		cpb.lastDialoguePrimitive =  dilgEvent.getPrimitiveType();
		switch (dilgEvent.getPrimitiveType()) {
		case TcapConstants.PRIMITIVE_BEGIN : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue begin indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_BEGIN ;
			BeginIndEvent begin = (BeginIndEvent)eventObject ;
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.ANALYZED_INFORMATION);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Next state:" + cpb.stateInfo.getCurrState());
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Origin Address: " + begin.getOriginatingAddress());
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Destination Address: " + begin.getDestinationAddress());
			cpb.CDR += new Date() + "--->Begin Rcvd:dlgId:" + dlgId; 
			break ;
		}
		case TcapConstants.PRIMITIVE_CONTINUE : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue continue indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_CONTINUE ;
			cpb.CDR += new Date() + "--->CNTE Rcvd:dlgId:" + dlgId; 
			break ;
		}
		case TcapConstants.PRIMITIVE_END : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue end indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_END ;
			EndIndEvent endIndEvent = (EndIndEvent)eventObject ;
			if(! endIndEvent.isComponentsPresent()){
				logger.debug(Util.toString(dlgId) + "::: processDialogue: NO component in TC_END:"+endIndEvent.isComponentsPresent());
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
				cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
				cpb.stateInfo.setCurrState(null);
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			}
			cpb.CDR += new Date() + "--->END Rcvd:dlgId:" + dlgId;
			break ;
		}
		case TcapConstants.PRIMITIVE_PROVIDER_ABORT : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue provider abort indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_PROVIDER_ABORT ;
			ProviderAbortIndEvent provdAbort = (ProviderAbortIndEvent)eventObject ;
			try {
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Provider Abort Reason:" + provdAbort.getPAbort());
			} catch (MandatoryParameterNotSetException e) {
				logger.error(Util.toString(dlgId) + "::: processDialogue: MandatoryParameterNotSetException ", e);
				throw e ;
			}
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.CDR += new Date() + "--->PABORT Rcvd:dlgId:" + dlgId;
			break ;
		}
		case TcapConstants.PRIMITIVE_USER_ABORT : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue user abort indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_USER_ABORT ;
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.CDR += new Date() + "--->UABORT Rcvd:dlgId:" + dlgId;
			break ;
		}
		case TcapConstants.PRIMITIVE_NOTICE : {
			logger.info(Util.toString(dlgId) + "::: processDialogue: dialogue notice indication recieved");
			cpb.lastDialoguePrimitive = TcapConstants.PRIMITIVE_NOTICE ;
			NoticeIndEvent noticeIndEvent = (NoticeIndEvent)eventObject;
			if(noticeIndEvent.isReportCausePresent()){
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + Util.formatBytes(noticeIndEvent.getReportCause()));
			}
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(null);
			logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + cpb.stateInfo.getCurrState());
			cpb.CDR += new Date() + "--->NOTICE Recvd:dlgId:" + dlgId;
			break ;
		}
		default : {
			logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue NOT RECOGNIZED recieved: "+ dilgEvent.getPrimitiveType());
		}
		}

	}
}
