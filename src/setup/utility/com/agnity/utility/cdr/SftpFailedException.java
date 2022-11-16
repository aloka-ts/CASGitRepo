package com.agnity.utility.cdr;

public class SftpFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5948863752324545881L;

	/**
	 * 
	 */
	public SftpFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SftpFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SftpFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SftpFailedException(Throwable arg0) {
		super(arg0);
	}

}
