package com.baypackets.ase.sysapps.pac.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"channelUserName",
		"channelName"
})

@XmlRootElement(name="Get-Subscribe-Request")
public class SubscribeRequest {

	@XmlElement(name = "ChannelUsername",required=true)
	private String channelUserName;
	
	@XmlElement(name = "ChannelName")
	private String channelName;
	
	public String getChannelUserName() {
		return channelUserName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelUserName(String channelUserName) {
		this.channelUserName = channelUserName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}
