//*******************************
//This is used to monitor
//StandByStatusCheckerThread 
//*******************************
package com.baypackets.ase.util.threadmonitor;

import org.apache.log4j.Logger;

public class AseStandByStatusThreadMonitor
{
	 private static final Logger logger = Logger.getLogger(AseStandByStatusThreadMonitor.class);
     public AseStandByStatusThreadMonitor()
     {
    	logger.error("AseStandByStatusThreadMonitor constructor called.... ");
        threadMonitorThread = new AseThreadMonitorThread();
     }

     // For StandByChecker Synchronization is not required but making it for future consideration
     // As of now this wont be overhead as only single thread is working.
     synchronized public void registerStandByCheckerThread(AseMonitoredThread monitoredThread)
         
     {

          threadMonitorThread.registerThread(monitoredThread);          

     }

     synchronized public void unregisterStandByCheckerThread(AseMonitoredThread monitoredThread)
         
     {

          threadMonitorThread.unregisterThread(monitoredThread);

     }

     public void initialize()
     {
    	 logger.error("AseStandByStatusThreadMonitor initialize called.... ");
         threadMonitorThread.start();
     }

     public void stop()
     {
          threadMonitorThread.shutdown();
     }

     private AseThreadMonitorThread threadMonitorThread;
}
