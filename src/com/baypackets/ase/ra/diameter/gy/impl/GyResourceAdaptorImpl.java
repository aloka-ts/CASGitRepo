package com.baypackets.ase.ra.diameter.gy.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceAdaptor;
import com.baypackets.ase.ra.diameter.gy.GyResourceEvent;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.GyResponse;
import com.baypackets.ase.ra.diameter.gy.GyStackInterface;
import com.baypackets.ase.ra.diameter.gy.rarouter.GyAppRouter;
import com.baypackets.ase.ra.diameter.gy.stackif.CreditControlRequestImpl;
import com.baypackets.ase.ra.diameter.gy.stackif.GyStackInterfaceFactory;
import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.resource.ResourceContext;

public class GyResourceAdaptorImpl implements GyResourceAdaptor {

	private static Logger logger = Logger.getLogger(GyResourceAdaptorImpl.class);

	private static ResourceContext context;
	private static GyStackInterface stackInterface;
	private short role = GyResourceAdaptor.ROLE_ACTIVE;
	private GyAppRouter appRouter = null;

	private boolean stackUP = false;
	private boolean canSendTOClient = true;
	private boolean canSendTOServer = true;
	private boolean peerDfnUp = true;
	private ArrayList appList = new ArrayList();
	public GyResourceAdaptorImpl() {
		super();
	}

	public void init(ResourceContext context) throws ResourceException {
		logger.debug("Inside GyResourceAdaptorImpl.init with context "+context);
		GyResourceAdaptorImpl.context = context;		
		logger.debug("init  class loader is "+this.getClass().getClassLoader());

		((GyResourceFactoryImpl)GyResourceAdaptorImpl.context.getResourceFactory()).init(GyResourceAdaptorImpl.context);

		logger.debug("Setting GyResourceAdaptorImpl in  GyResourceAdaptorFactory");
		GyResourceAdaptorFactory.setResourceAdaptor(this);
		// Get configuration role
		this.role = context.getCurrentRole();

		logger.debug("The system is " + (this.role == GyResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));


		// Load and initialize Ro stack interface
		logger.debug("init(): now load stack interface.");
		try {
			GyResourceAdaptorImpl.stackInterface = GyStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		} catch (Throwable t) {
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}
		try {
			logger.debug("init(): initialize stack interface...");
			try {
				GyResourceAdaptorImpl.stackInterface.init(context);
			} catch (GyResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("init(): stack interface is initialized.");
		} catch (Exception ex) {
			logger.error("Gy stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}
		appRouter = GyResourceAdaptorFactory.getAppRouter();
		//RoSession.setResourceAdaptor(this);

	}

	public void start() throws ResourceException {
		if (this.role != GyResourceAdaptor.ROLE_ACTIVE) {
			logger.info("Standby...");
			return;
		}
		logger.debug("Start stack interface...");
		try {
			try {
				GyResourceAdaptorImpl.stackInterface.start();
			} catch (GyResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stackUP = true; // TODO - what about with 1 DFN setup? Or when only single DFN is up in 2 DFN setup?
		} catch (Exception ex) {
			logger.error("Gy stack interface start() failed: " + ex);
			throw new ResourceException(ex);
		}
		logger.debug("Stack interface is started.");

	}

	public void stop() throws ResourceException {
		try {
			if(this.stackUP == true ) {
				logger.debug(" stoping stack " );
				try {
					GyResourceAdaptorImpl.stackInterface.stop();
				} catch (GyResourceException e) {
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
			if (message instanceof GyRequest) {
				if(this.canSendTOServer) {
					replicate((GyRequest)message);
					GyResourceAdaptorImpl.stackInterface.handleRequest((GyRequest)message);
				} else {
					logger.debug("Peer is disconnected. cannot send request");
					GyResourceEvent resourceEvent = new GyResourceEvent(message, 
							GyResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering GyResourceEvent"+e);
						throw new GyResourceException(e);
					}
				}
			} else if (message instanceof GyResponse) {
				if(this.canSendTOClient) {
					GyResourceAdaptorImpl.stackInterface.handleResponse((GyResponse)message);
				} else {
					logger.debug("Peer is disconnected. cannot send response");
					GyResourceEvent resourceEvent = new GyResourceEvent(message, 
							GyResourceEvent.RESPONSE_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering GyResourceEvent"+e);
						throw new GyResourceException(e);
					}
				}
			} else {
				logger.error("Message dropped: not a Ro message.");
			}
		} catch (GyResourceException ex) {
			logger.error("sendMessage() failed " + ex);			
		}

	}

	public void processed(SasMessage arg0) {
		// TODO Auto-generated method stub

	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// TODO Auto-generated method stub

	}

	public void deliverRequest(GyRequest request) throws ResourceException {
		logger.debug("deliverRequest(): request " + request.getType());

		DeployableObject aseContext = null;
		SipApplicationSession appSession = null;
		GySession GySession = null;
		String protocol = "sh_interface";
		//AseIc ic = null;
		aseContext = findApplication(request);
		logger.debug("AseContext found is " +aseContext);

		if(aseContext == null) {
			
			try {
				
				((CreditControlRequestImpl)request).createAnswer(4010).send();
				
			} catch (GyResourceException e) {
				logger.error("GyResourceException in sending response ",e);				
			} catch (Exception e) {
				logger.error("Exception in sending response ",e);
			} 
			return;
		}

		appSession = aseContext.createApplicationSession(protocol, null);
		GySession = (GySession)((GyResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);

		// TODO - Server mode
		if(request instanceof CreditControlRequest)
			((CreditControlRequestImpl)request).setProtocolSession(GySession);
		
		GySession.addRequest(request);
		if (GyResourceAdaptorImpl.context != null) {
			logger.debug("deliverRequest(): call context.");
			GyResourceAdaptorImpl.context.deliverMessage(request, true);
		} else {
			logger.debug("deliverRequest(): call request handler.");
			//			try {
			//				if (request.getUserData() != null) {
			//					logger.debug(request.getUserData().toString());
			//				}
			//			} catch (ValidationException e) {
			//				throw new GyResourceException(e);
			//			}
		}

	}
	private DeployableObject findApplication(GyRequest request) {
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
	public void deliverResponse(GyResponse response) throws ResourceException {
		logger.debug("deliverResponse(): response "+response); 
		if(logger.isDebugEnabled()) {
			logger.debug(" setting GySession state to inactive ");
		}
		((GySession)response.getSession()).setRoState(GySession.INACTIVE);

		if (GyResourceAdaptorImpl.context != null) {
			logger.debug("deliverResponse(): call context.");
			GyResourceAdaptorImpl.context.deliverMessage(response, true);
		} else {
			logger.debug("deliverResponse(): context is null.");
			//			logger.debug("deliverResponse(): call response handler.");
			//			if (response.getUserData() != null) {
			//				logger.debug(response.getUserData().toString());
			//			}
		}

	}

	public void deliverEvent(GyResourceEvent event) throws ResourceException {
		logger.debug("deliverEvent(): event[" + event.getType() + "]");

		boolean deliverUpward = false;
		if(event.getApplicationSession() != null )
		{
			logger.debug("app session is null");
			deliverUpward = true;
		}

		if (event.getType().equals(GyResourceEvent.TIMEOUT_EVENT)) {
			((GySession) (event.getMessage()).getSession())
					.setRoState(GySession.INACTIVE);
		} else if (event.getType().equals(GyResourceEvent.REQUEST_FAIL_EVENT)) {
		} else if (event.getType().equals(GyResourceEvent.RESPONSE_FAIL_EVENT)) {
		} else if (event.getType().equals(GyResourceEvent.GY_NOTIFY_DISCONNECT_PEER_REQUEST)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(GyResourceEvent.GY_DISCONNECT_PEER_RESPONSE)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(GyResourceEvent.GY_NOTIFY_PEER_UP)) {
			deliverUpward = true;
			this.canSendTOServer = true;
		} else if (event.getType().equals(GyResourceEvent.GY_NOTIFY_PEER_DOWN)) {
			deliverUpward = true;
			this.canSendTOServer = false;
		} else if (event.getType().equals(GyResourceEvent.GY_NOTIFY_UNEXPECTED_MESSAGE)) {
		} else if (event.getType().equals(GyResourceEvent.GY_NOTIFY_FAILOVER)) {
		}
		if(deliverUpward == true){
			if (GyResourceAdaptorImpl.context != null) {
				logger.debug("deliverEvent(): call context.");
				GyResourceAdaptorImpl.context.deliverEvent(event, true);
			} else {
				logger.debug("deliverEvent(): call event handler.");
			}
		}
		logger.debug("deliverEvent()End: for event:"+event.getType()+" canSendToClient:"+canSendTOClient+" cansendToServer:"+canSendTOServer);
	}

	public static ResourceContext getResourceContext() {
		return GyResourceAdaptorImpl.context;
	}

	public static GyStackInterface getStackInterface() {
		return GyResourceAdaptorImpl.stackInterface;
	}

	private void replicate(GyRequest request) {
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((GySession)request.getSession()).sendReplicationEvent(event);
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
