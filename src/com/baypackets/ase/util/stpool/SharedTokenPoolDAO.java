/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.ase.util.stpool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import oracle.jdbc.OracleStatement;
import org.apache.log4j.Logger;

/**
 * This class abstracts the DB access activities from the rest of the service.
 * All DB related operations are being performed in this class. 
 */
public class SharedTokenPoolDAO {

	/*
	 * logger : Logger instance for logging
	 */
	private static Logger logger = Logger.getLogger(SharedTokenPoolDAO.class);

	/*
	 * dataSource : SAS data source instance
	 */
	private static DataSource dataSource;

	private SharedTokenPoolDAO() {

	}

	/**
	 *  get data source object
	 */
	
	public static void init(){
		
		String PROVIDER_URL = "file:" + System.getProperty("ase.home")
				+ "/jndiprovider/fileserver/";
		String CONTEXT_FACTORY = "com.sun.jndi.fscontext.RefFSContextFactory";
		if (logger.isDebugEnabled()) {
			logger.debug("Getting Data source");
		}
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			InitialContext ctx = new InitialContext(env);
			dataSource = (DataSource) ctx.lookup("APPDB");// TODO: DS name
															// should be
															// configurable
		} catch (Exception e) {
			logger.error("Exception in datasource lookup", e);
		}
	}

	/**
	 * This method attempts to get the DB connection from data source
	 * 
	 * @return Connection : DB connection object
	 */

	public static Connection getConnection(int maxConnCnt) {
		Connection conn = null;
		for (int i = 0; i < maxConnCnt; i++) {
			try {
				conn = dataSource.getConnection();
				if (conn == null) {
					throw new SQLException("Database connection is null");
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Got DB connection");
				}
				break;
			} catch (SQLException ex) {
				conn = null;
				logger.error("Attempt " + i + "; Error in getting connection "
						+ ex.getMessage());

				if (logger.isInfoEnabled()) {
					logger.error("Attempt " + i
							+ "; Error while getting database connection", ex);
				}
			}
		}
		return conn;
	}

	/**
	 * This method is for cleanup of the DB resources. It closes the statements,
	 * connections and result set.
	 * 
	 * @param conn
	 *            represents the instance of Connection
	 * @param stmt
	 *            represents the instance of Statement
	 * @param stmtKey
	 *            represents the String variable.
	 * @param rs
	 *            represents the instance of ResultSet
	 */
	public static void cleanupResources(Connection conn, Statement stmt,
			String stmtKey, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception ex) {
			logger.error("Error while closing resultset.");
			if (logger.isInfoEnabled()) {
				logger.error("Error while closing resultset.", ex);
			}
		}

		try {
			if (stmt != null) {
				if (stmtKey != null) {
					((OracleStatement) stmt).closeWithKey(stmtKey);
				} else {
					stmt.close();
				}
				stmt = null;
			}
		} catch (Exception ex) {
			logger.error("Error while closing statement: " + ex.getMessage());
			if (logger.isInfoEnabled()) {
				logger.error("Error while closing statement.", ex);
			}
		}

		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception ex) {
			logger.error("Error while closing DB connection.");
			if (logger.isInfoEnabled()) {
				logger.error("Error while closing connection.", ex);
			}
		}
	}

	/**
	 * This method performs the roll back of database transaction.
	 * 
	 * @param conn
	 *            represents the instance of Connection
	 */
	public static void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (Exception ex) {
				logger.error("Exception in rolling back transaction: "
						+ ex.getMessage());
				if (logger.isInfoEnabled()) {
					logger.error("Failed to rollback transaction.", ex);
				}
			}
		}
	}
}
