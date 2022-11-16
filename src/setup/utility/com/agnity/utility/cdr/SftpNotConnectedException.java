package com.agnity.utility.cdr;

public class SftpNotConnectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5948863752324545881L;

	/**
	 * 
	 */
	public SftpNotConnectedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SftpNotConnectedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SftpNotConnectedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SftpNotConnectedException(Throwable arg0) {
		super(arg0);
	}

}
