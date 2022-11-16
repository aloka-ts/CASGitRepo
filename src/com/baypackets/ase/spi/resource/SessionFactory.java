package com.baypackets.ase.spi.resource;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;

/**
 * SessionFactory interface will be used by the container to
 * Create Protocol Sessions. The Resource Adaptor will implement this 
 * interface and provide the name of the implementation class 
 * to the container. 
 * 
 * The Container will instantiate and wrap it with a proxy
 * implementation and make it available to the resource-adaptor using the
 * <code>ResourceContext</code> interface.
 */
public interface SessionFactory {

	public void init(ResourceContext context) throws ResourceException;
	
	public SasProtocolSession createSession() throws ResourceException;
}
