package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class ShSessionFactory implements SessionFactory {
	
	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException {
		this.context = context;
		ShResourceAdaptorFactory.setSessionFactory(this);
	}

	public SasProtocolSession createSession() throws ResourceException {
		//String protocolName = this.context.getProtocol();
		String protocolName = "SH";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new ShSession(id);
	}

}
