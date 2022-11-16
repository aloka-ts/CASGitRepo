package com.baypackets.ase.ra.telnetssh.ls;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.ls.library.TelnetSession;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;

/**
 * The Class SessionPermRecoveryTask.
 * implements runnable. Its instance will be running as task in thread pool
 * 
 *
 * @author saneja
 */
public class SessionPermRecoveryTask implements Runnable {
	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(SessionPermRecoveryTask.class);
	
	private BlockingQueue<LsSession> lsSessionRecoveryQueue;
	/**
	 * Instantiates a new session recovery task.
	 *
	 */
	public SessionPermRecoveryTask() {
		this.lsSessionRecoveryQueue = new LinkedBlockingQueue<LsSession>();
	}
	
	private boolean destroyRecovery=false;
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.debug("Enter RecoveryThread(SessionPermRecoveryTask)");
		recoverLsSession();
		if(isInfoEnabled)
			logger.debug("Leave Recovery thread(SessionPermRecoveryTask)");
	}
	
	/**
	 * Adds the ls session to recovery Q
	 * wil be invoked from startSession 
	 * methods of LsSession implemnting 
	 * classes. executeRequest method 
	 * in LsManager will call method 
	 * if request fails at LS
	 *
	 * @param lsSession the ls session
	 */
	public void addLsSession(LsSession lsSession){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter addLsSession(SessionPermRecoveryTask)-->Adding LsSession to recovery lsId::"+lsSession.getLs().getLsId());
		lsSessionRecoveryQueue.add(lsSession);
		if(isDebugEnabled)
			logger.debug("Leave addLsSession(SessionPermRecoveryTask)-->Added LsSession to recovery");
	}
	
	/**
	 * Recover ls session.
	 * Pops ls session details form recovery Q.
	 * Attempts to recover LS session if recovery period for LS has expierd
	 * Calls starts session on LSSession
	 */
	private void recoverLsSession(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter recoverLsSession");
		LsSession lsSession=null;
		Date lsDownTime;
		long lsDownTimeInMilliSecs;
		long currenTimeInMilliSecs;
		//CR UAT 1219 Changes
		RaProperties raProperties = LsManager.getInstance().getRaProperties();
		int longRecoveryPeriod = raProperties.getLongRecoveryPeriod();
		LS ls = null;
		while(!destroyRecovery){	
			try {
				lsSession=lsSessionRecoveryQueue.take();
			} catch (InterruptedException e) {
				logger.error("InterruptedException in Recovery get operation",e);
			}
			ls = lsSession.getLs();
			if(ls==null){
				logger.error("(SessionPermRecoveryTask) LsSession with null LS details next iteration");
				continue;
			}
			lsDownTime=lsSession.getLsDownTimeStamp();
			lsDownTimeInMilliSecs=lsDownTime.getTime();
			currenTimeInMilliSecs=System.currentTimeMillis();
			if((currenTimeInMilliSecs - lsDownTimeInMilliSecs) < (longRecoveryPeriod*1000*60) ){
				try {
					Thread.sleep(longRecoveryPeriod*1000*60 - (currenTimeInMilliSecs - lsDownTimeInMilliSecs));
				} catch (InterruptedException e) {
					logger.error("InterruptedException in Sleep operation",e);
				}
			}
			synchronized (lsSession) {
				lsSession.startSession();	
			}
		}
		if(isDebugEnabled)
			logger.debug("Leave recoverLsSession(SessionPermRecoveryTask)");
	}
	
	/**
	 * Destroy recover task.
	 * used to shutdown this thread
	 */
	public void destroyRecoverTask(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter destroyRecoverTask(SessionPermRecoveryTask)");
		destroyRecovery=true;
		LsSession destroySession=new TelnetSession();
		lsSessionRecoveryQueue.add(destroySession);
		if(isDebugEnabled)
			logger.debug("Leave destroyRecoverTask(SessionPermRecoveryTask)");
	}


	
}