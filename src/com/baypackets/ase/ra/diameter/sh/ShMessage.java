package com.baypackets.ase.ra.diameter.sh;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;

public class ShMessage extends AbstractSasMessage implements Message, Constants {

	private static Logger logger = Logger.getLogger(ShMessage.class);

	private int type;  
	private String method;
	private SasProtocolSession session;
	private Destination m_destination=  null;

	//protected ResourceContext context;

	public ShMessage() {
		super();
		logger.debug("Inside ShMessage() constructor ");
	}

	public ShMessage(int type) {
		logger.debug("Inside ShMessage constructor ");
		this.type = type;
	}

	public String getMethod() {
		return this.method;
	}

	public String getProtocol() {
		return PROTOCOL;
	}

	public boolean isSecure() {
		return false;
	}

	public SasProtocolSession getProtocolSession() {
		return this.session;
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (create && ShResourceAdaptorImpl.getResourceContext() != null) {
			try {
				//this.session = this.context.getSessionFactory().createSession();
				this.session = ShResourceAdaptorImpl.getResourceContext().getSessionFactory().createSession();
			} catch (Exception e) {
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}

	public int getType() {
		return type;
	}

	public ResourceSession getSession() {
		return (ResourceSession)this.getProtocolSession();
	}

	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;		
	}

	public void send() throws IOException {
		logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			logger.info("Send to Sh resource adaptor directly");
			try {
				ShResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " ,e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void set(Object arg0) {
		// TODO Auto-generated method stub
		logger.info("set() is not supported.");

	}

	public Object get() {
		// TODO Auto-generated method stub
		logger.info("get() is not supported.");
		return null;
	}

	public void setProtocolSession(SasProtocolSession session) {
		this.session = session;		
	}
	/*	
	public ResourceContext getResourceContext() {
		return this.context;
	}

	public void setResourceContext(ResourceContext context) {
		this.context = context;
	}*/

	public void setDestination(Object destination)
	{
		if(m_destination==null)
			m_destination= new Destination();
		this.m_destination = (Destination)destination;
	}

	public Object getDestination()
	{
		return this.m_destination;
	}

	/**
	 * Sets the priority Message Flag for this message.
	 */
	public void setMessagePriority(boolean priority)        {
		priorityMsg = priority;
	}

	/**
	 * Returns the priority Message Flag for this message.
	 */
	public boolean getMessagePriority()     {
		return priorityMsg;
	}

}
