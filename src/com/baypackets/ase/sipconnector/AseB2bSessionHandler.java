package com.baypackets.ase.sipconnector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.UAMode;

import org.apache.log4j.Logger;

public class AseB2bSessionHandler implements Serializable{
	private static final long serialVersionUID = 341951848884370331L;
	private static Logger logger = Logger.getLogger(AseB2bSessionHandler.class);

	public static final String CSEQ_HEADER = "CSeq";
	
	//FT Handling strategy Update: Replication will be done for the provisional
	//responses as well, so need to replicate the list for pending messages corresponding
	//to INVITE request.

	private List<SipServletMessage> pendingUacMessages = Collections.synchronizedList(new ArrayList<SipServletMessage>());
	private List<SipServletMessage> pendingUasMessages = Collections.synchronizedList(new ArrayList<SipServletMessage>());
	private Map<String, SipServletRequest> linkedRequests = new HashMap<String, SipServletRequest>();
	
	public void sendRequest(AseSipServletRequest request) {
		if(logger.isDebugEnabled()){
			logger.debug("B2BHandler::sendRequest :" + request.getSession().getId() + "::" + request.getHeader(CSEQ_HEADER));
		}
		this.checkForPendingMessage(request, UAMode.UAC);
	}

	public void sendResponse(AseSipServletResponse response) {
		if(logger.isDebugEnabled()){
			logger.debug("B2BHandler::sendResponse :" + response.getSession().getId() + "::" +  response.getHeader(CSEQ_HEADER));
		}
		this.checkForPendingMessage(response, UAMode.UAS);

		//Remove the linked request when the transaction ends...
		if(response.isFinalResponse()){
			this.removeLinkedRequest((AseSipServletRequest)response.getRequest());
		}
	}

	public void receiveRequest(AseSipServletRequest request) {
		if(logger.isDebugEnabled()){
			logger.debug("B2BHandler::receiveRequest :" + request.getSession().getId() + "::" +  request.getHeader(CSEQ_HEADER));
		}

		this.checkForPendingMessage(request, UAMode.UAS);
	}

	public void receiveResponse(AseSipServletResponse response) {
		if(logger.isDebugEnabled()){
			logger.debug("B2BHandler::receiveResponse :" + response.getSession().getId() + "::" +  response.getHeader(CSEQ_HEADER));
		}

		this.checkForPendingMessage(response, UAMode.UAC);
		
		//Remove the linked request when the transaction ends...
		if(response.isFinalResponse()){
			this.removeLinkedRequest((AseSipServletRequest)response.getRequest());
		}
	}

	public void storeLinkedRequest(AseSipServletRequest request ){
		this.linkedRequests.put(request.getHeader(CSEQ_HEADER), request);
	}
	
	public void removeLinkedRequest(AseSipServletRequest request ){
		this.linkedRequests.remove(request.getHeader(CSEQ_HEADER));
	}
	
	public SipServletRequest getLinkedRequest(String id){
		return this.linkedRequests.get(id);
	}
	
	public List<SipServletMessage> getPendingMessages(UAMode mode){
		List<SipServletMessage> list = (mode == UAMode.UAC) ? pendingUacMessages : pendingUasMessages;
		return list;	
	}
	
	protected void checkForPendingMessage(AseSipServletMessage message, UAMode mode){
		
		List<SipServletMessage> list = this.getPendingMessages(mode);
		if(logger.isDebugEnabled()){
			logger.debug("Check Pending ::" + message.getClass().getSimpleName() + "::" + message.getHeader(CSEQ_HEADER));
		}
		
		//Remove the already committed messages.
		boolean commited = false;
		Iterator<SipServletMessage> it = list.iterator();
		for(;it.hasNext();){
			SipServletMessage tmp = it.next();
			commited = tmp.isCommitted();
			if(logger.isDebugEnabled()){
				logger.debug("Check Pending ::" + commited + "::" + tmp.getHeader(CSEQ_HEADER));
			}
			if(commited) {
				it.remove();
			}
		}
		
		//Add this message, if it is not commited..
		commited = message.isCommitted();
		if(!commited && !message.getMethod().equals("ACK")){
			if(logger.isDebugEnabled()) {
				logger.debug("Added message to pending::" + commited + "::" + message.getHeader(CSEQ_HEADER));
			}
			list.add(message);
		}
	}
}
