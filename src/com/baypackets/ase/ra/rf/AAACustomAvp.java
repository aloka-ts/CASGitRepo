package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the custom AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * @author Prashant Kumar
 *
 */

public class AAACustomAvp implements Serializable {

	private static Logger logger = Logger.getLogger(AAACustomAvp.class);

	private com.condor.diaCommon.AAACustomAvp m_customAvp = null ;

	private AAACustomGrpAvp customGrpAvp = null ;

	private AAACustomSingleAVP customSingleAvp = null ;
	
	public AAACustomAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constuctor of AAACustomAvp");
		}
		m_customAvp = new com.condor.diaCommon.AAACustomAvp();
	}
	/**
	 * Returns the Group AVP Object
	 *
	 * @return The Group AVP Object
	 */
	public AAACustomGrpAvp getGroupedAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGroupedAvp() called.");
		}
		return customGrpAvp ;
	}
	/**
	 * Sets the Group AVP Object
 	 * @param grpAvp - Group AVP Object to be set.
	 */
	public void setGroupedpAvp(AAACustomGrpAvp grpAvp )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGroupedAvp() called.");
		}
		customGrpAvp = grpAvp ;
		m_customAvp.setGroupedpAvp(grpAvp.getStackObject());
	}
	/**
	 * Return the Presence of Group AVP object
	 * 
	 * @return true if Group AVP object present else false
	 */
	public boolean getIsGrpAvpPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsGrpAvpPresent() called.");
		}
		return m_customAvp.getIsGrpAvpPresent() ;
	}
	/**
	 * Returns the Single AVP Object
	 *
	 * @return The Single AVP Object
	 */
	public AAACustomSingleAVP getSingleAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSingleAvp() called.");
		}
		return customSingleAvp ;
	}
	/**
 	 * Sets the single AVp object
 	 * 
	 * @param singleAvp - single AVp object to be set.
	 */
	public void setSingleAvp( AAACustomSingleAVP singleAvp )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSingleAvp() called.");
		}
		customSingleAvp = singleAvp ;
		m_customAvp.setSingleAvp(singleAvp.getStackObject()) ;
	}
	public com.condor.diaCommon.AAACustomAvp getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_customAvp ;
	}
}
		
