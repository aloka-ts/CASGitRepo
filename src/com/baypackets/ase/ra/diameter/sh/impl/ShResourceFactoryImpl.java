package com.baypackets.ase.ra.diameter.sh.impl;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.ra.diameter.sh.stackif.ShStackClientInterfaceImpl;
import com.baypackets.ase.ra.diameter.sh.stackif.ShUserDataRequestImpl;
import com.baypackets.ase.ra.diameter.sh.utils.ShStackConfig;
import fr.marben.diameter.DiameterSession;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.sh.ShProfileUpdateRequest;
import com.baypackets.ase.ra.diameter.sh.ShResourceFactory;
import com.baypackets.ase.ra.diameter.sh.ShSubscribeNotificationRequest;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

public class ShResourceFactoryImpl implements ShResourceFactory {

	private static final Logger logger = Logger.getLogger(ShResourceFactoryImpl.class);
	private ShMessageFactory msgFactory;
	private SessionFactory sessionFactory;
	private ResourceContext context;

	public void init(ResourceContext context) {
		logger.debug("init() is called.");

		logger.debug("Setting ResourceFactory in  ShResourceAdaptorFactory");
		ShResourceAdaptorFactory.setResourceFactory(this);

		this.context = context;
		if (context != null) {
			logger.debug("init(): get message factory from context.");
			this.msgFactory = ShMessageFactoryImpl.getInstance();
			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}else {
				logger.debug("Setting Message factory in  ShResourceAdaptorFactory");
				ShResourceAdaptorFactory.setMessageFactory(this.msgFactory);
			}
			logger.debug("init(): get session factory from context.");
			this.sessionFactory = context.getSessionFactory();
			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			} else {
				logger.debug("Setting session factory in  ShResourceAdaptorFactory");
				ShResourceAdaptorFactory.setSessionFactory(this.sessionFactory);
			}
		} else {
			logger.error("init(): null context received");
			try {
				this.msgFactory = ShResourceAdaptorFactory.getMessageFactory();
				logger.debug("Setting Message factory in  ShResourceAdaptorFactory");
				ShResourceAdaptorFactory.setMessageFactory(this.msgFactory);

				this.sessionFactory = ShResourceAdaptorFactory.getSessionFactory();
				logger.debug("Setting session factory in  ShResourceAdaptorFactory");
				ShResourceAdaptorFactory.setSessionFactory(this.sessionFactory);

			} catch (Exception e) {
				logger.error("init(): " ,e);
			}
		}
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

	@Override
	public Request createRequest(int arg0) throws ResourceException {
		logger.debug("Inside createRequest");
		throw new ResourceException(" not allowed for SH requests");
	}

	@Override
	public ShProfileUpdateRequest createProfileUpdateRequest(SipApplicationSession appSession) throws ResourceException {
		logger.debug("Entering createProfileUpdateRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");
		ShProfileUpdateRequest request = msgFactory.createProfileUpdateRequest(session);
		((SasMessage)request).setInitial(true);
		return request;
	}

	@Override
	public ShSubscribeNotificationRequest createSubscribeNotificationsRequest(SipApplicationSession appSession) throws ResourceException {
		logger.debug("Inside createSubscribeNotificationsRequest");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");
		ShSubscribeNotificationRequest request = msgFactory.createSubscribeNotificationsRequest(session);
		((SasMessage)request).setInitial(true);
		return request;
	}

	@Override
	public ShUserDataRequest createUserDataRequest(SipApplicationSession appSession,String realm,String msisdn) throws ResourceException {
		logger.debug("Inside createUserDataRequest");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");

		DiameterSession stackSession = null;

		String sessionId=session.getId(); // for testing
		if (!ShStackConfig.isStateless() && ((ShSession)session).getClientStackSession()==null) { //means there is not existing Ro sesssion found above
			stackSession = ShStackClientInterfaceImpl.shProvider
					.createClientDiameterSession(sessionId);
			if (logger.isInfoEnabled()) {
				logger.info("create stack session for statelfull"+stackSession);
			}
			// stackSession.ssetPerformFailover(true);
			((ShSession) session).setClientStackSession(stackSession);

			sessionId = stackSession.getSessionId();

		} else {
			((ShSession) session).setClientStackSessionId(sessionId);
			if (logger.isInfoEnabled()) {
				logger.info("Donot create stack session for stateless");
			}
		}
		ShUserDataRequest request = msgFactory.createUserDataRequest(session,realm,msisdn);

		if (sessionId == null) {
			logger.debug("create session id for request for stateless request");
			sessionId = ShStackClientInterfaceImpl.shProvider
					.getNextSessionIdValue();
		}

		if (logger.isInfoEnabled()) {
			logger.info("setSessionIDAVP " + sessionId);
		}
		((ShUserDataRequestImpl) request).setSessionIDAVP(sessionId);

		((SasMessage)request).setInitial(true);
		return request;
	}
}
