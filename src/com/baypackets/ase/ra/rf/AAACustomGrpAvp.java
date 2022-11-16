package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the custom grouped AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * @author Prashant Kumar
 *
 */

public class AAACustomGrpAvp implements Serializable {

	private static Logger logger = Logger.getLogger(AAACustomGrpAvp.class) ;

	private com.condor.diaCommon.AAACustomGrpAvp m_groupAvp = null ;

	private AAACustomSingleAVP[] singleAvp = null ;
	/**
	 * Creates a new instance of AAACustomGrpAvp
	 *
	 */	
	public AAACustomGrpAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of AAACustomGrpAvp");
		}
		m_groupAvp = new com.condor.diaCommon.AAACustomGrpAvp() ;
	}
	/**
	 * Returns the Group AVP Code
	 *
	 * @return The Group AVP Code
	 */
	public int getGrpAvpCode()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGrpAvpCode() called.");
		}
		return m_groupAvp.getGrpAvpCode() ;
	}	
	/** 
	 * Sets the Group AVP Code
	 *
	 * @param code the Group AVP Code
	 */
	public void setGrpAvpCode( int code )	
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGrpAvpCode() called.");
		}
		m_groupAvp.setGrpAvpCode(code);
	}	
	/**
	 * Returns the Group AVP Flag
	 *
	 * @return The Group AVP Flag
	 */
	public byte getGrpAvpFlag()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGrpAvpFlag() called.");
		}
		return m_groupAvp.getGrpAvpFlag() ;
	}
	/**
	 * Sets the Group AVP Flag
	 * 
	 * @param flag The Group AVP Flag
	 */
	public void setGrpAvpFlag( byte flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGrpAvpFlag() called.");
		}
		m_groupAvp.setGrpAvpFlag(flag);
	}	
	/**	
 	 * Returns the Group AVP Vendor ID	
	 *
	 * @return <code>int</code> - The Group AVP Vendor ID
	 */
	public int getGrpVId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGrpVId() called.");
		}
		return m_groupAvp.getGrpVId() ;
	}
	/**
	 * Sets the Group AVP Vendor ID
	 *
	 * @param  id The Group AVP Vendor ID
	 */
	public void setGrpVId( int id )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGrpVId() called.");
		}
		m_groupAvp.setGrpVId(id);
	}	
	/**
	 * Returns the number of Group AVPs
	 *
	 * @return The number of Group AVPs
	 */
	public int getNumberOfSingleAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNumberOfSingleAvp() called.");
		}
		return m_groupAvp.getNumberOfSingleAvp() ;
	}
	/**
	 * Returns the Single AVP object for that given index, null in case of error
	 *
	 * @return The <code>AAACustomSingleAVP</code> object 
	 */
	public AAACustomSingleAVP getSingleAvp( int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSingleAvp() called.");
		}
		return singleAvp[index] ;
	}	
	/**
	 * Sets the Single AVP object for that given index
	 *
	 * @param avp - AAACustomSingleAVP object to be added.
	 * 		  index - <code>int</code> index where the AVP is to be added
	 */
	public int setSingleAvp( AAACustomSingleAVP avp , int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSingleAvp() called.");
		}
		singleAvp[index] = avp ;
		return m_groupAvp.setSingleAvp(avp.getStackObject() , index);
	}
	/**	
	 * Creates the Custom AVP array for the given counter 
	 *
	 * param counter The size of the array to initialize
	 */
	public int initSingleAvp( int counter )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initSingleAvp() called.");
		}
		singleAvp = new AAACustomSingleAVP[counter] ;
		return m_groupAvp.initSingleAvp(counter);
	}
	public com.condor.diaCommon.AAACustomGrpAvp getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_groupAvp ;
	}
}	
		
	

