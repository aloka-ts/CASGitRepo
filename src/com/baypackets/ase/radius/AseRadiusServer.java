package com.baypackets.ase.radius;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.radius.RadiusService;
import com.baypackets.ase.spi.radius.RadiusServiceException;
import com.baypackets.ase.spi.radius.RadiusSession;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.workmanager.WorkManagerImpl;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import commonj.work.Work;
import commonj.work.WorkException;

public class AseRadiusServer extends RadiusServer implements MComponent {

    public ConfigRepository configRep;
    public RadiusService radiusService;
    private WorkManagerImpl workMgr;
    private int state = MComponentState.STOPPED;
    private String sessionTimeout;
    private String idleTimeout;
    private static Log logger = LogFactory.getLog(AseRadiusServer.class);

    public void init() throws RadiusException {
	try {
	    this.configRep = (ConfigRepository) Registry
		    .lookup(Constants.NAME_CONFIG_REPOSITORY);
	    String serverIP = this.configRep
		    .getValue(Constants.OID_BIND_ADDRESS);
	    if (serverIP != null && !serverIP.equals("")) {
		try {
		    this.setListenAddress(InetAddress.getByName(serverIP));
		} catch (UnknownHostException e) {
			if (logger.isInfoEnabled())
				logger.info(e.getMessage(), e);
		    throw new RadiusException(e.getMessage());
		}
	    }
	    sessionTimeout = this.configRep
		    .getValue(Constants.RADIUS_SESSION_TIMEOUT);
	    idleTimeout = this.configRep
		    .getValue(Constants.RADIUS_IDLE_TIMEOUT);
	    String authPort = this.configRep
		    .getValue(Constants.RADIUS_AUTH_PORT);
	    String accountingPort = this.configRep
		    .getValue(Constants.RADIUS_ACCOUNTING_PORT);
	    String socketTimeOut = this.configRep
		    .getValue(Constants.RADIUS_SOCKET_TIMEOUT);
	    if (authPort != null && !authPort.equals("")) {
		this.setAuthPort(Integer.parseInt(authPort));
	    }
	    if (accountingPort != null && !accountingPort.equals("")) {
		this.setAcctPort(Integer.parseInt(accountingPort));
	    }
	    if (socketTimeOut != null && !socketTimeOut.equals("")) {
		this.setSocketTimeout(Integer.parseInt(socketTimeOut));
	    }
	    this.workMgr = new WorkManagerImpl("RadiusWorkManager",
		    "RadiusServer");
	    this.workMgr.initialize();
	} catch (Exception e) {
	    logger.error("problem while initializing radius server");
	    logger.error(e.getMessage(), e);
	}
    }

    public void start(boolean listenAuth, boolean listenAcct) {
	if (logger.isDebugEnabled())
		logger.debug("going to start radius server with workmanger");
	this.workMgr.start();
	super.start(listenAuth, listenAcct);
    }

    public void stop() {
	if (logger.isInfoEnabled())
		logger.info("stopping WorkManager server");
	this.workMgr.stop();
	super.stop();
    }

    @Override
    public String getSharedSecret(InetSocketAddress client) {
	String secret = this.configRep.getValue(Constants.RADIUS_SHARED_SECRET);
	if (logger.isDebugEnabled())
		logger.debug("shared secret = " + secret);
	return secret;
    }

    @Override
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest,
	    InetSocketAddress client) throws RadiusException {
	if (logger.isDebugEnabled())
		logger.debug("inside accessRequestReceived method");
	radiusService = (RadiusService) Registry
		.lookup(Constants.RADIUS_SERVICE);
	if (radiusService == null) {
		if (logger.isDebugEnabled())
			logger.debug("radius service object found is " + radiusService);
	}
	RadiusSession radiusSession = new RadiusSession();
	radiusSession.setUsername(accessRequest.getUserName());
	radiusSession.setPassword(accessRequest.getUserPassword());
	radiusSession.setRequestType(RadiusSession.ACCESS);
	try {
		if (logger.isDebugEnabled())
			logger.debug("calling radius service with radiusSession = "
		    + radiusSession.toString());
	    radiusService.doAccess(radiusSession);
		if (logger.isDebugEnabled())
			logger.debug("radiusSession object after service call back= "
		    + radiusSession.toString());
	} catch (RadiusServiceException e) {
	    logger.error(e.getMessage(), e);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}

	// String plaintext = getUserPassword(accessRequest.getUserName());

	int type = RadiusPacket.ACCESS_REJECT;
	if (radiusSession.isAuthenticated()) {
	    type = RadiusPacket.ACCESS_ACCEPT;

	}


	RadiusPacket answer = new RadiusPacket(type, accessRequest
		.getPacketIdentifier());

	copyProxyState(accessRequest, answer);

	if (type == RadiusPacket.ACCESS_ACCEPT) {

	    String uploadBandwidth = radiusSession
		    .getVendorAttribute("fdXtended-Bandwidth-Up");
	    if (uploadBandwidth != null && !uploadBandwidth.equals("")) {
		answer.addAttribute("fdXtended-Bandwidth-Up", uploadBandwidth);
	    }

	    String downloadBandwidth = radiusSession
		    .getVendorAttribute("fdXtended-Bandwidth-Down");//fdXtended-BW-Down
	    if (downloadBandwidth != null && !downloadBandwidth.equals("")) {
		answer.addAttribute("fdXtended-Bandwidth-Down", downloadBandwidth);
	    }


	    String idleTimeoutfrmSvc = radiusSession
		    .getVendorAttribute("Idle-Timeout");
	    if (idleTimeoutfrmSvc != null && !idleTimeoutfrmSvc.equals("")) {
		answer.addAttribute("Idle-Timeout", idleTimeoutfrmSvc);
	    } else {
		answer.addAttribute("Idle-Timeout", idleTimeout);
	    }

	    String sessionTimeoutfrmSvc = radiusSession
		    .getVendorAttribute("Session-Timeout");
	    if (sessionTimeoutfrmSvc != null
		    && !sessionTimeoutfrmSvc.equals("")) {
		answer.addAttribute("Session-Timeout", sessionTimeoutfrmSvc);
	    } else {
		answer.addAttribute("Session-Timeout", sessionTimeout);
	    }

	    String fdXtendedExpiration = radiusSession
		    .getVendorAttribute("fdXtended-Expiration");
	    try {
		Integer.parseInt(fdXtendedExpiration);
		if (fdXtendedExpiration != null && !fdXtendedExpiration.equals("")) {
		    // answer.addAttribute("fdXtended-Expiration",
		    // fdXtendedExpiration);
		    answer.addAttribute("Session-Timeout", fdXtendedExpiration);
		}
	    } catch (NumberFormatException e) {
		logger.error("Value of fdXtended-Expiration is invalid "
			+ e.getMessage());
	    }


	}
	if (logger.isDebugEnabled())
		logger.debug("leaving accessRequestReceived");
	return answer;
    }

    /**
     * Constructs an answer for an Accounting-Request packet. This method should
     * be overriden if accounting is supported.
     * @param accountingRequest
     *            Radius request packet
     * @param client
     *            address of Radius client
     * @return response packet or null if no packet shall be sent
     * @exception RadiusException
     *                malformed request packet; if this exception is thrown, no
     *                answer will be sent
     */
    public RadiusPacket accountingRequestReceived(
	    AccountingRequest accountingRequest, InetSocketAddress client)
	    throws RadiusException {

	RadiusSession radiusSession = new RadiusSession();
	radiusSession.setUsername(accountingRequest.getUserName());
	if (accountingRequest.getAcctStatusType() == AccountingRequest.ACCT_STATUS_TYPE_START) {
	    radiusSession.setRequestType(RadiusSession.ACCOUNTING_START);
	} else if (accountingRequest.getAcctStatusType() == AccountingRequest.ACCT_STATUS_TYPE_STOP) {
	    radiusSession.setRequestType(RadiusSession.ACCOUNTING_STOP);
	}
	try {
		if (logger.isDebugEnabled())
			logger
				.debug("ACCOUNTING:calling radius service with radiusSession = "
				+ radiusSession.toString());
	    radiusService.doAccounting(radiusSession);
		if (logger.isDebugEnabled())
			logger.debug("radiusSession object after service call back= "
		    + radiusSession.toString());
	} catch (RadiusServiceException e) {
	    logger.error(e.getMessage(), e);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}

	RadiusPacket answer = new RadiusPacket(
		RadiusPacket.ACCOUNTING_RESPONSE, accountingRequest
			.getPacketIdentifier());
	copyProxyState(accountingRequest, answer);
	return answer;
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
		if (logger.isDebugEnabled())
			logger.debug("about to call socket.receive()");
		dgs.receive(packetIn);
		if (logger.isDebugEnabled())
		    logger.debug("receive buffer size = "
			    + dgs.getReceiveBufferSize());
	    } catch (SocketException se) {
		if (closing) {
		    // end thread
			if (logger.isInfoEnabled())
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

	    Work radiusWork = new RadiusWork(this, dgs, packetIn);
	    try {
		workMgr.schedule(radiusWork);
	    } catch (IllegalArgumentException e) {
		logger.error(e.getMessage(), e);
	    } catch (WorkException e) {
		logger.error(e.getMessage(), e);
	    }
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

    @Override
    public String getUserPassword(String userName) {
	if (logger.isInfoEnabled())
		logger.info("do not call this method");
	return null;
    }

    public void changeState(MComponentState componentState)
	    throws UnableToChangeStateException {
	if (logger.isDebugEnabled())	
		logger.debug("changeState(MComponentState): enter");

	switch (componentState.getValue()) {
	case MComponentState.LOADED:
	    if (state == MComponentState.STOPPED) {
			if (logger.isDebugEnabled())
				logger.debug("Component State: STOPPED ==> LOADED");

		try {
		    this.init();
		    state = MComponentState.LOADED;
		} catch (RadiusException e) {
		    logger.error("Exception in Initializing Radius Server", e);
		}
	    } else {
		logger.error("Illegal State Received");
		throw new UnableToChangeStateException("Illegal State Received");
	    }
	    break;

	case MComponentState.RUNNING:
	    if (state == MComponentState.LOADED) {
			if (logger.isDebugEnabled())
				logger.debug("Component State: LOADED ==> RUNNING");

		this.start(true, true);

		state = MComponentState.RUNNING;
	    } else {
		logger.error("Illegal State Received");
		throw new UnableToChangeStateException("Illegal State Received");
	    }
	    break;
	    

	case MComponentState.SOFT_STOP:
		if (state == MComponentState.RUNNING) {
			if (logger.isDebugEnabled()){
				logger.debug("Component State: RUNNING ==> SOFT STOP");
			}		
		state = MComponentState.SOFT_STOP;
		if (logger.isDebugEnabled())
			logger.debug("Component State changed to SOFT STOP . Do Nothing");
		}else {
		logger.error("Illegal State Received");
		throw new UnableToChangeStateException("Illegal State Received");
		}
		break;

	case MComponentState.STOPPED:
	    if (state == MComponentState.RUNNING) {
			if (logger.isDebugEnabled())
				logger.debug("Component State: RUNNING ==> STOPPED");

		this.stop();

		state = MComponentState.STOPPED;
	    } else if (state == MComponentState.SOFT_STOP){
	    	if(logger.isDebugEnabled()){
	    		logger.debug("Component State: SOFT_STOP ==> STOPPED");
	    	}
	    this.stop();
	    
		state = MComponentState.STOPPED;
	    } else {
		logger.error("Illegal State Received");
		throw new UnableToChangeStateException("Illegal State Received");
	    }
	    break;
		
		
	default:
	    logger.error("Illegal State Received");
	    throw new UnableToChangeStateException("Illegal State Received");
	}// switch
	if (logger.isDebugEnabled())
		logger.debug("changeState(MComponentState): exit");
    }

    public void updateConfiguration(Pair[] configData, OperationType optype)
	    throws UnableToUpdateConfigException {
		if (logger.isDebugEnabled())
			logger.debug("updateConfiguration(Pair[], OperationType): called");
    }

}
