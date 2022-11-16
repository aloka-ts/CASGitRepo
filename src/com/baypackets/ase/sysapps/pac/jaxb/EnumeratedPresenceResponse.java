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
		"aconyxUserPresence",
		"errors"
})

@XmlRootElement(name="Enumerated-Presence")
public class EnumeratedPresenceResponse {

	@XmlElement(name = "ApplicationID")
	protected String applicationId;
	
	@XmlElement(name = "AconyxUser")
	protected List<AconyxUserPresence> aconyxUserPresence;
		
	@XmlElement(name = "Error-List")
	protected Errors errors;
	

	
	public EnumeratedPresenceResponse(String applicationId, String aconyxUsername,List<AconyxUserPresence> aconyxUserPresence,List<String> invalidAconyxUsernameList,Errors errors) {
		super();
		this.applicationId = applicationId;
		this.aconyxUserPresence = aconyxUserPresence;
		this.errors = errors;
	}

	public EnumeratedPresenceResponse() {

	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<AconyxUserPresence> getAconyxUserPresence() {
		return aconyxUserPresence;
	}

	public void setAconyxUserPresence(List<AconyxUserPresence> aconyxUserPresence) {
		this.aconyxUserPresence = aconyxUserPresence;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
