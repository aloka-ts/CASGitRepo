package com.baypackets.ase.common.logging.debug;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


import com.baypackets.ase.common.logging.LoggingHandler;
import com.baypackets.ase.latency.AseLatencyLogger;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;


public class SelectiveMessageLogger{

	private static String MSG_SEPARATOR = "\r\n-------------------------------------------------------------------------\r\n";

	private static Logger _msgLogger;

	private static Logger _logger = Logger.getLogger(AseLatencyLogger.class);

	private static SelectiveMessageLogger _self;

	private static StringManager _strings = StringManager.getInstance(LoggingHandler.class.getPackage());

	private static SipDebugCriteria _criteria = SipDebugCriteria.getInstance();

	private static final Object _syncObj = new Object();

	private boolean hasCriteria = false;
	
	private boolean isTimerRunning = true;
	
	private final String HEADER_NAME = "P-Debug-ID";
	
	
	
	private SelectiveMessageLogger() {   

		if (_logger.isDebugEnabled()) {
			_logger.debug("SelectiveMessageLogger(): Preparing the Selective Message Logger...");
		}            

		try {
			_msgLogger = Logger.getLogger(Constants.NAME_SIP_SELECTIVE_LOGGER );
			_msgLogger.removeAllAppenders();
			TimedRollingFileAppender appender = new TimedRollingFileAppender();
			appender.setName(Constants.NAME_SIP_SELECTIVE_LOGGING_FILE_APPENDER);
			appender.setThreshold(Level.OFF);
			appender.setMaxFileSize("10MB");
			appender.setLayout(new PatternLayout("%d [%t] %m%n"));
			appender.setMaxBackupIndex(1000);
			_msgLogger.addAppender(appender);
			_msgLogger.setAdditivity(false);
		} catch (Exception e) {
			String msg = "Error occured while preparing the Selective Message Logger : " + e.toString();
			_logger.error(msg, e);
		}
	}


	public static SelectiveMessageLogger getInstance() {
		if (_self == null) {
			synchronized(_syncObj) {
				if (_self == null) {
					_self = new SelectiveMessageLogger();
				}
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning " + _self);
		}

		return _self;
	}

	public void setLogFileLocation(String path) throws IOException {
		try {
			TimedRollingFileAppender appender = 
				(TimedRollingFileAppender)_msgLogger.getAppender(Constants.NAME_SIP_SELECTIVE_LOGGING_FILE_APPENDER);

			appender.setFile(path);
		} catch (Exception e2) {
			_logger.error("Error while setting file path"+e2.toString(), e2);
		}
	}

	public void logIncomingRequest(AseSipServletRequest request , AseSipSession session) {

		if(!_criteria.isLoggingEnabled()){
			return;
		}
		
		if(!isTimerRunning){
			return;
		}

		if(!matchCriteria(request,session)){
			return;
		}

		Object[] params = {request.toString() + MSG_SEPARATOR};
		
			
		_msgLogger.log(Level.OFF, _strings.getString("SelectiveMessageLogger.logIncomingRequest",params));
		
	}
	

	    public void logOutgoingRequest(AseSipServletRequest request ) {
	    	
	    	if(!_criteria.isLoggingEnabled()){
				return;
			}
			
			if(!isTimerRunning){
				return;
			}
			
			if(!("TRUE").equals(request.getApplicationSession().getAttribute("SELECTIVE MESSAGE LOGGING"))){
				return;
			}
		
			
			String controlDebugId = _criteria.getControlDebugId();
			
			if(controlDebugId != null){
				request.setHeader(HEADER_NAME, controlDebugId);
			}

			Object[] params = {request.toString() + MSG_SEPARATOR};
			
			_msgLogger.log(Level.OFF, _strings.getString("SelectiveMessageLogger.logOutgoingRequest",params));
		
	    }

	    
		public void logResponse(byte direction,AseSipServletResponse response) {

			if(!_criteria.isLoggingEnabled()){
				return;
			}

			if(!isTimerRunning){
				return;
			}
			
			if(!("TRUE").equals(response.getApplicationSession().getAttribute("SELECTIVE MESSAGE LOGGING"))){
				return;
			}

			String controlDebugId = _criteria.getControlDebugId();
			
			if(direction == DsMessageLoggingInterface.DIRECTION_OUT){
				if(controlDebugId != null){
					response.setHeader(HEADER_NAME, controlDebugId);
				}
			}
			
			Object[] params = {response.toString() + MSG_SEPARATOR};

			if (direction == DsMessageLoggingInterface.DIRECTION_IN) {
				_msgLogger.log(Level.OFF, _strings.getString("SelectiveMessageLogger.logIncomingResponse", params));
			} else {
				_msgLogger.log(Level.OFF, _strings.getString("SelectiveMessageLogger.logOutgoingResponse", params));            
			}
		}


		
		private boolean matchCriteria(AseSipServletRequest request,AseSipSession session) {
			if(session == null){
				if(!hasCriteria){
					return false;
				}
				if(_criteria.matches(request)){
					startTimer();
					request.setAttribute("SELECTIVE MESSAGE LOGGING", "TRUE");
					_criteria.removeDebugSessions();
					hasCriteria = false;
					return true;
				}else{
					request.setAttribute("SELECTIVE MESSAGE LOGGING", "FALSE");
					return false;
				}
			}else{
				if(("TRUE").equals(session.getApplicationSession().getAttribute("SELECTIVE MESSAGE LOGGING"))){
					return true;
				}else{
					return false;
				}
			}
		}

		public void newCriteriaProvided(boolean criteriaProvided) {
			hasCriteria = true;
			isTimerRunning = true;
		}

		
		private void startTimer() {
			long stopTime = _criteria.getStopTime();
			if(stopTime == 0){
				return;
			}
			
			Timer timer = AseTimerService.instance().getGeneralPurposeTimer();
			StopLoggingTimerTask timerTask = new StopLoggingTimerTask();
			_criteria.setStopTimerTask(timerTask);
			timer.schedule(timerTask, stopTime*1000);
			
		}


		class StopLoggingTimerTask extends TimerTask{

			@Override
			public void run() {
				isTimerRunning = false;
				_criteria.setStopTimerStatus(true);
			}
		}
		
	}

