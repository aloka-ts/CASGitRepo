package com.baypackets.ase.ra.radius.stackif;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import com.baypackets.ase.ra.radius.stackif.AseRadiusServer;

public class RadiusWork implements Runnable{
	private static Logger logger = Logger.getLogger(RadiusWork.class);
	 private AseRadiusServer radiusServer;
	private DatagramSocket datagramSocket;
	private DatagramPacket packetIn;

	public RadiusWork(AseRadiusServer radiusServer,
			    DatagramSocket datagramSocket, DatagramPacket packetIn) {
			super();
			this.radiusServer = radiusServer;
			this.datagramSocket = datagramSocket;
			this.packetIn = packetIn;
		    }

	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("inside run of RadiusWork");
		try {
		    
		    // check client
		    InetSocketAddress localAddress = (InetSocketAddress) datagramSocket.getLocalSocketAddress();	
		    
		    if(logger.isDebugEnabled())
		    	logger.debug("Radius Packet received from: "+ packetIn.getAddress()+":"+packetIn.getPort());
		    
		    InetSocketAddress remoteAddress = new InetSocketAddress(packetIn
			    .getAddress(), packetIn.getPort());
		  
		    String secret = radiusServer.getSharedSecret(remoteAddress);
		  
		    if (secret == null) {
		    		logger.error("ignoring packet from unknown client "
		    				+ remoteAddress + " received on local address "
		    				+ localAddress);
		    		return;
		    }
		    // parse packet
		    RadiusPacket request = radiusServer.makeRadiusPacket(packetIn,
			    secret);
		    if (logger.isDebugEnabled())
		    	logger.debug("received packet from " + remoteAddress+ " on local address " + localAddress + ": " + request);
		    radiusServer.handleRadiusPacket(request,localAddress, remoteAddress,secret,datagramSocket);
		} catch (SocketTimeoutException ste) {
		     logger.trace("normal socket timeout");
		} catch (IOException ioe) {
		    // error while reading/writing socket
		    logger.error("communication error", ioe);
		} catch (RadiusException re) {
		    // malformed packet
		    logger.error("malformed Radius packet", re);
		} catch (Exception re) {
		    // malformed packet
		    logger.error("general exception");
		    logger.error(re.getMessage(), re);
		}
	    }
}
