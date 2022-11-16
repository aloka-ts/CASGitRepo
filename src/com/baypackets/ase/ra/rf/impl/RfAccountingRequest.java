package com.baypackets.ase.ra.rf.impl;

import com.baypackets.ase.ra.rf.*;
import com.baypackets.ase.ra.rf.*;
import com.baypackets.ase.ra.rf.RfResourceException;

import javax.servlet.sip.SipURI;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.spi.container.SasMessage;

import com.condor.rf.rfMessages.rfAccntData ;

public class RfAccountingRequest extends RfAbstractRequest
{
	private static Logger logger = Logger.getLogger(RfAccountingRequest.class);

	private rfAccntData m_rfdata = null ;

	private boolean isServiceInfoPresent = false ;

	private String userName = null;
	private AAACustomAvpInfo customAvpInfo = null ;
	private ServiceInfo serviceInfo = null;

	private int ACCOUNTING_RECORD_TYPE = -1 ;	//EVENT_RECORD = 0 ,
												//START_RECORD = 1 ,
												// INTERIM_RECORD = 2,
												//STOP_RECORD= 3
	private String sessionId = null ;
	private String originHost = null;
	private String originRealm = null;
	private String desthost = null;
	private String destRealm = null ;
	private int accntRecordNumber = -1;
	private int accntAppId = -1;
	private int AccntInterimInterval = -1 ;
	private String timestamp;
	private String routeRecord;

	private int type = 0;

	public RfAccountingRequest(RfSession rfSession , int type)
	{
		super(type);
		if(logger.isDebugEnabled())
			logger.debug("Inside RfAccountingRequest constructer");
		this.type = type;
		m_rfdata = new rfAccntData() ;
		rfSession.addRequest(this);
	}

	public String getSessionId()
	{
		if(logger.isDebugEnabled())
			logger.debug("getSessionId() called");
		if(this.sessionId == null)
		{
			sessionId = ((RfSession)getSession()).getSessionId();
			
		}
		return sessionId ;
	}

	public void setSessionId( String sessId)
	{
		if(logger.isDebugEnabled())
			logger.debug("setSessionId() called");
		this.sessionId = sessId;
		m_rfdata.setAcctSessionId(sessId) ;
	}

	public String getOriginHost()
	{
		if(logger.isDebugEnabled())
			logger.debug("getOriginHost() called");
		return this.originHost;
	}
	public void setOriginHost(String originHost)
	{
		if(logger.isDebugEnabled())
			logger.debug("setOriginHost() called");
		this.originHost = originHost;
	}
	public String getOriginRealm()
	{
		if(logger.isDebugEnabled())
			logger.debug("getOriginRealm() called");
		return this.originRealm ;
	}
	public void setOriginRealm(String originRealm)
	{
		if(logger.isDebugEnabled())
			logger.debug("setOriginRealm() called");
		this.originRealm = originRealm;
	}
	public String getDestHost()
	{
		if(logger.isDebugEnabled())
			logger.debug("getDestHost() called");
		return this.desthost;
	}
	public void setDestHost(String host)
	{
		if(logger.isDebugEnabled())
			logger.debug("setDestHost() called");
		this.desthost = host;
	}
	public String getDestRealm()
	{
		if(logger.isDebugEnabled())
			logger.debug("getDestRealm() called");
		return this.destRealm ;
	}
	public void setDestRealm(String destRealm)
	{
		if(logger.isDebugEnabled())
			logger.debug("setDestRealm() called");
		this.destRealm = destRealm;
	}
	public int getAccntRecordType() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntRecordType() called");
		return this.ACCOUNTING_RECORD_TYPE ;
	}
	public void setAccntRecordType(int accntRecordType)
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntRecordType() called");
		this.ACCOUNTING_RECORD_TYPE = accntRecordType ;
	}
	public int getAccntRecordNumber()
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntRecordNumber() called");
		return this.accntRecordNumber ;
	}
	public void setAccntRecordNumber( int acntRecordNumber)
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntRecordNumber() called");
		this.accntRecordNumber = acntRecordNumber;
	}
	public int getAccntApplicationId()
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntApplicationId() called");
		return this.accntAppId ;
	}
	public void setAccntApplicationId(int accntAppId )
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntApplicationId() called");
		this.accntAppId = accntAppId ;
	}
	public int getAccntInterimInterval()
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntInterimInterval() called");
		this.accntAppId = accntAppId ;
		return this.AccntInterimInterval;
	}
	public void setAccntInterimInterval(int AccntInterimInterval)
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntInterimInterval() called");
		this.AccntInterimInterval = AccntInterimInterval ;
	}
	//public int getOriginStateId() ;

	//public void setOriginStateId(int originStateId) ;

	public String getEventTimeStamp()
	{
		if(logger.isDebugEnabled())
			logger.debug("getEventTimeStamp() called");
		return this.timestamp ;
	}
	public void setEventTimeStamp(String timestamp)
	{
		if(logger.isDebugEnabled())
			logger.debug("setEventTimeStamp() called");
		this.timestamp = timestamp ;
		m_rfdata.setEventTimeStamp( timestamp) ;
	}
	public String getRouteRecord()
	{
		if(logger.isDebugEnabled())
			logger.debug("getRouteRecord() called");
		return this.routeRecord;
	}
	public void setRouteRecord(String routeRecord)
	{
		if(logger.isDebugEnabled())
			logger.debug("setRouteRecord() called");
		this.routeRecord = routeRecord ;
	}
	public ServiceInfo getServiceInfo()
	{
		if(logger.isDebugEnabled())
			logger.debug("getServiceInfo() called");
		return this.serviceInfo ;
	}
	public void setServiceInfo(ServiceInfo serviceInfo)
	{
		if(logger.isDebugEnabled())
			logger.debug("setServiceInfo() called");
		this.serviceInfo = serviceInfo;
		m_rfdata.setServiceInfo( serviceInfo.getStackObject() ) ;
		this.isServiceInfoPresent = true ;
	}
	public boolean getAccntSessionIdPBit()
	{
		if(logger.isDebugEnabled())
			logger.debug("getAccntSessionIdPBit() called");
		return m_rfdata.getAccntSessionIdPBit();
	}

	public void setAccntSessionIdPBit( boolean flag )
	{
		if(logger.isDebugEnabled())
			logger.debug("setAccntSessionIdPBit() called");
		 m_rfdata.setAccntSessionIdPBit( flag ) ;
	}
	public AAACustomAvpInfo getCustomAVPs()
	{
		if(logger.isDebugEnabled())
			logger.debug("getCustomAVPs(() called");
		return customAvpInfo ;
	}

	public void setCustomAVPs( AAACustomAvpInfo avpInfo )
	{
		if(logger.isDebugEnabled())
			logger.debug("setCustomAVPs(() called");
		customAvpInfo = avpInfo ;
		 m_rfdata.setCustomAVPs(avpInfo.getStackObject()) ;
	}
	public boolean getIsServiceInfoPresent()
	{
		if(logger.isDebugEnabled())
			logger.debug("getIsServiceInfoPresent(() called");
		return m_rfdata.getIsServiceInfoPresent() ;
	}

	public int getMultipleOccurenceMaxValue()
	{
		if(logger.isDebugEnabled())
			logger.debug("getMultipleOccurenceMaxValue() called");
		return m_rfdata.getMultipleOccurenceMaxValue() ;
	}
	public String getUserName()
	{
		if(logger.isDebugEnabled())
			logger.debug("getUserName() called");
		return this.userName;
	}

	//Setting the User Name
	public void setUserName(String name) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("setUserName() called");
		this.userName = name;
		m_rfdata.setUsername(name);
	}
	public boolean getUserNamePBit()
	{
		if(logger.isDebugEnabled())
			logger.debug("getUserNamePBit() called");
        return m_rfdata.getUserNamePBit();
	}

	public void setUserNamePBit( boolean flag )
	{
		if(logger.isDebugEnabled())
			logger.debug("setUserNamePBit() called");
		m_rfdata.setUserNamePBit(flag);
	}
	public com.condor.rf.rfMessages.rfAccntData getStackObject()
	{
		if(logger.isDebugEnabled())
			logger.debug("getStackObject() called");
		return m_rfdata ;
	}

	public void isAlreadyReplicated( boolean isReplicated ) {
		this.isReplicated = isReplicated ;
	}

	private boolean isReplicated = false ;
}
