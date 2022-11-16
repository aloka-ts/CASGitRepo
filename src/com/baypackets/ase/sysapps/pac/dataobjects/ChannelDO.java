package com.baypackets.ase.sysapps.pac.dataobjects;

import java.io.Serializable;
public class ChannelDO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String applicationId;
	private String aconyxUserName;
	private String channelUsername;
	private String password;
	private String encrypted;	
	private String channelName;
	private String channelURL;
	
	public ChannelDO(String applicationId, String aconyxUserName,
			String channelUsername, String password, String encrypted,
			String channelName, String channelURL) {
		this.applicationId = applicationId;
		this.aconyxUserName = aconyxUserName;
		this.channelUsername = channelUsername;
		this.password = password;
		this.encrypted = encrypted;
		this.channelName = channelName;
		this.channelURL = channelURL;
	}
	/**
	 * @param pacaggregationId the pacaggregationId to set
	 */
	public void setAconyxUserName(String pacaggregationId) {
		this.aconyxUserName = pacaggregationId;
	}
	/**
	 * @return the pacaggregationId
	 */
	public String getAconyxUserName() {
		return aconyxUserName;
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
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param encrypted the encrypted to set
	 */
	public void setEncrypted(String encrypted) {
		this.encrypted = encrypted;
	}
	/**
	 * @return the encrypted
	 */
	public String getEncrypted() {
		return encrypted;
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
	/**
	 * @param channelURL the channelURL to set
	 */
	public void setChannelURL(String channelURL) {
		this.channelURL = channelURL;
	}
	/**
	 * @return the channelURL
	 */
	public String getChannelURL() {
		return channelURL;
	}
}
