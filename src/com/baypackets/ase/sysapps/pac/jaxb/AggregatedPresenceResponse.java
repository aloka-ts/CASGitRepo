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
		"status",
		"userChannels",
		"errors"
})

@XmlRootElement(name="Aggregated-Presence")
public class AggregatedPresenceResponse {

	@XmlElement(name = "ApplicationID")
	protected String applicationId;

	@XmlElement(name = "AconyxUsername")
	protected String aconyxUsername;

	@XmlElement(name = "Status")
	protected String status;
	
	@XmlElement(name="UserChannel")
	protected List<UserChannel> userChannels;

	@XmlElement(name = "Error-List")
	protected Errors errors;
	

	
	public AggregatedPresenceResponse(String applicationId, String aconyxUsername, String status,
			List<UserChannel> userChannels, Errors errors) {
		super();
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.status = status;
		this.userChannels = userChannels;
		this.errors = errors;
	}

	public AggregatedPresenceResponse() {

	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
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

	public List<UserChannel> getUserChannels() {
		return userChannels;
	}

	public void setUserChannels(List<UserChannel> userChannels) {
		this.userChannels = userChannels;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}	
}
