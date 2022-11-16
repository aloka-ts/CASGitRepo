/*
 * Created on Aug 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;

import javax.servlet.ServletResponse;

import com.baypackets.ase.spi.container.SasMessage;

/**
 * 
 */
public interface AseBaseResponse extends ServletResponse, SasMessage {
		
	//public AseProtocolSession getProtocolSession();

	public AseProtocolSession getPrevSession();

	public AseBaseRequest getBaseRequest();

	public void setPrevSession(AseProtocolSession session);

}
