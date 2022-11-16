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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ConnResNode;

import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.CarrierDigits;
import com.agnity.win.asngenerated.ConnectResource;
import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.RoutingDigits;
import com.agnity.win.asngenerated.ConnectResource.ConnectResourceSequenceType;
import com.agnity.win.datatypes.NonASNDigitsType;
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

public class ConnResHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(ConnResHandler.class);
	private static Handler handler;

	private static final String CONNRES_CARRIER_DIGITS = "carrierDigits".toLowerCase();
	private static final String CONNRES_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String CONNRES_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String CONNRES_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String CONNRES_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String CONNRES_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String CONNRES_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String CONNRES_NUMPLAN = "numPlan".toLowerCase();
	private static final String CONNRES_ENCODINGSCHEME = "EncodingScheme".toLowerCase();

	private static final String CONNRES_DESTINATION_DIGITS = "destinationDigits".toLowerCase();
	private static final String CONNRES_ROUTING_DIGITS = "routingDigits".toLowerCase();
	
	private static final int CONNRES_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (ConnResHandler.class) {
				if (handler == null) {
					handler = new ConnResHandler();
				}
			}
		}
		return handler;
	}

	private ConnResHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside CONNRES handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.CONNRES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		ConnResNode CONNRESNode = (ConnResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		ConnectResource CONNRES = new ConnectResource();		
		ConnectResourceSequenceType connResSeqTyp = new ConnectResourceSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(CONNRES_CARRIER_DIGITS)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(CONNRES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CONNRES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CONNRES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CONNRES_ENCODINGSCHEME).getValue(varMap));
					
					CarrierDigits carrierDigits = new CarrierDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType inside carrierDigits"+e.toString());
					}
					digitTyp.setValue(digitVal);
					carrierDigits.setValue(digitTyp);
				
					connResSeqTyp.setCarrierDigits(carrierDigits);
				}else if(fieldElem.getFieldType().equals(CONNRES_DESTINATION_DIGITS)){
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(CONNRES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CONNRES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CONNRES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CONNRES_ENCODINGSCHEME).getValue(varMap));
					
					DestinationDigits destDigits = new DestinationDigits();
					DigitsType digType = new DigitsType();
					byte[] digTypeVal =null;
					try {
						digTypeVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType inside destinationDigits"+e.toString());
					}
					digType.setValue(digTypeVal);
					
					destDigits.setValue(digType);
					connResSeqTyp.setDestinationDigits(destDigits);
				}else if(fieldElem.getFieldType().equals(CONNRES_ROUTING_DIGITS)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(CONNRES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CONNRES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CONNRES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CONNRES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CONNRES_ENCODINGSCHEME).getValue(varMap));
										
					DigitsType digType = new DigitsType();
					byte[] digTypeVal =null;
					try {
						digTypeVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType inside destinationDigits"+e.toString());
					}
					digType.setValue(digTypeVal);
					
					RoutingDigits routingDigits = new RoutingDigits();
					routingDigits.setValue(digType);
					connResSeqTyp.setOutingDigits(routingDigits);
					
				}
			}
		}
		
		CONNRES.setValue(connResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(CONNRES);
		opCode.add(WinOpCodes.CR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] connRes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for CONNRESHandler--> Got conRes byte array:: "+Util.formatBytes(connRes));
		
//		if(logger.isDebugEnabled())
//			logger.debug("CONNRESHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("CONNRESHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
		int invokeId = simCpb.incrementAndGetInvokeId();
		//InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("CONNRESHandler processNode()-->ConRes byte array generated creating reqEvent["+connRes+"]");
		
		byte[] operationCode = {WinOpCodes.CR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, connRes));
		ire.setClassType(CONNRES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("CONNRESHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending connRes component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("CONNRESHandler processNode()-->component send");
		if(CONNRESNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("CONNRESHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.CONNRES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), CONNRESNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("CONNRESHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on connRes::"+CONNRESNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on connRes::"+CONNRESNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving connRes processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CONNRESHandler");

		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		List<Node> subElem = node.getSubElements();
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("CONNRESHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/

		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		ConnectResource CONNRES = null;
		try {
			byte[] paramCONNRES = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for CONNRESHandler--> starting first level decoding on CONNRES bytes:: "
								+ Util.formatBytes(paramCONNRES));
			CONNRES = (ConnectResource) WinOperationsCoding.decodeOperation(paramCONNRES, inkIndEvent);
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (CONNRES == null) {
			if (logger.isDebugEnabled())
				logger.debug("CONNRES is received null in processReceivedMessage() in CONNRESHandler");
			return false;
		}

		ConnectResourceSequenceType connResSeqTyp = CONNRES.getValue();
		NonASNDigitsType digitTypeDestnDig = null;
		NonASNDigitsType digitTypeCarrDig = null;
		NonASNDigitsType digitTypeRoutngnDig = null;
		
		try{
			if(connResSeqTyp.getDestinationDigits()!=null){
				digitTypeDestnDig = NonASNDigitsType.decodeDigits(connResSeqTyp.getDestinationDigits().getValue().getValue());
				logger.debug("value of destination digits are "+digitTypeDestnDig.getAddrSignal()+" "+digitTypeDestnDig.getTypeOfDigits()+" "+
						digitTypeDestnDig.getNatOfNumInd()+" "+digitTypeDestnDig.getNatOfNumAvlInd()+" "+digitTypeDestnDig.getNatOfNumPresInd()+
						" "+digitTypeDestnDig.getNatOfNumScrnInd()+" "+digitTypeDestnDig.getNumberingPlan()+" "+digitTypeDestnDig.getEncoding());
			}
			if(connResSeqTyp.isCarrierDigitsPresent()){
				digitTypeCarrDig = NonASNDigitsType.decodeDigits(connResSeqTyp.getCarrierDigits().getValue().getValue());
				logger.debug("value of carrier digits are "+digitTypeCarrDig.getAddrSignal()+" "+digitTypeCarrDig.getTypeOfDigits()+" "+
						digitTypeCarrDig.getNatOfNumInd()+" "+digitTypeCarrDig.getNatOfNumAvlInd()+" "+digitTypeCarrDig.getNatOfNumPresInd()+
						" "+digitTypeCarrDig.getNatOfNumScrnInd()+" "+digitTypeCarrDig.getNumberingPlan()+" "+digitTypeCarrDig.getEncoding());
			}
			if(connResSeqTyp.isOutingDigitsPresent()){
				digitTypeRoutngnDig = NonASNDigitsType.decodeDigits(connResSeqTyp.getOutingDigits().getValue().getValue());
				logger.debug("value of routing digits are "+digitTypeRoutngnDig.getAddrSignal()+" "+digitTypeRoutngnDig.getTypeOfDigits()+" "+
						digitTypeRoutngnDig.getNatOfNumInd()+" "+digitTypeRoutngnDig.getNatOfNumAvlInd()+" "+digitTypeRoutngnDig.getNatOfNumPresInd()+
						" "+digitTypeRoutngnDig.getNatOfNumScrnInd()+" "+digitTypeRoutngnDig.getNumberingPlan()+" "+digitTypeRoutngnDig.getEncoding());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
		}
				
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CONNRESHandler leaving with status true");
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for CONNRESHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.CONNRES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a conRes Node");
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
				logger.debug("CONNRESHandler validateMessage() isValid::["
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