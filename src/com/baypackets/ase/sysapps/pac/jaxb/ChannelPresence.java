package com.baypackets.ase.sysapps.pac.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"channelUsername",
		"channelName",
		"status",
		"customLabel"
		
})

@XmlRootElement(name="ChannelPresence")
public class ChannelPresence {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name = "ChannelUsername")
	protected String channelUsername;

	@XmlElement(name = "ChannelName")
	protected String channelName;

	@XmlElement(name = "Status")
	protected String status;
	
	@XmlElement(name = "CustomLabel")
	protected String customLabel;
	
	public ChannelPresence() {

	}
	
	public ChannelPresence(String aconyxUsername,String channelUsername, String channelName, String status, String customLabel) {
		super();
		this.aconyxUsername=aconyxUsername;
		this.channelUsername = channelUsername;
		this.channelName = channelName;
		this.status = status;
		this.customLabel = customLabel;
	}
	
	public String getAconyxUsername() {
		return aconyxUsername;
	}

	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}
	
	public String getChannelUsername() {
		return channelUsername;
	}

	public void setChannelUsername(String channelUsername) {
		this.channelUsername = channelUsername;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}


}

