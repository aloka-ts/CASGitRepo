package com.baypackets.ase.ra.diameter.ro;

import com.baypackets.ase.spi.resource.ResourceContext;

public interface RoStackInterface {
	
	public void init(ResourceContext context) throws RoResourceException;
	
	public void init(String configFileName) throws RoResourceException;
	
	public void startStack() throws RoResourceException;
	
	public void stopStack() throws RoResourceException;
	
	public void handleRequest(RoRequest request) throws RoResourceException;
	
	public void handleResponse(RoResponse response) throws RoResourceException;

}
