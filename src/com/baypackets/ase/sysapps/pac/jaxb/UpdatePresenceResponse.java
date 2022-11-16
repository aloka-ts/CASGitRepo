package com.baypackets.ase.sysapps.pac.jaxb;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
		"channelPresence",
		"errors"
})
@XmlRootElement(name="Updated-Presence")
public class UpdatePresenceResponse {
	@XmlElement(name = "ApplicationID")
	private String applicationId;
	
	@XmlElement(name = "ChannelPresence")
	private List<ChannelPresence> channelPresence;

	@XmlElement(name = "Error-List")
	private Errors errors;

	public UpdatePresenceResponse() {
		
	}

	public UpdatePresenceResponse(String applicationId,List<ChannelPresence> channelPresence, Errors errors) {
		super();
		this.applicationId = applicationId;
		this.channelPresence = channelPresence;
		this.errors = errors;
	}
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<ChannelPresence> getChannelPresence() {
		return channelPresence;
	}

	public void setChannelPresence(List<ChannelPresence> channelPresence) {
		this.channelPresence = channelPresence;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
}
