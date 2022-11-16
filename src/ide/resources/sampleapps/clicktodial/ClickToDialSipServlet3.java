/*
 * ClickToDialSipServlet3.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import java.util.Date;
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
import javax.servlet.http.HttpSession;


/**
 * This Servlet handles the final response to the re-invite sent from the 
 * callee to the caller.  It also handles any subsequent BYE request sent 
 * from the caller.
 */
public class ClickToDialSipServlet3 extends SipServlet implements Constants {
    
    /**
     * Invoked by the container to handle the final response to the re-invite
     * request sent from the callee to the caller.
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException {
        this.log("doSuccessResponse() called");

        try {
            // Consider the call to be "in progress" at this point.
            SipApplicationSession appSession = response.getApplicationSession();
            appSession.setAttribute(CALL_STATE, CALL_IN_PROGRESS);              
            appSession.setAttribute(CALL_START_TIME, new Date());
            
            /*
            ServletContext context = getServletContext();
            context.setAttribute(CALL_STATE, CALL_IN_PROGRESS);
            context.setAttribute(CALL_START_TIME, new Date());
            */
            
            this.log("Sending an ACK...");

            response.createAck().send();

            this.log("Successfully sent ACK.");
        } catch (Exception e) {
            String msg = "Error occurred while processing success response from the caller: " + e.toString();
            this.log(msg, e);
            throw new ServletException(msg);
        }
    }


    /**
     * Invoked by the container to handle a BYE request from the caller.
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

            /*
            ServletContext context = getServletContext();
            context.setAttribute(CALL_STATE, CALL_TERMINATED);
            context.setAttribute(CALL_END_TIME, new Date());
            */  
              
            // Acknowledge the BYE request.
            request.createResponse(200).send();            
            
            // Forward the BYE to the callee.
            SipSession calleeSession = (SipSession)request.getSession().getAttribute(PEER_SESSION);            
            calleeSession.createRequest("BYE").send();                                                
                        
            //appSession.invalidate();            
        } catch (Exception e) {
            String msg = "Error occurred while processing BYE request from the caller: " + e.toString();
            this.log(msg, e);
            throw new ServletException(e.toString());
        }
    }    
    
}
