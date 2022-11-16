package com.baypackets.ase.spi.container;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.spi.replication.ReplicationListener;

public interface SasApplicationSession extends SipApplicationSession {

	public void addProtocolSession(SasProtocolSession protocolSession);
	
	public void removeProtocolSession(SasProtocolSession session);
	
	public SasApplication getApplication();
	
	public ReplicationListener getReplicationListener();
	
	public void setLastAccessedTime(long time);

	public String getAppSessionId();
		
	//@saneja added to support reset of timeout; 
	//internal to SAS
	public void setTimeout(int timeout);
	
}
