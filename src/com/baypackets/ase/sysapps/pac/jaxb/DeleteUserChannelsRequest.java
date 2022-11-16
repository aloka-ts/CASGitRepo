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
		"aconyxUsername",
        "channels"
})
@XmlRootElement(name="Delete-User-Channels")
public class DeleteUserChannelsRequest {
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElement(name ="Channel")
	private List<Channel> channels;
	
	public DeleteUserChannelsRequest(){}
	
	public DeleteUserChannelsRequest(String applicationId,String aconyxUserName,List<Channel> channels){
		super();
		this.applicationId=applicationId;
		this.aconyxUsername=aconyxUserName;
		this.channels=channels;
	}
	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getAconyxUsername() {
		return aconyxUsername;
	}

	public void setAconyxUsername(String aconyxUserName) {
		this.aconyxUsername = aconyxUserName;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public List<Channel> getChannels() {
		return channels;
	}	

}
