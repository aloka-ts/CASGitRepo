/**
 * AseSipMessageHandler.java
 */

package com.baypackets.ase.sipconnector;


/**
 * This interface defines the various SIP message handlers
 */

interface AseSipMessageHandler {
	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException;
	 
	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException;

	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException;

	 /**
	  * Helper methods to check the bit codes set within the return value
	  */
	 public boolean isRetNoop(int ret);
	 public boolean isRetContinue(int ret);
	 public boolean isRetProxy(int ret);
	 public boolean isRetStateUpdate(int ret);
	 
	 /**
	  * Return values
	  * This are OR'ed into the return value as multiple operations
	  * may be required e.g. CONTINUE and PROXY may both be set in case
	  * the application is a PROXY application

	 /**
	  * Return value when no operation is required
	  */
	 public static int NOOP = 1;

	 /**
	  * Return value when message processing is to continue
	  * For message from the network, send it to the SERVLET
	  * For message from the SERVLET send it to the network
	  */
	 public static int CONTINUE = 2;

	 /**
	  * Return value when the message is to be proxied
	  * For messages coming from the network, this is an indication to 
	  * the caller that irrespective of whether the message is sent to the
	  * SERVLET or not, the message is to be proxied
	  */
	 public static int PROXY = 4;

	 /**
	  * Return value which indicated if this message should update session
	  * state or not.
	  */
	 public static int STATE_UPDATE = 8;
}

