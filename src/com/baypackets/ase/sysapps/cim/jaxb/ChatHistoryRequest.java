package com.baypackets.ase.sysapps.cim.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxuser",
		"buddies"
})
@XmlRootElement(name="ChatHistoryRequest")
public class ChatHistoryRequest {

	@XmlElement(name = "Aconyxuser")
	private String aconyxuser;

	@XmlElementWrapper(name = "BuddyList")
	@XmlElement(name = "Buddy")
	private List<String> buddies;
	
	public ChatHistoryRequest() {
		super();
	}
	public ChatHistoryRequest(String aconyxuser, List<String> buddies) {
		super();
		this.aconyxuser = aconyxuser;
		this.buddies = buddies;
	}
	public String getAconyxuser() {
		return aconyxuser;
	}
	public void setAconyxuser(String aconyxuser) {
		this.aconyxuser = aconyxuser;
	}
	public List<String> getBuddies() {
		return buddies;
	}
	public void setBuddies(List<String> buddies) {
		this.buddies = buddies;
	}
}


