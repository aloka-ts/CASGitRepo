package com.baypackets.ase.ra.diameter.ro.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.RoResourceAdaptor;
import com.baypackets.ase.ra.diameter.ro.RoStackInterface;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RoStackInterfaceFactory implements Constants {
	private static Logger logger = Logger.getLogger(RoStackInterfaceFactory.class);	
	private static RoStackInterfaceFactory instance = new RoStackInterfaceFactory();
	
	private RoStackInterfaceFactory() {
		
	}
	
	public static RoStackInterfaceFactory getInstance() {
		logger.debug("getInstance() is called.");
		return instance;
	}
	
	public RoStackInterface loadStackInterface(ResourceContext context, RoResourceAdaptor ra) {
		logger.debug("loadStackInterface(): load marben Stack.");
		if (context != null) {
			return new RoStackInterfaceImpl(ra);

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
			return new RoStackInterfaceImpl(ra);
		}
	}
}
