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

@XmlRootElement(name="Delete-Profiles-Response")
public class DeleteProfilesResponse {

	@XmlElement(name = "Profile")
	private List<Profile> serviceProfiles;

	@XmlElement(name = "Errors")
	protected Errors errors;

	public DeleteProfilesResponse() {

	}

	public DeleteProfilesResponse(List<Profile> serviceProfiles) {
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