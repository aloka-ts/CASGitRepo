/**
 * filename:	UsedServiceUnit.java
 *
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Used-Service-Unit AVP that is part of an credit
 * control Request.
 *
 * @author Neeraj Jain
 */

public interface UsedServiceUnit extends ServiceUnit
{
	/**
	 * This method returns the Reporting-Reason AVP that is part of a
	 * Multiple-Services-Credit-Control AVP in a credit control request.
	 *
	 * @return short object containing Reporting-Reason AVP.
	 */

	public short getReportingReason();

	/**
	 * This method returns the Tariff-Change-Usage AVP that is part of a
	 * Multiple-Services-Credit-Control AVP in a credit control request.
	 *
	 * @return short object containing Tariff-Change-Usage AVP.
	 */

	public short getTariffChangeUsage();

	/**
	 * This method associates the Reporting-Reason AVP to a Multiple-Services-Credit-Control
	 * AVP that is part of a credit control request.
	 *
	 * @param reportingReason - <code>short</code> object containing Reporting-Reason AVP
	 * to be set.
	 */

	public void setReportingReason(short reportingReason);

	/**
	 * This method associates the Tariff-Change-Usage AVP to a Multiple-Services-Credit-Control
	 * AVP that is part of a credit control request.
	 *
	 * @param tariffChangeUsage - <code>short</code> object containing Tariff-Change-Usage AVP
	 * to be set.
	 */

	public void setTariffChangeUsage(short tariffChangeUsage);
}
