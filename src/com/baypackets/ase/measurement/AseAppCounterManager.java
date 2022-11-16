package com.baypackets.ase.measurement;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.spi.measurement.AppCounterManager;

/**
 *  Class used as a wrapper above AseCounter 
 *  so that only increment, decrement and setCount 
 *  method is being exposed to the application.
 *  Application is able to modify the value of the 
 *  application specific measurement counters. 
 */
public class AseAppCounterManager implements AppCounterManager {

	private static Logger logger = Logger.getLogger(AseAppCounterManager.class);

	public static String DEPLOY_NAME = "DeployName";
	ServletContext ctx = null;

	//Overloaded instance method added to decouple the service code from passing the
	//servlet context
	public AseAppCounterManager(ServletContext ctx) {
		this.ctx= ctx;
	}

	public void decrementCounter(String counterName, ServletContext ctx) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.decrement();
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
	
	public void decrementCounter(String counterName) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(this.ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.decrement();
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}


	public void decrementCounter(String counterName, ServletContext ctx, int offset) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.decrement(offset);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
	
	public void decrementCounter(String counterName, int offset) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(this.ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.decrement(offset);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}


	public void incrementCounter(String counterName, ServletContext ctx) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.increment();
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
	
	public void incrementCounter(String counterName) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(this.ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.increment();
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}


	public void incrementCounter(String counterName, ServletContext ctx, int offset) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.increment(offset);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
	
	public void incrementCounter(String counterName, int offset) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(this.ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.increment(offset);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}


	public void resetCounter(String counterName, ServletContext ctx) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.setCount(0);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
	
	public void resetCounter(String counterName) {
		AseCounter cont = AseMeasurementManager.instance().getCounter(this.ctx.getAttribute(DEPLOY_NAME).toString(), counterName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		if(cont != null)
			cont.setCount(0);
		else
		{
			logger.error("Counter " + counterName + " not found in the list");
		}
	}
}