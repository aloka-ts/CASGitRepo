/*
 * AseSessionInvalidException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSessionInvalidException extends Exception {
	public AseSessionInvalidException() {
		  super();
	 }
         
	public AseSessionInvalidException(String message) {
		  super(message);
	 }

	public AseSessionInvalidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AseSessionInvalidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
