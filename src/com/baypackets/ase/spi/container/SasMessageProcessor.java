package com.baypackets.ase.spi.container;

import javax.servlet.ServletException;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;


public interface SasMessageProcessor {
	
	/**
	 * This method will process the message.
	 * @param message The message object to be processed.
	 * @return Whether or not the message was handled properly.
	 * @throws AseInvocationFailedException if there is a problem handling this mesasge. 
	 */
	public void processMessage(SasMessage message) throws AseInvocationFailedException, ServletException;
}
