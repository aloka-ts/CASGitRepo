package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"name",
		"contactViewName",
		"memberList",
		"pccList",
		"nonAconyxMemberList",
		"status"
})
@XmlRootElement(name="Group")
public class AddressBookGroup {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name= "Name")
	private String name;
	
	@XmlElement(name= "Contact-View")
	private String contactViewName;
	
	@XmlElementWrapper(name= "Members")
	@XmlElement(name= "Member")
	private List <String> memberList; 
	
	@XmlElementWrapper(name= "Members")
	@XmlElement(name= "PCC")
	private List <PersonalContactCard> pccList;
	
	@XmlElementWrapper(name= "NonAconyxMembers")
	@XmlElement(name= "NonAconyxMember")
	private List <NonAconyxMember> nonAconyxMemberList;
	
	@XmlElement(name = "Status")
	private String status;

	public AddressBookGroup(){}
	
	
	/**
	 * @param aconyxUsername
	 * @param name
	 * @param contactViewName
	 * @param memberList
	 * @param pccList
	 * @param nonAconyxMemberList
	 * @param status
	 */
	public AddressBookGroup(String aconyxUsername, String name,
			String contactViewName, List<String> memberList,
			List<PersonalContactCard> pccList,List <NonAconyxMember> nonAconyxMemberList,String status) {
		this.aconyxUsername = aconyxUsername;
		this.name = name;
		this.contactViewName = contactViewName;
		this.memberList = memberList;
		this.pccList = pccList;
		this.nonAconyxMemberList=nonAconyxMemberList;
		this.status = status;
	}



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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param contactViewName the contactViewName to set
	 */
	public void setContactViewName(String contactViewName) {
		this.contactViewName = contactViewName;
	}

	/**
	 * @return the contactViewName
	 */
	public String getContactViewName() {
		return contactViewName;
	}

	/**
	 * @param memberList the memberList to set
	 */
	public void setMemberList(List <String> memberList) {
		this.memberList = memberList;
	}

	/**
	 * @return the memberList
	 */
	public List <String> getMemberList() {
		return memberList;
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

	/**
	 * @param pccList the pccList to set
	 */
	public void setPCCList(List <PersonalContactCard> pccList) {
		this.pccList = pccList;
	}

	/**
	 * @return the pccList
	 */
	public List <PersonalContactCard> getPCCList() {
		return pccList;
	}
	
	/**
	 * @param nonAconyxMemberList the nonAconyxMemberList to set
	 */
	public void setNonAconyxMemberList(List <NonAconyxMember> nonAconyxMemberList) {
		this.nonAconyxMemberList = nonAconyxMemberList;
	}

	/**
	 * @return the nonAconyxMemberList
	 */
	public List <NonAconyxMember> getNonAconyxMemberList() {
		return nonAconyxMemberList;
	}
	
	
}
