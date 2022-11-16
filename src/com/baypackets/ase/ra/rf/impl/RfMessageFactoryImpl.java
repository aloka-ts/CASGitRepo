package com.baypackets.ase.ra.rf.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.*;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

import com.condor.rf.rfMessages.rfAccntResponse ;

public class RfMessageFactoryImpl implements RfMessageFactory, Constants 
{
	private static Logger logger = Logger.getLogger(RfMessageFactoryImpl.class);
	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException 
	{
		this.context = context;
	}

	public SasMessage createRequest(SasProtocolSession session, int type) throws ResourceException
	{
		RfAbstractRequest message = null;
		switch(type)
		{
			case EVENT:
				message = new RfAccountingRequest((RfSession)session , EVENT);
				break;

			case SESSION:
				message = new RfAccountingRequest((RfSession)session , SESSION);
				break;

			default:
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
		}
		message.setProtocolSession(session);
		//message.setResourceContext(this.context);
		//logger.debug("createRequest(): ResourceContext is set to " + this.context);
		if(logger.isDebugEnabled())
			logger.debug("leaving RfMessageFactoryImpl createRequest():");
		return message;
	}

	public SasMessage createResponse(SasMessage request) throws RfResourceException
	{
		return null;
	}


	public SasMessage createResponse(SasMessage request, int type) throws ResourceException
	{
		RfRequest rfRequest = (RfRequest)request;
		RfAbstractResponse message = null;
		switch (type) 
		{
			case EVENT:
				message = new RfAccountingResponse(rfRequest);
				break;
			case SESSION:
				message = new RfAccountingResponse(rfRequest);
				break;
			default:
				logger.error("Wrong/Unkown response type.");
				throw new ResourceException("Wrong/Unkown response type.");
		}
		message.setProtocolSession(request.getProtocolSession());
		//message.setResourceContext(this.context);
		((RfAccountingResponse)message).setAccntRecordType(rfRequest.getAccntRecordType());
		//logger.debug("createResponse(): ResourceContext is set to " + this.context);
		if(logger.isDebugEnabled())
			logger.debug("leaving RfMessageFactoryImpl createResponse():");
		return message;
	}

	public SasMessage createResponse(SasMessage request, int type, rfAccntResponse response ) throws RfResourceException
	{
		RfRequest rfRequest = (RfRequest)request;
		RfAbstractResponse message = null;
		switch (type)
		{
			case EVENT:
				message = new RfAccountingResponse(rfRequest, response);
				break;
			case SESSION:
				message = new RfAccountingResponse(rfRequest , response);
				break;
			default:
				logger.error("Wrong/Unkown response type.");
				throw new RfResourceException("Wrong/Unkown response type.");
		}
		if(logger.isDebugEnabled())
			logger.debug("request created successfully");
		message.setProtocolSession(request.getProtocolSession());
		//message.setResourceContext(this.context);
		((RfAccountingResponse)message).setAccntRecordType(rfRequest.getAccntRecordType());
		//logger.debug("createResponse(): ResourceContext is set to " + this.context);
		if(logger.isDebugEnabled())
			logger.debug("leaving RfMessageFactoryImpl createResponse.");
		return message;
	}
}
