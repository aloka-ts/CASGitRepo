/*
 * HeartbeatServlet.java
 */
package com.baypackets.ase.externaldevice;

import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerSelector;

import org.apache.log4j.Logger;

import com.baypackets.ase.mediaserver.MediaServerImpl;
import com.baypackets.ase.mediaserver.MediaServerManager;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sbb.ExternalDevice;
import com.baypackets.ase.util.AseUtils;
/*
 * HeartbeatServlet provides generic heartbeat processing for a set of External Devices
 * An instance of this class is created by a SIP servlet. The SIP servlet class implements
 * the HeartbeatListener interface by which this class communicates for container services,
 * provisioned data and event reporting.
 */
public class HeartbeatServlet {

    /* Strings used for storing information in the session contexts */
    private static String DEVICE_ID = "DEVICE_ID";
    private static String RETRY_COUNT = "RETRY_COUNT";
    private static String RESPONSE_PENDING = "RESPONSE_PENDING";

    private static Logger logger = Logger.getLogger(HeartbeatServlet.class);

    /**
     * Reference to the listener implemention
     */
    private HeartbeatListener listener;
    /**
     * Single application session used to send heartbeats
     */
    private SipApplicationSession appSession;
    /**
     * Application session ID from application session above.
     */
    private String appSessionId;
    /**
     * SIP Factory needed to create messages
     */
    private SipFactory factory;

    private HashMap<String, SipSession> serverMap;
    
    private MediaServerManager msManager;
    /**
     * This method is invoked once during startup to perform the
     * following actions:
     *  <ul>
     *      <li> Creates a new application session for all interactions
     *      <li> Invokes the "doHeartBeat" method.
     *  </ul>
     * @param listener callback listener object
     * @param ctx servlet context needed for access sip factory
     */
    public void init(HeartbeatListener listener, ServletContext ctx) throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("init(): Initializing the heartbeat Servlet...");
        }

    appSessionId = null;

        try {
            factory = (SipFactory)ctx.getAttribute(SipServlet.SIP_FACTORY);
            appSession = factory.createApplicationSession();
        appSessionId = appSession.getId();
            this.listener = listener;
            msManager = (MediaServerManager)ctx.getAttribute(MediaServerSelector.class.getName());
        serverMap = new HashMap<String, SipSession>();

        listener.createTimer(appSession);

        } catch (Exception e) {
            String msg = "Error occurred while initializing the heartbeat Servlet: " + e.getMessage();
            logger.error(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * This method is first called by the init method and then subsequently
     * invoked upon each expiry of the timer that is created by this method.
     * It performs the following operations:
     *  <ul>
     *      <li> Obtains the current meta data on all provisioned servers
     *           from the call back, creates a list of those servers
     *      <li> Invokes the "processExistingServers" method.
     *      <li> Invokes the "processAddedServers" method passing it the list
     *      of returned servers.
     *      <li> Invokes the "processRemovedServers" method passing it the list
     *      of servers.
     *      <li> Obtains the configured heartbeat interval from the
     *      call back and creates a one shot timer to fire at the
     *      specified interval. When the timer fires, this method will be
     *      invoked again.
     *  </ul>
     */
    public void doHeartbeat() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("doHeartbeat(): Invoked");
        }

        // Only Process if we are in ACTIVE role.
    if (listener.isActive()) {
            Iterator iterator = listener.findAll();

            Collection<ExternalDevice> servers = null;

            if (iterator != null && iterator.hasNext()) {
                servers = new ArrayList<ExternalDevice>();

                while (iterator.hasNext()) {
            ExternalDevice dev = (ExternalDevice)iterator.next();
                    servers.add(dev);
                }
            }

            this.processServers(servers);
            this.processRemovedServers(servers);

        }
    listener.createTimer(appSession);
    }


    /**
     * processServers sends heartbeats for new and existing servers.
     *  <ul>
     *      <li> If the given list of servers is null, it returns
     *      immediately.
     *      <li> For each server in the given list, it checks if there is
     *      an existing SIP session.  If not it gets the retry count from
     *      the listener and sets the send message flag
     *      <li> If a session exists, and the request pending is not set,
     *      the sendMessage flag is set and the retry count is retrieved
     *      from the existing session and that session is invalidated
     *      <li> If the send message flag was set then it sends an OPTIONS
     *      request to the server in the of a new SIP session.
     *  </ul>
     * @param servers current set of provisioned servers
     */
    public void processServers(Collection<ExternalDevice> servers) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("processServers(): Processing existing and new servers...");
        }

        if (servers == null) {
            return;
        }

        Iterator<ExternalDevice> iterator = servers.iterator();

        while (iterator.hasNext()) {
            ExternalDevice server = iterator.next();
            String id = server.getId();
            SipSession session = serverMap.get(id);

            if (server.isHeartbeatEnabled()) {
                boolean sendMessage = false;
                Integer retryCount = null;
                if (session == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("processServers(): Processing a new device with ID: " + server.getId());
                    }

                    sendMessage = true;
                    retryCount = new Integer(listener.getRetryCount());

                } else {
                    this.checkIfDown(session);

                    if (session.getAttribute(RESPONSE_PENDING) == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("processServers(): Processing an existing device with ID: " +
                                id);
                        }

                        sendMessage = true;
                        retryCount = (Integer)session.getAttribute(RETRY_COUNT);
                        if (retryCount == null) {
                            retryCount = new Integer(listener.getRetryCount());
                        }

                        //session.invalidate();

                    }
                }

                if (sendMessage) {
                	SipServletRequest request = null;
                    URI uri;
                    try {
                        uri = getTo(server);
                    } catch (Exception e) {
                        server.disableHeartbeat();
                        logger.error("Error retrieving URL from gateway device: " + id, e);
                        continue;
                    }
                    
                  //changes done for bug9852
                   /* SipServletRequest request = factory.createRequest(appSession, "OPTIONS",
                                          listener.getFrom(),
                                          uri);
					*/
                    
                    if (session == null)
                    {
                      request = this.factory.createRequest(this.appSession, "OPTIONS", this.listener.getFrom(), uri);

                      session = request.getSession();

                      session.setAttribute(DEVICE_ID, id);

                      ((AseSipSession)session).setRegion(SipApplicationRoutingRegion.TERMINATING_REGION);
                      this.serverMap.put(id, session);
                    }
                    else
                    {
                      request = session.createRequest("OPTIONS");
                      request.setRequestURI(uri);
                    }
                    
                 //   if(!uri.toString().startsWith("sip:ivr")){
                         request.setAttribute("DISABLE_OUTBOUND_PROXY", "");
                 //   }
                    //AseSipSession sess = (AseSipSession)request.getSession();
                    //sess.setAttribute(DEVICE_ID, id);
                    session.setAttribute(RETRY_COUNT, retryCount);
                    session.setAttribute(RESPONSE_PENDING, request.getCallId());
                    //sess.setRegion(SipApplicationRoutingRegion.TERMINATING_REGION);

                    //serverMap.put(id, request.getSession());

                    try {
                        request.send();
                        if (logger.isDebugEnabled()) {
                            logger.debug("processServers(): Successfully sent OPTIONS request.");
                        }
                    } catch (IOException ioe) {
                        logger.warn("processServers(): Failed to send OPTIONS request.");
                        listener.deviceDown((String)session.getAttribute(DEVICE_ID));
                        session.removeAttribute(RETRY_COUNT);
                        session.removeAttribute(RESPONSE_PENDING);
                    }
                }
            }
        }
    }


    /**
     * This method is invoked to perform the following functions.
     * <ul>
     *      <li> For each provisioned server create a collection of
     *           server IDs for still active servers (check heartbeat status)
     *      <li> For each sip session in process, if the server for that session
     *           is not in the valid set then invalidate the sip session and
     *           remove it from the application session list
     */
    public void processRemovedServers(Collection<ExternalDevice> servers) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("processRemovedServers(): Checking for any removed  servers...");
        }

        Iterator<ExternalDevice> iterator = null;

        Collection<String> serverIDs = new HashSet<String>(servers != null ? servers.size() : 0);

        if (servers != null) {
            iterator = servers.iterator();

            while (iterator.hasNext()) {
        ExternalDevice server = iterator.next();
        if (server.isHeartbeatEnabled()) {
            serverIDs.add(server.getId());
        }
            }
        }

    Set<String> set = serverMap.keySet();
    Iterator<String> serverIds = set.iterator();
    ArrayList<String> exServers = new ArrayList<String>();

        while (serverIds.hasNext()) {
            String serverId  = serverIds.next();

            if (!serverIDs.contains(serverId)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("processRemovedServers(): Device with ID, " + serverId +
                 " was removed, so invalidating it's associated SIP session...");
                }

        exServers.add(serverId);
        }
    }

    serverIds = exServers.iterator();
        while (serverIds.hasNext()) {
            String serverId  = serverIds.next();

        SipSession session = serverMap.get(serverId);
        serverMap.remove(serverId);
        session.invalidate();
        }
    }


    /**
     * This method is invoked whenever a 2xx response is received from one of
     * the servers.
     * <ul>
     *    <li> Inform with call back that the device has responsed correctly.
     *    <li> Reset the retry count to max retries
     *    <li> Reset the reply pending flag
     * <ul>
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("doSuccessResponse(): Received a " + response.getStatus() +
             " response from: " + response.getRemoteAddr());
        }

        SipSession session = response.getSession();

    // Inform manager of success response, if the server was down/suspect action will be taken
        listener.deviceUp((String)session.getAttribute(DEVICE_ID));

        String s = (String)session.getAttribute(RESPONSE_PENDING);
        if ((s != null) && (s.equals(response.getCallId()))) {
            session.setAttribute(RETRY_COUNT, new Integer(listener.getRetryCount()));
            session.removeAttribute(RESPONSE_PENDING);
        }
    }


    /**
     * This method is invoked by the container whenever an error response is
     * received  This is an error from the server, or from the container on
     * response timeout.
     * <ul>
     *    <li> Inform with call back that the device is down
     *    <li> Call checkIfDown
     * <ul>
     */
    public void doErrorResponse(SipServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("doErrorResponse(): Received a " + response.getStatus() +
             " error response from: " + response.getRemoteAddr());
        }

        // Handle Timeout through other means.
        if (response.getStatus() != SipServletResponse.SC_REQUEST_TIMEOUT) {
            SipSession session = response.getSession();

            String s = (String)session.getAttribute(RESPONSE_PENDING);
            if ((s != null) && (s.equals(response.getCallId()))) {
                listener.deviceDown((String)session.getAttribute(DEVICE_ID));
                session.removeAttribute(RESPONSE_PENDING);
            }
        }

    }


    /**
     * This method is invoked when the timer expires
     * <ul>
     *    <li> Cancel the timer
     *    <li> Call doHeartbeat
     * <ul>
     */
    public void timeout(javax.servlet.sip.ServletTimer servletTimer) {
        if (logger.isDebugEnabled()) {
            logger.debug("timeout(): Heartbeat timer has fired.  Invoking doHeartbeat() method...");
        }

        try {
            servletTimer.cancel();
            this.doHeartbeat();
        } catch (Exception e) {
            String msg = "Error occurred while pinging the  servers: " + e.getMessage();
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }


    /**
     * This method will perform one of the following actions based on the
     * value of the RETRY_COUNT attribute set in the given SIP session:
     *  <ul>
     *      <li> If the RETRY_COUNT attribute is null, no action is taken.
     *      <li> If the RETRY_COUNT value is 0, the call back is used to
     *      inform that the server represented by the given SipSession
     *      is down and the RETRY_COUNT attribute is set to null.
     *      <li> If the RETRY_COUNT is non-null and greater than 0, the call back
     *      is used to inform that the server is in a suspect state
     *      <li> If the RETRY_COUNT is non-null and greater than 0, the value
     *      is decremented by 1.
     *  </ul>
     */
    public void checkIfDown(SipSession session) {
        Integer retryCount = (Integer)session.getAttribute(RETRY_COUNT);

        if (retryCount == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("RETRY_COUNT attribute in session is null, so taking no action.");
            }
            return;
        }

        if (retryCount.intValue() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("RETRY_COUNT attribute in session is 0, so informing OutboundGatewayManager that this server is down.");
            }
            listener.deviceDown((String)session.getAttribute(DEVICE_ID));
            session.removeAttribute(RETRY_COUNT);
            session.removeAttribute(RESPONSE_PENDING);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("RETRY_COUNT attribute in session has a value of: " + retryCount +  ".  Decremeting value by 1.");
            }
            if (msManager != null){
            	MediaServer mediaServer = msManager.getMediaServer((String)session.getAttribute(DEVICE_ID));
				if ((retryCount.intValue() < listener.getRetryCount())
						&& (mediaServer != null && mediaServer.getState() != MediaServer.STATE_DOWN)) {
					listener.deviceSuspect((String) session.getAttribute(DEVICE_ID));
				}
            }else{
                if (retryCount.intValue() < listener.getRetryCount()) {
                    listener.deviceSuspect((String)session.getAttribute(DEVICE_ID));
                }
            }
            session.setAttribute(RETRY_COUNT, new Integer(retryCount.intValue() - 1));
        }
    }

    /**
     * Return the TO URI for the server, if the device has a URI provisioned
     * use that one, otherwise use the call back
     * @param dev device for which the URI is needed
     * @return URI created or obtained by call back
     */
    public URI getTo(ExternalDevice dev) throws Exception {
    if (dev.getHeartbeatUri() == null) {
        return listener.getTo(dev);
    } else {
      URI uri=factory.createURI(dev.getHeartbeatUri()); 
       if(uri.isSipURI()){
    		SipURI sipUri=(SipURI)uri;
    		String ipAddr=AseUtils.getIPAddress(sipUri.getHost());
    		sipUri.setHost(ipAddr);
    		return sipUri;
    	} 
        return factory.createURI(dev.getHeartbeatUri());
    }
    }

    /**
     * Test whether the passed in application sess is the one
     * being used for heartbeats
     * @param sess application session to test
     * @return true is application session matches, false otherwise.
     */
    public boolean isMyAppSession(SipApplicationSession sess) {
    return appSessionId.equals(sess.getId());
    }

}
