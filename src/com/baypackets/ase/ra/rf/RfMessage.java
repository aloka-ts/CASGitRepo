package com.baypackets.ase.ra.rf;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

//CL
import com.baypackets.ase.ra.rf.impl.RfSession;
import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorImpl;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.dispatcher.Destination;

/**
 * <code>RfMessage</code> interface represents an Rf message to
 * applications. It specifies common operations which can be performed
 * by applications.
 *
 * @author Prashant Kumar 
 */

public class RfMessage extends AbstractSasMessage implements Message, Constants 
{
	private static Logger logger = Logger.getLogger(RfMessage.class);
	private static final long serialVersionUID = 752488064084L;
	private int type;  
	private String method = null;
	private SasProtocolSession session = null;
	private Destination m_destination=  null;
	
	// protected ResourceContext context = null;
	
	public RfMessage(int type) 
	{
		if(logger.isDebugEnabled())
			logger.debug("inside constructor of RfMessage ");
		this.type = type;
	}

	public String getMethod() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getMethod() called.");
		return this.method;
	}

	public String getProtocol() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getProtocol() called.");
		return PROTOCOL;
	}

	public boolean isSecure() 
	{
		if(logger.isDebugEnabled())
			logger.debug("isSecure() called.");
		return false;
	}

	public SasProtocolSession getProtocolSession() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getProtocolSession() called.");
		return this.session;
	}

	public SasProtocolSession getProtocolSession(boolean create) 
	{
		if(logger.isDebugEnabled())
			logger.debug("getProtocolSession(boolean) called.");
		//if (create && this.context != null) 
		if (create && RfResourceAdaptorImpl.getResourceContext() != null) 
		{
			try 
			{
				if(logger.isDebugEnabled())
					logger.debug("creating the RfSession");
				//this.session = this.context.getSessionFactory().createSession();
				this.session = RfResourceAdaptorImpl.getResourceContext().getSessionFactory().createSession();
				((RfSession)(this.session)).setState(PENDING);
			} 
			catch (Exception e) 
			{
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}

	public int getType() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getType() called.");
		return type;
	}

	public ResourceSession getSession() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getSession() called.");
		return (ResourceSession)this.getProtocolSession();
	}

	public SipApplicationSession getApplicationSession() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getApplicationSession() called.");
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null)
		{
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;		
	}

	public void send() throws IOException 
	{
		if(logger.isDebugEnabled())
			logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null)
		{
			context.sendMessage(this);
		} 
		else 
		{
			if(logger.isInfoEnabled())
				logger.info("Send to Rf resource adaptor directly.");
			try 
			{
				RfResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} 
			catch (Exception e) 
			{
				logger.error("send(): " + e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void set(Object arg0) 
	{
		// TODO Auto-generated method stub
		if(logger.isInfoEnabled())
			logger.info("set() is not supported.");

	}

	public Object get() 
	{
		// TODO Auto-generated method stub
		if(logger.isInfoEnabled())
			logger.info("get() is not supported.");
		return null;
	}
	
	public void setProtocolSession(SasProtocolSession session) 
	{
		if(logger.isDebugEnabled())
			logger.debug("setProtocolSession() called.");
		this.session = session;		
	}
	
	/*public ResourceContext getResourceContext() 
	{
		logger.debug("getResourceContext() called.");
		return this.context;
	}
	
	public void setResourceContext(ResourceContext context) 
	{
		logger.debug("setResourceContext() called.");
		this.context = context;
	}
	*/

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
