
package com.baypackets.ase.jndi_jdbc.util;
import java.io.*;
import java.util.*;
import javax.sql.*;
import java.sql.*;
import com.baypackets.ase.jndi_jdbc.ds.*;
import java.util.*;
import org.apache.log4j.*;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;

/** This class is responsible for creating the connection pool as well as maintaining it. The DataSourceImpl class uses this clas for checking out and checking in the connections.
*/


public class ObjectPool
{
	private int minSize;  		//minimum size of the pool 
	private int maxSize;  		//maximum size of the pool
	private int increMent;
	private int initialSize; 	//initial size of the pool
	private int available;
	private int shrink;             //shrinking pool 1 for shrinking 0 for not shrinking
	private LinkedList listpooledconnection=null;
	private PooledObjectFactory pooledobjectfactory;
	private DataSourceImpl datasourceimpl=null;
	private ConnectionPoolDataSource connectionpooldatasource=null;
	private Properties initial_config=null;
	private int counter=0;
	private int busy_connections=0;
	private int free_connections=0;
	private AseAlarmService alarmService;
	
	private static Logger logger=Logger.getLogger(ObjectPool.class);

/** This constructor initailizes the pool on the basis of the parameters fetched from the deployment descriptor.
* @param datasourceimp It takes DataSourceImpl object as parameter which is calling ObjectPool constructor itself.
*/


	public ObjectPool(DataSourceImpl datasourceimp)  throws SQLException
	{
        	try
		{
			datasourceimpl=datasourceimp;


			initial_config=datasourceimpl.getinitialconfig();		//fetching intializing parameters from properties
     

			initialSize=Integer.parseInt((String)initial_config.get("initialsize"));
			maxSize=Integer.parseInt((String)initial_config.get("maxsize"));
			minSize=Integer.parseInt((String)initial_config.get("minsize"));
			increMent=Integer.parseInt((String)initial_config.get("increment"));
			shrink=Integer.parseInt((String)initial_config.get("shrink"));
			
			if (logger.isInfoEnabled()) {
				logger.info("initial size = "+initialSize);
				logger.info("max size = "+maxSize);
				logger.info("min size = "+minSize);
				logger.info("Increment = "+increMent);
				logger.info("Shrinking pool=="+shrink);  		//1 for shrinking pool 0 for non shrinking.
			}
//************************** Fixing the bug BPInd13303
//The initial Size, min Size, maxSize,increMent and shrink values are not checked

			if((initialSize<0)||(maxSize<0)||(minSize<0)||(increMent<0)||(shrink<0)||(shrink>1))
			{
				if (logger.isInfoEnabled()) {
					logger.info("Wrong Parameter values");
				}
				throw new SQLException();
			}

			if(minSize>maxSize)
			{
				if (logger.isInfoEnabled()) {
					logger.info("wrong Parameter values");
				}
				throw new SQLException();
			}

//*******************************************************************************************


			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);


			pooledobjectfactory =new PooledObjectFactoryImpl(datasourceimpl);
							

			listpooledconnection=new LinkedList();  //this linked list will store pooled connection
  

			counter=0;
			
			connectionpooldatasource=(ConnectionPoolDataSource)pooledobjectfactory.create(); 	//creating the Connection pool datasource.
			if (logger.isInfoEnabled()) {
				logger.info(connectionpooldatasource);
			}
		}
	
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		
		}

		try
		{
			for(int i=1;i<=initialSize;i++)
			{		
			
				//creating the pool of connections equal to initialSize
				PooledConnection pooledconnection=connectionpooldatasource.getPooledConnection();
				listpooledconnection.add(pooledconnection);
				if (logger.isInfoEnabled()) {
					logger.info(pooledconnection);
				}
			}
		}
		catch(SQLException esql1)
		{
			logger.error(esql1.toString(),esql1);
			throw esql1;
		}
		catch(Exception ee)
		{

			if (logger.isDebugEnabled()) {
				logger.debug(ee.toString(),ee);
			}
			
		}
		try
		{
			if (logger.isInfoEnabled()) {
				logger.info("The connection pool has been intialized ");	
				logger.info("There are "+listpooledconnection.size()+" connections in the connection pool"); //undone
			}
			free_connections=initialSize;
		}
		catch(Exception eee)
		{
			logger.error(eee.toString(),eee);
		}
	
		
		

	}
/** This method is used by DataSourceImpl class to get a pooledconnection from the connection pool.
* In this method it checks whether the number of connections in the pool are consistent with the parameters passed by deployment desc.
* It makes the pool to shrink if shrink option is given in the deployment descriptor.

* @return It returns the pooledconnection from the pool
*/
    


	public synchronized Object checkOut() throws SQLException
	{ 
//BPInd
		PooledConnection pooledconnection=null;

		//if((shrink!=0)&&(shrink!=1))
	//	{
	//		logger.info("wrong shrink parameter");
	//		return null;
		//}
		try
		{
			if((shrink==0)||(shrink==1))             //noshrinking
			{
				if (logger.isInfoEnabled()) {
					logger.info("Nature of pool[0 for non shrinking] [1 for shrinking]");
					logger.info("Nature === > "+shrink);
				}
				if(free_connections>0)
				{
					if (logger.isInfoEnabled()) {
						logger.info("checkOut: Get Free connections");
					}
					PooledConnection poolsconn=(PooledConnection)listpooledconnection.removeFirst();
					busy_connections++;
					free_connections--;
                                                        if(free_connections<0)
                                                                free_connections=0;
                                                        if(busy_connections<0)
                                                                busy_connections=0;
					if (logger.isInfoEnabled()) {
						logger.info("checkOut: Free Connections ==== > "+free_connections);
						logger.info("checkOut: Busy Connections ==== > "+busy_connections);
					}
					return poolsconn;
				}
				else
				{
					if(((busy_connections>=initialSize)||(busy_connections>=minSize))&&(busy_connections<maxSize)&&(increMent!=0)&&((busy_connections+free_connections)<=maxSize))
					{
						if((busy_connections+increMent)<=maxSize)
						{
							for(int k=0;k<increMent;k++)
							{
                                               			PooledConnection pool_shrink=connectionpooldatasource.getPooledConnection();
                                               			listpooledconnection.add(pool_shrink);
                                               			free_connections++;
							}
			 		                PooledConnection poool_conn_shrink=(PooledConnection)listpooledconnection.removeFirst();
                                       			free_connections--;
                                       			busy_connections++;
                                                        if(free_connections<0)
                                                                free_connections=0;
                                                        if(busy_connections<0)
                                                                busy_connections=0;
							if (logger.isInfoEnabled()) {
								logger.info("checkOut: After increment Free connections === >"+free_connections);
								logger.info("checkOut: After increment Busy Connections === > "+busy_connections);
							}
                                      			return poool_conn_shrink;
						}
						else
						{
							int difference = maxSize-busy_connections;
							for(int m=0;m<difference;m++)
							{
								PooledConnection pool_shrinks = connectionpooldatasource.getPooledConnection();
								listpooledconnection.add(pool_shrinks);
								free_connections++;
							}
							PooledConnection pool_conn_shrinks=(PooledConnection)listpooledconnection.removeFirst();
                                                        free_connections--;
                                                        busy_connections++;
                                                        if (logger.isInfoEnabled()) {
															logger.info("checkOut: After increment2 Free connections === >"+free_connections);
															logger.info("checkOut: After increment2 Busy Connections === > "+busy_connections);
														}
							if(free_connections<0)
								free_connections=0;
							if(busy_connections<0)
								busy_connections=0;
                                                        return pool_conn_shrinks;
                                                }
					}
					else
					{						if (logger.isInfoEnabled()) {
												logger.info("checkOut: We cannot give more connections to you");
                                       		}
											String alarmMsg="All the connections are busy, We cannt give your more connection";
                                      		this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_NO_FREE_CONNECTION_POOL, alarmMsg );
                                      		return null;
					}
				}
						

							
			}
			else
			{	
				if (logger.isInfoEnabled()) {
					logger.info("Returning nullllll");
				}
				return null;
			}
		}
		catch(Exception e1)
		{
			logger.error(e1.toString(),e1);
			return null;
		}

		//shrinking pool ===> shrink option is 1
		
	/*	try
		{
			if(busy_connections>=maxSize)
			{
				logger.info("We cannot give more connections to you");

				String alarmMsge="The maximum pooled connection limit is reached we cannot give you more connections";
				this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_MAX_LIMIT_POOL_REACHED, alarmMsge );
				return null;
			}


			if(free_connections>0)
			{
				if(free_connections>(busy_connections+increMent))
				{
					for(int j=0;j<increMent;j++)
					{
						if(free_connections<=minSize)
						break;
						PooledConnection poolcon=(PooledConnection)listpooledconnection.removeFirst();
						poolcon.close();
						free_connections--;
					}
					PooledConnection poolcons=(PooledConnection)listpooledconnection.removeFirst();
					free_connections--;
					busy_connections++;
					return poolcons;
				
				}
				else
				{
			
					PooledConnection poool=(PooledConnection)listpooledconnection.removeFirst();
					busy_connections++;
					free_connections--;
					return poool;
				}
			}
		
			else
			{
				if(busy_connections<=(maxSize-increMent))
				{
					//make incrementno. of more connections and add to the pool
					for(int i=0;i<increMent;i++)
					{
						PooledConnection pool=connectionpooldatasource.getPooledConnection();
						listpooledconnection.add(pool);
						free_connections++;
					}
					PooledConnection poool_conn=(PooledConnection)listpooledconnection.removeFirst();
					free_connections--;
					busy_connections++;
					return poool_conn;
				}
			
				if(busy_connections<maxSize)
				{
					PooledConnection pools=connectionpooldatasource.getPooledConnection();
					busy_connections++;
					return pools;
				
				
				}
			
				if(busy_connections>=maxSize)
				{
					logger.info("No new Connections can be given");
					return null;
				}
				return null;
			
			}
		}
		catch(SQLException esql)
		{
			logger.error(esql.toString(),esql);
			throw esql;
		}
		catch(Exception e2)
		{
			logger.error(e2.toString(),e2);
			return null;
		}*/
				
				
	}	

/** This method is used to return the pooledconnection to the connection pool.
* If the available connection are greater than the minimum  number than the connection is physically closed else return to the pool.
* In case of non shrinking pool it is returned to the pool.

* @param pool The parameter is the pooloedconnection which is to be closed.
*/

	public synchronized void checkIn(PooledConnectionImpl pool) throws SQLException,IOException
	{
		try
		{

			if(shrink==0)
			{
				listpooledconnection.add(pool);
				free_connections++;
				busy_connections--;

				if(free_connections<0)
					free_connections=0;
				if(busy_connections<0)
					busy_connections=0;
				if (logger.isInfoEnabled()) {
					logger.info("For Non Shrinking connection pool");
					logger.info("checkIn: Free Connections === > "+free_connections);
					logger.info("checkIn: Busy Connections === > "+busy_connections);
				}
				return;
			}
		}
		catch(Exception e1)
		{
			logger.error(e1.toString(),e1);
			return;
		}
		
		try
		{
			/*if(free_connections>=minSize)
			{
				pool.close();
				busy_connections--;
				return;
			}
			else
			{
				listpooledconnection.add(pool);
				busy_connections--;
				free_connections++;
				return;
			}*/

			if((busy_connections+free_connections)>minSize)
			{
				if (logger.isInfoEnabled()) {
					logger.info("checkIn: busy_connections+free_connections)>minSize ");
				}
				pool.close();
				busy_connections--;
				if(busy_connections<0)
					busy_connections=0;
								if (logger.isInfoEnabled()) {
									logger.info("checkIn: Free Connections === > "+free_connections);
									logger.info("checkIn: Busy Connections === > "+busy_connections);
								}
				return;
			}
			else
			{	
				if (logger.isInfoEnabled()) {
					logger.info("checkIn: busy_connections+free_connections<minSize ");
				}
				listpooledconnection.add(pool);
				busy_connections--;
				free_connections++;
				if(busy_connections<0)
					busy_connections=0;
				if(free_connections<0)
					free_connections=0;
								if (logger.isInfoEnabled()) {
									logger.info("checkIn: Free Connections === > "+free_connections);
									logger.info("checkIn: Busy Connections === > "+busy_connections);
								}
				return;
			}
				
			

			


		//***********************************************************	

	/*		if((busy_connections==1)&&(free_connections==2))
			{
				listpooledconnection.add(pool);
				free_connections++;
				busy_connections--;
				return;
			}
			
			if((busy_connections+free_connections)>=maxSize)
			{
				pool.close();
			

				busy_connections--;
				return;
			}
			else
			{
		
				if((busy_connections<(maxSize-increMent))&&(free_connections>0))
				{
					pool.close();
                       
					busy_connections--;
					return;

				
				}
				else
				{
					if(busy_connections>=(maxSize-increMent))
					{
     
			
			
						listpooledconnection.add(pool);
						free_connections++;
						busy_connections--;
						return;
					}
                        		else
					{
						pool.close();
						busy_connections--;
						return;
					}
				}
			}*/
		}
		catch(SQLException esql)
		{
			logger.error(esql.toString(),esql);
			throw esql;
		}
		catch(Exception e2)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(e2.toString(),e2);
			}
			return;
		}
	}
/** This method closes all the pooleconnection and inturn all the physical connections.
*/



	public synchronized void close() throws SQLException
	{
		
		
		int connectionpoolsize=listpooledconnection.size();
		try
		{	
			for(int i=1,j=1;i<=connectionpoolsize;i++)
			{
				PooledConnection pool=(PooledConnection)listpooledconnection.remove(0);
				if (logger.isInfoEnabled()) {
					logger.info("Closing Pooled connection");
				}
				pool.close();
			
			}
		}
		catch(SQLException ee)
		{
			logger.error(ee.toString(),ee);
			throw ee;
		}
		catch(Exception eq)
		{
			logger.error(eq.toString(),eq);
		
		}
			
	}
	

/** This method returns the number of free connections in the pool.
*/
	public int getFreeConnections()
	{
		return free_connections;
	}
	

/** This method returns the number of busy connections in the pool
*/
	public int getBusyConnections()
	{
		return busy_connections;
	}		
	
}
		

