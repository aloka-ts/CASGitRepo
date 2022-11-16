package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
		"aconyxUsername"
		})
		
@XmlRootElement(name="Delete-All-User-Channels")
public class DeleteAllUserChannelsRequest {
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	public DeleteAllUserChannelsRequest(){}
	public DeleteAllUserChannelsRequest(String applicationId,String aconyxUsername) {
		super();
		this.applicationId = applicationId;
		this.aconyxUsername=aconyxUsername;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
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
}
