/*
 * Created on Jan 18, 2005
 *
 */
package com.baypackets.ase.ocm;

/**
 * @author Dana
 *
 * This interface should be implemented by ServletRequest and ServletResponse
 * classes to support response time based overload control
 */
public interface TimeMeasurement {
	public void setTimestamp(long timestamp);
	
	public long getTimestamp();

	public boolean hasTimestamp();
}
