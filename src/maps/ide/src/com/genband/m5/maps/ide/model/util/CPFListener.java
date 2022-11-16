package com.genband.m5.maps.ide.model.util;

public interface CPFListener {

	public void resourceGenerated (CPFEvent e);
	
	/**
	 * 
	 * @param e
	 * @param handback this object is passed back to listener as is
	 */
	public void resourceGenerated (CPFEvent e, Object handback);
}
