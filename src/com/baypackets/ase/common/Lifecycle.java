/*
 * Lifecylce.java
 *
 * Created on August 6, 2004, 7:01 PM
 */
package com.baypackets.ase.common;

import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;


/**
 * This interface defines an object that is capable of being started up and 
 * shutdown.  Implementations are responsible for notifying each of their 
 * registered LifecylceListener objects when a "start" or "stop" action has 
 * been performed on them.
 *
 * @see com.baypackets.ase.common.LifecycleListener
 *
 * @author  Zoltan Medveczky
 */
public interface Lifecycle {
    
	/**
	 * Called to initialize the objects. 
	 * @throws Exception
	 */
	public void initialize() throws Exception;
	
    /**
     * Objects should perform whatever actions are required to move them into 
     * a "running" state when this method is invoked.  They should also notify 
     * all their registered listeners that they have been started.
     *
     * @throws StartupFailedException if an error occurs while starting up 
     * this component
     */
    public void start() throws StartupFailedException;
    
    /**
     * Objects should perform whatever actions are required to move them into 
     * a "stopped" state when this method is invoked.  They should also notify
     * all their registered listeners that they have been stopped.
     *
     * @throws ShutdownFailedException if an error occurs while shutting down 
     * this component
     */
    public void stop() throws ShutdownFailedException;
    
    /**
     * Returns "true" if this component is currently in the "running" state or
     * returns false otherwise.
     *
     * @return  A flag indicating this component's current running state
     */
    public boolean isRunning();
}
