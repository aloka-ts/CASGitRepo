package com.baypackets.ase.ra.radius.stackif;

import java.io.IOException;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusClient;

import com.baypackets.ase.ra.radius.AseRadiusClient;
import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccessAnswer;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingAnswer;
import com.baypackets.ase.ra.radius.RadiusException;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorImpl;
import com.baypackets.ase.ra.radius.stackif.RadiusAccessRequestImpl;

public class AseRadiusClientImpl implements AseRadiusClient{
	private static Logger logger = Logger.getLogger(AseRadiusClientImpl.class);
	RadiusClient radiusClient;
	/**
	 * Creates a new Radius client object for a special Radius server.
	 * @param hostName host name of the Radius server
	 * @param sharedSecret shared secret used to secure the communication
	 */
	public AseRadiusClientImpl(String hostName, String sharedSecret) {
		radiusClient=new RadiusClient(hostName,sharedSecret);
	}
	
		

	/**
	 * Sends an Access-Request packet and receives a response
	 * packet.
	 * @param request request packet
	 * @return Radius response packet
	 * @exception RadiusException malformed packet
	 * @exception IOException communication error (after getRetryCount()
	 * retries)
	 */
	public synchronized RadiusAccessAnswer authenticate(RadiusAccessRequest request) 
	throws IOException, RadiusException {
		if (logger.isInfoEnabled())
			logger.info("send Access-Request packet:"+request);
		RadiusAccessAnswer response=null;
		AccessRequest accessRequest=(AccessRequest)(((RadiusAccessRequestImpl)request).getRadiusPacket());
		try {
			RadiusResourceAdaptorImpl.getStackClientInterface().handleRequest(request);
			RadiusPacket responseStk = radiusClient.authenticate(accessRequest);
			if(responseStk!=null){
				RadiusAccessAnswerImpl response1=new RadiusAccessAnswerImpl(responseStk);
				response1.setRequest(request);
				response=response1;
				RadiusResourceAdaptorImpl.getStackClientInterface().handleResponse(response);
			}

		} catch (IOException e) {
			throw e; 
		} catch (org.tinyradius.util.RadiusException e) {
			throw new RadiusException(e.getMessage(),e.getCause());
		} catch (RadiusResourceException e) {
			throw new RadiusException(e.getMessage(),e.getCause());
		}
		
		if (logger.isInfoEnabled())
			logger.info("received packet: " + response);		
		return response;
	}
	
	/**
	 * Sends an Accounting-Request packet and receives a response
	 * packet.
	 * @param request request packet
	 * @return Radius response packet
	 * @exception RadiusException malformed packet
	 * @exception IOException communication error (after getRetryCount()
	 * retries)
	 */
	public synchronized RadiusAccountingAnswer account(RadiusAccountingRequest request) 
	throws IOException, RadiusException {
		if (logger.isInfoEnabled())
			logger.info("send Accounting-Request packet:"+request);
		RadiusAccountingAnswer response=null;
		AccountingRequest RadiusAccessRequest=(AccountingRequest)(((RadiusAccountingRequestImpl)request).getRadiusPacket());
		try {
			RadiusResourceAdaptorImpl.getStackClientInterface().handleRequest(request);
			RadiusPacket responseStk = radiusClient.account(RadiusAccessRequest);
			if(responseStk!=null){
				response=new RadiusAccountingAnswerImpl(responseStk);
				((RadiusAccountingAnswerImpl)response).setRequest(request);
				RadiusResourceAdaptorImpl.getStackClientInterface().handleResponse(response);
			}

		} catch (IOException e) {
			throw e; 
		} catch (org.tinyradius.util.RadiusException e) {
			throw new RadiusException(e.getMessage(),e.getCause());
		} catch (RadiusResourceException e) {
			throw new RadiusException(e.getMessage(),e.getCause());
		}
		
		if (logger.isInfoEnabled())
			logger.info("received packet: " + response);		
		return response;
	}

	/**
	 * Closes the socket of this client.
	 */
	public void close() {
		radiusClient.close();
	}
	
	/**
	 * Returns the Radius server auth port.
	 * @return auth port
	 */
	public int getAuthPort() {
		return radiusClient.getAuthPort();
	}
	
	/**
	 * Sets the auth port of the Radius server.
	 * @param authPort auth port, 1-65535
	 */
	public void setAuthPort(int authPort) {
		radiusClient.setAuthPort(authPort);
	}
	
	/**
	 * Returns the host name of the Radius server.
	 * @return host name
	 */
	public String getHostName() {
		return radiusClient.getHostName();
	}

	/**
	 * Sets the host name of the Radius server.
	 * @param hostName host name
	 */
	public void setHostName(String hostName) {
		radiusClient.setHostName(hostName);
	}
	
	/**
	 * Returns the retry count for failed transmissions.
	 * @return retry count
	 */
	public int getRetryCount() {
		return radiusClient.getRetryCount();
	}
	
	/**
	 * Sets the retry count for failed transmissions.
	 * @param retryCount retry count, >0
	 */
	public void setRetryCount(int retryCount) {
		radiusClient.setRetryCount(retryCount);
	}
	
	/**
	 * Returns the secret shared between server and client.
	 * @return shared secret
	 */
	public String getSharedSecret() {
		return radiusClient.getSharedSecret();
	}
	
	/**
	 * Sets the secret shared between server and client.
	 * @param sharedSecret shared secret
	 */
	public void setSharedSecret(String sharedSecret) {
		radiusClient.setSharedSecret(sharedSecret);
	}
	
	/**
	 * Returns the socket timeout.
	 * @return socket timeout, ms
	 */
	public int getSocketTimeout() {
		return radiusClient.getSocketTimeout();
	}
	
	/**
	 * Sets the socket timeout
	 * @param socketTimeout timeout, ms, >0
	 * @throws SocketException
	 */
	public void setSocketTimeout(int socketTimeout)
	throws SocketException {
		radiusClient.setSocketTimeout(socketTimeout);
	}
	
	/**
	 * Sets the Radius server accounting port.
	 * @param acctPort acct port, 1-65535
	 */
	public void setAcctPort(int acctPort) {
		radiusClient.setAcctPort(acctPort);
	}

	/**
	 * Returns the Radius server accounting port.
	 * @return acct port
	 */
	public int getAcctPort() {
		return radiusClient.getAcctPort();
	}
	
}
