package com.baypackets.ase.ari;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;

public interface AriSipServletRequest extends SipServletRequest {
        public void setRoutingDirective(SipApplicationRoutingDirective directive, 
					SipServletRequest origRequest)
                                        throws IllegalStateException;
}
