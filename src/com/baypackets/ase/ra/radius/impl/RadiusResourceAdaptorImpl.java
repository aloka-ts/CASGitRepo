package com.baypackets.ase.ra.radius.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.RadiusResourceAdaptor;
import com.baypackets.ase.ra.radius.RadiusResourceEvent;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.RadiusStackClientInterface;
import com.baypackets.ase.ra.radius.RadiusStackServerInterface;
import com.baypackets.ase.ra.radius.rarouter.RadiusAppRouter;
import com.baypackets.ase.ra.radius.stackif.RadiusAbstractRequest;
import com.baypackets.ase.ra.radius.stackif.RadiusStackInterfaceFactory;
import com.baypackets.ase.ra.radius.stackif.RadiusStackServerInterfaceImpl;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RadiusResourceAdaptorImpl implements RadiusResourceAdaptor {
	private static Logger logger = Logger.getLogger(RadiusResourceAdaptorImpl.class);
	private static ResourceContext context;
	private static RadiusStackClientInterface stackClientInterface;
	private static RadiusStackServerInterface stackServerInterface;
	private short role = RadiusResourceAdaptor.ROLE_ACTIVE;
	private RadiusAppRouter appRouter;
	@SuppressWarnings("unchecked")
	private ArrayList appList = new ArrayList();
	private boolean stackUP = false;
	

	@Override
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside RadiusResourceAdaptorImpl with context "+context);
		RadiusResourceAdaptorImpl.context = context;		
		if(logger.isDebugEnabled())
			logger.debug("init  class loader is "+this.getClass().getClassLoader());

		((RadiusResourceFactoryImpl)RadiusResourceAdaptorImpl.context.getResourceFactory()).init(RadiusResourceAdaptorImpl.context);
		
		RadiusResourceAdaptorFactory.setResourceAdaptor(this);
		// Get configuration role
		this.role = context.getCurrentRole();
		if(logger.isDebugEnabled())
			logger.debug("The system is " + (this.role == RadiusResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));


		// Load and initialize Ro stack interface
		if(logger.isDebugEnabled())
		logger.debug("init(): now load stack interface.");
		try {
			RadiusResourceAdaptorImpl.stackServerInterface = RadiusStackInterfaceFactory.getInstance().loadStackServerInterface(this);
			RadiusResourceAdaptorImpl.stackClientInterface = RadiusStackInterfaceFactory.getInstance().loadStackClientInterface(this);
		} catch (Throwable t) {
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}
		try {
			if(logger.isDebugEnabled())
				logger.debug("init(): initialize stack interface...");
			try {
				RadiusResourceAdaptorImpl.stackServerInterface.init(context);
				RadiusResourceAdaptorImpl.stackClientInterface.init(context);				
			} catch (RadiusResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(logger.isDebugEnabled())
				logger.debug("init(): stack interface is initialized.");
		} catch (Exception ex) {
			logger.error("Radius stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}
		appRouter = RadiusResourceAdaptorFactory.getAppRouter();
		//RoSession.setResourceAdaptor(this);

	}

	@Override
	public void start() throws ResourceException {
		if (this.role != RadiusResourceAdaptor.ROLE_ACTIVE) {
				if(logger.isInfoEnabled())
					logger.info("Standby...");
			return;
		}
		if(logger.isDebugEnabled())
			logger.debug("Start stack interface...");
		try {
			try {
				RadiusResourceAdaptorImpl.stackServerInterface.start();
				RadiusResourceAdaptorImpl.stackClientInterface.start();
			} catch (RadiusResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stackUP = true; 
		} catch (Exception ex) {
			logger.error("Radius stack interface start() failed: " + ex);
			throw new ResourceException(ex);
		}
		if(logger.isDebugEnabled())
			logger.debug("Stack interface is started.");

	}

	@Override
	public void stop() throws ResourceException {
		try {
			if(this.stackUP == true ) {
				if(logger.isDebugEnabled())
					logger.debug(" stoping stack " );
				try {
					RadiusResourceAdaptorImpl.stackServerInterface.stop();
					RadiusResourceAdaptorImpl.stackClientInterface.stop();
				} catch (RadiusResourceException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Radius stack interface stop()n failed: " + ex);
			throw new ResourceException(ex);
		}
		if(logger.isInfoEnabled())
			logger.info("Stack Interface successfully stopped");
	}
	
	@Override
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

	@Override
	public void deliverRequest(RadiusRequest request) throws ResourceException {
		if(logger.isDebugEnabled())
		logger.debug("deliverRequest(): request " + request.getType());

		DeployableObject aseContext = null;
		SipApplicationSession appSession = null;
		RadiusSession radiusSession = null;
		String protocol = "radius_interface";
		//AseIc ic = null;
		aseContext = findApplication(request);
		logger.debug("AseContext found is " +aseContext);

		if(aseContext == null) {		
			if(logger.isDebugEnabled())
				logger.debug("No Application Found for radius request"+request);
			((RadiusStackServerInterfaceImpl)stackServerInterface).incrementFailedToTriggerCounter();
			return;
		}

		appSession = aseContext.createApplicationSession(protocol, null);
		radiusSession = (RadiusSession)((RadiusResourceFactoryImpl)context.getResourceFactory()).createSession(appSession);
		
		// TODO - Server mode
		if(request instanceof RadiusAbstractRequest)
			((RadiusAbstractRequest)request).setProtocolSession(radiusSession);
		
		radiusSession.addRequest(request);
		if (RadiusResourceAdaptorImpl.context != null) {
			logger.debug("deliverRequest(): call context.");
			RadiusResourceAdaptorImpl.context.deliverMessage(request, true);
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
	
	@SuppressWarnings("unchecked")
	private DeployableObject findApplication(RadiusRequest request) {
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
	@SuppressWarnings("unused")
	private String fixAppName(String name) {
		return name.substring(0,name.indexOf("_"));
	}
	
	@Override
	public void deliverResponse(RadiusResponse response)
			throws ResourceException {
		// TODO Auto-generated method stub
		//Radius response will come on same thread of RadiusClient
	}
	
	@Override
	public void deliverEvent(RadiusResourceEvent event)
			throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("deliverEvent(): event[" + event.getType() + "]");
			boolean deliverUpward = false;
			if(event.getApplicationSession() != null )
			{
				if(logger.isDebugEnabled())
					logger.debug("app session is null");
				deliverUpward = true;
			}
			
			if (event.getType().equals(RadiusResourceEvent.TIMEOUT_EVENT)) {
				((RadiusSession) (event.getMessage()).getSession()).setRadiusState(RadiusSession.INACTIVE);
			}else if (event.getType().equals(RadiusResourceEvent.REQUEST_FAIL_EVENT)) {

			}else if (event.getType().equals(RadiusResourceEvent.REQUEST_FAIL_EVENT)) {

			}else if (event.getType().equals(RadiusResourceEvent.ERROR_MSG_RECEIVED)) {

			}
			if(deliverUpward == true){
				if (RadiusResourceAdaptorImpl.context != null) {
						logger.debug("deliverEvent(): call context.");
					RadiusResourceAdaptorImpl.context.deliverEvent(event, true);
				} else {
					logger.debug("deliverEvent(): call event handler.");
				}
			}
	}
	

	@Override
	public void configurationChanged(String arg0, Object arg1)
			throws ResourceException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendMessage(SasMessage message) throws IOException {
		try {
			if (message instanceof RadiusResponse) {
				RadiusResourceAdaptorImpl.stackServerInterface.handleResponse((RadiusResponse) message);
				}
			else{
				throw new RadiusResourceException("RadiusRequest cannot be sent like this....");
			}
			} catch (RadiusResourceException ex) {
				logger.error("sendMessage() failed " + ex);			
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public Iterator getAllRegisteredApps(){
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

	public static ResourceContext getResourceContext() {
		return RadiusResourceAdaptorImpl.context;
	}

	public static RadiusStackClientInterface getStackClientInterface() {
	return RadiusResourceAdaptorImpl.stackClientInterface;
	}
	public static RadiusStackServerInterface getStackServerInterface() {
		return RadiusResourceAdaptorImpl.stackServerInterface;
	}
	
//	private void replicate(RadiusRequest request) {
//		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
//		((RadiusSession)request.getSession()).sendReplicationEvent(event);
//	}
}
