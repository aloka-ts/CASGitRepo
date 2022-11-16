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
import com.agnity.simulator.callflowadaptor.element.child.ValidateElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.Cause;
import com.genband.inap.asngenerated.ReleaseCallArg;
import com.genband.inap.enumdata.CauseValEnum;
import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.LocationEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;

public class ReleaseCallHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(ReleaseCallHandler.class);
	private static Handler handler;

	private static final String RELEASE_CALL_FIELD_INITIAL_CALL_SEG = "initialCallSegment".toLowerCase();
	
	//cause Enums
	private static final String RC_LOCATION_ENUM = "locationEnum".toLowerCase();
	private static final String RC_CODING_STND_ENUM = "codingStndEnum".toLowerCase();
	private static final String RC_CAUSE_VALUE_ENUM = "causeValEnum".toLowerCase();
	
	//validate
	private static final String RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL = "initialCallSegment.causeVal".toLowerCase();
		
	private int RC_CLASS=4;
	
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (ReleaseCallHandler.class) {
				if(handler ==null){
					handler = new ReleaseCallHandler();
				}
			}
		}
		return handler;
	}

	private ReleaseCallHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside ReleaseCallHandler processNode()");

		if(!(node.getType().equals(Constants.RELEASE_CALL))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
//		ReleaseCallNode releaseCallNode = (ReleaseCallNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;

		if(subElemIterator.hasNext()){
			ire =createReleaseCall(simCpb,subElemIterator);

		}

		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("ReleaseCallHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending release call component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("ReleaseCallHandler processNode()-->component send");
		
		
		
		
		//for release call dialog is always TC_END
		DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),Constants.DIALOG_END,simCpb);
		try {
			if(logger.isDebugEnabled())
				logger.debug("ReleaseCallHandler processNode()-->sending created dialog ["+dialogEvent+"]");
			Helper.sendDialogue(dialogEvent, simCpb);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("Mandatory param excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		} catch (IOException e) {
			logger.error("IOException excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving ReleaseCallHandler processNode() with true");
		return true;
	}

	private InvokeReqEvent createReleaseCall(SimCallProcessingBuffer simCpb,
			Iterator<Node> fieldElemIterator) {
		ReleaseCallArg releaseCallArg = new ReleaseCallArg();
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(RELEASE_CALL_FIELD_INITIAL_CALL_SEG)){
					if(logger.isDebugEnabled())
						logger.debug("ReleaseCallHandler processNode()-->field::["+RELEASE_CALL_FIELD_INITIAL_CALL_SEG+"]");

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String locationEnum = subFieldElems.get(RC_LOCATION_ENUM).getValue(varMap);
					String codingStndEnum = subFieldElems.get(RC_CODING_STND_ENUM).getValue(varMap);
					String causeValEnum = subFieldElems.get(RC_CAUSE_VALUE_ENUM).getValue(varMap);
					
					byte[] causeBytes;
					try {
						causeBytes = com.genband.inap.datatypes.Cause.encodeCauseVal(LocationEnum.valueOf(locationEnum), 
								CodingStndEnum.valueOf(codingStndEnum), CauseValEnum.valueOf(causeValEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding Cause",e);
						return null;
					}
					
					Cause causeVal=new Cause();
					causeVal.setValue(causeBytes);
					releaseCallArg.selectInitialCallSegment(causeVal);
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("ReleaseCallHandler processNode()-->fields read...");
		
		//getting RC byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(releaseCallArg);
		opCode.add(InapOpCodes.RELEASE_CALL);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error encoding RC to byte array",e);
			return null;
		}
		byte[] rc = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for ReleaseCallHandler--> Got RC byte array:: "+Util.formatBytes(rc));

		//generate rc component req event
		byte[] rcOpCode = {0x16} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, rcOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SINGLE, rc));
		ire.setClassType(RC_CLASS);

		return ire;

	}
	
	
	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ReleaseCallHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("ReleaseCallHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		SetElem setElem = null;
		ValidateElem validateElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing RC message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsRc;
		ReleaseCallArg rc = null;

		try{
			parmsRc =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("Got Release Call byte array::"+Util.formatBytes(parmsRc));
			rc = (ReleaseCallArg)InapOperationsCoding.decodeOperation(parmsRc, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(rc == null){
			logger.error("rc is null");
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

			}else if(subElem.getType().equals(Constants.VALIDATE)){
				validateElem =(ValidateElem) subElem;

				String fieldName = validateElem.getFieldName();
				String expectedVal= validateElem.getFieldVal(varMap);

				if(fieldName.equals(RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL)){
					boolean status=validateRcCause(rc,expectedVal);
					if(!status){
						logger.debug("validate of CauseValue failed; return false");
						return status;
					}
				}//@End:check field name
			}//end if check for subelemnt type
		}//end while loop on subelem


		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ReleaseCallHandler with status true");
		return true;

	}
	

	private boolean validateRcCause(ReleaseCallArg rc, String expectedVal) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate RC initialSegmentCauseVAl");
		Cause initialCallSegment=null;
		if(rc.isInitialCallSegmentSelected())
			initialCallSegment=rc.getInitialCallSegment();
		if(initialCallSegment==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL +"] initialCallSegment Cause is null");
			return false;
		}
		
		byte[] causeBytes=initialCallSegment.getValue();
		logger.debug("Decode cause:;"+Util.formatBytes(causeBytes));
		
		CauseValEnum causeVal =null;
		try {
			causeVal = com.genband.inap.datatypes.Cause.decodeCauseVal(causeBytes).getCauseValEnum();
		} catch (InvalidInputException e) {
			logger.error("Exception decoding Cause for initialCallSegment ReleaseCallHandler",e);
		}
		
		
		if(causeVal==null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL +"] causeVal not found");
			return false;
		}

		if( (CauseValEnum.valueOf(expectedVal)).equals(causeVal)){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL +"] CauseValEnum Value matched Expected::["+expectedVal+
						"] Actual Value::["+causeVal.toString());
			return true;
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ RC_VALIDATE_INIT_CALL_SEG_CAUSE_VAL +"] CauseValEnum Value not matched Expected::["+expectedVal+
					"] Actual Value::["+causeVal.toString());
			return false;
		}

	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for ReleaseCallHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.RELEASE_CALL) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a RC  Node");
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
			}	
			if(logger.isDebugEnabled())
				logger.debug("ReleaseCallHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		} 

		return isValid;
	}



}
