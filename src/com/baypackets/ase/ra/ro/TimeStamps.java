/**
 * Filename:	TimeStamps.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Time-Stamps AVP that is part of
 * IMS-Information AVP in a credit control message.
 *
 * @author Neeraj Jain
 */

public interface TimeStamps {
	
	/**
	 * This method returns the SIP-Request-Timestamp AVP that is 
	 * part of Time-Stamps AVP in a credit control message.
	 *
	 * @return <code>String</code> object containing SIP-Request-Timestamp AVP.
	 */

	public String getSIPRequestTimestamp();

	/**
	 * This method returns the SIP-Response-Timestamp AVP that is 
	 * part of Time-Stamps AVP in a credit control message.
	 *
	 * @return <code>String</code> object containing SIP-Response-Timestamp AVP.
	 */

	public String getSIPResponseTimestamp();

	/**
	 * This method associates a SIP-Request-Timestamp AVP to TimeStamps AVP 
	 * that is part of a credit conrol message.
	 *
	 * @param timestamp -<code>String</code> object containing SIP-Request-Timestamp AVP
	 * to be set.
	 */

	public void setSIPRequestTimestamp(String timestamp);

	/**
	 * This method associates a SIP-Response-Timestamp AVP to TimeStamps AVP 
	 * that is part of a credit conrol message.
	 *
	 * @param timestamp -<code>String</code> object containing SIP-Response-Timestamp AVP
	 * to be set.
	 */

	public void setSIPResponseTimestamp(String timestamp);
}

