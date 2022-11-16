package com.baypackets.ase.ra.diameter.sh;

import java.util.Date;


public interface ShSubscribeNotificationRequest extends ShRequest {
	/**
	 * This method returns multiple Enumerated values from DataReference AVPs
	 * @return - An array of the Enumerated values.
	 * @throws ShResourceException - if parsing failed.
	 */
	public int[] getDataReferences()
	throws ShResourceException;

	/**
	 * This method returns multiple multiple OctetString values from DSAITag AVPs.
	 * 
	 * @return - An array of the OctetString value.
	 * @throws ShResourceException - if parsing failed
	 */
	public String[] getDSAITags()
	throws ShResourceException;


	/**
	 * This method returns a single Time value from ExpiryTime AVPs.
	 * 
	 * @return - The Time value.
	 * @throws ShResourceException - if parsing failed
	 */
	public Date getExpiryTime()
	throws ShResourceException;

	/**
	 * This method returns multiple Enumerated values from IdentitySet APVs.
	 * 
	 * @return - An array of the Enumerated values.
	 * @throws ShResourceException - if parsing failed
	 */
	public int[] getIdentitySets()
	throws ShResourceException;

	/**
	 * This method returns a single Enumerated value from OneTimeNotification AVPs.
	 * 
	 * @return - The Enumerated values.
	 * @throws ShResourceException - if parsing failed
	 */
	public int getOneTimeNotification()
	throws ShResourceException;

	/**
	 * This method returns a single Enumerated value from SendDataIndication AVPs.
	 * 
	 * @return - The Enumerated values.
	 * @throws ShResourceException - if parsing failed
	 */
	public int getSendDataIndication()
	throws ShResourceException;

	/**
	 * This method returns a single Enumerated value from SubsReqType AVPs.
	 * 
	 * @return - The Enumerated values.
	 * @throws ShResourceException - if parsing failed
	 */
	public int getSubsReqType()
	throws ShResourceException;
}
