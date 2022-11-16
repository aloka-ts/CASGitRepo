/*
 * Created on Aug 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;

import javax.servlet.ServletRequest;

import com.baypackets.ase.spi.container.SasMessage;

/**
 * 
 */
public interface AseBaseRequest extends ServletRequest, SasMessage {
	
	//public Dispatcher getDispatcher();
	
	//public AseProtocolSession getProtocolSession();
	
	public AseProtocolSession getPrevSession();

	public void setPrevSession(AseProtocolSession session);

	//public boolean isInitial();
	
	//public Destination getDestination();

	//public void setDestination(Destination dest);

	public boolean chainedDownstream();

	public void setChainedDownstream();

	//public boolean isLoopback();

	//public void setLoopback();

	//public AseIc getIc();

	//public String getMethod();
	
	//public Subject getSubject();
	
	//public void setSubject(Subject subject);
        
	//public void setUserPrincipal(Principal principal);
}
