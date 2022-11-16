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
//      File:   ServiceMap.java
//        
//      Desc:   This concrete class represents SOA service map. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           20/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa;

import java.util.*;
import java.net.URI;
import java.lang.IllegalArgumentException;
import java.net.URISyntaxException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.soa.exceptions.SoaException;

/**
 * This class maintains SOA Service Map.
 *
 * @author Somesh Kr. Srivastava
 */
public class ServiceMap {

	private Map<URI, Object> m_inactiveServices = new HashMap();
	private Map<URI, Object> m_activeServices = new HashMap();
	private Map<String, Object> m_implObject = new HashMap();
	private static Logger m_logger = Logger.getLogger(ServiceMap.class);


	/**
	 * @param p_uri URI of the service proxy object
	 * @param p_serviceProxy Object service proxy object
	 * @throws SoaException if the specified URI(key) is already mapping a value(Object)
	 */
	public synchronized void addService(URI p_uri, Object p_serviceProxy) throws SoaException {
			if( (p_uri == null) || (p_serviceProxy == null) ) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug(" key=" +p_uri+ "  value=" +p_serviceProxy);
				}
				throw new IllegalArgumentException("either key of value is null");
			}

			if( m_inactiveServices.containsKey(p_uri) || m_activeServices.containsKey(p_uri) ) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("given uri is=" +p_uri);
				}
				throw new SoaException("given uri is already present");
			}

			m_inactiveServices.put(p_uri, p_serviceProxy);
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("service proxy object " +p_serviceProxy+ " added with the URI " +p_uri);
			}
	}

	/**
	 * @param p_uri URI of the service proxy object
	 * @param p_serviceProxy Object service proxy object
	 */
	public synchronized void upgradeService(URI p_uri, Object p_serviceProxy) {
		if( (p_uri == null) || (p_serviceProxy == null) ) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(" key=" +p_uri+ "  value=" +p_serviceProxy);
			}
			throw new IllegalArgumentException("either key of value is null");
		}
		
		m_inactiveServices.put(p_uri, p_serviceProxy);
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("upgradation done for the service proxy object " +p_serviceProxy+ "  with the URI " +p_uri);
		}

	}

	/**
	 * @param p_uri URI of the service proxy object 
	 */
	public synchronized boolean removeService(URI p_uri) {
		if(p_uri == null) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("given uri is = " +p_uri);
			}
			throw new IllegalArgumentException("given uri is null");
		}
		
		if(m_inactiveServices.containsKey(p_uri)) {
			Object removed = m_inactiveServices.remove(p_uri);
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("service proxy object " +removed+ " removed from the given uri " +p_uri);
			}
			return true;
		}

		if(m_activeServices.containsKey(p_uri)) {
			Object removed = m_activeServices.remove(p_uri);
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("service proxy object " +removed+ " removed from the given uri " +p_uri);
			}
			return true;
		}

		m_logger.error("given key(uri) does not exist");
		return false;
	}

	/**
	 * This method removes the service from inactive map and adds it to activeservice map.
	 * @param p_uri URI of the service proxy object
	 * @throws SoaException if inactiveServiceMap does not contain this key(URI)
	 */
	public synchronized void activateService(URI p_uri) throws SoaException {
		if( !(m_inactiveServices.containsKey(p_uri)) ) {
			throw new SoaException("service not present");
		}

		Object inactiveService = m_inactiveServices.remove(p_uri);
		m_activeServices.put( p_uri, inactiveService );
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("service " +inactiveService+ " corresponding to given URI " +p_uri+ " has been activated" );
		}
	}
	
	/**
	 * This method returns service proxy object from  inactiveServices map
	 * @param p_uri URI of the service proxy object
	 */
	public synchronized Object getService(URI p_uri) {
		if(p_uri == null) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("given uri is =" +p_uri);
			}
			throw new IllegalArgumentException("given key(URI) is null");
		}

		if( !(m_inactiveServices.containsKey(p_uri)) ) {
			throw new IllegalArgumentException("given key(URI) does not exist");
		}

		return m_inactiveServices.get(p_uri);
	}

	/**
	 * This method returns service proxy object from activeService map.
	 * @param p_uri URI of the service proxy object
	 */
	public synchronized Object findService(URI p_uri) {
		if(p_uri == null) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("given uri is =" +p_uri);
			}
			throw new IllegalArgumentException("given key(URI) is null");
		}
		
		if( !(m_activeServices.containsKey(p_uri)) ) {
			throw new IllegalArgumentException("given key(URI) does not exist");
		}
		
		return m_activeServices.get(p_uri);
	}

	public void addImplObject(String key, Object value) {
		if( (key == null) || (value == null) ) {
			throw new IllegalArgumentException("key = " +key + "value = " +value);
		}
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("adding impl Object = " + value +"and key = " +key);
		}
		m_implObject.put(key, value);			
	}

	public Object getImplObject(String key) {
		if( (key != null) && (m_implObject.containsKey(key)) ) {
			return m_implObject.get(key);
		} else {
			throw new IllegalArgumentException("given key = " + key + "does not exist");
		}
	}
	

	//for UT
	/*public static void main(String arg[]) {
		URI uri = null;
		URI uri1 = null;
		try {
		 //ConsoleAppender ca = new ConsoleAppender(); 
		 //ca.setTarget(ConsoleAppender.SYSTEM_OUT);
		  // m_logger.addAppender(ca );	
			uri = new URI("http://somesh.com");
			uri1 = new URI("http://asdfadf.com");
		} catch(URISyntaxException ee) {
			m_logger.debug(ee.getMessage(), ee);
		}
		Object serviceProxy1 = new Object();
		Object serviceProxy2 = new Object();
		ServiceMap smp = new ServiceMap();
		try {
			smp.addService(uri, serviceProxy1);
			smp.upgradeService(uri, serviceProxy2);
			smp.activateService(uri);
			//smp.activateService(uri1);
			m_logger.debug("service proxy object returned is " +smp.findService(uri) );	
			m_logger.debug("service proxy object returned is " +smp.findService(uri1) );	
			//m_logger.debug("service proxy object returned is " +smp.getService(uri1) );	
		} catch(SoaException e) {
			m_logger.debug(e.getMessage(), e);
		}
		//smp.removeService(uri);
		//smp.removeService(uri1);
	} */

}
