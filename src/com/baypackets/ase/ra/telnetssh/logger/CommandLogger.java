/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The Class CommandLogger.
 * Logger class used by RA to 
 * log LS command and respective results
 *
 * @author saneja
 */
public class CommandLogger extends Level {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 300000001L;
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(CommandLogger.class);

	/** The log enabled. */
	private boolean logEnabled;
	
	private static CommandLogger commandLogger;
	
	/**
	 * Instantiates a new command logger.
	 */
	private CommandLogger() {
		super(100001, "COMMAND_LOG", Level.ERROR_INT);
		setLogEnabled(true);
	}
	
	
	/**
	 * Gets the single instance of CommandLogger.
	 *
	 * @return single instance of CommandLogger
	 */
	public static synchronized CommandLogger getInstance(){
		if(commandLogger==null)
			commandLogger=new CommandLogger();
		return commandLogger;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
	
	/**
	 * Sets the log enabled.
	 *
	 * @param logEnabled the logEnabled to set
	 */
	public void setLogEnabled(boolean logEnabled) {
		this.logEnabled = logEnabled;
	}
	
	/**
	 * Checks if is log enabled.
	 *
	 * @return the logEnabled
	 */
	public boolean isLogEnabled() {
		return logEnabled;
	}
	
	/**
	 * Logs the message at CommandLogger Level.
	 *
	 * @param message the message
	 */
	public boolean log(String message){
		if(isLogEnabled())
			logger.log(this, message);
		return isLogEnabled();
	}

}
