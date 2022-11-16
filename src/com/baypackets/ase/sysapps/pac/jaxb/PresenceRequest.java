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
		"userChannels"
})

@XmlRootElement(name="Get-Presence")
public class PresenceRequest {

	@XmlElement(name = "ApplicationID")
	private String applicationId;	

	@XmlElement(name="UserChannel")
	protected List<UserChannel> userChannels;

	public PresenceRequest() {
		
	}
	
	public PresenceRequest(String applicationId, List<UserChannel> userChannels) {
		super();
		this.applicationId=applicationId;
		this.userChannels = userChannels;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<UserChannel> getUserChannels() {
		return userChannels;
	}

	public void setUserChannels(List<UserChannel> userChannels) {
		this.userChannels = userChannels;
	}
	
}