/*
 * FileCopyException.java
 *
 * Created on August 8, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 * This exception is thrown by the FileUtils class if an error occurs 
 * while copying a specified file or directory.
 *
 * @see com.baypackets.ase.util.FileUtils
 *
 * @author Zoltan Medveczky
 */
public final class FileCopyException extends Exception {
    
    /**
     *
     *
     */
    public FileCopyException() {
        super();
    }
    
    /**
     * 
     *
     */
    public FileCopyException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public FileCopyException(Exception e) {
        super(e.toString());
    }

}
