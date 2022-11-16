/**
 * 
 */
package com.baypackets.ase.ra.telnetssh;

import java.util.List;

import com.baypackets.ase.ra.telnetssh.configmanager.LsConfigChangeData;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.message.LsResponse;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.resource.ResourceContext;

/**
 * The Interface LsResourceAdaptor
 * Defines API for RA.
 * Resource-Adaptor-Class should implement this interface
 *
 * @author saneja
 */
public interface LsResourceAdaptor extends ResourceAdaptor{
	
	/**
	 * Method Delivers response to application.
	 *
	 * @param lsResponse the ls response
	 * @throws ResourceException the resource exception
	 */
	public void deliverResponse(LsResponse lsResponse) throws ResourceException;
	
	/**
	 * Method is invoked form config manager 
	 * when configuration of LS is changed
	 *
	 * @param lsConfigChangeDataList the ls config change data list
	 */
	public void lsConfigurationChanged(List<LsConfigChangeData> lsConfigChangeDataList);
	
	/**
	 * Method Delivers events supported by RA to application in case of request is failed.
	 * Type of EVENT will be specified in LsResource Event.
	 *
	 * @param event the event
	 * @throws ResourceException the resource exception
	 */
	public void deliverEvent(LsResourceEvent event) throws ResourceException;
	
	/**
	 * Gets the resource context.
	 *
	 * @return the resource context
	 */
	public ResourceContext getResourceContext();
}
