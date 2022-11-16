package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"password",
		"encrypted",
		"role",
		"errors"
})
@XmlRootElement(name="Get-Aconyx-User-Data")
public class GetAconyxUserDataResponse {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name = "Password")
	private String password;
	
	@XmlElement(name = "Encrypted")
	private String encrypted;
	
	@XmlElement(name = "Role")
	private String role;
	
	@XmlElement(name = "Error-List")
	private Errors errors;
	public GetAconyxUserDataResponse(){}

	public GetAconyxUserDataResponse(String aconyxUsername, String password,
			String encrypted, String role, Errors errors) {
		super();
		this.aconyxUsername = aconyxUsername;
		this.password = password;
		this.setEncrypted(encrypted);
		this.role = role;
		this.errors = errors;
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

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param encrypted the encrypted to set
	 */
	public void setEncrypted(String encrypted) {
		this.encrypted = encrypted;
	}

	/**
	 * @return the encrypted
	 */
	public String getEncrypted() {
		return encrypted;
	}
}
