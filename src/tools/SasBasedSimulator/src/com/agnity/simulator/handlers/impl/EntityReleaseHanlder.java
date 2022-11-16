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
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EntityReleaseNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.CallSegmentID;
import com.genband.inap.asngenerated.Cause;
import com.genband.inap.asngenerated.EntityReleasedArg;
import com.genband.inap.asngenerated.EntityReleasedArg.CSFailureSequenceType;
import com.genband.inap.asngenerated.Reason;
import com.genband.inap.enumdata.CauseValEnum;
import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.LocationEnum;
import com.genband.inap.enumdata.ReasonEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;

public class EntityReleaseHanlder extends AbstractHandler{

	private static Logger logger = Logger.getLogger(EntityReleaseHanlder.class);
	private static Handler handler;

	private static final String ER_CALL_FIELD_INITIAL_CALL_SEG = "initialCallSegment".toLowerCase();
	private static final String ER_CAUSE = "Cause".toLowerCase();
	private static final String ER_REASON = "Reason".toLowerCase();
	
	//cause Enums
	private static final String ER_LOCATION_ENUM = "locationEnum".toLowerCase();
	private static final String ER_CODING_STND_ENUM = "codingStndEnum".toLowerCase();
	private static final String ER_CAUSE_VALUE_ENUM = "causeValEnum".toLowerCase();
	

		
	private int ER_CLASS=4;
	
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (EntityReleaseHanlder.class) {
				if(handler ==null){
					handler = new EntityReleaseHanlder();
				}
			}
		}
		return handler;
	}

	private EntityReleaseHanlder(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside EntityReleaseHandler processNode()");

		if(!(node.getType().equals(Constants.ER))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		EntityReleaseNode erNode = (EntityReleaseNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;

		if(subElemIterator.hasNext()){
			ire =createEntityRelease(simCpb,subElemIterator);

		}

		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("EntityReleaseHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending entity release component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("EntityReleaseHandler processNode()-->component send");
		
	
		DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),erNode.getDialogAs(),simCpb);
		try {
			if(logger.isDebugEnabled())
				logger.debug("EntityReleaseHandler processNode()-->sending created dialog ["+dialogEvent+"]");
			Helper.sendDialogue(dialogEvent, simCpb);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("Mandatory param excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		} catch (IOException e) {
			logger.error("IOException excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving EntityReleaseHandler processNode() with true");
		return true;
	}

	private InvokeReqEvent createEntityRelease(SimCallProcessingBuffer simCpb,
			Iterator<Node> fieldElemIterator) {
		EntityReleasedArg entityReleaseArg = new EntityReleasedArg();
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(ER_CAUSE)){
					if(logger.isDebugEnabled())
						logger.debug("EntityReleaseHandler processNode()-->field::["+ER_CAUSE+"]");

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String locationEnum = subFieldElems.get(ER_LOCATION_ENUM).getValue(varMap);
					String codingStndEnum = subFieldElems.get(ER_CODING_STND_ENUM).getValue(varMap);
					String causeValEnum = subFieldElems.get(ER_CAUSE_VALUE_ENUM).getValue(varMap);
					
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
					CSFailureSequenceType csFailureSeq= (entityReleaseArg.getCSFailure()!=null) ? 
									entityReleaseArg.getCSFailure() : (new CSFailureSequenceType());
					csFailureSeq.setCause(causeVal);
					entityReleaseArg.selectCSFailure(csFailureSeq);
					
				}else if(fieldName.equals(ER_CALL_FIELD_INITIAL_CALL_SEG)){
					
					if(logger.isDebugEnabled())
						logger.debug("EntityReleaseHandler processNode()-->field::["+ER_CALL_FIELD_INITIAL_CALL_SEG+"]");

					String val=fieldElem.getValue(varMap);
					CallSegmentID callSegId= new CallSegmentID(Integer.parseInt(val));

					CSFailureSequenceType csFailureSeq= (entityReleaseArg.getCSFailure()!=null) ? 
									entityReleaseArg.getCSFailure() : (new CSFailureSequenceType());
					csFailureSeq.setCallSegmentID(callSegId);
					entityReleaseArg.selectCSFailure(csFailureSeq);
					
				}else if(fieldName.equals(ER_REASON)){
					
					if(logger.isDebugEnabled())
						logger.debug("EntityReleaseHandler processNode()-->field::["+ER_REASON+"]");

					String val=fieldElem.getValue(varMap);
					Reason reason = new Reason();
					byte[] reasonByte = null;
					try {
						reasonByte = com.genband.inap.datatypes.Reason.encodeReason(ReasonEnum.valueOf(val));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding Reason",e);
						return null;
					}
					reason.setValue(reasonByte);

					CSFailureSequenceType csFailureSeq= (entityReleaseArg.getCSFailure()!=null) ? 
									entityReleaseArg.getCSFailure() : (new CSFailureSequenceType());
					csFailureSeq.setReason(reason);
					entityReleaseArg.selectCSFailure(csFailureSeq);
					
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("EntityReleaseHandler processNode()-->fields read...");
		
		//getting ER byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(entityReleaseArg);
		opCode.add(InapOpCodes.ER);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error encoding ER to byte array",e);
			return null;
		}
		byte[] er = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for EntityReleaseHandler--> Got ER byte array:: "+Util.formatBytes(er));

		//generate rc component req event
		byte[] erOpCode = new byte[]{InapOpCodes.ER_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, erOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SINGLE, er));
		ire.setClassType(ER_CLASS);

		return ire;

	}
	
	
	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for EntityReleaseHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("EntityReleaseHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

//		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing ER message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsER;
		EntityReleasedArg er = null;

		try{
			parmsER =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("Got ER byte array::"+Util.formatBytes(parmsER));
			er = (EntityReleasedArg)InapOperationsCoding.decodeOperation(parmsER, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(er == null){
			logger.error("er is null");
			return false;
		}
		if(logger.isDebugEnabled()){
			logger.debug("subelements ar::"+subElements);
		}
//		while (subElemIterator.hasNext()) {
//			subElem = subElemIterator.next();
//		//do nothing..
//		}//end while loop on subelem


		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for EntityReleaseHandler with status true");
		return true;

	}
		

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for EntityReleaseHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.ER) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a ER  Node");
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
				logger.debug("EntityReleaseHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
