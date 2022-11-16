package com.sas.util;

/**
 * Custom Exception Class for SAS JMX client
 */
public class SASUtilException extends Exception {

	/**
	 * constructor
	 * @param custom message
	 */
	public SASUtilException(String message)
	{
		// Log the custom error message 
		super(message);
	}

}
