package com.baypackets.ase.ra.diameter.ro.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.RoResourceAdaptor;
import com.baypackets.ase.ra.diameter.ro.RoResourceFactory;
import com.baypackets.ase.ra.diameter.ro.rarouter.RoAppRouter;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;

public class RoResourceAdaptorFactory {
	
	private static Logger logger = Logger.getLogger(RoResourceAdaptorFactory.class);
	
	private static RoResourceAdaptor resourceAdaptor;
	private static RoResourceFactory resourceFactory;
	private static MessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static RoResourceAdaptor getResourceAdaptor() throws ResourceException {
		logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) {
			resourceAdaptor = new RoResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	static void setResourceAdaptor(RoResourceAdaptor adaptor){
		logger.debug("Inside setResourceAdaptor() with ResourceAdaptor = "+resourceAdaptor);
		if (resourceAdaptor == null) {
			resourceAdaptor = adaptor;
		}
	} 
	public static RoResourceFactory getResourceFactory() throws ResourceException {
		logger.debug("Inside getResourceFactory()");
		if (resourceFactory == null) {
			resourceFactory = new RoResourceFactoryImpl();
			((RoResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	public static void setResourceFactory(RoResourceFactory factory) {
		logger.debug("Inside setResourceFactory() with ResourceFactory = "+resourceFactory);
		if (resourceFactory == null) {
			resourceFactory = factory;
		}
	} 
	public static MessageFactory getMessageFactory() throws ResourceException {
		logger.debug("Inside getMessageFactory()");
		if (messageFactory == null) {
			messageFactory = RoMessageFactoryImpl.getInstance();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	static void setMessageFactory(RoMessageFactory factory) {
		logger.debug("Inside setMessageFactory() with MessageFactory = "+messageFactory);
		if (messageFactory == null) {
			messageFactory = factory;
		}
	} 
	public static SessionFactory getSessionFactory() throws ResourceException {
		logger.debug("Inside getSessionFactory()");
		if (sessionFactory == null) {
			sessionFactory = new RoSessionFactory();
			sessionFactory.init(null);
		}
		return sessionFactory;
	}
	static void setSessionFactory(SessionFactory factory) {
		logger.debug("Inside setSessionFactory() with SessionFactory = "+sessionFactory);
		if (sessionFactory == null) {
			sessionFactory = factory;
		}
	} 
	
	public static RoAppRouter getAppRouter() throws ResourceException {
		logger.debug("Inside getAppRouter()");
		return RoAppRouter.getInstanse();
	}
}