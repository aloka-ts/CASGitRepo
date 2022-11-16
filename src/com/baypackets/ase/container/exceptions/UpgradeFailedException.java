package com.baypackets.ase.container.exceptions;

/**
 *
 */
public class UpgradeFailedException extends Exception {
	
	/**
	 * 
	 */
	public UpgradeFailedException() {
		super();
	}

	/**
	 * 
	 */
	public UpgradeFailedException(String message) {
		super(message);
	}

	
	public UpgradeFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpgradeFailedException(Throwable cause) {
		super(cause);
	}
	
}
