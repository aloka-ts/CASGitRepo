package com.baypackets.sas.ide.mgmt;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;

public class SASInstance {
	
	public static final SASInstance instance = new SASInstance();
	
	public static SASInstance getInstance(){
		return instance;
	} 
	
	private ILaunch launch = null;
	
	private SASInstance(){
	}
	
	public boolean isRunning(){
		if(this.launch == null)
			return false;
		
		IProcess[] processes = this.launch.getProcesses();
		if(processes == null)
			return false;
		
		for(int i=0; i<processes.length;i++){
			if(processes[i] != null && !processes[i].isTerminated()){
				return true;
			}
		}

		return false;
	}
	
	public boolean isConnected(){
		if(this.launch == null)
			return false;
		IDebugTarget[] targets = this.launch.getDebugTargets();
		if(targets == null)
			return false;
		
		for(int i=0; i<targets.length;i++){
			if(targets[i] != null && targets[i].canDisconnect()){
				return true;
			}
		}
		return false;
	}
	
	public boolean isActive(){
		return this.isRunning() || this.isConnected();
	}
	
	public void stop() throws DebugException{
		
		if(this.launch == null)
			return;
		
		IProcess[] processes = this.launch.getProcesses();
		if(processes == null)
			return;
		
		for(int i=0; i<processes.length;i++){
			if(processes[i] != null && processes[i].canTerminate()){
				processes[i].terminate();
			}
		}
		
		this.setLaunch(null);
	}
	
	
	public void disconnect() throws DebugException{
		
		if(this.launch == null)
			return;
		
		IDebugTarget[] targets = this.launch.getDebugTargets();
		if(targets == null)
			return;
		
		for(int i=0; i<targets.length;i++){
			if(targets[i] != null && targets[i].canDisconnect()){
				targets[i].disconnect();
			}
		}
		
		this.setLaunch(null);
	}

	void setLaunch(ILaunch launch) {
		this.launch = launch;
	}
}
