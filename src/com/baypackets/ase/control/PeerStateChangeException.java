/*
 * Created on Nov 15, 2004
 *
 */
package com.baypackets.ase.control;

/**
 * @author Ravi
 */
public class PeerStateChangeException extends Exception {
	
	public static final int DATA_CHANNEL_NOT_CONNECTED = 1;

	private int errorCode;	
	
	public PeerStateChangeException(int code, String msg){
		super(msg);
		this.errorCode = code;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int i) {
		errorCode = i;
	}

}
