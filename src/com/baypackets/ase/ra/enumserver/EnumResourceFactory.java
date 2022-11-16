package com.baypackets.ase.ra.enumserver;

import javax.servlet.sip.SipApplicationSession;

import org.xbill.DNS.RRset;

import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumResponse;
import com.baypackets.ase.ra.enumserver.rarouter.EnumAppRouter;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface EnumResourceFactory extends DefaultResourceFactory{
	
	public EnumResponse createResponse(SipApplicationSession appsession,int messageId,RRset[] records,EnumRequest request) throws ResourceException;

	public EnumRequest createRequest(byte[] message) throws ResourceException;

	public EnumAppRouter getAppRouter();

}
