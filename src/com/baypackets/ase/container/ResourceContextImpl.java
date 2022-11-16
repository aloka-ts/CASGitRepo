package com.baypackets.ase.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.measurement.ServiceMeasurementManager;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceFactory;
import com.baypackets.ase.resource.ResourceListener;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageCallback;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.ocm.OverloadManager;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.ResourceListenerProxy;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.spi.util.WorkManager;
import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.internalservices.AlarmService;

public class ResourceContextImpl extends AbstractDeployableObject 
									implements ResourceContext, SasMessageContext, 
									RoleChangeListener, AseEventListener{
	private static final long serialVersionUID = -3146342646478497222L;
	private static Logger logger = Logger.getLogger(ResourceContextImpl.class);
	private static final String DEFAULT_EVENT_ID = "DEFAULT".intern();
	
	private String adaptorClassName;
	private String resourceFactoryClassName;
	private String messageFactoryClassName;
	private String sessionFactoryClassName;
	private String measurementConfigFile;
	private String thresholdConfigFile;
	private String protocol;
		
	private AlarmService alarmService;
	private AgentDelegate agentDelegate;
	private AseHost host;
	private AseEngine engine;
	private CliInterface cliInterface;
	private OverloadManager overloadManager;
	private ServiceMeasurementManager measurementManager;
	private TimerService timerService;

	private ResourceAdaptor resourceAdaptor;
	private MessageFactory messageFactory;
	private SessionFactory sessionFactory;
	private ResourceFactory resourceFactory;
	
	private HashMap listenerProxyConfigMap = new HashMap();
	private HashMap listenerProxyMap = new HashMap();
	private ArrayList appList = new ArrayList();
	
	public void initialize() throws DeploymentFailedException{
		
		this.validate();
		
		try{
			ClassLoader loader = this.getClassLoader();
			Class adaptorClazz = Class.forName(this.adaptorClassName, true, loader);
			this.resourceAdaptor = (ResourceAdaptor) adaptorClazz.newInstance();
			
			Class msgFactoryClazz = Class.forName(this.messageFactoryClassName, true, loader);
			MessageFactory msgFactory = (MessageFactory) msgFactoryClazz.newInstance(); 
			this.messageFactory = new ProxyMessageFactory(msgFactory);
			
			Class sessionFactoryClazz = Class.forName(this.sessionFactoryClassName, true, loader);
			SessionFactory sessionFactory = (SessionFactory) sessionFactoryClazz.newInstance(); 
			this.sessionFactory = new ProxySessionFactory(sessionFactory);
			
			if(this.resourceFactoryClassName != null){
				Class resFactoryClazz = Class.forName(this.resourceFactoryClassName, true, loader);
				this.resourceFactory = (ResourceFactory) resFactoryClazz.newInstance();
			}else{
				this.resourceFactory = new DefaultResourceFactoryImpl();
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("resourceFactoryClassName = " + this.resourceFactoryClassName);
				logger.debug("resourceFactory = " + this.resourceFactory);
			}

			this.alarmService = (AlarmService) Registry.lookup(Constants.NAME_ALARM_SERVICE);
			this.agentDelegate = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
			this.host =(AseHost) Registry.lookup(Constants.NAME_HOST);
			this.engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
			this.cliInterface = (CliInterface)Registry.lookup(Constants.NAME_TELNET_SERVER);
			this.overloadManager = (OverloadManager)Registry.lookup(Constants.NAME_OC_MANAGER);
			this.measurementManager = new ServiceMeasurementManager(this.getObjectName());

			this.measurementManager.initialize(this.measurementConfigFile, this.thresholdConfigFile, this.getClassLoader());
			
			this.timerService = AseTimerService.instance();
			
			ClusterManager clusterManager = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
			clusterManager.registerRoleChangeListener(this, Constants.RCL_RESCTXT_PRIORITY);
			
			//Add the default event listener proxy.
			this.addListenerProxyConfig(DEFAULT_EVENT_ID, DefaultListenerProxy.class.getName(), ResourceListener.class.getName());
			
			//Call the init method on the resource adaptor.
			this.resourceAdaptor.init(this);
			
			//Initialize the Message Factory and the Session Factory instances.
			this.sessionFactory.init(this);
			this.messageFactory.init(this);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new DeploymentFailedException(e.getMessage(), e);
		}
	}
	
	private void validate() throws DeploymentFailedException{
		if(this.getClassLoader() == null){
			throw new DeploymentFailedException("Not able to get the class loader");
		}
		
		if(this.adaptorClassName == null){
			throw new DeploymentFailedException("Not able to get Resource Adaptor Class Name");
		}
		
		if(this.messageFactoryClassName == null){
			throw new DeploymentFailedException("Not able to get Message Factory Class Name");
		}
		
		if(this.sessionFactoryClassName == null){
			throw new DeploymentFailedException("Not able to get Session Factory Class Name");
		}
	}
	

	public void deploy() throws DeploymentFailedException {
		this.initialize();
		super.deploy();
	}
	
	
	public void undeploy() throws UndeploymentFailedException {
		ClusterManager clusterManager = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
		clusterManager.unregisterRoleChangeListener(this);
		super.undeploy();
	}
	
	public void activate() throws ActivationFailedException {
		try{
			this.resourceAdaptor.start();
		}catch(ResourceException e){
			throw new ActivationFailedException(e.getMessage(), e);
		}
		super.activate();
	}

	public void deactivate() throws DeactivationFailedException {
		try{
			this.resourceAdaptor.stop();
		}catch(ResourceException e){
			throw new DeactivationFailedException(e.getMessage(), e);
		}
		super.deactivate();
	}

	public AlarmService getAlarmService() {
		return this.alarmService;
	}

	public MeasurementManager getMeasurementManager() {
		return this.measurementManager;
	}

	public void updateConfigProperty(String name, String value) throws ResourceException {
		try{
			//ConfigurationDetail config = new ConfigurationDetail(name, value);
			//this.agentDelegate.modifyCfgParam(config);
		}catch(Exception e){
			throw new ResourceException(e.getMessage(), e);
		}
	}

	public SipApplicationSession getApplicationSession(String id) {
		return this.host.getApplicationSession(id);
	}

	public CliInterface getCliInterface() {
		return this.cliInterface;
	}

	public String getConfigProperty(String name) {
		ConfigRepository configDb = BaseContext.getConfigRepository();
		return configDb.getValue(name);
	}

	public short getCurrentRole() {
		String currRole = this.getConfigProperty(Constants.OID_CURRENT_ROLE);
		if((currRole == null) || currRole.equalsIgnoreCase("Active")) {
			return ResourceAdaptor.ROLE_ACTIVE;
		} else {
			return ResourceAdaptor.ROLE_STANDBY;
		}
	}

	public OverloadManager getOverloadManager() {
		return this.overloadManager;
	}

	public WorkManager getWorkManager() {
		return this.engine.getWorkManager();
	}

	public TimerService getTimerService() {
		return this.timerService;
	}

	public MessageFactory getMessageFactory() {
		return this.messageFactory;
	}

	
	public SasMessageCallback getMessageCallback() {
		if(this.resourceAdaptor instanceof SasMessageCallback){
			return (SasMessageCallback)this.resourceAdaptor;
		}
		return null;
		
	}

	public void sendMessage(SasMessage message) throws IOException {
		
		if(this.resourceAdaptor == null)
			return;
		try{
			this.fixMessage(message);
			this.resourceAdaptor.sendMessage(message);
		}catch(Exception e){
			logger.error("Sending Ro Message", e);
			throw new IOException(e.getMessage());
		}
	}

	public String getAdaptorClassName() {
		return adaptorClassName;
	}


	public void setAdaptorClassName(String adaptorClassName) {
		if(logger.isDebugEnabled()) {

		logger.debug("Inside setAdaptorClassName with "+adaptorClassName);
		}
		this.adaptorClassName = adaptorClassName;
	}


	public String getMeasurementConfigFile() {
		return measurementConfigFile;
	}


	public void setMeasurementConfigFile(String measurementConfigFile) {
		if(logger.isDebugEnabled()) {

		logger.debug("Inside setMeasurementConfigFile with "+measurementConfigFile);
		}
		this.measurementConfigFile = measurementConfigFile;
	}


	public String getMessageFactoryClassName() {
		return messageFactoryClassName;
	}


	public void setMessageFactoryClassName(String messageFactoryClassName) {
		this.messageFactoryClassName = messageFactoryClassName;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


	public String getResourceFactoryClassName() {
		return resourceFactoryClassName;
	}

	public void setResourceFactoryClassName(String resourceFactoryClassName) {
		this.resourceFactoryClassName = resourceFactoryClassName;
		if(logger.isDebugEnabled()) {
			logger.debug("set resourceFactoryClassName = " + this.resourceFactoryClassName);
		}
	}

	public String getSessionFactoryClassName() {
		return sessionFactoryClassName;
	}

	public void setSessionFactoryClassName(String sessionFactoryClassName) {
		this.sessionFactoryClassName = sessionFactoryClassName;
	}
	
	public String getThresholdConfigFile() {
		return thresholdConfigFile;
	}

	public void setThresholdConfigFile(String thresholdConfigFile) {
		this.thresholdConfigFile = thresholdConfigFile;
	}

	public ResourceFactory getResourceFactory() {
		if(logger.isDebugEnabled()) {
			logger.debug("returning resourceFactory = " + this.resourceFactory);
		}
		return resourceFactory;
	}

	/**
	 *	This method removes an <code>AseContext</code> from the list 
	 *	of AseContext which use this resource.This method is called
	 *	when an application is deactivated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void unregisterApp(AseContext ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside unregisterApp with context "+ctx);
		}
		this.appList.remove(ctx);
		if(logger.isDebugEnabled()) {

		logger.debug("registering with resource adaptor");
		}
		this.resourceAdaptor.unregisterApp(ctx);
	}

	/**
	 *	This method adds an <code>AseContext</code> to the list which 
	 *	uses this resource.This method is called when an application
	 *	is activated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void registerApp(AseContext ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context "+ctx);
		}
		this.appList.add(ctx);
		this.resourceAdaptor.registerApp(ctx);
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

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if( this.resourceAdaptor != null){
			this.resourceAdaptor.roleChanged(partitionInfo.getFip(), partitionInfo.getSubsysId(), partitionInfo.getRole());
		} 
	}
	
	public void configurationChanged(String oid, String value){
		try{
			this.resourceAdaptor.configurationChanged(oid, value);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	public void processMessage(SasMessage message) throws AseInvocationFailedException, ServletException {
		//NOOP.
	}

	public void deliverEvent(ResourceEvent event, boolean asynchronous) throws ResourceException {
		if(event == null){
			throw new IllegalArgumentException("The event object is NULL");
		}
		/*	
		if(event.getApplicationSession() == null){
			throw new IllegalArgumentException("The event object does not have an application session associated");
		}*/
		try{
			if(asynchronous){
				AseMessage msg = new AseMessage(event, this);
				int workQueue = this.getWorkQueue(event);
				if (workQueue != -1){
					msg.setWorkQueue(workQueue);
				}
				engine.handleMessage(msg);
			}else{
				engine.handleEvent(event, this);
			}
		}catch(Exception e){
			throw new ResourceException(e.getMessage(), e);
		}
	}

	public void deliverMessage(Message message, boolean asynchronous) throws ResourceException {
		if(!(message instanceof SasMessage)){
			throw new IllegalArgumentException("Message should implement the interface :" + SasMessage.class.getName());
		}
		this.fixMessage((SasMessage)message);
		
		try{
			if(asynchronous){
				AseMessage msg = new AseMessage((SasMessage)message);
				int workQueue = this.getWorkQueue(message);
				if (workQueue != -1){
					msg.setWorkQueue(workQueue);
				}
				engine.handleMessage(msg);
			}else{
				engine.processMessage((SasMessage)message);
			}
		}catch(Exception e){
			logger.error("In deliverMessage: ", e);
			throw new ResourceException(e.getMessage(), e);
		}
	}
	private int getWorkQueue (Message message){
		SipApplicationSessionImpl appSession = (SipApplicationSessionImpl) message.getApplicationSession();
		AseIc ic = null;
		if (appSession != null){
			ic = appSession.getIc();
			if (ic != null){
				if(logger.isDebugEnabled()){
					logger.debug("getWorkQueue(): Returning value from IC: " + ic.getWorkQueue());
				}
				return ic.getWorkQueue();
			}
		}
		return -1;
	}
	
	private int getWorkQueue (ResourceEvent event){
		SipApplicationSessionImpl appSession = (SipApplicationSessionImpl) event.getApplicationSession();
		AseIc ic = null;
		if (appSession != null){
			ic = appSession.getIc();
			if (ic != null){
				if(logger.isDebugEnabled()){
					logger.debug("getWorkQueue(): Returning value from IC: " + ic.getWorkQueue());
				}
				return ic.getWorkQueue();
			}
		}
		return -1;
	}
	public void handleEvent(EventObject eventObj) {
		if(!(eventObj instanceof ResourceEvent)){
			if(logger.isDebugEnabled()){
				logger.debug("Received an event other than Resource Event. Ignoring.....");
			}
			return;
		}
		
		ResourceEvent event = (ResourceEvent) eventObj;
		ListenerProxyConfig config = this.getListenerProxyConfig(event.getType());
		if(config == null){
			if(logger.isDebugEnabled()){
				logger.debug("Not able to get the listener proxy configuration for this event. Ignoring.....");
			}
			return;
		}
		
		ResourceListenerProxy proxy = (ResourceListenerProxy)this.listenerProxyMap.get(config.proxyClassName);
		if(proxy == null){
			if(logger.isDebugEnabled()){
				logger.debug("Not able to get the listener proxy instance for this event. Ignoring.....");
			}
			return;
		}
		try{
			AseContext context = null;
			SasApplicationSession appSession = (SasApplicationSession) event.
															getApplicationSession();
			if(appSession != null){
				context = (AseContext) appSession.getApplication();
				Class listenerClazz = Class.forName(config.listenerClassName, 
													true, context.getClassLoader());
				List list = context.getListeners(listenerClazz);
				EventListener[] listeners = new EventListener[list.size()];
				listeners = (EventListener[]) list.toArray(listeners);
				
				proxy.deliverEvent(this, event, listeners);
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("Delivering event to all registered apps if found one.");
				}
				Iterator itr = getAllRegisteredApps();
				if(!itr.hasNext()){
					logger.error("No application is registered for this resource");
					return;
				}
				while(itr.hasNext()){
					context = (AseContext)itr.next();
					Class listenerClazz = Class.forName(config.listenerClassName, 
														true, context.getClassLoader());
					List list = context.getListeners(listenerClazz);
					EventListener[] listeners = new EventListener[list.size()];
					listeners = (EventListener[]) list.toArray(listeners);
					
					proxy.deliverEvent(this, event, listeners);
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}		
	}
	
	public void addListenerProxyConfig(String eventId, String proxyClassName, String listenerClassName)
							throws Exception{
		if(eventId == null || proxyClassName == null || listenerClassName == null){
			return;
		}
		
		ListenerProxyConfig config = new ListenerProxyConfig();
		config.proxyClassName = proxyClassName;
		config.listenerClassName = listenerClassName;
		this.listenerProxyConfigMap.put(eventId, config);
		
		ResourceListenerProxy proxy = (ResourceListenerProxy)this.listenerProxyMap.get(proxyClassName);
		if(proxy == null){
			Class clazz = Class.forName(proxyClassName, true, this.getClassLoader());
			proxy = (ResourceListenerProxy) clazz.newInstance();
			this.listenerProxyMap.put(proxyClassName , proxy);
		}
	}
	
	public ListenerProxyConfig getListenerProxyConfig(String eventId){
		ListenerProxyConfig config = (ListenerProxyConfig)
								this.listenerProxyConfigMap.get(eventId);
		if(config == null){
			config = (ListenerProxyConfig) this.listenerProxyConfigMap.get(DEFAULT_EVENT_ID);
		}
		return config;
	}
	
	private void fixMessage(SasMessage message) throws ResourceException{
		if(message == null)
			return;
		message.setMessageContext(this);
		
		SasProtocolSession session = message.getProtocolSession();
		if(message.isInitial() || session == null)
			return;
		
		SasApplicationSession appSession = (SasApplicationSession)session.getApplicationSession();
		if(appSession == null){
			throw new ResourceException("Not able to get the application session for this subsequent message");
		}
		
		if(session.getHandler() == null){
			SasApplication application = appSession.getApplication();
			try{
				session.setHandler(application.getDefaultHandlerName(this.getObjectName()));
			}catch(ServletException e){
				throw new ResourceException(e.getMessage(), e);
			}
		}
	}

	public AseApplicationSession createApplicationSession(String protocol, String sessionId) {
		// no need to implement as this was added in the DeploableObject class only 
		// to create RA application router
		return null;
	}
	
	public static class ListenerProxyConfig {
		String proxyClassName;
		String listenerClassName;
	} 
	
	public static class DefaultListenerProxy implements ResourceListenerProxy {

		public DefaultListenerProxy(){}
		
		public void deliverEvent(ResourceContext context, ResourceEvent event, EventListener[] listeners) throws ResourceException {
			for(int i=0; i<listeners.length;i++){
				if(listeners[i] == null || !(listeners[i] instanceof ResourceListener))
					continue;
				ResourceListener listener = (ResourceListener) listeners[i];
				try{
					listener.handleEvent(event);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private class DefaultResourceFactoryImpl implements DefaultResourceFactory{
		
		public DefaultResourceFactoryImpl(){}
		
		public Request createRequest(int type) throws ResourceException {
			SasMessage message = getMessageFactory().createRequest(null, type);
			return (Request)message;
		}

		public ResourceSession createSession(SipApplicationSession appSession) throws ResourceException {
			
			if(appSession == null){
				throw new IllegalArgumentException("Application Session cannot be NULL ");
			}
			SasProtocolSession session = getSessionFactory().createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
			
			return (ResourceSession)session;
		}
		
	}
	
	private class ProxyMessageFactory implements MessageFactory{
		
		private MessageFactory factory;
		ProxyMessageFactory(MessageFactory factory){
			this.factory = factory;
		}
		
		public SasMessage createRequest(SasProtocolSession session, int type) throws ResourceException {
			SasMessage message = this.factory.createRequest(session, type);
			if(message != null){
				message.setMessageContext(ResourceContextImpl.this);
			}
			return message;
		}

		public SasMessage createResponse(SasMessage request, int type) throws ResourceException {
			SasMessage message = this.factory.createResponse(request, type);
			if(message != null){
				message.setMessageContext(ResourceContextImpl.this);
			}
			return message;
		}

		public void init(ResourceContext context) throws ResourceException {
			this.factory.init(context);
		}

		@Override
		public SasMessage createRequest(SasProtocolSession session, int type,
				String remoteRealm) throws ResourceException {
			// TODO Auto-generated method stub
			SasMessage message = this.factory.createRequest(session, type,remoteRealm);
			if(message != null){
				message.setMessageContext(ResourceContextImpl.this);
			}
			return message;
		}

		@Override
		public SasMessage createRequest(SasProtocolSession session, int type,
				String remoteRealm, String msisdn) throws ResourceException {
			// TODO Auto-generated method stub
			SasMessage message = this.factory.createRequest(session, type,
					remoteRealm, msisdn);
			if (message != null) {
				message.setMessageContext(ResourceContextImpl.this);
			}
			return message;
		}
	}
	
	private class ProxySessionFactory implements SessionFactory {

		private SessionFactory factory;
		ProxySessionFactory(SessionFactory factory){
			this.factory = factory;
		}
		
		public SasProtocolSession createSession() throws ResourceException {
			SasProtocolSession session = factory.createSession();
			return session;
		}

		public void init(ResourceContext context) throws ResourceException {
			this.factory.init(context);
		}
	}
}
