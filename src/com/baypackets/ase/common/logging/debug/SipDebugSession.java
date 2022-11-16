package com.baypackets.ase.common.logging.debug;

import java.util.regex.Pattern;

import javax.servlet.sip.SipServletMessage;

public class SipDebugSession{

	private String fromURI;
	private String toURI;
	private String debugID;
	private long stopTime = 0;
	private boolean hasControlDebugId = false;
	
	
	
	private String controlDebugID;

	public void setFromURI(String body) {
		this.fromURI = body;
		
	}

	public void setToURI(String body) {
		this.toURI = body;
		
	}

	public void setControlDebugId(String body) {
		this.controlDebugID = body;
		this.hasControlDebugId = true;
		
	}

	public void setStopTime(int time) {
		this.stopTime = time;
		
	}

	public void setDebugId(String body) {
		this.debugID = body;
		
	}
	
	public boolean isControlDebugIdPresent(){
		return hasControlDebugId;
	}

	public boolean matches(SipServletMessage message , boolean isPDebugIDHeaderPresent) {
	
		if (!match(message.getTo().getURI().toString(), toURI)) {
			return false;
		}
		if (!match(message.getFrom().getURI().toString(), fromURI)) {
			return false;
		}
		
		if(isPDebugIDHeaderPresent && debugID != null){
			if(!message.getHeader("P-Debug-ID").equals(debugID)){
				return false;
			}
			
		}
		
		return true;
	}

	private boolean match(Object value, String uri) {
		
		if(uri.isEmpty()){
			return false;
		}
		if (uri == null) {
			return true;
		}        
		if (value == null) {
			return false;
		}                                   
		return value.toString().contains(uri);
	}

	public String getControlDebugId() {
		return this.controlDebugID;
	}

	public long getStopTime() {
		return stopTime;
	}        

	
}