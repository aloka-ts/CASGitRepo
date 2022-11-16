package com.baypackets.ase.container;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.DeploymentListener;

public class AppLifeCycleListener implements DeploymentListener{

	private static Logger logger = Logger.getLogger(AppLifeCycleListener.class);
	
	private static AppLifeCycleListener listener = new AppLifeCycleListener();
	
	public static AppLifeCycleListener instance() {
		return listener;
	}
	
	public void add(AseContext context){
		if(context == null)
			throw new IllegalArgumentException("The Application Context cannot be null");
		
		context.getDeployer().registerStateChangeListener(context.getId(), this);
	}
	
	public void stateChangeCompleted(DeployableObject target, short prevState,
			short requestedState) {
		
		if(target instanceof AseContext && prevState == DeployableObject.STATE_READY 
				&& requestedState == DeployableObject.STATE_ACTIVE ) {
			invokeSipServletListener((AseContext)target);
		}
	}

	public void stateChangeFailed(DeployableObject target, short prevState,
			short requestedState) {
	}
	
	protected void invokeSipServletListener(AseContext context){
		AseContainer[] children = context.findChildren();
		for(AseContainer tmp: children){
			if(!(tmp instanceof AseWrapper))
				continue;
			
			AseWrapper wrapper = (AseWrapper) tmp;
			wrapper.invokeSipServletListener();
		}
	}

}
