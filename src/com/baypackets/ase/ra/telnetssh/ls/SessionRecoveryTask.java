/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.ls.library.TelnetSession;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;

/**
 * The Class SessionRecoveryTask.
 * implements runnable. Its instance will be running as task in thread pool
 * 
 *
 * @author saneja
 */
public class SessionRecoveryTask implements Runnable {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(SessionRecoveryTask.class);

	/** The common ls config. */
	private CommonLsConfig commonLsConfig;

	/** The ls session queue. */
	private BlockingQueue<LsSession> lsSessionQueue;
	
	private boolean destroyRecovery=false;
	
	private BlockingQueue<LsSession> lsSessionRecoveryQueue;
	
	private SessionPermRecoveryTask sessionPermRecoveryTask;

	/**
	 * Sets the common ls config.
	 *
	 * @param commonLsConfig the commonLsConfig to set
	 */
	public void setCommonLsConfig(CommonLsConfig commonLsConfig) {
		this.commonLsConfig = commonLsConfig;
	}

	/**
	 * Gets the common ls config.
	 *
	 * @return the commonLsConfig
	 */
	public CommonLsConfig getCommonLsConfig() {
		return commonLsConfig;
	}

	/**
	 * Sets the ls session queue.
	 *
	 * @param lsSessionQueue the lsSessionQueue to set
	 */
	public void setLsSessionQueue(BlockingQueue<LsSession> lsSessionQueue) {
		this.lsSessionQueue = lsSessionQueue;
	}

	/**
	 * Instantiates a new session recovery task.
	 *
	 */
	public SessionRecoveryTask() {
		this.lsSessionQueue = new LinkedBlockingQueue<LsSession>();
		this.lsSessionRecoveryQueue = new LinkedBlockingQueue<LsSession>();
	}
	
	public SessionPermRecoveryTask getSessionPermRecoveryTask() {
		return sessionPermRecoveryTask;
	}

	public void setSessionPermRecoveryTask(
			SessionPermRecoveryTask sessionPermRecoveryTask) {
		this.sessionPermRecoveryTask = sessionPermRecoveryTask;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.debug("Enter RecoveryThread");
		recoverLsSession();
		if(isInfoEnabled)
			logger.debug("Leave Recovery thread");

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
			logger.debug("Enter addLsSession-->Adding LsSession to recovery lsId::"+lsSession.getLs().getLsId());
		lsSession.setLsStatus(LsStatus.LS_DOWN);
		lsSessionQueue.add(lsSession);
		if(isDebugEnabled)
			logger.debug("Leave addLsSession-->Added LsSession to recovery");
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
		int recoveryPeriod=commonLsConfig.getRecoveryPeriod();
		//CR UAT 1219 Changes
		RaProperties raProperties = LsManager.getInstance().getRaProperties();
		int lsMaxLoginAttempts = raProperties.getLsMaxLoginAttempts();
		LS ls = null;
		while(!destroyRecovery){	
			try {
				lsSession=lsSessionQueue.take();
			} catch (InterruptedException e) {
				logger.error("InterruptedException in Recovery get operation",e);
			}
			ls = lsSession.getLs();
			if(ls==null){
				logger.error("LsSession with null LS details next iteration");
				continue;
			}
			if (ls.getFailedReLogins() >= lsMaxLoginAttempts){
				logger.error("LOGIN RETRIES exceeded for LS::::" + ls.getLsId() + "Retries::::" + ls.getFailedReLogins());
				if (!ls.isUnderRecovery()){
					this.sessionPermRecoveryTask.addLsSession(lsSession);
					ls.setUnderRecovery(true);
				}
				continue;
			}
			lsDownTime=lsSession.getLsDownTimeStamp();
			lsDownTimeInMilliSecs=lsDownTime.getTime();
			currenTimeInMilliSecs=System.currentTimeMillis();
			if((currenTimeInMilliSecs - lsDownTimeInMilliSecs) < (recoveryPeriod*1000) ){
				try {
					Thread.sleep((recoveryPeriod*1000)-(currenTimeInMilliSecs - lsDownTimeInMilliSecs));
				} catch (InterruptedException e) {
					logger.error("InterruptedException in Sleep operation",e);
				}
			}
			synchronized (lsSession) {
				lsSession.startSession();	
			}	
		}
		if(isDebugEnabled)
			logger.debug("Leave recoverLsSession");
	}


	/**
	 * Destroy recover task.
	 * used to shutdown this thread
	 */
	public void destroyRecoverTask(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter destroyRecoverTask");
		destroyRecovery=true;
		LsSession destroySession=new TelnetSession();
		lsSessionQueue.add(destroySession);
		if(isDebugEnabled)
			logger.debug("Leave destroyRecoverTask");
	}

}
