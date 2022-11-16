package com.baypackets.ase.sysapps.pac.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
		"aconyxUsername"
})

@XmlRootElement(name="Get-Aggregated-Presence")
public class AggregatedPresenceRequest {

	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername",required=true)
	protected String aconyxUsername;

	public AggregatedPresenceRequest() {		
	}

	public AggregatedPresenceRequest(String applicationId, String aconyxUsername, PresenceRequest schedule) {
		super();
		this.applicationId=applicationId;
		this.aconyxUsername = aconyxUsername;
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
}
