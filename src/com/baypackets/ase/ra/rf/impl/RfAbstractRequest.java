package com.baypackets.ase.ra.rf.impl;

import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.spi.container.SasMessage;

import com.baypackets.ase.ra.rf.*;

import com.condor.rf.rfMessages.rfAccntResponse ;


public abstract class RfAbstractRequest extends RfMessage implements RfRequest
{
	private static Logger logger = Logger.getLogger(RfAbstractRequest.class);
	private static int count = 0;

	private int type = 0;
	private int roleOfNode = -1;
	private int id = -1;

	public RfAbstractRequest(int type)
	{
		super(type);
		if(logger.isDebugEnabled())
			logger.debug("Inside construstor of RfAbstractRequest()");
		this.type = type;
	}

	public int getRoleOfNode()
	{
		if(logger.isDebugEnabled())
			logger.debug("getRoleOfNode() called");
		return this.roleOfNode;
	}

	public void setRoleOfNode(int role)
	{
		if(logger.isDebugEnabled())
			logger.debug("setRoleOfNode() called");
	this.roleOfNode = role;
	}

	public int getRequestType()
	{
		if(logger.isDebugEnabled())
			logger.debug("getRequestType() called");
		return this.type;		//1. Event Type Charging request 2. Session Type Charging request
	}

	public int getId()
	{
		if(logger.isDebugEnabled())
			logger.debug("getId() called");
		if (this.id==-1)
		{
			this.id = generateId();
		}
		return this.id;
	}
	public void setId(int id)
	{
		if(logger.isDebugEnabled())
			logger.debug("setId() called");
		this.id = id;
	}


	private static int generateId()
	{
		if(logger.isDebugEnabled())
			logger.debug("generateId() called");
		if (count >= Integer.MAX_VALUE)
		{
			count = 0;
		}
		return count++;
	}

	public Response createResponse(int type) throws ResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("createResponse(int type) is called.");
		//if (this.getResourceContext() != null)
		if (RfResourceAdaptorImpl.getResourceContext() != null)
		{
			//return (Response)this.getResourceContext().getMessageFactory().createResponse(this, type);
			return (Response)RfResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, type);
		}
		else
		{
			if(logger.isDebugEnabled())
				logger.debug("Use default MessageFactory.");
			return (Response)RfResourceAdaptorFactory.getMessageFactory().createResponse(this, type);
		}
	}

	public RfResponse createResponse() throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("createResponse() is called.");
		try
		{
			//if (this.getResourceContext() != null )
			if (RfResourceAdaptorImpl.getResourceContext() != null )
			{
				//return (RfResponse)this.getResourceContext().getMessageFactory().createResponse(this, this.getType());
				return (RfResponse)RfResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, this.getType());
			}
			else
			{
				if(logger.isDebugEnabled())
					logger.debug("Use default MessageFactory.");
				return (RfResponse)RfResourceAdaptorFactory.getMessageFactory().createResponse(this, this.getType());
			}
		}
		catch (Exception e)
		{
			throw new RfResourceException(e);
		}
	}
	public RfResponse createResponse(rfAccntResponse response , int type) throws RfResourceException
	{
		if(logger.isDebugEnabled())
			logger.debug("createResponse(RfRequest) called");
		try
		{
			/*if (this.getResourceContext() != null )
			{
				return (RfResponse)this.getResourceContext().getMessageFactory().createResponse(this, type , response);
			}
			else */
			{
				if(logger.isDebugEnabled())
					logger.debug("Use default MessageFactory.");
				RfMessageFactory msgFactory = RfResourceAdaptorFactory.getMessageFactory();
				return (RfResponse)msgFactory.createResponse(this, type , response );
			}
		}
		catch (Exception e)
		{
			throw new RfResourceException(e);
		}
	}

	// accounting request message specific methods

	public abstract String getSessionId() throws RfResourceException ;

	public abstract void setSessionId(String id) throws RfResourceException ;

	public abstract String getOriginHost() throws RfResourceException ;

	public abstract void setOriginHost(String originHost) throws RfResourceException ;

	public abstract String getOriginRealm() throws RfResourceException ;

	public abstract void setOriginRealm(String originRealm) throws RfResourceException ;

	public abstract String getDestRealm() throws RfResourceException ;

	public abstract void setDestRealm(String destHost) throws RfResourceException ;

	public abstract int getAccntRecordType() throws RfResourceException ;

	public abstract void setAccntRecordType(int accntRecordType) throws RfResourceException ;

	public abstract int getAccntRecordNumber() throws RfResourceException ;

	public abstract void setAccntRecordNumber( int acntRecordNumber) throws RfResourceException ;

	public abstract int getAccntApplicationId() throws RfResourceException ;

	public abstract void setAccntApplicationId(int accntAppId ) throws RfResourceException ;

	public abstract int getAccntInterimInterval() throws RfResourceException ;

	public abstract void setAccntInterimInterval(int AccntInterimInterval) throws RfResourceException ;

	//public abstract int getOriginStateId() throws RfResourceException ;

	//public abstract void setOriginStateId(int originStateId) throws RfResourceException ;

	public abstract String getEventTimeStamp() throws RfResourceException ;

	public abstract void setEventTimeStamp(String timestamp) throws RfResourceException ;

	//public abstract ProxyInfo getProxyInfo() throws RfResourceException ;

	//public abstract void setProxyInfo(ProxyInfo proxyInfo) throws RfResourceException ;

	public abstract String getRouteRecord() throws RfResourceException ;

	public abstract void setRouteRecord(String routeRecord) throws RfResourceException ;

	public abstract ServiceInfo getServiceInfo() throws RfResourceException ;

	public abstract void setServiceInfo(ServiceInfo servInfo) throws RfResourceException ;

}
