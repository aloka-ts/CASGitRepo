/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager;

/**
 * The Class LS Contains LS specific properties.
 *
 * @author saneja
 */
public class LS{
	
	/** The rowID id. */
	private String rowId;
	
	/** The id. Databse primary key*/
	private int Id;
	
	/** The ls id. */
	private int lsId;
	
	/** The ls ip. */
	private String lsIP;
	
	/** The ls port. */
	private int lsPort;
	
	/** The ls user. */
	private String lsUser;
	
	/** The ls password. */
	private String lsPassword;
	
	/** The ls q size. */
	private int lsQSize;
	
	/** The ls q threshold. */
	private int lsQThreshold;
	
	/** The ls connection type value should be telnet or ssh. */
	private String connType;
	
	/**for update active_scp ver is changed to 0 and new row is inserted
	 * for delete oactive_scp_version is changed to 0
	 */
	private int activeScpVer;
	
	//CR- UAT 1219 Changes
	/**
	 * Counter for failed reLogins
	 */
	private int failedReLogins;
	
	private boolean underRecovery = false;
	
	
	/**
	 * Instantiates a new lS param.
	 *	
	 * @param rowId the oracle rowId
	 * @param lsId the ls id
	 * @param lsIP the ls ip
	 * @param lsPort the ls port
	 * @param lsUser the ls user
	 * @param lsPassword the ls password
	 * @param lsQSize the ls q size
	 * @param lsQThreshold the ls q threshold
	 * @param connType the connection type
	 */
	public LS(String rowId, int lsId, String lsIP, int lsPort, String lsUser,
			String lsPassword, int lsQSize, int lsQThreshold, String connType) {
		this.rowId=rowId;
		this.lsId = lsId;
		this.lsIP = lsIP;
		this.lsPort = lsPort;
		this.lsUser = lsUser;
		this.lsPassword = lsPassword;
		this.lsQSize = lsQSize;
		this.lsQThreshold = lsQThreshold;
		this.connType = connType;
	}

	/**
	 * Sets the row id.
	 *
	 * @param rowId the rowId to set
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	/**
	 * Gets the row id.
	 *
	 * @return the rowId
	 */
	public String getRowId() {
		return rowId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		Id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Gets the ls id.
	 *
	 * @return the lsId
	 */
	public int getLsId() {
		return lsId;
	}
	
	/**
	 * Sets the ls id.
	 *
	 * @param lsId the lsId to set
	 */
	public void setLsId(int lsId) {
		this.lsId = lsId;
	}
	
	/**
	 * Gets the ls ip.
	 *
	 * @return the lsIP
	 */
	public String getLsIP() {
		return lsIP;
	}
	
	/**
	 * Sets the ls ip.
	 *
	 * @param lsIP the lsIP to set
	 */
	public void setLsIP(String lsIP) {
		this.lsIP = lsIP;
	}
	
	/**
	 * Gets the ls port.
	 *
	 * @return the lsPort
	 */
	public int getLsPort() {
		return lsPort;
	}
	
	/**
	 * Sets the ls port.
	 *
	 * @param lsPort the lsPort to set
	 */
	public void setLsPort(int lsPort) {
		this.lsPort = lsPort;
	}
	
	/**
	 * Gets the ls user.
	 *
	 * @return the lsUser
	 */
	public String getLsUser() {
		return lsUser;
	}
	
	/**
	 * Sets the ls user.
	 *
	 * @param lsUser the lsUser to set
	 */
	public void setLsUser(String lsUser) {
		this.lsUser = lsUser;
	}
	
	/**
	 * Gets the ls password.
	 *
	 * @return the lsPassword
	 */
	public String getLsPassword() {
		return lsPassword;
	}
	
	/**
	 * Sets the ls password.
	 *
	 * @param lsPassword the lsPassword to set
	 */
	public void setLsPassword(String lsPassword) {
		this.lsPassword = lsPassword;
	}
	
	/**
	 * Gets the ls q size.
	 *
	 * @return the lsQSize
	 */
	public int getLsQSize() {
		return lsQSize;
	}
	
	/**
	 * Sets the ls q size.
	 *
	 * @param lsQSize the lsQSize to set
	 */
	public void setLsQSize(int lsQSize) {
		this.lsQSize = lsQSize;
	}
	
	/**
	 * Gets the ls q threshold.
	 *
	 * @return the lsQThreshold
	 */
	public int getLsQThreshold() {
		return lsQThreshold;
	}
	
	/**
	 * Sets the ls q threshold.
	 *
	 * @param lsQThreshold the lsQThreshold to set
	 */
	public void setLsQThreshold(int lsQThreshold) {
		this.lsQThreshold = lsQThreshold;
	}

	/**
	 * Sets the conn type.
	 *
	 * @param connType the new conn type
	 */
	public void setConnType(String connType) {
		this.connType = connType;
	}

	/**
	 * Gets the conn type.
	 *
	 * @return the connType
	 */
	public String getConnType() {
		return connType;
	}

	/**
	 * @param activeScpVer the activeScpVer to set
	 */
	public void setActiveScpVer(int activeScpVer) {
		this.activeScpVer = activeScpVer;
	}

	/**
	 * @return the activeScpVer
	 */
	public int getActiveScpVer() {
		return activeScpVer;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "LS ID: "+lsId;
	}
	/**
	 * @return the failedReLogins
	 */
	public int getFailedReLogins() {
		return failedReLogins;
	}
	/**
	 * @param failedReLogins the failedReLogins to set
	 */
	public void setFailedReLogins(int failedReLogins) {
		this.failedReLogins = failedReLogins;
	}
	
	public boolean isUnderRecovery() {
		return underRecovery;
	}

	public void setUnderRecovery(boolean underRecovery) {
		this.underRecovery = underRecovery;
	}

}
