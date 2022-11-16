package com.agnity.sas.apps.domainobjects;

import com.agnity.sas.apps.util.SampleAppCallStateEnum;

/**
 * This class contains the call state information
 * @author saneja
 *
 */
		
public class SampleAppCallStateInfo {

	/** The callState contain the previous status of the Call */
	SampleAppCallStateEnum prevState ;
	/** The callState contain the current status of the Call */
	SampleAppCallStateEnum currState ;
	
	/** this will represent the current event */
	String crntEvent ;
	
	public SampleAppCallStateEnum getPrevState() {
		return prevState;
	}
	public void setPrevState(SampleAppCallStateEnum prevState) {
		this.prevState = prevState;
	}
	public SampleAppCallStateEnum getCurrState() {
		return currState;
	}
	public void setCurrState(SampleAppCallStateEnum currState) {
		this.prevState=this.getCurrState();
		this.currState = currState;
	}
	public String getCrntEvent() {
		return crntEvent;
	}
	public void setCrntEvent(String crntEvent) {
		this.crntEvent = crntEvent;
	}
	
}
