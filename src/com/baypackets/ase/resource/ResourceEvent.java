package com.baypackets.ase.resource;

import java.util.EventObject;

import javax.servlet.sip.SipApplicationSession;

public class ResourceEvent extends EventObject {

	private String type;
	private SipApplicationSession applicationSession;
	
	public ResourceEvent(Object source, String type, SipApplicationSession appSession) {
		super(source);
		this.type = type;
		this.applicationSession = appSession;
	}

	public SipApplicationSession getApplicationSession() {
		return applicationSession;
	}

	public String getType() {
		return type;
	}
}
