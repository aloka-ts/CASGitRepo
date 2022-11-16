package com.baypackets.ase.ra.rf;

import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;

/**
 * This interface defines methods to deliver requests, responses and events
 * received from stack to application
 * Resource adaptor will use these methods to submit messaegs to container thread pool
 *
 * @author Neeraj Jadaun
 */


public interface RfResourceAdaptor extends ResourceAdaptor
{

	public void deliverRequest(RfRequest request) throws ResourceException;

	//public void deliverData(Object obj) throws ResourceException;

	public void deliverResponse(RfResponse response) throws ResourceException;

	public void deliverEvent(ResourceEvent event) throws ResourceException ;
}

