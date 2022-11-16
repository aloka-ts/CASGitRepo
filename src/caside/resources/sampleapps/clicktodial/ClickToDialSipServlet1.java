package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.Constants;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;

/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
public class ClickToDialSipServlet1 extends SipServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	public void doProvisionalResponse(SipServletResponse response)
	throws ServletException
	{
		log("doProvisionalResponse() called");
		try
		{
			if (response.getContent() == null)
				log("No SDP found in provisional response.");
			else
				sendInvite(response);
		}
		catch (Exception e) {
			String msg = "Error occurred while processing provisional response: " + e.toString();
			log(msg, e);
			throw new ServletException(msg);
		}
	}

	public void doSuccessResponse(SipServletResponse response)
	throws ServletException
	{
		log("doSuccessResponse() called");
		try
		{
			SipSession callerSession = response.getSession();

			callerSession.setAttribute("SESSION_STATE", "SESSION_ESTABLISHED");

			if (callerSession.getAttribute("INVITE_SENT") == null) {
				sendInvite(response);
			}

			callerSession.setAttribute("OFFER_RESPONSE", response);

			byte[] calleeSDP = (byte[])callerSession.getAttribute("CALLEE_SDP");

			if (calleeSDP != null) {
				String contentType = (String)callerSession.getAttribute("CONTENT_TYPE");
				callerSession.setHandler("ClickToDialSipServlet3");
				SipServletRequest request = callerSession.createRequest("INVITE");
				request.setContent(calleeSDP, contentType);

				if (callerSession.getAttribute("RE_INVITE_SENT") == null) {
					callerSession.setAttribute("RE_INVITE_SENT", Boolean.TRUE);
					request.send();
				}
			}
		} catch (Exception e) {
			String msg = "Error occurred while processing success response: " + e.toString();
			log(msg, e);
			throw new ServletException(msg);
		}
	}

	private void sendInvite(SipServletResponse response)
	throws Exception
	{
		//check if an INVITE is already sent and its 1xx received
		if(response.getSession().getAttribute("PEER_SESSION") != null) {
			SipSession calleeSession = (SipSession) response.getSession().getAttribute("PEER_SESSION");
			if(calleeSession.getAttribute("1XX_RECEIVED") != null) {
				if(calleeSession.getAttribute("1XX_RECEIVED") == Boolean.TRUE) 
					return;
			}
		}

		log("Sending an INVITE request to the callee...");

		SipFactory factory = (SipFactory)getServletContext().getAttribute("javax.servlet.sip.SipFactory");
		SipApplicationSession appSession = response.getSession().getApplicationSession();
		URI callerURI = (URI)appSession.getAttribute("CALLER_URI");
		URI calleeURI = (URI)appSession.getAttribute("CALLEE_URI");

		SipServletRequest request = factory.createRequest(appSession, "INVITE", calleeURI, callerURI);

		SipSession callerSession = response.getSession();
		SipSession calleeSession = request.getSession();

		log("##" + appSession.toString());
		Iterator itr = appSession.getSessions();
		while (itr.hasNext()) {
			log("##" + ((SipSession)itr.next()).toString());
		}

		calleeSession.setAttribute("PEER_SESSION", callerSession);
		callerSession.setAttribute("PEER_SESSION", calleeSession);

		calleeSession.setAttribute("SESSION_NAME", "CALLEE_SESSION");
		calleeSession.setHandler("ClickToDialSipServlet2");

		request.setContent(response.getRawContent(), response.getContentType());
		request.send();

		response.getSession().setAttribute("INVITE_SENT", Boolean.TRUE);

		log("Successfully sent INVITE to callee.");
	}
}
