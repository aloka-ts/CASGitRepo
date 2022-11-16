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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResRespNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SeizeResourceRes;
import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.asngenerated.SeizeResource.SeizeResourceSequenceType;
import com.agnity.win.asngenerated.SeizeResourceRes.SeizeResourceResSequenceType;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class SeizeResRespHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(SeizeResRespHandler.class);
	private static Handler handler;

	private static final String SEIZERESRESP_DSTN_DIGIT = "destinationDigits".toLowerCase();
	private static final String SEIZERESRESP_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String SEIZERESRESP_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String SEIZERESRESP_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String SEIZERESRESP_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String SEIZERESRESP_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String SEIZERESRESP_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String SEIZERESRESP_NUMPLAN = "numPlan".toLowerCase();
	private static final String SEIZERESRESP_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
	
	private static final int SEIZERESRESP_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (SeizeResRespHandler.class) {
				if (handler == null) {
					handler = new SeizeResRespHandler();
				}
			}
		}
		return handler;
	}

	private SeizeResRespHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside SEIZERESRESP handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.SEIZERESRESP))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		SeizeResRespNode SEIZERESRESPNode = (SeizeResRespNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		SeizeResourceRes SEIZERESRESP = new SeizeResourceRes();
		SeizeResourceResSequenceType seizeResRespSeqTyp = new SeizeResourceResSequenceType();	
						
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(SEIZERESRESP_DSTN_DIGIT)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(SEIZERESRESP_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(SEIZERESRESP_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(SEIZERESRESP_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(SEIZERESRESP_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(SEIZERESRESP_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(SEIZERESRESP_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(SEIZERESRESP_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(SEIZERESRESP_ENCODINGSCHEME).getValue(varMap));
						
					DestinationDigits dstnDigits = new DestinationDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
							  digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
						 } catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding digits :"+e.toString());
						}
					digitTyp.setValue(digitVal);
					dstnDigits.setValue(digitTyp);					 
					seizeResRespSeqTyp.setDestinationDigits(dstnDigits);
				 }
			}
		}
		
		
		SEIZERESRESP.setValue(seizeResRespSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(SEIZERESRESP);
		opCode.add(WinOpCodes.SR);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] seizeResResp = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for SEIZERESRESPHandler--> Got seizeRes byte array:: "+Util.formatBytes(seizeResResp));
		
//		if(logger.isDebugEnabled())
//			logger.debug("SEIZERESRESPHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("SEIZERESRESPHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
//		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESRESPHandler processNode()-->SeizeResResp byte array generated creating reqEvent["+seizeResResp+"]");
		
		byte[] operationCode = {WinOpCodes.SR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.SEIZERES), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.SEIZERES));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.SEIZERES));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, seizeResResp));
		//rre.setClassType(SEIZERESRESP_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESRESPHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending seizeResResp component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESRESPHandler processNode()-->component send");
		if(SEIZERESRESPNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("SEIZERESRESPHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.SEIZERESRESP);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), SEIZERESRESPNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("SEIZERESRESPHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on seizeRes::"+SEIZERESRESPNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on seizeRes::"+SEIZERESRESPNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving seizeResResp processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SEIZERESRESPHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("SEIZERESRESPHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
		//Iterator<Node> subElemIterator = subElem.iterator();
		ResultIndEvent resIndEvent = (ResultIndEvent) message;
		SeizeResourceRes SEIZERESRESP = null;
		try {
			byte[] paramSEIZERES = resIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for SEIZERESRESPHandler--> starting first level decoding on SEIZERESRESP bytes:: "
								+ Util.formatBytes(paramSEIZERES));
			SEIZERESRESP = (SeizeResourceRes) WinOperationsCoding.decodeOperation(paramSEIZERES, resIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (SEIZERESRESP == null) {
			if (logger.isDebugEnabled())
				logger.debug("SEIZERESRESP is received null in processReceivedMessage() in SEIZERESRESPHandler");
			return false;
		}

		SeizeResourceResSequenceType seizeResRespSeqTyp = SEIZERESRESP.getValue();
		NonASNDigitsType digitType = null;
		DestinationDigits digits = null;		
		
		try{
			digits = seizeResRespSeqTyp.getDestinationDigits();
			if(digits!=null){
				digitType = NonASNDigitsType.decodeDigits(digits.getValue().getValue());
				logger.debug("value of digits are "+digitType.getAddrSignal()+" "+digitType.getTypeOfDigits()+" "+
						digitType.getNatOfNumInd()+" "+digitType.getNatOfNumAvlInd()+" "+digitType.getNatOfNumPresInd()+
						" "+digitType.getNatOfNumScrnInd()+" "+digitType.getNumberingPlan()+" "+digitType.getEncoding());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
						
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SEIZERESHandler leaving with status true");
		simCpb.removeDialogId(Constants.SEIZERES);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for SEIZERESRespHandler");

		if (!(message instanceof ResultIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not a ResultIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.SEIZERESRESP))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a seizeResResp Node");
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
			if(receivedInvoke.getDialogueId()!=simCpb.getWinReqDialogId(Constants.SEIZERES)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			if(receivedInvoke.getInvokeId()!=simCpb.getWinReqInvokeId(Constants.SEIZERES)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
			if (logger.isDebugEnabled())
				logger.debug("SEIZERESRESPHandler validateMessage() isValid::["
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