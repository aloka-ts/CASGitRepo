package com.baypackets.ase.ra.diameter.sh.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.sh.ShResourceAdaptor;
import com.baypackets.ase.ra.diameter.sh.ShStackInterface;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.spi.resource.ResourceContext;

public class ShStackInterfaceFactory implements Constants {
	private static Logger logger = Logger.getLogger(ShStackInterfaceFactory.class);	
	private static ShStackInterfaceFactory instance = new ShStackInterfaceFactory();
	
	private ShStackInterfaceFactory() {
		
	}
	
	public static ShStackInterfaceFactory getInstance() {
		logger.debug("getInstance() is called.");
		return instance;
	}
	
	public ShStackInterface loadStackInterface(ResourceContext context, ShResourceAdaptor ra) {
		logger.debug("loadStackInterface(): load Marben Stack.");
		if (context != null) {
			return new ShStackServerInterfaceImpl(ra);

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
			return new ShStackServerInterfaceImpl(ra);
		}
	}
}
