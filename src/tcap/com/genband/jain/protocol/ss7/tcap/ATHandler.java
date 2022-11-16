package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.tcap.TimeOutEvent;

import java.io.Serializable;
import java.util.List;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.genband.tcap.provider.TcapListener;

public class ATHandler implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 718703047961669301L;
	static private Logger logger = Logger.getLogger(ATHandler.class.getName());
	final String ListenerApp 		= "ListenerApp";
	private static int AT_IN_PROCESS = 0;
	private static int SUCCESS = 1;
	private static Boolean processingAT = false;
	private static ConfigRepository _configRepository = (ConfigRepository) Registry
			.lookup(Constants.NAME_CONFIG_REPOSITORY);
	private static int timePerChunk = Integer.valueOf(_configRepository.getValue(Constants.AT_THROTTLE_TIME));
	private static int callsPerChunk = Integer.valueOf(_configRepository.getValue(Constants.AT_THROTTLE_CALLS));
	
	public void timeout(ServletTimer timer){
		if (logger.isDebugEnabled()) {
			logger.log(Level.ERROR, "timeout called in ATHandler" );
		}
		SipApplicationSession appSession = timer.getApplicationSession();
		if (logger.isDebugEnabled()) {
			logger.log(Level.ERROR, "Starting another AT timer of 24 hrs" );
		}
		((TimerService)(JainTcapProviderImpl.getImpl().getServletContext().getAttribute(SipServlet.TIMER_SERVICE))).createTimer(appSession, 24*60*60*1000, false, new ATHandler());

		triggerActivityTest();
	}

	public int triggerActivityTest() {
		synchronized (processingAT) {
			if (processingAT){
				logger.error("AT has already been triggered and currently in progress ");
				return AT_IN_PROCESS;
			}
			processingAT = true;
		}
		try{
			Thread currentThread = Thread.currentThread();
			MonitoredThread mt = null;
			if (MonitoredThread.class.isInstance(currentThread)) {
				mt = (MonitoredThread) currentThread;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Inside ATHandler triggerActivityTest");
			}
			TcapSessionReplicator replicator = JainTcapProviderImpl.getImpl().replicator;

			List<TcapSessionImpl> tcapSessionList = replicator.getAllTcapSessions();
			logger.error("AT triggerd : Total number of tcap sessions : " + tcapSessionList.size());
			TimerService timerService = ((TimerService) JainTcapProviderImpl
					.getImpl().getServletContext()
					.getAttribute(SipServlet.TIMER_SERVICE));
			int counter = 0;
			int chunkNumber = 0;
			String appSessionId = null;
			SipApplicationSession appSession = null;
			ServletTimer appTimer = null;
			for(TcapSessionImpl tcapsession: tcapSessionList){
				logger.error("Activity Test :: tcap session dialog id : "+ tcapsession.getDialogueId());
				if (mt != null) {
					mt.updateTimeStamp();
				}
				try {
					if (counter >= callsPerChunk){
						chunkNumber++;
						counter = 0; 
					}
					appSessionId = (String) tcapsession.getAttribute(JainTcapProviderImpl.getImpl().APPLICATION_SESSION);
					appSession = tcapsession.getAppSession(appSessionId);
					appTimer = timerService.createTimer(appSession, chunkNumber*timePerChunk*1000, false, Constants.ACTIVITY_TEST_TIMER);
					appSession.setAttribute(Constants.ACTIVITY_TEST_TIMER, appTimer.getId());
					counter++;
//					TcapListener jtl = (TcapListener) tcapsession
//							.getAttribute(ListenerApp);
//					TimeOutEvent event = new TimeOutEvent();
//					event.setDialogueId(tcapsession.getDialogueId());
//					event.setTimerType(1);
					if (logger.isDebugEnabled()) {
						logger.log(Level.DEBUG, "calling processTimeOutEvent");
					}
//					jtl.processTimeOutEvent(event);
				}catch (Exception e) {
					logger.error("Exception while processing AT for dialogue id " + tcapsession.getDialogueId(), e);
					continue;
				} 
			}
		}finally{
			synchronized (processingAT) {
				processingAT = false;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("AT Executed Successfully");
		}
		return SUCCESS;
	}
	
}
