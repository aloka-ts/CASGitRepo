/**
 * EnumListener.java
 *
 *Created on March 15,2007
 */
package com.baypackets.ase.enumclient;

import java.util.List;

/**
 * This EnumListener interface is implemented by all applications who want to 
 * resolve telephone number asynchronously.
 * This interface provide callback methods for asynchronous resolution
 * @author Ashish kabra
 */
public interface EnumListener
{
	/** This is callback method which defines the action to be taken.
	 * @param list of URIs returned by DNS server
	 */
	 void receiveUriList(List list) ; 
	
	/** This method handles DNS errors in callback. 
	 * @param error returned by query to DNS server 
	 */
	public void handleError(int error);


	 /** This method handles all exceptions other than DNS errors in callback. 
     * @param e  response returned by query to DNS server 
     */
	public void handleException(Exception e);
}	
