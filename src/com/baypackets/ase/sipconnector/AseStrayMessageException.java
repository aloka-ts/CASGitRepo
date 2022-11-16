/*
 * AseStrayMessageException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseStrayMessageException extends Exception {
	public AseStrayMessageException() {
		  super();
	 }
	 
	public AseStrayMessageException(String message) {
		  super(message);
	 }

	public AseStrayMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseStrayMessageException(Throwable cause) {
		super(cause);
	}
}

