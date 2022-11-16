/*
 * CommandFailedException.java
 *
 * Created on July 13, 2004, 5:12 PM
 */
package com.baypackets.ase.spi.util;


/**
 * This exception is thrown by the CommandHandler interface if an error occurs 
 * while executing a given command.
 *
 * @see com.baypackets.ase.util.CommandHandler
 *
 * @author Zoltan Medveczky
 */
public final class CommandFailedException extends Exception {
    
    public CommandFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandFailedException(Throwable cause) {
		super(cause);
	}

	/**
     *
     *
     */
    public CommandFailedException() {
        super();
    }
    
    /**
     * 
     *
     */
    public CommandFailedException(String message) {
        super(message);
    }
}
