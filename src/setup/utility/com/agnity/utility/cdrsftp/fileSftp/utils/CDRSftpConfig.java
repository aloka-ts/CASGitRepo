
package com.agnity.utility.cdrsftp.fileSftp.utils;

public class CDRSftpConfig {
	
	private int cdrWaitInterval;
	private int sftpConnWaitInterval;
	private String cdrLocalDir;
	private String cdrArchiveDir;
	private String cdrFilePrefix;
	private String cdrFileExtension;
	private String remoteIp;
	private int remoteSftpPort;
	private String remoteSftpUser;
	private String remoteSftpPassword;
	private String remoteSftpDir;
	private String cdrRemoteFilePrefix;
	private String cdrRemoteFileExtension;
	private String cdrRemoteFileDateFormat;
	private boolean renameRemoteCdrFile;
	private int remoteSftpConnRetries;
	private int noOfCdrsToProcess;
	private boolean cdrDateDirEnabled;
	
	/**
	 * @return the cdrWaitInterval
	 */
	public int getCdrWaitInterval() {
		return cdrWaitInterval;
	}
	/**
	 * @param cdrWaitInterval the cdrWaitInterval to set
	 */
	public void setCdrWaitInterval(int cdrWaitInterval) {
		this.cdrWaitInterval = cdrWaitInterval;
	}
	
	/**
	 * @return the sftpConnWaitInterval
	 */
	public int getSftpConnWaitInterval() {
		return sftpConnWaitInterval;
	}
	/**
	 * @param cdrWaitInterval the sftpConnWaitInterval to set
	 */
	public void setSftpConnWaitInterval(int sftpConnWaitInterval) {
		this.sftpConnWaitInterval = sftpConnWaitInterval;
	}
	
	/**
	 * @return the cdrLocalDir
	 */
	public String getCdrLocalDir() {
		return cdrLocalDir;
	}
	/**
	 * @param cdrLocalDir the cdrLocalDir to set
	 */
	public void setCdrLocalDir(String cdrLocalDir) {
		this.cdrLocalDir = cdrLocalDir;
	}
	/**
	 * @param cdrArchiveDir the cdrArchiveDir to set
	 */
	public void setCdrArchiveDir(String cdrArchiveDir) {
		this.cdrArchiveDir = cdrArchiveDir;
	}
	/**
	 * @return the cdrArchiveDir
	 */
	public String getCdrArchiveDir() {
		return cdrArchiveDir;
	}
	/**
	 * @return the cdrFilePrefix
	 */
	public String getCdrFilePrefix() {
		return cdrFilePrefix;
	}
	/**
	 * @param cdrFilePrefix the cdrFilePrefix to set
	 */
	public void setCdrFilePrefix(String cdrFilePrefix) {
		this.cdrFilePrefix = cdrFilePrefix;
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
	 * @return the cdrRemoteFilePrefix
	 */
	public String getCdrRemoteFilePrefix() {
		return cdrRemoteFilePrefix;
	}
	
	/**
	 * @param renameRemoteCdrFile the renameRemoteCdrFile to set
	 */
	public void setRenameRemoteCdrFile(boolean renameRemoteCdrFile) {
		this.renameRemoteCdrFile = renameRemoteCdrFile;
	}
	/**
	 * @return the renameRemoteCdrFile
	 */
	public boolean getRenameRemoteCdrFile() {
		return renameRemoteCdrFile;
	}
	
	/**
	 * @param cdrRemoteFilePrefix the cdrRemoteFilePrefix to set
	 */
	public void setCdrRemoteFilePrefix(String cdrRemoteFilePrefix) {
		this.cdrRemoteFilePrefix = cdrRemoteFilePrefix;
	}
	
	/**
	 * @return the cdrRemoteFileExtension
	 */
	public String getCdrRemoteFileExtension() {
		return cdrRemoteFileExtension;
	}
	/**
	 * @param cdrRemoteFileExtension the cdrRemoteFileExtension to set
	 */
	public void setCdrRemoteFileExtension(String cdrRemoteFileExtension) {
		this.cdrRemoteFileExtension = cdrRemoteFileExtension;
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
	 * @return the cdrRemoteFileDateFormat
	 */
	public String getCdrRemoteFileDateFormat() {
		return cdrRemoteFileDateFormat;
	}
	/**
	 * @param cdrRemoteFileDateFormat the cdrRemoteFileDateFormat to set
	 */
	public void setCdrRemoteFileDateFormat(String cdrRemoteFileDateFormat) {
		this.cdrRemoteFileDateFormat = cdrRemoteFileDateFormat;
	}
	
	/**
	 * @return the remoteSftpPort
	 */
	public int getRemoteSftpConnRetries() {
		return remoteSftpConnRetries;
	}
	/**
	 * @param remoteSftpConnRetries the remoteSftpConnRetries to set
	 */
	public void setRemoteSftpConnRetries(int remoteSftpConnRetries) {
		this.remoteSftpConnRetries = remoteSftpConnRetries;
	}
	
	/**
	 * @return the noOfCdrsToProcess
	 */
	public int getNumberOfCdrsToProcess() {
		return noOfCdrsToProcess;
	}
	/**
	 * @param noOfCdrsToProcess the noOfCdrsToProcess to set
	 */
	public void setNumberOfCdrsToProcess(int noOfCdrsToProcess) {
		this.noOfCdrsToProcess = noOfCdrsToProcess;
	}
	
	/**
	 * @param cdrDateDirEnabled the cdrDateDirEnabled to set
	 */
	public void setCdrDateDirEnabled(boolean cdrDateDirEnabled) {
		this.cdrDateDirEnabled = cdrDateDirEnabled;
	}
	/**
	 * @return the cdrDateDirEnabled
	 */
	public boolean getCdrDateDirEnabled() {
		return cdrDateDirEnabled;
	}
}
