package com.baypackets.ase.sbb;

import java.util.HashMap;

import javax.servlet.sip.SipServletMessage;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.util.AseStrings;

/**
 * Default Conference Registry provides the default implementation of the
 * Conference Registry Interface.
 *
 */
public class DefaultConferenceRegistry implements ConferenceRegistry{
	private static final String ENCODE_KEY = "sas-sessionid".intern();
	
	private HashMap conferenceMap =  new HashMap();
	
	public ConferenceInfo findByConferenceID(String confId) {
		return (ConferenceInfo) this.conferenceMap.get(confId);
	}

	public String getConferenceURI(String conferenceId) {
		ConferenceInfo info = this.findByConferenceID(conferenceId);
		if(info == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"Conference\" <sip:");
		buffer.append(info.getConferenceId());
		buffer.append(AseStrings.AT);
		buffer.append(info.getHostName());
		buffer.append(AseStrings.COLON);
		buffer.append(info.getPort());
		buffer.append(AseStrings.SEMI_COLON);
		buffer.append(ENCODE_KEY);
		buffer.append(AseStrings.EQUALS);
		buffer.append(info.getApplicationSessionId());
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		return buffer.toString();
	}

	public boolean isMatchingRequest(String conferenceId, SipServletMessage message) {
		boolean matching = false;
		ConferenceInfo info = this.findByConferenceID(conferenceId);
		if(info != null){
			matching = info.getApplicationSessionId() != null && 
			info.getApplicationSessionId().equals(((SasApplicationSession)(message.getApplicationSession())).getAppSessionId());
		}
		return matching;
	}

	public void registerConference(ConferenceInfo info) {
		if(info != null && info.getConferenceId() != null){
			this.conferenceMap.put(info.getConferenceId(), info);
		}
	}

	public void unregisterConference(String conferenceId) {
		if(conferenceId != null){
			this.conferenceMap.remove(conferenceId);
		}
	}
}
