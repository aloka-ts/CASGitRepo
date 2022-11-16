package com.agnity.utility.cdrsftp.dbpull.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;
import javax.sql.rowset.JdbcRowSet;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.dbpull.dao.CDRPullDao;
import com.agnity.utility.cdrsftp.dbpull.utils.Constants;
import com.sun.rowset.JdbcRowSetImpl;

public class CDRPullDaoImpl  implements CDRPullDao{
	
	private static Logger logger = Logger.getLogger(CDRPullDaoImpl.class);
	
	private String cdrCntQuery;
	private String attributeFetchQuery;
	private String cdrFetchQuery;
	private String updateAttributeQuery;
	private String insertAttributeQuery;
	private String tsColumn;
	
	private String nextTs;
	
	private DataSource dataSource; 
	private Connection conn;
	
	//used default as it will have hetrogeneous objects
	private List<Statement> closeStatements;
	private List<ResultSet> closeResultSets;
	

	@Override
	public void initialize() throws Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  initialize()");
		
		try {
			//added as saftey to limit number of connections
			if(conn!=null){
				try{
					releaseConnection();
				}finally{
					try {
						if(!conn.isClosed()){
							conn.close();
						}
						conn=null;
					} catch (Exception e) {
						logger.warn("Reinitialize--Error releasing old connection--ignoring",e);
					}
				}
			}
			
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			closeStatements = new ArrayList<Statement>();
			closeResultSets = new ArrayList<ResultSet>();
			
		} catch (Exception e) {
			logger.error("error getting connection",e);
			throw e;
		}
				
		if(isDebug)
			logger.debug("Leave  CDRPullDaoImpl  initialize()");
		
	}
	
	private Connection getConnection() throws Exception{
		boolean isDebug=logger.isDebugEnabled();
		try {
			if(conn.isClosed()){
				if(isDebug)
					logger.debug("getConnection() closed connection reInitialize");
				initialize();
			}
			
			if(conn.isValid(10)){
				if(isDebug)
					logger.debug("getConnection() valid connection");
				return conn;
			}else{
				logger.error("Connection invalid restart");
				throw new Exception("DB connection is not valid; EXIT process");
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage(),e);
		}
		
	}
	
	
	@Override
	public void lockTable(String tableName) throws SQLException ,Exception {
		Object[] params={tableName};
		String command=MessageFormat.format(Constants.LOCK_QUERY, params);
		Statement lockStatement = getConnection().createStatement();
		lockStatement.execute(command);
		closeStatements.add(lockStatement);
	}
	
	@Override
	public int getPendingCdrCnt() throws SQLException ,Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  getPendingCdrCnt()");
		int cnt = 0;
		PreparedStatement stmt = getConnection().prepareStatement(cdrCntQuery);
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			cnt = rs.getInt(1);
		}
		
		rs.close();
		stmt.close();

		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl getPendingCdrCnt()::"+cnt);
		return cnt;
	}
	
	@Override
	public Map<String, String> getAttributes() throws SQLException, Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  getAttributes()");
		Map<String, String> attrMap = new HashMap<String, String>();
		PreparedStatement stmt = getConnection().prepareStatement(attributeFetchQuery);
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			attrMap.put(rs.getString(1),rs.getString(2));
		}
		
		rs.close();
		stmt.close();

		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl getAttributes()::"+attrMap);
		return attrMap;
	}

	
	@Override
	public List<String> fetchAndUpdateCdrsAndNextTs(int maxRecordCount,Map<String,String> updatableFields, boolean commitConnAndCloseStatements) 
										throws SQLException ,Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  fetchAndUpdateCdrs()");
		JdbcRowSet rowSet = new JdbcRowSetImpl(getConnection());
		rowSet.setReadOnly(false);
		rowSet.setCommand(cdrFetchQuery);
		rowSet.execute();
		List<String> cdrData = new ArrayList<String>();
		Set<Entry<String, String> > entrySet=updatableFields.entrySet();
		int readRecords =0;
		while (rowSet.next()) {
			cdrData.add(rowSet.getString(1));
			//updating rowSet with input data		
			for(Entry<String,String> entry: entrySet){
				rowSet.updateString(entry.getKey(),entry.getValue());
			}
			rowSet.updateRow();
			
			readRecords++;
			if (readRecords >= maxRecordCount) {
				break;
			}//end of read records
		}//end if rowset.next
		//saving current rowset for reuse
		if(rowSet.next()){
			nextTs = rowSet.getString(tsColumn);
		}
		closeResultSets.add(rowSet);
		if(commitConnAndCloseStatements){
			commitConnAndCloseStatements();
		}
		
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl fetchAndUpdateCdrs()::");
		return cdrData;
	}
	
	@Override
	public void updateAttributes(String name, String value, boolean commitConnAndCloseStatements) 
										throws SQLException ,Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  updateAttributes()");
		PreparedStatement stmt = getConnection().prepareStatement(updateAttributeQuery);
		stmt.setString(1, value);
		stmt.setString(2, name);
		stmt.executeUpdate();
		
		closeStatements.add(stmt);
		if(commitConnAndCloseStatements){
			commitConnAndCloseStatements();
		}
		
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl updateAttributes()::");

	}
	
	@Override
	public void insertAttributes(String name, String value, boolean commitConnAndCloseStatements) 
										throws SQLException ,Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  insertAttributes()");
		PreparedStatement stmt = getConnection().prepareStatement(insertAttributeQuery);
		stmt.setString(1, name);
		stmt.setString(2, value);
		stmt.executeUpdate();
		closeStatements.add(stmt);
		if(commitConnAndCloseStatements){
			commitConnAndCloseStatements();
		}
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl insertAttributes()::");

	}
	
	@Override
	public String getStoredNextTs() {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  getStoredNextTs()");
		
		String nextTsTmp = nextTs;
		nextTs = null;
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl getStoredNextTs()::"+nextTsTmp);
		return nextTsTmp;

	}

	@Override
	public void commitConnAndCloseStatements() throws SQLException, Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  commitConnAndCloseStatements()");
		
		try {
			if(!conn.isClosed()){
				conn.commit();
			}
		} catch (Throwable e) {
			logger.error("Error commiting", e);
			getConnection().close();
		} finally {

			for (ResultSet rs : closeResultSets) {
				try {
					rs.close();
				} catch (Throwable e) {
					logger.error("Unable to close rs::" + rs, e);
				}
			}

			for (Statement stmt : closeStatements) {
				try {
					stmt.close();
				} catch (Throwable e) {
					logger.error("Unable to close statement::" + stmt, e);
				}
			}
		}
		
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl commitConnAndCloseStatements()");
		
	}

	@Override
	public void releaseConnection() throws SQLException, Exception {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CDRPullDaoImpl  releaseConnection()");
		
		try {
			if(!conn.isClosed()){
				conn.rollback();
			}
		} catch (Throwable e) {
			logger.error("Error rollback", e);
			getConnection().close();
		} finally {

			for (ResultSet rs : closeResultSets) {
				try {
					rs.close();
				} catch (Throwable e) {
					logger.error("Unable to close rs::" + rs, e);
				}
			}

			for (Statement stmt : closeStatements) {
				try {
					stmt.close();
				} catch (Throwable e) {
					logger.error("Unable to close statement::" + stmt, e);
				}
			}
		}
		
		if(isDebug)
			logger.debug("leaving CDRPullDaoImpl releaseConnection()");
		
	}
	
	/**
	 * @param simpleJdbcTemplate the simpleJdbcTemplate to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the simpleJdbcTemplate
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public String getCdrCntQuery() {
		return cdrCntQuery;
	}

	public void setCdrCntQuery(String cdrCntQuery) {
		this.cdrCntQuery = cdrCntQuery;
	}

	public String getAttributeFetchQuery() {
		return attributeFetchQuery;
	}

	public void setAttributeFetchQuery(String attributeQuery) {
		this.attributeFetchQuery = attributeQuery;
	}

	public String getCdrFetchQuery() {
		return cdrFetchQuery;
	}

	public void setCdrFetchQuery(String cdrFetchQuery) {
		this.cdrFetchQuery = cdrFetchQuery;
	}

	public String getUpdateAttributeQuery() {
		return updateAttributeQuery;
	}

	public void setUpdateAttributeQuery(String updateAttributeQuery) {
		this.updateAttributeQuery = updateAttributeQuery;
	}

	public String getInsertAttributeQuery() {
		return insertAttributeQuery;
	}

	public void setInsertAttributeQuery(String insertAttributeQuery) {
		this.insertAttributeQuery = insertAttributeQuery;
	}

	public String getTsColumn() {
		return tsColumn;
	}

	public void setTsColumn(String tsAttribute) {
		this.tsColumn = tsAttribute;
	}

	@Override
	public void destroy() throws Exception {
		try{
			releaseConnection();
		}finally{
			try {
				if(!conn.isClosed()){
					conn.close();
				}
				conn=null;
			} catch (Exception e) {
				logger.warn("Reinitialize--Error releasing old connection--ignoring",e);
			}
		}
		
	}

	
}
