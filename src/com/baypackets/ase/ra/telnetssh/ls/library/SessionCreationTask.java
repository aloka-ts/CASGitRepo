/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls.library;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.ls.LsSession;

/**
 * The Class SessionCreationTask.
 * Implements runnable
 * Task for creating session with LS.
 * Started by LsManager
 *
 * @author saneja
 */
public class SessionCreationTask implements Runnable {
	
	/**
	 * Enum defines operation for this task
	 * @author abaxi
	 *
	 */
	public enum Operation {
		START_LS,UPDATE_LS
	}
	
	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(SessionCreationTask.class);
	
	/** The ls. */
	private LS ls;
	
	/** The fail alarm sent. */
	private boolean failAlarmSent=false;
	
	/** Operation enum specifies operation type*/
	private Operation operation=Operation.UPDATE_LS;
	
	public SessionCreationTask(Operation operation) {
		if(operation!=null){
			this.operation=operation;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("Enter SessionCreationTask run()-->Thread started   lsId::"+ls.getLsId());
		createSession();
		if(logger.isDebugEnabled())
			logger.debug("Leaving SessionCreationTask run()-->Thread Completed   lsId:::"+ls.getLsId());
	}
	
	/**
	 * Creates the session with LS.
	 */
	private void createSession(){
		if(logger.isDebugEnabled())
			logger.debug("Enter createSession() for LSId::"+ls.getLsId());
		LsSession lsSession=null;
		String connType=ls.getConnType();
		LsManager lsManager=LsManager.getInstance();
		if(connType!=null && connType.equalsIgnoreCase("telnet")){
			if(logger.isDebugEnabled())
				logger.debug("TelnetSession)");
			lsSession=new TelnetSession(); 
		}else if(connType!=null && connType.equalsIgnoreCase("ssh")){
			if(logger.isDebugEnabled())
				logger.debug("sshSession");
			lsSession=new SshSession();
		}else{
			logger.error("Unknown Sesison Type for LS lsiD::"+ls.getLsId()+"  ConnectionType:::"+ls.getConnType());
			throw new RuntimeException("Unknown session type for LS lsiD::"+ls.getLsId()+"  ConnectionType:::"+ls.getConnType());
		}
		lsSession.setLs(ls);
		lsSession.setFailAlarmSent(failAlarmSent);
		logger.debug("Before session Creation on LsId::"+ls.getLsId());
		lsSession.startSession();
		//setting ls Session MAp
		
		if(this.operation==Operation.START_LS){
			logger.debug("New session created try to increment size");			
			int size=lsManager.getLsIdSessionMap().size();
			int loadFactor=lsManager.getRaProperties().getDeQueueThreadLoadFactor();
			if(size % loadFactor ==0){
				logger.debug("New session created going increment size");		
				lsManager.getReaderThreadPool().incrementPoolSize(1);
			}			
		}
		lsManager.getLsIdSessionMap().put(ls.getLsId(), lsSession);
		//reader thread pool. size
		logger.debug("After session Creation on LsId::"+ls.getLsId());
		if(logger.isDebugEnabled())
			logger.debug("Leaving createSession()lsId:::"+ls.getLsId());
	}

	/**
	 * Sets the ls.
	 *
	 * @param ls the ls to set
	 */
	public void setLs(LS ls) {
		this.ls = ls;
	}

	/**
	 * Gets the ls.
	 *
	 * @return the ls
	 */
	public LS getLs() {
		return ls;
	}
	
	/**
	 * Checks if is fail alarm sent.
	 *
	 * @return the failAlarmSent
	 */
	public boolean isFailAlarmSent() {
		return failAlarmSent;
	}

	/**
	 * Sets the fail alarm sent.
	 *
	 * @param failAlarmSent the failAlarmSent to set
	 */
	public void setFailAlarmSent(boolean failAlarmSent) {
		this.failAlarmSent = failAlarmSent;
	}
	
}
