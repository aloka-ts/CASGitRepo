package com.baypackets.ase.ra.diameter.ro;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public interface RoResourceFactory extends DefaultResourceFactory {

	//public ShRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException;

	public CreditControlRequest createRequest (SipApplicationSession appSession,int type) throws ResourceException;

	public CreditControlRequest createRequest(SipApplicationSession appSession, int type ,String remoteRealm)
			throws ResourceException;
	
	public DiameterRoMessageFactory getDiameterRoMessageFactory();
	
	public DiameterMessageFactory getDiameterBaseMessageFactory();

	CreditControlRequest createRequest(SipApplicationSession appSession,String SessionId, int type,
			String remoteRealm) throws ResourceException;
}
