package com.baypackets.ase.ra.http;

import com.baypackets.ase.ra.http.event.HttpResourceEvent;
import com.baypackets.ase.ra.http.message.HttpResponse;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.resource.ResourceContext;

public interface HttpResourceAdaptor extends ResourceAdaptor{
	
	/**
	 * Method Delivers response to application.
	 *
	 * @param EnumResponse the http response
	 * @throws ResourceException the resource exception
	 */
	public void deliverResponse(HttpResponse httpResponse) throws ResourceException;
	
	
	
	/**
	 * Method Delivers events supported by RA to application in case of request is failed.
	 * Type of EVENT will be specified in HttpResource Event.
	 *
	 * @param event the event
	 * @throws ResourceException the resource exception
	 */
	public void deliverEvent(HttpResourceEvent event) throws ResourceException;
	
	/**
	 * Gets the resource context.
	 *
	 * @return the resource context
	 */
	public ResourceContext getResourceContext();

}
