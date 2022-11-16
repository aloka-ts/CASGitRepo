package com.agnity.simulator.handlers.impl.win;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.component.ResultReqEvent;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.CallControlDirNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.CallControlDirRespNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResRespNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.CallControlDirectiveRes;
import com.agnity.win.asngenerated.CallStatus;
import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SeizeResourceRes;
import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.asngenerated.CallControlDirectiveRes.CallControlDirectiveResSequenceType;
import com.agnity.win.asngenerated.SeizeResource.SeizeResourceSequenceType;
import com.agnity.win.asngenerated.SeizeResourceRes.SeizeResourceResSequenceType;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class CallControlDirHandlerResp extends AbstractHandler {

	Logger logger = Logger.getLogger(CallControlDirHandlerResp.class);
	private static Handler handler;

	private static final String CALLCNTRLDIRRESP_CALL_STATUS = "callStatus".toLowerCase();
	
	
	private static final int CALLCNTRLDIRRESP_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (CallControlDirHandlerResp.class) {
				if (handler == null) {
					handler = new CallControlDirHandlerResp();
				}
			}
		}
		return handler;
	}

	private CallControlDirHandlerResp() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside CALLCNTRLDIRRESP handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.CALLCONTROLDIRRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		CallControlDirRespNode callCntrlDirResNode = (CallControlDirRespNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		CallControlDirectiveRes callCntrlDirRes = new CallControlDirectiveRes();
		CallControlDirectiveResSequenceType callCntrlDirResSeqTyp = new CallControlDirectiveResSequenceType();	
						
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(CALLCNTRLDIRRESP_CALL_STATUS)){
					 CallStatus callStatus = new CallStatus();
					 String value = fieldElem.getValue(varMap);
					 CallStatus.EnumType callStatusEnum = CallStatus.EnumType.valueOf(value);
					 callStatus.setValue(callStatusEnum);
					 callCntrlDirResSeqTyp.setCallStatus(callStatus);
				 }
			}
		}
		
		
		callCntrlDirRes.setValue(callCntrlDirResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(callCntrlDirRes);
		opCode.add(WinOpCodes.CALL_CNTRL_DIR);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] callCntrlDirResp = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for CALLCNTRLDIRRESPHandler--> Got seizeRes byte array:: "+Util.formatBytes(callCntrlDirResp));
		
//		if(logger.isDebugEnabled())
//			logger.debug("CALLCNTRLDIRRESPHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("CALLCNTRLDIRRESPHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
//		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCNTRLDIRRESPHandler processNode()-->SeizeResResp byte array generated creating reqEvent["+callCntrlDirResp+"]");
		
		byte[] operationCode = {WinOpCodes.CALL_CNTRL_DIR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.CALLCONTROLDIRREQ), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.CALLCONTROLDIRREQ));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.CALLCONTROLDIRREQ));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, callCntrlDirResp));
		//rre.setClassType(CALLCNTRLDIRRESP_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCNTRLDIRRESPHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending CALLCNTRLDIRRESPHandler component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCNTRLDIRRESPHandler processNode()-->component send");
		if(callCntrlDirResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("CALLCNTRLDIRRESPHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.CALLCONTROLDIRRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), callCntrlDirResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("CALLCNTRLDIRRESPHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on seizeRes::"+callCntrlDirResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on seizeRes::"+callCntrlDirResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving CALLCNTRLDIRRESPHandler processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CALLCNTRLDIRRESPHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("CALLCNTRLDIRRESPHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
		//Iterator<Node> subElemIterator = subElem.iterator();
		ResultIndEvent resIndEvent = (ResultIndEvent) message;
		CallControlDirectiveRes callCntrlDirResp = null;
		try {
			byte[] paramCallCntrlDirRes = resIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for CALLCNTRLDIRRESPHandler--> starting first level decoding on CALLCNTRLDIRRESP bytes:: "
								+ Util.formatBytes(paramCallCntrlDirRes));
			callCntrlDirResp = (CallControlDirectiveRes) WinOperationsCoding.decodeOperation(paramCallCntrlDirRes, resIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (callCntrlDirResp == null) {
			if (logger.isDebugEnabled())
				logger.debug("CALLCNTRLDIRRESP is received null in processReceivedMessage() in CALLCNTRLDIRRESPHandler");
			return false;
		}

		CallControlDirectiveResSequenceType callCntrlDirRespSeqTyp = callCntrlDirResp.getValue();
		CallStatus callStatus = null;		
		
		try{
			callStatus = callCntrlDirRespSeqTyp.getCallStatus();
			if(callStatus!=null){
				logger.debug("value of callStatus is "+callStatus.getValue().toString());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CallCntrlDirResHandler leaving with status true");
		simCpb.removeDialogId(Constants.CALLCONTROLDIRREQ);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for CallCntrlDirResHandler");

		if (!(message instanceof ResultIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.CALLCONTROLDIRRES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a callCntrlDirResResp Node");
			return false;
		}

		if (!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		ResultIndEvent receivedInvoke = (ResultIndEvent) message;
		Operation opr;
		byte[] opCode;
		String opCodeStr = null;
		boolean isValid = false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if ((opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase()))&& (dialogType == tcapNode.getDialogType())) {
				isValid = true;
			}
			if(receivedInvoke.getDialogueId()!=simCpb.getWinReqDialogId(Constants.CALLCONTROLDIRREQ)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedInvoke.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.CALLCONTROLDIRREQ)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if (logger.isDebugEnabled())
				logger.debug("CALLCNTRLDIRRESPHandler validateMessage() isValid::["
						+ isValid + "]  Expected opcode::["
						+ tcapNode.getOpCodeString() + "] Actual Opcode::["
						+ opCodeStr + "] Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");

		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("ParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}

}