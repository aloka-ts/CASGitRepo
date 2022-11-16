package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class contains the info regarding all the custom AVP that are part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * @author Prashant Kumar
 *
 */

public class AAACustomAvpInfo implements Serializable {

	private static Logger logger = Logger.getLogger(AAACustomAvpInfo.class);

	private com.condor.diaCommon.AAACustomAvpInfo m_customAVPInfo = null ;
	
	private AAACustomAvp[] customAvp = null;
	/**
	 * Creates a new object of AAACustomAvpInfo
	 *
	 */
	public AAACustomAvpInfo()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of AAACustomAvpInfo");
		}
		m_customAVPInfo = new com.condor.diaCommon.AAACustomAvpInfo();
	}
	/**
	 * Returns the custom AVP object for that index, null in case of errors
	 * 
	 * @return The custom AVP object for this index.
	 */
	public AAACustomAvp getCustomAvp( int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getCustomAvp() called.");
		}
		return customAvp[index];
	}
	/**
	 * Assigns the custom AVP object for that index value to the given AAACustomAVP object in parameter
	 * 
	 * @param avp - custom AVP object to be assigned
	 * @param index - index  at which AVP object to be added.
	 */
	public int setCustomAvp( AAACustomAvp avp , int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setCustomAvp() called.");
		}
		customAvp[index] = avp ;
		return m_customAVPInfo.setCustomAvp( avp.getStackObject() , index ) ;
	}
	/**
 	 * Returns the number of Custom AVPs	
	 * 
	 * @return the number of Custom AVPs 
	 *
	 */
	public int getNoOfCustomAvp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfCustomAvp() called.");
		}
		return m_customAVPInfo.getNoOfCustomAvp();
	}
	/** 
	 * Creates the Custom AVP array for the given counter 
	 *
	 * @param counter size of the array.
	 */
	public int initCustomAvpTable( int counter )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initCustomAvpTable() called.");
		}
		customAvp = new AAACustomAvp[counter] ;
		return m_customAVPInfo.initCustomAvpTable(counter) ;
	}
	public com.condor.diaCommon.AAACustomAvpInfo getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_customAVPInfo ;
	}
}
