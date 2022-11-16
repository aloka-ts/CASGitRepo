/**
 * AseSipByeMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipServletRequest;

/**
 * This class provides a UA implememtation for handling BYE messages
 * and its responses
 */

class AseSipByeMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("BYE is not an initial Request");
		  throw new AseSipMessageHandlerException("BYE not an initial request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Sending a 481 response" +
									session.getLogId());
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
					 m_logger.debug("Return NOOP. Leaving " +
										 "handleSubsequentRequest" + session.getLogId());
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest " +
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

		  session.incrementPrCount();

		  // Validate the CSEQ Number of the incoming BYE
		  long diff = checkCSeq(request, session);
		  if(diff == 0) {
				m_logger.error("BYE retransmission received... discarding it. " +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  } else if(diff < 0) {
				m_logger.error("BYE request with invalid CSEQ received. " +
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


		  // If dialog is terminated or session is invalidated
		  boolean send481 = false;
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Sending a 481 response" +
									session.getLogId());
				send481 = true;
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. Sending a 481 response" +
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
					 m_logger.debug("Return NOOP. Leaving " +
										 "recvRequest" + session.getLogId());
				return genRetNoop();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. Leaving " +
									"recvRequest" + session.getLogId());

		  session.resetInvitation();

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

		  m_logger.error("BYE is not an initial request");
		  throw new AseSipMessageHandlerException("BYE not an initial request");
	 }

	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipByeMessageHandler " +
									"sendSubsequentRequest" + session.getLogId());

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." +
									session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. " +
									session.getLogId());
				throw new AseSipMessageHandlerException("Dialog terminated");
		  }

        // Check if there is an outstanding INVITE-2XX ACK that the
        // application has not sent as yet. If there is then throw
        // an exception
        //This is commented as we can still send BYE if ACK has not yet been sent
		//Refer Sec 15 of RFC 3261
		  /*if (true == session.isAckOutstanding()) {
				m_logger.error("Outstanding INVITE-2XX ACK. Cannot send a BYE. " +
						session.getLogId());
				throw new AseSipMessageHandlerException("Outstanding ACK");
		  }*/

		  // Mark the fact that a BYE has been sent
		  session.resetInvitation();
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
				m_logger.debug("Entering handleResponse " +
									session.getLogId());
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eating up the response" +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleResponse. Return NOOP. " +
										 session.getLogId());
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse. Return CONTINUE. " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvResponse. " +
									session.getLogId());

		  // Decrement the Pending Request Count
		  session.decrementPrCount();
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eating up the response" +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvResponse. Return NOOP. " +
										 session.getLogId());
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. Return CONTINUE. " +
									session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse. " +
									session.getLogId());
				
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  session.decrementPrCount();
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving sendResponse. Return CONTINUE. " +
									session.getLogId());
		  
		  return genRetContinue();
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
		  Logger.getLogger(AseSipByeMessageHandler.class);
}

