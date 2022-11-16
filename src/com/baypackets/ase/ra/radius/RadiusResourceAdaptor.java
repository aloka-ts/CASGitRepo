package com.baypackets.ase.ra.radius;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

public interface RadiusResourceAdaptor extends ResourceAdaptor {

	public void deliverRequest(RadiusRequest request) throws ResourceException;
	
	public void deliverResponse(RadiusResponse response) throws ResourceException;
	
	public void deliverEvent(RadiusResourceEvent event) throws ResourceException ;
}
