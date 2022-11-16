package com.agnity.utility.cdrsftp.fileSftp.exceptions;

public class InvalidCdrLocationException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7103526709563639340L;

	/**
	 * 
	 */
	public InvalidCdrLocationException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidCdrLocationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public InvalidCdrLocationException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidCdrLocationException(Throwable arg0) {
		super(arg0);
	}

}
