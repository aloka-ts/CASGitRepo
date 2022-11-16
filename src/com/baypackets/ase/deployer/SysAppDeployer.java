package com.baypackets.ase.deployer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.servicemgmt.ComponentDeploymentStatus;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.common.exceptions.StartupFailedException;

public class SysAppDeployer extends ApplicationDeployer {
	
	private static Logger logger = Logger.getLogger(SysAppDeployer.class);

	ArrayList<SysAppInfo> queue = new ArrayList<SysAppInfo>();

	private ComponentDeploymentStatus deploymentStatus = null;
	


	public SysAppDeployer() {
		super();
	}

	public String getDAOClassName() {
		return null;
	}
	
	public short getType(){
		return DeployableObject.TYPE_SYSAPP;
	}
	
	public boolean me_started = true;
	public void start() throws StartupFailedException {
                if(logger.isDebugEnabled()){
                        logger.debug("Start called on SysAppDeployer :" + getType());
                }
                super.started = true;
                me_started = true;
                Iterator<SysAppInfo> iterator = queue.iterator();
                for(;iterator.hasNext();){
                	_changeState(iterator.next());
                	iterator.remove();
                }
                if (deploymentStatus != null){
	                synchronized (deploymentStatus) {
	                	deploymentStatus.setSysAppsDeployed(true);
				if(logger.isDebugEnabled()) {

	                	logger.debug("SysApp deployed");
				}
	                	if (deploymentStatus.isSbbDeployed()){
					if(logger.isDebugEnabled()) {

	                		logger.debug("Notifying Ems Adaptor");
					}
	                		deploymentStatus.notify();
	                	}
	    			}
	            }
                

        }


	public void changeState(SysAppInfo appInfo){
		if(appInfo == null)
			return;
		
		if(logger.isDebugEnabled()){
			logger.debug("changeState IN ::"+ appInfo); 
		}
		
		if(!super.started){
			if(logger.isDebugEnabled()){
				logger.debug("The SysAppDeployer is not started yet. So queueing this request....");
			}
			me_started = super.started;
			if(!queue.contains(appInfo))
				queue.add(appInfo);

			return;
		}
		
		_changeState(appInfo);

	}
	
	private void _changeState(SysAppInfo appInfo){
		if(logger.isDebugEnabled()){
			logger.debug("_changeState IN ::"+ appInfo); 
		}
		try{
			String id = appInfo.getId();
			AbstractDeployableObject deployable = (id != null) ? (AbstractDeployableObject) findById(id) :null;
			if( deployable == null){
				InputStream stream = new BufferedInputStream(appInfo.getArchive().openStream());
                            	deployable = (AbstractDeployableObject) super.deploy(stream, null);
				appInfo.setId(deployable.getId());
			}

			if(deployable != null){
                        	deployable.setExpectedState(appInfo.getExpectedState());
                        	checkExpectedState(deployable.getId(), false);
			}
               }catch(Exception e){
               		logger.error(e.getMessage(), e);
               }
	}
	
	public ComponentDeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(ComponentDeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
}
