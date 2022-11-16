package com.agnity.simulator.callflowadaptor.element.type;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class TimerNode extends Node {
	public TimerNode() {
		super(Constants.TIMER_NODE);
		this.setAction(Constants.NO_ACTION);
	}
	
	/**
	 * @param DEFAULT_TCAP_SESSION_TIMEOUT the DEFAULT_TCAP_SESSION_TIMEOUT to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	/**
	 * @return the DEFAULT_TCAP_SESSION_TIMEOUT
	 */
	public int getTimeout() {
		return timeout;
	}
	
	//to handle message DEFAULT_TCAP_SESSION_TIMEOUT behavior
	private int timeout;

}
