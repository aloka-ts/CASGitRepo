/*
 * Created on Oct 25, 2004
 *
 */
package com.baypackets.ase.channel;

import java.util.Collection;

/**
 * @author Ravi
 */

/**	This interface defines the methods that need to be implemented by any object that should be replicated by the SAS and should be
*	activated on the peer when a fail over happens
*/
public interface ReplicationContext {

	/**	This method returns the Id
	*/
	public String getId();
	
	/**	This method returns the ClusterId
	*/
	public String getClusterId();
	
	/**	This method is called whenever fail-over occurs so as to bring this object to the same state as it was in the active SAS.
	*/
	public void activate();
	
	/**	This method cleans up object state when the object was cleaned up on the active SAS instance
	*/
	public void cleanup();
	
	/**	This method returns whether it is active or not
	*/
	public boolean isActive();
        
	/**	This method returns the application information 
	*/
	public Collection getAppInfo();
        
	/**	It returns the Subsystem Id
	*/
	public String getSubsystemId();
	
}
