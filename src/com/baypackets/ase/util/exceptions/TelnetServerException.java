/*
 * TelnetServerException.java
 *
 * Created on July 13, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 *
 * @see com.baypackets.ase.util.TelnetServer
 *
 * @author Zoltan Medveczky
 */
public final class TelnetServerException extends RuntimeException {
    
    /**
     *
     *
     */
    public TelnetServerException() {
        super();
    }
    
    /**
     * 
     *
     */
    public TelnetServerException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public TelnetServerException(Exception e) {
        super(e.toString());
    }

}
