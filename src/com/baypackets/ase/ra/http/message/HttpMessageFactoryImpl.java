package com.baypackets.ase.ra.http.message;

import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.ra.http.message.HttpMessageFactory;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.http.HttpResourceAdaptor;
import com.baypackets.ase.ra.http.HttpResourceAdaptorImpl;
import com.baypackets.ase.ra.http.message.HttpRequestImpl;
import com.baypackets.ase.ra.http.session.HttpResourceSession;
import com.baypackets.ase.ra.http.utils.Constants;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpMessageFactoryImpl implements HttpMessageFactory,Constants{
	
	/** The msg factory. */
	private MessageFactory msgFactory;
	
	/** The http resource adaptor. */
	private HttpResourceAdaptor httpResourceAdaptor;
	
	/** The http message factory. */
	private static HttpMessageFactoryImpl httpMessageFactory ;
	
	/** logger **/
	private static Logger logger = Logger.getLogger(HttpMessageFactoryImpl.class);
    

	
	public HttpMessageFactoryImpl(){
		httpMessageFactory = this;
		if(logger.isDebugEnabled())
			logger.debug("in HttpMessageFactoryImpl cons.");
	}
	@Override
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside init(ResourceContext) No impl required");

		try {
			this.httpResourceAdaptor=HttpResourceAdaptorImpl.getInstance();
		} catch (Exception e) {
			throw new ResourceException(e);
		}
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}

	}
	
	@Override
	public SasMessage createRequest(SasProtocolSession session, int type)
			throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside createRequest(session,type)");
		HttpRequestImpl message = null;
		switch (type) {
		case EXECUTE:
			if(isDebugEnabled)
				logger.debug("Creating EXECUTE");
			message=new HttpRequestImpl();
			message.setProtocolSession(session);
			message.setHttpResourceAdaptor(this.httpResourceAdaptor);
			((HttpResourceSession)session).setRequest(message);
			message.setType(EXECUTE);
			break;
		default:
			if(isDebugEnabled)
				logger.debug("Wrong/Unkown request type.");
			throw new ResourceException("Wrong/Unkown request type.");
		}
		if(isDebugEnabled)
			logger.debug("leaving createRequest():");
		return message;
	}

	@Override
	public SasMessage createResponse(SasMessage arg0, int arg1)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createResponse(session,type)");
		throw new ResourceException("createResponse() Not Supported, use HttpRequest.createResponse()");
	}



	public static HttpMessageFactoryImpl getInstance() {
		if(httpMessageFactory == null){
			//httpMessageFactory = new HttpMessageFactoryImpl();
				logger.error("HttpMessageFactoryImpl object is null.");
		}
		return httpMessageFactory;
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
