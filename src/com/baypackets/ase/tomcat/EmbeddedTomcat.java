/*
 * Created on Sep 2, 2004
 *
 */
package com.baypackets.ase.tomcat;

import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.ha.session.ClusterSessionListener;
import org.apache.catalina.ha.session.JvmRouteSessionIDBinderListener;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.JAASRealm;
import org.apache.catalina.startup.ConnectorCreateRule;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.group.interceptors.ThroughputInterceptor;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.transport.ReplicationTransmitter;
import org.apache.catalina.tribes.transport.nio.NioReceiver;
import org.apache.catalina.tribes.transport.nio.PooledParallelSender;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.IntrospectionUtils;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.InitializationFailedException;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.WebContainer;
import com.baypackets.ase.container.WebContainerState;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsagent.SoftShutdownInterface;



/**
 * This class manages an embedded instance of the Tomcat Servlet engine.
 *
 * @author Ravi
 */
public class EmbeddedTomcat implements WebContainer, SoftShutdownInterface {

	private static final Logger logger = Logger.getLogger(EmbeddedTomcat.class);

	private static final String ENGINE_NAME = "Catalina";
	private static final String HOST_NAME = "localhost";
	private static final String WEBAPP_BASE = "webapps";
	private static final String ROOT_CONTEXT = "ROOT";
	private static final String HTTP_LOGIN = "HttpLogin";
	private static final String GENERIC_PRINCIPAL = "org.apache.catalina.realm.GenericPrincipal";

	private SimpleTcpCluster cluster;
	protected static boolean convergedSessionRepEnable = false; 
	private boolean sharedExecutorEnabled=false;
	private Tomcat tomcatServer;
	private Engine engine;
	private Host host;
	private AseTomcatValve valve;
	private Connector httpConnector;
	private Connector httpsConnector;
	private int port = -1;
	private int httpsPort = -1;
	private String bindAddress;
	private WebContainerState state = WebContainerState.STOPPED;
	private ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

	private EmsAgent _emsAgent;

	/**
	 * Initializes the Tomcat server.
	 */
	public void initialize(Properties props) throws InitializationFailedException {    	
		// Get the bind address for Tomcat
		if(logger.isDebugEnabled()) {
			logger.debug("Entering initialize()");
		}
		this.bindAddress = AseUtils.getIPAddress(props.getProperty(Constants.OID_BIND_ADDRESS));
		if (this.bindAddress == null || this.bindAddress.trim().equals("")) {
			throw new InitializationFailedException("Bind Address for Tomcat cannot be NULL");
		}

		// Determine the port that the server will listen on
		String strPort = props.getProperty(Constants.OID_HTTP_CONNECTOR_PORT);
		
		/*
		 * in case of multiple http listening ports 
		 */
			String[] strports = null;
	
			Integer[] ports = null;
			if (strPort.indexOf(",") != -1) {
	
				strports = strPort.split(",");
	
				ports = new Integer[strports.length];
	
				for (int i = 0; i < strports.length; i++) {
	
					try {
						port = Integer.parseInt(strports[i]);
						ports[i] = port;
	
					} catch (NumberFormatException nfe) {
					}
	
					if (port < 0) {
						logger.error("Unable to get the value for HTTP Server Port :"
								+ strPort);
						throw new InitializationFailedException(
								"Unable to get the value for HTTP Server Port :"
										+ strPort);
					}
				}
			} else {
	
				try {
					this.port = Integer.parseInt(strPort);
				} catch (NumberFormatException nfe) {
				}
	
				if (port < 0) {
					logger.error("Unable to get the value for HTTP Server Port :"
							+ strPort);
					throw new InitializationFailedException(
							"Unable to get the value for HTTP Server Port :"
									+ strPort);
				}
			}
		if(logger.isDebugEnabled()) {
			logger.debug("Checking for NSEP enable flag");
		}
		//ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		int isCallPriorityEnabled = (int)Integer.parseInt(config.getValue(Constants.NSEP_CALL_PRIORITY_SUPPORTED));
		if(isCallPriorityEnabled == 1) {
			if(logger.isDebugEnabled()) {
				logger.debug("Going to set NSEP specific properties");
			}
			System.setProperty("NSEP_CALL_PRIORITY_SUPPORTED", Integer.toString(isCallPriorityEnabled));
			System.setProperty("NSEP_DSCP", config.getValue(Constants.NSEP_DSCP));
			System.setProperty("NSEP_HTTP_PORT", config.getValue(Constants.NSEP_HTTP_PORT));
			System.setProperty("NSEP_HTTP_THREAD_PRIORITY", config.getValue(Constants.NSEP_HTTP_THREAD_PRIORITY));
			System.setProperty("NSEP_MAX_HTTP_PRIORITY_THREADS" , config.getValue(Constants.NSEP_MAX_HTTP_PRIORITY_THREADS));
		}
		try {
			// Create the Tomcat container hierarchy
			tomcatServer = new Tomcat();
		// Getting Tomcat's Engine object
			engine = tomcatServer.getEngine();
			engine.setName(ENGINE_NAME);
			host=tomcatServer.getHost();
			valve = new AseTomcatValve();
			host.getPipeline().addValve(valve);
			host.setName(HOST_NAME);
			host.setAppBase(WEBAPP_BASE);
			//If converged session replication is enable then create cluster configuration else does nothing 
			String sessRepEnable = (String) config.getValue(Constants.CONVERGED_SESSION_REPLICATION_ENABLE);
			if(logger.isDebugEnabled()) 
			logger.debug("Converged session replication enable : " +sessRepEnable );
			if(sessRepEnable != null && ! AseStrings.BLANK_STRING.equals(sessRepEnable)) {
				if(AseStrings.TRUE_SMALL.equalsIgnoreCase(sessRepEnable.trim())) {
					convergedSessionRepEnable = true;
					this.createClusterConfigration(host);
				}
			}else{
				ConvergedStandardManagerImpl manager=new ConvergedStandardManagerImpl();
				host.setManager(manager);
			}
			engine.setDefaultHost(host.getName());
			
			// Create a ROOT Context for Tomcat			
			//tomcatServer.addContext(AseStrings.BLANK_STRING, ROOT_CONTEXT);
			
			MergedContext rootContext=new MergedContext();
			rootContext.setName(AseStrings.BLANK_STRING);
			rootContext.setPath(AseStrings.BLANK_STRING);
			rootContext.setDocBase(ROOT_CONTEXT);
			rootContext.addLifecycleListener(new FixContextListener());
			host.addChild(rootContext);
			
			
		//	 Create the HTTP connector and add it to the server
		//	InetAddress addr = null;
			
			if (ports == null) {
				
				httpConnector = tomcatServer.getConnector();
				httpConnector.setPort(port);
				httpConnector.setSecure(false);
				if (logger.isDebugEnabled()) {
					logger.debug("Inside Initialized : Connection TimeOut : "
							+ (String) config
									.getValue(Constants.TOMCAT_CONNECTION_TIMEOUT));
				}
				IntrospectionUtils.setProperty(httpConnector,
						"connectionTimeout", (String) config
								.getValue(Constants.TOMCAT_CONNECTION_TIMEOUT));

				// Determine the port for ssl that the server will listen on

				String strSSLPort = (String) config
						.getValue(Constants.SSL_CONNECTOR_PORT);
				try {
					this.httpsPort = Integer.parseInt(strSSLPort);
				} catch (NumberFormatException nfe) {
				}

				if (httpsPort < 0) {
					logger.error("Unable to get the value for HTTPS Server Port :"
							+ strSSLPort);
					throw new InitializationFailedException(
							"Unable to get the value for HTTPS Server Port :"
									+ strSSLPort);
				}
				// redirection from https to https
				String sslRedirection = (String) config
						.getValue(Constants.SSL_REDIRECTION_HTTP_TO_HTTPS);
				if (sslRedirection != null
						&& !AseStrings.BLANK_STRING.equals(sslRedirection)) {
					if (AseStrings.TRUE_SMALL.equalsIgnoreCase(sslRedirection
							.trim())) {
						httpConnector.setRedirectPort(httpsPort);
					}
				}
				
			//	httpConnector.setProperty("server", "TestEmbedded");
				IntrospectionUtils.setProperty(httpConnector, "server", "Apache-Coyote/1.1");
			//	IntrospectionUtils.setProperty(httpConnector, "org.apache.coyote.http11.Http11Protocol.SERVER","false");
			}else{ // for multiple ports need to add mutiple connectors
				
				for(int i=0;i<ports.length;i++){
					
					int port =ports[i];
					
					if(logger.isDebugEnabled()) 
						logger.debug("create http connector for listening port "+port);
					
					if (i == 0) {
						httpConnector = tomcatServer.getConnector();
					} else {
						httpConnector = new Connector();
					}
					httpConnector.setPort(port);
					httpConnector.setSecure(false);
					if (logger.isDebugEnabled()) {
						logger.debug("Inside Initialized : Connection TimeOut : "
								+ (String) config
										.getValue(Constants.TOMCAT_CONNECTION_TIMEOUT));
					}
					IntrospectionUtils.setProperty(httpConnector,
							"connectionTimeout", (String) config
									.getValue(Constants.TOMCAT_CONNECTION_TIMEOUT));
	
					// Determine the port for ssl that the server will listen on
	
					String strSSLPort = (String) config
							.getValue(Constants.SSL_CONNECTOR_PORT);
					try {
						this.httpsPort = Integer.parseInt(strSSLPort);
					} catch (NumberFormatException nfe) {
					}
	
					if (httpsPort < 0) {
						logger.error("Unable to get the value for HTTPS Server Port :"
								+ strSSLPort);
						throw new InitializationFailedException(
								"Unable to get the value for HTTPS Server Port :"
										+ strSSLPort);
					}
					// redirection from https to https
					String sslRedirection = (String) config
							.getValue(Constants.SSL_REDIRECTION_HTTP_TO_HTTPS);
					if (sslRedirection != null
							&& !AseStrings.BLANK_STRING.equals(sslRedirection)) {
						if (AseStrings.TRUE_SMALL.equalsIgnoreCase(sslRedirection
								.trim())) {
							httpConnector.setRedirectPort(httpsPort);
						}
					}
					
					if (i > 0) {
					//	httpConnector.setProperty("server", "Apache-Coyote/1.1");
						IntrospectionUtils.setProperty(httpConnector, "server", "Apache-Coyote/1.1");
					//	IntrospectionUtils.setProperty(httpConnector, "org.apache.coyote.http11.Http11Protocol.SERVER","false");
						if(logger.isDebugEnabled()) 
							logger.debug("adding connector for http "+tomcatServer.getService().getClass().toString() +" on port "+port);
						tomcatServer.getService().addConnector(httpConnector);
					}
				}
			}
			
			// ended connector addition
			
			//Creating HTTPS Connector 
			httpsConnector = new Connector();
			httpsConnector.setPort(httpsPort);
			httpsConnector.setSecure(true);
			httpsConnector.setScheme("https");
			IntrospectionUtils.setProperty(httpsConnector, "org.apache.coyote.http11.Http11Protocol.SERVER","false");
			IntrospectionUtils.setProperty(httpsConnector, "sslProtocol", "TLS");
			IntrospectionUtils.setProperty(httpsConnector, "clientAuth", "false");
			IntrospectionUtils.setProperty(httpsConnector, "SSLEnabled", "true");
			IntrospectionUtils.setProperty(httpsConnector, "keystorePass",(String) config.getValue(Constants.SSL_CERTIFICATE_KEYSTORE_FILE_PASSWORD));
			IntrospectionUtils.setProperty(httpsConnector, "keystoreType",(String) config.getValue(Constants.SSL_CERTIFICATE_KEYSTORE_FILE_TYPE));
			String fileName  = (String) config.getValue(Constants.SSL_CERTIFICATE_KEYSTORE_FILE_NAME);
			IntrospectionUtils.setProperty(httpsConnector, "keystoreFile",Constants.ASE_HOME+"/conf/"+fileName); 
			
			if(logger.isDebugEnabled()) 
				logger.debug("adding connector for https"+tomcatServer.getService().getClass().toString());
			
			
			httpsConnector.setProperty("server", "SecureEmbedded");
			IntrospectionUtils.setProperty(httpsConnector, "server", "Apache-Coyote/1.1");
			
			tomcatServer.getService().addConnector(httpsConnector); 					
			this.createThreadPoolConfiguration();
			JAASRealm jaasRealm = new JAASRealm();
			jaasRealm.setUseContextClassLoader(false);
			
			//using tomcat classes 
			jaasRealm.setUserClassNames(GENERIC_PRINCIPAL);
			jaasRealm.setRoleClassNames(GENERIC_PRINCIPAL);
			
			//will be used "HttpLogin" in credential file.
			jaasRealm.setAppName(HTTP_LOGIN);
			engine.setRealm(jaasRealm);		
			
			this.state = WebContainerState.INITIALIZED;
			//Register this object with EMSAgent so that a notification is received 
	        //to check whether shutdown is allowed after Softshutdown command is given
			_emsAgent = BaseContext.getAgent();
			if(_emsAgent!=null){
				_emsAgent.registerSoftShutdownInterface(this);
			}
			if(logger.isDebugEnabled()) 
				logger.debug("Tomcat server Initialized..engine. GenericPrincipal setAppName");
		} catch (Exception e) {
			String msg = "Error occurred while initializing the Tomcat Servlet engine: " + e.getMessage();
			logger.error(msg, e);
			throw new InitializationFailedException(msg);
		}
	}


	/**
	 * This method will be used to create Thread Pool configuration for tomcat connectors 
	 * @throws Exception
	 */
	private void createThreadPoolConfiguration() throws Exception {	
		this.sharedExecutorEnabled=Boolean.valueOf((String)config.getValue(Constants.TOMCAT_SHARED_EXECUTOR_ENABLED).trim());
		if(sharedExecutorEnabled){
			if(logger.isDebugEnabled())
			logger.debug("Creating shared executor for tomcat connectors:");
			int executorMinSpareThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_EXECUTOR_MIN_SPARE_THREADS).trim());
			int executorMaxThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_EXECUTOR_MAX_THREADS).trim());
	        StandardThreadExecutor executor=new StandardThreadExecutor();
	        executor.setName("tomcatThreadPool");
	        executor.setNamePrefix("catalina-exec-");
	        executor.setMinSpareThreads(executorMinSpareThreads);
	        executor.setMaxThreads(executorMaxThreads);
	        tomcatServer.getService().addExecutor(executor);
	        ConnectorCreateRule rule=new ConnectorCreateRule();
	        rule._setExecutor(httpConnector, executor);
	        rule._setExecutor(httpConnector, executor);
	        if(logger.isDebugEnabled())
	        	logger.debug("Created shared executor for tomcat connectors with Min:"+executorMinSpareThreads+" Max:"+executorMaxThreads+" threads");
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Creating executor for tomcat connectors as sharedExecutorEnabled is:"+sharedExecutorEnabled);
			int httpMinSpareThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_HTTP_CONNECTOR_MIN_SPARE_THREADS).trim());	
			int httpMaxThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_HTTP_CONNECTOR_MAX_THREADS).trim());
			
			int httpsMinSpareThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_HTTPS_CONNECTOR_MIN_SPARE_THREADS).trim());	
			int httpsMaxThreads=Integer.parseInt((String)config.getValue(Constants.TOMCAT_SHARED_HTTPS_CONNECTOR_MAX_THREADS).trim());
			
			IntrospectionUtils.setProperty(httpConnector, "minSpareThreads", httpMinSpareThreads+"");
			IntrospectionUtils.setProperty(httpConnector, "maxThreads", httpMaxThreads+"");
			
			IntrospectionUtils.setProperty(httpsConnector, "minSpareThreads", httpsMinSpareThreads+"");
			IntrospectionUtils.setProperty(httpsConnector, "maxThreads", httpsMaxThreads+"");
			if(logger.isDebugEnabled()){
				logger.debug("Created executor for tomcat http connectors with Min:"+httpMinSpareThreads+" Max:"+httpMaxThreads+" threads");
				logger.debug("Created executor for tomcat https connectors with Min:"+httpsMinSpareThreads+" Max:"+httpsMaxThreads+" threads");
		}
		}
	}


	public void createClusterConfigration(Host host) {
		if(logger.isDebugEnabled())
			logger.debug("Entering createClusterConfigration of Embedded Tomcat...");
		cluster = new ConvergedMcastServiceImpl();
		host.setCluster(cluster);
		this.configureCluster();
		if(logger.isDebugEnabled())
			logger.debug("Leaving createClusterConfigration of Embedded Tomcat...");
	}

	private void configureCluster() {
		if(logger.isDebugEnabled())
			logger.debug("Entering configureCluster of Embedded Tomcat...");
		
		ConvergedDeltaManagerImpl manager=ConvergedDeltaManagerImpl.getInstance();
		manager.setName("DeltaManager");
		manager.setNotifyListenersOnReplication(Boolean.valueOf((String)config.getValue(Constants.CONVERGED_SET_NOTIFYLISTENERS_ONREPLICATION)));
		manager.setNotifySessionListenersOnReplication(Boolean.valueOf((String)config.getValue(Constants.CONVERGED_SET_NOTIFYSESSIONLISTENERS_ONREPLICATION)));
		//This manager template will be used if no manager has been specified for the webapp, and the webapp is marked <distributable/> 
		//Tomcat will take this manager configuration and create a manager instance cloning this configuration. 
		cluster.setManagerTemplate(manager);
		cluster.addValve( new ReplicationValve());
		cluster.addClusterListener( new ClusterSessionListener());
		cluster.addClusterListener(new JvmRouteSessionIDBinderListener());
		configureChannel(cluster);
		if(logger.isDebugEnabled())
			logger.debug("Leaving configureCluster of Embedded Tomcat...");
	}
	private void configureChannel(SimpleTcpCluster cluster){
		GroupChannel channel=new GroupChannel();
		channel.setChannelSender(createClusterSender());
		channel.setChannelReceiver(createClusterReceiver());
		channel.setMembershipService(createMembershipService());
		//Order of Interceptor is important as per tomcat 7 clustering how section
		channel.addInterceptor(new TcpFailureDetector());
		channel.addInterceptor(new MessageDispatch15Interceptor());
		channel.addInterceptor(new ThroughputInterceptor());
		cluster.setChannel(channel);
	}

	private McastService createMembershipService() {
		if(logger.isDebugEnabled())
			logger.debug("Entering createMembershipService of Embedded Tomcat...");
		McastService result = new McastService();
		result.setAddress((String)config.getValue(Constants.CONVERGED_SET_MCASTADDRESS));
		result.setPort(Integer.parseInt((String)config.getValue(Constants.CONVERGED_SET_MCASTPORT)));
		result.setFrequency(Long.parseLong((String)config.getValue(Constants.CONVERGED_SET_MCASTFREQUENCY)));
		result.setDropTime(Long.parseLong((String)config.getValue(Constants.CONVERGED_SET_MCASTDROPTIME)));
		if(logger.isDebugEnabled())
			logger.debug("Leaving createMembershipService of Embedded Tomcat...");
		return result;
	}

	private NioReceiver createClusterReceiver() {
		if(logger.isDebugEnabled())
			logger.debug("Entering createClusterReceiver of Embedded Tomcat...");
		NioReceiver receiver = new NioReceiver();
		receiver.setAddress((String)config.getValue(Constants.CONVERGED_SET_TCP_LISTENADDDRESS));
		receiver.setPort(Integer.parseInt((String)config.getValue(Constants.CONVERGED_SET_TCP_LISTENPORT)));
		receiver.setSelectorTimeout(Long.parseLong((String)config.getValue(Constants.CONVERGED_SET_TCP_SELECTOR_TIMEOUT)));
		receiver.setMaxThreads(Integer.parseInt((String)config.getValue(Constants.CONVERGED_SET_TCP_THREADCOUNT)));
		if(logger.isDebugEnabled())
			logger.debug("Leaving createClusterReceiver of Embedded Tomcat...");
		return receiver;
	}

	private ReplicationTransmitter createClusterSender() {
		if(logger.isDebugEnabled())
			logger.debug("Entering createClusterSender of Embedded Tomcat...");
		ReplicationTransmitter result = new ReplicationTransmitter();
		result.setTransport(new PooledParallelSender());
		if(logger.isDebugEnabled())
			logger.debug("Leaving createClusterSender of Embedded Tomcat...");
		return result;
	} 

	/**
	 * Starts up the Tomcat server
	 */
	public void start() throws StartupFailedException {
		try {
			if(logger.isDebugEnabled())
				logger.debug("Going to start Tomcat server");
			tomcatServer.start();
			this.state = WebContainerState.RUNNING;
		} catch (Exception e) {
			String msg = "Error occurred while starting up the Tomcat server: " + e.getMessage();
			logger.error(msg, e);
			throw new StartupFailedException(msg);
		}
	}


	/**
	 * Shuts down the Tomcat server
	 */
	public void stop() throws ShutdownFailedException {
		try {
			tomcatServer.stop();
			this.state = WebContainerState.STOPPED;
		} catch (Exception e) {
			String msg = "Error occurred while shutting down the Tomcat server: " + e.getMessage();
			logger.error(msg, e);
			throw new ShutdownFailedException(msg);
		}
	}


	/**
	 * Returns the current state of this component.
	 */
	public WebContainerState getState() {
		return this.state;
	}


	public ClassLoader getClassLoader() {
		return null;
	}


	/**
	 * Deploys an application into Tomcat. 
	 */
	public void deploy(String contextPath, AseContext aseContext) throws DeploymentFailedException{
		 if (!contextPath.startsWith(AseStrings.SLASH)) {
			contextPath = AseStrings.SLASH + contextPath;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("deploy(): Deploying web app: " + contextPath);
		}
		Context ctx = this.createContext(contextPath, aseContext);
		Loader loader = this.createLoader(aseContext.getClassLoader());
		ctx.setLoader(loader);	
		this.host.addChild(ctx);
		if (!ctx.getAvailable()) {
			throw new DeploymentFailedException("Error occured while starting web app.  See error log for details.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("deploy(): Web app started successfully.");
		}
	}    


	/**
	 * Upgrades the web application specified by "appName".
	 */
	public void upgrade(String contextPath, AseContext context) throws UpgradeFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Upgrading web application, " + contextPath );
		}
		try {
			this.undeploy(contextPath);
			this.deploy(contextPath , context);
		} catch (Exception e) {
			String msg = "Error occured while upgrading web application: " + contextPath + ": " + e.toString();
			logger.error(msg, e);
			throw new UpgradeFailedException(msg);
		}
	}


	/**
	 * Undeploys the specified application from Tomcat.
	 */
	public void undeploy(String contextPath) throws UndeploymentFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Undeploying web application, " + contextPath + " from Tomcat.");
		}

		if (!contextPath.startsWith(AseStrings.SLASH)) {
			contextPath = AseStrings.SLASH + contextPath;
		}
		Context ctx = (Context)this.host.findChild(contextPath);
		this.host.removeChild(ctx); 
	}


	/**
	 * Indicates whether the specified application is deployed in Tomcat.
	 */
	public boolean isDeployed(String appName) {
		if (!appName.startsWith(AseStrings.SLASH)) {
			appName = AseStrings.SLASH + appName;
		}
		return this.host.findChild(appName) != null;
	}


	/**
	 * Creates a Context object for an application to be deployed into Tomcat.
	 */
	private Context createContext(String path, AseContext aseContext) {
		if(logger.isDebugEnabled()) {
			logger.debug("Creating Tomcat Context: " + path);
		}

		MergedContext context = new MergedContext(aseContext);
		context.setDocBase(aseContext.getUnpackedDir().getAbsolutePath());
		
		
		String servCtxName=aseContext.getServletContextName();
		
		if(logger.isDebugEnabled()) {
			logger.debug("set Tomcat Context path as servlet context name if available : " + servCtxName);
		}
		
		if (servCtxName != null) {
			context.setPath(servCtxName);
			aseContext.setContextPath(servCtxName);
		} else {
			context.setPath(path);
		}
		ContextConfig config = new ContextConfig();	
		((Lifecycle) context).addLifecycleListener(config);		
		return (context);
	}

	/**
	 * Returns the class loader to be used by the web app.
	 */
	private Loader createLoader(ClassLoader parent) {
	    WebappLoader loader = new WebappLoader(parent);
	    if(logger.isDebugEnabled()){
	    	logger.debug("create classLoader: Loader = "+loader);
	    	logger.debug("create classLoader: Parent = "+parent);
	    }
		loader.setDelegate(true);
		return loader;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.container.WebContainer#softStop()
	 * After receiving the softstop command sets the reject parameter
	 * as true which ensures the all incoming HTTP requests are rejected.
	 */
	public void softStop() {
		logger.debug("Inside Soft Stop : Embedded Tomvat set valve as true");
		valve.setReject(true);
	}


	@Override
	/*
	 * EMS Agent invokes this method to determine if it can continue
	 with shutdown process as part of soft shutdown call.
	 This method ,particularly, checks whether there are any pending
	 HTTP requests before giving a call for Shutdown
	 */
	public boolean isShutdownAllowed() throws Exception {

		long activeHttpSessions = AseMeasurementUtil.counterActiveHttpSessions.getCount();
		int pendingHttpRequests = valve.getPendingHttpRequests();

		if(activeHttpSessions > 0 || pendingHttpRequests>0){
			if(logger.isDebugEnabled()){
				logger.debug(activeHttpSessions + " Active HTTP Sessions and " +  
						pendingHttpRequests+" Pending HTTP Requests are there. Shutdown not allowed" );
			}
			return false;
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("No Active HTTP Sessions or Requests. Shutdown allowed" );
			}
			return true;
		}
	}
}

