/*
 * Created on Aug 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container.exceptions;

/**
 */
public class AseInvocationFailedException extends Exception {

	private int status = -1;
	
	/**
	 * 
	 */
	public AseInvocationFailedException() {
		super();
	}

	/**
	 * @param message
	 */
	public AseInvocationFailedException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public AseInvocationFailedException(String message, int status) {
		super(message);
		this.status = status;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AseInvocationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public AseInvocationFailedException(Throwable cause) {
		super(cause);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int i) {
		status = i;
	}
}
