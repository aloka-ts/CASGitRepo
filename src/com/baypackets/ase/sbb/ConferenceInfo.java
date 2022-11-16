package com.baypackets.ase.sbb;

/**
 * The Conference Info interface defines the attributes used for
 * identifying the conference.
 * 
 * This object provides the attributes that can be used to redirect
 * an incoming request to the server where the conference is available.
 *
 */
public interface ConferenceInfo {

	/**
	 * Returns the Application Session Identifier for the Conference
	 * @return Application Session ID.
	 */
	public String getApplicationSessionId();
	
	/**
	 * Returns the Conference Identifier
	 * @return Conference Identifier
	 */
	public String getConferenceId();

	/**
	 * Returns the host name of the SAS instance 
	 * where this conference is available. 
	 * @return Host name of the SAS instance where the conf available.
	 */
	public String getHostName();
	
	/**
	 * Port where the SAS instance that is hosting this conference, is listening.
	 * @return SAS listen port
	 */
	public int getPort();
}
