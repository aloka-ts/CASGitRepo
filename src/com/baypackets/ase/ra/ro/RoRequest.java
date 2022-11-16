/**
 * Filename:	RoRequest.java
 * Created On:	30-Sept-2006
 */
package com.baypackets.ase.ra.ro;

import com.baypackets.ase.resource.Request;

/**
 * <code>RoRequest</code> interface represents an Ro request to
 * applications. It specifies operations which can be performed
 * by applications an Ro request.
 *
 * @author Neeraj Jain
 */
public interface RoRequest extends Request, RoMessage
{
	/**
	 * This method returns type of this request that was given to factory at
	 * time of its creation.
	 *
	 * @return RO_FIRST_INTERROGATION/ RO_INTERMEDIATE_INTERROGATION/
	 *         RO_FINAL_INTERROGATION/ RO_DIRECT_DEBITING/ RO_REFUND_ACCOUNT
	 *         RO_CHECK_BALANCE/ RO_PRICE_ENQUERY
	 */
	//public int getType(); Already specified in Message

	/**
	 * This method is used by application to access Diameter Destination-Realm
	 * AVP of Ro request.
	 *
	 * @return Destination-Realm AVP value
	 */
	public DiamIdent getDestinationRealm();

	/**
	 * This method is used by application to access Diameter Destination-Host
	 * AVP of Ro request.
	 *
	 * @return Destination-Host AVP value
	 */
	public DiamIdent getDestinationHost();

	/**
	 * This method is used by application to set Diameter Destination-Realm
	 * AVP of Ro request.
	 *
	 * @param destRealm Destination-Realm AVP value to be set
	 *
	 * @throws IllegalStateException if the request is already sent
	 */
	public void setDestinationRealm(DiamIdent destRealm);

	/**
	 * This method is used by application to set Diameter Destination-Host
	 * AVP of Ro request.
	 *
	 * @param destHost Destination-Host AVP value to be set
	 *
	 * @throws IllegalStateException if the request is already sent
	 */
	public void setDestinationHost(DiamIdent destHost);
}
