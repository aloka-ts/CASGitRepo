package com.baypackets.ase.spi.util;

public interface WorkListener {

	public void workCompleted(Work work);
		
	public void workFailed(Work work, Throwable t);
	
	public void workTimedout(Work work);
}
