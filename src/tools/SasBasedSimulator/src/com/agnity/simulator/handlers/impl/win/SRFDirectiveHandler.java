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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SRFDIRECTIVENode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.AnnouncementCode;
import com.agnity.win.asngenerated.AnnouncementList;
import com.agnity.win.asngenerated.DigitCollectionControl;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.ExecuteScript;
import com.agnity.win.asngenerated.MobileDirectoryNumber;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.SRFDirective;
import com.agnity.win.asngenerated.ScriptArgument;
import com.agnity.win.asngenerated.ScriptName;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.asngenerated.SRFDirective.SRFDirectiveSequenceType;
import com.agnity.win.asngenerated.SeizeResource.SeizeResourceSequenceType;
import com.agnity.win.datatypes.NonASNAnnouncementCode;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class SRFDirectiveHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(SRFDirectiveHandler.class);
	private static Handler handler;

	private static final String SRFDIRECTIVE_ANNOUNCEMENT_LIST = "announcementList".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE1_TONE_ENUM = "announcementCode1_ToneEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE1_CLASS_ENUM = "announcementCode1_ClassEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM = "announcementCode1_StdAnnoucementEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT = "announcementCode1_CstmAnnoucement".toLowerCase();
	
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE2_TONE_ENUM = "announcementCode2_ToneEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE2_CLASS_ENUM = "announcementCode2_ClassEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM = "announcementCode2_StdAnnoucementEnum".toLowerCase();
	private static final String SRFDIRECTIVE_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT = "announcementCode2_CstmAnnoucement".toLowerCase();
	
	//private static final String SRFDIRECTIVE_DIG_COLLCTN_CNTRL = "digitCollectionControl";
	//private static final String SRFDIRECTIVE_EXEC_SCRIPT = "executeScript";
	
	private static final String SRFDIRECTIVE_MOB_DIR_NUMBR = "MobileDirectoryNumber".toLowerCase();
	private static final String SRFDIRECTIVE_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String SRFDIRECTIVE_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String SRFDIRECTIVE_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String SRFDIRECTIVE_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String SRFDIRECTIVE_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String SRFDIRECTIVE_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String SRFDIRECTIVE_NUMPLAN = "numPlan".toLowerCase();
	private static final String SRFDIRECTIVE_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
	
	private static final int SRFDirective_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (SRFDirectiveHandler.class) {
				if (handler == null) {
					handler = new SRFDirectiveHandler();
				}
			}
		}
		return handler;
	}

	private SRFDirectiveHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside SRFDirective handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.SRFDIRECTIVE))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		SRFDIRECTIVENode SRFDirectiveNode = (SRFDIRECTIVENode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		SRFDirective SRFDirective = new SRFDirective();
		SRFDirectiveSequenceType SRFDirectiveSeqTyp = new SRFDirectiveSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(SRFDIRECTIVE_ANNOUNCEMENT_LIST)){
					 //String value = fieldElem.getValue(varMap);
					 Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					 					 
					 AnnouncementList announceMentLst = new AnnouncementList();
					 AnnouncementCode annuncCode1 = new AnnouncementCode();
					 AnnouncementCode annuncCode2 = new AnnouncementCode();
					 ToneEnum tone = ToneEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE1_TONE_ENUM).getValue(varMap));
					 ClassEnum classType = ClassEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE1_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement = StdAnnoucementEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM).getValue(varMap));
					 byte cstmAnnouncmnt1 = Byte.parseByte(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT).getValue(varMap));
					 
					 ToneEnum tone2 = ToneEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE2_TONE_ENUM).getValue(varMap));
					 ClassEnum classType2 = ClassEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE2_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement2 = StdAnnoucementEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM).getValue(varMap));
					 byte cstmAnnouncmnt2 = Byte.parseByte(subFieldElems.get(SRFDIRECTIVE_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT).getValue(varMap));
					 
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
					 SRFDirectiveSeqTyp.setAnnouncementList(announceMentLst);
				 }else if(fieldElem.getFieldType().equals(SRFDIRECTIVE_MOB_DIR_NUMBR)){

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(SRFDIRECTIVE_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(SRFDIRECTIVE_ENCODINGSCHEME).getValue(varMap));
					
					MobileDirectoryNumber mobDirNumbr = new MobileDirectoryNumber();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					mobDirNumbr.setValue(digitTyp);
					SRFDirectiveSeqTyp.setMobileDirectoryNumber(mobDirNumbr);															 
					
				 }
			}
		}
		
		
		SRFDirective.setValue(SRFDirectiveSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(SRFDirective);
		opCode.add(WinOpCodes.SRF_DIR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] SRFDir = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for SRFDirectiveHandler--> Got SRFDirective byte array:: "+Util.formatBytes(SRFDir));
		
//		if(logger.isDebugEnabled())
//			logger.debug("SRFDirectiveHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("SRFDirectiveHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setWinReqDialogId(Constants.SRFDIRECTIVE,dialogueId);
//		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setWinReqInvokeId(Constants.SRFDIRECTIVE, invokeId);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveHandler processNode()-->SRFDirective byte array generated creating reqEvent["+SRFDir+"]");
		
		byte[] operationCode = {WinOpCodes.SRF_DIR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, SRFDir));
		ire.setClassType(SRFDirective_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending SRFDirective component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveHandler processNode()-->component send");
		if(SRFDirectiveNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("SRFDirectiveHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.SRFDIRECTIVE);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), SRFDirectiveNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("SRFDirectiveHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on SRFDirective::"+SRFDirectiveNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on SRFDirective::"+SRFDirectiveNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving SRFDirective processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SRFDirectiveHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("SRFDirectiveHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/

		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		SRFDirective SRFDirective = null;
		int dialogId,invokeId;
		try {
			byte[] paramSRFDirective = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for SRFDirectiveHandler--> starting first level decoding on SRFDirective bytes:: "
								+ Util.formatBytes(paramSRFDirective));
			SRFDirective = (SRFDirective) WinOperationsCoding.decodeOperation(paramSRFDirective, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (SRFDirective == null) {
			if (logger.isDebugEnabled())
				logger.debug("SRFDirective is received null in processReceivedMessage() in SRFDirectiveHandler");
			return false;
		}

		SRFDirectiveSequenceType SRFDirectiveSeqTyp = SRFDirective.getValue();
		
		NonASNAnnouncementCode annuncmntCode1 = null;
		NonASNAnnouncementCode annuncmntCode2 = null;
		NonASNDigitsType digitType = null;
		try{
			if(SRFDirectiveSeqTyp.isAnnouncementListPresent()){
				if(SRFDirectiveSeqTyp.getAnnouncementList().getAnnouncementCode1()!=null){
					annuncmntCode1 = NonASNAnnouncementCode.decodeAnnouncementCode(SRFDirectiveSeqTyp.getAnnouncementList().getAnnouncementCode1().getValue());
					logger.debug("value of annuncmntCode1 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode1.getTone()+" "+annuncmntCode1.getClassType()+" "+annuncmntCode1.getStdAnnoucement());
				}
				if(SRFDirectiveSeqTyp.getAnnouncementList().getAnnouncementCode2()!=null){
					annuncmntCode2 = NonASNAnnouncementCode.decodeAnnouncementCode(SRFDirectiveSeqTyp.getAnnouncementList().getAnnouncementCode2().getValue());
					logger.debug("value of annuncmntCode2 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode2.getTone()+" "+annuncmntCode2.getClassType()+" "+annuncmntCode2.getStdAnnoucement());
				}
			}
			if(SRFDirectiveSeqTyp.isMobileDirectoryNumberPresent()){
				digitType = NonASNDigitsType.decodeDigits(SRFDirectiveSeqTyp.getMobileDirectoryNumber().getValue().getValue());
				logger.debug("value of MobDirNumbr are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SRFDirectiveHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.SRFDIRECTIVE, dialogId);
		simCpb.setWinReqInvokeId(Constants.SRFDIRECTIVE, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for SRFDirectiveHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.SRFDIRECTIVE))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a SRFDirective Node");
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
				logger.debug("SRFDirectiveHandler validateMessage() isValid::["
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