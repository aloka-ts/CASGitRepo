/**
 * created as per solution to  bug saneja@bug7667
 * 
 * @author saneja
 */
package com.agnity.utility.cdrsftp.dbpush.utils;

public class CDRPushConfig {
	
	private String dbSrvc;
	private String dbUser;
	private String dbPassword;
	private String ctrlFileName;
	private int cdrPushWaitInterval;
	private String cdrPushPrimaryDirName;
	private String cdrPushSecondaryDirName;
	private String cdrPushArchiveDirName;
	private String cdrPushFilePrefix;
	private String cdrPushFileExtension;
	private int cdrFileSize;
	private String cdrPushIgnoreCdrIdentifier;
	private String cdrPushOemCdrHeader;
	private int cdrStartIndex;
	private int maxCdrWriters;
	
	
	/**
	 * @return the dbSrvc
	 */
	public String getDbSrvc() {
		return dbSrvc;
	}
	/**
	 * @param dbSrvc the dbSrvc to set
	 */
	public void setDbSrvc(String dbSrvc) {
		this.dbSrvc = dbSrvc;
	}
	/**
	 * @return the dbUser
	 */
	public String getDbUser() {
		return dbUser;
	}
	/**
	 * @param dbUser the dbUser to set
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	/**
	 * @return the dbPassword
	 */
	public String getDbPassword() {
		return dbPassword;
	}
	/**
	 * @param dbPassword the dbPassword to set
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	/**
	 * @return the ctrlFileName
	 */
	public String getCtrlFileName() {
		return ctrlFileName;
	}
	/**
	 * @param ctrlFileName the ctrlFileName to set
	 */
	public void setCtrlFileName(String ctrlFileName) {
		this.ctrlFileName = ctrlFileName;
	}
	/**
	 * @return the cdrPushWaitInterval
	 */
	public int getCdrPushWaitInterval() {
		return cdrPushWaitInterval;
	}
	/**
	 * @param cdrPushWaitInterval the cdrPushWaitInterval to set
	 */
	public void setCdrPushWaitInterval(int cdrPushWaitInterval) {
		this.cdrPushWaitInterval = cdrPushWaitInterval;
	}
	/**
	 * @return the cdrPushPrimaryDirName
	 */
	public String getCdrPushPrimaryDirName() {
		return cdrPushPrimaryDirName;
	}
	/**
	 * @param cdrPushPrimaryDirName the cdrPushPrimaryDirName to set
	 */
	public void setCdrPushPrimaryDirName(String cdrPushPrimaryDirName) {
		this.cdrPushPrimaryDirName = cdrPushPrimaryDirName;
	}
	/**
	 * @return the cdrPushSecondaryDirName
	 */
	public String getCdrPushSecondaryDirName() {
		return cdrPushSecondaryDirName;
	}
	/**
	 * @param cdrPushSecondaryDirName the cdrPushSecondaryDirName to set
	 */
	public void setCdrPushSecondaryDirName(String cdrPushSecondaryDirName) {
		this.cdrPushSecondaryDirName = cdrPushSecondaryDirName;
	}
	/**
	 * @param cdrPushArchiveDirName the cdrPushArchiveDirName to set
	 */
	public void setCdrPushArchiveDirName(String cdrPushArchiveDirName) {
		this.cdrPushArchiveDirName = cdrPushArchiveDirName;
	}
	/**
	 * @return the cdrPushArchiveDirName
	 */
	public String getCdrPushArchiveDirName() {
		return cdrPushArchiveDirName;
	}
	/**
	 * @return the cdrPushFilePrefix
	 */
	public String getCdrPushFilePrefix() {
		return cdrPushFilePrefix;
	}
	/**
	 * @param cdrPushFilePrefix the cdrPushFilePrefix to set
	 */
	public void setCdrPushFilePrefix(String cdrPushFilePrefix) {
		this.cdrPushFilePrefix = cdrPushFilePrefix;
	}
	/**
	 * @return the cdrPushFileExtension
	 */
	public String getCdrPushFileExtension() {
		return cdrPushFileExtension;
	}
	/**
	 * @param cdrPushFileExtension the cdrPushFileExtension to set
	 */
	public void setCdrPushFileExtension(String cdrPushFileExtension) {
		this.cdrPushFileExtension = cdrPushFileExtension;
	}
	/**
	 * @return the cdrFileSize
	 */
	public int getCdrFileSize() {
		return cdrFileSize;
	}
	/**
	 * @param cdrFileSize the cdrFileSize to set
	 */
	public void setCdrFileSize(int cdrFileSize) {
		this.cdrFileSize = cdrFileSize;
	}
	/**
	 * @return the cdrPushIgnoreCdrIdentifier
	 */
	public String getCdrPushIgnoreCdrIdentifier() {
		return cdrPushIgnoreCdrIdentifier;
	}
	/**
	 * @param cdrPushIgnoreCdrIdentifier the cdrPushIgnoreCdrIdentifier to set
	 */
	public void setCdrPushIgnoreCdrIdentifier(String cdrPushIgnoreCdrIdentifier) {
		this.cdrPushIgnoreCdrIdentifier = cdrPushIgnoreCdrIdentifier;
	}
	
	public String getCdrPushOemCdrHeader() {
		return cdrPushOemCdrHeader;
	}
	
	public void setCdrPushOemCdrHeader(String cdrPushOemCdrHeader) {
		this.cdrPushOemCdrHeader = cdrPushOemCdrHeader;
	}
	
	/**
	 * @return the cdrStartIndex
	 */
	public int getCdrStartIndex() {
		return cdrStartIndex;
	}
	/**
	 * @param cdrStartIndex the cdrStartIndex to set
	 */
	public void setCdrStartIndex(int cdrStartIndex) {
		this.cdrStartIndex = cdrStartIndex;
	}
	/**
	 * @param maxCdrWriters the maxCdrWriters to set
	 */
	public void setMaxCdrWriters(int maxCdrWriters) {
		this.maxCdrWriters = maxCdrWriters;
	}
	/**
	 * @return the maxCdrWriters
	 */
	public int getMaxCdrWriters() {
		return maxCdrWriters;
	}

}
