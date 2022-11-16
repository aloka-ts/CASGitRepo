/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.message.LsRequest;

/**
 * The Class LsReaderTask.
 * implements Runnable
 * This class will run dequeue thread on Queue
 *
 * @author saneja
 */
public class LsReaderTask implements Runnable {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsReaderTask.class);

	private LsRequest lsRequest;
	
	private LsSession lsSession;
	
	private String command;
	


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(logger.isInfoEnabled())
			logger.info("Enter Reader thread on lsId:::"+lsSession.getLs().getLsId());
		//read response
		boolean isRequest=lsRequest != null?true:false;
		
		LsResult result=lsSession.readResponse(isRequest);
		//handle response
		boolean status = false;
		if (lsRequest != null)
			status = LsManager.getInstance().handleResult(lsRequest, result);
		else
			status = LsManager.getInstance().handleResult(lsSession, command, result);
		//set session available
		if(status)
			lsSession.markAvailable();

		if(logger.isInfoEnabled())
			logger.info("Leave Reader thread on lsId:::"+lsSession.getLs().getLsId());
	}

	/**
	 * @param lsrequest the lsrequest to set
	 */
	public void setLsRequest(LsRequest lsRequest) {
		this.lsRequest = lsRequest;
	}

	/**
	 * @return the lsrequest
	 */
	public LsRequest getLsRequest() {
		return lsRequest;
	}

	/**
	 * @param lsSession the lsSession to set
	 */
	public void setLsSession(LsSession lsSession) {
		this.lsSession = lsSession;
	}

	/**
	 * @return the lsSession
	 */
	public LsSession getLsSession() {
		return lsSession;
	}
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

}
