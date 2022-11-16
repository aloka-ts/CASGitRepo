/*
 * AseSipValidationException.java
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSipValidationException extends Exception {
	public AseSipValidationException() {
        super();
    }

	public AseSipValidationException(String message) {
        super(message);
    }

	public AseSipValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseSipValidationException(Throwable cause) {
		super(cause);
	}
}
