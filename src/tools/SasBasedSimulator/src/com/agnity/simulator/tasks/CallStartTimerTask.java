/**
 * 
 */
package com.agnity.simulator.tasks;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.agnity.simulator.domainobjects.SimulatorConfig;

/**
 * @author saneja
 *
 */
public class CallStartTimerTask extends TimerTask {
	private static Logger logger = Logger.getLogger(CallStartTimerTask.class);
	
	private AtomicInteger currentCallCounter;
	private int currentCps;
	private long startTimeinMilliSecs;
	private long lastIncrementTimeinMilliSecs;
	private SimulatorConfig configData;
	private Timer timer;
	private ExecutorService executor;

	/**
	 * 
	 */
	public CallStartTimerTask(SimulatorConfig configData, Timer timer) {
		this.configData= configData;
		this.timer = timer;

		currentCallCounter = new AtomicInteger(0);
		currentCps = configData.getInitialCps();
		startTimeinMilliSecs =0L;
		lastIncrementTimeinMilliSecs = 0L;
		executor = Executors.newCachedThreadPool();

	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("CallStartTimerTask Triggered");
		
		callPreProcess();

	}

	private void callPreProcess() {
		long currTime= System.currentTimeMillis();

		synchronized (this) {
			if(startTimeinMilliSecs == 0L){
				startTimeinMilliSecs = currTime;
				lastIncrementTimeinMilliSecs = currTime;
			}else if( ((currTime -lastIncrementTimeinMilliSecs)/1000) >  configData.getCpsIncrementFreq()){
					int cps= currentCps + configData.getCpsIncremntValue()  ;
					currentCps = Math.min(cps, configData.getMaxCps());
			}
		} // end synchronized

		if(logger.isDebugEnabled())
			logger.debug("callPreProcess after synch block");
		//pre process complete submit call for excution
		for (int i =0; i< currentCps; i++){
			int currentCounter =currentCallCounter.incrementAndGet();
			if(configData.getTotalCalls() ==0 || currentCounter <= configData.getTotalCalls()){
				executor.submit(new InitiateCallTask(timer));
			}else {
				timer.cancel();
				executor.shutdown();
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("callPreProcess after task submission total calls::" +currentCallCounter.get());
	}
}