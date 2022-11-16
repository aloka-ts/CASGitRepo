/*
 * ConnectException.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;


/**
 * This ConnectException is thrown by various SBB interface methods if an error occurs
 * while connecting an endpoint.
 *   
 * @see com.baypackets.ase.sbb.SBB
 * 
 * @author BayPackets
 */
public class ConnectException extends Exception {

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectException(Throwable cause) {
		super(cause);
	}

	public ConnectException() {
		super();
	}
	
	public ConnectException(String msg) {
		super(msg);
	}
	
}
