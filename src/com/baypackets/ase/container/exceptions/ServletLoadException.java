/*
 * ServletLoadException.java
 *
 * Created on August 9, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown if an error occurs when loading a Serlvet
 * class.
 *
 * @see com.baypackets.ase.container.AseWrapper
 *
 * @author Zoltan Medveczky
 */
public final class ServletLoadException extends Exception {
    
    /**
     *
     *
     */
    public ServletLoadException() {
        super();
    }
    
    /**
     * 
     *
     */
    public ServletLoadException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public ServletLoadException(Exception e) {
        super(e.toString());
    }
    
}
