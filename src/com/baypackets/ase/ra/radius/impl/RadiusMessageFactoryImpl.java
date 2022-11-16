package com.baypackets.ase.ra.radius.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.stackif.RadiusAbstractRequest;
import com.baypackets.ase.ra.radius.stackif.RadiusAccessRequestImpl;
import com.baypackets.ase.ra.radius.stackif.RadiusAccountingRequestImpl;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RadiusMessageFactoryImpl implements RadiusMessageFactory {
	
	private static Logger logger = Logger.getLogger(RadiusMessageFactoryImpl.class);
	private ResourceContext context;
	private MessageFactory msgFactory;
	private static RadiusMessageFactoryImpl radiusMessageFactory;
	

	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public RadiusMessageFactoryImpl(){
		if(logger.isDebugEnabled())
			logger.debug("creating RadiusMessageFactoryImpl object");
		radiusMessageFactory=this;
	}
	
	/**
	 *	This  method returns the instance of RadiusMessageFactory.
	 *
	 *	@return RadiusMessageFactory object.
	 */
	public static RadiusMessageFactory getInstance(){
		if(radiusMessageFactory==null){
			radiusMessageFactory = new RadiusMessageFactoryImpl();
		}
		return radiusMessageFactory;
	}
	
	@Override
	public void init(ResourceContext context)
			throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		this.setContext(context);
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}
		if(logger.isDebugEnabled())
			logger.debug("Setting RadiusMessageFactory in  RadiusResourceAdaptorFactory");
		RadiusResourceAdaptorFactory.setMessageFactory(this);
	}
	
	@Override
	public RadiusAccessRequest createRadiusAccessRequest(SasProtocolSession session) throws ResourceException {
		return (RadiusAccessRequest)this.createRequest(session, Constants.ACCESS_REQUEST);
	}
	
	@Override
	public RadiusAccountingRequest createRadiusAccountingRequest(SasProtocolSession session) throws ResourceException {
		return (RadiusAccountingRequest)this.createRequest(session, Constants.ACCOUNTING_REQUEST);
	}
	
	
	@Override
	public SasMessage createRequest(SasProtocolSession session,
			int type) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createRequest(session,type)");
		RadiusAbstractRequest message = null;
		if(type==Constants.ACCESS_REQUEST)
			message = new RadiusAccessRequestImpl();
		else if(type==Constants.ACCOUNTING_REQUEST)
			message=	new RadiusAccountingRequestImpl();
		else
			throw new ResourceException("Invalid Request Type.......");

		message.setProtocolSession(session);
		if(logger.isDebugEnabled())
			logger.debug("leaving createRequest():");
		return message;
	}

	@Override
	public SasMessage createResponse(SasMessage paramSasMessage, int paramInt)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RadiusAccessRequest createRadiusAccessRequest(
			SasProtocolSession session, String username, String userPassword)
			throws ResourceException {
		if(username==null || userPassword==null||username.trim().isEmpty()||userPassword.trim().isEmpty()){
			throw new ResourceException("username or userpassword can not be null or blank");
		}
		RadiusAccessRequest request=new RadiusAccessRequestImpl(username,userPassword);
		((RadiusAbstractRequest)request).setProtocolSession(session);
		return request;
	}

	@Override
	public RadiusAccountingRequest createRadiusAccountingRequest(
			SasProtocolSession session, String userName, int acctStatusType)
			throws ResourceException {
		if(userName==null ||userName.trim().isEmpty()){
			throw new ResourceException("username can not be null or blank");
		}
		RadiusAccountingRequest request=new RadiusAccountingRequestImpl(userName,acctStatusType);
		((RadiusAbstractRequest)request).setProtocolSession(session);
		return request;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(ResourceContext context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public ResourceContext getContext() {
		return context;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
