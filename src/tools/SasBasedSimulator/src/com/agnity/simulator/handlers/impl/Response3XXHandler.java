package com.agnity.simulator.handlers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.RedirectResNode;
import com.agnity.simulator.domainobjects.Header;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;


public class Response3XXHandler extends AbstractHandler{



	private static Logger logger = Logger.getLogger(Response3XXHandler.class);
	private static Handler handler;

	private static final String HEADER_CONTACT = "Contact";
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (Response3XXHandler.class) {
				if(handler ==null){
					handler = new Response3XXHandler();
				}
			}
		}
		return handler;
	}

	private Response3XXHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside Response3XXHandler processNode()");

		if(!(node.getType().equals(Constants.INVITE_REDIRECT_RES_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	


		RedirectResNode resp3xxNode = (RedirectResNode) node;
		int status = Integer.parseInt(resp3xxNode.getMessage());


		SipServletResponse redirectResp= null;
		
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


		//reading sub elemnts
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.HEADER)){
				HeaderElem headerElem = (HeaderElem) subElem;
				String headerName = headerElem.getName();
				String headerValue = null;
				if(headerName.equalsIgnoreCase(HEADER_CONTACT)){
					headerName= HEADER_CONTACT;
					headerValue = Helper.getValueForHedaerUri(headerElem.getValue(),varMap);
				}else{
					headerValue = headerElem.getValue();
				}
				headerList.add( new Header(headerName,headerValue) );
			}

		}//complete while

		try {
			redirectResp = inviteReq.createResponse(status);

			Iterator<Header> headerIterator=headerList.iterator();
			while(headerIterator.hasNext()){
				Header header = headerIterator.next();
				redirectResp.addHeader(header.getHeaderName(), header.getHeaderValue());
			}
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(redirectResp);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(redirectResp);
			else
				simCpb.setLastSipMessage(redirectResp);
			
			redirectResp.send();
		} catch (IOException e) {
			logger.error("IOException sending invite request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving Response3XXHandler processNode() with status true");
		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for Response3XXHandler");

		if(!(node.getType().equals(Constants.INVITE_REDIRECT_RES_NODE))){
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
				logger.debug("Response3XXHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}

		//storing required hedaers
		storeFields(subElements, simCpb, successMessage);
			
			
			
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for Response3XXHandler as true");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for Response3XXHandler");

		if(!(message instanceof SipServletResponse)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip response message");
			return false;
		}

		SipServletResponse sipResponse= (SipServletResponse) message;
		if(!( node.getType().equals(Constants.INVITE_REDIRECT_RES_NODE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a Invite 3XX Node");
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
		RedirectResNode sipNode = (RedirectResNode) node;

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
				logger.debug("Leaving validateMessage() for Response3XXHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for Response3XXHandler with status "+isValid);
		return isValid;
	}
	
	private void storeFields(List<Node> subElements, SimCallProcessingBuffer simCpb, SipServletMessage message) {
		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;
		Iterator<Node> subElemIterator = subElements.iterator();

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					var = new Variable();
					var.setVarName(varName);
				}

				String varField = setElem.getVarField();
				int startIndex = setElem.getStartIndx();
				int endIndx= setElem.getEndIndx();
				String varVal = null;
				String contact =null;
				if(varField.equals(HEADER_CONTACT.toLowerCase())){
					ListIterator<String > iterator=message.getHeaders(HEADER_CONTACT);
					StringBuilder contacts=new StringBuilder();
					if(iterator.hasNext()){
						contact = iterator.next();
						if(endIndx == -1){
							endIndx = contact.length();
						}
						contact = contact.substring(startIndex, endIndx);
						contacts.append(contact);
						//expect only single value in contact
//						while(iterator.hasNext()){
//							contacts.append("|");
//							contacts.append(iterator.next());
//						}
					}
					varVal=contacts.toString();
					if(logger.isDebugEnabled())
						logger.debug("Contact header read(pipe seperated values)::["+varVal+"]");
				} 
				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);

			}//end if check for set elem
		}//end while loop on subelem

	}

}
