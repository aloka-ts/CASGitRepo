package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername"
})

@XmlRootElement(name="Delete-Aconyx-User")
public class DeleteAconyxUserRequest {
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
		
	public DeleteAconyxUserRequest(){}
	
	public DeleteAconyxUserRequest(String aconyxUserName) {
		super();
		this.aconyxUsername = aconyxUserName;
		
	}

	/**
	 * @param aconyxUserName the aconyxUserName to set
	 */
	public void setAconyxUsername(String aconyxUserName) {
		this.aconyxUsername = aconyxUserName;
	}

	/**
	 * @return the aconyxUserName
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}	
}
