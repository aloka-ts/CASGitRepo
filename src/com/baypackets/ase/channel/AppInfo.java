/*
 * Created on Oct 25, 2004
 *
 */
package com.baypackets.ase.channel;

/**
 *
 */
public class AppInfo implements java.io.Serializable {

	private static final long serialVersionUID = 334466558354L;
    private String applicationId;
    private String hostName;
    private boolean appSyncForDeployReceived;
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String appName) {
        this.applicationId = appName;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    /**
     * This method returns boolean indicating that AppSyncMessage for this application received or not
     * @return
     */
    public boolean isAppSyncForDeployReceived(){
    	return this.appSyncForDeployReceived;
    }
    
    /**
     * This method sets boolean indicating that AppSyncMessage for this application received or not
     * @param appSyncForDeployReceived
     */
    public void setAppSyncForDeployReceived(boolean appSyncForDeployReceived){
    	this.appSyncForDeployReceived=appSyncForDeployReceived;
    }
    
}
