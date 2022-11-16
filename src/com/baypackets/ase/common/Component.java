package com.baypackets.ase.common;


public class Component {
	
	private String componentName;
	private long processTime; // in milliseconds
	private BackgroundProcessListener listener;
	private long dumpTime; // in milliseconds
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public BackgroundProcessListener getListener() {
		return listener;
	}
	
	public void setListener(BackgroundProcessListener listener) {
		this.listener = listener;
	}
	
	public long getProcessTime() {
		return processTime;
	}
	
	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	public long  getDumpTime() {

		return dumpTime;
	}

	public void setDumpTime(long dumpTime) {
		this.dumpTime = dumpTime;
	}
	
	
	

}
