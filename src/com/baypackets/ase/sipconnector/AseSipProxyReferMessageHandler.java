/**
 * AseSipProxyReferMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

/**
 * This class provides a UA implememtation for handling REFER messages
 * and its responses
 */

class AseSipProxyReferMessageHandler extends AseSipProxyDefaultMessageHandler {

	public int recvRequest(AseSipServletRequest request,
							AseSipSession session)
		throws AseSipMessageHandlerException {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering recvRequest " + session.getLogId());

		// If dialog is terminated or session is invalid generate
		// and send a 481 response
		boolean proxyReq = false;
		  
		if(true == isDialogTerminated(session)) {
			m_logger.error("Sip Dialog termainated. " +
							"Proxy the request. " +
							session.getLogId());
			proxyReq = true;
		}

		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated. " +
							"Proxy the request. " +
							session.getLogId());
			proxyReq = true;
		}

		if(false == session.isRecordRouted()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Record ROUTE = FALSE. " +
								"Proxy the request. " +
								session.getLogId());
			proxyReq = true;
		}

		if(true == proxyReq) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return PROXY . Leaving recvRequest " +
								session.getLogId());

			return genRetProxy();
		}

		// Force the request to create the subscriptions
		if(true == session.isFirstRefer()) {
			request.getSubscription(true);
			request.getReferencedSubscription();
		} else {
			request.getSubscription(false);
		}
		  
		if(m_logger.isDebugEnabled())
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

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering sendInitialRequest " + session.getLogId());

		if(true == isDialogTerminated(session)) {
			m_logger.error("Sip Dialog termainated. " + session.getLogId());
			throw new AseSipMessageHandlerException("Dialog Terminated");
		}

		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated. " + session.getLogId());
			throw new AseSipMessageHandlerException("Session invalidated");
		}

		if(session.isRecordRouted()) {
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
		  
			session.getConnector().addSubscription(sub1, session);
			session.getConnector().addSubscription(sub2, session);
		}

		// Mark the fact that we have sent the 1st Refer in this dialog
		session.firstReferSent();
		  
		if(m_logger.isDebugEnabled())
			m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
							"Leaving sendInitialRequest" +
							session.getLogId());

		int ret = 0;
		ret |= CONTINUE;
		ret |= STATE_UPDATE;
		return ret;
	}

	public int recvResponse(AseSipServletResponse response,
							AseSipSession session)
		throws AseSipMessageHandlerException {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering recvResponse " + session.getLogId());

		int responseClass = response.getDsResponse().getResponseClass();

		// If dialog is terminated or session is invalidated
		boolean eatItUp = false;
		  
		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated. Eat it up" +
							session.getLogId());
			eatItUp = true;
		}

		if(true == isDialogTerminated(session)) {
			m_logger.error("Dialog terminated. Eat it up" +
							session.getLogId());
			eatItUp = true;
		}

		if(true == eatItUp) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. Return NOOP" +
								session.getLogId());

			return genRetNoop();
		}

		// If 100 response then NOOP
		if(100 == response.getDsResponse().getStatusCode()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("100 REFER response. Return NOOP. " +
								"Leaving recvResponse" +
								session.getLogId());

			return genRetNoop();
		}

		// If response is to an initial request send it to the PROXY
		// object
		AseSipServletRequest request =
								(AseSipServletRequest)response.getRequest();
		if(request.isInitial()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Response to initial request. " +
								"Sending it to the PROXY object" +
								session.getLogId());
				return session.getProxy().recvResponse(response, session);
		}

		// If other 1XX response then CONTINUE
		if(1 == responseClass) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("1XX REFER response. Return CONTINUE. " +
								"Leaving recvResponse" +
								session.getLogId());

			return genRetContinue();
		}

		// If a 2XX response is received then a subscription has been created
		if(2 == responseClass) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("2XX REFER response. " + session.getLogId());
				
			// we have already forced the request to create the subscriptions
			// Retrieve them here
			AseSipServletRequest req =
								(AseSipServletRequest)response.getRequest();
				
			AseSipSubscription sub1 = req.getSubscription(false);
			AseSipSubscription sub2 = null;
			if(null != sub1.getReferencedId())
				sub2 = req.getReferencedSubscription();
				
			// If the subscription does not exist in the subscription list
			// then add it
			if(false == session.doesSubscriptionExist(sub1)) {
				if(m_logger.isDebugEnabled())
					m_logger.debug("Subscription does not exist in list. " +
									"Adding it" + session.getLogId());
					 
			session.addSubscription(sub1);
			if(null != sub2)
				session.addSubscription(sub2);
			} else {
				if(m_logger.isDebugEnabled())
					m_logger.debug("Subscription exists in list. NOOP" +
									session.getLogId());
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE." +
								"Leaving recvResponse " +
								session.getLogId());
				
			int ret = 0;
			ret |= STATE_UPDATE;
			ret |= CONTINUE;
			return ret;
		}

		if(m_logger.isDebugEnabled())
			m_logger.debug("3XX - 6XX SUBSCRIBE response." +
							session.getLogId());
		  
		// we have already forced the request to create the subscriptions
		// Retrieve them here
		AseSipServletRequest req =
							(AseSipServletRequest)response.getRequest();
		  
		AseSipSubscription sub1 = req.getSubscription(false);
		AseSipSubscription sub2 = null;
		if(null != sub1.getReferencedId())
			sub2 = req.getReferencedSubscription();
		  
		// If this is  response to the initial request
		if(true == req.isInitial()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("3XX - 6XX SUBSCRIBE response to initial " +
								"request. Remove subscription from the " +
								"Subscription Manager. " +
								session.getLogId());

			session.getConnector().removeSubscription(sub1, session);
			if(null != sub2)
				session.getConnector().removeSubscription(sub2, session);
				
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE." +
								"Leaving recvResponse " +
								session.getLogId());

			int ret = 0;
			ret |= STATE_UPDATE;
			ret |= CONTINUE;
			return ret;
		} else {
			if(m_logger.isDebugEnabled())
				m_logger.debug("3XX - 6XX SUBSCRIBE response to subsequent " +
								"request. Nothing to do." + session.getLogId());
				
			if(m_logger.isDebugEnabled())
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

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering sendResponse " + session.getLogId());

		// If dialog is terminated or session is invalidated
		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated." + session.getLogId());
			throw new AseSipMessageHandlerException("Session invalidated");
		}

		if(true == isDialogTerminated(session)) {
			m_logger.error("Dialog terminated. " + session.getLogId());
			throw new AseSipMessageHandlerException("Dialog terminated");
		}

		return genRetContinue();
	}

	/**
	* The logger reference
	*/
	transient private static Logger m_logger =
					Logger.getLogger(AseSipProxyReferMessageHandler.class);
}

