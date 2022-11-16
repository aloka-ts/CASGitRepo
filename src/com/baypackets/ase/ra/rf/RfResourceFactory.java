/**
 * Filename:    RfResourceFactory.java
 */
package com.baypackets.ase.ra.rf;

import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

/**
 * This interface provides method to create a RfRequest with a particular type and in a specific application
 * session
 * In this method resource adaptor creates a new RfSession and associates the it to the appsession 
 * passed in as a parameter
 * 
 * parameter type - EVENT = 1
 * 				  - SESSION =2
 * 
 * parameter appsession - application session, with which the newly created RfRequest is associated
 *
 * 
 * @author Neeraj Jadaun	 
 */ 	


public interface RfResourceFactory extends DefaultResourceFactory
{
	public RfRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException;

}
