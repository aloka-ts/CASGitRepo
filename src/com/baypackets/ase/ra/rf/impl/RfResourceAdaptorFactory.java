package com.baypackets.ase.ra.rf.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.rf.RfResourceFactory;
import com.baypackets.ase.ra.rf.impl.RfMessageFactoryImpl;
import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorImpl;
import com.baypackets.ase.ra.rf.impl.RfResourceFactoryImpl;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;

import com.baypackets.ase.ra.rf.RfMessageFactory ;

public class RfResourceAdaptorFactory 
{
	private static Logger logger = Logger.getLogger(RfResourceAdaptorFactory.class);

	private static RfResourceAdaptor resourceAdaptor;
	private static RfResourceFactory resourceFactory;
	private static RfMessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static RfResourceAdaptor getResourceAdaptor() throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) 
		{
			resourceAdaptor = new RfResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	public static RfResourceFactory getResourceFactory() throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Inside getResourceFactory() ");
		if (resourceFactory == null) 
		{
			resourceFactory = new RfResourceFactoryImpl();
			((RfResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	public static RfMessageFactory getMessageFactory() throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("inside getMessageFactory()");
		if (messageFactory == null) 
		{
			if(logger.isDebugEnabled())
				logger.debug("message factory is null");
			messageFactory = new RfMessageFactoryImpl();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	public static SessionFactory getSessionFactory() throws ResourceException 
	{
		if (sessionFactory == null) 
		{
			sessionFactory = new RfSessionFactory();
			sessionFactory.init(null);
		}
		return sessionFactory;
	}

}
