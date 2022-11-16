/**
 * AseSipMessageHandlerFactory.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * This class provides a factory for SIP message handlers
 */

class AseSipMessageHandlerFactory {

	 /**
	  * Return the AseSipMessageHandler corresponding to the methodId and 
	  * the session role.
	  * The methodId is a constant defined by the DS stack
	  * The role is the session role with constants defined AseSipConstants
	  */
	 static AseSipMessageHandler getHandler(int methodId,
														 AseSipSession session) {

		  switch (methodId) {

		  case DsSipConstants.INVITE:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyInviteMsgHandler;
				else
					 return m_inviteMsgHandler;

		  case DsSipConstants.ACK:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyAckMsgHandler;
				else
					 return m_ackMsgHandler;

		  case DsSipConstants.BYE:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyByeMsgHandler;
				else
					 return m_byeMsgHandler;

		  case DsSipConstants.CANCEL:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyCancelMsgHandler;
				else
					 return m_cancelMsgHandler;

		  case DsSipConstants.PRACK:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyDefaultMsgHandler;
				else
					return m_prackMsgHandler;

		  case DsSipConstants.SUBSCRIBE:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					return m_proxySubscribeMsgHandler;
				else
					return m_subscribeMsgHandler;

		  case DsSipConstants.REFER:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					return m_proxyReferMsgHandler;
				else
					return m_referMsgHandler;

		  case DsSipConstants.NOTIFY:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					return m_proxyNotifyMsgHandler;
				else
					return m_notifyMsgHandler;

		  case DsSipConstants.MESSAGE:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					return m_proxyMessageMsgHandler;
				else
					return m_messageMsgHandler;

		  case DsSipConstants.PUBLISH:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					return m_proxyPublishMsgHandler;
				else
					return m_PublishMsgHandler;

		  default:
				if (AseSipSession.ROLE_PROXY == session.getRole())
					 return m_proxyDefaultMsgHandler;
				else
					 return m_defaultMsgHandler;
		  }
	 }

	 private static AseSipMessageHandler m_inviteMsgHandler =
		  new AseSipInviteMessageHandler();
	 private static AseSipMessageHandler m_ackMsgHandler =
		  new AseSipAckMessageHandler();
	 private static AseSipMessageHandler m_byeMsgHandler =
		  new AseSipByeMessageHandler();
	 private static AseSipMessageHandler m_cancelMsgHandler =
		  new AseSipCancelMessageHandler();
	 private static AseSipMessageHandler m_prackMsgHandler =
		  new AseSipPrackMessageHandler();
	 private static AseSipMessageHandler m_subscribeMsgHandler =
		  new AseSipSubscribeMessageHandler();
	 private static AseSipMessageHandler m_referMsgHandler =
		  new AseSipReferMessageHandler();
	 private static AseSipMessageHandler m_notifyMsgHandler =
		  new AseSipNotifyMessageHandler();
	 private static AseSipMessageHandler m_messageMsgHandler =
		  new AseSipMessageMessageHandler();
	 private static AseSipMessageHandler m_PublishMsgHandler =
		  new AseSipPublishMessageHandler();
	 private static AseSipMessageHandler m_defaultMsgHandler =
		  new AseSipDefaultMessageHandler();

	 private static AseSipMessageHandler m_proxyInviteMsgHandler =
		  new AseSipProxyInviteMessageHandler();
	 private static AseSipMessageHandler m_proxyAckMsgHandler =
		  new AseSipProxyAckMessageHandler();
	 private static AseSipMessageHandler m_proxyByeMsgHandler =
		  new AseSipProxyByeMessageHandler();
	 private static AseSipMessageHandler m_proxyCancelMsgHandler =
		  new AseSipProxyCancelMessageHandler();
	 private static AseSipMessageHandler m_proxySubscribeMsgHandler =
		  new AseSipProxySubscribeMessageHandler();
	 private static AseSipMessageHandler m_proxyReferMsgHandler =
		  new AseSipProxyReferMessageHandler();
	 private static AseSipMessageHandler m_proxyNotifyMsgHandler =
		  new AseSipProxyNotifyMessageHandler();
	 private static AseSipMessageHandler m_proxyMessageMsgHandler =
		  new AseSipProxyMessageMessageHandler();
	 private static AseSipMessageHandler m_proxyPublishMsgHandler =
		  new AseSipProxyPublishMessageHandler();
	 private static AseSipMessageHandler m_proxyDefaultMsgHandler =
		  new AseSipProxyDefaultMessageHandler();
}

