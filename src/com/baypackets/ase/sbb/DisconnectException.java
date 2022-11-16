/*
 * DisconnectException.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;


/**
 * The DisconnectException is thrown by the various SBB interface methods if an error
 * occurs while disconnecting an endpoint. 
 * 
 * @see com.baypackets.ase.sbb.SBB
 * 
 * @author BayPackets
 */
public class DisconnectException extends Exception {

	public DisconnectException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DisconnectException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public DisconnectException() {
		super();
	}
	
	public DisconnectException(String msg) {
		super(msg);
	}
	
}
