package com.agnity.simulator.callflowadaptor.element.type;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;

public class StartNode extends Node {
	private String flowType;
	private boolean supportsReliable;
	
	/**
	 * @param flowType the flowType to set
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	/**
	 * @return the flowType
	 */
	public String getFlowType() {
		return flowType;
	}
	
	public StartNode(){
		super(Constants.START);
		this.setAction(Constants.NO_ACTION);
		this.setSupportsReliable(false);
		super.setInitial(true);
	}

	/**
	 * @param supportsReliable the supportsReliable to set
	 */
	public void setSupportsReliable(boolean supportsReliable) {
		this.supportsReliable = supportsReliable;
	}

	/**
	 * @return the supportsReliable
	 */
	public boolean isSupportsReliable() {
		return supportsReliable;
	}

	
	
	
}
