/*
 * UserChannelDataRow.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.cache;

public class UserChannelDataRow {

	private String channelUsername;
	private int channelId;
	private String password;
	private String encrypted;
	private String channelURL;
	private String status;
	private String customLabel;
	private int opreationalStatus;
	private long lastUpdated;

	public UserChannelDataRow(String channelUsername, int channelId,
			String password, String encrypted, String channelURL,
			String status, String customLabel, int opreationalStatus,
			long lastUpdated) {
		super();
		this.setChannelUsername(channelUsername);
		this.setChannelId(channelId);
		this.setPassword(password);
		this.setEncrypted(encrypted);
		this.setChannelURL(channelURL);
		this.setStatus(status);
		this.setCustomLabel(customLabel);
		this.setOpreationalStatus(opreationalStatus);
		this.setLastUpdated(lastUpdated);
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
	 * @param channelId the channelId to set
	 */
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return channelId;
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

	/**
	 * @param status
	 *            the status to set
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
	 * @param opreationalStatus
	 *            the opreationalStatus to set
	 */
	public void setOpreationalStatus(int opreationalStatus) {
		this.opreationalStatus = opreationalStatus;
	}

	/**
	 * @return the opreationalStatus
	 */
	public int getOpreationalStatus() {
		return opreationalStatus;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the lastUpdated
	 */
	public long getLastUpdated() {
		return lastUpdated;
	}

}
