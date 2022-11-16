package com.baypackets.ase.sysapps.cim.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"serviceProfiles"
})

@XmlRootElement(name="Update-Profiles-Request")
public class UpdateProfilesRequest {

	@XmlElement(name = "ServiceProfile")
	private List<ServiceProfile> serviceProfiles;

	public UpdateProfilesRequest() {

	}

	public UpdateProfilesRequest(List<ServiceProfile> serviceProfiles) {
		super();
		this.serviceProfiles = serviceProfiles;
	}

	public List<ServiceProfile> getServiceProfiles() {
		return serviceProfiles;
	}

	public void setServiceProfiles(List<ServiceProfile> serviceProfiles) {
		this.serviceProfiles = serviceProfiles;
	}
}