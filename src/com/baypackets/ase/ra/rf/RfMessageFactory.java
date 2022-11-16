package com.baypackets.ase.ra.rf;

import javax.servlet.sip.SipURI;

import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.MessageFactory;

import com.condor.rf.rfMessages.rfAccntResponse ;

public interface RfMessageFactory extends MessageFactory 
{
	public SasMessage createResponse(SasMessage request) throws RfResourceException;
	
	public SasMessage createResponse(SasMessage request, int type ,  rfAccntResponse response) throws RfResourceException;
}
