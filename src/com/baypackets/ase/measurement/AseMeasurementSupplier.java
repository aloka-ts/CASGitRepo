/*
 * AseMeasurementSupplier.java
 *
 * Created on August 7, 2004, 4:22 PM
 */
package com.baypackets.ase.measurement;

import org.apache.log4j.Logger;

import com.agnity.oems.agent.messagebus.meascounters.UsageParamSupplier;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.bayprocessor.slee.common.EntityNotFoundException;

public class AseMeasurementSupplier implements UsageParamSupplier {
	
	private static Logger logger = Logger.getLogger(AseMeasurementSupplier.class);
	
	public int getUsageParam(int index) throws EntityNotFoundException {
		return 0;
	}

	public int getUsageParam(String name) throws EntityNotFoundException {
		if(logger.isInfoEnabled()){
			logger.info("getUsageParam invoked for :" + name);
		}
		MeasurementManager measMgr = AseMeasurementManager.instance().getDefaultMeasurementManager();
		AseCounter counter = (AseCounter)measMgr.getMeasurementCounter(name);
		return counter != null ? (int)counter.getCount() : 0;
	}

	public void initialize() {
	}
}
