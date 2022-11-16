package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
		"aconyxUsername",
        "errors"
})
@XmlRootElement(name="Deleted-All-User-Channels")
public class DeleteAllUserChannelsResponse {
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
		
	@XmlElement(name = "Error-List")
	protected Errors errors;
		
	public  DeleteAllUserChannelsResponse() {}
	public DeleteAllUserChannelsResponse(String applicationId,
			String aconyxUsername, Errors errors) {
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.errors = errors;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getAconyxUsername() {
		return aconyxUsername;
	}

	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
