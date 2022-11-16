
package com.baypackets.ase.jndi_jdbc.ds;
import javax.sql.*;
import javax.naming.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.baypackets.ase.jndi_jdbc.util.*;
import javax.naming.event.*;
import org.apache.log4j.*;

/** This class DataSourceImpl implements DataSource, Referenceable and ConnectionEventListener interfaces
* It contains the instance variable as ObjectPool, DataBase info, hashtable.
* ObjectPool creates the connection, adjust the numbr of connections and closes the connections.
* DatabaseInfo stores the initial information from the SasDataSOurceFactory.
* Hashtable ObjectPoolStaticStorage stores the initial state of the DataSourceIMpl
* Hashtable DatabaseStaticStorage stores the databse accessing information and initial information i.e. like minpoolsize, maxpoolsize etc.
*/


public class DataSourceImpl implements DataSource, Referenceable, ConnectionEventListener
{
	private  ObjectPool poolOfPooledConnection=null;
          	
	private Properties DatabaseInfo=null;        //datastructure to store that information
    
  	private static Hashtable ObjectPoolStaticStorage=new Hashtable();  //it stores the pool initialization information
	private static Hashtable DatabaseStaticStorage=new Hashtable(); //storing the database user/password information.
	private String DatasourceImplName=null;

	private static Logger logger=Logger.getLogger(DataSourceImpl.class);


	public DataSourceImpl()
	{
		if (logger.isInfoEnabled()) {
			logger.info("Empty constructor is called");
		}
	}

/** This constructor recreates the DataSourceImpl object with the old properties by fetching from the static storage
* @param str Its a key to fetch the old state from the static storage
*/

	public DataSourceImpl(String StaticStorageKey) 
	{
		try
	    	{
				if (logger.isInfoEnabled()) {
					logger.info("DataSourceImpl Has been initiated");
				}
			poolOfPooledConnection=(ObjectPool)ObjectPoolStaticStorage.get(StaticStorageKey);		//fetching the objectpool from the storage


			DatabaseInfo=(Properties)DatabaseStaticStorage.get(StaticStorageKey);
			if (logger.isInfoEnabled()) {
				logger.info(poolOfPooledConnection);
				logger.info(DatabaseInfo);
			
				logger.info("The key is ==== > "+StaticStorageKey);
			}
			DatasourceImplName=StaticStorageKey;

		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
		}


	}
/** This constructor initailizes the DataSourceImpl object. It creates the initial ConnectionPool by calling ObjectPool constructor.
* It also store  state of object pool and initializing information (rread from the deployment descriptor).
* @param prop its the initial information as obtained from the SasDataSourceFactory.
*/

 	public DataSourceImpl(Properties prop) //throws SQLException,IOException  //This constructor intializes the connection pool
 	{
  

		try
		{
			
			DatabaseInfo=prop;
			poolOfPooledConnection=new ObjectPool(this);		//this creates the pool
				
			DatasourceImplName=(String)DatabaseInfo.get("name");
			if (logger.isInfoEnabled()) {
				logger.info("DataSourceImplName=====> "+DatasourceImplName);
			}
			ObjectPoolStaticStorage.put(DatasourceImplName,poolOfPooledConnection);
			DatabaseStaticStorage.put(DatasourceImplName,DatabaseInfo);
			if (logger.isInfoEnabled()) {
				logger.info("DataSource Impl has been initiated");
			}
		}
		catch(Exception execp)
		{
				
			logger.error(execp.toString(),execp);
		}
	
 	}
    
    

   //***************************************************************************************************************//


	public boolean setinitialconfiguration(Properties prop)
	{
		try
		{
			DatabaseInfo=new Properties();
			DatabaseInfo=prop;
			return true;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return false;
		}
	}



//********************************************************************************************************************//		
/** This method gives a logical connection to the aspiring application.
* @return  It returns the connection.
*/
 	public synchronized Connection getConnection()throws SQLException		//getting a connection from the pool
    	{
		Connection conn=null;
		PooledConnection pool=null;
		
		
		try
		{
			pool=(PooledConnection)poolOfPooledConnection.checkOut();
			pool.addConnectionEventListener(this);
			
			conn=pool.getConnection();

			//conn.setFlag();
			
			return conn;
		}
		catch(SQLException e)
		{
			
			
			logger.error(e.toString(),e);
			throw e;
			
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}

 	}
 /** It returns the initializing parameters as obtained from SasDataSourceFactory  
* @return it returns the property
*/
	public Properties getinitialconfig()
  	{
   		return DatabaseInfo;
  	}

/** This method is not implemented
*/


	public void ConnectionClosed()
	{
		if (logger.isInfoEnabled()) {
			logger.info("IT does nothing");
    	}
	}

/** This method is not implemented
*/


 	public void connectionErrorOccurred()
	{
		if (logger.isInfoEnabled()) {
			logger.info("It does nothing");	
		}
  	}

/** This method gives a reference to the object which we bounded to the name.
* @return It returns the reference On the basis of this reference the object is recreated during lookuptime.
*/


 	public Reference getReference() 			//returning the reference of the datasource
    	{
    		try
    		{
				if (logger.isInfoEnabled()) {
					logger.info("Reference method has been called");
				}
			String classname=DataSourceImpl.class.getName();
			if (logger.isInfoEnabled()) {
				logger.info("ClassName=====> "+classname);
			}
			String classfactoryname=DataSourceImplFactory.class.getName();
			if (logger.isInfoEnabled()) {
				logger.info("ClassFactoryName=====> "+classfactoryname);
			}
			StringRefAddr classref=new StringRefAddr("DatasourceImplName",DatasourceImplName);

			return new Reference(classname,classref,classfactoryname,null);
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			return null;
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
 	public void setLogWriter(PrintWriter out) throws SQLException
 	{
    		if (logger.isInfoEnabled()) {
				logger.info("This method is not being implemented");	
			}
  	}
/** This method is not implemented
*/
	public Connection getConnection(String name,String password) throws SQLException
    	{

		Connection conn=null;
                PooledConnection pool=null;

 		String userNameDatabaseInfo=null;
 		String passwordDatabaseInfo=null;
		
		try
		{
			userNameDatabaseInfo=(String)DatabaseInfo.get("username");
			passwordDatabaseInfo=(String)DatabaseInfo.get("password");

		}

		catch(NullPointerException enull)
		{
			logger.error("Null Pointer Exception");
			logger.error("getConnection(user,password)===>");
			logger.error(enull.toString(),enull);
			return null;
		}


		if((userNameDatabaseInfo.equals(name))&&(passwordDatabaseInfo.equals(password)))
		{
			try
                	{
                        	pool=(PooledConnection)poolOfPooledConnection.checkOut();
                        	pool.addConnectionEventListener(this);

                        	conn=pool.getConnection();

				//conn.setFlag();

                        	return conn;
                	}
                	catch(SQLException e)
                	{


                        	logger.error(e.toString(),e);
                        	throw e;

                	}
                	catch(Exception e)
                	{
                        	logger.error(e.toString(),e);
                        	return null;
                	}
		}
		if (logger.isInfoEnabled()) {
			logger.info("The user name and password supplied by the user are wrong");
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

	public String toString()
	{
		try
		{
			String classname=DataSourceImpl.class.getName();
			return classname;	
		
		
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
	}
/** This method closes the logical connection 
*/
	public void connectionClosed(ConnectionEvent event)
	{
		
		try
		{
			PooledConnectionImpl pool=(PooledConnectionImpl)event.getSource();
			poolOfPooledConnection.checkIn(pool);
		}
		catch(SQLException e)
		{

			logger.error(e.toString(),e);
		}
		catch(IOException e)
		{
		
		
			logger.error(e.toString(),e);
		}
	}
	public void connectionErrorOccurred(ConnectionEvent event)
	{

		SQLException e=event.getSQLException();
		if (logger.isInfoEnabled()) {
			logger.info(e.toString(),e);
		}
	}
/** This method closes the datasource ,completely  closes all the connections.
*/
	public void close() throws SQLException		//closing the total connections 
	{
		try
		{
			try
			{
				ObjectPoolStaticStorage.remove((String)DatasourceImplName);
				DatabaseStaticStorage.remove((String)DatasourceImplName);
			}
			
			catch(Exception e)
			{
				logger.error(e.toString(),e);
				logger.error("CLEANING THE STATIC STORAGE");
			}
		
			poolOfPooledConnection.close();
			if (logger.isInfoEnabled()) {
				logger.info("DataSourceImpl is closed");
			}
		}
		catch(SQLException e1)
		{
			logger.error(e1.toString(),e1);
			throw e1;
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
		}
	}
	
/** This method for giving the free connetions in the connectionpool
* @return the number of free connections
*/
	
	public int getFreeConnections()	throws Exception			//returning the total number of free connections.
	{
		try
		{
			return poolOfPooledConnection.getFreeConnections();
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			throw ee ;
		}
	}
/** This method return the number of busy connections
* @return no. of connections that are busy
*/
	public int getBusyConnections()	throws Exception			//returning the total number of busy connections.
	{
		try
		{
		
			return poolOfPooledConnection.getBusyConnections();
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
	}
	
}



