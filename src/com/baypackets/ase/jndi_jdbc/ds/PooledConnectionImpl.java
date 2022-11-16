package com.baypackets.ase.jndi_jdbc.ds;


import javax.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.baypackets.ase.jndi_jdbc.util.*;
import org.apache.log4j.*;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
/** This class implements the javax.sql.PooledConnection interface. This class creates a logical connection object for each 
* getConnection() and returns the same
*/


public class PooledConnectionImpl implements PooledConnection
{

	private  LogicalConnection logicalconnection=null;

	private ConnectionEvent connevent=null;
	private DataSourceImpl dataSourceImpl=null;
	public Properties databaseinfo=null;
	private Connection connection=null;    //undone
	private ObjectCache objectcache=null;   //Prepared statement cache added today
	private int cacheSize=0;
	private AseAlarmService alarmService;
	
	private static Logger logger=Logger.getLogger(PooledConnectionImpl.class);

	public PooledConnectionImpl()
	{
		try
	    	{
            		objectcache=new ObjectCache();


        	}
        	catch(Exception e)
        	{

			logger.error(e.toString(),e);
                       
        	}
        
        
		
	}
/** This method makes the pysical connection indirectly by calling the constructor of LogicalConnection class.
* @param datasourceimp It gives DataSourceImpl object as parameter.
*/



	public void initialize(DataSourceImpl datasourceimp) throws SQLException
	{
		try
		{
		
			dataSourceImpl=datasourceimp;
	
			//databaseinfo=datasourceimpl.DatabaseInfo;
			databaseinfo=dataSourceImpl.getinitialconfig();

			logicalconnection=new LogicalConnection(this);

			String cachesize=(String)databaseinfo.get("cachesize");
			cacheSize=Integer.parseInt(cachesize);
			if (logger.isInfoEnabled()) {
				logger.info("The cache size is "+cacheSize);
			}
			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);

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

/** This method gives the logical connection
*/



	public synchronized Connection getConnection() throws SQLException
	{
		logicalconnection.setFlag();
		if (logger.isInfoEnabled()) {
			logger.info("Logical Connection Flag has been setted to true");
		}
		return logicalconnection;
	}


	public void setFlag()
	{
		logicalconnection.setFlag();
	}
                                                                                                                             
	
/** This method permanently closes the physical connection
*/
	
	public synchronized void close() throws SQLException
	{
		
		try
		{
		
			logicalconnection.finalclose();
		}
		catch(SQLException e)
		{
				
			logger.error(e.toString(),e);
			throw e;
		}
	}
/** This method is not being implemented
*/

	public void addConnectionEventListener(ConnectionEventListener conn)
	{
		
		;
	}

/** This method is not being implemented
*/

	
	public void removeConnectionEventListener(ConnectionEventListener conn)
	{
		;
	}
//////////////////////////////////////////////////////////////	

/** This method returns the LogicalPrepareStatement from the cache if it is present otherwise it gives a new one.
*/



	public synchronized LogicalPrepareStatement prepareStatementCalled(String sql,Connection conn)throws SQLException,IOException
	{
		if(conn==null)
			return null;
		try
		{
			if(cacheSize>0)
			{
			
				if(objectcache.size()>0)
				{

					LogicalPrepareStatement logicalpreparestatement1=(LogicalPrepareStatement)objectcache.get(sql);
					if(logicalpreparestatement1==null)
					{
						logicalpreparestatement1=new LogicalPrepareStatement(sql,conn,this);
						if(objectcache.size()<cacheSize)
						{
							objectcache.put(sql,logicalpreparestatement1);
							if (logger.isInfoEnabled()) {
								logger.info("There are "+objectcache.size()+" LogicalPreparedStatement in the Object Cache");			
							}
							return logicalpreparestatement1;
						}
						else
						{
							if(objectcache.removeleastrecentlyused()==true)
							{
								objectcache.put(sql,logicalpreparestatement1);
								return logicalpreparestatement1;
							}
							else
							{
								if (logger.isInfoEnabled()) {
									logger.info("Sorry! Your upper limit of the prepared statement is reached");
								}
								String alarmMsg="The preparedStatement Cache is full, You cannot have another prepared statement";
								this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_CACHE_FULL, alarmMsg );
								return null;

                      						/*LogicalPrepareStatement logicalstatement_s=new LogicalPrepareStatement(sql,conn,this);
                      						return logicalstatement_s;*/

							}
						}
					}
					else
					{
						;
					}

					if (logger.isInfoEnabled()) {
						logger.info("The size of the Object Cache is "+objectcache.size());

						logger.info(logicalpreparestatement1);

						logger.info("The logical statement obtained from Object cache "+logicalpreparestatement1);
					}
					return logicalpreparestatement1;
				}
				else
				{
					if (logger.isInfoEnabled()) {
						logger.info("The cache is empty");
						logger.info("Object cache size is "+objectcache.size());
					}
					LogicalPrepareStatement logicalpreparestatement2=new LogicalPrepareStatement(sql,conn,this);	
					if(objectcache.size()<cacheSize)
						objectcache.put(sql,logicalpreparestatement2);
					else
					{
						;
					}
					if (logger.isInfoEnabled()) {
						logger.info(logicalpreparestatement2);	
						logger.info("The size of the Object Cache is "+objectcache.size());	
					}
					return logicalpreparestatement2;
				}
			}

			else
			{
				LogicalPrepareStatement logicalstatement=new LogicalPrepareStatement(sql,conn,this);
				return logicalstatement;
			}
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			return null;
		}
			
		
		
	}

	
	public synchronized void logicalConnectionClosed()
	{
		try
		{
			connevent=new ConnectionEvent(this);
		
			dataSourceImpl.connectionClosed(connevent);
		}
		catch(Exception e)
		{
			connevent=new ConnectionEvent(this,(SQLException)e);
			
			
			dataSourceImpl.connectionErrorOccurred(connevent);
			logger.error(e.toString(),e);
		}  

		
	}
	
	public synchronized void logicalPrepareStatementClosed(LogicalPrepareStatement prep,String sql)
	{
		try
		{
				
			objectcache.settime(sql);		
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
		
				
	}
	
                               

}

