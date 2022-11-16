/*
 * FileMoveException.java
 *
 * Created on August 8, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 * This exception is thrown by the FileUtils class if an error occurs 
 * while moving a specified file or directory.
 *
 * @see com.baypackets.ase.util.FileUtils
 *
 * @author Zoltan Medveczky
 */
public final class FileMoveException extends Exception {
    
    /**
     *
     *
     */
    public FileMoveException() {
        super();
    }
    
    /**
     * 
     *
     */
    public FileMoveException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public FileMoveException(Exception e) {
        super(e.toString());
    }

}
