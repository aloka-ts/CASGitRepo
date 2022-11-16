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
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.UpdateSuccessResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;

public class Update2XXHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(Update2XXHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Update2XXHandler.class) {
				if(handler ==null){
					handler = new Update2XXHandler();
				}
			}
		}
		return handler;
	}

	private Update2XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside UPDATE2XXHandler processNode()");

		if(!(node.getType().equals(Constants.UPDATE_SUCCESS_RES_NODE))){
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
		
		UpdateSuccessResNode update2xxNode = (UpdateSuccessResNode) node;
		boolean enableSdp = ((SipNode)node).isEnableSdp();
		
		boolean lastSavedSdp = ((SipNode)node).isLastSavedSdp();
		
		int status = Integer.parseInt(update2xxNode.getMessage());
		SipServletRequest prevRequest= null;

		int leg = node.getSipLeg();
		SipServletMessage message = null;
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();
		
		if(message == null) {
			logger.error("Last sip message can not be null for UPDATE response");
			return false;
		}

		if(message instanceof SipServletRequest) {
			prevRequest = (SipServletRequest)message;
		}else{
			logger.error("Prevois mesaage not request cant send response on same."+message);
			return false;
		}
		
		if(!(prevRequest.getMethod().equals("UPDATE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Last message not UPDATE method");
			return false;
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
			SipServletResponse updateResp = prevRequest.createResponse(status);
			if(enableSdp){
				if(logger.isDebugEnabled())
					logger.debug("add SDP as enabled for message");
				updateResp.setContent(sdp, Constants.SDP_CONTENT_TYPE);
			}
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(updateResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(updateResp);
			else
			simCpb.setLastSipMessage(updateResp);

			updateResp.send();
		} catch (IOException e) {
			logger.error("IOException sending UPDATE 2XX response",e);
			return false;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving UPDATE2XXHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for UPDATE2XXHandler");

		if(!(node.getType().equals(Constants.UPDATE_SUCCESS_RES_NODE))){
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
		SipServletMessage update2xxMessage = ((SipServletMessage) message);
		byte sdp[] = null;
		String sdptext="";
		Multipart mp= null;
		BodyPart bodyPart = null;
		if(((SipNode)node).isLastSavedSdp())
		{
			try {
					if(update2xxMessage.getContentType()!=null)
					{
					if(update2xxMessage.getContentType().equalsIgnoreCase(Constants.SDP_CONTENT_TYPE))
						sdp = update2xxMessage.getRawContent();
					else if(update2xxMessage.getContentType().startsWith(Constants.MULTIPART_CONTENT_TYPE))
						 {
							mp = (Multipart)update2xxMessage.getContent();
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
				logger.debug("UPDATE2XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for UPDATE2XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for UPDATE2XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.UPDATE_SUCCESS_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a UPDATE 2XX Node");
			return false;
		}
		if(!(sipResponse.getMethod().equals("UPDATE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Response not for UPDATE method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		UpdateSuccessResNode sipNode = (UpdateSuccessResNode) node;
		
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
				logger.debug("Leaving validateMessage() for UpdateHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for UPDATE2XXHandler with status "+isValid);
		return isValid;
	}
}
