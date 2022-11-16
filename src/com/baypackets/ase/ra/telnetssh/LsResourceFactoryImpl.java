/**
 * 
 */
package com.baypackets.ase.ra.telnetssh;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.message.LsMessageFactory;
import com.baypackets.ase.ra.telnetssh.message.LsMessageFactoryImpl;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.session.LsResourceSessionFactory;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;

/**
 * The Class LsResourceFactoryImpl.
 * Implemnting class for LsResourceFactory
 *
 * @author saneja
 */
public class LsResourceFactoryImpl implements LsResourceFactory, Constants {

	/** The logger. */
	private static Logger logger = Logger.getLogger(LsResourceFactoryImpl.class);

	/** The msg factory. */
	private LsMessageFactory msgFactory;

	/** The session factory. */
	private SessionFactory sessionFactory;

	private ResourceContext context;

	/**
	 * Initializes the resourceFactory.
	 *
	 * @param context the context
	 */
	public void init(ResourceContext context) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside LsResourceFactoryImpl init().");
		this.context=context;
		if (context != null) {
			logger.error("LsResourceFactory init(): context received");
			if(isDebugEnabled)
				logger.debug("init(): get session factory");
			this.sessionFactory = (SessionFactory)this.context.getSessionFactory();
			if (this.sessionFactory== null)
				logger.warn("LsResourceFactory init(): null SessionFactory from context");
				this.sessionFactory = LsResourceSessionFactory.getInstance();
			if(isDebugEnabled)	
				logger.debug("init(): get message factory");
//			this.msgFactory = (LsMessageFactory)this.context.getMessageFactory();
			if (this.msgFactory == null)
				logger.warn("LsResourceFactory init(): null MessageFactory from context");
				this.msgFactory = LsMessageFactoryImpl.getInstance();
		}else {
			logger.error("LsResourceFactory init(): null context received");
			if(isDebugEnabled)
				logger.debug("init(): get message factory");
			this.msgFactory = LsMessageFactoryImpl.getInstance();
			if(isDebugEnabled)
				logger.debug("init(): get session factory");
			this.sessionFactory = LsResourceSessionFactory.getInstance();
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.DefaultResourceFactory#createSession(javax.servlet.sip.SipApplicationSession)
	 */
	@Override
	public ResourceSession createSession(SipApplicationSession appSession)
	throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createSession");

		throw new ResourceException("Direct creation of Session not allowed. For implicit session creation " +
				"Use createRequest(appSession,lsId, lsCommand).");

	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.TelnetSshResourceFactory#createRequest(int, javax.servlet.sip.SipApplicationSession)
	 */
	@Override
	public LsRequest createRequest(SipApplicationSession appSession, int lsId, String lsCommand)
	throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceFactoryImpl createRequest(appSession)");
		SasProtocolSession session = null;
		LsRequestImpl request=null;
		if (appSession != null){
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
			if(isDebugEnabled)
				logger.debug("Creating Request");
			request = (LsRequestImpl) ((LsMessageFactoryImpl)msgFactory).createRequest(session, EXECUTE);
			request.setLsId(lsId);
			request.setLsCommand(lsCommand);
			((SasMessage)request).setInitial(true);
		}else{
			throw new ResourceException("ApSession cannot be null");
		}
		return request;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.DefaultResourceFactory#createRequest(int)
	 */
	@Override
	public Request createRequest(int type) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createRequest(type)");
		throw new ResourceException("API not supported. Use createRequest(appSession,lsId,lsCommand)");
	}


}
