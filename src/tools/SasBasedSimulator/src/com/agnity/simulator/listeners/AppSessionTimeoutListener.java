package com.agnity.simulator.listeners;

import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.utils.Helper;

public class AppSessionTimeoutListener implements SipApplicationSessionListener {

	private static Logger logger = Logger.getLogger(AppSessionTimeoutListener.class);
	@Override
	public void sessionCreated(
			SipApplicationSessionEvent paramSipApplicationSessionEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(
			SipApplicationSessionEvent paramSipApplicationSessionEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionExpired(
			SipApplicationSessionEvent event) {
		String appSessionId= event.getApplicationSession().getId();
		if(logger.isDebugEnabled())
			logger.debug("Appsession expired for appsession ID::"+appSessionId);
		InapIsupSimServlet instance = InapIsupSimServlet.getInstance();
		
		SimCallProcessingBuffer simCpb = instance.getAppSessionIdCallData().get(appSessionId);
		if(simCpb != null){
			if(logger.isDebugEnabled())
				logger.debug("Fail call Case:::Appsession expired uncleared appsession ID::"+appSessionId);
			Counters.getInstance().incrementFailedCalls();
			Helper.cleanUpResources(simCpb, true);			 
		}
		SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"]  App session timed out on ISUP/SIP call");
		if(instance.isTestSuite()){
			if(logger.isDebugEnabled())
				logger.debug("AppSessionTimeoutListener sessionExpired()-->Test suite; attempt next flow");
			instance.initializeAndStartFlow();
		}

		if(logger.isDebugEnabled())
			logger.debug("Leave Appsession expired for appsession ID::"+appSessionId);
	}

	@Override
	public void sessionReadyToInvalidate(
			SipApplicationSessionEvent paramSipApplicationSessionEvent) {
		// TODO Auto-generated method stub

	}

}
