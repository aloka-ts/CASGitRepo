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
		"channelPresence"
})
@XmlRootElement(name="Update-Presence")
public class UpdatePresenceRequest {
	@XmlElement(name = "ApplicationID")
	private String applicationId;

	@XmlElement(name = "ChannelPresence")
	private List<ChannelPresence> channelPresence;
	
public UpdatePresenceRequest() {
		
	}

public UpdatePresenceRequest(String applicationId,List<ChannelPresence> channelPresence) {
	super();
	this.applicationId = applicationId;
	this.channelPresence = channelPresence;
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
	
}
