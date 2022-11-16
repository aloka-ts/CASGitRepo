package com.baypackets.ase.sysapps.cab.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"name", "contact", "sipAddress", "status"})

@XmlRootElement(name="NonAconyxMember")
public class NonAconyxMember {

	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "Contact")
	private String contact;
	
	@XmlElement(name = "SIPAddress")
	private String sipAddress;
	
	 @XmlElement(name="Status")
	 private String status;

	public NonAconyxMember() {}

	/**
	 * @param name
	 * @param contact
	 * @param sipAddress
	 */
	public NonAconyxMember(String name,
			String contact, String sipAddress) {
		this.name = name;
		this.contact = contact;
		this.sipAddress = sipAddress;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(String fieldList) {
		this.contact = contact;
	}

	/**
	 * @return the status
	 */
	public String getSIPAddress() {
		return sipAddress;
	}

	/**
	 * @param sipAddress the sipAddress to set
	 */
	public void setSIPAddress(String sipAddress) {
		this.sipAddress = sipAddress;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	
}
