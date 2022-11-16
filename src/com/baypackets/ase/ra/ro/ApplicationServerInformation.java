/**
 * Filename:	ApplicationServerInformation.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/**
 * This interface defines the Event-Type AVP that is part of an credit control 
 * request according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Event-Type AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface ApplicationServerInformation {
	
	/**
	 * This method returns Application-Server AVP associated with a credit control
	 * message.
	 * 
	 * @return String object containing the Application-Server AVP.
	 */
	public String getApplicationServer();

	/**
	 * This method returns Application-Provided-Called-Party-Address AVP associated 
	 * with a credit control message.
 	 *
	 * @return Iterator on Application-Provided-Called-Party-Address AVP.
	 */
	public Iterator getApplicationProvidedCalledPartyAddresses();

	/**
	 * This method associates the Application-Server AVP to a credit control
	 * message.
	 * 
	 * @param appServer - Application-Server AVP to be set.
	 */
	public void setApplicationServer(String appServer);

	/**
	 * This method associates the Application-Provided-Called-Party-Address AVP 
	 * to a credit control message.
	 *
	 * @param addr - Application-Provided-Called-Party-Address AVP to be set.
	 */
	public void addApplicationProvidedCalledPartyAddress(String addr);

	/**
	 * This method removes the Application-Provided-Called-Party-Address AVP 
	 * associated with a credit control message.
	 * 
	 * @param addr - the Application-Provided-Called-Party-Address AVP to be removed.
	 * @return boolean - true if success elese false.
	 */
	public boolean removeApplicationProvidedCalledPartyAddress(String addr);
}

