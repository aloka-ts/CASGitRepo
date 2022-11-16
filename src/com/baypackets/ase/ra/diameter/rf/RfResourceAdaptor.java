package com.baypackets.ase.ra.diameter.rf;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

public interface RfResourceAdaptor extends ResourceAdaptor {
	
	public void deliverRequest(RfRequest request) throws ResourceException;
	
	public void deliverResponse(RfResponse response) throws ResourceException;
	
	public void deliverEvent(RfResourceEvent event) throws ResourceException ;

}
