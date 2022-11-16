/*
 * AseAnnotationProcessingException.java
 *
 * Created on December 30, 2010
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown if an error occurs while reading annotation 
 * in the application.
 *
 * @see com.baypackets.ase.container.deployer.AseAnnotationProcessor
 *
 */
public final class AseAnnotationProcessingException extends Exception {
    
    public AseAnnotationProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseAnnotationProcessingException(Throwable cause) {
		super(cause);
	}

	/**
     *
     *
     */
    public AseAnnotationProcessingException() {
        super();
    }
    
    /**
     * 
     *
     */
    public AseAnnotationProcessingException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public AseAnnotationProcessingException(Exception e) {
        super(e.toString());
    }

}
