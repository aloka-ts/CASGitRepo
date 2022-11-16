/*
 * AseSipSessionException.java
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSipSessionException extends Exception {
	public AseSipSessionException() {
        super();
    }

	public AseSipSessionException(String message) {
        super(message);
    }

	public AseSipSessionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AseSipSessionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
