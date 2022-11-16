package com.baypackets.ase.ra.http;

import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface HttpResourceFactory extends DefaultResourceFactory{
	
	public HttpRequest createRequest(SipApplicationSession appSession, String url, String httpMethod) throws ResourceException;

}
