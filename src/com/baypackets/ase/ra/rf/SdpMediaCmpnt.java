package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the SDP-Media-Component AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of SDP-Media-Component AVP.
 *
 * @author Prashant Kumar
 *
 */

public class SdpMediaCmpnt implements Serializable {

	private Logger logger = Logger.getLogger(SdpMediaCmpnt.class);
	
	private com.condor.chargingcommon.SdpMediaCmpnt m_sdpMediaCmp = null;
	
	/**	
 	 * This method creates a new instance of SdpMediaCmpnt
	 *
	 */
	public SdpMediaCmpnt()
	{
		if (logger.isDebugEnabled()) {		
			logger.debug("Inside the constructor of SdpMediaCmpnt");
		}
		m_sdpMediaCmp = new com.condor.chargingcommon.SdpMediaCmpnt();
	}

	/**
	 * This method is used by appliaction to get AuthorizedQOS AVP
	 *
	 * @return String object containing AuthorizedQOS AVP
	 */ 
	public String getAuthorizedQOS()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAuthorizedQOS() called.");
		}
		return m_sdpMediaCmp.getAuthorizedQOS();
	}
	
	/**
	 * This method is used by appliaction to set AuthorizedQOS AVP
	 *
	 * @param qos - AuthorizedQOS AVP to be set
	 */
	public void setAuthorizedQOS( String qos )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setAuthorizedQOS() called.");
		}
		m_sdpMediaCmp.setAuthorizedQOS(qos);
	}

	/**
	 * This method is used by appliaction to get GPRS-Charging-ID AVP
	 *
	 * @return String object containing GPRS-Charging-ID AVP
	 */
	public String getGPRSChargingId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGPRSChargingId() called.");
		}
		return m_sdpMediaCmp.getGPRSChargingId();
	}

	/**
	 * This method is used by appliaction to set GPRS-Charging-ID AVP
	 *
	 * @param gprsId - GPRS-Charging-ID AVP to be set
	 */
	public void setGPRSChargingId( String gprsId)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGPRSChargingId() called.");
		}
		m_sdpMediaCmp.setGPRSChargingId(gprsId);
	}
	
	/**
	 * This method returns a boolean showing the presence of Media-Initiator-Flag AVP
	 *
 	 * @return boolean - true if present
	 * 				   - false if not present
	 */
	public boolean getIsMediaInitFlagPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsMediaInitFlagPresent() called.");
		}
		return m_sdpMediaCmp.getIsMediaInitFlagPresent();
	}
	
	/**
	 * This method is used by appliaction to get Media-Initiator-Flag AVP
	 *
	 * @return Media-Initiator-Flag AVP
	 */
	public int getMediaInitFlag()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getMediaInitFlag() called.");
		}
		return m_sdpMediaCmp.getMediaInitFlag();
	}

	/**
	 * This method is used by appliaction to set Media-Initiator-Flag AVP
	 *
	 * @param flag - Media-Initiator-Flag AVP to be set
	 */
	public void setMediaInitFlag( int flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setMediaInitFlag() called.");
		}
		m_sdpMediaCmp.setMediaInitFlag(flag);
	}

	/**
	 * This method returns Number of SdpMediaDescriptors in this request
	 *
	 * @return Number of SdpMediaDescriptors 
	 */ 
	public byte getNoOfsdpMediaDesc()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfsdpMediaDesc() called.");
		}
		return m_sdpMediaCmp.getNoOfsdpMediaDesc();
	}

	/**
	 * This method is used by application to get the SdpMediaDescriptor at
	 * a specified index
	 *
	 * @return String object containing SdpMediaDescriptor
	 */
	public String getSdpMediaDesc( int index)
	{	
		if (logger.isDebugEnabled()) {
			logger.debug("getSdpMediaDesc() called.");
		}
		return m_sdpMediaCmp.getSdpMediaDesc(index);
	}
	
	/**
	 * This method is used by application to set SdpMediaDescriptor at 
	 * a specified index
	 *
	 * @param sdpmediaDes - SdpMediaDescriptor AVp to be set
	 * 		  index - index at which AVP is to be set
	 */
	public boolean setSdpMediaDesc(String sdpmediaDes , int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSdpMediaDesc() called.");
		}
		return m_sdpMediaCmp.setSdpMediaDesc( sdpmediaDes , index);
	}
	
	/**
	 * This method is used by application to get SDP-Media-Name AVP
	 * 
	 * @return SDP-Media-Name AVP
	 */
	public String getSdpMediaName()
	{ 
		if (logger.isDebugEnabled()) {
			logger.debug("getSdpMediaName() called.");
		}
		return m_sdpMediaCmp.getSdpMediaName();
	}

	/**
	 * This method is used by application to set SDP-Media-Name AVP
	 * 
	 * @param sdpName  - SDP-Media-Name AVP to be set	
 	 */	 	
	public void setSdpMediaName( String sdpName)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSdpMediaName() called.");
		}
		m_sdpMediaCmp.setSdpMediaName(sdpName);
	}
	
	/**
	 * This method is used by application to initialize SdpMediaDescriptor array
	 *
	 * @param count - size of the array to be initialized
	 */
	public int initNoOfsdpMediaDesc( byte count )
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("initNoOfsdpMediaDesc() called.");
		}
		return m_sdpMediaCmp.initNoOfsdpMediaDesc(count);
	}
	public com.condor.chargingcommon.SdpMediaCmpnt getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
		return m_sdpMediaCmp;
	}
}
