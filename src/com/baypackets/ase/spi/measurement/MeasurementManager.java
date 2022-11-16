package com.baypackets.ase.spi.measurement;

public interface MeasurementManager {

	public String INSTANCE = "AseMeasurementManager";
	
	public MeasurementCounter getMeasurementCounter(String name);
	
	public MeasurementCounter getThresholdCounter(String name);
}
