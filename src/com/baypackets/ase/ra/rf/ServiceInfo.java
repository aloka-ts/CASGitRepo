package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.spi.container.SasMessage;

/**
 * This class defines the Service-Information AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Service-Information AVP.
 *
 * @author Prashant Kumar
 *
 */

public class ServiceInfo implements Serializable {

	private static Logger logger = Logger.getLogger(ServiceInfo.class);
	
	private com.condor.chargingcommon.ServiceInfo m_serviceInfo = null ;

	private ImsInfo imsInfo = null;

	/**
	 * This method creates a new instance of ServiceInfo
	 *
	 */	
	public ServiceInfo()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of ServiceInfo ");
		}
		m_serviceInfo = new com.condor.chargingcommon.ServiceInfo() ;
	}
	
	/**
	 * This method returns ImsInfo AVP
	 *
	 * @return <code>ImsInfo</code> object containing ImsInfo AVP
	 */
	public ImsInfo getImsInfo()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getImsInfo() called.");
		}
		return imsInfo ;
	}

	/**
	 * This method is used to set ImsInfo AVP 
	 *
	 * @param info - ImsInfo AVP to be set.
	 */
	public void setImsInfo( ImsInfo info)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setImsInfo() called.");
		}
		imsInfo = info ;
		m_serviceInfo.setImsInfo(info.getStackObject()) ;
	}

	
	/**
	 * This method returns a boolean which represents the presence of ImsInfo AVP
	 *
	 * @return boolean - true if pesent
	 * 					 false if not present
	 */
	public boolean getIsImsInfoPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsImsInfoPresent() called.");
		}
		return m_serviceInfo.getIsImsInfoPresent() ;
	}

	/**
	 * This method sets a boolean which represents the presence of ImsInfo AVP
	 *
	 * @param flag - true if present
	 * 				 false if not present
	 */
	public void setIsImsInfoPresent( boolean flag )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsImsInfoPresent() called.");
		}
		m_serviceInfo.setIsImsInfoPresent(flag ) ;	
	}
	public com.condor.chargingcommon.ServiceInfo getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_serviceInfo;
	}
}
	
