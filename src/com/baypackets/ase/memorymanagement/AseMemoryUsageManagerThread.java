/**
 * 
 * AseMemoryUsageManagerThread.java
 * This class is written for running AseMemroryUsageManager's checkMemoryUsage() method 
 * in a separate thread.
 * 
 * @author Amit Baxi
 */
package com.baypackets.ase.memorymanagement;
import org.apache.log4j.Logger;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

class AseMemoryUsageManagerThread extends MonitoredThread implements
		ThreadOwner {
	private static Logger logger = Logger.getLogger(TelnetServer.class);
	private ThreadMonitor threadMonitor = null;

	public AseMemoryUsageManagerThread() {
		super("AseMemoryUsageManagerThread", AseThreadMonitor.getThreadTimeoutTime(), BaseContext.getTraceService());
		threadMonitor = (ThreadMonitor) Registry.lookup(Constants.NAME_THREAD_MONITOR);
	}

	public void run() {
		try {
			// Set thread state to idle before registering
			if(logger.isInfoEnabled())
				logger.info("Inside run method of AseMemoryUsageManagerThread.java.....");
			this.setThreadState(MonitoredThreadState.Idle);
			threadMonitor.registerThread(this);
		} catch (ThreadAlreadyRegisteredException exp) {
			logger.error("This thread is already registered with Thread Monitor",exp);
		}

		AseMemoryUsageManager aseMemoryUsageManager = AseMemoryUsageManager.getInstance();
		try {
			while(true){
				this.updateTimeStamp();
				this.setThreadState(MonitoredThreadState.Running);
				aseMemoryUsageManager.checkMemoryUsage();
				this.setThreadState(MonitoredThreadState.Idle);
			}
		} catch (Exception e) {
			logger.error("Error while starting AseMemoryUsageManager" + e);
		}
		
	}

	@Override
	public ThreadOwner getThreadOwner() {
		return this;
	}

	@Override
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " expired");
		// Print the stack trace
		StackDumpLogger.logStackTraces();
		return ThreadOwner.SYSTEM_RESTART;
	}

}
