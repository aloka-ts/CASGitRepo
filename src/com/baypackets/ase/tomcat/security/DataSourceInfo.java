package com.baypackets.ase.tomcat.security;

import javax.sql.DataSource;

public class DataSourceInfo {

	private DataSource currentDataSource=null;
	private DataSource secondaryDataSource=null;
	private DataSource primaryDataSource=null;
	private String loginModuleName=null;
	 //Default number of datasource is 1
    private int numberOfDataSources=1;
	
	/**
	 * @param currentDataSource
	 * @param secondaryDataSource
	 * @param primaryDataSource
	 */
	public DataSourceInfo(String loginModuleName,DataSource primaryDataSource,
			DataSource secondaryDataSource) {
		this.loginModuleName=loginModuleName;
		this.currentDataSource = (primaryDataSource==null)?secondaryDataSource:primaryDataSource;
		this.secondaryDataSource = secondaryDataSource;
		this.primaryDataSource = primaryDataSource;
		if(primaryDataSource!=null && secondaryDataSource!=null)
			numberOfDataSources=2;
	}


	/**
	 * @return the numberOfDataSource
	 */
	protected int getNumberOfDataSources() {
		return numberOfDataSources;
	}


	/**
	 * @param numberOfDataSource the numberOfDataSource to set
	 */
	protected void setNumberOfDataSources(int numberOfDataSources) {
		this.numberOfDataSources = numberOfDataSources;
	}


	/**
	 * @return the loginModuleName
	 */
	protected String getLoginModuleName() {
		return loginModuleName;
	}


	/**
	 * @param loginModuleName the loginModuleName to set
	 */
	protected void setLoginModuleName(String loginModuleName) {
		this.loginModuleName = loginModuleName;
	}


	/**
	 * @return the currentDataSource
	 */
	protected DataSource getCurrentDataSource() {
		return currentDataSource;
	}


	/**
	 * @param currentDataSource the currentDataSource to set
	 */
	protected void setCurrentDataSource(DataSource currentDataSource) {
		this.currentDataSource = currentDataSource;
	}


	
	/**
	 * @return the primaryDataSource
	 */
	protected DataSource getPrimaryDataSource() {
		return primaryDataSource;
	}


	/**
	 * @param primaryDataSource the primaryDataSource to set
	 */
	protected void setPrimaryDataSource(DataSource primaryDataSource) {
		this.primaryDataSource = primaryDataSource;
	}



	
	
	/**
	 * @return the secondaryDataSource
	 */
	protected DataSource getSecondaryDataSource() {
		return secondaryDataSource;
	}


	/**
	 * @param secondaryDataSource the secondaryDataSource to set
	 */
	protected void setSecondaryDataSource(DataSource secondaryDataSource) {
		this.secondaryDataSource = secondaryDataSource;
	}


	protected DataSource flipDataSource(DataSource ds) {
		this.currentDataSource=(ds==primaryDataSource)?secondaryDataSource:primaryDataSource;
		return currentDataSource;
	}
	
}
