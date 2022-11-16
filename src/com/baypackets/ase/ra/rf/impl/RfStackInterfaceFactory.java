package com.baypackets.ase.ra.rf.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.Constants;
import com.baypackets.ase.ra.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.rf.RfStackInterface;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RfStackInterfaceFactory implements Constants 
{
	private static Logger logger = Logger.getLogger(RfStackInterfaceFactory.class);	
	private static RfStackInterfaceFactory instance = new RfStackInterfaceFactory();
	
	private RfStackInterfaceFactory() 
	{
		if(logger.isInfoEnabled())
			logger.info("RfStackInterfaceFactory() Constructor called");
	}
	
	public static RfStackInterfaceFactory getInstance() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getInstance() is called.");
		return instance;
	}
	
	public RfStackInterface loadStackInterface(ResourceContext context, RfResourceAdaptor ra) 
	{
		if(logger.isDebugEnabled())
			logger.debug("loadStackInterface(): load Condor Stack.");
		if (context != null) {
			return new CondorStackInterface(ra);

         /*
         -- Uncomment this for supporting multiple stack vendors
			String provider = (String)context.getConfigProperty(STACK_PROVIDER);
			if(logger.isInfoEnabled())
			logger.info("Rf stack interface is provided by " + provider);
			if (provider.startsWith("com.condor")) {
				return new CondorStackInterface(ra);
			}
			logger.error("Rf stack interface provided by " + provider + " is not supported");
         */
		} 
		else 
		{
			if(logger.isInfoEnabled())
				logger.info("Use default Rf stack interface.");
			return new CondorStackInterface(ra);
		}
	}
}
