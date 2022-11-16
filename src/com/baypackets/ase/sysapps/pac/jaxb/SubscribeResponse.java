package com.baypackets.ase.sysapps.pac.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.baypackets.ase.sysapps.pac.jaxb.Errors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUserName",
		"applicationId",
		"errors"
})

@XmlRootElement(name="Get-Subscribe-Response")

public class SubscribeResponse {
	
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUserName;
	
	@XmlElement(name = "ApplicationId")
	private String applicationId;

	@XmlElement(name = "Error-List")
	protected Errors errors;
	
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public void setApplicationId(String applicationID) {
		this.applicationId = applicationID;
	}

	public void setAconyxUserName(String aconyxUsername) {
		this.aconyxUserName = aconyxUsername;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getAconyxUserName() {
		return aconyxUserName;
	}
	
	public Errors getErrors() {
		return errors;
	}

}
