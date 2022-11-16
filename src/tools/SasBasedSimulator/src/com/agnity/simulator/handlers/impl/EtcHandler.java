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
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EtcNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.AssistingSSPIPRoutingAddress;
import com.genband.inap.asngenerated.CorrelationID;
import com.genband.inap.asngenerated.CriticalityType;
import com.genband.inap.asngenerated.CriticalityType.EnumType;
import com.genband.inap.asngenerated.Digits;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionExtension;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.ScfID;
import com.genband.inap.asngenerated.TtcCarrierInformation;
import com.genband.inap.asngenerated.TtcCarrierInformationTransfer;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.GenericDigits;
import com.genband.inap.datatypes.GenericNumber;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.DigitCatEnum;
import com.genband.inap.enumdata.EncodingSchemeEnum;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.NumQualifierIndEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class EtcHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(EtcHandler.class);
	private static Handler handler;
	
	private static final String ETC_SET_ASSISTING_SSP_ROUT_ADD= "assistingSSPIPRoutingAddress".toLowerCase();
	private static final String ETC_SET_CORR_ID= "correlationID".toLowerCase();
	
	private static final int ETC_CLASS = 2;
	
	//Fileds
	private static final String ETC_FIELD_ASSISTING_SSP_ROUT_ADD = "assistingSSPIPRoutingAddress".toLowerCase();
	private static final Object ETC_FIELD_CORR_ID ="correlationID".toLowerCase();
	private static final Object ETC_FIELD_SCF_ID = "scfId".toLowerCase();
	private static final Object ETC_FIELD_CARRIER_INFO_TRANSFER = "ttcCarrierInformationTransfer".toLowerCase();
	private static final Object ETC_FIELD_PARTY_TO_CONNECT = "partyToConnect".toLowerCase();
	
	//enums
	//assisting sspip
	private static final String ETC_ENUM_NUM_QUALIFIER_IND_ENUM = "NumQualifierIndEnum".toLowerCase();
	private static final String ETC_ENUM_NATURE_OF_ADD = "NatureOfAddEnum".toLowerCase();
	private static final String ETC_ENUM_NUM_PLAN = "NumPlanEnum".toLowerCase();
	private static final String ETC_ENUM_SCREENING_ENUM = "ScreeningIndEnum".toLowerCase();
	private static final String ETC_ENUM_NUM_INCMPLT__ENUM = "NumIncmpltEnum".toLowerCase();
	private static final String ETC_ENUM_ADRS_PRESNT_RESTD_ENUM = "AddPrsntRestEnum".toLowerCase();
	
	//corrID
	private static final String ETC_ENUM_ENCODING_SCHEME = "EncodingSchemeEnum".toLowerCase();
	private static final String ETC_ENUM_DIGIT_CAT_ENUM = "DigitCatEnum".toLowerCase();
	
	//scfID
	private static final String ETC_ENUM_SPC_IND_ENUM = "SPCIndicatorEnum".toLowerCase();
	private static final String ETC_ENUM_SSN_IND_ENUM = "SSNIndicatorEnum".toLowerCase();
	private static final String ETC_ENUM_GT_IND_ENUM = "GTIndicatorEnum".toLowerCase();
	private static final String ETC_ENUM_ROUTING_IND_ENUM = "RoutingIndicatorEnum".toLowerCase();
	
	private static final String ETC_SUBFIELD_ZONE_PC = "zone_PC".toLowerCase();
	private static final String ETC_SUBFIELD_NET_PC = "net_PC".toLowerCase();
	private static final String ETC_SUBFIELD_SP_PC = "sp_PC".toLowerCase();
	private static final String ETC_SUBFIELD_SSN = "ssn".toLowerCase();
	
	//party to connect
	private static final String ETC_SUBFIELD_LEG_ID = "legId".toLowerCase();
	//carrierinfo transfer
	private static final String ETC_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String ETC_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String ETC_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();
		
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (EtcHandler.class) {
				if(handler ==null){
					handler = new EtcHandler();
				}
			}
		}
		return handler;
	}

	private EtcHandler(){

	}


	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside EtcHandler processNode()");

		if(!(node.getType().equals(Constants.ETC))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		EtcNode etcNode = (EtcNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		EstablishTemporaryConnectionArg etcArg = new EstablishTemporaryConnectionArg();

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		EstablishTemporaryConnectionExtension etcExt =null;
		//adding variables to CPB
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(ETC_FIELD_ASSISTING_SSP_ROUT_ADD)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String numQualifierIndEnum= subFieldElems.get(ETC_ENUM_NUM_QUALIFIER_IND_ENUM).getValue(varMap);
					String natureOfAddEnum = subFieldElems.get(ETC_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(ETC_ENUM_NUM_PLAN).getValue(varMap);
					String screeningEnum = subFieldElems.get(ETC_ENUM_SCREENING_ENUM).getValue(varMap);
					String numIncomplteEnum = subFieldElems.get(ETC_ENUM_NUM_INCMPLT__ENUM).getValue(varMap);
					String adrsPresntRestdEnum = subFieldElems.get(ETC_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
					
					//CalledPArt field
					byte[] byteArAssistingSspIp= null;
					try {
						byteArAssistingSspIp = GenericNumber.encodeGenericNum(NumQualifierIndEnum.valueOf(numQualifierIndEnum), fieldElem.getValue(varMap), 
								NatureOfAddEnum.valueOf(natureOfAddEnum), 
								NumPlanEnum.valueOf(numPlanEnum), AddPrsntRestEnum.valueOf(adrsPresntRestdEnum), 
								ScreeningIndEnum.valueOf(screeningEnum), NumIncmpltEnum.valueOf(numIncomplteEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding destinationroutAddrs in ETC request",e);
						return false;
					}	
					AssistingSSPIPRoutingAddress assistingSspIpRoutAddr =new AssistingSSPIPRoutingAddress();

					Digits digits=new Digits();
					digits.setValue(byteArAssistingSspIp);
					assistingSspIpRoutAddr.setValue(digits);

					etcArg.setAssistingSSPIPRoutingAddress(assistingSspIpRoutAddr);
					
				}else if(fieldName.equals(ETC_FIELD_CORR_ID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String encodingSchemeEnum = subFieldElems.get(ETC_ENUM_ENCODING_SCHEME).getValue(varMap);
					String digitCatEnum = subFieldElems.get(ETC_ENUM_DIGIT_CAT_ENUM).getValue(varMap);
					byte[] corrId =null;
					try {
						corrId = GenericDigits.encodeGenericDigits(EncodingSchemeEnum.valueOf(encodingSchemeEnum), 
								DigitCatEnum.valueOf(digitCatEnum),fieldElem.getValue(varMap));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding corrID in ETC request",e);
						return false;
					}
					Digits digits=new Digits();
					CorrelationID correlationId=new CorrelationID();
					digits.setValue(corrId);
					correlationId.setValue(digits);
					etcArg.setCorrelationID(correlationId);
				}else if(fieldName.equals(ETC_FIELD_SCF_ID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String spcIndicatorEnum = subFieldElems.get(ETC_ENUM_SPC_IND_ENUM).getValue(varMap);
					String ssnIndicatorEnum = subFieldElems.get(ETC_ENUM_SSN_IND_ENUM).getValue(varMap);
					String gtIndicatorEnum = subFieldElems.get(ETC_ENUM_GT_IND_ENUM).getValue(varMap);
					String routingIndicatorEnum = subFieldElems.get(ETC_ENUM_ROUTING_IND_ENUM).getValue(varMap);
					int zone_PC= Integer.parseInt(
							subFieldElems.get(ETC_SUBFIELD_ZONE_PC).getValue(varMap));
					int net_PC=Integer.parseInt(
							subFieldElems.get(ETC_SUBFIELD_NET_PC).getValue(varMap));
					int sp_PC=Integer.parseInt(
							subFieldElems.get(ETC_SUBFIELD_SP_PC).getValue(varMap));
					int ssn=Integer.parseInt(
							subFieldElems.get(ETC_SUBFIELD_SSN).getValue(varMap));
					
					ScfID scfI =null;
					try {
						scfI = new ScfID(ScfId.encodeScfId(SPCIndicatorEnum.valueOf(spcIndicatorEnum), 
								SSNIndicatorEnum.valueOf(ssnIndicatorEnum),GTIndicatorEnum.valueOf(gtIndicatorEnum), 
								RoutingIndicatorEnum.valueOf(routingIndicatorEnum), zone_PC,net_PC,sp_PC,ssn));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding scfId in COn request",e);
						return false;
					}
					etcArg.setScfID(scfI);

				}else if(fieldName.equals(ETC_FIELD_PARTY_TO_CONNECT)){
					
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String legIdstr = subFieldElems.get(ETC_SUBFIELD_LEG_ID).getValue(varMap);
					int legId= 1;//default is 1
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}
					if(logger.isDebugEnabled()){
						logger.debug("Got leg ID"+legId);
					}
					LegID legIdField = new LegID();
					
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectSendingSideID(new LegType(legType));

					EstablishTemporaryConnectionArg.PartyToConnectChoiceType ptc = new EstablishTemporaryConnectionArg.PartyToConnectChoiceType();
					ptc.selectLegID(legIdField);
					etcArg.setPartyToConnect(ptc);
					
					
				}else if(fieldName.equals(ETC_FIELD_CARRIER_INFO_TRANSFER)){

					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String carrierInfoSubordinateEnum = subFieldElems.get(ETC_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
					String carrierInfoNameEnum = subFieldElems.get(ETC_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
					String transitCarrierIndEnum = subFieldElems.get(ETC_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);
					
					try {
												
						//	
						CarrierIdentificationCode cic=new CarrierIdentificationCode();
						cic.setCarrierIdentCode(value);

						CarrierInfoSubordinate cis=new CarrierInfoSubordinate();
						cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.valueOf(carrierInfoSubordinateEnum));
						cis.setCarrierIdentificationCode(cic);
						//			cis.setCarrierInfoSubOrdinateLength(carrierInfoSubOrdinateLength)
						//			cis.setPoiChargeAreaInfo(poiChargeAreaInfo);
						//			cis.setPoiLevelInfo(poiLevelInfo);


						LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate =new LinkedList<CarrierInfoSubordinate>();
						carrierInfoSubordinate.add(cis);

						CarrierInformation ci=new CarrierInformation();
						//			ci.setCarrierInfoLength(carrierInfoLength);
						ci.setCarrierInfoSubordinate(carrierInfoSubordinate);
						ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.valueOf(carrierInfoNameEnum));

						LinkedList<CarrierInformation> carrierInformation =new LinkedList<CarrierInformation>();
						carrierInformation.add(ci);


						TtcCarrierInformationTransfer ttcCIT=new TtcCarrierInformationTransfer(
								TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum.valueOf(transitCarrierIndEnum), 
										carrierInformation));

						TtcCarrierInformation ttcCI=new TtcCarrierInformation();
						ttcCI.selectTtcCarrierInformationTransfer(ttcCIT);
						
						if(etcExt == null){
							etcExt=new EstablishTemporaryConnectionExtension();
						}
						etcExt.setTtcCarrierInformation(ttcCI);
						
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCarrierInfoTransfer Number in etcExt",e);
						return false;
					}

				}
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("EtcHandler processNode()-->fields read...saving extension if present");
		//setting extension
		if(etcExt!= null){
			if(logger.isDebugEnabled())
				logger.debug("EtcHandler processNode()-->extension present");
			//setting extension
			byte[] encodedEtcExt;
			try {
				encodedEtcExt = InapOperationsCoding.encodeEtcExt(etcExt);
				CriticalityType crit = new CriticalityType();
				crit.setValue(EnumType.ignore);
				if(logger.isDebugEnabled())
					logger.debug("EtcHandler processNode()-->set criticallity to ignore and type to 253");
				ExtensionField ef=new ExtensionField();
				ef.setValue(encodedEtcExt);
				//type for ETC extensions is 253
				ef.setType(253L);
				ef.setCriticality(crit);
				//setting extension
				Collection<ExtensionField> value=new LinkedList<ExtensionField>();
				value.add(ef);
				etcArg.setExtensions(value);
			} catch (Exception e) {
				logger.error("Error encoding Idp Extensions",e);
				return false;
			}
		}//complete extension setting

		if(logger.isDebugEnabled())
			logger.debug("EtcHandler processNode()-->Extension set encode ETCArg");
		//getting ETC byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(etcArg);
		opCode.add(InapOpCodes.ETC);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enetccoding ETC to byte array",e);
			return false;
		}
		byte[] etc = encode.get(0);
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for EtcHandler--> Got ETC byte array:: "+Util.formatBytes(etc));
			
		//generate etc component req event
		byte[] IdpOpCode = {0x11} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, IdpOpCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, etc));
		ire.setClassType(ETC_CLASS);
		if(logger.isDebugEnabled())
			logger.debug("EtcHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending etc component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("EtcHandler processNode()-->component send");
		//if last message generate dialog
		if(etcNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("EtcHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),etcNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("EtcHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on ETC::"+etcNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on ETC::"+etcNode.getDialogAs(),e);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving EtcHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for EtcHandler");

//		EtcNode etcNode = (EtcNode) node;
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("EtcHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		
		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;
		
		Iterator<Node> subElemIterator = subElements.iterator();
		
		//parsing ETC message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsEtc;
		EstablishTemporaryConnectionArg etc = null;
		
		try{
			parmsEtc =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for EtcHandler--> starting first level decoding on ETC bytes:: "+Util.formatBytes(parmsEtc));
			etc = (EstablishTemporaryConnectionArg)InapOperationsCoding.decodeOperation(parmsEtc, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}
		
		if(etc == null){
			logger.error("etc is null");
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
				
				String varField = setElem.getVarField();
				int startIndex = setElem.getStartIndx();
				int endIndx= setElem.getEndIndx();
				String varVal = null;
				
				if(varField.equals(ETC_SET_ASSISTING_SSP_ROUT_ADD)){
					byte[] assistingSspRouteAddrs= etc.getAssistingSSPIPRoutingAddress().getValue().getValue();
					try {
						GenericNumber genNum= GenericNumber.decodeGenericNum(assistingSspRouteAddrs);
						String addrSignalVal = genNum.getAddrSignal();
						if(endIndx == -1){
							endIndx = addrSignalVal.length();
						}
						varVal = addrSignalVal.substring(startIndex, endIndx);
						
					} catch (InvalidInputException e) {
						logger.error("Exit due to AssistingSSp decode failed with exception",e);
						break;
					}
				} else if(varField.equals(ETC_SET_CORR_ID)){
					byte[] corrId= etc.getCorrelationID().getValue().getValue();
					try {
						GenericDigits genDig= GenericDigits.decodeGenericDigits(corrId);
						String digits = genDig.getDigits();
						if(endIndx == -1){
							endIndx = digits.length();
						}
						varVal = digits.substring(startIndex, endIndx);
						
					} catch (InvalidInputException e) {
						logger.error("Exit due to AssistingSSp decode failed with exception",e);
						break;
					}
				}
				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);
				
			}//end if check for set elem
		}//end while loop on subelem
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for EtcHandler with status true");
		return true;

	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for EtcHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.ETC) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a ETC Node");
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
				logger.debug("EtcHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
