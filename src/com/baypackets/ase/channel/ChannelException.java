package com.baypackets.ase.channel;

public class ChannelException extends java.lang.Exception {
	public ChannelException(){
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ChannelException(Throwable cause) {
		super(cause);
	}

	public ChannelException (String msg){
		super (msg);
	}
}
