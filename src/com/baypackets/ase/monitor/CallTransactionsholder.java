package com.baypackets.ase.monitor;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.LongAdder;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.monitor.CallStatsHolder.CallState;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;

public class CallTransactionsholder {
	
	private static Logger _logger = Logger.getLogger(CallTransactionsholder.class);
	
	    private LongAdder networkTransactionsCount=new LongAdder();
		
		private LongAdder aggregatedTransactionsCount=new LongAdder();
		
		protected LongAdder newCallCount = new LongAdder();
		
		protected static float newCallsPerSecond;
		
		protected float networkTransactionsPerSec;
		
		protected float aggregatedTransactionsPerSecond;
		
		private OverloadControlManager ocm=null;
		
		int reportingtime=1;

		public int getReportingtime() {
			return reportingtime;
		}

		public void setReportingtime(int reportingtime, OverloadControlManager overloadControlManager) {
			this.reportingtime = reportingtime;
			this.ocm=overloadControlManager;
			
			if(_logger.isDebugEnabled()){
				_logger.debug("setReportingtime " +reportingtime +" ocm "+ocm);
			}
		}
		
		
		public void incrementNetworkTransactionsCount(long value){
			
			if(_logger.isDebugEnabled()){
				_logger.debug("incrementNetworkTransactionsCount "+ value +" to current count" +networkTransactionsCount.longValue());
			}
			networkTransactionsCount.add(value);
		}
		
		public void incrementNewCallCount(AseApplicationSession appSession){

			if(appSession == null){
				if(_logger.isDebugEnabled()){
					_logger.debug("New Call Count incremented from current count " +newCallCount);
				}
				newCallCount.increment();
			}else{
				CallStatistics callStatistics = appSession.getCallStatistics();
				// In case of FT, Call Statistics may be null
				if(callStatistics == null){
					if(_logger.isDebugEnabled()){
						_logger.debug("Call Statistics object not available. Returning");
					}
					return;
				}
				Iterator it=appSession.getSessions(AseStrings.PROTOCOL_SIP);
				int sessionCount=0;
				while(it.hasNext()){
					it.next();
					sessionCount++;
					if(sessionCount>1){
						break;
					}
				}

				if(callStatistics.getCallState() == CallState.NONE && sessionCount==1){
					newCallCount.increment();
					if(_logger.isDebugEnabled()){
						_logger.debug("New Call Count incremented");
					}
					callStatistics.setCallState(CallState.NEW);
					if(_logger.isDebugEnabled()){
						_logger.debug("Call State set as : " +callStatistics.getCallState()+ " for AppSession"
								+ appSession);
					}
				}
			}
		}
		

		/**
		 * This method is used to increment Aggregated transactions count
		 * @param value
		 */
		public void incrementAggregatedTransactionsCount(long value){
			
			if(_logger.isDebugEnabled()){
				_logger.debug("incrementAggregatedTransactionsCount "+ value+" to current count" +aggregatedTransactionsCount.longValue());
			}
			aggregatedTransactionsCount.add(value);
		}
		
		
		public void reset() {
			
			newCallCount.reset();
			networkTransactionsCount.reset();
			aggregatedTransactionsCount.reset();
			
			newCallsPerSecond=0.0f;
			networkTransactionsPerSec=0.0f;
			aggregatedTransactionsPerSecond=0.0f;
			
		}

		public void reportCounters() {
			
			if(_logger.isDebugEnabled()){
				_logger.debug("Calculate New Calls per second for count "+ newCallCount+" For duration "+ reportingtime+"secs");
			}
			if(_logger.isDebugEnabled()){
				_logger.debug("Calculate Average NTPS for count   "+ networkTransactionsCount.longValue()+" For duration "+ reportingtime+"secs");
			}
			if(_logger.isDebugEnabled()){
				_logger.debug("Calculate Average Aggregated NTPS for count   "+ aggregatedTransactionsCount.longValue()+" For duration "+ reportingtime+"secs");
			}
			
			newCallsPerSecond = newCallCount.longValue()/(reportingtime);
			networkTransactionsPerSec = networkTransactionsCount.longValue()/(reportingtime);
			aggregatedTransactionsPerSecond = aggregatedTransactionsCount.longValue()/(reportingtime);
			
			AseMeasurementUtil.counterNewCalls.setCount(Math.round(newCallsPerSecond));
			AseMeasurementUtil.counterNetworkTransactionsPerSec.setCount(Math.round(networkTransactionsPerSec));
			AseMeasurementUtil.counterAggregatedTransactionsPerSec.setCount(Math.round(aggregatedTransactionsPerSecond));
			
			this.ocm.update(OverloadControlManager.NETWORK_TRANSACTIONS_PER_SECOND_ID, Math.round(networkTransactionsPerSec));
			this.ocm.update(OverloadControlManager.AGGREGATED_TRANSACTIONS_PER_SECOND_ID, Math.round(aggregatedTransactionsPerSecond));
			this.ocm.update(OverloadControlManager.NEW_CALLS_PER_SECOND_ID, Math.round(newCallsPerSecond));	
		}

		
		
		public Object toStringLevel() {
			
			StringBuilder callStatsBuilder = new StringBuilder();
			
			callStatsBuilder.append("Printing Call Stats for new calls for last call stats interval\n");
			
			callStatsBuilder.append("New Calls per second = ");
			callStatsBuilder.append(Math.round(newCallsPerSecond));
			callStatsBuilder.append("\n");
			
			callStatsBuilder.append("Average Network Transactions Per Second = ");
			callStatsBuilder.append(Math.round(networkTransactionsPerSec));
			callStatsBuilder.append("\n");
			
			callStatsBuilder.append("Average Aggregated Transactions Per Second = ");
			callStatsBuilder.append(Math.round(aggregatedTransactionsPerSecond));
			callStatsBuilder.append("\n");

			return callStatsBuilder.toString();
		}
		

}
