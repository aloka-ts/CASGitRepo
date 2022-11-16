package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
        "errors"
})

@XmlRootElement(name="Deleted-All-App-Channels")
public class DeleteAllAppChannelsResponse {

	@XmlElement(name = "ApplicationID")
	private String applicationId;
	@XmlElement(name = "Error-List")
	private Errors errors;
	
	public DeleteAllAppChannelsResponse(){}
	public DeleteAllAppChannelsResponse(String applicationId,Errors errors) {
		super();
		this.applicationId = applicationId;
		this.errors = errors;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}
}
