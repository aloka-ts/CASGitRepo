package com.agnity.simulator.statistics;

import java.util.concurrent.atomic.AtomicInteger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.logger.SuiteLogger;

public class Counters {

	private Counters(){
		unExpectedMessages = new AtomicInteger(0);
		scuccesCalls = new AtomicInteger(0);
		failedCalls = new AtomicInteger(0);
		totalCalls = new AtomicInteger(0);
		unHandledNode = new AtomicInteger(0);

		//suite
		suiteUnExpectedMessages = new AtomicInteger(0);
		suiteScuccesCalls = new AtomicInteger(0);
		suiteFailedCalls = new AtomicInteger(0);
		suiteTotalCalls = new AtomicInteger(0);
		suiteUnHandledNode = new AtomicInteger(0);


	}

	public static Counters getInstance() {
		if(instance ==null){
			synchronized(Counters.class){
				if(instance == null){
					instance = new Counters();
				}//@End: inner block if instance is null 
			}//@End: synchronized block 
		}//@End: outer block if instance is null 
		return instance;
	}

	public void incrementUnExpectedMessages() {
		unExpectedMessages.incrementAndGet();
		suiteUnExpectedMessages.incrementAndGet();
	}

	/**
	 * @return the unExpectedMessages
	 */
	public int getUnExpectedMessages() {
		return unExpectedMessages.intValue();
	}


	public void incrementScuccesCalls() {
		int success = scuccesCalls.incrementAndGet() ;
		int failed = getFailedCalls();
		int total = getTotalCalls();
		if( (failed+success) >= total){
			String header= InapIsupSimServlet.getInstance().getCurrentFileName()+" STATISTICS";
			printStats(total, success, failed, getUnExpectedMessages(), getUnHandledNode(),header);
		}

				success=suiteScuccesCalls.incrementAndGet();
		//		failed = suiteFailedCalls.intValue();
		//		total= suiteTotalCalls.intValue();
		//		if( (failed+success) >= total){
		//			String header= "TEST SUITE STATISTICS";
		//			printStats(total, success, failed, suiteUnExpectedMessages.intValue(), suiteUnHandledNode.intValue(),header);
		//		}
	}

	/**
	 * @return the scuccesCalls
	 */
	public int getScuccesCalls() {
		return scuccesCalls.intValue();
	}

	public void incrementFailedCalls() {
		int failed = failedCalls.incrementAndGet();
		int success = getScuccesCalls();
		int total = getTotalCalls();
		if( (failed+success) >= total){
			String header= InapIsupSimServlet.getInstance().getCurrentFileName()+" STATISTICS";
			printStats(total, success, failed, getUnExpectedMessages(), getUnHandledNode(),header);
		}

				failed = suiteFailedCalls.incrementAndGet();
		//		success=suiteScuccesCalls.intValue();
		//		total= suiteTotalCalls.intValue();
		//		if( (failed+success) >= total){
		//			String header= "TEST SUITE STATISTICS";
		//			printStats(total, success, failed, suiteUnExpectedMessages.intValue(), suiteUnHandledNode.intValue(),header);
		//		}

	}

	/**
	 * @return the failedCalls
	 */
	public int getFailedCalls() {
		return failedCalls.intValue();
	}

	public void incrementTotalCalls() {
		totalCalls.incrementAndGet();
		suiteTotalCalls.incrementAndGet();
	}

	/**
	 * @return the TotalCalls
	 */
	public int getTotalCalls() {
		return totalCalls.intValue();
	}

	public void incrementUnHandledNode() {
		unHandledNode.incrementAndGet();
		suiteUnHandledNode.incrementAndGet();
	}

	/**
	 * @return the Unhandled node instances
	 */
	public int getUnHandledNode() {
		return unHandledNode.intValue();
	}

	public void printStats(){
		int success = getScuccesCalls() ;
		int failed = getFailedCalls();
		int total = getTotalCalls();
//		int unExpectedMsgs=getUnExpectedMessages();
//		int unHandledNodes= getUnHandledNode();
		String header= InapIsupSimServlet.getInstance().getCurrentFileName()+" STATISTICS";
//		printStats(total, success, failed, unExpectedMsgs,unHandledNodes,header );

		//suite stats		
		success=suiteScuccesCalls.intValue();
		failed = suiteFailedCalls.intValue();
		total= suiteTotalCalls.intValue();
		header= "TEST SUITE STATISTICS";
		printStats(total, success, failed, suiteUnExpectedMessages.intValue(), suiteUnHandledNode.intValue(),header);


	}


	private void printStats(int total,int success, int failed,int unExpectedMsgs,int unHandledNodes, String header ){
		StringBuilder stats=new StringBuilder();

		stats.append("\r\n");
		stats.append("####################################################################################");
		stats.append("\r\n");
		stats.append("###############################");
		stats.append(header);
		stats.append("###############################");
		stats.append("\r\n");
		stats.append("####################################################################################");
		stats.append("\r\n\r\n");
		stats.append("     1.     Total Calls:: [");
		stats.append(total);
		stats.append("]");
		stats.append("\r\n");
		stats.append("     2.     Success Calls:: [");
		stats.append(success);
		stats.append("]");
		stats.append("\r\n");
		stats.append("     3.     Failed Calls:: [");
		stats.append(failed);
		stats.append("]");
		stats.append("\r\n");
		stats.append("     4.     Unexpected Messages:: [");
		stats.append(unExpectedMsgs);
		stats.append("]");
		stats.append("\r\n");
		stats.append("     5.     Handler Missing:: [");
		stats.append(unHandledNodes);
		stats.append("]");
		stats.append("\r\n\r\n");
		stats.append("####################################################################################");

		SuiteLogger.getInstance().log(stats.toString());
	}


	public void resetStats(boolean suite){

		unExpectedMessages = new AtomicInteger(0);
		scuccesCalls = new AtomicInteger(0);
		failedCalls = new AtomicInteger(0);
		totalCalls = new AtomicInteger(0);
		unHandledNode = new AtomicInteger(0);

		//suite
		if(suite){
			suiteUnExpectedMessages = new AtomicInteger(0);
			suiteScuccesCalls = new AtomicInteger(0);
			suiteFailedCalls = new AtomicInteger(0);
			suiteTotalCalls = new AtomicInteger(0);
			suiteUnHandledNode = new AtomicInteger(0);
		}

	}

	private static Counters instance;
	private AtomicInteger unExpectedMessages;
	private AtomicInteger scuccesCalls;
	private AtomicInteger totalCalls;
	private AtomicInteger failedCalls;
	private AtomicInteger unHandledNode;

	private AtomicInteger suiteUnExpectedMessages;
	private AtomicInteger suiteScuccesCalls;
	private AtomicInteger suiteTotalCalls;
	private AtomicInteger suiteFailedCalls;
	private AtomicInteger suiteUnHandledNode;

}
