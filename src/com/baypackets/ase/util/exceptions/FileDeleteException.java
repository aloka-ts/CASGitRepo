/*
 * FileDeleteException.java
 *
 * Created on August 8, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 * This exception is thrown by the FileUtils class if an error occurs 
 * while deleting a specified file or directory.
 *
 * @see com.baypackets.ase.util.FileUtils
 *
 * @author Zoltan Medveczky
 */
public final class FileDeleteException extends Exception {
    
    /**
     *
     *
     */
    public FileDeleteException() {
        super();
    }
    
    /**
     * 
     *
     */
    public FileDeleteException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public FileDeleteException(Exception e) {
        super(e.toString());
    }

}
