/**
 * AseSipProxyCancelMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class provides PROXY implementation for CANCEL messages
 */

class AseSipProxyCancelMessageHandler
	 extends AseSipProxyDefaultMessageHandler {
	 
	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("CANCEL not an initial request" + session.getLogId());
		  throw new
				AseSipMessageHandlerException("CANCEL not an initial request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside handleSubsequentRequest" +
									session.getLogId());

		  // Get original INVITE
		  AseSipTransaction txn = null;
		  AseSipServletRequest invite = null;

		  if((txn = (AseSipTransaction)request.getServerTxn()) != null) {
		  	invite = txn.getAseSipRequest();
		  } else if((txn = (AseSipTransaction)request.getPseudoServerTxn()) != null) {
		  	invite = txn.getAseSipRequest();
		  } else {
		  	throw new AseSipMessageHandlerException("No associated server transaction with CANCEL");
		  }

		  int ret = 0;
		  if(invite.isInitial()) {
			m_logger.debug("Sending received CANCEL to the PROXY object");
		  	ret = session.getProxy().handleSubsequentRequest(request, session);

		  	// CANCEL to initial INVITE, continue processing
			if(session.getProxy().getSupervised()) {
		  		ret |= CONTINUE;
			} else {
		  		ret |= NOOP;
			}
		  } else {
		  	// CANCEL to re-INVITE, we create and send the CANCEL request downstream
			AseConnectorSipFactory factory =
					(AseConnectorSipFactory)session.getConnector().getFactory();
			AseSipServletRequest newCancel = factory.createCancel(invite);

			try {
				session.sendRequest(newCancel);
			}
			catch (AseSipSessionException exp)	{
				//Don't log error here
			} catch(Exception e) {
				m_logger.error("sending CANCEL", e);
			}

			if(session.isRecordRouted()) {
		  		ret |= CONTINUE;
			} else {
		  		ret |= NOOP;
			}
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
				m_logger.debug("Entering recvRequest. " +
									session.getLogId());
		  
		  int ret = genRetContinue();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvRequest. " +
									session.getLogId());
		  return ret;
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendInitialRequest " +
									session.getLogId());

		  m_logger.error("CANCEL not an initial request" + session.getLogId());
		  throw new
				AseSipMessageHandlerException("CANCEL not an initial request");
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
		  return genRetContinue();
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {
		  return genRetContinue();
	 }
	 
	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException {
		  return genRetContinue();
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. " +
									"Return NOOP" + session.getLogId());

		  return genRetNoop();
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. " +
									"Return CONTINUE" + session.getLogId());
		  return genRetContinue();
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
		  Logger.getLogger(AseSipProxyCancelMessageHandler.class);
}
