package com.agnity.simulator.domainobjects;


public class WinMsgValues{
	
	int dialogueId;
	int invokeId;
			
	public WinMsgValues(int dialogueId, int invokeId) {
		super();
		this.dialogueId = dialogueId;
		this.invokeId = invokeId;
	}
	
	/**
	 * @return the dialogueId
	 */
	public int getDialogueId() {
		return dialogueId;
	}
	/**
	 * @param dialogueId the dialogueId to set
	 */
	public void setDialogueId(int dialogueId) {
		this.dialogueId = dialogueId;
	}
	/**
	 * @return the invokeId
	 */
	public int getInvokeId() {
		return invokeId;
	}
	/**
	 * @param invokeId the invokeId to set
	 */
	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}
	
	
	
}