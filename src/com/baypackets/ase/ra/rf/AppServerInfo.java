package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Application-Server-Information AVP that is part of an accounting request.
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Application-Server-Information AVP.
 *
 * @author Prashant Kumar
 *
 */

public class AppServerInfo implements Serializable {

	public static Logger logger = Logger.getLogger(AppServerInfo.class);
	
	com.condor.chargingcommon.AppServerInfo m_appServInfo = null;
	
	/**
 	 * Creates a new instance of AppServerInfo
	 *
	 */
	public AppServerInfo()
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("Inside the constructor of AppServerInfo");
		}
		m_appServInfo = new com.condor.chargingcommon.AppServerInfo();
	}
	/**
	 * This method returns Application-Provided-Called-Party-Address AVP
	 *
	 * @return String object containing Application-Provided-Called-Party-Address AVP
	 */
	public String getAppProvidedCldPrtyAdrs()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAppProvidedCldPrtyAdrs() called.");
		}
		return m_appServInfo.getAppProvidedCldPrtyAdrs();
	}
	/** 
	 * This methdod sets the Application-Provided-Called-Party-Address AVP.
	 * The Application-Provided-Called-Party-Address AVP holds the called 
	 * party number (SIP URI, E.164), if it is determined by an application server
	 * 
	 * @param cldparty Application-Provided-Called-Party-Address AVP to be set
	 */
	public void setAppProvidedCldPrtyAdrs(String cldparty)
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("setAppProvidedCldPrtyAdrs() called.");
		}
		m_appServInfo.setAppProvidedCldPrtyAdrs(cldparty);
	}
	/**
	 * This method returns Application-Server AVP
	 *
	 * @return String object containing Application-Server AVP
 	 */ 
	public String getAppServer()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAppServer() called.");
		}
		return m_appServInfo.getAppServer();
	}
	/** This method sets the Application-Server AVP
	 * 
	 * @param appServer - The Application-Server AVP to be set.
	 */
	public void setAppServer( String appServer)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setAppServer() called.");
		}
		m_appServInfo.setAppServer(appServer);
	}
	
	public com.condor.chargingcommon.AppServerInfo getStackObject()
	{	
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_appServInfo;
	}
}
