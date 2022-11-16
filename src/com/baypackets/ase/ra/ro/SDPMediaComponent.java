/**
 * Filename:	SDPMediaComponent.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/**
 * This interface defines the SDP-Media-Component AVP that is part of 
 * IMS-Information AVP in a credit control message.
 *
 * @author Neeraj Jain
 */

public interface SDPMediaComponent {

	/**
 	 * This method returns the SDP-Media-Name AVP associated with the 
	 * credit control message.
	 *
	 * @return <code>String</code> object containing SDP-Media-Name AVP.
	 */

	public String getSDPMediaName();

	/**
 	 * This method returns the SDP-Media-Description AVP associated with the 
	 * credit control message.
	 *
	 * @return <code>Iterator</code> object on SDP-Media-Description AVP.
	 */
	public Iterator getSDPMediaDescriptions();

	/**
 	 * This method returns the Media-Initator-Flag AVP associated with the 
	 * credit control message.
	 *
	 * @return <code>short</code> object containing Media-Initator-Flag AVP.
	 */
	public short getMediaInitiatorFlag();

	/**
 	 * This method returns the Authorized-Qos AVP associated with the 
	 * credit control message.
	 *
	 * @return <code>String</code> object containing Authorized-Qos AVP.
	 */
	public String getAuthorizedQoS();

	/**
 	 * This method returns the GPRS-Charging-Id AVP associated with the 
	 * credit control message.
	 *
	 * @return <code>String</code> object containing SDP-Media-Name AVP.
	 */
	public String getGPRSChargingId();

	/**
	 * This method associates the SDP-Media-Name AVP to a credit control message.
	 *
	 * @param sdpMediaName - <code>String</code> object containing SDP-Media-Name AVP to be set.
	 */

	public void setSDPMediaName(String sdpMediaName);

	/**
	 * This method associates the SDP-Media-Description AVP to a credit control message.
	 *
	 * @param sdpMediaDesc - <code>String</code> object containing SDP-Media-Description AVP to be set.
	 */

	public void addSDPMediaDescription(String sdpMediaDesc);

	/**
	 * This method removes the SDP-Media-Description AVP associated with a credit control 
 	 * message.
	 *
	 * @param sdpMediaDesc - <code>String</code> object containing SDP-Media-Description AVP to be removed.
	 */

	public boolean removeSDPMediaDescription(String sdpMediaDesc);

	/**
	 * This method associates the Media-Initiator-Flag AVP to a credit control message.
	 *
	 * @param mediaInitFlag - <code>short</code> object containing Media-Initiator-Flag AVP to be set.
	 */

	public void setMediaInitiatorFlag(short mediaInitFlag);

	/**
	 * This method associates the Authorized-Qos AVP to a credit control message.
	 *
	 * @param qos - <code>String</code> object containing Authorized-Qos AVP to be set.
	 */

	public void setAuthorizedQoS(String qos);

	/**
	 * This method associates the GPRS-Charging-Id AVP to a credit control message.
	 *
	 * @param gprsChargingId - <code>String</code> object containing GPRS-Charging-Id AVP to be set.
	 */

	public void setGPRSChargingId(String gprsChargingId);
}

