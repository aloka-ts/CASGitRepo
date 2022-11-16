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
		"channels",
        "errors"
})
@XmlRootElement(name="Get-All-App-Channels")
public class GetAllAppChannelsResponse {
	@XmlElement(name = "ApplicationID")
	private String applicationId;
	
	@XmlElement(name ="Channel")
	private List<Channel> channels;
	
	@XmlElement(name = "Error-List")
	protected Errors errors;
		
	public GetAllAppChannelsResponse(){}
	public GetAllAppChannelsResponse(String applicationId, List<Channel> channels, Errors errors) {
		this.applicationId = applicationId;
		this.channels = channels;
		this.errors = errors;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public List<Channel> getChannels() {
		return channels;
	}	
	
	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
