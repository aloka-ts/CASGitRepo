//*********************************************************************
//	 GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary 
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall 
// apply:
// 
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************

//**********************************************************************
//
//     File:     NetworkMutexImpl
//
//     Desc:     This file contains NetworkMutexImpl class. 
//
//     Author 				Date		 Description
//    ---------------------------------------------------------
//     Ashish Kabra  27/09/07     Initial Creation
//
//*********************************************************************

package com.baypackets.ase.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * This class implements NetworkMutex interface. It defines the 
 * network lock acquiring process.	
 * @author Ashish kabra
 */
public class NetworkMutexImpl implements NetworkMutex {
	//Lock states
	public static final short NOLOCK = 0;
	public static final short ACQUIRED = 1;
	public static final short TRYING = 2;
	
	//@saneja:bug11318:: change CONN_TIMEOUT to 2000 so as to reduce delay of peer takeover on FT
	private static final int CONN_TIMEOUT = 2000;
	
	private static final int SLEEP_TIME = 5000;
	private static final Logger logger = Logger.getLogger(NetworkMutexImpl.class);						
	
	private String m_host;
	private Object m_synchronizationObject = new Object();
	private short m_state = NOLOCK;
	private Random	m_random;
	private	int m_port;
	private List<String> m_peerList;
	private ServerSocket m_serverSocket;

	private ConfigRepository configRep;
	private int networkMutexConnectTimeout;
	private int networkMutexLockFlag;
	
	
	/**
	 * This is three argument constructor for class.
	 * @param port This port should be same for all machine in cluster
	 * @param peerList list of all machines in cluster
	 * @param host selfId
	 */
	public NetworkMutexImpl(int port, List peerList , String host) {
		m_port = port;
		m_peerList = new ArrayList<String>(peerList);
		m_host = host;
		m_random = new Random();
		this.configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		networkMutexConnectTimeout = Integer.parseInt(this.configRep.getValue(Constants.NETWOTK_MUTEX_CONNECT_TIMEOUT));
		networkMutexLockFlag = Integer.parseInt(this.configRep.getValue(Constants.NETWOTK_MUTEX_LOCK_ACQUIRE_RELEASE_FLAG));
		logger.error("networkMutexConnectTimeout val : " + networkMutexConnectTimeout);
		logger.error("networkMutexLockFlag val.. " + networkMutexLockFlag);

	}
	
	/** 
	 * This method sets peerList to given list of members
	 * @param list list of peers string IDs
	 */
	public synchronized void setPeerList(ArrayList list ) { 
		m_peerList = list;
	}

	/** 
	 * This method tries to starts sever socket on specified port.
	 * only one member may acquire a lock at a time.
	 * @throws NetworkMutexException 
	 */
	public synchronized void acquireLock() throws NetworkMutexException {
		// This login is required in case of N+1, where N >= 2
		if(networkMutexLockFlag == 1){
			
			boolean notAcquired = true;

			if(logger.isDebugEnabled() ) {
				logger.debug( "Inside acquireLock method " ) ;
			}
			if( m_state == ACQUIRED ) {
				logger.error("Another thread trying to acquire Lock in SAS JVM while lock state is ACQUIRED" ) ; 
				try {
					m_synchronizationObject.wait();
				} catch (Exception ex ) { 
					logger.error(" Exception in waiting for lock to be released" ) ;
				}
			}
			while ( notAcquired ) { 	
				while(isAnyPeerListening() ) { 
					//sleep for fixed time
					this.sleep(false);
				}
			
				try { 
					this.startListening();
					m_state = TRYING ;
					if(logger.isDebugEnabled() ) {
						logger.debug( "Successfully created server socket. Network mutex state: TRYING " ) ;
					}
				} catch (NetworkMutexException ex ) {
					logger.error("error while creating socket" , ex ) ;
					throw (new NetworkMutexException("error while creating socket" ) );
				}
			
				if( isAnyPeerListening() ) { 
					//have to check wether server socket exist before closing
					if ( m_state == TRYING && m_serverSocket != null ) {
						try {
							if(logger.isDebugEnabled() ) {
								logger.debug( "Race condition occured while trying to acquire Network Mutex " ) ;
								logger.debug( " Close the server socket and try again " ) ;
							} 
							m_serverSocket.close();
						} catch (IOException ex ) {
							throw (new NetworkMutexException("IOException while closing socket from TRYING state"));
						}
					}
					this.sleep(true);
					m_state = NOLOCK ;
					notAcquired = true;
				} else {
					notAcquired = false;
				}
			}

			logger.error( "LOCK ACQUIRED " ) ;
			m_state = ACQUIRED ; 
		}

	}

	/** 
	 * This method closes the socket which was opened in acquireLock() method
	 *  If Lock is not acquired then this method simply returns  causing FATAL error
	 */
	public synchronized void releaseLock() {
		// This login is required in case of N+1, where N >= 2
		if(networkMutexLockFlag == 1){

			if(m_state == NOLOCK) {
				if(logger.isDebugEnabled() ) {
					logger.debug( "No Lock is acquired right now " ) ;
				}
				return;
			}

			if(m_state == TRYING) {
				if(logger.isDebugEnabled() ) {
					logger.debug( "Current Lock state is TRYING , so not able to release the lock ");
				}
				return;
			}

			// u may check here wether server socket exist or not
			if( m_serverSocket != null && m_state == ACQUIRED ) {
				try {
					m_serverSocket.close();
					m_state = NOLOCK;
					synchronized (m_synchronizationObject ) { 
						m_synchronizationObject.notify();
					}

					logger.error( " LOCK RELEASED" ) ;
				} catch (IOException ex ) {
					logger.error ( "IOException while closing socket from ACQUIRED state " , ex ) ;
				} catch (IllegalMonitorStateException exp ) {
					logger.error ( "error while notify " , exp) ;
				}
			}
		}
	}
	
	
	/** 
	 * This method check whether lock is acquired or not
	 * @return boolean true if lock is acquired otherwise returns false
	 */
	public boolean isLockAcquired() { 
		if ( m_state == ACQUIRED ) {
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Retruns the state of lock.
	 * @return m_state state of lock
	 */
	public short getState() { 
		return m_state;
	}

	/** 
	 * This method creates server  socket. This method is called in acquireLock() 
	 * method
	 * throws NetworkMutexException 
	 */
	private void startListening() throws NetworkMutexException {
		try { 
			m_serverSocket = new ServerSocket(m_port);
			if(logger.isDebugEnabled() ) {
				logger.debug( " Created server socket and start listening for other peers" ) ;
			}
		}catch (Exception ex ) { 
			logger.error("Exception occured while creating ServerSocket " ,ex ) ;
			throw new NetworkMutexException("Socket creation failed");
		}
	}

	/** 
	 * This method tells whether any peer is listening to specified port or not
	 * @return boolean true if server socket is already created on other machine 
	 * otherwise false
	 */
	private boolean isAnyPeerListening() {
		// Try connecting to all peers one-by-one
		if(logger.isDebugEnabled() ) {
			logger.debug(" Inside isAnyPeerListening() " + networkMutexConnectTimeout ) ;
		}
		for( String host: m_peerList ) {
			if (host == null || host.compareToIgnoreCase(m_host) == 0 ) { 
				continue;
			}
			Socket cliSocket = new Socket();
			SocketAddress serverAddr = new InetSocketAddress(host, m_port);
			if(logger.isDebugEnabled() ) {
				logger.debug("Trying to connect to server socket on other m/cs " ) ;
			}
			try {
				cliSocket.connect(serverAddr, networkMutexConnectTimeout);
				cliSocket.close();
				if(logger.isDebugEnabled() ) {
					logger.debug(" Peer listening so going into loop.");
					logger.debug(" Leaving isAnyPeerListening() " ) ;
				}
				return true;
			} catch(java.net.ConnectException exp) {
				if(logger.isDebugEnabled() ) {
					logger.debug("Peer " + host + " is not listening.. try next one " ) ;
				}
				continue;
			} catch(Exception exp) {
				// print the stack trace
				logger.error("Error occurred while checking peers", exp);
			}
		}// for

		if(logger.isDebugEnabled() ) {
			logger.debug(" No peer listening." ) ;
			logger.debug(" Leaving isAnyPeerListening() " ) ;
		}

		return false;
	}

	/** 
	 * This method is used to whenever current thread need to wait depending 
	 * upon the lock state.
	 * @param random If this flag is true then waits for random time period 
	 * between 0 to 17.34 seconds otherwise wait for SLEEP_TIME defined above.
	 */
	private void sleep(boolean random) {

		int time = 0;
		if(random == false) {
			time = SLEEP_TIME;
			if(logger.isDebugEnabled() ) {
				logger.debug("sleeping for fixed time for : " + time + " milliseconds " ) ;
			}
		} else {
			// Calculate random time between 0 and 17.34 seconds
			time = m_random.nextInt(17341);
			if(logger.isDebugEnabled() ) {
				logger.debug("sleeping for random time for : " + time + " milliseconds " ) ; 
			}
		}

		try {
			Thread.sleep(time);
		} catch(Exception exp) {
			logger.error("Exception while sleeping for " + time, exp);
		}
	}
}
