package com.baypackets.ase.common.logging.debug;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.sip.SipServletMessage;

import com.baypackets.ase.common.logging.debug.SelectiveMessageLogger.StopLoggingTimerTask;


public class SipDebugCriteria {

	private static SipDebugCriteria _instance = new SipDebugCriteria();
	private Set debugSessions = new LinkedHashSet();
	private final String HEADER_NAME = "P-Debug-ID";
	private boolean needsMatching;
	private boolean isPDebugIdHeaderPresent = false;
	private String controlDebugId = null;
	private boolean enableLogging = false;
	private long stopTime = 0;
	private StopLoggingTimerTask stopTimerTask;
	private boolean isStopTimerCancelled = false;
	public static SipDebugCriteria getInstance(){
		return _instance;
	}


	public void addDebugSession(SipDebugSession session){
		debugSessions.add(session);
	}

	public void removeDebugSessions() {
		debugSessions.clear();
	}
	
	public Set getDebugSessions(){
		return debugSessions;
	}

	public boolean matches(SipServletMessage message) {

		Iterator criteria = debugSessions.iterator();
		setPDebugIdHeaderStatus(message);
		needsMatching = true;
		if (!criteria.hasNext()) {
			return true;
		}

		while (criteria.hasNext()) {
			SipDebugSession session = (SipDebugSession)criteria.next();
			if(!isPDebugIdHeaderPresent){
				if(!session.isControlDebugIdPresent()){
					needsMatching = false;
				}
			}
			if(needsMatching){
				if (session.matches(message,isPDebugIdHeaderPresent)) {
					this.setControlDebugId(session);
					this.setStopTime(session);
					return true;
				}
			}        
		}
		return false;        
	}




	private void setStopTime(SipDebugSession session) {
		stopTime = session.getStopTime();
		
	}


	private void setControlDebugId(SipDebugSession session) {
		if(!isPDebugIdHeaderPresent){
			controlDebugId = session.getControlDebugId();
		}
	}

	public String getControlDebugId(){
		return controlDebugId;
	}


	private void setPDebugIdHeaderStatus(SipServletMessage message){
		if(message.getHeader(HEADER_NAME) != null){
			isPDebugIdHeaderPresent = true;
		}else{
			isPDebugIdHeaderPresent = false;
		}
	}


	public void setLoggingStatus(boolean isLoggingEnabled) {
		enableLogging = isLoggingEnabled;
		
	}
	
	public boolean isLoggingEnabled(){
		return enableLogging;
	}


	public long getStopTime() {
		return stopTime;
	}


	public void setStopTimerTask(StopLoggingTimerTask timerTask) {
		this.stopTimerTask = timerTask; 
		
	}

	public void setStopTimerStatus(boolean cancel){
		isStopTimerCancelled = cancel;
	}


	public void clear() {
		if(this.getDebugSessions().size()!=0){
			this.removeDebugSessions();
		}
		if(!isStopTimerCancelled && stopTimerTask != null){
			this.stopTimerTask.cancel();
		}
		
	}





}
