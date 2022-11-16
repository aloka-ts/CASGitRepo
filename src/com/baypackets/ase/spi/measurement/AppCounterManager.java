package com.baypackets.ase.spi.measurement;

import javax.servlet.ServletContext;

/**
 * Used to expose the increment and decrement API to 
 * the application as value of application specific
 * measurement counters are modified by application
 * only.
 */

public interface AppCounterManager {
	
	public String INSTANCE = "AppCounterManager";
	
	/**
	 * Decrements a counter object specified
	 * by the counterName.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param ctx
	 */
	public void decrementCounter(String counterName, ServletContext ctx);
	
	/**
	 * Decrements a counter object specified
	 * by the counterName.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 */
	public void decrementCounter(String counterName);

	/**
	 * Decrements a counter object specified
	 * by the counterName by specified offset. 
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param ctx
	 * @param offset
	 */
	public void decrementCounter(String counterName, ServletContext ctx, int offset);
	
	/**
	 * Decrements a counter object specified
	 * by the counterName by specified offset. 
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param offset
	 */
	public void decrementCounter(String counterName, int offset);
	
	/**
	 * Increments a counter object specified
	 * by the counterName.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param ctx
	 */
	public void incrementCounter(String counterName, ServletContext ctx);
	
	/**
	 * Increments a counter object specified
	 * by the counterName.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 */
	public void incrementCounter(String counterName);


	/**
	 * Increments a counter object specified
	 * by the counterName by specified offset. 
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param ctx
	 * @param offset
	 */
	public void incrementCounter(String counterName, ServletContext ctx,int offset);
	
	/**
	 * Increments a counter object specified
	 * by the counterName by specified offset. 
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param offset
	 */
	public void incrementCounter(String counterName, int offset);

	
	/**
	 * Resets a counter object specified
	 * by the counterName to zero.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 * @param ctx
	 */
	public void resetCounter(String counterName, ServletContext ctx);
	/**
	 * Resets a counter object specified
	 * by the counterName to zero.
	 * CounterName must be a valid identifier
	 * defined in the configuration xml of application
	 * @param counterName
	 */
	public void resetCounter(String counterName);

}
