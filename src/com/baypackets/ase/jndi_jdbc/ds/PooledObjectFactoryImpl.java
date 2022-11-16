package com.baypackets.ase.jndi_jdbc.ds;

import javax.sql.*;
import java.sql.*;
import java.io.*;
import com.baypackets.ase.jndi_jdbc.util.*;
import org.apache.log4j.*;

/** This class implements the PooledObjectFactory interface
* It basically craetes the object object in this case is ConnectionPoolDataSource object.
*/


public class PooledObjectFactoryImpl implements PooledObjectFactory
{
	private DataSourceImpl datasourceimpl=null;
	
	private static Logger logger=Logger.getLogger(PooledObjectFactoryImpl.class);

    
	public PooledObjectFactoryImpl(DataSourceImpl datasourceimp)
	{
		
		try
	    	{
			if (logger.isInfoEnabled()) {
				logger.info("PooledObjectFactory object is initiated");
			}
			datasourceimpl=datasourceimp;
                
       		 }
        	catch(Exception e)
        	{
			logger.error(e.toString(),e);
			
                       
        	}

	}

/** It creates the ConnectionPoolDataSource object
*/
	public Object create() 
	{
		try
		{
					
			ConnectionPoolDataSource cpds=new ConnectionPoolDataSourceImpl(datasourceimpl);
			return cpds;
		}
		catch(Exception e)
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Exception occurred in class PooledObject Factory "+e);
				logger.debug(e.toString(),e);
			}
			return null;
		}
	}

	public void cleanup()
	{
		if (logger.isInfoEnabled()) {
			logger.info("This method has not been implemented");
		}
	}
}

