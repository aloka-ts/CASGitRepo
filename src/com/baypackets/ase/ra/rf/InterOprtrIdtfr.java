package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Inter-Operator-Identifier AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Inter-Operator-Identifier AVP.
 *
 * @author Prashant Kumar
 *
 */

public class InterOprtrIdtfr implements Serializable {
	
	private Logger logger = Logger.getLogger(InterOprtrIdtfr.class);

	private com.condor.chargingcommon.InterOprtrIdtfr m_interOptID = null;
	
	/**
	 * This method creates a new instance of InterOprtrIdtfr
	 */
	public InterOprtrIdtfr()
	{
		if(logger.isDebugEnabled())
			logger.debug("Inside the constructor of InterOprtrIdtfr");			
		m_interOptID = new com.condor.chargingcommon.InterOprtrIdtfr();
	}

	/**
	 * This method is used by application to get Originating-IOI AVP
	 *
	 * @return String object containing Originating-IOI AVP
	 */		
	public String getOrigIOI()
	{
		if(logger.isDebugEnabled())
			logger.debug("getOrigIOI() called.");
		return m_interOptID.getOrigIOI();
	}
	
	/**
	 * This method is used by application to get Terminating-IOI AVP
	 *
	 * @return String object containing Terminating-IOI AVP
	 */
	public String getTerminatingIOI()
	{
		if(logger.isDebugEnabled())
			logger.debug("getTerminatingIOI() called.");
		return m_interOptID.getTerminatingIOI();
	}
	
	/**
	 * This method is used by application to set Originating-IOI AVP
	 * 
	 * @param origIOI - Originating-IOI AVP to be set
	 */
	public void setOrigIOI( String origIOI)
	{
		if(logger.isDebugEnabled())
			logger.debug("setOrigIOI() called.");
		m_interOptID.setOrigIOI(origIOI);
	}

	/**
	 * This method is used by application to set Terminating-IOI AVP
	 * 
	 * @param termIOI - Terminating-IOI AVP to be set
	 */
	public void setTerminatingIOI( String termIOI )
	{
		if(logger.isDebugEnabled())
			logger.debug("setTerminatingIOI() called.");
		m_interOptID.setTerminatingIOI(termIOI);
	}
	
	public com.condor.chargingcommon.InterOprtrIdtfr getStackObject()
	{
		if(logger.isDebugEnabled())
			logger.debug("getStackObject() called.");
		return m_interOptID;
	}
}	
	

