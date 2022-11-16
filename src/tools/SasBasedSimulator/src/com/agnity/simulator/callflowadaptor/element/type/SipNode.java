package com.agnity.simulator.callflowadaptor.element.type;

import com.agnity.simulator.callflowadaptor.element.Node;


public abstract class SipNode extends Node {
	private boolean enableSdp;
	private boolean lastSavedSdp;
	//private boolean enableInfo;
	private boolean lastSavedInfo;
	
	private boolean reInvite;
	
	public SipNode(String type) {
		super(type);
	}
	/**
	 * @param enableSdp the enableSdp to set
	 */
	public void setEnableSdp(boolean enableSdp) {
		this.enableSdp = enableSdp;
	}
	/**
	 * @return the enableSdp
	 */
	public boolean isEnableSdp() {
		return enableSdp;
	}
	
	/**
	 * @return the enableSdp
	 */
	public boolean isLastSavedSdp() {
		return lastSavedSdp;
	}
	/**
	 * @param enableSdp the enableSdp to set
	 */
	public void setLastSavedSdp(boolean lastSavedSdp) {
		this.lastSavedSdp = lastSavedSdp;
	}
	
	
	
	/**
	 * @param enableInfo the enableInfo to set
	 */
	/*public void setEnableInfo(boolean enableInfo) {
		this.enableInfo = enableInfo;
	}*/
	/**
	 * @return the enableInfo
	 */
	/*public boolean isEnableInfo() {
		return enableInfo;
	}*/
	
	/**
	 * @return the enableInfo
	 */
	public boolean isLastSavedInfo() {
		return lastSavedInfo;
	}
	/**
	 * @param enableInfo the enableInfo to set
	 */
	public void setLastSavedInfo(boolean lastSavedInfo) {
		this.lastSavedInfo = lastSavedInfo;
	}
	/**
	 * @return the reInvite
	 */
	public boolean isReInvite() {
		return reInvite;
	}
	/**
	 * @param reInvite the reInvite to set
	 */
	public void setReInvite(boolean reInvite) {
		this.reInvite = reInvite;
	}
}
