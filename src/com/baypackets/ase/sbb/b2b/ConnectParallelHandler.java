package com.baypackets.ase.sbb.b2b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sipconnector.AseSipSessionState;


/**
 * Implementation of the connectParallel handler.
 * This class is responsible for handling connectParallel operation. 
 * ConnectParallel operation will send INVITE requests to multiple destination in parallel
 * and connects with the party from which the success response is received first
 * and all the other call legs are cancelled.
 */

public class ConnectParallelHandler extends ConnectHandler implements java.io.Serializable {
	private List<URI> bPartyURIList = null;
	private ArrayList<SipServletRequest> peerReqList = new ArrayList<SipServletRequest>();
	private boolean isCallConnected = false;
	private static Logger logger = Logger.getLogger(ConnectParallelHandler.class.getName());
	private SipSession partyA = null;
	private SipSession partyB = null;

	public ConnectParallelHandler() {
		super();
	}

	public ConnectParallelHandler(SipServletRequest request,
			List<URI> termPartyList) {	
		super(request,null);
		this.bPartyURIList = termPartyList;
	}

	public ConnectParallelHandler(SipServletRequest request,
			List<URI> termPartyList, Address from) {
		super(request,null,from);
		this.bPartyURIList = termPartyList;
	}

	/**
     * This method will be invoked to start connectParallel operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     */
	
	public void start(){
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> ConnectParallelHandler entered start() ");
		}

		SipServletRequest requestOut = null;
		SBB sbb = (SBB)getOperationContext();
		partyA = requestIn.getSession();
		sbb.addA(partyA); // Add A party
		requestIn.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestIn);
		try{
			SipFactory factory =  (SipFactory)sbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			for(URI uri : bPartyURIList){
				Address termParty = factory.createAddress(uri);
				requestOut = factory.createRequest(requestIn.getApplicationSession(), requestIn.getMethod(), requestIn.getFrom(), termParty);
				if(requestIn.getContent() !=null){
					requestOut.setContent(requestIn.getContent(),requestIn.getContentType());
				}
				SipSession bParty = requestOut.getSession();
				bParty.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestOut);
				bParty.setHandler(Constants.SBB_SERVLET_NAME);
				bParty.setAttribute(SBBOperationContext.ATTRIBUTE_SBB,sbb.getName());
				peerReqList.add(requestOut);
				sendRequest(requestOut);		
			}
		}catch(Exception e){
			logger.error("Exception starting ConnectParallelHandler " + e.getMessage());
		}
	}

	/**
	 * This method handles the following scenarios
	 * 
	 * 1. CANCEL received from A-Party, then all the B-party call legs are cancelled
	 * 2. INVITE received from A-Party, then one of the B-parties is connected and rest of the 
	 * 	  legs are cancelled.
	 * 3.    
	 */

	public void handleRequest(SipServletRequest request){
		logger.debug("<SBB> handleRequest ConnectParallelHandler");
		if(request.getMethod().equals(Constants.METHOD_CANCEL)){
			request.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_EARLY));
			super.handleRequest(request);
			cancelOtherCallLegs();
		}else{
			super.handleRequest(request);
		}
	}

	/**
	 * This method handles the response received.
	 */
	public void handleResponse(SipServletResponse response) {
		int responseCode = response.getStatus();
		logger.debug("<SBB> ConnectParallelHandler handleResponse : " + responseCode);
		switch(responseCode/100){
		case Constants.RESPONSE_1XX:
			handleProvisionalResponse(response,responseCode);
			break;
		case Constants.RESPONSE_2XX:
			handleSuccessResponse(response,responseCode);
			break;
		case Constants.RESPONSE_3XX:
			handleRedirectionResponse(response,responseCode);
			break;
		case Constants.RESPONSE_4XX:
		case Constants.RESPONSE_5XX:
			handleErrorResponse(response,responseCode);
			break;
		default:
			break;
		}
	}



	/**
	 * This method handles the 3xx response received.
	 */
	private void handleRedirectionResponse(SipServletResponse response,
			int responseCode) {
		super.handleResponse(response);

	}

	/**
	 * This method handles the Provisional Response received.
	 * 
	 */
	private void handleProvisionalResponse(SipServletResponse response, int responseCode){
		super.handleResponse(response);
		if(isCallConnected){
			try {
				response.getRequest().createCancel().send();
			} catch (Exception e) {
				logger.error("ConnectParallelHandler Exception handlling Provisional Response  " + e.getMessage());
			}
		}
	}
	
	/**
	 * This method handles the Success Response received.
	 * If the call is not connected, then one of the B-parties is connected and others are cancelled.
	 * If the call is connected and sucess response is received from one of the B-parties,
	 * then BYE is send to that B-party.
	 * 
	 */

	private void handleSuccessResponse(SipServletResponse response, int responseCode){
		SBB sbb = (SBB)getOperationContext();
		try{
			if(response.getMethod().equals(Constants.METHOD_INVITE)){
				if(!isCallConnected){
					isCallConnected = true;
					requestOut = response.getRequest();
					peerReqList.remove(requestOut);
					cancelOtherCallLegs();
					partyB = response.getSession();
					sbb.addB(partyB); // Add B Party
					super.handleResponse(response);
				}else{
					response.getSession().createRequest(Constants.METHOD_BYE).send();
				}
			}else{
				super.handleResponse(response);
			}
		}catch(Exception e){
			logger.error("ConnectParallelHandler Exception handling Success Response  " + e.getMessage());
		}
	}

	/**
	 * This method handles the Error Response received.
	 * 1. Error response other than 487 is received , this leg is removed from the cancellation list.
	 * 	  CANCEL received from A-party, rest of the B-legs are cancelled. 
	 * 2. 487 is received, ACK is send by the container.
	 * 
	 */
	private void handleErrorResponse(SipServletResponse response, int responseCode){

		if(responseCode == SipServletResponse.SC_REQUEST_TERMINATED){
			logger.debug("<SBB> ConnectParallelHandler 487 received for CANCEL request. Returning");
			return;
		}else{
			logger.debug("<SBB> ConnectParallelHandler " + responseCode + " received for INVITE request.");
			if(peerReqList.size() == 1){
				super.handleResponse(response);
			}else{
				logger.debug("<SBB> ConnectParallelHandler Removing request for the list");
				peerReqList.remove(response.getRequest());
			}
		}
	}
	
	
	/**
	 * isMatching method is overriden to ensure that the CANCEL received from A-Party is 
	 * handled by the ConnectParallel Handler.
	 */

	public boolean isMatching(SipServletMessage message){
		if(logger.isDebugEnabled()){
			logger.debug("<SBB> entered isMatching ConnectParallelHandler");
		}

		if (message instanceof SipServletRequest) 
		{
			if (message.getMethod().equalsIgnoreCase(Constants.METHOD_CANCEL) && partyA == message.getSession()){
				return true;
			}else{
				return super.isMatching(message);
			}
		}
		else {
			return super.isMatching(message);
		}

	}

	 /**
     * This method is used to cancel other legs
     */
	private void cancelOtherCallLegs() {

		if (logger.isDebugEnabled()) {
			logger.debug(" cancelOtherCallLegs() entering....");
		}
		if (peerReqList != null && !peerReqList.isEmpty()) {
			Iterator<SipServletRequest> itr = peerReqList.iterator();
			while (itr.hasNext()) {
				SipServletRequest request = itr.next();
				SipSession peerSession = request.getSession();
				try {
					switch(peerSession.getState()){
					case INITIAL: 
					case EARLY:
						if (logger.isInfoEnabled()) {
							logger.info("<SBB> Downstream Dialog is in INITIAL/EARLY state, sending a CANCEL request");
						}
						request.createCancel().send();
						break;

					case CONFIRMED:
						peerSession.createRequest(Constants.METHOD_BYE).send();
						break;

					default:
						break;
					}

				} catch (Exception e) {
					logger.error("cancelOtherCallLegs(): An Exception occured during cancel" + e);
				}

			}
			if (logger.isDebugEnabled()) {
				logger.debug(" cancelOtherCallLegs() exiting....");
			}
		}
	}
}
