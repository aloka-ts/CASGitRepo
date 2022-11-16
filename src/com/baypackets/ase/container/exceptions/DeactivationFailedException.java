/*
 * Created on May 6, 2004
 *
 */
package com.baypackets.ase.container.exceptions;

/**
 * @author Ravi
 */
public class DeactivationFailedException extends Exception {

	/**
	 * 
	 */
	public DeactivationFailedException() {
		super();
	}

	/**
	 * @param message
	 */
	public DeactivationFailedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DeactivationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DeactivationFailedException(Throwable cause) {
		super(cause);
	}
}
