package com.agnity.simulator.tasks;

import jain.protocol.ss7.tcap.dialogue.DialogueConstants;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

//import SasBasedSimulator.src.com.agnity.simulator.utils.Handler;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Helper;
import com.agnity.simulator.handlers.impl.RsnHandler;

public class RsnTimerTask extends TimerTask {
	
	
	//Instance of logger
	private static Logger logger = Logger.getLogger(RsnTimerTask.class);	 
	public SimCallProcessingBuffer simCpb =null;
	public Node node =null;

	public RsnTimerTask(Timer time)
	{
		if(logger.isInfoEnabled())
			logger.info("TimertaskRsn created..");
	}
	
	
	@Override
	public void run() {
		logger.info("TimertaskRsn started..");
		if(logger.isInfoEnabled())
			logger.info("TimertaskRsn sending RSN msg");
		/*Map<Integer, SimCallProcessingBuffer> tcapCallData = InapIsupSimServlet.getInstance().getTcapCallData();
		SimCallProcessingBuffer buffer = tcapCallData.get(dlgId);

		if(buffer != null){
			Helper.sendUserAbort(InapIsupSimServlet.getInstance(), buffer,DialogueConstants.ABORT_REASON_USER_SPECIFIC);
			Helper.cleanUpResources(buffer,false);

		}

		if(logger.isInfoEnabled())
			logger.info("TimertaskAT completed");*/
		try
		{
		RsnHandler rsnHandler = new RsnHandler();//RsnHandler.getInstance();
		rsnHandler.callProcessNode();
		}catch(Exception e)
		{
			logger.error("exception thrown from Rsnhandler/simulatorclasses/Tcap",e);
		}
		

	}
	
}