package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.Constants;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
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
public class ClickToDialHttpServlet extends HttpServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	ServletContext ctx = null;

	public void init()
	{
		this.ctx = getServletContext();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException
	{
		log("doPost() called");

		PrintWriter out = null;
		try
		{
			response.setContentType("text/html");

			out = new PrintWriter(response.getOutputStream());

			if (request.getParameter("Dial") == null) {
				displayForm(out);
			} else {
				log("Handling HTTP POST request...");

				String toNumber = request.getParameter("toNumber");
				String toGateway = request.getParameter("toGateway");
				String fromNumber = request.getParameter("fromNumber");
				String fromGateway = request.getParameter("fromGateway");

				SipFactory factory = (SipFactory)getServletContext().getAttribute("javax.servlet.sip.SipFactory");
				URI calleeURI = factory.createURI("sip:" + toNumber + "@" + toGateway);
				URI callerURI = factory.createURI("sip:" + fromNumber + "@" + fromGateway); 
				SipApplicationSession appSession = factory.createApplicationSession();

				appSession.setAttribute("CALL_STATE", "CALL_BEING_SETUP");
				appSession.setAttribute("CALLER_URI", callerURI);
				appSession.setAttribute("CALLEE_URI", calleeURI);

				log("Creating a SIP INVITE request...");
				SipServletRequest sipRequest = factory.createRequest(appSession, "INVITE", callerURI, calleeURI);

				SipSession callerSession = sipRequest.getSession();
				callerSession.setAttribute("SESSION_NAME", "CALLER_SESSION");
				callerSession.setHandler("ClickToDialSipServlet1");

				log("Successfully created INVITE request.  Sending request to caller...");
				sipRequest.send();

				log("Sent INVITE to caller.");

				displaySuccessPage(out, appSession.getId());
			}
		} catch (Exception e) {
			log(e.toString(), e);
			displayErrorPage(out, e);
			throw new ServletException(e.toString());
		}
	}

	private void displaySuccessPage(PrintWriter out, String appSessionID)
	throws Exception
	{
		log("Displaying success page to the client...");
		out.println("<html>");
		out.println("<b>Dialout initiated</b><br/><br/>");
		out.println("<a href=\"CallStatusServlet?appSessionID=" + appSessionID);
		out.println("\">Click here to view the status of the call</a>");
		out.println("<br/><hr/>");
		out.println("</html>");
		out.close();
	}

	private void displayForm(PrintWriter out)
	throws Exception
	{
		log("Displaying fill-out form to the client...");
		out.println("<html>");
		out.println("<h3>Click to Dial Demo Application</h3>");
		out.println("<form action=\"ClickToDialHttpServlet\" method=\"POST\">");
		out.println("<table width=\"50%\" border=\"0\">");
		out.println("<tr><td colspan=\"2\"><b>Party A</b> (caller)</td></tr>");
		out.println("<tr><td><b>Number .....  </b><input size=\"15\" name=\"fromNumber\"></td></tr>");
		out.println("<tr><td><b>IP & Port .. </b><input size=\"15\" name=\"fromGateway\"></td></tr>");
		out.println("<tr><td><br/></td></tr>");
		out.println("<tr><td colspan=\"2\"><b>Party B</b> (callee)</td></tr>");
		out.println("<tr><td><b>Number .....  </b><input size=\"15\" name=\"toNumber\"></td></tr>");
		out.println("<tr><td><b>IP & Port .. </b><input size=\"15\" name=\"toGateway\"></td></tr>");
		out.println("<tr><td><br/></td></tr>");
		out.println("<tr><td>");
		out.println("<input type=\"submit\" name=\"Dial\" value=\"Dial\"/>&nbsp;&nbsp;");
		out.println("<input type=\"reset\"/>");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");
		out.println("<hr/>");
		out.println("</html>");
		out.close();
	}

	private void displayErrorPage(PrintWriter out, Exception e)
	{
		log("Displaying error page...");
		try
		{
			if (out != null) {
				out.println("<html>");
				out.println("Oops! The following error occurred: " + e.toString());
				out.println("</html>");
				out.close();
			}
		} catch (Exception e2) {
			log(e2.toString(), e2);
		}
	}
}
