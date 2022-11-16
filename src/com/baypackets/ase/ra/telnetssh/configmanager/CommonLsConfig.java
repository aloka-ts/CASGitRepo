/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager;

/**
 * The Class CommonLSConfig.
 * Contains common configuration specified by LS
 *
 * @author saneja
 */
public class CommonLsConfig {
	
	/** The id. Databse primary key*/
	private int Id;
	
	/** The recovery Period for LS after timeout. */
	private int recoveryPeriod;
	
	/** The no response timer value. */
	private int noResponseTimer;
	
	/** The number of re attempts allowed in case of timeout. */
	private int reAttempt;
	
	/**for update active_scp ver is changed to 0 and new row is inserted
	 * for delete oactive_scp_version is changed to 0
	 */
	private int activeScpVer;
	
	
	//saneja @bug 10179 [
	/** The command log enabled. */
	private boolean commandLogEnabled;
	
	/** The output delim. */
	private String outputDelim;
	
	/** The suppress command. */
	private String suppressCommand;
	
	/** The delim resptimer. */
	private int delimRespTimer;
	
	/** The telnet prompt. --future use*/
	private String telnetPrompt;
	
	/** The resp seperator. */
	private String respSeperator;
	
	/**connection timeout..--future use*/
	private int connectTimeout;
	
	//] closed saneja @bug 10179
	
	
	
	/**
	 * Instantiates a new lS param.
	 *
	 * @param recoveryPeriod the recovery period
	 * @param noResponseTimer the no response timer
	 * @param reAttempt the re attempt
	 */
	public CommonLsConfig(int recoveryPeriod, int noResponseTimer, int reAttempt) {
		this.recoveryPeriod = recoveryPeriod;
		this.noResponseTimer = noResponseTimer;
		this.reAttempt = reAttempt;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		Id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Gets the recovery period.
	 *
	 * @return the recoveryPeriod
	 */
	public int getRecoveryPeriod() {
		return recoveryPeriod;
	}


	/**
	 * Sets the recovery period.
	 *
	 * @param recoveryPeriod the recoveryPeriod to set
	 */
	public void setRecoveryPeriod(int recoveryPeriod) {
		this.recoveryPeriod = recoveryPeriod;
	}


	/**
	 * Gets the no response timer.
	 *
	 * @return the noResponseTimer
	 */
	public int getNoResponseTimer() {
		return noResponseTimer;
	}


	/**
	 * Sets the no response timer.
	 *
	 * @param noResponseTimer the noResponseTimer to set
	 */
	public void setNoResponseTimer(int noResponseTimer) {
		this.noResponseTimer = noResponseTimer;
	}


	/**
	 * Gets the re attempt.
	 *
	 * @return the reAttempt
	 */
	public int getReAttempt() {
		return reAttempt;
	}


	/**
	 * Sets the re attempt .
	 *
	 * @param reAttempt the reAttempt to set
	 */
	public void setReAttempt(int reAttempt) {
		this.reAttempt = reAttempt;
	}
	
	/**
	 * @param activeScpVer the activeScpVer to set
	 */
	public void setActiveScpVer(int activeScpVer) {
		this.activeScpVer = activeScpVer;
	}

	/**
	 * @return the activeScpVer
	 */
	public int getActiveScpVer() {
		return activeScpVer;
	}

	//saneja @bug 10179 [
	/**
	 * @return the commandLogEnabled
	 */
	public boolean isCommandLogEnabled() {
		return commandLogEnabled;
	}

	/**
	 * @param commandLogEnabled the commandLogEnabled to set
	 */
	public void setCommandLogEnabled(boolean commandLogEnabled) {
		this.commandLogEnabled = commandLogEnabled;
	}

	/**
	 * @return the outputDelim
	 */
	public String getOutputDelim() {
		return outputDelim;
	}

	/**
	 * @param outputDelim the outputDelim to set
	 */
	public void setOutputDelim(String outputDelim) {
		this.outputDelim = outputDelim;
	}

	/**
	 * @return the suppressCommand
	 */
	public String getSuppressCommand() {
		return suppressCommand;
	}

	/**
	 * @param suppressCommand the suppressCommand to set
	 */
	public void setSuppressCommand(String suppressCommand) {
		this.suppressCommand = suppressCommand;
	}

	/**
	 * @return the delimRespTimer
	 */
	public int getDelimRespTimer() {
		return delimRespTimer;
	}

	/**
	 * @param delimRespTimer the delimRespTimer to set
	 */
	public void setDelimRespTimer(int delimRespTimer) {
		this.delimRespTimer = delimRespTimer;
	}

	/**
	 * @return the telnetPrompt
	 */
	public String getTelnetPrompt() {
		return telnetPrompt;
	}

	/**
	 * @param telnetPrompt the telnetPrompt to set
	 */
	public void setTelnetPrompt(String telnetPrompt) {
		this.telnetPrompt = telnetPrompt;
	}

	/**
	 * @return the respSeperator
	 */
	public String getRespSeperator() {
		return respSeperator;
	}

	/**
	 * @param respSeperator the respSeperator to set
	 */
	public void setRespSeperator(String respSeperator) {
		this.respSeperator = respSeperator;
	}

	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	//]closed saneja @bug 10179
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		//saneja @bug 10179 [
		//return "RecoveryPeriod: "+recoveryPeriod+"  reAttempts: "+reAttempt+"  noResponseTimer: "+noResponseTimer;
		return "RecoveryPeriod: "+recoveryPeriod+"  reAttempts: "+reAttempt+"  noResponseTimer: "+noResponseTimer+
		"  commandLogEnabled:"+commandLogEnabled+"  outputDelim:"+outputDelim+"  suppressCommand:"+suppressCommand+
		"  delimRespTimer:"+delimRespTimer+"  telnetPrompt:"+telnetPrompt+"  respSeperator:"+respSeperator+
		"  connectTimeout:"+connectTimeout;
		//]closed saneja @bug 10179
	}

}
