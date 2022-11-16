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
        "channels",
        "errors"
})
@XmlRootElement(name="Assigned-User-Channels")
public class AssignUserChannelsResponse {
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElement(name ="Channel")
	private List<Channel> channels;
	
	@XmlElement(name = "Error-List")
	protected Errors errors;
		
	public AssignUserChannelsResponse(){}
	public AssignUserChannelsResponse(String applicationId,
			String aconyxUsername,List<Channel> channels, Errors errors) {
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.channels = channels;
		this.errors = errors;
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
	
	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
