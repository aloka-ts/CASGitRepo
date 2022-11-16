/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.utils;

/**
 * The Class RaProperties.
 * Contains values of configurable properties
 *
 * @author saneja
 */
public class RaProperties {
	
	/** The de queue thread load factor. */
	private int deQueueThreadLoadFactor;
	
	/** The command log enabled. */
	private boolean commandLogEnabled;
	
	/** The output delim. */
	private String outputDelim;
	
	/** The suppress command. */
	private String suppressCommand;
	
	/** The delim resptimer. */
	private int delimRespTimer;
	
	/** The telnet prompt. */
	private String telnetPrompt;
	
	/** The resp seperator. */
	private String respSeperator;
	
	/**connection timeout..*/
	private int connectTimeout;
	
	/**
	 * Timer after which RA needs to send the keep alive command
	 */
	private long keepAliveTimer;
	
	/** The Keep alive command. */
	private String keepAliveCommand;
	
	/** The Keep alive command. */
	private int keepAliveFailedAttempts;
	
	//CR UAT-1219 Changes
	/**
	 * RA would wait for this much time after login, before 
	 * sending the suppress command
	 */
	private long waitToSuppressCommand;

	/**
	 * Suppress Command Timeout
	 */
	private int suppressCommandTimeout;
	
	/**
	 * Suppress Command Delim
	 */
	private String suppressCommandDelim;
	
	/**
	 * Maximum Login Attempts for LS
	 */
	private int lsMaxLoginAttempts;
	
	private int longRecoveryPeriod;
	
	/**
	 * Logging period in seconds for LS queue logging.
	 */
	private int lsQueueLoggingPeriod;
	/**
	 * Boolean to indicate telnet ssh ra running in local environment.
	 */
	private boolean localEnvironment;
	
	/**
	 * Checks if is command log enabled.
	 *
	 * @return the commandLogEnabled
	 */
	public boolean isCommandLogEnabled() {
		return commandLogEnabled;
	}

	/**
	 * Sets the command log enabled.
	 *
	 * @param commandLogEnabled the commandLogEnabled to set
	 */
	public void setCommandLogEnabled(boolean commandLogEnabled) {
		this.commandLogEnabled = commandLogEnabled;
	}

	/**
	 * Gets the output delim.
	 *
	 * @return the outputDelim
	 */
	public String getOutputDelim() {
		return outputDelim;
	}

	/**
	 * Sets the output delim.
	 *
	 * @param outputDelim the outputDelim to set
	 */
	public void setOutputDelim(String outputDelim) {
		this.outputDelim = outputDelim;
	}

	/**
	 * Gets the suppress command.
	 *
	 * @return the suppressCommand
	 */
	public String getSuppressCommand() {
		return suppressCommand;
	}

	/**
	 * Sets the suppress command.
	 *
	 * @param suppressCommand the suppressCommand to set
	 */
	public void setSuppressCommand(String suppressCommand) {
		this.suppressCommand = suppressCommand;
	}

	/**
	 * Gets the delim resptimer.
	 *
	 * @return the delimResptimer
	 */
	public int getDelimRespTimer() {
		return delimRespTimer;
	}

	/**
	 * Sets the delim resptimer.
	 *
	 * @param delimRespTimer the new delim resp timer
	 */
	public void setDelimRespTimer(int delimRespTimer) {
		this.delimRespTimer = delimRespTimer;
	}

	/**
	 * Gets the telnet prompt.
	 *
	 * @return the telnetPrompt
	 */
	public String getTelnetPrompt() {
		return telnetPrompt;
	}

	/**
	 * Sets the telnet prompt.
	 *
	 * @param telnetPrompt the telnetPrompt to set
	 */
	public void setTelnetPrompt(String telnetPrompt) {
		this.telnetPrompt = telnetPrompt;
	}

	/**
	 * Sets the de queue thread load factor.
	 *
	 * @param deQueueThreadLoadFactor the deQueueThreadLoadFactor to set
	 */
	public void setDeQueueThreadLoadFactor(int deQueueThreadLoadFactor) {
		this.deQueueThreadLoadFactor = deQueueThreadLoadFactor;
	}

	/**
	 * Gets the de queue thread load factor.
	 *
	 * @return the deQueueThreadLoadFactor
	 */
	public int getDeQueueThreadLoadFactor() {
		return deQueueThreadLoadFactor;
	}

	/**
	 * Sets the resp seperator.
	 *
	 * @param respSeperator the respSeperator to set
	 */
	public void setRespSeperator(String respSeperator) {
		this.respSeperator = respSeperator;
	}

	/**
	 * Gets the resp seperator.
	 *
	 * @return the respSeperator
	 */
	public String getRespSeperator() {
		return respSeperator;
	}

	/**
	 * @param connectTimeout the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}
	/**
	 * @return the keepAliveTimer
	 */
	public long getKeepAliveTimer() {
		return keepAliveTimer;
	}

	/**
	 * @param keepAliveTimer the keepAliveTimer to set
	 */
	public void setKeepAliveTimer(long keepAliveTimer) {
		this.keepAliveTimer = keepAliveTimer;
	}
	/**
	 * @return the keepAliveCommand
	 */
	public String getKeepAliveCommand() {
		return keepAliveCommand;
	}

	/**
	 * @param keepAliveCommand the keepAliveCommand to set
	 */
	public void setKeepAliveCommand(String keepAliveCommand) {
		this.keepAliveCommand = keepAliveCommand;
	}
	/**
	 * @return the keepAliveFailedAttempts
	 */
	public int getKeepAliveFailedAttempts() {
		return keepAliveFailedAttempts;
	}
	/**
	 * @param keepAliveFailedAttempts the keepAliveFailedAttempts to set
	 */
	public void setKeepAliveFailedAttempts(int keepAliveFailedAttempts) {
		this.keepAliveFailedAttempts = keepAliveFailedAttempts;
	}

	/**
	 * @return the waitToSuppressCommand
	 */
	public long getWaitToSuppressCommand() {
		return waitToSuppressCommand;
	}
	/**
	 * @param waitToSuppressCommand the waitToSuppressCommand to set
	 */
	public void setWaitToSuppressCommand(long waitToSuppressCommand) {
		this.waitToSuppressCommand = waitToSuppressCommand;
	}

	/**
	 * @return the suppressCommandTimeout
	 */
	public int getSuppressCommandTimeout() {
		return suppressCommandTimeout;
	}

	/**
	 * @param suppressCommandTimeout the suppressCommandTimeout to set
	 */
	public void setSuppressCommandTimeout(int suppressCommandTimeout) {
		this.suppressCommandTimeout = suppressCommandTimeout;
	}
	/**
	 * @return the suppressCommandDelim
	 */
	public String getSuppressCommandDelim() {
		return suppressCommandDelim;
	}

	/**
	 * @param suppressCommandDelim the suppressCommandDelim to set
	 */
	public void setSuppressCommandDelim(String suppressCommandDelim) {
		this.suppressCommandDelim = suppressCommandDelim;
	}
	
	/**
	 * @return the lsMaxLoginAttempts
	 */
	public int getLsMaxLoginAttempts() {
		return lsMaxLoginAttempts;
	}
	/**
	 * @param lsMaxLoginAttempts the lsMaxLoginAttempts to set
	 */
	public void setLsMaxLoginAttempts(int lsMaxLoginAttempts) {
		this.lsMaxLoginAttempts = lsMaxLoginAttempts;
	}
	
	public int getLongRecoveryPeriod() {
		return longRecoveryPeriod;
	}

	public void setLongRecoveryPeriod(int longRecoveryPeriod) {
		this.longRecoveryPeriod = longRecoveryPeriod;
	}

	public int getLsQueueLoggingPeriod() {
		return lsQueueLoggingPeriod;
	}

	public void setLsQueueLoggingPeriod(int lsQueueLoggingPeriod) {
		this.lsQueueLoggingPeriod = lsQueueLoggingPeriod;
	}

	public boolean isLocalEnvironment() {
		return localEnvironment;
	}

	public void setLocalEnvironment(boolean localEnvironment) {
		this.localEnvironment = localEnvironment;
	}

	
}
