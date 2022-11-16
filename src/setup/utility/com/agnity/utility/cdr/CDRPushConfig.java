/**
 * 
 */
package com.agnity.utility.cdr;

/**
 * The Class CDRPushToDSIConfig.
 *
 * @author saneja
 */
public class CDRPushConfig {
	
	/**
	 * Gets the dsi ip.
	 *
	 * @return the dsiIP
	 */
	public String getDsiIP() {
		return dsiIP;
	}
	
	/**
	 * Sets the dsi ip.
	 *
	 * @param dsiIP the dsiIP to set
	 */
	public void setDsiIP(String dsiIP) {
		this.dsiIP = dsiIP;
	}
	
	/**
	 * @param dsiPort the dsiPort to set
	 */
	public void setDsiPort(int dsiPort) {
		this.dsiPort = dsiPort;
	}

	/**
	 * @return the dsiPort
	 */
	public int getDsiPort() {
		return dsiPort;
	}

	/**
	 * Gets the dsi user.
	 *
	 * @return the dsiUser
	 */
	public String getDsiUser() {
		return dsiUser;
	}
	
	/**
	 * Sets the dsi user.
	 *
	 * @param dsiUser the dsiUser to set
	 */
	public void setDsiUser(String dsiUser) {
		this.dsiUser = dsiUser;
	}
	
	/**
	 * Gets the dsi password.
	 *
	 * @return the dsiPassword
	 */
	public String getDsiPassword() {
		return dsiPassword;
	}
	
	/**
	 * Sets the dsi password.
	 *
	 * @param dsiPassword the dsiPassword to set
	 */
	public void setDsiPassword(String dsiPassword) {
		this.dsiPassword = dsiPassword;
	}
	
	/**
	 * Gets the dsi push dir.
	 *
	 * @return the dsiPushDir
	 */
	public String getDsiPushDir() {
		return dsiPushDir;
	}
	
	/**
	 * Sets the dsi push dir.
	 *
	 * @param dsiPushDir the dsiPushDir to set
	 */
	public void setDsiPushDir(String dsiPushDir) {
		this.dsiPushDir = dsiPushDir;
	}
	
	/**
	 * Gets the cdr push wait interval.
	 *
	 * @return the cdrPushWaitInterval
	 */
	public int getCdrPushWaitInterval() {
		return cdrPushWaitInterval;
	}
	
	/**
	 * Sets the cdr push wait interval.
	 *
	 * @param cdrPushWaitInterval the cdrPushWaitInterval to set
	 */
	public void setCdrPushWaitInterval(int cdrPushWaitInterval) {
		this.cdrPushWaitInterval = cdrPushWaitInterval;
	}
	
	/**
	 * Gets the cdrpush local dir name.
	 *
	 * @return the cdrpushLocalDirName
	 */
	public String getCdrPushLocalDirName() {
		return cdrPushLocalDirName;
	}
	
	/**
	 * Sets the cdrpush local dir name.
	 *
	 * @param cdrpushLocalDirName the cdrpushLocalDirName to set
	 */
	public void setCdrPushLocalDirName(String cdrPushLocalDirName) {
		this.cdrPushLocalDirName = cdrPushLocalDirName;
	}
	
	/**
	 * Gets the cdr push local file prefix.
	 *
	 * @return the cdrPushLocalFilePrefix
	 */
	public String getCdrPushLocalFilePrefix() {
		return cdrPushLocalFilePrefix;
	}
	
	/**
	 * Sets the cdr push local file prefix.
	 *
	 * @param cdrPushLocalFilePrefix the cdrPushLocalFilePrefix to set
	 */
	public void setCdrPushLocalFilePrefix(String cdrPushLocalFilePrefix) {
		this.cdrPushLocalFilePrefix = cdrPushLocalFilePrefix;
	}
	
	/**
	 * @param cdrPushLocalFileExtension the cdrPushLocalFileExtension to set
	 */
	public void setCdrPushLocalFileExtension(String cdrPushLocalFileExtension) {
		this.cdrPushLocalFileExtension = cdrPushLocalFileExtension;
	}

	/**
	 * @return the cdrPushLocalFileExtension
	 */
	public String getCdrPushLocalFileExtension() {
		return cdrPushLocalFileExtension;
	}

	/**
	 * Gets the cdr push local loc type.
	 *
	 * @return the cdrPushLocalLocType
	 */
	public String getCdrPushLocalLocType() {
		return cdrPushLocalLocType;
	}
	
	/**
	 * Sets the cdr push local loc type.
	 *
	 * @param cdrPushLocalLocType the cdrPushLocalLocType to set
	 */
	public void setCdrPushLocalLocType(String cdrPushLocalLocType) {
		this.cdrPushLocalLocType = cdrPushLocalLocType;
	}
	
	/**
	 * Gets the cdr push non dsi identifier.
	 *
	 * @return the cdrPushNonDSIIdentifier
	 */
	public String getCdrPushNonDSIIdentifier() {
		return cdrPushNonDSIIdentifier;
	}
	
	/**
	 * Sets the cdr push non dsi identifier.
	 *
	 * @param cdrPushNonDSIIdentifier the cdrPushNonDSIIdentifier to set
	 */
	public void setCdrPushNonDSIIdentifier(String cdrPushNonDSIIdentifier) {
		this.cdrPushNonDSIIdentifier = cdrPushNonDSIIdentifier;
	}
	
	/**
	 * @param cdrStartIndex the cdrStartIndex to set
	 */
	public void setCdrStartIndex(int cdrStartIndex) {
		this.cdrStartIndex = cdrStartIndex;
	}

	/**
	 * @return the cdrStartIndex
	 */
	public int getCdrStartIndex() {
		return cdrStartIndex;
	}

	/** The dsi ip. */
	private String dsiIP;
	
	/** The dsi port. */
	private int dsiPort;
	
	/** The dsi user. */
	private String dsiUser;
	
	/** The dsi password. */
	private String dsiPassword;
	
	/** The dsi push dir. */
	private String dsiPushDir;
	
	/** The cdr push wait interval. */
	private int cdrPushWaitInterval;
	
	/** The cdr push local dir name. */
	private String cdrPushLocalDirName;
	
	/** The cdr push local file prefix. */
	private String cdrPushLocalFilePrefix;
	
	private String cdrPushLocalFileExtension;
	
	/** The cdr push local loc type. */
	private String cdrPushLocalLocType;
	
	/** The cdr push non dsi identifier. */
	private String cdrPushNonDSIIdentifier;
	
	/**start index for cdr file ftp*/
	private int cdrStartIndex;

}
