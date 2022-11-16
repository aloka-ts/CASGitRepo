/**
 * Filename:	ServerCapabilities.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/**
 * This interface defines the Server-Capabilities AVP that is part of 
 * IMS-Information AVP in a credir-control Message.
 *
 * @author Neeraj Jain
 */

public interface ServerCapabilities extends CustomAVPMap {

	/**
 	 * This method returns the mandatory capabilities of a server.
	 *
	 * @return <code>Iterator</code> on mandatory capabilities.
	 */

	public Iterator getMandatoryCapabilities();	

	/**
 	 * This method returns the optional capabilities of a server.
	 *
	 * @return <code>Iterator</code> on optional capabilities.
	 */

	public Iterator getOptionalCapabilities();

	/**
 	 * This method returns the Server names.
	 *
	 * @return <code>Iterator</code> on Server names.
	 */

	public Iterator getServerNames();

	/**
	 * This method associates a mandatory capability to a credit control 
	 * message.
	 *
	 * @param mandCap - mandatory capability to be associated.
	 */

	public void addMandatoryCapability(int mandCap);

	/**
	 * This method associates a optional capability to a credit control 
	 * message.
	 *
	 * @param optCap - optional capability to be associated.
	 */

	public void addOptionalCapability(int optCap);

	/**
	 * This method associates a Server name to a credit control 
	 * message.
	 *
	 * @param servName - Server name to be associated.
	 */

	public void addServerName(String servName);

	/**
	 * This method removes a mandatory capability from a credit control 
	 * message.
	 *
	 * @param mandCap - mandatory capabilities to be removed.
	 */

	public boolean removeMandatoryCapability(int mandCap);

	/**
	 * This method removes a optional capability from a credit control 
	 * message.
	 *
	 * @param optCap - optional capabilities to be removed.
	 */

	public boolean removeOptionalCapability(int optCap);

	/**
	 * This method removes a server name from a credit control 
	 * message.
	 *
	 * @param servName - server name to be removed.
	 */

	public boolean removeServerName(String servName);
}

