/**
 * AseSipInviteMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import java.util.Random;

import com.baypackets.ase.util.*;
import com.baypackets.ase.container.AseApplicationSession;

/**
 * This class provides a UA implememtation for handling INVITE messages
 * and its responses
 */

class AseSipInviteMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  // Add this request as an outstanding request and increment PR count
		  session.addOutstandingRequest(request);
		  session.incrementPrCount();
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving handleInitialRequest " +
									session.getLogId());

		  int ret = 0;
		  ret |= CONTINUE;
		  ret |= STATE_UPDATE;
		  return ret;
	 }
	 
	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());

		  // If dialog is terminated or session is invalid generate
		  // and send a 481 response
		  boolean send481 = false;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " +
									"Create and send 481 response" +
									session.getLogId());
				send481 = true;
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									"Create and send 481 response" +
									session.getLogId());
				send481 = true;
		  }
				
		  if (true == send481) {
				AseSipServletResponse resp = 
					 (AseSipServletResponse)request.
					 createResponse(481);
				try {
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception", e);
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP. " +
										 "Leaving handleSubsequentRequest " +
										 session.getLogId());

				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest. " +
									"Return CONTINUE. " + session.getLogId());
		  
		  return genRetContinue();
	 }

	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest " +
									session.getLogId());
		  int direction = session.checkDirection(request);
		  // Add this request as an outstanding request and increment PR Count
		  session.addOutstandingRequest(request);
		  session.incrementPrCount();

		  // Validate the CSEQ Number of the incoming INVITE
		  long diff = checkCSeq(request, session);
		  long cseq = request.getDsRequest().getCSeqNumber();
		  if((diff == 0) && session.isRequestOutstanding(cseq)) {
				m_logger.error("INVITE retransmission received... discarding it. " +
															session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  } else if(diff <= 0) {
				m_logger.error("INVITE request with invalid CSEQ received. " +
									"Sending a 500 response" + session.getLogId());
				AseSipServletResponse resp = 
					 (AseSipServletResponse)request.
					 createResponse(500, "Server Internal Error");
				try {
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception", e);
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  }

		  // If dialog is terminated or session is invalid generate
		  // and send a 481 response
		  boolean send481 = false;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " +
									"Create and send 481 response" +
									session.getLogId());
				send481 = true;
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									"Create and send 481 response" +
									session.getLogId());
				send481 = true;
		  }
				
		  if (true == send481) {
				AseSipServletResponse resp = 
					 (AseSipServletResponse)request.createResponse(481);
				try {
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception", e);
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  }
		  
		  if(AseSipSessionState.STATE_EARLY==session.getSessionState())
		  {
			  
			  if(direction == session.DIR_UPSTREAM)
			  {
				  m_logger.error("re-INVITE received with early dialog in downstream direction " +
							"Sending a 500 response" + session.getLogId());
				  AseSipServletResponse resp = 
					  (AseSipServletResponse)request.
				  		createResponse(500, "Server Internal Error");
				  Random randomizer= new Random();
				  int retryAfter = randomizer.nextInt(11); 
			  
				  //String interval = null;
				  String interval=Integer.toString(retryAfter);
			  
				  resp.addHeader("Retry-After" , interval);
				  try {
					  	session.sendResponse(resp);
				  }
				  catch (Exception e) {
					  m_logger.error("sendResponse Exception", e);
				  	}
			  
				  return genRetNoop();
			  }
			  if(direction == session.DIR_DOWNSTREAM)
			  {
				  m_logger.error("re-INVITE received with early dialog in upstream direction" +
							"Sending a 491 response" + session.getLogId());
				  AseSipServletResponse resp = 
					  (AseSipServletResponse)request.
					  	createResponse(491, "Request Pending");
				  try {
					  	session.sendResponse(resp);
				  }
				  catch (Exception e) {
					  m_logger.error("sendResponse Exception", e);
				  }
				  return genRetNoop();
			  }
			  
		  }
					 
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. Leaving " +
									"recvRequest" + session.getLogId());

		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest " +
									session.getLogId());

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Dialog Terminated");
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  // Add this request as an outstanding request and increment PR count
		  session.addOutstandingRequest(request);
		  session.incrementPrCount();

		  // Set the default handler, if not already set
		  try {
			session.updateDefaultHandler();
		  } catch(Exception e) {
			m_logger.error("Exception setting the default handler", e);
			throw new AseSipMessageHandlerException(e.toString());
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. Leaving " +
									"sendInitialRequest" + session.getLogId());

		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest " +
									session.getLogId());

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Dialog Terminated");
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  // Add this request as an outstanding request and increment PR count
		  session.addOutstandingRequest(request);
		  session.incrementPrCount();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. Leaving " +
									"sendSubsequentRequest" + session.getLogId());

		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPreSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPostSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleResponse. " +
									session.getLogId());

		  // If dialog is terminated or session is invalidated
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  /*** BPInd18609 fix starts
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Dialog terminated");
		  }
		  **** BPInd18609 fix ends */

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse. " +
									"Return CONTINUE. " + session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvResponse " +
									session.getLogId());
		  
		if (response.getStatus() == 100) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Response is 100.  Returning...");
			}
			return genRetNoop();
		}	  

		long cseq = response.getDsResponse().getCSeqNumber();
		  
		  // Check if the request is still outstanding.
		  if (false == session.isRequestOutstanding(cseq)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("No matching request found. " +
										 session.getLogId());
				if(response.isFinalResponse())	{
					throw new AseSipMessageHandlerException("No matching Request");
				}else	{
					if (m_logger.isDebugEnabled()) {
                                		m_logger.debug("Response is Provisional.Return with no operation...");
                        		}
                        		return genRetNoop();
				}
		  }
		  //Overloaded the exception constructor for solving UAT 792
		  // If dialog is terminated or session is invalidated
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated",true);
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Dialog terminated",true);
		  }

		  // If 100 INVITE response then  nothing to do
		  if (100 == response.getDsResponse().getStatusCode()) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("100 INVITE response. Return NOOP" +
										 session.getLogId());
					 m_logger.debug("Leaving recvResponse " +
										 session.getLogId());
				}
			   return genRetNoop();
		  }

		  // If other 1XX responses send them on their way
		  // Later add RPR code here
		  if (1 == response.getDsResponse().getResponseClass()) {
				if (true == response.isReliable()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("1XX Reliable INVITE response. " +
											  session.getLogId());
					 if (AseSip100RelHandlerInterface.NOOP ==
						  session.recvReliableResponse(response)) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("PRACK handler returned NOOP. " +
													"Leaving recvResponse. Return NOOP." +
													session.getLogId());
						  return genRetNoop();
					 }
				}
				
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("1XX INVITE response. Return STATE_UPDATE " +
										 "and CONTINUE" +
										 session.getLogId());
					 m_logger.debug("Leaving recvResponse " +
										 session.getLogId());
				}

				int ret = 0;
				ret |= STATE_UPDATE;
				ret |= CONTINUE;
				return ret;
		  }

		  AseSipServletRequest req = null;
		  
		  // If 2XX responses, remove outstanding request and add to the
		  // success list and send them on their way
		  // Later add RPR code here
		  if (2 == response.getDsResponse().getResponseClass()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("2XX INVITE response." + session.getLogId());

				req = session.removeOutstandingRequest(response.getDsResponse().
																	getCSeqNumber());
				session.addSuccessRequest(req);
				
				session.recvFinalResponse(response);

				// Mark the fact that an invitation is in progress
				session.setInvitation();
				
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Return STATE_UPDATE and CONTINUE." +
										 session.getLogId());
					 m_logger.debug("Leaving recvResponse " +
										 session.getLogId());
				}
				
				int ret = 0;
				ret |= STATE_UPDATE;
				ret |= CONTINUE;
				return ret;
		  }

		  // If 3XX-6XX responses, unset invitation and send them on their way
		  // Later add RPR code here
		  if (m_logger.isDebugEnabled())
				m_logger.debug("3XX-6XX INVITE response. " +
									"Generate and send an ACK" + session.getLogId());

		  req = session.removeOutstandingRequest(response.getDsResponse().
															  getCSeqNumber());
		  session.addFailureRequest(req);
		  session.recvFinalResponse(response);
		  
		  if(response.getSource() != AseSipConstants.SRC_ASE) {
			  try {
					AseSipServletRequest ackReq =
						 (AseSipServletRequest)response.createAck();
					if(AseUtils.getCallPrioritySupport() == 1)      {
	                	if(((AseApplicationSession)response.getApplicationSession()).getPriorityStatus())    {
	                    	String rphValue = response.getRequest().getHeader(Constants.RPH);
	                    		if(rphValue != null)    {
	                    			ackReq.setHeader(Constants.RPH,rphValue);
	                    		}
	                   }
	                }
		
					session.sendRequest(ackReq);
			  }
			  catch (AseSipSessionException exp)	{
			  	//Don't log error here
			  }
			  catch (Exception e) {
					m_logger.error("Exception generating and sending ACK", e);
			  }
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return STATE_UPDATE and CONTINUE." +
									session.getLogId());
				m_logger.debug("Leaving recvResponse " +
									session.getLogId());
		  }
		  
		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse " +
									session.getLogId());

		  // If dialog is terminated or session is invalidated
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Dialog terminated");
		  }

		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();
		  
		  // INVITE requests can be cancelled. Check if the underlying
		  // request has been cancelled.
		  // If it is cancelled allow only 487 responses
		  AseSipServletRequest req =
				(AseSipServletRequest)(response.getRequest());
		  
		  if (req.isCancelled() && 487 != statusCode) {

			  if (m_logger.isDebugEnabled())
				  m_logger.debug("Request has been cancelled. " +
						  session.getLogId());
			  
			  req.setUnCommitted();

			  throw new
			  AseSipMessageHandlerException("Request has been cancelled");

		  }
		  
		  // Check if the request is still outstanding.
		  if (false == session.isRequestOutstanding(response.getDsResponse().
																  getCSeqNumber())) {
				if (m_logger.isDebugEnabled())
					 m_logger.error("No matching request found. " +
										 session.getLogId());
				throw new AseSipMessageHandlerException("No corresponding" +
																	 "request found");
		  }
				
		  // If 100 INVITE response then send it on its way
		  if (100 == statusCode) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("100 INVITE response. Return CONTINUE" +
										 session.getLogId());
					 m_logger.debug("Leaving sendResponse " +
										 session.getLogId());
				}
				return genRetContinue();
		  }

		  // If other 1XX responses
		  // Later add RPR code here
		  if (1 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("NON 100 1XX INVITE response." +
										 session.getLogId());
				if (true == response.isReliable()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Reliable 1XX INVITE response." +
											  session.getLogId());
					 session.sendReliableResponse(response);
				}
				
		  }
		  // 2XX response
		  else if (2 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("2XX INVITE response." + session.getLogId());

				//BPInd10494. Moved this line before removing the req from outstanding list.
				//This was done to ensure that 
				//if there is any validation exception on the session.sendFinalResponse(), 
				//then request will not be removed from the outstanding request list.	
				session.sendFinalResponse(response);

				req = session.removeOutstandingRequest(response.getDsResponse().
																	getCSeqNumber());
				session.addSuccessRequest(req);

				// Mark the fact that an invitation is set
				session.setInvitation();
		  }
		  // 3XX - 6XX response
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("3XX-6XX INVITE response." +
										 session.getLogId());
				
				//BPInd10494. Moved this line before removing the req from outstanding list.
				//This was done to ensure that 
				//if there is any validation exception on the session.sendFinalResponse(), 
				//then request will not be removed from the outstanding request list.	
				session.sendFinalResponse(response);

				req = session.removeOutstandingRequest(response.getDsResponse().
																	getCSeqNumber());
				session.addFailureRequest(req);
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return STATE_UPDATE and CONTINUE." +
									"Leaving sendResponse " +
									session.getLogId());
		  }
		  
		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipInviteMessageHandler.class);
}

