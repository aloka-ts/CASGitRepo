package com.baypackets.ase.sysapps.cim.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"userName",
		"status"
})

@XmlRootElement(name="Profile")
public class Profile {

	@XmlElement(name = "AconyxUsername")
	private String userName;

	@XmlElement(name = "Status")
	private String status;
	
	public Profile() {
	}

	public Profile(String userName, String status) {
		super();
		this.userName = userName;
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
