package com.baypackets.ase.ra.diameter.gy.impl;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceFactory;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

public class GyResourceFactoryImpl implements GyResourceFactory {

	private static Logger logger = Logger.getLogger(GyResourceFactoryImpl.class);
	private GyMessageFactory msgFactory;
	private SessionFactory sessionFactory;
	private ResourceContext context;

	public void init(ResourceContext context) {
		logger.debug("init() is called.");

		GyResourceAdaptorFactory.setResourceFactory(this);
		this.context = context;

		if (context != null) {

			this.msgFactory = GyMessageFactoryImpl.getInstance();

			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}else {
				GyResourceAdaptorFactory.setMessageFactory(this.msgFactory);
			}

			this.sessionFactory = (SessionFactory)context.getSessionFactory();

			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			} else {
				GyResourceAdaptorFactory.setSessionFactory(this.sessionFactory);
			}

		} else {
			logger.error("init(): null context received");

			try {

				this.msgFactory = (GyMessageFactory) GyResourceAdaptorFactory.getMessageFactory();
				GyResourceAdaptorFactory.setMessageFactory(this.msgFactory);

				this.sessionFactory = GyResourceAdaptorFactory.getSessionFactory();
				GyResourceAdaptorFactory.setSessionFactory(this.sessionFactory);

			} catch (Exception e) {
				logger.error("init(): " + e);
			}
		}
		//		if (context != null) {
		//			logger.debug("init(): get message factory and session factory from context.");
		//			this.msgFactory = context.getMessageFactory();
		//			if (this.msgFactory == null) {
		//				logger.error("init(): null message factory.");
		//			}
		//			this.sessionFactory = (SessionFactory)context.getSessionFactory();
		//			if (this.msgFactory == null) {
		//				logger.error("init(): null session factory.");
		//			}
		//		} else {
		//			try {
		//				this.msgFactory = GyResourceAdaptorFactory.getMessageFactory();
		//				this.sessionFactory = GyResourceAdaptorFactory.getSessionFactory();
		//			} catch (Exception e) {
		//				logger.error("init(): " + e);
		//			}
		//		}
	}

	public ResourceSession createSession(SipApplicationSession appSession)
	throws ResourceException {
		logger.debug("Inside createSession");
		if(appSession == null){
			throw new IllegalArgumentException("Application Session cannot be NULL ");
		}
		SasProtocolSession session = sessionFactory.createSession();
		((SasApplicationSession)appSession).addProtocolSession(session);
		logger.debug("leaving createSession");
		return (ResourceSession)session;
	}
	//@Override
	public Request createRequest(int arg0) throws ResourceException {
		logger.debug("Inside createRequest");
		throw new ResourceException(" not allowed for RF requests");
	}


	//@Override
	public CreditControlRequest createRequest(SipApplicationSession appSession,
			int type) throws ResourceException {
		logger.debug("Entering createProfileUpdateRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");
		CreditControlRequest request = ((GyMessageFactoryImpl)msgFactory).createCreditControlRequest(session,type);
		((SasMessage)request).setInitial(true);
		return request;
	}
}
