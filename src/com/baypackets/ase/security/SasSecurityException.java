/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.security;

/**
 * @author Ravi
 */
public class SasSecurityException extends Exception {

	public SasSecurityException(){
		super();
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public SasSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SasSecurityException(Throwable cause) {
		super(cause);
	}

	public SasSecurityException(String msg){
		super(msg);
	}
}
