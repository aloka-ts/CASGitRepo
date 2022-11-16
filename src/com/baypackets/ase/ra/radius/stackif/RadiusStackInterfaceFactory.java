package com.baypackets.ase.ra.radius.stackif;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.RadiusResourceAdaptor;
import com.baypackets.ase.ra.radius.RadiusStackClientInterface;
import com.baypackets.ase.ra.radius.RadiusStackServerInterface;
import com.baypackets.ase.ra.radius.utils.Constants;

public class RadiusStackInterfaceFactory implements Constants {

	private static Logger logger = Logger.getLogger(RadiusStackInterfaceFactory.class);
	private static RadiusStackInterfaceFactory instance = new RadiusStackInterfaceFactory();
	
	private RadiusStackInterfaceFactory(){
		
	}
	public static RadiusStackInterfaceFactory getInstance() {
		if(logger.isDebugEnabled())
			logger.debug("getInstance() is called....");
		return instance;
	}
	public RadiusStackClientInterface loadStackClientInterface(RadiusResourceAdaptor ra) {
		logger.error("loadStackInterface(): ....");
		if (ra != null) {
			return new RadiusStackClientInterfaceImpl(ra);
		} else
			return null;
	}
	
	public RadiusStackServerInterface loadStackServerInterface(RadiusResourceAdaptor ra) {
		if(logger.isDebugEnabled())
			logger.debug("loadStackServerInterface(): .....");
		if (ra != null) {
			return new RadiusStackServerInterfaceImpl(ra);
		} 
			return null;
	}
}
