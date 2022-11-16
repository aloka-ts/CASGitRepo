/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.exception;

import com.baypackets.ase.resource.ResourceException;

/**
 * The Class LsResourceException.
 * The own exception thrown by RA
 *
 * @author saneja
 */
public class LsResourceException extends ResourceException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 200000001L;
	
	/**
	 * Instantiates a new ls resource exception.
	 */
	public LsResourceException() {
		super();
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param message the message
	 */
	public LsResourceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public LsResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param cause the cause
	 */
	public LsResourceException(Throwable cause) {
		super(cause);
	}
}
