package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Server-Capabilities AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Server-Capabilities AVP.
 *
 * @author Prashant Kumar
 *
 */

public class ServerCapability implements Serializable {
	
	private Logger logger = Logger.getLogger(ServerCapability.class);

	public static int EXCEEDED_MAX_LIMIT = com.condor.chargingcommon.ServerCapability.EXCEEDED_MAX_LIMIT ;
	public static int SUCCESS = com.condor.chargingcommon.ServerCapability.SUCCESS ;

	private com.condor.chargingcommon.ServerCapability m_serverCapability = null;

	/**
	 * This method creates a new instance of ServerCapability
	 *
	 */
	public ServerCapability()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of ServerCapability ");
		}
		m_serverCapability = new com.condor.chargingcommon.ServerCapability();
	}

	/**
	 * This method is used by application to get madatory capability at a specific index
	 *
	 * @param index - index at which mandatory capability is to be get
	 */	
	public int getMandatoryCapability( int index)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getMandatoryCapability() called.");
		}
		return m_serverCapability.getMandatoryCapability(index);
	}
	
	/**
	 * This method is used by application to set madatory capability at a specific index
	 *
	 * @param manCapabi -  madatory capability to be set
	 * 		  index - index at which mandatory capability is to be set
	 */
	public boolean setMandatoryCapability( int manCapabi , int index)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setMandatoryCapability() called.");
		}
		try{
				m_serverCapability.setMandatoryCapability(manCapabi , index);
				return true;
		}
		catch(Exception e )
		{
			return false;
		}
	}
	
	/** returns the number of mandatory capability
	 *
	 */
	public byte getNoOfMandatoryCapabilitys()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfMandatoryCapabilitys() called.");
		}
		return m_serverCapability.getNoOfMandatoryCapabilitys();
	}

	/**
	 * returns the number of optional capability
	 *
	 */
	public  byte getNoOfOptionalCapabilitys()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfOptionalCapabilitys() called.");
		}
		return m_serverCapability.getNoOfOptionalCapabilitys();
	}

	/**
	 * Returns the number of server name 
	 *
	 */
	public byte getNoOfServerName()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfServerName() called.");
		}
		return m_serverCapability.getNoOfServerName();
	}
	
	/** 
	 * Returns the optional capability at a specified index 
	 *
	 */
	public int getOptionalCapability( int index)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getOptionalCapability() called.");
		}
		return m_serverCapability.getOptionalCapability(index);
	}

	/**
	 * Sets the optional capability at a specified index
	 *
	 */
	public boolean setOptionalCapability( int optCapabi , int indx)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setOptionalCapability() called.");
		}
		try{
				m_serverCapability.setOptionalCapability(optCapabi , indx);
				return true;
		}
		catch(Exception e )
		{
			return false;
		}
	}
	
	/**
	 * Returns the server name at a specified index
	 *
	 */
	public String getServerName( int index)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getServerName() called.");
		}
		return m_serverCapability.getServerName(index);
	}
	
	/**	 
	 * Sets the server name at a specified index
	 *
	 */
	public boolean setServerName(String servername , int index)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setServerName() called.");
		}
		try{
				m_serverCapability.setServerName(servername , index);
				return true ;
		}
		catch(Exception e)
		{
			return false;
		}	
	}

	/**
	 * Initializes manadatory capability array
	 *
	 */
	public int initNoOfMandatoryCapabilitys( byte count)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initNoOfMandatoryCapabilitys() called.");
		}
		return m_serverCapability.initNoOfMandatoryCapabilitys(count);
	}

	/**
	 * Initializes optional capability array
	 *
	 */
	public int initNoOfOptionalCapabilitys( byte count)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initNoOfOptionalCapabilitys() called.");
		}
		return m_serverCapability.initNoOfOptionalCapabilitys(count);
	}

	/**
	 * Initializes server name array
	 *
	 */
	public int initNoOfServerName( byte count)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initNoOfServerName() called.");
		}
		return m_serverCapability.initNoOfServerName(count);
	}
	public com.condor.chargingcommon.ServerCapability getStackObject()
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("getStackObject() called.");
		}
		return m_serverCapability;
	}
}
