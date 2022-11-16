package com.baypackets.ase.ra.diameter.sh;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

public interface ShResourceAdaptor extends ResourceAdaptor {
	
	public void deliverRequest(ShRequest request) throws ResourceException;
	
	public void deliverResponse(ShResponse response) throws ResourceException;
	
	public void deliverEvent(ShResourceEvent event) throws ResourceException ;

}
