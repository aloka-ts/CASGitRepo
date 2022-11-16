package com.agnity.utility.cdrsftp.dbpush.exceptions;

public class InvalidLocalDirException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5948863752324545881L;

	/**
	 * 
	 */
	public InvalidLocalDirException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidLocalDirException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public InvalidLocalDirException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidLocalDirException(Throwable arg0) {
		super(arg0);
	}

}
