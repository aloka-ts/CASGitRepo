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
		"password",
		"encrypted",
		"channelName",
		"channelURL",
		"status"
})
@XmlRootElement(name="Channel")
public class Channel {
	
	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name = "ChannelUsername")
	private String channelUsername;
	
	@XmlElement(name = "Password")
	private String password;
	
	@XmlElement(name = "Encrypted")
	private String encrypted;	
	
	@XmlElement(name = "ChannelName")
	private String channelName;
	
	@XmlElement(name = "Channel-URL")
	private String channelURL;
		
	@XmlElement(name = "Status")
	private String status;
	
	public Channel() {
		
	}

	
	public Channel(String aconyxUsername,String channelUsername, String password, String encrypted,
			String channelName, String channelURL,String status) {
		super();
		this.aconyxUsername=aconyxUsername;
		this.channelUsername = channelUsername;
		this.password = password;
		this.encrypted = encrypted;
		this.channelName = channelName;
		this.channelURL = channelURL;
		this.status=status;
	}

	
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	public String getAconyxUsername() {
		return aconyxUsername;
	}
	
	public void setChannelUsername(String channelUsername) {
		this.channelUsername = channelUsername;
	}

	public String getChannelUsername() {
		return channelUsername;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setPassowrd(String passowrd) {
		this.password = passowrd;
	}

	public String getPassowrd() {
		return password;
	}

	public void setEncrypted(String encrypted) {
		this.encrypted = encrypted;
	}

	public String getEncrypted() {
		return encrypted;
	}

	public void setChannelURL(String channelURL) {
		this.channelURL = channelURL;
	}

	public String getChannelURL() {
		return channelURL;
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



