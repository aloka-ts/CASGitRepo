package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ClientErrResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.enumdata.CauseValEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.LocationEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.messagetypes.RLCMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.Util;


public class Response4XXHandler extends AbstractHandler{



	private static Logger logger = Logger.getLogger(Response4XXHandler.class);
	private static Handler handler;

	private static final String REL_FIELD_CAUSE = "Cause".toLowerCase();
	private static final String REL_LOCATION_ENUM = "LocationEnum".toLowerCase();
	private static final String REL_CODING_STND_ENUM = "CodingStndEnum".toLowerCase();
	private static final String REL_CAUSE_VALUE_ENUM = "CauseValEnum".toLowerCase();


	private static final String RLC_FIELD_CAUSE = "Cause".toLowerCase();

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Response4XXHandler.class) {
				if(handler ==null){
					handler = new Response4XXHandler();
				}
			}
		}
		return handler;
	}

	private Response4XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Response4XXHandler processNode()");

		if(!(node.getType().equals(Constants.CLIENT_ERROR_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	


		ClientErrResNode resp4xxNode = (ClientErrResNode) node;
		int status = Integer.parseInt(resp4xxNode.getMessage());


		SipServletResponse clientErrorResp= null;
		
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
			logger.error("Last message not present can't send response or invite request is not present lastmessage::"+
					message+"  InviteReq::"+inviteReq);
			return false;
		}

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();


		Node subElem =null;

		Map<String, Variable> varMap = simCpb.getVariableMap();
		List<Header> headerList= new ArrayList<Header>();

		RLCMessage rlcMessage= null;
		RELMessage relMessage =null;
		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}else if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside Response4XXHandler processNode()-BODY elem checking sub elems for body");

				if(message.getMethod().equals("CANCEL")){
					rlcMessage = createRLC(subElem,varMap);
				}else{
					relMessage = createREL(subElem,varMap);
				}
			}//@End:Body

		}//complete while

		try {
			clientErrorResp = inviteReq.createResponse(status);

			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				clientErrorResp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}
			
			
			if(relMessage != null){
				if(relMessage.getMessageType() == null){
					logger.error("Error creating REL message");
					return false;
				}
				
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(relMessage);
				opCode.add(ISUPConstants.OP_CODE_REL);
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] rel =  encode.get(0);
					Multipart mp = new MimeMultipart();	
//					//adding SDP to multipart
//					Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					//adding REL to multipart
					Helper.formMultiPartMessage(mp, rel, Constants.ISUP_CONTENT_TYPE);
					clientErrorResp.setContent(mp,mp.getContentType());

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
			}else if(rlcMessage != null){
				if(rlcMessage.getMessageType() == null){
					logger.error("Error creating RLC message");
					return false;
				}
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(rlcMessage);
				opCode.add(ISUPConstants.OP_CODE_RLC);
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] rlc =  encode.get(0);
					Multipart mp = new MimeMultipart();	
//					//adding SDP to multipart
//					Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					//adding RLC to multipart
					Helper.formMultiPartMessage(mp, rlc, Constants.ISUP_CONTENT_TYPE);
					clientErrorResp.setContent(mp,mp.getContentType());

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
			}
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(clientErrorResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(clientErrorResp);
			else
				simCpb.setLastSipMessage(clientErrorResp);
			
			clientErrorResp.send();
		} catch (IOException e) {
			logger.error("IOException sending invite request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Response4XXHandler processNode() with status true");
		return true;

	}

	private RELMessage createREL(Node subElem, Map<String, Variable> varMap) {
		FieldElem fieldElem = null;
		Node bodySubElem = null;
		RELMessage relMessage = new RELMessage();
		// Message type will be Constant for REL
		relMessage.setMessageType(new byte[]{0x0c});

		Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();

		while(bodySubElemIterator.hasNext()){
			bodySubElem = bodySubElemIterator.next();
			if(bodySubElem.getType().equals(Constants.FIELD)){
				fieldElem  = (FieldElem) bodySubElem; 
				String fieldName = fieldElem.getFieldType();

				if(fieldName.equals(REL_FIELD_CAUSE)){
					if(logger.isInfoEnabled())
						logger.info("Inside CancelHandler processNode()->setting cause");
					byte[] cause = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String locationEnum = subFieldElems.get(REL_LOCATION_ENUM).getValue(varMap);
					String codingStdEnum = subFieldElems.get(REL_CODING_STND_ENUM).getValue(varMap);
					String causeValueEnum = subFieldElems.get(REL_CAUSE_VALUE_ENUM).getValue(varMap);

					try {
						cause = Cause.encodeCauseVal(LocationEnum.valueOf(locationEnum), 
								CodingStndEnum.valueOf(codingStdEnum), 
								CauseValEnum.valueOf(causeValueEnum));

					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding Cause in REL/4XX request",e);
						relMessage = new RELMessage();
						return relMessage;
					} 
					relMessage.setCause(cause);
				}//@End cause
			}//@End: field 
		}//@End: body subelem
		return relMessage;
	}

	private RLCMessage createRLC(Node subElem, Map<String, Variable> varMap) {
		FieldElem fieldElem = null;
		Node bodySubElem = null;
		Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();
		RLCMessage rlcMessage = new RLCMessage();
		rlcMessage.setMessageType(new byte[]{0x10});
		Map<Integer,byte[]> optMap=new HashMap<Integer,byte[]>();

		while(bodySubElemIterator.hasNext()){
			bodySubElem = bodySubElemIterator.next();
			if(bodySubElem.getType().equals(Constants.FIELD)){
				if(logger.isDebugEnabled())
					logger.debug("Inside Response4XXHandler processNode()-Field subelem checking field type");
				fieldElem  = (FieldElem) bodySubElem; 
				String fieldName = fieldElem.getFieldType();

				if(fieldName.equals(RLC_FIELD_CAUSE)){
					if(logger.isDebugEnabled())
						logger.debug("Inside Response4XXHandler processNode()-Field subelem type Cause");
					byte[] cause= hexStringToByteArray(fieldElem.getValue(varMap));
					optMap.put(0x12, cause);
				}
			}
		}//end while subelms for body
		//setting RLC params 
		rlcMessage.setOtherOptParams(optMap);
		return rlcMessage;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Response4XXHandler");

		if(!(node.getType().equals(Constants.CLIENT_ERROR_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	

		SipServletMessage successMessage = (SipServletMessage) message;
		
		int leg = node.getSipLeg();
		SipServletMessage lastSipMessage = null;
		if(leg == 1)
			lastSipMessage = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			lastSipMessage = simCpb.getLastSipMessageLeg2();
		else
			lastSipMessage = simCpb.getLastSipMessage();
		//SipServletMessage lastSipMessage = message;
		//SipServletMessage lastSipMessage = simCpb.getLastSipMessage();
		
		
		if(leg == 1)
			simCpb.setLastSipMessageLeg1(successMessage);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2(successMessage);
		else
			simCpb.setLastSipMessage(successMessage);
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("Response4XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		try {			
			String contentType = successMessage.getContentType();
			//check if multipart content present
			byte[] isupContent= null;
			if(contentType!=null && contentType.startsWith(Constants.MULTIPART_CONTENT_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->Multipart content present");
				Multipart mp = (Multipart) successMessage.getContent();
				int bpCount =mp.getCount();
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->Checking for ISUP content type["+Constants.ISUP_CONTENT_TYPE+"]");
				for(int i =0; i<bpCount;i++){
					BodyPart bp = mp.getBodyPart(i);
					if(logger.isDebugEnabled())
						logger.debug("Response4XXHandler processRecievedMessage()->Matching body part content type["+bp.getContentType()+"]");
					if(bp.getContentType().equals(Constants.ISUP_CONTENT_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("Response4XXHandler processRecievedMessage()->ISUP content found");
						ByteArrayInputStream bis=(ByteArrayInputStream) bp.getContent();	
						int bytes=bis.available();
						isupContent=new byte[bytes];
						bis.read(isupContent,0,bytes);
						break;
					}
				}//end for loop
			}else{
				logger.error("Response4XXHandler processRecievedMessage()->Multipart content type not present so return with false");
				return false; 
			}

			if(isupContent == null){
				logger.error("Response4XXHandler processRecievedMessage()->ISUP content not found so return with false");
				return false; 
			}
			if(logger.isDebugEnabled())
				logger.debug("Response4XXHandler processRecievedMessage()->Recived RLC bytes::["+Util.formatBytes(isupContent)+"]");

			//parsing RLC
			LinkedList<byte[]> byteList=new LinkedList<byte[]>();
			byteList.add(isupContent);
			LinkedList<String> opCodeList=new LinkedList<String>();
			if(lastSipMessage.getMethod().equals("CANCEL")){
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->last message cancel parsing for REL");
				opCodeList.add(ISUPConstants.OP_CODE_REL);
				List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
				RELMessage relMessage = (RELMessage) list.get(0);
				//XXX store required elems form rlcMessage
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->REL message parsed:: "+relMessage);
			}else{
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->last message cancel parsing for RLC");
				opCodeList.add(ISUPConstants.OP_CODE_RLC);
				List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
				RLCMessage rlcMessage = (RLCMessage) list.get(0);
				//XXX store required elems form rlcMessage
				if(logger.isDebugEnabled())
					logger.debug("Response4XXHandler processRecievedMessage()->RLC message parsed:: "+rlcMessage);
			}
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
			logger.debug("leaving processRecievedMessage() for Response4XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Response4XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.CLIENT_ERROR_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a Invite 4XX Node");
			return false;
		}
		if(!(sipResponse.getMethod().equals("INVITE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Response not for Invite method");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		ClientErrResNode sipNode = (ClientErrResNode) node;

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
				logger.debug("Leaving validateMessage() for Response4XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Response4XXHandler with status "+isValid);
		return isValid;
	}


	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[(i / 2)] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}


}
