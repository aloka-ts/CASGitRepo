/*
 * ClickToDialHttpServlet.java
 *
 * Created on June 30, 2004, 12:20 PM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.ServletParseException;
import java.io.PrintWriter;


/**
 * This Servlet processes an HTTP POST request to initiate a SIP INVITE that 
 * will establish a call between two specified parties.
 */
public class ClickToDialHttpServlet extends HttpServlet implements Constants {
        
    private static String DUMMY_SDP =
        "v=0\r\n" +
        "o=CiscoSystemsSIP-GW-UserAgent 6343 6424 IN IP4 192.168.6.20\r\n" +
        "s=SIP Call\r\n" +
        "c=IN IP4 0.0.0.0\r\n" +
        "t=0 0\r\n" +
        "m=audio 16444 RTP/AVP 18 0 8 4 2 3 100\r\n" +
        "a=rtpmap:18 G729/8000\r\n" +
        "a=fmtp:18 annexb=no\r\n" +
        "a=rtpmap:0 PCMU/8000\r\n" +
        "a=rtpmap:8 PCMA/8000\r\n" +
        "a=rtpmap:4 G723/8000\r\n" +
        "a=fmtp:4 annexa=no\r\n" +
        "a=rtpmap:2 G726-32/8000\r\n" +
        "a=rtpmap:3 GSM/8000\r\n" +
        "a=rtpmap:100 X-NSE/8000\r\n" +
        "a=fmtp:100 192-194\r\n";           
    
    
    /**
     * Invoked by the container to handle an HTTP GET request.  This method
     * simply forwards the request to the "doPost" method.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        this.doPost(request, response);
    }


    /**
     * Invoked by the container to handle an HTTP POST request.  The request
     * is expected to contain the phone numbers of two specified parties 
     * (a caller and a callee) for which a call will be established.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        this.log("doPost() called");
        
        PrintWriter out = null;
        
        try {
            response.setContentType("text/html");

            out = new PrintWriter(response.getOutputStream());

            if (request.getParameter("Dial") == null) {
                displayForm(out);
            } else {                
                this.log("Handling HTTP POST request...");

                // Get the request parameter values...
                String toNumber = request.getParameter("toNumber");
                String toGateway = request.getParameter("toGateway");
                String fromNumber = request.getParameter("fromNumber");
                String fromGateway = request.getParameter("fromGateway");

                // Get the SipFactory to create URIs for the caller and callee.
                SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);

                // Construct a SIP URI for the callee.
                URI calleeURI = factory.createURI("sip:" + toNumber + "@" + toGateway);

                // Construct a SIP URI for the caller.
                URI callerURI = factory.createURI("sip:" + fromNumber + "@" + fromGateway);

                // Prepare a new application session.
                SipApplicationSession appSession = factory.createApplicationSession();
                appSession.setAttribute(CALL_STATE, CALL_BEING_SETUP);                
                appSession.setAttribute(CALLER_URI, callerURI);
                appSession.setAttribute(CALLEE_URI, calleeURI);
                
                this.log("Creating a SIP INVITE request...");

                // Generate an INVITE request to get the caller's SDP info.
                SipServletRequest sipRequest = factory.createRequest(appSession, "INVITE", callerURI, callerURI);
                sipRequest.setContent(DUMMY_SDP.getBytes(), "application/sdp");
                
                // Prepare the caller's session.
                SipSession callerSession = sipRequest.getSession();
                callerSession.setAttribute(SESSION_NAME, CALLER_SESSION);
                callerSession.setHandler("ClickToDialSipServlet1");

                this.log("Successfully created INVITE request.  Sending request to caller...");

                sipRequest.send();

                this.log("Sent INVITE to caller.");
                
                displaySuccessPage(out, appSession.getId());
            }
        } catch (Exception e) {
            this.log(e.toString(), e);
            displayErrorPage(out, e);          
            throw new ServletException(e.toString());
        } 
    }
    
    
    /**
     * Displays a success message to the client after posting the request.
     */
    private void displaySuccessPage(PrintWriter out, String appSessionID) throws Exception {
        this.log("Displaying success page to the client...");
        out.println("<html>");
        out.println("<b>Your INVITE request has been sent...</b><br/><br/>");
        out.println("<a href=\"CallStatusServlet?appSessionID=" + appSessionID);
        out.println("\">Click here</a>, to view the status of the call");
        out.println("<br/><hr/>");
        out.println("</html>");
        out.close();
    }
    
    
    /**
     * Displays a fill-out form to the client.
     */
    private void displayForm(PrintWriter out) throws Exception {  
        this.log("Displaying fill-out form to the client...");
        out.println("<html>");        
        out.println("<h3><u>Click to Dial</u></h3>");
        out.println("<form action=\"ClickToDialHttpServlet\" method=\"POST\">");
        out.println("<table width=\"50%\" border=\"0\">");
        out.println("<tr><td colspan=\"2\"><b>To</b> (callee)</td></tr>");
        out.println("<tr><td><b>Number:</b><input size=\"15\" name=\"toNumber\"></td></tr>");
        out.println("<tr><td><b>SIP Phone IP:</b><input size=\"15\" name=\"toGateway\"></td></tr>");
        out.println("<tr><td><br/></td></tr>");
        out.println("<tr><td colspan=\"2\"><b>From</b> (caller)</td></tr>");
        out.println("<tr><td><b>Number:</b><input size=\"15\" name=\"fromNumber\"></td></tr>");
        out.println("<tr><td><b>SIP Phone IP:</b><input size=\"15\" name=\"fromGateway\"></td></tr>");
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

    
    /**
     * Displays an error page to the client.
     */
    private void displayErrorPage(PrintWriter out, Exception e) {
        this.log("Displaying error page...");
        
        try {
            if (out != null) {
                out.println("<html>");
                out.println("The following error occurred: " + e.toString());
                out.println("</html>");
                out.close();
            }
        } catch (Exception e2) {
            this.log(e2.toString(), e2);
        }
    }
    
}

