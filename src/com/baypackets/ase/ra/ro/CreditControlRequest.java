/**
 * Filename:	CreditControlRequest.java
 * Created On:	30-Sept-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.List;

/**
 * <code>CreditControlRequest</code> interface represents Credit Control
 * request (as per RFC 4006) to applications. It specifies common operations
 * which can be performed by applications on all Credit Control request.
 *
 * @author Neeraj Jain
 */
public interface CreditControlRequest extends RoRequest, CreditControlMessage {
	/**
	 * Retrieves Service-Context-Id AVP (code: 461) from request.
	 *
	 * @return value of Service-Context-Id AVP if present,
	 *         <code>null</code> otherwise
	 */
	public String getServiceContextId();
	
	
	public int getCommandCode();

	/**
	 * Retrieves User-Name AVP (code: 1) from request.
	 *
	 * @return value of User-Name AVP if present,
	 *         <code>null</code> otherwise
	 */
	public String getUserName();

	/**
	 * Retrieves Origin-State-Id AVP (code: 278) from request.
	 *
	 * @return value of Origin-State-Id AVP if present,
	 *         -1 otherwise
	 */
	public long getOriginStateId();

	/**
	 * Retrieves Event-Timestamp AVP (code: 55) from request.
	 *
	 * @return value of Event-Timestamp AVP if present,
	 *         -1 otherwise
	 */
	public int getEventTimestamp();

	/**
	 * Retrieves list of Subscription-Id AVP (code: 443) from request.
	 *
	 * @return list of <code>SubscriptionId</code> objects, if Subscription-Id AVP
	 *         is present,
	 *         <code>null</code> otherwise
	 */
	public List getSubscriptionIdList();

	/**
	 * Retrieves Termination-Cause AVP (code: 295) from request.
	 *
	 * @return value of Termination-Cause AVP if present,
	 *         -1 otherwise
	 */
	public short getTerminationCause();

	/**
	 * Retrieves Requested-Action AVP (code: 436) from request.
	 *
	 * @return value of Requested-Action AVP,
	 *         -1 otherwise
	 */
	public short getRequestedAction();

	/**
	 * Retrieves Multiple-Services-Indicator AVP (code: 455) from request.
	 *
	 * @return value of Multiple-Services-Indicator AVP if present,
	 *         -1 otherwise
	 */
	public short getMultipleServicesIndicator();

	/**
	 * Retrieves list of Multiple-Services-Credit-Control AVP (code: 456) from
	 * request.
	 *
	 * @return list of <code>CCRMultipleServicesCreditControl</code> objects if
	 *         Multiple-Services-Credit-Control AVP is present,
	 *         <code>null</code> otherwise
	 */
	public List getMultipleServicesCreditControlList();

	/**
	 * Retrieves User-Equipment-Info AVP (code: 458) from request.
	 *
	 * @return value of User-Equipment-Info AVP if present,
	 *         <code>null</code> otherwise
	 */
	public UserEquipmentInfo getUserEquipmentInfo();

	/**
	 * Sets value of Service-Context-Id AVP (code: 461) on request.
	 *
	 * @param servContextId value of Service-Context-Id AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setServiceContextId(String servContextId)
		throws RoResourceException;

	/**
	 * Sets value of User-Name AVP (code: 1) on request.
	 *
	 * @param username value of User-Name AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setUserName(String username)
		throws RoResourceException;

	/**
	 * Sets value of Origin-State-Id AVP (code: 278) on request.
	 *
	 * @param origStateId value of Origin-State-Id AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setOriginStateId(long origStateId)
		throws RoResourceException;

	/**
	 * Sets value of Event-Timestamp AVP (code: 55) on request.
	 *
	 * @param timestamp value of Event-Timestamp AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setEventTimestamp(int timestamp)
		throws RoResourceException;

	/**
	 * Adds value of Subscription-Id AVP (code: 443) into list in request. It creates
	 * a new list if it is the first value in this list.
	 *
	 * @param subscriptionId value of Subscription-Id AVP to be added
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void addSubscriptionId(SubscriptionId subscriptionId)
		throws RoResourceException;
	
	/**
	 * Sets value of Termination-Cause AVP (code: 295) on request.
	 *
	 * @param termCause value of Termination-Cause AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setTerminationCause(short termCause)
		throws RoResourceException;

	/**
	 * Sets value of Multiple-Services-Indicator AVP (code: 455) on request.
	 *
	 * @param mulServIndicator value of Multiple-Services-Indicator AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setMultipleServicesIndicator(short mulServIndicator)
		throws RoResourceException;

	/**
	 * Adds value of Multiple-Services-Credit-Control AVP (code: 456) into list on
	 * request. It creates a new list if it is the first value in this list.
	 *
	 * @param mulServCC value of Multiple-Services-Credit-Control AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void addMultipleServicesCreditControl(
							CCRMultipleServicesCreditControl mulServCC)
		throws RoResourceException;

	/**
	 * Sets value of User-Equipment-Info AVP (code: 458) on request.
	 *
	 * @param ueInfo value of User-Equipment-Info AVP to be set
	 *
	 * @throws RoResourceException if the request is already sent
	 */
	public void setUserEquipmentInfo(UserEquipmentInfo ueInfo)
		throws RoResourceException;
}
