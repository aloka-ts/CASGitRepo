package com.baypackets.ase.ra.radius.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.RadiusResourceAdaptor;
import com.baypackets.ase.ra.radius.RadiusResourceFactory;
import com.baypackets.ase.ra.radius.rarouter.RadiusAppRouter;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;

public class RadiusResourceAdaptorFactory {
	
	private static Logger logger = Logger.getLogger(RadiusResourceAdaptorFactory.class);
	
	private static RadiusResourceAdaptor resourceAdaptor;
	private static RadiusResourceFactory resourceFactory;
	private static MessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static RadiusResourceAdaptor getResourceAdaptor() throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) {
			resourceAdaptor = new RadiusResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	static void setResourceAdaptor(RadiusResourceAdaptor adaptor){
		if(logger.isDebugEnabled())
			logger.debug("Inside setResourceAdaptor() with ResourceAdaptor = "+resourceAdaptor);
		if (resourceAdaptor == null) {
			resourceAdaptor = adaptor;
		}
	} 
	public static RadiusResourceFactory getResourceFactory() throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside getResourceFactory()");
		if (resourceFactory == null) {
			resourceFactory = new RadiusResourceFactoryImpl();
			((RadiusResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	public static void setResourceFactory(RadiusResourceFactory factory) {
		if(logger.isDebugEnabled())
			logger.debug("Inside setResourceFactory() with ResourceFactory = "+resourceFactory);
		if (resourceFactory == null) {
			resourceFactory = factory;
		}
	} 
	public static MessageFactory getMessageFactory() throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside getMessageFactory()");
		if (messageFactory == null) {
			messageFactory = RadiusMessageFactoryImpl.getInstance();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	static void setMessageFactory(RadiusMessageFactory factory) {
		if(logger.isDebugEnabled())
			logger.debug("Inside ShMessageFactory() with MessageFactory = "+messageFactory);
		if (messageFactory == null) {
			messageFactory = factory;
		}
	} 
	public static SessionFactory getSessionFactory() throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside getSessionFactory()");
		if (sessionFactory == null) {
			sessionFactory = new RadiusSessionFactory();
			sessionFactory.init(null);
		}
		return sessionFactory;
	}
	static void setSessionFactory(SessionFactory factory) {
		if(logger.isDebugEnabled())
			logger.debug("Inside setSessionFactory() with SessionFactory = "+sessionFactory);
		if (sessionFactory == null) {
			sessionFactory = factory;
		}
	} 
	
	public static RadiusAppRouter getAppRouter() throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside getAppRouter()");
		return RadiusAppRouter.getInstanse();
	}
}