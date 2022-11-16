package com.baypackets.ase.jndi_jdbc.ds;

import javax.sql.*;
import javax.naming.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.baypackets.ase.jndi_jdbc.util.*;
import org.apache.log4j.*;

/** This class implements the interface sql.ConnectionPoolDataSource
* This class creates the PooledConnection 
*/


public class ConnectionPoolDataSourceImpl implements ConnectionPoolDataSource
{
	private DataSourceImpl datasourceimpl=null;
	static Logger logger=Logger.getLogger(ConnectionPoolDataSourceImpl.class);
 	public ConnectionPoolDataSourceImpl(DataSourceImpl datasourceimp) 
	{
		try
	    	{
			datasourceimpl=datasourceimp;
			if (logger.isInfoEnabled()) {
				logger.info("ConnectionPoolDataSourceImpl object is initiated");
            }    
        	}
        	catch(Exception e)
        	{
			logger.error(e.toString(),e);
                       
        	}

		
	}

 /** This method is not implemented
*/   

	public void connectionClosed(PooledConnection pool) throws SQLException, IOException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
		}
	}

/** This method gives the pooledconnection, each pooledconnection contains the logical connection.
*/

	public PooledConnection getPooledConnection() throws SQLException
	{
		try
		{
			PooledConnectionImpl poolconnimpl=new PooledConnectionImpl();
			if (logger.isInfoEnabled()) {
				logger.info("I am in the connectionpooldatasource and pooledconnectionobject is initiated");
			}
			poolconnimpl.initialize(datasourceimpl);
		
			return poolconnimpl;
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error("Exception in getPooledConnection in ConnectionPoolDataSourceImpl class"+e);
			logger.error(e.toString(),e);
			return null;
		}
			
		
	}

/** This method is not implemented
*/

	public PooledConnection getPooledConnection(String user,String password) throws SQLException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
        }	
			return null;
	
	}

/** This method is not implemented
*/

	public PrintWriter getLogWriter() throws SQLException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
		}
		return null;
	}

/** This method is not implemented
*/

	public void setLogWriter(PrintWriter pout) throws SQLException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
		}
	}

/** This method is not implemented
*/

	public void setLoginTimeout(int seconds) throws SQLException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
		}
	}

/** This method is not implemented
*/
	
	public int getLoginTimeout() throws SQLException
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method is not being implemented");
		}
		return 0;
	}
	

}
		
				

