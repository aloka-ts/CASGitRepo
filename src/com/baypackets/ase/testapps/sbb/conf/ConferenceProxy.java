/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */



package com.baypackets.ase.testapps.sbb.conf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;

public class ConferenceProxy extends SipServlet {

	public ConferenceProxy() {
		super();
	}

	protected void doInvite(SipServletRequest req) throws ServletException, IOException {
		if(!req.isInitial()){
			log("Received a Non-INVITE request. So ignoring it");
			return;
		}
		
		SipFactory factory = (SipFactory) getServletContext().getAttribute(SipServlet.SIP_FACTORY);
		Proxy proxy = req.getProxy();
		proxy.setRecordRoute(true);
		proxy.setRecurse(true);
		proxy.proxyTo(factory.createURI("sip:12345@192.168.2.197:5060"));
	}

	
}
