package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId"
		})
		
@XmlRootElement(name="Get-All-App-Channels")
public class GetAllAppChannelsRequest {
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	public GetAllAppChannelsRequest(){}
	public GetAllAppChannelsRequest(String applicationId) {
		super();
		this.applicationId = applicationId;
		}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}
	
}

