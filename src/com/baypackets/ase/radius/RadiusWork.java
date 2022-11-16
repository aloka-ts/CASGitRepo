package com.baypackets.ase.radius;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import commonj.work.Work;

public class RadiusWork implements Work {

    private static Log logger = LogFactory.getLog(RadiusWork.class);
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
	if (logger.isDebugEnabled()) {
		logger.debug("inside run of RadiusWork");
	}
	try {
		if (logger.isDebugEnabled()) {
			logger.debug("1111");
		}
	    // check client
	    InetSocketAddress localAddress = (InetSocketAddress) datagramSocket
		    .getLocalSocketAddress();
		if (logger.isDebugEnabled()) {
			logger.debug("2222");
			logger.debug("packetIn.getAddress " + packetIn.getAddress());
			logger.debug("packetIn.getPort " + packetIn.getPort());
		}
	    InetSocketAddress remoteAddress = new InetSocketAddress(packetIn
		    .getAddress(), packetIn.getPort());
		if (logger.isDebugEnabled()) 		
			logger.debug("3333");
	    String secret = radiusServer.getSharedSecret(remoteAddress);
		if (logger.isDebugEnabled()) {
				logger.debug("4444");
				logger.debug("shared secret in RadiusWork is = " + secret);
			}
	    if (secret == null) {
		if (logger.isDebugEnabled())
		    logger.debug("ignoring packet from unknown client "
			    + remoteAddress + " received on local address "
			    + localAddress);
		if (logger.isInfoEnabled()) 	
			logger.info("shared secret mismatch returning....");
		return;
	    }

	    // parse packet
	    RadiusPacket request = radiusServer.makeRadiusPacket(packetIn,
		    secret);
	    if (logger.isDebugEnabled())
		logger.debug("received packet from " + remoteAddress
			+ " on local address " + localAddress + ": " + request);

	    // handle packet
		if (logger.isTraceEnabled()) 
			logger.trace("about to call RadiusServer.handlePacket()");
	    RadiusPacket response = radiusServer.handlePacket(localAddress,
		    remoteAddress,
		    request, secret);

	    // send response
	    if (response != null) {
		if (logger.isDebugEnabled())
		    logger.debug("send response: " + response);
		DatagramPacket packetOut = radiusServer.makeDatagramPacket(
			response, secret,
			remoteAddress.getAddress(), packetIn.getPort(), request);
		datagramSocket.send(packetOut);
	    } else{
			if (logger.isDebugEnabled())
				logger.debug("no response sent");
		}
		
	} catch (SocketTimeoutException ste) {
	    // this is expected behaviour
		if (logger.isDebugEnabled())
			logger.debug("5555");
		if (logger.isTraceEnabled())	
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

    @Override
    public boolean isDaemon() {
	return false;
    }

    @Override
    public void release() {
	if (logger.isInfoEnabled())
		logger.info("radius work release called");
    }






}