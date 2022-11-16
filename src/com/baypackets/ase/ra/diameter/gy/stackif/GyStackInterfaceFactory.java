package com.baypackets.ase.ra.diameter.gy.stackif;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.GyResourceAdaptor;
import com.baypackets.ase.ra.diameter.gy.GyStackInterface;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.spi.resource.ResourceContext;

public class GyStackInterfaceFactory implements Constants {
	private static Logger logger = Logger.getLogger(GyStackInterfaceFactory.class);	
	private static GyStackInterfaceFactory instance = new GyStackInterfaceFactory();
	
	private GyStackInterfaceFactory() {
		
	}
	
	public static GyStackInterfaceFactory getInstance() {
		logger.debug("getInstance() is called.");
		return instance;
	}
	
	public GyStackInterface loadStackInterface(ResourceContext context, GyResourceAdaptor ra) {
		logger.debug("loadStackInterface(): load Condor Stack.");
		if (context != null) {
			return new GyStackInterfaceImpl(ra);

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
			return new GyStackInterfaceImpl(ra);
		}
	}
}
