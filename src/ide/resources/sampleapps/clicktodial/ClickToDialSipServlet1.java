/*
 * ClickToDialSipServlet1.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipApplicationSession;


/**
 * This Servlet handles the provisional and final responses to the initial 
 * invite sent out by an HTTP Servlet to the caller.  When a response is 
 * received, the caller's SDP info is extracted from the response and sent to
 * the callee in a new initial invite.
 */
public class ClickToDialSipServlet1 extends SipServlet implements Constants {
        
    /**
     * This method will be called by the container when it receives the 
     * caller's provisional response to the initial invite sent out by 
     * an HTTP Servlet.  If the response contains the caller's SDP 
     * information, an initial invite will be sent to the callee which will
     * contain the caller's SDP.
     */
    public void doProvisionalResponse(SipServletResponse response) throws ServletException {
        this.log("doProvisionalResponse() called");

        try {
            // Look for the caller's SDP in the provisional response.
            if (response.getContent() == null) {
                this.log("No SDP found in provisional response.");
            } else {
                sendInvite(response);
            }
        } catch (Exception e) {
            String msg = "Error occurred while processing provisional response: " + e.toString();
            this.log(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * This method is called by the container when it recieves the caller's 
     * final response to the initial invite sent out by an HTTP Servlet.  If 
     * the response contains the caller's SDP information and an initial invite
     * to the callee was not already sent in the "doProvisionalResponse" 
     * method, one will be sent here.
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException {
        this.log("doSuccessResponse() called");

        try { 
            SipSession callerSession = response.getSession();                        
            
            // Consider the caller's session to be "established" at this point.
            callerSession.setAttribute(SESSION_STATE, SESSION_ESTABLISHED);
            
            // Send the INVITE to the callee if it hasn't already been sent.
            if (callerSession.getAttribute(INVITE_SENT) == null) {
                sendInvite(response);
            }

            this.log("Sending an ACK to the caller...");
                        
            response.createAck().send();
                
            this.log("Successfully sent ACK to caller.");
            
            byte[] calleeSDP = (byte[])callerSession.getAttribute(CALLEE_SDP);

            // Generate a re-invite request to forward the callee's SDP to the
            // caller if it's available in the caller's session.
            if (calleeSDP != null) {
                String contentType = (String)callerSession.getAttribute(CONTENT_TYPE);
                callerSession.setHandler("ClickToDialSipServlet3");
                SipServletRequest request = callerSession.createRequest("INVITE");
                request.setContent(calleeSDP, contentType);
                
                // See if a re-invite was already sent in the meantime.
                if (callerSession.getAttribute(RE_INVITE_SENT) == null) {
                    callerSession.setAttribute(RE_INVITE_SENT, Boolean.TRUE);                                
                    request.send();                  
                }
            }
                            
            // Keep track of the fact that an ACK was sent to the caller.
            callerSession.setAttribute(ACK_SENT, Boolean.TRUE);             
        } catch (Exception e) {
            String msg = "Error occurred while processing success response: " + e.toString();
            this.log(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * Sends an INVITE request to the callee to forward the caller's SDP.
     */
    private void sendInvite(SipServletResponse response) throws Exception {
        this.log("Sending an INVITE request to the callee...");

        SipFactory factory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
        SipApplicationSession appSession = response.getSession().getApplicationSession();
        URI callerURI = (URI)appSession.getAttribute(CALLER_URI);
        URI calleeURI = (URI)appSession.getAttribute(CALLEE_URI);

        // Create the INVITE to forward the caller's SDP to the callee.
        SipServletRequest request = factory.createRequest(appSession, "INVITE", callerURI, calleeURI);
        
        SipSession callerSession = response.getSession();
        SipSession calleeSession = request.getSession();
        
        // Correlate the two call sessions.
        calleeSession.setAttribute(PEER_SESSION, callerSession);
        callerSession.setAttribute(PEER_SESSION, calleeSession);
        
        // Prepare the callee's session.
        calleeSession.setAttribute(SESSION_NAME, CALLEE_SESSION);
        calleeSession.setHandler("ClickToDialSipServlet2");        
        
        // Prepare the request and send it.
        request.setContent(response.getRawContent(), response.getContentType());        
        request.send();

        // Keep track of the fact that an INVITE request was sent.
        response.getSession().setAttribute(INVITE_SENT, Boolean.TRUE);
        
        this.log("Successfully sent INVITE to callee.");
    }

}
