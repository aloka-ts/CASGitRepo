/*
 * AseOutOfSequenceException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseOutOfSequenceException extends Exception {
	 public AseOutOfSequenceException() {
		  super();
	 }
	 
	 public AseOutOfSequenceException(String message) {
		  super(message);
	 }

	public AseOutOfSequenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseOutOfSequenceException(Throwable cause) {
		super(cause);
	}
}

