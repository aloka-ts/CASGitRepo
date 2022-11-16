package com.baypackets.ase.resource;

import java.io.IOException;
import javax.servlet.sip.SipApplicationSession;

/**
 * Message interface defines the root interface for all the resource 
 * messages.
 */
public interface Message {

	/**
	 * Returns the Type of this Message Object.
	 * The type can be any integer value as defined by the resource-adaptor's
	 * application contract.
	 * @return Integer value specifying the type of this message object.
	 */
	public int getType();
	
	/**
	 * Returns the Session associated with this message.
	 * It will return NULL if there are no sessions associated with this message.
	 * 
	 * @return the Session associated with it. NULL if no Session is associated.
	 */
	public ResourceSession getSession();
	
	/**
	 * Returns the SIP application session associated with this message.
	 * It will return NULL if there are no sessions associated with this message.
	 * 
	 * @return the Application Session associated with it. NULL if no session is associated.
	 */
	public SipApplicationSession getApplicationSession();
	
	/**
	 * Send this message to the specified resource using the resource adaptor.
	 * 
	 * @throws IOException - if a transport error occurs when trying to send this request.
	 * @throws IllegalStateException - if this message cannot legally be sent in the current state.
	 */
	public void send() throws IOException;
	
	/**
	 * Sets the contents of this message. 
	 * Contents of the message will be the part of the contract between the application and the resource adaptor. 
	 *  
	 * @param content Contents of the Message.
	 * @throws IllegalArgumentException - if the Resource Adaptor does not able to understand the contents.
	 * @throws IllegalStateException - if the message is already sent or if the message is readOnly.
	 */
	public void set(Object content);
	
	/**
	 * Returns the contents of this message.
	 * @return the Contents of this Message.
	 */
	public Object get();
}
