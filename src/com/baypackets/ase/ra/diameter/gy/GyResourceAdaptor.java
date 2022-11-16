package com.baypackets.ase.ra.diameter.gy;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

public interface GyResourceAdaptor extends ResourceAdaptor {
	
	public void deliverRequest(GyRequest request) throws ResourceException;
	
	public void deliverResponse(GyResponse response) throws ResourceException;
	
	public void deliverEvent(GyResourceEvent event) throws ResourceException ;

}