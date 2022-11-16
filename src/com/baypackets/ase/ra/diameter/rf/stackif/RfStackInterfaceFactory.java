package com.baypackets.ase.ra.diameter.rf.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.diameter.rf.RfStackInterface;
import com.baypackets.ase.ra.diameter.rf.utils.Constants;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RfStackInterfaceFactory implements Constants {
	private static Logger logger = Logger.getLogger(RfStackInterfaceFactory.class);	
	private static RfStackInterfaceFactory instance = new RfStackInterfaceFactory();
	
	private RfStackInterfaceFactory() {
		
	}
	
	public static RfStackInterfaceFactory getInstance() {
		logger.debug("getInstance() is called.");
		return instance;
	}
	
	public RfStackInterface loadStackInterface(ResourceContext context, RfResourceAdaptor ra) {
		logger.debug("loadStackInterface(): load Condor Stack.");
		if (context != null) {
			return new RfStackInterfaceImpl(ra);

         /*
         -- Uncomment this for supporting multiple stack vendors
			String provider = (String)context.getConfigProperty(STACK_PROVIDER);
			logger.info("Sh stack interface is provided by " + provider);
			if (provider.startsWith("com.condor")) {
				return new CondorStackInterface(ra);
			}
			logger.error("Sh stack interface provided by " + provider + " is not supported");
         */
		} else {
			logger.info("Use default Sh stack interface.");
			return new RfStackInterfaceImpl(ra);
		}
	}
}
