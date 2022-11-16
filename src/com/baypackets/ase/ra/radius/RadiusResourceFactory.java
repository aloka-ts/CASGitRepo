package com.baypackets.ase.ra.radius;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface RadiusResourceFactory extends DefaultResourceFactory {

	
	/**
	 * This method creates Radius Access Request using given session.
	 * @param session
	 * @return
	 * @throws ResourceException
	 */
	public RadiusAccessRequest createRadiusAccessRequest(SipApplicationSession session)throws ResourceException;
	
	/**
	 * This method creates Radius Accounting Request using given session.
	 * @param session
	 * @return
	 * @throws ResourceException
	 */
	public RadiusAccountingRequest createRadiusAccountingRequest(SipApplicationSession session)throws ResourceException;
	/**
	 * This method creates Radius Access Request using given session, username and password.
	 * @param userName username to be used for authentication
	 * @param userPassword password to be used for authentication
	 * @param session
	 * @return
	 * @throws ResourceException
	 */
	public RadiusAccessRequest createRadiusAccessRequest(SipApplicationSession session,String userName,String userPassword)throws ResourceException;
	
	/**
	 * This method creates Radius Accounting Request using given session, username and acctStatusType.
	 * @param userName username to be used for authentication
	 * @param acctStatusType acctStatusType to be used for Accounting Request
	 * @param session
	 * @return
	 * @throws ResourceException
	 */
	public RadiusAccountingRequest createRadiusAccountingRequest(SipApplicationSession session, String userName,int  acctStatusType)throws ResourceException;
	
	/**
	 *  This method creates Radius Client to bes used by application for sending radius request.
	 * @param hostName hostname of Radius Server
	 * @param sharedSecret shared secret used for encrypting packets
	 * @return
	 * @throws ResourceException
	 */
	public AseRadiusClient createAseRadiusClient(String hostName, String sharedSecret)throws ResourceException;
}
