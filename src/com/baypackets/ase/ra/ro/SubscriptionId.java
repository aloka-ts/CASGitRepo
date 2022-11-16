/**
 * Filename:	SubscriptionId.java
 * Created On:	06-Oct-2006
 */
package com.baypackets.ase.ra.ro;

/**
 * This interface represents Subscription-Id AVP (code: 443) as per RFC 4006.
 * Applications can create objects of this type using
 * <code>RoResourceFactory.createSubscriptionId</code> method.
 *
 * @author Neeraj Jain
 */
public interface SubscriptionId {
	/**
	 * Retrieves Subscription-Id-Type AVP (code: 450).
	 *
	 * @return <code>Constants.ST_END_USER_E164</code> or
	 *         <code>Constants.ST_END_USER_IMSI</code> or
	 *         <code>Constants.ST_END_USER_SIP_URI</code> or
	 *         <code>Constants.ST_END_USER_NAI</code> or
	 *         <code>Constants.ST_END_USER_PRIVATE</code>
	 */
	public short getSubscriptionIdType();

	/**
	 * Retrieves Subscription-Id-Data AVP (code: 444).
	 *
	 * @return Subscription-Id-Data AVP
	 */
	public String getSubscriptionIdData();

	/**
	 * Sets Subscription-Id-Type AVP (code: 450).
	 *
	 * @param subsIdType this should be either of
	 *                   <code>Constants.ST_END_USER_E164</code>,
	 *                   <code>Constants.ST_END_USER_IMSI</code>,
	 *                   <code>Constants.ST_END_USER_SIP_URI</code>,
	 *                   <code>Constants.ST_END_USER_NAI</code> or
	 *                   <code>Constants.ST_END_USER_PRIVATE</code>
	 */
	public void setSubscriptionIdType(short subsIdType);

	/**
	 * Sets Subscription-Id-Data AVP (code: 444).
	 *
	 * @param subsIdData this is value of Subscription-Id-Data AVP
	 */
	public void setSubscriptionIdData(String subsIdData);
}

