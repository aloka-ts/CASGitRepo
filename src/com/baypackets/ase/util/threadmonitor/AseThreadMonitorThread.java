package com.baypackets.ase.util.threadmonitor;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class AseThreadMonitorThread extends Thread
{
	private ConfigRepository configRep;
	private int standByStatusCheckerMonitorInterval;
	private static final Logger logger = Logger.getLogger(AseThreadMonitorThread.class);
    public AseThreadMonitorThread()
    {
      super("AseThreadMonitor");
      monitoredThreadsArr = new Vector();
      this.configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
      standByStatusCheckerMonitorInterval = Integer.parseInt(this.configRep.getValue(Constants.STANDBY_STATUS_CHECKER_MONITOR_INTERVAL));

    }

    synchronized public void run()
    {
    	logger.error("AseThreadMonitorThread run called....." + standByStatusCheckerMonitorInterval);
    	while(running)
    	{
    		try
    		{
    			// wait for configured time duration
    			wait(standByStatusCheckerMonitorInterval);
    			
    			//In monitoredThreadsArr, there should be only 1 entry of StandByStatusChecker
    			if(monitoredThreadsArr.size() != 0 ){
        			
        			boolean threadExpired = false;
        			AseMonitoredThread aseMonitoredThread = (AseMonitoredThread) monitoredThreadsArr.get(0);
        			long timeOutTime = aseMonitoredThread.getTimeoutTime();
        			
        			// Monitor standbystatuschecker when the same only in running state 
        			if(!aseMonitoredThread.getThreadState().equals(RUNNING)) {
        				
        				// Dont want to do anything when state is idle ( there are only two states : running and idle )
        				continue;
        			}

        			long timeDiff = System.currentTimeMillis() - aseMonitoredThread.getUpdateTime() 
        			- timeOutTime;

        			if( (timeDiff >0) && (threadExpired == false) )
        			{
        				threadExpired = true;
        			}

        			// This is for future consideration, if any other state adds or need to implement logic accordingly.
        			if(threadExpired == true) 
        			{
        				// removing so that monitor thread will not monitor it again
        				monitoredThreadsArr.remove(0); 
        				logger.debug("AseThreadMonitorThread run : thread exp..");
        				aseMonitoredThread.getStandByThreadOwner().standByThreadExpired(aseMonitoredThread);
        			}
    			}

    		}catch(Exception e)
    		{
    			logger.error("Exception  in AseThreadMonitorThread run " + e.getMessage());
          }
      }
    }

    public void shutdown()
    {
      running = false;
    }

    synchronized public void registerThread(AseMonitoredThread monitoredThread)
    {
    	logger.debug("AseThreadMonitorThread registerThread  =>" + monitoredThreadsArr);
    	// removing any existing element before registering.We are expecting only one element.
    	// This is for safety
    	monitoredThreadsArr.removeAllElements();
    	// Adding for monitoring
    	monitoredThreadsArr.add(monitoredThread);
    	logger.error("AseThreadMonitorThread registerThread  =>" + monitoredThreadsArr);
    }

    synchronized public void unregisterThread(AseMonitoredThread monitoredThread)
    
    {
      if(monitoredThreadsArr.contains(monitoredThread) )
      {
    	  logger.error("AseThreadMonitorThread unregisterThread....." + monitoredThreadsArr);
          monitoredThreadsArr.remove(monitoredThread);
      }
    }

    private boolean running = true;
    private Vector monitoredThreadsArr;
    private static String RUNNING = "running";


}
