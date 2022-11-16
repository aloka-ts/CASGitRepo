//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   ListenerRegistry.java
//
//      Desc:   This class represents Notification Application Registry (NAR)
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           20/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa;


import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;
import com.baypackets.ase.soa.codegenerator.proxy.BaseProxy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.soa.exceptions.SoaException;


/**
 * This class represents Notification Application Registry (NAR)
 *
 * @author Somesh Kr. Srivastava
 */
public class ListenerRegistry {
	
	private Map m_listeners = new Hashtable();
	private static Logger m_logger = Logger.getLogger(ListenerRegistry.class);
	private Map<URI, Object> m_remoteListeners = new Hashtable();
	private Map<String, Object> m_remoteListenerProxies = new HashMap();


	/**
	 * This method adds a new entry into m_listeners map with given URI as key and Object as value.
	 *
	 * @param p_uri URI of co-resident listener object
	 * @param p_coResListener Object of co-resident listener
	 */
	public void addListener(URI p_uri, Object p_coResListener) throws SoaException {
		if( (p_uri == null) || (p_coResListener == null) ) {
			throw new IllegalArgumentException("key or value is null" + "---key = " +p_uri + "---value = " +p_coResListener);
		}

		if(m_listeners.containsKey(p_uri)) {
			throw new SoaException("hashtable already contains this key");
		}
		
			m_listeners.put(p_uri, p_coResListener);
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("co-res listener " +p_coResListener+ " is added with the URI " +p_uri);
			}
	}

	/**
	 * This method returns the listener object found in m_listeners corresponding to given
	 * URI. It returns null if no listener was found.
	 *
	 * @param p_uri URI of the listener object
	 * @return listener Object corresponding to given URI
	 */
	public Object findListener(URI p_uri) {
		return m_listeners.get(p_uri);
	}


	public void addListenerRemoteProxy(String p_className, Object p_listenerRemoteProxy) {
		this.m_remoteListenerProxies.put(p_className, p_listenerRemoteProxy);
	}

		
	public Object findRemoteListener(URI p_uri, String p_className) {
		if(m_remoteListeners.containsKey(p_uri)) {
			return m_remoteListeners.get(p_uri);
		}
		
		Object remoteListProxy = m_remoteListenerProxies.get(p_className);
		if(remoteListProxy == null) {
			m_logger.error("No SOA proxy found for listener class: " + p_className);
			throw new IllegalArgumentException("Listener class proxy not found");
		}
		
		Object proxy = null;
		try {
			proxy = ((BaseProxy)remoteListProxy).clone();
			((BaseProxy) proxy).setURI(p_uri.toString());
		} catch(Exception e) {
			m_logger.error("Unable to clone",e);
		}
		
		
		m_remoteListeners.put(p_uri, proxy);
		return proxy;
	}


	/**
	 * This method removes the entry corresponding to given URI from m_listeners map.
	 * It returns true if an entry removed and false if no entry with given URI was found.
	 * 
	 * @param p_uri URI of the co-resident listener object
	 * @return true if the listener object corresponding to given URI is removed successfully
	 * false if object not found in map.
	 */
	public boolean removeListener(URI p_uri) {
			if(p_uri == null) {
				m_logger.error("given key(URI) is null");
				return false;
			}

			if( !(m_listeners.containsKey(p_uri)) ) {
				m_logger.error("given key(URI) does not exist");
				return false;
			}

			m_listeners.remove(p_uri);
			return true;

	}


	public void upgradeListener(URI p_uri, Object p_coResListener) {
		if( (p_uri == null) || (p_coResListener == null) ) {
			throw new IllegalArgumentException("key or value is null" + "---key = " +p_uri + "---value = " +p_coResListener);
		}

		m_listeners.put(p_uri, p_coResListener);
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Upgradation done with the co-res listener object " +p_coResListener+ " and URI " +p_uri);
		}
	}


	//for UT
	/* public static void main(String arg[]) {
		URI uri = null;
		URI uri1 = null;
		try {
			uri = new URI("http://somesh.com");
			uri1 = new URI("http://asdfadf.com");
		} catch(URISyntaxException ee) {
			m_logger.debug(ee.getMessage(), ee);
		}
		Object listener = new Object();
		ListenerRegistry lrg = new ListenerRegistry();
		
		try {
			lrg.addListener(uri, listener);
			//lrg.addListener(uri, listener);
			//m_logger.debug("listener object returned is " +lrg.findListener(uri) );
			//m_logger.debug("listener object returned is " +lrg.findListener(uri1) );
			//if(lrg.removeListener(uri1)) {
			//	m_logger.debug("removed");
			//} else {
			//	m_logger.debug("remove failed");
			//}
		} catch(SoaException e) {
			m_logger.debug(e.getMessage(), e);
		}
		lrg.upgradeListener(uri, new Object() );
	} */


}


