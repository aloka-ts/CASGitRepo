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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OCalledPartyBusyNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.IMSI;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.OCalledPartyBusy;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;
import com.agnity.win.asngenerated.OCalledPartyBusy.OCalledPartyBusySequenceType;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNIMSIType;
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
import com.agnity.win.enumdata.MultipleTerminationsEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.enumdata.ProfileEnum;
import com.agnity.win.enumdata.RemoteUserInteractionEnum;
import com.agnity.win.enumdata.SubscriberPINInterceptEnum;
import com.agnity.win.enumdata.TerminationListEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class OCalledPartyBusyHandler extends AbstractHandler{

	Logger logger = Logger.getLogger(OCalledPartyBusyHandler.class);
	private static Handler handler;

	
	 
	
	private static final String OCLDPARTYBUSY_BILLING_ID = "BillingId".toLowerCase();
	private static final String OCLDPARTYBUSY_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String OCLDPARTYBUSY_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String OCLDPARTYBUSY_ID_NUMBER = "idNumber".toLowerCase();
	private static final String OCLDPARTYBUSY_SEGMENT_COUNTER = "segmentCounter".toLowerCase();

	private static final String OCLDPARTYBUSY_MSCID = "Mscid".toLowerCase();
	private static final String OCLDPARTYBUSY_MARKETID = "marketID".toLowerCase();
	private static final String OCLDPARTYBUSY_SWITCHNUMBER = "switchNo".toLowerCase();
		
	private static final String OCLDPARTYBUSY_TRIGGER_TYPE = "TriggerType".toLowerCase();
	
	private static final String OCLDPARTYBUSY_WIN_CAPABILITY = "WinCapability".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_INIT = "TriggerCapability_init".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_KDIGIT = "TriggerCapability_kDigit".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_ALLCALLS = "TriggerCapability_allCalls".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_REVERTIVECALL = "TriggerCapability_revertiveCall".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_CALL_TYPE = "TriggerCapability_call_type".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_UNRECNO = "TriggerCapability_unrecNo".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_PRIORAGMT = "TriggerCapability_priorAgmt".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_ADVTERM = "TriggerCapability_advTerm".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_TERMRESAVAIL = "TriggerCapability_termResAvail".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_TBUSY = "TriggerCapability_tBusy".toLowerCase();
	private static final String OCLDPARTYBUSY_TRIGGER_CAPABILITY_TNOANS = "TriggerCapability_tNoAns".toLowerCase();
	
	private static final String OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM = "WINOperationsCapability_CircuitSwitchedDataEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CCDIRENUM = "WINOperationsCapability_CCDIREnum".toLowerCase();
	private static final String OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM = "WINOperationsCapability_PositionRequestEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM = "WINOperationsCapability_ConnectResourceEnum".toLowerCase();
	
	private static final String OCLDPARTYBUSY_TRANSCTN_CAPBLTY = "TransactionCapability".toLowerCase();

	private static final String OCLDPARTYBUSY_PROFILE_ENUM = "ProfileEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_BUSY_DETECTION_ENUM = "BusyDetectionEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_ANNOUNCEMENT_ENUM = "AnnouncementsEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_REMOTE_USR_INTERACTN_ENUM = "RemoteUserInteractionEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_SUBSCRBR_PIN_INTRCPT_ENUM = "SubscriberPINInterceptEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_MULTPL_TERM_ENUM = "MultipleTerminationsEnum".toLowerCase();
	private static final String OCLDPARTYBUSY_TERMLIST_ENUM ="TerminationListEnum".toLowerCase();
	
	private static final String ORREQ_MSID = "Msid".toLowerCase();
	private static final String ORREQ_IMSI = "IMSI".toLowerCase();
	private static final String ORREQ_MOBILE_IDENTIFICATION_NUM = "MobileIdentificationNumber".toLowerCase();
	
	
	private static final int OCALLEDPARTYBUSY_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (OCalledPartyBusyHandler.class) {
				if (handler == null) {
					handler = new OCalledPartyBusyHandler();
				}
			}
		}
		return handler;
	}

	private OCalledPartyBusyHandler() {

	}
	
	@Override
protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside OCLDPARTYBUSY handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.OCALLEDPARTYBUSY))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
				
		OCalledPartyBusyNode oCldPartyBusyNode = (OCalledPartyBusyNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		OCalledPartyBusy oCldPartyBusy = new OCalledPartyBusy();
		OCalledPartyBusySequenceType oCldPartyBusySeqTyp = new OCalledPartyBusySequenceType();	
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(OCLDPARTYBUSY_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(OCLDPARTYBUSY_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(OCLDPARTYBUSY_ORIGINATING_SWITCH_NUM).getValue(varMap);
					String idNumber = subFieldElems.get(OCLDPARTYBUSY_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(OCLDPARTYBUSY_SEGMENT_COUNTER).getValue(varMap);
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
						
					oCldPartyBusySeqTyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(OCLDPARTYBUSY_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(OCLDPARTYBUSY_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(OCLDPARTYBUSY_SWITCHNUMBER).getValue(varMap);
						
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
					oCldPartyBusySeqTyp.setMscid(mscid);
				}else if(fieldElem.getFieldType().equals(OCLDPARTYBUSY_TRIGGER_TYPE)){
					String value = fieldElem.getValue(varMap);
					TriggerType.EnumType triggerTypeEnum = TriggerType.EnumType.valueOf(value);
					TriggerType trigTyp = new TriggerType();
					trigTyp.setValue(triggerTypeEnum);
					oCldPartyBusySeqTyp.setTriggerType(trigTyp);
				}else if(fieldElem.getFieldType().equals(OCLDPARTYBUSY_WIN_CAPABILITY)){
					
					WINCapability wincap = new WINCapability();
					TriggerCapability trigCap = new TriggerCapability();
					WINOperationsCapability winOpCap = new WINOperationsCapability();
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					byte init = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_INIT).getValue(varMap));
					byte kDigit = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_KDIGIT).getValue(varMap));
					byte allCalls = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_ALLCALLS).getValue(varMap));
					byte revertiveCall = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_REVERTIVECALL).getValue(varMap));
					byte call_type = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_CALL_TYPE).getValue(varMap));
					byte unrecNo = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_UNRECNO).getValue(varMap));
					byte priorAgmt = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_PRIORAGMT).getValue(varMap));
					byte advTerm = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_ADVTERM).getValue(varMap));
					byte termResAvail = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_TERMRESAVAIL).getValue(varMap));
					byte tBusy = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_TBUSY).getValue(varMap));
					byte tNoAns = Byte.parseByte(subFieldElems.get(OCLDPARTYBUSY_TRIGGER_CAPABILITY_TNOANS).getValue(varMap));
					
					byte[] trigCapVal =null;
					try {
						trigCapVal = NonASNTriggerCapability.encodeTriggerCapability(init, kDigit, allCalls, revertiveCall, call_type, unrecNo, priorAgmt, advTerm, termResAvail, tBusy, tNoAns);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding TriggerCapability "+e.toString());
					}
					trigCap.setValue(trigCapVal);
					
					String val1 = subFieldElems.get(OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM).getValue(varMap);
					CircuitSwitchedDataEnum cktSwtchDataEnum = CircuitSwitchedDataEnum.valueOf(val1);
					LinkedList<CircuitSwitchedDataEnum> l0 = new LinkedList<CircuitSwitchedDataEnum>();
					l0.add(cktSwtchDataEnum);
					
					String val2 = subFieldElems.get(OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CCDIRENUM).getValue(varMap);
					CCDIREnum ccdirEnum = CCDIREnum.valueOf(val2);
					LinkedList<CCDIREnum> l1 = new LinkedList<CCDIREnum>();
					l1.add(ccdirEnum);
					
					String val3 = subFieldElems.get(OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM).getValue(varMap);
					PositionRequestEnum posReqEnum = PositionRequestEnum.valueOf(val3);
					LinkedList<PositionRequestEnum> l2 = new LinkedList<PositionRequestEnum>();
					l2.add(posReqEnum);
					
					String val4 = subFieldElems.get(OCLDPARTYBUSY_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM).getValue(varMap);
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
					oCldPartyBusySeqTyp.setWinCapability(wincap);
				}else if (fieldElem.getFieldType().equals(OCLDPARTYBUSY_TRANSCTN_CAPBLTY)){
					//setting dummy val as NONASNdatatype are not implemented yet
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
										
					ProfileEnum proflEnum = ProfileEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_PROFILE_ENUM).getValue(varMap));
					BusyDetectionEnum busyDetectnEnum = BusyDetectionEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_BUSY_DETECTION_ENUM).getValue(varMap));
					AnnouncementsEnum annuncmntEnum = AnnouncementsEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_ANNOUNCEMENT_ENUM).getValue(varMap));
					RemoteUserInteractionEnum remotUsrIntEnum = RemoteUserInteractionEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_REMOTE_USR_INTERACTN_ENUM).getValue(varMap));
					SubscriberPINInterceptEnum subsPinIntrcptEnum = SubscriberPINInterceptEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_SUBSCRBR_PIN_INTRCPT_ENUM).getValue(varMap));
					MultipleTerminationsEnum mltplTermEnum = MultipleTerminationsEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_MULTPL_TERM_ENUM).getValue(varMap));
					TerminationListEnum termListEnum = TerminationListEnum.valueOf(subFieldElems.get(OCLDPARTYBUSY_TERMLIST_ENUM).getValue(varMap));
										
					byte[] byteValTransctnCap = null;
					try {
						byteValTransctnCap = NonASNTransactionCapability.encodeTransactionCapability(proflEnum, busyDetectnEnum, annuncmntEnum, remotUsrIntEnum, subsPinIntrcptEnum, mltplTermEnum, termListEnum);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding TransactionCapability "+e.toString());
					}
					
					TransactionCapability transCap = new TransactionCapability();
					transCap.setValue(byteValTransctnCap);
					oCldPartyBusySeqTyp.setTransactionCapability(transCap);
				}else if(fieldElem.getFieldType().equals(ORREQ_MSID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems = fieldElem.getSubFieldElements();
								
					String IMSI = subFieldElems.get(ORREQ_IMSI).getValue(varMap);
					String mobileIdentificationNumber = subFieldElems.get(ORREQ_MOBILE_IDENTIFICATION_NUM).getValue(varMap);
					MSID msid = new MSID();
					if(!(IMSI.equals("null"))&&!(mobileIdentificationNumber.equals("null"))){
						logger.debug("Only one out of IMSI and MobileIdentificationNumber should be set");
						return false;
					}
					if(IMSI.equals("null")&&mobileIdentificationNumber.equals("null")){
						logger.debug("Atleast one out of IMSI and MobileIdentificationNumber should be set");
						return false;
					}
					if(!(IMSI.equals("null"))){
						logger.debug("Selecting IMSI");
						IMSI imsi = new IMSI();
						byte[] imsiVal =null;
						try {
							imsiVal = NonASNIMSIType.encodeIMSIType(IMSI);
						} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding IMSI in MSID :"+e.toString());
						}
						imsi.setValue(imsiVal);
						msid.selectImsi(imsi);
						oCldPartyBusySeqTyp.setMsid(msid);
					}else if(!(mobileIdentificationNumber.equals("null"))){
						logger.debug("Selecting MobileIdentificationNumber");
						MobileIdentificationNumber mobIdenNum = new MobileIdentificationNumber();
						MINType minTyp = new MINType();
						byte[] mobIdenNoVal =null;
						try {
							mobIdenNoVal = NonASNMINType.encodeMINType(mobileIdentificationNumber);
						} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding MobileIdentificationNumber in MSID :"+e.toString());
						}
						minTyp.setValue(mobIdenNoVal);
						mobIdenNum.setValue(minTyp);
						msid.selectMobileIdentificationNumber(mobIdenNum);
						oCldPartyBusySeqTyp.setMsid(msid);
					}				
				}
			}
		}
		
				
		oCldPartyBusy.setValue(oCldPartyBusySeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(oCldPartyBusy);
		opCode.add(WinOpCodes.O_CLD_PTY_BUSY);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] oCldPrtyBusyAnswer = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for OCLDPARTYBUSYHandler--> Got OCLDPARTYBUSY byte array:: "+Util.formatBytes(oCldPrtyBusyAnswer));
		
		if(logger.isDebugEnabled())
			logger.debug("OCLDPARTYBUSYHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("OCLDPARTYBUSYHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.OCALLEDPARTYBUSY,dialogueId);
		simCpb.setWinReqInvokeId(Constants.OCALLEDPARTYBUSY, invokeId);
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("OCLDPARTYBUSYHandler processNode()-->OCLDPARTYBUSY byte array generated creating reqEvent["+oCldPrtyBusyAnswer+"]");
		
		byte[] operationCode = {WinOpCodes.O_CLD_PTY_BUSY_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, oCldPrtyBusyAnswer));
		ire.setClassType(OCALLEDPARTYBUSY_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("OCLDPARTYBUSYHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending OCLDPARTYBUSY component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("OCLDPARTYBUSYHandler processNode()-->component send");
		if(oCldPartyBusyNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("OCLDPARTYBUSYHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.OCALLEDPARTYBUSY);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), oCldPartyBusyNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("OCLDPARTYBUSYHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on OCLDPARTYBUSY::"+oCldPartyBusyNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on OCLDPARTYBUSY::"+oCldPartyBusyNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving OCLDPARTYBUSY processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OCLDPARTYBUSYHandler"); 
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("TanswerHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		//Iterator<Node> subElemIterator = subElem.iterator();
	    int dialogId,invokeId;
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		OCalledPartyBusy oCldpartyBusy = null;
		try {
			byte[] paramOCldPartyBusy = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for OCLDPARTYBUSYHandler--> starting first level decoding on OCLDPARTYBUSY bytes:: "
								+ Util.formatBytes(paramOCldPartyBusy));
			oCldpartyBusy = (OCalledPartyBusy) WinOperationsCoding.decodeOperation(paramOCldPartyBusy, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (oCldpartyBusy == null) {
			if (logger.isDebugEnabled())
				logger.debug("OCLDPARTYBUSY is received null in processReceivedMessage() in OCLDPARTYBUSYHandler");
			return false;
		}

		OCalledPartyBusySequenceType oCldPartyBusySeqTyp = oCldpartyBusy.getValue();
		NonASNBillingID billingId = null;
		
		NonASNMSCID mscid = null;
		TriggerCapability TrigCap = null;
		WINOperationsCapability WinOpCap =  null;
		NonASNTriggerCapability trigCapability = null;
		NonASNTransactionCapability tranCap = null;
		
		if(oCldPartyBusySeqTyp.getTriggerType()!=null){
			TriggerType.EnumType trigTyp = oCldPartyBusySeqTyp.getTriggerType().getValue();
			logger.debug("values of Trigger Type Enum is : "+trigTyp);
		}
		if(oCldPartyBusySeqTyp.getWinCapability()!=null){
			if(oCldPartyBusySeqTyp.getWinCapability().getTriggerCapability()!=null)
				TrigCap = oCldPartyBusySeqTyp.getWinCapability().getTriggerCapability();
			if(oCldPartyBusySeqTyp.getWinCapability().getWINOperationsCapability()!=null)
				WinOpCap = oCldPartyBusySeqTyp.getWinCapability().getWINOperationsCapability();
		}
		
				
		NonASNWINOperationCapability WinOPCapability = null;
		try{
			if(oCldPartyBusySeqTyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(oCldPartyBusySeqTyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			if(oCldPartyBusySeqTyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(oCldPartyBusySeqTyp.getMscid().getValue());
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
			if(oCldPartyBusySeqTyp.getTransactionCapability()!=null){
				tranCap = NonASNTransactionCapability.decodeTransactionCapability(oCldPartyBusySeqTyp.getTransactionCapability().getValue());
				logger.debug("value of transaction capability are "+tranCap.getProfile()+" ,"+tranCap.getBusyDetection()
						+" ,"+tranCap.getRemoteUserInteraction()+" ,"+tranCap.getSubscriberPINIntercept()+" ,"+tranCap.getAnnouncements()
						+" ,"+tranCap.getMultipleTerminations()	+" ,"+tranCap.getTerminationList());
			}

		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
				
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OCLDPARTYBUSYHandler leaving with status true");
			simCpb.setWinReqDialogId(Constants.OCALLEDPARTYBUSY, dialogId);
			simCpb.setWinReqInvokeId(Constants.OCALLEDPARTYBUSY, invokeId);
			return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for OCLDPARTYBUSYHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.OCALLEDPARTYBUSY))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a OCLDPARTYBUSY Node");
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
				logger.debug("OCLDPARTYBUSYHandler validateMessage() isValid::["
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