/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
*/




package com.baypackets.ase.testapps.b2b;

import com.baypackets.ase.sbb.CDR;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.log4j.Logger;

public class B2bServlet extends SipServlet {

	private static String PEER_SESSION_ID = "PEER_SESSION_ID";
	private static String STATE = "CALL_STATE";
	private static String INITIAL = "INITIAL";
	private static String EARLY = "EARLY";
	private static String IN_PROGRESS = "IN_PROGRESS";
	private static String CONFIRMED = "CONFIRMED";
	private static String CONNECTED = "CONNECTED";
	private static String DISCONNECTED = "DISCONNECTED";
	private static String FAILED = "FAILED";
	private static String CANCELLED = "CANCELLED";
	private static Logger _logger = Logger.getLogger(B2bServlet.class);
	private static Configuration _config;
	private static SipMessageManager _msgMngr;
	private static SipFactory _factory;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if (_logger.isDebugEnabled()) {
			_logger.debug("init() called...");
		}

		try {	
			_factory = (SipFactory)this.getServletContext().getAttribute(SIP_FACTORY);
			File file = new File(config.getInitParameter("config-file"));
			boolean reload = new Boolean(config.getInitParameter("reload-if-modified")).booleanValue();
			_config = new Configuration(file, reload, _factory); 
			_msgMngr = new SipMessageManager();	
		} catch (Exception e) {
			String msg = "Error occurred while initializing B2bServlet: " + e.getMessage();
			_logger.error(msg, e);
			throw new ServletException(msg);
		}		
	}


	public void doRequest(SipServletRequest request) throws ServletException, IOException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Received a " + request.getMethod() + " request from: " + request.getRemoteAddr());
		}

		if (request.getMethod().equals("INVITE") && request.isInitial()) {
			this.doInvite(request);
		} else if (request.getMethod().equals("ACK")) {
			this.doAck(request);
		} else if (request.getMethod().equals("CANCEL")) {
			this.doCancel(request);
		} else {
			SipApplicationSession appSession = request.getApplicationSession();

			if (request.getMethod().equals("BYE")) {
				String callState = (String)appSession.getAttribute(STATE);
				
				if (DISCONNECTED.equals(callState)) {
					return;
				}
				appSession.setAttribute(STATE, DISCONNECTED);
				_msgMngr.cleanUp(appSession.getId());
			} else {
				appSession.setAttribute(STATE, IN_PROGRESS);
			}
			
			SipSession session = this.getPeerSession(request.getSession());
			SipServletRequest outgoing = session.createRequest(request.getMethod());
			this.setContent(request, outgoing);
			_msgMngr.correlate(request, outgoing);

			if (request.getHeader("RACK") != null) {
				outgoing.setHeader("RACK", request.getHeader("RACK"));
			}

			this.sleep(request);
			outgoing.send();
		}
	}


	public void doAck(SipServletRequest request) throws ServletException, IOException {
		SipApplicationSession appSession = request.getApplicationSession();
		appSession.setAttribute(STATE, CONNECTED);
		SipServletResponse response = _msgMngr.getInviteResponse(appSession.getId());
		SipServletRequest outgoing = response.createAck();
		this.setContent(request, outgoing);
		this.sleep(request);
		outgoing.send();
	}


	public void doInvite(SipServletRequest request) throws ServletException, IOException {
		request.createResponse(100).send();

		SipApplicationSession appSession = request.getApplicationSession();
		appSession.setAttribute(STATE, INITIAL);
		SipURI uri = (SipURI)_config.getPeerRequestURI(request.getRemoteAddr());
		SipServletRequest outgoing = _factory.createRequest(appSession, "INVITE", request.getFrom().getURI(), uri);
		request.getSession().setAttribute(PEER_SESSION_ID, outgoing.getSession().getId());
		outgoing.getSession().setAttribute(PEER_SESSION_ID, request.getSession().getId());
		this.setContent(request, outgoing);

		if (request.getHeader("Supported") != null) {
			outgoing.setHeader("Supported", request.getHeader("Supported"));
		}       

		_msgMngr.correlate(request, outgoing);

		if (_config.writeCDR()) {
			request.getSession().getAttribute(CDR.class.getName());
		}

		this.sleep(request);
		outgoing.send();		
	}

	
	public void doResponse(SipServletResponse response) throws ServletException, IOException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Received a " + response.getStatus() + " for a " + response.getMethod() + " request from: " + response.getRemoteAddr());
		}
		
		SipApplicationSession appSession = response.getApplicationSession();
		SipServletRequest request = (SipServletRequest)_msgMngr.getAssociatedMessage(response.getRequest());
		SipServletResponse outgoing = request.createResponse(response.getStatus());
		this.setContent(response, outgoing);

		if (_config.writeCDR()) {
			if (response.getMethod().equals("BYE")) {
				try {
					CDR cdr = (CDR)response.getSession().getAttribute(CDR.class.getName());
					cdr.write();
					cdr = (CDR)this.getPeerSession(response.getSession()).getAttribute(CDR.class.getName());
					cdr.write();
				} catch (Exception e) {
					this.log("Error occurred while writing CDRs: " + e.getMessage(), e);
				}
			} else if (response.getRequest().isInitial() && response.getMethod().equals("INVITE")) {
				response.getSession().getAttribute(CDR.class.getName());
			}
		}
	
		if (response.getStatus() >= 100 && response.getStatus() < 200) {
			appSession.setAttribute(STATE, EARLY);

			if (response.getHeader("Require") != null) {
				outgoing.setHeader("Require", response.getHeader("Require"));
			}
		} else if (response.getStatus() >= 200 && response.getStatus() < 300) {
			appSession.setAttribute(STATE, CONFIRMED);
		} else if (response.getStatus() >= 400) {
			appSession.setAttribute(STATE, FAILED);
		}

		if (response.getStatus() >= 200 && response.getStatus() < 300 &&
		    response.getMethod().equals("INVITE")) {
			_msgMngr.storeInviteResponse(response);
		}

		this.sleep(response);
		
		if (response.getHeader("RSeq") != null) {
			outgoing.sendReliably();
		} else {
			outgoing.send();
		}

		if (response.getMethod().equals("BYE")) {
			appSession.invalidate();
		}
	}


	public void doCancel(SipServletRequest cancel) throws ServletException, IOException {
		SipApplicationSession appSession = cancel.getApplicationSession();
		
		String callState = (String)appSession.getAttribute(STATE);
		
		if (!(callState.equals(INITIAL) || callState.equals(EARLY))) {
			return;
		}

		appSession.setAttribute(STATE, CANCELLED);
		SipServletRequest request = (SipServletRequest)_msgMngr.getAssociatedMessage(cancel);
		request.createCancel().send();
	}


	private void sleep(SipServletMessage msg) throws ServletException {
		int sleepTime = _config.getSleepTimeFor(msg);

		if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				String errMsg = "Error occurred while pausing thread: " + e.getMessage();
				_logger.error(errMsg, e);
				throw new ServletException(errMsg);
			}
		}
	}

	
	private SipSession getPeerSession(SipSession session) {
		String peerSessionID = (String)session.getAttribute(PEER_SESSION_ID);
	
		Iterator sessions = session.getApplicationSession().getSessions();

		while (sessions.hasNext()) {
			SipSession peerSession = (SipSession)sessions.next();
			
			if (peerSession.getId().equals(peerSessionID)) {
				return peerSession;
			}
		}
		return null;
	}


	private void setContent(SipServletMessage msg1, SipServletMessage msg2) throws ServletException {
		try {
			Object obj = msg1.getContent();

			if (obj != null) {
				msg2.setContent(obj, msg1.getContentType());
			}
		} catch (Exception e) {
			String msg = "Error occurred while setting content on outgoing SIP message: " + e.getMessage();
			this.log(msg, e);
			throw new ServletException(msg);
		}
	}
	
}
