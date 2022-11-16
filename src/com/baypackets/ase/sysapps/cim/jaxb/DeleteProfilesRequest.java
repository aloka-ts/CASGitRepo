package com.baypackets.ase.sysapps.cim.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"username"
})

@XmlRootElement(name="Delete-Profiles-Request")
public class DeleteProfilesRequest {

	@XmlElement(name = "UserName",required=true)
	private List<String> username;
	

	public DeleteProfilesRequest() {
		
	}

	public DeleteProfilesRequest(List<String> username) {
		super();
		this.username = username;
	}
	
	public List<String> getUsername() {
		return username;
	}

	public void setUsername(List<String> username) {
		this.username = username;
	}
	
}