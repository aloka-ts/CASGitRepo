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
import com.agnity.win.asngenerated.ReleaseCause;
import com.agnity.win.asngenerated.TAnswer;
import com.agnity.win.asngenerated.TDisconnect;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;

import com.agnity.win.asngenerated.AnalyzedInformation.AnalyzedInformationSequenceType;
import com.agnity.win.asngenerated.TAnswer.TAnswerSequenceType;
import com.agnity.win.asngenerated.TDisconnect.TDisconnectSequenceType;

import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonAsnMobileIdNum;

import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNTriggerCapability;

import com.agnity.win.datatypes.NonASNWINOperationCapability;

import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PositionRequestEnum;

import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class TDisconnectHandler extends AbstractHandler{

	Logger logger = Logger.getLogger(TDisconnectHandler.class);
	private static Handler handler;

		
	private static final String TDISCONNECT_BILLING_ID = "BillingId".toLowerCase();
	private static final String TDISCONNECT_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String TDISCONNECT_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String TDISCONNECT_ID_NUMBER = "idNumber".toLowerCase();
	private static final String TDISCONNECT_SEGMENT_COUNTER = "segmentCounter".toLowerCase();

	private static final String TDISCONNECT_MSCID = "Mscid".toLowerCase();
	private static final String TDISCONNECT_MARKETID = "marketID".toLowerCase();
	private static final String TDISCONNECT_SWITCHNUMBER = "switchNo".toLowerCase();
		
	private static final String TDISCONNECT_TRIGGER_TYPE = "TriggerType".toLowerCase();
	
	private static final String TDISCONNECT_WIN_TIME_OF_DAY = "TimeOfDay".toLowerCase();
	
	private static final String TDISCONNECT_REL_CAUSE = "RelCause".toLowerCase();
	
	private static final int TDISCONNECT_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (TDisconnectHandler.class) {
				if (handler == null) {
					handler = new TDisconnectHandler();
				}
			}
		}
		return handler;
	}

	private TDisconnectHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside TDISCONNECT handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.TDISCONNECT))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		
		TDisconnectNode tdiscnnctNode = (TDisconnectNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		TDisconnect tdisconnect = new TDisconnect();
		TDisconnectSequenceType tdisconnctseqtyp = new TDisconnectSequenceType();
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(TDISCONNECT_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(TDISCONNECT_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(TDISCONNECT_ORIGINATING_SWITCH_NUM).getValue(varMap);
					String idNumber = subFieldElems.get(TDISCONNECT_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(TDISCONNECT_SEGMENT_COUNTER).getValue(varMap);
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
						
					tdisconnctseqtyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(TDISCONNECT_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(TDISCONNECT_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(TDISCONNECT_SWITCHNUMBER).getValue(varMap);
						
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
					tdisconnctseqtyp.setMscid(mscid);
				}else if(fieldElem.getFieldType().equals(TDISCONNECT_TRIGGER_TYPE)){
					String value = fieldElem.getValue(varMap);
					TriggerType.EnumType triggerTypeEnum = TriggerType.EnumType.valueOf(value);
					TriggerType trigTyp = new TriggerType();
					trigTyp.setValue(triggerTypeEnum);
					tdisconnctseqtyp.setTriggerType(trigTyp);
				}else if(fieldElem.getFieldType().equals(TDISCONNECT_WIN_TIME_OF_DAY)){
					String todValue = fieldElem.getValue(varMap);
					TimeOfDay tod = new TimeOfDay();
					tod.setValue(Long.parseLong(todValue));
					tdisconnctseqtyp.setTimeOfDay(tod);
				}else if(fieldElem.getFieldType().equals(TDISCONNECT_REL_CAUSE)){
					ReleaseCause relCause = new ReleaseCause();
					ReleaseCause.EnumType relCauseVal = ReleaseCause.EnumType.valueOf(fieldElem.getValue(varMap));
					relCause.setValue(relCauseVal);
					tdisconnctseqtyp.setReleaseCause(relCause);
				}
			}
		}
		
		//some problem in decoding of Billing Id happens, alternate workaround told by win codec team is to set msid (dummy value)
		MSID msid = new MSID();
		String mobileIdnNum = "101";
		
		MobileIdentificationNumber mobIdnNum = new MobileIdentificationNumber();
		byte[] mintyp = null;
//		try {
//			mintyp = NonAsnMobileIdNum.encodeMin(mobileIdnNum);
//		} catch (InvalidInputException e1) {
//			// TODO Auto-generated catch block
//			logger.debug("Error while encoding MSID "+e1.toString());
//		}
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
		tdisconnctseqtyp.setMsid(msid);
		
		tdisconnect.setValue(tdisconnctseqtyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(tdisconnect);
		opCode.add(WinOpCodes.T_DISC);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] tdiscnnct = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for TdisconnectHandler--> Got Tdisconnect byte array:: "+Util.formatBytes(tdiscnnct));
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.TDISCONNECT,dialogueId);
		simCpb.setWinReqInvokeId(Constants.TDISCONNECT, invokeId);
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectHandler processNode()-->Tdisconnect byte array generated creating reqEvent["+tdiscnnct+"]");
		
		byte[] operationCode = {WinOpCodes.T_DISC_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, tdiscnnct));
		ire.setClassType(TDISCONNECT_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending Tdisconnect component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("TdisconnectHandler processNode()-->component send");
		if(tdiscnnctNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("TdisconnectHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.TDISCONNECT);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), tdiscnnctNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("TdisconnectHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on Tdisconnect::"+tdiscnnctNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on Tdisconnect::"+tdiscnnctNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving Tdisconnect processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TdisconnectHandler"); 
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("TdisconnectHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		int dialogId,invokeId;
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		TDisconnect tdiscnct = null;
		try {
			byte[] paramtdiscnct = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for TdisconnectHandler--> starting first level decoding on Tdisconnect bytes:: "
								+ Util.formatBytes(paramtdiscnct));
			tdiscnct = (TDisconnect) WinOperationsCoding.decodeOperation(paramtdiscnct, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (tdiscnct == null) {
			if (logger.isDebugEnabled())
				logger.debug("Tdisconnect is received null in processReceivedMessage() in TdisconnectHandler");
			return false;
		}

		TDisconnectSequenceType tdiscnctSeqTyp = tdiscnct.getValue();
		NonASNBillingID billingId = null;
		NonASNDigitsType digitType = null;
		NonASNMSCID mscid = null;
		TriggerCapability TrigCap = null;
		WINOperationsCapability WinOpCap =  null;
		NonASNTriggerCapability trigCapability = null;
		
		if(tdiscnctSeqTyp.getTriggerType()!=null){
			TriggerType.EnumType trigTyp = tdiscnctSeqTyp.getTriggerType().getValue();
			logger.debug("values of Trigger Type Enum is : "+trigTyp);
		}
		
		if(tdiscnctSeqTyp.getTimeOfDay()!=null){
			logger.debug("values of Time of day is : "+tdiscnctSeqTyp.getTimeOfDay().getValue());
		}
		
		if(tdiscnctSeqTyp.isReleaseCausePresent()){
			logger.debug("value of Release cause is : "+tdiscnctSeqTyp.getReleaseCause().getValue().toString());
		}
		
		try{
			if(tdiscnctSeqTyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(tdiscnctSeqTyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			
			if(tdiscnctSeqTyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(tdiscnctSeqTyp.getMscid().getValue());
				logger.debug("values of mscid are "+mscid.getMarketID()+" "+mscid.getSwitchNo());
			}
						
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
				
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TdisconnectHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.TDISCONNECT, dialogId);
		simCpb.setWinReqInvokeId(Constants.TDISCONNECT, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for TdisconnectHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.TDISCONNECT))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a Tdisconnect Node");
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
				logger.debug("TdisconnectHandler validateMessage() isValid::["
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
	