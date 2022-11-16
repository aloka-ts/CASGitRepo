package com.baypackets.ase.ra.diameter.ro;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

public interface RoResourceAdaptor extends ResourceAdaptor {
	
	public void deliverRequest(RoRequest request) throws ResourceException;
	
	public void deliverResponse(RoResponse response) throws ResourceException;
	
	public void deliverEvent(RoResourceEvent event) throws ResourceException ;

}
