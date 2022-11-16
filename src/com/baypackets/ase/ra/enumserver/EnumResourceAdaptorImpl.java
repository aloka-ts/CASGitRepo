package com.baypackets.ase.ra.enumserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.enumserver.event.EnumResourceEvent;
import com.baypackets.ase.ra.enumserver.exception.EnumResourceException;
import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumRequestImpl;
import com.baypackets.ase.ra.enumserver.message.EnumResponse;
import com.baypackets.ase.ra.enumserver.qm.EnumQueueManagerImpl;
import com.baypackets.ase.ra.enumserver.rarouter.EnumAppRouter;
import com.baypackets.ase.ra.enumserver.receiver.EnumReceiver;
import com.baypackets.ase.ra.enumserver.session.EnumResourceSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.ra.enumserver.utils.Constants;

public class EnumResourceAdaptorImpl implements EnumResourceAdaptor {

	private static EnumResourceAdaptor EnumResourceAdaptor;

	/** Logger **/
	private static Logger logger = Logger
			.getLogger(EnumResourceAdaptorImpl.class);

	/** The status of RA, IS RA up or down. */
	private boolean raUp = false;

	/** Flag to mark true when ra is active on machine */
	private boolean raActive = false;

	/** The app list. MAintains list of deployed apps using the RA */
	private ArrayList<DeployableObject> appList = new ArrayList<DeployableObject>();

	/** The can send message. */
	private boolean canSendMessage = false;

	/** The Resourcecontext. */
	public ResourceContext context;

	private EnumReceiver enumReceiver;

	/** The role for RA/SAS active standby. */
	private short role = EnumResourceAdaptor.ROLE_ACTIVE;

	private EnumQueueManagerImpl queueManager;

	private EnumAppRouter appRouter;

	public EnumResourceAdaptorImpl() {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("creating EnumAdaptor object.");
		EnumResourceAdaptor = this;
		if (isDebugEnabled) {
			logger.debug("(cons)EnumresourceAdaptor:" + EnumResourceAdaptor);
		}
	}

	@Override
	public void deliverEvent(EnumResourceEvent event) throws ResourceException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Inside deliverEvent(): EnumResourceEvent::"
					+ event.getType());

		boolean deliverUpward = true;

		if (event.getType().equals(EnumResourceEvent.RESPONSE_FAIL_EVENT)) {
			((EnumResourceSession) (event.getMessage()).getSession())
					.setSessionState(EnumResourceSession.ENUM_INACTIVE);
			((EnumRequestImpl) event.getMessage())
					.setStatus(EnumRequest.REQUEST_INACTIVE);
			// replicate((EnumRequest)event.getMessage());
		} else if (event.getType().equals(EnumResourceEvent.QUEUE_FULL)) {
			((EnumResourceSession) (event.getMessage()).getSession())
					.setSessionState(EnumResourceSession.ENUM_INACTIVE);
			((EnumRequestImpl) event.getMessage())
					.setStatus(EnumRequest.REQUEST_INACTIVE);
			// replicate((LsRequest)event.getMessage());
		} else if (event.getType().equals(EnumResourceEvent.RA_DOWN)) {
			this.canSendMessage = false;
		} else if (event.getType().equals(EnumResourceEvent.RA_UP)) {
			this.canSendMessage = true;
		}

		if (deliverUpward == true) {
			if (context != null) {
				if (isDebugEnabled)
					logger.debug("deliverEvent(): call context.");
				context.deliverEvent(event, true);
			} else {
				if (isDebugEnabled)
					logger.debug("deliverEvent(): Context is null Failed");
			}
		}
		if (isDebugEnabled)
			logger.debug("Leaving deliverEvent() ");

	}

	@Override
	public ResourceContext getResourceContext() {

		return this.context;

	}

	@Override
	public void configurationChanged(String arg0, Object arg1)
			throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(ResourceContext context) throws ResourceException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		boolean isInfoEnabled = logger.isInfoEnabled();
		if (isInfoEnabled)
			logger.info("Inside EnumResourceAdaptor init() with context "
					+ context);
		this.context = context;
		if (context == null) {
			logger.error("InitialIzation Failed-RA created with null resource context");
			throw new EnumResourceException(
					"InitialIzation Failed-->Resource Context is null");
		}
		if (isDebugEnabled)
			logger.debug("init  class loader is "
					+ this.getClass().getClassLoader());
		// Initializes the resource factory
		((EnumResourceFactoryImpl) context.getResourceFactory()).init(context);
		// Gets the role for current SAS standby/active
		this.role = context.getCurrentRole();
		if (isDebugEnabled)
			logger.debug("The system is "
					+ (this.role == EnumResourceAdaptor.ROLE_ACTIVE ? "active"
							: "standby"));

		queueManager = EnumQueueManagerImpl.getInstance();
		enumReceiver = EnumReceiver.getInstance();
		enumReceiver.setResourceFactory((EnumResourceFactoryImpl) context
				.getResourceFactory());
		enumReceiver.setResourceAdaptor(this);

		appRouter = ((EnumResourceFactoryImpl) context.getResourceFactory())
				.getAppRouter();
		if (isInfoEnabled)
			logger.info("Leaving EnumResourceAdaptor init()");
	}

	@Override
	public void roleChanged(String clusterId, String subsystemId, short role) {
		boolean isInfoEnabled = logger.isInfoEnabled();
		if (isInfoEnabled)
			logger.info("Inside EnumResourceAdaptor roleChanged() to " + role);

		short preRole = this.role;
		this.role = role;
		if (logger.isDebugEnabled())
			logger.debug("is RA active::[" + raActive + "]");
		if (preRole != ROLE_ACTIVE && role == ROLE_ACTIVE && raActive) {
			try {
				this.start();
			} catch (Exception e) {
				logger.error("Exception in roleChanged(): ", e);
			}
		}
		if (isInfoEnabled)
			logger.info("Leaving EnumResourceAdaptor roleChanged()");
	}

	@Override
	public void sendMessage(SasMessage message) throws IOException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Inside SendMessage");
		try {
			if (message instanceof EnumResponse
					|| message instanceof EnumRequest) {
				if (this.canSendMessage) {
					// replicate((LsRequest)message);
					queueManager.enQueueMessage((EnumMessage) message);
				} else {
					if (isDebugEnabled)
						logger.debug("Peer is disconnected. cannot send response");
					EnumResourceEvent resourceEvent = new EnumResourceEvent(
							message, EnumResourceEvent.RESPONSE_FAIL_EVENT,
							message.getApplicationSession());
					resourceEvent.setMessage((EnumMessage) message);
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error(
								"Exception in delivering EnumResourceEvent", e);
						throw new EnumResourceException(e);
					}
				}
			} else {
				logger.error("Message dropped: not a EnumRequest.");
			}
		} catch (Exception e) {
			logger.error("sendMessage() failed ", e);
		}

		if (isDebugEnabled)
			logger.debug("Leaving SendMessage");

	}

	@Override
	public void start() throws ResourceException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		boolean isInfoEnabled = logger.isInfoEnabled();
		if (isInfoEnabled)
			logger.info("Inside EnumResourceAdaptor start()");
		raActive = true;
		if (this.role != EnumResourceAdaptor.ROLE_ACTIVE) {
			if (isInfoEnabled)
				logger.info("Standby...");
			return;
		}
		try {

			queueManager.load();
			if (isDebugEnabled)
				logger.debug("Queue Manager initialized, Queue Created");
			enumReceiver.start(this);
			this.raUp = true;
			this.canSendMessage = true;
			// send RA up event to app
			if (isDebugEnabled)
				logger.debug("sending RA up event");
			EnumResourceEvent resourceEvent = new EnumResourceEvent(
					"RA_UP_EVENT", EnumResourceEvent.RA_UP, null);
			try {
				deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering EnumResourceEvent", e);
				throw new EnumResourceException(e);
			}
			if (isDebugEnabled)
				logger.debug("RA UP event sent");
			// Notifying waiting threads...
			synchronized (this) {
				notifyAll();
			}
		} catch (Exception e) {
			logger.error("Exception while starting RA", e);
			throw (new EnumResourceException(e));
		}

		if (isInfoEnabled)
			logger.info("Leaving EnumResourceAdaptor start():: RA succesfully started");
	}

	@Override
	public void stop() throws ResourceException {
		boolean isDebugEnabled = logger.isDebugEnabled();
		boolean isInfoEnabled = logger.isInfoEnabled();
		if (isInfoEnabled)
			logger.info("Inside EnumResourceAdaptor stop()");
		raActive = false;
		try {
			if (this.raUp == true) {
				if (isDebugEnabled)
					logger.debug(" stoping RA ");
				// send RA- down event to app
				if (isDebugEnabled)
					logger.debug("sending RA down event");
				EnumResourceEvent resourceEvent = new EnumResourceEvent(
						"RA_DOWN_EVENT", EnumResourceEvent.RA_DOWN, null);
				try {
					deliverEvent(resourceEvent);
				} catch (ResourceException e) {
					logger.error("Exception in delivering EnumResourceEvent", e);
					throw new EnumResourceException(e);
				}
				if (isDebugEnabled)
					logger.debug("RA down event sent");
				this.canSendMessage = false;
				this.raUp = false;

				queueManager.destroy();
				enumReceiver.stop();
			}
		} catch (Exception ex) {
			logger.error("Exception while stopping RA", ex);
			throw new EnumResourceException(ex);
		}
		if (isInfoEnabled)
			logger.info("Leaving EnumResourceAdaptor stop():: RA successfully stopped");
	}

	@Override
	public void registerApp(DeployableObject ctx) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context= " + ctx);
		}
		appRouter.generateRules();
		this.appList.add(ctx);

	}

	@Override
	public void unregisterApp(DeployableObject ctx) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside unregisterApp() with context " + ctx);
		}
		appRouter.removeRulesForApp(ctx.getObjectName());
		this.appList.remove(ctx);
	}

	@Override
	public void deliverRequest(EnumRequest request) throws ResourceException {
		if (logger.isDebugEnabled())
			logger.debug("deliverRequest(): request " + request.getType());

		DeployableObject aseContext = null;
		SipApplicationSession appSession = null;
		EnumResourceSession enumSession = null;
		String protocol = Constants.PROTOCOL;

		aseContext = findApplication(request);
		logger.debug("AseContext found is " + aseContext);

		if (aseContext == null) {
			if (logger.isDebugEnabled())
				logger.debug("No Application Found for enum request" + request);
			return;
		}

		appSession = aseContext.createApplicationSession(protocol, null);
		enumSession = (EnumResourceSession) ((EnumResourceFactoryImpl) context
				.getResourceFactory()).createSession(appSession);

		logger.debug("deliverRequest(): enumSession is ." + enumSession.getId());

		// Server mode
		((EnumMessage) request).setProtocolSession(enumSession);

		if (logger.isDebugEnabled()) {
			logger.debug("deliverRequest(): setRequest. isInitial "
					+ ((EnumMessage) request).isInitial());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("deliverRequest(): set CDR ref on enum sesion");
		}
		enumSession.setAttribute(CDR.class.getName(), ((EnumMessage) request).getCDR());
		enumSession.setRequest((EnumRequestImpl) request);
		if (context != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("deliverRequest(): call context.");
			}
			enumReceiver.processRequest(request);
		} else {
			logger.error("deliverRequest(): could not deliver request to request handler.");

		}
	}

	@SuppressWarnings("unchecked")
	private DeployableObject findApplication(EnumRequest request) {
		DeployableObject ctx = null;
		String name = null;

		String appName = this.appRouter.getMatchingApp(request);
		if (appName == null) {
			return null;
		}

		for (Iterator apps = this.getAllRegisteredApps(); apps.hasNext();) {
			ctx = (DeployableObject) apps.next();
			// name = fixAppName(ctx.getName());
			name = ctx.getObjectName();
			if (logger.isDebugEnabled()) {
				logger.debug("Checking app " + name);
			}
			if (name.equals(appName)) {

				if (logger.isDebugEnabled()) {
					logger.debug("return app " + name);
				}
				return ctx;
			}
		}
		return null;
	}

	/*
	 * 
	 */
	public Iterator<DeployableObject> getAllRegisteredApps() {
		if (logger.isDebugEnabled()) {
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

	public static EnumResourceAdaptor getInstance()
			throws EnumResourceException {
		if (EnumResourceAdaptor == null) {
			logger.error("ERROR::::RA Object is null");
			throw new EnumResourceException(
					"ResourceAdaptorImpl Instance is null.");
		}
		return EnumResourceAdaptor;
	}

}
