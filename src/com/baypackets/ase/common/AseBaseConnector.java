/*
 * Created on Aug 13, 2004
 */

package com.baypackets.ase.common;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;

/**
 * @author Neeraj Jain
 *
 * The <code>AseBaseConnector</code> is the base class implementation of
 * protocol connectors. This class provides a framework for container to
 * interact with a connector in a protocol independent manner. It implements
 * <code>MComponent</code> interface so as to be managed by the TMN framework.
 *
 * @see com.baypackets.slee.agent.MComponent
 */
public abstract class AseBaseConnector
	implements com.baypackets.bayprocessor.agent.MComponent {

	/**
	 * Constructor. Does nothing.
	 */
	public AseBaseConnector() {
		m_l.log(Level.ALL, "AseBaseConnector():enter");
		m_l.log(Level.ALL, "AseBaseConnector():exit");
	}

	/**
	 * Subclasses should implement this method to change state of connector
	 *
	 * @param componentState state to which connector has to transit. It can be
	 *                       LOADED, RUNNING or STOPPED.
	 */
	public abstract void changeState(MComponentState componentState);

	/**
	 * Subclasses should implement this method to update their configuration.
	 *
	 * @param componentState state to which connector has to transit
	 *
	 * @param opType type of operation - ADD, MODIFY or DELETE
	 */
	public abstract void updateConfiguration(	Pair[]			configData,
												OperationType	opType);

	/**
	 * Subclasses should implement this method to handle the messages coming
	 * from container.
	 *
	 * @param message message from the container
	 */
	public abstract void handleMessage(AseMessage message) throws IOException;

	/**
	 * Subclasses should return associated protocol in this method.
	 *
	 * @return returns connector protocol string
	 */
	public abstract java.lang.String getProtocol();

	/**
	 * Subclasses should return associated factory in this method.
	 *
	 * @return returns connector factory object
	 */
	public abstract java.lang.Object getFactory();

	/**
	 * This method is notification from container about termination of given
	 * protocol session. Subclasses should remove the given protocol session
	 * from underlying data structures.
	 *
	 * @param session protocol session to be removed
	 */
	public abstract void removeSession(SasProtocolSession session);

	/**
	 * This method is notification from container about addition of given
	 * protocol session. Subclasses should add the given protocol session
	 * to underlying data structures.
	 *
	 * @param session protocol session to be added
	 */
	public abstract void addSession(SasProtocolSession session);

	/**
	 * Notification to connector that a message has been processed.
	 */
	public void messageProcessed() {
	}
	
	/**
	 * Accesor method for container.
	 *
	 * @return associated container reference
	 */
	public AseContainer getContainer() {
		m_l.log(Level.ALL, "getContainer()");
		return m_container;
	}

	/**
	 * Sets reference to container and dispatcher objects.
	 *
	 * @param container reference to container instance
	 *
	 * @param dispatcher reference to this protocol connector specific
	 *                   dispatcher object
	 */
	protected void initialize(	AseContainer	container ) {
		m_l.log(Level.ALL, "initialize(AseContainer, AseDispatcher).enter");

		m_container		= container;
		
		m_l.log(Level.ALL, "initialize(AseContainer, AseDispatcher).exit");
	}

	/**
	 * Registers this connector and dispatcher with container.
	 */
	protected void start() {
		m_l.log(Level.ALL, "start()");
		//TBD
	}

	/**
	 * Unregisters this connector and dispatcher with container.
	 */
	protected void stop() {
		m_l.log(Level.ALL, "stop()");
		//TBD
	}

	private AseContainer	m_container		= null;

	
	private Logger m_l = Logger.getLogger(AseBaseConnector.class.getName());
}
