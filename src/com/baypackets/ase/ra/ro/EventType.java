/**
 * Filename:	EventType.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Event-Type AVP that is part of an credit control request 
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Event-Type AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface EventType {

	/**
	 * This method returns the SIP-Method AVP associated with the credit 
	 * control request.
	 *
	 * @return String object containing the SIP-Method AVP.
	 */

	public String getSIPMethod();

	/**
	 * This method returns the Event AVP associated with the credit control
	 * request.
	 *
	 * @return String object containing the Event AVP.
	 */

	public String getEvent();

	/**
	 * This method returns the Expires AVP associated with the credit control
	 * request.
	 *
	 * @return int object containing the Expires AVP.
	 */
	public int getExpires();

	/**
	 * This method associates the SIP-Method AVP to a credit control request.
	 *
	 * @param method - String object containing the SIP-Method AVP.
	 */
	public void setSIPMethod(String method);

	/**
	 * This method associates the Event AVP associated to a credit control
	 * request.
	 *
	 * @param event - String object containing the Event AVP.
	 */
	public void setEvent(String event);

	/**
	 * This method associates the Expires AVP associated to a credit control
	 * request.
	 *
	 * @param expires - int object containing the Expires AVP.
	 */
	public void setExpires(int expires);
}

