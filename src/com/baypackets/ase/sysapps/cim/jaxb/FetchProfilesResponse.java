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

@XmlRootElement(name="Fetch-Profiles-Response")
public class FetchProfilesResponse {

	@XmlElement(name = "ServiceProfile")
	private List<ServiceProfile> serviceProfiles;

	@XmlElement(name = "Errors")
	protected Errors errors;

	public FetchProfilesResponse() {

	}

	public FetchProfilesResponse(List<ServiceProfile> serviceProfiles) {
		super();
		this.serviceProfiles = serviceProfiles;
	}

	public List<ServiceProfile> getServiceProfiles() {
		return serviceProfiles;
	}

	public void setServiceProfiles(List<ServiceProfile> serviceProfiles) {
		this.serviceProfiles = serviceProfiles;
	}


	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}