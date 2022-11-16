/*
 * TbctController.java
 * 
 * Created on Aug 17, 2005
 */
package com.baypackets.ase.sbb;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipSession;

/**
 * The TBCT controller interface <code>TbctController</code> defines an 
 * object that is used to connect an existing call leg to an address (both
 * terminating on same gateway) using TBCT.
 * 
 * @author BayPackets
 */
public interface TbctController extends SBB {	
	
	/**
	 * This method is invoked to connect an existing call leg to an address.
	 * This method requires that A-party of this call is already connected.
	 * <p>
	 * 
	 * <pre>
	 * This method sends out an INVITE to specified B-party address. When it
	 * receives the 2xx final response for the INVITE, it sends ACK for the
	 * same and B-party call leg is established.
	 *
	 * It then send a REFER request to gateway to interconnect both A-party
	 * and B-party call legs and get itself out of the call path. All incoming
	 * NOTIFY requests are responded with 200 OK.
	 *
	 * If everything goes well, it notifies the application that TBCT is
	 * successful. If TBCT did not go through, application is notified that
	 * TBCT is failed.
	 * </pre>
	 * 
	 * @param sessionA - The <code>SipSession</code> object corresponding to
	 * established call leg with A-party.
	 * @param addressB - The address of the B-party to dial out to.
	 *
	 * @throws IllegalArgumentException if sessionA or addressB is null.
	 * @throws IllegalStateException if sessionA is not in CONFIRMED state
	 * or if it was explicitly invalidated.
	 * @throws ConnectException if an error occurs while performing TBCT.
	 */
	public void connect(SipSession sessionA, Address addressB)
		throws ConnectException, IllegalStateException, IllegalArgumentException;

	public void transfer(SipSession sessionA, SipSession sessionB)
		throws ConnectException, IllegalStateException, IllegalArgumentException;
}
