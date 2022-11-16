package com.agnity.simulator.callflowadaptor.element.type;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;

public class CleanUpNode extends Node {
	public CleanUpNode() {
		super(Constants.CleanUp_NODE);
		this.setAction(Constants.NO_ACTION);
	}
	
	private String command;

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