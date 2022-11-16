/*
 * MsHeartBeatServlet.java
 *
 * Created on July 2, 2005, 10:23 PM
 */
package com.baypackets.ase.sysapps.msheartbeat;

import com.baypackets.ase.mediaserver.MediaServerManager;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.externaldevice.HeartbeatServlet;
import com.baypackets.ase.externaldevice.HeartbeatListener;
import com.baypackets.ase.sbb.ExternalDevice;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipConnector;

import java.net.InetAddress;
import java.util.Iterator;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.TimerService;
import org.apache.log4j.Logger;

public class MsHeartBeatServlet extends SipServlet implements TimerListener, SipApplicationSessionListener, HeartbeatListener {

    private static Logger logger = Logger.getLogger(MsHeartBeatServlet.class);
    private static MediaServerManager msManager;
    private static SipFactory factory;
    private static HeartbeatServlet hbServlet;


    /**
     * This method is invoked once during container startup to perform the
     * following actions:
     *  <ul>
     *      <li> Gets the sip factory
     *      <li> gets the Media Server Manager/Selector object
     *      <li> creates/initializes the heartbeat servlet handler
     *  </ul>
     */
    public void init() throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("init(): Initializing the media server heartbeat Servlet...");
        }

        try {
        factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
            msManager =
                (MediaServerManager)this.getServletContext().getAttribute(MediaServerSelector.class.getName());

        hbServlet = new HeartbeatServlet();
        hbServlet.init(this, this.getServletContext());

        } catch (Exception e) {
            String msg = "Error occurred while initializing the media server heartbeat Servlet: " + e.getMessage();
            logger.error(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * This method is invoked whenever a 2xx response is received.
     * If the app session id matches the one created by the heartbeat
     * handler pass the response to it.
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("doSuccessResponse(): Received a " + response.getStatus() +
             " response from: " + response.getRemoteAddr());
        }

    if (hbServlet.isMyAppSession(response.getApplicationSession())) {
        hbServlet.doSuccessResponse(response);
    }
    }


    /**
     * This method is invoked whenever an error response is received.
     * If the app session id matches the one created by the heartbeat
     * handler pass the response to it.
     */
	public void doErrorResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("doErrorResponse(): Received a "
					+ response.getStatus() + " error response from: "
					+ response.getRemoteAddr());
		}

		if (hbServlet.isMyAppSession(response.getApplicationSession())) {
			if (response.getStatus() != SipServletResponse.SC_REQUEST_TIMEOUT  && response.getStatus() != SipServletResponse.SC_SERVICE_UNAVAILABLE){
				logger.error("non-408 response received, still treating MS as UP");
				hbServlet.doSuccessResponse(response);
			}else{
				logger.error("408 or 503 response received");
			}
			//No need of invoking error response on the listener as now for error
			//responses other than 408 are symbol of Media Server is still UP
			//hbServlet.doErrorResponse(response);
		}
	}

    /*
     * Implementation of the TimerListener interface
     */

    /**
     * This method is invoked whenever the timer that was created by the
     * heartbeat handler fires.  This method simply invokes the heartbeat
     * handerl timeout method.
     */
    public void timeout(javax.servlet.sip.ServletTimer servletTimer) {
        if (logger.isDebugEnabled()) {
            logger.debug("timeout(): Heartbeat timer has fired.  Invoking doHeartBeat() method...");
        }

        try {
        hbServlet.timeout(servletTimer);
        } catch (Exception e) {
            String msg = "Error occurred while during timeout processing : " + e.getMessage();
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }


    /*
     * Implementation of the SipApplicationSessionListener interface
     */

    /**
     * This method is invoked by the container when it is about to expire the
     * app session.  This method will request that the container extend the app
     * session by some delta number of minutes.
     */
    public void sessionExpired(SipApplicationSessionEvent event) {
    if (hbServlet.isMyAppSession(event.getApplicationSession())) {
        event.getApplicationSession().setExpires(3);
    }
    }


    /**
     *
     */
    public void sessionDestroyed(SipApplicationSessionEvent event) {
        // No implementation, interface requirement
    }


    /**
     *
     */
    public void sessionCreated(SipApplicationSessionEvent event) {
        // No implementation, interface requirement
    }


    /*
     * Implementation of the HeartbeatListener interface
     */

    /**
     * Call back from heartbeat handler, pass call to manager to get servers
     */
    public Iterator findAll() {
    return msManager.findAll();
    }

    /**
     * Call back from heartbeat handler to create timer, create based on configured
     * heartbeat interval
     */
    public void createTimer(SipApplicationSession sess) {
        TimerService timerService = (TimerService)this.getServletContext().getAttribute(TIMER_SERVICE);
        timerService.createTimer(sess, msManager.getMediaServerHeartBeatInterval() * 1000, false, null);
    }

    /**
     * Call back from heartbeat handler to create To header for heartbeat.
     */
    public URI getTo(ExternalDevice dev) throws Exception {
    MediaServer ms = (MediaServer)dev;
        MsAdaptor msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(ms);
        String uri = msAdaptor.getMediaServerURI(ms, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
        return factory.createURI(uri);
    }

    /**
     * Call back from heartbeat handler to create From header for heartbeat.
     */
    public URI getFrom() throws Exception {
        return factory.createURI("sip:gb.com");
    }

    /**
     * Call back from heartbeat handler to get configure retry count
     */
    public int getRetryCount() {
    return msManager.getRetryCount();
    }

    /**
     * call back from heartbeat handler to indicate device is up.
     */
    public void deviceUp(String id) {
    msManager.mediaServerUp(id);
    }

    /**
     * call back from heartbeat handler to indicate device is down.
     */
    public void deviceDown(String id) {
    msManager.mediaServerDown(id);
    }

    /**
     * call back from heartbeat handler to indicate device is suspect.
     */
    public void deviceSuspect(String id) {
    msManager.mediaServerSuspect(id);
    }
    
    public MediaServer getMediaServer(String id){
    	return msManager.getMediaServer(id);
    }

    /**
     * Call back to get if server role ACTIVE
     */
    public boolean isActive() {
        AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
        return connector.getRole() == 1;
    }


	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
