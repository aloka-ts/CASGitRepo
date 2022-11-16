package com.baypackets.ase.ra.enumserver;

import com.baypackets.ase.ra.enumserver.event.EnumResourceEvent;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.resource.ResourceContext;

public interface EnumResourceAdaptor extends ResourceAdaptor{
	
	/**
	 * Method Delivers events supported by RA to application in case of request is failed.
	 * Type of EVENT will be specified in EnumResource Event.
	 *
	 * @param event the event
	 * @throws ResourceException the resource exception
	 */
	public void deliverEvent(EnumResourceEvent event) throws ResourceException;
	
	/**
	 * Gets the resource context.
	 *
	 * @return the resource context
	 */
	public ResourceContext getResourceContext();

	void deliverRequest(EnumRequest request) throws ResourceException;

}
