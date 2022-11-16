package com.agnity.utility.cdrsftp.dbpush.exceptions;

public class CounterFileIoFailedException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7103526709563639340L;

	/**
	 * 
	 */
	public CounterFileIoFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CounterFileIoFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CounterFileIoFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CounterFileIoFailedException(Throwable arg0) {
		super(arg0);
	}

}
