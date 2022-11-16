package com.agnity.simulator.handlers.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.SusResIndicators;
import com.genband.isup.enumdata.SusResIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.SUSRESMessage;
import com.genband.isup.operations.ISUPOperationsCoding;

public class InfoHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(InfoHandler.class);
	private static Handler handler;
	
	private static final String SUSRES_FIELD_MSG_TYPE = "messageType".toLowerCase();
	private static final String SUSRES_FIELD_SUS_RES_IND = "suspendResumeInd".toLowerCase();
	
	private static final String SUSRES_SUS_RES_IND_ENUM = "SusResIndEnum".toLowerCase();
	
	private static final String SUSPEND_MSG_TYPE = "SUSPEND".toLowerCase();
	private static final String RESUME_MSG_TYPE = "RESUME".toLowerCase();
	
	

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (InfoHandler.class) {
				if(handler ==null){
					handler = new InfoHandler();
				}
			}
		}
		return handler;
	}

	private InfoHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside InfoHandler processNode()");

		if(!(node.getType().equals(Constants.INFO))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		
		int leg = node.getSipLeg();
		SipServletMessage message = null;
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();
		
		if(message == null) {
			logger.error("Last sip message can not be null for INFO");
			return false;
		}
		
		//boolean enableInfo = ((SipNode)node).isEnableInfo();
		
		boolean lastSavedInfo = ((SipNode)node).isLastSavedInfo();
		
		String infocontent="";
		String contentType="";
		if(lastSavedInfo)
		{
			//if we are sending our message on leg1 than we will use lastinfocontent from leg2 and vice versa
			if(leg==1){
				infocontent = simCpb.getLastInfoContentLeg2();
				contentType = simCpb.getLastContentTypeLeg2();
			}
			else if(leg==2){
				infocontent = simCpb.getLastInfoContentLeg1();
				contentType = simCpb.getLastContentTypeLeg1();
			}
			else{
				infocontent = simCpb.getLastInfoContent();
				contentType = simCpb.getLastContentType();
			}
		}
		
		
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();


		Node subElem =null;

		Map<String, Variable> varMap = simCpb.getVariableMap();
		List<Header> headerList= new ArrayList<Header>();

		SUSRESMessage susResMessage= null;
		
		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}else if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside InfoHandler processNode()-BODY elem checking sub elems for body");
				susResMessage = createSusRes(subElem,varMap);
				
			}//@End:Body

		}//complete while

		try {
			if(logger.isDebugEnabled())
				logger.debug("Inside InfoHandler processNode()-->Creating Info");
			SipServletRequest infoRequest = message.getSession().createRequest("INFO");
			
			
			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				infoRequest.addHeader(header.getHeaderName(), header.getHeaderValue());
			}
			
			
			if(susResMessage != null){
				if(susResMessage.getMessageType() == null){
					logger.error("Error creating SUSRES message");
					return false;
				}
				
				LinkedList<byte[]> encode = null;
				LinkedList<Object> objLL = new LinkedList<Object>();
				LinkedList<String> opCode = new LinkedList<String>();

				objLL.add(susResMessage);
				opCode.add(susResMessage.getMessageType());
				try {
					encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
					byte[] rel =  encode.get(0);
					Multipart mp = new MimeMultipart();	
//					//adding SDP to multipart
//					Helper.formMultiPartMessage(mp, sdp.getBytes(), Constants.SDP_CONTENT_TYPE);
					//adding REL to multipart
					Helper.formMultiPartMessage(mp, rel, Constants.ISUP_CONTENT_TYPE);
					infoRequest.setContent(mp,mp.getContentType());

				} catch (MessagingException e) {
					logger.error("MessagingException creating and setting multipat message",e);
					return false;
				}catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException setting content",e);
					return false;
				}catch (Exception e) {
					logger.error("Exception encoding SUSRES message",e);
					return false;
				}
			}else{
				if(lastSavedInfo){
					if(logger.isDebugEnabled())
						logger.debug("add infocontent as enabled for message");
					if(contentType.equalsIgnoreCase(Constants.ISUP_CONTENT_TYPE)){
					try{
						Multipart mp = new MimeMultipart();	
						Helper.formMultiPartMessage(mp, infocontent.getBytes(), Constants.ISUP_CONTENT_TYPE);
						/*if(logger.isDebugEnabled())
							logger.debug("content type set is "+mp.getContentType());
						*/infoRequest.setContent(mp,mp.getContentType());
					}catch (MessagingException e) {
						logger.error("MessagingException creating and setting multipat message",e);
						return false;
					}
					}else
						infoRequest.setContent(infocontent,contentType);
				}
			}
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(infoRequest);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(infoRequest);
			else
				simCpb.setLastSipMessage(infoRequest);
			
			infoRequest.send();
			if(logger.isDebugEnabled())
				logger.debug("Inside InfoHandler processNode()-->Info sent");
		} catch (IOException e) {
			logger.error("IOException sending INFO request",e);
			return false;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving InfoHandler processNode() with status true");
		return true;

	}
	
	
	private SUSRESMessage createSusRes(Node subElem, Map<String, Variable> varMap) {
		if(logger.isDebugEnabled())
			logger.debug("Enter createSusRes");
		FieldElem fieldElem = null;
		Node bodySubElem = null;
		SUSRESMessage susResMessage = new SUSRESMessage();

		Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();

		while(bodySubElemIterator.hasNext()){
			bodySubElem = bodySubElemIterator.next();
			if(bodySubElem.getType().equals(Constants.FIELD)){
				fieldElem  = (FieldElem) bodySubElem; 
				String fieldName = fieldElem.getFieldType();

				if(fieldName.equals(SUSRES_FIELD_MSG_TYPE)){
					if(logger.isInfoEnabled())
						logger.info("Inside InfoHandler processNode()->setting message type");
					String msgType= fieldElem.getValue(varMap);
					if(logger.isDebugEnabled())
						logger.debug("Message Type value is::["+msgType+"]");
					
					if(msgType.equalsIgnoreCase(SUSPEND_MSG_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("Suspend message setting");
						susResMessage.setMessageType(new byte[]{0x0d});
					}else if(msgType.equalsIgnoreCase(RESUME_MSG_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("Resume message setting");
						susResMessage.setMessageType(new byte[]{0x0e});
					}else{
						logger.error("Invalid message type entered");
						susResMessage = new SUSRESMessage();
						return susResMessage;
					}
					
					
					
				}else if(fieldName.equals(SUSRES_FIELD_SUS_RES_IND)){
					if(logger.isInfoEnabled())
						logger.info("Inside InfoHandler processNode()->setting susResInd");
					byte[] suspendResumeInd = null;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

					String susResIndEnum = subFieldElems.get(SUSRES_SUS_RES_IND_ENUM).getValue(varMap);


					try {
						suspendResumeInd = SusResIndicators.encodeSusResInd(SusResIndEnum.valueOf(susResIndEnum));

					} catch (InvalidInputException e) {
						logger.error("InvalidInputException encoding Cause in susResIndEnum",e);
						susResMessage = new SUSRESMessage();
						return susResMessage;
					} 
					susResMessage.setSuspendResumeInd(suspendResumeInd);
				}//@End cause
			}//@End: field 
		}//@End: body subelem
		return susResMessage;
	}
	
	
	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for InfoHandler");

		if(!(node.getType().equals(Constants.INFO))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		
		int leg = node.getSipLeg();
		if(leg == 1)
			simCpb.setLastSipMessageLeg1((SipServletMessage) message);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2((SipServletMessage) message);
		else
			simCpb.setLastSipMessage((SipServletMessage) message);
		
		byte infoContent[];
		String infotext="";
		String contentType="";
		if(((SipNode)node).isLastSavedInfo())
		{
			try {
					infoContent = ((SipServletMessage) message).getRawContent();
					contentType = ((SipServletMessage) message).getContentType();
					//assuming content is in system's default encoding
					infotext = new String(infoContent);
					if(leg==1){
						simCpb.setLastInfoContentLeg1(infotext);
						simCpb.setLastContentTypeLeg1(contentType);
					}
					else if(leg==2){
						simCpb.setLastInfoContentLeg2(infotext);
						simCpb.setLastContentTypeLeg2(contentType);
					}
					else{
						simCpb.setLastInfoContent(infotext);
						simCpb.setLastContentType(contentType);
					}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("Encoding is not supported"+e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("IO exception when getting media type"+e);
			}
		}
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("InfoHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for InfoHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {


		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for InfoHandler");

		if(!(message instanceof SipServletRequest)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip request message");
			return false;
		}

		SipServletRequest sipRequest= (SipServletRequest) message;
		if(!( node.getType().equals(Constants.INFO) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a INFO Node");
			return false;
		}
		
		if(!(sipRequest.getMethod().equals("INFO")) ){
			if(logger.isDebugEnabled())
				logger.debug("Not an INFO method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
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
				logger.debug("Leaving validateMessage() for InfoHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for InfoHandler with status "+true);
		return true;
	}



}
