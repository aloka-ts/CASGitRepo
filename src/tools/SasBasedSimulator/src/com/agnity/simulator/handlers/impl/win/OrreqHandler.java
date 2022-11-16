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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OrreqNode;

import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;

import com.agnity.win.asngenerated.ElectronicSerialNumber;
import com.agnity.win.asngenerated.IMSI;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.OriginationRequest;
import com.agnity.win.asngenerated.OriginationTriggers;
import com.agnity.win.asngenerated.TransactionCapability;

import com.agnity.win.asngenerated.OriginationRequest.OriginationRequestSequenceType;

import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNIMSIType;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonAsnElectronicSerialNum;

import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class OrreqHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(OrreqHandler.class);
	private static Handler handler;

	private static final String ORREQ_BILLING_ID = "BillingId".toLowerCase();
	private static final String ORREQ_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String ORREQ_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String ORREQ_ID_NUMBER = "idNumber".toLowerCase();
	private static final String ORREQ_SEGMENT_COUNTER = "segmentCounter".toLowerCase();

	private static final String ORREQ_DIGITS = "Digits".toLowerCase();
	private static final String ORREQ_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String ORREQ_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String ORREQ_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String ORREQ_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String ORREQ_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String ORREQ_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String ORREQ_NUMPLAN = "numPlan".toLowerCase();
	private static final String ORREQ_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
	
	private static final String ORREQ_ELEC_SERIAL_NUMBER = "ElectronicSerialNumber".toLowerCase();
	private static final String ORREQ_MANUFACTURERSCODE = "manufacturersCode".toLowerCase();
	private static final String ORREQ_SERIALNUMBER = "serialNumber".toLowerCase();
	
	private static final String ORREQ_MSCID = "Mscid".toLowerCase();
	private static final String ORREQ_MARKETID = "marketID".toLowerCase();
	private static final String ORREQ_SWITCHNUMBER = "switchNo".toLowerCase();
	
	private static final String ORREQ_MSID = "Msid".toLowerCase();
	private static final String ORREQ_IMSI = "IMSI".toLowerCase();
	private static final String ORREQ_MOBILE_IDENTIFICATION_NUM = "MobileIdentificationNumber".toLowerCase();
	
	private static final String ORREQ_ORIGINATION_TRIGGER = "OriginationTriggers".toLowerCase();
	
	private static final String ORREQ_TRANSACTION_CAPABILITY = "TransactionCapability".toLowerCase();
	
	private static int idNoBillingId;
	
	private static final int ORREQ_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (OrreqHandler.class) {
				if (handler == null) {
					handler = new OrreqHandler();
				}
			}
		}
		return handler;
	}

	private OrreqHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside ORREQ handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.ORREQ))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		
		OrreqNode orreqNode = (OrreqNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		OriginationRequest ORREQ = new OriginationRequest();
		OriginationRequestSequenceType origReqSeqTyp = new OriginationRequestSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(ORREQ_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(ORREQ_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(ORREQ_ORIGINATING_SWITCH_NUM).getValue(varMap);
					String idNumber = subFieldElems.get(ORREQ_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(ORREQ_SEGMENT_COUNTER).getValue(varMap);
					BillingID billingid = new BillingID();
					
					short originatingMarketID = Short.parseShort(origMrktId);
					short originatingSwitchNo = Short.parseShort(origSwitchNo);
					int idNo = Integer.parseInt(idNumber);
					idNoBillingId = idNo;
					short segCounter = Short.parseShort(segmentCounter);
					byte[] billingIdVal = null;
					
					try {
						billingIdVal = NonASNBillingID.encodeBillingID(originatingMarketID, originatingSwitchNo, idNo, segCounter);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding billingId :"+e.toString());
					}
					
					
					billingid.setValue(billingIdVal);
						
					origReqSeqTyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(ORREQ_DIGITS)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ORREQ_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ORREQ_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ORREQ_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ORREQ_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ORREQ_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ORREQ_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ORREQ_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ORREQ_ENCODINGSCHEME).getValue(varMap));
					
					Digits digits = new Digits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding digits :"+e.toString());
					}
					digitTyp.setValue(digitVal);
					digits.setValue(digitTyp);
				
					origReqSeqTyp.setDigits(digits);
				}else if(fieldElem.getFieldType().equals(ORREQ_ELEC_SERIAL_NUMBER)){
					ElectronicSerialNumber elecSerialNum = new ElectronicSerialNumber();
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					byte mc = Byte.parseByte(subFieldElems.get(ORREQ_MANUFACTURERSCODE).getValue(varMap));
					int sn = Integer.parseInt(subFieldElems.get(ORREQ_SERIALNUMBER).getValue(varMap));
					byte[] nonAsnElecSerialNum = null;
					try {
						nonAsnElecSerialNum = NonAsnElectronicSerialNum.encodeEcn(mc, sn);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception while encoding electronicSerialNumber :"+e.toString());
					}
					elecSerialNum.setValue(nonAsnElecSerialNum);
					origReqSeqTyp.setElectronicSerialNumber(elecSerialNum);
				}else if(fieldElem.getFieldType().equals(ORREQ_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(ORREQ_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(ORREQ_SWITCHNUMBER).getValue(varMap);
						
					MSCID mscid = new MSCID();
					byte[] mscidVal =null;
					try {
						mscidVal = NonASNMSCID.encodeMSCID(Short.parseShort(marketID), Integer.parseInt(switchNo));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding MSCID :"+e.toString());
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding MSCID :"+e.toString());
					}
					
					mscid.setValue(mscidVal);
					origReqSeqTyp.setMscid(mscid);
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
						origReqSeqTyp.setMsid(msid);
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
						origReqSeqTyp.setMsid(msid);
					}
					
					
				}else if(fieldElem.getFieldType().equals(ORREQ_ORIGINATION_TRIGGER)){
					//setting dummy val as NONASNdatatype are not implemented yet
					String value = fieldElem.getValue(varMap);
					byte [] byteVal = Helper.hexStringToByteArray(value);
					OriginationTriggers origTrig = new OriginationTriggers();
					origTrig.setValue(byteVal);
					origReqSeqTyp.setOriginationTriggers(origTrig);
				}else if(fieldElem.getFieldType().equals(ORREQ_TRANSACTION_CAPABILITY)){
					//setting dummy val as NONASNdatatype are not implemented yet
					String value = fieldElem.getValue(varMap);
					byte [] byteVal = Helper.hexStringToByteArray(value);
					TransactionCapability transCap = new TransactionCapability();
					transCap.setValue(byteVal);
					origReqSeqTyp.setTransactionCapability(transCap);
					
					
				}else if(fieldElem.getFieldType().equals(ORREQ_MOBILE_IDENTIFICATION_NUM)){
					String value = fieldElem.getValue(varMap);
					MobileIdentificationNumber mobIdnNum = new MobileIdentificationNumber();
					MINType minTyp = new MINType();
					byte[] mobIdenNoVal =null;
					try {
						mobIdenNoVal = NonASNMINType.encodeMINType(value);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding MobileIdentificationNumber in MSID :"+e.toString());
					}
					minTyp.setValue(mobIdenNoVal);
					mobIdnNum.setValue(minTyp);
					origReqSeqTyp.setMobileIdentificationNumber(mobIdnNum);					
				}
			}
		}
		
		ORREQ.setValue(origReqSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(ORREQ);
		opCode.add(WinOpCodes.OR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] orreq = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for OrreqHandler--> Got orreq byte array:: "+Util.formatBytes(orreq));
		
		if(logger.isDebugEnabled())
			logger.debug("OrreqHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("OrreqHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.ORREQ,dialogueId);
		simCpb.setWinReqInvokeId(Constants.ORREQ,invokeId);
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("OrreqHandler processNode()-->Orreq byte array generated creating reqEvent["+orreq+"]");
		
		byte[] operationCode = {0x2F};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, orreq));
		ire.setClassType(ORREQ_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("OrreqHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending orreq component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("OrreqHandler processNode()-->component send");
		if(orreqNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("OrreqHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.ORREQ);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), orreqNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("OrreqHandler processNode()-->sending created dialog ["+dre+"]");
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

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OrreqHandler");

		//List<Node> subElem = node.getSubElements();
	
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("OrreqHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/

		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		OriginationRequest orreq = null;
		int dialogId,invokeId;
		try {
			byte[] paramorreq = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for OrreqHandler--> starting first level decoding on orreq bytes:: "
								+ Util.formatBytes(paramorreq));
			orreq = (OriginationRequest) WinOperationsCoding.decodeOperation(paramorreq, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (orreq == null) {
			if (logger.isDebugEnabled())
				logger.debug("orreq is received null in processReceivedMessage() in OrreqHandler");
			return false;
		}

		OriginationRequestSequenceType origreqseqtyp = orreq.getValue();
		NonASNBillingID billingId = null;
		NonASNDigitsType digitType = null;
		NonASNMSCID mscid = null;
		try{
			if(origreqseqtyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(origreqseqtyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			if(origreqseqtyp.getDigits()!=null){
				digitType = NonASNDigitsType.decodeDigits(origreqseqtyp.getDigits().getValue().getValue());
				logger.debug("value of digits are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
			if(origreqseqtyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(origreqseqtyp.getMscid().getValue());
				logger.debug("values of mscid are "+mscid.getMarketID()+" "+mscid.getSwitchNo());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
		
		if(origreqseqtyp.isMsidPresent())	
			logger.debug("vaue of msid is: Imsi Selected = "+origreqseqtyp.getMsid().isImsiSelected()+"MobileIdentificationNumber = "+origreqseqtyp.getMsid().isMobileIdentificationNumberSelected());

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OrreqHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.ORREQ, dialogId);
		simCpb.setWinReqInvokeId(Constants.ORREQ, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for OrreqHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.ORREQ))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a orreq Node");
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
				logger.debug("OrreqHandler validateMessage() isValid::["
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