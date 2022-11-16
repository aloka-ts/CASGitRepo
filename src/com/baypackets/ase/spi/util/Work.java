package com.baypackets.ase.spi.util;

public interface Work {

	public void execute();
	
	public WorkListener getWorkListener();
	
	public int getTimeout();
}
