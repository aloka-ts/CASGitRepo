/**
 * FileName:	FinalUnitIndication.java
 *
 */

package com.baypackets.ase.ra.ro;

/**
 * This class defines the Final-Unit-Indication AVP that is part of an credit control request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Final-Unit-Indication AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface FinalUnitIndication
{
	/**
	 * This field indicates to the credit-control application the action to be taken 
	 * when the user's account cannot cover the service cost.
	 *
	 */
	public short getFinalUnitAction();

	/**
	 * provides filter rules corresponding to services that are to remain accessible even 
	 * if there are no more service units granted.
	 * 
	 * @return <code>IPFilterRule</code> object.
	 */

	public IPFilterRule getRestrictedFilterRule();

	/**
	 * This method returns the redirect server.
	 *	
	 * @return <code>RedirectServer</code> object.
	 */

	public RedirectServer getRedirectServer();
}
