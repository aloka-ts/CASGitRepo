package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.DfcNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.DisconnectForwardConnectionWithArgumentArg;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class DfcHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(DfcHandler.class);
	private static Handler handler;
	private static final String NO_ARG_DFC_OPCODE="0x12".toLowerCase();
	private static final int DFC_CLASS=2;
	
	//fields
	private static final String DFC_FIELD_PARTY_TO_DISCONNECT = "partyToDisconnect".toLowerCase();
	
	//subfields
	private static final String DFC_SUBFIELD_LEG_ID = "legId".toLowerCase();

	//	private static final int DFC_CLASS = 2;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (DfcHandler.class) {
				if(handler ==null){
					handler = new DfcHandler();
				}
			}
		}
		return handler;
	}

	private DfcHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside DfcHandler processNode()");

		if(!(node.getType().equals(Constants.DFC))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		DfcNode dfcNode = (DfcNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;
		
		if(subElemIterator.hasNext()){
			if(logger.isDebugEnabled())
				logger.debug("args DFC");
			ire =createDfcWithArgs(simCpb,subElemIterator);
			
		}else{
			if(logger.isDebugEnabled())
				logger.debug("No args DFC");
			ire = createNoArgDfc(simCpb);
		}
		
		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("DfcHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending dfc component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("DfcHandler processNode()-->component send");
		//if last message generate dialog
		if(dfcNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("DfcHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),dfcNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("DfcHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on DFC::"+dfcNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on DFC::"+dfcNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving DfcHandler processNode() with status true");
		return true;

	}

	private InvokeReqEvent createDfcWithArgs(SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator) {
		DisconnectForwardConnectionWithArgumentArg dfcArg = new DisconnectForwardConnectionWithArgumentArg();

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(DFC_FIELD_PARTY_TO_DISCONNECT)){
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String legIdstr = subFieldElems.get(DFC_SUBFIELD_LEG_ID).getValue(varMap);
					int legId= 1;//default is 1
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}
					if(logger.isDebugEnabled()){
						logger.debug("Got leg ID"+legId);
					}
					LegID legIdField = new LegID();
					
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectSendingSideID(new LegType(legType));
					
					DisconnectForwardConnectionWithArgumentArg.PartyToDisconnectChoiceType ptdc = 
						new DisconnectForwardConnectionWithArgumentArg.PartyToDisconnectChoiceType();
					ptdc.selectLegID(legIdField);
					

					dfcArg.setPartyToDisconnect(ptdc);


				}//complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("DfcHandler processNode()-->fields read...saving extension if present");
		//no extension for DFc
		
		//getting DFC byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(dfcArg);
		opCode.add(InapOpCodes.DFC);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error endfccoding DFC to byte array",e);
			return null;
		}
		byte[] dfc = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for DFCHandler--> Got DFC byte array:: "+Util.formatBytes(dfc));

		//generate dfc component req event
		byte[] dfcOpCode = {0x1E} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, dfcOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, dfc));
		ire.setClassType(DFC_CLASS);
		
		return ire;
	}

	private InvokeReqEvent createNoArgDfc(SimCallProcessingBuffer buffer) {
		
		byte[] dfcOpCode = {0x12} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, dfcOpCode);
				
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), buffer.getDialogId(), requestOp);
		ire.setInvokeId(buffer.incrementAndGetInvokeId());
		//ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, null));
		ire.setClassType(DFC_CLASS);
		
		return ire;
		
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for DfcHandler");

		//		DfcNode dfcNode = (DfcNode) node;

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("DfcHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing DFC message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsDfc;
		DisconnectForwardConnectionWithArgumentArg dfc = null;

		try{
			parmsDfc =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for DfcHandler--> starting first level decoding on DFC bytes:: "+Util.formatBytes(parmsDfc));
			dfc = (DisconnectForwardConnectionWithArgumentArg)InapOperationsCoding.decodeOperation(parmsDfc, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(dfc == null){
			logger.error("dfc is null");
			return false;
		}

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					var = new Variable();
					var.setVarName(varName);
				}
				String varVal = null;



				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);

			}//end if check for set elem
		}//end while loop on subelem
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for DfcHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for DfcHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.DFC) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a DFC Node");
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

		InvokeIndEvent receivedInvoke = (InvokeIndEvent)message; 
		Operation opr;
		byte[] opCode ;
		String opCodeStr= null ;
		boolean isValid= false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if( (opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase())) && (dialogType== tcapNode.getDialogType()) ){
				isValid= true;
			}else if( (opCodeStr.toLowerCase().equals(NO_ARG_DFC_OPCODE.toLowerCase())) && (dialogType== tcapNode.getDialogType()) ){
				isValid= true;
			}	
			if(logger.isDebugEnabled())
				logger.debug("DfcHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] or  [" + NO_ARG_DFC_OPCODE.toLowerCase()+ "] Actual Opcode::["+opCodeStr+
						"] Expected DialogType::["+ tcapNode.getDialogType()+ "] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		} 

		return isValid;
	}



}
