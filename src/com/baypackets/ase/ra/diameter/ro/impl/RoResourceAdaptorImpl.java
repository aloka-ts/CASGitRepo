package com.baypackets.ase.ra.diameter.ro.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.ro.RoMessage;
import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceAdaptor;
import com.baypackets.ase.ra.diameter.ro.RoResourceEvent;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.RoResponse;
import com.baypackets.ase.ra.diameter.ro.RoStackInterface;
import com.baypackets.ase.ra.diameter.ro.rarouter.RoAppRouter;
import com.baypackets.ase.ra.diameter.ro.stackif.CreditControlRequestImpl;
import com.baypackets.ase.ra.diameter.ro.stackif.RoAbstractRequest;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackInterfaceFactory;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.resource.ResourceContext;

import fr.marben.diameter.DiameterSession;

public class RoResourceAdaptorImpl implements RoResourceAdaptor {

	private static Logger logger = Logger.getLogger(RoResourceAdaptorImpl.class);

	private static ResourceContext context;
	public static RoStackInterface stackInterface;
	private short role = RoResourceAdaptor.ROLE_ACTIVE;
	private RoAppRouter appRouter = null;

	private boolean stackUP = false;
	private boolean canSendTOClient = true;
	private boolean canSendTOServer = true;
	private boolean peerDfnUp = true;
	private ArrayList appList = new ArrayList();
	// Map for RoSession containing mapping of stack session and RoSession
	private static ConcurrentHashMap<String, RoSession> roSessionMap=new ConcurrentHashMap<String, RoSession>();
	public RoResourceAdaptorImpl() {
		super();
	}

	public void init(ResourceContext context) throws ResourceException {
		logger.debug("Inside RoResourceAdaptorImpl.init with context "+context);
		RoResourceAdaptorImpl.context = context;		
		logger.debug("init  class loader is "+this.getClass().getClassLoader());

		((RoResourceFactoryImpl)RoResourceAdaptorImpl.context.getResourceFactory()).init(RoResourceAdaptorImpl.context);

		logger.debug("Setting RoResourceAdaptorImpl in  RoResourceAdaptorFactory");
		RoResourceAdaptorFactory.setResourceAdaptor(this);
		// Get configuration role
		this.role = context.getCurrentRole();

		logger.debug("The system is " + (this.role == RoResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));


		// Load and initialize Ro stack interface
		logger.debug("init(): now load stack interface.");
		try {
			RoResourceAdaptorImpl.stackInterface = RoStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		} catch (Throwable t) {
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}
		try {
			logger.debug("init(): initialize stack interface...");
			try {
				RoResourceAdaptorImpl.stackInterface.init(context);
			} catch (RoResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("init(): stack interface is initialized.");
		} catch (Exception ex) {
			logger.error("Ro stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}
		appRouter = RoResourceAdaptorFactory.getAppRouter();
		//RoSession.setResourceAdaptor(this);

	}

	public void start() throws ResourceException {
		if (this.role != RoResourceAdaptor.ROLE_ACTIVE) {
			logger.info("Standby...");
			return;
		}
		logger.debug("Start stack interface...");
		try {
			try {
				RoResourceAdaptorImpl.stackInterface.startStack();
			} catch (RoResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stackUP = true; // TODO - what about with 1 DFN setup? Or when only single DFN is up in 2 DFN setup?
		} catch (Exception ex) {
			logger.error("Ro stack interface start() failed: " + ex);
			throw new ResourceException(ex);
		}
		logger.debug("Stack interface is started.");

	}

	public void stop() throws ResourceException {
		try {
			if(this.stackUP == true ) {
				logger.debug(" stoping stack " );
				try {
					RoResourceAdaptorImpl.stackInterface.stopStack();
				} catch (RoResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Ro stack interface stop()n failed: " + ex);
			throw new ResourceException(ex);
		}

		logger.info("Stack Interface successfully stopped");
	}

	public void configurationChanged(String arg0, Object arg1)
	throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void roleChanged(String clusterId, String subsystemId, short role) {
		logger.debug("roleChanged(): role is changed to " + role);
		short preRole = this.role;
		this.role = role;
		if (preRole != ROLE_ACTIVE && role == ROLE_ACTIVE) {
			try {
				this.start();
			} catch (Exception e) {
				logger.error("roleChanged(): " + e);
			}
		}
	}

	public void sendMessage(SasMessage message) throws IOException {
		logger.debug("Sending message: " + message);
		try {
			if (message instanceof RoRequest) {
				if(this.canSendTOServer) {
					replicate((RoRequest)message);
					RoResourceAdaptorImpl.stackInterface.handleRequest((RoRequest)message);
				} else {
					logger.debug("Peer is disconnected. cannot send request");
					RoResourceEvent resourceEvent = new RoResourceEvent(message, 
							RoResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					resourceEvent.setMessage((RoMessage)message);
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering RoResourceEvent"+e);
						throw new RoResourceException(e);
					}
				}
			} else if (message instanceof RoResponse) {
				if(this.canSendTOClient) {
					replicate((RoResponse)message);
					RoResourceAdaptorImpl.stackInterface.handleResponse((RoResponse)message);
				} else {
					logger.debug("Peer is disconnected. cannot send response");
					RoResourceEvent resourceEvent = new RoResourceEvent(message, 
							RoResourceEvent.RESPONSE_FAIL_EVENT, message.getApplicationSession());
					resourceEvent.setMessage((RoMessage)message);
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering RoResourceEvent"+e);
						throw new RoResourceException(e);
					}
				}
			} else {
				logger.error("Message dropped: not a Ro message.");
			}
		} catch (RoResourceException ex) {
			logger.error("sendMessage() failed " + ex);			
		}

	}

	public void processed(SasMessage arg0) {
		// TODO Auto-generated method stub

	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// TODO Auto-generated method stub

	}

	public void deliverRequest(RoRequest request) throws ResourceException {
		if(logger.isDebugEnabled())
		logger.debug("deliverRequest(): request " + request.getType());

		
		SipApplicationSession appSession = null;
		RoSession roSession = null;
		// TODO - Server mode
		if(request instanceof CreditControlRequest){
			DiameterSession serverStackSession=((CreditControlRequestImpl)request).getServerStackSession();
			 String id=((CreditControlRequestImpl)request).getSessionId();//ServerStackSession(); 
			//String id=serverStackSession.getSessionId();
			if(request.getType()==Constants.INITIAL_REQUEST || request.getType()==Constants.EVENT_REQUEST ){
				String protocol = "ro_interface";
				DeployableObject aseContext = null;
				//AseIc ic = null;
				aseContext = findApplication(request);
				logger.debug("AseContext found is " +aseContext);
				if(aseContext == null) {
					logger.error("AseContext not found for the request so sending 4010 response");
					try {

						((CreditControlRequestImpl)request).createAnswer(ResultCodes.DIAMETER_END_USER_SERVICE_DENIED).send();

					} catch (RoResourceException e) {
						logger.error("RoResourceException in sending response ",e);				
					} catch (Exception e) {
						logger.error("Exception in sending response ",e);
					} 
					return;
				}
				appSession = aseContext.createApplicationSession(protocol, null);
				roSession = (RoSession)((RoResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);
				roSession.setServerStackSession(((CreditControlRequestImpl)request).getServerStackSession());
				roSession.setServerStackSessionId(id);
				
				if(logger.isDebugEnabled()){
					logger.debug("put sessionId in roSessinMap"+ roSessionMap);
				}
				roSessionMap.put(id, roSession);
				// Set worker queue for request
				((SipApplicationSessionImpl)appSession).getIc().setWorkQueue(((RoAbstractRequest)request).getWorkQueue());
			}
			else{
				if(logger.isDebugEnabled()){
					logger.debug("get Sesion id from map "+ roSessionMap);
				}
				roSession=roSessionMap.get(id);
				if(roSession==null){
//					try {
//						
//						logger.error("RoSession not found for the request so sending 4010 response, Stack sessionID:"+id);
//						((CreditControlRequestImpl)request).createAnswer(ResultCodes.DIAMETER_END_USER_SERVICE_DENIED).send();
//						
//						if(serverStackSession!=null){
//						   serverStackSession.delete();
//						}
//					} catch (RoResourceException e) {
//						logger.error("RoResourceException in sending response ",e);				
//					} catch (Exception e) {
//						logger.error("Exception in sending response ",e);
//					} 
//					return;
					
					String protocol = "ro_interface";
					DeployableObject aseContext = null;
					//AseIc ic = null;
					aseContext = findApplication(request);
					logger.debug("AseContext found is " +aseContext);
					if(aseContext == null) {
						logger.error("AseContext not found for the request so sending 3010 response");
						try {

							((CreditControlRequestImpl)request).createAnswer(ResultCodes.DIAMETER_UNKNOWN_PEER).send();

						} catch (RoResourceException e) {
							logger.error("RoResourceException in sending response ",e);				
						} catch (Exception e) {
							logger.error("Exception in sending response ",e);
						} 
						return;
					}
					appSession = aseContext.createApplicationSession(protocol, null);
					roSession = (RoSession)((RoResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);
					roSession.setServerStackSession(((CreditControlRequestImpl)request).getServerStackSession());
					roSession.setServerStackSessionId(id);
					
					if(logger.isDebugEnabled()){
						logger.debug("put sessionId in roSessinMap"+ roSessionMap);
					}
					roSessionMap.put(id, roSession);
					// Set worker queue for request
					((SipApplicationSessionImpl)appSession).getIc().setWorkQueue(((RoAbstractRequest)request).getWorkQueue());
				}else{
					if(logger.isDebugEnabled())
						logger.debug("RoSession found for request");
				}
			}
			((CreditControlRequestImpl)request).setProtocolSession(roSession);
			((CreditControlRequestImpl)request).setTimestamp(System.currentTimeMillis());
			roSession.addRequest(request);
		}
		
		
		if (RoResourceAdaptorImpl.context != null) {
			logger.debug("deliverRequest(): call context.");
			RoResourceAdaptorImpl.context.deliverMessage(request, true);
		} else {
			logger.debug("deliverRequest(): call request handler.");
			//			try {
			//				if (request.getUserData() != null) {
			//					logger.debug(request.getUserData().toString());
			//				}
			//			} catch (ValidationException e) {
			//				throw new RoResourceException(e);
			//			}
		}

	}
	private DeployableObject findApplication(RoRequest request) {
		DeployableObject ctx = null;
		String name = null;

		String appName = this.appRouter.getMatchingApp(request);
		if(appName == null) {
			return null;
		}

		for(Iterator apps = this.getAllRegisteredApps(); apps.hasNext();) {
			ctx = (DeployableObject)apps.next();
			//name = fixAppName(ctx.getName());
			name=ctx.getObjectName();
			if(logger.isDebugEnabled()) {
				logger.debug("Checking app " +name);
			}
			if(name.equals(appName)) {
				return ctx;
			}
		}
		return null;
	}
	private String fixAppName(String name) {
		return name.substring(0,name.indexOf("_"));
	}
	public void deliverResponse(RoResponse response) throws ResourceException {
		logger.debug("deliverResponse(): response "+response); 
		if(logger.isDebugEnabled()) {
			logger.debug(" setting RoSession state to inactive ");
		}
		((RoSession)response.getSession()).setRoState(RoSession.INACTIVE);

		if (RoResourceAdaptorImpl.context != null) {
			logger.debug("deliverResponse(): call context.");
			RoResourceAdaptorImpl.context.deliverMessage(response, true);
		} else {
			logger.debug("deliverResponse(): context is null.");
			//			logger.debug("deliverResponse(): call response handler.");
			//			if (response.getUserData() != null) {
			//				logger.debug(response.getUserData().toString());
			//			}
		}

	}

	public void deliverEvent(RoResourceEvent event) throws ResourceException {
		logger.debug("deliverEvent(): event[" + event.getType() + "]");

		boolean deliverUpward = false;
		if(event.getApplicationSession() != null )
		{
			logger.debug("app session is null");
			deliverUpward = true;
		}

		if (event.getType().equals(RoResourceEvent.TIMEOUT_EVENT)) {
			((RoSession) (event.getMessage()).getSession())
					.setRoState(RoSession.INACTIVE);
		} else if (event.getType().equals(RoResourceEvent.REQUEST_FAIL_EVENT)) {
		} else if (event.getType().equals(RoResourceEvent.RESPONSE_FAIL_EVENT)) {
		} else if (event.getType().equals(RoResourceEvent.RO_NOTIFY_DISCONNECT_PEER_REQUEST)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(RoResourceEvent.RO_DISCONNECT_PEER_RESPONSE)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(RoResourceEvent.RO_NOTIFY_PEER_UP)) {
			deliverUpward = true;
			this.canSendTOServer = true;
		} else if (event.getType().equals(RoResourceEvent.RO_NOTIFY_PEER_DOWN)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(RoResourceEvent.RO_NOTIFY_UNEXPECTED_MESSAGE)) {
		} else if (event.getType().equals(RoResourceEvent.RO_NOTIFY_FAILOVER)) {
		}
		if(deliverUpward == true){
			if (RoResourceAdaptorImpl.context != null) {
				logger.debug("deliverEvent(): call context.");
				RoResourceAdaptorImpl.context.deliverEvent(event, true);
			} else {
				logger.debug("deliverEvent(): call event handler.");
			}
		}
		logger.debug("deliverEvent()End: for event:"+event.getType()+" canSendToClient:"+canSendTOClient+" cansendToServer:"+canSendTOServer);
	}

	public static ResourceContext getResourceContext() {
		return RoResourceAdaptorImpl.context;
	}

	public static RoStackInterface getStackInterface() {
		return RoResourceAdaptorImpl.stackInterface;
	}

	private void replicate(RoRequest request) {
		if(request.getSession()!=null){
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE_REQ");
		((RoSession)request.getSession()).sendReplicationEvent(event);
		}
	}
	
	private void replicate(RoResponse response) {
		if(response.getSession()!=null){
		ReplicationEvent event = new ReplicationEvent(response.getSession() , "RESOURCE_RES");
		((RoSession)response.getSession()).sendReplicationEvent(event);
		}
	}

	/**
	 *	This method removes an <code>AseContext</code> from the list 
	 *	of AseContext which use this resource.This method is called
	 *	when an application is deactivated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void unregisterApp(DeployableObject ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside unregisterApp() with context (ObjectName)"+ ctx.getObjectName());
		}
		this.appList.remove(ctx);
	}

	/**
	 *	This method adds an <code>AseContext</code> to the list which 
	 *	uses this resource.This method is called when an application
	 *	is activated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void registerApp(DeployableObject ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context (ObjectName)"+ ctx.getObjectName());
		}
		appRouter.generateRules();
		this.appList.add(ctx);
	}

	/**
	 *	This method returns the Iterator over all of the 
	 *	<code>AseContext</code> which uses this resource.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public Iterator getAllRegisteredApps(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAllRegisteredApps()");
		}
		return this.appList.iterator();
	}
	
	public static void removeRoSession(String id){
		if(logger.isDebugEnabled()) {
			logger.debug("removeRoSession from map"+ id);
		}
		if(roSessionMap.remove(id)==null){
			logger.error("removeRoSession(String): No RoSession found for : " + id);
		}
	}
	
	public static void addRoSession(String id,RoSession session){
		if(logger.isDebugEnabled()) {
			logger.debug("addRoSession to map"+ id);
		}
		roSessionMap.put(id, session);
	}
	
}



