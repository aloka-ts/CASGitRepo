package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"errors"
})

@XmlRootElement(name="Deleted-Aconyx-User")
public class DeleteAconyxUserResponse {
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
		
	@XmlElement(name = "Error-List")
	private Errors errors;
	
	public DeleteAconyxUserResponse(){}
	
	public DeleteAconyxUserResponse(String aconyxUsername, Errors errors) {
		super();
		this.aconyxUsername = aconyxUsername;
		this.errors=errors;
	}

	/**
	 * @param aconyxUserName the aconyxUserName to set
	 */
	public void setAconyxUsername(String aconyxUserName) {
		this.aconyxUsername = aconyxUserName;
	}

	/**
	 * @return the aconyxUserName
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
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
