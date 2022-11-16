package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Time-Stamp AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Time-Stamp AVP.
 *
 * @author Prashant Kumar
 *
 */

public class TimeStamp implements Serializable {
	
	private Logger logger = Logger.getLogger(TimeStamp.class);

	private com.condor.chargingcommon.TimeStamp m_timestamp = null;

	/**
	 * This method creates a new instance of TimeStamp
	 *
	 */
	public TimeStamp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of TimeStamp ");
		}	
		m_timestamp = new com.condor.chargingcommon.TimeStamp();
	}

	/**
	 * This method returns the time of initial sip request
	 *
	 */	
	public String getSipReqTimeStamp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSipReqTimeStamp() called.");
		}	
		return m_timestamp.getSipReqTimeStamp();
	}

	/**
	 * This method returns the time of initial sip response
	 *
	 */
        public String getSipResTimeStamp()
        {
			if (logger.isDebugEnabled()) {
				logger.debug("getSipResTimeStamp() called.");
			}
                return m_timestamp.getSipResTimeStamp();
        }

	/**
	 * This method sets the time of initial sip request
	 *
	 */
        public void setSipReqTimeStamp(String s)
        {
			if (logger.isDebugEnabled()) {
				logger.debug("setSipReqTimeStamp() called.");
			}
                m_timestamp.setSipReqTimeStamp( s) ;
        }

	/**
	 * This method sets the time of initial sip response
	 *
	 */
        public void setSipResiTmeStamp(String s)
        {
			if (logger.isDebugEnabled()) {
				logger.debug("setSipResiTmeStamp() called.");
			}
		m_timestamp.setSipResTimeStamp( s) ;
	}
	public com.condor.chargingcommon.TimeStamp getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_timestamp ;
	}
}
