/**
 * Filename: AseSipNotifyMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipServletRequest;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipSubscriptionStateHeader;

import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;



/**
 * This class provides a UA implememtation for handling NOTIFY messages
 * and its responses
 */

class AseSipNotifyMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside handleInitialRequest" +
									session.getLogId());
			
	      session.addOutstandingRequest(request);
		  session.incrementPrCount();
		  
		//BpInd17838
		if(Constants.NOTIFY_FLAG==false)
		{
			m_logger.error("NOTIFY is not an initial request");
			AseSipServletResponse resp = (AseSipServletResponse)request.createResponse(400);
			try
			{
				String body = new String("Initial Notify flag not set");
				resp.setContentLength(body.length());
				resp.setContent(body,"text/plain");
			}
			catch(Exception e)
			{
				m_logger.error("set Content exception",e);
			}

				try {
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception", e);
				}
		  	throw new
					AseSipMessageHandlerException("NOTIFY not an initial request");
		}
		else
		{
			int ret=0;
			ret|=CONTINUE;
			return ret;
		}
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

		  // Validate the CSEQ Number of the incoming NOTIFY
		  long diff = checkCSeq(request, session);
		  if(diff == 0) {
				m_logger.error("NOTIFY retransmission received... discarding it. " +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  } else if(diff < 0) {
				m_logger.error("NOTIFY request with invalid CSEQ received. " +
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

		  // Get the subscription from the request and see if we find a match
		  AseSipSubscription sub = request.getSubscription(false);
		  m_logger.debug("NOTIFY SUB = " + sub);
		  
		  if (true == session.doesSubscriptionExist(sub)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Matching subscription found in " +
										 "subscription list. " + session.getLogId());
				
				// See if the subscription is terminated
				if (false == this.isSubscriptionTerminated(request)) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription active. " +
											  "Return STATE_UPDATE and CONTINUE. " + 
											  "Leaving recvRequest" +
											  session.getLogId());
					 
					 int ret = 0;
					 ret |= CONTINUE;
					 ret |= STATE_UPDATE;
					 return ret;
				}
				else {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Subscription terminated. " +
											  "Cleaning up subscriptions " + 
											  session.getLogId());
					 
					 AseSipSubscription matchSub =
						  session.getMatchingSubscription(sub);
					 AseSipSubscription matchRefSub = null;
					 
					 if (null != matchSub.getReferencedId())
						  matchRefSub = AseSipSubscription.
								createReferencedSubscription(matchSub);

					 // Remove the subscriptions from the subscription list
					 // and the subscription manager
					 session.removeSubscription(matchSub);
					 session.removeSubscription(matchRefSub);
					 
					 session.getConnector().removeSubscription(matchSub,
					 											session);
					 session.getConnector().removeSubscription(matchRefSub,
					 											session);
					 
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
											  "Leaving recvRequest" +
											  session.getLogId());
					 
					 int ret = 0;
					 ret |= CONTINUE;
					 ret |= STATE_UPDATE;
					 return ret;
				}
		  }

		  // Subscription not found in subscription list
		  // Since we have reached this point the subscription exists in the
		  // subscription manager
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Subscription not found in subscriptions list" +
									"Try and find a match in the Subscription manager" +
									session.getLogId());

		  AseSipSubscription matchSub = session.getConnector().
				getMatchingSubscription(sub);

		  if (null == matchSub) {
				m_logger.error("No maching Subscription found. " +
									"Send 481 response" + session.getLogId());
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
					 m_logger.debug("Return NOOP. Leaving recvRequest. " +
										 session.getLogId());
				return genRetNoop();
		  }
		  
		  
		  // Check if subscripton is still active
		  if (false == this.isSubscriptionTerminated(request)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Active subscription. " +
										 "Adding subscription to the subscription " +
										 "list. Return STATE_UPDATE and CONTINUE. " + 
										 "Leaving recvRequest" +
										 session.getLogId());

				session.addSubscription(sub);
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Terminated subscription. " +
										 "Remove subscription from subscription " +
										 "manager. Return STATE_UPDATE and CONTINUE. " + 
										 "Leaving recvRequest" +
										 session.getLogId());

				AseSipSubscription matchRefSub = null;
				
				if (null != matchSub) {
					 if (null != matchSub.getReferencedId())
						  matchRefSub = AseSipSubscription.
								createReferencedSubscription(matchSub);
				}
				session.getConnector().removeSubscription(matchSub, session);
				if (null != matchRefSub)
					 session.getConnector().removeSubscription(matchRefSub,
					 											session);
				
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest " +
									session.getLogId());

		//BpInd17838
		if(Constants.NOTIFY_FLAG==false)
		{
			m_logger.error("NOTIFY is not an initial request");
		  	throw new
					AseSipMessageHandlerException("NOTIFY not an initial request");
		}
		else
		{
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  session.incrementPrCount();

		  // Set the default handler, if not already set
		  try
		  {
		  	session.updateDefaultHandler();	
		  }
		  catch(Exception e) 
		  {
		   	m_logger.error("Exception setting the default handler", e);
		   	throw new AseSipMessageHandlerException(e.toString());
		  }
			int ret=0;
			ret|=CONTINUE;
			return ret;
		}
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


		  // Get the subscription from the request and see if we find a match
		  AseSipSubscription sub = request.getSubscription(false);

		//if (false == session.doesSubscriptionExist(sub)) {
		//		m_logger.error("No matching subscription found." +
		//							session.getLogId());
		//		throw new
		//			 AseSipMessageHandlerException("No matching subscription");
		//}
		  
		  // Increment the Pending request count
		  session.incrementPrCount();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Matching subscription found in " +
									"subscription list. " + session.getLogId());
		  
		  // See if the subscription is terminated
		  if (false == this.isSubscriptionTerminated(request)) {
		  		if (false == session.doesSubscriptionExist(sub)) {
					if (m_logger.isDebugEnabled())
					 	m_logger.debug("Subscription not found in list. " +
										 "Adding into it. " + 
										 session.getLogId());
						session.addSubscription(sub);
				}

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Subscription active. " +
										 "Return STATE_UPDATE and CONTINUE. " + 
										 "Leaving recvRequest" +
										 session.getLogId());
				
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Subscription terminated. " +
										 "Cleaning up subscriptions " + 
										 session.getLogId());
				
				AseSipSubscription matchSub =
						  session.getMatchingSubscription(sub);
				AseSipSubscription matchRefSub = null;
					 
				if (matchSub!=null && null != matchSub.getReferencedId()){
					 matchRefSub = AseSipSubscription.
						  createReferencedSubscription(matchSub);
				}
				
				// Remove the subscriptions from the subscription list
				session.removeSubscription(matchSub);
				session.removeSubscription(matchRefSub);
				
				if (m_logger.isDebugEnabled())
						  m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
											  "Leaving recvRequest" +
											  session.getLogId());
				
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
		  }
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
					 m_logger.debug("100 NOTIFY response. Return NOOP. " +
										 "Leaving recvResponse" +
										 session.getLogId());

				return genRetNoop();
		  }
				
		  // If other 1XX response then CONTINUE
		  if (1 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("1XX NOTIFY response. Return CONTINUE. " +
										 "Leaving recvResponse" +
										 session.getLogId());

				return genRetContinue();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("NOTIFY Final response. " +
									"Return STATE UPDATE and CONTINUE. " +
									"Leaving recvResponse" +
									session.getLogId());
		
		  int ret = 0;

		  //BpInd17838
		  if(!response.getRequest().isInitial())  
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


		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();

		  // If 1XX response then NOOP
		  if (1 == responseClass) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("1XX SUBSCRIBE response. Return CONTINUE. " +
										 "Leaving sendResponse" +
										 session.getLogId());

				return genRetContinue();
		  }

		  // Final response for SUBSCRIBE sent
		  // Decrement pending request count
		  session.decrementPrCount();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("NOTIFY Final response. " +
									"Return STATE UPDATE and CONTINUE. " +
									"Leaving recvResponse" +
									session.getLogId());
		
		  int ret = 0;
		  //BpInd17838
		  if(!response.getRequest().isInitial())  
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
	  * Method to check the state of the subscription
	  */
	 private boolean isSubscriptionTerminated(AseSipServletRequest request) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering isSubscriptionTerminated");

		  // Retrieve the DsSipSubscriptionStateHeader
		  DsSipHeader ssHeader = null;
		  try {
				ssHeader = request.getDsRequest().
					 getHeaderValidate(DsSipConstants.SUBSCRIPTION_STATE);
		  }
		  catch (Exception e) {
				// Should not happen. Validation is already done
				m_logger.error("Error retrieving Subscription-State Header ", e);
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return TRUE. " +
										 "Leaving isSubscriptionTerminated");

				return true;
		  }

		  DsByteString sState =
				((DsSipSubscriptionStateHeader)ssHeader).getState();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Subscription State = [ " + sState.toString() +
									"]");

		  boolean retVal = false;
		  if (true == DsByteString.equals(DsSipConstants.BS_TERMINATED,
													 sState)) {
				retVal =  true;
		  }
		  else
				retVal = false;
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving isSubscriptionTerminated. Return " +
									retVal);

		  return retVal;
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger = Logger.getLogger(AseSipNotifyMessageHandler.class);
}

