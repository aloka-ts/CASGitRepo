/*
 * Created on Aug 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import java.util.Timer;
import java.util.TimerTask;

import com.baypackets.ase.spi.util.TimerService;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AseTimerService implements TimerService{
	
	private static final AseTimerService timerService = new AseTimerService();
	public static AseTimerService instance(){
		return timerService;
	} 	
	
	//Create this timer thread as a daemon thread.
	//supporting 5 timers to avoid contaention and balance load
	private Timer timer1 = new Timer(true);
	private Timer timer2 = new Timer(true);
	private Timer timer3 = new Timer(true);
	private Timer timer4 = new Timer(true);
	private Timer timer5 = new Timer(true);
	
	private Timer gpTimer = new Timer(true);
	
	public Timer getTimer(String id){
		int hashcode = id.hashCode();
		
		switch (hashcode%5){
			case 0: {
				return timer1;
			}
			case 1: {
				return timer2;
			}
			case 2: {
				return timer3;
			}
			case 3: {
				return timer4;
			}
			case 4: {
				return timer5;
			}
			default: {
				return timer1;
			}
		}

	}
	
	public Timer getTimer(){
		return this.timer1;
	}
	
	public Timer getGeneralPurposeTimer(){
		return this.gpTimer;
	}

	public void schedule(TimerTask task, long delay, long period) {
		this.gpTimer.schedule(task, delay, period);
	}

	public void schedule(TimerTask task, long delay) {
		this.gpTimer.schedule(task, delay);
	}

	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		this.gpTimer.scheduleAtFixedRate(task, delay, period);
	}
	
	
}
