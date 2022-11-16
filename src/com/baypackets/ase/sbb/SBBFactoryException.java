/*
 * SBBFactoryException.java
 * 
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.sbb;


/**
 * The SBBFactoryException is thrown by the SBBFactory methods.
 * 
 * @see com.baypackets.ase.sbb.SBBFactory
 * 
 * @author BayPackets
 */
public class SBBFactoryException extends Exception {

	public SBBFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public SBBFactoryException(Throwable cause) {
		super(cause);
	}

	public SBBFactoryException() {
		super();
	}
	
	public SBBFactoryException(String msg) {
		super(msg);
	}
	
}
