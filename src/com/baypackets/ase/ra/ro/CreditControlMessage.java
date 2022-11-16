/**
 * Filename:	CreditControlMessage.java
 * Created On:	30-Sept-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * <code>CreditControlMessage</code> interface represents Credit Control
 * message (as per RFC 4006) to applications. It specifies common operations
 * which can be performed by applications on all Credit Control messages.
 *
 * @author Neeraj Jain
 */
public interface CreditControlMessage extends RoMessage, CustomAVPMap
{
	/**
	 * This method is used by application to get Auth-Application-Id AVP of
	 * a Credit Control message.
	 *
	 * @return Auth-Application-Id AVP value
	 */
	public long getAuthApplicationId();

	/**
	 * This method is used by application to get CC-Request-Type AVP of
	 * a Credit Control message.
	 *
	 * @return CC-Request-Type AVP value
	 */
	public short getCCRequestType();

	/**
	 * This method is used by application to get CC-Request-Number AVP of
	 * a Credit Control message.
	 *
	 * @return CC-Request-Number AVP value
	 */
	public long getCCRequestNumber();

	/**
	 * This method is used by application to get Service-Information AVP of
	 * a Credit Control message.
	 *
	 * @return Service-Information AVP value
	 *         null if this AVP is not present
	 */
	public ServiceInformation getServiceInformation();

	/**
	 * This method sets value of Service-Information AVP into Credit Control
	 * message.
	 *
	 * @param servInfo Service-Information AVP value to be set into message
	 *
	 * @throws IllegalStateException if message is unmodifiable
	 */
	public void setServiceInformation(ServiceInformation servInfo);
}
