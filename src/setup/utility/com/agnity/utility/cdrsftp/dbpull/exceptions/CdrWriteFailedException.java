package com.agnity.utility.cdrsftp.dbpull.exceptions;

public class CdrWriteFailedException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7103526709563639340L;

	/**
	 * 
	 */
	public CdrWriteFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CdrWriteFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CdrWriteFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CdrWriteFailedException(Throwable arg0) {
		super(arg0);
	}

}
