package com.baypackets.ase.resource;

/**
 * Request interface represents a Request received from or send out to a resource.
 * 
 * When receiving an inbound request from the resource, the resource adaptor will
 * parse the message and create an instance of the Request Object and deliver it to
 * the application using the Message Handler interface.
 * 
 * When the application want to send out a request, it can create a request
 * <code>ResourceFactory.createRequest()</code> OR <code>ResourceSession.createRequest()</code>
 * methods and then send it using the send method.
 *
 */
public interface Request extends Message {

	/**
	 * Creates a response object with the specified type for this request.
	 * 
	 * The type will be part of the contract between the application and the resource adaptor.
	 * 
	 * @param type - Type of response to be created.
	 * @return the newly created Response object.
	 * @throws IllegalStateException if the request is in an invalid state 
	 * to create a new response. 
	 */
	public Response createResponse(int type) throws ResourceException;
}
