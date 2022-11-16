package com.genband.ase.alcx.DatabaseService;

import java.sql.*;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;


public class DbAccessService {

	
	static Logger logger = Logger.getLogger(DbAccessService.class.getName());
	private static DbAccessService dbAccessObj = null;

	private DbConnectionPool dbConnPoolObj;


	/**
	 * 
	 * Constructor to initialize the class and initialize the dbConnPoolObj.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private DbAccessService(String driverName, String driverUrl,
	        String username, String password,
	        int maxConnections) throws IOException, FileNotFoundException,
			SQLException, ClassNotFoundException {
		
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.DbAccessService() Entering");
		dbConnPoolObj = new DbConnectionPool(driverName, driverUrl,
		        username, password,
		        maxConnections);
		
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.DbAccessService() Leaving");
	}

	/**
	 * 
	 * Public API to invoke the constructor of this class.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void initializeDbAccessService(String driverName, String driverUrl,
	        String username, String password,
	        int maxConnections) throws IOException,SQLException, ClassNotFoundException {
		
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.initializeDbAccessService() Entering");
		if (dbAccessObj == null) {
			dbAccessObj = new DbAccessService(driverName, driverUrl,
			         username,  password,
			         maxConnections);
		}
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.initializeDbAccessService()Leaving");
	}

	/**
	 * 
	 * It is a public API for the outside world to access the only object of
	 * this class
	 * 
	 * @return Returns the dbAccessObj
	 */
	public static DbAccessService getInstance() {
		return dbAccessObj;
	}

	/**
	 * 
	 * API for outside world to access the connection. This would internally get
	 * a connection from the connection pool.
	 * 
	 * @return Returns the connection object.
	 */
	public Connection getConnection() throws SQLException{
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.getConnection() Entering");
		Connection conn = dbConnPoolObj.getConnectionFromPool();
		if (conn == null) 
		{
			return null ; 
		}
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			if(logger.isDebugEnabled())
				logger.debug("DbAccessService.getConnection() Leaving with conn " + conn);
			return conn;
		} catch (SQLException ex) {
			logger.error("DbAccessService.getConnection() Problem in the connection received from pool " + 
					"so returning it back to the pool",ex);
			dbConnPoolObj.putConnectionIntoPool(conn) ; 
			logger.error("DbAccessService.getConnection()"+
					"Leaving with conn " + null,ex);
			throw ex;
			//return null;
		}
	}

	/**
	 * 
	 * API for outside world to release the connection. This would internally
	 * put back the connection in the connection pool.
	 * 
	 * @param conn
	 *            the connection object to be released
	 */
	public void releaseConnection(Connection conn) {
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.releaseConnection() Entering with conn " + conn);
		if (conn == null)
		{
			logger.error("DbAccessService.releaseConnection() The connection is null so can't do anything, so returning");
			return ; 
		}
		try {
			if (!conn.getAutoCommit()) {
				conn.setAutoCommit(true);
			}
		} catch (SQLException ex) {
			logger.error("DbAccessService.releaseConnection()"+
					"Problem while trying to reset the auto commit mode");
		}
		dbConnPoolObj.putConnectionIntoPool(conn);
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.releaseConnection() Leaving");
	}

	/**
	 * 
	 * API for outside world to commit or rollback on a connection
	 * 
	 * @param conn
	 *            The connection on which commit or roll back needs to be fired.
	 * @param commitFlag
	 *            If it is true, then connection would be commited, else rolled
	 *            back.
	 * @throws SQLException
	 */
	public void completeDbAction(Connection conn, boolean commitFlag)
			throws SQLException {
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.completeDbAction()"+
						"Entering with conn " + conn + " and commit Flag "
								+ commitFlag);
		if (conn == null)
		{
			logger.error("DbAccessService.completeDbAction()"+
			"The connection is null so can't do anything, so returning");
			return ; 
		}
		if (commitFlag) {
			conn.commit();
		} else {
			conn.rollback();
		}
		if(logger.isDebugEnabled())
			logger.debug("DbAccessService.completeDbAction() Leaving");
	}

}
