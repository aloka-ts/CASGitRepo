package com.baypackets.ase.ra.enumserver.message;

import org.xbill.DNS.RRset;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

public interface EnumMessageFactory extends MessageFactory {

	SasMessage createResponse(SasProtocolSession session, int send,
			EnumRequest request, RRset[] records) throws ResourceException;
	
	SasMessage createRequest(SasProtocolSession session, int type, byte[] message) throws ResourceException;

}
