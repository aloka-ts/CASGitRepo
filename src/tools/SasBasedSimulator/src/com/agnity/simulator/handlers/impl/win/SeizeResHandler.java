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
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SeizeResourceRes;
import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.asngenerated.SeizeResource.SeizeResourceSequenceType;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class SeizeResHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(SeizeResHandler.class);
	private static Handler handler;

	private static final String SEIZERES_PREF_LANG_INDICATR = "preferredLanguageIndicator".toLowerCase();
	private static final String SEIZERES_SPCL_RES = "SpecializedResource".toLowerCase();
	
	private static final int SEIZERES_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (SeizeResHandler.class) {
				if (handler == null) {
					handler = new SeizeResHandler();
				}
			}
		}
		return handler;
	}

	private SeizeResHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside SEIZERES handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.SEIZERES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		SeizeResNode SEIZERESNode = (SeizeResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		SeizeResource SEIZERES = new SeizeResource();
		SeizeResourceSequenceType seizeResSeqTyp = new SeizeResourceSequenceType();	
						
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(SEIZERES_PREF_LANG_INDICATR)){
					 String value = fieldElem.getValue(varMap);
					 PreferredLanguageEnum prefLangEnum = PreferredLanguageEnum.valueOf(value);
					 
					 PreferredLanguageIndicator prefLangInd = new PreferredLanguageIndicator();
					 LinkedList<PreferredLanguageEnum> l0 = new LinkedList<PreferredLanguageEnum>();
					 l0.add(prefLangEnum);
					 byte[] prefLanfIndVal = null;
					try {
						prefLanfIndVal = NonASNPreferredLanguageIndicator.encodePreferredLanguageIndicator(l0);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception while encoding preferredLanguageIndicator :"+e.toString());
					}
					 prefLangInd.setValue(prefLanfIndVal);
					 seizeResSeqTyp.setPreferredLanguageIndicator(prefLangInd);
				 }else if(fieldElem.getFieldType().equals(SEIZERES_SPCL_RES)){
					 String value = fieldElem.getValue(varMap);
					 SpecializedResource SpclRes = new SpecializedResource();
					 ResourceTypeEnum resTypEnum = ResourceTypeEnum.valueOf(value);
					 LinkedList<ResourceTypeEnum> l0 = new LinkedList<ResourceTypeEnum>();
					 l0.add(resTypEnum);
					 byte[] spclResVal = null;
					try {
						spclResVal = NonASNSpecializedResource.encodeResourceType(l0);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception while encoding SpecializedResource :"+e.toString());
					}
					 SpclRes.setValue(spclResVal);
					 seizeResSeqTyp.setSpecializedResource(SpclRes);
				 }
			}
		}
		
		
		SEIZERES.setValue(seizeResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(SEIZERES);
		opCode.add(WinOpCodes.SR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] seizeRes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for SEIZERESHandler--> Got seizeRes byte array:: "+Util.formatBytes(seizeRes));
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESHandler processNode()-->Got dialog ID["+dialogueId+"]");
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.SEIZERES,dialogueId);
		simCpb.setWinReqInvokeId(Constants.SEIZERES, invokeId);
		simCpb.setTcap(true);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESHandler processNode()-->SeizeRes byte array generated creating reqEvent["+seizeRes+"]");
		
		byte[] operationCode = {WinOpCodes.SR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), dialogueId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, seizeRes));
		ire.setClassType(SEIZERES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending seizeRes component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("SEIZERESHandler processNode()-->component send");
		if(SEIZERESNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("SEIZERESHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.SEIZERES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), SEIZERESNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("SEIZERESHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on seizeRes::"+SEIZERESNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on seizeRes::"+SEIZERESNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving seizeRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SEIZERESHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("SEIZERESHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
	
		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		SeizeResource SEIZERES = null;
		int dialogId,invokeId;
		try {
			byte[] paramSEIZERES = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for SEIZERESHandler--> starting first level decoding on SEIZERES bytes:: "
								+ Util.formatBytes(paramSEIZERES));
			SEIZERES = (SeizeResource) WinOperationsCoding.decodeOperation(paramSEIZERES, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (SEIZERES == null) {
			if (logger.isDebugEnabled())
				logger.debug("SEIZERES is received null in processReceivedMessage() in SEIZERESHandler");
			return false;
		}

		SeizeResourceSequenceType seizeResSeqTyp = SEIZERES.getValue();
		NonASNPreferredLanguageIndicator prefLangIndicatr = null;
		NonASNSpecializedResource spclResIndicatr = null;
				
		try{
			if(seizeResSeqTyp.isPreferredLanguageIndicatorPresent()){
				prefLangIndicatr = NonASNPreferredLanguageIndicator.decodePreferredLanguageIndicator(seizeResSeqTyp.getPreferredLanguageIndicator().getValue());
				logger.debug("value of PreferredLanguageIndicator i.e. PreferredLanguageEnum is "+prefLangIndicatr.getPreferredLanguage().get(0).toString());
			}
			if(seizeResSeqTyp.isSpecializedResourcePresent()){
				spclResIndicatr = NonASNSpecializedResource.decodeResourceType(seizeResSeqTyp.getSpecializedResource().getValue());
				logger.debug("value of SpecializedResource i.e. ResourceTypeEnum is "+spclResIndicatr.getResourceType().get(0).toString());
			}
			
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
					
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SEIZERESHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.SEIZERES, dialogId);
		simCpb.setWinReqInvokeId(Constants.SEIZERES, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for SEIZERESHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.SEIZERES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a seizeRes Node");
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
				logger.debug("SEIZERESHandler validateMessage() isValid::["
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