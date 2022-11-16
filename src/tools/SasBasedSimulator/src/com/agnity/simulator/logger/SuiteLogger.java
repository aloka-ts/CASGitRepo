/**
 * 
 */
package com.agnity.simulator.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The Class SuiteLogger.
 * Logger class used by simulator to 
 * log test results and statistics
 *
 * @author saneja
 */
public class SuiteLogger extends Level {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 300000001L;
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SuiteLogger.class);

	/** The log enabled. */
	private boolean logEnabled;
	
	private static SuiteLogger suiteLogger;
	
	/**
	 * Instantiates a new command logger.
	 */
	private SuiteLogger() {
		super(200001, "SUITE_LOG", Level.ERROR_INT);
		setLogEnabled(true);
	}
	
	
	/**
	 * Gets the single instance of SuiteLogger.
	 *
	 * @return single instance of SuiteLogger
	 */
	public static synchronized SuiteLogger getInstance(){
		if(suiteLogger==null)
			suiteLogger=new SuiteLogger();
		return suiteLogger;
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
	 * Logs the message at SuiteLogger Level.
	 *
	 * @param message the message
	 */
	public boolean log(String message){
		if(isLogEnabled())
			logger.log(this, message);
		return isLogEnabled();
	}

}
