/*
 * MessageModifier.java
 * 
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import javax.servlet.sip.SipServletMessage;


/**
 * The MessageFilter interface provides a callback to an SBB object so that
 * it can perform various manipulations to SIP messages before the SBB sends 
 * them to the network.  Modifications performed by this object must conform to 
 * JSR 116; this includes SDP and/or non-system header changes.
 * 
 * @see com.baypackets.ase.sbb.SBB
 * @author BayPackets
 */
public interface MessageFilter extends Serializable {

	/**
	 * This method is invoked by the SBB to perform any manipulations to the
	 * specified outgoing SIP message before it is sent to the network.
	 * 
	 * @param outgoing - The SIP message that the SBB sends to the network.
	 * The implementation of this method can add, remove, or modify non-system
	 * headers or the SDP of this object.
	 */
	public void doFilter(SipServletMessage outgoing);
	
}
