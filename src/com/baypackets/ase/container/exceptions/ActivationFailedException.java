/*
 * Created on May 6, 2004
 *
 */
package com.baypackets.ase.container.exceptions;

/**
 * @author Ravi
 */
public class ActivationFailedException extends Exception {

	/**
	 * 
	 */
	public ActivationFailedException() {
		super();
	}

	/**
	 * @param message
	 */
	public ActivationFailedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ActivationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ActivationFailedException(Throwable cause) {
		super(cause);
	}

}
