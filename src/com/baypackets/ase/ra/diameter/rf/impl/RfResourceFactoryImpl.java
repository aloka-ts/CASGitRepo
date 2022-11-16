package com.baypackets.ase.ra.diameter.rf.impl;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfResourceFactory;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

public class RfResourceFactoryImpl implements RfResourceFactory {

	private static Logger logger = Logger.getLogger(RfResourceFactoryImpl.class);
	private RfMessageFactory msgFactory;
	private SessionFactory sessionFactory;
	private ResourceContext context;

	public void init(ResourceContext context) {
		logger.debug("init() is called.");
		logger.debug("Setting ResourceFactory in  RfResourceAdaptorFactory");
		RfResourceAdaptorFactory.setResourceFactory(this);
		this.context = context;
		if (context != null) {
			logger.debug("init(): get message factory from context.");
			this.msgFactory = RfMessageFactoryImpl.getInstance();
			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}else {
				logger.debug("Setting Message factory in  RfResourceAdaptorFactory");
				RfResourceAdaptorFactory.setMessageFactory(this.msgFactory);
			}
			logger.debug("init(): get session factory from context.");
			this.sessionFactory = (SessionFactory)context.getSessionFactory();
			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			} else {
				logger.debug("Setting session factory in  RfResourceAdaptorFactory");
				RfResourceAdaptorFactory.setSessionFactory(this.sessionFactory);
			}
		} else {
			logger.error("init(): null context received");
			try {
				this.msgFactory = (RfMessageFactory) RfResourceAdaptorFactory.getMessageFactory();
				logger.debug("Setting Message factory in  RfResourceAdaptorFactory");
				RfResourceAdaptorFactory.setMessageFactory(this.msgFactory);

				this.sessionFactory = RfResourceAdaptorFactory.getSessionFactory();
				logger.debug("Setting session factory in  RfResourceAdaptorFactory");
				RfResourceAdaptorFactory.setSessionFactory(this.sessionFactory);

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
		//				this.msgFactory = RfResourceAdaptorFactory.getMessageFactory();
		//				this.sessionFactory = RfResourceAdaptorFactory.getSessionFactory();
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
	public RfAccountingRequest createRequest(SipApplicationSession appSession,
			int type) throws ResourceException {
		logger.debug("Entering createProfileUpdateRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");
		RfAccountingRequest request = ((RfMessageFactoryImpl)msgFactory).createAccountingRequest(session,type);
		((SasMessage)request).setInitial(true);
		return request;
	}
}
