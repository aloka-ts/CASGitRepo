package com.baypackets.ase.spi.container;

import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;

public interface SasMessage {

	/**
	 * Returns the protocol Session associated with this Message.
	 * @return The protocol Session associated with this Message. 
	 * NULL for Sessionless messages. 
	 */
	public SasProtocolSession getProtocolSession();
	
	/**
	 * Returns the protocol Session associated with this Message. Optionally
	 * creates protocol session, if not already present.
	 *
	 * @param create if true and session does not exists, create one.
	 * @return The protocol Session associated with this Message. 
	 */
	public SasProtocolSession getProtocolSession(boolean create);
	
	/**
	 * Returns the Application Session associated with this message. 
	 * @return The application Session associated with this message.
	 */
	public SipApplicationSession getApplicationSession();
	
	/**
	 * Returns whether or not this message is INITIAL
	 * @return Flag indicating whether this is an INITIAL message or not.
	 */
	public boolean isInitial();
	
	/**
	 * Sets whether or not this message is an initial request or not.
	 * @param initial Flag indicating whether this is an initial message or not.
	 */
	public void setInitial(boolean initial);

	/**
	 * Returns whether the message is a Loopback message or not.
	 * @return Flag indicating whether it is a looped back message or not.
	 */
	public boolean isLoopback();
	
	/**
	 * Sets whether or not this message is Loopback message. 
	 * @param loopback Flag indicating whether it is a loopback message or not.
	 */
	public void setLoopback(boolean loopback);
	
	/**
	 * This method will return the original message if this message is a 
	 * Loopback message. The container will make use of this method to
	 * associate the messages that form the application chain.
	 * @return The original message object if this is a loopback message, NULL otherwise.
	 */
	public SasMessage getLoopbackSourceMessage();
	
	/**
	 * Sets the original message that resulted in this looped back message. 
	 * @param message The Original Message
	 */
	public void setLoopbackSourceMessage(SasMessage message);
	
	/**
	 * Returns the subject associated with this messasge.
	 * @return
	 */
	public Subject getSubject();
	
	/**
	 * Sets the Subject associated with this message.
	 * @param subject
	 */
	public void setSubject(Subject subject);
        
	/**
	 * Returns the Principal associated with this message.
	 */
	public Principal getUserPrincipal();
	
	/**
	 * Sets the Principal associated with this message.
	 * @param principal
	 */
	public void setUserPrincipal(Principal principal);
	
	/**
	 * This method would be used by the container to associate an
	 * initial message with an existing application session without doing any rule matching.
	 * 
	 *  If the resource adptor wants to associate this message with an existing application session,
	 *  it should 
	 *  <li>
	 *  a. Override this method.
	 *  b. Return a valid applcation session ID to which this message is to be associated.
	 *  </li>
	 *  
	 *  In case of, the application session ID returned by this method is INVALID,
	 *  then the container will continue with the default processing. That is,
	 *  Identify the matching rule and give it to the matching application.
	 *  
	 *  @return Application Session ID to which this message is to be associated. */
	public String decode();
	
	/**
	 * Returns the Message Context associated with this message.
	 * @return
	 */
	public SasMessageContext getMessageContext();
	
	/**
	 * Sets the Context object for this message.
	 * @param source
	 */
	public void setMessageContext(SasMessageContext source);
	
	/**
	 * Returns the name and version of the protocol the request uses in the form protocol/majorVersion.minorVersion, for example, SIP/2.0
	 * @return a String containing the protocol name and version number
	 */
	public String getProtocol();

	/**
	 * Returns the name of the specific method inside the protocol.
	 * For eg., in case of SIP protocol, it could be INVITE, OPTIONS, CANCEL, ACK, BYE, REGISTER, etc.
	 * In case of HTTP protocol, it could be GET, POST etc.
	 * In case of the protocol does not have any methods like this, this method would return an empty String.
	 * 
	 * @return String containing the name of the method used in the message.
	 */
	public String getMethod();
	
	/**
	 *Returns a boolean indicating whether this request was made using a secure channel, such as TLS
	 *@return a boolean indicating if the request was made using a secure channel 
	 */
	public boolean isSecure();
	
	/**
	 * Returns the handler object associated with this message.
	 * @return String object containing the name of the handler.
	 */
	public String getHandler();
	
	/**
	 * Sets the handler to be used for processing this message.
	 * This method will be called by the container after doing a rule match.
	 * 
	 * @param handler Name of the handler that will process this message.
	 */
	public void setHandler(String handler) throws ServletException;

	// Bug BPInd15323: [
	/**
	 * Returns the index of the worker thread queue to enqueue this message in.
	 */
	public int getWorkQueue();
	// ]

	// Bug BPInd 17365
	/**
	 * Returns the destination of the message
	 *
	 */
	 public Object getDestination();

	 /**
	  * Sets the destination of this message
	  */
	  public void setDestination(Object destination);

	public void setMessagePriority(boolean priority);	
	
	public boolean getMessagePriority();

}
