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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TBusyResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.TBusyRes;
import com.agnity.win.asngenerated.TBusyRes.TBusyResSequenceType;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;




public class TBusyResHandler extends AbstractHandler{
	
	Logger logger = Logger.getLogger(TBusyResHandler.class);
	private static Handler handler;
	
	private static final String TBUSYRESP_DMH_SERVICEID = "Dmh_ServiceID".toLowerCase();
	private static final String TBUSYRESP_DMH_SERVID_MARKETID = "mktIdList".toLowerCase();
	private static final String TBUSYRESP_DMH_SERVID_SEGID = "mktSegIdList".toLowerCase();
	private static final String TBUSYRESP_DMH_SERVID_SERVID = "svcIdList".toLowerCase();
		
		
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (TBusyResHandler.class) {
				if(handler ==null){
					handler = new TBusyResHandler();
				}
			}
		}
		return handler;
	}
	
	private TBusyResHandler(){
		
	}
	
	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside TBusyRes handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.TBUSYRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		TBusyResNode tbusyResNode = (TBusyResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		TBusyRes tbusyres = new TBusyRes();	
		TBusyResSequenceType tbusyresSeqTyp = new TBusyResSequenceType();
		
		
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				if(fieldElem.getFieldType().equals(TBUSYRESP_DMH_SERVICEID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					DMH_ServiceID servID = new DMH_ServiceID();
					short mktId = Short.parseShort(subFieldElems.get(TBUSYRESP_DMH_SERVID_MARKETID).getValue(varMap));
					byte sgmntId = Byte.parseByte(subFieldElems.get(TBUSYRESP_DMH_SERVID_SEGID).getValue(varMap));
					short srvcId = Short.parseShort(subFieldElems.get(TBUSYRESP_DMH_SERVID_SERVID).getValue(varMap));
					LinkedList<Short> l0 = new LinkedList<Short>();
					LinkedList<Byte> l1 = new LinkedList<Byte>();
					LinkedList<Short> l2 = new LinkedList<Short>();
					l0.add(mktId);
					l1.add(sgmntId);
					l2.add(srvcId);
					byte[] DMHServId =null;
					try {
						DMHServId = NonASNDmhServiceId.encodeDmhServiceId(l0, l1, l2);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding DMH_SERVICEID"+e);
					}
					servID.setValue(DMHServId);
					tbusyresSeqTyp.setDmh_ServiceID(servID);
				}				
			}
		}
		
		tbusyres.setValue(tbusyresSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(tbusyres);
		opCode.add(WinOpCodes.T_BUSY);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] tbusyRes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for TBusyRes--> Got TBusyRes byte array:: "+Util.formatBytes(tbusyRes));
						
		if(logger.isDebugEnabled())
			logger.debug("TBusyResHandler processNode()-->TBusyRes byte array generated creating reqEvent["+tbusyRes+"]");
		
		byte[] operationCode = {WinOpCodes.T_BUSY_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.TBUSY), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.TBUSY));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.TBUSY));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, tbusyRes));
		//rre.setClassType(TdisconnectResRES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("TBusyRes processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending TBusyRes component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("TBusyRes processNode()-->component send");
		if(tbusyResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("TBusyRes processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.TBUSYRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), tbusyResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("TBusyRes processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on TBusyRes::"+tbusyResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on TBusyRes::"+tbusyResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving TBusyRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TBusyRes");
		
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
		ResultIndEvent resIndEvent = (ResultIndEvent)message;
		TBusyRes tBusyRes = null;
		try{
			byte[] paramtbusy = resIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for TBusyRes--> starting first level decoding on TBusyRes bytes:: "+Util.formatBytes(paramtbusy));
			tBusyRes = (TBusyRes)WinOperationsCoding.decodeOperation(paramtbusy, resIndEvent);
		}catch(ParameterNotSetException pnse){
			logger.debug("parameter not set exception"+pnse);
			return false;
		}catch(Exception e){
			logger.debug("decode failed"+e);
			return false;
		}
		
		if(tBusyRes==null){
			if(logger.isDebugEnabled())
				logger.debug("TBusyRes is received null in processReceivedMessage() in TBusyRes");
			return false;
		}
		
		TBusyResSequenceType tBusyResseqtyp = tBusyRes.getValue();
		DMH_ServiceID dmh_servcId = tBusyResseqtyp.getDmh_ServiceID();
		NonASNDmhServiceId dmhServcId = null;
		if(dmh_servcId!=null){
			try {
				dmhServcId = NonASNDmhServiceId.decodeDmhServiceId(dmh_servcId.getValue());
			} catch (InvalidInputException exceptn) {
				// TODO Auto-generated catch block
				logger.debug("Exception in decoding"+exceptn.toString());
			}
		if(logger.isDebugEnabled())
			logger.debug("Value received of dmh service id are "+ dmhServcId.getDmhServiceId()+" ,"+dmhServcId.getMarketId()+" ,"+dmhServcId.getMarketSegId());
		}
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TBusyRes leaving with status true");
		simCpb.removeDialogId(Constants.TBUSY);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for TBusyRes");

		if(!(message instanceof ResultIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.TBUSYRES) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a TBusyRes Node");
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

		ResultIndEvent receivedInvoke = (ResultIndEvent)message; 
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
			if(receivedInvoke.getDialogueId()!=simCpb.getWinReqDialogId(Constants.TBUSY)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedInvoke.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.TBUSY)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if(logger.isDebugEnabled())
				logger.debug("TBusyRes validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
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