/*
 * AseSipMessageHandlerException.java
 */

package com.baypackets.ase.sipconnector;

import java.lang.Exception;

public class AseSipMessageHandlerException extends Exception {
	
	// UAT-792 : race Condition where ACK for 4XX response is not
	// going.
	// This was happening because of the fact that SIP Session was
	// terminated.
	// When session is terminated InviteMsgHandler got
	// AseSipMessageHandlerException
	// and on its handling it sends NOOP.
	// There are other cases as well where we can get NOOP:
	// 1) When we don't find any outstanding request.
	// 2) When we need to proxy the response.
	private boolean sessionTermOrInvalid = false;
	
	public boolean isSessionTermOrInvalid() {
		return sessionTermOrInvalid;
	}

	public AseSipMessageHandlerException() {
        super();
    }

	public AseSipMessageHandlerException(String message) {
        super(message);
    }
	
	public AseSipMessageHandlerException(String message, boolean sessionTermOrInvalid) {
		super(message);
		this.sessionTermOrInvalid = sessionTermOrInvalid;
    }

	public AseSipMessageHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AseSipMessageHandlerException(Throwable cause) {
		super(cause);
	}
}
