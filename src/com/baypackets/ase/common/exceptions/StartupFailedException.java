/*
 * StartupFailedException.java
 *
 * Created on August 04, 2004, 5:12 PM
 */
package com.baypackets.ase.common.exceptions;


/**
 * This exception is thrown by the "start" method of the Lifecycle interface
 * if an error occurs while starting up the Lifecyle component.
 *
 * @see com.baypackets.ase.common.Lifecylce
 *
 * @author Zoltan Medveczky
 */
public final class StartupFailedException extends Exception {
    
    /**
     *
     *
     */
    public StartupFailedException() {
        super();
    }
    
    /**
     * 
     *
     */
    public StartupFailedException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public StartupFailedException(Exception e) {
        super(e.toString());
    }

}
