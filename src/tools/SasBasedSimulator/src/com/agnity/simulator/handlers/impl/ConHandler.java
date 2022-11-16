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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ConNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.CalledPartyNumber;
import com.genband.inap.asngenerated.ConnectArg;
import com.genband.inap.asngenerated.ConnectExtension;
import com.genband.inap.asngenerated.CorrelationID;
import com.genband.inap.asngenerated.CriticalityType;
import com.genband.inap.asngenerated.CriticalityType.EnumType;
import com.genband.inap.asngenerated.DestinationRoutingAddress;
import com.genband.inap.asngenerated.Digits;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.RedirectReason;
import com.genband.inap.asngenerated.ScfID;
import com.genband.inap.asngenerated.ServiceInteractionIndicatorsTwo;
import com.genband.inap.asngenerated.ServiceInteractionIndicatorsTwo.RedirectServiceTreatmentIndSequenceType;
import com.genband.inap.asngenerated.TtcCarrierInformation;
import com.genband.inap.asngenerated.TtcCarrierInformationTransfer;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.GenericDigits;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.DigitCatEnum;
import com.genband.inap.enumdata.EncodingSchemeEnum;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.IntNwNumEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.RedirectionReasonEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class ConHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(ConHandler.class);
	private static Handler handler;
	private static final int CON_CLASS = 2;

	private static final String CON_SET_DESTINATION_ROUT_ADD= "destinationRoutingAddress".toLowerCase();
	private static final String CON_SET_CORR_ID= "correlationID".toLowerCase();

	//Fileds
	private static final String CON_FIELD_DESTN_ROUTING_ADDRESS = "destinationRoutingAddress".toLowerCase();
	private static final String CON_FIELD_CORR_ID ="correlationID".toLowerCase();
	private static final String CON_FIELD_SCF_ID = "scfId".toLowerCase();
	private static final String CON_FIELD_CARRIER_INFO_TRANSFER = "ttcCarrierInformationTransfer".toLowerCase();
	private static final String CON_FIELD_SRVC_INTERACTION_IND = "serviceInteractionIndicatorsTwo".toLowerCase();

	//enums
	//destinationroutaddrs
	private static final String CON_ENUM_NATURE_OF_ADD = "NatureOfAddEnum".toLowerCase();
	private static final String CON_ENUM_NUM_PLAN = "NumPlanEnum".toLowerCase();
	private static final String CON_ENUM_INT_NTW_ENUM = "IntNwNumEnum".toLowerCase();
	//corrID
	private static final String CON_ENUM_ENCODING_SCHEME = "EncodingSchemeEnum".toLowerCase();
	private static final String CON_ENUM_DIGIT_CAT_ENUM = "DigitCatEnum".toLowerCase();

	//scfID
	private static final String CON_ENUM_SPC_IND_ENUM = "SPCIndicatorEnum".toLowerCase();
	private static final String CON_ENUM_SSN_IND_ENUM = "SSNIndicatorEnum".toLowerCase();
	private static final String CON_ENUM_GT_IND_ENUM = "GTIndicatorEnum".toLowerCase();
	private static final String CON_ENUM_ROUTING_IND_ENUM = "RoutingIndicatorEnum".toLowerCase();

	private static final String CON_SUBFIELD_ZONE_PC = "zone_PC".toLowerCase();
	private static final String CON_SUBFIELD_NET_PC = "net_PC".toLowerCase();
	private static final String CON_SUBFIELD_SP_PC = "sp_PC".toLowerCase();
	private static final String CON_SUBFIELD_SSN = "ssn".toLowerCase();

	//service interactiion ind
	private static final String CON_SUBFIELD_ALLOWED_CD_IN_NUM_PRESENTATION_IND = "allowedCdINNoPresentaionInd".toLowerCase();
	private static final String CON_SUBFIELD_CALLED_IN_NUM_OVERRIDE = "calledINNumberOverriding".toLowerCase();
	private static final String CON_SUBFIELD_RDRCT_SRVC_TRTMNT_IND = "redirectionReasonEnum".toLowerCase();
	//carrierinfo transfer
	private static final String CON_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String CON_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String CON_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();

	//validate CON fields
	//validate field names
	private static final String CON_VALIDATE_DESTN_ROUTING_ADDRESS = "destinationRoutingAddress".toLowerCase();
	private static final String CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS = "CalledPartyAddress".toLowerCase();
	private static final String CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS = "natureOfAdrs".toLowerCase();
	private static final String CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN = "numPlan".toLowerCase();
	private static final String CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM = "intNtwrkNum".toLowerCase();
	
	private static final String CON_VALIDATE_SRVC_INT_IND_ALLWD_CD_IN_NO_PRSNTN_IND = "srvcIntIndTwo.allowedCdINNoPresentaionInd".toLowerCase();
	private static final String CON_VALIDATE_SRVC_INT_IND_CLD_IN_NUM_OVRD = "srvcIntIndTwo.calledINNumberOverriding".toLowerCase();
	private static final String CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND = "srvcIntIndTwo.redirectServiceTreatmentInd".toLowerCase();
	private static final String CON_VALIDATE_CARRIER_INFO_TRANSFER = "ttcCarrierInformationTransfer".toLowerCase();
	
	private static final String CON_VALIDATE_CRITIACLITY = "criticality".toLowerCase();
	private static final String CON_VALIDATE_TYPE = "type".toLowerCase();
	
	
	
	private enum TypeEnum{

		con(-1),idp(-2),etc(-3);
		
		private TypeEnum(int i){
			this.code=i;
		}
		private int code;

		public int getCode() {
			return code;
		}
		
		public static TypeEnum fromInt(int num) {
			TypeEnum type=con;
			switch (num) {
				case 1: { 
					type= con; 
					break;
				}
				case 2: { 
					type= idp;
					break;
				}
				case 3: { 
					type= etc;
					break;
				}				
			}//@End Switch
			return type;
		}
	}
	
	
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (ConHandler.class) {
				if(handler ==null){
					handler = new ConHandler();
				}
			}
		}
		return handler;
	}

	private ConHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside ConHandler processNode()");

		if(!(node.getType().equals(Constants.CON))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		ConNode conNode = (ConNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		ConnectArg conArg = new ConnectArg();

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		ConnectExtension conExt =null;
		//adding variables to CPB
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(CON_FIELD_DESTN_ROUTING_ADDRESS)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding destnrouteAddr");
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String natureOfAddEnum = subFieldElems.get(CON_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(CON_ENUM_NUM_PLAN).getValue(varMap);
					String intNwNumEnum = subFieldElems.get(CON_ENUM_INT_NTW_ENUM).getValue(varMap);
					//CalledPArt field
					byte[] b= null;
					try {
						b = CalledPartyNum.encodeCaldParty(fieldElem.getValue(varMap), NatureOfAddEnum.valueOf(natureOfAddEnum), 
								NumPlanEnum.valueOf(numPlanEnum), IntNwNumEnum.valueOf(intNwNumEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding destinationroutAddrs in CON request",e);
						return false;
					}
					if(logger.isDebugEnabled())
						logger.debug("Encoding destnrouteAddr got called party num byte array::"+com.genband.inap.util.Util.formatBytes(b));
					CalledPartyNumber cpNum = new CalledPartyNumber();
					cpNum.setValue(b);
					Collection<CalledPartyNumber> coll = new LinkedList<CalledPartyNumber>();
					coll.add(cpNum);
					DestinationRoutingAddress destAdd = new DestinationRoutingAddress();
					destAdd.setValue(coll);

					conArg.setDestinationRoutingAddress(destAdd);

				}else if(fieldName.equals(CON_FIELD_CORR_ID)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding corrId");
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String encodingSchemeEnum = subFieldElems.get(CON_ENUM_ENCODING_SCHEME).getValue(varMap);
					String digitCatEnum = subFieldElems.get(CON_ENUM_DIGIT_CAT_ENUM).getValue(varMap);
					byte[] corrId =null;
					try {
						corrId = GenericDigits.encodeGenericDigits(EncodingSchemeEnum.valueOf(encodingSchemeEnum), 
								DigitCatEnum.valueOf(digitCatEnum),fieldElem.getValue(varMap));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding corrID in CON request",e);
						return false;
					}
					if(logger.isDebugEnabled())
						logger.debug("Encoding corrId digit byte array::"+com.genband.inap.util.Util.formatBytes(corrId));
					Digits digits=new Digits();
					CorrelationID correlationId=new CorrelationID();
					digits.setValue(corrId);
					correlationId.setValue(digits);
					conArg.setCorrelationID(correlationId);
				}else if(fieldName.equals(CON_FIELD_SCF_ID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					if(logger.isDebugEnabled())
						logger.debug("Encoding scfId");

					String spcIndicatorEnum = subFieldElems.get(CON_ENUM_SPC_IND_ENUM).getValue(varMap);
					String ssnIndicatorEnum = subFieldElems.get(CON_ENUM_SSN_IND_ENUM).getValue(varMap);
					String gtIndicatorEnum = subFieldElems.get(CON_ENUM_GT_IND_ENUM).getValue(varMap);
					String routingIndicatorEnum = subFieldElems.get(CON_ENUM_ROUTING_IND_ENUM).getValue(varMap);
					int zone_PC= Integer.parseInt(
							subFieldElems.get(CON_SUBFIELD_ZONE_PC).getValue(varMap));
					int net_PC=Integer.parseInt(
							subFieldElems.get(CON_SUBFIELD_NET_PC).getValue(varMap));
					int sp_PC=Integer.parseInt(
							subFieldElems.get(CON_SUBFIELD_SP_PC).getValue(varMap));
					int ssn=Integer.parseInt(
							subFieldElems.get(CON_SUBFIELD_SSN).getValue(varMap));

					ScfID scfI =null;
					try {
						scfI = new ScfID(ScfId.encodeScfId(SPCIndicatorEnum.valueOf(spcIndicatorEnum), 
								SSNIndicatorEnum.valueOf(ssnIndicatorEnum),GTIndicatorEnum.valueOf(gtIndicatorEnum), 
								RoutingIndicatorEnum.valueOf(routingIndicatorEnum), zone_PC,net_PC,sp_PC,ssn));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding scfId in COn request",e);
						return false;
					}
					if(logger.isDebugEnabled())
						logger.debug("Encoding scfID complete::");
					conArg.setScfID(scfI);

				}else if(fieldName.equals(CON_FIELD_SRVC_INTERACTION_IND)){
					//ServiceInteractionIndicatorsTwo
					if(logger.isDebugEnabled())
						logger.debug("Encoding ServiceInteractionIndicatorsTwo");
					ServiceInteractionIndicatorsTwo sIIT =new ServiceInteractionIndicatorsTwo();
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String allowedcdINNoPresent=subFieldElems.get(CON_SUBFIELD_ALLOWED_CD_IN_NUM_PRESENTATION_IND).getValue(varMap);
					if(allowedcdINNoPresent!=null){
						if(logger.isDebugEnabled())
							logger.debug("Encoding ServiceInteractionIndicatorsTwo allowedCdINNoPresentaionInd");
						boolean allowedCdINNoPresentaionInd= Boolean.parseBoolean(allowedcdINNoPresent);
						sIIT.setAllowedCdINNoPresentaionInd(allowedCdINNoPresentaionInd);
					}
					String calledINNumOvride=subFieldElems.get(CON_SUBFIELD_CALLED_IN_NUM_OVERRIDE).getValue(varMap);
					if(calledINNumOvride!=null){
						if(logger.isDebugEnabled())
							logger.debug("Encoding ServiceInteractionIndicatorsTwo calledINNumberOverriding");
						boolean calledINNumberOverriding= Boolean.parseBoolean(calledINNumOvride);
						sIIT.setCalledINNumberOverriding(calledINNumberOverriding);
					}
					String redirectionReasonEnumVal = subFieldElems.get(CON_SUBFIELD_RDRCT_SRVC_TRTMNT_IND).getValue(varMap);
					if(redirectionReasonEnumVal!=null){
						if(logger.isDebugEnabled())
							logger.debug("Encoding ServiceInteractionIndicatorsTwo rdrctsrvctrtmntInd");
						byte[] rdrctReasonVal=null;
						try {
							rdrctReasonVal=
								com.genband.inap.datatypes.RedirectReason.encodeRedirectionReason(RedirectionReasonEnum.valueOf(redirectionReasonEnumVal));
						} catch (InvalidInputException e) {
							logger.error("InvalidInputException encoding rdrctsrvctrtmntInd in COn request",e);
							return false;
						}
						RedirectReason redirectReason = new RedirectReason(rdrctReasonVal);
						RedirectServiceTreatmentIndSequenceType rdrctSrvcTrtmntIndType= new RedirectServiceTreatmentIndSequenceType();
						rdrctSrvcTrtmntIndType.setRedirectReason(redirectReason);
						sIIT.setRedirectServiceTreatmentInd(rdrctSrvcTrtmntIndType);
					}

					if(logger.isDebugEnabled())
						logger.debug("Encoding ServiceInteractionIndicatorsTwo complete");

					conArg.setServiceInteractionIndicatorsTwo(sIIT);
				}else if(fieldName.equals(CON_FIELD_CARRIER_INFO_TRANSFER)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcCArrierInfoTransfer");
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String carrierInfoSubordinateEnum = subFieldElems.get(CON_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
					String carrierInfoNameEnum = subFieldElems.get(CON_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
					String transitCarrierIndEnum = subFieldElems.get(CON_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);

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

						if(conExt == null){
							conExt=new ConnectExtension();
						}
						conExt.setTtcCarrierInformation(ttcCI);
						if(logger.isDebugEnabled())
							logger.debug("Encoding complete");
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCarrierInfoTransfer Number in conExt",e);
						return false;
					}

				}
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("ConHandler processNode()-->fields read...saving extension if present");
		//setting extension
		if(conExt!= null){
			if(logger.isDebugEnabled())
				logger.debug("ConHandler processNode()-->extension present");
			//setting extension
			byte[] encodedConExt;
			try {
				encodedConExt = InapOperationsCoding.encodeConnectExt(conExt);
				if(logger.isDebugEnabled())
					logger.debug("Encoded extensions::"+com.genband.inap.util.Util.formatBytes(encodedConExt));

				CriticalityType crit = new CriticalityType();
				if(logger.isDebugEnabled())
					logger.debug("ConHandler processNode()-->set criticallity to ignore and type to 255");
				crit.setValue(EnumType.ignore);
				ExtensionField ef=new ExtensionField();
				ef.setValue(encodedConExt);
				//type for CON extensions is 255
				ef.setType(255L);
				ef.setCriticality(crit);
				//setting extension
				Collection<ExtensionField> value=new LinkedList<ExtensionField>();
				value.add(ef);
				conArg.setExtensions(value);
			} catch (Exception e) {
				logger.error("Error encoding CON Extensions",e);
				return false;
			}
		}//complete extension setting

		if(logger.isDebugEnabled())
			logger.debug("ConHandler processNode()-->Extension set encode CONArg");
		//getting CON byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(conArg);
		opCode.add(InapOpCodes.CONNECT);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enconcoding CON to byte array",e);
			return false;
		}
		byte[] con = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CONHandler--> Got CONNECT byte array:: "+Util.formatBytes(con));

		//generate con component req event
		byte[] conOpCode = {0x14} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, conOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, con));
		ire.setClassType(CON_CLASS);

		if(logger.isDebugEnabled())
			logger.debug("ConHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending con component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("ConHandler processNode()-->component send");
		//if last message generate dialog
		if(conNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("ConHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),conNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("ConHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on CON::"+conNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on CON::"+conNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving ConHandler processNode() with status true");
		return true;

	}


	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ConHandler");

		//		ConNode conNode = (ConNode) node;

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("ConHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		SetElem setElem = null;
		ValidateElem validateElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ConHandler--> varMAp is"+varMap);

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing CON message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsCon;
		ConnectArg con = null;

		try{
			parmsCon =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for ConHandler--> starting first level decoding on CON bytes:: "+Util.formatBytes(parmsCon));
			con = (ConnectArg)InapOperationsCoding.decodeOperation(parmsCon, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(con == null){
			logger.error("con is null");
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ConHandler--> first level decode operation complete Got Con::"+con);


		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for ConHandler-->creating new variable");
					var = new Variable();
					var.setVarName(varName);
				}

				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for ConHandler--variable created");

				String varField = setElem.getVarField();
				int startIndex = setElem.getStartIndx();
				int endIndx= setElem.getEndIndx();
				String varVal = null;


				if(varField.equals(CON_SET_DESTINATION_ROUT_ADD)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for ConHandler-->set destn Rout Add");
					Collection<CalledPartyNumber> collDestnRoutAddr = con.getDestinationRoutingAddress().getValue();
					byte[] destinationRoutingAddrs=null;
					if(collDestnRoutAddr.iterator().hasNext())
						destinationRoutingAddrs = collDestnRoutAddr.iterator().next().getValue();
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for ConHandler-->got destinationRoutingAddrs bytes::"+destinationRoutingAddrs);
					try {
						if(destinationRoutingAddrs !=null){
							CalledPartyNum cldPartyDesRoutAddr = CalledPartyNum.decodeCaldParty(destinationRoutingAddrs);
							String addrSignalVal = cldPartyDesRoutAddr.getAddrSignal();
							if(endIndx == -1){
								endIndx = addrSignalVal.length();
							}
							varVal = addrSignalVal.substring(startIndex, endIndx);
							if(logger.isDebugEnabled())
								logger.debug("processRecievedMessage() for ConHandler-->got destinationRoutingAddrs values::"+varVal);
						}
					} catch (InvalidInputException e) {
						logger.error("Exit due to AssistingSSp decode failed with exception",e);
						return false;
					}
				} else if(varField.equals(CON_SET_CORR_ID)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for ConHandler-->set Correlation ID");
					byte[] corrId= con.getCorrelationID().getValue().getValue();
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for ConHandler-->got corrID bytes::"+corrId);
					try {
						GenericDigits genDig= GenericDigits.decodeGenericDigits(corrId);
						String digits = genDig.getDigits();
						if(endIndx == -1){
							endIndx = digits.length();
						}
						varVal = digits.substring(startIndex, endIndx);
						if(logger.isDebugEnabled())
							logger.debug("processRecievedMessage() for ConHandler-->got corrId value::"+varVal);

					} catch (InvalidInputException e) {
						logger.error("Exit due to AssistingSSp decode failed with exception",e);
						return false;
					}
				}
				//finally storing variable
				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for ConHandler-->store variable in CPB::");
				var.setVarValue(varVal);
				simCpb.addVariable(var);
				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for ConHandler-->gVariable in CPB stores");

			}else if(subElem.getType().equals(Constants.VALIDATE)){
				validateElem =(ValidateElem) subElem;

				String fieldName = validateElem.getFieldName();
				String expectedVal= validateElem.getFieldVal(varMap);
				//validateElem.getSubFieldElements().entrySet().iterator();
				if(fieldName.equals(CON_VALIDATE_DESTN_ROUTING_ADDRESS)){
					boolean status=validateConDestnRoutAddrs(con,validateElem,varMap);
					if(!status){
						logger.debug("validate of destn Route addrs failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_CARRIER_INFO_TRANSFER)){
					boolean status=validateCarrierInfoTrfrCIC(con,expectedVal);
					if(!status){
						logger.debug("validate of carier info code in carrier info trfr failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_SRVC_INT_IND_ALLWD_CD_IN_NO_PRSNTN_IND)){
					boolean status=validateSrvcIntIndAllwdCdInNoPrsntInd(con,expectedVal);
					if(!status){
						logger.debug("validate of service interaction Ind allowed cd in no prsntn failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_SRVC_INT_IND_CLD_IN_NUM_OVRD)){
					boolean status=validateSrvcIntIndCldInNumOvrd(con,expectedVal);
					if(!status){
						logger.debug("validate of service interaction Ind  cld in num ovrd failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND)){
					boolean status=validateSrvcIntIndRdrctSrvcTrtmntInd(con,expectedVal);
					if(!status){
						logger.debug("validate of service interaction Ind rdrct srvc trtmnt ind failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_CRITIACLITY)){
					boolean status=validateExtensionFieldCriticalityandType(con,expectedVal,fieldName);
					if(!status){
						logger.debug("validate of criticality failed; return false");
						return status;
					}
				}else if(fieldName.equals(CON_VALIDATE_TYPE)){
					boolean status=validateExtensionFieldCriticalityandType(con,expectedVal,fieldName);
					if(!status){
						logger.debug("validate of type failed; return false");
						return status;
					}
				}//@End:check field name
			}//end if check for sub elemType
		}//end while loop on subelem

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ConHandler leaving with status true");
		return true;

	}

	private boolean validateSrvcIntIndRdrctSrvcTrtmntInd(ConnectArg con,
			String expectedVal) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate ServiceIntearctionIndtwo rdrctServiceTrtmntInd");
		ServiceInteractionIndicatorsTwo srvcIntIndTwo = null;

		if(con.isServiceInteractionIndicatorsTwoPresent()){
			srvcIntIndTwo=con.getServiceInteractionIndicatorsTwo();

		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] srvcIntIndTwo not present");
			return false;
		}
		RedirectServiceTreatmentIndSequenceType rdrctSrvcTrtmntIndSeqType=null;
		if(srvcIntIndTwo.isRedirectServiceTreatmentIndPresent()){
			rdrctSrvcTrtmntIndSeqType=srvcIntIndTwo.getRedirectServiceTreatmentInd();
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] srvcIntIndTwo rdrctSrvcTrtmntIndSeqType not present");
			return false;
		}

		RedirectReason redirectReason = null;
		if(rdrctSrvcTrtmntIndSeqType.isRedirectReasonPresent()){
			redirectReason=rdrctSrvcTrtmntIndSeqType.getRedirectReason();
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] srvcIntIndTwo redirectReason not present");
			return false;
		}
		byte[] redirectReasonVal=redirectReason.getValue();

		if(logger.isDebugEnabled())
			logger.debug("Decoing redirect reason val::"+Util.formatBytes(redirectReasonVal));


		com.genband.inap.datatypes.RedirectReason redirectionReason=null;
		try {
			redirectionReason=com.genband.inap.datatypes.RedirectReason.decodeRedirectionReason(redirectReasonVal);
		} catch (InvalidInputException e) {
			logger.error("Exception decoding redirection reason for Connect message ConHandler",e);
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] srvcIntIndTwo redirectReason decode failed");
			return false;
		}

		RedirectionReasonEnum redirectionReasonEnum = redirectionReason.getRedirectionReasonEnum();
		if(redirectionReasonEnum==null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] redirectionReasonEnum  Enum Value is null");
			return false;
		}
		if( (RedirectionReasonEnum.valueOf(expectedVal)).equals(redirectionReasonEnum)){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] redirectionReasonEnum Value matched Expected::["+expectedVal+
						"] Actual Value::["+redirectionReasonEnum.toString());
			return true;
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_RDRCT_SRVC_TRTMNT_IND +"] redirectionReasonEnum Value not matched Expected::["+expectedVal+
					"] Actual Value::["+redirectionReasonEnum.toString());
			return false;
		}	

	}

	private boolean validateSrvcIntIndCldInNumOvrd(ConnectArg con,
			String expectedVal) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate ServiceIntearctionIndtwo cldInNumOvride");
		ServiceInteractionIndicatorsTwo srvcIntIndTwo = null;

		if(con.isServiceInteractionIndicatorsTwoPresent())
			srvcIntIndTwo=con.getServiceInteractionIndicatorsTwo();

		if(srvcIntIndTwo==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_CLD_IN_NUM_OVRD +"] srvcIntIndTwo not present");
			return false;
		}
		Boolean cldInNumOvride=null;
		if(srvcIntIndTwo.isCalledINNumberOverridingPresent())
			cldInNumOvride=srvcIntIndTwo.getCalledINNumberOverriding();

		if(cldInNumOvride!=null &&  cldInNumOvride.equals(Boolean.valueOf(expectedVal)) ){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_SRVC_INT_IND_CLD_IN_NUM_OVRD +"] srvcIntIndTwo cldInNumOvride matched Expected::["+expectedVal+
						"] Actual Value::["+cldInNumOvride);
			return true;
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_CLD_IN_NUM_OVRD +"] srvcIntIndTwo cldInNumOvride not matched Expected::["+expectedVal+
					"] Actual Value::["+cldInNumOvride);
			return false;
		}
	}

	private boolean validateSrvcIntIndAllwdCdInNoPrsntInd(ConnectArg con,
			String expectedVal) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate ServiceIntearctionIndtwo allowedCdInNOPesntnInd");
		ServiceInteractionIndicatorsTwo srvcIntIndTwo = null;

		if(con.isServiceInteractionIndicatorsTwoPresent())
			srvcIntIndTwo=con.getServiceInteractionIndicatorsTwo();

		if(srvcIntIndTwo==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_ALLWD_CD_IN_NO_PRSNTN_IND +"] srvcIntIndTwo not present");
			return false;
		}
		Boolean allowedCdInNOPesntnInd=null;
		if(srvcIntIndTwo.isAllowedCdINNoPresentaionIndPresent())
			allowedCdInNOPesntnInd=srvcIntIndTwo.getAllowedCdINNoPresentaionInd();

		if(allowedCdInNOPesntnInd!=null &&  allowedCdInNOPesntnInd.equals(Boolean.valueOf(expectedVal)) ){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_SRVC_INT_IND_ALLWD_CD_IN_NO_PRSNTN_IND +"] srvcIntIndTwo allowedCdInNOPesntnInd matched Expected::["+expectedVal+
						"] Actual Value::["+allowedCdInNOPesntnInd);
			return true;
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_SRVC_INT_IND_ALLWD_CD_IN_NO_PRSNTN_IND +"] srvcIntIndTwo allowedCdInNOPesntnInd not matched Expected::["+expectedVal+
					"] Actual Value::["+allowedCdInNOPesntnInd);
			return false;
		}


	}
	
	private boolean validateExtensionFieldCriticalityandType(ConnectArg con,
			String expectedVal,String fieldName) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate ServiceIntearctionIndtwo rdrctServiceTrtmntInd");
		
		Collection<ExtensionField> extensionsList=null;
		if(con.isExtensionsPresent()){
			extensionsList=con.getExtensions();
		}
		if(extensionsList==null ||extensionsList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CRITIACLITY +"] Extensions not present");
			return false;
		}
		ExtensionField ext = extensionsList.iterator().next();
		if(ext == null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CRITIACLITY +"] ExtensionField not present");
			return false;
		}
		if(fieldName.equalsIgnoreCase("criticality")){
			CriticalityType crtType = ext.getCriticality();
			if(crtType.getValue()==CriticalityType.EnumType.valueOf(expectedVal))
			{
				if(logger.isDebugEnabled())
					logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_CRITIACLITY+"] criticality matched Expected::["+expectedVal+
							"] Actual Value::["+crtType.getValue().toString());
				return true;
			}else{
				SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_CRITIACLITY +"] criticality not matched Expected::["+expectedVal+
						"] Actual Value::["+crtType.getValue().toString());
				return false;
			}
		}else if(fieldName.equalsIgnoreCase("type")){
			Long type = ext.getType();
			TypeEnum val = TypeEnum.valueOf(expectedVal);
			if(type==val.getCode())
			{
				if(logger.isDebugEnabled())
					logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_TYPE+"] type matched Expected::["+expectedVal+
							"] Actual Value::["+type);
				return true;
			}else{
				SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_TYPE +"] Type not matched Expected::["+expectedVal+
						"] Actual Value::["+type);
				return false;
			}
		}
		return false;
		}
		
	

	private boolean validateCarrierInfoTrfrCIC(ConnectArg con,
			String expectedVal) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate carrier info code in ttcCarrierInfoTrfr");


		Collection<ExtensionField> extensionsList=null;
		if(con.isExtensionsPresent()){
			extensionsList=con.getExtensions();
		}
		if(extensionsList==null ||extensionsList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] Extensions not present");
			return false;

		}
		
		byte[] extension= extensionsList.iterator().next().getValue();
		if(logger.isDebugEnabled())
			logger.debug("DecodingExtension::"+Util.formatBytes(extension));
		ConnectExtension conExt=null;
		try {
			conExt = InapOperationsCoding.decodeConnectExt(extension);
		} catch (Exception e1) {
			logger.error("Exception decoding etensions for Connect message ConHandler",e1);
		}
		if(conExt==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] conExt is null");
			return false;
		}
		TtcCarrierInformation ttcCarrierInfo=conExt.getTtcCarrierInformation();
		if(ttcCarrierInfo==null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] ttcCarrierInfo not found");
			return false;
		}
		TtcCarrierInformationTransfer ttcCarrierInfoTrfr=null;

		if(ttcCarrierInfo.isTtcCarrierInformationTransferSelected())
			ttcCarrierInfoTrfr=ttcCarrierInfo.getTtcCarrierInformationTransfer();

		if(ttcCarrierInfoTrfr==null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] ttcCarrierInfoTrfr not found");
			return false;
		}
		byte[] ttcCITBytes = ttcCarrierInfoTrfr.getValue();
		if(logger.isDebugEnabled())
			logger.debug("decoding ttcCITBytes::"+Util.formatBytes(ttcCITBytes));


		List<CarrierInformation> carrierInfoList=null;
		try {
			carrierInfoList = TtcCarrierInfoTrfr.decodeTtcCarrierInfoTrfr(ttcCITBytes).getCarrierInformation();
		} catch (InvalidInputException e) {
			logger.error("Exception decoding calledparty num for destnrouting Addr in ConHandler",e);
		}
		if(carrierInfoList==null || carrierInfoList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"]  carrierInfoList  is null or empty in decoded ttcCarrierInfoTrfr");
			return false;
		}
		CarrierInformation carrierInfo = carrierInfoList.iterator().next();
		List<CarrierInfoSubordinate> carrierInfoSubList =carrierInfo.getCarrierInfoSubordinate();
		if(carrierInfoSubList==null || carrierInfoSubList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"]  carrierInfoSubList  is null or empty in carrierInfo");
			return false;
		}	
		CarrierInfoSubordinate carrierInfoSub = carrierInfoSubList.iterator().next();
		CarrierIdentificationCode cic = carrierInfoSub.getCarrierIdentificationCode();

		if(cic==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"]  cic  is null in carrierInfoSub");
			return false;
		}
		String cicVal=cic.getCarrierIdentCode();

		if( cicVal !=null  &&  cicVal.equals(expectedVal) ){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] CarrienfoCode Value  matched Expected::["+expectedVal+
						"] Actual Value::["+cicVal);
			return true;
		}else{
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_CARRIER_INFO_TRANSFER +"] CarrienfoCode value not matched Expected::["+expectedVal+
					"] Actual Value::["+cicVal);
			return false;
		}
	}

	private boolean validateConDestnRoutAddrs(ConnectArg con, ValidateElem validateElem,Map<String, Variable> varMap) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate destn Route Adds");
		DestinationRoutingAddress dstnRoutingAddr=con.getDestinationRoutingAddress();
		if(dstnRoutingAddr==null ){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS +"] dstnRoutingAddr not present");
			return false;
		}
		Collection<CalledPartyNumber> cldPartyNumList=dstnRoutingAddr.getValue();
		if(cldPartyNumList==null || cldPartyNumList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS +"] dstnRoutingAddr called partynum list is null or empty");
			return false;
		}
		CalledPartyNumber cldPartyNum=cldPartyNumList.iterator().next();
		if(cldPartyNum==null){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS +"] dstnRoutingAddr cldPartyNum not found");
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("Bytes for destn Routing addr::"+Util.formatBytes(cldPartyNum.getValue()));
		
	
		try {
			CalledPartyNum cpn = CalledPartyNum.decodeCaldParty(cldPartyNum.getValue());
			//Entry<String,SubFieldElem> ent = validateElem.getSubFieldElements().entrySet();
			for(Map.Entry<String, SubFieldElem> ent : validateElem.getSubFieldElements().entrySet())
			{
			String key = ent.getKey();
			if(key.equalsIgnoreCase(CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS))
			{
				String CalledPartyAddress = cpn.getAddrSignal();
				if(CalledPartyAddress==null){
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS +"]  CalledPartyAddress  is null");
					return false;
				}

				if( CalledPartyAddress.equals(validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS).getValue(varMap)) ){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS +"] CalledPartyAddress  matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS).getValue(varMap)+
								"] Actual Value::["+CalledPartyAddress);
					
				}else{
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS +"] CalledPartyAddress not matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_CALLD_PARTY_ADDRESS).getValue(varMap)+
							"] Actual Value::["+CalledPartyAddress);
					return false;
				}
			}	
			if(key.equalsIgnoreCase(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS)){
				NatureOfAddEnum natureOfAdrs = cpn.getNatureOfAdrs();
				String validateVal_natureOfAdrs = validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS).getValue(varMap); 
				
				if(natureOfAdrs==null){
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS +"]  natureOfAdrs  is null");
					return false;
				}
				
				if( natureOfAdrs==NatureOfAddEnum.valueOf(validateVal_natureOfAdrs)){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS +"] natureOfAdrs  matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS).getValue(varMap)+
								"] Actual Value::["+natureOfAdrs.toString());
					
				}else{
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS +"] natureOfAdrs not matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NATURE_OF_ADDRESS).getValue(varMap)+
							"] Actual Value::["+natureOfAdrs.toString());
					return false;
				}
				
			}
			
			if(key.equalsIgnoreCase(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN)){
				NumPlanEnum numPlan = cpn.getNumPlan();
				String validateVal_numPlan = validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN).getValue(varMap);
				if(numPlan==null){
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN +"]  numPlan  is null");
					return false;
				}
				
				if( numPlan == NumPlanEnum.valueOf(validateVal_numPlan)){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN +"] numPlan  matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN).getValue(varMap)+
								"] Actual Value::["+numPlan.toString());
					
				}else{
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN +"] numPlan not matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_NUMBERING_PLAN).getValue(varMap)+
							"] Actual Value::["+numPlan.toString());
					return false;
				}
			}
			
			if(key.equalsIgnoreCase(CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM)){
						
				IntNwNumEnum intNtwrkNum = cpn.getIntNtwrkNum();
				String validateVal_intNtwrkNum = validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM).getValue(varMap);
			
				if(intNtwrkNum==null){
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM +"]  intNtwrkNum  is null");
					return false;
				}
				
				if( intNtwrkNum == IntNwNumEnum.valueOf(validateVal_intNtwrkNum)){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM +"] intNtwrkNum  matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM).getValue(varMap)+
								"] Actual Value::["+intNtwrkNum.toString());
					
				}else{
					SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM +"] intNtwrkNum not matched Expected::["+validateElem.getSubFieldElements().get(CON_VALIDATE_DESTN_ROUTING_ADDRESS_INT_NTWRK_NUM).getValue(varMap)+
							"] Actual Value::["+intNtwrkNum.toString());
					return false;
				}
			}	
		}//end of while
		} catch (InvalidInputException e) {
			logger.error("Exception decoding calledparty num for destnrouting Addr in ConHandler",e);
		}	
		return true;
	}
			
	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for ConHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.CON) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a CONNECT Node");
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
				logger.debug("ConHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
