package com.agnity.simulator.tasks;

import jain.protocol.ss7.tcap.dialogue.DialogueConstants;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.utils.Helper;

public class ActivityTestTimerTask extends TimerTask {


	private Integer dlgId ;

	//Instance of logger
	private static Logger logger = Logger.getLogger(ActivityTestTimerTask.class);	 


	public ActivityTestTimerTask(Timer timer, int dlgId ) {
		if(logger.isInfoEnabled())
			logger.info("TimertaskAT created..");
		this.dlgId = dlgId ;

	}


	@Override
	public void run() {
		logger.info("TimertaskAT started..");
		if(logger.isInfoEnabled())
			logger.info("TimertaskAT sending abort msg");
		Map<Integer, SimCallProcessingBuffer> tcapCallData = InapIsupSimServlet.getInstance().getTcapCallData();
		SimCallProcessingBuffer buffer = tcapCallData.get(dlgId);

		if(buffer != null){
			Helper.sendUserAbort(InapIsupSimServlet.getInstance(), buffer,DialogueConstants.ABORT_REASON_USER_SPECIFIC);
			Helper.cleanUpResources(buffer,false);

		}

		if(logger.isInfoEnabled())
			logger.info("TimertaskAT completed");

	}

}
