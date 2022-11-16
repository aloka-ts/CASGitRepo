/**
 * Filename:	GrantedServiceUnit.java
 *
 */
package com.baypackets.ase.ra.ro;

/** 
 * This interfce defines the Granted-Service-Unit AVP that is part of an credit 
 * control request.
 *
 * @author Neeraj Jain
 */

public interface GrantedServiceUnit extends ServiceUnit {

	/**
	 * This method retunrs the time in seconds since January 1, 1900, 00:00 UTC, when 
	 * the tariff of the service will be changed.
	 *
	 * @return long object containig time in seconds since January 1, 1900, 00:00 UTC.
	 */

	public long getTariffTimeChange();
}
