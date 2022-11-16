package com.agnity.win.exceptions;

/**
 * This exception will be thrown when input is invalid.
 * @author vgoel
 *
 */

public class InvalidInputException extends Exception{
	
	/**
	 * default serila version id
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String msg) {
		super(msg);
	}

}
