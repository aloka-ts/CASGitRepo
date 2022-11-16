package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.ra.diameter.sh.ShResourceAdaptor;
import com.baypackets.ase.ra.diameter.sh.ShResourceFactory;
import com.baypackets.ase.ra.diameter.sh.rarouter.ShAppRouter;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.SessionFactory;
import org.apache.log4j.Logger;

public class ShResourceAdaptorFactory {
	
	private static final Logger logger = Logger.getLogger(ShResourceAdaptorFactory.class);
	
	private static ShResourceAdaptor resourceAdaptor;
	private static ShResourceFactory resourceFactory;
	private static ShMessageFactory messageFactory;
	private static SessionFactory sessionFactory;
	
	public static ShResourceAdaptor getResourceAdaptor() throws ResourceException {
		logger.debug("Inside getResourceAdaptor()");
		if (resourceAdaptor == null) {
			resourceAdaptor = new ShResourceAdaptorImpl();
			resourceAdaptor.init(null);
		}
		return resourceAdaptor;
	}
	
	static void setResourceAdaptor(ShResourceAdaptor adaptor){
		logger.debug("Inside setResourceAdaptor() with ResourceAdaptor = "+resourceAdaptor);
		if (resourceAdaptor == null) {
			resourceAdaptor = adaptor;
		}
	} 
	
	public static ShResourceFactory getResourceFactory() throws ResourceException {
		logger.debug("Inside getResourceFactory()");
		if (resourceFactory == null) {
			resourceFactory = new ShResourceFactoryImpl();
			((ShResourceFactoryImpl)resourceFactory).init(null);
		}
		return resourceFactory;
	}
	
	static void setResourceFactory(ShResourceFactory factory) {
		logger.debug("Inside setResourceFactory() with ResourceFactory = "+resourceFactory);
		if (resourceFactory == null) {
			resourceFactory = factory;
		}
	} 
	
	public static ShMessageFactory getMessageFactory() throws ResourceException {
		logger.debug("Inside getMessageFactory()");
		if (messageFactory == null) {
			messageFactory = ShMessageFactoryImpl.getInstance();
			messageFactory.init(null);
		}
		return messageFactory;
	}
	
	static void setMessageFactory(ShMessageFactory factory) {
		logger.debug("Inside ShMessageFactory() with MessageFactory = "+messageFactory);
		if (messageFactory == null) {
			messageFactory = factory;
		}
	} 
	
	public static SessionFactory getSessionFactory() throws ResourceException {
		logger.debug("Inside getSessionFactory()");
		if (sessionFactory == null) {
			sessionFactory = new ShSessionFactory();
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
	
	public static ShAppRouter getAppRouter() throws ResourceException {
		logger.debug("Inside getAppRouter()");
		return ShAppRouter.getInstanse();
	}
	
}