package com.camel.CAPMsg;

/**
 * This class contains the call state information
 * @author nkumar
 *
 */
		
public class SasCapCallStateInfo {

	/** The callState contain the previous status of the Call */
	SasCapCallStateEnum prevState ;
	/** The callState contain the current status of the Call */
	SasCapCallStateEnum currState ;
	
	/** this will represent the current event */
	String crntEvent ;
	
	public SasCapCallStateEnum getPrevState() {
		return prevState;
	}
	public void setPrevState(SasCapCallStateEnum prevState) {
		this.prevState = prevState;
	}
	public SasCapCallStateEnum getCurrState() {
		return currState;
	}
	public void setCurrState(SasCapCallStateEnum currState) {
		this.currState = currState;
	}
	public String getCrntEvent() {
		return crntEvent;
	}
	public void setCrntEvent(String crntEvent) {
		this.crntEvent = crntEvent;
	}
	
}
