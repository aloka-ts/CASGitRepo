/*
 * MediaServerException.java
 * 
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.sbb;


/**
 * The MediaServerException is thrown by the MediaServer interface methods if an error
 * occurs while interacting with the media server.
 * 
 * @see com.baypackets.ase.sbb.MsSessionController
 * 
 * @author BayPackets
 */
public class MediaServerException extends Exception {

	public MediaServerException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MediaServerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MediaServerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public MediaServerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
