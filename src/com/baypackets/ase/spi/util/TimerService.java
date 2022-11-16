package com.baypackets.ase.spi.util;

import java.util.TimerTask;

public interface TimerService {

	public void schedule(TimerTask task, long delay);
	
	public void schedule(TimerTask task, long delay, long period);
	
	public void scheduleAtFixedRate(TimerTask task, long delay,long period);
}
