package com.genband.m5.maps.mgmt;

import com.baypackets.bayprocessor.agent.ComponentManagerImpl;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.ManagerAgentImpl;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.internalservices.BpSubsysTypeCode;
import com.baypackets.bayprocessor.slee.internalservices.SleeTraceService;
import com.baypackets.bayprocessor.slee.internalservices.TraceLevel;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;

public class MapsProvider {

        public static Logger logger = Logger.getLogger(MapsProvider.class);

	int mapsState;

	private static String SRC_FILE 	= "[ MapsProvider.java ] ";
	public final int WAIT_TIME 	= 10000;
	private static int threadTimeoutTime = 100;
	private static final int MAPS_SERVER_MANAGER_PRIORITY  = 1;
	private static final BpSubsysTypeCode SUBSYS_TYPE = BpSubsysTypeCode.SubsysTypeCode_Spsi;
	public static final String NAME_THREAD_MONITOR 	= "ThreadMonitor";
	
	private ComponentManagerImpl componentMgr;
	private ManagerAgentImpl managerAgentImpl;
	public ConfigRepository cfgRep;

	
	
	public MapsProvider() {
		
		// Setting the MAPS state to stopped state so that it can be loaded and
		// started up.
		
		mapsState = MComponentState.STOPPED;
    	        componentMgr = new ComponentManagerImpl();
		managerAgentImpl = new ManagerAgentImpl();

	} 
	
	public static void main(String args[]) {
		MapsProvider mapsProvider = new MapsProvider();

		try {
			mapsProvider.initialize(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

	 
	 class changeStateToRunningThread extends Thread {              
			
		 public void run() {
			    
			 try {
				sleep(10000);
				
				log("Calling change component state to RUNNING. ");
				managerAgentImpl.changeComponentState(1); 
			    
			    }
			    catch(Exception e) {
				
			    log("Error occurred while calling Change component state to RUNNING.");
			    e.printStackTrace();
								
			    }
			}
	}
	 
	 private void log(String logMesg){
		 
		 logger.info(logMesg);
		 
	 } 
	
	 
	 /**
	   * This method is called to initialize the MAPS Server and agent.
	   */	 
	public void initialize(String args[]) throws InitializationFailedException {

		try {
			
			/*
			 * Initialize the component manager.
			 * Get the Config repository and set it in the maps context.
			 */
			componentMgr.initialize(args);
			
			cfgRep = componentMgr.getConfigRepository();
    		
			MapsContext.setConfigRepository(cfgRep);
			BaseContext.setConfigRepository(cfgRep);
			
			log("ComponentManager intitialization successful.");
			log("Before instantiating Slee Trace Service.");
			
			SleeTraceService ts = new SleeTraceService(cfgRep);
			log("Changing state of SleeTrace Service");
                        ts.changeState(new MComponentState(0));
                        ts.setTraceLevel(-1);
                        MapsContext.setTraceService(ts);
                        BaseContext.setTraceService(ts);
                        log("Register Slee Trace Service.");
                        componentMgr.registerComponent(ts, 1);
				
                        ts.trace(TraceLevel.VERBOSE, 0, SRC_FILE, "initialize()","TraceService initialisation Successful.");
            
                        log("TraceService initialized.");
            
                    
				if (cfgRep == null)
					log("Config Repository is null.");				
					
				else
					log("Config Repository is not null.");
					
				if (args == null)
					log("Args is null.");
					
				else
					log("Args is not null.");
	
				if (componentMgr == null)
					log("Component Manager is null.");
					
				else
					log("Component Manager is NOT null.");
		

			 /*
			  * Create manager agent and fetch the parameters from the manager.
			  *  ManagerAgentImpl extends EmsAgent so initialize called on EmsAgent.
			  */
			
			log("Before instantiating Thread Monitor.");			
			ThreadMonitor th = new ThreadMonitor();
			managerAgentImpl.initialize(componentMgr, cfgRep,th,args,SUBSYS_TYPE,threadTimeoutTime);
			log("ManagerAgent initialized successfully.");			
									
			/*
			 * Getting configuration parameters from EmsAgent.
			 * and setting DB Mode.
			 */
			
			Pair[] pairs = managerAgentImpl.getConfigParams();
			log("Pairs retrieved.");
			log("Number of Pairs retireved :"+pairs.length);
			componentMgr.setValue(pairs);
			log("ConfigRepository updation Successful.");
								
			MapsContext.setAgent(managerAgentImpl);
			BaseContext.setAgent(managerAgentImpl);
						
			
			MapsConfig mapsConf = MapsContext.getMapsConfig();
						
			String dbFtReqd = cfgRep.getValue(MapsContext.DB_FT_MODE);
			if (dbFtReqd.equals("0"))
				mapsConf.mode = MapsContext.NON_FT_MODE;
			else if (dbFtReqd.equals("1"))
				mapsConf.mode = MapsContext.FT_MODE;
			else if (dbFtReqd.equals("3"))
				mapsConf.mode = MapsContext.RAC_MODE;
			
			log( "DBFT Mode = ["+ mapsConf.mode + "]");
			MapsContext.setMapsConfig(mapsConf);
			log(" : Config Value set successful.");
			
			//Now create all the components and register them with the component manager.
			createMapsComponents();
			log("Component creation Successful.");
			
			//Change the component manager's state to LOADED.
    		        componentMgr.changeState(new MComponentState(MComponentState.LOADED));
			log("Change state to LOADED done for Component Manager.");
			
			
			//Change the manager agent's state to LOADED.
			managerAgentImpl.changeComponentState(0);
			log("Manager Agent Change state successful.");
			
			
			//Update Config of MAPS.
			setMapsConfigParams(cfgRep);
			log("Maps Configuration parameters set successfully.");
			
			
			//Set System Properties to be used by JBoss
			setSystemProperties(cfgRep);
									
			changeStateToRunningThread thr = new changeStateToRunningThread();
			
			log("Calling Change State to running thread.");
			thr.run();
			log("Change State to running thread run successful.");

			//Start the orb.
			managerAgentImpl.orb_run();
			log("Coming out of orb_run()");
			
			managerAgentImpl.check_shutdown();
			log(" : Coming out of check_shutdown()");
						
			log(" : Before System exit()");
			
			System.exit(0);
			log(" : After System exit()");
			

		} // End of try block
		
		catch (Exception e) {
			e.printStackTrace();
			throw new InitializationFailedException("MapsProvider could not be initialized.");
		}
	} 
	

	/*
	 * Any Service such as Alarm Service or Timer Service if needed in future 
	 * can be registered here as part of MAPS components
	 */
	
	private void createMapsComponents() throws InitializationFailedException
	  {
		log("Inside createMapsComponents() ");
		try {
			
			MapsServerManager appMgr = new MapsServerManager(componentMgr.getConfigRepository());
			componentMgr.registerComponent(appMgr, MAPS_SERVER_MANAGER_PRIORITY);
			log("ComponentManager - register component - Maps Server - successful.");

			MapsContext.setAppServerManager(appMgr);

			log("Exiting createMapsComponents() ");
			
		} catch (Exception exp) {
			
			exp.printStackTrace();
			throw new InitializationFailedException(
					"in createMapsComponents : MapsProvider could not be initialized.");
			
		}
	  }
		  	
/*
 * Used to update parameters in MapsConfig to be used for MapsServer Manager
 * @configRepository Used to bring data from OIDs
 */
	  private void setMapsConfigParams(ConfigRepository configRepository) 
	  {

		try{
		  
		log( "Setting Maps Config params.");

		MapsConfig mapsConfig = MapsContext.getMapsConfig();
		
		mapsConfig.designatedRole = configRepository.getValue(MapsContext.DESIGNATED_ROLE);
		log( "designatedRole = ["+ mapsConfig.designatedRole + "]");

		mapsConfig.driverName = configRepository.getValue(ParameterName.DB_DRIVER_NAME);
		log( "driverName = [" + mapsConfig.driverName + "]");

		mapsConfig.dbRWFlag = Integer.parseInt(configRepository	.getValue(ParameterName.DB_WRITE_STATUS));
		log( "DB Write status = [" + mapsConfig.dbRWFlag	+ "]");

		mapsConfig.primaryDbSID = configRepository.getValue(ParameterName.DB_SID);
		log( "primaryDbSID = ["+ mapsConfig.primaryDbSID + "]");

		if ( mapsConfig.mode == MapsContext.RAC_MODE ){
			
			mapsConfig.secondaryDbSID = configRepository.getValue(MapsContext.DB_SID_SEC);
			log( "secondaryDbSID = ["+ mapsConfig.secondaryDbSID + "]");
			
			mapsConfig.secondaryDbIp = configRepository.getValue(MapsContext.SEC_DB_IP_ADDRESS);
			log( "secondaryDbIp = ["+ mapsConfig.secondaryDbIp + "]");

			mapsConfig.secondaryListenerPort = configRepository.getValue(MapsContext.DB_LISTENER_PORT_2);
			log( "secondaryListenerPort = ["+ mapsConfig.secondaryListenerPort + "]");
					
		}
		
		mapsConfig.dbUser = configRepository.getValue(ParameterName.DB_USER_NAME);
		log( "dbUser = [" + mapsConfig.dbUser + "]");

		mapsConfig.dbPassword = configRepository.getValue(ParameterName.DB_PASSWORD);
		log( "dbPassword = [" + mapsConfig.dbPassword + "]");

		mapsConfig.primaryDbIp = configRepository.getValue(ParameterName.DB_IP_ADDRESS);
		log( "primaryDbIp = ["	+ mapsConfig.primaryDbIp + "]");

		mapsConfig.primaryListenerPort = configRepository.getValue(MapsContext.DB_LISTENER_PORT);
		log( "primaryListenerPort = ["	+ mapsConfig.primaryListenerPort + "]");
				
		mapsConfig.minDBPoolSize = configRepository.getValue(ParameterName.DB_MIN_CON_THRESHOLD);
		log( "minDBPoolSize = ["+ mapsConfig.minDBPoolSize + "]");
		
		mapsConfig.maxDBPoolSize = configRepository.getValue(MapsContext.DB_MAX_CON_THRESHOLD);
		log( "maxDBPoolSize = ["+ mapsConfig.maxDBPoolSize + "]");
		
		MapsConfig.tmpLocation = configRepository.getValue(MapsContext.TMP_LOCATION);
		log( "tmpLocation = ["+ mapsConfig.tmpLocation + "]");

		MapsConfig.bindIp = configRepository.getValue(MapsContext.WWW_BIND_IP);
		log( "bindIp = [" + mapsConfig.bindIp + "]");

		MapsConfig.isSSLEnabledForHttp = configRepository.getValue(MapsContext.WWW_SERVER_SSL);
		log( "isSSLEnabledForHttp = ["	+ mapsConfig.isSSLEnabledForHttp + "]");
		
		MapsConfig.sslPort = configRepository.getValue(MapsContext.WWW_SSL_PORT);
		log( "sslPort = [" + mapsConfig.sslPort + "]");

		MapsConfig.logLevel = configRepository.getValue(ParameterName.TR_TRACE_LEVEL);
		log( "Log Level = [" + mapsConfig.logLevel	+ "]");

		MapsConfig.httpPort = configRepository.getValue(MapsContext.HTTP_PORT);
		log( "HTTP Port = [" + mapsConfig.httpPort + "]");
		
		MapsConfig.nsPort = configRepository.getValue(MapsContext.NS_PORT);
		log( "JNDI NS Port = [" + mapsConfig.nsPort + "]");
				
				
		MapsContext.setMapsConfig(mapsConfig);

		log( "Exiting setMapsConfigParams() ");
		
		}catch(Exception ex){
			
			log("Exception occurred while updating MapsConfig Paraamters");
			ex.printStackTrace();
		}
		
	}
	  
  /*
   * Used to set Non-Runtime Configured parameters in System environment.
   * These parameters are those needed by JBoss
   */
	  private void setSystemProperties(ConfigRepository configRepository)throws InitializationFailedException{
		  
		try{
		
		log( "Going to set SYSTEM Properties !");

		System.setProperty("java.io.tmpdir",MapsContext.getMapsConfig().tmpLocation);
		log( "java.io.tmpdir has been set to "
				+ System.getProperty("java.io.tmpdir"));

		System.setProperty("jboss.temp.dir",MapsContext.getMapsConfig().tmpLocation);
		log( "jboss.temp.dir has been set to "
				+ System.getProperty("jboss.temp.dir"));

		System.setProperty("jboss.data.dir",MapsContext.getMapsConfig().tmpLocation);
		log( "jboss.data.dir has been set to "
				+ System.getProperty("jboss.data.dir"));

		System.setProperty("jboss.server.log.dir", configRepository.getValue(ParameterName.TR_LOG_DIR));
		log( "jboss.server.log.dir has been set to "
				+ System.getProperty("jboss.server.log.dir"));
		
		/*
		 * JBoss log4j.xml Changes
		 */
		
		System.setProperty("genband.maps.log_dir",configRepository.getValue(ParameterName.TR_LOG_DIR));
		log( "genband.maps.log_dir has been set to "
				+ System.getProperty("genband.maps.log_dir"));

		String logFile = configRepository.getValue(ParameterName.TR_LOG_FILE_NAME);
		String mapsLogFile = updateFilename(logFile);
		
		System.setProperty("genband.maps.log_file",mapsLogFile);
		log( "genband.maps.log_file has been set to "
				+ System.getProperty("genband.maps.log_file"));
		
		System.setProperty("genband.maps.log_level",MapsContext.getMapsConfig().logLevel);
		log( "genband.maps.log_level has been set to "
				+ System.getProperty("genband.maps.log_level"));
		
		
		/*
		 * server.xml Changes
		 */
		
		System.setProperty("jboss.bind.address",MapsContext.getMapsConfig().bindIp);
		log( "jboss.bind.address has been set to "
				+ System.getProperty("jboss.bind.address"));
		
		System.setProperty("genband.maps.www.port",MapsContext.getMapsConfig().httpPort);
		log( "genband.maps.www.port has been set to "
				+ System.getProperty("genband.maps.www.port"));
				
		System.setProperty("genband.maps.www.ssl.port",MapsContext.getMapsConfig().sslPort);
		log( "genband.maps.www.ssl.port has been set to "
				+ System.getProperty("genband.maps.www.ssl.port"));
		
		String flagSSL="false";
		if(MapsContext.getMapsConfig().isSSLEnabledForHttp == "0"){
			flagSSL="false";
		}else{
			flagSSL="true";
		}
		
		System.setProperty("genband.maps.www.server_ssl",flagSSL);
		log( "genband.maps.www.server_ssl has been set to "
				+ System.getProperty("genband.maps.www.server_ssl"));
		
								
		/*
		 * JNDI Parameters update (in jboss-service.xml)
		 */
		System.setProperty("genband.maps.ns.port",MapsContext.getMapsConfig().nsPort);
		log( "genband.maps.ns.port has been set to "
				+ System.getProperty("genband.maps.ns.port"));
		
		int rmi_port = Integer.parseInt(MapsContext.getMapsConfig().nsPort)-1;
		
		System.setProperty("genband.maps.ns.rmi_port",Integer.toString(rmi_port));
		log( "genband.maps.ns.rmi_port has been set to "
				+ System.getProperty("genband.maps.ns.rmi_port"));
		
		
		String dbConnectUrl = null;
		String pridbip = MapsContext.getMapsConfig().primaryDbIp;
		String secdbip = MapsContext.getMapsConfig().secondaryDbIp;
		String pridbsid = MapsContext.getMapsConfig().primaryDbSID;
		String secdbsid = MapsContext.getMapsConfig().secondaryDbSID;
		String pridbport = MapsContext.getMapsConfig().primaryListenerPort;
		String secdbport = MapsContext.getMapsConfig().secondaryListenerPort;
		String dbUser = MapsContext.getMapsConfig().dbUser;
		String dbPasswd = MapsContext.getMapsConfig().dbPassword;
		String minDbPool = MapsContext.getMapsConfig().minDBPoolSize;
		String maxDbPool = MapsContext.getMapsConfig().maxDBPoolSize;
		String dbDriver = MapsContext.getMapsConfig().driverName;
		
		log("Using parameters : " + pridbip + " " + secdbip
					+ " " + pridbsid + " " + secdbsid + " " + pridbport + " "
					+ secdbport + " " + dbUser + " " + dbPasswd + " "
					+ minDbPool + " " + maxDbPool + " " + dbDriver);
		
		log("Going to create DB URL.");
		log("mapsCofig Mode: "+MapsContext.getMapsConfig().mode);
		log("MapsContext RAC Mode :"+MapsContext.RAC_MODE);
		
		if (MapsContext.getMapsConfig().mode == MapsContext.RAC_MODE){
			
			log("Got RAC Configuration.");
			
			if ((pridbsid != null && !pridbsid.trim().equals(""))
				&& (secdbsid != null && !secdbsid.trim().equals(""))
				&& (pridbsid.equals(secdbsid))) {

			
			dbConnectUrl = "jdbc:oracle:thin:@(description=(address_list=(load_balance=on)(failover=on)(address=(protocol=tcp)(host="
					+ pridbip.trim()
					+ ")(port="
					+ pridbport
					+ "))(address=(protocol=tcp)(host="
					+ secdbip.trim()
					+ ")(port="
					+ secdbport
					+ ")))(connect_data=(service_name="
					+ pridbsid.trim()
					+ ")(failover_mode=(type=select)(method=basic))))";
			
		
			System.setProperty("genband.maps.connection_url",dbConnectUrl);
						
			log( "genband.maps.connection_url has been set to "
					+ System.getProperty("genband.maps.connection_url"));
		 
			}else{
				
				log("Invalid Db Parameters recieved !");
				throw new InitializationFailedException("Invalid Db Parameters recieved");
			}
			
		}else if(MapsContext.getMapsConfig().mode == MapsContext.NON_FT_MODE){
			
			log("Got Non-FT Config.");
			
			if ((pridbsid != null && !pridbsid.trim().equals(""))){
				
			dbConnectUrl = "jdbc:oracle:thin:@" + pridbip + ":"
								+ pridbport + ":" + pridbsid;
			
			
			System.setProperty("genband.maps.connection_url",dbConnectUrl);	
			MapsContext.getMapsConfig().dbUrl = dbConnectUrl;
			
			log( "genband.maps.connection_url has been set to "
					+ System.getProperty("genband.maps.connection_url"));
			
			}
		 	
			
		}else{
			
			log("Invalid Db Mode !");
			throw new InitializationFailedException("Invalid DB Mode recieved : "+MapsContext.getMapsConfig().mode);
			
		}
			
		 log("genband.maps.connection_url has been set to : "
					+ System.getProperty("genband.maps.connection_url"));

			System.setProperty("genband.maps.db_user", dbUser);
			log("genband.maps.db_user has been set to : "
					+ System.getProperty("genband.maps.db_user"));

			System.setProperty("genband.maps.db_passwd", dbPasswd);
			log("genband.maps.db_passwd has been set to : "
					+ System.getProperty("genband.maps.db_passwd"));

			System.setProperty("genband.maps.min_pool_size", minDbPool);
			log("genband.maps.min_pool_size has been set to : "
					+ System.getProperty("genband.maps.min_pool_size"));

			System.setProperty("genband.maps.max_pool_size", maxDbPool);
			log("genband.maps.max_pool_size has been set to : "
					+ System.getProperty("genband.maps.max_pool_size"));

			System.setProperty("genband.maps.driver_class", dbDriver);
			log("genband.maps.driver_class has been set to : "
					+ System.getProperty("genband.maps.driver_class"));
		        
                        //Changing for default schema parameter.
			
			System.setProperty("hibernate.default_schema", dbUser);
			log("hibernate.default_schema has been set to "
					+ System.getProperty("hibernate.default_schema"));
			  
	  	 }catch(Exception e){
			 
			 log("Exception occured while setting system variables.");
			 e.printStackTrace();
			 
		 }
	  }
	  
	  private String updateFilename(String _fileName){
		  
		  String _newName ="";
		  int index = _fileName.indexOf(".");
		  if (index >= 0){
		  
		  String logExtn = _fileName.substring(index);
		  String logName = _fileName.substring(0,index).concat("_maps");
		  _newName = logName.concat(logExtn);
		  }else{
			  _newName = _fileName.concat("_maps");
		  }
			  
		  log("Log file to be used for MAPS : "+_newName);
		  
		  return _newName;
		  	  
		  
	  }
	  
	  
}

