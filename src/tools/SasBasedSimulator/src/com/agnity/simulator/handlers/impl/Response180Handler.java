package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.BodyElem;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ProvResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.CarrierIdentificationCode;
import com.genband.isup.enumdata.CarrierInfoSubordinateEnum;
import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.CarrierInfoSubordinate;
import com.genband.isup.datatypes.CarrierInformation;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.datatypes.EventInfo;
import com.genband.isup.datatypes.OptBwCallIndicators;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.enumdata.CallDiversionIndEnum;
import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.CarrierInfoNameEnum;
import com.genband.isup.enumdata.CauseValEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.EventIndEnum;
import com.genband.isup.enumdata.EventPrsntRestIndEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.InbandInfoIndEnum;
import com.genband.isup.enumdata.InfoDiscriminationIndiEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.LocationEnum;
import com.genband.isup.enumdata.MLPPUserIndEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SimpleSegmentationIndEnum;
import com.genband.isup.enumdata.TransitCarrierIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.ACMMessage;
import com.genband.isup.messagetypes.CPGMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.Util;


public class Response180Handler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(Response180Handler.class);
	private static Handler handler;

	//CPG
	//fields
	private static final String CPG_FIELD_EVENT_INFO = "EventInfo".toLowerCase();
	private static final String CPG_FIELD_BW_CALL_IND = "BwCallIndicators".toLowerCase();
	
	//Enums
	private static final String CPG_ENUM_EVENT_IND_ENUM = "EventIndEnum".toLowerCase();
	private static final String CPG_ENUM_EVENT_PRSNT_REST_IND_ENUM = "EventPrsntRestIndEnum".toLowerCase();
	
	private static final String CPG_ENUM_CHARGE_IND_ENUM = "ChargeIndEnum".toLowerCase();
	private static final String CPG_ENUM_CALLED_PARTY_STATUS_IND_ENUM = "CalledPartyStatusIndEnum".toLowerCase();
	private static final String CPG_ENUM_CALLED_PARTY_CAT_IND_ENUM = "CalledPartyCatIndEnum".toLowerCase();
	private static final String CPG_ENUM_END_TO_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String CPG_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String CPG_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String CPG_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String CPG_ENUM_HOLDING_IND_ENUM = "HoldingIndEnum".toLowerCase();
	private static final String CPG_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String CPG_ENUM_ECHO_CONT_DEVICE_IND_ENUM = "EchoContDeviceIndEnum".toLowerCase();
	private static final String CPG_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	
	///ACM
	//fields
	private static final String ACM_FIELD_BW_CALL_IND = "BwCallIndicators".toLowerCase();
	private static final String ACM_FIELD_OPT_BW_CALL_IND = "OptBwCallIndicators".toLowerCase();
	private static final String ACM_FIELD_CARRIER_INFO_TRFR = "CarrierInfoTrfr".toLowerCase();
	private static final String ACM_FIELD_CHARGE_AREA_INFO = "ChargeAreaInfo".toLowerCase();
	private static final String ACM_FIELD_CAUSE_IND = "CauseIndicators".toLowerCase();
	
	//Enums
	private static final String ACM_ENUM_CHARGE_IND_ENUM = "ChargeIndEnum".toLowerCase();
	private static final String ACM_ENUM_CALLED_PARTY_STATUS_IND_ENUM = "CalledPartyStatusIndEnum".toLowerCase();
	private static final String ACM_ENUM_CALLED_PARTY_CAT_IND_ENUM = "CalledPartyCatIndEnum".toLowerCase();
	private static final String ACM_ENUM_END_TO_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String ACM_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String ACM_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String ACM_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String ACM_ENUM_HOLDING_IND_ENUM = "HoldingIndEnum".toLowerCase();
	private static final String ACM_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String ACM_ENUM_ECHO_CONT_DEVICE_IND_ENUM = "EchoContDeviceIndEnum".toLowerCase();
	private static final String ACM_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	
	//optbackwardcallInd
	private static final String ACM_ENUM_IN_BAND_INFO_IND_ENUM = "InbandInfoIndEnum".toLowerCase();
	private static final String ACM_ENUM_CALL_DIVERSION_IND_ENUM = "CallDiversionIndEnum".toLowerCase();
	private static final String ACM_ENUM_SIMPLE_SEGMENTAION_IND_ENUM = "SimpleSegmentationIndEnum".toLowerCase();
	private static final String ACM_ENUM_MLPP_USR_IND_ENUM = "MLPPUserIndEnum".toLowerCase();
	
	//ttcCarrierInfoTransfer
	private static final String ACM_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String ACM_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String ACM_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();
	
	//ttc Charge ARea Info
	private static final String ACM_ENUM_TTC_INFO_DISCR_IND_ENUM = "InfoDiscriminationIndiEnum".toLowerCase();
	
	//causeInd
	private static final String ACM_LOCATION_ENUM = "LocationEnum".toLowerCase();
	private static final String ACM_CODING_STND_ENUM = "CodingStndEnum".toLowerCase();
	private static final String ACM_CAUSE_VALUE_ENUM = "CauseValEnum".toLowerCase();
	
	
	
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Response180Handler.class) {
				if(handler ==null){
					handler = new Response180Handler();
				}
			}
		}
		return handler;
	}

	private Response180Handler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Response180Handler processNode()");

		if(!(node.getType().equals(Constants.INVITE_PROV_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		
		ProvResNode provNode = (ProvResNode) node;
		int status = Integer.parseInt(provNode.getMessage());
		if(status != 180){
			logger.error("Not a 180 Ringing node, return with status false");
			return false;
		}
		
		BodyElem bodyElem =null;
		SipServletResponse invite180Resp= null;
		
		int leg = node.getSipLeg();
		SipServletMessage message = null;
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();
		
		
		SipServletRequest inviteReq = null;
		if(leg==1)
			 inviteReq= simCpb.getOrigInviteRequestLeg1();
		else if(leg==2)
			inviteReq= simCpb.getOrigInviteRequestLeg2();
		else
			inviteReq= simCpb.getOrigInviteRequest();
		
		if(message ==null || inviteReq ==null){
			logger.error("Last message not present cant send response or invite request is not present lastmessage::"+
					message+"  InviteReq::"+inviteReq);
			return false;
		}

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();


		Node subElem =null;

		Map<String, Variable> varMap = simCpb.getVariableMap();
		List<Header> headerList= new ArrayList<Header>();

		CPGMessage cpgMessage= null;
		ACMMessage acmMessage = null;
		
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}else if(subElem.getType().equals(Constants.BODY)){
				bodyElem = (BodyElem)subElem;
				if(!subElem.getSubElements().isEmpty())
				{
				String bodyType = ((BodyElem)subElem).getBodyType();
				if(bodyType == null || bodyType.equalsIgnoreCase(Constants.CPG)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-create CPG");
					cpgMessage = createCpg(subElem,varMap);
				}else if(bodyType.equalsIgnoreCase(Constants.ACM)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-create ACM");
					acmMessage = createAcm(subElem,varMap);
				}else{
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Unknown Body type");
				}
			   }//end of if
			}//end of else if

		}//complete while

		String sdp="";
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
			else
				{
				logger.debug("Control will never reach here");
				sdp = InapIsupSimServlet.getInstance().getConfigData().getSdp();
				}
		}
		
		try {
			invite180Resp = inviteReq.createResponse(status);
				
			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				invite180Resp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}

			if(cpgMessage != null){
				if(cpgMessage.getMessageType() == null){
					logger.error("Error creating CPG message");
					return false;
				}
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(cpgMessage);
				opCode.add(ISUPConstants.OP_CODE_CPG);
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] cpg =  encode.get(0);
					Multipart mp = new MimeMultipart();	
					//adding SDP to multipart
					if(enableSdp){
						if(logger.isDebugEnabled())
							logger.debug("add SDP as enabled for message");
						Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					}
					//adding CPG to multipart
					Helper.formMultiPartMessage(mp, cpg, Constants.ISUP_CONTENT_TYPE);
					invite180Resp.setContent(mp,mp.getContentType());

				} catch (MessagingException e) {
					logger.error("MessagingException creating and setting multipat message",e);
					return false;
				}catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException setting content",e);
					return false;
				}catch (Exception e) {
					logger.error("Exception encoding CPG message",e);
					return false;
				}
			}else if(acmMessage != null){
				if(acmMessage.getMessageType() == null){
					logger.error("Error creating ACM message");
					return false;
				}
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(acmMessage);
				opCode.add(ISUPConstants.OP_CODE_ACM);
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] acm =  encode.get(0);
					
					Multipart mp = new MimeMultipart();	
					//adding SDP to multipart
					if(enableSdp){
						if(logger.isDebugEnabled())
							logger.debug("add SDP as enabled for message");
						Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					}
					//adding ACM to multipart
					Helper.formMultiPartMessage(mp, acm, Constants.ISUP_CONTENT_TYPE);
					invite180Resp.setContent(mp,mp.getContentType());

				} catch (MessagingException e) {
					logger.error("MessagingException creating and setting multipat message",e);
					return false;
				}catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException setting content",e);
					return false;
				}catch (Exception e) {
					logger.error("Exception encoding REL message",e);
					return false;
				}
			}else{
				if(enableSdp){
					if(logger.isDebugEnabled())
						logger.debug("add SDP as enabled for message");
					invite180Resp.setContent(sdp,Constants.SDP_CONTENT_TYPE);
				}
			}
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(invite180Resp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(invite180Resp);
			else
				simCpb.setLastSipMessage(invite180Resp);
			
			/*if(simCpb.isSupportsReliable()){
				invite180Resp.sendReliably();
			}else{
				invite180Resp.send();
			}*/
			
			//enable sdp check is to avoid sending a response reliable unless sdp is not in it
			if(leg == 1){
				
				if(simCpb.isSupportsReliableLeg1()&&enableSdp){
					invite180Resp.sendReliably();
				}else{
					invite180Resp.send();
				}
				
			}else if(leg ==2){
				
				if(simCpb.isSupportsReliableLeg2()&&enableSdp){
					invite180Resp.sendReliably();
				}else{
					invite180Resp.send();
				}
				
			}else if(simCpb.isSupportsReliable()&&enableSdp){
			
				invite180Resp.sendReliably();
			
			}else{
			
				invite180Resp.send();
			
			}
			
		} catch (IOException e) {
			logger.error("IOException sending 180 resp",e);
			return false;
		} catch (Rel100Exception e) {
			logger.error("Rel100Exception sending 180 resp",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Response180Handler processNode() with status true");
		return true;


	}

	private ACMMessage createAcm(Node subElem, Map<String, Variable> varMap) {

		if(logger.isDebugEnabled())
			logger.debug("Inside Response183Handler processNode()-BODY elem checking sub elems for body");
		FieldElem fieldElem = null;
		Node bodySubElem = null;
		Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();
		ACMMessage acmMessage = new ACMMessage();
		acmMessage.setMessageType(new byte[]{0x06});
		while(bodySubElemIterator.hasNext()){
			bodySubElem = bodySubElemIterator.next();
			if(bodySubElem.getType().equals(Constants.FIELD)){
				if(logger.isDebugEnabled())
					logger.debug("Inside Response183Handler processNode()-Field subelem checking field type");
				fieldElem  = (FieldElem) bodySubElem; 
				String fieldName = fieldElem.getFieldType();

				if(fieldName.equals(ACM_FIELD_BW_CALL_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type BwCallInd");
					byte[] bwCallIndicators = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String chargeIndEnum = subFieldElems.get(ACM_ENUM_CHARGE_IND_ENUM).getValue(varMap);
					String calledPartyStatusIndEnum = subFieldElems.get(ACM_ENUM_CALLED_PARTY_STATUS_IND_ENUM).getValue(varMap);
					String calledPartyCatIndEnum = subFieldElems.get(ACM_ENUM_CALLED_PARTY_CAT_IND_ENUM).getValue(varMap);
					String endToEndMethodIndEnum = subFieldElems.get(ACM_ENUM_END_TO_END_METHOD_IND_ENUM).getValue(varMap);
					String interNwIndEnum = subFieldElems.get(ACM_ENUM_INTER_NW_IND_ENUM).getValue(varMap);
					String endToEndInfoIndEnum = subFieldElems.get(ACM_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
					String isdnUserPartIndEnum = subFieldElems.get(ACM_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap);
					String holdingIndEnum = subFieldElems.get(ACM_ENUM_HOLDING_IND_ENUM).getValue(varMap);
					String isdnAccessIndEnum = subFieldElems.get(ACM_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap);
					String echoContDeviceIndEnum = subFieldElems.get(ACM_ENUM_ECHO_CONT_DEVICE_IND_ENUM).getValue(varMap);
					String sccpMethodIndENum = subFieldElems.get(ACM_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
					try {
						bwCallIndicators = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.valueOf(chargeIndEnum), 
								CalledPartyStatusIndEnum.valueOf(calledPartyStatusIndEnum),
								CalledPartyCatIndEnum.valueOf(calledPartyCatIndEnum), 
								EndToEndMethodIndEnum.valueOf(endToEndMethodIndEnum),InterNwIndEnum.valueOf(interNwIndEnum), 
								EndToEndInfoIndEnum.valueOf(endToEndInfoIndEnum),ISDNUserPartIndEnum.valueOf(isdnUserPartIndEnum), 
								HoldingIndEnum.valueOf(holdingIndEnum),ISDNAccessIndEnum.valueOf(isdnAccessIndEnum), 
								EchoContDeviceIndEnum.valueOf(echoContDeviceIndEnum),SCCPMethodIndENum.valueOf(sccpMethodIndENum));								
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding BwCallInd in ACM/180 response",e);
						acmMessage.setMessageType(null);
						return acmMessage;
					}
					acmMessage.setBackwardCallIndicators(bwCallIndicators);
				}else if(fieldName.equals(ACM_FIELD_OPT_BW_CALL_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type OptBwCallInd");
					byte[] optBwCallIndicators = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String inbandInfoIndEnum = subFieldElems.get(ACM_ENUM_IN_BAND_INFO_IND_ENUM).getValue(varMap);
					String callDiversionIndEnum = subFieldElems.get(ACM_ENUM_CALL_DIVERSION_IND_ENUM).getValue(varMap);
					String simpleSegmentationIndEnum = subFieldElems.get(ACM_ENUM_SIMPLE_SEGMENTAION_IND_ENUM).getValue(varMap);
					String mlppUserIndEnum = subFieldElems.get(ACM_ENUM_MLPP_USR_IND_ENUM).getValue(varMap);
					try {
						optBwCallIndicators = OptBwCallIndicators.encodeOptBwCallInd(InbandInfoIndEnum.valueOf(inbandInfoIndEnum), 
								CallDiversionIndEnum.valueOf(callDiversionIndEnum), 
								SimpleSegmentationIndEnum.valueOf(simpleSegmentationIndEnum), 
								MLPPUserIndEnum.valueOf(mlppUserIndEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding OptBwCallInd in ACM/180 response",e);
						acmMessage.setMessageType(null);
						return acmMessage;
					}
					acmMessage.setOptBwCallIndicators(optBwCallIndicators);

				}else if(fieldName.equals(ACM_FIELD_CARRIER_INFO_TRFR)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type CarrierInfoTrfr");
					byte[] carrierInfoTrfr = null;
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String carrierInfoSubordinateEnum = subFieldElems.get(ACM_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
					String carrierInfoNameEnum = subFieldElems.get(ACM_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
					String transitCarrierIndEnum = subFieldElems.get(ACM_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);
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
						logger.error("InvalidInputException encoding CarrierInfoTrfr in ACM/180 response",e);
						acmMessage.setMessageType(null);
						return acmMessage;
					}
					acmMessage.setCarrierInfoTrfr(carrierInfoTrfr);

				}else if(fieldName.equals(ACM_FIELD_CHARGE_AREA_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type chargeAreaInfo");
					byte[] chargeAreaInfo = null;
					String chargeAreaValue= fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String infoDiscriminationIndiEnum = subFieldElems.get(ACM_ENUM_TTC_INFO_DISCR_IND_ENUM).getValue(varMap);
					try {
						chargeAreaInfo = TtcChargeAreaInfo.encodeTtcChargeAreaInfo(chargeAreaValue, 
								InfoDiscriminationIndiEnum.valueOf(infoDiscriminationIndiEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding chargeAreaInfo in ACM/180 response",e);
						acmMessage.setMessageType(null);
						return acmMessage;
					}
					acmMessage.setChargeAreaInfo(chargeAreaInfo);

				}else if(fieldName.equals(ACM_FIELD_CAUSE_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type CauseInd");
					byte[] causeIndicators = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String locationEnum = subFieldElems.get(ACM_LOCATION_ENUM).getValue(varMap);
					String codingStdEnum = subFieldElems.get(ACM_CODING_STND_ENUM).getValue(varMap);
					String causeValueEnum = subFieldElems.get(ACM_CAUSE_VALUE_ENUM).getValue(varMap);
					try {
						
						causeIndicators= Cause.encodeCauseVal(LocationEnum.valueOf(locationEnum), 
								CodingStndEnum.valueOf(codingStdEnum), 
								CauseValEnum.valueOf(causeValueEnum));

					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding CauseInd in ACM/180 response",e);
						acmMessage.setMessageType(null);
						return acmMessage;
					}
					acmMessage.setCauseIndicators(causeIndicators);

				}//@end if fieldNAme
			}//@end if field
		}//@end while
		return acmMessage;

	
	}

	private CPGMessage createCpg(Node subElem, Map<String, Variable> varMap) {

		if(logger.isDebugEnabled())
			logger.debug("Inside Response180Handler processNode()-BODY elem checking sub elems for body");
		FieldElem fieldElem = null;
		Node bodySubElem = null;
		Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();
		CPGMessage cpgMessage = new CPGMessage();
		cpgMessage.setMessageType(new byte[]{0x2C});
		while(bodySubElemIterator.hasNext()){
			bodySubElem = bodySubElemIterator.next();
			if(bodySubElem.getType().equals(Constants.FIELD)){
				if(logger.isDebugEnabled())
					logger.debug("Inside Response180Handler processNode()-Field subelem checking field type");
				fieldElem  = (FieldElem) bodySubElem; 
				String fieldName = fieldElem.getFieldType();

				if(fieldName.equals(CPG_FIELD_EVENT_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type eventInfo");
					byte[] eventInfo = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String eventIndEnum = subFieldElems.get(CPG_ENUM_EVENT_IND_ENUM).getValue(varMap);
					String eventPrsntRestIndEnum = subFieldElems.get(CPG_ENUM_EVENT_PRSNT_REST_IND_ENUM).getValue(varMap);
					try {
						
						eventInfo =EventInfo.encodeEventInfo(EventIndEnum.valueOf(eventIndEnum), 
								EventPrsntRestIndEnum.valueOf(eventPrsntRestIndEnum));
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding eventInfo in CPG/180 response",e);
						cpgMessage = new CPGMessage();
						return cpgMessage;
					}
					cpgMessage.setEvenntInfo(eventInfo);
				}else if(fieldName.equals(CPG_FIELD_BW_CALL_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response180Handler processNode()-Field subelem type BwCallInd");
					byte[] bwCallIndicators = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String chargeIndEnum = subFieldElems.get(CPG_ENUM_CHARGE_IND_ENUM).getValue(varMap);
					String calledPartyStatusIndEnum = subFieldElems.get(CPG_ENUM_CALLED_PARTY_STATUS_IND_ENUM).getValue(varMap);
					String calledPartyCatIndEnum = subFieldElems.get(CPG_ENUM_CALLED_PARTY_CAT_IND_ENUM).getValue(varMap);
					String endToEndMethodIndEnum = subFieldElems.get(CPG_ENUM_END_TO_END_METHOD_IND_ENUM).getValue(varMap);
					String interNwIndEnum = subFieldElems.get(CPG_ENUM_INTER_NW_IND_ENUM).getValue(varMap);
					String endToEndInfoIndEnum = subFieldElems.get(CPG_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
					String isdnUserPartIndEnum = subFieldElems.get(CPG_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap);
					String holdingIndEnum = subFieldElems.get(CPG_ENUM_HOLDING_IND_ENUM).getValue(varMap);
					String isdnAccessIndEnum = subFieldElems.get(CPG_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap);
					String echoContDeviceIndEnum = subFieldElems.get(CPG_ENUM_ECHO_CONT_DEVICE_IND_ENUM).getValue(varMap);
					String sccpMethodIndENum = subFieldElems.get(CPG_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
					try {
						bwCallIndicators = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.valueOf(chargeIndEnum), 
								CalledPartyStatusIndEnum.valueOf(calledPartyStatusIndEnum),
								CalledPartyCatIndEnum.valueOf(calledPartyCatIndEnum), 
								EndToEndMethodIndEnum.valueOf(endToEndMethodIndEnum),InterNwIndEnum.valueOf(interNwIndEnum), 
								EndToEndInfoIndEnum.valueOf(endToEndInfoIndEnum),ISDNUserPartIndEnum.valueOf(isdnUserPartIndEnum), 
								HoldingIndEnum.valueOf(holdingIndEnum),ISDNAccessIndEnum.valueOf(isdnAccessIndEnum), 
								EchoContDeviceIndEnum.valueOf(echoContDeviceIndEnum),SCCPMethodIndENum.valueOf(sccpMethodIndENum));								
					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding BwCallInd in CPG/180 response",e);
						cpgMessage = new CPGMessage();
						return cpgMessage;
					}
					cpgMessage.setBwCallIndicators(bwCallIndicators);
				}//@End: if fieldName
			}//@End:if field
		}//@End:while
		return cpgMessage;

	
		
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Response180Handler");

		if(!(node.getType().equals(Constants.INVITE_PROV_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		SipServletResponse provMessage = (SipServletResponse) message;
		boolean isReliable = Helper.isReliable(provMessage);
		
		int leg = node.getSipLeg();
		if(leg == 1)
			simCpb.setLastSipMessageLeg1(provMessage);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2(provMessage);
		else
			simCpb.setLastSipMessage(provMessage);
		
		//simCpb.setSupportsReliable(isReliable);
		
		if(leg ==1 )
			simCpb.setSupportsReliableLeg1(isReliable);
		else if(leg ==2)
			simCpb.setSupportsReliableLeg2(isReliable);
		else
			simCpb.setSupportsReliable(isReliable);
		
		
		
		//code added for extracting sdp out of incoming message
		SipServletMessage res180Message = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(res180Message.getContentType()!=null)
					{
					if(res180Message.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = res180Message.getRawContent();
					else if(res180Message.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)res180Message.getContent();
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
				logger.debug("Response180Handler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}
		try {			
			String contentType = provMessage.getContentType();
			//check if multipart content present
			byte[] isupContent= null;
			if(contentType!=null && contentType.startsWith(Constants.MULTIPART_CONTENT_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("Response180Handler processRecievedMessage()->Multipart content present");
					mp = (Multipart) provMessage.getContent();
				int bpCount =mp.getCount();
				if(logger.isDebugEnabled())
					logger.debug("Response180Handler processRecievedMessage()->Checking for ISUP content type["+Constants.ISUP_CONTENT_TYPE+"]");
				for(int i =0; i<bpCount;i++){
					BodyPart bp = mp.getBodyPart(i);
					if(logger.isDebugEnabled())
						logger.debug("Response180Handler processRecievedMessage()->Matching body part content type["+bp.getContentType()+"]");
					if(bp.getContentType().equals(Constants.ISUP_CONTENT_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("Response180Handler processRecievedMessage()->ISUP content found");
						ByteArrayInputStream bis=(ByteArrayInputStream) bp.getContent();	
						int bytes=bis.available();
						isupContent=new byte[bytes];
						bis.read(isupContent,0,bytes);
						break;
					}
				}//end for loop
			}else{
				logger.error("Response180Handler processRecievedMessage()->Multipart content type not present so return with false");
				return false; 
			}
			
			if(isupContent == null){
				logger.error("Response180Handler processRecievedMessage()->ISUP content not found so return with false");
				return false; 
			}
			if(logger.isDebugEnabled())
				logger.debug("Response180Handler processRecievedMessage()->Recived CPG bytes::["+Util.formatBytes(isupContent)+"]");
			
			//parsing CPG
			LinkedList<byte[]> byteList=new LinkedList<byte[]>();
			byteList.add(isupContent);
			LinkedList<String> opCodeList=new LinkedList<String>();
			opCodeList.add(ISUPConstants.OP_CODE_CPG);

			List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
			CPGMessage cpgMessage = (CPGMessage) list.get(0);
			//XXX store required elems form incoming msg
			if(logger.isDebugEnabled())
				logger.debug("Response180Handler processRecievedMessage()->CPG message parsed:: "+cpgMessage);

		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException getting content",e);
			return false;
		} catch (IOException e) {
			logger.error("IOException getting content",e);return false;
		} catch (MessagingException e) {
			logger.error("MessagingException getting bodypart",e);
			return false;
		} 

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for Response180Handler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Response180Handler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}
		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.INVITE_PROV_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a Prov Resp Node");
			return false;
		}		
		if(!(sipResponse.getMethod().equals("INVITE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Response not for invite method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		ProvResNode sipNode = (ProvResNode) node;
		
		String nodeMessageType = sipNode.getMessage();
		String sipRespType= Integer.toString(sipResponse.getStatus());
		boolean isValid = false;
		if(sipRespType.equalsIgnoreCase(nodeMessageType)){
			isValid = true;
			if(logger.isDebugEnabled())
				logger.debug("Both sip message status and node message type matches.. setting valid to true");
		}
		
		//changes for b2b mode,this check is to avoid handling of an unexpected leg message
		if(InapIsupSimServlet.getInstance().isB2bMode){
			int leg = node.getSipLeg();
			String callId="";
			if(leg==1)
				callId = simCpb.getLastSipMessageLeg1().getCallId();
			else if(leg==2)
				callId = simCpb.getLastSipMessageLeg2().getCallId();
			if(!(callId.equalsIgnoreCase(((SipServletMessage) message).getCallId())))
			{
				logger.debug("Leaving validateMessage() for Response180Handler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Response180Handler with status "+isValid);
		return isValid;
	}
}
