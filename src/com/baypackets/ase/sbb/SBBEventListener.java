/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;

/**
 * The SBBEventListener interface defines the callback mechanism for the SBBs 
 * to communicate with the application to notify the status or outcome of the action
 * requested by the application.  
 */
public interface SBBEventListener extends Serializable {
	
	/**
	 * A constant that requests that the SBB continue 
	 * the default behavior.
	 */
	public static final int CONTINUE = 1;
	
	/**
	 * A constant that requests that the SBB skip
	 * the default processing. 
	 */
	public static final int NOOP =2;
	
	/**
	 * The SBB invokes this method to indicate the occurance of a specific event
	 * like CONNECTED, CONNECT_FAILED or SIGNALING_IN_PROGRESS, etc.
	 *
	 *<p>
	 * The application returns value to tell the SBB whether or not 
	 * continuing with the default processing for this event.
	 * 
	 * @param sbb The SBB object that generated this event.
	 * @param event The event object
	 * @return CONTINUE if the SBB should continue the default processing, else NOOP.
	 */
	public int handleEvent(SBB sbb, SBBEvent event);

	/**
	 * This method is invoked to notify this object that a failover has occurred
	 * and it is now running in a process space outside the one it was created in.
	 * The application should implement this method to re-obtain any transient
	 * resources like sockets or database connections.
	 * @param sbb The SBB object to which this Event Listener is associated
	 */
	public void activate(SBB sbb);

}
