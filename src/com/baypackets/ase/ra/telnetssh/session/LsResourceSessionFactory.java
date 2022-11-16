/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.session;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.message.LsMessageFactoryImpl;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

/**
 * A factory for creating LsResourceSession objects.
 * Implements SessionFactory
 *
 * @author saneja
 */
public class LsResourceSessionFactory implements SessionFactory{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(LsMessageFactoryImpl.class);
//	private SessionFactory sessionFactory;
	/** The ls resource session factory. */
private static LsResourceSessionFactory lsResourceSessionFactory;
//	private ResourceContext context;

	/**
	 *	Default constructor for creating LsMessageFactory object.
	 *
	 */
	public LsResourceSessionFactory(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("creating LsResourceSessionFactory object");
		lsResourceSessionFactory=this;
	}

	/**
	 *	This  method returns the instance of LsMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static LsResourceSessionFactory getInstance(){
		if(lsResourceSessionFactory==null){
			lsResourceSessionFactory = new LsResourceSessionFactory();
		}
		return lsResourceSessionFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.MessageFactory#init(com.baypackets.ase.spi.resource.ResourceContext)
	 */
	@Override
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.SessionFactory#createSession()
	 */
	@Override
	public SasProtocolSession createSession() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createSession");
		String protocolName = "TELNET_SSH";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new LsResourceSession(id);
	}

}
