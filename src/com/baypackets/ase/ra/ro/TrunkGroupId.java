/**
 * Filename:	TrunkGroupId.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface defines the Trunk-Group-ID AVP that is part of
 * IMS-Information AVP in a credit control message.
 *
 * @author Neeraj Jain
 */

public interface TrunkGroupId {

	/**
	 * This method returns the Incoming-Trunk-Group-ID AVP that is
	 * part of Trunk-Group-ID AVP in a credit control message.
	 *
	 * @return <code>String</code> object containing Incoming-Trunk-Group-ID AVP.
	 */

	public String getIncomingTrunkGroupId();

	/**
	 * This method returns the Outgoing-Trunk-Group-ID AVP that is
	 * part of Trunk-Group-ID AVP in a credit control message.
	 *
	 * @return <code>String</code> object containing Outgoing-Trunk-Group-ID AVP.
	 */

	public String getOutgoingTrunkGroupId();

	/**
	 * This method sets the Incoming-Trunk-Group-ID AVP into Trunk-Group-ID
	 * AVP.
	 *
	 * @param itgId - <code>String</code> object containing Incoming-Trunk-Group-ID 
	 * AVP to be set.
	 */

	public void setIncomingTrunkGroupId(String itgId);
	
	/**
	 * This method sets the Outgoing-Trunk-Group-ID AVP into Trunk-Group-ID
	 * AVP.
	 *
	 * @param otgId - <code>String</code> object containing Outgoing-Trunk-Group-ID 
	 * AVP to be set.
	 */

	public void setOutgoingTrunkGroupId(String otgId);
}

