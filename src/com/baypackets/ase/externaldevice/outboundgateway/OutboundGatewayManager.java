/**
 * OutboundGatewayManager.java
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import com.baypackets.ase.sbb.OutboundGateway;

import java.util.Iterator;

/**
 * The OutboundGatewayManager interface defines the APIs for provisioning the
 * Outbuond Gateways into the SAS.
 * 
 * <p>
 * The findXXX methods returns the outbound gateways that matches the criteria irrespective of the Status. 
 * 
 *<p>
 *	The Outbound Gateway Manager object will be available as an attribute
 *	through the ServletContext
 *  
 */
public interface OutboundGatewayManager {

	/**
	 * Returns the Outbound Gateway that has the same ID as specified.
	 * @param id  specified gateway identifier.
	 * @return the matching Outbound Gateway object or NULL.
	 */
	public OutboundGateway findById(String id);
	
	/**
	 * Returns all the server objects that are provisioned in the SAS.
	 * @return Iterator over all the server objects.
	 */
	public Iterator findAll();
	
	/**
	 *	Adds an Outbound Gateway to the existing repository of servers.
	 *
	 * @param obgw Outbound Gateway object to be added.
	 * @throws IllegalArgumentException if there exist an OutboundGateway with the same ID.  
	 */	
	public void addOutboundGateway(OutboundGateway obgw);
	
	/**
	 * Updates a Outbound Gateway's state to SUSPECT
	 *
	 * @param id  ID of the Outbound Gateway to be updated.
	 */	
	public void outboundGatewaySuspect(String id);
	
	/**
	 * Updates a Outbound Gateway's state to INACTIVE.
	 *
	 * @param id  ID of the Outbound Gateway to be updated.
	 */	
	public void outboundGatewayDown(String id);
	
	/**
	 * Updates a Outbound Gateway's state to ACTIVE.
	 * <p>
	 *
	 * @param id  ID of the Outbound Gateway to be updated.
	 */	
	public void outboundGatewayUp(String id);
	
	/**
	 * Removes the Outbound Gateway with the specified ID.
	 * @param id  ID of the Outbound Gateway to be removed.
	 */
	public void removeOutboundGateway(String id);
	
	/**
	 * Gets the time interval in seconds, 
	 * at which the system checks the health of the Outbound Gateways.
	 * @return heart beat time interval.
	 */
	public int getOutboundGatewayHeartBeatInterval();
	
	/**
	 * Sets the time interval in seconds at which the system
	 * checks the health of the Outbound Gateways.
	 * @param interval Heart Beat Time Interval
	 */
	public void setOutboundGatewayHeartBeatInterval(int interval);
	
	/**
	 * Gets the current retry count. 
	 * @return Current retry count.
	 */
	public int getRetryCount();

	/**
	 * Sets the retry count. 
	 * The container would send the heart beat message this many times
	 * before declaring the outbound gateway as failed. 
	 * @param count Retry Count
	 */
	public void setRetryCount(int count);
	
}
