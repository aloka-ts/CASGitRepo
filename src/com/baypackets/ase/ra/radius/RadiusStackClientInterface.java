package com.baypackets.ase.ra.radius;

import com.baypackets.ase.spi.resource.ResourceContext;

public interface RadiusStackClientInterface {
	public void init(ResourceContext context) throws RadiusResourceException;

	public void init(String configFileName) throws RadiusResourceException;

	public void start() throws RadiusResourceException;

	public void stop() throws RadiusResourceException;

	public void handleRequest(RadiusRequest request) throws RadiusResourceException;

	public void handleResponse(RadiusResponse response) throws RadiusResourceException;
	
}
