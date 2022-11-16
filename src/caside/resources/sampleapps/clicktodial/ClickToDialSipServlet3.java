package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.Constants;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

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
public class ClickToDialSipServlet3 extends SipServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	public void doSuccessResponse(SipServletResponse response)
	throws ServletException
	{
		log("CALLER doSuccessResponse() called");

		if(response.getMethod().equals("BYE")){
			log("CALLER doSuccessResponse() called for bye");
			return;
		}
		else{	// INVITE's 200 Ok recevived
			try
			{
				SipApplicationSession appSession = response.getApplicationSession();
				appSession.setAttribute("CALL_STATE", CALL_ESTABLISHED);
				appSession.setAttribute("CALL_START_TIME", new Date());

				log("CALLER Sending an ACK...");

				response.createAck().send();

				log("CALLER Successfully sent ACK.");
			} catch (Exception e) {
				String msg = "Error occurred while processing success response from the caller: " + e.toString();
				log(msg, e);
				throw new ServletException(msg);
			}
		}
	}

	public void doBye(SipServletRequest request)
	throws ServletException
	{
		log("CALLER doBye() called");
		try
		{
			SipApplicationSession appSession = request.getApplicationSession();
			SipSession calleeSession = (SipSession)request.getSession().getAttribute("PEER_SESSION");
			if (appSession.getAttribute("CALL_STATE") == null) {
				return;
			}
			appSession.setAttribute("CALL_STATE", "CALL_TERMINATED");
			appSession.setAttribute("CALL_END_TIME", new Date());

			request.createResponse(200).send();			
			calleeSession.createRequest("BYE").send();
		}
		catch (Exception e)
		{
			String msg = "Error occurred while processing BYE request from the caller: " + e.toString();
			log(msg, e);
			throw new ServletException(e.toString());
		}
	}

	public void doInvite(SipServletRequest request)
	throws ServletException
	{
		log("CALLER doInvite() called");
		try
		{
			request.getSession().setAttribute("RE_INVITE", request);


			SipApplicationSession appSession = request.getApplicationSession();
			SipSession calleeSession = (SipSession)request.getSession().getAttribute("PEER_SESSION");
			if (appSession.getAttribute("CALL_STATE") == null) {
				return;
			}

			SipServletRequest calleeRequest = calleeSession.createRequest("INVITE");
			calleeRequest.setContent(request.getRawContent(), request.getContentType());
			calleeRequest.send();
		}
		catch (Exception e)
		{
			String msg = "Error occurred while processing INVITE request from the caller: " + e.toString();
			log(msg, e);
			throw new ServletException(e.toString());
		}
	}

	public void doAck(SipServletRequest request)
	throws ServletException
	{
		log("CALLER doAck() called");

		//do nothing
	}
}

