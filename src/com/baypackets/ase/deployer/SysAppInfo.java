/*
 * SysAppInfo.java
 *
 * Created on July 3, 2005, 7:00 PM
 */
package com.baypackets.ase.deployer;

import java.net.URL;

import com.baypackets.ase.util.AseStrings;


/**
 * This class provides a data object to encapsulate the info on a
 * particular system application.  A system app is a SIP Servlet application
 * used internally by the platform.
 *
 * @author Baypackets
 */
public class SysAppInfo implements java.io.Serializable {
	private static final long serialVersionUID = 8480558354L;
    private String name;
    private String id;
    private URL archive;
    private Short deployOnRole;
    private Short startOnRole;
    private short expectedState;

		/**
		 * Returns the location of the application archive file to deploy.
		 */
    public URL getArchive() {
        return archive;
    }

    public void setArchive(URL archive) {
        this.archive = archive;
    }

		/**
		 * Returns the cluster role which the platform must be in to deploy this 
		 * system app.  If null, the app will be deployed on platform startup.
		 *
		 * @see com.baypackets.ase.control.AseRoles
		 */
    public Short getDeployOnRole() {
        return deployOnRole;
    }

    public void setDeployOnRole(Short role) {
        this.deployOnRole = role;
    }

		/**
		 * Returns the cluster role which the platform must be in to start up
		 * this system app.  If null, the app will be started on platform startup.
		 */
    public Short getStartOnRole() {
        return startOnRole;
    }

    public void setStartOnRole(Short role) {
        this.startOnRole = role;
    }
    
    /**
		 * Returns the app's unique identifier.  This value will only be assigned
		 * when the app is first deployed.
		 */
		public java.lang.String getId() {
        return id;
    }
    
    public void setId(java.lang.String id) {
        this.id = id;
    }

    /**
     * Returns the app's configured name.  This value will only be assigned
     * when the app is first deployed.
     */
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public short getExpectedState(){
	return this.expectedState;
    }
	
    public void setExpectedState(short state){
	this.expectedState = state;
    }

    public String toString(){
	StringBuffer buffer = new StringBuffer();
	buffer.append("SysAppInfo [id=");
	buffer.append(id);
	buffer.append(",name=");
	buffer.append(name);
	buffer.append(",expectedState=");
	buffer.append(expectedState);
	buffer.append(",deploy-on-role=");
	buffer.append(deployOnRole);
	buffer.append(",start-on-role=");
	buffer.append(startOnRole);
	buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
	return buffer.toString();
    }
}
