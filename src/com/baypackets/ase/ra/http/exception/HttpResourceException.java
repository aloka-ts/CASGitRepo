
/**
 * 
 */
package com.baypackets.ase.ra.http.exception;

import com.baypackets.ase.resource.ResourceException;

/**
 * The Class HttpResourceException.
 * The own exception thrown by RA
 *
 * @author RaviNarayan
 */
public class HttpResourceException extends ResourceException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 200000001L;
	
	/**
	 * Instantiates a new http resource exception.
	 */
	public HttpResourceException() {
		super();
	}

	/**
	 * Instantiates a new http resource exception.
	 *
	 * @param message the message
	 */
	public HttpResourceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new http resource exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public HttpResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param cause the cause
	 */
	public HttpResourceException(Throwable cause) {
		super(cause);
	}
}
