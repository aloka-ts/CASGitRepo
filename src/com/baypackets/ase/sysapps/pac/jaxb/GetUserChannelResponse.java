package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "applicationId", "aconyxUsername", "channel" , "errors" })
@XmlRootElement(name = "Get-User-Channel")
public class GetUserChannelResponse {

	@XmlElement(name = "ApplicationID")
	private String applicationId;

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;

	@XmlElement(name = "Channel")
	private Channel channel;
	
	@XmlElement(name = "Error-List")
	private Errors errors;
	
	// Default Constructor
	public GetUserChannelResponse() {
	}

	public GetUserChannelResponse(String applicationId, String aconyxUsername,
			Channel channel,Errors errors) {
		super();
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.channel = channel;
		this.errors=errors;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set
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
	 * @param aconyxUsername
	 *            the aconyxUsername to set
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
	 * @param channel the channel to set
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}

}
