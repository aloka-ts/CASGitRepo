/*
 * AseDialogInvalidException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseDialogInvalidException extends Exception {
	public AseDialogInvalidException() {
		  super();
	 }
	 
	public AseDialogInvalidException(String message) {
		  super(message);
	 }

	public AseDialogInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseDialogInvalidException(Throwable cause) {
		super(cause);
	}
	
}
