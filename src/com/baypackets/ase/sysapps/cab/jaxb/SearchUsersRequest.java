package com.baypackets.ase.sysapps.cab.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"searchBy",
		"searchValue"
		})
		
@XmlRootElement(name="Search-Users")
public class SearchUsersRequest {

	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElement(name= "SearchBy")
	private String searchBy;
	
	@XmlElement(name= "SearchValue")
	private String searchValue;

	
	
	/**
	 * @param aconyxUsername the aconyxUsername to set
	 */
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	/**
	 * @return the aconyxUsername
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}
	/**
	 * @param memberList the memberList to set
	 */

	/**
	 * @param searchBy the searchBy to set
	 */
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	/**
	 * @return the searchBy
	 */
	public String getSearchBy() {
		return searchBy;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}
	
}

