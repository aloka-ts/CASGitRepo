package com.baypackets.ase.ra.ro.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.ra.ro.stackif.CondorStackInterface;

public class RoStackInterfaceFactory {
	private static Logger logger = Logger.getLogger(RoStackInterfaceFactory.class);	
	private static RoStackInterfaceFactory instance = new RoStackInterfaceFactory();
	
	private RoStackInterfaceFactory() {
		logger.info("RoStackInterfaceFactory() Constructor called");
	}
	
	public static RoStackInterfaceFactory getInstance() {
		logger.debug("getInstance() is called.");
		return instance;
	}
	
	public RoStackInterface loadStackInterface(ResourceContext context, RoResourceAdaptor ra) {
		logger.debug("loadStackInterface(): load Condor Stack.");
		if (context != null) {
			return new CondorStackInterface(ra);

         /*
         -- Uncomment this for supporting multiple stack vendors
			String provider = (String)context.getConfigProperty(STACK_PROVIDER);
			logger.info("Ro stack interface is provided by " + provider);
			if (provider.startsWith("com.condor")) {
				return new CondorStackInterface(ra);
			}
			logger.error("Ro stack interface provided by " + provider + " is not supported");
         */
		} else {
			logger.info("Use default Ro stack interface.");
			return new CondorStackInterface(ra);
		}
	}
}
