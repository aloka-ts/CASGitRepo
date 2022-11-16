package com.baypackets.ase.ari;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

public interface AriSipSession extends SipSession {
    public void setRegion(SipApplicationRoutingRegion region);

    public SipApplicationRoutingRegion getRegion();

    public void setSubscriberURI(String uri);

    public URI getSubscriberURI();
}
 