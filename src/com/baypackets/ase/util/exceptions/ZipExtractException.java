/*
 * ZipExtractException.java
 *
 * Created on August 8, 2004, 5:12 PM
 */
package com.baypackets.ase.util.exceptions;


/**
 * This exception is thrown by the FileUtils class if an error occurs 
 * while extracting the contents of a zip file.
 *
 * @see com.baypackets.ase.util.FileUtils
 *
 * @author Zoltan Medveczky
 */
public final class ZipExtractException extends Exception {
    
    /**
     *
     *
     */
    public ZipExtractException() {
        super();
    }
    
    /**
     * 
     *
     */
    public ZipExtractException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public ZipExtractException(Exception e) {
        super(e.toString());
    }

}
