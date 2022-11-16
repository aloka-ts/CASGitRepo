package com.baypackets.ase.sbb.conf;

import java.io.Serializable;

import com.baypackets.ase.sbb.ConferenceInfo;

public class ConferenceInfoImpl implements ConferenceInfo, Serializable {
	
	private static final long serialVersionUID = 243547432483548251L;
	private String conferenceId;
	
	private String applicationSessionId;
	
	private String hostName;
	
	private int port;
	
	public void setApplicationSessionId(String applicationSessionId) {
		this.applicationSessionId = applicationSessionId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getApplicationSessionId() {
		return this.applicationSessionId;
	}

	public String getConferenceId() {
		return this.conferenceId;
	}

	public String getHostName() {
		return this.hostName;
	}

	public int getPort() {
		return this.port;
	}
}
