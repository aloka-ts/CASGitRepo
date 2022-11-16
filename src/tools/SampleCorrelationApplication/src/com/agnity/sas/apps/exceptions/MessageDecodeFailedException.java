package com.agnity.sas.apps.exceptions;

public class MessageDecodeFailedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3838859920504949436L;

	/**
	 * 
	 */
	public MessageDecodeFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageDecodeFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MessageDecodeFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MessageDecodeFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
