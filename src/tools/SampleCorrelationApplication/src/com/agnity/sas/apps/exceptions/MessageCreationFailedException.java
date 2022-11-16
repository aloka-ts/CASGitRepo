package com.agnity.sas.apps.exceptions;

public class MessageCreationFailedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3838859920504949436L;

	/**
	 * 
	 */
	public MessageCreationFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageCreationFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MessageCreationFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MessageCreationFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
