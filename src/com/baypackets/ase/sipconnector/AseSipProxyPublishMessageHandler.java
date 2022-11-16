/**
 * AseSipProxyPublishMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;

import com.baypackets.ase.container.AseProtocolSession;

/**
 * This class provides a default proxy implementation for the
 * AseSipMessageHandler
 */

class AseSipProxyPublishMessageHandler extends AseSipProxyDefaultMessageHandler {

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());
		  int ret = 0;
		  ret |= PROXY;

		  if (true == session.isRecordRouted()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Record Route = TRUE. Invoke the servlet " +
										 session.getLogId());
		  		ret |= CONTINUE;

		  		if (m_logger.isDebugEnabled())
					m_logger.debug("Leaving handleSubsequentRequest. " +
									"Return PROXY | CONTINUE. " + session.getLogId());
		  } else {
		  		if (m_logger.isDebugEnabled())
					m_logger.debug("Leaving handleSubsequentRequest. " +
									"Return PROXY. " + session.getLogId());
		  }
		  
		  return ret;
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest" + session.getLogId());
		  
		  // If dialog is terminated or session is invalid generate
		  // and send a 481 response
		  boolean proxyReq = false;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " +
									"Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									"Proxy the request" +
									session.getLogId());
				proxyReq = true;
		  }

		  if (false == session.isRecordRouted()) {
		  		if (m_logger.isDebugEnabled())
					m_logger.debug("Record ROUTE = FALSE. Proxy the request. " +
									session.getLogId());
				proxyReq = true;
		  }
				
		  if (true == proxyReq) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return PROXY . Leaving recvRequest " +
										 session.getLogId());

				return genRetProxy();
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
				m_logger.debug("Entering sendInitialRequest " + session.getLogId());

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving " +
									"sendInitialRequest" + session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest " + session.getLogId());

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Dialog Terminated");
		  }

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
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
		  		m_logger.debug("Entering requestPreSend " +
									session.getLogId());
		  
		  // Add VIA only for subsequent requests
		  if(!request.isInitial()) {
		  		addViaHeader(request, session, false);
		  }

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
				m_logger.debug("Entering handleResponse" + session.getLogId());

		  // If response is to an initial request send it to the PROXY
		  // object
		  AseSipServletRequest request =
				(AseSipServletRequest)response.getRequest();

		  if (request.isInitial()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Response to initial request. " +
										 "Sending it to the PROXY object" +
										 session.getLogId());
				int ret = session.getProxy().handleResponse(response, session);
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleResponse" + session.getLogId());
				
				return ret;
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Response to subsequent request." +
									session.getLogId());
			
		  int ret = 0;
		  ret |= PROXY;

		  /*** BPInd18609 fix starts
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog terminated. Return PROXY" +
									session.getLogId());
		  } else
		  **** BPInd18609 fix ends */

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Return PROXY" +
									session.getLogId());
		  } else if (false == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised is FALSE. Return PROXY" +
										 session.getLogId());
		  } else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised is TRUE. " +
										 "Return PROXY and CONTINUE" +
										 session.getLogId());
				ret |= CONTINUE;
		  }
				
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse" + session.getLogId());

		  return ret;
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvResponse. " +
									session.getLogId());

		  if(!response.getRequest().isInitial()) {
		  		// Check for responses to subsequent requests
		  		// if dialog is terminated or session is invalidated
		  		boolean eatItUp = false;
		  
		  		if (false == isSessionValid(session)) {
					m_logger.error("Session invalidated. Eating up the response" +
									session.getLogId());
					eatItUp = true;
		  		}

		  		if (true == isDialogTerminated(session)) {
					m_logger.error("Dialog terminated. Eating up the response" +
									session.getLogId());
					eatItUp = true;
		  		}

		  		if (true == eatItUp) {
					if (m_logger.isDebugEnabled())
					 	m_logger.debug("Leaving recvResponse. " +
										 "Return NOOP. " + session.getLogId());
				
					return genRetNoop();
		  		}
	 	  } else {
		  		// If response is to an initial request send it to the PROXY
		  		// object
		  		if (m_logger.isDebugEnabled())
					m_logger.debug("Response to initial request. " +
										"Sending it to the PROXY object" +
										session.getLogId());
				return session.getProxy().recvResponse(response, session);
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. " +
									"Return CONTINUE. " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse " + session.getLogId());

		  if(!response.getRequest().isInitial()) {
		  		// Check for responses to subsequent requests only,
		  		// if session is invalidated
		  		if (false == isSessionValid(session)) {
					m_logger.error("Session invalidated." + session.getLogId());
					throw new AseSipMessageHandlerException("Session invalidated");
		  		}
		  }

		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return CONTINUE." +
									"Leaving sendResponse " +
									session.getLogId());
		  }
		  
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
		  Logger.getLogger(AseSipProxyPublishMessageHandler.class);
}

