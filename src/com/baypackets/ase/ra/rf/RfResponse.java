package com.baypackets.ase.ra.rf;

import com.baypackets.ase.resource.Response;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;

/**
 * <code>RfResponse</code> interface represents an Rf response to
 * applications. It specifies operations which can be performed
 * by applications on an Rf response.
 *
 * @author Prashant Kumar
 */

public interface RfResponse extends Response 
{

	//public Request getRequest() throws ResourceException ;
	/**
     * This method returns the session id for this response.
     * @return The session id for this response.
     * @throws ResourceException if an exception occurs.
     */
	public String getSessionId() throws ResourceException ;
	/**
     * This method sets the session id for this response.
     * @param sessId - The session id for this response.
     * @throws ResourceException if an exception occurs.
     */
	public void setSessionId( String sessId) throws ResourceException ;
	/**
	 * This method returns the result code associated with this response
	 * @return the result code associated with this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public int getResultCode() throws ResourceException ;
	/**
	 * This method sets the result code associated with this response.
	 * @param code - The result code to be associated with this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setResultCode( int code ) throws ResourceException ;
	/**
	 * This method returns the origin host.
	 * @return the origin host or "null" if nothing is set.
	 * @throws ResourceException if an exception occurs.
	 */
	public String  getOrigHost() throws ResourceException ;
	/**
	 * This method sets the origin host.
	 * @param origHost - the origin host to be set.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setOrigHost( String origHost) throws ResourceException ;
	/**
	 * This method returns the origin realm.
	 * @return the origin realm  or "null" if nothing is set.
	 * @throws ResourceException if an exception occurs.	
	 */
	public String getOrigRealm() throws ResourceException ;
	/**
	 * This method sets the origin realm.
	 * @param origRealm - The origin realm to be set.
	 * @throws ResourceException if an exception occurs. 
	 */	
	public void setOrigRealm( String origRealm) throws ResourceException ;
	/**
	 * This method returns the accounting record type of this response.
	 * @return The accounting record type of this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public int getAccntRecordType() throws ResourceException ;
	/**
	 * This method sets the accounting record type of this response.
	 * @param recordType - The accounting record type of this response to be set.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setAccntRecordType( int recordType) throws ResourceException ;
	/**
	 * This method returns the accounting record number of this response.
	 * @return The accounting record number of this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public int getAccntRecordNum() throws ResourceException ;
	/**
	 * This method sets the accounting record number of this response.
	 * @param recordnumber - The accounting record number to be set in this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setAccntRecordNum( int recordnumber) throws ResourceException ;
	/**
	 * This method returns the accounting application id for this response.
	 * @return The accounting application id for this request or "-1" if nothing is set.
	 * @throws ResourceException if an exception occurs.
	 */
	public int getAccntApplicaionId() throws ResourceException ;
	/**
	 * This method sets the accounting application id for this response.
	 * @param appId - The accounting application id for this response to be set.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setAccntApplicaionId( int appId) throws ResourceException ;
	/**
	 * This method returns the user name associated with this response.
	 * @return The user name associated with this response.
	 * @throws ResourceException if an exception occurs.
	 */	
	public String getUserName() throws ResourceException ;
	/**
	 * This method sets the user name to this response.
	 * @param userUame - The user name to be set nto this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public void setUserName( String userUame) throws ResourceException ;
	/**
	 * This method returns the accounting interim interval for this response.
	 * @return The accounting interim interval for this response.
	 * @throws ResourceException if an exception occurs.
	 */
	public int getAccntInterimInterval() throws ResourceException ;
	/**
	 * This method sets the accounting interim interval for this response.
     * @param interimInterval - The accounting interim interval for this response to be set.
	 * throws ResourceException if an exception occurs.
	*/
	public void setAccntInterimInterval( int interimInterval) throws ResourceException ;

	//public int getOriginStateId() throws ResourceException ;

	//public void setOriginStateId(int originStateId) throws ResourceException ;
	/**
	 * This method returns the Event time Stamp for this response.
	 * @return The Event time Stamp for this response.
	 * throws ResourceException if an exception occurs.
	 */
	public String getEventTimeStamp() throws ResourceException ;
	/**
	 * This method sets the Event time Stamp for this response.
	 * @param eventTImeStamp - The Event time Stamp to be set for this response.
	 * throws ResourceException if an exception occurs.
	 */
	public void setEventTimeStamp( String eventTImeStamp) throws ResourceException ;
	/**
	 * This method return if accounting interim interval is present in this response.
 	 * @return True : if accounting interim interval is present otherwise false.
	 * throws ResourceException if an exception occurs.
	 */
	public boolean getIsAcctInteIntvalPresent() throws ResourceException ;
	/**
	 * This method return if result code is present in this response.
	 * @return True : if result code is present otherwise false.
	 * throws ResourceException if an exception occurs.
	 */	
	public boolean getIsResultCodePresent() throws ResourceException ;
	/**
	 * This method sets if result code is present in this response.
	 * @param isRsltCodePresent - True : is result code is present in this response.
	 * throws ResourceException if an exception occurs.
	 */	
	public void setIsResultCodePresent( boolean isRsltCodePresent) throws ResourceException ;

	//public ProxyInfo getProxyInfo() throws ResourceException ;
	
	//public void setProxyInfo(ProxyInfo proxyInfo) throws ResourceException ;
}
