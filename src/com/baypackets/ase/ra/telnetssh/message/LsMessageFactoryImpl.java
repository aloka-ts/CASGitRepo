/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.LsResourceAdaptorImpl;
import com.baypackets.ase.ra.telnetssh.session.LsResourceSession;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

/**
 * The Class LsMessageFactoryImpl.
 * Implements LsMessageFactory
 *
 * @author saneja
 */
public class LsMessageFactoryImpl implements LsMessageFactory, Constants{

	/** The logger. */
	private static Logger logger = Logger.getLogger(LsMessageFactoryImpl.class);

	/** The msg factory. */
	private MessageFactory msgFactory;
	
	/** The ls message factory. */
	private static LsMessageFactoryImpl lsMessageFactory;

	/** The ls resource adaptor. */
	private LsResourceAdaptor lsResourceAdaptor;
	
	/**
	 *	Default constructor for creating LsMessageFactory object.
	 *
	 */
	public LsMessageFactoryImpl(){
		if(logger.isDebugEnabled())
			logger.debug("creating LsMessageFactory object");
		lsMessageFactory=this;
	}

	/**
	 *	This  method returns the instance of LsMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static LsMessageFactory getInstance(){
		if(lsMessageFactory==null){
			lsMessageFactory = new LsMessageFactoryImpl();
		}
		return lsMessageFactory;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.MessageFactory#init(com.baypackets.ase.spi.resource.ResourceContext)
	 */
	@Override
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside init(ResourceContext) No impl required");

//		this.context = context;
		
		
		try {
			this.lsResourceAdaptor=LsResourceAdaptorImpl.getInstance();
		} catch (Exception e) {
			throw new ResourceException(e);
		}
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}

	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.MessageFactory#createRequest(com.baypackets.ase.spi.container.SasProtocolSession, int)
	 */
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type)
	throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createRequest(session,type)");
		LsRequestImpl message = null;
		switch (type) {
		case EXECUTE:
			if(isDebugEnabled)
				logger.debug("Creating EXECUTE");
//			message = new LsRequestImpl((LsResourceSession)session);
			message=new LsRequestImpl();
			message.setProtocolSession(session);
			message.setLsResourceAdaptor(lsResourceAdaptor);
			((LsResourceSession)session).setRequest(message);
			message.setType(EXECUTE);
			break;
		default:
			if(isDebugEnabled)
				logger.debug("Wrong/Unkown request type.");
			throw new ResourceException("Wrong/Unkown request type.");
		}
		if(isDebugEnabled)
			logger.debug("leaving createRequest():");
		return message;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.MessageFactory#createResponse(com.baypackets.ase.spi.container.SasMessage, int)
	 */
	@Override
	public SasMessage createResponse(SasMessage arg0, int arg1)
	throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createResponse(session,type)");
		throw new ResourceException("createResponse() Not Supported, use LsRequest.createResponse()");
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm, String msisdn) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
