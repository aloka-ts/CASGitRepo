package com.baypackets.ase.ra.rf.impl;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.ra.rf.Constants;
import com.baypackets.ase.ra.rf.RfRequest;
import com.baypackets.ase.ra.rf.RfResourceException;
import com.baypackets.ase.ra.rf.RfResourceFactory;
import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RfResourceFactoryImpl implements RfResourceFactory, Constants 
{
	
	private static Logger logger = Logger.getLogger(RfResourceFactoryImpl.class);
	private ResourceContext context;
	private MessageFactory msgFactory;
	private SessionFactory sessionFactory;
	
	public void init(ResourceContext context) 
	{
		if(logger.isDebugEnabled())
			logger.debug("init() is called.");
		this.context = context;
		if (context != null) 
		{
			if(logger.isDebugEnabled())
				logger.debug("init(): get message factory and session factory from context.");
			this.msgFactory = (MessageFactory)context.getMessageFactory();
			if (this.msgFactory == null) 
			{
				logger.error("init(): null message factory.");
			}
			this.sessionFactory = (SessionFactory)context.getSessionFactory();
			if (this.msgFactory == null) 
			{
				logger.error("init(): null session factory.");
			}
		} 
		else 
		{
			try 
			{
				this.msgFactory = RfResourceAdaptorFactory.getMessageFactory();
				this.sessionFactory = RfResourceAdaptorFactory.getSessionFactory();
			} 
			catch (Exception e) 
			{
				logger.error("init(): " + e);
			}
		}
	}

	public ResourceSession createSession(SipApplicationSession appSession) throws ResourceException
	{	
		if(logger.isDebugEnabled())
			logger.debug("Inside createSession()");
		if(appSession == null)
		{
			throw new IllegalArgumentException("Application Session cannot be NULL ");
		}
		SasProtocolSession session = sessionFactory.createSession();
		((SasApplicationSession)appSession).addProtocolSession(session);
		if(logger.isDebugEnabled())
			logger.debug("leaving createSession()");
		return (ResourceSession)session;
	}
	
	public RfRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering createRequest(SipApplicationSession).");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		if(logger.isInfoEnabled())
			logger.info("Creating Request");
		SasMessage message = msgFactory.createRequest(session, type);
		message.setInitial(true);
		if(logger.isDebugEnabled())
			logger.debug("Leaving createRequest(SipApplicationSession).");
		return (RfRequest)message;
		
	}

	public Request createRequest(int type) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering createRequest(int).");
		if(logger.isInfoEnabled()) {
			if(type==EVENT)
				logger.info("Creating EVENT RFAccountingRequest using Resource Factory");
			else
				logger.info("Creating SESSION RFAccountingRequest using Resource Factory");
		}
			
		return this.createRequest(null, type);
	}

}
