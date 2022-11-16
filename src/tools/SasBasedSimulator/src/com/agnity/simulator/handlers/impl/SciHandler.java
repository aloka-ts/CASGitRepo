package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.child.ValidateElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SciNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.SCIBillingChargingCharacteristics;
import com.genband.inap.asngenerated.SendChargingInformationArg;
import com.genband.inap.asngenerated.TTCNOSpecificParameterSCIBCC;
import com.genband.inap.asngenerated.TTCNOSpecificParametersSCIBCC;
import com.genband.inap.asngenerated.TTCSCIBillingChargingCharacteristics;
import com.genband.inap.asngenerated.TTCSpecificSCIBCC;
import com.genband.inap.datatypes.SCIBillingChargingChar;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class SciHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(SciHandler.class);
	private static Handler handler;


	private static final int SCI_CLASS = 2;

	//fields
	private static final String SCI_FIELD_PARTY_TO_CHARGE = "partyToCharge".toLowerCase();
	private static final String SCI_FIELD_BILLING_CHARGING_CHARACTERSTICS = "SCIBillingChargingCharacteristics".toLowerCase();
	
	//enum
	private static final String SCI_SUBFIELD_LEG_ID = "legId".toLowerCase();
	//TTCSciBCC
	private static final String SCI_SUBFIELD_NO_CHARGE_IND = "noChargeIndicator".toLowerCase();
	
	//Validate
	private static final String SCI_VALIDATE_PARTY_TO_CHARGE = "partyToCharge.legId.legType".toLowerCase();
	private static final String SCI_VALIDATE_SCI_BILLING_CHRGNG_CHARACTERISTICS = "sCIBillingChargingCharacteristics.ChargeIndicator".toLowerCase();
	private static final String SCI_VALIDATE_SCI_CHRGNG_INFO_ARG = "SendChargingInformationArg".toLowerCase();
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (SciHandler.class) {
				if(handler ==null){
					handler = new SciHandler();
				}
			}
		}
		return handler;
	}

	private SciHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside SciHandler processNode()");

		if(!(node.getType().equals(Constants.SCI))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		SciNode sciNode = (SciNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;

		if(subElemIterator.hasNext()){
			ire =createSci(simCpb,subElemIterator);

		}

		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("SciHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending sci component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("SciHandler processNode()-->component send");
		//if last message generate dialog
		if(sciNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("SciHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),sciNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("SciHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on SCI::"+sciNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on SCI::"+sciNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving SciHandler processNode() with status true");
		return true;

	}

	private InvokeReqEvent createSci(SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator) {

		SendChargingInformationArg sciArg = new SendChargingInformationArg();
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(SCI_FIELD_PARTY_TO_CHARGE)){

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String legIdstr = subFieldElems.get(SCI_SUBFIELD_LEG_ID).getValue(varMap);
					int legId= 2;//default is 2
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}

					
					LegID legIdField = new LegID();
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectSendingSideID(new LegType(legType));

					sciArg.setPartyToCharge(legIdField);
				}else if(fieldName.equals(SCI_FIELD_BILLING_CHARGING_CHARACTERSTICS)){
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					//read subfields value
					boolean noChargeInd= Boolean.parseBoolean(
							subFieldElems.get(SCI_SUBFIELD_NO_CHARGE_IND).getValue(varMap));
					
					TTCSpecificSCIBCC ttcSpecificSciBccVal = new TTCSpecificSCIBCC();
					ttcSpecificSciBccVal.setNoChargeIndicator(noChargeInd);
					
					TTCNOSpecificParameterSCIBCC ttcNoSpecificParameter = new TTCNOSpecificParameterSCIBCC();
					ttcNoSpecificParameter.selectTTCSpecificSCIBCC(ttcSpecificSciBccVal);
					
					Collection<TTCNOSpecificParameterSCIBCC> ttcNoSpecificParamsColl= new LinkedList<TTCNOSpecificParameterSCIBCC>();
					ttcNoSpecificParamsColl.add(ttcNoSpecificParameter);
					
					TTCNOSpecificParametersSCIBCC ttcNoSpcfcParamSciBcc = new TTCNOSpecificParametersSCIBCC();
					ttcNoSpcfcParamSciBcc.setValue(ttcNoSpecificParamsColl);
					
					TTCSCIBillingChargingCharacteristics  ttcSciBillingChargingCharacteristics = 
						new TTCSCIBillingChargingCharacteristics();
					ttcSciBillingChargingCharacteristics.selectTTCNOSpecificParametersSCIBCC(ttcNoSpcfcParamSciBcc);
					
					byte[] billingChargingChar = null;
					
					try {
						billingChargingChar= SCIBillingChargingChar.encodeSciBillingChar(ttcSciBillingChargingCharacteristics);
					} catch (Exception e) {
						logger.error("Error encoding billingChargingChar",e);
						return null;
					}
					SCIBillingChargingCharacteristics sciBillingChargingChar = 
						new SCIBillingChargingCharacteristics(billingChargingChar);
					sciArg.setSCIBillingChargingCharacteristics(sciBillingChargingChar);
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("SciHandler processNode()-->fields read...saving extension if present");
		//no extension for DFc

		//getting SCI byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(sciArg);
		opCode.add(InapOpCodes.SCI);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enscicoding SCI to byte array",e);
			return null;
		}
		byte[] sci = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SCIHandler--> Got SCI byte array:: "+Util.formatBytes(sci));

		//generate sci component req event
		byte[] sciOpCode = {0x2E} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, sciOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, sci));
		ire.setClassType(SCI_CLASS);

		return ire;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for SciHandler");

		//		SciNode sciNode = (SciNode) node;

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("SciHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;
		ValidateElem validateElem = null;
		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing SCI message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsSci;
		SendChargingInformationArg sci = null;

		try{
			parmsSci =invokeIndEvent.getParameters().getParameter();
			sci = (SendChargingInformationArg)InapOperationsCoding.decodeOperation(parmsSci, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(sci == null){
			logger.error("sci is null");
			return false;
		}

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					var = new Variable();
					var.setVarName(varName);
				}
				String varVal = null;



				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);

			}else if(subElem.getType().equals(Constants.VALIDATE)){
				validateElem =(ValidateElem) subElem;
				
				String fieldName = validateElem.getFieldName();
				
				if(fieldName.equals(SCI_VALIDATE_SCI_CHRGNG_INFO_ARG))
				{
					boolean status=validateSendChargingInformationArg(sci,validateElem,varMap);
					if(!status){
						SuiteLogger.getInstance().log("validate of SendChargingInformationArg failed; return false");
						return status;
					}else{
						SuiteLogger.getInstance().log("VALIDATE SUCCESS FOR ALL FIELDS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName());
					}
				}
			}//end if check for set elem
		}//end while loop on subelem

		if(logger.isDebugEnabled())
			logger.debug("Leave processRecievedMessage() for SciHandler");
		return true;
	}

	public boolean validateSendChargingInformationArg(SendChargingInformationArg sci,ValidateElem validateElem,Map<String, Variable> varMap){
		
		SCIBillingChargingCharacteristics scibillingChrgngCharectristics = sci.getSCIBillingChargingCharacteristics();
		LegID partytocharge = sci.getPartyToCharge();
		
		if(scibillingChrgngCharectristics==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ "scibillingChrgngCharectristics" +"] is null in SendChargingInformationArg");
			return false;
		}
		
		if(partytocharge==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ "partytocharge" +"] is null in SendChargingInformationArg");
			return false;
		}
		
		LegType sendingSideLegType =null;
		if(partytocharge.isSendingSideIDSelected()){
			sendingSideLegType=partytocharge.getSendingSideID();
		}
		
		if(validateElem.getSubFieldElements().isEmpty()){
			if(logger.isDebugEnabled())
			logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
				"] Fields are not found in CallFlow xml");
			return false;
		}
		
		for(Map.Entry<String, SubFieldElem> ent : validateElem.getSubFieldElements().entrySet())
		{
			String key = ent.getKey();
			String expectedVal = ent.getValue().getValue(varMap);
			if(key.equalsIgnoreCase(SCI_VALIDATE_SCI_BILLING_CHRGNG_CHARACTERISTICS))
			{
				Boolean expectedValChargeIndicator = Boolean.parseBoolean(ent.getValue().getValue(varMap));
				TTCSCIBillingChargingCharacteristics ttcSciBiillingChargingChar=null;
				try{
					ttcSciBiillingChargingChar = SCIBillingChargingChar.decodeSciBillingChar(scibillingChrgngCharectristics.getValue());
				}catch(Exception e)
				{
					if(logger.isDebugEnabled())
					{
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"]"+"Exception while decoding "+e.toString());
					}
				}
				if(!ttcSciBiillingChargingChar.isTTCNOSpecificParametersSCIBCCSelected()){
					logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::[ ttcSciBiillingChargingChar ] is not selected");
								
					return false;
				}
				TTCNOSpecificParametersSCIBCC ttcNospecificParams = ttcSciBiillingChargingChar.getTTCNOSpecificParametersSCIBCC();
				
				if(ttcNospecificParams==null){
					logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::[ ttcNospecificParams ] is not selected");
					return false;
				}
				
				Collection<TTCNOSpecificParameterSCIBCC> ttcNospecificParamList = ttcNospecificParams.getValue();
				TTCNOSpecificParameterSCIBCC ttcNospecificParam = ttcNospecificParamList.iterator().next();
				if(!ttcNospecificParam.isTTCSpecificSCIBCCSelected())
				{
					logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::[ ttcNospecificParam ] is not selected");
					return false;
				}
				TTCSpecificSCIBCC ttcSpecificSciBcc = ttcNospecificParam.getTTCSpecificSCIBCC();
				if(!ttcSpecificSciBcc.isNoChargeIndicatorPresent())
				{
					logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::[ ttcSpecificSciBcc ] is not selected");
					return false;
				}
				Boolean valfromMessg = ttcSpecificSciBcc.getNoChargeIndicator();
				if(valfromMessg==expectedValChargeIndicator){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ SCI_VALIDATE_SCI_BILLING_CHRGNG_CHARACTERISTICS +"] sCIBillingChargingCharacteristics Value matched Expected::["+expectedValChargeIndicator+
								"] Actual Value::["+valfromMessg);
					continue;
				}else{
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ SCI_VALIDATE_SCI_BILLING_CHRGNG_CHARACTERISTICS +"] sCIBillingChargingCharacteristics Value not matched Expected::["+expectedValChargeIndicator+
									"] Actual Value::["+valfromMessg);
					return false;
				}
			}
			if(key.equalsIgnoreCase(SCI_VALIDATE_PARTY_TO_CHARGE)){
				if(sendingSideLegType==null){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ SCI_VALIDATE_PARTY_TO_CHARGE +"] partytocharge legId  sendingSideLegType is null");
					return false;
				}
				if( (expectedVal.equals("1")  &&  (sendingSideLegType.getValue()[0]==(byte)0x01))  ||
						(expectedVal.equals("2")  &&  (sendingSideLegType.getValue()[0]==(byte)0x02))){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ SCI_VALIDATE_PARTY_TO_CHARGE +"] partytocharge legId   sendingSideLegType Value matched Expected::["+expectedVal+
								"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
					continue;							
				}else{
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ SCI_VALIDATE_PARTY_TO_CHARGE +"] partytocharge legId   sendingSideLegType Value not matched Expected::["+expectedVal+
							"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
					return false;
				}
			}
		}// end of for loop
		//reaches here only if attributes matches
		return true;
	}
	
	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for SciHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.SCI) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a SCI Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		InvokeIndEvent receivedInvoke = (InvokeIndEvent)message; 
		Operation opr;
		byte[] opCode ;
		String opCodeStr= null ;
		boolean isValid= false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if( (opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase())) && (dialogType== tcapNode.getDialogType()) ){
				isValid= true;
			}	
			if(logger.isDebugEnabled())
				logger.debug("SciHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		} 

		return isValid;
	}



}
