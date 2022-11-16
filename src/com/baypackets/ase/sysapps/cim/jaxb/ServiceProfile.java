package com.baypackets.ase.sysapps.cim.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"userName",
		"state",
		"smsInState"
})

@XmlRootElement(name="ServiceProfile")
public class ServiceProfile {

	@XmlElement(name = "AconyxUsername")
	private String userName;

	@XmlElement(name = "SMS-Out-State")
	private String state;
	
	@XmlElement(name = "SMS-In-State")
	private String smsInState;

	public ServiceProfile() {

	}

	public ServiceProfile(String userName, String state,String smsInState) {
		super();
		this.userName = userName;
		this.state = state;
		this.smsInState=smsInState;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSmsInState() {
		return smsInState;
	}

	public void setSmsInState(String smsInState) {
		this.smsInState = smsInState;
	}

}