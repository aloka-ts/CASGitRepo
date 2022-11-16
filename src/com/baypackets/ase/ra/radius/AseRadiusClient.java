package com.baypackets.ase.ra.radius;

import java.io.IOException;
import java.net.SocketException;


public interface AseRadiusClient {

	/**
	 * Sends an Access-Request packet and receives a response
	 * packet.
	 * @param request request packet
	 * @return Radius response packet
	 * @exception RadiusException malformed packet
	 * @exception IOException communication error (after getRetryCount()
	 * retries)
	 */
	public RadiusAccessAnswer authenticate(RadiusAccessRequest request)
	throws IOException, RadiusException;
	/**
	 * Sends an Accounting-Request packet and receives a response
	 * packet.
	 * @param request request packet
	 * @return Radius response packet
	 * @exception RadiusException malformed packet
	 * @exception IOException communication error (after getRetryCount()
	 * retries)
	 */
	public RadiusAccountingAnswer account(RadiusAccountingRequest request) 
	throws IOException, RadiusException;
	
	/**
	 * Closes the socket of this client.
	 */
	public void close() ;
	
	/**
	 * Returns the Radius server auth port.
	 * @return auth port
	 */
	public int getAuthPort() ;
	
	/**
	 * Sets the auth port of the Radius server.
	 * @param authPort auth port, 1-65535
	 */
	public void setAuthPort(int authPort);
	
	/**
	 * Returns the host name of the Radius server.
	 * @return host name
	 */
	public String getHostName() ;
	
	/**
	 * Sets the host name of the Radius server.
	 * @param hostName host name
	 */
	public void setHostName(String hostName) ;
	
	/**
	 * Returns the retry count for failed transmissions.
	 * @return retry count
	 */
	public int getRetryCount() ;	
	
	/**
	 * Sets the retry count for failed transmissions.
	 * @param retryCount retry count, >0
	 */
	public void setRetryCount(int retryCount) ;
	
	/**
	 * Returns the secret shared between server and client.
	 * @return shared secret
	 */
	public String getSharedSecret() ;
	
	/**
	 * Sets the secret shared between server and client.
	 * @param sharedSecret shared secret
	 */
	public void setSharedSecret(String sharedSecret);
	
	/**
	 * Returns the socket timeout.
	 * @return socket timeout, ms
	 */
	public int getSocketTimeout() ;
	
	/**
	 * Sets the socket timeout
	 * @param socketTimeout timeout, ms, >0
	 * @throws SocketException
	 */
	public void setSocketTimeout(int socketTimeout)
	throws SocketException ;
	
	/**
	 * Sets the Radius server accounting port.
	 * @param acctPort acct port, 1-65535
	 */
	public void setAcctPort(int acctPort) ;
	
	/**
	 * Returns the Radius server accounting port.
	 * @return acct port
	 */
	public int getAcctPort() ;
	
}
