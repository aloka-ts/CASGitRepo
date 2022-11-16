package com.genband.jain.protocol.ss7.tcap;

import java.util.TimerTask;
import org.apache.log4j.Logger;

public class SasActivationTimerTask extends TimerTask {
	static private Logger logger = Logger.getLogger(SasActivationTimerTask.class);
	
	@Override
	public void run() {
		if(logger.isDebugEnabled()){
			logger.debug("Sas Activation HB Timeout: check if HB established");
		}
		
		JainTcapProviderImpl.getImpl().startHB();
		
	}
	
}