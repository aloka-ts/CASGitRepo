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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.AnlyzdNode;

import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.domainobjects.WinMsgDataKey;
import com.agnity.simulator.domainobjects.WinMsgValues;
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
import com.agnity.win.asngenerated.IMSI;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;

import com.agnity.win.asngenerated.AnalyzedInformation.AnalyzedInformationSequenceType;

import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNIMSIType;
import com.agnity.win.datatypes.NonASNMINType;

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


public class AnlyzdHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(AnlyzdHandler.class);
	private static Handler handler;

	private static final String ANLYZD_BILLING_ID = "BillingId".toLowerCase();
	private static final String ANLYZD_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String ANLYZD_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String ANLYZD_ID_NUMBER = "idNumber".toLowerCase();
	private static final String ANLYZD_SEGMENT_COUNTER = "segmentCounter".toLowerCase();

	private static final String ANLYZD_DIGITS = "Digits".toLowerCase();
	private static final String ANLYZD_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String ANLYZD_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String ANLYZD_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String ANLYZD_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String ANLYZD_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String ANLYZD_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String ANLYZD_NUMPLAN = "numPlan".toLowerCase();
	private static final String ANLYZD_ENCODINGSCHEME = "EncodingScheme".toLowerCase();

	private static final String ANLYZD_MSCID = "Mscid".toLowerCase();
	private static final String ANLYZD_MARKETID = "marketID".toLowerCase();
	private static final String ANLYZD_SWITCHNUMBER = "switchNo".toLowerCase();
		
	private static final String ANLYZD_TRIGGER_TYPE = "TriggerType".toLowerCase();
	
	private static final String ANLYZD_WIN_CAPABILITY = "WinCapability".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_INIT = "TriggerCapability_init".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_KDIGIT = "TriggerCapability_kDigit".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_ALLCALLS = "TriggerCapability_allCalls".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_REVERTIVECALL = "TriggerCapability_revertiveCall".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_CALL_TYPE = "TriggerCapability_call_type".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_UNRECNO = "TriggerCapability_unrecNo".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_PRIORAGMT = "TriggerCapability_priorAgmt".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_ADVTERM = "TriggerCapability_advTerm".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_TERMRESAVAIL = "TriggerCapability_termResAvail".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_TBUSY = "TriggerCapability_tBusy".toLowerCase();
	private static final String ANLYZD_TRIGGER_CAPABILITY_TNOANS = "TriggerCapability_tNoAns".toLowerCase();
	
	private static final String ANLYZD_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM = "WINOperationsCapability_CircuitSwitchedDataEnum".toLowerCase();
	private static final String ANLYZD_WIN_OPERATIONS_CAPABILITY_CCDIRENUM = "WINOperationsCapability_CCDIREnum".toLowerCase();
	private static final String ANLYZD_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM = "WINOperationsCapability_PositionRequestEnum".toLowerCase();
	private static final String ANLYZD_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM = "WINOperationsCapability_ConnectResourceEnum".toLowerCase();
	
	private static final String ANLYZD_TRANSCTN_CAPBLTY = "TransactionCapability".toLowerCase();
	
	private static final String ANLYZD_WIN_TIME_OF_DAY = "TimeOfDay".toLowerCase();
	
	private static final String ANLYZD_DESTINATION_DIGITS = "DestinationDigits".toLowerCase();
	
	private static final String ANLYZD_MSID = "Msid".toLowerCase();
	private static final String ANLYZD_IMSI = "IMSI".toLowerCase();
	private static final String ANLYZD_MOBILE_IDENTIFICATION_NUM = "MobileIdentificationNumber".toLowerCase();
	
	private static final int ANLYZD_CLASS = 2;
	
	
	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (AnlyzdHandler.class) {
				if (handler == null) {
					handler = new AnlyzdHandler();
				}
			}
		}
		return handler;
	}

	private AnlyzdHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside ANLYZD handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.ANLYZD))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		//dummy val to be used as byte array
		byte [] dummyDefVal = {(byte)0x02,(byte)0x03};
		String idNumber;
		AnlyzdNode anlyzdNode = (AnlyzdNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		AnalyzedInformation ANLYZD = new AnalyzedInformation();
		AnalyzedInformationSequenceType analyzdInfoSeqTyp = new AnalyzedInformationSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(ANLYZD_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(ANLYZD_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(ANLYZD_ORIGINATING_SWITCH_NUM).getValue(varMap);
					idNumber = subFieldElems.get(ANLYZD_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(ANLYZD_SEGMENT_COUNTER).getValue(varMap);
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
						
					analyzdInfoSeqTyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(ANLYZD_DIGITS)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_ENCODINGSCHEME).getValue(varMap));
					
					Digits digits = new Digits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					digits.setValue(digitTyp);
				
					analyzdInfoSeqTyp.setDigits(digits);
				}else if(fieldElem.getFieldType().equals(ANLYZD_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(ANLYZD_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(ANLYZD_SWITCHNUMBER).getValue(varMap);
						
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
					analyzdInfoSeqTyp.setMscid(mscid);
				}else if(fieldElem.getFieldType().equals(ANLYZD_TRIGGER_TYPE)){
					String value = fieldElem.getValue(varMap);
					TriggerType.EnumType triggerTypeEnum = TriggerType.EnumType.valueOf(value);
					TriggerType trigTyp = new TriggerType();
					trigTyp.setValue(triggerTypeEnum);
					analyzdInfoSeqTyp.setTriggerType(trigTyp);
				}else if(fieldElem.getFieldType().equals(ANLYZD_WIN_CAPABILITY)){
					
					WINCapability wincap = new WINCapability();
					TriggerCapability trigCap = new TriggerCapability();
					WINOperationsCapability winOpCap = new WINOperationsCapability();
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					byte init = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_INIT).getValue(varMap));
					byte kDigit = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_KDIGIT).getValue(varMap));
					byte allCalls = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_ALLCALLS).getValue(varMap));
					byte revertiveCall = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_REVERTIVECALL).getValue(varMap));
					byte call_type = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_CALL_TYPE).getValue(varMap));
					byte unrecNo = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_UNRECNO).getValue(varMap));
					byte priorAgmt = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_PRIORAGMT).getValue(varMap));
					byte advTerm = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_ADVTERM).getValue(varMap));
					byte termResAvail = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_TERMRESAVAIL).getValue(varMap));
					byte tBusy = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_TBUSY).getValue(varMap));
					byte tNoAns = Byte.parseByte(subFieldElems.get(ANLYZD_TRIGGER_CAPABILITY_TNOANS).getValue(varMap));
					
					byte[] trigCapVal =null;
					try {
						trigCapVal = NonASNTriggerCapability.encodeTriggerCapability(init, kDigit, allCalls, revertiveCall, call_type, unrecNo, priorAgmt, advTerm, termResAvail, tBusy, tNoAns);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding TriggerCapability "+e.toString());
					}
					trigCap.setValue(trigCapVal);
					
					String val1 = subFieldElems.get(ANLYZD_WIN_OPERATIONS_CAPABILITY_CKT_SWTCHDATAENUM).getValue(varMap);
					CircuitSwitchedDataEnum cktSwtchDataEnum = CircuitSwitchedDataEnum.valueOf(val1);
					LinkedList<CircuitSwitchedDataEnum> l0 = new LinkedList<CircuitSwitchedDataEnum>();
					l0.add(cktSwtchDataEnum);
					
					String val2 = subFieldElems.get(ANLYZD_WIN_OPERATIONS_CAPABILITY_CCDIRENUM).getValue(varMap);
					CCDIREnum ccdirEnum = CCDIREnum.valueOf(val2);
					LinkedList<CCDIREnum> l1 = new LinkedList<CCDIREnum>();
					l1.add(ccdirEnum);
					
					String val3 = subFieldElems.get(ANLYZD_WIN_OPERATIONS_CAPABILITY_POS_REQ_ENUM).getValue(varMap);
					PositionRequestEnum posReqEnum = PositionRequestEnum.valueOf(val3);
					LinkedList<PositionRequestEnum> l2 = new LinkedList<PositionRequestEnum>();
					l2.add(posReqEnum);
					
					String val4 = subFieldElems.get(ANLYZD_WIN_OPERATIONS_CAPABILITY_CON_RES_ENUM).getValue(varMap);
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
					analyzdInfoSeqTyp.setWinCapability(wincap);
				}else if (fieldElem.getFieldType().equals(ANLYZD_TRANSCTN_CAPBLTY)){
					//setting dummy val as NONASNdatatype are not implemented yet
					String value = fieldElem.getValue(varMap);
					byte [] byteVal = Helper.hexStringToByteArray(value);
					TransactionCapability transCap = new TransactionCapability();
					transCap.setValue(byteVal);
					analyzdInfoSeqTyp.setTransactionCapability(transCap);
				}else if(fieldElem.getFieldType().equals(ANLYZD_WIN_TIME_OF_DAY)){
					String todValue = fieldElem.getValue(varMap);
					TimeOfDay tod = new TimeOfDay();
					tod.setValue(Long.parseLong(todValue));
					analyzdInfoSeqTyp.setTimeOfDay(tod);
				}else if(fieldElem.getFieldType().equals(ANLYZD_DESTINATION_DIGITS)){
					DestinationDigits dstnDigits = new DestinationDigits();
					DigitsType digtType = new DigitsType();
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_ENCODINGSCHEME).getValue(varMap));
					
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digtType.setValue(digitVal);
					
					dstnDigits.setValue(digtType);
					analyzdInfoSeqTyp.setDestinationDigits(dstnDigits);
				}else if(fieldElem.getFieldType().equals(ANLYZD_MSID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems = fieldElem.getSubFieldElements();
					String IMSI = subFieldElems.get(ANLYZD_IMSI).getValue(varMap);
					String mobileIdentificationNumber = subFieldElems.get(ANLYZD_MOBILE_IDENTIFICATION_NUM).getValue(varMap);
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
						analyzdInfoSeqTyp.setMsid(msid);
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
						analyzdInfoSeqTyp.setMsid(msid);
					}
					
				}
			}
		}
		
		if(!analyzdInfoSeqTyp.isMsidPresent()){
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
			analyzdInfoSeqTyp.setMsid(msid);
		}
				
		ANLYZD.setValue(analyzdInfoSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(ANLYZD);
		opCode.add(WinOpCodes.AIR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] anlyzd = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for AnlyzdHandler--> Got anlyzd byte array:: "+Util.formatBytes(anlyzd));
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqInvokeId(Constants.ANLYZD,invokeId);
		simCpb.setWinReqDialogId(Constants.ANLYZD,dialogueId);
		
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdHandler processNode()-->Anlyzd byte array generated creating reqEvent["+anlyzd+"]");
		
		byte[] operationCode = {0x40};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, anlyzd));
		ire.setClassType(ANLYZD_CLASS);
		ire.setLinkedId(invokeId);
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending anlyzd component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdHandler processNode()-->component send");
		if(anlyzdNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("AnlyzdHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.ANLYZD);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), anlyzdNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("AnlyzdHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on anlyzd::"+anlyzdNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on anlyzd::"+anlyzdNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving anlyzd processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for AnlyzdHandler");
		
		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("AnlyzdHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		 */
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		int dialogId,invokeId;
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		AnalyzedInformation anlyzd = null;
		try {
			byte[] paramanlyzd = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for AnlyzdHandler--> starting first level decoding on anlyzd bytes:: "
								+ Util.formatBytes(paramanlyzd));
			anlyzd = (AnalyzedInformation) WinOperationsCoding.decodeOperation(paramanlyzd, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (anlyzd == null) {
			if (logger.isDebugEnabled())
				logger.debug("anlyzd is received null in processReceivedMessage() in AnlyzdHandler");
			return false;
		}

		AnalyzedInformationSequenceType analyzdInfoSeqTyp = anlyzd.getValue();
		NonASNBillingID billingId = null;
		NonASNDigitsType digitType = null;
		NonASNMSCID mscid = null;
		TriggerCapability TrigCap = null;
		WINOperationsCapability WinOpCap =  null;
		NonASNTriggerCapability trigCapability = null;
		if(analyzdInfoSeqTyp.getTriggerType()!=null){
			TriggerType.EnumType trigTyp = analyzdInfoSeqTyp.getTriggerType().getValue();
			logger.debug("values of Trigger Type Enum is : "+trigTyp);
		}
		if(analyzdInfoSeqTyp.getWinCapability()!=null){
			if(analyzdInfoSeqTyp.getWinCapability().getTriggerCapability()!=null)
				TrigCap = analyzdInfoSeqTyp.getWinCapability().getTriggerCapability();
			if(analyzdInfoSeqTyp.getWinCapability().getWINOperationsCapability()!=null)
				WinOpCap = analyzdInfoSeqTyp.getWinCapability().getWINOperationsCapability();
		}
		NonASNWINOperationCapability WinOPCapability = null;
		try{
			if(analyzdInfoSeqTyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(analyzdInfoSeqTyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			if(analyzdInfoSeqTyp.getDigits()!=null){
				digitType = NonASNDigitsType.decodeDigits(analyzdInfoSeqTyp.getDigits().getValue().getValue());
				logger.debug("value of digits are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
			if(analyzdInfoSeqTyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(analyzdInfoSeqTyp.getMscid().getValue());
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

		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
		
		
		
		
		
		
		
		
		
		
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for AnlyzdHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.ANLYZD, dialogId);
		simCpb.setWinReqInvokeId(Constants.ANLYZD, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for AnlyzdHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.ANLYZD))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a anlyzd Node");
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
				logger.debug("AnlyzdHandler validateMessage() isValid::["
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
