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
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;

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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SRFDIRECTIVERetResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.SRFDirectiveRes;
import com.agnity.win.asngenerated.SRFDirectiveRes.SRFDirectiveResSequenceType;
import com.agnity.win.datatypes.NonASNDigitsType;
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

public class SRFDirectiveRetResHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(SRFDirectiveRetResHandler.class);
	private static Handler handler;

	private static final String SRFDIRECTIVERETRES_DIGITS = "Digits".toLowerCase();
	private static final String SRFDIRECTIVERETRES_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String SRFDIRECTIVERETRES_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String SRFDIRECTIVERETRES_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String SRFDIRECTIVERETRES_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String SRFDIRECTIVERETRES_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String SRFDIRECTIVERETRES_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String SRFDIRECTIVERETRES_NUMPLAN = "numPlan".toLowerCase();
	private static final String SRFDIRECTIVERETRES_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
	
	//private static final String SRFDirectiveRetResRETRES_SCRPT_RES = "ScriptResult";
	
	private static final int SRFDirectiveRetRes_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (SRFDirectiveRetResHandler.class) {
				if (handler == null) {
					handler = new SRFDirectiveRetResHandler();
				}
			}
		}
		return handler;
	}

	private SRFDirectiveRetResHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside SRFDirectiveRetRes handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.SRFDIRECTIVE_RET_RES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		SRFDIRECTIVERetResNode SRFDirectiveRetResNode = (SRFDIRECTIVERetResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		SRFDirectiveRes SRFDirectiveRetResRes = new SRFDirectiveRes();
		SRFDirectiveResSequenceType SRFDirectiveRetResResSeqTyp = new SRFDirectiveResSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(SRFDIRECTIVERETRES_DIGITS)){

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(SRFDIRECTIVERETRES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(SRFDIRECTIVERETRES_ENCODINGSCHEME).getValue(varMap));
					
					Digits digits = new Digits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					digits.setValue(digitTyp);
					SRFDirectiveRetResResSeqTyp.setDigits(digits);															 
					
				 }
			}
		}
		
		
		SRFDirectiveRetResRes.setValue(SRFDirectiveRetResResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(SRFDirectiveRetResRes);
		opCode.add(WinOpCodes.SRF_DIR);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] SRFDir = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for SRFDirectiveRetResHandler--> Got SRFDirectiveRetRes byte array:: "+Util.formatBytes(SRFDir));
		
//		if(logger.isDebugEnabled())
//			logger.debug("SRFDirectiveRetResHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("SRFDirectiveRetResHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
//		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveRetResHandler processNode()-->SRFDirectiveRetRes byte array generated creating reqEvent["+SRFDir+"]");
		
		byte[] operationCode = {WinOpCodes.SRF_DIR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.SRFDIRECTIVE), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.SRFDIRECTIVE));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.SRFDIRECTIVE));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, SRFDir));
		//ire.setClassType(SRFDirectiveRetRes_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveRetResHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending SRFDirectiveRetRes component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("SRFDirectiveRetResHandler processNode()-->component send");
		if(SRFDirectiveRetResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("SRFDirectiveRetResHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.SRFDIRECTIVE_RET_RES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), SRFDirectiveRetResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("SRFDirectiveRetResHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on SRFDirectiveRetRes::"+SRFDirectiveRetResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on SRFDirectiveRetRes::"+SRFDirectiveRetResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving SRFDirectiveRetRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SRFDirectiveRetResHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("SRFDirectiveRetResHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		ResultIndEvent resIndEvent = (ResultIndEvent) message;
		SRFDirectiveRes SRFDirectiveRetRes = null;
		try {
			byte[] paramSRFDirectiveRetRes = resIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for SRFDirectiveRetResHandler--> starting first level decoding on SRFDirectiveRetRes bytes:: "
								+ Util.formatBytes(paramSRFDirectiveRetRes));
			SRFDirectiveRetRes = (SRFDirectiveRes) WinOperationsCoding.decodeOperation(paramSRFDirectiveRetRes, resIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (SRFDirectiveRetRes == null) {
			if (logger.isDebugEnabled())
				logger.debug("SRFDirectiveRetRes is received null in processReceivedMessage() in SRFDirectiveRetResHandler");
			return false;
		}

		SRFDirectiveResSequenceType SRFDirectiveRetResSeqTyp = SRFDirectiveRetRes.getValue();
				
		NonASNDigitsType digitType = null;
		try{
					
			if(SRFDirectiveRetResSeqTyp.isDigitsPresent()){
				digitType = NonASNDigitsType.decodeDigits(SRFDirectiveRetResSeqTyp.getDigits().getValue().getValue());
				
				logger.debug("value of digits are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SRFDirectiveRetResHandler leaving with status true");
		simCpb.removeDialogId(Constants.SRFDIRECTIVE);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for SRFDirectiveRetResHandler");
				
		if (!(message instanceof ResultIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.SRFDIRECTIVE_RET_RES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a SRFDirectiveRetRes Node");
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

		ResultIndEvent receivedInvoke = (ResultIndEvent) message;
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
//			if(receivedInvoke.getDialogueId()!=simCpb.getWinReqDialogId(Constants.SRFDIRECTIVE)){
//				isValid = false;
//				logger.debug("dialogue id for response is not matching with request,so call failed ");
//			}
			if(receivedInvoke.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.SRFDIRECTIVE)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if (logger.isDebugEnabled())
				logger.debug("SRFDirectiveRetResHandler validateMessage() isValid::["
						+ isValid + "]  Expected opcode::["
						+ tcapNode.getOpCodeString() + "] Actual Opcode::["
						+ opCodeStr + "] Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");

		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("ParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}

}