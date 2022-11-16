package com.baypackets.ase.ra.http.session;

import org.apache.log4j.Logger;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class HttpResourceSessionFactory implements SessionFactory {

	private static HttpResourceSessionFactory httpResourceSessionFactory = null;
	private Logger logger = Logger.getLogger(HttpResourceSessionFactory.class);
	
	
	public HttpResourceSessionFactory(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("creating HttpResourceSessionFactory object");
		httpResourceSessionFactory=this;
	}
	@Override
	public SasProtocolSession createSession() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createSession");
		String protocolName = "HTTP";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new HttpResourceSession(id);
	}

	@Override
	public void init(ResourceContext arg0) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		
	}

	public static SessionFactory getInstance() {
		if(httpResourceSessionFactory==null){
			httpResourceSessionFactory = new HttpResourceSessionFactory();
		}
		return httpResourceSessionFactory;
	}

}
