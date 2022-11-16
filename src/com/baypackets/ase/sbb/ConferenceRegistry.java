package com.baypackets.ase.sbb;

import javax.servlet.sip.SipServletMessage;

/**
 * Conference Registry interface can be used 
 * to find the conference controller using the conference id.
 * 
 * By default a conference registry object would be available as an attribute 
 * in the Servlet Context with the name <code>"com.baypackets.ase.sbb.ConferenceRegistry"</code>
 * 
 * If the application want to use a different conference registry implementation,
 * it should create and add it to the Servlet Context as an attribute with the name  
 * <code>"com.baypackets.ase.sbb.ConferenceRegistry"</code> during the initialization.
 */
public interface ConferenceRegistry {

	/**
	 * This method returns the Conference Info for the specified Conference
	 * @param confId - Conference Identifier.
	 * @return The Conference Info associated with this conference.
	 * 			NULL if the conference is not registered with this registry.
	 */
	public ConferenceInfo findByConferenceID(String confId);
	
	/**
	 * Registers the Conference into this registry.
	 * @param info  Conference Info object.
	 */
	public void registerConference(ConferenceInfo info);

	/**
	 * Unregisters the conference specified by this conference Identifier.
	 * @param conferenceId Conference Identifier.
	 */
	public void unregisterConference (String conferenceId);
	
	/**
	 * Returns whether or not this request/response 
	 * belongs to the conference as specified by the conference identifier.  
	 * 
	 * @param conferenceId The Conference Identifier
	 * @param message SIP Servlet Message 
	 * @return true if the conference is registered with this registry and 
	 * the message belongs to this conference session, false otherwise
	 */
	public boolean isMatchingRequest(String conferenceId, SipServletMessage message);
	
	
	/**
	 * Returns the URI for the conference specified by the Identifier.
	 * @param conferenceId The Conference Identifier.
	 * @return Conference URI if the conference is registered with this registry.
	 */
	public String getConferenceURI(String conferenceId);
}
