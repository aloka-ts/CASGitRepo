
/**
 * 
 */
package com.baypackets.ase.ra.enumserver.exception;

import com.baypackets.ase.resource.ResourceException;

/**
 * The Class EnumResourceException.
 * The own exception thrown by RA
 *
 * @author RaviNarayan
 */
public class EnumResourceException extends ResourceException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 200000001L;
	
	/**
	 * Instantiates a new Enum resource exception.
	 */
	public EnumResourceException() {
		super();
	}

	/**
	 * Instantiates a new http resource exception.
	 *
	 * @param message the message
	 */
	public EnumResourceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Enum resource exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public EnumResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param cause the cause
	 */
	public EnumResourceException(Throwable cause) {
		super(cause);
	}
}
