package com.baypackets.ase.resource;

/**
 * Defines an Exception that would be thrown by the container,
 * whenever there is a problem processing the messages from/to 
 * the resource adaptor.  
 */
public class ResourceException extends Exception {

	public ResourceException() {
		super();
	}

	public ResourceException(String message) {
		super(message);
	}

	public ResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceException(Throwable cause) {
		super(cause);
	}
}
