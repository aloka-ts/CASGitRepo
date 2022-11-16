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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OAnswerNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.IMSI;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileDirectoryNumber;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.OAnswer;
import com.agnity.win.asngenerated.TimeDateOffset;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.OAnswer.OAnswerSequenceType;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNIMSIType;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class OAnswerHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(OAnswerHandler.class);
	private static Handler handler;

	private static final String OANSWER_BILLING_ID = "BillingId".toLowerCase();
	private static final String OANSWER_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String OANSWER_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String OANSWER_ID_NUMBER = "idNumber".toLowerCase();
	private static final String OANSWER_SEGMENT_COUNTER = "segmentCounter".toLowerCase();
	
	private static final String OANSWER_MSCID = "Mscid".toLowerCase();
	private static final String OANSWER_MARKETID = "marketID".toLowerCase();
	private static final String OANSWER_SWITCHNUMBER = "switchNo".toLowerCase();
	
	private static final String OANSWER_MSID = "Msid".toLowerCase();
	private static final String OANSWER_IMSI = "IMSI".toLowerCase();
	private static final String OANSWER_MOBILE_IDENTIFICATION_NUM = "MobileIdentificationNumber".toLowerCase();

	private static final String OANSWER_TRIGGER_TYPE = "TriggerType".toLowerCase();
	
	private static final String OANSWER_TIME_OF_DAY = "TimeOfDay".toLowerCase();
	
	private static final String OANSWER_TIME_DATE_OFFSET = "TimeDateOffset".toLowerCase();
	
	private static final String OANSWER_MDN = "Mobiledirectorynumber".toLowerCase();
	private static final String OANSWER_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String OANSWER_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String OANSWER_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String OANSWER_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String OANSWER_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String OANSWER_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String OANSWER_NUMPLAN = "numPlan".toLowerCase();
	private static final String OANSWER_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
			
	
	
	private static final int OANSWER_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (OAnswerHandler.class) {
				if (handler == null) {
					handler = new OAnswerHandler();
				}
			}
		}
		return handler;
	}

	private OAnswerHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside Oanswerhandler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.OANSWER))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		//dummy val to be used as byte array
		byte [] dummyDefVal = {(byte)0x02,(byte)0x03};
		
		OAnswerNode oAnswerNode = (OAnswerNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		OAnswer oAnswer = new OAnswer();
		OAnswerSequenceType oAnswerSeqTyp = new OAnswerSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(OANSWER_MSCID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								
					String marketID = subFieldElems.get(OANSWER_MARKETID).getValue(varMap);
					String switchNo = subFieldElems.get(OANSWER_SWITCHNUMBER).getValue(varMap);
						
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
					oAnswerSeqTyp.setMscid(mscid);
				}else if(fieldElem.getFieldType().equals(OANSWER_MDN)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(OANSWER_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(OANSWER_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(OANSWER_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(OANSWER_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(OANSWER_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(OANSWER_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(OANSWER_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(OANSWER_ENCODINGSCHEME).getValue(varMap));
					
					MobileDirectoryNumber mdn = new MobileDirectoryNumber();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					mdn.setValue(digitTyp);
				
					oAnswerSeqTyp.setMobileDirectoryNumber(mdn);
				}else if(fieldElem.getFieldType().equals(OANSWER_BILLING_ID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String origMrktId = subFieldElems.get(OANSWER_ORIGINATING_MARKET_ID).getValue(varMap);
					String origSwitchNo = subFieldElems.get(OANSWER_ORIGINATING_SWITCH_NUM).getValue(varMap);
					String idNumber = subFieldElems.get(OANSWER_ID_NUMBER).getValue(varMap);
					String segmentCounter = subFieldElems.get(OANSWER_SEGMENT_COUNTER).getValue(varMap);
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
						
					oAnswerSeqTyp.setBillingID(billingid);
				}else if(fieldElem.getFieldType().equals(OANSWER_TRIGGER_TYPE)){
					String value = fieldElem.getValue(varMap);
					TriggerType.EnumType triggerTypeEnum = TriggerType.EnumType.valueOf(value);
					TriggerType trigTyp = new TriggerType();
					trigTyp.setValue(triggerTypeEnum);
					oAnswerSeqTyp.setTriggerType(trigTyp);
				}else if(fieldElem.getFieldType().equals(OANSWER_MSID)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems = fieldElem.getSubFieldElements();
								
					String IMSI = subFieldElems.get(OANSWER_IMSI).getValue(varMap);
					String mobileIdentificationNumber = subFieldElems.get(OANSWER_MOBILE_IDENTIFICATION_NUM).getValue(varMap);
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
						oAnswerSeqTyp.setMsid(msid);
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
						oAnswerSeqTyp.setMsid(msid);
					}
				}else if(fieldElem.getFieldType().equals(OANSWER_TIME_OF_DAY)){
					String todValue = fieldElem.getValue(varMap);
					TimeOfDay tod = new TimeOfDay();
					tod.setValue(Long.parseLong(todValue));
					oAnswerSeqTyp.setTimeOfDay(tod);
				}else if(fieldElem.getFieldType().equals(OANSWER_TIME_DATE_OFFSET)){
					//setting dummy val as NONASNdatatype are not implemented yet
					String value = fieldElem.getValue(varMap);
					byte [] byteVal = Helper.hexStringToByteArray(value);
					TimeDateOffset tymDatOffSet = new TimeDateOffset();
					tymDatOffSet.setValue(byteVal);
					oAnswerSeqTyp.setTimeDateOffset(tymDatOffSet);
				}
			}
		}
		
		oAnswer.setValue(oAnswerSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(oAnswer);
		opCode.add(WinOpCodes.O_ANS);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] oanswer = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for OAnswerHandler--> Got oanswer byte array:: "+Util.formatBytes(oanswer));
		
		if(logger.isDebugEnabled())
			logger.debug("OAnswerHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("OAnswerHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		simCpb.setDialogId(dialogueId);
		simCpb.setTcap(true);
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("OAnswerHandler processNode()-->OAnswer byte array generated creating reqEvent["+oanswer+"]");
		
		byte[] operationCode = {WinOpCodes.O_ANS_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, oanswer));
		ire.setClassType(OANSWER_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("OAnswerHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending oanswer component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("OAnswerHandler processNode()-->component send");
		if(oAnswerNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("OAnswerHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.OANSWER);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), oAnswerNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("OAnswerHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on oanswer::"+oAnswerNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on oanswer::"+oAnswerNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving oanswer processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OAnswerHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("OAnswerHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/

		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		OAnswer oanswer = null;
		try {
			byte[] paramoanswer = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for OAnswerHandler--> starting first level decoding on oanswer bytes:: "
								+ Util.formatBytes(paramoanswer));
			oanswer = (OAnswer) WinOperationsCoding.decodeOperation(paramoanswer, inkIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (oanswer == null) {
			if (logger.isDebugEnabled())
				logger.debug("oanswer is received null in processReceivedMessage() in OAnswerHandler");
			return false;
		}

		OAnswerSequenceType oanswerSeqTyp = oanswer.getValue();
		NonASNBillingID billingId = null;
		NonASNDigitsType digitType = null;
		NonASNMSCID mscid = null;
		
		if(oanswerSeqTyp.getTriggerType()!=null){
			TriggerType.EnumType trigTyp = oanswerSeqTyp.getTriggerType().getValue();
			logger.debug("values of Trigger Type Enum is : "+trigTyp);
		}
		
		
		try{
			if(oanswerSeqTyp.getBillingID()!=null){
				billingId = NonASNBillingID.decodeBillingID(oanswerSeqTyp.getBillingID().getValue());
				logger.debug("value of billing ID attributes are "+billingId.getIdNo()+" "+billingId.getOriginatingMarketID()+" "+billingId.getOriginatingSwitchNo()+" "+billingId.getSegmentCounter());
			}
			if(oanswerSeqTyp.isMobileDirectoryNumberPresent()){
				digitType = NonASNDigitsType.decodeDigits(oanswerSeqTyp.getMobileDirectoryNumber().getValue().getValue());
				logger.debug("value of MobileDirNumber are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
			if(oanswerSeqTyp.isMSCIdentificationNumberPresent()){
				mscid = NonASNMSCID.decodeMSCID(oanswerSeqTyp.getMscid().getValue());
				logger.debug("values of mscid are "+mscid.getMarketID()+" "+mscid.getSwitchNo());
			}
			if(oanswerSeqTyp.isMsidPresent()){
				logger.debug("vaue of msid is: Imsi Selected = "+oanswerSeqTyp.getMsid().isImsiSelected()+"MobileIdentificationNumber = "+oanswerSeqTyp.getMsid().isMobileIdentificationNumberSelected());
			}
			if(oanswerSeqTyp.getTimeOfDay()!=null){
				logger.debug("value of time of day is : "+oanswerSeqTyp.getTimeOfDay().getValue());
			}
			
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for OAnswerHandler leaving with status true");
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for OAnswerHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.OANSWER))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a oanswer Node");
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
				logger.debug("OAnswerHandler validateMessage() isValid::["
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