package com.agnity.simulator.handlers.impl.win;

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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TBusyNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TDisconnectNode;

import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;

import com.agnity.win.asngenerated.AnalyzedInformation;

import com.agnity.win.asngenerated.MSCID;

import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.TAnswer;
import com.agnity.win.asngenerated.TBusy;
import com.agnity.win.asngenerated.TDisconnect;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;

import com.agnity.win.asngenerated.AnalyzedInformation.AnalyzedInformationSequenceType;
import com.agnity.win.asngenerated.TAnswer.TAnswerSequenceType;
import com.agnity.win.asngenerated.TBusy.TBusySequenceType;
import com.agnity.win.asngenerated.TDisconnect.TDisconnectSequenceType;

import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNTransactionCapability;

import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNTriggerCapability;

import com.agnity.win.datatypes.NonASNWINOperationCapability;

import com.agnity.win.enumdata.AnnouncementsEnum;
import com.agnity.win.enumdata.BusyDetectionEnum;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.MultipleTerminationsEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.enumdata.ProfileEnum;
import com.agnity.win.enumdata.RemoteUserInteractionEnum;
import com.agnity.win.enumdata.SubscriberPINInterceptEnum;
import com.agnity.win.enumdata.TerminationListEnum;

import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TBusyHandler extends AbstractHandler{

	Logger logger = Logger.getLogger(TBusyHandler.class);
	private static Handler handler;

		
	private static final String TBUSY_BILLING_ID = "BillingId".toLowerCase();
	private static final String TBUSY_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String TBUSY_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String TBUSY_ID_NUMBER = "idNumber".toLowerCase();
	private static final String TBUSY_SEGMENT_COUNTER = "segmentCounter".toLowerCase();

	private static final String TBUSY_MSCID = "Mscid".toLowerCase();
	private static final String TBUSY_MARKETID = "marketID".toLowerCase();
	private static final String TBUSY_SWITCHNUMBER = "switchNo".toLowerCase();
	
	private static final String TBUSY_TRANSCTN_CAPBLTY = "TransactionCapability".toLowerCase();

	private static final String TBUSY_PROFILE_ENUM = "ProfileEnum".toLowerCase();
	private static final String TBUSY_BUSY_DETECTION_ENUM = "BusyDetectionEnum".toLowerCase();
	private static final String TBUSY_ANNOUNCEMENT_ENUM = "AnnouncementsEnum".toLowerCase();
	private static final String TBUSY_REMOTE_USR_INTERACTN_ENUM = "RemoteUserInteractionEnum".toLowerCase();
	private static final String TBUSY_SUBSCRBR_PIN_INTRCPT_ENUM = "SubscriberPINInterceptEnum".toLowerCase();
	private static final String TBUSY_MULTPL_TERM_ENUM = "MultipleTerminationsEnum".toLowerCase();
	private static final String TBUSY_TERMLIST_ENUM ="TerminationListEnum".toLowerCase();
		
	private static final String TBUSY_WIN_CAPABILITY = "WinCapability".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_INIT = "TriggerCapability_init".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_KDIGIT = "TriggerCapability_kDigit".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_ALLCALLS = "TriggerCapability_allCalls".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_REVERTIVECALL = "TriggerCapability_revertiveCall".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_CALL_TYPE = "TriggerCapability_call_type".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_UNRECNO = "TriggerCapability_unrecNo".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_PRIORAGMT = "TriggerCapability_priorAgmt".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_ADVTERM = "TriggerCapability_advTerm".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_TERMRESAVAIL = "TriggerCapability_termResAvail".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_TBUSY = "TriggerCapability_tBusy".toLowerCase();
	private static final String TBUSY_TRIGGER_CAPABILITY_TNOANS = "TriggerCapability_tNoAns".toLowerCase();
	
	private static final String TBUSY_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM = "WINOperationsCapability_CircuitSwitchedDataEnum".toLowerCase();
	private static final String TBUSY_WIN_OPERATIONS_CAPABILITY_CCDIRENUM = "WINOperationsCapability_CCDIREnum".toLowerCase();
	private static final String TBUSY_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM = "WINOperationsCapability_PositionRequestEnum".toLowerCase();
	private static final String TBUSY_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM = "WINOperationsCapability_ConnectResourceEnum".toLowerCase();

	private static final String TBUSY_TRIGGER_TYPE = "TriggerType".toLowerCase();
	
	private static final String TBUSY_WIN_TIME_OF_DAY = "TimeOfDay".toLowerCase();
	
	private static final int TBUSY_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (TBusyHandler.class) {
				if (handler == null) {
					handler = new TBusyHandler();
				}
			}
		}
		return handler;
	}

	private TBusyHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside TBUSY handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.TBUSY))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		
		TBusyNode tbusyNode = (TBusyNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		TBusy tbusy = new TBusy();
	
		TBusySequenceType tbusySeqTyp = new TBusySequenceType(); 
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(TBUSY_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(TBUSY_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(TBUSY_ORIGINATING_SWITCH_NUM).getValue(varMap);
					String idNumber = subFieldElems.get(TBUSY_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(TBUSY_SEGMENT_COUNTER).getValue(varMap);
					BillingID billingid = new BillingID();
					
					short originatingMarketID = Short.parseShort(origMrktId);
					short originatingSwitchNo = Short.parseShort(origSwitchNo);
					int idNo = Integer.parseInt(idNumber);
					short segCounter = Short.parseShort(segmentCounter);
					byte[] billingIdVal = null;
					
					try {
						billingIdVal = NonASNBillingID.encodeBillingID(originatingMarketID, originatingSwitchNo, idNo, segCounter);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding billingId :"+e.toString());
					}
					
					
					billingid.setValue(billingIdVal);
						
					tbusySeqTyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(TBUSY_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(TBUSY_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(TBUSY_SWITCHNUMBER).getValue(varMap);
						
					MSCID mscid = new MSCID();
					byte[] mscidVal =null;
					try {
						mscidVal = NonASNMSCID.encodeMSCID(Short.parseShort(marketID), Integer.parseInt(switchNo));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding MSCID "+e.toString());
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding MSCID "+e.toString());
					}
					
					mscid.setValue(mscidVal);
					tbusySeqTyp.setMscid(mscid);
				}else if (fieldElem.getFieldType().equals(TBUSY_TRANSCTN_CAPBLTY)){
					//setting dummy val as NONASNdatatype are not implemented yet
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
										
					ProfileEnum proflEnum = ProfileEnum.valueOf(subFieldElems.get(TBUSY_PROFILE_ENUM).getValue(varMap));
					BusyDetectionEnum busyDetectnEnum = BusyDetectionEnum.valueOf(subFieldElems.get(TBUSY_BUSY_DETECTION_ENUM).getValue(varMap));
					AnnouncementsEnum annuncmntEnum = AnnouncementsEnum.valueOf(subFieldElems.get(TBUSY_ANNOUNCEMENT_ENUM).getValue(varMap));
					RemoteUserInteractionEnum remotUsrIntEnum = RemoteUserInteractionEnum.valueOf(subFieldElems.get(TBUSY_REMOTE_USR_INTERACTN_ENUM).getValue(varMap));
					SubscriberPINInterceptEnum subsPinIntrcptEnum = SubscriberPINInterceptEnum.valueOf(subFieldElems.get(TBUSY_SUBSCRBR_PIN_INTRCPT_ENUM).getValue(varMap));
					MultipleTerminationsEnum mltplTermEnum = MultipleTerminationsEnum.valueOf(subFieldElems.get(TBUSY_MULTPL_TERM_ENUM).getValue(varMap));
					TerminationListEnum termListEnum = TerminationListEnum.valueOf(subFieldElems.get(TBUSY_TERMLIST_ENUM).getValue(varMap));
										
					byte[] byteValTransctnCap = null;
					try {
						byteValTransctnCap = NonASNTransactionCapability.encodeTransactionCapability(proflEnum, busyDetectnEnum, annuncmntEnum, remotUsrIntEnum, subsPinIntrcptEnum, mltplTermEnum, termListEnum);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding TransactionCapability "+e.toString());
					}
					
					TransactionCapability transCap = new TransactionCapability();
					transCap.setValue(byteValTransctnCap);
					tbusySeqTyp.setTransactionCapability(transCap);
				}else if(fieldElem.getFieldType().equals(TBUSY_TRIGGER_TYPE)){
					String value = fieldElem.getValue(varMap);
					TriggerType.EnumType triggerTypeEnum = TriggerType.EnumType.valueOf(value);
					TriggerType trigTyp = new TriggerType();
					trigTyp.setValue(triggerTypeEnum);
					tbusySeqTyp.setTriggerType(trigTyp);
				}else if(fieldElem.getFieldType().equals(TBUSY_WIN_CAPABILITY)){
					
					WINCapability wincap = new WINCapability();
					TriggerCapability trigCap = new TriggerCapability();
					WINOperationsCapability winOpCap = new WINOperationsCapability();
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					byte init = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_INIT).getValue(varMap));
					byte kDigit = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_KDIGIT).getValue(varMap));
					byte allCalls = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_ALLCALLS).getValue(varMap));
					byte revertiveCall = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_REVERTIVECALL).getValue(varMap));
					byte call_type = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_CALL_TYPE).getValue(varMap));
					byte unrecNo = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_UNRECNO).getValue(varMap));
					byte priorAgmt = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_PRIORAGMT).getValue(varMap));
					byte advTerm = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_ADVTERM).getValue(varMap));
					byte termResAvail = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_TERMRESAVAIL).getValue(varMap));
					byte tBusy = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_TBUSY).getValue(varMap));
					byte tNoAns = Byte.parseByte(subFieldElems.get(TBUSY_TRIGGER_CAPABILITY_TNOANS).getValue(varMap));
					
					byte[] trigCapVal =null;
					try {
						trigCapVal = NonASNTriggerCapability.encodeTriggerCapability(init, kDigit, allCalls, revertiveCall, call_type, unrecNo, priorAgmt, advTerm, termResAvail, tBusy, tNoAns);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding TriggerCapability "+e.toString());
					}
					trigCap.setValue(trigCapVal);
					
					String val1 = subFieldElems.get(TBUSY_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM).getValue(varMap);
					CircuitSwitchedDataEnum cktSwtchDataEnum = CircuitSwitchedDataEnum.valueOf(val1);
					LinkedList<CircuitSwitchedDataEnum> l0 = new LinkedList<CircuitSwitchedDataEnum>();
					l0.add(cktSwtchDataEnum);
					
					String val2 = subFieldElems.get(TBUSY_WIN_OPERATIONS_CAPABILITY_CCDIRENUM).getValue(varMap);
					CCDIREnum ccdirEnum = CCDIREnum.valueOf(val2);
					LinkedList<CCDIREnum> l1 = new LinkedList<CCDIREnum>();
					l1.add(ccdirEnum);
					
					String val3 = subFieldElems.get(TBUSY_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM).getValue(varMap);
					PositionRequestEnum posReqEnum = PositionRequestEnum.valueOf(val3);
					LinkedList<PositionRequestEnum> l2 = new LinkedList<PositionRequestEnum>();
					l2.add(posReqEnum);
					
					String val4 = subFieldElems.get(TBUSY_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM).getValue(varMap);
					ConnectResourceEnum conResEnum = ConnectResourceEnum.valueOf(val4);
					LinkedList<ConnectResourceEnum> l3 = new LinkedList<ConnectResourceEnum>();
					l3.add(conResEnum);
					
					byte[] winOpCapVal =null;
					try {
						winOpCapVal = NonASNWINOperationCapability.encodeWINOperationCapability(l0, l1, l2, l3);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding WINOperationCapability "+e.toString());
					}
					winOpCap.setValue(winOpCapVal);
					
					wincap.setTriggerCapability(trigCap);
					wincap.setWINOperationsCapability(winOpCap);
					tbusySeqTyp.setWinCapability(wincap);
				}
			}
		}
		
		//some problem in decoding of Billing Id happens, alternate workaround told by win codec team is to set msid (dummy value)
		MSID msid = new MSID();
		String mobileIdnNum = "101";
		MobileIdentificationNumber mobIdnNum = new MobileIdentificationNumber();
		byte[] mintyp =null;
		try {
			mintyp = NonASNMINType.encodeMINType(mobileIdnNum);
		} catch (InvalidInputException e1) {
			// TODO Auto-generated catch block
			logger.debug("Error while encoding MSID "+e1.toString());
		}
		MINType mintype = new MINType();
		mintype.setValue(mintyp);
		mobIdnNum.setValue(mintype);
		msid.selectMobileIdentificationNumber(mobIdnNum);
		tbusySeqTyp.setMsid(msid);
		
		tbusy.setValue(tbusySeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(tbusy);
		opCode.add(WinOpCodes.T_BUSY);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message "+e.toString());
			return false;
		}
		
		byte[] tbusyparam = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for TbusyHandler--> Got Tbusy byte array:: "+Util.formatBytes(tbusyparam));
		
		if(logger.isDebugEnabled())
			logger.debug("TbusyHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("TbusyHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.TBUSY,dialogueId);
		simCpb.setWinReqInvokeId(Constants.TBUSY, invokeId);
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("TbusyHandler processNode()-->Tbusy byte array generated creating reqEvent["+tbusyparam+"]");
		
		byte[] operationCode = {WinOpCodes.T_BUSY_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, tbusyparam));
		ire.setClassType(TBUSY_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("TbusyHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending Tbusy component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("TbusyHandler processNode()-->component send");
		if(tbusyNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("TbusyHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.TBUSY);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), tbusyNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("TbusyHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on Tbusy::"+tbusyNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on Tbusy::"+tbusyNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving Tbusy processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TbusyHandler"); 
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("TbusyHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		//Iterator<Node> subElemIterator = subElem.iterator();

		int dialogId,invokeId;
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		TBusy tbusy = null;
		try {
			byte[] paramtbusy = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for TbusyHandler--> starting first level decoding on Tbusy bytes:: "
								+ Util.formatBytes(paramtbusy));
			tbusy = (TBusy) WinOperationsCoding.decodeOperation(paramtbusy, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (tbusy == null) {
			if (logger.isDebugEnabled())
				logger.debug("Tbusy is received null in processReceivedMessage() in TbusyHandler");
			return false;
		}

		TBusySequenceType tbusySeqTyp = tbusy.getValue();
		NonASNBillingID billingId = null;
		NonASNMSCID mscid = null;
		TriggerCapability TrigCap = null;
		WINOperationsCapability WinOpCap =  null;
		NonASNTriggerCapability trigCapability = null;
		NonASNTransactionCapability tranCap = null;
		
		if(tbusySeqTyp.getTriggerType()!=null){
			TriggerType.EnumType trigTyp = tbusySeqTyp.getTriggerType().getValue();
			logger.debug("values of Trigger Type Enum is : "+trigTyp);
		}
				
		if(tbusySeqTyp.getWinCapability()!=null){
			if(tbusySeqTyp.getWinCapability().getTriggerCapability()!=null)
				TrigCap = tbusySeqTyp.getWinCapability().getTriggerCapability();
			if(tbusySeqTyp.getWinCapability().getWINOperationsCapability()!=null)
				WinOpCap = tbusySeqTyp.getWinCapability().getWINOperationsCapability();
		}
		
		NonASNWINOperationCapability WinOPCapability = null;
		try{
			if(tbusySeqTyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(tbusySeqTyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			
			if(tbusySeqTyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(tbusySeqTyp.getMscid().getValue());
				logger.debug("values of mscid are "+mscid.getMarketID()+" "+mscid.getSwitchNo());
			}
			
			if(TrigCap!=null){
				trigCapability = NonASNTriggerCapability.decodeTriggerCapability(TrigCap.getValue());
				logger.debug("values of TriggerCapability are : "+trigCapability.getInit_can_be_armed()+" "+
						trigCapability.getKDigit_can_be_armed()+" "+trigCapability.getAll_can_be_armed()+" "+
						trigCapability.getRvtc_can_be_armed()+" "+trigCapability.getCt_can_be_armed()+" "+
						trigCapability.getUnrec_can_be_armed()+" "+trigCapability.getPa_can_be_armed()+" "+
						trigCapability.getAt_can_be_armed()+" "+trigCapability.getTra_can_be_armed()+" "+
						trigCapability.getTbusy_can_be_armed()+" "+trigCapability.getTna_can_be_armed());
			}
			if(WinOpCap!=null){
				WinOPCapability = NonASNWINOperationCapability.decodeWINOperationCapability(WinOpCap.getValue());
				logger.debug("values of WinOperationsCapability are : "+WinOPCapability.getCircuitSwitchedData()+" "+
						WinOPCapability.getCCDIR()+" "+WinOPCapability.getPositionRequest()+" "+
						WinOPCapability.getConnectResource());
			}
			if(tbusySeqTyp.getTransactionCapability()!=null){
				tranCap = NonASNTransactionCapability.decodeTransactionCapability(tbusySeqTyp.getTransactionCapability().getValue());
				logger.debug("value of transaction capability are "+tranCap.getProfile()+" ,"+tranCap.getBusyDetection()
						+" ,"+tranCap.getRemoteUserInteraction()+" ,"+tranCap.getSubscriberPINIntercept()+" ,"+tranCap.getAnnouncements()
						+" ,"+tranCap.getMultipleTerminations()	+" ,"+tranCap.getTerminationList());
			}

		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
		
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TbusyHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.TBUSY, dialogId);
		simCpb.setWinReqInvokeId(Constants.TBUSY, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for TbusyHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.TBUSY))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a Tbusy Node");
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
				logger.debug("TbusyHandler validateMessage() isValid::["
						+ isValid + "]  Expected opcode::["
						+ tcapNode.getOpCodeString() + "] Actual Opcode::["
						+ opCodeStr + "] Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");

		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}


}
	