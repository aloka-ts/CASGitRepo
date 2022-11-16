package com.baypackets.ase.sbb.mediaserver;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.b2b.EarlyMediaConnectHandler;

public class MsConnectHandler extends EarlyMediaConnectHandler implements EarlyMediaCallback {
	
	private static final long serialVersionUID = 4071455728543333637L;
	
	private static final Logger logger = Logger.getLogger(MsConnectHandler.class);
	
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public MsConnectHandler() {
		super();
	}

	public MsConnectHandler(SipServletRequest request, Address partyB, Address from) {
		super(request, partyB, from);
		// TODO Auto-generated constructor stub
	}

	public MsConnectHandler(SipServletRequest incomingReq, Address addressB) {
		super(incomingReq, addressB);
	}

	public void handleResponse(SipServletResponse response) {
		int responseCode = response.getStatus();
		if(responseCode >=200 && responseCode < 299 && response.getMethod().equals("INVITE")){
			((MsSessionControllerImpl)this.getSBB()).parseSDP(response);
	   }
		
		//Do the default handling as done in the base class.
		super.handleResponse(response);
	}
}
