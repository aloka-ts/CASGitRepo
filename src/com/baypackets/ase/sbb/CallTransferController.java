package com.baypackets.ase.sbb;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipSession;

/**
* This is an interface which extends SBB interface
* @author BayPackets
**/
 

public interface CallTransferController extends SBB
{
	/**
	* This method is invoked to transfer already connected A-party to B-party.
	* @param sessionA - It corresponds to established dialog with A-party
	* @param addressB - Its the address of party B
	* @throws ConnectException
	* @throws IllegalStateException
	* @throws IllegalArgumentException
	*/

	public void transfer(SipSession sessionA, Address addressB) throws ConnectException, IllegalStateException, IllegalArgumentException;
}

