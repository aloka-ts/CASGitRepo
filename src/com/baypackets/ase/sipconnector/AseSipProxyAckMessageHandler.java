/**
 * AseSipProxyAckMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class provides a PROXY implememtation for handling ACK messages
 */

class AseSipProxyAckMessageHandler extends AseSipProxyDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("ACK is not an initial request." + session.getLogId());
		  throw new AseSipMessageHandlerException("ACK not an initial request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());

		  // If dialog is terminated or session is invalid proxy the request
		  boolean proxyReq = false;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog terminated. Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }

		  // Application invocation for ACK will be decided by supervised flag,
		  // not by record-route flag
		  if (false == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised = FALSE. Proxy the request. " +
										 session.getLogId());
				proxyReq = true;
		  }
		  
		  if (true == proxyReq) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return PROXY. " +
										 "Leaving handleSubsequentRequest " +
										 session.getLogId());

				return genRetProxy();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE and PROXY. " +
									"Leaving handleSubsequentRequest " +
									session.getLogId());
		  
		  int ret = 0;
		  ret |= PROXY;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest " +
									session.getLogId());

		  // If ACK for a failure response eat it up
		  if (request.isNon2XXAck()) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("ACK for non-2xx response. Return NOOP " +
										 session.getLogId());
					 m_logger.debug("Leaving AseSipProxyAckMessageHandler " +
										 "recvRequest" + session.getLogId());
				}
				
				return genRetNoop();
		  }

		  // Else ACK for a 2xx final response
		  if (m_logger.isDebugEnabled())
				m_logger.debug("ACK for 2xx final response." +
									session.getLogId());

		  // If Max-Forwards is 0, return NOOP
		  try {
		  	AseSipMaxForwardsHeaderHandler.validateMaxForwards(request);
		  } catch(javax.servlet.sip.TooManyHopsException exp) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("ACK with Max-Forwards exhausted. Return NOOP " +
										 session.getLogId());
					 m_logger.debug("Leaving AseSipProxyAckMessageHandler " +
										 "recvRequest" + session.getLogId());
				}
				
				return genRetNoop();
		  }

		  // If dialog is terminated or session is invalid proxy the request
		  boolean proxyReq = false;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog terminated. Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }

		  // Application invocation for ACK will be decided by supervised flag,
		  // not by record-route flag
		  if (false == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised = FALSE. Proxy the request. " +
										 session.getLogId());
				proxyReq = true;
		  }
		  
		  if (true == proxyReq) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return PROXY. Leaving recvRequest " +
										 session.getLogId());

				return genRetProxy();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving recvRequest " +
									session.getLogId());
		  
		  return genRetContinue();
	 }

	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendInitialRequest " +
									session.getLogId());

		  m_logger.error("ACK not an initial request");
		  throw new AseSipMessageHandlerException("ACK not an initial request");
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  // If Max-Forwards is 0, return NOOP
		  try {
		  	AseSipMaxForwardsHeaderHandler.validateMaxForwards(request);
		  } catch(javax.servlet.sip.TooManyHopsException exp) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("ACK with Max-Forwards exhausted. Return NOOP " +
										 session.getLogId());
					 m_logger.debug("Leaving AseSipProxyAckMessageHandler " +
										 "recvRequest" + session.getLogId());
				}
				
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendSubsequentRequest" +
									"Return CONTINUE" + session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering requestPreSend " +
									session.getLogId());

		  // Add VIA only for 2xx ACK
		  if (false == request.isNon2XXAck())
				addViaHeader(request, session, false);
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving requestPreSend. Return CONTINUE " +
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
				m_logger.debug("Inside handleResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend." +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipProxyAckMessageHandler.class);
}

