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
		"userChannels",
		"errors"
})

@XmlRootElement(name="Get-AconyxUsername")
public class GetAconyxUsernameResponse {

	@XmlElement(name = "ApplicationID")
	protected String applicationId;
	
	@XmlElement(name="UserChannel")
	private List<UserChannel> userChannels;

	@XmlElement(name = "Error-List")
	protected Errors errors;
	
	public GetAconyxUsernameResponse() {
		
	}

	public GetAconyxUsernameResponse(String applicationId, List<UserChannel> userChannels, Errors errors) {
		super();
		this.applicationId = applicationId;
		this.setUserChannels(userChannels);
		this.errors = errors;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @param userChannels the userChannels to set
	 */
	public void setUserChannels(List<UserChannel> userChannels) {
		this.userChannels = userChannels;
	}

	/**
	 * @return the userChannels
	 */
	public List<UserChannel> getUserChannels() {
		return userChannels;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
	
}
