package com.baypackets.ase.ra.telnetssh.ls;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.logger.CommandLogger;
import com.baypackets.ase.ra.telnetssh.qm.LsQueue;
import com.baypackets.ase.ra.telnetssh.qm.QueueCleanTask;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.qm.QueueManagerImpl;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;

/**
 * The Class KeepAliveTask.
 * implements runnable. Its instance will be running as task in thread pool
 * 
 *
 * @author pgandhi
 */
public class KeepAliveTask implements Runnable {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(KeepAliveTask.class);

	/** The ls session queue. */
	private Collection<LsSession> lsSessionCollection = null;
	
	private LsManager lsManager = LsManager.getInstance();

	private boolean destroyKeepAlive = false;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.debug("Enter KeepAliveTask Thread");
		keepAliveLsSession();
		if(isInfoEnabled)
			logger.debug("Leave KeepAliveTask thread");

	}
	
	/**
	 * Keep Alive ls session.
	 * After Configured period of time, RA sends the keep alive command.
	 * Attempts to recover LS session if recovery period for LS has expierd
	 * Calls starts session on LSSession
	 */
	private void keepAliveLsSession(){
		boolean isDebugEnabled = logger.isDebugEnabled();
		RaProperties raProperties = lsManager.getRaProperties();
		long keepAliveTime = raProperties.getKeepAliveTimer();
		String command = raProperties.getKeepAliveCommand();
		if(isDebugEnabled){
			logger.debug("keepAliveTime: " + keepAliveTime + " command: " + command);
		}
		QueueManagerImpl qm = (QueueManagerImpl) QueueManagerImpl.getInstance();
		LsQueue lsQueue = null;
		QueueCleanTask queueCleanTask = null;
		ThreadPoolManager sessionThreadPool = lsManager.getSessionPool();
		ThreadPoolManager readerThreadPool = lsManager.getReaderThreadPool();
		while(!destroyKeepAlive){
			try {
				Thread.sleep(keepAliveTime*1000);
			} catch (InterruptedException e) {
				logger.error("Null input on LsInteractionTask deleteLsQ()",e);			
			}
			lsSessionCollection = lsManager.getLsIdSessionMap().values();
			LS ls = null;
			int lsId = 0;
			for (LsSession session : lsSessionCollection){
				ls = session.getLs();
				if (session.getLsStatus() == LsStatus.LS_DOWN){
					continue;
				}
				lsId = ls.getLsId();
				
				if(CommandLogger.getInstance().isLogEnabled()){
					CommandLogger.getInstance().log("Keep Alive Command Send--> on LsId::'"+ lsId +
						"'  Command::'"+command+"'");
				}
				
				CommandStatus status = session.sendCommand(command);
				
				
				if(status.equals(CommandStatus.SEND_ERROR)){
					logger.error("KeepAliveTask keepAliveLsSession()-->Error in send command " + command + " lsId::" + lsId);
					lsQueue = qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
					queueCleanTask = new QueueCleanTask(lsQueue);
					//This is added to mark ls session available after cleaning the queue.
					queueCleanTask.setLsSession(session);
					if(isDebugEnabled)
						logger.debug("KeepAliveTask keepAliveLsSession()-->starting Q clean Task on LsId::"+ lsId);
					sessionThreadPool.submitTask(queueCleanTask);
				}else if(status.equals(CommandStatus.SEND_FAIL)){  //if fail due to busy LS
					logger.error("KeepAliveTask keepAliveLsSession()-->Command already sent, LS is not available for execution LsId::"+ lsId);
				}else if(status.equals(CommandStatus.SEND_SUCCESS)){
					if(isDebugEnabled)
						logger.debug("LsManager sendRequest()-->Enque success");
					LsReaderTask readTask = new LsReaderTask();
					readTask.setLsSession(session);
					readTask.setCommand(command);
					readerThreadPool.submitTask(readTask);
				}
			}
		}
	}
	public boolean isDestroyKeepAlive() {
		return destroyKeepAlive;
	}

	public void setDestroyKeepAlive(boolean destroyKeepAlive) {
		this.destroyKeepAlive = destroyKeepAlive;
	}
	public Collection<LsSession> getLsSessionCollection() {
		return lsSessionCollection;
	}

	public void setLsSessionCollection(Collection<LsSession> lsSessionCollection) {
		this.lsSessionCollection = lsSessionCollection;
	}


}
