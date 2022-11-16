package com.agnity.simulator.handlers.impl.win;


import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
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
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OrreqRetResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.AccessDeniedReason;
import com.agnity.win.asngenerated.OriginationRequestRes;
import com.agnity.win.asngenerated.OriginationRequestRes.OriginationRequestResSequenceType;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;




public class OrigReqRetResultHandler extends AbstractHandler{
	
	Logger logger = Logger.getLogger(OrigReqRetResultHandler.class);
	private static Handler handler;
	
	private static final String orreq_FIELD_ACCSS_DENIED_REASON = "AccessDeniedReason".toLowerCase();
	
	private static final int ORREQRES_CLASS = 2;
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (OrigReqRetResultHandler.class) {
				if(handler ==null){
					handler = new OrigReqRetResultHandler();
				}
			}
		}
		return handler;
	}
	
	private OrigReqRetResultHandler(){
		
	}
	
	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside OrigReqRetResult handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.ORIG_REQ_RET_RESULT))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		OrreqRetResNode orreqNode = (OrreqRetResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		OriginationRequestRes orreqres = new OriginationRequestRes();	
		OriginationRequestResSequenceType origReqSeqRetTyp = new OriginationRequestResSequenceType();
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				if(fieldElem.getFieldType().equals(orreq_FIELD_ACCSS_DENIED_REASON)){
					String value = fieldElem.getValue(varMap);
					AccessDeniedReason accssDenieedReason = new AccessDeniedReason();
					accssDenieedReason.setValue(AccessDeniedReason.EnumType.valueOf(value));
					origReqSeqRetTyp.setAccessDeniedReason(accssDenieedReason);
				}				
			}
		}
		
		orreqres.setValue(origReqSeqRetTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(orreqres);
		opCode.add(WinOpCodes.OR);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] orreq = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for OrigReqRetResultHandler--> Got orreq byte array:: "+Util.formatBytes(orreq));
						
		if(logger.isDebugEnabled())
			logger.debug("OrigReqRetResultHandler processNode()-->OrigReqRetResult byte array generated creating reqEvent["+orreq+"]");
		
		byte[] operationCode = {0x2F};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.ORREQ), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.ORREQ));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.ORREQ));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, orreq));
		//rre.setClassType(ORREQRES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("OrigReqRetResultHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending orreq component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("OrigReqRetResultHandler processNode()-->component send");
		if(orreqNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("OrigReqRetResultHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.ORIG_REQ_RET_RESULT);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), orreqNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("OrigReqRetResultHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on orreq::"+orreqNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on orreq::"+orreqNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving orreq processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OrigReqRetResultHandler");
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if(subElem.size()==0){
			if(logger.isDebugEnabled())
				logger.debug("OrigReqRetResultHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		//ComponentIndEvent compIndEvent = (ComponentIndEvent) message;
		ResultIndEvent resultIndEvent = null;
		RejectIndEvent rejectindEvent = null;
		byte[] paramorreq;
		
		//ResultIndEvent resIndEvent = (ResultIndEvent)message;
		OriginationRequestRes orreqres = null;
		try{
				if(message instanceof ResultIndEvent){
					resultIndEvent = (ResultIndEvent) message;
					paramorreq = resultIndEvent.getParameters().getParameter();
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for OrigReqRetResultHandler--> starting first level decoding on orreq bytes:: "+Util.formatBytes(paramorreq));
					orreqres = (OriginationRequestRes)WinOperationsCoding.decodeOperation(paramorreq, resultIndEvent);
				}
				else {
					rejectindEvent = (RejectIndEvent) message;
					logger.debug("Problem in reject is "+rejectindEvent.getProblem()+"ProblemType in Reject is " + rejectindEvent.getProblemType());
					return true;
				}
				
			
		}catch(ParameterNotSetException pnse){
			logger.debug("parameter not set exception"+pnse);
			return false;
		}catch(Exception e){
			logger.debug("decode failed"+e);
			return false;
		}
		
		if(orreqres==null){
			if(logger.isDebugEnabled())
				logger.debug("orreq is received null in processReceivedMessage() in OrigReqRetResultHandler");
			return false;
		}
		
		OriginationRequestResSequenceType origreqresseqtyp = orreqres.getValue();
		AccessDeniedReason accsDenidReasn = origreqresseqtyp.getAccessDeniedReason();
		AccessDeniedReason.EnumType enumTyp = null;
		if(accsDenidReasn!=null){
			 enumTyp = accsDenidReasn.getValue();
		if(logger.isDebugEnabled())
			logger.debug("Value received of access denied reason type is "+ enumTyp);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OrigReqRetResultHandler leaving with status true");
		simCpb.removeDialogId(Constants.ORREQ);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for OrigReqRetResultHandler");

		if (!((message instanceof ResultIndEvent)||(message instanceof RejectIndEvent))){
			if(logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.ORIG_REQ_RET_RESULT) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a orreq Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		ResultIndEvent receivedInvoke = null;
		if(message instanceof ResultIndEvent)
			receivedInvoke = (ResultIndEvent) message;
		Operation opr;
		byte[] opCode ;
		String opCodeStr= null ;
		boolean isValid= false;
		try {
			int recvdDialogueId = ((ComponentIndEvent)message).getDialogueId();
			
			if(message instanceof ResultIndEvent){
				opr = receivedInvoke.getOperation();
				opCode = opr.getOperationCode();
				opCodeStr = Util.formatBytes(opCode);
				if(opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase())&&((dialogType== tcapNode.getDialogType())) ){
					isValid= true;
				}
				if(logger.isDebugEnabled())
					logger.debug("OrigReqRetResultHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
							"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
							"] Actual DialogType::["+dialogType+"]");
				int recvdInvokeId = receivedInvoke.getInvokeId();
				if(recvdInvokeId!=simCpb.getWinReqInvokeId(Constants.ORREQ)){
					isValid = false;
					logger.debug("invoke id for response is not matching with request,so call failed ");
				}
			}
			else{
				if((dialogType== tcapNode.getDialogType()))
					isValid= true;
			}
			if(recvdDialogueId!=simCpb.getWinReqDialogId(Constants.ORREQ)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(logger.isDebugEnabled())
				logger.debug("OrigReqRetResultHandler validateMessage() isValid::["+isValid+"] **RejectIndEvent** Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		}catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("ParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}
	
	
	
}