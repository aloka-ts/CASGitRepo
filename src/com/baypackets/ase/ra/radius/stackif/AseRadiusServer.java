package com.baypackets.ase.ra.radius.stackif;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;

import com.baypackets.ase.ra.radius.RadiusMessage;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.RadiusStackServerInterface;



public class AseRadiusServer extends RadiusServer{
	private static Logger logger = Logger.getLogger(AseRadiusServer.class);
	private String sessionTimeout;
    private String idleTimeout;
    private String serverIP;
    private String sharedSecret;
	ExecutorService executorService;
	private int maxNumThreads;
	private RadiusStackServerInterfaceImpl serverInterface;
	private int socketTimeOut;
	private ConcurrentHashMap<String, ReceivedPacket> receivedPackets = new ConcurrentHashMap<String, ReceivedPacket>(100);
	private MapCleanerThread mapCleanerThread;
	/**
	 * @param sessionTimeout
	 * @param idleTimeout
	 * @param serverIP
	 * @param authPort
	 * @param accountingPort
	 * @param socketTimeOut
	 * @param sharedSecret
	 */
	public AseRadiusServer(RadiusStackServerInterfaceImpl serverInterface,String sessionTimeout, String idleTimeout,
			String serverIP, int authPort, int accountingPort,
			int socketTimeOut, String sharedSecret,int maxNumThreads,long duplicateInterval) {
		this.sessionTimeout = sessionTimeout;
		this.idleTimeout = idleTimeout;
		this.setAcctPort(accountingPort);
		this.setAuthPort(authPort);
		this.socketTimeOut=socketTimeOut;
		this.serverIP=serverIP;
		this.sharedSecret = sharedSecret;		
		this.maxNumThreads=maxNumThreads;
		this.serverInterface=serverInterface;
		this.setDuplicateInterval(duplicateInterval);
	}

	@Override
	public String getSharedSecret(InetSocketAddress arg0) {
		return sharedSecret;
	}

	@Override
	public String getUserPassword(String arg0) {
		logger.error("Do not call this method not");
		return null;
	}

	@Override
	public void start(boolean listenAuth, boolean listenAcct) {
		try {
			this.setListenAddress(InetAddress.getByName(serverIP));
			this.setSocketTimeout(socketTimeOut);
		} catch (UnknownHostException e) {
			logger.error("UnknownHost Exception in AseRadiusSever start()...",e);
		} catch (SocketException e) {
			logger.error("Error in setting socket timeout",e);
		}
		executorService=Executors.newFixedThreadPool(maxNumThreads);
		super.start(listenAuth, listenAcct);
		this.mapCleanerThread = new MapCleanerThread();
		mapCleanerThread.start();
	}
	
	@Override
	public void stop() {
		executorService.shutdown();
		super.stop();
	}
	
	/**
     * Listens on the passed socket, blocks until stop() is called.
     * @param s
     *            socket to listen on
     */
    protected void listen(DatagramSocket dgs) {
    	while (true) {
    	    DatagramPacket packetIn = new DatagramPacket(
    		    new byte[RadiusPacket.MAX_PACKET_LENGTH],
    		    RadiusPacket.MAX_PACKET_LENGTH);
    	    // receive packet
    	    try {
    		logger.debug("about to call socket.receive()");
    		dgs.receive(packetIn);
    		if (logger.isDebugEnabled())
    		    logger.debug("receive buffer size = "
    			    + dgs.getReceiveBufferSize());
    	    } catch (SocketException se) {
    		if (closing) {
    		    // end thread
    		    logger.info("got closing signal - end listen thread");
    		    return;
    		} else {
    		    // retry s.receive()
    		    logger.error("SocketException during s.receive() -> retry",
    			    se);
    		    continue;
    		}
    	    } catch (IOException e) {
    		// logger.error(e.getMessage(), e);
    		continue;
    	    }
    	   RadiusWork work=new RadiusWork(this, dgs, packetIn); 
    	   this.executorService.submit(work);
    	}
    }
    
	public void sendResponse(RadiusResponse response) throws IOException,Exception {
		// TODO Auto-generated method stub
		 if (response != null) {
				if (logger.isDebugEnabled())
				    logger.debug("send response: " + response);
				RadiusRequest request=(RadiusRequest)response.getRequest();
				DatagramSocket dgs=request.getDatagramSocket();				
				InetSocketAddress remoteAddress=request.getRemoteAddress();	
				RadiusPacket stackResponse=((RadiusAbstractResponse)response).getRadiusPacket();
				RadiusPacket stackRequest=((RadiusAbstractRequest)request).getRadiusPacket();
				//If any Proxy-State attributes were present in the Access-Request,
				// they MUST be copied unmodified and in order into the response packet.
				copyProxyState(stackRequest, stackResponse);
				DatagramPacket packetOut = this.makeDatagramPacket(stackResponse, getSharedSecret(remoteAddress),
					remoteAddress.getAddress(), remoteAddress.getPort(),stackRequest);				
				dgs.send(packetOut);

			}else{
			logger.error("No response sent");	
			}
	}

	 public RadiusPacket makeRadiusPacket(DatagramPacket packet,
			    String sharedSecret) throws IOException, RadiusException {
			return super.makeRadiusPacket(packet, sharedSecret);
		    }
		    
		    public RadiusPacket handlePacket(InetSocketAddress localAddress,
			    InetSocketAddress remoteAddress, RadiusPacket request,
			    String sharedSecret) throws RadiusException, IOException {
			return super.handlePacket(localAddress, remoteAddress, request,
				sharedSecret);
		    }

		    public DatagramPacket makeDatagramPacket(RadiusPacket packet,
			    String secret, InetAddress address, int port, RadiusPacket request)
			    throws IOException {
			return super.makeDatagramPacket(packet, secret, address, port, request);

		    }

			public void handleRadiusPacket(RadiusPacket request,
					InetSocketAddress localAddress,
					InetSocketAddress remoteAddress, String secret,DatagramSocket dgs) {
				if(logger.isDebugEnabled())
					logger.debug("Inside handleRadiusPacket().......");
				if (!isPacketDuplicate(request, remoteAddress)) {
					serverInterface.handleIncomingRadiusRequest(request,localAddress,remoteAddress,dgs);
				} else
					if(logger.isDebugEnabled())
						logger.debug("Ignore duplicate packet");
			}
			
			@Override
			 /**
		     * Checks whether the passed packet is a duplicate. A packet is duplicate if
		     * another packet with the same identifier has been sent from the same host
		     * in the last time.
		     * @param packet
		     *            packet in question
		     * @param address
		     *            client address
		     * @return true if it is duplicate
		     */
		    protected boolean isPacketDuplicate(RadiusPacket packet,
			    InetSocketAddress address) {
				long now = System.currentTimeMillis();
				byte[] authenticator = packet.getAuthenticator();
		    	String key = packet.getPacketIdentifier() + address.toString();
				ReceivedPacket p = receivedPackets.get(key);
				if (p != null) {
					if (authenticator != null && p.authenticator != null) {
						// packet is duplicate if stored authenticator is equal
						// to the packet authenticator
						return Arrays.equals(p.authenticator, authenticator);
					} else {
						// should not happen, packet is duplicate
						return true;
					}
				}
				// add packet to receive list
				ReceivedPacket rp = new ReceivedPacket();
				rp.address = address;
				rp.packetIdentifier = packet.getPacketIdentifier();
				rp.receiveTime = now;
				rp.authenticator = authenticator;
				receivedPackets.put(key, rp);
				return false;
		    }
			
			class MapCleanerThread extends Thread{
		    	public void run() {
		    		while(!closing){
		    			long now = System.currentTimeMillis();
		    			long intervalStart = now - getDuplicateInterval();
						for (Entry<String, ReceivedPacket> e : receivedPackets
								.entrySet()) {
		    			    ReceivedPacket p = e.getValue();
		    			    if (p.receiveTime < intervalStart) {
		    			    		receivedPackets.remove(e.getKey());
		    			} 				
		    		}
						try {
							sleep(getDuplicateInterval());
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    	}

		    }
		}
}
/**
 * This internal class represents a packet that has been received by the server.
 */
class ReceivedPacket {

    /**
     * The identifier of the packet.
     */
    public int packetIdentifier;

    /**
     * The time the packet was received.
     */
    public long receiveTime;

    /**
     * The address of the host who sent the packet.
     */
    public InetSocketAddress address;

    /**
     * Authenticator of the received packet.
     */
    public byte[] authenticator;

}
