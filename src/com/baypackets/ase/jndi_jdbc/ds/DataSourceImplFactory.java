package com.baypackets.ase.jndi_jdbc.ds;
import javax.naming.*;
import java.util.Hashtable;
import javax.naming.spi.ObjectFactory;
import com.baypackets.ase.jndi_jdbc.util.*;
import org.apache.log4j.*;

import java.io.*;

/** This class implements javax.naming.spi.ObjectFactory interface
* It recreates the DataSourceImpl object on the basis Reference
*/

public class DataSourceImplFactory implements ObjectFactory
{

	static Logger logger=Logger.getLogger(DataSourceImplFactory.class);

	public DataSourceImplFactory()
	{
		if (logger.isInfoEnabled()) {
			logger.info("DataSourceImplFactory has been initiated");
		}
	}

/** This methos returns the object instance with same properties as determined by the object ( first argument)
* @param object it is the same reference as passed by getReference method of DataSourceImpl class
* @param name the context name 
* @param ctx The context to which the name is bound
* @param env the environment of the context
* @return it returns the recreated object
*/

	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable env) throws Exception
	{

		try
		{
			if (logger.isInfoEnabled()) {
				logger.info("getObjectInstance method has been called");
			}
			if(object instanceof Reference)
			{
				Reference reference=(Reference)object;
		
				if(reference.getClassName().equals(DataSourceImpl.class.getName()))
				{
				
					RefAddr addr=reference.get("DatasourceImplName");
			

					if(addr!=null)
					{
						if (logger.isInfoEnabled()) {
							logger.info("getObjectInstance: fetching the key");
						}
						String hash_map_key=(String)addr.getContent();
						if (logger.isInfoEnabled()) {
							logger.info("getObjectInstance:HashMapKey===> "+hash_map_key);
						}
						return (new DataSourceImpl(hash_map_key));

						
					}
				}	
			}
			return null;
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			
			throw ee;
		}
	}
}
	
