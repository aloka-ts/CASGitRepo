/*
 * StringManagerException.java
 *
 * Created on July 13, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 * This exception is thrown by the StringManager class if an error occurs 
 * while instantiating a StringManager for a given package.
 *
 * @see com.baypackets.ase.util.StringManager
 *
 * @author Zoltan Medveczky
 */
public final class StringManagerException extends RuntimeException {
    
    /**
     *
     *
     */
    public StringManagerException () {
        super();
    }
    
    /**
     * 
     *
     */
    public StringManagerException (String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public StringManagerException (Exception e) {
        super(e.toString());
    }

}
