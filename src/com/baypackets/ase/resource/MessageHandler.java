package com.baypackets.ase.resource;

import javax.servlet.ServletContext;

/**
 * Message Listener Object is used to receive asynchronously delivered messages.
 */
public interface MessageHandler {

	/**
	 * This method will be called by the container to initialize the Message Handler Object.
	 * @param context
	 */
	public void init(ServletContext context);
	
	
	/**
	 * This method will be used to pass the 
	 * messages received from the resource will be passed to the application. 
	 * 
	 * @param message Message received from the Resource.
	 * @throws ResourceException if the application does not know how to handle this message.
	 */
	public void handleMessage(Message message) throws ResourceException;
	
	/**
	 * This method will be called by the contaier, when the application is undeployed
	 * or when the container is shutdown.
	 *
	 */
	public void destroy();
}
