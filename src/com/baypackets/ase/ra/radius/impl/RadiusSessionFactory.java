package com.baypackets.ase.ra.radius.impl;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;
public class RadiusSessionFactory implements SessionFactory {

	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException {
		this.setContext(context);
		RadiusResourceAdaptorFactory.setSessionFactory(this);
	}

	public SasProtocolSession createSession() throws ResourceException {
		//String protocolName = this.context.getProtocol();
		String protocolName = "RADIUS";
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		return new RadiusSession(id);
		
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
