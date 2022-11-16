/*
 * AseSipSessionStateException.java
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSipSessionStateException extends Exception {
	public AseSipSessionStateException() {
		  super();
	 }
	 
	public AseSipSessionStateException(String message) {
		  super(message);
	 }

	public AseSipSessionStateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AseSipSessionStateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}

