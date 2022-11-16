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
		"memberList"
		})
@XmlRootElement(name="Get-Member-Details")
public class GetMemberDetailsRequest {

	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElementWrapper(name= "Members")
	@XmlElement(name= "Member")
	private List <String> memberList;

	public GetMemberDetailsRequest(){};
	/**
	 * @param aconyxUsername
	 * @param memberList
	 */
	public GetMemberDetailsRequest(String aconyxUsername,
			List<String> memberList) {
		this.aconyxUsername = aconyxUsername;
		this.memberList = memberList;
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
}
