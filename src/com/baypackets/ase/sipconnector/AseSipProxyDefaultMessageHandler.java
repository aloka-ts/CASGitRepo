/**
 * AseSipProxyDefaultMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;

/**
 * This class provides a default proxy implementation for the
 * AseSipMessageHandler
 */

class AseSipProxyDefaultMessageHandler implements AseSipMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("Received initial Request. Cannot come to the " +
							  "Proxy Message Handler" + session.getLogId());
		  throw new AseSipMessageHandlerException("Illegal Request");
	 }
	 
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
				m_logger.debug("Entering sendInitialRequest " + session.getLogId());

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog termainated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Dialog Terminated");
		  }

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
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

		  int ret = 0;
		  ret |= CONTINUE;
		  return ret;
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

		  // If dialog is terminated or session is invalidated
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

		  // If response is to an initial request send it to the PROXY
		  // object
		  AseSipServletRequest request =
		  				(AseSipServletRequest)response.getRequest();
		  if (request.isInitial()) {
		  		if (m_logger.isDebugEnabled())
					m_logger.debug("Response to initial request. " +
										"Sending it to the PROXY object" +
										session.getLogId());
				return session.getProxy().recvResponse(response, session);
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. " +
									"Return STATE UPDATE and CONTINUE. " +
									session.getLogId());

		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse " + session.getLogId());

		  // If session is invalidated
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated." + session.getLogId());
				throw new AseSipMessageHandlerException("Session invalidated");
		  }

		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return STATE_UPDATE and CONTINUE." +
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

	 boolean isDialogTerminated(AseSipSession session) {
		  if (AseSipSessionState.STATE_TERMINATED == session.getSessionState())
				return true;
		  return false;
	 }
	 
	 boolean isSessionValid(AseSipSession session) {
		  if (SipSession.State.TERMINATED  == session.getState())
				return false;
		  return true;
	 }

	 void addViaHeader(AseSipServletRequest request, AseSipSession session,
	 						boolean isLocalBranchId) {
			m_logger.debug("Entering addViaHeader");
			//JSR 289.34
			SipURI uri = ((AseSipSession)request.getSession()).getOutboundInterface();
			if(uri != null){
				AseSipViaHeaderHandler.addViaHeader(request,
						new DsByteString(uri.getHost()),
						uri.getPort(),
					 	DsSipTransportType.UDP,
					 	isLocalBranchId);
				
			}else{
		  	AseSipViaHeaderHandler.addViaHeader(request,
							new DsByteString(session.getConnector().getIPAddress()),
						 	session.getConnector().getPort(),
						 	DsSipTransportType.UDP,
						 	isLocalBranchId);
			}
			m_logger.debug("Leaving addViaHeader");
	 }
	 
	 protected int genRetContinue() {
		  int ret = 0;
		  ret |= CONTINUE;
		  return ret;
	 }

	 protected int genRetNoop() {
		  int ret = 0;
		  ret |= NOOP;
		  return ret;
	 }

	 protected int genRetProxy() {
		  int ret = 0;
		  ret |= PROXY;
		  return ret;
	 }
	 
	 public boolean isRetContinue(int ret) {
		  if ((ret & AseSipMessageHandler.CONTINUE) ==
				AseSipMessageHandler.CONTINUE)
				return true;
		  return false;
	 }
	 
	 public boolean isRetNoop(int ret) {
		  if ((ret & AseSipMessageHandler.NOOP) ==
				AseSipMessageHandler.NOOP)
				return true;
		  return false;
	 }

	 public boolean isRetProxy(int ret) {
		  if ((ret & AseSipMessageHandler.PROXY) ==
				AseSipMessageHandler.PROXY)
				return true;
		  return false;
	 }

	 public boolean isRetStateUpdate(int ret) {
		  if ((ret & AseSipMessageHandler.STATE_UPDATE) ==
				AseSipMessageHandler.STATE_UPDATE)
				return true;
		  return false;
	 }
		  
	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipProxyDefaultMessageHandler.class);
}

