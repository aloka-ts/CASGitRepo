package com.baypackets.ase.sysapps.cim.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"serviceProfiles",
		"errors"
})

@XmlRootElement(name="Create-Profiles-Response")
public class CreateProfilesResponse {

	@XmlElement(name = "Profile")
	private List<Profile> serviceProfiles;

	@XmlElement(name = "Errors")
	protected Errors errors;

	public CreateProfilesResponse() {

	}

	public CreateProfilesResponse(List<Profile> serviceProfiles) {
		super();
		this.serviceProfiles = serviceProfiles;
	}

	public List<Profile> getServiceProfiles() {
		return serviceProfiles;
	}

	public void setServiceProfiles(List<Profile> serviceProfiles) {
		this.serviceProfiles = serviceProfiles;
	}


	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}