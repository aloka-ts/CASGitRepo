package com.baypackets.ase.spi.container;

import java.io.Serializable;
import java.security.Principal;
import java.util.Random;
import javax.security.auth.Subject;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

public abstract class AbstractSasMessage implements SasMessage, Serializable, Cloneable {
	private static final long serialVersionUID = 38970298082488064L;
	private static Logger _logger = Logger.getLogger(AbstractSasMessage.class);

	private transient SasMessageContext context;
	
	private String handler;
	private SasMessage loopbackSource;
	private Subject subject;
	private Principal principal;
	private Random random = new Random(); // Bug BPInd15323
	
	private boolean initial;
	protected boolean priorityMsg = false;
	private boolean loopback;
	
	
	public AbstractSasMessage() {
		super();
	}
	
	/**
	 * Returns the handler associated with this message 
	 */
	public String getHandler() {
		return this.handler;
	}

	/**
	 * Returns the Original Sas Message from which this request has loopbed back.
	 */
	public SasMessage getLoopbackSourceMessage() {
		return this.loopbackSource;
	}

	/**
	 * Returns the Source of this SAS Message object. 
	 */
	public SasMessageContext getMessageContext() {
		return this.context;
	}
	
	/**
	 * Returns the subject associated with this message
	 */
	public Subject getSubject() {
		return this.subject;
	}
	
	/**
	 * Returns the User Prinicipal associated with this message.
	 * @return The principal object associated with this message.
	 */
	public Principal getUserPrincipal() {
		return this.principal;
	}

	/**
	 * Returns whether this message is initial message or not
	 */
	public boolean isInitial() {
		return this.initial;
	}

	/**
	 * Returns whether this message is a looped back message or not.
	 */
	public boolean isLoopback() {
		return this.loopback;
	}

	/**
	 * Returns the handler associated with this message.
	 */
	public void setHandler(String handler) throws ServletException {
		this.handler = handler;
	}

	/**
	 * Sets the Initial Message Flag for this message.
	 */
	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	/**
	 * Sets the priority Message Flag for this message.
	 */
	public abstract void setMessagePriority(boolean priority);
                                                                                                                             
	/**
	 * Returns the priority Message Flag for this message.
	 */
        public abstract boolean getMessagePriority();


	/**
	 * Sets the Loopback Flag for this message.
	 */
	public void setLoopback(boolean loopback) {
		this.loopback = loopback;
	}

	/**
	 * Sets the Source Message from which this message has looped back.
	 */
	public void setLoopbackSourceMessage(SasMessage message) {
		this.loopbackSource = message;
	}

	/**
	 * Sets the Source of this Message. If this is not set by the resource-adaptor,
	 * the Container will associate the <code>ResourceAdaptor</code> object itself,
	 * as the source of the message. 
	 * 
	 * If the resource-adaptor, set this value other than the ResourceAdaptor object,
	 * it should also override and implement the send() method of this class. 
	 */
	public void setMessageContext(SasMessageContext source) {
		this.context = source;
	}

	/**
	 * Sets the Subject associated with this request.
	 */
	public void setSubject(Subject subject) {
		this.subject =subject;
	}

	/**
	 * Sets the User Principal associated with this message.
	 */
	public void setUserPrincipal(Principal principal) {
		this.principal = principal;
	}
			
	/**
	 * The Sub-classes should override this method if they 
	 * want to use this functionality. 
	 * This implementation just returns a NULL value.
	 */
	public String decode(){
		return null;
	}

	// Bug BPInd15323: [
	/**
	 * Returns the index of the worker thread queue to 
	 * enqueue this message in.  This implementation will
	 * simply return the result of a random number generator.
	 * Subclasses should override this method if they can
	 * provide a more appropriate index based on the type of
	 * message protocol (ex. hash of the message dialog ID).
	 */
	public int getWorkQueue() {
		int value = this.random.nextInt();

		if (_logger.isDebugEnabled()) {
			_logger.debug("getWorkQueue(): Returning value: " + value);
		}
		return value;
	}
	// ]
	
	/**
	 * Returns the protocol specific method name for this message.
	 */
	public abstract String getMethod();
	
	/**
	 * Returns the name of the protocol.
	 */
	public abstract String getProtocol();
	
	/**
	 * Returns the falg indicating whether this message came over a
	 * secure channel or not. 
	 */
	public abstract boolean isSecure();

	/**
	 * Returns the protocol Session associated with this message.
	 * Create session if create is true and session does not already
	 * exist.
	 */
	public abstract SasProtocolSession getProtocolSession(boolean create);

	/**
	 * Returns the protocol Session associated with this message.
	 * Return NULL for sessionless message.
	 */
	public abstract SasProtocolSession getProtocolSession();

}
