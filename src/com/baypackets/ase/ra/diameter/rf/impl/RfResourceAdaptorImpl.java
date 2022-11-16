package com.baypackets.ase.ra.diameter.rf.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfRequest;
import com.baypackets.ase.ra.diameter.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.diameter.rf.RfResourceEvent;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.RfResponse;
import com.baypackets.ase.ra.diameter.rf.RfStackInterface;
import com.baypackets.ase.ra.diameter.rf.rarouter.RfAppRouter;
import com.baypackets.ase.ra.diameter.rf.stackif.RfAbstractRequest;
import com.baypackets.ase.ra.diameter.rf.stackif.RfAccountingRequestImpl;
import com.baypackets.ase.ra.diameter.rf.stackif.RfStackInterfaceFactory;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RfResourceAdaptorImpl implements RfResourceAdaptor {

	private static Logger logger = Logger.getLogger(RfResourceAdaptorImpl.class);

	private static ResourceContext context;
	private static RfStackInterface stackInterface;
	private short role = RfResourceAdaptor.ROLE_ACTIVE;
	private RfAppRouter appRouter = null;

	private boolean stackUP = false;
	private boolean canSendToServer = true;
	private boolean canSendToClient = true;
	private boolean peerDfnUp = true;
	private ArrayList appList = new ArrayList();

	public RfResourceAdaptorImpl() {
		super();
	}

	public void init(ResourceContext context) throws ResourceException {
		logger.debug("Inside RfResourceAdaptorImpl.init with context "+context);
		RfResourceAdaptorImpl.context = context;		
		logger.debug("init  class loader is "+this.getClass().getClassLoader());

		((RfResourceFactoryImpl)RfResourceAdaptorImpl.context.getResourceFactory()).init(RfResourceAdaptorImpl.context);

		logger.debug("Setting RfResourceAdaptorImpl in  RfResourceAdaptorFactory");
		RfResourceAdaptorFactory.setResourceAdaptor(this);
		
		// Get configuration role
		this.role = context.getCurrentRole();

		logger.debug("The system is " + (this.role == RfResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));


		// Load and initialize Rf stack interface
		logger.debug("init(): now load stack interface.");
		try {
			RfResourceAdaptorImpl.stackInterface = RfStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		} catch (Throwable t) {
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}
		try {
			logger.debug("init(): initialize stack interface...");
			try {
				RfResourceAdaptorImpl.stackInterface.init(context);
			} catch (RfResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("init(): stack interface is initialized.");
		} catch (Exception ex) {
			logger.error("Rf stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}
		appRouter = RfResourceAdaptorFactory.getAppRouter();
		//RfSession.setResourceAdaptor(this);

	}

	public void start() throws ResourceException {
		if (this.role != RfResourceAdaptor.ROLE_ACTIVE) {
			logger.info("Standby...");
			return;
		}
		logger.debug("Start stack interface...");
		try {
			try {
				RfResourceAdaptorImpl.stackInterface.start();
			} catch (RfResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stackUP = true; // TODO - what about with 1 DFN setup? Or when only single DFN is up in 2 DFN setup?
		} catch (Exception ex) {
			logger.error("Rf stack interface start() failed: " + ex);
			throw new ResourceException(ex);
		}
		logger.debug("Stack interface is started.");

	}

	public void stop() throws ResourceException {
		try {
			if(this.stackUP == true ) {
				logger.debug(" stoping stack " );
				try {
					RfResourceAdaptorImpl.stackInterface.stop();
				} catch (RfResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Rf stack interface stop()n failed: " + ex);
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
			if (message instanceof RfRequest) {
				if(this.canSendToServer) {
					replicate((RfRequest)message);
					RfResourceAdaptorImpl.stackInterface.handleRequest((RfRequest)message);
				} else {
					logger.debug("Peer is disconnected. cannot send request");
					RfResourceEvent resourceEvent = new RfResourceEvent(message, 
							RfResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering RfResourceEvent"+e);
						throw new RfResourceException(e);
					}
				}
			} else if (message instanceof RfResponse) {
				if(this.canSendToClient) {
					RfResourceAdaptorImpl.stackInterface.handleResponse((RfResponse)message);
				} else {
					logger.debug("Peer is disconnected. cannot send response");
					RfResourceEvent resourceEvent = new RfResourceEvent(message, 
							RfResourceEvent.RESPONSE_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering RfResourceEvent"+e);
						throw new RfResourceException(e);
					}
				}
			} else {
				logger.error("Message dropped: not a Rf message.");
			}
		} catch (RfResourceException ex) {
			logger.error("sendMessage() failed " + ex);			
		}

	}

	public void processed(SasMessage arg0) {
		// TODO Auto-generated method stub

	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// TODO Auto-generated method stub

	}

	public void deliverRequest(RfRequest request) throws ResourceException {
		logger.debug("deliverRequest(): request " + request.getType());

		DeployableObject aseContext = null;
		SipApplicationSession appSession = null;
		RfSession rfSession = null;
		String protocol = "sh_interface";
		//AseIc ic = null;
		aseContext = findApplication(request);
		logger.debug("AseContext found is " +aseContext);

		if(aseContext == null) {
			try {
				((RfAccountingRequest)request).createAnswer(4010).send();
			} catch (RfResourceException e) {
				logger.error("RfResourceException in sending response ",e);				
			} catch (Exception e) {
				logger.error("Exception in sending response ",e);
			} 
			return;
		}

		appSession = aseContext.createApplicationSession(protocol, null);
		rfSession = (RfSession)((RfResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);

		// TODO - Server mode
		if(request instanceof RfAccountingRequest)
			((RfAccountingRequestImpl)request).setProtocolSession(rfSession);
		
		rfSession.addRequest(request);
		// Set worker queue for request
				((SipApplicationSessionImpl)appSession).getIc().setWorkQueue(((RfAbstractRequest)request).getWorkQueue());				
		if(context != null)
		{
			logger.debug("deliverRequest(): call context.");
			context.deliverMessage(request, true);
		} else
		{
			logger.debug("deliverRequest(): call request handler.");
		}

	}

	private DeployableObject findApplication(RfRequest request) {
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
	public void deliverResponse(RfResponse response) throws ResourceException {
		logger.debug("deliverResponse(): response "+response); 
		if(logger.isDebugEnabled()) {
			logger.debug(" setting RfSession state to inactive ");
		}
		((RfSession)response.getSession()).setShState(RfSession.RF_INACTIVE);

		if (this.context != null) {
			logger.debug("deliverResponse(): call context.");
			this.context.deliverMessage(response, true);
		} else {
			logger.debug("deliverResponse(): context is null.");
			//			logger.debug("deliverResponse(): call response handler.");
			//			if (response.getUserData() != null) {
			//				logger.debug(response.getUserData().toString());
			//			}
		}

	}

	public void deliverEvent(RfResourceEvent event) throws ResourceException {
		logger.debug("deliverEvent(): event[" + event.getType() + "]");

		boolean deliverUpward = false;
		if(event.getApplicationSession() != null )
		{
			logger.debug("app session is null");
			deliverUpward = true;
		}
		
		if(event.getType().equals(RfResourceEvent.TIMEOUT_EVENT)) {
			((RfSession)(event.getMessage()).getSession()).setShState(RfSession.RF_INACTIVE);
		} else if(event.getType().equals(RfResourceEvent.REQUEST_FAIL_EVENT)){
		} else if(event.getType().equals(RfResourceEvent.RESPONSE_FAIL_EVENT)){
		} else if(event.getType().equals(RfResourceEvent.RF_NOTIFY_DISCONNECT_PEER_REQUEST)){
			deliverUpward = true;
			this.canSendToServer= false;
		}else if(event.getType().equals(RfResourceEvent.RF_DISCONNECT_PEER_RESPONSE)){
			deliverUpward = true;
			this.canSendToServer= false;
		}else if (event.getType().equals(RfResourceEvent.RF_NOTIFY_PEER_UP)){
			deliverUpward = true;
			this.peerDfnUp = true;
			this.canSendToServer = true;
		}else if (event.getType().equals(RfResourceEvent.RF_NOTIFY_PEER_DOWN)){
			deliverUpward = true;
			this.peerDfnUp = false;
			this.canSendToServer= false;
		}else if (event.getType().equals(RfResourceEvent.RF_NOTIFY_UNEXPECTED_MESSAGE)){
		}else if (event.getType().equals(RfResourceEvent.RF_NOTIFY_FAILOVER)){
		}
		
		if(deliverUpward == true){
			if (this.context != null) {
				logger.debug("deliverEvent(): call context.");
				this.context.deliverEvent(event, true);
			} else {
				logger.debug("deliverEvent(): call event handler.");
			}
		}
		logger.debug("deliverEvent()End: for event:"+event.getType()+" canSendToClient:"+canSendToClient+" cansendToServer:"+canSendToServer);	
	}

	public static ResourceContext getResourceContext() {
		return RfResourceAdaptorImpl.context;
	}

	public static RfStackInterface getStackInterface() {
		return RfResourceAdaptorImpl.stackInterface;
	}

	private void replicate(RfRequest request) {
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((RfSession)request.getSession()).sendReplicationEvent(event);
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
			logger.debug("Inside unregisterApp() with context "+ctx);
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
			logger.debug("Inside registerApp() with context= "+ctx);
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
}

