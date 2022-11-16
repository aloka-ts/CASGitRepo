/**
 * FileName:	CCRMultipleServicesCreditControl.java
 */
 
package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/** This interface defines Multiple-Services-Credit-Control AVP in
 *  a credit control request message.
 *
 * Application can use it's methods to fill various fields of
 * Multiple-Services-Credit-Control AVP in a credit control request.
 *
 * @author Neearaj Jain
 */


public interface CCRMultipleServicesCreditControl
{
	/**
	 * This method returns Requestes-service-Unit AVP associated with a 
	 * credit control request.
	 * 
	 * @return <code>RequestedServiceUnit</code> object containing Requestes-service-Unit AVP.
	 */

	public RequestedServiceUnit getRequestedServiceUnit();

	/**
 	 * This method asociates a Requestes-service-Unit AVP to a credit control request.
	 * 
	 * @param rsu - <code>RequestedServiceUnit</code> to be set.
	 */

	public void setRequestedServiceUnit(RequestedServiceUnit rsu);

	/**
	 * This method returns Used-Service-Unit AVP associated with a credit control request.
	 * 
	 * @return Iterator  on Used-Service-Unit AVP associated.
	 */
	public Iterator getUsedServiceUnits();

	/**
	 * This method asociates a Used-Service-Unit AVP to a credit control request.
	 * @param usu - /code>UsedServiceUnit</code> to be set.
	 */
	
	public void addUsedServiceUnit(UsedServiceUnit usu);

	/**
 	 * This method removes a Used-Service-Unit AVP from a credit control request.
	 *
	 * @param usu - <code>UsedServiceUnit</code> AVP to be removed.
	 */
	public void removeUsedServiceUnit(UsedServiceUnit usu);

	/**
	 * This method returns Rating-Group AVP associated with a credit control request.
	 *
	 * return long object containing Rating-Group AVP.
	 */

	public long getRatingGroup();

	/**
	 * This method associates a Rating-Group AVP with a credit control request.
	 *
	 * @param ratingGroup - Rating-Group AVP to be associated.
	 */
 
	public void setRatingGroup(long ratingGroup);

	/**
	 * This method returns the Reporting-Reason AVP associated with a credit control request
	 *
	 * @return short object containing Reporting-Reason AVP.
	 */

	public short getReportingReason();

	/**
	 * This method associates a Reporting-Reason AVP to a credit control request.
	 *
	 * @param reportingReason - Reporting-Reason AVP to be set.
	 */
 
	public void setReportingReason(short reportingReason);

	/**
	 * This method returns the Trigger-Type AVP associated with a credit control request.
	 *
	 * @return short object containing Trigger-Type AVP.
	 */

	public Iterator getTriggerTypes();

	/**
	 * This method adds Trigger-Type AVP to a credit control request.
	 *
	 * @param triggerType - Trigger-Type AVP to be set.
	 */
	public void addTriggerType(short triggerType);

	/**
	 * This method removes the Trigger-Type AVP from a credit control request.
	 *
	 */
	public void removeTriggerType(short triggerType);
}
