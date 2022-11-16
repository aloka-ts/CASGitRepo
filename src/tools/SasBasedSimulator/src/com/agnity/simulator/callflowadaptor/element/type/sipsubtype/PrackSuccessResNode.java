package com.agnity.simulator.callflowadaptor.element.type.sipsubtype;

import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.utils.Constants;

public class PrackSuccessResNode extends SipNode {
	private String message;
	
	public PrackSuccessResNode() {
		super(Constants.PRACK_SUCCESS_RES_NODE);
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
