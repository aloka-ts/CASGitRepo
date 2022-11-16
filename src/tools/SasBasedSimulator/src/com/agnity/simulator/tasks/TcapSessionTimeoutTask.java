package com.agnity.simulator.tasks;

import java.util.TimerTask;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public class TcapSessionTimeoutTask extends TimerTask {
	private static Logger logger = Logger.getLogger(TcapSessionTimeoutTask.class);
	private SimCallProcessingBuffer simCpb;

	public TcapSessionTimeoutTask(SimCallProcessingBuffer simCpb) {
		this.simCpb= simCpb;

	}

	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("TcapSessionTimeoutTask-->Timeout Task triggered verify if its tcap Session");
		long lastInvokeTime = simCpb.getLastInvokeTime();
		long currTime = System.currentTimeMillis();
		long delay = currTime-lastInvokeTime;
		SipApplicationSession appSession = simCpb.getSipAppSession();
		if(appSession!=null){
			if(logger.isDebugEnabled())
				logger.debug("TcapSessionTimeoutTask-->Cancel this task form execution as appsession is associated");
			this.cancel();
			return;
		}
		if(logger.isDebugEnabled())
			logger.debug("TcapSessionTimeoutTask-->Timeout happened for non sip call on message");
		long expectedDelay = Constants.DEFAULT_TCAP_SESSION_TIMEOUT;
		String configuredDelay=InapIsupSimServlet.getInstance().getConfigData().getTcapSessionTimeout();
		if(configuredDelay!=null){
			try{
				expectedDelay = Long.parseLong(configuredDelay);
			}catch (Throwable e) {
				if(logger.isDebugEnabled())
					logger.debug("TcapSessionTimeoutTask-->Invalid DEFAULT_TCAP_SESSION_TIMEOUT value configures " +
							"using default value(in seconds)" +	Constants.DEFAULT_TCAP_SESSION_TIMEOUT);
			}
		}
		expectedDelay*=1000;
		if(logger.isDebugEnabled())
			logger.debug("TcapSessionTimeoutTask-->got imeout chk val::"+expectedDelay);
		if(delay >= expectedDelay ){
			if(logger.isDebugEnabled())
				logger.debug("TcapSessionTimeoutTask-->Timeout happened for non sip call on message");
			this.cancel();
			Counters.getInstance().incrementFailedCalls();
			Helper.cleanUpResources(simCpb, true);
			SuiteLogger.getInstance().log("FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"]  session timed out on TCAP call");
			if(InapIsupSimServlet.getInstance().isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("TcapSessionTimeoutTask-->Test suite; attempt next flow");
				InapIsupSimServlet.getInstance().initializeAndStartFlow();
			}
			return;	
		}
		

	}


}
