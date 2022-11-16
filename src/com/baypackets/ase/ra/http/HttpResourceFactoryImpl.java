package com.baypackets.ase.ra.http;

import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.message.HttpRequestImpl;
import com.baypackets.ase.ra.http.message.HttpMessageFactory;
import com.baypackets.ase.ra.http.message.HttpMessageFactoryImpl;
//import com.baypackets.ase.ra.http.message.HttpRequestImpl;
import com.baypackets.ase.ra.http.session.HttpResourceSessionFactory;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.ra.http.utils.Constants;

public class HttpResourceFactoryImpl implements HttpResourceFactory,Constants{

	public static Logger logger = Logger.getLogger(HttpResourceFactoryImpl.class);
	/** The msg factory. */
	private HttpMessageFactory msgFactory;

	/** The session factory. */
	private SessionFactory sessionFactory;

	private ResourceContext context;
	
	public void init(ResourceContext context) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside HttpResourceFactoryImpl init().");
		this.context=context;
		if (context != null) {
			logger.error("HttpResourceFactory init(): context received");
			if(isDebugEnabled)
				logger.debug("init(): get session factory");
			this.sessionFactory = (SessionFactory)this.context.getSessionFactory();
			if (this.sessionFactory== null)
				logger.warn("HttpResourceFactory init(): null SessionFactory from context");
				this.sessionFactory = HttpResourceSessionFactory.getInstance();
			if(isDebugEnabled)	
				logger.debug("init(): get message factory");
//			this.msgFactory = (HttpMessageFactory)this.context.getMessageFactory();
			if (this.msgFactory == null)
				logger.warn("HttpResourceFactory init(): null MessageFactory from context");
				this.msgFactory = HttpMessageFactoryImpl.getInstance();
		}else {
			logger.error("HttpResourceFactory init(): null context received");
			if(isDebugEnabled)
				logger.debug("init(): get message factory");
			this.msgFactory = HttpMessageFactoryImpl.getInstance();
			if(isDebugEnabled)
				logger.debug("init(): get session factory");
			this.sessionFactory = HttpResourceSessionFactory.getInstance();
		}
	}
	@Override
	public HttpRequest createRequest(SipApplicationSession appSession,
			String url, String httpMethod) throws ResourceException {

		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering HttpResourceFactoryImpl createRequest(appSession)");
		SasProtocolSession session = null;
		HttpRequestImpl request=null;
		if (appSession != null){
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
			if(isDebugEnabled)
				logger.debug("Creating Request");
			request = (HttpRequestImpl) ((HttpMessageFactoryImpl)msgFactory).createRequest(session, EXECUTE);
			request.setURL(url);
			request.setHttpMethod(httpMethod);
			((SasMessage)request).setInitial(true);
		}else{
			throw new ResourceException("AppSession cannot be null");
		}
		return request;
	
	}

	@Override
	public Request createRequest(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceSession createSession(SipApplicationSession arg0)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
