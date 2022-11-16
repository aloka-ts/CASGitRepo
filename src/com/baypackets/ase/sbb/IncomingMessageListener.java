/*
 * MessageModifier.java
 * 
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import javax.servlet.sip.SipServletMessage;


/**
 * The IncomingMessageListener interface provides a callback to an Application about 
 * various incoming Messages.
 * @see com.baypackets.ase.sbb.SBB
 * @author BayPackets
 */
public interface IncomingMessageListener extends Serializable {

	/**
	 * This method is invoked by the SBB to Notify the Application about the 
	 * incoming message to the SBB.
	 * 
	 * @param outgoing - The SIP message that the SBB sends to the network.
	 * The implementation of this method can add, remove, or modify non-system
	 * headers or the SDP of this object.
	 */
	public void handleIncomingMessage(SipServletMessage incoming);
	
}
