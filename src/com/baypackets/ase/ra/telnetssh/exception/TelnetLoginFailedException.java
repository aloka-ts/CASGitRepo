/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.exception;

/**
 * The Class TelnetLoginFailedException.
 * Exception thrown on login failures establishing TelnetSession.
 * 
 * @author saneja
 */
public class TelnetLoginFailedException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 400000001L;
	
	/**
	 * Instantiates a new ls resource exception.
	 */
	public TelnetLoginFailedException() {
		super();
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param message the message
	 */
	public TelnetLoginFailedException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public TelnetLoginFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new ls resource exception.
	 *
	 * @param cause the cause
	 */
	public TelnetLoginFailedException(Throwable cause) {
		super(cause);
	}
}
