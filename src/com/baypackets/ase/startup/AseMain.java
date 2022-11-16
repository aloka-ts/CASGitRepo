/*
 * AseMain.java
 *
 * Created on August 7, 2004, 4:13 PM
 */
package com.baypackets.ase.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Permission;
import java.security.Policy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

import com.agnity.oems.agent.messagebus.OEMSServiceStarter;
import com.agnity.oems.agent.messagebus.dto.OemsInitDTO;
import com.agnity.oems.agent.messagebus.enumeration.ComponentType;
import com.agnity.oems.agent.messagebus.enumeration.MessageBusType;
import com.agnity.oems.agent.messagebus.enumeration.MessageType;
import com.agnity.oems.agent.messagebus.meascounters.OemsAgent;
import com.agnity.oems.agent.messagebus.utils.OemsUtils;
import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.client.utility.RedisClientOptions;
import com.agnity.redis.connection.RedisConnectionInfo;
import com.agnity.redis.policy.RedisConnectionPolicy;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.logging.LoggingHandler;
import com.baypackets.ase.common.logging.RepositorySelectorImpl;
import com.baypackets.ase.common.logging.SelectiveLoggerFactory;
import com.baypackets.ase.container.SysAppDeployer;
import com.baypackets.ase.control.AseRedisHearbeatUpdater;
import com.baypackets.ase.sbbdeployment.SBBDeployerComponent;
import com.baypackets.ase.security.SasPolicy;
import com.baypackets.ase.servicemgmt.ComponentDeploymentStatus;
import com.baypackets.ase.servicemgmt.SasServiceManager;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AsePing;
import com.baypackets.ase.util.AseRexecPrintStream;
import com.baypackets.ase.util.AseRexecRollover;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTUIService;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.DOMUtils;
import com.baypackets.ase.util.EvaluationVersion;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.ase.util.consumer.OemsMessageConsumer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.internalservices.BpSubsysTypeCode;
import com.baypackets.bayprocessor.slee.internalservices.SleeDbAccessService;
import com.baypackets.bayprocessor.slee.internalservices.TraceLevel;
import com.baypackets.bayprocessor.slee.internalservices.TraceServiceImpl;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsagent.GenericComponentManager;
import com.baypackets.emsliteagent.EmsLiteAgent;


/**
 * This class contains the main method used to bootstrap the Agility 
 * Servlet Engine (ASE).
 *
 */
public final class AseMain {
    
    private static Logger logger = Logger.getLogger(AseMain.class);
    private static StringManager _strings = StringManager.getInstance(AseMain.class.getPackage());
    
    //Subsystem type for ASE.
    private static final BpSubsysTypeCode SUBSYS_TYPE = BpSubsysTypeCode.SubsysTypeCode_Spsi;
    
    private GenericComponentManager componentManager = null;
    private ConfigRepository configRepository = null;
    private EmsAgent agent = null;
    private boolean started = false;
    private String[] args = new String[0]; 
    
    private boolean stopped = false;
    private boolean emsAgentReqd = false;
    private boolean emslAgentReqd = false;
    
    //Adding EMSLite agent
    private EmsLiteAgent emslAgent = null;
    private int tomcatReqd = Constants.WEB_CONTAINER_REQUIRED;
    private Object waitObject = new Object();
    private Map<Short,List<AseStartUpListener>> listeners =new TreeMap<Short,List<AseStartUpListener>>();
    private ComponentDeploymentStatus compDeploymentStatus = new ComponentDeploymentStatus();
	private String siteId;
	private boolean subscribeconfigChannel=true;
    
    protected static String propertyHashes=null;
    
    private static String selfSubsystemName=null;
    
    public static AseMain startup=null;
    
    protected static String propertyChannelName="CONFIG_CHANNEL";
    public static String standbyNotiChannelName="STANDBY_ROLE_NOTIFY_CHANNEL";
    
    /**
     * Private constructor.
     *
     */
    public AseMain() {        
    }
    
    private void validate(String[] args) throws Exception{
		//Get the location of the install root directory from the System properties
		if(Constants.ASE_HOME == null){
			throw new Exception(_strings.getString("AseMain.noHome"));
		}
		
		// check for existence of specified installation root directory
		File installRoot = new File(Constants.ASE_HOME);            
		if (!(installRoot.exists() && installRoot.isDirectory())) {
			throw new Exception(_strings.getString("AseMain.noInstallRoot", Constants.ASE_HOME));
		}
		
		//check for the existence of the XML configuration file
		File propFile = new File(Constants.ASE_HOME, Constants.FILE_PROPERTIES);            
		if (!propFile.exists()) {
			throw new Exception(_strings.getString("AseMain.noPropertiesFile"));
		}
	}
    
    
    /**
     * Performs initialization of the application using the given arguments.
     *
     */
    private void initialize (String[] args) throws Exception{
		//Check whether the ems agent initialization required.
		String strReqd = System.getProperty(Constants.PROP_IS_AGENT_REQD);
		//Changes for EMS-Lite
		String emsLiteReqd = System.getProperty(Constants.PROP_IS_WEMS_AGENT_REQD);
		this.emsAgentReqd = (strReqd == null) ? false : strReqd.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL);
		this.emslAgentReqd = (emsLiteReqd == null) ? false : emsLiteReqd.trim().equalsIgnoreCase("true");
		//Initialize the logger, so that it can log any log messages.
		this.initializeLogger();
		
		//Print some server information.
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Starting the ASE Server at " + new Date());
			logger.info("Ase Home :" + Constants.ASE_HOME);
			logger.info("EMS Managed :" + this.emsAgentReqd);
			logger.info("wEMS Managed :" + this.emslAgentReqd);
		}
		
		if (EvaluationVersion.FLAG) {
			logger.error("RUNNING EVALUATION VERSION OF SAS");

			if (this.emsAgentReqd) {
				logger.error("SAS evaluation version cannot be run with EMS");
				throw new Exception("EMS interface is not supported!!!");
			}
		} else {
			logger.error("RUNNING STANDARD VERSION OF SAS");
		}

		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Initializing the properties");
		}
		
		//registering oemsAgentLibrary
		logger.info("Oems Agent library register started");
		OEMSServiceStarter oemsAgentInstence = getOemsAgentWrapper();
		Registry.bind(Constants.OEMS_AGENT_WRAPPER, oemsAgentInstence);

		
		AseTUIService aseTUIService = AseTUIService.getTUIInstence();
	    Registry.bind(Constants.TUI_SERVICE_INSTANCE, aseTUIService);
	
		RedisWrapper redisWrapper=createRedisWrapper();
		Registry.bind(Constants.REDIS_WRAPPER, redisWrapper);
		
		
		
		OemsAgent oemsAgent  = new OemsAgent();
		Registry.bind(Constants.OEMS_AGENT, oemsAgent);
		
		String propYmlPath = Constants.ASE_HOME + File.separator
				+ Constants.FILE_PROPERTIES;
		//create a temp string array for holding the arguments.
		String[] temp = new String[2];
		
		//Set the properties file name to the arguments list
		temp[0] = "-p";
		temp[1] =propYmlPath;// Constants.ASE_HOME + File.separator + Constants.FILE_PROPERTIES;
		String propYmlpath=temp[1];
		this.args = AseUtils.joinArray(this.args, temp);
			
		//Set the sub-system ID and name if not starting through EMS.
		if (!this.emsAgentReqd && !this.emslAgentReqd) {
			temp = new String[4];
			temp[0] = "-a";
			temp[1] = AseStrings.BLANK_STRING+Constants.DEFAULT_SUBSYS_ID;
			temp[2] = "-s";
			temp[3] = Constants.DEFAULT_SUBSYS_NAME;
			this.args = AseUtils.joinArray(this.args, temp);
		}
			
		//Check whether the HConfig file is available or not.
		File hConfigFile = new File(Constants.ASE_HOME, Constants.FILE_HCONFIG);            
		if(this.emsAgentReqd && !hConfigFile.exists()){
			throw new Exception(_strings.getString("AseMain.noHConfigFile"));
		}
		
		//Set the HConfig File Name to the arguments list
		if (hConfigFile.exists()) {
			temp = new String[2];
			temp[0] = "-z";
			temp[1] = Constants.ASE_HOME + File.separator + Constants.FILE_HCONFIG;
			this.args = AseUtils.joinArray(this.args, temp);
		}

    	
    	//Now append the actual args passed.
		this.args = AseUtils.joinArray(this.args, args);
		
//		//Create the necessary directories.
//		this.createDirs();
		 
    	//Create the Component Manager and bind it to the registry
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Creating and initializing the component manager...");
		}
    	this.componentManager = new GenericComponentManager();
    	componentManager.initialize(this.args);
    	Registry.bind(Constants.NAME_COMPONENT_MANAGER, this.componentManager);
    	
		//Get the config repository and bind it to the registry
		this.configRepository = componentManager.getConfigRepository();
		
		
		
		BaseContext.setConfigRepository(this.configRepository);
		Registry.bind(Constants.NAME_CONFIG_REPOSITORY, this.configRepository);
		
		
		AseSystemOutLogger sysoutLogger = new AseSystemOutLogger(
				"AseSystemOutTimeLogger");
		sysoutLogger.start();
		
		
		 siteId=fetchSiteId();
		configRepository.setValue(Constants.CAS_SITE_ID, siteId);
		
		/**
		 * parse instance specific properties
		 */
		parseInstanceProperties();
		
//		String ase_redis = Constants.ASE_HOME + File.separator
//				+ Constants.FILE_ASE_REDIS_YML;
		
		//putConfiguratioinDb(redisWrapper,ase_redis);
		
		readConfigurationFromRedisDB(redisWrapper);
		
		//Create the necessary directories.
		this.createDirs();

		//Check whether tomcat is required or not.
		try{
			String strTomcatReqd = this.configRepository.getValue(Constants.PROP_HTTP_CONTAINER_REQD);
			strTomcatReqd = (strTomcatReqd == null) ? AseStrings.BLANK_STRING : strTomcatReqd.trim();
			this.tomcatReqd = Integer.parseInt(strTomcatReqd);
		}catch(NumberFormatException nfe){
			logger.warn("Using the Default value. Ignoring the NumberFormatException while parsing property : " +Constants.PROP_HTTP_CONTAINER_REQD);
			logger.error(nfe.getMessage(), nfe);
		}
		
    	//Get the components
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Creating the components specified in the server-config.xml");
		}
		Object[] components = this.createComponents();
		
		//Initialize the agent if required.
		if(this.emsAgentReqd){
			this.initializeAgent();
		}
		//Initialize the EMS Lite Agent, 
		if(this.emslAgentReqd){
			this.initializeEMSLAgent();
		}

		 //Check if Reference IP address is pingable, if not then it is network isolation
                // so initiate a normal shutdown
                String refIps = AseUtils.getIPAddressList(configRepository.getValue(Constants.OID_REFERENCE_IP),false);
                
            //    String[] ref_ips = refIp.split(",");
                AsePing.setTcapPingFlag();
                
		//	for (int j = 0; j < ref_ips.length; j++) {
				
				if (AsePing.ping(refIps) != true) {
					if (logger.isInfoEnabled()) {
						logger.info("Network Isolation");
						logger.info("shutdown the SAS instance and exit JVM ");
					}
	
					System.exit(1);
				}
		//	}
	
		
		//Register the components with the component manager if they are MComponent.
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Registering the components with component manager...");
		}
    	for(int i=0; components != null && i<components.length;i++){
    		if(components[i] == null)
    			continue;
			
			if(components[i] instanceof MComponent){
				this.componentManager.registerComponent((MComponent)components[i], i);
			}
    	}
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Registered the components with component manager...");
		}
		    	
    	//Change the component manager state to loaded.
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Changing the components state to loaded");
		}
		MComponentState loaded = new MComponentState(MComponentState.LOADED);
		if(logger.isDebugEnabled())
		logger.debug("Changign the component State");
		this.componentManager.changeState(loaded);
		if(logger.isDebugEnabled())
		logger.debug("Component state changed");
		if(this.agent != null){
			//Change the agents state to LOADED. 
			//This will report loaded state to the EMS
			if(logger.isDebugEnabled())
			logger.debug("Going to call EMS");
			agent.changeComponentState(loaded.getValue());
		}
		if(this.emslAgent != null){
			if (logger.isEnabledFor(Level.DEBUG)){
				logger.debug("Going to call wEMS");
			}
			this.emslAgent.changeComponentState(loaded.getValue());
		}
		
		//Register the commands with the telnet server...
		if(logger.isDebugEnabled())
		logger.debug("Going to register commands");
		this.registerCommands();

		
		
 		// Install the SAS security policy for the entire JVM.
 		SasPolicy policy = (SasPolicy)Registry.lookup(Constants.NAME_SAS_SECURITY_POLICY);
                
 		if (policy == null) {
 			logger.warn("No security policy defined for SAS.");
 		} else {                
 			Policy.setPolicy(policy);
                 
 			if (logger.isDebugEnabled()) {
 				logger.debug("Successfully installed SAS security policy.");
 			}
 		}
 		
		if (subscribeconfigChannel) {
			propertyChannelName = selfSubsystemName + "." + propertyChannelName;

			try {

				if (logger.isDebugEnabled()) {
					logger.debug("subscribe channel for config ."
							+ propertyChannelName);
				}

				redisWrapper.getPubSubOperations().subscribe(
						new AsePubSubPropsListener(propertyChannelName,
								components, redisWrapper), propertyChannelName);
			} catch (Exception e) {
				logger.error(
						"could not subscribe channel " + standbyNotiChannelName
								+ " or " + propertyChannelName, e);
			}
		}else{
			
			if (logger.isDebugEnabled()) {
				logger.debug("Donot subscribe channel for config channel it is disabled .");
			}
		}
		
		/**
		 * below channel will be used by CAS to subscribe this channel if role will be active for this cAS. 
		 * this channel name is used bye ClusterManager
		 * 
		 */
		standbyNotiChannelName = siteId + "."+standbyNotiChannelName;
		

		if (logger.isEnabledFor(Level.INFO)){
			logger.info("ASE Server initialization completed...");
		}
	}
    
    
	private void putConfigurationinDb(RedisWrapper redisWrapper,
			String yamlPropFile) {

		if (logger.isDebugEnabled()) {
			logger.debug("putConfigurationinDb ..." + yamlPropFile);
		}
		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(yamlPropFile))) {

			TreeMap<String, String> config = yaml.loadAs(in, TreeMap.class);

			for (String key : config.keySet()) {

				// System.out.println(" key "+ key+" value "+config.get(key));
				redisWrapper.getHashOperations().addInHashes(
						selfSubsystemName + ":config", key, config.get(key));

				if (logger.isDebugEnabled()) {
					logger.debug("Setting value reading from properties " + key
							+ " : " + config.get(key));
				}
			}

		} catch (Exception e) {
			logger.error("exception occured");
			e.printStackTrace();
		}

	}
    
    /**
     * read configuratoon from data base
     * @param redisWrapper
     */
	private void readConfigurationFromRedisDB(RedisWrapper redisWrapper) {
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("readConfigurationFromRedisDB..from  "+ propertyHashes +" For selfSubsystemName "+ selfSubsystemName);
		}
		
		String readConfigEnabled = BaseContext.getConfigRepository().getValue(
				Constants.PROP_REDIS_CONFIG_ENABLED);
		boolean readConfig = false;
		if ("1".equals(readConfigEnabled)) {
			readConfig = true;
		}
			
		if (readConfig && selfSubsystemName != null&& !selfSubsystemName.isEmpty()) {
			
			try{
			Map<String, String> aseYmlProp = redisWrapper.getHashOperations()
					.getAllHashes(propertyHashes);

			if (aseYmlProp != null && !aseYmlProp.isEmpty()) {
				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("readConfigurationFromRedisDB found properties from redis ..reading it");
				}
				
				Iterator<String> keys = aseYmlProp.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = aseYmlProp.get(key);
					BaseContext.getConfigRepository().setValue(key, value);
					  if (logger.isEnabledFor(Level.DEBUG)) {
				            logger.debug("readConfigurationFromRedisDB reading from redis -->ase property key  = "+ key +" value " + value );
				        }
						
				}
			}else{
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("readConfigurationFromRedisDB "+ propertyHashes +"  is empty ..");
				}
				
			}
			}catch(Exception e){
				
				logger.error(" Exception thrown on reading prop from redis "+ e);
			}
		}
	}

	/**
     * Initializes the log4j logging utility.
     *
     */
    private void initializeLogger() throws Exception {
        LoggerFactory factory = new SelectiveLoggerFactory();
        
        LogManager.setRepositorySelector(new RepositorySelectorImpl(factory) , factory);

        File log4jConfigFile = new File(Constants.ASE_HOME, Constants.FILE_LOG_CONFIG);

        if (log4jConfigFile.exists() && log4jConfigFile.isFile()) {
            // Initialize the logger from XML configuration file 
            DOMConfigurator.configure(log4jConfigFile.getAbsolutePath());
        } else {
            // If no config file was found, use the basic configuration
            BasicConfigurator.configure();
	}
    }
    
    /**
     * Initializes the EMS Lite agent to facilitate communication with the EMS Lite
     * management application.
     *
     */
    private void initializeEMSLAgent() throws Exception{    	
        if (logger.isEnabledFor(Level.INFO)) {
            logger.info("Initializing the wEMS Agent ....");
        }
    	
        this.emslAgent = new EmsLiteAgent();
        // Get reference to thread monitor from registry
		ThreadMonitor thMonitor =
					(ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

		this.emslAgent.initialize(this.componentManager, this.configRepository,
				this.args, thMonitor, AseThreadMonitor.getThreadTimeoutTime());

        // Get the new configuration list from the EMS
		Pair[] pairs = emslAgent.getConfigParams();
        
		// Set soft shutdown listener
//		agent.registerSoftShutdownInterface(new SoftShutdownInterface() {
//				public boolean isShutdownAllowed() {
//					return 0 >= AseMeasurementManager.instance().getCounter("Number of Active Application Sessions").getCount();
//				}
//			});

        if (logger.isEnabledFor(Level.DEBUG)) {
            logger.debug("No. of parameters retrieved from BayManager = "+ pairs.length);
        }
		
        // Update the config repository with the new values from EMS.
        this.componentManager.setValue(pairs);
        // Set the Agent Reference in the Base Context.
        
        BaseContext.setEmslagent(emslAgent);
        //Setting the Ems Lite coordinator in the registry so that it can be used by
        //HTTP Application bundled with CAS for communication with EMS Lite.
        //EmsLiteCoordinator coordinator = new EmsLiteCoordinatorImpl();
        //Registry.bind(Constants.EMSL_COORDINATOR, coordinator);
        if (logger.isEnabledFor(Level.INFO)){
            logger.info("EMS Agent initialized...");
        }
    }
    
    /**
     * Initializes the EMS agent to facilitate communication with the EMS 
     * management application.
     *
     */
    private void initializeAgent() throws Exception{    	
        if (logger.isEnabledFor(Level.INFO)) {
            logger.info("Initializing the EMS Agent ....");
	}
    	
    	// Instantiate the agent object
    	agent = new EmsAgent();
    	
		// Get reference to thread monitor from registry
		ThreadMonitor thMonitor =
					(ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

    	// Initialize the agent
        agent.initialize(this.componentManager, this.configRepository, thMonitor,
						this.args, SUBSYS_TYPE, AseThreadMonitor.getThreadTimeoutTime());

        // Get the new configuration list from the EMS
		Pair[] pairs = agent.getConfigParams();
        
		// Set soft shutdown listener
		/*agent.registerSoftShutdownInterface(new SoftShutdownInterface() {
				public boolean isShutdownAllowed() {
					logger.debug("Inside AseMain getCount: " + AseMeasurementManager.instance().getCounter("Number of Active Application Sessions").getCount());
					return 0 >= AseMeasurementManager.instance().getCounter("Number of Active Application Sessions").getCount();
					
				}
			});*/

        if (logger.isEnabledFor(Level.DEBUG)) {
            logger.debug("No. of parameters retrieved from BayManager = "+ pairs.length);
	}
		
        // Update the config repository with the new values from EMS.
	this.componentManager.setValue(pairs);
		
        // Set the Agent Reference in the Base Context.
        BaseContext.setAgent(agent);

        if (logger.isEnabledFor(Level.INFO)){
            logger.info("EMS Agent initialized...");
	}
    }
    
    
    /**
     * Starts up all application components.
     */
    private void start() throws Exception {
        if (logger.isEnabledFor(Level.INFO)){
            logger.info("Starting the components...");
		}
        try {
			Registry.bind(Constants.NAME_ASEMAIN, this);
			if (logger.isEnabledFor(Level.INFO))
			logger.info("ASE MAIN HAS BEEN BINDED");
		} catch(Exception e) {
			logger.error("Exception ",e);
		}
        
        MComponentState running = new MComponentState(MComponentState.RUNNING);
    	
        if (this.agent != null) {
			// In case of EMS, call change state on agent.
			this.agent.changeComponentState(running.getValue());

			// Wait till all applications are deployed
			SasServiceManager ssm = (SasServiceManager) Registry.lookup("SasServiceManager");
			if(logger.isInfoEnabled())
			logger.info("Waiting for ServManager to notify ssm");
			synchronized (ssm) {
				while(!ssm.isStartupComplete()) {
					try {
						ssm.wait();
					} catch( Exception ex) {
						logger.error("While waiting for notification from ServManager thread", ex);
						logger.error("Still moving forward..... ");
					}
				}
			}

			// Reporting to EMS about the RUNING state
			this.agent.reportState();

			// Set thread timeout for agent thread again as OID config would have come
			// by now.
			this.agent.setThreadTimeout(AseThreadMonitor.getThreadTimeoutTime());
    	} else if (this.emslAgent != null) {
    		//Get the trace criteria defined if any from EMSLite.
    		//This method will make a REST request and get all the trace criteria and then call 
    		//markCallForTrace of EMSLite agent to add constraint to CallTraceManager.
    		this.emslAgent.getAllCallTraceCriteria();
    		
			// Wait till all applications are deployed
            //this will ensure that applications are also deployed
    		this.emslAgent.changeComponentState(running.getValue());
            this.emslAgent.reportState(running.getValue());
            if (logger.isEnabledFor(Level.INFO)){
                logger.info("reported State to wEMS Agent.");
            }
    	}else {
            // In the stand-alone model, call changeState on component manager
            this.componentManager.changeState(running);
    	}

		if (logger.isEnabledFor(Level.INFO)) {
            logger.info("ASE Server started at " + new Date());
		}
		this.started=true;
    }

    
    /**
     * Causes the main thread to wait until the shutdown hook it initiated
     * at which time clean will be performed.
     *
     */
    public void check_shutdown() throws Exception {
    	
    	 System.out.println("My Own PID is !" + ManagementFactory.getRuntimeMXBean().getName());
        if (this.agent != null){
            // Call the orb_run method on the Agent
            try {
                this.agent.orb_run();
            } catch (Exception e) {
                // If there is any exception, just ignore it....
				logger.error(e.getMessage(), e);
            }
            
            // In case of EMS, make the main thread wait on the ORB object.
            this.agent.check_shutdown();
    	} else if (this.emslAgent != null) {
    		// In case of EMS Lite, make the main thread wait on the EMS Lite object.
    		 this.emslAgent.check_shutdown();
    	} else {
            // If no EMS, make the main thead wait on this local object.
            synchronized (this.waitObject) {
                while (!this.stopped) {
                    try{
                        this.waitObject.wait();
                    } catch(InterruptedException e) {}	
                }
            }
    	}
    }
    
    /**
     * Shuts down the application.
     */
    public void shutdown() throws Exception {
        if (logger.isEnabledFor(Level.INFO)){
            logger.info("Shutdown called for ASE Server at "+ new Date());
		}
        this.stopped = true;

		//Changed for SAS IDE
		try {
			int managerstate = this.componentManager.getSubsystemState();

			if(managerstate==MComponentState.STOPPED) {
				if (logger.isEnabledFor(Level.INFO))
				logger.info("The component manager state is stopped");

				synchronized(this.waitObject) {
					this.waitObject.notifyAll();
				}
			}
		} catch(Exception ejmx) {
			logger.error(ejmx.getMessage(),ejmx);
		} finally {
			if (logger.isEnabledFor(Level.INFO)){
				logger.info("Shutdown Completed for ASE Server at "+ new Date());
			}
			System.exit(1);
		}

		// Coding change
    	try{
    		if (this.agent != null) {
            	// In case of EMS, call changeState on the Agent.
            	this.agent.changeComponentState(MComponentState.STOPPED);
    		} else if(this.emslAgent != null){
    			this.emslAgent.changeComponentState(MComponentState.STOPPED);
    		} else {
			
				//Call the changeState stopped on all the Components... 
				MComponentState stopState = new MComponentState(MComponentState.STOPPED);
				this.componentManager.changeState(stopState);

            	// In case of no EMS, notify the main thread
            	synchronized(this.waitObject){
                	this.waitObject.notifyAll();
            	}
    		}
    	}catch(Exception e){
			logger.error(e.getMessage(), e);
    	}finally{
			if (logger.isEnabledFor(Level.INFO)){
				logger.info("Shutdown Completed for ASE Server at "+ new Date());
			}
    		System.exit(1);
    	}    	
    }
    
    /**
     * Registers commands with the TelnetServer utility.
     *
     */
    private void registerCommands() throws Exception{		
        if (logger.isEnabledFor(Level.INFO)){
            logger.info("Registering the telnet commands");
	}

        TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);

        //Register the Shutdown command with the telnet server.
		//Make it hidden if the server is started from EMS.... 
		ShutDownCommand shutdownCommand = new ShutDownCommand();
        telnetServer.registerHandler(Constants.CMD_STOPSERVER, shutdownCommand,false);
	
        // Register the printStack command with the manager.
		StackDumpCommand printStackCommand = new StackDumpCommand();
        telnetServer.registerHandler(Constants.CMD_PRINTSTACK, printStackCommand,false);
		
        // Register the LoggingHandler for configuring the logging criteria
        telnetServer.registerHandler("logging", new LoggingHandler(),false);
        
        if (logger.isEnabledFor(Level.INFO)) {
            logger.info("Registering the telnet commands complete...");
        }
    }
    
    public void registerAseStartUpListener(AseStartUpListener listener, short priority){
    	if(logger.isDebugEnabled()){
    		logger.debug("registerAseStartUpListener() called on priorty:"+priority+"  listener::"+listener);
    	}

    	if(started)
    	{
    		try {
    			if(logger.isDebugEnabled()){
    				logger.debug("registerAseStartUpListener(): event generated for listener:"+listener);
    			}
    			listener.serverStarted();;
    		} catch(Exception exp) {
    			logger.error("Error in listener.serverStarted()", exp);
    		}
    	}else{
    		List<AseStartUpListener> startupListenerListForPriority = this.listeners.get(priority);
    		if(startupListenerListForPriority == null ){
    			if(logger.isInfoEnabled()){
    				logger.info("registerAseStartUpListener() list was null on priorty:"+priority);
    			}
    			startupListenerListForPriority = new ArrayList<AseStartUpListener>();
    		}
    		if(!startupListenerListForPriority.contains(listener)){
    			if(logger.isInfoEnabled()){
    				logger.info("registerAseStartUpListener() adding listener on priorty:"+priority);
    			}
    			startupListenerListForPriority.add(listener);
    		}else{
    			if(logger.isInfoEnabled()){
    				logger.info("registerAseStartUpListener() listener already present on priorty:"+priority);
    			}
    		}
    		this.listeners.put(priority, startupListenerListForPriority);
    	}

    	if(logger.isDebugEnabled()){
    		logger.debug("registerAseStartUpListener() Exit");
    	}
    }
    
    public void unregisterAseStartUpListener(AseStartUpListener listener){
    	if(logger.isDebugEnabled()){
			logger.debug("unregisterAseStartUpListener() Enter");
		}
    	Set<Entry<Short, List<AseStartUpListener>>> entrySet = listeners.entrySet();
		for (Entry<Short, List<AseStartUpListener>> entry : entrySet) {
			short priority = entry.getKey();
			List<AseStartUpListener> aseStartupListenerList = entry.getValue();
			
			if (aseStartupListenerList != null) {
				if (logger.isDebugEnabled()) {
					logger
						.debug("unregisterAseStartUpListener() aseStartupListenerList Found on priority::"
										+ priority);
				}
				boolean status = aseStartupListenerList.remove(listener);
				if (status) {
					if (logger.isDebugEnabled()) {
						logger
							.debug("unregisterAseStartUpListener() listener removed on priority::"
											+ priority);
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger
							.debug("unregisterAseStartUpListener() listener Not found on priority::"
											+ priority);
					}
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger
						.debug("unregisterAseStartUpListener() roleChangeListenerList NULL on priority::"
										+ priority);
				}
			}//@End if (roleChangeListenerList != null) 
		}
    	if(logger.isDebugEnabled()){
			logger.debug("unregisterAseStartUpListener() Exit");
		}
    }
    
    public void generateStartupEvent(){
    	if(logger.isDebugEnabled()){
    		logger.debug("generateStartupEvent() Called");
    	}
    	//used treemap to iterate in sort order and avoided use of Constants.RCL_NUM_OF_LISTENERS to make it more generic
    	Set<Short> priortySet = listeners.keySet();
    	for(short priorty:priortySet){
    		if(logger.isDebugEnabled()){
    			logger.debug("generateStartupEvent() Generating on priority::"+priorty);
    		}
    		List<AseStartUpListener> startUpListenerList = listeners.get(priorty);
    		
    		if(startUpListenerList != null) {
    			if(logger.isDebugEnabled()){
    				logger.debug("generateStartupEvent(): listeners found at priority: " + priorty);
    			}
    			for(AseStartUpListener listener: startUpListenerList){
    				
    				try {
    					listener.serverStarted();;
    					if(logger.isDebugEnabled()){
    						logger.debug("generateStartupEvent(): event generated for listener:"+listener);
    					}
    				} catch(Exception exp) {
    					logger.error("Error in listener.serverStarted()", exp);
    				}
    			}//@End for on RoleChangeListenerList
    			
    			
    		}else{
    			if(logger.isDebugEnabled()){
    				logger.debug("generateStartupEvent(): listeners NULL at priority: " + priorty);
    			}
    		}//@End if(roleChangeListenerList != null)
    	}//@End for loop on priorty
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("generateStartupEvent() Exit");
    	}
    }
    
    
	/**
	*
	*/
	private void createDirs() throws Exception
	{
		if (logger.isEnabledFor(Level.INFO))
		{
			logger.info("Checking and Creating required directories...");
		}
		//Create the log directory
    		//File logDir = new File(Constants.ASE_HOME, Constants.FILE_LOG_DIR);		//Changed by NJADAUN LOG LOCATION CHANGES
    		File logDir = new File(Constants.FILE_LOG_DIR);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
    	
		//Create the apps directory.
		String appDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_APP_DEPLOY_DIR);
		
		File appsDir =null;
		
		if (appDeployDir != null && !appDeployDir.isEmpty()) {
			appsDir = new File(appDeployDir);
		}else{
			appsDir = new File(Constants.ASE_HOME, Constants.FILE_HOST_DIR);
		}
		
		if(!appsDir.exists())
		{
    			appsDir.mkdirs();
		}
		//Create the resources directory.
		
		String resDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_RESOURCE_DEPLOY_DIR);
		
		File resourcesDir =null;
		
		if (resDeployDir != null && !resDeployDir.isEmpty()) {
			resourcesDir = new File(resDeployDir);
		}else{
		 resourcesDir = new File(Constants.ASE_HOME, "resources");
		}
		
		if(!resourcesDir.exists())
		{
    			resourcesDir.mkdirs();
		}
    		//Create or re-create the tmp directory.
		File tmpDir =  new File(Constants.ASE_HOME, Constants.FILE_TMP_DIR);
		if(tmpDir.exists())
		{
			tmpDir.delete();
		}
		tmpDir.mkdirs();
	}
    
    private Object[] createComponents() throws Exception{
		if (logger.isEnabledFor(Level.INFO))
		logger.info("Creating the required components...");
		
		Object[] components = null;
		Document doc =  null;
    	
    	//Get the server-config file's name
    	String fileName = System.getProperty(Constants.PROP_SERVER_CONFIG);
    	
    	//If the system property is used, 
    	//load the configuration from the file specified
    	//Else use the default file.
    	if(fileName != null){
			if (logger.isEnabledFor(Level.INFO))
    		logger.info("Loading the required components info from :" + fileName);	
    		doc = this.getXMLDocument(fileName, false);
    	}else{
			if (logger.isEnabledFor(Level.INFO))
			logger.info("Loading the requierd components info from the default config file");
			doc = this.getXMLDocument(Constants.FILE_CONFIG, true);
    	}
		
		//Iterate through the elements and create the MComponent objects.
		Element rootEl = doc.getDocumentElement();
		NodeList list = rootEl.getElementsByTagName("Component");
		components = new Object[list.getLength()];
		for(int i=0;i<list.getLength();i++){
			Element el = (Element)list.item(i);
			components[i] = this.createComponent(el);
		}
		
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Components created...");
		}
		return components;
    }
    
    private Object createComponent(Element element) throws Exception{
    	Object component = null;
    	
    	//Return null if the element is null.
    	if(element == null)
    		return component;
    	
    	//Get the component name.
		String componentName = element.getAttribute("name");
		
		//Do not create the Embedded web-container if the configuration does not require it.
		//May need to find some better way of dealing with this.
		if(Constants.NAME_WEB_CONTAINER.equalsIgnoreCase(componentName) && 
				this.tomcatReqd != Constants.WEB_CONTAINER_REQUIRED){
			return component;		
		}
    	
    	//Parse the component element and get the properties
		String clazzName = AseStrings.BLANK_STRING;
    	Element child =  DOMUtils.getFirstChildElement(element);
		while(child != null){
			String name = child.getTagName();
			String value = DOMUtils.getChildCharacterData(child);
			if(name.equals("Class")){
				clazzName = value;
			}
			child = DOMUtils.getNextSiblingElement(child);
		}
		
		//Create the instance of the MComponent
		if(logger.isDebugEnabled())
		logger.debug("Going to create component: " + clazzName);
		Class clazz = Class.forName(clazzName);
		try{
			Class[] types = new Class[]{ConfigRepository.class};
			Constructor constructor = clazz.getConstructor(types);
			Object[] values = new Object[]{this.configRepository};
			component = constructor.newInstance(values);
		}catch(NoSuchMethodException nse){
			component = clazz.newInstance();
		}
		
		//Bind this object with the regitry, if it has a name.
		if(componentName != null && !componentName.trim().equals(AseStrings.BLANK_STRING)){
			if(logger.isDebugEnabled())
			logger.debug("Going to bind component: " + clazzName + " with name: " + componentName);
			Registry.bind(componentName, component);
		}
    
    // Need to set DbAccessService instance in BaseContext 
    // so that Servlets can get access to DbAccessService
		if(Constants.NAME_DBACCESS_SERVICE.equalsIgnoreCase(componentName)) { 
			if(logger.isDebugEnabled())
			logger.debug("Going to set DbAccessService: " + clazzName + " in BaseContext");
			BaseContext.setDbAccessService(((SleeDbAccessService)component));
		}

		if(Constants.NAME_SAS_SVC_MGR.equalsIgnoreCase(componentName)) { 
			if(logger.isDebugEnabled())
			logger.debug("Going to set Common Deployment Object in Sas Service Manager Instance");
			((SasServiceManager)component).setDeploymentStatus(compDeploymentStatus);
		}
		
		if(Constants.NAME_SYS_APP_DEPLOYER.equalsIgnoreCase(componentName)) { 
			if(logger.isDebugEnabled())
			logger.debug("Going to set Common Deployment Object in SYS App Deployer Instance");
			((SysAppDeployer)component).setDeploymentStatus(compDeploymentStatus);
		}
		if(Constants.NAME_SBB_DEPLOYER.equalsIgnoreCase(componentName)) { 
			if(logger.isDebugEnabled())
			logger.debug("Going to set Common Deployment Object in SBB Deployer Instance");
			((SBBDeployerComponent)component).setDeploymentStatus(compDeploymentStatus);
		}
		//return this object
    	return component;
    }
    
    private Document getXMLDocument(String name, boolean isDefault) throws Exception{
    	InputStream stream = null;
    	Document doc = null;
		try{
			//Get the input stream to read from.
			if(isDefault){
				stream = this.getClass().getResourceAsStream(name);
			}else{
				stream = new FileInputStream(name);
			}
			
			//Parse the XML file and create the Document object
			DocumentBuilderFactory factory = null;
			factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(stream);
		}finally{
			if(stream != null)
				stream.close();
		}
		return doc;
    }

	class ShutDownCommand implements CommandHandler{
		
		public String execute(String command, String[] args, InputStream in, OutputStream out){
			String msg = "Shutdown of the server started.";
			try{
				shutdown();
			}catch(Exception e){
				msg = "Exception stopping the server..." +e;
				logger.error(msg);
			}
			return msg;
		}

		public String getUsage(String command) {
			return command;
        }
	}
	
	class StackDumpCommand implements CommandHandler{
		public String execute(String command, String[] args, InputStream in, OutputStream out){
			try {
				StackDumpLogger.logStackTraces();
			} catch(Throwable thr) {
				return "Error in printing the stack";
			}

			return "Completed printing the stack";
		}

		public String getUsage(String command) {
			return "dump-stack: prints the stack trace "+
                         "of all threads of the servlet engine";
		}
	}

    /**
     * Bootstraps the ASE by parsing it's XML configuration file.
     *
     * @param args contains the absolute path of the ASE's installation
     * root directory
     */
    public static void main(String[] args) {
    	int status = 0;
        try {
        	
        	 Runtime.getRuntime().addShutdownHook(new Thread()
        	    {
        	      public void run()
        	      {
        	        System.out.println("Ase Shutdown Hook is called !" + new Date());
        	        System.out.println("CAS Process Terminating ...Free Memory "+Runtime.getRuntime().freeMemory() +" Totl memory "+  Runtime.getRuntime().totalMemory());
          	        generateStackDumpOnExit();
       	      }
        	    });
        	 
        	 System.setSecurityManager(new SecurityManager() {
        		    @Override
        		    public void checkExit(int status) {
        		        new Exception("CAS exit attempt with return code "+status).printStackTrace();
        		    }
        		    // note that all dedicated check... methods delegate to the two below,
        		    // so overriding these is sufficient to enable all other actions
        		    @Override
        		    public void checkPermission(Permission perm, Object context) { }

        		    @Override
        		    public void checkPermission(Permission perm) { }
        		});
        
			Thread.currentThread().setContextClassLoader(AseMain.class.getClassLoader());	
	        startup = new AseMain();
            startup.validate(args);
            startup.initialize(args);
            startup.start();
			startup.setAseRexecPrintStream();
			startup.warmup();
			startup.generateStartupEvent();
            startup.check_shutdown();
        } catch (Throwable e) {
        	status = 1;
            	logger.error(e.toString(), e);
        }finally{
        	 System.out.println("Caling System.exit() !");
        	System.exit(status);
        }
    }
    
	private void setAseRexecPrintStream(){
		logger.error("set AseRexecPrintStream in System out & err.");
		try{
			AseRexecRollover rexecRollover = AseRexecRollover.getAseRexecRollover();
			AseRexecPrintStream rexecPS = new AseRexecPrintStream(new FileOutputStream(rexecRollover.getFilePath(),true));
			System.setOut(rexecPS);
			System.setErr(rexecPS);	
		}catch(FileNotFoundException e){
			logger.error("exception in setting AseRexecPrintStream."+e);
		}
	}
	
	private void warmup(){
		logger.error("Warmup Started...");

		warmupAR();
		warmupInap();
		warmupIsup();
		callingForName();
		warmupCall();
		warmupTcapCall();

		logger.error("Warmup Completed...");
	}
	
	private void warmupTcapCall() {
		logger.error("Starting warmupTcapCall Warmup..");
		try {
			Class clazz = Class.forName("com.genband.jain.protocol.ss7.tcap.JainTcapProviderImpl");
			clazz.getMethod("warmup", null).invoke(clazz, null);
		}catch (Exception e) {
			logger.error("warmupTcapCall warmup failed.. " +e);
		} finally {
			logger.error("warmupTcapCall WARMUP Completed..");
		}
	}
	
	private void callingForName() {

		logger.error("Starting callingForName Warmup");
		try {
			
			Class.forName("com.baypackets.ase.sipconnector.AseSipServletMessage");
			Class.forName("com.baypackets.ase.sipconnector.AseSipServletRequest");
			Class.forName("com.baypackets.ase.sipconnector.AseSipSession");
			Class.forName("com.baypackets.ase.container.AseApplicationSession");
			
			//clazz.getMethod("warmup", null).invoke(clazz, null);
		}catch (Exception e) {
			logger.error("callingForName failed");
		} finally {
			logger.error("callingForName WARMUP COMPLETED");
		}

	}

	private void warmupAR() {
		//DB warmup in AR

		logger.error("Starting AR Warmup..");
		try {
			Class clazz = Class.forName("com.baypackets.ase.router.AseSipApplicationRouterManager");
			clazz.getMethod("warmup", null).invoke(clazz, null);
		}catch (Exception e) {
			logger.error("AR warmup failed " + e);
		} finally {
			logger.error("AR Warmup completed..");
		}

	}

	private void warmupIsup() {
		logger.error("Starting ISUP Warmup..");
		try {
			Class clazz = Class.forName("com.genband.isup.operations.ISUPOperationsCoding");
			clazz.getMethod("warmup", null).invoke(clazz, null);
			
		}catch (Exception e) {
			logger.error("ISUP warmup failed" + e.getMessage());
			logger.error("ISUP warmup failed...." + e);
		} finally {
			logger.error("ISUP Warmup Completed..");
		}

	}

	private void warmupInap() {
		logger.error("Starting INAP Warmup");
		try {
			Class clazz = Class.forName("com.genband.inap.operations.InapOperationsCoding");
			clazz.getMethod("warmup", null).invoke(clazz, null);
			
		}catch (Exception e) {
			logger.error("INAP warmup failed..." + e);
		} finally {
			logger.error("INAP Warmup Completed..");
		}
	}

	@SuppressWarnings("unchecked")
	private void warmupCall() {
		// running warmuup calls
		logger.error("Starting Warmup Call..");

		try {
			Class clazz = Class.forName("com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransportLayer");
			clazz.getMethod("warmup", null).invoke(clazz, null);

		}catch (Exception e) {
			logger.error("Warmup call failed", e);
		}finally {
			logger.error("Warmup call Completed..");
		}
	}
	
	private static String fetchSiteId(){
		String filepath = Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES;
		Properties redisP = AseUtils.getProperties(filepath);
		
		
		/*
		 * cas0001  where first 00 is siteid
		 */
		int indexcas=selfSubsystemName.indexOf("cas") ;
		String siteId=selfSubsystemName.substring(indexcas+3,indexcas+5);
		
		if (logger.isDebugEnabled()) {
			logger.debug("SiteId from properties is  "+siteId);
		}
		
		return siteId;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private RedisWrapper createRedisWrapper() throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("createRedisWrapper from redis configuration: ");
		}

		String filepath = Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES;
	//	Properties redisP = AseUtils.getProperties(filepath);
		
		InputStream in = null;
		RedisWrapper redisWrapper = null;
		try {
			in = Files.newInputStream(Paths.get(filepath));
			Yaml yaml = new Yaml();
			TreeMap<String, String> redisP = yaml.loadAs(in, TreeMap.class);

			if (logger.isDebugEnabled()) {
				logger.debug("Read redis configuration: from properties  "
						+ redisP.toString());
			}

			java.security.Security.setProperty(
					RedisConstants.NETWORK_ADDRESS_CACHE_TTL,
					redisP.get(RedisConstants.NETWORK_ADDRESS_CACHE_TTL));
			java.security.Security
					.setProperty(
							RedisConstants.NETWORK_ADDRESS_CACHE_NGTIVE_TTL,
							redisP.get(RedisConstants.NETWORK_ADDRESS_CACHE_NGTIVE_TTL));

			RedisConnectionInfo connectionInfo1 = null;

			if (redisP.get(RedisConstants.PRIMARY_IP_ADDRESS) != null
					&& !redisP.get(RedisConstants.PRIMARY_IP_ADDRESS).isEmpty()) {

				if (logger.isDebugEnabled()) {
					logger.debug("Redis configuration: is IP based ");
				}

				connectionInfo1 = new RedisConnectionInfo("shard1",
						RedisConnectionPolicy.IP_BASED);
				connectionInfo1.setPrimaryIP(redisP
						.get(RedisConstants.PRIMARY_IP_ADDRESS));

				if (redisP.get(RedisConstants.SECONDARY_IP_ADDRESS) != null
						&& !redisP.get(RedisConstants.SECONDARY_IP_ADDRESS)
								.isEmpty()) {
					connectionInfo1.setSecondaryIP(redisP
							.get(RedisConstants.SECONDARY_IP_ADDRESS));
				}

			} else if (redisP.get(RedisConstants.REDIS_FQDN) != null
					&& !redisP.get(RedisConstants.REDIS_FQDN).isEmpty()) {

				if (logger.isDebugEnabled()) {
					logger.debug("Redis configuration: is FQDN based ");
				}
				// "redis-11896.rediscluster.agnity.com"
				connectionInfo1 = new RedisConnectionInfo("shard1",
						RedisConnectionPolicy.FQDN_BASED);
				connectionInfo1.setFQDN(redisP.get(RedisConstants.REDIS_FQDN));
			}

			int port = Integer.parseInt(redisP.get(RedisConstants.REDIS_PORT));
			connectionInfo1.setPort(port);

			int maxPoolContns = Integer.parseInt(redisP
					.get(RedisConstants.MAX_POOL_CONNECTIONS));
			connectionInfo1.setMaxPoolConnections(maxPoolContns);

			int maxPubSubClntCntns = Integer.parseInt(redisP
					.get(RedisConstants.MAX_PUBSUB_CLIENT_CONNECTIONS));
			connectionInfo1.setMaxPubSubClientConnections(maxPubSubClntCntns);

			int maxSerlconnts = Integer.parseInt(redisP
					.get(RedisConstants.MAX_SERIALIZE_CONNECT_CONNECTIONS));
			connectionInfo1.setMaxSerializeClientConnections(maxSerlconnts);

			RedisClientOptions clientOptions = new RedisClientOptions();

			int cmdtimeout = Integer.parseInt(redisP
					.get(RedisConstants.COMMAND_TIMEOUT));
			clientOptions.setCommandTimeOut(cmdtimeout);

			connectionInfo1.setClientOptions(clientOptions);
			redisWrapper = RedisWrapper.getRedisWrapper(connectionInfo1);

			selfSubsystemName = redisP.get(RedisConstants.SELF_SUBSYSTEM_NAME);
			propertyHashes = selfSubsystemName.toUpperCase() + ":config";
			
			
			String subscribeChannel=redisP.getOrDefault(RedisConstants.SUBSCRIBE_PROPERTY_CHANNEL,"true");
			
			subscribeconfigChannel=Boolean.parseBoolean(subscribeChannel);
			

			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("selfSubsystemName is " + selfSubsystemName);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("leaving createRedisWrapper with : "
						+ redisWrapper);
			}
		} catch (Exception e) {
			logger.error(" this is Exception " + e);

		} finally {
			in.close();
		}
		return redisWrapper;
	}
   
   /**
    * Used for initializing the oemsAgent library
    * By using instance OEMSServiceStarter can call sendAlram/sendHeatbeat all other methods
    * @return
    * @throws Exception
    */
	private  OEMSServiceStarter getOemsAgentWrapper() throws Exception {
		
		logger.info("starting load oems properties file" + Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES);
		if (logger.isDebugEnabled()) {
			logger.debug("getOemsAgentWrapper from oems Agent configuration: ");
		}

		String filepath = Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES;
	//	Properties redisP = AseUtils.getProperties(filepath);
		
		OEMSServiceStarter initializeOemsAgent=null;
		InputStream in=null;
		try {
			in = Files.newInputStream(Paths.get(filepath));
			Yaml yaml = new Yaml();
			TreeMap<String, String> oemsAgentProperties = yaml.loadAs(in,
					TreeMap.class);

			// String filepath = Constants.ASE_HOME + "/" +
			// Constants.FILE_CAS_STARUP_PROPERTIES;
			// Properties oemsAgentProperties =
			// AseUtils.getProperties(filepath);

			// tui enabled or not
			logger.info("Initilizing TUI ");
			AseTUIService.tuiEnabled = oemsAgentProperties
					.get("oems.tuiEnabled").toString()
					.equals(Constants.TUI_ENABLED);

			logger.info("TUI enabled:" + AseTUIService.tuiEnabled);
			OemsInitDTO oemsInitDTO = new OemsInitDTO();

			List<String> sitesIds = OemsUtils.splictStringToList(
					oemsAgentProperties.get("oems.sitesIds"),// Constants.SITES_IDS),
					Constants.SPLITTER_COMMA);
			String bootstrapServer = oemsAgentProperties
					.get("oems.bootStrapServer");// Constants.BOOTSTRAP_SERVER);
			String selfInstenceId = oemsAgentProperties
					.get("oems.selfInstenceId");// ,
												// Constants.SELF_INSTENCE_ID);
			String componentType = oemsAgentProperties
					.get("oems.componenttype");// , Constants.COMPONENT_TYPE);
			String consumerGroup = oemsAgentProperties
					.get("oems.consumerGroup");// , Constants.CONSUMER_GROUP);
			Map<MessageType, MessageBusType> instenceType = new HashedMap();
			String alarmSiteType = oemsAgentProperties
					.get("oems.alarm.message.type");// ,
													// Constants.ALARM_SITE_TYPE);
			String heratbeatSiteType = oemsAgentProperties
					.get("oems.heratbeat.message.type");// ,
														// Constants.HEATBEAT_SITE_TYPE);
			String inventoryiteType = oemsAgentProperties
					.get("oems.inventory.message.type");// ,
														// Constants.INVENTORY_SITE_TYPE);
			String calltraceSiteType = oemsAgentProperties
					.get("oems.calltrace.message.type");// ,
														// Constants.CALL_TRACE_SITE_TYPE);
			String measurementSitesitesIdsType = oemsAgentProperties
					.get("oems.measurement.set.message.type");// Constants.MSET_TYPE);

			instenceType.put(MessageType.ALARM,
					MessageBusType.valueOf(alarmSiteType));
			instenceType.put(MessageType.HEARTBEAT,
					MessageBusType.valueOf(heratbeatSiteType));
			instenceType.put(MessageType.INVENTORY,
					MessageBusType.valueOf(inventoryiteType));
			instenceType.put(MessageType.CALLTRACE,
					MessageBusType.valueOf(calltraceSiteType));
			instenceType.put(MessageType.MEASUREMENT,
					MessageBusType.valueOf(measurementSitesitesIdsType));
			instenceType.put(MessageType.STATISTICS,
					MessageBusType.valueOf("ALL"));
			String hbtInterval = oemsAgentProperties
					.get("oems.heartbeat.interval.seconds");// ,Constants.HBT_INTERVAL_SECONDS);
			String subscribedSiteIds = oemsAgentProperties
					.get("oems.subscribed.siteIds");
			oemsInitDTO.setHbtTimeDifference(Integer.parseInt(hbtInterval));
			oemsInitDTO.setBoootStrapServer(bootstrapServer);
			oemsInitDTO.setComponentType(ComponentType.valueOf(componentType));
			oemsInitDTO.setConsumerGroup(consumerGroup);
			oemsInitDTO.setSelfInstanceId(selfInstenceId);
			oemsInitDTO.setSiteIds(sitesIds);

			oemsInitDTO.setSubscribedSitesIds(splictStringToList(
					subscribedSiteIds, ","));
			oemsInitDTO.setInstanceType(instenceType);

			logger.info("OemsInitDTO while initilizing:" + oemsInitDTO);

			// once got initialized a heartbeat thread will start that will
			// publish
			// heartbeat
			initializeOemsAgent = OEMSServiceStarter.initializeOemsAgent(
					oemsInitDTO, new OemsMessageConsumer());

			logger.info("Oems Agent library  initilized successfully:");
		} catch (Exception e) {
			logger.error(" Xception occured while creating oems agent  " + e);
		} finally {
			in.close();

		}
		return initializeOemsAgent;
	}
	
	
	/**
	 * parse instance specific properties
	 * 
	 * @param casinstanceYml
	 */
	private void parseInstanceProperties() {

		logger.info("parseInstanceProperties() from file " +selfSubsystemName);
		
		if (selfSubsystemName == null || selfSubsystemName.isEmpty()) {

			String filepath = Constants.ASE_HOME + "/"
					+ Constants.FILE_CAS_STARUP_PROPERTIES;
			Properties startupP = AseUtils.getProperties(filepath);
			selfSubsystemName = startupP
					.getProperty(RedisConstants.SELF_SUBSYSTEM_NAME);
		}

		logger.info("parseInstanceProperties() selfSubsystemName from file " +selfSubsystemName);
		
		String instanceid = null;
		if (selfSubsystemName != null && !selfSubsystemName.isEmpty()) {

			instanceid=selfSubsystemName.substring(selfSubsystemName
					.indexOf("cas") + 3);
		}
		else{
			logger.error("susbystem name not properly defined could not get instanceid so will not read instance specific properties from ase<instanceid>.yml");
			return;
		}
		
		logger.info("parseInstanceProperties() instanceid is " +instanceid);
		
		String casInstanceYml = Constants.ASE_HOME + "/conf/" + "ase"+instanceid + ".yml";
		logger.info("parse instance properties from file ..." + casInstanceYml);

		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(casInstanceYml))) {

			TreeMap<String, String> config = yaml.loadAs(in, TreeMap.class);

			for (String key : config.keySet()) {

				configRepository.setValue(key, config.get(key));

				logger.info("Setting value reading from properties " + key
							+ " : " + config.get(key));
				
			}
		} catch (IOException e) {
			logger.error(" IO xception while reading cas instance properties"
					+ e);
		}

	}
	
	private  List<String> splictStringToList(String phrase, String splitter) {

		List<String> list = new ArrayList<String>();
		String[] split = phrase.split(splitter);
		for (String str : split) {
			if(str != null && str !="") {
				list.add(str);	
			}
			
		}
		return list;

	}
	
	 /**
	   * This method is used to Generate Heap dump and Exit from SAS system
	   */
	   public static void generateStackDumpOnExit(){
		   
		   /*
			 * Generate Heap dump e.g. /usr/java/jdk1.6.0_17/bin/jmap
			 * -dump:file=/space/myHeapDump.dmp pid
			 * 
			 */
			System.out.println("generateStackDumpOnExitnProcess--->");
		   ThreadInfo[] threads = ManagementFactory.getThreadMXBean()
			        .dumpAllThreads(true, true);
			
		   String dumpFile ="/LOGS/CAS/CAS_threadump_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss-SSS"))+".log";
			 FileOutputStream fi=null;;
			try {
				fi = new FileOutputStream(new File(dumpFile));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (ThreadInfo info : threads) {
			   // System.out.print(info);
			    
			   try{
					 fi.write(info.toString().getBytes());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   
			}
			
			System.out.println("generateStackDumpOnExitnProcess--->witten in " + dumpFile);
//			String processname = ManagementFactory.getRuntimeMXBean().getName();
//			String pid = processname.substring(0, processname.indexOf(AseStrings.AT));
//
//			System.out.println("Process is "
//					+ ManagementFactory.getRuntimeMXBean().getName() + " PID :"
//					+ pid);
//
//			String jdkHome = System.getProperty("java.home");
//
//			System.out.println("\n JDK HOME : " + jdkHome);
//
//			String jdkPATH = jdkHome.substring(0, jdkHome.length() - 3);
//
//			String commands[] = new String[3];
//
//			String dumpFile ="/LOGS/CAS/CAS_threadump_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"))+".txt";
//			
//			commands[0] ="jstack";
//			commands[1] = pid;
//			commands[2] = ">>" + dumpFile;
//			
//			System.out.println("Execute---> "+ commands[0] +" "+ commands[1]+" "+commands[2]);
//
//
//			File archiveFile = new File(BaseContext.getConfigRepository()
//					.getValue(Constants.OID_LOGS_DIR));
//
//			try {
//				Runtime.getRuntime().exec("kill -QUIT "+pid);//, null, archiveFile);
//			} catch (IOException e) {
//
//				System.out.println("Error while creating Memory dump !!!!");
//			}	
			System.out.println("Exiting from System !!!!");

	   }

}
