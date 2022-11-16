package com.baypackets.ase.sysapps.pac.jaxb;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsers",
		"errors"
})
@XmlRootElement(name="All-Aconyx-Users-Data")
public class GetAllAconyxUsersDataResponse {

	
	@XmlElement(name= "Aconyx-User")
	private List<AconyxUser> aconyxUsers;
	@XmlElement(name = "Error-List")
	private Errors errors;
	/**
	 * @param aconyxUsers the aconyxUsers to set
	 */
	public void setAconyxUsers(List<AconyxUser> aconyxUsers) {
		this.aconyxUsers = aconyxUsers;
	}
	/**
	 * @return the aconyxUsers
	 */
	public List<AconyxUser> getAconyxUsers() {
		return aconyxUsers;
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
