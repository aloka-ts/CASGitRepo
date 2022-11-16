
package com.baypackets.ase.teststubs;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseBaseResponse;
import javax.servlet.sip.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

public class DummySipSession extends AseProtocolSession implements SipSession {

        DummySipSession(String id) {
                super(id);
        }

        public void invalidate() {
        }



        public String getProtocol() {
                return "SIP";
        }



        public void removeAttribute (String str) {}





	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSession#getCallId()
	 */
	public String getCallId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSession#getLocalParty()
	 */
	public Address getLocalParty() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSession#getRemoteParty()
	 */
	public Address getRemoteParty() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSession#createRequest(java.lang.String)
	 */
	public SipServletRequest createRequest(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}




	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSession#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public boolean getInvalidateWhenReady() {
		// TODO Auto-generated method stub
		return false;
	}

	public SipApplicationRoutingRegion getRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getSubscriberURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isReadyToInvalidate() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setInvalidateWhenReady(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setOutboundInterface(InetSocketAddress arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setOutboundInterface(InetAddress arg0) {
		// TODO Auto-generated method stub
		
	}

	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}


}
