/**
 * FileName:	ServiceUnit.java
 *
 */

package com.baypackets.ase.ra.ro;

/** 
 * This interface defines the common fields of Requested-Service-Unit AVP,
 * Used-Service-Unit AVP and Grated-ServiceUnit AVP  and works as a base for
 * RequestedServiceUnit interface, UsedServiceUnit interface and Grated-ServiceUnit 
 * interface. All of these three interfaces extends ServiceUnit interface.
 */

public interface ServiceUnit
{
	/**
	 * This method returns the CC-Time AVP that is part of a 
	 * Multiple-Services-Credit-Control AVP in a credit control message.
	 *
	 * @return long containing CC-Time AVP.
	 */
 
	public long getCCTime();

	/**
	 * This method returns the CC-Total-Octates AVP that is part of a 
	 * Multiple-Services-Credit-Control AVP in a credit control message.
	 *
	 * @return long containing CC-Total-Octates AVP.
	 */

	public long getCCTotalOctets();

	/**
	 * This method returns the CC-Input-Octates AVP that is part of a 
	 * Multiple-Services-Credit-Control AVP in a credit control message.
	 *
	 * @return long containing CC-Input-Octates AVP.
	 */
	
	public long getCCInputOctets();

	/**
	 * This method returns the CC-Output-Octates AVP that is part of a 
	 * Multiple-Services-Credit-Control AVP in a credit control message.
	 *
	 * @return long containing CC-Output-Octates AVP.
	 */

	public long getCCOutputOctets();

	/**
	 * This method returns the CC-Service-Specific-Units AVP that is part of a 
	 * Multiple-Services-Credit-Control AVP in a credit control message.
	 *
	 * @return long containing CC-Service-Specific-Units AVP.
	 */

	public long getCCServiceSpecificUnits();

	/**
	 * This method associates the CC-Time AVP to a Multiple-Services-Credit-Control 
	 * AVP that is part of a credit control message.
	 *
	 * @param time - <code>long</code> object containing CC-Time AVP.
	 */

	public void setCCTime(long time);

	/**
	 * This method associates the CC-Total-Octates AVP to a Multiple-Services-Credit-Control 
	 * AVP that is part of a credit control message.
	 *
	 * @param octets - <code>long</code> object containing CC-Total-Octates AVP.
	 */

	public void setCCTotalOctets(long octets);

	/**
	 * This method associates the CC-Input-Octates AVP to a Multiple-Services-Credit-Control 
	 * AVP that is part of a credit control message.
	 *
	 * @param octets - <code>long</code> object containing CC-Input-Octates AVP.
	 */

	public void setCCInputOctets(long octets);

	/**
	 * This method associates the CC-Output-Octates AVP to a Multiple-Services-Credit-Control 
	 * AVP that is part of a credit control message.
	 *
	 * @param octets - <code>long</code> object containing CC-Output-Octates AVP.
	 */

	public void setCCOutputOctets(long octets);

	/**
	 * This method associates the CC-Service-Specific-UNits AVP to a Multiple-Services-Credit-Control 
	 * AVP that is part of a credit control message.
	 *
	 * @param units - <code>long</code> object containing CC-Service-Specific-UNits AVP.
	 */

	public void setCCServiceSpecificUnits(long units);
}
