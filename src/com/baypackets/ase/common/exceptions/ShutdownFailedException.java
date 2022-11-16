/*
 * ShutdownFailedException.java
 *
 * Created on August 04, 2004, 5:12 PM
 */
package com.baypackets.ase.common.exceptions;


/**
 * This exception is thrown by the "stop" method of the Lifecycle interface
 * if an error occurs while shutting down the Lifecyle component.
 *
 * @see com.baypackets.ase.common.Lifecylce
 *
 * @author Zoltan Medveczky
 */
public final class ShutdownFailedException extends Exception {
    
    /**
     *
     *
     */
    public ShutdownFailedException() {
        super();
    }
    
    /**
     * 
     *
     */
    public ShutdownFailedException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public ShutdownFailedException(Exception e) {
        super(e.toString());
    }

}
