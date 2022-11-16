/****
  Copyright (c) 2015 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 

  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.
 ****/


package com.baypackets.ase.monitor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.baypackets.ase.common.LongAdder;
import com.baypackets.ase.common.Registry;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;

/**
 * This class stores the data stats collected for the call traffic 	
 * @author Mithil Aggarwal
 */

public class CallStatsHolder {

	private static Logger _logger = Logger.getLogger(CallStatsHolder.class);

	public List<CallStatistics> callStatisticsObjList = Collections.synchronizedList(new LinkedList<CallStatistics>());
	
	public CallTransactionsNotifier callTransNotifier=null;

//	protected LongAdder newCallCount = new LongAdder();
	
//	private LongAdder networkTransactionsCount=new LongAdder();
//	
//	private LongAdder aggregatedTransactionsCount=new LongAdder();
//	
//	private float networkTransactionsPerSec;
//	
//	private float aggregatedTransactionsPerSecond;
	
	public CallTransactionsNotifier getCallTransNotifier() {
		return callTransNotifier;
	}

	public void setCallTransNotifier(CallTransactionsNotifier callTransNotifier) {
		this.callTransNotifier = callTransNotifier;
	}



	private LongAdder currentInProgressCallCount = new LongAdder();

	private OverloadControlManager ocm;

	private static LongAdder totalInProgressCallCount = new LongAdder();

	/*Thread safe as the variables totalCallsOnHold and  totalCallHoldTime used only from the process() method 
	 * in CallStatsProcessor on which the AseBackgroundListener thread works */
	protected static LongAdder totalCallsOnHold = new LongAdder();

	protected static long totalCallHoldTime ;
	
	private static float avgCallHoldTime;
	
//	private static float newCallsPerSecond;

	
	public enum CallState {
		NONE,NEW, IN_PROGRESS,END_PROGRESS,CONNECTED,DISCONNECTED,END 
	}


	public CallStatsHolder() {
		
		this.ocm = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
		if(_logger.isDebugEnabled()) _logger.debug("CallStatsHolder object created");
	}

	/*
	 * Increments the counter when a new call is received
	 */
//	public void incrementNewCallCount(AseApplicationSession appSession){
//
//		if(appSession == null){
//			if(_logger.isDebugEnabled()){
//				_logger.debug("New Call Count incremented from current count " +newCallCount);
//			}
//			newCallCount.increment();
//		}else{
//			CallStatistics callStatistics = appSession.getCallStatistics();
//			// In case of FT, Call Statistics may be null
//			if(callStatistics == null){
//				if(_logger.isDebugEnabled()){
//					_logger.debug("Call Statistics object not available. Returning");
//				}
//				return;
//			}
//			Iterator it=appSession.getSessions(AseStrings.PROTOCOL_SIP);
//			int sessionCount=0;
//			while(it.hasNext()){
//				it.next();
//				sessionCount++;
//				if(sessionCount>1){
//					break;
//				}
//			}
//
//			if(callStatistics.getCallState() == CallState.NONE && sessionCount==1){
//				newCallCount.increment();
//				if(_logger.isDebugEnabled()){
//					_logger.debug("New Call Count incremented");
//				}
//				callStatistics.setCallState(CallState.NEW);
//				if(_logger.isDebugEnabled()){
//					_logger.debug("Call State set as : " +callStatistics.getCallState()+ " for AppSession"
//							+ appSession);
//				}
//			}
//		}
//	}

	/*
	 * Increments the Calls in progress count received during last interval
	 * and the total call progress count 
	 */
	public void incrementInProgressCallCount(AseApplicationSession appSession){

		CallStatistics callStatistics;
		if(appSession !=null){
			callStatistics = appSession.getCallStatistics();
			// In case of FT, Call Statistics may be null
			if(callStatistics == null){
				if(_logger.isDebugEnabled()){
					_logger.debug("Call Statistics object not available. Returning");
				}
				return;
			}
		}else{
			if(_logger.isDebugEnabled()) _logger.debug("Inside incrementInProgressCallCount : AppSession null returning");
			return;
		}
		if(callStatistics.getCallState() == CallState.NONE
				|| callStatistics.getCallState() == CallState.NEW){
			totalInProgressCallCount.increment();
			currentInProgressCallCount.increment();
			callStatistics.setCallState(CallState.IN_PROGRESS);
			if(_logger.isDebugEnabled()){
				_logger.debug("Progress Count incremented Call State Set as:: " + callStatistics.getCallState());
			}
		}
	}
	
	/*
	 * Decrements the TotalCalls in progress count for the calls for which
	 * BYE or CANCEL is received.
	 */
	public void decrementInProgressCallCount(AseApplicationSession appSession) {
		
		CallStatistics callStatistics;
		if(appSession !=null){
			callStatistics = appSession.getCallStatistics();
			// In case of FT, Call Statistics may be null
			if(callStatistics == null){
				if(_logger.isDebugEnabled()){
					_logger.debug("Call Statistics object not available. Returning");
				}
				return;
			}
		}else{
			if(_logger.isDebugEnabled()) _logger.debug("Inside decrementInProgressCallCount : AppSession null returning");
			return;
		}
		
		if(callStatistics.getCallState() == CallState.CONNECTED  || 
				callStatistics.getCallState() == CallState.IN_PROGRESS){
			_logger.debug("Progress Count decremented Call State :: " + callStatistics.getCallState());
			totalInProgressCallCount.decrement();
			callStatistics.setCallState(CallState.END_PROGRESS);
			if(_logger.isDebugEnabled()){
				_logger.debug("Total Call Progress Count decremented Call State Set as:: " + callStatistics.getCallState());
			}
		}
	}


	/*
	 * Returns the CallStatistics Object List that were created
	 * during the interval 
	 */
	public List<CallStatistics> getCallStatisticsObjList(){
		return callStatisticsObjList;
	}


	/*
	 * Sets the timestamps when is connected and disconnected
	 */
	public void setCallHoldTime(boolean initialTime, AseApplicationSession appSession){

		CallStatistics callStatistics;
		if(appSession !=null){
			callStatistics = appSession.getCallStatistics();
			// In case of FT, Call Statistics may be null
			if(callStatistics == null){
				if(_logger.isDebugEnabled()){
					_logger.debug("Call Statistics object not available. Returning");
				}
				return;
			}
		}else{
			if(_logger.isDebugEnabled()) _logger.debug("Inside setCallHoldTime : AppSession null returning");
			return;
		}

		if(initialTime){
			if(callStatistics.getCallState() == CallState.IN_PROGRESS){
				callStatistics.setCallBeginTime();
				this.callStatisticsObjList.add(callStatistics);
				callStatistics.setCallState(CallState.CONNECTED);
				if(_logger.isDebugEnabled()){
					_logger.debug("Call State Set as:: " + callStatistics.getCallState());
				}
			}
		}else{
			if(callStatistics.getCallState() == CallState.END_PROGRESS){
				if(!(callStatistics.getCallBeginTime() == 0)){
					callStatistics.setCallEndTime();
				}
				callStatistics.setCallCompleted(true);
				callStatistics.setCallState(CallState.DISCONNECTED);
				if(_logger.isDebugEnabled()){
					_logger.debug("Call State Set as:: " + callStatistics.getCallState());
				}
			}
		}
	}


	/*
	 * Displays the collected call stats
	 */
	protected String toStringLevel() {
		
		if(totalCallsOnHold.longValue() == 0){
			avgCallHoldTime = 0.0f;
		}else{
			avgCallHoldTime = (float) (totalCallHoldTime/(totalCallsOnHold.longValue() * 1000000000.0));
		}
		
//		int loggingInterval =CallStatsProcessor.getInstance().getCallStatsLoggingInterval();
		
//		newCallsPerSecond = newCallCount.longValue()/(loggingInterval);
//		
//		if(_logger.isDebugEnabled()){
//			_logger.debug("Calculate New Calls per second for count "+ newCallCount.longValue()+" For duration "+ loggingInterval+"secs");
//		}
		
//		if(_logger.isDebugEnabled()){
//			_logger.debug("Calculate Average NTPS for count   "+ networkTransactionsCount.longValue()+" For duration "+ loggingInterval+"secs");
//		}
//		if(_logger.isDebugEnabled()){
//			_logger.debug("Calculate Average Aggregated NTPS for count   "+ aggregatedTransactionsCount.longValue()+" For duration "+ loggingInterval+"secs");
//		}
//		
//		networkTransactionsPerSec = networkTransactionsCount.longValue()/(loggingInterval);
//		aggregatedTransactionsPerSecond = aggregatedTransactionsCount.longValue()/(loggingInterval);
		
		StringBuilder callStatsBuilder = new StringBuilder();
		
		callStatsBuilder.append("Printing In progress Call Stats for the last call stats interval\n");
		
//		callStatsBuilder.append("New Calls per second = ");
//		callStatsBuilder.append(Math.round(callTransNotifier.getCurrentCallData().newCallsPerSecond));
//		callStatsBuilder.append("\n");
		
		callStatsBuilder.append("SIP Calls Currently in Progress = ");
		callStatsBuilder.append(this.currentInProgressCallCount);
		callStatsBuilder.append("\n");
		
		callStatsBuilder.append("Total SIP Calls in Progress = ");
		callStatsBuilder.append(totalInProgressCallCount);
		callStatsBuilder.append("\n");

		callStatsBuilder.append("Average SIP Call Hold Time (in seconds) = ");
		callStatsBuilder.append(Math.round(avgCallHoldTime));
		callStatsBuilder.append("\n");
		
//		callStatsBuilder.append("Average Network Transactions Per Second = ");
//		callStatsBuilder.append(Math.round(callTransNotifier.getCurrentCallData().networkTransactionsPerSec));
//		callStatsBuilder.append("\n");
//		
//		callStatsBuilder.append("Average Aggregated Transactions Per Second = ");
//		callStatsBuilder.append(Math.round(callTransNotifier.getCurrentCallData().aggregatedTransactionsPerSecond));
//		callStatsBuilder.append("\n");

		return callStatsBuilder.toString();
	}
	
	/*
	 * Resets all the call stats as zero after 
	 * the completion of call stats logging interval
	 */

	public void reset() {
//		newCallCount.reset();
		currentInProgressCallCount.reset();
		totalCallsOnHold.reset();
		totalCallHoldTime = 0;
		avgCallHoldTime = 0.0f;
//		newCallsPerSecond = 0.0f;
		callStatisticsObjList.clear();
		
//		networkTransactionsCount.reset();
//		aggregatedTransactionsCount.reset();
//		networkTransactionsPerSec=0.0f;
//		aggregatedTransactionsPerSecond=0.0f;
	}
	


	/*
	 * Set the calculated call stats as measurement counters
	 * so that they can displayed as measurement counters.
	 */
	public void setMeasurementCounters() {
		if(_logger.isDebugEnabled()){
			_logger.debug("Setting Peg Counts for Measurement Counters");
		}
//		AseMeasurementUtil.counterNewCalls.setCount(Math.round(newCallsPerSecond));
		AseMeasurementUtil.counterCallsCurrentlyInProgress.setCount(this.currentInProgressCallCount.intValue());
		AseMeasurementUtil.counterTotalCallsInProgress.setCount(totalInProgressCallCount.intValue());
		AseMeasurementUtil.counterAverageCallHoldTime.setCount(Math.round(avgCallHoldTime));
//		AseMeasurementUtil.counterNetworkTransactionsPerSec.setCount(Math.round(networkTransactionsPerSec));
//		AseMeasurementUtil.counterAggregatedTransactionsPerSec.setCount(Math.round(aggregatedTransactionsPerSecond));
//		
//		this.ocm.update(OverloadControlManager.NETWORK_TRANSACTIONS_PER_SECOND_ID, Math.round(networkTransactionsPerSec));
//		this.ocm.update(OverloadControlManager.AGGREGATED_TRANSACTIONS_PER_SECOND_ID, Math.round(aggregatedTransactionsPerSecond));
//		this.ocm.update(OverloadControlManager.NEW_CALLS_PER_SECOND_ID, Math.round(newCallsPerSecond));
		
		
	}
}

