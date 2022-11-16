package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.BodyElem;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;

public class AckHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(AckHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (AckHandler.class) {
				if(handler ==null){
					handler = new AckHandler();
				}
			}
		}
		return handler;
	}

	private AckHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside AckHandler processNode()");

		if(!(node.getType().equals(Constants.ACK))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}		
		
		List<Node> subElements =node.getSubElements();		
		Iterator<Node> subElemIterator = subElements.iterator();
		BodyElem bodyElem =null;
		Node subElem =null;
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside AckHandler processNode()-BODY elem checking sub elems for body");
				bodyElem = (BodyElem)subElem;
			}
		}
		
		SipServletResponse prevResponse = null;
		
		int leg = node.getSipLeg();
		SipServletMessage message = null;
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();
		
		if(message == null) {
			logger.error("Last sip message can not be null for ACK");
			return false;
		}
		
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		
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
		
		
		if(message instanceof SipServletResponse) {
			prevResponse = (SipServletResponse)message;
		}

		try {
			SipServletRequest ackRequest = prevResponse.createAck();
			
			if(enableSdp){
				if(logger.isDebugEnabled())
					logger.debug("add SDP as enabled for message");
				ackRequest.setContent(sdp,Constants.SDP_CONTENT_TYPE);
			}
		
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(ackRequest);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(ackRequest);
			else
				simCpb.setLastSipMessage(ackRequest);
			
			simCpb.setSipAppSession(ackRequest.getApplicationSession());
			InapIsupSimServlet.getInstance().getAppSessionIdCallData().put(ackRequest.getApplicationSession().getId(),simCpb );
			
			
			
			if(logger.isDebugEnabled())
				logger.debug("ACK created sending ACK");
			ackRequest.send();
		} catch (IOException e) {
			logger.error("IOException sending ACK request",e);
			return false;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving AckHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for AckHandler");

		if(!(node.getType().equals(Constants.ACK))){
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
		
		//code added for extracting sdp out of incoming message
		SipServletMessage ackMessage = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(ackMessage.getContentType()!=null)
					{
					if(ackMessage.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = ackMessage.getRawContent();
					else if(ackMessage.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)ackMessage.getContent();
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
				logger.debug("AckHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for AckHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {


		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for AckHandler");

		if(!(message instanceof SipServletRequest)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip request message");
			return false;
		}

		SipServletRequest sipRequest= (SipServletRequest) message;
		if(!( node.getType().equals(Constants.ACK) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a ACK Node");
			return false;
		}
		if(!(sipRequest.getMethod().equals("ACK")) ){
			if(logger.isDebugEnabled())
				logger.debug("Not an ACK method");
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
				logger.debug("Leaving validateMessage() for AckHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for AckHandler with status "+true);
		return true;
	}



}
