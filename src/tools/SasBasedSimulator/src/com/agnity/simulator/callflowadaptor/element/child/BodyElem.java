package com.agnity.simulator.callflowadaptor.element.child;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public class BodyElem extends Node{
	
	private String bodyType;
	
	private String sdp;
	


	public BodyElem() {
		super(Constants.BODY, true);
	}


	@Override
	public String toString() {
		
		return super.toString();
	}


	/**
	 * @param bodyType the bodyType to set
	 */
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}


	/**
	 * @return the bodyType
	 */
	public String getBodyType() {
		return bodyType;
	}
	

	/**
	 * @return the sdp
	 */
	public String getSdp() {
		return sdp;
	}


	/**
	 * @param sdp the sdp to set
	 */
	public void setSdp(String sdp) {
		this.sdp = sdp;
	}
	
}
