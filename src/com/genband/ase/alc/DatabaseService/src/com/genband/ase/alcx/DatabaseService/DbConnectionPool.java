package com.genband.ase.alcx.DatabaseService;

import java.util.*;
import java.sql.*;

import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;



public class DbConnectionPool {

	private static int MAX_CONNECTIONS;
	private String driverName, driverUrl, username, password;
	private static Vector<Connection> locked;

	private static Vector<Connection> unlocked;

	static Logger logger = Logger.getLogger(DbConnectionPool.class.getName());

	private static Properties prop;

	/**
	 * 
	 * Constructor to initialize the DB Connection Pool.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected DbConnectionPool(String driverName, String driverUrl,
	        String username, String password,
	        int maxConnections) throws FileNotFoundException, IOException,
			SQLException, ClassNotFoundException {
		super();
		
		if(logger.isDebugEnabled())
		logger.debug("DbConnectionPool.DbConnectionPool() Entering");
		
		locked = new Vector<Connection>();
		unlocked = new Vector<Connection>();
		
		MAX_CONNECTIONS = maxConnections;
		this.driverName =driverName;
		this.driverUrl =driverUrl;
		this.username =username;
		this.password =password;
		
		initializeConnections();
		
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.DbConnectionPool() Leaving");
	}

	/**
	 * 
	 * API to initialize the connections to the database.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void initializeConnections() throws SQLException,
			ClassNotFoundException {
		
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.initializeConnections() Entering");
		// Note : This method will throw a ClassNotFoundException if the
		// appropriate JDBC driver is not loaded
		
		Class.forName(this.driverName);

		if (unlocked.size() <= 0) {
			for (int i = 0; i < MAX_CONNECTIONS; i++) {
				Connection conn;
				// Note : This method will throw an SQLException : No suitable driver
				// if the appropriate JDBC driver is not loaded
				conn = create();
				unlocked.add(conn);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.initializeConnections() Leaving");
	}

	/**
	 * 
	 * API to create a new connection and return to the caller.
	 * 
	 * @return	Returns the Connection object created.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	private Connection create() throws SQLException {
		
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.create() Entering");
		
		
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.create() Leaving");
		return (DriverManager.getConnection(driverUrl,this.username ,this.password));
	}

	

	/**
	 * 
	 * API to put back the used connection in the pool, so that it 
	 * maybe used by other requests.
	 *  
	 * @param conn	The connection object that is to be returned to the pool
	 */
	protected void putConnectionIntoPool(Connection conn) {
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.putConnectionIntoPool() Entering with conn " + conn);
		
		locked.remove(conn);
		unlocked.add(conn);
		
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.putConnectionIntoPool() Leaving");
	}

	/**
	 * 
	 * API to get a connection from the Pool.
	 * 
	 * @return	The connection object.
	 */
	protected Connection getConnectionFromPool() {
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.getConnectionFromPool() Entering");
		Connection conn = null;
		if (unlocked.size() > 0) {
			conn = getConnection();
			
			if(logger.isDebugEnabled())
				logger.debug("DbConnectionPool.getConnectionFromPool() Leaving with conn " + conn);
			return conn;
		} else {
			if(logger.isDebugEnabled())
				logger.debug("DbConnectionPool.getConnectionFromPool() No Usable Connections left in the pool");
			if(logger.isDebugEnabled())
				logger.debug("DbConnectionPool.getConnectionFromPool()Leaving with conn " + null);
			return null;
		}
	}

	/**
	 * 
	 * API to validate if a connection is stale/invalid or not.
	 * 
	 * @param conn	The connection object to be checked.
	 * @return		Returns a Boolean i.e. True if connection is valid and 
	 * 				False if connection is stale
	 */
	private boolean validate(Connection conn) {
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.validate() Entering with conn "
				+ conn);
		try {
			
			if(!conn.isValid(2)){
				if(logger.isDebugEnabled()){
					logger.debug("DbConnectionPool.validate() Connection "+ conn + " is invalid");
				}
				return false;
			}
			if (conn.isClosed()) {
				if(logger.isDebugEnabled())
					logger.debug("DbConnectionPool.validate()Leaving - value : false");
				return false;
			} else {
				if(logger.isDebugEnabled())
					logger.debug("DbConnectionPool.validate() Leaving - value : true");
				return true;
			}
		} catch (SQLException e) {
			
				logger.error("DbConnectionPool.validate() Leaving - Found connection to be stale",e);
			return (false);
		}
	}

	/**
	 * 
	 * API to close a connection
	 * 
	 * @param conn	The connection object to be closed.
	 * @throws SQLException
	 */
	private void expire(Connection conn) throws SQLException {
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.expire() Entering with conn "
				+ conn);
		conn.close();
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.expire()Leaving");
	}

	/**
	 * 
	 * API checks for a valid connection in the pool and returns it to the caller. 
	 * If it finds a stale connection, it closes and recreates it
	 * 
	 * @return	The Connection object.
	 */
	private Connection getConnection() {
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.getConnection() Entering");
		Connection conn = null;
		if (unlocked.size() > 0) {
			Enumeration e = unlocked.elements();
			while (e.hasMoreElements()) {
				conn = (Connection) e.nextElement();
				if (validate(conn)) {
					unlocked.remove(conn);
					locked.add(conn);
					if(logger.isDebugEnabled())
						logger.debug("DbConnectionPool.getConnection()Leaving with conn " + conn);
					return (conn);
				} else {
					// object failed validation
					unlocked.remove(conn);
					try {
						expire(conn);
						conn = create();
					} catch (SQLException ex) {
						continue;
					}
					unlocked.add(conn);
					if(logger.isDebugEnabled())
						logger.debug("DbConnectionPool.getConnection()Leaving with conn " + conn);
					return (conn);
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("DbConnectionPool.getConnection()Leaving with conn " + conn);
		return conn;
	}
}
