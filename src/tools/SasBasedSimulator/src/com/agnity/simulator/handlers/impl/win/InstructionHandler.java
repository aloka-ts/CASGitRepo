package com.agnity.simulator.handlers.impl.win;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.io.IOException;
import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.InstrctnReqNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.AnalyzedInformation;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class InstructionHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(InstructionHandler.class);
	private static Handler handler;
	
	private static final int InstructionReq_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (InstructionHandler.class) {
				if (handler == null) {
					handler = new InstructionHandler();
				}
			}
		}
		return handler;
	}

	private InstructionHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside InstructionReq handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.INSTRUCTIONREQ))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		//dummy val to be used as byte array
		byte [] dummyIRVal = {(byte)0x02,(byte)0x03};
		InstrctnReqNode instrctnReqNode = (InstrctnReqNode)node;
		
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionReqHandler processNode()-->generating Dialog ID");
		
		/*
		 * Reeta generated dialogue id here 
		 */
//		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("InstructionReqHandler processNode()-->Got dialog ID["+simCpb.getDialogId()+"]");
//		
//		simCpb.setDialogId(dialogueId);
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setWinReqDialogId(Constants.INSTRUCTIONREQ,simCpb.getDialogId());
		simCpb.setWinReqInvokeId(Constants.INSTRUCTIONREQ,invokeId);
	//	simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionReqHandler processNode()-->IR dummy byte array creating reqEvent["+dummyIRVal+"]");
		
		byte[] operationCode = {WinOpCodes.IR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, dummyIRVal));
		ire.setClassType(InstructionReq_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionReqHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending InstructionReq component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("InstructionReqHandler processNode()-->component send");
		if(instrctnReqNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("InstructionReqHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.INSTRUCTIONREQ);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), instrctnReqNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("InstructionReqHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on InstructionReq::"+instrctnReqNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on InstructionReq::"+instrctnReqNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving InstructionReq processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InstructionReqHandler");
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("InstructionReqHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		int dialogId,invokeId;
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		try {
			
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for InstructionReqHandler--> getDialogueId:: "
								+dialogId);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}
		
	
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InstructionReqHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.INSTRUCTIONREQ, dialogId);
		simCpb.setWinReqInvokeId(Constants.INSTRUCTIONREQ, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for InstructionReqHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.INSTRUCTIONREQ))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a InstructionReq Node");
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

		InvokeIndEvent receivedInvoke = (InvokeIndEvent) message;
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
			if (logger.isDebugEnabled())
				logger.debug("InstructionHandler validateMessage() isValid::["
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