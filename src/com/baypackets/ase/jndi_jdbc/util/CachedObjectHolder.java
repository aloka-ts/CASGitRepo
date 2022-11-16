package com.baypackets.ase.jndi_jdbc.util;

import com.baypackets.ase.jndi_jdbc.ds.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/** This class holds a LogicalPrepareStatemt.
*/


public class CachedObjectHolder
{
	public long timeStamp;
	public boolean open;
		
	public LogicalPrepareStatement cachedObject;
		
	private static Logger logger=Logger.getLogger(CachedObjectHolder.class);

	public CachedObjectHolder()
	{
       		try
		{
			if (logger.isInfoEnabled()) {
				logger.info("Cached Object Holder object is initiated");
			}
			open=true;
		}
		catch(Exception e1)
		{
			if (logger.isInfoEnabled()) {
				logger.info(e1.toString(),e1);
			}
		}
	}

}
