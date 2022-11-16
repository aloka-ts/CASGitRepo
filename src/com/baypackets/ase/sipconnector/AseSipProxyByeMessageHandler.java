/**
 * AseSipProxyByeMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class provides PROXY implementation for BYE messages
 */

class AseSipProxyByeMessageHandler extends AseSipProxyDefaultMessageHandler {
	 
	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("BYE is not an initial request." + session.getLogId());
		  throw new AseSipMessageHandlerException("BYE not an initial request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest" +
									session.getLogId());

		  int ret = 0;
		  ret |= PROXY;

		  if (false == isSessionValid(session)) {
				m_logger.debug("Session invalidated. Return PROXY.");

				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleSubsequentRequest" +
										 session.getLogId());

				return ret;
		  }
		  
		  if (true == session.isRecordRouted()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Record Route = TRUE " +
										 "Return CONTINUE and PROXY" +
										 session.getLogId());
				ret |= CONTINUE;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Record Route = FALSE " +
										 "Return PROXY" +
										 session.getLogId());
		  }
				
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest" +
									session.getLogId());
		  return ret;
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest" + session.getLogId());
		  
		  // Mark the fact that a BYE has been received
		  session.resetInvitation();

		  // If dialog is termintaed or session is invalidated proxy the
		  // request
		  int ret = 0;
		  
		  if (true == isDialogTerminated(session)) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Dialog terminated. Return PROXY" +
										 session.getLogId());
					 m_logger.debug("Leaving recvRequest" + session.getLogId());
				}
				
				ret |= PROXY;
				return ret;
		  }
		  
		  if (false == isSessionValid(session)) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Session invalidated. Return PROXY and " +
										 "STATE_UPDATE" + session.getLogId());
					 m_logger.debug("Leaving recvRequest" + session.getLogId());
				}
			  
				ret |= STATE_UPDATE;
				ret |= PROXY;
				return ret;
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return STATE_UPDATE and CONTINUE" +
									session.getLogId());
				m_logger.debug("Leaving recvRequest" + session.getLogId());
		  }
		  
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendInitialRequest " +
									session.getLogId());

		  m_logger.error("BYE not an initial request");
		  throw new AseSipMessageHandlerException("BYE not an initial request");
	 }

	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendSubsequentRequest. " +
									"Return CONTINUE" + session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering requestPreSend " +
									session.getLogId());

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
				m_logger.debug("Inside requestPostSend. " +
									"Return CONTINUE" + session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleResponse" + session.getLogId());

		  int ret = 0;
		  ret |= PROXY;

		  if (false == isSessionValid(session)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Session invalidated. Return PROXY" +
										 session.getLogId());
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleResponse" + session.getLogId());
				
				return ret;
		  }
		  
		  if (true == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("SUPERVISED = TRUE. " +
										 "Return CONTINUE and PROXY" +
										 session.getLogId());
				ret |= CONTINUE;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("SUPERVISED = FALSE. " +
										 "Return PROXY" +
										 session.getLogId());
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse" + session.getLogId());
				
		  return ret;
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvResponse" + session.getLogId());

		  int ret = 0;

		  if (false == isSessionValid(session)) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Session invalidated. Return PROXY" +
										 session.getLogId());
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvResponse" + session.getLogId());
				
				ret |= PROXY;
				return ret;
		  }
		  
		  if (true == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("SUPERVISED = TRUE. Return CONTINUE" +
										 session.getLogId());
				ret |= CONTINUE;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("SUPERVISED = FALSE. Return PROXY" +
										 session.getLogId());
				ret |= PROXY;
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse" + session.getLogId());
				
		  return ret;
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendResponse. " +
									"Return CONTINUE" + session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend. " +
									"Return CONTINUE" + session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. " +
									"Return CONTINUE" + session.getLogId());
		  return genRetContinue();
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipProxyByeMessageHandler.class);
}
