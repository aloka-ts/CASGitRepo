package com.baypackets.ase.ra.diameter.gy.impl;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class GySessionFactory implements SessionFactory {
	
	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException {
		this.context = context;
		GyResourceAdaptorFactory.setSessionFactory(this);
	}

	public SasProtocolSession createSession() throws ResourceException {
		//String protocolName = this.context.getProtocol();
		String protocolName = "GY";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new GySession(id);
	}

}
