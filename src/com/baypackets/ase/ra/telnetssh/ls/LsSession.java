/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

import java.util.Date;
import java.util.List;

import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;

/**
 * The Class LsSession.
 * Abstract defining LsSession
 * Need to be extended by classes 
 * Implementing telnet or ssh sessions with LS
 *
 * @author saneja
 */
public abstract class LsSession {

	/** The ls. */
	private LS ls;
	
	/** The ls status. */
	private LsStatus lsStatus;
	
	/** The prompt. */
	private String prompt;
	
	/** The ls down time stamp. */
	private Date lsDownTimeStamp;
	
	/** The failur alarm flag. */
	private boolean failAlarmSent;
	
	/** Counter to track number of times keep alive command failed. */
	private int keepAliveCommandFailedCtr = 0;
	
	/**
	 * Execute command. and returns the result or response.
	 * synchronous implemntation
	 *
	 * @param command the command
	 * @return the string
	 */
	public abstract List<String> executeCommand(String command); 
	
	/**
	 * send command to LS and return. with send status
	 * asynchronous implemntation.
	 * resp is return in seperate thread
	 *
	 * @param command the command
	 * @return CommandStatus enum value
	 */
	public abstract CommandStatus sendCommand(String command); 
	
	
	/**
	 * reads response for 
	 * asynchronous implemntation
	 *
	 * @param command the command
	 * @return the list of strings
	 */
	public abstract LsResult readResponse(boolean isRequest); 
	
	/**
	 * Start session.
	 */
	public abstract void startSession(); 
	
	/**
	 * Stop session.
	 */
	public abstract void stopSession();
	
	/**
	 * makes LsSession available for next message with asynchronous flow
	 */
	public abstract void markAvailable();
	
	public abstract LsResult readResponseKeepAliveCommand();
	
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
	 * Sets the ls status.
	 *
	 * @param lsStatus the lsStatus to set
	 */
	public void setLsStatus(LsStatus lsStatus) {
		this.lsStatus = lsStatus;
	}
	
	/**
	 * Gets the ls status.
	 *
	 * @return the lsStatus
	 */
	public LsStatus getLsStatus() {
		return lsStatus;
	}
	
	/**
	 * Sets the prompt.
	 *
	 * @param prompt the prompt to set
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	/**
	 * Gets the prompt.
	 *
	 * @return the prompt
	 */
	public String getPrompt() {
		return prompt;
	}
	
	/**
	 * Sets the ls down time stamp.
	 *
	 * @param lsDownTimeStamp the lsDownTimeStamp to set
	 */
	public void setLsDownTimeStamp(Date lsDownTimeStamp) {
		this.lsDownTimeStamp = lsDownTimeStamp;
	}
	
	/**
	 * Gets the ls down time stamp.
	 *
	 * @return the lsDownTimeStamp
	 */
	public Date getLsDownTimeStamp() {
		return lsDownTimeStamp;
	}

	/**
	 * 
	 */
	public LsSession() {
		super();
		this.setLsStatus(LsStatus.LS_DOWN);
	}

	/**
	 * @param failAlarmSent the failAlarmSent to set
	 */
	public void setFailAlarmSent(boolean failAlarmSent) {
		this.failAlarmSent = failAlarmSent;
	}

	/**
	 * @return the failAlarmSent
	 */
	public boolean isFailAlarmSent() {
		return failAlarmSent;
	}
	
	/**
	 * @return the keepAliveCommandFailedCtr
	 */
	public int getKeepAliveCommandFailedCtr() {
		return keepAliveCommandFailedCtr;
	}

	/**
	 * @param keepAliveCommandFailedCtr the keepAliveCommandFailedCtr to set
	 */
	public void setKeepAliveCommandFailedCtr(int keepAliveCommandFailedCtr) {
		this.keepAliveCommandFailedCtr = keepAliveCommandFailedCtr;
	}

	

	
	
}
