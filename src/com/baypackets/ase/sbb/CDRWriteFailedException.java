/*
 * Created on Jun 16, 2005
 *
 */
package com.baypackets.ase.sbb;

/**
 * The CDRWriteFailedException is thrown when the Write operation on the CDR fails.
 */
public class CDRWriteFailedException extends Exception {

	/**
	 */
	public CDRWriteFailedException() {
		super();
	}

	/**
	 * @param message
	 */
	public CDRWriteFailedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CDRWriteFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CDRWriteFailedException(Throwable cause) {
		super(cause);
	}

}
