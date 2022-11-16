package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"role",
		"errors"
})

@XmlRootElement(name="Added-Aconyx-User")
public class AddAconyxUserResponse {
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElement(name = "Role")
	private String role;
	
	@XmlElement(name = "Error-List")
	private Errors errors;
	
	public AddAconyxUserResponse(){}
	
	public AddAconyxUserResponse(String aconyxUsername, String role, Errors errors) {
		super();
		this.aconyxUsername = aconyxUsername;
		this.role = role;
		this.errors=errors;
	}

	/**
	 * @param aconyxUserName the aconyxUserName to set
	 */
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	/**
	 * @return the aconyxUserName
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
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
