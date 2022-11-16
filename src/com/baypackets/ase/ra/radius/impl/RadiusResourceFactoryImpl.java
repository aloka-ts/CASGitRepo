package com.baypackets.ase.ra.radius.impl;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.AseRadiusClient;
import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.RadiusResourceFactory;
import com.baypackets.ase.ra.radius.stackif.AseRadiusClientImpl;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

public class RadiusResourceFactoryImpl implements RadiusResourceFactory {
	private static Logger logger = Logger.getLogger(RadiusResourceFactoryImpl.class);
	private RadiusMessageFactory msgFactory;
	private SessionFactory sessionFactory;
	private ResourceContext context;
	
	
	public void init(ResourceContext context) {
		if(logger.isDebugEnabled())
			logger.debug("init() is called.");

		RadiusResourceAdaptorFactory.setResourceFactory(this);
		this.setContext(context);

		if (context != null) {

			this.msgFactory = RadiusMessageFactoryImpl.getInstance();

			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}else {
				RadiusResourceAdaptorFactory.setMessageFactory(this.msgFactory);
			}

			this.sessionFactory = (SessionFactory)context.getSessionFactory();

			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			} else {
				RadiusResourceAdaptorFactory.setSessionFactory(this.sessionFactory);
			}

		} else {
			logger.error("init(): null context received");

			try {

				this.msgFactory = (RadiusMessageFactory) RadiusResourceAdaptorFactory.getMessageFactory();
				RadiusResourceAdaptorFactory.setMessageFactory(this.msgFactory);

				this.sessionFactory = RadiusResourceAdaptorFactory.getSessionFactory();
				RadiusResourceAdaptorFactory.setSessionFactory(this.sessionFactory);

			} catch (Exception e) {
				logger.error("init(): " + e);
			}
		}
	}
	
	@Override
	public RadiusAccessRequest createRadiusAccessRequest(
			SipApplicationSession appSession) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Entering createRadiusAccessRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		if(logger.isDebugEnabled())
			logger.debug("Creating Request");
		RadiusAccessRequest request = ((RadiusMessageFactoryImpl)msgFactory).createRadiusAccessRequest(session);
		((SasMessage)request).setInitial(true);
		return request;
	}

	@Override
	public RadiusAccountingRequest createRadiusAccountingRequest(SipApplicationSession appSession) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Entering createRadiusAccountingRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		if(logger.isDebugEnabled())
			logger.info("Creating Request");
		RadiusAccountingRequest request = ((RadiusMessageFactoryImpl)msgFactory).createRadiusAccountingRequest(session);
		((SasMessage)request).setInitial(true);
		return request;
	}

	@Override
	public Request createRequest(int arg0) throws ResourceException {
		logger.debug("Inside createRequest");
		throw new ResourceException(" not allowed for Radius requests");
	}

	@Override
	public ResourceSession createSession(SipApplicationSession appSession)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createSession");
		if(appSession == null){
			throw new IllegalArgumentException("Application Session cannot be NULL ");
		}
		SasProtocolSession session = sessionFactory.createSession();
		((SasApplicationSession)appSession).addProtocolSession(session);
		if(logger.isDebugEnabled())
			logger.debug("leaving createSession");
		return (ResourceSession)session;
	}

	@Override
	public AseRadiusClient createAseRadiusClient(String hostName,
			String sharedSecret) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createAseRadiusClient");
		if(hostName == null){
			throw new IllegalArgumentException("HostName cannot be NULL ");
		}
		if(sharedSecret == null){
			throw new IllegalArgumentException("sharedSecret cannot be NULL ");
		}
		return new AseRadiusClientImpl(hostName, sharedSecret);
	}

	@Override
	public RadiusAccessRequest createRadiusAccessRequest(
			SipApplicationSession appSession, String userName, String userPassword)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Entering createRadiusAccessRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		if(logger.isDebugEnabled())
			logger.debug("Creating Request");
		RadiusAccessRequest request = ((RadiusMessageFactoryImpl)msgFactory).createRadiusAccessRequest(session,userName,userPassword);
		((SasMessage)request).setInitial(true);
		return request;
	}

	@Override
	public RadiusAccountingRequest createRadiusAccountingRequest(
			SipApplicationSession appSession, String userName, int acctStatusType)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Entering createRadiusAccountingRequest.");
		SasProtocolSession session = null;
		if (appSession != null) 
		{
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		if(logger.isDebugEnabled())
			logger.info("Creating Request");
		RadiusAccountingRequest request = ((RadiusMessageFactoryImpl)msgFactory).createRadiusAccountingRequest(session,userName,acctStatusType);
		((SasMessage)request).setInitial(true);
		return request;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(ResourceContext context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public ResourceContext getContext() {
		return context;
	}

}
