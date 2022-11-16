/*
 * ProcessMessageException.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;


/**
 * The ProcessMessageException is thrown by the SBB if an error occurs
 * while processing a given SIP message.
 * 
 * @see com.baypackets.ase.sbb.SBB  
 * 
 * @author BayPackets
 */
public class ProcessMessageException extends Exception {

	public ProcessMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessMessageException(Throwable cause) {
		super(cause);
	}

	public ProcessMessageException() {
		super();
	}
	
	public ProcessMessageException(String msg) {
		super(msg);
	}
	
}
