package com.baypackets.ase.spi.container;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;

/**
 * The Callback interface for the SAS Message Processor 
 * to notify the message source about 
 * the Successful completion OR the failure of the message processing. 
 */
public interface SasMessageCallback {

	/**
	 * Notifies the Source object that the message was processed successfully.
	 * @param mesasge - Message that was processed successfully.
	 */
	public void processed(SasMessage mesasge);
	
	/**
	 * Notifies that the Source object the message processing failed.
	 * @param message - Message that was not processed successfully.
	 * @param e - The exception object during the message processing.
	 */
	public void failed(SasMessage message, AseInvocationFailedException e);
	
}
