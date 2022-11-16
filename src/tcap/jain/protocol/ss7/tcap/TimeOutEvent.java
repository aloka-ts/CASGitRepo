package jain.protocol.ss7.tcap;

public class TimeOutEvent {
	
	/** It will represent the timertype.
	 * 1-ActivityTest
	 */
	Integer timerType ;
	
	Integer dialogueId ;

	public Integer getTimerType() {
		return timerType;
	}

	public void setTimerType(Integer timerType) {
		this.timerType = timerType;
	}

	public Integer getDialogueId() {
		return dialogueId;
	}

	public void setDialogueId(Integer dialogueId) {
		this.dialogueId = dialogueId;
	}
	
	
	
}
