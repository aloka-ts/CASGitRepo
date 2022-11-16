package com.genband.m5.maps.mgmt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.Main;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;

public class MapsServerManager implements MComponent {

	public static Logger logger = Logger.getLogger(MapsServerManager.class);
	
	private int componentState = MComponentState.STOPPED;
	private static String SRC_FILE = "[ MapsServerManager.java ]";
	
	//JBoss Server Specific parameters
	private int START = 1;
	private int STOP = 2;
	private int SLEEP_TIME = 120000;
	private ConfigRepository config = null;
	
	//DB related parameters
	//MAPS would only be supported on RAC or Non-FT mode.
	public static int RECONNECT_SLEEP_TIME = 60000;
	
	//Support for Thread Monitor
	private int state = -1;
	private static int threadTimeoutTime = 100;
	public static final String PROP_MT_MONITOR_THREAD_TIMEOUT = "mt.monitor.thread.timeout.sec";
	public static final String PROP_MT_MONITOR_RESTART_ON_EXPIRY = "mt.monitor.restart.on.expiry";	
	ThreadMonitor thMon = new ThreadMonitor();
	
	
	/*
	 * Embedded JBossServer Class
	 */

	public class JBossServer implements Runnable {
		int action = 0;

		JBossServer(int pAction) {
			action = pAction;
		}

		public void run() {

			if (action == START) {
				String[] args = new String[1];
				args[0] = new String("-h");

				try {

					logger.info( " Calling JBoss Main and Boot.");
					Main main = new Main();
					main.boot(args);
					logger.info( " Called JBoss Main and Boot.");
				} catch (Exception e) {
					logger.error( "Exception While calling JBoss Main and Boot.",e);
					//e.printStackTrace();

				}
			} else if (action == STOP) {
				try {

					logger.info( " Calling JBoss shutdown. ");

					String shutdown_script = System.getProperty("JBOSS_HOME")
							+ "/bin/shutdown.sh";

					logger.info( " shutdown_script is : "
							+ shutdown_script);

					int exitValue = runCommand(shutdown_script, null, null);
					if (exitValue == 0) {
						logger.info( " Shutdown script execution successful.");
					} else {
						logger.info( " Shutdown script could not be executed. ");
					}

					try {

						logger.info( " Going to Sleep for 1 min .");
						logger.info(" Waiting for shutdown to be successful.");
						Thread.sleep(60 * 1000);

					} catch (Exception exp) {
						logger.error(" Exception occurred while calling Shutdown script.",exp);
						//exp.printStackTrace();
					}

					logger.info( " Ending MAPS process. ");

					/*String killScriptPath = System.getProperty("INSTALLROOT")
							+ "/" + System.getProperty("SUBSYSTEM")
							+ "/scripts/kill_jboss.sh";

					logger.info(" Calling kill jboss script : " + killScriptPath);

					exitValue = runCommand(killScriptPath, null, null);

					if (exitValue == 0) {
						logger.info( " kill_jboss executed successfully.");
					} else {
						logger.info( " Error occurred while executing kill_jboss. ");
					}
					 */
					
					System.exit(0);
					
				} catch (Exception ex) {

					logger.error(" Exception while calling Shutdown on JBoss",ex);
					//ex.printStackTrace();

				}
			}
		}
	}

	public MapsServerManager(ConfigRepository config)
			throws InitializationFailedException {
		
		logger.info(" Constructor of MapsServerManager called.");
		this.config = config;

	}

	/*
	 * IMP : Inherited abstract method from MComponent
	 */
	public void changeState(MComponentState mcomponentState)
			throws UnableToChangeStateException {

		logger.info( " Going to Change State of MComponent.");

		int newState = mcomponentState.getValue();

		//Change to Loaded State
		if (newState == MComponentState.LOADED) {
			if (componentState == MComponentState.STOPPED) {
				
				//Change for Thread Monitor
				try{
				String timeout =
					config.getValue(PROP_MT_MONITOR_THREAD_TIMEOUT);
				if(timeout != null) {
					threadTimeoutTime = Integer.parseInt(timeout);
				}

				if(logger.isDebugEnabled()) {
					logger.debug("Thread timeout time is : " + threadTimeoutTime);
				}

				// Get configured value for system-restart on thread timeout flag
				String isSysResReqStr =
				config.getValue(PROP_MT_MONITOR_RESTART_ON_EXPIRY);

				boolean isSysResReq = false;

				if(isSysResReqStr != null
				&& Integer.parseInt(isSysResReqStr) == 1) {
					isSysResReq = true;
					logger.debug("System restart on thread blocking is enabled");
				}

				thMon.initialize(BaseContext.getTraceService(),isSysResReq );
				state = MComponentState.LOADED;
				} catch(Exception e) {
					logger.error("Exception in Loading Thread Monitor", e);
					throw new UnableToChangeStateException("Cannot change Thread Monitor state");
				}
				//Change for Thread Monitor over
				
				componentState = MComponentState.LOADED;
				logger.info( " State of MAPS Server to be changed from STOPPED ==> LOADED.");

			} else {
				throw new UnableToChangeStateException(
						"Unable to change state STOPPED to LOADED.");
			}
		}

		//Change to Running State
		else if (newState == MComponentState.RUNNING) {
			if (componentState == MComponentState.LOADED) {

				logger.info( " State of MAPS Server to be changed from LOADED ==> RUNNING.");

				startAllComponents();

				MapsContext.getMapsConfig().primaryMAPS = true;
				componentState = MComponentState.RUNNING;
				
			} else {
				throw new UnableToChangeStateException(
						"Unable to change state LOADED to RUNNING.");
			}
		}

		//Change to Stopped State
		else if (newState == MComponentState.STOPPED) {

			logger.info(" Change State to STOPPED on MAPS Server called.");

			stopAllComponents();
			componentState = MComponentState.STOPPED;
			
		} else if (newState == MComponentState.ERROR) {

			throw new UnableToChangeStateException("MComponent State in ERROR.");
			
		} else {
			
			logger.error("Illegal State recieved.");
			throw new UnableToChangeStateException("Illegal State recieved.");
			
		}
		return;
	}

	/*
	 * IMP : Inherited abstract method from MComponent. 
	 * Called whenever a runtime parameter update happens from console.
	 * MAPS allows update for RTC parameters- LOG_LEVEL, DB_MIN_CON_THRESHOLD and DB_MAX_CON_THRESHOLD.
	 * This would call all the required MapsComponent passing them the Pair[] as an event.
	 */

	public void updateConfiguration(Pair[] configData, OperationType optype)
			throws UnableToUpdateConfigException {
		//

		try {
			
			logger.info("updateConfiguration() Called.");
			logger.info("Getting an Instance of JMXClientContext");
			JMXClientContext obJMXUtility = new JMXClientContext ();
			logger.info("Checking the Changed set of values recieved.");			
			
			if (configData != null) {
				if (configData.length > 0) {

					for (int i = 0; i < configData.length; i++) {

						String first = (String) configData[i].getFirst();	//OID
						String second = (String) configData[i].getSecond();	//Updated Value of OID
						if ((first != null) && (second != null)) {
							
							logger.info("Pair Recieved : first : "+ first+" second : "+second);
							
							if (first.equals(ParameterName.DB_WRITE_STATUS)) {
								
									logger.info("Updating DB WRITE STATUS Configuration.");
									
									if (second.equals("1")) {
										MapsContext.getMapsConfig().dbRWFlag = 1;
									} else if (second.equals("0")) {
										MapsContext.getMapsConfig().dbRWFlag = 0;
									} else if (second.equals("2")) {
										MapsContext.getMapsConfig().dbRWFlag = 2;
									}
									
									logger.info(" Done with Setting DB WRITE STATUS Configuration.");
								
							}else if(first.equals(ParameterName.TR_TRACE_LEVEL)){
								
									logger.info("Updating Trace Level Configuration.");
									
									String serverTraceLevel = "";
									MapsContext.getMapsConfig().logLevel = second;
									logger.info("Trace Level Configuration : "+second);
									
									if (second.equals("TRACE")) {
									     
										serverTraceLevel = "TRACE";
																			
									}else if (second.equals("VERBOSE")){
										
										serverTraceLevel = "DEBUG";
										
									}else if (second.equals("WARNING")){
																			
										serverTraceLevel = "INFO";
										
									}else if (second.equals("ERROR")) {
										
										serverTraceLevel = "ERROR";
										
									}else if (second.equals("ALARM")){
										
										serverTraceLevel = "FATAL";
									}
																
									List<String> opArgs = new ArrayList<String>();
									opArgs.add("org.apache");
									opArgs.add(serverTraceLevel);
									String on = "jboss.system:service=Logging,type=Log4jService";
	
									obJMXUtility.invoke(on, "setLoggerLevel", opArgs);
																	
									System.out.println(SRC_FILE + "genband.maps.log_dir has been set to "
											+ System.getProperty("genband.maps.log_dir"));
	
									logger.info(" Updated Trace Level Configuration.");
								
																
							}else if(first.equals(ParameterName.DB_MIN_CON_THRESHOLD)){
									
									logger.info(" Updating Minimum DB Connection Threshold to : "+second);
									
									obJMXUtility.set(
													"jboss.jca:service=ManagedConnectionPool,name=PortalDS",
													"MinSize", second);
									
									logger.info(" Updated Minimum DB Connection Threshold.");
															
							}else if(first.equals(MapsContext.DB_MAX_CON_THRESHOLD)){
								
									logger.info(" Updating Maximum DB Connection Threshold to :"+second);
									
									obJMXUtility.set(
													"jboss.jca:service=ManagedConnectionPool,name=PortalDS",
													"MaxSize",second);
									
									logger.info(" Updated Maximum DB Connection Threshold.");
								
							}
								
						}
					}
				}
			}

		} catch (Throwable ex) {

			logger.error(" Exception in updateConfiguration()",ex);
			throw new UnableToUpdateConfigException(
					"from updateConfiguration(): Unable to update Configuration.");

		}
		return;
	}

	/*
	 * Method to Start JBoss and Other components
	 */
	public void startAllComponents() throws UnableToChangeStateException {

		try {

			logger.info(" Going to Start all components of MAPS.");

			//checking if db is up and in usable state
			pingDatabase();

			//starting all components once db is known to be up
			startServer();
			
			//Any other managed component which needs to be started should be handled here.

		} catch (Throwable ex) {

			logger.error( "Exception in startAllComponents() ", ex);

			throw new UnableToChangeStateException("Unable to change state");
		}

	}

	public static void pingDatabase() throws Exception {

			logger.info( " racDBPing() called .");

			ResultSet rs = null;
			Class.forName(MapsContext.getMapsConfig().driverName);
			Connection conn = null;
			Statement stmt = null;
			String query = null;

			try {

				String racdburl = null;

				String pridbip = MapsContext.getMapsConfig().primaryDbIp;
				logger.info( " Primary DB IP : " + pridbip);

				String secdbip = MapsContext.getMapsConfig().secondaryDbIp;
				logger.info( " Secondary DB IP : " + secdbip);

				String pridbsid = MapsContext.getMapsConfig().primaryDbSID;
				logger.info( " Primary DB SID : " + pridbsid);

				String secdbsid = MapsContext.getMapsConfig().secondaryDbSID;
				logger.info(" Secondary DB SID : " + secdbsid);

				String pridbport = MapsContext.getMapsConfig().primaryListenerPort;
				logger.info( " Primary DB Listener Port : "
						+ pridbport);

				String secdbport = MapsContext.getMapsConfig().secondaryListenerPort;
				logger.info( " Secondary DB Listener Port : "
						+ secdbport);

				if (MapsContext.getMapsConfig().mode == MapsContext.RAC_MODE) {
					
					if ((pridbsid != null && !pridbsid.trim().equals(""))
							&& (secdbsid != null && !secdbsid.trim().equals(""))
							&& (pridbsid.equals(secdbsid))) {
	
						racdburl = "jdbc:oracle:thin:@(description=(address_list=(load_balance=on)(failover=on)(address=(protocol=tcp)(host="
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
	
						logger.info( " RAC DB URL Prepared : "+ racdburl);
	
					} else {
						throw new Exception(
								"Invalid DB SID . They are not same as required for RAC or they are empty or null. ");
					}
				}

				else if (MapsContext.getMapsConfig().mode == MapsContext.NON_FT_MODE) {

					if ((pridbsid != null && !pridbsid.trim().equals(""))) {
	
						racdburl = "jdbc:oracle:thin:@" + pridbip + ":"
						+ pridbport + ":" + pridbsid;
	
						logger.info( " NON FT DB URL Prepared : "+ racdburl);
	
					} else {
						throw new Exception(
								"Invalid DB URL for NOn FT config. Check it. " + pridbsid);
					}
					
				}
				else {
					
					throw new Exception(
							"Unsupported DB mode " + MapsContext.getMapsConfig().mode);
					
				}

				conn = DriverManager.getConnection(racdburl, MapsContext
						.getMapsConfig().dbUser,
						MapsContext.getMapsConfig().dbPassword);

				stmt = conn.createStatement();
				query = "select to_char(SYSDATE, 'DD-MON-YYYY HH24:MI:SS') CURRENT_DATE from dual";
				rs = stmt.executeQuery(query);

				while (rs.next()) {

					logger.info( " Database is up at "
							+ rs.getString("CURRENT_DATE"));

				}

				MapsContext.getAgent().notifyDBConnection(
						MapsContext.getMapsConfig().primaryDbSID,
						RSIEmsTypes.ConnectionState.ConnectionState_UP);

				

			} catch (SQLException sq) {

				logger.error( " SQLException in racDBPing() ",sq);
				//sq.printStackTrace();
				
				//Adding code for reporting Maps-PrimaryDB Down link-state to EMS
				MapsContext.getAgent().notifyDBConnection(
						MapsContext.getMapsConfig().primaryDbSID,
						RSIEmsTypes.ConnectionState.ConnectionState_DOWN);

			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception ex) {

					logger.error( " Exception in closing connection ",ex);

				}
			}
	}

	private void startServer() throws UnableToChangeStateException {
		try {

			logger.info("StartServer() in MAPS Server Manager called");

			String urlString = "";

			String[] args = new String[4];
			args[0] = new String("-c");
			args[1] = new String("maps");
			args[2] = new String("-b");
			String bindingIp = MapsContext.getMapsConfig().bindIp;
			args[3] = bindingIp;

			logger.info( "Starting JBOSS");

			startJboss(args);

			logger.info( "Started JBOSS");

			Thread.sleep(SLEEP_TIME);

			//System properties coming from scripts/maps 
			if (MapsContext.getMapsConfig().isSSLEnabledForHttp == "1") {

				urlString = "https://" + config.getValue("1.3.1") + ":"
						+ System.getProperty("genband.maps.www.ssl.port") + "/jmx-console";

				logger.info( "Going to test URL : "
						+ urlString);
			} else {

				urlString = "http://" + config.getValue("1.3.1") + ":"
						+ System.getProperty("genband.maps.www.port") + "/jmx-console";

				logger.info( "Going to test URL : "
						+ urlString);

			}
			boolean alive = false;

			String retryCount = System.getProperty("WebServerUrlRetry");

			logger.info( "Retry Count retrieved: "
					+ retryCount);

			int retries = Integer.parseInt(retryCount);

			while (alive != true && retries > 0) {

				logger.info( "Checking JBoss Server State");
				Thread.sleep(RECONNECT_SLEEP_TIME);
				alive = MapsContext.checkServerState(urlString);
				if (alive != true) {
					retries--;
					if (retries == 0) {
						throw new UnableToChangeStateException(
								"Unable to Connect to URL..");
					}
				}

			}
		} catch (Exception io) {

			logger.info( "Exception in startServer()");
			io.printStackTrace();
			throw new UnableToChangeStateException("Unable to change State");
		}
	}

	public static void startJboss(final String[] args) throws Exception {
		Runnable worker = new Runnable() {
			public void run() {
				try {

					logger.info( "Going to Boot JBoss..");

					Main main = new Main();
					main.boot(args);
				} catch (Exception e) {
					logger.info( "Failed to Boot Jboss");
					
					e.printStackTrace();

				}
			}

		};

		ThreadGroup threads = new ThreadGroup("jboss");
		new Thread(threads, worker, "main").start();

	}

	public void stopAllComponents() throws UnableToChangeStateException {
		logger.info( "Going to Stop Jboss from within stopALlComponents()");

		try {
			stopServer();

		} catch (Exception ex) {
			throw new UnableToChangeStateException("Unable to change state");
		}
	}

	private void stopServer() throws UnableToChangeStateException {
		try {

			logger.info( "Stop Server Called.");

			JBossServer jboss = new JBossServer(STOP);
			Thread jbossThread = new Thread(jboss);
			logger.info( "About to start shutdon thread");
			jbossThread.start();
			logger.info( "Shutdown thread started");
			//			jbossThread.join();

		} catch (Throwable io) {
			System.err.println("Exception in starting shutdown thread.");
			throw new UnableToChangeStateException("Unable to change state");
		}
	}

	public static int runCommand(String cmd, String[] envp, File dir)
			throws IOException, InterruptedException {
		int exitVal;

		logger.info( " Command in runCommand : " + cmd);

		try {
			Process proc = Runtime.getRuntime().exec(cmd, envp, dir);
			StreamGobblerThread errorGobbler = new StreamGobblerThread(proc
					.getErrorStream(), "ERROR : ");
			StreamGobblerThread outputGobbler = new StreamGobblerThread(proc
					.getInputStream(), "OUTPUT : ");
			errorGobbler.start();
			outputGobbler.start();
			exitVal = proc.waitFor();
		} catch (IOException e) {
			logger.info(" Got IO Exception in runCommand() : ");
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			logger.info(" Got InterruptedException Exception in runCommand() : ");
			e.printStackTrace();
			throw e;
		}
		return exitVal;
	}

}

