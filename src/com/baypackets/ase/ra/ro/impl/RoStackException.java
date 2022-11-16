package com.baypackets.ase.ra.ro.impl;

/**
 * Defines an Exception that would be thrown by the stack interfaces,
 * whenever there is a problem processing the messages from/to 
 * the stack.
 *
 * @author Neeraj Jain
 */
public class RoStackException extends Exception {

	public RoStackException() {
		super();
	}

	public RoStackException(String message) {
		super(message);
	}

	public RoStackException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoStackException(Throwable cause) {
		super(cause);
	}
}
