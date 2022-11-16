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
		"aconyxUsernameList"
})

@XmlRootElement(name="Get-Enumerated-Presence")
public class EnumeratedPresenceRequest {

	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername")
	protected List<String> aconyxUsernameList;

	public EnumeratedPresenceRequest() {		
	}

	public EnumeratedPresenceRequest(String applicationId, List<String> aconyxUsernameList) {
		super();
		this.applicationId=applicationId;
		this.aconyxUsernameList = aconyxUsernameList;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<String> getAconyxUsernames() {
		return aconyxUsernameList;
	}

	public void setAconyxUsernames(List<String> aconyxUsername) {
		this.aconyxUsernameList = aconyxUsername;
	}
}
