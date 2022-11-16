
/*
 * Created on Mar 09, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipDiagnosticsLogger;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

public class AseTimerClock extends MonitoredThread implements ThreadOwner, Runnable {
	private static Logger logger = Logger.getLogger(AseTimerClock.class);
	private static AseTimerClock aseTimer = new AseTimerClock();
	private int waitInterval = 30;

	private ThreadMonitor threadMonitor;
	HashSet analysisList = new HashSet();
	LinkedList additionList = new LinkedList();

	boolean stopped = false;
	// for locking 
	Object obj = new Object();

	private AseTimerClock() {
		super ("AseTimerClock", AseThreadMonitor.getThreadTimeoutTime(),
			(TraceService)Registry.lookup(Constants.NAME_TRACE_SERVICE));
		this.threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

		try {
			ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			waitInterval = Integer.parseInt( configRep.getValue(Constants.ASE_TIMER_CLOCK_WAIT_TIME) );
		} catch(Exception ex) {
			logger.error("Exception in extracting time interval", ex);
			logger.error("Assuming default time interval of 30 seconds");
			waitInterval = 30;
		}
	}

	public static AseTimerClock getInstance()
	{
		return aseTimer;
	}

	public void shutdown()
	{
		logger.info(" shutdown called on AseTimerClock ");
		stopped = true;
	}
	
	public void add( AseApplicationSession app)
	{
		if(logger.isDebugEnabled()) {
			logger.debug("  Adding appSession " + app);
		}
                synchronized(obj) {
                   if(app.getTimeout()>0 && !analysisList.contains(app)){    
                        additionList.add(app); 
                    }else if( app.getAttribute(Constants.DIALOGUE_ID)!=null  && !analysisList.contains(app) ){
                    	additionList.add(app); 
                    }
                }
 
	}

	public void run(){
		boolean logEnabled = logger.isDebugEnabled();

		if (logEnabled) {
			logger.debug("IN run()");
			logger.info("AseTimerClock Thread started, waitInterval = "
				+ waitInterval);
		}

		//
		// Register this thread with thread monitor
		//

		try {
			if (logEnabled) {
				logger.debug("Registering with ThreadMonitor...");
			}

			this.setThreadState(MonitoredThreadState.Idle);
			this.threadMonitor.registerThread(this);

			if (logEnabled) {
				logger.debug("Successfully registered with ThreadMonitor.");
			}
		} catch (ThreadAlreadyRegisteredException e) {
			logger.error("Thread is already registered with ThreadMonitor.", e);
		}

		try {
			int retValue=0;
			AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);

			while (!this.stopped) {
				logEnabled = logger.isDebugEnabled();

				// Append the additionList to analysisList
				if (logEnabled) {
					logger.debug(" AnalysisList length = " + analysisList.size()
						+ " additionList length = " + additionList.size());
				}
		
				synchronized(obj){
					analysisList.addAll( additionList );
					additionList.clear();
				}

				try {
					if (logEnabled) {
					  logger.debug("Setting thread monitored state to IDLE.");
						logger.debug("Sleeping for " + waitInterval*1000 + " seconds...");
					}
					Thread.sleep(waitInterval*1000);
				} catch (Exception ex) {
					logger.error("Sleeping " , ex);
				}

				if (logEnabled) {
					logger.debug("Woke up. Setting thread monitored state to RUNNING.");
				}

				Iterator itr = analysisList.iterator();
			
				if (logEnabled) {
					logger.debug("AnalysisList length = " + analysisList.size() + 
						" additionList length = " + additionList.size());
				}

				AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
				int count = 0;
			
				while(itr.hasNext()) {
					AseApplicationSession app = (AseApplicationSession) itr.next();
					retValue = app.adjustTimer(waitInterval);

					if (retValue == -1) {
						AseMessage aseMesg = new AseMessage( 
							new AseEvent(app, Constants.EVENT_APPLICATION_SESSION_EXPIRED, 
							app), app );
						aseMesg.setWorkQueue(app.getIc().getWorkQueue());
					
						if (logEnabled) {
							logger.debug("Removing Expired AppSession: " + app);
						}

						synchronized(obj){
							itr.remove();
						}
						if (diag.isAppInvalidationLoggingEnabled()) {
							diag.log("Removed AppSession (expired): " + app);
						}

						this.updateTimeStamp();
						this.setThreadState(MonitoredThreadState.Running);

						engine.handleMessage(aseMesg);

						this.setThreadState(MonitoredThreadState.Idle);
					} else if(retValue < -1) {
						// Remove the AppSession from list without any callback
						if (logEnabled) {
							logger.debug("Removing Invalidated AppSession: " + app);
						}
					
						synchronized(obj){
							itr.remove();
						}
						if (diag.isAppInvalidationLoggingEnabled()) {
							diag.log("Removed AppSession (invalidated): " + app);
						}
					}

					if(++count > 10000) {
						yield();
						count = 0;
					}
				} // inner while
			} // outer while
		} catch(Throwable e) {
			logger.error("Throwable thrown in AseTimerClock thread: ", e);
		} finally {
			//
			// Register this thread with thread monitor
			//

			try {
				threadMonitor.unregisterThread(this);
				if (logEnabled) {
					logger.debug("Successfully unregistered with ThreadMonitor.");
				}
			} catch (ThreadNotRegisteredException e) {
				logger.error("Thread is not currently registered with ThreadMonitor.");
			}
		}

		if (logEnabled) {
			logger.debug("OUT run()");
		}
	} //run

	  
	public ThreadOwner getThreadOwner() {
		return this;
	}

	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " has expired");
		StackDumpLogger.logStackTraces();
		return ThreadOwner.SYSTEM_RESTART;
	}
} 

