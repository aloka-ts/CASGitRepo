package com.baypackets.ase.monitor;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.util.Constants;

public class CallTransactionsNotifier implements BackgroundProcessListener {
	
	private static Logger _logger = Logger.getLogger(CallTransactionsNotifier.class);

    private volatile CallTransactionsholder currTransHolder=new CallTransactionsholder(); // Place Holder for storing the call stats for current interval
	
	private volatile CallTransactionsholder prevTransHolder=new CallTransactionsholder(); // Place Holder for storing previous interval call stats for processing.
	
	//Flag to check that data is not read and written simultaneously in the currCallStats
	
	private volatile boolean isReadyForExchange=false;
	
	int reportingtime=1;

	public int getReportingtime() {
		return reportingtime;
	}
	
	/**
	 * This method is used to increment network transactions count
	 * @param value
	 */
	public void incrementNetworkTransactionsCount(long value){
		
	         getCurrentCallData().incrementNetworkTransactionsCount(value);
	}
	
	public void incrementNewCallCount(AseApplicationSession appSession){
             getCurrentCallData().incrementNewCallCount(appSession);
	}
	
	
	/**
	 * This method is used to increment Aggregated transactions count
	 * @param value
	 */
	public void incrementAggregatedTransactionsCount(long value){
		
	    getCurrentCallData().incrementAggregatedTransactionsCount(value);
	}

	public CallTransactionsNotifier(String measurementInterval) {

		
		if (measurementInterval != null && !measurementInterval.isEmpty()) {
			try {
				reportingtime = Integer.parseInt(measurementInterval);
				if(reportingtime < 1){
					_logger.error("CallTransactions mesaurement Interval is less than 1  seconds "
							+ "Setting minimum value of 1 seconds. ");
					reportingtime= 1;
				}else if(reportingtime > 300){
					_logger.error("CallTransactions mesaurement Interval is more than 5 minutes "
							+ "Setting maximum value of 5 minutes. ");
					reportingtime = 300;
				}else{
					if(_logger.isDebugEnabled()) _logger.debug("CallTransactions mesaurement Interval set as " + reportingtime + " seconds");
				}
				
			} catch (NumberFormatException e) {
				reportingtime=1;
			}
		} else {
			reportingtime=1;
			_logger.error("No Call transactions measuremen interval provided using default value 1");
			
		}
		
		currTransHolder.setReportingtime(reportingtime,(OverloadControlManager) Registry.lookup(Constants.NAME_OC_MANAGER));
		prevTransHolder.setReportingtime(reportingtime,(OverloadControlManager) Registry.lookup(Constants.NAME_OC_MANAGER));
	}

	@Override
	public void process(long currentTime) {
		
		if(_logger.isDebugEnabled()){
			_logger.debug("Inside : process() process transaction counters ");
		}
	
		isReadyForExchange=true;
		
		synchronized (CallStatsProcessor.class) {
			if(isReadyForExchange){
				CallTransactionsholder tempCallStat=currTransHolder;
				currTransHolder=prevTransHolder;
				prevTransHolder=tempCallStat;
				isReadyForExchange=false;
			}
		}
		
		prevTransHolder.reportCounters();
		
		print(prevTransHolder);
		
		prevTransHolder.reset();
	
	}
	
	/**Publishes the Call stats obtained after processing the collected call data
	 * @param callDataProvider -- CallStatsHolder object that contains the collected 
	 * data for the previous interval 
	 */
	
	private void print(CallTransactionsholder callTransactionHolder){
		if(_logger.isDebugEnabled()){
			_logger.debug(callTransactionHolder.toStringLevel());
		}
	}
	 
		protected CallTransactionsholder getCurrentCallData(){
			if(isReadyForExchange){
				synchronized (CallStatsProcessor.class) {
					if(isReadyForExchange){
						CallTransactionsholder tempCallStat=currTransHolder;
						currTransHolder=prevTransHolder;
						prevTransHolder=tempCallStat;
						isReadyForExchange=false;
					}
				}
			}
			return currTransHolder;
		}
	

}
