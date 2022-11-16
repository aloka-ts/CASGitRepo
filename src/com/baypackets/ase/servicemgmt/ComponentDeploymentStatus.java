/*
 * ComponentDeploymentStatus.java
 *
 * Created on February 27, 2012, 3:36 PM
 */
package com.baypackets.ase.servicemgmt;

/**
 * An instance of this class provided the base for communication between 
 * the main thread (Responsible for deploying sbb and sysapps) and EmsAdaptor 
 * thread (Responsible for deploying services from EMS) .
 */
public class ComponentDeploymentStatus{
	
	private boolean sbbDeployed;
	private boolean sysAppsDeployed;
	
	public boolean isSbbDeployed() {
		return sbbDeployed;
	}
	public void setSbbDeployed(boolean sbbDeployed) {
		this.sbbDeployed = sbbDeployed;
	}
	public boolean isSysAppsDeployed() {
		return sysAppsDeployed;
	}
	public void setSysAppsDeployed(boolean sysAppsDeployed) {
		this.sysAppsDeployed = sysAppsDeployed;
	}
}