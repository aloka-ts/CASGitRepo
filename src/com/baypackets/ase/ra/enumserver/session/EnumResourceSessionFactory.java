package com.baypackets.ase.ra.enumserver.session;

import org.apache.log4j.Logger;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class EnumResourceSessionFactory implements SessionFactory {

	private static EnumResourceSessionFactory enumResourceSessionFactory = null;
	private Logger logger = Logger.getLogger(EnumResourceSessionFactory.class);
	
	
	public EnumResourceSessionFactory(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("creating EnumResourceSessionFactory object");
		enumResourceSessionFactory=this;
	}
	@Override
	public SasProtocolSession createSession() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createSession");
		String protocolName = "ENUM";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new EnumResourceSession(id);
	}

	@Override
	public void init(ResourceContext arg0) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		
	}

	public static SessionFactory getInstance() {
		if(enumResourceSessionFactory==null){
			enumResourceSessionFactory = new EnumResourceSessionFactory();
		}
		return enumResourceSessionFactory;
	}

}
