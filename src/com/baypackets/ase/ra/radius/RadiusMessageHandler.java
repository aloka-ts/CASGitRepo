package com.baypackets.ase.ra.radius;

import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceException;
public interface RadiusMessageHandler extends MessageHandler {
	
	/**
	 * This method provides callback of RADIUS Access Requests for Applications.
	 * @param request
	 * @throws ResourceException
	 */
	public void handleRadiusAccessRequest(RadiusAccessRequest request)throws ResourceException;
	
	/**
	 * This method provides callback of RADIUS Accounting Requests for Applications.
	 * @param request
	 * @throws ResourceException
	 */
	public void handleRadiusAccountingRequest(RadiusAccountingRequest request)throws ResourceException;
}
