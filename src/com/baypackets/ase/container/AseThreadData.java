/**
 * Filename: AseThreadData.java
 *
 * Created on Mar 8, 2005
 */
package com.baypackets.ase.container;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.latency.AseLatencyData.ThreadLocalLatencyContainer;


/**
 * The <code>AseThreadData</code> class contains thread specific data.
 *
 * @author Neeraj Jain
 */
public class AseThreadData {

	///////////////////////////////// Attributes //////////////////////////////

	/**
	 * Thread specific data contains a linked list
	 */
	private static ThreadLocal m_tData = new ThreadLocal() {
		protected Object initialValue() {
			m_l.debug("initialValue(): called");
			return new AseThreadData();
		}
	};

	/**
	 * List to contain message objects to be processed in context of current thread.
	 */
	private LinkedList m_list = new LinkedList();

	//private AseLatencyData storedLatencyData ;
	
	private final ThreadLocalLatencyContainer latencyContainer = new ThreadLocalLatencyContainer() ;
	/**
	 * Object to contain current IC reference
	 */
	private AseIc m_currentIc = null;

	private static Logger m_l = Logger.getLogger(AseThreadData.class.getName());

	///////////////////////// List Access Methods /////////////////////////////

	/**
	 * Adds object at end of list.
	 * @param obj object to be added into tail of list.
	 */
	public static void add(Object obj) {
		m_l.debug("add(Object): called");
		AseThreadData atd = (AseThreadData)m_tData.get();
		atd.m_list.add(obj);
	}

	/**
	 * Removes object from head of list.
	 * @return objects removed from head of list.
	 */
	public static Object get() {
		m_l.debug("get(): enter");

		AseThreadData atd = (AseThreadData)m_tData.get();
		if(atd.m_list.size() > 0) {
			m_l.debug("Returning data from thread list");
			m_l.debug("get(): exit");
			return atd.m_list.removeFirst();	
		}

		m_l.debug("Thread list is empty");
		m_l.debug("get(): exit");
		return null;
	}
	
	/*
	 * Method added for Latency Measurements
	 */
	public static ThreadLocalLatencyContainer getLatencyContainer(){
		AseThreadData atd = (AseThreadData)m_tData.get();

		return atd.latencyContainer;
	}

	
	/////////////////////////// IC Access Methods ////////////////////////////

	public static AseIc getCurrentIc() {
		AseThreadData atd = (AseThreadData)m_tData.get();
		return atd.m_currentIc;
	}

	public static void setCurrentIc(AseIc ic) {
		AseThreadData atd = (AseThreadData)m_tData.get();
		atd.m_currentIc = ic;
	}

	/**
	 * Takes lock on given IC if it is different than thread IC.
	 * @return true, if lock was taken; false, if lock was not taken
	 */
	public static boolean setIcLock(AseIc ic) {
		AseThreadData atd = (AseThreadData)m_tData.get();
		if(atd.m_currentIc != ic) {
			try {
				if(atd.m_currentIc != null) {
					atd.m_currentIc.release();
				}
				ic.acquire();
			} catch(AseLockException exp) {
				m_l.error("Changing IC locks", exp);
			}

			return true;
		}

		return false;
	}

	/**
	 * Takes lock on given IC if it is different than thread IC.
	 * @return true, if lock was taken; false, if lock was not taken
	 */
	public static boolean setIcLock(AseApplicationSession appSession) {
		AseThreadData atd = (AseThreadData)m_tData.get();
		if(atd.m_currentIc != appSession.getIc()) {
			try {
				if(atd.m_currentIc != null) {
					atd.m_currentIc.release();
				}
				appSession.getIc().acquire();
			} catch(AseLockException exp) {
				m_l.error("Changing IC locks", exp);
			}

			return true;
		}

		return false;
	}

	/**
	 * Takes lock on given IC if it is different than thread IC.
	 * @return true, if lock was taken; false, if lock was not taken
	 */
	public static boolean setIcLock(AseProtocolSession protocolSession) {
		AseThreadData atd = (AseThreadData)m_tData.get();
		if(atd.m_currentIc != ((AseApplicationSession)protocolSession.getApplicationSession()).getIc()) {
			try {
				if(atd.m_currentIc != null) {
					atd.m_currentIc.release();
				}
				((AseApplicationSession)protocolSession.getApplicationSession()).getIc().acquire();
			} catch(AseLockException exp) {
				m_l.error("Changing IC locks", exp);
			}

			return true;
		}

		return false;
	}

	/**
	 * Releases lock on given IC if it was previously taken (as indicated
	 * by passed flag).
	 */
	public static void resetIcLock(AseIc ic, boolean changeLock) {
		if(!changeLock) {
			return;
		}

		AseThreadData atd = (AseThreadData)m_tData.get();
		try {
			ic.release();
			if(atd.m_currentIc != null) {
				atd.m_currentIc.acquire();
			}
		} catch(AseLockException exp) {
			m_l.error("Resetting IC locks", exp);
		}
	}

	/**
	 * Releases lock on given IC if it was previously taken (as indicated
	 * by passed flag).
	 */
	public static void resetIcLock(AseApplicationSession appSession, boolean changeLock) {
		if(!changeLock) {
			return;
		}

		AseThreadData atd = (AseThreadData)m_tData.get();
		try {
			appSession.getIc().release();
			if(atd.m_currentIc != null) {
				atd.m_currentIc.acquire();
			}
		} catch(AseLockException exp) {
			m_l.error("Resetting IC locks", exp);
		}
	}

	/**
	 * Releases lock on given IC if it was previously taken (as indicated
	 * by passed flag).
	 */
	public static void resetIcLock(AseProtocolSession protocolSession, boolean changeLock) {
		if(!changeLock) {
			return;
		}

		AseThreadData atd = (AseThreadData)m_tData.get();
		try {
			((AseApplicationSession)protocolSession.getApplicationSession()).getIc().release();
			if(atd.m_currentIc != null) {
				atd.m_currentIc.acquire();
			}
		} catch(AseLockException exp) {
			m_l.error("Resetting IC locks", exp);
		}
	}
}
