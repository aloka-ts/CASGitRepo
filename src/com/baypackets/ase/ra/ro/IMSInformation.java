/**
 * Filename:	IMSInformation.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/**
 * This class defines the Event-Type AVP that is part of an credit control request.
 *
 * Application can use it's methods to fill various fields of IMS-Information AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface IMSInformation {

	/**
	 * This method is used by application to get Event-Type AVP
	 *
	 * @return <code>EventType</code> object containing Event-Type AVP
	 */

	public EventType getEventType();

    /** This method is used by application to set Event-Type AVP
     *
     * @param et - Event-Type AVP to be set
     */
	
	public void setEventType(EventType et);

    /**
     * This method is used by application to get the RoleOfNode AVP
     *
     * @return The RoleOfNode AVP
     */

	public short getRoleOfNode();

    /**
     * This method is used by application to set the RoleOfNode AVP
     *
     * @param role - The RoleOfNode AVP to be set
     */

	public void setRoleOfNode(short role);

    /**
     * This method is used by application to get the Node-Functionality AVP
     *
     * @return Node-Functionality AVP
     */

	public short getNodeFunctionality();

    /**
     * This method is used by application to set Node-Functionality AVP
     *
     * @param func - Node-Functionality AVP to be set
     */

	public void setNodeFunctionality(short func);

    /**
     * This method is used by application to get UserSessionId AVP
     *
     * @return String object containing UserSessionId AVP
     */

	public String getUserSessionId();

    /**
     * This method is used by application to set UserSessionId AVP
     *
     * @param id - UserSessionId AVP to be set
     */

	public void setUserSessionId(String id);

    /**
     * This method is used by application to get CallingPartyAddress
     *
     * @return String object containing CallingPartyAddress
     */

	public String getCallingPartyAddress();

    /**
     * This method is used by application to set Calling-Party-Address AVP.
     *
     * @param addr - CallingPartyAddress to be set
     */

	public void setCallingPartyAddress(String addr);

    /**
     * This method is used by application to get Called-Party-Address AVP.
     *
     * @return String object containing Called-Party-Address
     */

	public String getCalledPartyAddress();

    /**
     * This method is used by application to set Called-Party-Address AVP.
     *
     * @param addr - Called-Party-Address to be set by application
     */

	public void setCalledPartyAddress(String addr);

    /**
     * This method is used by application to get Time-Stamp AVP
     *
     * @return <code>TimeStamp</code> object containing Time-Stamp AVP
     */

	public TimeStamps getTimeStamps();

    /**
     * This method is used by application to set Time-Stamp AVP
     *
     * @param ts - Time-Stamp AVP to be set
     */

	public void setTimeStamps(TimeStamps ts);

    /**
     * This method is used by application to get Inter-Operator-Identifier AVP
     *
     * @return <code>InterOperatorIdentifier</code> object containing Inter-Operator-Identifier
     * AVP to be set
     */

	public InterOperatorIdentifier getInterOperatorIdentifier();

    /**
     * This method is used by application to set Inter-Operator-Identifier AVP
     *
     * @param ioi - Inter-Operator-Identifier AVP to be set
     */

	public void setInterOperatorIdentifier(InterOperatorIdentifier ioi);

    /**
     * This method is used by application to get IMS-Charging-Identifier AVP
     *
     * @return String object containing IMS-Charging-Identifier AVP
     */

	public String getIMSChargingIdentifier();

    /**
     * This method is used by application to set IMS-Charging-Identifier AVP
     *
     * @param id - IMS-Charging-Identifier AVP to be set
     */

	public void setIMSChargingIdentifier(String id);

    /**
     * This method is used by application to get GGSN-Address AVP
     *
     * @return <code>Address</code> object containing GGSN-Address AVP
     */

	public Address getGGSNAddress();

    /**
     * This method is used by application to set GGSN-Address AVP
     *
     * @param addr - GGSN-Address AVP to be set
     */

	public void setGGSNAddress(Address addr);

    /**
     * This method is used by application to get Served-Party-IP-Address AVP
     *
     * @return <code>Address</code> object containing Served-Party-IP-Address AVP
     */

	public Address getServedPartyIPAddress();

    /**
     * This method is used by application to set Served-Party-IP-Address AVP
     *
     * @param addr - Served-Party-IP-Address AVP to be set
     */

	public void setServedPartyIPAddress(Address addr);

    /**
     * This method is used by application to get Server-Capability AVP
     *
     * @return <code>ServerCapabilities<code> object containing Server-Capability AVP
     */

	public ServerCapabilities getServerCapabilities();

    /**
     * This method is used by application to set Server-Capability AVP
     *
     * @param servCaps - Server-Capability AVP to be set
     */

	public void setServerCapabilities(ServerCapabilities servCaps);

    /**
     * This method is used by application to get Trunk-Group-ID AVP
     *
     * @return <code>TrunkGroupId</code> object containing Trunk-Group-ID AVP
     */

	public TrunkGroupId getTrunkGroupId();

    /**
     * This method is used by application to set Trunk-Group-ID AVP
     *
     * @param tgId - Trunk-Group-ID AVP to be set
     */

	public void setTrunkGroupId(TrunkGroupId tgId);

    /**
     * This method is used by application to get BearerService
     *
     * @return byte array object containing BearerService
     */

	public byte[] getBearerService();

    /** 
	 * This method is used by application to set BearerService
     *
     * @param bearerService - BearerService to be set by application
     */

	public void setBearerService(byte[] bearerService);

    /**
     * This method is used by application to get Service-Id AVP
     *
     * @return String object containing Service-Id AVP
     */

	public String getServiceId();

    /**
     * This method is used by application to set Service-Id AVP
     *
     * @param id - Service-Id AVP to be set
     */

	public void setServiceId(String id);

    /**
     * This method is used by application to get Service-Specific-Data AVP
     *
     * @return String object containing Service-Specific-Data AVP
     */

	public String getServiceSpecificData();

    /**
     * This method is used by application to set Service-Specific-Data AVP
     *
     * @param ssData - Service-Specific-Data AVP to be set
     */

	public void setServiceSpecificData(String ssData);

    /**
     * This method is used by application to get MessageBody AVP
     *
     * @return <code>MessageBody</code> object containing MessageBody AVP
     */

	public MessageBody getMessageBody();

    /**
     * This method is used by application to set MessageBody AVP
     *
     * @param msgBody - MessageBody AVP to be set
     */

	public void setMessageBody(MessageBody msgBody);

    /**
     * This method is used by application to get CauseCode AVP
     *
     * return CauseCode AVP
     */

	public int getCauseCode();

    /**
     * This method is used by application to set CauseCode AVP
     *
     * @param causeCode - CauseCode AVP to be set
     */

	public void setCauseCode(int causeCode);

    /**
     * This method is used by application to get all ApplicationServiceInfo
     * AVP associated with a Credit control message.
     *
     * @return Iterator object containing ApplicationServiceInfo AVP
     */

	public Iterator getApplicationServerInformations();

    /**
     * This method is used by application to set ApplicationServiceInfo in a 
	 * credit control message.
     *
     * @param asInfo - ApplicationServiceInfo AVP to be added.
     */

	public void addApplicationServerInformation(ApplicationServerInformation asInfo);

	/** 
	 * This method removes the application server info from a credit control message..
	 *
 	 * @return boolean - true if removed successfull else false
	 */

	public boolean removeApplicationServerInformation(ApplicationServerInformation asInfo);

    /**
     * This method is used by application to get SDP-Session-Description AVP associated 
	 * with a credit control request.
     *
     * @return Iterator containing all SDP-Session-Description AVP in CC request.
     */

	public Iterator getSDPSessionDescriptions();
	 
	/**
	 * This method is used to add SDP-Session-Description AVP in a credit control request.
	 *
	 */ 
	public void addSDPSessionDescription(String sdpDesc);

	/**
	 * This method is used by application to remove a SDP-Session-Description AVp from a
	 * credit control request.
	 *
	 */	
	public boolean removeSDPSessionDescription(String sdpDesc);

    /**
     * This method is used by application to get SDP-Media-Component AVP associated
	 * with a credit control request.
     *
     * @return Iterator containing all SDP-Media-Component AVP in CCR.
     */

	public Iterator getSDPMediaComponents();

	/**
     * This method is used to add SDP-Media-Component AVP in a credit control request.
     *
     */

	public void addSDPMediaComponent(SDPMediaComponent medComp);

	/**
     * This method is used by application to remove a SDP-Media-ComponentSDP-Media-Component AVP from a
     * credit control request.
     *  
     */
	public boolean removeSDPMediaComponent(SDPMediaComponent medComp);
}

