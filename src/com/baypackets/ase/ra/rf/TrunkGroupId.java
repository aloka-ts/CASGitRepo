package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Trunk-Group-Id AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Trunk-Group-Id AVP.
 *
 * @author Prashant Kumar
 *
 */

public class TrunkGroupId implements Serializable {

	private Logger logger = Logger.getLogger(TrunkGroupId.class);

	private com.condor.chargingcommon.TrunkGroupId m_trunkGrpId = null;

	/**
	 * This method creates a new instance of TrunkGroupId
	 *
	 */
	public TrunkGroupId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of TrunkGroupId ");
		}
		m_trunkGrpId = new com.condor.chargingcommon.TrunkGroupId();
	}

	/**
	 * This method returns Incoming-Trunk-Group-ID AVP
 	 *	
	 * return String object containing Incoming-Trunk-Group-ID AVP
	 */
	public String getIncomingTGID()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIncomingTGID() called.");
		}
		return m_trunkGrpId.getIncomingTGID();
	}

	/**
	 * This method returns Outgoing-Trunk-Group-ID AVP
	 *
	 * @return String object containing Outgoing-Trunk-Group-ID AVP
	 */
	public String getOutgoingTGID()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getOutgoingTGID() called.");
		}
		return m_trunkGrpId.getOutgoingTGID();
	}

	/**
	 * This method sets the Incoming-Trunk-Group-ID AVP
	 *
	 * @param InID - Incoming-Trunk-Group-ID AVP to be set.
	 */
	public void setIncomingTGID(java.lang.String InID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIncomingTGID() called.");
		}
		m_trunkGrpId.setIncomingTGID( InID ) ;
	}

	/**
	 * This method sets the Outgoing-Trunk-Group-ID AVP
	 *
	 * @param OuID - Outgoing-Trunk-Group-ID AVP to be set
	 */
	public void setOutgoingTGID(java.lang.String OuID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setOutgoingTGID() called.");
		}
		m_trunkGrpId.setOutgoingTGID( OuID) ;
	}
	public com.condor.chargingcommon.TrunkGroupId getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_trunkGrpId;
	}
}	
