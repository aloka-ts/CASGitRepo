/*
 * Interface defining methods to be implemnted by dao
 * @author sumit
 */
package com.agnity.utility.cdrsftp.dbpull.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;



public interface CDRPullDao {
	
	/**
	 * Initializes DS and gets connection
	 * This app works on single connection as it is single thread
	 * @throws Exception
	 */
	public void initialize() throws Exception;
	
	/**
	 * Destroys resources created in initialize
	 * This app works on single connection as it is single thread
	 * @throws Exception
	 */
	public void destroy() throws Exception;

	
	/**
	 * returns number of CDRs waiting to be SFTPed
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public int getPendingCdrCnt() throws SQLException, Exception;

	/**
	 * locks the input table
	 * also disables commit on shared connection
	 * @param tableName
	 * @throws SQLException
	 * @throws Exception
	 */
	public void lockTable(String tableName) throws SQLException, Exception;

	/**
	 * get attributes for utility
	 * @return map of attributes and values
	 * @throws SQLException
	 * @throws Exception
	 */
	public Map<String, String> getAttributes() throws SQLException, Exception;
	
	/**
	 * Sets commit to false;
	 * reads n CDRs from db
	 * updates these cdrs status and sent filename in memory
	 * commits if flag is true
	 * @param recordCount
	 * @param updatableFields
	 * @param commitConnAndCloseStatements
	 * @return list of read Cdrs
	 * @throws SQLException
	 * @throws Exception
	 */
	public List<String> fetchAndUpdateCdrsAndNextTs(int recordCount,Map<String,String> updatableFields, boolean commitConnAndCloseStatements) 
			throws SQLException, Exception;

	/**
	 * updates specific attribute in DB
	 * @param name
	 * @param value
	 * @param commitConnAndCloseStatements
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateAttributes(String name, String value, boolean commitConnAndCloseStatements) 
			throws SQLException ,Exception;
	
	public void insertAttributes(String name, String value, boolean commitConnAndCloseStatements) 
			throws SQLException ,Exception;
	
	public String getStoredNextTs();

	public void commitConnAndCloseStatements ()throws SQLException,Exception;
	
	public void releaseConnection ()throws SQLException,Exception;
}
