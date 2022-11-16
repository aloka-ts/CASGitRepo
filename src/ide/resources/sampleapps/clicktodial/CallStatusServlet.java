/*
 * CallStatusServlet.java
 *
 * Created on September 10, 2004, 8:12 PM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;


/**
 * This Servlet displays the status of a phone call that was established for
 * two parties specified in an HTTP POST request.
 */
public class CallStatusServlet extends HttpServlet implements Constants {
        
    /**
     * Invoked by the container to handle an HTTP GET request.  This method
     * simply forwards the request to the "doPost" method.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        this.doPost(request, response);
    }

    
    /**
     * Invoked by the container to handle an HTTP POST request.  The request is
     * expected to contain the ID of a SIP application session that contains 
     * the status information of a phone call that was established for two 
     * parties specified from a web browser.  The ID is used to lookup the app 
     * session object in the session Map provided by the ServletContext.
     * The status of that call, including it's current state and duration are 
     * output to the client.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {        
        try { 
            response.setContentType("text/html");
            
            PrintWriter out = new PrintWriter(response.getOutputStream());            
            out.println("<html>");        
            out.println("<h3><u>Call Status</u></h3>");            
                                    
            Map sessionMap = (Map)this.getServletContext().getAttribute(APP_SESSION_MAP); 
                        
            if (sessionMap == null) {
                this.log("Application session map is not available in ServletContext.");
            } else {
                this.log("Got application session map from ServletContext.");
            }
                        
            String appSessionID = request.getParameter("appSessionID");
            
            if (appSessionID == null) {
                this.log("\"appSessionID\" parameter in request is null.");
            } else {
                this.log("\"appSessionID\" parameter in request is: " + appSessionID);
            }
            
            SipApplicationSession appSession = sessionMap != null ? (SipApplicationSession)sessionMap.get(appSessionID) : null;
            
            if (appSession == null) {
                this.log("No application session found for ID: " + appSessionID);
            } else {
                this.log("Got application session for ID: " + appSessionID);
            }
            
            String callState = appSession != null ? (String)appSession.getAttribute(CALL_STATE) : CALL_TERMINATED;                        
            
            this.log("Call state is: " + callState);                
                        
            if (CALL_TERMINATED.equals(callState)) {
                out.println("Call State: CALL TERMINATED <br/>");
                out.println("Duration: " + getCallDuration(appSession, appSessionID) + "<br/>");
            } else if (CALL_IN_PROGRESS.equals(callState)) {
                out.println("Call State: CALL IN PROGRESS <br/>");
                out.println("Duration: " + getCallDuration(appSession, appSessionID) + "<br/>");
            } else if (CALL_BEING_SETUP.equals(callState)) {
                out.println("Call State: CALL BEING SETUP <br/>");
                                                                
                boolean calleeSessionEstablished = false;
                boolean callerSessionEstablished = false;

                Iterator sessions = appSession.getSessions("SIP");
                
                if (sessions == null || !sessions.hasNext()) {
                    this.log("No SIP sessions contained in application session.");
                } else {
                    while (sessions.hasNext()) {
                        SipSession session = (SipSession)sessions.next();
                
                        if (CALLER_SESSION.equals(session.getAttribute(SESSION_NAME)) &&
                            SESSION_ESTABLISHED.equals(session.getAttribute(SESSION_STATE))) {
                            callerSessionEstablished = true;                        
                        } else if (CALLEE_SESSION.equals(session.getAttribute(SESSION_NAME)) &&
                            SESSION_ESTABLISHED.equals(session.getAttribute(SESSION_STATE))) {
                            calleeSessionEstablished = true;
                        }
                    }
                }
                
                if (callerSessionEstablished) {
                    out.println("Caller Leg: ESTABLISHED <br/>");
                } else {
                    out.println("Caller Leg: NOT YET ESTABLISHED <br/>");
                }
                
                if (calleeSessionEstablished) {
                    out.println("Callee Leg: ESTABLISHED <br/>");
                } else {
                    out.println("Callee Leg: NOT YET ESTABLISHED <br/>");
                }                
            }
            
            out.println("<br/><a href=\"CallStatusServlet?appSessionID=");
            out.println(appSessionID);
            out.println("\">Refresh View</a>");
            out.println("<br/><hr/>");
            out.println("</html>");
            out.close();
        } catch (Exception e) {
            this.log(e.toString(), e);
            throw new ServletException(e.toString());
        }
    }
    
    
    /**
     * Uses the attributes stored in the given app session object to calculate 
     * the duration of the call.  If the given app session is null (which 
     * would be the case if the app session had expired) then a lookup to the 
     * data store using the given app session ID will be made to obtain the 
     * call duration.
     */
    private String getCallDuration(SipApplicationSession appSession, String appSessionID) throws Exception {
        if (appSession == null) {
            return getCallDuration(appSessionID);
        }
        
        Date callStartTime = (Date)appSession.getAttribute(CALL_START_TIME);
        
        if (callStartTime == null) {
            return getCallDuration(appSessionID);
        }
        
        Date callEndTime = (Date)appSession.getAttribute(CALL_END_TIME);
        
        if (callEndTime == null) {
            callEndTime = new Date();
        }
        
        return calculateCallDuration(callStartTime, callEndTime);      
    }        
        
    
    /**
     *
     */
    private String getCallDuration(String appSessionID) throws Exception {        
        CallDAO dao = (CallDAO)this.getServletContext().getAttribute(CALL_DAO);                
        Call call = dao.findByID(appSessionID);   
        return calculateCallDuration(call.getCallStartTime(), call.getCallEndTime());
    }
    
    
    /**
     *
     */
    private String calculateCallDuration(Date callStartTime, Date callEndTime) throws Exception {
        long millis = callEndTime.getTime() - callStartTime.getTime();
        
        long seconds = millis / 1000;
        
        if (seconds == 0) {
            return millis + " milliseconds";
        }
        
        long minutes = seconds / 60;
        
        if (minutes == 0) {
            return seconds + " seconds";
        }
        
        seconds = seconds % 60;
        
        return minutes + " minutes " + seconds + " seconds";                
    }
    
}
