package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ProvResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;


public class Default1XXHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(Default1XXHandler.class);
	private static Handler handler;
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Default1XXHandler.class) {
				if(handler ==null){
					handler = new Default1XXHandler();
				}
			}
		}
		return handler;
	}

	private Default1XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Response1XXHandler processNode()");

		if(!(node.getType().equals(Constants.INVITE_PROV_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		

		BodyElem bodyElem =null;
			
		ProvResNode provNode = (ProvResNode) node;
		int status = Integer.parseInt(provNode.getMessage());
		

		boolean enableSdp = provNode.isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
				
		SipServletResponse provResp= null;
		
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

		List<Header> headerList= new ArrayList<Header>();

		

		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}else if(subElem.getType().equals(Constants.BODY)){
				if(logger.isDebugEnabled())
					logger.debug("Inside AckHandler processNode()-BODY elem checking sub elems for body");
				bodyElem = (BodyElem)subElem;
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
			provResp = inviteReq.createResponse(status);
				
			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				provResp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}

			if(enableSdp){
				if(logger.isDebugEnabled())
					logger.debug("add SDP as enabled for message");
				provResp.setContent(sdp,Constants.SDP_CONTENT_TYPE);
			}
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(provResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(provResp);
			else
				simCpb.setLastSipMessage(provResp);
			
			//enable sdp check is to avoid sending a response reliable unless sdp is not in it
			if(leg == 1){
				
				if(simCpb.isSupportsReliableLeg1()&&enableSdp){
					provResp.sendReliably();
				}else{
					provResp.send();
				}
				
			}else if(leg ==2){
				
				if(simCpb.isSupportsReliableLeg2()&&enableSdp){
					provResp.sendReliably();
				}else{
					provResp.send();
				}
				
			}else if(simCpb.isSupportsReliable()&&enableSdp){
			
				provResp.sendReliably();
			
			}else{
			
				provResp.send();
			
			}
		} catch (IOException e) {
			logger.error("IOException sending default resp",e);
			return false;
		} catch (Rel100Exception e) {
			logger.error("Rel100Exception sending default resp",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Response1XXHandler processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Response1XXHandler");

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
		
		
		
		//code added for extracting sdp out of incoming message
		SipServletMessage res1xxMessage = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(res1xxMessage.getContentType()!=null)
					{
					if(res1xxMessage.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = res1xxMessage.getRawContent();
					else if(res1xxMessage.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)res1xxMessage.getContent();
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
		
		
		
		
		if(leg ==1 )
			simCpb.setSupportsReliableLeg1(isReliable);
		else if(leg ==2)
			simCpb.setSupportsReliableLeg2(isReliable);
		else
			simCpb.setSupportsReliable(isReliable);
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("Response1XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}
		
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for Response1XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Response1XXHandler");

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
				logger.debug("Leaving validateMessage() for Default1XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Response1XXHandler with status "+isValid);
		return isValid;
	}
}
