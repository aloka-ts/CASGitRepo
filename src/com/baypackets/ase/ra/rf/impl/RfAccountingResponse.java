package com.baypackets.ase.ra.rf.impl;

import com.baypackets.ase.ra.rf.*;
	
import org.apache.log4j.Logger;

import com.condor.rf.rfMessages.rfAccntResponse;

public class RfAccountingResponse extends RfAbstractResponse 
{
	private static Logger logger = Logger.getLogger("RfAccountingResponse.class");

	private RfRequest request = null;
	
	private rfAccntResponse m_accntResponse = null;
	private String sessionId = null;
	private String origHost = null;
	private String origRealm = null ;
        
	public RfAccountingResponse(RfRequest req)
	{
		super(req);
		if(logger.isDebugEnabled())
			logger.debug("Inside constructor of RfAccountingResponse");
		this.request = req;
	}

	public RfAccountingResponse(RfRequest req , rfAccntResponse response) 
	{
		super(req);
		if(logger.isDebugEnabled())
			logger.debug("inside constructor of RfAccountingResponse.");
		this.request = req;
		m_accntResponse = response;
		if(logger.isDebugEnabled())
			logger.debug("leaving constructor of RfAccountingResponse.");
	}

	public String getSessionId() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getSessionId() called");
		return this.request.getSessionId();
	}
	public void setSessionId( String sessId) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setSessionId() called");
		this.sessionId = sessId;
	}
	public int getResultCode() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getResultCode() called");
		return m_accntResponse.getResultCode();
	}
	public void setResultCode( int code) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setResultCode() called");
		m_accntResponse.setResultCode(code);
	}
	public String  getOrigHost() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getOrigHost() called");
		return this.origHost;
	}
	public void setOrigHost(String origHost) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setOrigHost() called");
		this.origHost = origHost ;
	}
	public String getOrigRealm() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getOrigRealm() called");
		return this.origRealm;
	}
	public void setOrigRealm(String origRealm) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setOrigRealm() called");
		this.origRealm = origRealm;
	}
	public int getAccntRecordNum() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntRecordNum() called");
		return m_accntResponse.getAccntRecordNum();
	}
	public void setAccntRecordNum( int recordNum) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntRecordNum() called");
		m_accntResponse.setAccntRecordNum(recordNum);
	}
	public int getAccntRecordType() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntRecordType() called");
		return m_accntResponse.getAccntRecordType();
	}
	public void setAccntRecordType( int type) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntRecordType() called");
		m_accntResponse.setAccntRecordType(type);
	}
	public int getAccntApplicaionId() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntApplicaionId() called");
		return this.request.getAccntApplicationId();
	}
	public void setAccntApplicaionId(int appId) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntApplicaionId() called");
		this.request.setAccntApplicationId(appId);
	}
	public String getUserName() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getUserName() called");
		return m_accntResponse.getUserName();
	}
	public void setUserName( String userUame) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setUserName() called");
		m_accntResponse.setUserName(userUame);
	}
	public int getAccntInterimInterval() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntInterimInterval() called");
		return m_accntResponse.getAcctInterimInterval();
	}
	public void setAccntInterimInterval( int interval) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntInterimInterval() called");
		m_accntResponse.setAcctInterimInterval(interval);
	}
	public String getEventTimeStamp() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getEventTimeStamp() called");
		return m_accntResponse.geteventTimeStamp();
	}
	public void setEventTimeStamp( String timestmp) throws RfResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("setEventTimeStamp() called");
		m_accntResponse.setEventTimeStamp(timestmp);
	}
	public boolean getIsAcctInteIntvalPresent() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getIsAcctInteIntvalPresent() called");
		return m_accntResponse.getIsAcctInteIntvalPresent();
	}
	public boolean getIsResultCodePresent() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getIsResultCodePresent() called");
		return m_accntResponse.isResultCodePresent();
	}
	public void setIsResultCodePresent( boolean flag) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setIsResultCodePresent() called");
		m_accntResponse.setIsResultCodePresent(flag);
	}
}
