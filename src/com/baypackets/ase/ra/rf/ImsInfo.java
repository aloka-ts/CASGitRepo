package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the IMS-Information AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of IMS-Information AVP.
 *
 * @author Prashant Kumar
 *
 */

public class ImsInfo implements Serializable
{

	private static Logger logger = Logger.getLogger(ImsInfo.class);
	
	private com.condor.chargingcommon.ImsInfo m_imsInfo = null ;

	private AppServerInfo[] appServInfo = null;
	private EventType eventtype = null;
	private InterOprtrIdtfr interOprID = null;
	private MessageBody messageBody = null;
	private SdpMediaCmpnt[] sdpMediaComp = null;
	private ServerCapability serverCapability = null;
	private TimeStamp timeStamp = null;
	private TrunkGroupId trungkGrpID = null;

	/**
	 * This method returns an instance of ImsInfo
	 *
	 */
	public ImsInfo()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of ImsInfo");
		}
		m_imsInfo = new com.condor.chargingcommon.ImsInfo() ;
	}

	/**
	 * This method is used by application to get ApplicationServiceInfo
	 * AVP from Specified index
	 *
	 * @return <code>AppServerInfo</code> object containing ApplicationServiceInfo AVP
	 */
	public AppServerInfo getAppServInfo(int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAppServInfo() called.");	
		}
		return appServInfo[index];
	}

	/**
	 * This method is used by application to set ApplicationServiceInfo
	 * AVP at Specified index
	 *
	 * @param servInfo - ApplicationServiceInfo AVP to be added.
	 * 		  index - index at which ApplicationServiceInfo AVP is to be added
	 */
	public boolean setAppServInfo( AppServerInfo servInfo , int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setAppServInfo() called.");	
		}
		try
		{
			appServInfo[index] = servInfo;
			m_imsInfo.setAppServInfo(servInfo.getStackObject() , index );
			return true;
		}
		catch(Exception e )
		{
			return false;
		}
	}

	/**
	 * This method is used by application to get BearerService
	 * 
	 * @return String object containing BearerService
	 */
	public String getBearerService()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getBearerService() called.");	
		}
		return m_imsInfo.getBearerService() ;
	}
	
	/** This method is used by application to set BearerService
	 *
	 * @param service - BearerService to be set by application
	 */
	public void setBearerService( String service )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setBearerService() called.");	
		}
		m_imsInfo.setBearerService(service);
	}

	/**
	 * This method is used by application to get Called-Party-Address
	 *
	 * @return String object containing Called-Party-Address
	 */	
	public String getCalledPrtyAdrs()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getCalledPrtyAdrs() called.");	
		}
		return m_imsInfo.getCalledPrtyAdrs() ;
	}

	/**
	 * This method is used by application to set Called-Party-Address
	 *
	 * @param addr - Called-Party-Address to be set by application
	 */
	public void setCalledPrtyAdrs( String addr )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setCalledPrtyAdrs() called.");	
		}
		m_imsInfo.setCalledPrtyAdrs( addr) ;
	}

	/**
	 * This method is used by application to get CallingPartyAddress
	 *
	 * @return String object containing CallingPartyAddress
	 */ 	
	public String getCallingPrtyAdrs()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getCallingPrtyAdrs() called.");	
		}
		return m_imsInfo.getCallingPrtyAdrs() ;
	}

	/**
	 * This method is used by application to set CallingPartyAddress
	 *
	 * @param addr - CallingPartyAddress to be set
	 */
	public void setCallingPrtyAdrs(String addr )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setCallingPrtyAdrs() called.");	
		}
		m_imsInfo.setCallingPrtyAdrs(addr);
	}

	/**
	 * This method is used by application to get CauseCode AVP
	 *
	 * return CauseCode AVP
	 */
	public int getCauseCode()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getCauseCode() called.");	
		}
		return m_imsInfo.getCauseCode() ;
	}

	/**
	 * This method is used by application to set CauseCode AVP
	 *
	 * @param code - CauseCode AVP to be set
	 */
	public void setCauseCode( int code )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setCauseCode() called.");	
		}
		m_imsInfo.setCauseCode(code);
	}
	
	/**
	 * This method is used by application to get Event-Type AVP
	 *
	 * @return <code>EventType</code> object containing Event-Type AVP
	 */	

	public EventType getEventType()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getEventType() called.");	
		}
		return eventtype;
	}

	/** This method is used by application to set Event-Type AVP
	 *
	 * @param et - Event-Type AVP to be set
	 */
	public void setEventType( EventType et)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setEventType() called.");	
		}
		eventtype = et;
		m_imsInfo.setEventType(et.getStackObject());
	}

	/**
	 * This method is used by application to get GGSN-Address AVP
	 *
	 * @return String object containing GGSN-Address AVP
	 */
	public String getGgsnAddress()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getGgsnAddress() called.");	
		}
		return m_imsInfo.getGgsnAddress() ;
	}

	/**
	 * This method is used by application to set GGSN-Address AVP
	 *
	 * @param ggsnAddr - GGSN-Address AVP to be set
	 */
	public void setGgsnAddress( String ggsnAddr )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setGgsnAddress() called.");	
		}
		m_imsInfo.setGgsnAddress(ggsnAddr);
	}

	/**
	 * This method is used by application to get IMS-Charging-Identifier AVP
	 *
	 * @return String object containing IMS-Charging-Identifier AVP
	 */ 	
	public String getImsChargingID()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getImsChargingID() called.");	
		}
		return m_imsInfo.getImsChargingID() ;
	}

	/**
	 * This method is used by application to set IMS-Charging-Identifier AVP
	 *
	 * @param id - IMS-Charging-Identifier AVP to be set
	 */
	public void setImsChargingID(String id )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setImsChargingID() called.");	
		}	
		m_imsInfo.setImsChargingID(id);
	}

	/**
	 * This method is used by application to get Inter-Operator-Identifier AVP
	 *
	 * @return <code>InterOprtrIdtfr</code> object containing Inter-Operator-Identifier 
	 * AVP to be set
	 */
	public InterOprtrIdtfr getIntrOprtrIdtfr()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIntrOprtrIdtfr() called.");	
		}
		return interOprID ;
	}

	/**
	 * This method is used by application to set Inter-Operator-Identifier AVP
	 *
	 * @param optrId - Inter-Operator-Identifier AVP to be set
	 */
	public void setIntrOprtrIdtfr( InterOprtrIdtfr optrId )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIntrOprtrIdtfr() called.");	
		}
		interOprID = optrId;	
		m_imsInfo.setIntrOprtrIdtfr(optrId.getStackObject());
	}

	/**
	 * This method is used by application to get the status of a boolean 
	 * associated with the associated uri 
	 *
	 * @return boolean - true if associated uri present
	 * 					 false if associated uri not present
	 */
	public boolean getIsAssociatedUriPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsAssociatedUriPresent() called.");	
		}
		return m_imsInfo.getIsAssociatedUriPresent();
	}

	/**
	 * This method is used by application to set the status of a boolean
	 * associated with the associated uri
	 *
	 * @param flag - true if associated uri present
	 * 				 false if associated uri not present
	 */
	public void setIsAssociatedUriPresent( boolean flag )
	{	
		if (logger.isDebugEnabled()) {
			logger.debug("setIsAssociatedUriPresent() called.");	
		}
		m_imsInfo.setIsAssociatedUriPresent(flag);
	}

	/**
	 * This method is used by application to get the status of a boolean
	 * associated with the CauseCode AVP
	 *
	 * @return boolean - true if  CauseCode present
	 *                   false if CauseCode not present
	 */
	public boolean getIsCauseCodePresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsCauseCodePresent() called.");	
		}
		return m_imsInfo.getIsCauseCodePresent() ;
	}

	/**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of EventType AVP
	 *
	 * @return boolean - true if EventType present
	 * 					 false if EventType not present
	 */
	public boolean getIsEventTypePresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsEventTypePresent() called.");	
		}
		return m_imsInfo.getIsEventTypePresent() ;
	}

	/**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of InterOperatorID AVP
	 *
	 * @return boolean - true if present
	 * 					 false if not present
	 */
	public boolean getIsIntrOprtrIdtfrPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsIntrOprtrIdtfrPresent() called.");	
		}
		return m_imsInfo.getIsIntrOprtrIdtfrPresent() ;
	}

	/**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of MessageBody AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsMessageBodyPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsMessageBodyPresent() called.");	
		}
		return m_imsInfo.getIsMessageBodyPresent() ;
	}
   
	 /**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of NodeFunctionality AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsNodeFncltyPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsNodeFncltyPresent() called.");	
		}
		return m_imsInfo.getIsNodeFncltyPresent() ;
	}
   
	 /**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of RoleOfNode AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsRoleOfNodePresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsRoleOfNodePresent() called.");	
		}
		return m_imsInfo.getIsRoleOfNodePresent() ;
	}
   
	 /**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of ServerCapability AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsServerCapabilityPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsServerCapabilityPresent() called.");	
		}
		return m_imsInfo.getIsServerCapabilityPresent() ;
	}
   
	 /**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of TimeStamp AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsTimeStampPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsTimeStampPresent() called.");	
		}
		return m_imsInfo.getIsTimeStampPresent();
	}
   
	 /**
	 * This method is used by application to get the status of a boolean
	 * associated with the presnce of TrunkGroupID AVP
	 *
	 * @return boolean - true if present
	 *                   false if not present
	 */
	public boolean getIsTrunkGroupIdPresent()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsTrunkGroupIdPresent() called.");	
		}
		return m_imsInfo.getIsTrunkGroupIdPresent() ;
	}

	/**
	 * This method is used by application to get MessageBody AVP
	 *
	 * @return <code>MessageBody</code> object containing MessageBody AVP
	 */
	public MessageBody getMessageBody()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getMessageBody() called.");	
		}
		return messageBody ;
	}

	/**
	 * This method is used by application to set MessageBody AVP
	 *
	 * @param msgBody - MessageBody AVP to be set
	 */
	public void setMessageBody( MessageBody msgBody )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setMessageBody() called.");	
		}
		messageBody = msgBody ;
		m_imsInfo.setMessageBody(msgBody.getStackObject());
	}

	/**
	 * This method is used by application to get the Node-Functionality AVP
	 *
	 * @return Node-Functionality AVP 
	 */
	public int getNodeFnclty()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNodeFnclty() called.");	
		}
		return m_imsInfo.getNodeFnclty() ;
	}

	/**
	 * This method is used by application to set Node-Functionality AVP
	 *
	 * @param nodefunction - Node-Functionality AVP to be set
	 */
	public void setNodeFnclty( int nodefunction )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setNodeFnclty() called.");	
		}
		m_imsInfo.setNodeFnclty(nodefunction);
	}

	/**
	 * This method is used by application to get the Number Of ApplicationServerInformation 
	 * AVP present in the request
	 *
	 * @return Number Of ApplicationServerInformation AVP present in the request
	 */
	public byte getNoOfAppServInfo()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfAppServInfo() called.");	
		}
		return m_imsInfo.getNoOfAppServInfo() ;
	}

	/**
	 * This method is used by application to get the Number of SDP-Media-Component
	 * AVP present in the request
	 *
	 * @return Number of SDP-Media-Component AVP presnt in the request
	 */
	public byte getNoOfSDPMediaCmpnt()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfSDPMediaCmpnt() called.");	
		}
		return m_imsInfo.getNoOfSDPMediaCmpnt() ;
	}

	/**
	 * This method is used by application to get the number of SDP-Session-Description
	 * AVP present in the request
	 *
	 * @return number of SDP-Session-Description AVP present in the request
	 */
	public byte getNoOfSDPSesDscptn()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getNoOfSDPSesDscptn() called.");	
		}
		return m_imsInfo.getNoOfSDPSesDscptn();
	}

	/**
	 * This method is used by application to get the RoleOfNode AVP
	 *
	 * @return The RoleOfNode AVP
	 */
	public int getRoleOfNode()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getRoleOfNode() called.");	
		}
		return m_imsInfo.getRoleOfNode() ;
	}

	/**
	 * This method is used by application to set the RoleOfNode AVP
	 * 
	 * @param role - The RoleOfNode AVP to be set
	 */
	public void setRoleOfNode( int role )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setRoleOfNode() called.");	
		}
		m_imsInfo.setRoleOfNode(role);
	}

	/**
	 * This method is used by application to get SDP-Media-Component AVP
	 * at a specified index
	 * 
	 * @return <code>SdpMediaCmpnt</code> object containing SDP-Media-Component AVP
	 */
	public SdpMediaCmpnt getSdpMediaCmpnt(int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSdpMediaCmpnt() called.");	
		}
		return sdpMediaComp[index];
	}

	/**
	 * This method is used by application to set SDP-Media-Component AVP 
	 * at a specified index
	 * 
	 * @param sdpmedia - SDP-Media-Component AVP to be set
	 *        index - index at which SDP-Media-Component AVP to be set 
	 */
	public boolean setSdpMediaCmpnt( SdpMediaCmpnt sdpmedia , int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSdpMediaCmpnt() called.");	
		}
		try
		{
			sdpMediaComp[index] = sdpmedia ;
			m_imsInfo.setSdpMediaCmpnt(sdpmedia.getStackObject() , index );
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
			
	}

	/**
	 * This method is used by application to get SDP-Session-Description AVP 
	 * at a specified index
	 *
	 * @return String object containing SDP-Session-Description AVP
	 */
	public String getSdpSesDscptn( int index )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSdpSesDscptn() called.");	
		}
		return m_imsInfo.getSdpSesDscptn( index ) ;
	}

	/**
 	 * This method is used by application to set SDP-Session-Description AVP
	 * at a specified index
	 * 
	 * @param  sdpdscrpn - SDP-Session-Description AVP to be set
	 * 		   index - index at which AVP to be set
	 */
	public boolean setSdpSesDscptn( String sdpdscrpn , int index )
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("setSdpSesDscptn() called.");	
		}
		 return m_imsInfo.setSdpSesDscptn(sdpdscrpn , index );
	}

	/**
	 * This method is used by application to get Server-Capability AVP	
	 * 
	 * @return <code>ServerCapability<code> object containing Server-Capability AVP
	 */  		
	public ServerCapability getServerCapability()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getServerCapability() called.");	
		}
		return serverCapability;
	}

	/**
	 * This method is used by application to set Server-Capability AVP
	 * 
	 * @param servCapability - Server-Capability AVP to be set
	 */
	public void setServerCapability( ServerCapability servCapability )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setServerCapability() called.");	
		}
		serverCapability = servCapability;
		m_imsInfo.setServerCapability(servCapability.getStackObject());
	}

	/**
	 * This method is used by application to get Service-Id AVP
	 * 
	 * @return String object containing Service-Id AVP
	 */
	public String getServiceId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getServiceId() called.");	
		}
		return m_imsInfo.getServiceId() ;
	}

	/**
	 * This method is used by application to set Service-Id AVP
	 *
	 * @param servId - Service-Id AVP to be set
	 */
	public void setServiceId( String servId)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setServiceId() called.");	
		}
		m_imsInfo.setServiceId(servId);
	}

	/**
	 * This method is used by application to get Service-Specific-Data AVP
	 *	
	 * @return String object containing Service-Specific-Data AVP
	 */
	public String getServiceSpecData()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getServiceSpecData() called.");	
		}
		return m_imsInfo.getServiceSpecData() ;
	}

	/**
	 * This method is used by application to set Service-Specific-Data AVP
	 *
	 * @param servdata - Service-Specific-Data AVP to be set
	 */
	public void setServiceSpecData( String servdata )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setServiceSpecData() called.");	
		}
		m_imsInfo.setServiceSpecData(servdata);
	}

	/**
	 * This method is used by application to get Served-Party-IP-Address AVP
	 *
	 * @return String object containing Served-Party-IP-Address AVP
	 */
	public String getSrvdPrtyIpAdrs()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getSrvdPrtyIpAdrs() called.");	
		}
		return m_imsInfo.getSrvdPrtyIpAdrs() ;
	}

	/**
	 * This method is used by application to set Served-Party-IP-Address AVP	
	 *
	 * @param servdAddr - Served-Party-IP-Address AVP to be set
	 */	
	public void setSrvdPrtyIpAdrs( String servdAddr )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setSrvdPrtyIpAdrs() called.");	
		}
		m_imsInfo.setSrvdPrtyIpAdrs(servdAddr);
	}

	/**
	 * This method is used by application to get Time-Stamp AVP
	 *
	 * @return <code>TimeStamp</code> object containing Time-Stamp AVP
	 */
	public TimeStamp getTimeStamp()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getTimeStamp() called.");	
		}
		return timeStamp;
	}

	/**
	 * This method is used by application to set Time-Stamp AVP
	 *
	 * @param time - Time-Stamp AVP to be set
	 */
	public void setTimeStamp( TimeStamp time)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setTimeStamp() called.");	
		}
		timeStamp = time;
		m_imsInfo.setTimeStamp( time.getStackObject());
	}

	/**
	 * This method is used by application to get Trunk-Group-ID AVP
	 *
	 * @return <code>TrunkGroupId</code> object containing Trunk-Group-ID AVP
	 */
	public TrunkGroupId getTrunkGroupId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getTrunkGroupId() called.");	
		}
		return trungkGrpID ;
	}

	/**
	 * This method is used by application to set Trunk-Group-ID AVP
	 *
	 * @param trunkId - Trunk-Group-ID AVP to be set
	 */
	public void setTrunkGroupId( TrunkGroupId trunkId)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setTrunkGroupId() called.");	
		}
		trungkGrpID = trunkId ;
		m_imsInfo.setTrunkGroupId(trunkId.getStackObject());
	}

	/**
	 * This method is used by application to get UserSessionId AVP
	 *
	 * @return String object containing UserSessionId AVP
	 */	
	public String getUserSessId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getUserSessId() called.");	
		}
		return m_imsInfo.getUserSessId() ;
	}

	/**
	 * This method is used by application to set UserSessionId AVP
	 * 
	 * @param sessionId - UserSessionId AVP to be set
	 */
	public void setUserSessId( String sessionId )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setUserSessId() called.");	
		}
		m_imsInfo.setUserSessId(sessionId);
	}

	/**
	 * This method is used by application to Initializes AppServerInfo array
	 *
	 * @param counter - size by which array is to be initialized
	 */
	public int initAppServerInfo( byte counter )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initAppServerInfo() called.");	
		}
		appServInfo = new AppServerInfo[counter];
		return m_imsInfo.initAppServerInfo(counter );
	}

	/**
	 * This method is used by application to Initializes Sdp-Media-Component array
	 *
	 * @param counter - size by which array is to be initialized
	 */
	public int initSdpMediaComponent( byte counter )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initSdpMediaComponent() called.");	
		}
		sdpMediaComp =  new SdpMediaCmpnt[counter] ;
		return m_imsInfo.initSdpMediaComponent(counter );
	}

	/**
	 * This method is used by application to Initializes Sdp-Session-Description array
	 *
	 * @param counter - size by which array is to be initialized
	 */ 
	public int initSdpSessionDescription(byte counter )
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initSdpSessionDescription() called.");	
		}
		return m_imsInfo.initSdpSessionDescription(counter);
	}

	public com.condor.chargingcommon.ImsInfo getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");	
		}
		return m_imsInfo;
	}
}
