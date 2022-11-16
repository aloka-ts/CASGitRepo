/**
 * FileName:	CCAMultipleServicesCreditControl.java
 * Created On:  11-Oct-2006
 */ 
package com.baypackets.ase.ra.ro;

/** This interface defines Multiple-Services-Credit-Control AVP in 
 *  a credit control answer message. 
 *  
 * Application can use it's methods to fill various fields of 
 * Multiple-Services-Credit-Control AVP.
 *
 * @author Neearaj Jain
 */

public interface CCAMultipleServicesCreditControl
{
	/**
 	 * This method returns the Granted-Service-Unit AVP associated with a 
	 * credit control answer.
	 * 
	 * @return <code>GrantedServiceUnit</code> object containing Granted-Service-Unit
	 * AVP.
 	 */

	public GrantedServiceUnit getGrantedServiceUnit();

	/**
	 * This method returns the Rating-Group AVP associated with a credit control answer.
	 *
	 * @return long object containing Rating-Group AVP.
	 */

	public long getRatingGroup();
	
	/**
	 * This method returns the Validity-Time AVP associated with a credit control answer.
	 *
	 * @return long object containing Validity-Time AVP.
	 */

	public long getValidityTime();

	/**
 	 * This method returns the  Result-code AVP associated with a credit control answer
	 *
	 * @return long object containing Result-Code AVP.
	 */

	public long getResultCode();
	
	/**
	 * This method returns the Final-Unit-Indication AVP associated with a credit control
	 * answer 
	 *
	 * @return <code>FinalUnitIndication</code> object containing Final-Unit-Indication AVP.
	 */

	public FinalUnitIndication getFinalUnitIndication();

	/**
	 * This method returns the Time-Quota-Threshold AVP associated with a credit control answer.
	 *
	 * @return long object containing Time-Quota-Threshold AVP.
	 */

	public long getTimeQuotaThreshold();

	/**
	 * This method returns the Volume-Quota-Threshold AVP associated with a credit control answer.
	 *
	 * @return long object containing Volume-Quota-Threshold AVP.
	 */

	public long getVolumeQuotaThreshold();

	/**
	 * This method returns the Quota-Holding-Time AVP associated with a credit control answer.
	 *
	 * @return long object containing Quota-Holding-Time AVP.
	 */

	public long getQuotaHoldingTime();

	/**
 	 * This method returns the Quota-consumption-Time AVP associated with a credit control answer.
	 *
	 * @return long object containig Quota-consumption-Time AVP.
	 */

	public long getQuotaConsumptionTime();

	/**
	 * This method returns the Trigger-Type AVP associated with a credit control answer.
	 *
	 * @return short object containing Trigger-Type AVP.
	 */

	public short getTriggerType();
}
