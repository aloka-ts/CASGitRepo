package com.agnity.camelv2.exceptions;

public class InvalidInputException extends Exception{
	
	/**
	 * default serila version id
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String msg) {
		super(msg);
	}

}
