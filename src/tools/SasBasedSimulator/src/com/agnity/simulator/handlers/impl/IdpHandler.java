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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.IdpNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.BearerCapability;
import com.genband.inap.asngenerated.CalledPartyNumber;
import com.genband.inap.asngenerated.CallingPartyNumber;
import com.genband.inap.asngenerated.CallingPartysCategory;
import com.genband.inap.asngenerated.CriticalityType;
import com.genband.inap.asngenerated.CriticalityType.EnumType;
import com.genband.inap.asngenerated.EventTypeBCSM;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.ForwardCallIndicators;
import com.genband.inap.asngenerated.GenericNumbers;
import com.genband.inap.asngenerated.InitialDPArg;
import com.genband.inap.asngenerated.InitialDPExtension;
import com.genband.inap.asngenerated.Integer4;
import com.genband.inap.asngenerated.ServiceKey;
import com.genband.inap.asngenerated.TerminalType;
import com.genband.inap.asngenerated.TtcCarrierInformationTransfer;
import com.genband.inap.asngenerated.TtcContractorNumber;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CallingPartyNum;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.FwCallIndicators;
import com.genband.inap.datatypes.GenericNumber;
import com.genband.inap.datatypes.TtcAdtnalPartyCategory;
import com.genband.inap.datatypes.TtcCalledINNumber;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.datatypes.TtcChargeAreaInfo;
import com.genband.inap.datatypes.TtcContractorNum;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.AdtnlPartyCat1Enum;
import com.genband.inap.enumdata.AdtnlPartyCatNameEnum;
import com.genband.inap.enumdata.CalgPartyCatgEnum;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.EndToEndInfoIndEnum;
import com.genband.inap.enumdata.EndToEndMethodIndEnum;
import com.genband.inap.enumdata.ISDNAccessIndEnum;
import com.genband.inap.enumdata.ISDNUserPartIndEnum;
import com.genband.inap.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.inap.enumdata.InfoDiscriminationIndiEnum;
import com.genband.inap.enumdata.IntNwNumEnum;
import com.genband.inap.enumdata.InterNwIndEnum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat1Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat2Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat3Enum;
import com.genband.inap.enumdata.NatIntNatCallIndEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.NumQualifierIndEnum;
import com.genband.inap.enumdata.SCCPMethodIndENum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.enumdata.TTCNatureOfAddEnum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.enumdata.TransmissionMedReqEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.NonAsnArg;
import com.genband.tcap.parser.Util;

public class IdpHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(IdpHandler.class);
	private static Handler handler;

	private static final String IDP_FIELD_SRV_KEY= "serviceKey".toLowerCase();
	private static final String IDP_FIELD_DIALED_DIG= "dialledDigits".toLowerCase();
	private static final String IDP_FIELD_CALLED_PARTY_NUM= "calledPartyNumber".toLowerCase();
	private static final String IDP_FIELD_CALLING_PARTY_NUM= "callingPartyNumber".toLowerCase();
	private static final String IDP_FIELD_CALLING_PARTYS_CATG= "callingPartysCategory".toLowerCase();
	private static final String IDP_FIELD_CALLING_PARTY_SUBADD= "callingPartySubaddress".toLowerCase();
	private static final String IDP_FIELD_MISC_CALL_INFO= "miscCallInfo".toLowerCase();
	private static final String IDP_FIELD_TERMINAL_TYPE= "terminalType".toLowerCase();
	private static final String IDP_FIELD_FW_CALL_IND= "forwardCallIndicators".toLowerCase();
	private static final String IDP_FIELD_BEARER_CAP= "bearerCapability".toLowerCase();
	private static final String IDP_FIELD_EVENT_TYPE_BCSM= "eventTypeBCSM".toLowerCase();
	private static final String IDP_FIELD_GENERIC_NUMS= "genericNumbers".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_CONTRACTOR_NUM = "ttcContractorNum".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_CALLED_IN_NUM= "ttcCalledInNum".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_CARRIER_INFO_TRFR= "ttcCarrierInformationTransfer".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_CHARGE_AREA_INFO = "ttcChargeAreaInformation".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_ADDITIONAL_PARTY_CATG = "ttcAdditionalPartysCategory".toLowerCase();
	private static final String IDP_FIELD_EXT_TTC_SSP_CHARGE_AREA_INFO = "ttcSSPChargeAreaInformation".toLowerCase();
	
	
	private static final int IDP_CLASS = 2;

	//set
	private static final String IDP_SET_SERVICE_KEY = "serviceKey".toLowerCase();
	private static final String IDP_SET_CALLING_PARTY_NUM = "callingPartyNumber".toLowerCase();
	private static final String IDP_SET_CALLED_PARTY_NUM = "calledPartyNumber".toLowerCase();
	private static final String IDP_SET_CALLING_PARTY_CATG = "callingPartysCategory".toLowerCase();
	private static final String IDP_SET_CALLED_IN_NUM = "ttcCalledInNum".toLowerCase();
	private static final String IDP_SET_CONTRACTOR_NUM = "ttcContractorNum".toLowerCase();

	//enums
	//dialled digits, calling party, called part, contractor num, calledIn,generic num
	private static final String IDP_ENUM_NATURE_OF_ADD = "NatureOfAddEnum".toLowerCase();
	private static final String IDP_ENUM_NUM_PLAN = "NumPlanEnum".toLowerCase();
	private static final String IDP_ENUM_INT_NTW_ENUM = "IntNwNumEnum".toLowerCase();
	private static final String IDP_ENUM_SCREENING_ENUM = "ScreeningIndEnum".toLowerCase();
	private static final String IDP_ENUM_NUM_INCMPLT__ENUM = "NumIncmpltEnum".toLowerCase();
	private static final String IDP_ENUM_ADRS_PRESNT_RESTD_ENUM = "AddPrsntRestEnum".toLowerCase();
	private static final String IDP_ENUM_TTC_NATURE_OF_ADD = "TTCNatureOfAddEnum".toLowerCase();
	private static final String IDP_ENUM_NUM_QUAL_IND_ENUM = "NumQualifierIndEnum".toLowerCase();
	//callingpartycatg
	private static final String IDP_ENUM_CALG_PARTY_CATG_ENUM = "CalgPartyCatgEnum".toLowerCase();
	//terminalType
	private static final String IDP_ENUM_TERM_TYPE_ENUM_TYPE = "TerminalType.EnumType".toLowerCase();
	
	//fwcallind
	private static final String IDP_ENUM_NAT_INT_NAT_CALL_IND_ENUM = "NatIntNatCallIndEnum".toLowerCase();
	private static final String IDP_ENUM_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String IDP_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String IDP_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String IDP_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String IDP_ENUM_ISDN_USER_PART_PREF_IND_ENUM = "ISDNUserPartPrefIndEnum".toLowerCase();
	private static final String IDP_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String IDP_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	//bearercap
	private static final String IDP_ENUM_TRANS_MED_REQ_ENUM = "TransmissionMedReqEnum".toLowerCase();
	//eventtypebcssmtype
	private static final String IDP_ENUM_EVENT_TYPE_BCSM_ENUM_TYPE = "EventTypeBCSM.EnumType".toLowerCase();
	
	//ttcCarrierInfoTransfer
	private static final String IDP_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String IDP_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String IDP_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();
	
	//ttc Charge ARea Info
	private static final String IDP_ENUM_TTC_INFO_DISCR_IND_ENUM = "InfoDiscriminationIndiEnum".toLowerCase();
	
	//ttcAddtionalPartysCatg
	private static final String IDP_ENUM_TTC_ADDTNL_PARTY_CATG_NAME_ENUM= "AdtnlPartyCatNameEnum".toLowerCase();
	private static final String IDP_ENUM_TTC_ADDTNL_PARTY_CATG_1_ENUM= "AdtnlPartyCat1Enum".toLowerCase();
	private static final String IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_1_ENUM= "MobileAdtnlPartyCat1Enum".toLowerCase();
	private static final String IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_2_ENUM= "MobileAdtnlPartyCat2Enum".toLowerCase();
	private static final String IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_3_ENUM= "MobileAdtnlPartyCat3Enum".toLowerCase();
	
	//ACN
	private static final String IDP_FIELD_ACN = "acn".toLowerCase();
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (IdpHandler.class) {
				if(handler ==null){
					handler = new IdpHandler();
				}
			}
		}
		return handler;
	}

	private IdpHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside IdpHandler processNode()");

		if(!(node.getType().equals(Constants.IDP))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		IdpNode idpNode = (IdpNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		InitialDPArg idpArg = new InitialDPArg();

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		InitialDPExtension idpExt =null;
		//adding variables to CPB
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(IDP_FIELD_SRV_KEY)){
					//servicekey field
					String value = fieldElem.getValue(varMap);
					Integer4 skInt4 = new Integer4();
					skInt4.setValue(Integer.valueOf(value));
					ServiceKey srvKey = new ServiceKey();
					srvKey.setValue(skInt4);
					idpArg.setServiceKey(srvKey);
				}else if(fieldName.equals(IDP_FIELD_DIALED_DIG)){
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String natureOfAddEnum = subFieldElems.get(IDP_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					String intNwNumEnum = subFieldElems.get(IDP_ENUM_INT_NTW_ENUM).getValue(varMap);
					//CalledPArt field
					byte[] byteArrDialedDig= null;
					try {
						byteArrDialedDig = CalledPartyNum.encodeCaldParty(value, NatureOfAddEnum.valueOf(natureOfAddEnum), 
								NumPlanEnum.valueOf(numPlanEnum), IntNwNumEnum.valueOf(intNwNumEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding destinationroutAddrs in CON request",e);
						return false;
					}	
					CalledPartyNumber dialledDigits = new CalledPartyNumber();				
					dialledDigits.setValue(byteArrDialedDig);
					idpArg.setDialledDigits(dialledDigits);
				}else if(fieldName.equals(IDP_FIELD_CALLED_PARTY_NUM)){

					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String natureOfAddEnum = subFieldElems.get(IDP_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					String intNwNumEnum = subFieldElems.get(IDP_ENUM_INT_NTW_ENUM).getValue(varMap);
					//CalledPArt field
					byte[] byteArrDialedDig= null;
					try {
						byteArrDialedDig = CalledPartyNum.encodeCaldParty(value, NatureOfAddEnum.valueOf(natureOfAddEnum), 
								NumPlanEnum.valueOf(numPlanEnum), IntNwNumEnum.valueOf(intNwNumEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding destinationroutAddrs in CON request",e);
						return false;
					}	
					CalledPartyNumber calledPartyNum = new CalledPartyNumber();				
					calledPartyNum.setValue(byteArrDialedDig);
					idpArg.setCalledPartyNumber(calledPartyNum);

				}else if(fieldName.equals(IDP_FIELD_CALLING_PARTY_NUM)){

					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String natureOfAddEnum = subFieldElems.get(IDP_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					String screeningEnum = subFieldElems.get(IDP_ENUM_SCREENING_ENUM).getValue(varMap);
					String numIncomplteEnum = subFieldElems.get(IDP_ENUM_NUM_INCMPLT__ENUM).getValue(varMap);
					String adrsPresntRestdEnum = subFieldElems.get(IDP_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
					
					CallingPartyNumber callingPartyNum = new CallingPartyNumber();				
					try {
						callingPartyNum.setValue(CallingPartyNum.encodeCalgParty(value, NatureOfAddEnum.valueOf(natureOfAddEnum), 
								NumPlanEnum.valueOf(numPlanEnum),AddPrsntRestEnum.valueOf(adrsPresntRestdEnum), 
								ScreeningIndEnum.valueOf(screeningEnum), NumIncmpltEnum.valueOf(numIncomplteEnum)));
					} catch (InvalidInputException e) {
						logger.error("Error encoding calling party num",e);
						return false;
					}
					idpArg.setCallingPartyNumber(callingPartyNum);

				}else if(fieldName.equals(IDP_FIELD_CALLING_PARTYS_CATG)){
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String calgPartyCatgEnum = subFieldElems.get(IDP_ENUM_CALG_PARTY_CATG_ENUM).getValue(varMap);
					CallingPartysCategory callingPartsCatg = new CallingPartysCategory(NonAsnArg.encodeCalgPartyCatg(
							CalgPartyCatgEnum.valueOf(calgPartyCatgEnum)));

					idpArg.setCallingPartysCategory(callingPartsCatg);

				}else if(fieldName.equals(IDP_FIELD_CALLING_PARTY_SUBADD)){
					if(logger.isDebugEnabled())
						logger.debug("Unsuuported field callingPartySubAddress");
					//XXX
					
					
//					CallingPartySubaddress clpSubAdd= new CallingPartySubaddress();
//					
//					idpArg.setCallingPartySubaddress(clpSubAdd);
				}else if(fieldName.equals(IDP_FIELD_MISC_CALL_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Unsuuported field miscCallInfo");
					//XXX
				}else if(fieldName.equals(IDP_FIELD_TERMINAL_TYPE)){
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-Field terminalType");
					TerminalType termType = new TerminalType();
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String termTypeEnumType = subFieldElems.get(IDP_ENUM_TERM_TYPE_ENUM_TYPE).getValue(varMap);
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-Field terminalType value:"+termTypeEnumType);
					termType.setValue(com.genband.inap.asngenerated.TerminalType.EnumType.valueOf(termTypeEnumType));
					idpArg.setTerminalType(termType);
				}else if(fieldName.equals(IDP_FIELD_FW_CALL_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-Field subelem type FWD CALL IND");
					byte[] forwardCallIndicators = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String natIntNatCallIndEnum = subFieldElems.get(IDP_ENUM_NAT_INT_NAT_CALL_IND_ENUM).getValue(varMap);
					String endMethodIndEnum= subFieldElems.get(IDP_ENUM_END_METHOD_IND_ENUM).getValue(varMap); 
					String interNwIndEnum = subFieldElems.get(IDP_ENUM_INTER_NW_IND_ENUM).getValue(varMap);							
					String endToEndInfoIndEnum = subFieldElems.get(IDP_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
					String isdnUserPartIndEnum= subFieldElems.get(IDP_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap); 
					String isdnUserPartPrefIndEnum = subFieldElems.get(IDP_ENUM_ISDN_USER_PART_PREF_IND_ENUM).getValue(varMap); 
					String isdnAccessIndEnum = subFieldElems.get(IDP_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap); 
					String sccpMethodIndENum = subFieldElems.get(IDP_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
					try {
						forwardCallIndicators=FwCallIndicators.encodeFwCallInd(NatIntNatCallIndEnum.valueOf(natIntNatCallIndEnum), 
								EndToEndMethodIndEnum.valueOf(endMethodIndEnum), InterNwIndEnum.valueOf(interNwIndEnum), 
								EndToEndInfoIndEnum.valueOf(endToEndInfoIndEnum), ISDNUserPartIndEnum.valueOf(isdnUserPartIndEnum), 
								ISDNUserPartPrefIndEnum.valueOf(isdnUserPartPrefIndEnum), ISDNAccessIndEnum.valueOf(isdnAccessIndEnum), 
								SCCPMethodIndENum.valueOf(sccpMethodIndENum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding FwCallIndicators in IAM/invite request",e);
						return false;
					}
					ForwardCallIndicators fwdCallInd= new ForwardCallIndicators();
					fwdCallInd.setValue(forwardCallIndicators);
					idpArg.setForwardCallIndicators(fwdCallInd);

				}else if(fieldName.equals(IDP_FIELD_BEARER_CAP)){
					//					String value = fieldElem.getValue(varMap);
					BearerCapability bc = new BearerCapability();
					//XXX beareCAp not encoded bc.selectBearerCap(value)
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String transMedReq = subFieldElems.get(IDP_ENUM_TRANS_MED_REQ_ENUM).getValue(varMap);
					bc.selectTmr(NonAsnArg.encodeTmr(TransmissionMedReqEnum.valueOf(transMedReq)));

					idpArg.setBearerCapability(bc);



				}else if(fieldName.equals(IDP_FIELD_EVENT_TYPE_BCSM)){
					EventTypeBCSM eventTypeBcsm = new EventTypeBCSM();
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String eventTypeBcsmType = subFieldElems.get(IDP_ENUM_EVENT_TYPE_BCSM_ENUM_TYPE).getValue(varMap);
					eventTypeBcsm.setValue(com.genband.inap.asngenerated.EventTypeBCSM.EnumType.valueOf(eventTypeBcsmType));

					idpArg.setEventTypeBCSM(eventTypeBcsm);

				}else if(fieldName.equals(IDP_FIELD_GENERIC_NUMS)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding Genberic Number");
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String numQualifierIndEnum = subFieldElems.get(IDP_ENUM_NUM_QUAL_IND_ENUM).getValue(varMap);
					String natureOfAddEnum = subFieldElems.get(IDP_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					String screeningEnum = subFieldElems.get(IDP_ENUM_SCREENING_ENUM).getValue(varMap);
					String numIncomplteEnum = subFieldElems.get(IDP_ENUM_NUM_INCMPLT__ENUM).getValue(varMap);
					String adrsPresntRestdEnum = subFieldElems.get(IDP_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
					
					
					byte[] genNumBytes = null;
					try {
						genNumBytes = GenericNumber.encodeGenericNum(NumQualifierIndEnum.valueOf(numQualifierIndEnum),value, 
								NatureOfAddEnum.valueOf(natureOfAddEnum),NumPlanEnum.valueOf(numPlanEnum), 
								AddPrsntRestEnum.valueOf(adrsPresntRestdEnum),ScreeningIndEnum.valueOf(screeningEnum), 
								NumIncmpltEnum.valueOf(numIncomplteEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding Generic numbers in IAM/invite request",e);
						return false;
					}
					if(logger.isDebugEnabled())
						logger.debug("Got gen num byte::"+com.genband.inap.util.Util.formatBytes(genNumBytes));
					
					com.genband.inap.asngenerated.GenericNumber  genNum = new com.genband.inap.asngenerated.GenericNumber(genNumBytes);
					
					GenericNumbers genNumbers= idpArg.getGenericNumbers();
					if(genNumbers == null){
						if(logger.isDebugEnabled())
							logger.debug("First gen num, genumbers is null");
						genNumbers = new GenericNumbers();
					}
					
					Collection<com.genband.inap.asngenerated.GenericNumber> genNumList = genNumbers.getValue();
					if(genNumList==null){
						if(logger.isDebugEnabled())
							logger.debug("First element in gen num list, genumbers list is null");
						genNumList = new LinkedList<com.genband.inap.asngenerated.GenericNumber>();
					}
					genNumList.add(genNum);
					genNumbers.setValue(genNumList);
					
					idpArg.setGenericNumbers(genNumbers);
					
				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_CONTRACTOR_NUM)){
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String natureOfAddEnum = subFieldElems.get(IDP_ENUM_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					TtcContractorNumber ttcContractNum =null;
					try {
						ttcContractNum = new TtcContractorNumber(
								TtcContractorNum.encodeTtcContractorNum(value, NatureOfAddEnum.valueOf(natureOfAddEnum), 
										NumPlanEnum.valueOf(numPlanEnum)));
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcContractorNumber(ttcContractNum);
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCalledIn Number in idpExt",e);
						return false;
					}

				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_CALLED_IN_NUM)){
					String value = fieldElem.getValue(varMap);
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String ttcNatureOfAddEnum = subFieldElems.get(IDP_ENUM_TTC_NATURE_OF_ADD).getValue(varMap);
					String numPlanEnum = subFieldElems.get(IDP_ENUM_NUM_PLAN).getValue(varMap);
					String adrsPresntRestdEnum = subFieldElems.get(IDP_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
					
					com.genband.inap.asngenerated.TtcCalledINNumber ttcInNum =null;
					try {
						ttcInNum = new com.genband.inap.asngenerated.TtcCalledINNumber(
								TtcCalledINNumber.encodeTtcCalledINNum(value, TTCNatureOfAddEnum.valueOf(ttcNatureOfAddEnum), 
										NumPlanEnum.valueOf(numPlanEnum), AddPrsntRestEnum.valueOf(adrsPresntRestdEnum)));
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcCalledINNumber(ttcInNum);
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCalledIn Number in idpExt",e);
						return false;
					}
				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_CARRIER_INFO_TRFR)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcCArrierInfoTransfer");
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String carrierInfoSubordinateEnum = subFieldElems.get(IDP_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
					String carrierInfoNameEnum = subFieldElems.get(IDP_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
					String transitCarrierIndEnum = subFieldElems.get(IDP_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);
					
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
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcCarrierInformationTransfer(ttcCIT);
						if(logger.isDebugEnabled())
							logger.debug("Encoding complete");
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCarrierInfoTransfer Number in idpExt",e);
						return false;
					}
				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_CHARGE_AREA_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcChargeAreaInfo");
					String chargeAreaInfo = fieldElem.getValue(varMap);
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String infoDiscriminationIndiEnum = subFieldElems.get(IDP_ENUM_TTC_INFO_DISCR_IND_ENUM).getValue(varMap);

					com.genband.inap.asngenerated.TtcChargeAreaInformation ttcCAInfo =null;
					try {
						ttcCAInfo = new com.genband.inap.asngenerated.TtcChargeAreaInformation(
								TtcChargeAreaInfo.encodeTtcChargeAreaInfo(chargeAreaInfo, 
										InfoDiscriminationIndiEnum.valueOf(infoDiscriminationIndiEnum)));
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcChargeAreaInformation(ttcCAInfo);
					} catch (InvalidInputException e) {
						logger.error("Error encoding charge area info in idpExt",e);
						return false;
					}
				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_ADDITIONAL_PARTY_CATG)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcAdditionalPartysCatg");
										
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String additinalPartyCatgName = subFieldElems.get(IDP_ENUM_TTC_ADDTNL_PARTY_CATG_NAME_ENUM).getValue(varMap);
					String catgVal=null;
					com.genband.inap.asngenerated.TtcAdditionalPartysCategory ttcAddPartysCatg =null;
					LinkedList<TtcAdtnalPartyCategory> ttcAdtnalPartyCategories=null;
					TtcAdtnalPartyCategory ttcAdPartyCatg= new TtcAdtnalPartyCategory();
					
					AdtnlPartyCatNameEnum addtionalPartyCatName = AdtnlPartyCatNameEnum.valueOf(additinalPartyCatgName);
					ttcAdPartyCatg.setAdtnlPartyCatNameEnum(addtionalPartyCatName);
					switch(addtionalPartyCatName){
						case PSTN_CATEGORY_1:
							if(logger.isDebugEnabled())
								logger.debug("PSTN catg");
							catgVal = subFieldElems.get(IDP_ENUM_TTC_ADDTNL_PARTY_CATG_1_ENUM).getValue(varMap);
							ttcAdPartyCatg.setAdtnlPartyCat1Enum(AdtnlPartyCat1Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_1:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 1");
							catgVal = subFieldElems.get(IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_1_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat1Enum(MobileAdtnlPartyCat1Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_2:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 2");
							catgVal = subFieldElems.get(IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_2_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat2Enum(MobileAdtnlPartyCat2Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_3:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 3");
							catgVal = subFieldElems.get(IDP_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_3_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat3Enum(MobileAdtnlPartyCat3Enum.valueOf(catgVal));
							break;
						default:
							if(logger.isDebugEnabled())
								logger.debug("reserved or spare");
							break;
					}
					
					ttcAdtnalPartyCategories = new LinkedList<TtcAdtnalPartyCategory>();
					ttcAdtnalPartyCategories.add(ttcAdPartyCatg);
									
					try {
						ttcAddPartysCatg = new com.genband.inap.asngenerated.TtcAdditionalPartysCategory(
								TtcAdtnalPartyCategory.encodeTtcAdtnlPartyCat(ttcAdtnalPartyCategories));
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcAdditionalPartysCategory(ttcAddPartysCatg);
					} catch (InvalidInputException e) {
						logger.error("Error encoding ttcAddPartysCatg Number in idpExt",e);
						return false;
					}
				}else if(fieldName.equals(IDP_FIELD_EXT_TTC_SSP_CHARGE_AREA_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcSSPChargeAreaInfo");
					String chargeAreaInfo = fieldElem.getValue(varMap);
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String infoDiscriminationIndiEnum = subFieldElems.get(IDP_ENUM_TTC_INFO_DISCR_IND_ENUM).getValue(varMap);

					com.genband.inap.asngenerated.TtcChargeAreaInformation ttcCAInfo =null;
					try {
						ttcCAInfo = new com.genband.inap.asngenerated.TtcChargeAreaInformation(
								TtcChargeAreaInfo.encodeTtcChargeAreaInfo(chargeAreaInfo, 
										InfoDiscriminationIndiEnum.valueOf(infoDiscriminationIndiEnum)));
						if(idpExt == null){
							idpExt=new InitialDPExtension();
						}
						idpExt.setTtcSSPChargeAreaInformation(ttcCAInfo);
					} catch (InvalidInputException e) {
						logger.error("Error encoding SSP charge area info in idpExt",e);
						return false;
					}
				}else if(fieldName.equals(IDP_FIELD_ACN)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ACN");
					//setting acn and store dilaog portion enable if acn value is present in
					String acn = fieldElem.getValue(varMap);
					if(acn!=null && acn.matches("([0-9a-fA-F])+")&&((acn.length())%2==0)){
						simCpb.setDialoguePortionPresent(true);
						simCpb.setAppContextName(Helper.hexStringToByteArray(acn));
					}else{
						simCpb.setDialoguePortionPresent(false);
						simCpb.setAppContextName(null);
					}
				}//@End:filed type
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->fields read...saving extension if present");
		//setting extension
		if(idpExt!= null){
			if(logger.isDebugEnabled())
				logger.debug("IdpHandler processNode()-->extension present");
			//setting extension
			byte[] encodedIdpExt;
			try {
				encodedIdpExt = InapOperationsCoding.encodeIdpExt(idpExt);
				CriticalityType crit = new CriticalityType();
				if(logger.isDebugEnabled())
					logger.debug("IdpHandler processNode()-->set criticallity to ignore and type to 254");
				crit.setValue(EnumType.ignore);
				ExtensionField ef=new ExtensionField();
				ef.setValue(encodedIdpExt);
				//type for IDP extensions is 254
				ef.setType(254L);
				ef.setCriticality(crit);
				//setting extension
				Collection<ExtensionField> value=new LinkedList<ExtensionField>();
				value.add(ef);
				idpArg.setExtensions(value);
			} catch (Exception e) {
				logger.error("Error encoding Idp Extensions",e);
				return false;
			}
		}//complete extension setting

		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->Extension set encode IDPArg");
		//getting IDP byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(idpArg);
		opCode.add(InapOpCodes.IDP);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enidpcoding IDP to byte array",e);
			return false;
		}
		byte[] idp = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for IDPHandler--> Got Idp byte array:: "+Util.formatBytes(idp));
		
		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->generating Dialog ID");
		int dialogId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->Got dialog ID["+dialogId+"]");
		simCpb.setDialogId(dialogId);
		simCpb.setTcap(true);

		//storing call data
		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogId, simCpb);

		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->idp byte array generated creating reqEvent["+idp+"]");

		//generate idp component req event
		byte[] IdpOpCode = {0x00} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, IdpOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, idp));
		ire.setClassType(IDP_CLASS);
		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending idp component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->component send");
		//if last message generate dialog
		if(idpNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("IdpHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),idpNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("IdpHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on IDP::"+idpNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on IDP::"+idpNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving IdpHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for IdpHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("IdpHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for IdpHandler--> varMAp is"+varMap);

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing IDP message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] paramsIdp;
		InitialDPArg idp = null;

		try{
			paramsIdp =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for IdpHandler--> starting first level decoding on IDP bytes:: "+Util.formatBytes(paramsIdp));
			idp = (InitialDPArg)InapOperationsCoding.decodeOperation(paramsIdp, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(idp == null){
			logger.error("idp is null");
			return false;
		}

		InitialDPExtension idpExt = null;
		if(idp.isExtensionsPresent()){
			Collection<ExtensionField> extensionList=idp.getExtensions();
			if(extensionList!=null){
				try{
					idpExt = InapOperationsCoding.decodeInitialDPExt(extensionList.iterator().next().getValue());
				}catch (Exception e){
					logger.error("Error decoding Exceptions");
					return false;
				}
			}
		}

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for IdpHandler--> first level decode operation complete Got IDP::"+idp);


		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->creating new variable");
					var = new Variable();
					var.setVarName(varName);
				}

				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for IdpHandler--variable created");

				String varField = setElem.getVarField();
				//				int startIndex = setElem.getStartIndx();
				//				int endIndx= setElem.getEndIndx();
				String varVal = null;


				if(varField.equals(IDP_SET_SERVICE_KEY)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set srvkey");
					int srvKey = idp.getServiceKey().getValue().getValue();
					varVal = Integer.toString(srvKey);
				} else if(varField.equals(IDP_SET_CALLING_PARTY_NUM)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set callingPArtyNum");
					if(idp.isCallingPartyNumberPresent()){
						try {
							CallingPartyNum clpNum = CallingPartyNum.decodeCalgParty(idp
									.getCallingPartyNumber().getValue());
							varVal = clpNum.getAddrSignal();
						} catch (Exception e) {
							logger.error("Exit due to calling party decode failed with exception",e);
							return false;
						}
					}else{
						logger.error("Exit due to calling party not present");
						return false;
					}
				}else if(varField.equals(IDP_SET_CALLED_PARTY_NUM)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set calledPArtyNum");
					if(idp.isCalledPartyNumberPresent()){
						try {
							CalledPartyNum clpNum = CalledPartyNum.decodeCaldParty(idp
									.getCalledPartyNumber().getValue());
							varVal = clpNum.getAddrSignal();
						} catch (Exception e) {
							logger.error("Exit due to called party decode failed with exception",e);
							return false;
						}
					}else{
						logger.error("Exit due to called party not present");
						return false;
					}
				}else if(varField.equals(IDP_SET_CALLING_PARTY_CATG)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set callingPArtycatg");
					if(idp.isCallingPartysCategoryPresent()){
						try {
							CalgPartyCatgEnum clpCatg = NonAsnArg.decodeCalgPartyCatg(idp.getCallingPartysCategory().getValue());
							varVal = clpCatg.name();
						} catch (Exception e) {
							logger.error("Exit due to calling party catg decode failed with exception",e);
							return false;
						}
					}else{
						logger.error("Exit due to calling party catg not present");
						return false;
					}
				}else if(varField.equals(IDP_SET_CALLED_IN_NUM)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set calledIn num");
					if(idpExt!=null){
						com.genband.inap.asngenerated.TtcCalledINNumber generatedCalledInNum= idpExt.getTtcCalledINNumber();
						if(generatedCalledInNum!=null){
							try {
								varVal = TtcCalledINNumber.decodeTtcCalledINNum(generatedCalledInNum.getValue()).getAddrSignal();
							} catch (InvalidInputException e) {
								logger.error("Exception decoding calledIn num");
								return false;
							}
						}else{
							logger.error("Exit due to CalledInNum not present");
							return false;
						}
					}else{
						logger.error("Exit due to extension not present");
						return false;
					}
				}else if(varField.equals(IDP_SET_CONTRACTOR_NUM)){
					if(logger.isDebugEnabled())
						logger.debug("processRecievedMessage() for IdpHandler-->set contractorNum");
					if(idpExt!=null){
						TtcContractorNumber ttcContractorNum= idpExt.getTtcContractorNumber();
						if(ttcContractorNum!=null){
							try {
								varVal = TtcContractorNum.decodeTtcContractorNum(ttcContractorNum.getValue()).getAddrSignal();
							} catch (InvalidInputException e) {
								logger.error("Exception decoding contractorNum");
								return false;
							}
						}else{
							logger.error("Exit due to contractorNum not present");
							return false;
						}
					}else{
						logger.error("Exit due to extension not present");
						return false;
					}
				}
				//finally storing variable
				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for IdpHandler-->store variable in CPB::");
				var.setVarValue(varVal);
				simCpb.addVariable(var);
				if(logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for IdpHandler-->gVariable in CPB stores");

			}//end if check for set elem
		}//end while loop on subelem

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for IdpHandler leaving with status true");
		return true;

	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for IdpHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.IDP) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a IDP  Node");
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
				logger.debug("IdpHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
