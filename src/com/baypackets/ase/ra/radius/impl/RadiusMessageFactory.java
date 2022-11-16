package com.baypackets.ase.ra.radius.impl;

import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

public interface RadiusMessageFactory extends MessageFactory {


	/**
	 * Creates Radius Access Request.
	 * @return Radius Access Request
	 */
	public RadiusAccessRequest createRadiusAccessRequest(SasProtocolSession session)throws ResourceException;
	
	/**
	 * Creates Radius Accounting Request.
	 * @return Radius Accounting Request
	 */	
	public RadiusAccountingRequest createRadiusAccountingRequest(SasProtocolSession session)throws ResourceException;
	
	/**
	 * Creates Radius Access Request.
	 * @return Radius Access Request
	 */
	public RadiusAccessRequest createRadiusAccessRequest(SasProtocolSession session,String username,String userPassword)throws ResourceException;
	
	/**
	 * Creates Radius Accounting Request.
	 * @return Radius Accounting Request
	 */	
	public RadiusAccountingRequest createRadiusAccountingRequest(SasProtocolSession session,String userName,int acctStatusType)throws ResourceException;
	
	
	
	}
