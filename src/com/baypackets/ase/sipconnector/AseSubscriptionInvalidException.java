/*
 * AseSubscriptionInvalidException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSubscriptionInvalidException extends Exception {
	public AseSubscriptionInvalidException() {
		  super();
	 }
	 
	public AseSubscriptionInvalidException(String message) {
		  super(message);
	 }

	public AseSubscriptionInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseSubscriptionInvalidException(Throwable cause) {
		super(cause);
	}
	
}
