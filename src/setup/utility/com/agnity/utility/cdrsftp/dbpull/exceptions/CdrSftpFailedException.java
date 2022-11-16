package com.agnity.utility.cdrsftp.dbpull.exceptions;

public class CdrSftpFailedException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7103526709563639340L;

	/**
	 * 
	 */
	public CdrSftpFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CdrSftpFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CdrSftpFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CdrSftpFailedException(Throwable arg0) {
		super(arg0);
	}

}
