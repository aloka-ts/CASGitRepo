/**
 * AseSip100RelHandlerInterface.java
 */

package com.baypackets.ase.sipconnector;

/**
 * This keeps track of Relibale responses and PRACK's
 */

interface AseSip100RelHandlerInterface {
	 int sendReliableResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException;
	 int recvReliableResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException;
	 int sendPrack(AseSipServletRequest request)
		  throws AseSipMessageHandlerException;
	 int recvPrack(AseSipServletRequest request)
		  throws AseSipMessageHandlerException;
	 void sendFinalResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException;
	 void recvFinalResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException;

	 public static int CONTINUE = 1;
	 public static int NOOP = 2;
}
