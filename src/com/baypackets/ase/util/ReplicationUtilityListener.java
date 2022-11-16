/**
 * Created on Apr 8, 2013
 * @author saneja
 * Special Listener used for replicated related activities.
 * Currently it supports Gives call backs to implementing class
 *
 */
package com.baypackets.ase.util;

public interface ReplicationUtilityListener {
	
	/**
	 * This method will be implementred by Jaintacp app class 
	 * which will get callbacks whenever Tcap AppSession is cleaned.
	 * @param dialogId
	 * @see AseApplicationSession.cleanup()
	 */
	public void appSessionCleaned(String dialogId);
}
