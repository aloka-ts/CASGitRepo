/**
 * AseSipPrackMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipServletRequest;

/**
 * This class provides a UA implememtation for handling PRACK messages
 * and its responses
 */

class AseSipPrackMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("PRACK is not an initial Request");
		  throw new
				AseSipMessageHandlerException("PRACK not an initial request");
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
					 m_logger.debug("Return NOOP. " +
										 "Leaving handleSubsequentRequest " +
										 session.getLogId());

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

		  // Validate the CSEQ Number of the incoming PRACK
		  long diff = checkCSeq(request, session);
		  if(diff == 0) {
				m_logger.error("PRACK retransmission received... discarding it. " +
									session.getLogId());

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return NOOP . Leaving recvRequest " +
										 session.getLogId());

				return genRetNoop();
		  } else if(diff < 0) {
				m_logger.error("PRACK request with invalid CSEQ received. " +
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

		  // Send this to the AseSip100RelHandler
		  if (AseSip100RelHandlerInterface.NOOP == session.recvPrack(request)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("PRACK Handler returned NOOP. Return NOOP" +
										 "Leaving recvRequest" + session.getLogId());

				return NOOP;
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving " +
									"recvRequest" + session.getLogId());
		  
		  return genRetContinue();
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest " +
									session.getLogId());

		  m_logger.error("PRACK is not an initial Request");
		  throw new
				AseSipMessageHandlerException("PRACK not an initial request");
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest" +
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

		  // Send this to the AseSip100RelHandler
		  if (AseSip100RelHandlerInterface.NOOP == session.sendPrack(request)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("PRACK Handler returned NOOP. Return NOOP" +
										 "Leaving sendSubsequentRequest" +
										 session.getLogId());

				return NOOP;
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving " +
									"sendSubsequentRequest" + session.getLogId());

		  return genRetContinue();
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
				m_logger.debug("Entering handleResponse." +
									session.getLogId());

		  // If dialog is terminated or session is invalidated
		  boolean eatItUp = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }

		  /*** BPInd18609 fix starts
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }
		  **** BPInd18609 fix ends */

		  if (true == eatItUp) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvResponse. Return NOOP" +
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
				m_logger.debug("Entering recvResponse." +
									session.getLogId());

		  // If dialog is terminated or session is invalidated
		  boolean eatItUp = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. Eat it up" +
									session.getLogId());
				eatItUp = true;
		  }

		  if (true == eatItUp) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvResponse. Return NOOP" +
										 session.getLogId());
				
				return genRetNoop();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. Return CONTINUE" +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse." +
									session.getLogId());

		  if(response.getStatus() != 481) {
			  // If dialog is terminated or session is invalidated
			  if (false == isSessionValid(session)) {
				  m_logger.error("Session invalidated." + session.getLogId());
				  throw new AseSipMessageHandlerException("Session invalidated");
			  }

			  if (true == isDialogTerminated(session)) {
				  m_logger.error("Dialog terminated. " + session.getLogId());
				  throw new AseSipMessageHandlerException("Dialog terminated");
			  }
		  }

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
		  Logger.getLogger(AseSipPrackMessageHandler.class);
}

