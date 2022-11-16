package com.baypackets.ase.spi.resource;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;

/**
 * MessageFactory interface will be used by the container to
 * Create Messages. The Resource Adaptor will implement this 
 * interface and provide the name of the implementation class 
 * to the container. 
 * 
 * The Container will instantiate and wrap it with a proxy
 * implementation and make it available to the resource-adaptor using the
 * <code>ResourceContext</code> interface.
 */
public interface MessageFactory {

	public void init(ResourceContext context) throws ResourceException;
	
	public SasMessage createRequest(SasProtocolSession session, int type) throws ResourceException;
	
	public SasMessage createResponse(SasMessage request, int type) throws ResourceException;
	
	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm) throws ResourceException;
	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm,String msisdn) throws ResourceException;

//	SasMessage createRequest(SasProtocolSession session, int type,
//			byte[] message) throws ResourceException;
}
