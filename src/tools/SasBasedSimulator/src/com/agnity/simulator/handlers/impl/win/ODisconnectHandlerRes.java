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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ODisconnectResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.AnnouncementCode;
import com.agnity.win.asngenerated.AnnouncementList;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.ODisconnectRes;
import com.agnity.win.asngenerated.ODisconnectRes.ODisconnectResSequenceType;
import com.agnity.win.datatypes.NonASNAnnouncementCode;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class ODisconnectHandlerRes extends AbstractHandler {

	Logger logger = Logger.getLogger(ODisconnectHandlerRes.class);
	private static Handler handler;

	private static final String ODISCONNECT_ANNOUNCEMENT_LIST = "announcementList".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE1_TONE_ENUM = "announcementCode1_ToneEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE1_CLASS_ENUM = "announcementCode1_ClassEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM = "announcementCode1_StdAnnoucementEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT = "announcementCode1_CstmAnnoucement".toLowerCase();
	
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE2_TONE_ENUM = "announcementCode2_ToneEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE2_CLASS_ENUM = "announcementCode2_ClassEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM = "announcementCode2_StdAnnoucementEnum".toLowerCase();
	private static final String ODISCONNECT_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT = "announcementCode2_CstmAnnoucement".toLowerCase();
	
	private static final String ODISCONNECT_DMH_SERVID = "DMH_ServiceID".toLowerCase();
	private static final String ODISCONNECT_DMH_SERVID_MARKETID = "mktIdList".toLowerCase();
	private static final String ODISCONNECT_DMH_SERVID_SEGID = "mktSegIdList".toLowerCase();
	private static final String ODISCONNECT_DMH_SERVID_SERVID = "svcIdList".toLowerCase();
	
	private static final int ODISCONNECT_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (ODisconnectHandlerRes.class) {
				if (handler == null) {
					handler = new ODisconnectHandlerRes();
				}
			}
		}
		return handler;
	}

	private ODisconnectHandlerRes() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside ODisconnectRes handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.ODISCONNECTRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		ODisconnectResNode oDisconnectResNode = (ODisconnectResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		ODisconnectRes oDisconnectRes = new ODisconnectRes();
		ODisconnectResSequenceType oDisconnectResSeqTyp = new ODisconnectResSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(ODISCONNECT_DMH_SERVID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					DMH_ServiceID servID = new DMH_ServiceID();
					short mktId = Short.parseShort(subFieldElems.get(ODISCONNECT_DMH_SERVID_MARKETID).getValue(varMap));
					byte sgmntId = Byte.parseByte(subFieldElems.get(ODISCONNECT_DMH_SERVID_SEGID).getValue(varMap));
					short srvcId = Short.parseShort(subFieldElems.get(ODISCONNECT_DMH_SERVID_SERVID).getValue(varMap));
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
					oDisconnectResSeqTyp.setDmh_ServiceID(servID);
				}else if(fieldElem.getFieldType().equals(ODISCONNECT_ANNOUNCEMENT_LIST)){

					 //String value = fieldElem.getValue(varMap);
					 Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					 					 
					 AnnouncementList announceMentLst = new AnnouncementList();
					 AnnouncementCode annuncCode1 = new AnnouncementCode();
					 AnnouncementCode annuncCode2 = new AnnouncementCode();
					 ToneEnum tone = ToneEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE1_TONE_ENUM).getValue(varMap));
					 ClassEnum classType = ClassEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE1_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement = StdAnnoucementEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM).getValue(varMap));
					 byte cstmAnnouncmnt1 = Byte.parseByte(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT).getValue(varMap));
					 
					 ToneEnum tone2 = ToneEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE2_TONE_ENUM).getValue(varMap));
					 ClassEnum classType2 = ClassEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE2_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement2 = StdAnnoucementEnum.valueOf(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM).getValue(varMap));
					 byte cstmAnnouncmnt2 = Byte.parseByte(subFieldElems.get(ODISCONNECT_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT).getValue(varMap));
					 
					 byte[] annuncCode1Val = null;
					try {
						annuncCode1Val = NonASNAnnouncementCode.encodeAnnouncementCode(tone, classType, stdAnnoucement,cstmAnnouncmnt1);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding AnnouncementCode1"+e);
					}
					 byte[] annuncCode2Val = null;
					try {
						annuncCode2Val = NonASNAnnouncementCode.encodeAnnouncementCode(tone2, classType2, stdAnnoucement2,cstmAnnouncmnt2);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding AnnouncementCode2"+e);
					}
					 annuncCode1.setValue(annuncCode1Val);
					 annuncCode2.setValue(annuncCode2Val);
					 announceMentLst.setAnnouncementCode1(annuncCode1);
					 announceMentLst.setAnnouncementCode2(annuncCode2);
					 oDisconnectResSeqTyp.setAnnouncementList(announceMentLst);
				 
				}
			}
		}
		
		oDisconnectRes.setValue(oDisconnectResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(oDisconnectRes);
		opCode.add(WinOpCodes.O_DISC);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] odisconnectres = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for ODisconnectResHandler--> Got odisconnect byte array:: "+Util.formatBytes(odisconnectres));
		
//		if(logger.isDebugEnabled())
//			logger.debug("ODisconnectResHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("ODisconnectResHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
//		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("ODisconnectResHandler processNode()-->ODisconnectRes byte array generated creating reqEvent["+odisconnectres+"]");
		
		byte[] operationCode = {WinOpCodes.O_DISC_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.ODISCONNECT), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.ODISCONNECT));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.ODISCONNECT));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, odisconnectres));
		//rre.setClassType(ODISCONNECT_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("ODisconnectResHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending odisconnect component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("ODisconnectResHandler processNode()-->component send");
		if(oDisconnectResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("ODisconnectResHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.ODISCONNECTRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), oDisconnectResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("ODisconnectResHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on ODisconnectRes::"+oDisconnectResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on ODisconnectRes::"+oDisconnectResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving ODisconnectRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ODisconnectResHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("ODisconnectResHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		ResultIndEvent resIndEvent = (ResultIndEvent) message;
		ODisconnectRes odisconnectRes = null;
		try {
			byte[] paramodisconnect = resIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for ODisconnectResHandler--> starting first level decoding on odisconnect bytes:: "
								+ Util.formatBytes(paramodisconnect));
			odisconnectRes = (ODisconnectRes) WinOperationsCoding.decodeOperation(paramodisconnect, resIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (odisconnectRes == null) {
			if (logger.isDebugEnabled())
				logger.debug("ODisconnectRes is received null in processReceivedMessage() in ODisconnectResHandler");
			return false;
		}

		ODisconnectResSequenceType odisconnectResSeqTyp = odisconnectRes.getValue();
		NonASNAnnouncementCode annuncmntCode1 = null;
		NonASNAnnouncementCode annuncmntCode2 = null;
		NonASNDmhServiceId dmhServCID = null;
		AnnouncementCode code1 = null;
		AnnouncementCode code2 = null;
		try{
			if(odisconnectResSeqTyp.isAnnouncementListPresent()){
				code1 = odisconnectResSeqTyp.getAnnouncementList().getAnnouncementCode1();
				code2 = odisconnectResSeqTyp.getAnnouncementList().getAnnouncementCode2();
				if(code1!=null){
					annuncmntCode1 = NonASNAnnouncementCode.decodeAnnouncementCode(code1.getValue());
					logger.debug("value of annuncmntCode1 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode1.getTone()+" "+annuncmntCode1.getClassType()+" "+annuncmntCode1.getStdAnnoucement());
				}
				if(code2!=null){
					annuncmntCode2 = NonASNAnnouncementCode.decodeAnnouncementCode(code2.getValue());
					logger.debug("value of annuncmntCode1 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode2.getTone()+" "+annuncmntCode2.getClassType()+" "+annuncmntCode2.getStdAnnoucement());
				}			
			}
			if(odisconnectResSeqTyp.isDmh_ServiceIDPresent()){
				dmhServCID = NonASNDmhServiceId.decodeDmhServiceId(odisconnectResSeqTyp.getDmh_ServiceID().getValue());
				logger.debug("value of DMH_ServiceID is :(mkt,segment,service) "+dmhServCID.getMarketId()+" "+dmhServCID.getMarketSegId()+" "+dmhServCID.getDmhServiceId());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ODisconnectResHandler leaving with status true");
		simCpb.removeDialogId(Constants.ODISCONNECT);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for ODisconnectResHandler");

		if (!(message instanceof ResultIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and ResultIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.ODISCONNECTRES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a ODisconnectRes Node");
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
			if(receivedResult.getDialogueId()!=simCpb.getWinReqDialogId(Constants.ODISCONNECT)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedResult.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.ODISCONNECT)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if (logger.isDebugEnabled())
				logger.debug("ODisconnectResHandler validateMessage() isValid::["
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