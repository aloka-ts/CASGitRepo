package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"applicationId",
		"aconyxUsername",
		"channelUsername",
		"channelName"
})

@XmlRootElement(name="Get-User-Channel")
public class GetUserChannelRequest {
	
	@XmlElement(name = "ApplicationID",required=true)
	private String applicationId;
	
	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	@XmlElement(name = "ChannelUsername")
	private String channelUsername;
	
	@XmlElement(name = "ChannelName")
	private String channelName;
	
	//Default Constructor
	public GetUserChannelRequest(){}
	
	public GetUserChannelRequest(String applicationId, String aconyxUsername,
			String channelUsername, String channelName) {
		super();
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.channelUsername = channelUsername;
		this.channelName = channelName;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the applicationId
	 */
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

	/**
	 * @param channelUsername the channelUsername to set
	 */
	public void setChannelUsername(String channelUsername) {
		this.channelUsername = channelUsername;
	}

	/**
	 * @return the channelUsername
	 */
	public String getChannelUsername() {
		return channelUsername;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}
		
}
