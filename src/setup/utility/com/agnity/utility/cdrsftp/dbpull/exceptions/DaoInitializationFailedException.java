package com.agnity.utility.cdrsftp.dbpull.exceptions;

public class DaoInitializationFailedException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7103526709563639340L;

	/**
	 * 
	 */
	public DaoInitializationFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DaoInitializationFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public DaoInitializationFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DaoInitializationFailedException(Throwable arg0) {
		super(arg0);
	}

}
