package com.baypackets.ase.sysapps.pac.dataobjects;

import java.io.Serializable;

public class PresenceDO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String applicationId;
	private String aconyxUsername;
	private String channelUsername;
	private String channelName;
	private String password;
	private String encrypted;
	private String channelURL;
	private String status;
	private String customLabel;	
	
	public PresenceDO(){
		
	}
	public PresenceDO(String applicationId, String aconyxUsername,String channelUsername, String channelName,String password,String encrypted,String channelURL,String status,
			String customLabel) {
		super();
		this.applicationId = applicationId;
		this.aconyxUsername = aconyxUsername;
		this.channelUsername = channelUsername;
		this.channelName = channelName;
		this.password=password;
		this.encrypted=encrypted;
		this.channelURL=channelURL;
		this.status = status;
		this.customLabel = customLabel;
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
	 * @param customLabel the customLabel to set
	 */
	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}
	/**
	 * @return the customLabel
	 */
	public String getCustomLabel() {
		return customLabel;
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
