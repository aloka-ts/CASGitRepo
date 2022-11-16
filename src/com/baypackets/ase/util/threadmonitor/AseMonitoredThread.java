package com.baypackets.ase.util.threadmonitor;
//*******************************
//This is a generic AseMonitoredThread but used to monitor
//StandByStatusCheckerThread 
//*******************************
public abstract class AseMonitoredThread extends Thread
{

     public AseMonitoredThread(String _threadName,long _timeOutTime)
     {
         super(_threadName);
         timeOutTime = _timeOutTime;
     }

     public void updateTimeStamp()
     {
          lastUpdateTimeStamp = System.currentTimeMillis();
     }

     public long getUpdateTime()
     {
          return lastUpdateTimeStamp;
     }

     public long getTimeoutTime()
     {
          return timeOutTime;
     }

     public void setTimeoutTime(long timeOut)
     {
          timeOutTime = timeOut;
     }

     public String getThreadState()
     {
          return threadState;
     }

     public void setThreadState(String threadState)
     {
          this.threadState = threadState;
     }
     
     public abstract AseThreadOwner getStandByThreadOwner();

     private volatile long timeOutTime =  1000;
     private volatile long lastUpdateTimeStamp = 0 ;
     private volatile String threadState = "running";
}
