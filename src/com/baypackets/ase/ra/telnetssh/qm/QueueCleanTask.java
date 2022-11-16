/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.ls.LsSession;

/**
 * The Class QueueCleanTask.
 * Implements Runnable
 * This class will run thread to clean Q on LS.
 *
 * @author saneja
 */
public class QueueCleanTask implements Runnable {

	private static final Logger logger=Logger.getLogger(QueueCleanTask.class);

	/** queue reference. */
	private LsQueue lsQueue;	
	
	/** 
	 * LsSession which needs to be enabled for further requests
	 */
	private LsSession lsSession;
	
	/**
	 * 
	 * @return LsSession
	 */
	public LsSession getLsSession() {
		return lsSession;
	}
	/**
	 * @param LsSession the lsSession
	 */
	public void setLsSession(LsSession lsSession) {
		this.lsSession = lsSession;
	}

	/**
	 * Instantiates a new work manager.
	 *
	 * @param lsQueue the queue
	 */
	public QueueCleanTask(LsQueue lsQueue) {
		this.lsQueue=lsQueue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("Enter Q clean Task");
		QueueManager qm= QueueManagerImpl.getInstance();
		qm.cleanQ(lsQueue);
		if (lsSession != null){
			logger.error("Queue is cleaned, marking LS Session available");
			lsSession.markAvailable();
		}
		if(logger.isDebugEnabled())
			logger.debug("Leave Q clean Task");
	}

}