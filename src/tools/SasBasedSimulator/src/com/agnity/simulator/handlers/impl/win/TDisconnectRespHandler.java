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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TDisconnectRespNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.AccessDeniedReason;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.OriginationRequestRes;
import com.agnity.win.asngenerated.TDisconnectRes;
import com.agnity.win.asngenerated.OriginationRequestRes.OriginationRequestResSequenceType;
import com.agnity.win.asngenerated.TDisconnectRes.TDisconnectResSequenceType;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;




public class TDisconnectRespHandler extends AbstractHandler{
	
	Logger logger = Logger.getLogger(TDisconnectRespHandler.class);
	private static Handler handler;
	
	private static final String TDISCONNECTRESP_DMH_SERVICEID = "Dmh_ServiceID".toLowerCase();
	private static final String TDISCONNECTRESP_RES_DMH_SERVID_MARKETID = "mktIdList".toLowerCase();
	private static final String TDISCONNECTRESP_RES_DMH_SERVID_SEGID = "mktSegIdList".toLowerCase();
	private static final String TDISCONNECTRESP_RES_DMH_SERVID_SERVID = "svcIdList".toLowerCase();
		
		
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (TDisconnectRespHandler.class) {
				if(handler ==null){
					handler = new TDisconnectRespHandler();
				}
			}
		}
		return handler;
	}
	
	private TDisconnectRespHandler(){
		
	}
	
	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside TdiscnnctRes handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.TDISCONNECTRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		TDisconnectRespNode tdisconnctResNode = (TDisconnectRespNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		TDisconnectRes tdisccnctres = new TDisconnectRes();	
		TDisconnectResSequenceType tdisccnctresSeqTyp = new TDisconnectResSequenceType();
		
		
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				if(fieldElem.getFieldType().equals(TDISCONNECTRESP_DMH_SERVICEID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					DMH_ServiceID servID = new DMH_ServiceID();
					short mktId = Short.parseShort(subFieldElems.get(TDISCONNECTRESP_RES_DMH_SERVID_MARKETID).getValue(varMap));
					byte sgmntId = Byte.parseByte(subFieldElems.get(TDISCONNECTRESP_RES_DMH_SERVID_SEGID).getValue(varMap));
					short srvcId = Short.parseShort(subFieldElems.get(TDISCONNECTRESP_RES_DMH_SERVID_SERVID).getValue(varMap));
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
					tdisccnctresSeqTyp.setDmh_ServiceID(servID);
				}				
			}
		}
		
		tdisccnctres.setValue(tdisccnctresSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(tdisccnctres);
		opCode.add(WinOpCodes.T_DISC);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] tdiscRes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for TdiscnnctResHandler--> Got TdisconnectRes byte array:: "+Util.formatBytes(tdiscRes));
						
		if(logger.isDebugEnabled())
			logger.debug("TdiscnnctResHandler processNode()-->TdisconnectRes byte array generated creating reqEvent["+tdiscRes+"]");
		
		byte[] operationCode = {WinOpCodes.T_DISC_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.TDISCONNECT), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.TDISCONNECT));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.TDISCONNECT));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, tdiscRes));
		//rre.setClassType(TdisconnectResRES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectRes processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending TdisconnectRes component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectRes processNode()-->component send");
		if(tdisconnctResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("TdisconnectRes processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.TDISCONNECTRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), tdisconnctResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("TdisconnectRes processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on TdisconnectRes::"+tdisconnctResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on TdisconnectRes::"+tdisconnctResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving TdisconnectRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TdisconnectRes");
		
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
		TDisconnectRes tDiscnnctRes = null;
		try{
			byte[] paramtdisc = resIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for TdisconnectRes--> starting first level decoding on TdisconnectRes bytes:: "+Util.formatBytes(paramtdisc));
			tDiscnnctRes = (TDisconnectRes)WinOperationsCoding.decodeOperation(paramtdisc, resIndEvent);
		}catch(ParameterNotSetException pnse){
			logger.debug("parameter not set exception"+pnse);
			return false;
		}catch(Exception e){
			logger.debug("decode failed"+e);
			return false;
		}
		
		if(tDiscnnctRes==null){
			if(logger.isDebugEnabled())
				logger.debug("TdisconnectRes is received null in processReceivedMessage() in TdisconnectRes");
			return false;
		}
		
		TDisconnectResSequenceType tDiscnnctResseqtyp = tDiscnnctRes.getValue();
		DMH_ServiceID dmh_servcId = tDiscnnctResseqtyp.getDmh_ServiceID();
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
			logger.debug("processRecievedMessage() for TdisconnectRes leaving with status true");
		simCpb.removeDialogId(Constants.TDISCONNECTRES);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for TdisconnectRes");

		if(!(message instanceof ResultIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.TDISCONNECTRES) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a TdisconnectRes Node");
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
			if(receivedInvoke.getDialogueId()!=simCpb.getWinReqDialogId(Constants.TDISCONNECT)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedInvoke.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.TDISCONNECT)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if(logger.isDebugEnabled())
				logger.debug("TdisconnectRes validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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