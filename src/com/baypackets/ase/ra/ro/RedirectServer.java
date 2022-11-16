/**
 * Filename:	RedirectServer.java
 *
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Redirect-Server AVP that is part of an credit control
 * request according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Redirect-Server AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface RedirectServer
{
	/**
	 * This method returns the Redirect-Address-Type AVP associated with the credit
	 * control answer.
	 *
	 * @return short containing the SIP-Method AVP.
	 */

	public short getRedirectServerAddressType();

	/**
	 * This method returns the Redirect-Server-Address AVP associated with the credit
	 * control answer.
	 *
	 * @return String object containing the SIP-Method AVP.
	 */
	public String getRedirectServerAddress();
}
