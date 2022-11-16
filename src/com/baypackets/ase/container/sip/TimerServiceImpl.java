/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container.sip;

import java.io.Serializable;
import java.util.Timer;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.container.AseApplicationSession;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TimerServiceImpl implements TimerService {
	
	private static Logger _logger = Logger.getLogger(TimerServiceImpl.class);

	public TimerServiceImpl(){
	}	

	/**
	 * Creates a one-time ServletTimer and schedules it to expire after the specified delay
	 */
	public ServletTimer createTimer(
		SipApplicationSession appSession,
		long delay,
		boolean isPersistent,
		Serializable info) {
		
		boolean changeLock = AseThreadData.setIcLock((AseApplicationSession)appSession);

		try {
			//Check for the null value of appSession
			if(appSession == null){
				throw new IllegalArgumentException("Application Session cannot be NULL");
			}
		
			//Check the state of the Application Session
			((SipApplicationSessionImpl)appSession).checkValid();
		
			ServletTimerImpl servletTimer = new ServletTimerImpl(appSession,delay,isPersistent,info);
			this.getTimer(appSession.getId()).schedule(servletTimer.getTimerTask(), delay);

			return servletTimer;
		} finally {
			AseThreadData.resetIcLock((AseApplicationSession)appSession, changeLock);
		}
	}
	
	/**
	 * Creates a repeating ServletTimer and schedules it to expire 
	 * after the specified delay and then again at approximately regular intervals
	 */
	public ServletTimer createTimer(
		SipApplicationSession appSession,
		long delay,
		long period,
		boolean fixedDelay,
		boolean isPersistent,
		Serializable info) {

		boolean changeLock = AseThreadData.setIcLock((AseApplicationSession)appSession);

		try {
			//Check for the null value of appSession
			if(appSession == null){
				throw new IllegalArgumentException("Application Session cannot be NULL");
			}
		
			//Check the state of the Application Session
			((SipApplicationSessionImpl)appSession).checkValid();
		
			ServletTimerImpl servletTimer =
				new ServletTimerImpl(appSession,delay, period, fixedDelay, isPersistent,info);
			if(fixedDelay) {
				this.getTimer(appSession.getId()).scheduleAtFixedRate(servletTimer.getTimerTask(), delay, period);
			} else {
				this.getTimer(appSession.getId()).schedule(servletTimer.getTimerTask(), delay, period);
			}

			return servletTimer;
		} finally {
			AseThreadData.resetIcLock((AseApplicationSession)appSession, changeLock);
		}
	}
	
	public Timer getTimer(){
		return AseTimerService.instance().getTimer();
	}
	
	public Timer getTimer(String id){
		return AseTimerService.instance().getTimer(id);
	}
}
