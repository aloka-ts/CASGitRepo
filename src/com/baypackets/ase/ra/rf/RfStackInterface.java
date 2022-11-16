package com.baypackets.ase.ra.rf;

import com.baypackets.ase.spi.resource.ResourceContext;

public interface RfStackInterface
{
	/** 
	 * This interface provides methods to manage diameter stack. This interface provides methods to initialize, 
	 * start , stop.
	 * This interface also provides methods to send diameter messsages originated by application
	 * to diameter stack, which inturn send these messaeges to remote server.
	 *
	 */
	public void init(ResourceContext context) throws RfResourceException;

	public void init(String configFileName) throws RfResourceException;

	public void start() throws RfResourceException;

	public void stop() throws RfResourceException;

	public void handleRequest(RfRequest request) throws RfResourceException;

	public void handleResponse(RfResponse response) throws RfResourceException;
}
