/*
* RfRequest.java
*
* Created on Sep 15,2006
*/

package com.baypackets.ase.ra.rf;

import javax.servlet.sip.SipURI;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.ra.rf.RfResourceException;

/**
 * <code>RfRequest</code> interface represents an Rf request to
 * applications. It specifies operations which can be performed
 * by applications on an Rf request.
 *
 * @author Prashant Kumar 
 */

public interface RfRequest extends Request
{
	/**
	 * This method returns the role of the node
	 *
	 * @return The role of the node.
	 */
	public int getRoleOfNode();
	/**
	 * This method sets the role of the node
	 * @param role - Role to be assigned to this node.
	 */
	public void setRoleOfNode(int role);
	/**
	* This method returns the id of this request.
	*
	* @return The id of this request.
	*/	
	public int getId();
	/**
	 * This method sets the id of this request.
	 *
	 * @param id - id to be assigned to this request.
	 */
	public void setId(int id) ;
	/**
	 * This method returns the type of the request.
	 * 
	 * @return <code>int</code> - The type of the request.
	 * 					1 Event Type Charging request
	 * 					2 Session Type Charging request
	 */
	public int getRequestType();

	//public Response createResponse(int type) throws RfResourceException;
	/**
	 * This method creates a response for this request.
	 * @return RfResponse for this request.
	 * @throws RfResourceException if an exsception occurs in the creation of 
	 * RfResponse.
	 */	
	public RfResponse createResponse() throws RfResourceException;
        
	// accounting reqeust message specific methods
	/**
	 * This method returns the session id for this request.
	 * @return The session id for this request.
	 * @throws RfResourceException if an exception occurs.
	 */
	public String getSessionId() throws RfResourceException ;
	/**
	 * This method sets the session id for this request.
	 * @param id - he session id for this request.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setSessionId(String id) throws RfResourceException ;
	/**
	 * This method returns the origin host.
	 * @return the origin host or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public String getOriginHost() throws RfResourceException ;
	/**
	 * This method sets the origin host.
	 * @param originHost the origin host to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setOriginHost(String originHost) throws RfResourceException ;
	/**
	 * This method returns the origin realm.
	 * @return the origin realm or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public String getOriginRealm() throws RfResourceException ;
	/**
	 * This method sets the origin realm.
	 * @param originRealm - the origin realm to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setOriginRealm(String originRealm) throws RfResourceException ;
	/**
	 * This method returns the Destination host.
	 * @return The Destination host or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public String getDestHost() throws RfResourceException ;
	/**
	 * This method sets the Destination host.
	 * @param destHost - The Destination host to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setDestHost(String destHost) throws RfResourceException ;
	/**
	 * This method returns the Destination realm.
	 * @return The Destination realm or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */ 
	public String getDestRealm() throws RfResourceException ;
	/** 
	 * This method sets the Destination realm.
	 * @param destRealm - The Destination realm to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setDestRealm(String destRealm) throws RfResourceException ;
	/**
	 * This method returns the accounting record type of this reqeust.
	 * @return The accounting record type of this reqeust.
	 * @throws RfResourceException if an exception occurs.
	 */
	public int getAccntRecordType() throws RfResourceException ;
	/**
	 * This method sets the accounting record type of this reqeust.
	 * @param accntRecordType - The accounting record type of this reqeust to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setAccntRecordType(int accntRecordType) throws RfResourceException ;
	/**
	 * This method returns the accounting record number of this reqeust.
	 * @return The accounting record number of this reqeust or "-1" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public int getAccntRecordNumber() throws RfResourceException ;
	/**
	 * This method sets the accounting record number of this reqeust.
	 * @param acntRecordNumber - The accounting record number of this reqeust to be set.
	 * @throws RfResourceException if an exception occurs.
	 */	
	public void setAccntRecordNumber( int acntRecordNumber) throws RfResourceException ;
	/**
	 * This method returns the accounting application id for this request.
	 * @return The accounting application id for this request or "-1" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public int getAccntApplicationId() throws RfResourceException ;
	/**
	 * This method sets the accounting application id for this request.
	 * @param accntAppId - The accounting application id for this request to be set.
	 * @throws RfResourceException if an exception occurs.
	 */ 
	public void setAccntApplicationId(int accntAppId ) throws RfResourceException ;
	/**
	 * This method returns the accounting interim interval for this request.
	 * @return The accounting interim interval for this request or "-1" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public int getAccntInterimInterval() throws RfResourceException ;
	/**
	 * This method sets the accounting interim interval for this request.
	 * @param accntInterimInterval - The accounting interim interval for this request to be set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setAccntInterimInterval(int accntInterimInterval) throws RfResourceException ;

    //public int getOriginStateId() throws RfResourceException ;

    //public void setOriginStateId(int originStateId) throws RfResourceException ;
	/**
	 * This method returns the Event time Stamp for this request.
	 * @return The Event time Stamp for this request or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public String getEventTimeStamp() throws RfResourceException ;
	/**
	 * This method sets the Event time Stamp for this request.
	 * @param timestamp - The Event time Stamp to be set for this request.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setEventTimeStamp(String timestamp) throws RfResourceException ;

	//public ProxyInfo getProxyInfo() throws RfResourceException ;

	//public void setProxyInfo(ProxyInfo proxyInfo) throws RfResourceException ;
	/**
	 * This method returns the route record for this request.
	 * @return The route record for this request or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */ 
	public String getRouteRecord() throws RfResourceException ;
	/** This method sets the route record for this request.
	 * @param routeRecord - The route record to se set for this request.
	 * @throws RfResourceException if an exception occurs.
	 */ 
	public void setRouteRecord(String routeRecord) throws RfResourceException ;
	/**
	 * This method returns the Service Info Object associated with this request.
	 * @return The Service Info Object associated with this request or "null" if nothing is set.
	 * @throws RfResourceException if an exception occurs.
	 */
	public ServiceInfo getServiceInfo() throws RfResourceException ;
	/**
	 * This method sets the Service Info Object into this request.
	 * @param servInfo - The Service Info Object to be set into this request object.
	 * @throws RfResourceException if an exception occurs.
	 */
	public void setServiceInfo(ServiceInfo servInfo) throws RfResourceException ;
}
