package com.baypackets.ase.ra.diameter.ro.impl;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.stackif.CreditControlRequestImpl;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceFactory;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackClientInterfaceImpl;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.ro.utils.RoStackConfig;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

import fr.marben.diameter.DiameterException;
import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter.DiameterSession;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public class RoResourceFactoryImpl implements RoResourceFactory {

	private static Logger logger = Logger.getLogger(RoResourceFactoryImpl.class);
	private RoMessageFactory msgFactory;
	private SessionFactory sessionFactory;
	private ResourceContext context;

	public void init(ResourceContext context) {
		logger.debug("init() is called.");

		RoResourceAdaptorFactory.setResourceFactory(this);
		this.context = context;

		if (context != null) {

			this.msgFactory = RoMessageFactoryImpl.getInstance();

			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}else {
				RoResourceAdaptorFactory.setMessageFactory(this.msgFactory);
			}

			this.sessionFactory = (SessionFactory)context.getSessionFactory();

			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			} else {
				RoResourceAdaptorFactory.setSessionFactory(this.sessionFactory);
			}

		} else {
			logger.error("init(): null context received");

			try {

				this.msgFactory = (RoMessageFactory) RoResourceAdaptorFactory.getMessageFactory();
				RoResourceAdaptorFactory.setMessageFactory(this.msgFactory);

				this.sessionFactory = RoResourceAdaptorFactory.getSessionFactory();
				RoResourceAdaptorFactory.setSessionFactory(this.sessionFactory);

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
		//				this.msgFactory = RoResourceAdaptorFactory.getMessageFactory();
		//				this.sessionFactory = RoResourceAdaptorFactory.getSessionFactory();
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


	@Override
	public CreditControlRequest createRequest(SipApplicationSession appSession,
			int type,String remoteRealm) throws ResourceException {
		logger.debug("Entering createRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		
		String sessionId=null;
	
		DiameterSession stackSession=null;
		if (!RoStackConfig.isStateless()) {
			logger.debug("create session for stateful request");
			stackSession = RoStackClientInterfaceImpl.roProvider
					.createClientDiameterAcctSession(null);
			// stackSession.ssetPerformFailover(true);
			((RoSession) session).setClientStackSession(stackSession);

			sessionId = stackSession.getSessionId();
		} else {
          logger.debug("create session id for request for stateless request");
			sessionId = RoStackClientInterfaceImpl.roProvider
					.getNextSessionIdValue();
			((RoSession) session).setClientStackSessionId(sessionId);
		}
		
		logger.info("Creating Request");
		CreditControlRequest request = ((RoMessageFactoryImpl)msgFactory).createCreditControlRequest(session,type,remoteRealm);
		((SasMessage)request).setInitial(true);
		
		((CreditControlRequestImpl) request).setSessionIDAVP(sessionId);
		
		return request;
	}
	
	/**
	 * This method should be used for intial/update/terminate
	 * update and terminate should have existing session id
	 */
	@Override
	public CreditControlRequest createRequest(SipApplicationSession appSession,
			String sessionId, int type, String remoteRealm)
			throws ResourceException {
		if(logger.isDebugEnabled()){
		logger.debug("Entering createRequest.");
		}
		SasProtocolSession session = null;
		if (sessionId != null) {
			CreditControlRequestImpl ccr = (CreditControlRequestImpl) ((RoStackInterfaceImpl) RoResourceAdaptorImpl.stackInterface)
					.getInitialOrEventRequest(sessionId);
			if (ccr != null) {
				session = (SasProtocolSession) ccr.getSession();
			}
			if(logger.isDebugEnabled()){
			logger.debug("RoSession found for this request is " + session);
			}
		}
		if (session == null && appSession != null) {

			if(logger.isDebugEnabled()){
				logger.debug("create new RO Session as existing session not found");
				}
			session = sessionFactory.createSession();
			((SasApplicationSession) appSession).addProtocolSession(session);

		}
		if (logger.isInfoEnabled()) {
			logger.info("Creating Request");
		}
		DiameterSession stackSession = null;
		
		if (!RoStackConfig.isStateless() && ((RoSession)session).getClientStackSession()==null) { //means there is not existing Ro sesssion found above
			stackSession = RoStackClientInterfaceImpl.roProvider
					.createClientDiameterAcctSession(null);
			// stackSession.ssetPerformFailover(true);
			((RoSession) session).setClientStackSession(stackSession);

			sessionId = stackSession.getSessionId();

		} else {
			((RoSession) session).setClientStackSessionId(sessionId);
			if (logger.isInfoEnabled()) {
				logger.info("Donot create stack session for stateless");
			}
		}
		CreditControlRequest request = ((RoMessageFactoryImpl) msgFactory)
				.createCreditControlRequest(session, type, remoteRealm);

		if (sessionId == null) {
			logger.debug("create session id for request for stateless request");
			sessionId = RoStackClientInterfaceImpl.roProvider
					.getNextSessionIdValue();
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("setSessionIDAVP " + sessionId);
		}
		((CreditControlRequestImpl) request).setSessionIDAVP(sessionId);

		((SasMessage) request).setInitial(true);
		return request;
	}

	@Override
	public CreditControlRequest createRequest(SipApplicationSession appSession,
			int type) throws ResourceException {
		logger.debug("Entering createRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		logger.info("Creating Request");
		DiameterSession stackSession=null;
		stackSession=RoStackClientInterfaceImpl.roProvider.createClientDiameterAcctSession(null);
		//stackSession.ssetPerformFailover(true);
		((RoSession) session).setClientStackSession(stackSession);
		CreditControlRequest request = ((RoMessageFactoryImpl)msgFactory).createCreditControlRequest(session,type);
		((SasMessage)request).setInitial(true);
		return request;
	}
	
	@Override
	public DiameterRoMessageFactory getDiameterRoMessageFactory(){
		return msgFactory.getDiameterRoMessageFactory();
	}
	
	@Override
	public DiameterMessageFactory getDiameterBaseMessageFactory(){
		return ((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();
	}
}


