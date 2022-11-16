package com.baypackets.ase.resource;

import java.util.Enumeration;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;

/**
 * Represents a Session between two messaging end-points namely
 *  a. The application that is  sending or receiving the message and
 *  b. The resource that is receiving or sending the message.
 *
 * The Session object will be used to store the state information.
 * The Session object can be obtained by calling the 
 * <code>Message.getSession()</code> method.
 */
public interface ResourceSession {
	/**
	 * Returns the Application Session associated with this protocol Session
	 * @return the Sip Application Session associated with the session object.
	 */
	public SipApplicationSession getApplicationSession();

	/**
	 * Returns resource listener for this session
	 * @return Returns the name of the message listener associated with this session.
	 */
	public String getMessageListener();
	
	/**
	 * Sets the name of the handler for this session
	 * 
	 * @param string Name of the message listener to be associated with this session.
	 */
	public void setMessageListener(String name) throws ResourceException;
	
	/**
	 * Returns the attribute object
	 * @param name - name of the attribute
	 */
	public Object getAttribute(String name);	
	
	/**
	 * Returns the list of available attribute names in an enumeration.
	 * @return Enumeration for the available attribute names.
	 */
	public Enumeration getAttributeNames();	
	
	/**
	 * Sets the following attribute into the session.
	 * @param name - name of the attribute
	 */
	public void setAttribute(String name, Object value);
	
	/**
	 * Removes the attribute with the specified name.
	 * @param name - name of the attribute
	 */
	public void removeAttribute(String name);
	
	/**
	 * Returns the created time for this session.
	 * @return the creation time for this session.
	 */
	public long getCreationTime();	
	
	/**
	 * Returns the ID for this session
	 * @return the ID for this session object.
	 */
	public String getId();
	
	/**
	 * Returns the last accessed time for the session
	 * @return the last accessed time for this session.
	 */
	public long getLastAccessedTime();
	
	/**
	 * Returns the state of this session
	 * @return the State of this Session.
	 */
	public int  getProtocolSessionState();
	
	/**
	 * Invalidates this session.
	 */
	public void invalidate ();
	
	/**
	 * Creates a Message object of the specified type and returns it.
	 * The message created will be associated with the session creating it.
	 * 
	 * The types will be specified part of the contract between the application
	 * and the resource adaptor.
	 * 
	 * @param type Type of the message to be created.
	 * @return newly created Request Object.
	 */
	public Message createMessage(int type) throws ResourceException;
}
