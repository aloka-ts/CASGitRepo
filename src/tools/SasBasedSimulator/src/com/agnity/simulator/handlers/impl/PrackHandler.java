package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.BodyElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;

public class PrackHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(PrackHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (PrackHandler.class) {
				if(handler ==null){
					handler = new PrackHandler();
				}
			}
		}
		return handler;
	}

	private PrackHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside PrackHandler processNode()");

		if(!(node.getType().equals(Constants.PRACK))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		SipServletResponse prevResponse = null;
		
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
		
		int leg = node.getSipLeg();
		SipServletMessage message = null;
		
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();
		
		if(message == null) {
			logger.error("Last sip message can not be null for PRACK");
			return false;
		}
		
		if(leg==1){
			if(!simCpb.isSupportsReliableLeg1()){
				logger.error("ERROR:: For non-reliable flow, PRACK cannot be sent..return with status false");
				return false;
			}
		}else if(leg==2){
			if(!simCpb.isSupportsReliableLeg2()){
				logger.error("ERROR:: For non-reliable flow, PRACK cannot be sent..return with status false");
				return false;
			}
		}else{
			if(!simCpb.isSupportsReliable()){
				logger.error("ERROR:: For non-reliable flow, PRACK cannot be sent..return with status false");
				return false;
			}
		}
		
		
		
		if(message instanceof SipServletResponse) {
			prevResponse = (SipServletResponse)message;
		}
		
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
			if(logger.isDebugEnabled())
				logger.debug("Inside PrackHandler processNode()-->Creating pRACk");
			SipServletRequest prackRequet = prevResponse.createPrack();
			
			if(enableSdp){
				if(logger.isDebugEnabled())
					logger.debug("add SDP as enabled for message");
				prackRequet.setContent(sdp,Constants.SDP_CONTENT_TYPE);
			}
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(prackRequet);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(prackRequet);
			else
				simCpb.setLastSipMessage(prackRequet);
			
			

			prackRequet.send();
			if(logger.isDebugEnabled())
				logger.debug("Inside PrackHandler processNode()-->pRACk sent");
		} catch (IOException e) {
			logger.error("IOException sending PRACK request",e);
			return false;
		} catch (Rel100Exception e) {
			logger.error("Rel100Exception creating PRACK request",e);
			return false;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving PrackHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for PrackHandler");

		if(!(node.getType().equals(Constants.PRACK))){
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
		SipServletMessage prackMessage = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(prackMessage.getContentType()!=null)
					{
					if(prackMessage.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = prackMessage.getRawContent();
					else if(prackMessage.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)prackMessage.getContent();
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
				logger.debug("PrackHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for PrackHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {


		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for PrackHandler");

		if(!(message instanceof SipServletRequest)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip request message");
			return false;
		}

		SipServletRequest sipRequest= (SipServletRequest) message;
		if(!( node.getType().equals(Constants.PRACK) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a PRACK Node");
			return false;
		}
		if(!(sipRequest.getMethod().equals("PRACK")) ){
			if(logger.isDebugEnabled())
				logger.debug("Not an PRACK method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		
		
				
		if(InapIsupSimServlet.getInstance().isB2bMode){
			int leg = node.getSipLeg();
			String callId="";
			
			//checking for reliable
			if(leg==1){
				if(!simCpb.isSupportsReliableLeg1()){
					logger.error("ERROR:: For non-reliable flow, PRACK is received..return with status false");
					return false;
				}
			}else if(leg==2){
				if(!simCpb.isSupportsReliableLeg2()){
					logger.error("ERROR:: For non-reliable flow, PRACK is received..return with status false");
					return false;
				}
			}
			//changes for b2b mode,this check is to avoid handling of an unexpected leg message
			if(leg==1)
				callId = simCpb.getLastSipMessageLeg1().getCallId();
			else if(leg==2)
				callId = simCpb.getLastSipMessageLeg2().getCallId();
			if(!(callId.equalsIgnoreCase(((SipServletMessage) message).getCallId())))
			{
				logger.debug("Leaving validateMessage() for PrackHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}else if(!simCpb.isSupportsReliable()){
			logger.error("ERROR:: For non-reliable flow, PRACK is received..return with status false");
			return false;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for PrackHandler with status "+true);
		return true;
	}



}
