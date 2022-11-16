package com.baypackets.ase.sbb;

/**
 *  The Early Media Callback interface.
 *  
 *  The applications use this interface to notify the SBB objects
 *  after it has completed the operations during the early media
 *  whether to continue connecting or disconnect the session with the
 *  originating session or to send a redirect response to the originating endpoint.
 *  
 *  The early media callback can be obtained using the getCallback() method
 *  by passing <code>"com.baypackets.ase.sbb.EarlyMediaCallback"</code> as
 *  the name of the callback.
 * 
 *  The early media callback will be available to the application only if 
 *  all of the following conditions are met.
 *  <li>
 *  a. The SBB implements the MsSessionController interface.
 *  b. The EARLY_MEDIA behavioural attribute in the SBB is set to true.
 *  c. The application has returned a NOOP for the EVENT_EARLY_MEDIA and the
 *     application has not yet invoked any of the callback methods.  
 *  </li> 
 */
public interface EarlyMediaCallback extends SBBCallback {

	/**
	 * Resumes the ongoing connect operation by sending 
	 * a 2xx response to the originating side.
	 */
	public void connect();
	
	/**
	 * Sends a redirect response to the originating endpoint and terminate the
	 * Media Server Connection.
	 * @param responseCode Response code to be used in the redirect response.
	 * @param uris - List of URIs to be sent in the contact header.
	 * @throws IllegalArgumentException if the responseCode is not a 3xx response OR 
	 *	 URIs specified are NULL. 
	 */
	public void redirect(int responseCode, String[] uris) throws IllegalArgumentException;
	
	/**
	 * Sends a redirect response to the originating endpoint and terminate the
	 * Media Server Connection.
	 * @param responseCode Response code to be used in the redirect response.
	 * @param reasonPhrase Reason Phrase to be used in the redirect response.
	 * @param uris - List of URIs to be sent in the contact header.
	 * @throws IllegalArgumentException if the responseCode is not a 3xx response OR 
	 *	 URIs specified are NULL. 
	 */
	public void redirect(int responseCode, String reasonPhrase, String[] uris) throws IllegalArgumentException;
	
	
	/**
	 * Disconnects the session with the originating endpoint by sending a 4xx error response.
	 * It also terminates the Media Server Connection.
	 */
	public void disconnect();
}
