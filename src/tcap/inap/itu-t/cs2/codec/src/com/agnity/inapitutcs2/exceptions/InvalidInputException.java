package com.agnity.inapitutcs2.exceptions;

/**
 * This exception will be thrown when input is invalid.
 * @author Mriganka
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
