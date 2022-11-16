package com.baypackets.ase.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

/*
 * This class spawns a thread that periodically checks whether the CAS Logs directory 
 * is present or not. If the directory is not present, the current date directory is created
 * and an alarm is raised. The thread is registered with the ThreadMonitor to monitor the 
 * thread. 
 */

public final class LogDirChecker extends MonitoredThread implements ThreadOwner{

	private ThreadOwner threadOwner = null;
	private static final SimpleDateFormat dateDirFormat = new SimpleDateFormat("MM_dd_yyyy");
	private static long logDirMonitorInterval = 0; //Interval after which CAS Log Dir availability is checked
	private ThreadMonitor threadMonitor = null;
	private static Logger logger = Logger.getLogger(LogDirChecker.class);
	private String fileDir = null;
	private String fileName = null;
	private static boolean isFailureAlarmRaised = false;
	private static final String EMS_FILE_APPENDER = "EmsFileAppender";
	private static final int LOG_DIR_PRESENT  = 1512;
	private static final int LOG_DIR_NOT_PRESENT  = 1511;
	private static final String LOG_DIR_PRESENT_ALARM_MSG = "CAS Logs directory successfully created.";
	private static final String LOG_DIR_NOT_PRESENT_ALARM_MSG = "CAS Logs directory not present. Creating new one.";
	private TimedRollingFileAppender appender = null;
	private static LogDirChecker logDirChecker = new LogDirChecker();
	ConfigRepository configRepositery = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	private AseAlarmService alarmService;

	public static LogDirChecker getInstance() { 
		return logDirChecker; 
	}

	private void initialize(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside LogDirChecker initialize");
		}
		fileDir = configRepositery.getValue(Constants.OID_LOGS_DIR);
		fileName = configRepositery.getValue(Constants.OID_LOGS_FILE);
		threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
		appender = (TimedRollingFileAppender)Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
		alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);
		String interval = configRepositery.getValue(Constants.LOG_DIR_CHECKER_THREAD_MONITOR_INTERVAL);
		setLogDirCheckerThreadInterval(interval);
		//Thread sleeps for logDirMonitorInterval. So, this value is added to ThreadMonitor timeout value 
		//to increase the timeout time.
		setTimeoutTime(logDirMonitorInterval + AseThreadMonitor.getThreadTimeoutTime());
		setThreadState(MonitoredThreadState.Idle);
		setThreadOwner(this);
	}

	private LogDirChecker() {
		super("LogDirChecker", AseThreadMonitor.getThreadTimeoutTime(), 
				(AseTraceService) Registry
				.lookup(Constants.NAME_TRACE_SERVICE));
		initialize();
	}

	@Override
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " expired" + "State : " + thread.getState() + this.getThreadState());

		// Print the stack trace
		StackDumpLogger.logStackTraces();
		logger.error("Thread [" + this.getName() +
				"] is being stoppped.");

		this.setThreadState(MonitoredThreadState.Stopped);
		return ThreadOwner.CONTINUE;
	}

	@Override
	public ThreadOwner getThreadOwner() {
		return threadOwner;
	}

	public void run(){
		// Register thread with thread monitor
		if(logger.isDebugEnabled()){
			logger.debug("Inside LogDirChecker run()");
		}
		registerThread();
		this.setThreadState(MonitoredThreadState.Running);
		this.updateTimeStamp();

		try{
			while(true){
				try{
					this.setThreadState(MonitoredThreadState.Running);
					this.updateTimeStamp();
					checkLogFile();
					Thread.sleep(logDirMonitorInterval*1000);
					//On expiry, the thread is removed from monitored Thread List. To ensure that 
					//thread is monitored it is re-registered, if it had expired earlier.

					if(this.getThreadState() == MonitoredThreadState.Stopped){
						logger.error("LogDirChecker Thread Stopped. Register Thread agin with ThreadMonitor");
						registerThread();
					}
					this.updateTimeStamp();
				}catch(InterruptedException e){
					logger.error("LogDirChecker Thread interrupted " + e.getMessage(),e);
				}catch(IOException e){
					logger.error("IO Exception " + e.getMessage(),e);
				}catch(Exception e){
					logger.error("Exception: " + e.getMessage(),e);
				}
			}
		}finally {
			// Unregister thread with thread monitor
			logger.error("INside finally for LogDirChecker got exception");
			try {
				threadMonitor.unregisterThread(this);
			} catch (ThreadNotRegisteredException exp) {
				logger.error(
						"This thread is not registered with Thread Monitor",
						exp);
			}
		}//@end outer try catch finally
	}

	private void raiseFailureAlarm(){
		logger.debug("Inside LogDirChecker : raiseSuccessAlarm()");
		try{
			if(!isFailureAlarmRaised){
				this.alarmService.sendAlarm(LOG_DIR_NOT_PRESENT,LOG_DIR_NOT_PRESENT_ALARM_MSG );
				isFailureAlarmRaised = true;
			}
		}catch (Exception e) {
			logger.error("Exception while Raising alarm ",e);
		}
	}

	private void raiseSuccessAlarm(){
		logger.debug("Inside LogDirChecker : raiseFailureAlarm()");
		try{
			if(isFailureAlarmRaised){
				this.alarmService.sendAlarm(LOG_DIR_PRESENT,LOG_DIR_PRESENT_ALARM_MSG );
				isFailureAlarmRaised = false;
			}
		}catch (Exception e) {
			logger.error("Exception while Raising alarm ",e);
		}
	}

	public void setThreadOwner(ThreadOwner threadOwn) {
		threadOwner = threadOwn;
	}


	/*
	 * Return the Absolute path CAS current Date Directory.
	 */
	private String getLogDir(){
		StringBuilder dirName = new StringBuilder();
		dirName.append(fileDir);
		dirName.append(File.separator);
		//Obtaining the date from Current Time and formatting it according to the date format MM_dd_yyyy
		String dateFolder = dateDirFormat.format(new Date(System.currentTimeMillis()));
		dirName.append(dateFolder);
		if(logger.isDebugEnabled()){
			logger.debug("CAS Date Dir : " + dirName.toString());
		}
		return dirName.toString();

	}

	/*
	 * This methods checks whether the current CAS Date Directory exists or not.
	 * If it does not exist, then it raises an alarm. On successfull creation of 
	 * directory, a clearing alarm is raised.
	 */
	private void checkLogFile() throws IOException{
		File logDateDir = new File(getLogDir());
		File logFile = new File(fileDir,fileName);
		if(!logDateDir.exists()){
			logger.error("CAS Log Directory does not exist: ");
			raiseFailureAlarm();
			appender.close();//closing the previous writer
			appender.setFile(logFile.getAbsolutePath()); // setting the writer again
			if(logDateDir.exists()){
				logger.error("CAS Log Directory Successfully created.");
				raiseSuccessAlarm();
			}else{
				logger.error("CAS Log Directory could not be created.");
				System.out.println("CAS Log Directory could not be created.");//Directing ouput to rexec
			}
		}
	}

	/*
	 * Set the value of logDirMonitorInterval. If value is negative or zero , then LogDirChecker
	 * Thread is not spawned.
	 */
	private void setLogDirCheckerThreadInterval(String interval) {
		if(logger.isDebugEnabled()){
			logger.debug("Value of logDirMonitorInterval property : " +  interval);
		}
		if(StringUtils.isBlank(interval)){
			logDirMonitorInterval = 0;
			return;
		}

		try{
			logDirMonitorInterval = Long.valueOf(interval);
			logDirMonitorInterval = logDirMonitorInterval*60; //Converting to seconds
		}catch(NumberFormatException e){
			logger.error("Illegal Argument of logDirCheckInterval . Setting Default Value."+ e.getMessage(),e);
		}catch(Exception e){
			logger.error("Exception while reading logDirCheckInterval." + e.getMessage() , e);
		}

		if(logger.isInfoEnabled()){
			logger.info("Value of logDirMonitorInterval set as : " +  logDirMonitorInterval);
		}

	}

	public long getLogDirCheckerThreadInterval(){
		return logDirMonitorInterval;
	}

	/*
	 * Registers the LogDirChecker Thread with the ThreadMonitor
	 */
	private void registerThread(){
		try {
			threadMonitor.registerThread(this);
		} catch(ThreadAlreadyRegisteredException exp) {
			logger.error("This thread is already registered with Thread Monitor", exp);
		}
	}
}
