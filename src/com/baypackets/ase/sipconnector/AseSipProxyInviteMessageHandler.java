/**
 * AseSipProxyInviteMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.*;
import com.baypackets.ase.container.AseApplicationSession;

/**
 * This class provides PROXY implementation for INVITE messages
 */

class AseSipProxyInviteMessageHandler
	 extends AseSipProxyDefaultMessageHandler {
	 
	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("Received initial INVITE Request. Cannot come to the " +
							  "Proxy Message Handler" + session.getLogId());
		  throw new AseSipMessageHandlerException("Illegal Request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest");
		  
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
		  if (false == session.isRecordRouted()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Record ROUTE = FALSE. Proxy the request. " +
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
				m_logger.debug("Entering recvRequest");
		  
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
		  if (false == session.isRecordRouted()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Record ROUTE = FALSE. Proxy the request. " +
										 session.getLogId());
				proxyReq = true;
		  }
		  
		  if (true == proxyReq) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Return PROXY. " +
										 "Leaving recvRequest " +
										 session.getLogId());

				return genRetProxy();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. " +
									"Leaving recvRequest " +
									session.getLogId());
		  
		  return genRetContinue();
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest" + session.getLogId());

		  // For initial requests just give it to the PROXY object
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Initial request. " +
									"Sending it to the PROXY object" +
									session.getLogId());
		  return session.getProxy().sendInitialRequest(request, session);
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendSubsequentRequest. Return CONTINUE. " +
									session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering requestPreSend " +
									session.getLogId());
		  
		  // Add VIA only for re-INVITEs
		  if(!request.isInitial()) {
		  		addViaHeader(request, session, false);
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPreSend. Return CONTINUE. " +
									session.getLogId());
		  return genRetContinue();
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPostSend. Return CONTINUE. " +
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
		  }
		  else
		  **** BPInd18609 fix ends */

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Return PROXY" +
									session.getLogId());
		  }
		  else if (false == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised is FALSE. Return PROXY" +
										 session.getLogId());
		  }
		  else {
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
				m_logger.debug("Entering recvResponse" + session.getLogId());

		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();
		  
		  // If 100 response return NOOP
		  if (100 == statusCode) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("100 response received. Return NOOP");
					 m_logger.debug("Leaving recvResponse" + session.getLogId());
				}

				return genRetNoop();
		  }
		  
		  // If the response is a 3XX-6XX response send an ACK
		  if (3 <= responseClass && responseClass <= 6) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Received a 3xx-6xx response. " +
										 "Generate and send an ACK" +
										 session.getLogId());
				
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
					 m_logger.error("Exception creating and sending ACK", e);
				}
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
				m_logger.debug("Response to subsequent request. " +
									session.getLogId());
		  
		  int ret = 0;
		  ret |= PROXY;
		  
		  if (true == isDialogTerminated(session)) {
				m_logger.error("Sip Dialog terminated. Return PROXY" +
									session.getLogId());
		  }
		  else if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. " +
									"Return PROXY and STATE_UPDATE" +
									session.getLogId());
				ret |= STATE_UPDATE;
		  }
		  else if (false == session.isSupervised()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised is FALSE. " +
										 "Return PROXY and STATE_UPDATE" +
										 session.getLogId());
				ret |= STATE_UPDATE;
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Supervised is TRUE. " +
										 "Return PROXY STATE_UPDATE and CONTINUE" +
										 session.getLogId());
				ret |= STATE_UPDATE;
				ret |= CONTINUE;
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse" + session.getLogId());

		  return ret;
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendResponse" + session.getLogId());

		  // If the source of the response in the servlet then give this to
		  // the proxy object
		  
		  if (AseSipConstants.SRC_SERVLET == response.getSource()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Response sent by the application. " +
										 "See what the PROXY object has to say. " +
										 session.getLogId());
				int ret = 0;
				ret = session.getProxy().sendResponse(response, session);

				if (true == isRetContinue(ret)) {
					 ret |= STATE_UPDATE;
					 if (m_logger.isDebugEnabled()) {
						  m_logger.debug("Return CONTINUE and STATE_UPDATE" +
											  session.getLogId());
						  m_logger.debug("Leaving sendResponse" +
											  session.getLogId());
					 }
					 return ret;
				}
				else {
					 if (m_logger.isDebugEnabled()) {
						  m_logger.debug("Return NOOP" +
											  session.getLogId());
						  m_logger.debug("Leaving sendResponse" +
											  session.getLogId());
					 }
					 return ret;
				}
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Received proxied response" + session.getLogId());
				
		  int responseClass = response.getDsResponse().getResponseClass();
		  
		  // If this is a 1XX or 2XX response then state update has happened
		  // when response was received from the network. So return CONTINUE
		  // If this is some other response then state update has to happen
		  
		  if (1 == responseClass || 2 == responseClass) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("1XX-2XX response" + session.getLogId());
					 m_logger.debug("Return CONTINUE" + session.getLogId());
					 m_logger.debug("Leaving sendResponse");
				}
				return genRetContinue();
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("3xx-6xx response" + session.getLogId());
				m_logger.debug("Return CONTINUE and STATE_UPDATE" +
									session.getLogId());
				m_logger.debug("Leaving sendResponse");
		  }
			
		  session.unsetInvitation();
		  int ret = 0;
		  ret |= STATE_UPDATE;
		  ret |= CONTINUE;
		  return ret;
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {
		  return genRetContinue();
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {
		  return genRetContinue();
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipProxyInviteMessageHandler.class);
}
