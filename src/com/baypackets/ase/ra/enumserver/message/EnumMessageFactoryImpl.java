package com.baypackets.ase.ra.enumserver.message;

import org.apache.log4j.Logger;
import org.xbill.DNS.RRset;

import com.baypackets.ase.ra.enumserver.EnumResourceAdaptor;
import com.baypackets.ase.ra.enumserver.EnumResourceAdaptorImpl;
import com.baypackets.ase.ra.enumserver.session.EnumResourceSession;
import com.baypackets.ase.ra.enumserver.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

public class EnumMessageFactoryImpl implements EnumMessageFactory,Constants{
	
	/** The msg factory. */
	private MessageFactory msgFactory;
	
	/** The Enum resource adaptor. */
	private EnumResourceAdaptor enumResourceAdaptor;
	
	/** The Enum message factory. */
	private static EnumMessageFactoryImpl enumMessageFactory ;
	
	/** logger **/
	private static Logger logger = Logger.getLogger(EnumMessageFactoryImpl.class);
    

	
	public EnumMessageFactoryImpl(){
		enumMessageFactory = this;
		if(logger.isDebugEnabled())
			logger.debug("in EnumMessageFactoryImpl cons.");
	}
	@Override
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside init(ResourceContext) No impl required");

		try {
			this.enumResourceAdaptor=EnumResourceAdaptorImpl.getInstance();
		} catch (Exception e) {
			throw new ResourceException(e);
		}
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}

	}
	

	@Override
	public SasMessage createResponse(SasProtocolSession session,int send,EnumRequest request, RRset[] records)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createResponse(session,type,request,records)");
		boolean isDebugEnabled=logger.isDebugEnabled();
		
		EnumResponseImpl message = null;
		switch (send) {
		case SEND:
			if(isDebugEnabled)
				logger.debug("Creating SEND");
			message=new EnumResponseImpl(request);
			message.setProtocolSession(session);
			message.setEnumResourceAdaptor(this.enumResourceAdaptor);
			message.setDNSRecords(records);
			((EnumResourceSession)session).setRequest((EnumRequestImpl)request);
			message.setType(SEND);
			break;
		default:
			if(isDebugEnabled)
				logger.debug("Wrong/Unkown request type.");
			throw new ResourceException("Wrong/Unkown request type.");
		}
		if(isDebugEnabled)
			logger.debug("leaving createResponse():");
		return message;
	}



	public static EnumMessageFactoryImpl getInstance() {
		if(enumMessageFactory == null){
			enumMessageFactory = new EnumMessageFactoryImpl();
				logger.error("EnumMessageFactoryImpl object is null.");
		}
		return enumMessageFactory;
	}
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type, byte[] message)
			throws ResourceException {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled())
			logger.debug("Inside createRequest(session,type,message)");
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createRequest(session,type,message)");
		EnumRequestImpl request = null;
		switch (type) {
		case RECEIVE:
			if(isDebugEnabled)
				logger.debug("Creating RECEIVE");
			request=new EnumRequestImpl();
			request.setData(message);
			request.setProtocolSession(session);
			request.setEnumResourceAdaptor(this.enumResourceAdaptor);
			((EnumResourceSession)session).setRequest(request);
			request.setType(RECEIVE);
			break;
		default:
			if(isDebugEnabled)
				logger.debug("Wrong/Unkown request type.");
			throw new ResourceException("Wrong/Unkown request type.");
		}
		if(isDebugEnabled)
			logger.debug("leaving createRequest():");
		return request;
	}
	@Override
	public SasMessage createResponse(SasMessage request, int type)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm, String msisdn) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
