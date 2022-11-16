/**
 * created as per solution to  bug saneja@bug7667
 * 
 * @author saneja
 */
package com.agnity.utility.cdrsftp.dbpull.utils;

public class CDRPullConfig {
	
	private String remoteIp;
	private int remoteSftpPort;
	private String remoteSftpUser;
	private String remoteSftpPassword;
	private String remoteSftpDir;
	private int cdrPullWaitInterval;
	private String localDirName;
	private String oemString;
	private String camVersion;
	private String cdrFileExtension;
	private String fip;
	private String cdrFilePrefix;
	private int maxCdrs;
	private long rolloverTime;//in seconds
	private String lockTable;	
	private String statusColumn;	
	private String sentFileColumn;	
	private String cdrFileCntrIdentifier;
	private int cdrFileSizeAdjFactor;
	private int cdrFileSizeAdjApplyCrit;
	
	/**
	 * @return the remoteIp
	 */
	public String getRemoteIp() {
		return remoteIp;
	}
	/**
	 * @param remoteIp the remoteIp to set
	 */
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}
	/**
	 * @return the remoteSftpPort
	 */
	public int getRemoteSftpPort() {
		return remoteSftpPort;
	}
	/**
	 * @param remoteSftpPort the remoteSftpPort to set
	 */
	public void setRemoteSftpPort(int remoteSftpPort) {
		this.remoteSftpPort = remoteSftpPort;
	}
	/**
	 * @return the remoteSftpUser
	 */
	public String getRemoteSftpUser() {
		return remoteSftpUser;
	}
	/**
	 * @param remoteSftpUser the remoteSftpUser to set
	 */
	public void setRemoteSftpUser(String remoteSftpUser) {
		this.remoteSftpUser = remoteSftpUser;
	}
	/**
	 * @return the remoteSftpPassword
	 */
	public String getRemoteSftpPassword() {
		return remoteSftpPassword;
	}
	/**
	 * @param remoteSftpPassword the remoteSftpPassword to set
	 */
	public void setRemoteSftpPassword(String remoteSftpPassword) {
		this.remoteSftpPassword = remoteSftpPassword;
	}
	/**
	 * @return the remoteSftpDir
	 */
	public String getRemoteSftpDir() {
		return remoteSftpDir;
	}
	/**
	 * @param remoteSftpDir the remoteSftpDir to set
	 */
	public void setRemoteSftpDir(String remoteSftpDir) {
		this.remoteSftpDir = remoteSftpDir;
	}
	/**
	 * @return the cdrPullWaitInterval
	 */
	public int getCdrPullWaitInterval() {
		return cdrPullWaitInterval;
	}
	/**
	 * @param cdrPullWaitInterval the cdrPullWaitInterval to set
	 */
	public void setCdrPullWaitInterval(int cdrPullWaitInterval) {
		this.cdrPullWaitInterval = cdrPullWaitInterval;
	}
	/**
	 * @return the localDirName
	 */
	public String getLocalDirName() {
		return localDirName;
	}
	/**
	 * @param localDirName the localDirName to set
	 */
	public void setLocalDirName(String localDirName) {
		this.localDirName = localDirName;
	}
//	/**
//	 * @return the dbUrl
//	 */
//	public String getDbUrl() {
//		return dbUrl;
//	}
//	/**
//	 * @param dbUrl the dbUrl to set
//	 */
//	public void setDbUrl(String dbUrl) {
//		this.dbUrl = dbUrl;
//	}
//	/**
//	 * @return the dbUser
//	 */
//	public String getDbUser() {
//		return dbUser;
//	}
//	/**
//	 * @param dbUser the dbUser to set
//	 */
//	public void setDbUser(String dbUser) {
//		this.dbUser = dbUser;
//	}
//	/**
//	 * @return the dbPassword
//	 */
//	public String getDbPassword() {
//		return dbPassword;
//	}
//	/**
//	 * @param dbPassword the dbPassword to set
//	 */
//	public void setDbPassword(String dbPassword) {
//		this.dbPassword = dbPassword;
//	}
	/**
	 * @return the oemString
	 */
	public String getOemString() {
		return oemString;
	}
	/**
	 * @param oemString the oemString to set
	 */
	public void setOemString(String oemString) {
		this.oemString = oemString;
	}
	/**
	 * @return the camVersion
	 */
	public String getCamVersion() {
		return camVersion;
	}
	/**
	 * @param camVersion the camVersion to set
	 */
	public void setCamVersion(String camVersion) {
		this.camVersion = camVersion;
	}
	/**
	 * @return the cdrFileExtension
	 */
	public String getCdrFileExtension() {
		return cdrFileExtension;
	}
	/**
	 * @param cdrFileExtension the cdrFileExtension to set
	 */
	public void setCdrFileExtension(String cdrFileExtension) {
		this.cdrFileExtension = cdrFileExtension;
	}
	/**
	 * @param fip the fip to set
	 */
	public void setFip(String fip) {
		this.fip = fip;
	}
	/**
	 * @return the fip
	 */
	public String getFip() {
		return fip;
	}
	public String getCdrFilePrefix() {
		return cdrFilePrefix;
	}
	public void setCdrFilePrefix(String cdrFilePrefix) {
		this.cdrFilePrefix = cdrFilePrefix;
	}
	public int getMaxCdrs() {
		return maxCdrs;
	}
	public void setMaxCdrs(int maxCdrs) {
		this.maxCdrs = maxCdrs;
	}
	public long getRolloverTime() {
		return rolloverTime;
	}
	public void setRolloverTime(long rolloverTime) {
		this.rolloverTime = rolloverTime;
	}
	public String getLockTable() {
		return lockTable;
	}
	public void setLockTable(String lockTable) {
		this.lockTable = lockTable;
	}
	public String getStatusColumn() {
		return statusColumn;
	}
	public void setStatusColumn(String statusColumn) {
		this.statusColumn = statusColumn;
	}
	public String getSentFileColumn() {
		return sentFileColumn;
	}
	public void setSentFileColumn(String sentFileColumn) {
		this.sentFileColumn = sentFileColumn;
	}
	public void setCdrFileCntrIdentifier(String cdrFileCntrIdentifier) {
		this.cdrFileCntrIdentifier = cdrFileCntrIdentifier;
	}
	public String getCdrFileCntrIdentifier() {
		return cdrFileCntrIdentifier;
	}
	public void setCdrFileSizeAdjFactor(int cdrFileSizeAdjFactor) {
		this.cdrFileSizeAdjFactor = cdrFileSizeAdjFactor;
	}
	public int getCdrFileSizeAdjFactor() {
		return cdrFileSizeAdjFactor;
	}
	public void setCdrFileSizeAdjApplyCrit(int cdrFileSizeAdjApplyCrit) {
		this.cdrFileSizeAdjApplyCrit = cdrFileSizeAdjApplyCrit;
	}
	public int getCdrFileSizeAdjApplyCrit() {
		return cdrFileSizeAdjApplyCrit;
	}
	
	
	
}
