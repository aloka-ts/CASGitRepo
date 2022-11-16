package com.baypackets.ase.spi.container;

import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationListener;

public interface SasProtocolSession extends Replicable {
	
	public static final int INVALID = 0;
	public static final int VALID = 1;
	public static final int DESTROYED = 2;
	
	/**
	 * Returns the ID for this session
	 * @return the ID for this session object.
	 */
	public String getId();
	
	/**
	 * Returns the Application Session associated with this protocol Session
	 * @return
	 */
	public SipApplicationSession getApplicationSession();

	/**
	 * Sets the application session for this protocol session.
	 * This method will be called from the AseHost while associating the appsession
	 * @param session
	 */
	public void setApplicationSession(SipApplicationSession session, int index);

	/**
	 * Returns the state of this session
	 * @return
	 */
	public int getProtocolSessionState();

	/**
	 * Sets the state for this session.
	 * @param i
	 */
	public void setProtocolSessionState(int i);
	
	/**
	 * Invalidates this session.
	 */
	public void invalidate ();
	
	/**
	 * Base class implementation of the cleanup. 
	 * Sub-classes need to over-ride this method for complete cleanup. 
	 */
	public void cleanup();
	
	/**
	 * Returns the protocol used by this session.
	 * @return
	 */
	public String getProtocol();
	
	/**
	 * Returns resource listener for this session
	 * @return
	 */
	public String getHandler();
	
	/**
	 * Sets the name of the handler for this session
	 * @param string
	 */
	public void setHandler(String name) throws ServletException;
	
	/**
	 * Returns the Replication Listener Object that can be 
	 * used for initiating a replication.
	 * @return Replication Listener for this protocol Session.
	 */
	public ReplicationListener getReplicationListener();
	
	/**
	 * The SAS container invokes this method on the session, 
	 * as part of delivering the message to the message handler.
	 * 
	 * In case of the Sessionless messages,
	 * the container will directly deliver the message to the listener without
	 * going to the session objects.
	 * 
	 * The default base class implementation provided by the container will
	 * provide an implementation that will send the message to the appropriate message handler
	 * based on the matched rule.
	 * 
	 * So the sub classes of the Default Protocol Session classes can override this 
	 * method so as to decide whether or not deliver this message to the message listeners
	 * based on their internal states.
	 * 
	 * @param message Message to be delivered to the application messsage listener.
	 * @throws AseInvocationFailedException
	 */
	public void handleMessage(SasMessage message) throws AseInvocationFailedException, ServletException;
	
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
	 * gets the replication listener and calls handleReplicationEvent on it.
	 * @param event - ReplicationEvent to be send.
	 */
	public void sendReplicationEvent(ReplicationEvent event);
}
