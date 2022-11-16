/**
 * EnumServer.java
 *
 *Created on March 15,2007
 */
package com.baypackets.ase.enumclient;

import java.lang.String;

/**
 * This class defines various contains configuration parameter for ENUM client.
 * @author Ashish kabra
 */
	
public class EnumServer {
	
	/** server parameter */ 
 	//IP-Address of server. 	
	private String m_ipAddr ;
	//port to listen. 
	private int m_port ;
	//time to wait before resending query. 
	private int m_timeout ;
	//number of retries per query  that server will make
	private int m_retries;
	//Type of protocol to be used. 
	private String m_protocol;
	//flag wether server is added from config file or by application
	private boolean m_configFlag ;
	
	//constructors 
	public EnumServer() {
	}

	public EnumServer(String ip) {
		this(ip , 53 , "TCP" , 3 , 60 ) ;
	}

	public EnumServer(String ip , int port , String protocol) {
		this(ip , port , protocol , 3 , 60 );
	}

	public EnumServer(String ip , int port , String protocol , int retries , int timeout ) {
		m_ipAddr = ip;
        m_port = port;
        m_protocol = protocol ;
		m_retries= retries;	
		m_timeout = timeout;
		m_configFlag = false;
	}
	
	
	
	public boolean getConfigFlag() {
		return m_configFlag ;
	}


	public void setConfigFlag (boolean flag) { 
		m_configFlag = flag;
	}


	/** Returns the protocol specified for communication. 
	 * @return protocol to be used (TCP/UDP)
	 */
	public String getProtocol() {
		return m_protocol;
	}

	/** Returns the amount of time to wait for a response before giving up.
	 * @return time to wait before resending query. 
	 */
	public int getTimeout() {
		return m_timeout ;
	}

	/** Returns the number of retries per query  that server will make
	 * @return number of retries per query
     */
	public int getRetries() {
		return m_retries;
	}

	/** Returns the port to communicate.
     * @return port
     */
	public int getPort() {
		return m_port;
	}

	/** Returns the IP Address / Name of the server. 
	 * @return Address / Name of the server
	 */
	public String getIpAddr() {
		return m_ipAddr ;
	}

	/** sets the protocol specified for communication. 
	 * @param protocol to be used (TCP/UDP)
	 */
	public void setProtocol(String protocol) {
		 m_protocol=protocol;
	}

	/** sets the amount of time to wait for a response before giving up.
	 * @param time to wait before resending query. 
	 */
	public void setTimeout(int timeout) {
		 m_timeout = timeout ;
	}

	/** sets the number of retries per query  that server will make
	 * @param number of retries per query
     */
	public void setRetries(int retries) {
		 m_retries =retries;
	}

	/** sets the port to communicate.
     * @param port
     */
	public void setPort(int port ) {
		 m_port = port;
	}

	/** sets the IP Address / Name of the server. 
	 * @param Address / Name of the server
	 */
	public void setIpAddr(String ip) {
		 m_ipAddr = ip;
	}

	/*
	
	// UNIT TESTING 
	public int test () {
		int j=2; 
		if( j==5 ) {
			test();
		}
		return j;
	}

	public static void main(String args[] ) { 
		EnumServer server = new EnumServer("192.168.9.121");
		//System.out.println( " port for sever " + server.getPort() ) ;
	}
	*/
}
