/*
 * Created on Nov 27, 2004
 *
 */
package com.baypackets.ase.cdr;

/**
 * This exception would be thrown when initialization fails.
 */
public class InitializationFailedException extends Exception {

	/**
	 * 
	 */
	public InitializationFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InitializationFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializationFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InitializationFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
