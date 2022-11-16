package com.agnity.sas.apps.listener;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.agnity.sas.apps.domainobjects.SampleAppCallProcessBuffer;

public class EtcTimerTask extends TimerTask implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034984394063485202L;
	//Instance of logger
	private static Logger logger = Logger.getLogger(EtcTimerTask.class);	 
	private Timer timer;
	private SampleAppCallProcessBuffer buffer;

	public EtcTimerTask(SampleAppCallProcessBuffer buffer, Timer timer) {
		logger.info("Timertask created..");
		this.buffer=buffer;
		this.timer=timer;

	}

	@Override
	public void run() {
		logger.info("Timertask started..");
		
		logger.error("ETC timer expired for dialog:::"+buffer.getDlgId());
		
		logger.info("Timer cancelled..");
		this.timer.cancel();
		
	}
}