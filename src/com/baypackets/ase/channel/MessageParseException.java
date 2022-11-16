/*
 * Created on Apr 7, 2005
 *
 */
package com.baypackets.ase.channel;

/**
 * @author Ravi
 */
public class MessageParseException extends ChannelException {

	/**
	 * 
	 */
	public MessageParseException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public MessageParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param msg
	 */
	public MessageParseException(String msg) {
		super(msg);
	}
}
