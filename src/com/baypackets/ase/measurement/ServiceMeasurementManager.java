package com.baypackets.ase.measurement;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;

public class ServiceMeasurementManager implements MeasurementManager{
	private static Logger logger = Logger.getLogger(ServiceMeasurementManager.class);
	private String serviceName;

	public ServiceMeasurementManager(String name) {
		super();
		serviceName = name;
	}

	public MeasurementCounter getMeasurementCounter(String name) {
		logger.info(" Inside ServiceMeasurementManager name :"+ name);
		AseCounter counter = AseMeasurementManager.instance().getCounter(serviceName, name, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
	   logger.info("counter:"+ counter);
	   return counter;
	}

	public MeasurementCounter getThresholdCounter(String name) {
		return AseMeasurementManager.instance().getCounter(serviceName, name,AseMeasurementManager.TYPE_THRESHOLD_COUNTER);
	}
	
	public void initialize(String measConfigFile, String thresConfigFile, ClassLoader loader) throws Exception{
		AseMeasurementManager.instance().initMeasurementCounters(this.serviceName, measConfigFile, loader);
		AseMeasurementManager.instance().initThresholdCounters(this.serviceName, thresConfigFile, loader);
	}
	
	

}
