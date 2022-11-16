/**
 * AseSipProxySubscribeMessageHandler.java
 * Created on : June 29, 2005
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

/**
 * This class provides a proxy implememtation for handling SUBSCRIBE messages
 * and its responses
 */

class AseSipProxySubscribeMessageHandler extends AseSipProxyDefaultMessageHandler {

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
			// where a NOTIFY is received before the SUBSCRIBE response.
			// Thus we add the subscription to the subscription manager
			AseSipSubscription sub = request.getSubscription(false);
			session.getConnector().addSubscription(sub, session);
		}
		  
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

		if (true == isDialogTerminated(session)) {
			m_logger.error("Dialog terminated. Eating up the response" +
													session.getLogId());
			eatItUp = true;
		}

		if(eatItUp) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvResponse. Return NOOP" +
								session.getLogId());

			return genRetNoop();
		}

		// If 100 response then NOOP
		if(100 == response.getDsResponse().getStatusCode()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("100 SUBSCRIBE response. Return NOOP. " +
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
				m_logger.debug("1XX SUBSCRIBE response. Return CONTINUE. " +
								"Leaving recvResponse" +
								session.getLogId());

			return genRetContinue();
		}
			
		// If a 2XX response is received then a subscription has been created
		if(2 == responseClass) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("2XX SUBSCRIBE response. " +
								session.getLogId());

			AseSipServletRequest req =
							(AseSipServletRequest)(response.getRequest());
			AseSipSubscription sub = req.getSubscription(false);

			// If the subscription does not exist in the subscription list
			// then add it
			if(false == session.doesSubscriptionExist(sub)) {
				if(m_logger.isDebugEnabled())
					m_logger.debug("Subscription does not exist in list. " +
									"Adding it" + session.getLogId());

				session.addSubscription(sub);
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

		// 3XX - 6XX response
		if(m_logger.isDebugEnabled())
			m_logger.debug("3XX - 6XX SUBSCRIBE response." + session.getLogId());
		  
		AseSipServletRequest req =
						(AseSipServletRequest)(response.getRequest());
		AseSipSubscription sub = req.getSubscription(false);

		// If this is  response to the initial request
		if(true == req.isInitial()) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("3XX - 6XX SUBSCRIBE response to initial " +
								"request. Remove subscription from the " +
								"Subscription Manager. " +
								session.getLogId());
			session.getConnector().removeSubscription(sub, session);
				
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
	 
	/**
	* The logger reference
	*/
	private static Logger m_logger =
			Logger.getLogger(AseSipProxySubscribeMessageHandler.class);
}

