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
public class ClickToDialSipServlet2 extends SipServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	public void doProvisionalResponse(SipServletResponse response)
	throws ServletException
	{
		log("CALLEE doProvisionalResponse() called");
		try
		{
			if (response.getContent() == null)
				log("CALLEE No SDP info found in provisional response.");
			else
				sendAckOrSuccessResponse(response);

			response.getSession().setAttribute("1XX_RECEIVED",Boolean.TRUE);
		}
		catch (Exception e) {
			String msg = "Error occurred while processing provisional response from callee: " + e.toString();
			log(msg, e);
			throw new ServletException(msg);
		}
	}

	public void doSuccessResponse(SipServletResponse response)
	throws ServletException
	{

		log("CALLEE doSuccessResponse() called");

		if(response.getMethod().equals("BYE")){
			log("CALLEE doSuccessResponse() called for bye");
			return;
		}
		try
		{
			SipSession calleeSession = response.getSession();

			calleeSession.setAttribute("SESSION_STATE", "SESSION_ESTABLISHED");

			sendAckOrSuccessResponse(response);

			log("CALLEE Sending an ACK to the callee...");

			response.createAck().send();

			log("CALLEE Successfully sent ACK.");

			Date callStartTime = new Date();
			response.getApplicationSession().setAttribute("CALL_STATE", "CALL_IN_PROGRESS");
			response.getApplicationSession().setAttribute("CALL_START_TIME", callStartTime);

			response.getSession().setAttribute("1XX_RECEIVED",Boolean.FALSE);
		} catch (Exception e) {
			String msg = "Error occurred while processing success response from callee: " + e.toString();
			log(msg, e);
			throw new ServletException(e.toString());
		}
	}

	public void doBye(SipServletRequest request)
	throws ServletException
	{
		log("CALLEE doBye() called");
		try
		{
			SipApplicationSession appSession = request.getApplicationSession();

			if (appSession.getAttribute("CALL_STATE") == null) {
				return;
			}

			appSession.setAttribute("CALL_STATE", "CALL_TERMINATED");
			appSession.setAttribute("CALL_END_TIME", new Date());

			SipSession callerSession = (SipSession)request.getSession().getAttribute("PEER_SESSION");
			request.createResponse(200).send();
			callerSession.createRequest("BYE").send();
		}
		catch (Exception e)
		{
			String msg = "Error occurred while processing BYE request from callee: " + e.toString();
			log(msg, e);
			throw new ServletException(msg);
		}
	}

	private void sendAckOrSuccessResponse(SipServletResponse response)
	throws Exception
	{
		SipSession calleeSession = response.getSession();
		SipSession callerSession = (SipSession)calleeSession.getAttribute("PEER_SESSION");

		if (callerSession.getAttribute("ACK_SENT") == null) {
			callerSession.setAttribute("CALLEE_SDP", response.getRawContent());
			callerSession.setAttribute("CONTENT_TYPE", response.getContentType());

			SipServletResponse offerResponse = (SipServletResponse) callerSession.getAttribute("OFFER_RESPONSE");
			SipServletRequest answerAck = offerResponse.createAck();
			String contentType = response.getContentType();
			callerSession.setHandler("ClickToDialSipServlet3");
			answerAck.setContent(response.getRawContent(), contentType);
			answerAck.send();
			callerSession.setAttribute("ACK_SENT", Boolean.TRUE);

			return;
		}

		log("CALLEE Sending 200 OK to the caller with the callee's SDP...");

		callerSession.setHandler("ClickToDialSipServlet3");
		SipServletRequest callerReInvite = (SipServletRequest) callerSession.getAttribute("RE_INVITE");
		SipServletResponse callerResponse = callerReInvite.createResponse(SipServletResponse.SC_OK);
		callerResponse.setContent(response.getRawContent(), response.getContentType());
		callerResponse.send();

		response.getSession().setAttribute("RE_INVITE_RESPONSE_SENT", Boolean.TRUE);

		log("CALLEE Successfully sent 200 OK for RE-INVITE from caller.");
	}
}

