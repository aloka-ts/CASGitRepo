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
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InviteSuccessResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.ANMMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.Util;


public class Invite2XXHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(Invite2XXHandler.class);
	private static Handler handler;

	//fields
	private static final String ANM_FIELD_BW_CALL_IND = "BwCallIndicators".toLowerCase();
		
	//Enums
	private static final String ANM_ENUM_CHARGE_IND_ENUM = "ChargeIndEnum".toLowerCase();
	private static final String ANM_ENUM_CALLED_PARTY_STATUS_IND_ENUM = "CalledPartyStatusIndEnum".toLowerCase();
	private static final String ANM_ENUM_CALLED_PARTY_CAT_IND_ENUM = "CalledPartyCatIndEnum".toLowerCase();
	private static final String ANM_ENUM_END_TO_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String ANM_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String ANM_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String ANM_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String ANM_ENUM_HOLDING_IND_ENUM = "HoldingIndEnum".toLowerCase();
	private static final String ANM_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String ANM_ENUM_ECHO_CONT_DEVICE_IND_ENUM = "EchoContDeviceIndEnum".toLowerCase();
	private static final String ANM_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Invite2XXHandler.class) {
				if(handler ==null){
					handler = new Invite2XXHandler();
				}
			}
		}
		return handler;
	}

	private Invite2XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Invite2XXHandler processNode()");

		if(!(node.getType().equals(Constants.INVITE_SUCCESS_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		
		
		InviteSuccessResNode invite2XXNode = (InviteSuccessResNode) node;
		int status = Integer.parseInt(invite2XXNode.getMessage());
//		if(status != 200){
//			logger.error("Not a 200 OK node, return with status false");
//			return false;
//		}
		
		
		SipServletResponse invite200Resp= null;
		
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

		BodyElem bodyElem =null;
		Node subElem =null;
		
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		
		Map<String, Variable> varMap = simCpb.getVariableMap();
		List<Header> headerList= new ArrayList<Header>();

		ANMMessage anmMessage= null;

		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}else if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside Invite2XXHandler processNode()-BODY elem checking sub elems for body");
				FieldElem fieldElem = null;
				bodyElem = (BodyElem)subElem;
				Node bodySubElem = null;
				Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();
				if(!subElem.getSubElements().isEmpty())
				{
					anmMessage = new ANMMessage();
					anmMessage.setMessageType(new byte[]{0x09});
				}
				while(bodySubElemIterator.hasNext()){
					bodySubElem = bodySubElemIterator.next();
					if(bodySubElem.getType().equals(Constants.FIELD)){
						if(logger.isDebugEnabled())
							logger.debug("Inside Invite2XXHandler processNode()-Field subelem checking field type");
						fieldElem  = (FieldElem) bodySubElem; 
						String fieldName = fieldElem.getFieldType();

						if(fieldName.equals(ANM_FIELD_BW_CALL_IND)){
							if(logger.isDebugEnabled())
								logger.debug("Inside Invite2XXHandler processNode()-Field subelem type BwCallInd");
							byte[] bwCallIndicators = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
							String chargeIndEnum = subFieldElems.get(ANM_ENUM_CHARGE_IND_ENUM).getValue(varMap);
							String calledPartyStatusIndEnum = subFieldElems.get(ANM_ENUM_CALLED_PARTY_STATUS_IND_ENUM).getValue(varMap);
							String calledPartyCatIndEnum = subFieldElems.get(ANM_ENUM_CALLED_PARTY_CAT_IND_ENUM).getValue(varMap);
							String endToEndMethodIndEnum = subFieldElems.get(ANM_ENUM_END_TO_END_METHOD_IND_ENUM).getValue(varMap);
							String interNwIndEnum = subFieldElems.get(ANM_ENUM_INTER_NW_IND_ENUM).getValue(varMap);
							String endToEndInfoIndEnum = subFieldElems.get(ANM_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
							String isdnUserPartIndEnum = subFieldElems.get(ANM_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap);
							String holdingIndEnum = subFieldElems.get(ANM_ENUM_HOLDING_IND_ENUM).getValue(varMap);
							String isdnAccessIndEnum = subFieldElems.get(ANM_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap);
							String echoContDeviceIndEnum = subFieldElems.get(ANM_ENUM_ECHO_CONT_DEVICE_IND_ENUM).getValue(varMap);
							String sccpMethodIndENum = subFieldElems.get(ANM_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
							try {
								bwCallIndicators = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.valueOf(chargeIndEnum), 
										CalledPartyStatusIndEnum.valueOf(calledPartyStatusIndEnum),
										CalledPartyCatIndEnum.valueOf(calledPartyCatIndEnum), 
										EndToEndMethodIndEnum.valueOf(endToEndMethodIndEnum),InterNwIndEnum.valueOf(interNwIndEnum), 
										EndToEndInfoIndEnum.valueOf(endToEndInfoIndEnum),ISDNUserPartIndEnum.valueOf(isdnUserPartIndEnum), 
										HoldingIndEnum.valueOf(holdingIndEnum),ISDNAccessIndEnum.valueOf(isdnAccessIndEnum), 
										EchoContDeviceIndEnum.valueOf(echoContDeviceIndEnum),SCCPMethodIndENum.valueOf(sccpMethodIndENum));								
							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding BwCallInd in ANM/183 response",e);
								return false;
							}
							anmMessage.setBackwardCallIndicators(bwCallIndicators);
						}
					}
				}

			}

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
			else{
				logger.debug("Control will never reach here");
				sdp = InapIsupSimServlet.getInstance().getConfigData().getSdp();
			}
				
		}		
			
		try {
			invite200Resp = inviteReq.createResponse(status);
				
			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				invite200Resp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}

			if(anmMessage != null){
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(anmMessage);
				opCode.add(ISUPConstants.OP_CODE_ANM);
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] anm =  encode.get(0);
					Multipart mp = new MimeMultipart();	
					//adding SDP to multipart
					if(enableSdp){
						if(logger.isDebugEnabled())
							logger.debug("add SDP as enabled for message");
						Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					}
					//adding ANM to multipart
					Helper.formMultiPartMessage(mp, anm, Constants.ISUP_CONTENT_TYPE);
					invite200Resp.setContent(mp,mp.getContentType());

				} catch (MessagingException e) {
					logger.error("MessagingException creating and setting multipat message",e);
					return false;
				}catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException setting content",e);
					return false;
				}catch (Exception e) {
					logger.error("Exception encoding ANM message",e);
					return false;
				}
			}else{
				if(enableSdp){
					if(logger.isDebugEnabled())
						logger.debug("add SDP as enabled for message");
					invite200Resp.setContent(sdp,Constants.SDP_CONTENT_TYPE);
				}
			}
			
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(invite200Resp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(invite200Resp);
			else
				simCpb.setLastSipMessage(invite200Resp);
			
			logger.debug("setting call buffer b4 sending 2xx to xlite, id= "+invite200Resp.getApplicationSession().getId()+" buffer = "+simCpb);
			
			simCpb.setSipAppSession(invite200Resp.getApplicationSession());
			//InapIsupSimServlet.getInstance().getSipCallData().put(callId, simCpb);
			InapIsupSimServlet.getInstance().getAppSessionIdCallData().put(invite200Resp.getApplicationSession().getId(),simCpb );
			
			
			invite200Resp.send();
		} catch (IOException e) {
			logger.error("IOException sending invite request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Invite2XXHandler processNode() with status true");
		return true;


	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Invite2XXHandler");

		if(!(node.getType().equals(Constants.INVITE_SUCCESS_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		
		SipServletMessage successMessage = (SipServletMessage) message;
		
		int leg = node.getSipLeg();
		if(leg == 1)
			simCpb.setLastSipMessageLeg1(successMessage);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2(successMessage);
		else
			simCpb.setLastSipMessage(successMessage);
		
		
		
		
		
		//code added for extracting sdp out of incoming message
		SipServletMessage res2xxMessage = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(res2xxMessage.getContentType()!=null)
					{
					if(res2xxMessage.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = res2xxMessage.getRawContent();
					else if(res2xxMessage.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)res2xxMessage.getContent();
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
				logger.debug("Invite2XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}
								
		try {			
			String contentType = successMessage.getContentType();
			//check if multipart content present
			byte[] isupContent= null;
			if(contentType!=null && contentType.startsWith(Constants.MULTIPART_CONTENT_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("Invite2XXHandler processRecievedMessage()->Multipart content present");
						mp = (Multipart) successMessage.getContent();
				int bpCount =mp.getCount();
				if(logger.isDebugEnabled())
					logger.debug("Invite2XXHandler processRecievedMessage()->Checking for ISUP content type["+Constants.ISUP_CONTENT_TYPE+"]");
				for(int i =0; i<bpCount;i++){
					BodyPart bp = mp.getBodyPart(i);
					if(logger.isDebugEnabled())
						logger.debug("Invite2XXHandler processRecievedMessage()->Matching body part content type["+bp.getContentType()+"]");
					if(bp.getContentType().equals(Constants.ISUP_CONTENT_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("Invite2XXHandler processRecievedMessage()->ISUP content found");
						ByteArrayInputStream bis=(ByteArrayInputStream) bp.getContent();	
						int bytes=bis.available();
						isupContent=new byte[bytes];
						bis.read(isupContent,0,bytes);
						break;
					}
				}//end for loop
			}else{
				logger.error("Invite2XXHandler processRecievedMessage()->Multipart content type not present so return with false");
				return false; 
			}
			
			if(isupContent == null){
				logger.error("Invite2XXHandler processRecievedMessage()->ISUP content not found so return with false");
				return false; 
			}
			if(logger.isDebugEnabled())
				logger.debug("Invite2XXHandler processRecievedMessage()->Recived ANM bytes::["+Util.formatBytes(isupContent)+"]");
			
			//parsing ANM
			LinkedList<byte[]> byteList=new LinkedList<byte[]>();
			byteList.add(isupContent);
			LinkedList<String> opCodeList=new LinkedList<String>();
			opCodeList.add(ISUPConstants.OP_CODE_ANM);

			List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
			ANMMessage anmMessage = (ANMMessage) list.get(0);
			//XXX store required elems form anmMessage
			if(logger.isDebugEnabled())
				logger.debug("Invite2XXHandler processRecievedMessage()->ANM message parsed:: "+anmMessage);

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
			logger.debug("leaving processRecievedMessage() for Invite2XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Invite2XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.INVITE_SUCCESS_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a INVITE 2XX Node");
			return false;
		}
		if(!(sipResponse.getMethod().equals("INVITE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Response not for INVITE method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		InviteSuccessResNode sipNode = (InviteSuccessResNode) node;
		
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
				logger.debug("Leaving validateMessage() for Invite2XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Invite2XXHandler with status "+isValid);
		return isValid;
	}
}
