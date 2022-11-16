package com.baypackets.ase.monitor;

import org.apache.log4j.Logger;

import com.baypackets.ase.monitor.CallStatsHolder.CallState;

/**
 * This class stores the call statistics for each call.
 * An object of this class is created for every call 	
 * @author Mithil Aggarwal
 */

public class CallStatistics {

	private static Logger _logger = Logger.getLogger(CallStatistics.class);

	private boolean isCallCompleted = false;

	private long callBeginTime = 0;

	private long callEndTime = 0;
	
	private long callHoldTime ;

	private CallState callState = CallState.NONE;

	public long getCallBeginTime() {
		return callBeginTime;
	}


	public void setCallCompleted(boolean isCallCompleted) {
		this.isCallCompleted = isCallCompleted;
	}
	
	/*
	 * Calculates the call Hold Time for the call
	 */

	public void calculateCallHoldTime(){

		if(this.isCallCompleted){
			if(this.callEndTime != 0){ // Check to ignore the call hold time for stuck calls. LEV-1535
				this.callHoldTime = (this.callEndTime - this.callBeginTime);
			}else{
				this.callHoldTime = 0;
			}
		}else{
			this.callHoldTime = (System.nanoTime() - this.callBeginTime);
		}

		CallStatsHolder.totalCallHoldTime += this.callHoldTime;
		CallStatsHolder.totalCallsOnHold.increment();
		if(_logger.isDebugEnabled()){
			_logger.debug("Total Call Hold Time(in nanoseconds) : " + CallStatsHolder.totalCallHoldTime 
							+ "  Call Hold Time (in nanoseconds) : " + callHoldTime);
			_logger.debug("Total Calls on Hold ::  " + CallStatsHolder.totalCallsOnHold.intValue());
		}
	}	

	public boolean isCallCompleted(){
		return isCallCompleted;
	}

	public CallState getCallState(){
		return callState;
	}

	public void setCallState(CallState callState){
		this.callState = callState;
	}

	public void setCallBeginTime() {

		if(_logger.isDebugEnabled()){
			_logger.debug("Call Begin Time Set ");
		}
		this.callBeginTime = System.nanoTime();
	}

	public void setCallEndTime() {

		if(_logger.isDebugEnabled()){
			_logger.debug("Call End Time Set ");
		}
		this.callEndTime = System.nanoTime();

	}

	
}
