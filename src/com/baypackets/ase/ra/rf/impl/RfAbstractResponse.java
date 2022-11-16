package com.baypackets.ase.ra.rf.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.*;

import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;

public abstract class RfAbstractResponse extends RfMessage implements RfResponse 
{
	private static Logger logger = Logger.getLogger(RfAbstractResponse.class);
	
	private RfRequest request;
	
	public RfAbstractResponse(RfRequest request) 
	{
		super(request.getRequestType());
		if(logger.isDebugEnabled())
			logger.debug("Inside constructor of RfAbstractResponse()");
		this.request = request;
	}

	public Request getRequest() 
	{
		if(logger.isDebugEnabled())
			logger.debug(" getRequest() called");
		return (Request)this.request;
	}

	public abstract String getSessionId() throws ResourceException ;
	
	public abstract void setSessionId( String sessId) throws ResourceException ;

	public abstract int getResultCode() throws ResourceException ;

	public abstract void setResultCode( int code ) throws ResourceException ;

	public abstract String  getOrigHost() throws ResourceException ;

	public abstract void setOrigHost( String origHost) throws ResourceException ;

	public abstract String getOrigRealm() throws ResourceException ;

	public abstract void setOrigRealm( String origRealm) throws ResourceException ;

	public abstract int getAccntRecordType() throws ResourceException ;

	public abstract void setAccntRecordType( int recordType) throws ResourceException ;

	public abstract int getAccntRecordNum() throws ResourceException ;

	public abstract void setAccntRecordNum( int recordnumber) throws ResourceException ;

	public abstract int getAccntApplicaionId() throws ResourceException ;

	public abstract void setAccntApplicaionId( int appId) throws ResourceException ;

	public abstract String getUserName() throws ResourceException ;

	public abstract void setUserName( String userUame) throws ResourceException ;

	public abstract int getAccntInterimInterval() throws ResourceException ;

	public abstract void setAccntInterimInterval( int interimInterval) throws ResourceException ;
	
	//public abstract int getOriginStateId() throws ResourceException ;

	//public abstract void setOriginStateId(int originStateId) throws ResourceException ;

	public abstract String getEventTimeStamp() throws ResourceException ;

	public abstract void setEventTimeStamp( String eventTImeStamp) throws ResourceException ;
	
	public abstract boolean getIsResultCodePresent() throws ResourceException ;

	public abstract void setIsResultCodePresent( boolean isRsltCodePresent) throws ResourceException ;
	
	//public abstract ProxyInfo getProxyInfo() throws ResourceException ;

	//public abstract void setProxyInfo(ProxyInfo proxyInfo) throws ResourceException ;
	
}
