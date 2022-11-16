package com.baypackets.ase.ra.http;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.http.event.HttpResourceEvent;
import com.baypackets.ase.ra.http.message.HttpMessage;
import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.message.HttpRequestImpl;
import com.baypackets.ase.ra.http.message.HttpResponse;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.resource.ResourceContext;
import org.apache.log4j.Logger;
import com.baypackets.ase.ra.http.exception.HttpResourceException;
import com.baypackets.ase.ra.http.qm.HttpQueueManagerImpl;
import com.baypackets.ase.ra.http.session.HttpResourceSession;
import com.baypackets.ase.ra.http.web.WebManager;




public class HttpResourceAdaptorImpl implements HttpResourceAdaptor {

	private static HttpResourceAdaptor httpResourceAdaptor;
	
	/** Logger **/
	private static Logger logger = Logger.getLogger(HttpResourceAdaptorImpl.class);
	
	/** The status of RA, IS RA up or down. */
	private boolean raUp = false;
	
	/** Flag to mark true when ra is active on machine */
	private boolean raActive=false;
	
	/** The app list. MAintains list of deployed apps using the RA*/
	private ArrayList<DeployableObject> appList = new ArrayList<DeployableObject>();

	/** The can send message. */
	private boolean canSendMessage = false;
	
	/** The Resourcecontext. */
	private ResourceContext context;
	
	private WebManager webManager;
	
	/** The role for RA/SAS active standby. */
	private short role = HttpResourceAdaptor.ROLE_ACTIVE;
	
	private HttpQueueManagerImpl queueManager;
	
	public HttpResourceAdaptorImpl(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("creating HttpAdaptor object.");
		httpResourceAdaptor= this;
		//ravi
		if(isDebugEnabled)
			logger.debug("(cons)httpresourceAdaptor:"+httpResourceAdaptor);
	}
	@Override
	public void deliverEvent(HttpResourceEvent event) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside deliverEvent(): HttpResourceEvent::"+event.getType()); 

		boolean deliverUpward=true;

		if(event.getType().equals(HttpResourceEvent.REQUEST_FAIL_EVENT)) {
			((HttpResourceSession)(event.getMessage()).getSession()).setSessionState(HttpResourceSession.HTTP_INACTIVE);
			((HttpRequestImpl)event.getMessage()).setStatus(HttpRequest.REQUEST_INACTIVE);
			//replicate((HttpRequest)event.getMessage());
		} else if(event.getType().equals(HttpResourceEvent.QUEUE_FULL)){
			((HttpResourceSession)(event.getMessage()).getSession()).setSessionState(HttpResourceSession.HTTP_INACTIVE);
			((HttpRequestImpl)event.getMessage()).setStatus(HttpRequest.REQUEST_INACTIVE);
			//replicate((LsRequest)event.getMessage());
		} 
		else if(event.getType().equals(HttpResourceEvent.RA_DOWN)){
			this.canSendMessage= false;
		}else if (event.getType().equals(HttpResourceEvent.RA_UP)){
			this.canSendMessage = true;
		}

		if(deliverUpward == true){
			if (context != null) {
				if(isDebugEnabled)
					logger.debug("deliverEvent(): call context.");
				context.deliverEvent(event, true);
			} else {
				if(isDebugEnabled)
					logger.debug("deliverEvent(): Context is null Failed");
			}
		}
		if(isDebugEnabled)
			logger.debug("Leaving deliverEvent() ");

	}

	@Override
	public void deliverResponse(HttpResponse httpResponse)
			throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside deliverResponse(): response "+httpResponse); 
		//setting Session state to Inactive
		((HttpResourceSession)httpResponse.getSession()).setSessionState(HttpResourceSession.HTTP_INACTIVE);
		((HttpRequestImpl)httpResponse.getRequest()).setStatus(HttpRequest.REQUEST_INACTIVE);
		if (context != null) {
			if(isDebugEnabled){
				logger.debug("deliverResponse(): call context.");
			}
			context.deliverMessage(httpResponse, true);
		} else {
			logger.error("Unable to deliver Response, Resource Context is null");
		}
		if(isDebugEnabled)
			logger.debug("Leaving deliverResponse() ");
	}

	@Override
	public ResourceContext getResourceContext() {

		return context;
	
	}

	@Override
	public void configurationChanged(String arg0, Object arg1)
			throws ResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(ResourceContext context) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside HttpResourceAdaptor init() with context "+context);
		this.context = context;		
		if(context==null){
			logger.error("InitialIzation Failed-RA created with null resource context");
			throw new HttpResourceException("InitialIzation Failed-->Resource Context is null");
		}
		if(isDebugEnabled)
			logger.debug("init  class loader is "+this.getClass().getClassLoader());
		//Initializes the resource factory	
		((HttpResourceFactoryImpl)context.getResourceFactory()).init(context);
		// Gets the role for current SAS standby/active
		this.role = context.getCurrentRole();
		if(isDebugEnabled)
			logger.debug("The system is " + (this.role == HttpResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));
		
		queueManager =HttpQueueManagerImpl.getInstance();
		webManager = WebManager.getInstance();
		if(isInfoEnabled)
			logger.info("Leaving HttpResourceAdaptor init()");
	}


	@Override
	public void roleChanged(String clusterId, String subsystemId, short role) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside HttpResourceAdaptor roleChanged() to "+role);

		short preRole = this.role;
		this.role = role;
		if(logger.isDebugEnabled())
			logger.debug("is RA active::["+raActive+"]");
		if (preRole != ROLE_ACTIVE && role == ROLE_ACTIVE && raActive) {
			try {
				this.start();
			} catch (Exception e) {
				logger.error("Exception in roleChanged(): " , e);
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving HttpResourceAdaptor roleChanged()");
	}

	@Override
	public void sendMessage(SasMessage message) throws IOException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside SendMessage");
		try {
			if (message instanceof HttpRequest) {
				if(this.canSendMessage) {
					//replicate((LsRequest)message);					
					queueManager.enQueueRequest((HttpRequest)message);
				} else {
					if(isDebugEnabled)
						logger.debug("Peer is disconnected. cannot send request");
					HttpResourceEvent resourceEvent = new HttpResourceEvent(message, 
							HttpResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					resourceEvent.setMessage((HttpMessage) message);
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering HttpResourceEvent",e);
						throw new HttpResourceException(e);
					}
				}
			} else{
				logger.error("Message dropped: not a HttpRequest.");
			}
		}catch(Exception e){
			logger.error("sendMessage() failed ", e);	
		}

		if(isDebugEnabled)
			logger.debug("Leaving SendMessage");

	}

	@Override
	public void start() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside HttpResourceAdaptor start()");
		raActive=true;
		if (this.role != HttpResourceAdaptor.ROLE_ACTIVE) {
			if(isInfoEnabled)
				logger.info("Standby...");
			return;
		}
		try{
			
			queueManager.load();
			if(isDebugEnabled)
				logger.debug("Queue Manager initialized, Queue Created");
			webManager.start(this);
			this.raUp=true;
			this.canSendMessage=true;
			//send RA up event to app
			if(isDebugEnabled)
				logger.debug("sending RA up event");
			HttpResourceEvent resourceEvent = new HttpResourceEvent("RA_UP_EVENT", 
					HttpResourceEvent.RA_UP, null);
			try {
				deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering HttpResourceEvent",e);
				throw new HttpResourceException(e);
			}
			if(isDebugEnabled)
				logger.debug("RA UP event sent");
			//Notifying waiting threads...
			synchronized (this) {
				notifyAll();				
			}
		}catch(Exception e){
			logger.error("Exception while starting RA", e);
			throw(new HttpResourceException(e));
		}

		if(isInfoEnabled)
			logger.info("Leaving HttpResourceAdaptor start():: RA succesfully started");
	}

	@Override
	public void stop() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside HttpResourceAdaptor stop()");
		raActive=false;
		try {
			if(this.raUp == true ) {
				if(isDebugEnabled)
					logger.debug(" stoping RA " );
				//send RA- down event to app
				if(isDebugEnabled)
					logger.debug("sending RA down event");
				HttpResourceEvent resourceEvent = new HttpResourceEvent("RA_DOWN_EVENT", 
						HttpResourceEvent.RA_DOWN, null);
				try {
					deliverEvent(resourceEvent);
				} catch (ResourceException e) {
					logger.error("Exception in delivering HttpResourceEvent",e);
					throw new HttpResourceException(e);
				}
				if(isDebugEnabled)
					logger.debug("RA down event sent");
				this.canSendMessage=false;
				this.raUp=false;
				
				queueManager.destroy();
				webManager.stop();
			}
		} catch (Exception ex) {
			logger.error("Exception while stopping RA", ex);
			throw new HttpResourceException(ex);
		}
		if(isInfoEnabled)
			logger.info("Leaving HttpResourceAdaptor stop():: RA successfully stopped");
	}

	@Override
	public void registerApp(DeployableObject ctx) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context= "+ctx);
		}
		this.appList.add(ctx);
		
	}
	
	@Override
	public void unregisterApp(DeployableObject ctx) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside unregisterApp() with context "+ctx);
		}
		this.appList.remove(ctx);
	}

	/*
	 * 
	 */
	public Iterator<DeployableObject> getAllRegisteredApps(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAllRegisteredApps()");
		}
		return this.appList.iterator();
	}
	@Override
	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processed(SasMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	public static HttpResourceAdaptor getInstance() throws HttpResourceException {
		if(httpResourceAdaptor==null){
			logger.error("ERROR::::RA Object is null");
			throw new HttpResourceException("ResourceAdaptorImpl Instance is null.");
		}
		return httpResourceAdaptor;
	}

}
