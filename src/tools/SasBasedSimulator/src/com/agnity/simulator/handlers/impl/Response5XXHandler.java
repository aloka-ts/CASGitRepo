package com.agnity.simulator.handlers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ServerErrResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;


public class Response5XXHandler extends AbstractHandler{



	private static Logger logger = Logger.getLogger(Response5XXHandler.class);
	private static Handler handler;
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Response5XXHandler.class) {
				if(handler ==null){
					handler = new Response5XXHandler();
				}
			}
		}
		return handler;
	}

	private Response5XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Response5XXHandler processNode()");

		if(!(node.getType().equals(Constants.SERVER_ERROR_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	


		ServerErrResNode resp5xxNode = (ServerErrResNode) node;
		int status = Integer.parseInt(resp5xxNode.getMessage());


		SipServletResponse serverErrorResp= null;
		
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

//		Map<String, Variable> varMap = simCpb.getVariableMap();
		List<Header> headerList= new ArrayList<Header>();


		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				headerList.add( new Header(headerElem.getName(),headerElem.getValue()) );
			}

		}//complete while

		try {
			serverErrorResp = inviteReq.createResponse(status);

			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				serverErrorResp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}

			if(leg == 1)
				simCpb.setLastSipMessageLeg1(serverErrorResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(serverErrorResp);
			else			
			simCpb.setLastSipMessage(serverErrorResp);
			
			serverErrorResp.send();
		} catch (IOException e) {
			logger.error("IOException sending invite request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Response5XXHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Response5XXHandler");

		if(!(node.getType().equals(Constants.SERVER_ERROR_RES_NODE))){
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
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("Response5XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		//storing required hedaers
//		storeFields(subElements, simCpb, successMessage);
			
			
			
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for Response5XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Response5XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.SERVER_ERROR_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a Invite 5XX Node");
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
		ServerErrResNode sipNode = (ServerErrResNode) node;

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
				logger.debug("Leaving validateMessage() for Response5XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Response5XXHandler with status "+isValid);
		return isValid;
	}
	
	

}
