package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"name",
		"fieldList",
		"status"
})

@XmlRootElement(name="ContactView")
public class ContactView {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "Field")
	private List<String> fieldList;
	
	@XmlElement(name = "Status")
	private String status;

	public ContactView() {}

	/**
	 * @param aconyxUsername
	 * @param name
	 * @param fieldList
	 * @param status
	 */
	public ContactView(String aconyxUsername, String name,
			List<String> fieldList, String status) {
		this.aconyxUsername = aconyxUsername;
		this.name = name;
		this.fieldList = fieldList;
		this.status = status;
	}

	/**
	 * @return the aconyxUsername
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}

	/**
	 * @param aconyxUsername the aconyxUsername to set
	 */
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
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
	 * @return the fieldList
	 */
	public List<String> getFieldList() {
		return fieldList;
	}

	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
