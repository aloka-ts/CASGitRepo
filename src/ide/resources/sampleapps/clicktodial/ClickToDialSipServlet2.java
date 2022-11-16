/*
 * ClickToDialSipServlet2.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.http.HttpSession;


/**
 * This Servlet handles the provisional and final responses to the initial
 * invite sent to the callee by ClickToDialSipServlet1.  When a response is
 * recieved from the callee, his SDP is extracted from the response
 * and sent to the caller in a re-invite request.
 */
public class ClickToDialSipServlet2 extends SipServlet implements Constants {    
    
    /**
     * This method will be called by the container when it recieves the 
     * callee's provisional response to the initial invite sent out by
     * ClickToDialSipServlet1.  If the response contains the callee's SDP 
     * information, a re-invite will be sent to the caller that will
     * contain the callee's SDP.
     */
    public void doProvisionalResponse(SipServletResponse response) throws ServletException {
        this.log("doProvisionalResponse() called");

        try {
            // Look for an SDP in the provisional response.
            if (response.getContent() == null) {
                this.log("No SDP info found in provisional response.");
            } else {
                sendReInvite(response);
            }
        } catch (Exception e) {
            String msg = "Error occurred while processing provisional response from callee: " + e.toString();
            this.log(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * This method will be called by the container when it recieves the 
     * callee's final response to the initial invite sent out by
     * ClickToDialSipServlet1.  If the response contains the callee's SDP 
     * information and a re-invite was not already sent in the 
     * "doProvisionalResponse" method, a re-invite will be sent here.
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException {
        this.log("doSuccessResponse() called");

        try {
            SipSession calleeSession = response.getSession();
            
            // Consider the callee's session to be "established" at this point.
            calleeSession.setAttribute(SESSION_STATE, SESSION_ESTABLISHED);           
            
            sendReInvite(response);

            this.log("Sending an ACK to the callee...");

            response.createAck().send();

            this.log("Successfully sent ACK.");
        } catch (Exception e) {
            String msg = "Error occurred while processing success response from callee: " + e.toString();
            this.log(msg, e);
            throw new ServletException(e.toString());
        }
    }


    /**
     * Called by the container to handle a BYE request from the callee.
     */
    public void doBye(SipServletRequest request) throws ServletException {
        this.log("doBye() called");

        try {
            SipApplicationSession appSession = request.getApplicationSession();
            
            // Can't do anything if the app session has been invalidated.
            if (appSession.getAttribute(CALL_STATE) == null) {
                return;
            }            
            
            // Consider the call to be terminated at this point.            
            appSession.setAttribute(CALL_STATE, CALL_TERMINATED);                        
            appSession.setAttribute(CALL_END_TIME, new Date());
            
            // Acknowledge the BYE request.
            request.createResponse(200).send();

            // Forward the BYE to the caller.
            SipSession callerSession = (SipSession)request.getSession().getAttribute(PEER_SESSION);            
            callerSession.createRequest("BYE").send();                                               
            
            //appSession.invalidate();
        } catch (Exception e) {
            String msg = "Error occurred while processing BYE request from callee: " + e.toString();
            this.log(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * Sends a re-invite request to the caller to forward the callee's SDP.
     */
    private void sendReInvite(SipServletResponse response) throws Exception {
        SipSession calleeSession = response.getSession();
        SipSession callerSession = (SipSession)calleeSession.getAttribute(PEER_SESSION);
        
        // Only send the re-invite if we have sent an ACK to the caller.
        if (callerSession.getAttribute(ACK_SENT) == null) {
            callerSession.setAttribute(CALLEE_SDP, response.getRawContent());
            callerSession.setAttribute(CONTENT_TYPE, response.getContentType());
            return;
        }
        
        this.log("Sending a RE-INVITE request to the caller with the callee's SDP...");

        // Generate a re-invite to forward the callee's SDP to the caller.
        callerSession.setHandler("ClickToDialSipServlet3");
        SipServletRequest request = callerSession.createRequest("INVITE");
        request.setContent(response.getRawContent(), response.getContentType());
        request.send();

        // Keep track of the fact that the re-invite was sent.
        response.getSession().setAttribute(RE_INVITE_SENT, Boolean.TRUE);

        this.log("Successfully sent RE-INVITE to the caller.");
    }          

}
