package com.baypackets.ase.sysapps.pac.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"status",
		"channelPresence"
})

@XmlRootElement(name="AconyxUser")
public class AconyxUserPresence {

	@XmlElement(name = "AconyxUsername")
	protected String aconyxUsername;
	
	@XmlElement(name = "Status")
	protected String status;
	
	@XmlElement(name = "ChannelPresence")
	protected List<ChannelPresence> channelPresence;
	
	public AconyxUserPresence() {
		
	}

	public AconyxUserPresence(String aconyxUsername,String status, List<ChannelPresence> channelPresence) {
		super();
		this.aconyxUsername = aconyxUsername;
		this.channelPresence = channelPresence;
		this.status = status;
	}

	public String getAconyxUsername() {
		return aconyxUsername;
	}

	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ChannelPresence> getChannelPresenceList() {
		return channelPresence;
	}

	public void setChannelPresenceList(List<ChannelPresence> channelPresence) {
		this.channelPresence = channelPresence;
	}
}
