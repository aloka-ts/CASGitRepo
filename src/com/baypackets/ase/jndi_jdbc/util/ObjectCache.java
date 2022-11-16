package com.baypackets.ase.jndi_jdbc.util;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.Date;

import com.baypackets.ase.jndi_jdbc.ds.*;
import org.apache.log4j.*;
/** This class provides cache for the objects of CachedObjectHolder type.
* This cache uses the least recently used LRU policy
*/

public class ObjectCache
{
	Hashtable cache=null ;
		
	private static Logger logger=Logger.getLogger(ObjectCache.class);
	public ObjectCache()
	{
		if (logger.isInfoEnabled()) {
			logger.info("ObjectCache has been initiated");
		}
		cache=new Hashtable();

			
	}
	
	
/** This method give the logicalpreparestatement corresponding to the name.
* @param name its acts as a key to fetch the LogicalPrepareStatement from the cache.
* @return It returns the corresponding LogicalPrepare statement.
*/

	public Object  get(String name)
	{
		try
		{
			if (logger.isInfoEnabled()) {
				logger.info(" I am in get method os Object Cache");
			}
			CachedObjectHolder holder=null;	
			if (logger.isInfoEnabled()) {
				logger.info("The logicalpreparestatement is "+cache.get(name));
			}
			holder=(CachedObjectHolder)cache.get(name);
			if (logger.isInfoEnabled()) {
				logger.info(holder);
			}
			if(holder!=null)
			{
				java.util.Date date=new java.util.Date();
				holder.timeStamp=date.getTime();
				holder.open=true;
				return holder.cachedObject;
			}

			return null;

		}
		catch(Exception e)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(e.toString(),e);
			}
			return null;
		}
	}


	/** This method puts the LogicalPrepareStatement in the cache.The sql query is the name (key) by which this logicalpreparestatement will be known.
	* @param name The sql query
	* @param logical The logicalPrepareStatement which is to be stored
	*/

	public void put(String name,LogicalPrepareStatement logical)
	{
		try
		{
			CachedObjectHolder holder=new CachedObjectHolder();

			holder.cachedObject=logical;
			if (logger.isInfoEnabled()) {
				logger.info("There are "+cache.size()+" LogicalPreparedStatement in the Cache");
			}
			Date date=new Date();
			long msec=date.getTime();
			holder.timeStamp=msec;
			cache.put(name,holder);
		}
		catch(Exception e)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(e.toString(),e);
			}
		}
	}

	/** It returns the size of the cache
	*/

	public int size()
	{
		try
		{
		
			return cache.size();
		}
		catch(Exception e)
		{
			if (logger.isInfoEnabled()) {
				logger.info(e.toString(),e);
			}
			return 0;
		}
	}
	
	/** It sets the time/ modifies teh time ath whcih the corresponding logical preparestatement is accessed.
	* @param sql The key by which the logicalpreparestatement will be recognized.
	*/


	public boolean settime(String sql)
	{
		try
		{
		 	CachedObjectHolder holder=new CachedObjectHolder();
		 	holder=(CachedObjectHolder)cache.get(sql);
		 	if(holder!=null)
			{
				holder.open=false;
				return true;
			}
			else
				return false;
		}
		catch(Exception e)
		{
			if (logger.isInfoEnabled()) {
				logger.info(e.toString(),e);
			}
			return false;
		}
	}

	/** It removes the least recently used LogicalPrepareStatement from the cache
	*/


	public boolean removeleastrecentlyused()
	{
		Enumeration values=null;

		String str=null;
		values=cache.keys();
		try
		{
			while(values.hasMoreElements())
			{
				str=(String)values.nextElement();
				CachedObjectHolder holder=(CachedObjectHolder)cache.get(str);
				if(holder!=null)
				{
					if(holder.open==false)
					{
						(holder.cachedObject).finalclose();
						return true;
					}
				}
					
			}
			return false;
		}
		catch(Exception e)
		{
			if (logger.isInfoEnabled()) {
				logger.info("The exception is SQLException "+e);
				logger.info(e.toString(),e);
			}
			return false;
		}
			
				
					
	}
}	
			


