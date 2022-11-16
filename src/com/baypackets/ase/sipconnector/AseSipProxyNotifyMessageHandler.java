/**
 * AseSipProxyNotifyMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipSubscriptionStateHeader;

/**
 * This class provides a UA implememtation for handling NOTIFY messages
 * and its responses
 */

class AseSipProxyNotifyMessageHandler extends AseSipProxyDefaultMessageHandler {

	public int recvRequest(AseSipServletRequest request,
							AseSipSession session)
		throws AseSipMessageHandlerException {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering recvRequest " + session.getLogId());

		// If dialog is terminated or session is invalid, proxy the request
		boolean proxyReq = false;
		  
		if(true == isDialogTerminated(session)) {
			m_logger.error("Sip Dialog termainated. " +
							"Proxy the request" +
							session.getLogId());
			proxyReq = true;
		}

		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated. " +
							"Proxy the request" +
							session.getLogId());
			proxyReq = true;
		}

		if(true == proxyReq) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return PROXY . Leaving recvRequest " +
								session.getLogId());

			return genRetProxy();
		}

		// Get the subscription from the request and see if we find a match
		AseSipSubscription sub = request.getSubscription(false);
		  
		if(true == session.doesSubscriptionExist(sub)) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Matching subscription found in " +
								"subscription list. " + session.getLogId());
				
			// See if the subscription is terminated
			if(false == this.isSubscriptionTerminated(request)) {
				if(m_logger.isDebugEnabled())
					m_logger.debug("Subscription active. " +
									"Return STATE_UPDATE and CONTINUE. " + 
									"Leaving recvRequest" +
									session.getLogId());
					 
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
			} else {
				if(m_logger.isDebugEnabled())
					m_logger.debug("Subscription terminated. " +
									"Cleaning up subscriptions " + 
									session.getLogId());
					 
				AseSipSubscription matchSub = session.getMatchingSubscription(sub);
				AseSipSubscription matchRefSub = null;
					 
				if(null != matchSub.getReferencedId())
					matchRefSub = AseSipSubscription.
						createReferencedSubscription(matchSub);

				// Remove the subscriptions from the subscription list
				// and the subscription manager
				session.removeSubscription(matchSub);
				session.removeSubscription(matchRefSub);
					 
				session.getConnector().removeSubscription(matchSub, session);
				session.getConnector().removeSubscription(matchRefSub, session);
					 
				if(m_logger.isDebugEnabled())
					m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving recvRequest" +
									session.getLogId());
					 
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
			}
		} // subscription exists in session subscription list

		// Subscription not found in subscription list
		// Since we have reached this point the subscription exists in the
		// subscription manager
		if(m_logger.isDebugEnabled())
			m_logger.debug("Subscription not found in subscriptions list" +
					"Try and find a match in the Subscription manager" +
					session.getLogId());

		AseSipSubscription matchSub = session.getConnector().
											getMatchingSubscription(sub);

		if(null == matchSub) {
			m_logger.error("No maching Subscription found. " +
							"Proxy the request" + session.getLogId());
				
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return PROXY. Leaving recvRequest. " +
									session.getLogId());
			return genRetProxy();
		}

		// Check if subscripton is still active
		if(false == this.isSubscriptionTerminated(request)) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Active subscription. " +
							"Adding subscription to the subscription " +
							"list. Return STATE_UPDATE and CONTINUE. " + 
							"Leaving recvRequest" +
							session.getLogId());

			session.addSubscription(sub);

			int ret = 0;
			ret |= CONTINUE;
			ret |= STATE_UPDATE;
			return ret;
		} else {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Terminated subscription. " +
						"Remove subscription from subscription " +
						"manager. Return STATE_UPDATE and CONTINUE. " + 
						"Leaving recvRequest" +
						session.getLogId());

			AseSipSubscription matchRefSub = null;
				
			if(null != matchSub.getReferencedId())
				matchRefSub = AseSipSubscription.
								createReferencedSubscription(matchSub);
			session.getConnector().removeSubscription(matchSub, session);

			if(null != matchRefSub)
				session.getConnector().removeSubscription(matchRefSub,
																session);
				
			int ret = 0;
			ret |= CONTINUE;
			ret |= STATE_UPDATE;
			return ret;
		}
	}

	public int sendInitialRequest(AseSipServletRequest request,
									AseSipSession session)
		throws AseSipMessageHandlerException {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering sendInitialRequest " + session.getLogId());

		m_logger.error("NOTIFY is not an initial request");

		throw new
			AseSipMessageHandlerException("NOTIFY not an initial request");
	}

	public int sendSubsequentRequest(AseSipServletRequest request,
										AseSipSession session)
		throws AseSipMessageHandlerException {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering sendSubsequentRequest " +
														session.getLogId());

		if(true == isDialogTerminated(session)) {
			m_logger.error("Sip Dialog termainated. " + session.getLogId());

			throw new AseSipMessageHandlerException("Dialog Terminated");
		}

		if(false == isSessionValid(session)) {
			m_logger.error("Session invalidated. " + session.getLogId());

			throw new AseSipMessageHandlerException("Session invalidated");
		}

		// Get the subscription from the request and see if we find a match
		AseSipSubscription sub = request.getSubscription(false);

		if(false == session.doesSubscriptionExist(sub)) {
			m_logger.error("No matching subscription found." +
												session.getLogId());
			throw new
				AseSipMessageHandlerException("No matching subscription");
		}
		  
		if(m_logger.isDebugEnabled())
			m_logger.debug("Matching subscription found in " +
							"subscription list. " + session.getLogId());
		  
		// See if the subscription is terminated
		if(false == this.isSubscriptionTerminated(request)) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Subscription active. " +
								"Return STATE_UPDATE and CONTINUE. " + 
								"Leaving recvRequest" +
								session.getLogId());

			int ret = 0;
			ret |= CONTINUE;
			ret |= STATE_UPDATE;
			return ret;
		} else {
			if(m_logger.isDebugEnabled())
				m_logger.debug("Subscription terminated. " +
								"Cleaning up subscriptions " + 
								session.getLogId());
				
			AseSipSubscription matchSub =
								session.getMatchingSubscription(sub);
			AseSipSubscription matchRefSub = null;
					 
			if(null != matchSub.getReferencedId())
				matchRefSub = AseSipSubscription.
								createReferencedSubscription(matchSub);
				
			// Remove the subscriptions from the subscription list
			session.removeSubscription(matchSub);
			session.removeSubscription(matchRefSub);
				
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return STATE_UPDATE and CONTINUE. " + 
									"Leaving recvRequest" +
									session.getLogId());
				
			int ret = 0;
			ret |= CONTINUE;
			ret |= STATE_UPDATE;
			return ret;
		}
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
			m_logger.error("Dialog terminated. Eating up the response" +
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
			m_logger.debug("100 NOTIFY response. Return NOOP. " +
												"Leaving recvResponse" +
												session.getLogId());

			return genRetNoop();
		}

		// If other 1XX response then CONTINUE
		if(1 == responseClass) {
			if(m_logger.isDebugEnabled())
				m_logger.debug("1XX NOTIFY response. Return CONTINUE. " +
								"Leaving recvResponse" +
								session.getLogId());

			return genRetContinue();
		}

		if(m_logger.isDebugEnabled())
			m_logger.debug("NOTIFY Final response. " +
								"Return STATE UPDATE and CONTINUE. " +
								"Leaving recvResponse" +
								session.getLogId());
		
		int ret = 0;
		ret |= STATE_UPDATE;
		ret |= CONTINUE;
		return ret;
	}

	/**
	* Method to check the state of the subscription
	*/
	private boolean isSubscriptionTerminated(AseSipServletRequest request) {

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering isSubscriptionTerminated");

		// Retrieve the DsSipSubscriptionStateHeader
		DsSipHeader ssHeader = null;
		try {
			ssHeader = request.getDsRequest().
				getHeaderValidate(DsSipConstants.SUBSCRIPTION_STATE);
		} catch (Exception e) {
			// Should not happen. Validation is already done
			m_logger.error("Error retrieving Subscription-State Header ", e);
			if(m_logger.isDebugEnabled())
				m_logger.debug("Return TRUE. " +
								"Leaving isSubscriptionTerminated");

			return true;
		}

		DsByteString sState =
					((DsSipSubscriptionStateHeader)ssHeader).getState();

		if(m_logger.isDebugEnabled())
			m_logger.debug("Subscription State = [ " + sState.toString() +
									"]");
		boolean retVal = false;
		if(DsByteString.equals(DsSipConstants.BS_TERMINATED, sState)) {
			retVal = true;
		} else {
			retVal = false;
		}
		  
		if(m_logger.isDebugEnabled())
			m_logger.debug("Leaving isSubscriptionTerminated. Return " +
																retVal);

		return retVal;
	}

	/**
	* The logger reference
	*/
	transient private static Logger m_logger =
				Logger.getLogger(AseSipProxyNotifyMessageHandler.class);
}

