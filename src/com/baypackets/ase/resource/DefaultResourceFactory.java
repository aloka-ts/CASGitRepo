package com.baypackets.ase.resource;

import javax.servlet.sip.SipApplicationSession;
/**
 * This is the default resource factory interface provided by the container.
 * 
 * This interface provides methods for
 * <li>
 * Creating the resource session objects.
 * Creating the requests that will be sent out without any session involved.
 * </li>
*/
public interface DefaultResourceFactory extends ResourceFactory {
	
	/**
	 * Crates a new Resource Session object and associates it with 
	 * the specified application session and returns it.
	 * 
	 * @param appSession Application Session to which this session is to be associated with.
	 * @return newly created Resource Session.
	 * @throws IllegalArgumentException if the application session specified is NULL (OR) 
	 * 			the application session specified is not in a VALID State.
	 * @throws IllegalStateException if the resource adaptor is not in a VALID state to create any session.
	 * 			(eg.) The resource adaptor is already stopped or undeployed.
	 */
	public ResourceSession createSession(SipApplicationSession appSession) throws ResourceException;
	
	/**
	 * Creates a new Request object and return it.
	 * 
	 * This method will be used by the applications when it does not want to
	 * create a session object for sending this message. This method will be useful
	 * if there is no state information associated with this request that needs to 
	 * be replicated. 
	 * 
	 * The type specified here will be the part of the contract between the
	 * application and the resource adaptor.
	 * 
	 * @param type Type of the request to be created.
	 * @return Newly created request object.
	 * @throws IllegalStateException if the resource adaptor is not in a VALID state to create any session.
	 * 			(eg.) The resource adaptor is already stopped or undeployed. 
	 * */
	public Request createRequest(int type) throws ResourceException;

}
