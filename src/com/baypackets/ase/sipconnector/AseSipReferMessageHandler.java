/**
 * AseSipReferMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipServletRequest;

/**
 * This class provides a UA implememtation for handling REFER messages
 * and its responses
 */

class AseSipReferMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  // Increment the pending request count
		  session.incrementPrCount();

		  // Force the request to create the subscriptions
		  request.getSubscription(true);
		  request.getReferencedSubscription();
		  session.firstReferSent();
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving handleInitialRequest" +
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
				m_logger.debug("Inside handleSubsequentRequest " +
									"Return CONTINUE. " +
									session.getLogId());

		  return genRetContinue();
	 }

	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest " +
									session.getLogId());

		  // Increment the pending request count
		  session.incrementPrCount();

		  // Validate the CSEQ Number of the incoming REFER
		  long diff = checkCSeq(request, session);
		  if(diff == 0) {
				m_logger.error("REFER retransmission received... discarding it. " +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  } else if(diff < 0) {
				m_logger.error("REFER request with invalid CSEQ received. " +
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

		  // Force the request to create the subscriptions
		  if (true == session.isFirstRefer()) {
				request.getSubscription(true);
				request.getReferencedSubscription();
		  }
		  else {
				request.getSubscription(false);
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving recvRequest" +
									session.getLogId());

		  int ret = 0;
		  ret |= CONTINUE;
		  ret |= STATE_UPDATE;
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

		  // Increment the pending request count
		  session.incrementPrCount();

		  // Since we are sending an initial request, no dialog has
		  // been established as yet. We need to take care of the case
		  // where a NOTIFY is received before the REFER response.

		  // For the first REFER in the dialog we need to work with two
		  // subscriptions. The original subscription and the referenced
		  // subscription
		  // For the second and consequitive REFER's we just need the
		  // original subscription
		  // Since this is an original request so there can be no REFER's
		  // in this dialog as yet.
		  AseSipSubscription sub1 = request.getSubscription(true);
		  AseSipSubscription sub2 = request.getReferencedSubscription();
		  m_logger.debug("SUB1 = " + sub1);
		  m_logger.debug("SUB2 = " + sub2);
		  
		  session.getConnector().
				addSubscription(sub1, (AseSipSession)(request.getSession()));
		  session.getConnector().
				addSubscription(sub2, (AseSipSession)(request.getSession()));

		  // Mark the fact that we have sent the 1st Refer in this dialog
		  session.firstReferSent();
		  
		  // Set the default handler, if not already set
		  try {
			session.updateDefaultHandler();
		  } catch(Exception e) {
			m_logger.error("Exception setting the default handler", e);
			throw new AseSipMessageHandlerException(e.toString());
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving sendInitialRequest" +
									session.getLogId());

		  int ret = 0;
		  ret |= CONTINUE;
		  ret |= STATE_UPDATE;
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

		  // Increment the Pending request count
		  session.incrementPrCount();

		  // Force the request to create a subscription
		  // Check if this is the first REFER or not before doing this
		  if (true == session.isFirstRefer()) {
		      session.firstReferSent();

				AseSipSubscription sub1 = request.getSubscription(true);
				AseSipSubscription sub2 = request.getReferencedSubscription();
		      session.getConnector().
				    addSubscription(sub1, (AseSipSession)(request.getSession()));
		      session.getConnector().
				    addSubscription(sub2, (AseSipSession)(request.getSession()));
		  }
		  else {
				AseSipSubscription sub = request.getSubscription(false);
		      session.getConnector().
				    addSubscription(sub, (AseSipSession)(request.getSession()));
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving sendSubsequentRequest" +
									session.getLogId());

		  int ret = 0;
		  ret |= CONTINUE;
		  ret |= STATE_UPDATE;
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
		  boolean eatItUp = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }

		  if (true == eatItUp) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleResponse. Return NOOP" +
										 session.getLogId());
				
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse. Return CONTINUE" +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvResponse " +
									session.getLogId());

		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();

		  // If final response then decrement the PR count
		  if (1 != responseClass)
				session.decrementPrCount();
		  
		  // If dialog is terminated or session is invalidated
		  boolean eatItUp = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }

		  if (true == eatItUp) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvResponse. Return NOOP" +
										 session.getLogId());
				
				return genRetNoop();
		  }

		  // If 100 response then NOOP
		  if (100 == statusCode) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("100 REFER response. Return NOOP. " +
										 "Leaving recvResponse" +
										 session.getLogId());

				return genRetNoop();
		  }
				
		  // If other 1XX response then CONTINUE
		  if (1 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("1XX REFER response. Return CONTINUE. " +
										 "Leaving recvResponse" +
										 session.getLogId());

				return genRetContinue();
		  }

		  // If a 2XX response is received then a subscription has been created
		  if (2 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("2XX REFER response. " +
										 session.getLogId());
				
				// we have already forced the request to create the subscriptions
				// Retrieve them here
				AseSipServletRequest req =
					 (AseSipServletRequest)response.getRequest();
				
				AseSipSubscription sub1 = req.getSubscription(false);
				AseSipSubscription sub2 = null;
				if (null != sub1.getReferencedId())
					 sub2 = req.getReferencedSubscription();
				
				// If the subscription does not exist in the subscription list
				// then add it
				if (false == session.doesSubscriptionExist(sub1)) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription does not exist in list. " +
						  				"sub = " + sub1.toString() +
											  "Adding it" + session.getLogId());
					 
					 session.addSubscription(sub1);
					 if (null != sub2) {
					 	  if (m_logger.isDebugEnabled())
						  	m_logger.debug("ref-sub = " + sub2.toString());
						  session.addSubscription(sub2);
					}
				}
				else {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription exists in list. NOOP" +
											  session.getLogId());
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return STATE_UPDATE and CONTINUE." +
										 "Leaving recvResponse " +
										 session.getLogId());
				
				int ret = 0;
				ret |= STATE_UPDATE;
				ret |= CONTINUE;
				return ret;
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("3XX - 6XX SUBSCRIBE response." +
									session.getLogId());
		  
		  // we have already forced the request to create the subscriptions
		  // Retrieve them here
		  AseSipServletRequest req =
				(AseSipServletRequest)response.getRequest();
		  
		  AseSipSubscription sub1 = req.getSubscription(false);
		  AseSipSubscription sub2 = null;
		  if (null != sub1.getReferencedId())
				sub2 = req.getReferencedSubscription();
		  
		  // If this is  response to the initial request
		  if (true == req.isInitial()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("3XX - 6XX SUBSCRIBE response to initial " +
										 "request. Remove subscription from the " +
										 "Subscription Manager. " +
										 session.getLogId());

				session.getConnector().removeSubscription(sub1, session);
				if (null != sub2)
					 session.getConnector().removeSubscription(sub2, session);
				
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return STATE_UPDATE and CONTINUE." +
										 "Leaving recvResponse " +
										 session.getLogId());
				
				int ret = 0;
				ret |= STATE_UPDATE;
				ret |= CONTINUE;
				return ret;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("3XX - 6XX SUBSCRIBE response to subsequent " +
										 "request. Nothing to do." + session.getLogId());
				
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return CONTINUE and STATE_UPDATE. " +
										 "Leaving recvResponse " +
										 session.getLogId());
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }
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

		  // If 1XX response then NOOP
		  if (1 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("1XX REFER response. Return CONTINUE. " +
										 "Leaving sendResponse" +
										 session.getLogId());

				return genRetContinue();
		  }

		  // Final response for SUBSCRIBE sent
		  // Decrement pending request count
		  session.decrementPrCount();

		  // If a 2XX response is been sent then a subscription has been created
		  if (2 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("2XX REFER response. " +
										 session.getLogId());
				
				AseSipServletRequest req =
					 (AseSipServletRequest)(response.getRequest());
				AseSipSubscription sub1 = req.getSubscription(false);
				AseSipSubscription sub2 = null;
				
				if (null != sub1.getReferencedId())
					 sub2 = req.getReferencedSubscription();
				
				// If the subscription does not exist in the subscription list
				// then add it
				if (false == session.doesSubscriptionExist(sub1)) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription does not exist in list. " +
						  				"sub = " + sub1.toString() +
											  "Adding it" + session.getLogId());
					 
					 session.addSubscription(sub1);
					 if (null != sub2) {
					 	  if (m_logger.isDebugEnabled())
						  	m_logger.debug("ref-sub = " + sub2.toString());
						  session.addSubscription(sub2);
					}
				}
				else {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription exists in list. NOOP" +
											  session.getLogId());
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return CONTINUE and STATE_UPDATE. " +
										 "Leaving sendResponse" +
										 session.getLogId());
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("3XX - 6XX SUBSCRIBE response. " +
									"Leaving sendResponse. " +
									"Return CONTINUE and STATE_UPDATE" +
									session.getLogId());
		  
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
		  Logger.getLogger(AseSipReferMessageHandler.class);
}

