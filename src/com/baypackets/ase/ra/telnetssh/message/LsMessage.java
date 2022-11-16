/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import java.io.IOException;
import java.util.Random;

import javax.print.attribute.standard.Destination;
import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;

/**
 * The Class LsMessage.
 * Extends AbstractSasMessage
 * This class defines format of message
 * to be exchanged between RA and application
 * LsRequest and LsResponse extends this class.
 *
 * @author saneja
 */
public class LsMessage extends AbstractSasMessage implements Message{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 500000001L;
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LsMessage.class);
	
	/** The type. */
	private int type;  
	
	/** The message method. */
	private String method;
	
	/** The session. */
	private SasProtocolSession session;
	
	private Destination m_destination=  null;
	
	/** The Constant PROTOCOL. */
	private String PROTOCOL="TELNET_SSH";
	
	/** The ls resource adaptor. */
	private transient LsResourceAdaptor lsResourceAdaptor;
	
	private Random random = new Random();
	/**
	 * Instantiates a new ls message.
	 */
	public LsMessage() {
		super();
		if(logger.isDebugEnabled())
			logger.debug("Inside LsMessage() constructor ");
	}

	/**
	 * Instantiates a new ls message.
	 *
	 * @param type the type
	 */
	public LsMessage(int type) {
		if(logger.isDebugEnabled())
			logger.debug("Inside LsMessage(int) constructor ");
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#getMethod()
	 */
	@Override
	public String getMethod() {
		return this.method;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#getProtocolSession(boolean)
	 */
	@Override
	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (this.session==null && create && lsResourceAdaptor.getResourceContext() != null) {
			try {
				//this.session = this.context.getSessionFactory().createSession();
				this.session = lsResourceAdaptor.getResourceContext().getSessionFactory().createSession();
			} catch (Exception e) {
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#getProtocolSession()
	 */
	@Override
	public SasProtocolSession getProtocolSession() {
		return this.session;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#getType()
	 */
	@Override
	public int getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#getApplicationSession()
	 */
	@Override
	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#getSession()
	 */
	@Override
	public ResourceSession getSession() {
		return (ResourceSession)this.getProtocolSession();
	}

		/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#send()
	 */
	@Override
	public void send() throws IOException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			if(isDebugEnabled)
				logger.debug("Sending to resource adaptor directly.");
			try {
				lsResourceAdaptor.sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " + e);
				throw new IOException(e);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#set(java.lang.Object)
	 */
	@Override
	public void set(Object arg0) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("set() called.:::::Not Supported");
		
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Message#get()
	 */
	@Override
	public Object get() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("get() called.:::::Not Supported");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.SasMessage#getDestination()
	 */
	@Override
	public Object getDestination() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("getDestination() called.");
		return m_destination;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.SasMessage#setDestination(java.lang.Object)
	 */
	@Override
	public void setDestination(Object destn) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("setDestination() called.");
		this.m_destination=(Destination)destn;
		
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#setMessagePriority(boolean)
	 */
	@Override
	public void setMessagePriority(boolean priority) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("setMessagePriority() called.:::::Not Supported");
		
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractSasMessage#getMessagePriority()
	 */
	@Override
	public boolean getMessagePriority() {
		return priorityMsg;
	}
	
	/*
	 * 
	 */
	/**
	 * Sets the protocol session.
	 *
	 * @param session the new protocol session
	 */
	public void setProtocolSession(SasProtocolSession session) {
		this.session = session;		
	}

	/**
	 * @param lsResourceAdaptor the lsResourceAdaptor to set
	 */
	public void setLsResourceAdaptor(LsResourceAdaptor lsResourceAdaptor) {
		this.lsResourceAdaptor = lsResourceAdaptor;
	}

	/**
	 * @return the lsResourceAdaptor
	 */
	protected LsResourceAdaptor getLsResourceAdaptor() {
		return lsResourceAdaptor;
	}
	
	public int getWorkQueue() {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if(isDebugEnabled){
			logger.debug("getWorkQueue() called.");
		}
		SipApplicationSessionImpl appSession = (SipApplicationSessionImpl) this.getApplicationSession(); 
		AseIc ic = null;
		if (appSession != null){
			ic = appSession.getIc();
			if (ic != null){
				if (isDebugEnabled) {
					logger.debug("getWorkQueue(): Returning value from IC: " + ic.getWorkQueue());
				}
				return ic.getWorkQueue();
			}
		}
		int value = this.random.nextInt();

		if (isDebugEnabled) {
			logger.debug("getWorkQueue(): Returning value: " + value);
		}
		return value;

	}

}
