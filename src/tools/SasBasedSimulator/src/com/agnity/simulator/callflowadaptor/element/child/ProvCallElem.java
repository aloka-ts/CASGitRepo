package com.agnity.simulator.callflowadaptor.element.child;

import java.util.concurrent.atomic.AtomicLong;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class ProvCallElem extends Node{
	
	private String cmmndName;
	
	private String command;
	//private String nov;
	//private int incrementBy;
	//AtomicLong longVal;
	
	public ProvCallElem() {
		super(Constants.PROVCALL,true);
		/*longVal=null;
		this.nov = Constants.NOV_STATIC;
		incrementBy =1;*/
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


	public String getCmmndName() {
		return cmmndName;
	}


	public void setCmmndName(String cmmndName) {
		this.cmmndName = cmmndName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.PROVCALL);
		builder.append("  ");
		
		builder.append(" commandName='");
		builder.append(cmmndName);
		builder.append("'");
		
		builder.append(" command to execute='");
		builder.append(command);
		builder.append("'");
						
		builder.append(super.toString());
		
		return builder.toString();
	}

/*
	/**
	 * @param incrementBy the incrementBy to set
	 */
	/*/*	public void setIncrementBy(int incrementBy) {
		this.incrementBy = incrementBy;
	}
	*/

	/**
	 * @return the incrementBy
	 */
	/*	public int getIncrementBy() {
		return incrementBy;
	}
	
	*/
}
