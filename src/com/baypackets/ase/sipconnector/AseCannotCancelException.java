/*
 * AseCannotCancelException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseCannotCancelException extends Exception {
	 public AseCannotCancelException() {
		  super();
	 }
	 
	 public AseCannotCancelException(String message) {
		  super(message);
	 }

	public AseCannotCancelException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseCannotCancelException(Throwable cause) {
		super(cause);
	}
}

