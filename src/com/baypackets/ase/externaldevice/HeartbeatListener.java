package com.baypackets.ase.externaldevice;

import java.util.Iterator;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.URI;

import com.baypackets.ase.sbb.ExternalDevice;

/**
 * The HeartbeatListener interface is the mechanism for the HeartbeatServlet to call back to the
 * invoking servlet as needed for information, container services and event reporting.
 * @see com.baypackets.ase.externaldevice.HeartbeatServlet
 */
public interface HeartbeatListener {

    /**
     * Call back to iterator to access provisioned servers
     */
    public Iterator findAll();

    /**
     * Call back to request servlet timer creation
     */
    public void createTimer(SipApplicationSession sess);

    /**
     * Call back to get the TO URI specific to the External Device implementation
     */
    public URI getTo(ExternalDevice dev) throws Exception;

    /**
     * Call back to get the FROM URI specific to the External Device implementation
     */
    public URI getFrom() throws Exception;

    /**
     * Call back to get the configured retry count for the device type
     */
    public int getRetryCount();

    /**
     * Event report for device connectivity established
     */
    public void deviceUp(String id);

    /**
     * Event report for device connectivity lost
     */
    public void deviceDown(String id);

    /**
     * Event report for device connectivity suspect (first heartbeat failure)
     */
    public void deviceSuspect(String id);

    /**
     * Call back to get current server role
     */
    public boolean isActive();

}
