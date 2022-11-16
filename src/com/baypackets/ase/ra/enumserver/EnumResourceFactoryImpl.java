package com.baypackets.ase.ra.enumserver;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;
import org.xbill.DNS.RRset;

import com.baypackets.ase.ra.enumserver.message.EnumResponse;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumRequestImpl;
import com.baypackets.ase.ra.enumserver.message.EnumMessageFactory;
import com.baypackets.ase.ra.enumserver.message.EnumMessageFactoryImpl;
import com.baypackets.ase.ra.enumserver.message.EnumResponseImpl;
import com.baypackets.ase.ra.enumserver.rarouter.EnumAppRouter;
import com.baypackets.ase.ra.enumserver.session.EnumResourceSessionFactory;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.ra.enumserver.utils.Constants;

public class EnumResourceFactoryImpl implements EnumResourceFactory,Constants{

	public static Logger logger = Logger.getLogger(EnumResourceFactoryImpl.class);
	/** The msg factory. */
	private EnumMessageFactory msgFactory;

	/** The session factory. */
	private SessionFactory sessionFactory;

	private ResourceContext context;
	
	public void init(ResourceContext context) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside EnumResourceFactoryImpl init().");
		this.context=context;
		if (context != null) {
			logger.error("EnumResourceFactory init(): context received");
			if(isDebugEnabled)
				logger.debug("init(): get session factory" +this.context.getSessionFactory());
			this.sessionFactory = (SessionFactory)this.context.getSessionFactory();
			if (this.sessionFactory== null)
				logger.warn("EnumResourceFactory init(): null SessionFactory from context");
				this.sessionFactory = EnumResourceSessionFactory.getInstance();
			if(isDebugEnabled)	
				logger.debug("init(): get message factory");
			if (this.msgFactory == null)
				logger.warn("EnumResourceFactory init(): null MessageFactory from context");
				this.msgFactory = EnumMessageFactoryImpl.getInstance();
		}else {
			logger.error("EnumResourceFactory init(): null context received");
			if(isDebugEnabled)
				logger.debug("init(): get message factory");
			this.msgFactory = EnumMessageFactoryImpl.getInstance();
			if(isDebugEnabled)
				logger.debug("init(): get session factory");
			this.sessionFactory = EnumResourceSessionFactory.getInstance();
		}
	}
	@Override
	public EnumRequest createRequest(byte[] message) throws ResourceException {

		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering EnumResourceFactoryImpl createRequest(appSession)");
		SasProtocolSession session = null;
		EnumRequestImpl request=null;
			session = sessionFactory.createSession();
			if(isDebugEnabled)
				logger.debug("Creating Request");
			request = (EnumRequestImpl) ((EnumMessageFactoryImpl)msgFactory).createRequest(session, RECEIVE, message);
			((SasMessage)request).setInitial(false);
		return request;
	
	}

	@Override
	public Request createRequest(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
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
	public EnumResponse createResponse(SipApplicationSession appSession,int messageId, RRset[] records,EnumRequest request)
			throws ResourceException {
		// TODO Auto-generated method stub
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering EnumResourceFactoryImpl createRequest(appSession)");
		SasProtocolSession session = null;
		EnumResponseImpl response=null;
		if (appSession != null){
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
			if(isDebugEnabled)
				logger.debug("Creating response");
			response = (EnumResponseImpl) ((EnumMessageFactoryImpl)msgFactory).createResponse(session, SEND,request,records);
			
			((SasMessage)request).setInitial(false);
		}else{
			throw new ResourceException("AppSession cannot be null");
		}
		return response;
	
	}
	@Override
	public EnumAppRouter getAppRouter() {
		// TODO Auto-generated method stub
		return EnumAppRouter.getInstanse();
	}

}
