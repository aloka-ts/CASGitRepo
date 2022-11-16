package com.agnity.simulator.handlers.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InfoSuccessResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;

public class Info2XXHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(Info2XXHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Info2XXHandler.class) {
				if(handler ==null){
					handler = new Info2XXHandler();
				}
			}
		}
		return handler;
	}

	private Info2XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside INFO2XXHandler processNode()");

		if(!(node.getType().equals(Constants.INFO_SUCCESS_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		InfoSuccessResNode info2xxNode = (InfoSuccessResNode) node;
		int status = Integer.parseInt(info2xxNode.getMessage());
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
			logger.error("Last sip message can not be null for INFO response");
			return false;
		}

		if(message instanceof SipServletRequest) {
			prevRequest = (SipServletRequest)message;
		}else{
			logger.error("Prevois mesaage not request cant send response on same."+message);
			return false;
		}
		
		if(!(prevRequest.getMethod().equals("INFO")) ){
			if(logger.isDebugEnabled())
				logger.debug("Last message not INFO method");
			return false;
		}
		
		try {
			SipServletResponse infoResp = prevRequest.createResponse(status);
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(infoResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(infoResp);
			else
				simCpb.setLastSipMessage(infoResp);

			infoResp.send();
		} catch (IOException e) {
			logger.error("IOException sending INFO 2XX response",e);
			return false;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving INFO2XXHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for INFO2XXHandler");

		if(!(node.getType().equals(Constants.INFO_SUCCESS_RES_NODE))){
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

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("INFO2XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for INFO2XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for INFO2XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.INFO_SUCCESS_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a INFO 2XX Node");
			return false;
		}
		if(!(sipResponse.getMethod().equals("INFO")) ){
			if(logger.isDebugEnabled())
				logger.debug("Response not for INFO method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		InfoSuccessResNode sipNode = (InfoSuccessResNode) node;
		
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
				logger.debug("Leaving validateMessage() for Info2XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for INFO2XXHandler with status "+isValid);
		return isValid;
	}
}
