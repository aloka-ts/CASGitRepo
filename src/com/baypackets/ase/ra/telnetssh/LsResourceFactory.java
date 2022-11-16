/**
 * 
 */
package com.baypackets.ase.ra.telnetssh;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

/**
 * A factory for creating request, response and session objects
 *
 * @author saneja
 */
public interface LsResourceFactory extends DefaultResourceFactory{
	
	/**
	 * Creates a new LsRequest, Input parameters are 
	 * sipApplicationSession,Ls Id and command.
	 *
	 * @param appSession the SipApplicationSession
	 * @param lsId 
	 * @param lsCommand Command to be executed on LS
	 * @return the Request object Its a type of LsRequest
	 * @throws ResourceException the resource exception
	 */
	public LsRequest createRequest(SipApplicationSession appSession, int lsId, String lsCommand) throws ResourceException;

}
