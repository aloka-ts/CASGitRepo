package com.baypackets.ase.jndi_jdbc.ds;

//import java.sql.Connection;
import java.sql.*;
import java.util.*;
import java.io.*;
import javax.sql.*;
import com.baypackets.ase.jndi_jdbc.util.*;
import org.apache.log4j.*;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;


/** This class wraps the phyical connection object. It delegates all the methods to the underlying physical connection obejct except
* the close() and prepareStatement() method.
*/

public class LogicalConnection implements Connection
{
	private PooledConnectionImpl pooledConnection=null;
	private Connection connection=null;
	private Properties databaseInfo=null;	
	private Connection realConnection=null;
	private static Logger logger=Logger.getLogger(LogicalConnection.class);
	private AseAlarmService alarmService;
	
	private boolean setFlagClose=true;
	
	public LogicalConnection()
	{
		if (logger.isInfoEnabled()) {
			logger.info("Logical Connection instance is initiated");
		}
		databaseInfo=new Properties();
	}
	
	public LogicalConnection(PooledConnectionImpl pooledConnectionImpl) throws SQLException,IOException
	{
		
        	try
		{
			if (logger.isInfoEnabled()) {
				logger.info("LogicalConnection(Property) Constructor");
			}
			pooledConnection=pooledConnectionImpl;
			if (logger.isInfoEnabled()) {
				logger.info("Logical Connection instance is initiated");
			
				//****************************************************************************
				//i have to read this information from pooledconnection object
					logger.info(" pooledConnectionImpl is  "+pooledConnection);
			}

			databaseInfo=pooledConnection.databaseinfo;
			if (logger.isInfoEnabled()) {
				logger.info(" The databaseinfo is "+databaseInfo);
            }                                                                                                             
        		String url=(String)databaseInfo.get("url");
			if (logger.isInfoEnabled()) {
				logger.info("URL====> "+url);
			}
        		String username=(String)databaseInfo.get("username");
			if (logger.isInfoEnabled()) {
				logger.info("User Name===> "+username);
			}
        		String password=(String)databaseInfo.get("password");
			String drivername=(String)databaseInfo.get("drivername");
			if (logger.isInfoEnabled()) {
				logger.info("Driver Name=====> "+drivername);
			}
			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);


			try
			{
				//DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				DriverManager.registerDriver((Driver)Class.forName(drivername).newInstance());

		 		realConnection=DriverManager.getConnection(url,username,password);
				connection=realConnection;
				if (logger.isInfoEnabled()) {
					logger.info("Connection established");
				}
			}
			catch(SQLException esq)
			{
				logger.error(esq.toString(),esq);
				String alarmMsg="Unable to connect to the DataBase "+url;
				this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_UNABLE_CONNECT_DATABASE, alarmMsg );

				throw esq;
			}


		
		}

			
		catch(Exception een)
		{
			logger.error(een.toString(),een);
		}
		
	}

/** This method is not being implemented.
*/
	
	
	public Connection getConnection(String username,String password, String url) throws SQLException,IOException
	{
		logger.error("This method is not being implemented");
 		return null;

	}

/** This method closes the physical connection 
*/

	public void finalclose() throws SQLException
	{
		try
		{
			realConnection.close();
			if (logger.isInfoEnabled()) {
				logger.info("finalclose():Closing the physical connection");
			}
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			throw e;
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
		}
	}
		
	public void clearWarnings() 
	{
		try
		{
			if(setFlagClose==true)
			{
				connection=realConnection;
			}
		
		
			connection.clearWarnings();
		}
		catch(SQLException e)
		{
			
			logger.error(e.toString(),e);

		}
	}


//**************************************************************************************//
	public void close() 
	{
		try
		{
			if(setFlagClose)
			{
				setFlagClose=false;
				connection=null;
				if (logger.isInfoEnabled()) {
					logger.info("Logical Connection is closed");
				}
				pooledConnection.logicalConnectionClosed();
			}
			
		}
	
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
		
	}
//**************************************************************************************//
	public void commit() 
	{
		try
		{
			 if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.commit();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
		}
	}

	public Statement createStatement() 
	{
		try
		{
		         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.createStatement();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) 
	{
		try
		{

	                 if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.createStatement(resultSetType,resultSetConcurrency);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
	

	public boolean getAutoCommit() 
	{
		try
		{

	                 if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.getAutoCommit();
		}
		catch(SQLException e)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(e.toString(),e);
			 }
			 return false;
		}
	}
	
	public String getCatalog() 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.getCatalog();
		}
		catch(SQLException e)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(e.toString(),e);
			}
			return null	;
		}
	}

	public DatabaseMetaData getMetaData() 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.getMetaData();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
	
	public int getTransactionIsolation() 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.getTransactionIsolation();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return 0;
		}
	}
//**********************************************************	
	public Map getTypeMap() 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
		
			return connection.getTypeMap(); 
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
//***********************************************************
	public SQLWarning getWarnings() 
	{	
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.getWarnings();
		}
		catch(SQLException e)
		{	
			logger.error(e.toString(),e);
			return null;
		}
	 }
	
	public boolean isClosed() 
	{
		try
		{

	                if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.isClosed();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return false;
		} 	
	}
	
	
	public boolean isReadOnly() 
	{
		try
		{

			if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }	
			return connection.isReadOnly(); 
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return false;
		}	
	}
	
	
	public String nativeSQL(String sql) 
	{
		try
		{	

                          if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.nativeSQL(sql);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		}
 	
	}
	
	
	public CallableStatement prepareCall(String sql) 
	{
		try
		{	

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareCall(sql);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			return null;
		} 	
	}
	
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) 
	{	
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareCall(sql,resultSetType,resultSetConcurrency);
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	
	}
	
	

	//*******************************************************************************************
	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
	
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }

			return pooledConnection.prepareStatementCalled(sql,connection);  
			
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error("Error in PreparedStatement ");
			return null;
		}
	}

	//*****************************************************************************************
	
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) 
	{
		try
		{

	                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			 return connection.prepareStatement(sql,resultSetType,resultSetConcurrency);
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}	
	}
	
	
	public void rollback() 
	{
		try
		{

	                if(setFlagClose==true)
                        {
                                connection=realConnection;
                        } 
			connection.rollback();
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			
		}
	}
	
	public void setAutoCommit(boolean autoCommit) 
	{
		try
		{	

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.setAutoCommit(autoCommit);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
		}

	}
	
	public void setCatalog(String catalog) 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.setCatalog(catalog);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			
		}

	}
	
	public void setReadOnly(boolean readOnly) 
	{
		try
		{
                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
	

			connection.setReadOnly(readOnly);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
		}
 	}
	
	public void setTransactionIsolation(int level) 
	{
		try
		{
                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
	
			connection.setTransactionIsolation(level);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
		}
	}
	
	public void setTypeMap(Map map) 
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.setTypeMap(map);
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
		}
			 	
	}

	public void setHoldability(int holdability) throws SQLException
	{
		try
		{
                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.setHoldability(holdability);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
	}

	public int getHoldability() throws SQLException
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
		
			return connection.getHoldability();
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return 0;
		}
		

	}

	public Savepoint setSavepoint() throws SQLException
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.setSavepoint();
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
	public Savepoint setSavepoint(String name) throws SQLException
	{
		try
		{
			if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.setSavepoint(name);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
	public void rollback(Savepoint savepoint) throws SQLException
	{
		try
		{
                        if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.rollback(savepoint);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
		
		
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		try
		{

	                if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			connection.releaseSavepoint(savepoint);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
	}
	public PreparedStatement prepareStatement(String sql,String[] columnNames) throws SQLException
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareStatement(sql,columnNames);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
		
	}
	
	public PreparedStatement prepareStatement(String sql,int[] columnIndexes) throws SQLException
	{
		try
		{
			return connection.prepareStatement(sql,columnIndexes);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}

	public PreparedStatement prepareStatement(String sql,int autoGeneratedKeys) throws SQLException
	{
		try
		{

	                if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareStatement(sql,autoGeneratedKeys);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}

	public CallableStatement prepareCall(String sql,int resultSetType,int resultSetConcurrency,int resultSetHoldability) throws SQLException
	{
		try
		{

                         if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareCall(sql,resultSetType, resultSetConcurrency,resultSetHoldability);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}

	public Statement createStatement(int resultSetType,int resultSetConcurrency,int resultSetHoldability) throws SQLException
	{
		try
		{


			if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.createStatement(resultSetType, resultSetConcurrency,resultSetHoldability);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
	public PreparedStatement prepareStatement(String sql,int resultSetType,int resultSetConcurrency,int resultSetHoldability) throws SQLException
	{
		try
		{

                        if(setFlagClose==true)
                        {
                                connection=realConnection;
                        }
			return connection.prepareStatement(sql,resultSetType, resultSetConcurrency,resultSetHoldability);
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}

	public void setFlag()
	{
		setFlagClose= true;
	}

}
