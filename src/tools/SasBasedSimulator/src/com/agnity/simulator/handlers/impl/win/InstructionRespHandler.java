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
import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.InstrctnReqNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.InstructionRespNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.util.Util;


public class InstructionRespHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(InstructionRespHandler.class);
	private static Handler handler;
	
	
	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (InstructionRespHandler.class) {
				if (handler == null) {
					handler = new InstructionRespHandler();
				}
			}
		}
		return handler;
	}

	private InstructionRespHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside InstructionResp handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.INSTRUCTIONRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		//dummy val to be used as byte array
		byte [] dummyIRVal = {(byte)0x02,(byte)0x03};
		InstructionRespNode instrctnRespNode = (InstructionRespNode)node;
		
		
//		if(logger.isDebugEnabled())
//			logger.debug("InstructionRespHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("InstructionRespHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		//int invokeId = simCpb.incrementAndGetInvokeId();
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionRespHandler processNode()-->IR dummy byte array creating RespEvent["+dummyIRVal+"]");
		
		byte[] operationCode = {WinOpCodes.IR_BYTE};
		Operation RespuestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.INSTRUCTIONREQ), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.INSTRUCTIONREQ));
		rre.setOperation(RespuestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.INSTRUCTIONREQ));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, dummyIRVal));
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionRespHandler processNode()-->RespEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending InstructionResp component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionRespHandler processNode()-->component send");
		if(instrctnRespNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("InstructionRespHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.INSTRUCTIONRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), instrctnRespNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("InstructionRespHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on InstructionResp::"+instrctnRespNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on InstructionResp::"+instrctnRespNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving InstructionResp processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InstructionRespHandler");
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("InstructionRespHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InstructionRespHandler leaving with status true");
		simCpb.removeDialogId(Constants.INSTRUCTIONRES); //Reeta
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for InstructionRespHandler");

		if (!(message instanceof ResultIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.INSTRUCTIONRES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a InstructionResp Node");
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

		ResultIndEvent receivedResult = (ResultIndEvent) message;
		Operation opr;
		byte[] opCode;
		String opCodeStr = null;
		boolean isValid = false;
		try {
				opr = receivedResult.getOperation();
				opCode = opr.getOperationCode();
				opCodeStr = Util.formatBytes(opCode);
			if ((opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase()))&& (dialogType == tcapNode.getDialogType())) {
				isValid = true;
			}
			if(receivedResult.getDialogueId()!=simCpb.getWinReqDialogId(Constants.INSTRUCTIONREQ)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedResult.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.INSTRUCTIONREQ)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if (logger.isDebugEnabled())
				logger.debug("InstructionResHandler validateMessage() isValid::["
						+ isValid + "]  Expected opcode::["
						+ tcapNode.getOpCodeString() + "] Actual Opcode::["
						+ opCodeStr + "] Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");

		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		} catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("ParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}

}