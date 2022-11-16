package com.baypackets.ase.ra.diameter.rf;

import com.baypackets.ase.spi.resource.ResourceContext;

public interface RfStackInterface {
	
	public void init(ResourceContext context) throws RfResourceException;
	
	public void init(String configFileName) throws RfResourceException;
	
	public void start() throws RfResourceException;
	
	public void stop() throws RfResourceException;
	
	public void handleRequest(RfRequest request) throws RfResourceException;
	
	public void handleResponse(RfResponse response) throws RfResourceException;

}
