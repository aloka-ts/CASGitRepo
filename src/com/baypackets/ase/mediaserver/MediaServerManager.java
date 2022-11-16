/*
 * Created on Jun 16, 2005
 *
 */
package com.baypackets.ase.mediaserver;

import java.util.Iterator;

import com.baypackets.ase.sbb.MediaServer;

/**
 * The MediaServerManager interface defines the APIs for provisioning the
 * Media Servers into the SAS.
 * 
 * <p>
 * The findXXX methods returns the Media Servers that matches the criteria irrespective of the Status. 
 * 
 *<p>
 *	The Media server Manager also defines the methods for registering and unregistering
 *listeners that will be notified when a Media server is added (OR) modified (OR) removed 
 *from the system.
 *
 *<p>
 *	The Media Server Manager object will be available as an attribute
 *	through the ServletContext interface with the name 
 *	<code>"com.baypackets.ase.ext.mediaserver.MediaServerSelector"</code>
 *	So any application that would like to get the Media Server instance can get it as follows.
 *<pre>	
 *<code>
 *	 MediaServerManager msMgr = (MediaServerManager) getServletContext().getAttribute("com.baypackets.ase.ext.mediaserver.MediaServerSelector");
 *</code>
 *</pre>	 
 *  
 */
public interface MediaServerManager {

	/**
	 * Returns the Media Server that has the same ID as specified.
	 * @param id  specified media server identifier.
	 * @return the matching Media Server object or NULL.
	 */
	public MediaServer findById(String id);
	
	/**
	 * Returns all the Media server objects that are provisioned in the SAS.
	 * @return Iterator over all the media server objects.
	 */
	public Iterator findAll();
	
	/**
	 *	Adds an Media Server to the existing repository of media servers.
	 *
	 * @param ms  Media Server object to be added.
	 * @throws IllegalArgumentException if there exist an MediaServer with the same ID.  
	 */	
	public void addMediaServer(MediaServer ms);
	
	/**
	 * Updates a Media Server's status to status INACTIVE.
	 *
	 * @param id  ID of the Media Server to be updated.
	 */	
	public void mediaServerSuspect(String id);
	
	/**
	 * Updates a Media Server's status to status INACTIVE.
	 *
	 * @param id  ID of the Media Server to be updated.
	 */	
	public void mediaServerDown(String id);
	
	/**
	 * Updates a Media Server's status to status ACTIVE.
	 * <p>
	 * The Status would be updated only if the parameter <code>"mediaserver.autodetect"</code>
	 * is configured with a value "true".
	 *
	 * @param id  ID of the Media Server to be updated.
	 */	
	public void mediaServerUp(String id);
	
	/**
	 * Updates a Media Server's status to status ADMIN.
	 *
	 * @param id  ID of the Media Server to be updated.
	 */	
	public void mediaServerAdmin(String id);
	
	/**
	 * Removes the Media Server with the specified ID.
	 * @param id  ID of the Media Server to be removed.
	 */
	public void removeMediaServer(String id);
	
	/**
	 * Gets the time interval in seconds, 
	 * at which the Media Server Manager is checking the health of the Media Servers.
	 * @return heart beat time interval.
	 */
	public int getMediaServerHeartBeatInterval();
	
	/**
	 * Sets the time interval in seconds at which the Media Server Manager 
	 * checks the health of the Media Servers.
	 * @param interval Heart Beat Time Interval
	 */
	public void setMediaServerHeartBeatInterval(int interval);
	
	/**
	 * Gets the current retry count. 
	 * @return Current retry count.
	 */
	public int getRetryCount();

	/**
	 * Sets the retry count. 
	 * The container would send the heart beat message this many times
	 * before declaring the media server as failed. 
	 * @param count Retry Count
	 */
	public void setRetryCount(int count);
	
	/**
	 * Gets the timeout value for the operations performed on the Media Servers.
	 * @return Media Server Operation Timeout in seconds.
	 */
	public int getOperationTimeout();
	
	/**
	 * Sets the timeout value in seconds for the Media Server Operations (play, playCollect, reocrd, etc)
	 * @param timeout in seconds for Media Server Operation 
	 */
	public void setOperationTimeout(int timeout);
	
	/**
	 * Gets the no. of active local/remote media servers configured
	 * for particular capabilities
	 * Added to support geographically closer media server functionality
	 */
	public int getActiveMSCount(int capabilities, int isRemote);
	
	public MediaServer getMediaServer(String id);
}
