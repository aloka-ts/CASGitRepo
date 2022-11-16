/**
 * FileName:	ServiceInformation.java
 *
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Service-Information AVP that is part 
 * of a credit control message.
 *
 * @author Neeraj Jain.
 */

public interface ServiceInformation {
	/**
 	 * This method returns the IMS-Information AVP that is part of a 
	 * Service-Information AVP in a credit control message.
	 *
	 * @return <code>IMSInformation</code> object containing IMS-Information AVP.
	 */

	public IMSInformation getIMSInformation();

	/**
	 * This method associates an IMS-Information AVP to a Service-Information 
	 * AVP in a credit control message.
	 *
	 * @param imsInfo - <code>IMSInformation</code> object containing IMS-Information 
	 * AVPto be associated.
	 */

	public void setIMSInformation(IMSInformation imsInfo);
}

