package com.baypackets.ase.ra.rf.impl;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class RfSessionFactory implements SessionFactory 
{
	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException 
	{
		this.context = context;

	}

	public SasProtocolSession createSession() throws ResourceException 
	{
		String protocolName = this.context.getProtocol();
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new RfSession(id);
	}

}
