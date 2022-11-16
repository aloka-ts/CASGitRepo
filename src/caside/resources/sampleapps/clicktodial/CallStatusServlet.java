package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.Call;
import com.baypackets.clicktodial.util.CallDAO;
import com.baypackets.clicktodial.util.Constants;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
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
public class CallStatusServlet extends HttpServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException
	{
		try
		{
			response.setContentType("text/html");

			PrintWriter out = new PrintWriter(response.getOutputStream());
			out.println("<html>");
			out.println("<h3><u>Call Status</u></h3>");

			Map sessionMap = (Map)getServletContext().getAttribute("com.baypackets.ase.AppSessionMap");

			if (sessionMap == null)
				log("Application session map is not available in ServletContext.");

			String appSessionID = request.getParameter("appSessionID");

			if (appSessionID == null)
				log("\"appSessionID\" parameter in request is null.");
			else {
				log("\"appSessionID\" parameter in request is: " + appSessionID);
			}

			SipApplicationSession appSession = sessionMap != null ? (SipApplicationSession)sessionMap.get(appSessionID) : null;

			if (appSession == null)
				log("No application session found for ID: " + appSessionID);

			String callState = appSession != null ? (String)appSession.getAttribute("CALL_STATE") : "CALL_TERMINATED"; 
			log("Call state is: " + callState);

			if ("CALL_TERMINATED".equals(callState)) {
				out.println("Call State: CALL TERMINATED <br/>");
				out.println("Duration: " + getCallDuration(appSession, appSessionID) + "<br/>");
			} else if ("CALL_IN_PROGRESS".equals(callState)) {
				out.println("Call State: CALL IN PROGRESS <br/>");
				out.println("Duration: " + getCallDuration(appSession, appSessionID) + "<br/>");
			} else if ("CALL_BEING_SETUP".equals(callState)) {
				out.println("Call State: CALL BEING SETUP <br/>");
			}else if ("CALL_ESTABLISHED".equals(callState)) {
				out.println("Call State: CALL ESTABLISHED <br/>");
			}

				boolean calleeSessionEstablished = false;
				boolean callerSessionEstablished = false;

				Iterator sessions = appSession.getSessions("SIP");

				if ((sessions == null) || (!sessions.hasNext()))
					log("No SIP sessions contained in application session.");
				else {
					do 
					{
						SipSession session = (SipSession)sessions.next();

						if (("CALLER_SESSION".equals(session.getAttribute("SESSION_NAME"))) && 
								("SESSION_ESTABLISHED".equals(session.getAttribute("SESSION_STATE")))) {
							callerSessionEstablished = true; } 
						else {
							if ((!"CALLEE_SESSION".equals(session.getAttribute("SESSION_NAME"))) || 
									(!"SESSION_ESTABLISHED".equals(session.getAttribute("SESSION_STATE")))) continue;
							calleeSessionEstablished = true;
						}
					} while (sessions.hasNext());
				}

				if (callerSessionEstablished)
					out.println("Caller Leg: ESTABLISHED <br/>");
				else {
					out.println("Caller Leg: NOT CONNECTED <br/>");
				}

				if (calleeSessionEstablished)
					out.println("Callee Leg: ESTABLISHED <br/>");
				else {
					out.println("Callee Leg: NOT CONNECTED <br/>");
				}

			out.println("<br/><a href=\"CallStatusServlet?appSessionID=");
			out.println(appSessionID);
			out.println("\">Refresh</a>");
			out.println("<br/><hr/>");
			out.println("</html>");
			out.close();
		} catch (Exception e) {
			log(e.toString(), e);
			throw new ServletException(e.toString());
		}
	}

	private String getCallDuration(SipApplicationSession appSession, String appSessionID)
	throws Exception
	{
		if (appSession == null) {
			return getCallDuration(appSessionID);
		}

		Date callStartTime = (Date)appSession.getAttribute("CALL_START_TIME");

		if (callStartTime == null) {
			return getCallDuration(appSessionID);
		}

		Date callEndTime = (Date)appSession.getAttribute("CALL_END_TIME");

		if (callEndTime == null) {
			callEndTime = new Date();
		}

		return calculateCallDuration(callStartTime, callEndTime);
	}

	private String getCallDuration(String appSessionID)
	throws Exception
	{
		CallDAO dao = (CallDAO)getServletContext().getAttribute("CALL_DAO");
		Call call = dao.findByID(appSessionID);
		return calculateCallDuration(call.getCallStartTime(), call.getCallEndTime());
	}

	private String calculateCallDuration(Date callStartTime, Date callEndTime)
	throws Exception
	{
		long millis = callEndTime.getTime() - callStartTime.getTime();
		long seconds = millis / 1000L;

		if (seconds == 0L) {
			return millis + " milliseconds";
		}

		long minutes = seconds / 60L;

		if (minutes == 0L) {
			return seconds + " seconds";
		}

		seconds %= 60L;

		return minutes + " minutes " + seconds + " seconds";
	}
}

