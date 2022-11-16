package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.BodyElem;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.FromElem;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.child.ToElem;
import com.agnity.simulator.callflowadaptor.element.child.UriElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.AdditionalPartyCat;
import com.genband.isup.datatypes.AdditionalPartyCatPair;
import com.genband.isup.datatypes.CalledPartyNum;
import com.genband.isup.datatypes.CallingPartyNum;
import com.genband.isup.datatypes.CarrierIdentificationCode;
import com.genband.isup.datatypes.CarrierInfoSubordinate;
import com.genband.isup.datatypes.CarrierInformation;
import com.genband.isup.datatypes.DPCInfo;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.GenericDigits;
import com.genband.isup.datatypes.GenericNumber;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.datatypes.ScfId;
import com.genband.isup.datatypes.TtcCalledINNumber;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.AdditionalPartyCat1Enum;
import com.genband.isup.enumdata.AdditionalPartyCatNameEnum;
import com.genband.isup.enumdata.CalgPartyCatgEnum;
import com.genband.isup.enumdata.CarrierInfoNameEnum;
import com.genband.isup.enumdata.CarrierInfoSubordinateEnum;
import com.genband.isup.enumdata.ContCheckIndEnum;
import com.genband.isup.enumdata.DigitCatEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EncodingSchemeEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.GTIndicatorEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.isup.enumdata.InfoDiscriminationIndiEnum;
import com.genband.isup.enumdata.IngressTrunkCategoryEnum;
import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.MemberStatusIndEnum;
import com.genband.isup.enumdata.MobileAdditionalPartyCat1Enum;
import com.genband.isup.enumdata.MobileAdditionalPartyCat2Enum;
import com.genband.isup.enumdata.NatIntNatCallIndEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.NumQualifierIndEnum;
import com.genband.isup.enumdata.RoutingIndicatorEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SPCIndicatorEnum;
import com.genband.isup.enumdata.SSNIndicatorEnum;
import com.genband.isup.enumdata.SatelliteIndEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.enumdata.TTCNatureOfAddEnum;
import com.genband.isup.enumdata.TransitCarrierIndEnum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.IAMMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.NonAsnArg;
import com.genband.isup.util.Util;

public class InviteHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(InviteHandler.class);
	private static Handler handler;

	private static final String IAM_FIELD_CALLED_PARTY_NUM= "CalledPartyNumber".toLowerCase();
	private static final String IAM_FIELD_CALLING_PARTY_NUM= "CallingPartyNumber".toLowerCase();
	private static final String IAM_FIELD_CALLED_IN_NUM= "CalledINNumber".toLowerCase();
	private static final String IAM_FIELD_CORRELATION_ID = "CorrelationId".toLowerCase();
	private static final String IAM_FIELD_NATURE_OF_CONN_IND = "NatureOfConnIndicators".toLowerCase();
	private static final String IAM_FIELD_FWD_CALL_IND = "ForwardCallIndicators".toLowerCase();
	private static final String IAM_FIELD_CALLING_PARTY_CATG= "CallingPartyCategory".toLowerCase();
	private static final String IAM_FIELD_TRANS_MED_REQ = "TransMediumReq".toLowerCase();
	private static final String IAM_FIELD_SCF_ID = "scfId".toLowerCase();
	private static final String IAM_FIELD_ADDITIONAL_PARTY_CATG = "additionalPartyCategory".toLowerCase();
	private static final String IAM_FIELD_CARRIER_INFO_TRFR = "carrierInformationTransfer".toLowerCase();
	private static final String IAM_FIELD_DPC_INFO = "dpcInfo".toLowerCase();
	private static final String IAM_FIELD_CHARGE_AREA_INFO = "chargeAreaInformation".toLowerCase();
	private static final String IAM_FIELD_GENERIC_NUM = "genericNum".toLowerCase();;



	//ENUMS
	//called part calling part and called ind num
	private static final String IAM_ENUM_NATURE_OF_ADD = "NatureOfAddEnum".toLowerCase();
	private static final String IAM_ENUM_NUM_QUALIF = "numQualifierIndEnum".toLowerCase();
	private static final String IAM_ENUM_NUM_PLAN = "NumPlanEnum".toLowerCase();
	private static final String IAM_ENUM_INT_NTW_ENUM = "IntNwNumEnum".toLowerCase();
	private static final String IAM_ENUM_SCREENING_ENUM = "ScreeningIndEnum".toLowerCase();
	private static final String IAM_ENUM_NUM_INCMPLT__ENUM = "NumIncmpltEnum".toLowerCase();
	private static final String IAM_ENUM_ADRS_PRESNT_RESTD_ENUM = "AddPrsntRestEnum".toLowerCase();
	private static final String IAM_ENUM_TTC_NATURE_OF_ADD = "TTCNatureOfAddEnum".toLowerCase();

	//correlation id
	private static final String IAM_ENUM_ENCODING_SCHEME_ENUM = "EncodingSchemeEnum".toLowerCase();
	private static final String IAM_ENUM_DIGIT_CAT_ENUM = "DigitCatEnum".toLowerCase();

	//nature of conn ind
	private static final String IAM_ENUM_SATELLITE_IND_ENUM = "SatelliteIndEnum".toLowerCase();
	private static final String IAM_ENUM_CONT_CHECK_ENUM = "ContCheckIndEnum".toLowerCase();
	private static final String IAM_ENUM_ECHO_CONT_DEVICE_IND_ENUM = "EchoContDeviceIndEnum".toLowerCase();

	//fwcallind
	private static final String IAM_ENUM_NAT_INT_NAT_CALL_IND_ENUM = "NatIntNatCallIndEnum".toLowerCase();
	private static final String IAM_ENUM_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String IAM_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String IAM_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String IAM_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String IAM_ENUM_ISDN_USER_PART_PREF_IND_ENUM = "ISDNUserPartPrefIndEnum".toLowerCase();
	private static final String IAM_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String IAM_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	//calling party catg
	private static final String IAM_ENUM_CALG_PARTY_CATG_ENUM = "CalgPartyCatgEnum".toLowerCase();
	private static final String IAM_ENUM_TRANS_MED_REQ_ENUM = "TransmissionMedReqEnum".toLowerCase();
	
	//scfID
	private static final String IAM_ENUM_SPC_IND_ENUM = "SPCIndicatorEnum".toLowerCase();
	private static final String IAM_ENUM_SSN_IND_ENUM = "SSNIndicatorEnum".toLowerCase();
	private static final String IAM_ENUM_GT_IND_ENUM = "GTIndicatorEnum".toLowerCase();
	private static final String IAM_ENUM_ROUTING_IND_ENUM = "RoutingIndicatorEnum".toLowerCase();
	private static final String IAM_SUBFIELD_ZONE_PC = "zone_PC".toLowerCase();
	private static final String IAM_SUBFIELD_NET_PC = "net_PC".toLowerCase();
	private static final String IAM_SUBFIELD_SP_PC = "sp_PC".toLowerCase();
	
	//additionalPArtyCAtg
	private static final String IAM_ENUM_ADDITIONAL_PARTY_CATG_NAME = "categoryName".toLowerCase();
	private static final String IAM_ENUM_ADDITIONAL_PARTY_CATG_VALUE = "categoryValue".toLowerCase();
	
	//ttcCarrierInfoTransfer
	private static final String IAM_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String IAM_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String IAM_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();
	
	//dpcInfo
	private static final Object IAM_SUBFIELD_DPC_CODE1 = "dpcCode1".toLowerCase();
	private static final Object IAM_SUBFIELD_DPC_CODE2 = "dpcCode2".toLowerCase();
	private static final Object IAM_SUBFIELD_DA_CALL_IND = "daCallIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_MEMBER_CHK_STATUS = "memberCheckStatusIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_NO_CALL_IND = "noIdCallIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_ORIG_BYPASS_IND = "originatingByPassIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_ORIG_IGS_IND = "originatingIgsGcIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_PREFIX_IND = "prefix0088Indicator".toLowerCase();
	private static final Object IAM_SUBFIELD_PUB_CALL_IND = "publicCallIndicator".toLowerCase();
	private static final Object IAM_SUBFIELD_VIRTUAL_NW_IND = "virtualNwHandOffIndicator".toLowerCase();
	private static final Object IAM_ENUM_INGRESS_TRUNK_CATG = "IngressTrunkCategoryEnum".toLowerCase();
	private static final Object IAM_ENUM_MEMBER_STATUS_IND = "MemberStatusIndEnum".toLowerCase();
	
	//ttc Charge ARea Info
	private static final String IAM_ENUM_TTC_INFO_DISCR_IND_ENUM = "InfoDiscriminationIndiEnum".toLowerCase();
	
	//HEader
	private static final String HEADER_PAI = "P-Asserted-Identity";
		
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (InviteHandler.class) {
				if(handler ==null){
					handler = new InviteHandler();
				}
			}
		}
		return handler;
	}

	private InviteHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside InviteHandler processNode()");

		if(!(node.getType().equals(Constants.INVITE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		BodyElem bodyElem =null;
		Node subElem =null;

		Map<String, Variable> varMap = simCpb.getVariableMap();

		String fromUri="sip:";
		String toUri="sip:";
		String requestUri ="sip:";
		List<Header> headerList= new ArrayList<Header>();

		IAMMessage iamMessage= null;
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.URI)){
				UriElem uriElem = (UriElem) subElem; 
				requestUri+= uriElem.getUri(varMap);
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->request URI created::"+requestUri);
			}else if(subElem.getType().equals(Constants.TO)){
				ToElem toElem = (ToElem) subElem;
				toUri+= toElem.getTo(varMap);
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->to URI created::"+toUri);
			}else if(subElem.getType().equals(Constants.FROM)){
				FromElem fromElem = (FromElem) subElem;
				fromUri+=fromElem.getFrom(varMap);
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->from URI created::"+fromUri);
			}else if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				String headerName = headerElem.getName();
				String headerValue = null;
				if(headerName.equalsIgnoreCase(HEADER_PAI)){
					headerName= HEADER_PAI;
					headerValue = Helper.getValueForHedaerUri(headerElem.getValue(),varMap);
				}else{
					headerValue = headerElem.getValue();
				}
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->header name::"+headerName+" value::"+headerValue);
				headerList.add( new Header(headerName,headerValue) );

			}else if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-BODY elem checking sub elems for body");
				FieldElem fieldElem = null;
				bodyElem = (BodyElem)subElem;
				Node bodySubElem = null;
				Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();
				if(!subElem.getSubElements().isEmpty())
				{
					iamMessage = new IAMMessage();
					iamMessage.setMessageType(new byte[]{0x01});
				}
				while(bodySubElemIterator.hasNext()){
					bodySubElem = bodySubElemIterator.next();
					if(bodySubElem.getType().equals(Constants.FIELD)){
						if(logger.isDebugEnabled())
							logger.debug("Inside InviteHandler processNode()-Field subelem checking field type");
						fieldElem  = (FieldElem) bodySubElem; 
						String fieldName = fieldElem.getFieldType();

						if(fieldName.equals(IAM_FIELD_CALLED_PARTY_NUM)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type calledpartynum");
							byte[] calledPartyNum = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String natureOfAddEnum = subFieldElems.get(IAM_ENUM_NATURE_OF_ADD).getValue(varMap);
							String numPlanEnum = subFieldElems.get(IAM_ENUM_NUM_PLAN).getValue(varMap);
							String intNwNumEnum = subFieldElems.get(IAM_ENUM_INT_NTW_ENUM).getValue(varMap);
							try {
								calledPartyNum = CalledPartyNum.encodeCaldParty(fieldElem.getValue(varMap), NatureOfAddEnum.valueOf(natureOfAddEnum), 
										NumPlanEnum.valueOf(numPlanEnum), IntNwNumEnum.valueOf(intNwNumEnum));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding CalledPartyNum in IAM/invite request",e);
								return false;
							}
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-->calledPArtyNum::"+Util.formatBytes(calledPartyNum));
							iamMessage.setCalledPartyNumber(calledPartyNum);
						}else if(fieldName.equals(IAM_FIELD_CALLING_PARTY_NUM)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type calling party num");
							byte[] callingPartyNum = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String natureOfAddEnum = subFieldElems.get(IAM_ENUM_NATURE_OF_ADD).getValue(varMap);
							String numPlanEnum = subFieldElems.get(IAM_ENUM_NUM_PLAN).getValue(varMap);
							String screeningEnum = subFieldElems.get(IAM_ENUM_SCREENING_ENUM).getValue(varMap);
							String numIncomplteEnum = subFieldElems.get(IAM_ENUM_NUM_INCMPLT__ENUM).getValue(varMap);
							String adrsPresntRestdEnum = subFieldElems.get(IAM_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
							try {
								callingPartyNum = CallingPartyNum.encodeCalgParty(fieldElem.getValue(varMap), 
										NatureOfAddEnum.valueOf(natureOfAddEnum), NumPlanEnum.valueOf(numPlanEnum), 
										AddPrsntRestEnum.valueOf(adrsPresntRestdEnum), ScreeningIndEnum.valueOf(screeningEnum),
										NumIncmpltEnum.valueOf(numIncomplteEnum));

							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding CallingPartyNum in IAM/invite request",e);
								return false;
							}
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-->calllingPArty::"+Util.formatBytes(callingPartyNum));
							iamMessage.setCallingPartyNumber(callingPartyNum);
						}else if(fieldName.equals(IAM_FIELD_CALLED_IN_NUM)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type calledINnum");
							byte[] calledInNum = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String natureOfAddEnum = subFieldElems.get(IAM_ENUM_TTC_NATURE_OF_ADD).getValue(varMap);
							String numPlanEnum = subFieldElems.get(IAM_ENUM_NUM_PLAN).getValue(varMap);
							String adrsPresntRestdEnum = subFieldElems.get(IAM_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
							try {
								calledInNum = 
									TtcCalledINNumber.encodeTtcCalledINNum(fieldElem.getValue(varMap), 
											TTCNatureOfAddEnum.valueOf(natureOfAddEnum), NumPlanEnum.valueOf(numPlanEnum), 
											AddPrsntRestEnum.valueOf(adrsPresntRestdEnum));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding CalledInNum in IAM/invite request",e);
								return false;
							}
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-->calledInNum::"+Util.formatBytes(calledInNum));
							iamMessage.setCalledINNumber(calledInNum);
						}else if(fieldName.equals(IAM_FIELD_CORRELATION_ID)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type correlation id");
							byte[] correlationId = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String encodingSchemeEnum = subFieldElems.get(IAM_ENUM_ENCODING_SCHEME_ENUM).getValue(varMap);
							String digitCatEnum = subFieldElems.get(IAM_ENUM_DIGIT_CAT_ENUM).getValue(varMap);
							try {
								correlationId = 
									GenericDigits.encodeGenericDigits(EncodingSchemeEnum.valueOf(encodingSchemeEnum), 
											DigitCatEnum.valueOf(digitCatEnum), fieldElem.getValue(varMap));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding correlationId in IAM/invite request",e);
								return false;
							}
							iamMessage.setCorrelationId(correlationId);
						}else if(fieldName.equals(IAM_FIELD_NATURE_OF_CONN_IND)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type nature of conn ind");
							byte[] natureOfConnIndicators = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String satelliteIndEnum = subFieldElems.get(IAM_ENUM_SATELLITE_IND_ENUM).getValue(varMap);
							String contCheckIndEnum = subFieldElems.get(IAM_ENUM_CONT_CHECK_ENUM).getValue(varMap);
							String echoContDeviceIndEnum = subFieldElems.get(IAM_ENUM_ECHO_CONT_DEVICE_IND_ENUM).getValue(varMap);
							try {
								natureOfConnIndicators = NatOfConnIndicators.encodeConnIndicators(SatelliteIndEnum.valueOf(satelliteIndEnum), 
										ContCheckIndEnum.valueOf(contCheckIndEnum), EchoContDeviceIndEnum.valueOf(echoContDeviceIndEnum));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding NatOfConnIndicators in IAM/invite request",e);
								return false;
							}
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-nature of conn INd"+natureOfConnIndicators+
										"  length::"+natureOfConnIndicators.length);
							iamMessage.setNatureOfConnIndicators(natureOfConnIndicators);
						}else if(fieldName.equals(IAM_FIELD_FWD_CALL_IND)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type FWD CALL IND");
							byte[] forwardCallIndicators = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String natIntNatCallIndEnum = subFieldElems.get(IAM_ENUM_NAT_INT_NAT_CALL_IND_ENUM).getValue(varMap);
							String endMethodIndEnum= subFieldElems.get(IAM_ENUM_END_METHOD_IND_ENUM).getValue(varMap); 
							String interNwIndEnum = subFieldElems.get(IAM_ENUM_INTER_NW_IND_ENUM).getValue(varMap);							
							String endToEndInfoIndEnum = subFieldElems.get(IAM_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
							String isdnUserPartIndEnum= subFieldElems.get(IAM_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap); 
							String isdnUserPartPrefIndEnum = subFieldElems.get(IAM_ENUM_ISDN_USER_PART_PREF_IND_ENUM).getValue(varMap); 
							String isdnAccessIndEnum = subFieldElems.get(IAM_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap); 
							String sccpMethodIndENum = subFieldElems.get(IAM_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
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
							iamMessage.setForwardCallIndicators(forwardCallIndicators);
						}else if(fieldName.equals(IAM_FIELD_CALLING_PARTY_CATG)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type callingPartyCatg");
							byte[] callingPartyCatg = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String calgPartyCatgEnum = subFieldElems.get(IAM_ENUM_CALG_PARTY_CATG_ENUM).getValue(varMap);
							callingPartyCatg=NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.valueOf(calgPartyCatgEnum));
							iamMessage.setCallingPartyCategory(callingPartyCatg);
						}else if(fieldName.equals(IAM_FIELD_TRANS_MED_REQ)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type TMR");
							byte[] tmr = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String tmrEnum = subFieldElems.get(IAM_ENUM_TRANS_MED_REQ_ENUM).getValue(varMap);
							tmr= NonAsnArg.encodeTmr(TransmissionMedReqEnum.valueOf(tmrEnum));
							iamMessage.setTmr(tmr);
						}else if(fieldName.equals(IAM_FIELD_SCF_ID)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type SCFID");
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							byte[]scfId = null;
							String pc = fieldElem.getValue(varMap);
							if(pc!=null   &&   !(pc.trim().equals(""))  ){
								int pcVal=Integer.parseInt(pc);	
								try {
									scfId = ScfId.encodeScfId(pcVal);
								} catch (InvalidInputException e) {
									logger.error("InvalidInputException encoding default  scfId in IAM request",e);
									return false;
								}
							}else{
								String spcIndicatorEnum = subFieldElems.get(IAM_ENUM_SPC_IND_ENUM).getValue(varMap);
								String ssnIndicatorEnum = subFieldElems.get(IAM_ENUM_SSN_IND_ENUM).getValue(varMap);
								String gtIndicatorEnum = subFieldElems.get(IAM_ENUM_GT_IND_ENUM).getValue(varMap);
								String routingIndicatorEnum = subFieldElems.get(IAM_ENUM_ROUTING_IND_ENUM).getValue(varMap);
								int zone_PC= Integer.parseInt(
										subFieldElems.get(IAM_SUBFIELD_ZONE_PC).getValue(varMap));
								int net_PC=Integer.parseInt(
										subFieldElems.get(IAM_SUBFIELD_NET_PC).getValue(varMap));
								int sp_PC=Integer.parseInt(
										subFieldElems.get(IAM_SUBFIELD_SP_PC).getValue(varMap));
								
								
								try {
									scfId =ScfId.encodeScfId(SPCIndicatorEnum.valueOf(spcIndicatorEnum), 
											SSNIndicatorEnum.valueOf(ssnIndicatorEnum),GTIndicatorEnum.valueOf(gtIndicatorEnum), 
											RoutingIndicatorEnum.valueOf(routingIndicatorEnum), zone_PC, net_PC, sp_PC);
								} catch (InvalidInputException e) {
									logger.error("InvalidInputException encoding scfId in IAM request",e);
									return false;
								}
								
							}
							iamMessage.setScfId(scfId);
						}else if(fieldName.equals(IAM_FIELD_ADDITIONAL_PARTY_CATG)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type addtional pArty CAtg");
							byte[] apCatg = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String catgName = subFieldElems.get(IAM_ENUM_ADDITIONAL_PARTY_CATG_NAME).getValue(varMap);
							String catgValue = subFieldElems.get(IAM_ENUM_ADDITIONAL_PARTY_CATG_VALUE).getValue(varMap);
							
							int catgNameCode =AdditionalPartyCatNameEnum.valueOf(catgName).getCode();
							int catgValueCode= -1;
							switch(catgNameCode){
							case 252:
								catgValueCode = MobileAdditionalPartyCat2Enum.valueOf(catgValue).getCode();
								break;
							case 253:
								catgValueCode = MobileAdditionalPartyCat1Enum.valueOf(catgValue).getCode();
								break;
							case 254: 
								catgValueCode = AdditionalPartyCat1Enum.valueOf(catgValue).getCode();
								break;
							}
							AdditionalPartyCatPair apcp = new AdditionalPartyCatPair();
							apcp.setAdditionalPartyCatPair(catgNameCode, catgValueCode);
							LinkedList<AdditionalPartyCatPair> catNameList = new LinkedList<AdditionalPartyCatPair>();
							catNameList.add(apcp);							
							try {
								apCatg= AdditionalPartyCat.encodeAdditionalPartyCategory(catNameList);
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding additional PArty Catg in IAM request",e);
								return false;
							}
							Map<Integer,byte[]> otherOptParams= iamMessage.getOtherOptParams();
							if (otherOptParams == null){
								otherOptParams = new HashMap<Integer, byte[]>();
								iamMessage.setOtherOptParams(otherOptParams);
							}
							otherOptParams.put(ISUPConstants.CODE_ADDITIONAL_PARTY_CAT,apCatg);
							//note supported in IAM encoding						
							//iamMessage.setAdditionalPartyCat(apCatg);
						}else if(fieldName.equals(IAM_FIELD_CARRIER_INFO_TRFR)){
//							String citStr=fieldElem.getValue(varMap);
//							byte[] cit = Helper.hexStringToByteArray(citStr);
							
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type CarrierInfoTrfr");
							byte[] carrierInfoTrfr = null;
							String value = fieldElem.getValue(varMap);
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							
							String carrierInfoSubordinateEnum = subFieldElems.get(IAM_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
							String carrierInfoNameEnum = subFieldElems.get(IAM_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
							String transitCarrierIndEnum = subFieldElems.get(IAM_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);
							
							
							try {
							//	
								CarrierIdentificationCode cic=new CarrierIdentificationCode();
								cic.setCarrierIdentCode(value);

								CarrierInfoSubordinate cis=new CarrierInfoSubordinate();
								cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.valueOf(carrierInfoSubordinateEnum));
								cis.setCarrierIdentificationCode(cic);

								
								LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate =new LinkedList<CarrierInfoSubordinate>();
								carrierInfoSubordinate.add(cis);
								
								CarrierInformation carrierInfo = new CarrierInformation();
								//carrierInfo.setCarrierInfoLength(carrierInfoLength)
								carrierInfo.setCarrierInfoNameEnum(CarrierInfoNameEnum.valueOf(carrierInfoNameEnum));
								carrierInfo.setCarrierInfoSubordinate(carrierInfoSubordinate);

								LinkedList<CarrierInformation> carrirInfoList=new LinkedList<CarrierInformation>();
								carrirInfoList.add(carrierInfo);
								
								
								carrierInfoTrfr = TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(
										TransitCarrierIndEnum.valueOf(transitCarrierIndEnum),carrirInfoList );
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding CarrierInfoTrfr in IAM/invite request",e);
								return false;
							}
							Map<Integer,byte[]> otherOptParams= iamMessage.getOtherOptParams();
							if (otherOptParams == null){
								otherOptParams = new HashMap<Integer, byte[]>();
								iamMessage.setOtherOptParams(otherOptParams);
							}
							otherOptParams.put(ISUPConstants.CODE_CARRIER_INFO_TRFR,carrierInfoTrfr);
							//note supported in IAM encoding						
						}else if(fieldName.equals(IAM_FIELD_DPC_INFO)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type DPCInfo");
							byte[] dpcInfoBytes =null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							
							String dpcCode1 = subFieldElems.get(IAM_SUBFIELD_DPC_CODE1).getValue(varMap);
							String dpcCode2 = subFieldElems.get(IAM_SUBFIELD_DPC_CODE2).getValue(varMap);
							
							String dACallInd = subFieldElems.get(IAM_SUBFIELD_DA_CALL_IND).getValue(varMap);
							String memberChkSts = subFieldElems.get(IAM_SUBFIELD_MEMBER_CHK_STATUS).getValue(varMap);
							String noCallInd = subFieldElems.get(IAM_SUBFIELD_NO_CALL_IND).getValue(varMap);
							String origByPassInd = subFieldElems.get(IAM_SUBFIELD_ORIG_BYPASS_IND).getValue(varMap);
							String origIGSInd = subFieldElems.get(IAM_SUBFIELD_ORIG_IGS_IND).getValue(varMap);
							String prefixInd = subFieldElems.get(IAM_SUBFIELD_PREFIX_IND).getValue(varMap);
							String pubCallInd = subFieldElems.get(IAM_SUBFIELD_PUB_CALL_IND).getValue(varMap);
							String virtualNwInd = subFieldElems.get(IAM_SUBFIELD_VIRTUAL_NW_IND).getValue(varMap);
							
							String ingressTrunkCategory = subFieldElems.get(IAM_ENUM_INGRESS_TRUNK_CATG).getValue(varMap);
							String memberStatusInd = subFieldElems.get(IAM_ENUM_MEMBER_STATUS_IND).getValue(varMap);
			
							
							DPCInfo dpcInfo = new DPCInfo();
							dpcInfo.setDPCCode1(Helper.hexStringToByteArray(dpcCode1));
							dpcInfo.setDPCCode2(Helper.hexStringToByteArray(dpcCode2));

							dpcInfo.setIngressTrunkCategory(IngressTrunkCategoryEnum.valueOf(ingressTrunkCategory));
							dpcInfo.setMemberStatusInd(MemberStatusIndEnum.valueOf(memberStatusInd));
							
							if(dACallInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->dACallInd not null::"+dACallInd);
								dpcInfo.setDACallInd(Byte.parseByte(dACallInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->dACallInd null:: use default::"+1);
								dpcInfo.setDACallInd((byte) 1);
							}
							
							if(memberChkSts !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->memberChkSts not null::"+memberChkSts);
								dpcInfo.setMemberCheckStatusIndicator(Byte.parseByte(memberChkSts));							
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->memberChkSts null:: use default::"+0);
								dpcInfo.setMemberCheckStatusIndicator((byte) 0);
							}
						
							if(noCallInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->noCallInd not null::"+noCallInd);
								dpcInfo.setNoIdCallIndicator(Byte.parseByte(noCallInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->noCallInd null:: use default::"+1);
								dpcInfo.setNoIdCallIndicator((byte) 1);
							}
							
							if(origByPassInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->origByPassInd not null::"+origByPassInd);
								dpcInfo.setOriginatingByPassIndicator(Byte.parseByte(origByPassInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->origByPassInd null:: use default::"+1);
								dpcInfo.setOriginatingByPassIndicator((byte) 1);
							}
							
							if(origIGSInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->origIGSInd not null::"+origIGSInd);
								dpcInfo.setOriginatingIGSIndicator(Byte.parseByte(origIGSInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->origIGSInd null:: use default::"+1);
								dpcInfo.setOriginatingIGSIndicator((byte) 1);
							}
							
							if(prefixInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->prefixInd not null::"+prefixInd);
								dpcInfo.setPrefix0088Indicator(Byte.parseByte(prefixInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->prefixInd null:: use default::"+1);
								dpcInfo.setPrefix0088Indicator((byte) 1);
							}
							
							if(pubCallInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->pubCallInd not null::"+pubCallInd);
								dpcInfo.setPublicCallIndicator(Byte.parseByte(pubCallInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->pubCallInd null:: use default::"+1);
								dpcInfo.setPublicCallIndicator((byte) 1);
							}
							
							if(virtualNwInd !=null){
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->virtualNwInd not null::"+virtualNwInd);
								dpcInfo.setVirtualNetworkHandOffIndicator(Byte.parseByte(virtualNwInd));
							}else{
								if(logger.isDebugEnabled())
									logger.debug("processNode()->Field DPCInfo->virtualNwInd null:: use default::"+1);
								dpcInfo.setVirtualNetworkHandOffIndicator((byte) 1);
							}
							
							try {
								dpcInfoBytes = DPCInfo.encodeDPCInfo(dpcInfo);
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding DPCINFO in IAM/invite request",e);
								return false;
							}
							
							
							//iamMessage.setDPCInfo(dpcInfoBytes);
							Map<Integer,byte[]> otherOptParams= iamMessage.getOtherOptParams();
							if (otherOptParams == null){
								otherOptParams = new HashMap<Integer, byte[]>();
								iamMessage.setOtherOptParams(otherOptParams);
							}
							otherOptParams.put(ISUPConstants.CODE_DPC_INFO,dpcInfoBytes);
						}else if(fieldName.equals(IAM_FIELD_CHARGE_AREA_INFO)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type charge area info");
							byte[] chargeAreaInfoBytes =null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							
							String chargeAreaInfo = fieldElem.getValue(varMap);
							String infoDiscriminationIndiEnum = subFieldElems.get(IAM_ENUM_TTC_INFO_DISCR_IND_ENUM).getValue(varMap);
							try {
								chargeAreaInfoBytes = TtcChargeAreaInfo.encodeTtcChargeAreaInfo(chargeAreaInfo, 
										InfoDiscriminationIndiEnum.valueOf(infoDiscriminationIndiEnum));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding DPCINFO in IAM/invite request",e);
								return false;
							}							
							
							//iamMessage.setChargeAreaInformation(chargeAreaInfoBytes);
							
							Map<Integer,byte[]> otherOptParams= iamMessage.getOtherOptParams();
							if (otherOptParams == null){
								otherOptParams = new HashMap<Integer, byte[]>();
								iamMessage.setOtherOptParams(otherOptParams);
							}
							otherOptParams.put(ISUPConstants.CODE_CHARGE_AREA_INFO,chargeAreaInfoBytes);
							
						}else if(fieldName.equals(IAM_FIELD_GENERIC_NUM)){
							if(logger.isDebugEnabled())
								logger.debug("Inside InviteHandler processNode()-Field subelem type generic num");
							byte[] genNumBytes =null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							
							String genNum = fieldElem.getValue(varMap);
							String numQualifierIndEnum = subFieldElems.get(IAM_ENUM_NUM_QUALIF).getValue(varMap);
							String natureOfAdrsEnum = subFieldElems.get(IAM_ENUM_NATURE_OF_ADD).getValue(varMap);
							String numberingPlanEnum = subFieldElems.get(IAM_ENUM_NUM_PLAN).getValue(varMap);
							String adrsPresntRestdEnum = subFieldElems.get(IAM_ENUM_ADRS_PRESNT_RESTD_ENUM).getValue(varMap);
							String screeningEnum = subFieldElems.get(IAM_ENUM_SCREENING_ENUM).getValue(varMap);
							String numIncomplte = subFieldElems.get(IAM_ENUM_NUM_INCMPLT__ENUM).getValue(varMap);
							try {	 
								genNumBytes = GenericNumber.encodeGenericNum(NumQualifierIndEnum.valueOf(numQualifierIndEnum), 
										genNum, NatureOfAddEnum.valueOf(natureOfAdrsEnum), NumPlanEnum.valueOf(numberingPlanEnum), 
										AddPrsntRestEnum.valueOf(adrsPresntRestdEnum), ScreeningIndEnum.valueOf(screeningEnum), 
										NumIncmpltEnum.valueOf(numIncomplte));
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding genericnum in IAM/invite request",e);
								return false;
							}							
							
							iamMessage.setGenericNumber(genNumBytes);
						
						}//@END field elem type
					}//@END ELEMNT type is field
				}//complete body sub elem while

			}//complete body sub elem 

		}//complete while
		
		if(logger.isDebugEnabled())
			logger.debug("Inside InviteHandler processNode()-->Creating sipmessage");

		SipFactory factory = InapIsupSimServlet.getInstance().getFactory();
		
		SipApplicationSession appSession = null;
		String sdp = null;
		if(simCpb.isB2bCall() == true) {
			appSession = simCpb.getSipAppSession();			
		} else {
			appSession = factory.createApplicationSession();			
		}
		
		//SipApplicationSession appSession = factory.createApplicationSession();
		int leg = node.getSipLeg(); 
		if(enableSdp&&lastSavedSdp)
		{
			//if we are sending our message on leg1 than we will use lastsdpcontent from leg2 and vice versa
			if(leg==1)
				sdp = simCpb.getLastSdpContentLeg2();
			else if(leg==2)
				sdp = simCpb.getLastSdpContentLeg1();
			else
				sdp = simCpb.getLastSdpContent();
		}
		else if(enableSdp){
			if(bodyElem!=null)
			{
				sdp = bodyElem.getSdp();
				logger.debug("setting ur defined sdp");
			}
			else{
				logger.debug("Control will never reach here");
				sdp = InapIsupSimServlet.getInstance().getConfigData().getSdp();
				}
		}
		
		try {
			SipServletRequest request=null;
			boolean isReinvite = ((SipNode)node).isReInvite();
			
			if(((leg==1)&&(simCpb.getLastSipMessageLeg1()!=null))&&isReinvite)
			{
				request = simCpb.getLeg1().createRequest("INVITE");
			}
			else if(((leg==2)&&(simCpb.getLastSipMessageLeg2()!=null))&&isReinvite)
			{
				request = simCpb.getLeg2().createRequest("INVITE");
			}
			else if(((simCpb.getLastSipMessage()!=null))&&isReinvite)
			{
				request = simCpb.getLastSipMessage().getSession().createRequest("INVITE");
			}
			else
				request = factory.createRequest(appSession, "INVITE", fromUri, toUri);
			
			if(simCpb.isB2bCall() == true) {
				if(simCpb.getLeg1() != null && simCpb.getLeg2() == null) {
					SipSession session = request.getSession();
					simCpb.setLeg2(session);
					session.setAttribute(Constants.INTIAL_INVITE, request);
				}
				if(simCpb.getLeg2() != null && simCpb.getLeg1() == null) {
					SipSession session = request.getSession();
					simCpb.setLeg1(session);
					session.setAttribute(Constants.INTIAL_INVITE, request);
				}
				
				try {
					
					//here let's say if send action is on leg 1 than receive earlier was on leg2,
					//so originalInvite was received on leg2
					/*int leg = node.getSipLeg();
					if(leg==1)
						sdp = simCpb.getOrigInviteRequestLeg2().getContent().toString();
					else if(leg==2)
						sdp = simCpb.getOrigInviteRequestLeg1().getContent().toString();					
					*/
				} catch(Exception ex) {
					ex.printStackTrace();
				}			
				
				if(logger.isDebugEnabled())
					logger.debug("Leg 1 : " + simCpb.getLeg1() + ". Leg 2 : " + simCpb.getLeg2());
				
				//simCpb.getOrigInviteRequest().getSession().setAttribute("PEER_SESSION", request.getSession());
				//request.getSession().setAttribute("PEER_SESSION", simCpb.getOrigInviteRequest().getSession());
			} 
			
			request.setRequestURI(factory.createURI(requestUri));
			if(logger.isDebugEnabled())
				logger.debug("Inside InviteHandler processNode()-->request created uri set to::"+requestUri);

			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				request.addHeader(header.getHeaderName(), header.getHeaderValue());
			}
			
			if(logger.isDebugEnabled())
				logger.debug("Inside InviteHandler processNode()-->Checking IAM content");
			if(iamMessage != null){
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(iamMessage);
				opCode.add(ISUPConstants.OP_CODE_IAM);
				try {
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-->Encoding IAM content");
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] iam =  encode.get(0);
					Multipart mp = new MimeMultipart();	
					//adding SDP to multipart
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-->Creating Multipart storing SDP conetnt part");
					if(enableSdp){
						if(logger.isDebugEnabled())
							logger.debug("add SDP as enabled for message");
						Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					}
					//adding IAM to multipart
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-->Creating sipmessage storing IAM content part msg=["+Util.formatBytes(iam)+"]");
					Helper.formMultiPartMessage(mp, iam, Constants.ISUP_CONTENT_TYPE);
					
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()-->setting multipart");
					request.setContent(mp,mp.getContentType());
					
					if(logger.isDebugEnabled())
						logger.debug("Inside InviteHandler processNode()--> multipart content set");

				} catch (MessagingException e) {
					logger.error("MessagingException creating and setting multipat message",e);
					return false;
				}catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException setting content",e);
					return false;
				}catch (Exception e) {
					logger.error("Exception encoding IAM message",e);
					return false;
				}

			}else{
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->Pure sip message setting SDP in message");
				if(enableSdp){
					if(logger.isDebugEnabled())
						logger.debug("add SDP as enabled for message");
					request.setContent(sdp,Constants.SDP_CONTENT_TYPE);
				}
				if(logger.isDebugEnabled())
					logger.debug("Inside InviteHandler processNode()-->SDP set");
			}
			if(logger.isDebugEnabled())
				logger.debug("Inside InviteHandler processNode()-->storing variables");
			String callId = request.getCallId();
			simCpb.setCallId(callId);
			
			//int leg = node.getSipLeg();
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(request);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(request);
			else
				simCpb.setLastSipMessage(request);
			
			
			if(leg==1)
				simCpb.setOrigInviteRequestLeg1(request);
			else if(leg==2)
				simCpb.setOrigInviteRequestLeg2(request);
			else
				simCpb.setOrigInviteRequest(request);
			
			simCpb.setSipAppSession(request.getApplicationSession());
			InapIsupSimServlet.getInstance().getSipCallData().put(callId, simCpb);
			InapIsupSimServlet.getInstance().getAppSessionIdCallData().put(request.getApplicationSession().getId(),simCpb );
			
			if(logger.isDebugEnabled())
				logger.debug("Inside InviteHandler processNode()-->sending sipmessage::"+request);
//			if(simCpb.isSupportsReliable())
//				request.addHeader(Constants.HDR_REQUIRE, Constants.VALUE_100REL);
			
			simCpb.setSupportsReliable(Helper.supports100Rel(request));
			
			
			request.send();
		} catch (ServletParseException e) {
			logger.error("ServletParseException creating invite request",e);
			return false;
		} catch (IOException e) {
			logger.error("IOException sending invite request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving InviteHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InviteHandler");

		if(!(node.getType().equals(Constants.INVITE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		
		SipServletRequest inviteReq = (SipServletRequest) message;
		String callId = inviteReq.getCallId();
		boolean isReliable = Helper.supports100Rel(inviteReq);
		
		
		//code added by aarti
		if(node.getSipLeg() == 1) {
			simCpb.setLeg1(inviteReq.getSession());
			simCpb.setB2bCall(true);
		}
		if(node.getSipLeg() == 2) {
			simCpb.setLeg2(inviteReq.getSession());
			simCpb.setB2bCall(true);
		}
		inviteReq.getSession().setAttribute(Constants.INTIAL_INVITE, inviteReq);
		
		
		simCpb.setCallId(callId);
		
		int leg = node.getSipLeg();
		if(leg == 1)
			simCpb.setLastSipMessageLeg1(inviteReq);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2(inviteReq);
		else
			simCpb.setLastSipMessage(inviteReq);
		
		
		if(leg==1)
			simCpb.setOrigInviteRequestLeg1(inviteReq);
		else if(leg==2)
			simCpb.setOrigInviteRequestLeg2(inviteReq);
		else
			simCpb.setOrigInviteRequest(inviteReq);
		
		simCpb.setSipAppSession(inviteReq.getApplicationSession());
		InapIsupSimServlet.getInstance().getAppSessionIdCallData().put(inviteReq.getApplicationSession().getId(),simCpb );
		//simCpb.setSupportsReliable(isReliable);
		
		if(leg ==1 )
			simCpb.setSupportsReliableLeg1(isReliable);
		else if(leg ==2)
			simCpb.setSupportsReliableLeg2(isReliable);
		else
			simCpb.setSupportsReliable(isReliable);
		
		InapIsupSimServlet.getInstance().getSipCallData().put(callId, simCpb);
		
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(inviteReq.getContentType()!=null)
					{
						if(inviteReq.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
							sdp = inviteReq.getRawContent();
						else if(inviteReq.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
							 {
								mp = (Multipart)inviteReq.getContent();
								int i = 0;
								while(i<mp.getCount())
								{
									bodyPart = mp.getBodyPart(i);
									if(bodyPart.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE)){
										ByteArrayInputStream bais=(ByteArrayInputStream) bodyPart.getContent();	
										int bytesNum=bais.available();
										sdp=new byte[bytesNum];
										bais.read(sdp,0,bytesNum);
									}
									i++;
								}
							 }
					}
					if(sdp!=null)
						sdptext = new String(sdp);
					if(leg==1)
						simCpb.setLastSdpContentLeg1(sdptext);
					else if(leg==2)
						simCpb.setLastSdpContentLeg2(sdptext);
					else
						simCpb.setLastSdpContent(sdptext);
					
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("Encoding is not supported"+e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("IO exception when getting media type"+e);
			}catch (MessagingException e) {
				logger.error("MessagingException getting bodypart",e);
				return false;
			}
		}
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("InviteHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}
		
		try {			
			String contentType = inviteReq.getContentType();
			//check if multipart content present
			byte[] isupContent= null;
			if(contentType!=null && contentType.startsWith(Constants.MULTIPART_CONTENT_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("InviteHandler processRecievedMessage()->Multipart content present");
						mp = (Multipart) inviteReq.getContent();
				int bpCount =mp.getCount();
				if(logger.isDebugEnabled())
					logger.debug("InviteHandler processRecievedMessage()->Checking for ISUP content type["+Constants.ISUP_CONTENT_TYPE+"]");
				for(int i =0; i<bpCount;i++){
					BodyPart bp = mp.getBodyPart(i);
					if(logger.isDebugEnabled())
						logger.debug("InviteHandler processRecievedMessage()->Matching body part content type["+bp.getContentType()+"]");
					if(bp.getContentType().equals(Constants.ISUP_CONTENT_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("InviteHandler processRecievedMessage()->ISUP content found");
						ByteArrayInputStream bis=(ByteArrayInputStream) bp.getContent();	
						int bytes=bis.available();
						isupContent=new byte[bytes];
						bis.read(isupContent,0,bytes);
						break;
					}
				}//end for loop
			}else{
				logger.error("InviteHandler processRecievedMessage()->Multipart content type not present so return with false");
				return false; 
			}
			
			if(isupContent == null){
				logger.error("InviteHandler processRecievedMessage()->ISUP content not found so return with false");
				return false; 
			}
			if(logger.isDebugEnabled())
				logger.debug("InviteHandler processRecievedMessage()->Recived IAM bytes::["+Util.formatBytes(isupContent)+"]");
			
			//parsing IAM
			LinkedList<byte[]> byteList=new LinkedList<byte[]>();
			byteList.add(isupContent);
			LinkedList<String> opCodeList=new LinkedList<String>();
			opCodeList.add(ISUPConstants.OP_CODE_IAM);

			List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
			IAMMessage iamMessage = (IAMMessage) list.get(0);
			//XXX store required elems form iamMessage
			if(logger.isDebugEnabled())
				logger.debug("InviteHandler processRecievedMessage()->IAM message parsed:: "+iamMessage);
			

		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException getting content",e);
			return false;
		} catch (IOException e) {
			logger.error("IOException getting content",e);
			return false;
		} catch (MessagingException e) {
			logger.error("MessagingException getting bodypart",e);
			return false;
		}
		
		

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for InviteHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for InviteHandler");

		if(!(message instanceof SipServletRequest)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip request message");
			return false;
		}

		SipServletRequest sipRequest= (SipServletRequest) message;
		if(!( node.getType().equals(Constants.INVITE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a INVITE Node");
			return false;
		}
		if(!(sipRequest.getMethod().equals("INVITE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Not an INVITE method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		//changes for b2b mode,this check is to avoid handling of an unexpected leg message
	/*	if(InapIsupSimServlet.getInstance().isB2bMode){
			int leg = node.getSipLeg();
			String callId="";
			if(leg==1)
				callId = simCpb.getLastSipMessageLeg1().getCallId();
			else if(leg==2)
				callId = simCpb.getLastSipMessageLeg2().getCallId();
			if(callId.equalsIgnoreCase(((SipServletRequest) message).getCallId()))
				return true;
			else
				return false;
		}
		*/
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for InviteHandler with status "+true);
		return true;
	}



}
