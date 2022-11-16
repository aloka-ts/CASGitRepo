/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
*/



package com.baypackets.ase.testapps.b2b;

import javax.servlet.sip.*;
import java.util.*;

public class SipMessageManager {
	
	private static String PEER_REQUEST = "PEER_REQUEST";

	private Map inviteMap = new Hashtable();
	private Map responseMap = new Hashtable();

	public void correlate(SipServletRequest incoming, SipServletRequest outgoing) {
		incoming.setAttribute(PEER_REQUEST, outgoing);
		outgoing.setAttribute(PEER_REQUEST, incoming);
	
		if (incoming.getMethod().equals("INVITE")) {
			this.inviteMap.put(incoming.getApplicationSession().getId(), incoming);
		}
	}

	public SipServletMessage getAssociatedMessage(SipServletRequest request) {
		if (request.getMethod().equals("CANCEL")) {
			return (SipServletMessage)this.inviteMap.get(request.getApplicationSession().getId());
		}
		return (SipServletMessage)request.getAttribute(PEER_REQUEST);	
	}

	public void storeInviteResponse(SipServletResponse response) {
		this.responseMap.put(response.getApplicationSession().getId(), response);
	}

	public SipServletResponse getInviteResponse(String appSessionId) {
		return (SipServletResponse)this.responseMap.get(appSessionId);
	}

	public void cleanUp(String appSessionId) {
		this.inviteMap.remove(appSessionId);
		this.responseMap.remove(appSessionId);
	}

}
