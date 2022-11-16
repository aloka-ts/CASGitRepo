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
		"status"
})

@XmlRootElement(name="UserChannel")
public class UserChannel {
	
	@XmlElement(name = "AconyxUsername")
	protected String aconyxUsername;
	
	@XmlElement(name = "ChannelUsername")
	protected String channelUsername;
	
	@XmlElement(name = "ChannelName")
	protected String channelName;
	
	@XmlElement(name = "Status")
	private String status;

	public UserChannel() {

	}

	public UserChannel(String channelUsername,String aconyxUsername, String channelName,String status) {
		super();
		this.channelUsername = channelUsername;
		this.aconyxUsername = aconyxUsername;
		this.channelName = channelName;
		this.setStatus(status);
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

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


}
