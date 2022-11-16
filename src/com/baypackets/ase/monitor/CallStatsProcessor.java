
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * This class sets the initial parameters for monitoring call traffic and 
 * processes the statistics collected for the call traffic. 	
 * @author Mithil Aggarwal
 */

public class CallStatsProcessor implements BackgroundProcessListener{
	
	private static Logger _logger = Logger.getLogger(CallStatsProcessor.class);
	
	private boolean _isInitialized = false;
	
	private static CallStatsProcessor callStatProcessorObj = new CallStatsProcessor(); 
	
	private volatile CallStatsHolder currCallStats=new CallStatsHolder(); // Place Holder for storing the call stats for current interval
	
	private volatile CallStatsHolder previousCallStats=new CallStatsHolder(); // Place Holder for storing previous interval call stats for processing.
	
	//Flag to check that data is not read and written simultaneously in the currCallStats
	
	private volatile boolean isReadyForExchange=false;
	
	private static boolean  isTrafficMonitoringEnabled = false;
	
	public List<CallStatistics> permCallStatisticsObjList = new ArrayList<CallStatistics>();
	
	CallTransactionsNotifier callTransNotifier=null;
	
	private static int loggingTime = 1;
	
	
	/*
	 * A private Constructor prevents any other class from instantiating
	 */
	private CallStatsProcessor() {
	
	}

	public static CallStatsProcessor getInstance() {
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning " + callStatProcessorObj);
		}

		return callStatProcessorObj;
		
	}
	
	/**
	 * Initializes the Call Stats Processor and sets parameters 
	 * like call stats logging time and traffic monitoring flag
	 */
	public void initialize(){
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Entering initialize()");
		}
		try {
			if (_isInitialized) {
				_logger.error("Call Stats Processor is already initialized... returning");
				return;
			}
			
			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String strValue;
			strValue = repository.getValue(Constants.PROP_MONITOR_TRAFFIC);
			if(strValue!=null){
				strValue = strValue.trim();
				if((AseStrings.TRUE_CAPS).equalsIgnoreCase(strValue)){
					isTrafficMonitoringEnabled = true;
				}
			}

			if(!isTrafficMonitoringEnabled){
				_logger.error("Traffic Monitoring Not Enabled : ");
				return;
			}
			
			strValue = repository.getValue(Constants.PROP_CALL_STATS_LOGGING_INTERVAL);
			
			setCallStatsLoggingInterval(strValue);
			
			strValue = repository.getValue(Constants.PROP_CALL_TRANSACTIONS_MEASMNT_INTERVAL);
			
			AseBackgroundProcessor processor = (AseBackgroundProcessor) Registry.lookup(Constants.BKG_PROCESSOR);
			
			processor.registerBackgroundListener(this, loggingTime*60);
			
			callTransNotifier=new CallTransactionsNotifier(strValue);
			
			processor.registerBackgroundListener(callTransNotifier, callTransNotifier.getReportingtime()); //processor.registerBackgroundListener(this, loggingTime*60);

			_isInitialized = true; // set this true here after registering with background processor
			
			
			currCallStats.setCallTransNotifier(callTransNotifier);
			previousCallStats.setCallTransNotifier(callTransNotifier);


		} catch(Throwable thr) {
			_logger.error("initialize(): Caught throwable", thr);
		}
		 
		if (_logger.isDebugEnabled()) {
			_logger.debug("Exiting initialize()");
		}
		
		
	}
	
	
	/**This method sets the interval after which the reported call stats will be calculated
	 * @param callStatsLoggingInt -- Interval in seconds after which the Call Stats are calculated
	 */
	private void setCallStatsLoggingInterval(String callStatsLoggingInt) {
		if(_logger.isDebugEnabled()){
			_logger.debug("Inside setCallStatsLoggingInterval :: callStatsLoggingInt = " + callStatsLoggingInt);
		}
		
		if (callStatsLoggingInt != null && !callStatsLoggingInt.isEmpty()) {
			try {
				loggingTime = Integer.parseInt(callStatsLoggingInt);
				if(loggingTime < 1){
					_logger.error("CallStats Logging Interval is less than 3 seconds "
							+ "Setting minimum value of 1 minute. ");
					loggingTime= 1;
				}else if(loggingTime > 5){
					_logger.error("CallStats Logging Interval is more than 5 minutes "
							+ "Setting maximum value of 5 minutes. ");
					loggingTime = 5;
				}else{
					if(_logger.isDebugEnabled()) _logger.debug("Call Stats Interval set as " + loggingTime + " minutes");
				}
				
			} catch (NumberFormatException e) {
				isTrafficMonitoringEnabled = false;
				_logger.error("Illegal Argument in Call Stats Logging Interval . Disabling Traffic Monitoring "
						+ e.getMessage());
			}
		} else {
			_logger.error("No Call Stats Logging time specified. Disabling Traffic Monitoring");
			isTrafficMonitoringEnabled = false;
		}
		
	}
	
	/**
	 * returns interval 
	 * @return
	 */
	public int getCallStatsLoggingInterval(){
		return loggingTime*60;
	}


	/*
	 * Returns the value of the flag which notifies whether the
	 * call traffic is to be monitored or not
	 */
	 
	public boolean isTrafficMonitoringEnabled(){
		return isTrafficMonitoringEnabled;
		
	}
	
	
	/**This method is invoked when CAS receives a new call 
	 * @param appSession - Application Session for the call
	 */
	public void reportNewCall(AseApplicationSession appSession){
		if(_logger.isDebugEnabled()){
			_logger.debug("Reporting New call");
		}
		callTransNotifier.incrementNewCallCount(appSession);
		
	}
	
	/**
	 * This method is invoked to report that the call has reached the 
	 * progress state. i.e. any provisional response is received other 
	 * than 100 Trying
	 * @param initialTime -- true value indicates that the call has received provisional response
	 * so, progress count has to be incremented. false value indicates that CANCEL or BYE request 
	 * is received and progress count has to be decremented 
	 * @param appSession -- Application Session for the call
	 */
	
	public void reportInProgressCall(boolean initialTime, AseApplicationSession appSession){
		if(_logger.isDebugEnabled()){
			_logger.debug("Reporting In Progress Call");
		}
			if(initialTime){
				getCurrentCallData().incrementInProgressCallCount(appSession);
			}else{
				getCurrentCallData().decrementInProgressCallCount(appSession);
			}
	}
	
	/**This method in invoked to report the times when the call starts and end
	 * @param initialTime -- true value indicates that the call has disconnected
	 * false value indicated that the call is disconnected.
	 * @param appSession -- Application Session for the call
	 */
	
	public void reportCallHoldTime(boolean initialTime, AseApplicationSession appSession){
		if(_logger.isDebugEnabled()){
			_logger.debug("Reporting Call Hold Time");
		}
		getCurrentCallData().setCallHoldTime(initialTime, appSession);
	}

	/**
	 * This method is used to report current network transactions count value to
	 * current call stats holder
	 * @param value
	 */
	public void reportNetworkTransactionsValue(long value){
		if(_logger.isDebugEnabled()){
			_logger.debug("Reporting Network Transactions count value ");
		}
		callTransNotifier.incrementNetworkTransactionsCount(value);
		
	}
	
	/**
	 * This method is used to report current aggregated transactions count value to
	 * current call stats holder
	 * @param value
	 */
	public void reportAggregatedTransactionsValue(long value){
		if(_logger.isDebugEnabled()){
			_logger.debug("Reporting Aggregated Transactions count value");
		}
		callTransNotifier.incrementAggregatedTransactionsCount(value);
		
	}
	/**Implementation of AseBackgroundProcessor Interface.
	 * Receives callback after the call stats interval set in the properties file.
	 * Processes the call stats collected over the last interval.re
	 */
	
	@Override
	public void process(long currentTime) {

		if(_logger.isDebugEnabled()){
			_logger.debug("Inside CallStatsProcessor : process()");
		}
		
		if(!isTrafficMonitoringEnabled){
			return;
		}
	
		isReadyForExchange=true;
		
		synchronized (CallStatsProcessor.class) {
			if(isReadyForExchange){
				CallStatsHolder tempCallStat=currCallStats;
				currCallStats=previousCallStats;
				previousCallStats=tempCallStat;
				isReadyForExchange=false;
			}
		}
				
		List<CallStatistics> tempList = previousCallStats.getCallStatisticsObjList();
		
		permCallStatisticsObjList.addAll(tempList);
	
		Iterator <CallStatistics> itr = permCallStatisticsObjList.iterator();
		
		while(itr.hasNext()){
			CallStatistics callStatistics = itr.next();
			// CallHoldTime calculated for only those calls that are completed. LEV-1535
			if(callStatistics.isCallCompleted()){ 
				callStatistics.calculateCallHoldTime();
				itr.remove();
			}
			
		}
		
		print(previousCallStats);
		
		previousCallStats.setMeasurementCounters();
		
		previousCallStats.reset();
		
	}
	
	/**Publishes the Call stats obtained after processing the collected call data
	 * @param callDataProvider -- CallStatsHolder object that contains the collected 
	 * data for the previous interval 
	 */
	
	private void print(CallStatsHolder callDataProvider){
		_logger.error(callDataProvider.toStringLevel());
	}

	/**
	 * 
	 * @return The CallStatsHolder object on which call stats are reported 
	 */
	protected CallStatsHolder getCurrentCallData(){
		if(isReadyForExchange){
			synchronized (CallStatsProcessor.class) {
				if(isReadyForExchange){
					CallStatsHolder tempCallStat=currCallStats;
					currCallStats=previousCallStats;
					previousCallStats=tempCallStat;
					isReadyForExchange=false;
				}
			}
		}
		return currCallStats;
	}

}
