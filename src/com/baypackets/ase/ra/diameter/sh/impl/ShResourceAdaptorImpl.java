package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.sh.*;
import com.baypackets.ase.ra.diameter.sh.rarouter.ShAppRouter;
import com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager.SHParseRulesException;
import com.baypackets.ase.ra.diameter.sh.stackif.*;
import com.baypackets.ase.ra.diameter.sh.utils.ResultCodes;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.resource.ResourceContext;
import fr.marben.diameter.DiameterSession;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipApplicationSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ShResourceAdaptorImpl implements ShResourceAdaptor {


	private static final Logger logger = Logger.getLogger(ShResourceAdaptorImpl.class);

	private static ResourceContext context;
	public static ShStackInterface stackInterface;
	private short role = ShResourceAdaptor.ROLE_ACTIVE;
	private ShAppRouter appRouter = null;

	private boolean stackUP = false;
	private boolean canSendToServer = true;
	private final boolean canSendToClient = true;
	private final ArrayList appList = new ArrayList();
	private static final ConcurrentHashMap<String, ShSession> shSessionMap=new ConcurrentHashMap<String, ShSession>();
	public ShResourceAdaptorImpl() {
		super();
	}

	public void init(ResourceContext context) throws ResourceException {

		logger.debug("Inside ShResourceAdaptorImpl.init() with context "+context);
		ShResourceAdaptorImpl.context = context;		

		((ShResourceFactoryImpl)ShResourceAdaptorImpl.context.getResourceFactory()).init(ShResourceAdaptorImpl.context);

		logger.debug("Setting ShResourceAdaptorImpl in  ShResourceAdaptorFactory");
		ShResourceAdaptorFactory.setResourceAdaptor(this);

		// Get configuration role
		this.role = context.getCurrentRole();

		logger.debug("The system is " + (this.role == ShResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));


		// Load and initialize Sh stack interface
		logger.debug("init(): now load stack interface.");
		try {
			ShResourceAdaptorImpl.stackInterface = ShStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		} catch (Throwable t) {
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}
		try {
			logger.debug("init(): initialize stack interface...");
			try {
				ShResourceAdaptorImpl.stackInterface.init(context);
			} catch (ShResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("init(): stack interface is initialized.");
		} catch (Exception ex) {
			logger.error("Sh stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}
		appRouter = ShResourceAdaptorFactory.getAppRouter();
	}

	public void start() throws ResourceException {
		if (this.role != ShResourceAdaptor.ROLE_ACTIVE) {
			logger.info("Standby...");
			return;
		}
		logger.debug("Start stack interface...");
		try {
			try {
				ShResourceAdaptorImpl.stackInterface.startStack();
			} catch (ShResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stackUP = true; // TODO - what about with 1 DFN setup? Or when only single DFN is up in 2 DFN setup?
		} catch (Exception ex) {
			logger.error("Sh stack interface start() failed: " ,ex);
			throw new ResourceException(ex);
		}
		logger.debug("Stack interface is started.");

	}

	public void stop() throws ResourceException {
		try {
			if(this.stackUP == true ) {
				logger.debug(" stoping stack " );
				try {
					ShResourceAdaptorImpl.stackInterface.stopStack();
				} catch (ShResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Sh stack interface stop()n failed: " + ex);
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
			if (message instanceof ShRequest) {
				if(this.canSendToServer) {
					replicate((ShRequest)message);
					ShResourceAdaptorImpl.stackInterface.handleRequest((ShRequest)message);
				} else {
					logger.debug("Peer is disconnected. cannot send request");
					ShResourceEvent resourceEvent = new ShResourceEvent(message, 
							ShResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering ShResourceEvent"+e);
						throw new ShResourceException(e);
					}
				}
			} else if (message instanceof ShResponse) {
				if(this.canSendToClient) {
					ShResourceAdaptorImpl.stackInterface.handleResponse((ShResponse)message);
				} else {
					logger.debug("Peer is disconnected. cannot send response");
					ShResourceEvent resourceEvent = new ShResourceEvent(message, 
							ShResourceEvent.RESPONSE_FAIL_EVENT, message.getApplicationSession());
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering ShResourceEvent"+e);
						throw new ShResourceException(e);
					}
				}
			} else {
				logger.error("Message dropped: not a Sh message.");
			}
		} catch (ShResourceException ex) {
			logger.error("sendMessage() failed " + ex);			
		}

	}

	public void processed(SasMessage arg0) {
		// TODO Auto-generated method stub

	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// TODO Auto-generated method stub

	}

	public void deliverRequest(ShRequest request) throws ResourceException {
		logger.debug("deliverRequest(): request " + request.getType());

		DeployableObject aseContext = null;
		SipApplicationSession appSession = null;
		ShSession shSession = null;
		String protocol = "sh_interface";
		//AseIc ic = null;
		aseContext = findApplication(request);
		logger.debug("AseContext found is " +aseContext);

		if(aseContext == null) {
			try {
				((ShUserDataRequestImpl)request).createAnswer(ResultCodes.DIAMETER_END_USER_SERVICE_DENIED).send();
			} catch (ShResourceException e) {
				logger.error("ShResourceException in sending response ",e);				
			} catch (Exception e) {
				logger.error("Exception in sending response ",e);
			} 
			return;
		}

		appSession = aseContext.createApplicationSession(protocol, null);
		shSession = (ShSession)((ShResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);

		DiameterSession serverStackSession=null;
		String id=null;

		if(request instanceof ShUserDataRequestImpl)
		{((ShUserDataRequestImpl)request).setProtocolSession(shSession);
			((ShUserDataRequestImpl)request).setTimestamp(System.currentTimeMillis());
			serverStackSession=((ShUserDataRequestImpl)request).getServerStackSession();
			id= request.getSessionId();//ServerStackSession();
		}
		else if(request instanceof ShProfileUpdateRequestImpl)
		{((ShProfileUpdateRequestImpl)request).setProtocolSession(shSession);
		((ShProfileUpdateRequestImpl)request).setTimestamp(System.currentTimeMillis());
			serverStackSession=((ShProfileUpdateRequestImpl)request).getServerStackSession();
			id= request.getSessionId();//ServerStackSession();
		}
		else
		{if(request instanceof ShSubscribeNotificationRequestImpl)
				((ShSubscribeNotificationRequestImpl)request).setProtocolSession(shSession);
			((ShSubscribeNotificationRequestImpl)request).setTimestamp(System.currentTimeMillis());
			serverStackSession=((ShSubscribeNotificationRequestImpl)request).getServerStackSession();
			id= request.getSessionId();//ServerStackSession();
		}

		shSession.setServerStackSession(((ShSubscribeNotificationRequestImpl)request).getServerStackSession());
		shSession.setServerStackSessionId(id);
		shSessionMap.put(id, shSession);
		shSession.addRequest(request);
		// Set worker queue for request
		((SipApplicationSessionImpl)appSession).getIc().setWorkQueue(((ShAbstractRequest)request).getWorkQueue());
	//	ShResourceAdaptorImpl _tmp1 = this;
		if(context != null)
		{
			logger.debug("deliverRequest(): call context.");
	//		ShResourceAdaptorImpl _tmp2 = this;
			context.deliverMessage(request, true);
		} else
		{
			logger.debug("deliverRequest(): call request handler.");
		}

	}

	private DeployableObject findApplication(ShRequest request) {
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
	public void deliverResponse(ShResponse response) throws ResourceException {
		logger.debug("deliverResponse(): response "+response); 
		if(logger.isDebugEnabled()) {
			logger.debug(" setting ShSession state to inactive ");
		}
		((ShSession)response.getSession()).setShState(ShSession.SH_INACTIVE);

		if (ShResourceAdaptorImpl.context != null) {
			logger.debug("deliverResponse(): call context.");
			ShResourceAdaptorImpl.context.deliverMessage(response, true);
		} else {
			logger.debug("deliverResponse(): context is null.");
		}

	}

	public void deliverEvent(ShResourceEvent event) throws ResourceException {
		logger.debug("deliverEvent(): event[" + event.getType() + "]");

		boolean deliverUpward = false;
		if(event.getApplicationSession() != null )
		{
			logger.debug("app session is null");
			deliverUpward = true;
		}

		if(event.getType().equals(ShResourceEvent.TIMEOUT_EVENT)) {
			((ShSession)(event.getMessage()).getSession()).setShState(ShSession.SH_INACTIVE);
		} else if(event.getType().equals(ShResourceEvent.REQUEST_FAIL_EVENT)){
		} else if(event.getType().equals(ShResourceEvent.RESPONSE_FAIL_EVENT)){
		} else if(event.getType().equals(ShResourceEvent.SH_NOTIFY_DISCONNECT_PEER_REQUEST)){
			deliverUpward = true;
			this.canSendToServer= false;
		}else if(event.getType().equals(ShResourceEvent.SH_DISCONNECT_PEER_RESPONSE)){
			deliverUpward = true;
			this.canSendToServer= false;
		}else if (event.getType().equals(ShResourceEvent.SH_NOTIFY_PEER_UP)){
			deliverUpward = true;
			this.canSendToServer = true;
		}else if (event.getType().equals(ShResourceEvent.SH_NOTIFY_PEER_DOWN)){
			deliverUpward = true;
			this.canSendToServer= false;
		}else if (event.getType().equals(ShResourceEvent.SH_NOTIFY_UNEXPECTED_MESSAGE)){
		}else if (event.getType().equals(ShResourceEvent.SH_NOTIFY_FAILOVER)){
		}

		if(deliverUpward == true){
			if (ShResourceAdaptorImpl.context != null) {
				logger.debug("deliverEvent(): call context.");
				ShResourceAdaptorImpl.context.deliverEvent(event, true);
			} else {
				logger.debug("deliverEvent(): call event handler.");
			}
		}
		logger.debug("deliverEvent()End: for event:"+event.getType()+" canSendToClient:"+canSendToClient+" cansendToServer:"+canSendToServer);
	}

	public static ResourceContext getResourceContext() {
		return ShResourceAdaptorImpl.context;
	}

	public static ShStackInterface getStackInterface() {
		return ShResourceAdaptorImpl.stackInterface;
	}

	private void replicate(ShRequest request) {
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((ShSession)request.getSession()).sendReplicationEvent(event);
	}

	private void replicate(ShResponse response) {
		if(response.getSession()!=null){
			ReplicationEvent event = new ReplicationEvent(response.getSession() , "RESOURCE_RES");
			((ShSession)response.getSession()).sendReplicationEvent(event);
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
	public void registerApp(DeployableObject ctx)  {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context= "+ctx);
		}
		try {
			appRouter.generateRules();
		} catch (SHParseRulesException e) {
			e.printStackTrace();
		}
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

	public static void removeShSession(String id){
		if(logger.isDebugEnabled()) {
			logger.debug("removeShSession from map"+ id);
		}
		if(shSessionMap.remove(id)==null){
			logger.error("removeShSession(String): No RoSession found for : " + id);
		}
	}

	public static void addShSession(String id,ShSession session){
		if(logger.isDebugEnabled()) {
			logger.debug("addShSession to map"+ id);
		}
		shSessionMap.put(id, session);
	}
}

