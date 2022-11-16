package com.baypackets.ase.ra.diameter.rf.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.diameter.rf.RfResourceFactory;
import com.baypackets.ase.ra.diameter.rf.rarouter.RfAppRouter;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;

public class RfResourceAdaptorFactory {
	
	private static Logger logger = Logger.getLogger(RfResourceAdaptorFactory.class);
	
	private static RfResourceAdaptor resourceAdaptor;
	private static RfResourceFactory resourceFactory;
	private static RfMessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static RfResourceAdaptor getResourceAdaptor() throws ResourceException {
		logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) {
			resourceAdaptor = new RfResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	static void setResourceAdaptor(RfResourceAdaptor adaptor){
		logger.debug("Inside setResourceAdaptor() with ResourceAdaptor = "+resourceAdaptor);
		if (resourceAdaptor == null) {
			resourceAdaptor = adaptor;
		}
	} 
	public static RfResourceFactory getResourceFactory() throws ResourceException {
		logger.debug("Inside getResourceFactory()");
		if (resourceFactory == null) {
			resourceFactory = new RfResourceFactoryImpl();
			((RfResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	public static void setResourceFactory(RfResourceFactory factory) {
		logger.debug("Inside setResourceFactory() with ResourceFactory = "+resourceFactory);
		if (resourceFactory == null) {
			resourceFactory = factory;
		}
	} 
	public static RfMessageFactory getMessageFactory() throws ResourceException {
		logger.debug("Inside getMessageFactory()");
		if (messageFactory == null) {
			messageFactory = RfMessageFactoryImpl.getInstance();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	static void setMessageFactory(RfMessageFactory factory) {
		logger.debug("Inside ShMessageFactory() with MessageFactory = "+messageFactory);
		if (messageFactory == null) {
			messageFactory = factory;
		}
	} 
	public static SessionFactory getSessionFactory() throws ResourceException {
		logger.debug("Inside getSessionFactory()");
		if (sessionFactory == null) {
			sessionFactory = new RfSessionFactory();
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
	
	public static RfAppRouter getAppRouter() throws ResourceException {
		logger.debug("Inside getAppRouter()");
		return RfAppRouter.getInstanse();
	}
}