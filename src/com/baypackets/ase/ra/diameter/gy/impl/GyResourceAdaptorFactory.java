package com.baypackets.ase.ra.diameter.gy.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.GyResourceAdaptor;
import com.baypackets.ase.ra.diameter.gy.GyResourceFactory;
import com.baypackets.ase.ra.diameter.gy.rarouter.GyAppRouter;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;

public class GyResourceAdaptorFactory {
	
	private static Logger logger = Logger.getLogger(GyResourceAdaptorFactory.class);
	
	private static GyResourceAdaptor resourceAdaptor;
	private static GyResourceFactory resourceFactory;
	private static MessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static GyResourceAdaptor getResourceAdaptor() throws ResourceException {
		logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) {
			resourceAdaptor = new GyResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	static void setResourceAdaptor(GyResourceAdaptor adaptor){
		logger.debug("Inside setResourceAdaptor() with ResourceAdaptor = "+resourceAdaptor);
		if (resourceAdaptor == null) {
			resourceAdaptor = adaptor;
		}
	} 
	public static GyResourceFactory getResourceFactory() throws ResourceException {
		logger.debug("Inside getResourceFactory()");
		if (resourceFactory == null) {
			resourceFactory = new GyResourceFactoryImpl();
			((GyResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	public static void setResourceFactory(GyResourceFactory factory) {
		logger.debug("Inside setResourceFactory() with ResourceFactory = "+resourceFactory);
		if (resourceFactory == null) {
			resourceFactory = factory;
		}
	} 
	public static MessageFactory getMessageFactory() throws ResourceException {
		logger.debug("Inside getMessageFactory()");
		if (messageFactory == null) {
			messageFactory = GyMessageFactoryImpl.getInstance();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	static void setMessageFactory(GyMessageFactory factory) {
		logger.debug("Inside ShMessageFactory() with MessageFactory = "+messageFactory);
		if (messageFactory == null) {
			messageFactory = factory;
		}
	} 
	public static SessionFactory getSessionFactory() throws ResourceException {
		logger.debug("Inside getSessionFactory()");
		if (sessionFactory == null) {
			sessionFactory = new GySessionFactory();
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
	
	public static GyAppRouter getAppRouter() throws ResourceException {
		logger.debug("Inside getAppRouter()");
		return GyAppRouter.getInstanse();
	}
}