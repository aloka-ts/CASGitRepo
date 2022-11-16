/*
 * Created on Dec 22, 2004
 *
 */
package com.baypackets.testapps;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.sip.*;

/**
 *
 */
public class UacServlet extends SipServlet implements TimerListener {    
     
	private static String METHOD = "METHOD";
	private static String OPTIONS = "OPTIONS";
	private static String INVITE = "INVITE";
	
	private String toURI;
	private String fromURI;
	private	String method; 
	private String content;
	private int callDuration;            
	
	private int startDelay;
	private int burstInterval;
	private int burstSize;
	private int totalCalls;
	
	private int successCalls;
            
	/**
	 *
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        
		log("init() called");
                
		try {            
            
			toURI = config.getInitParameter("toURI");
			fromURI = config.getInitParameter("fromURI");
			method = config.getInitParameter("method"); 
			content = config.getInitParameter("content");
			callDuration = Integer.parseInt(config.getInitParameter("callDuration"));
			startDelay = Integer.parseInt(config.getInitParameter("startDelay"));
			burstInterval = Integer.parseInt(config.getInitParameter("burstInterval"));
			burstSize = Integer.parseInt(config.getInitParameter("burstSize"));
			totalCalls = Integer.parseInt(config.getInitParameter("totalCalls"));
            
			ServletContext context = getServletContext();
			TimerService timerService = (TimerService)getServletContext().getAttribute(TimerService.class.getName());
			SipFactory factory = (SipFactory)getServletContext().getAttribute(SipFactory.class.getName());
			SipApplicationSession appSession = factory.createApplicationSession();
			timerService.createTimer(appSession, startDelay, burstInterval, true, false, "INVITE");
			
			log("doSuccessResponse(): registered a timer listener...");
		} catch (Exception e) {
			log(e.toString(), e);
			throw new ServletException(e.toString());
		}
	}
   
	/**
	 *
	 */
	protected void doOptions(SipServletRequest req) throws ServletException, IOException {
		log ("doOptions() called");
	}
    
	/**
	 *
	 */
	protected void doAck(SipServletRequest req) throws ServletException, IOException {
		log("doAck() called");
	}
            
    
	/**
	 *
	 */
	public void destroy() {
		log("destroy() called");
	}


	/**
	 *
	 */
	protected void doInvite(SipServletRequest req) throws IOException, ServletException {
		log("doInvite() called");                    
	}


	/**
	 *
	 */
	protected void doBye(SipServletRequest req) throws IOException {
		log("doBye() called");
	}


	/**
	 *
	 */
	protected void doProvisionalResponse(SipServletResponse resp) {
		log("doProvisionalResponse() called");
		log("allowHeader: " + resp.getHeader("Allow"));        
	}

    
	/**
	 *
	 */
	protected void doErrorResponse(SipServletResponse resp) throws IOException {
		log("doErrorResponse() called");  
		log("doErrorResponse(): received response from " + resp.getRemoteAddr());

		String method = (String)resp.getRequest().getMethod();
        
		if (OPTIONS.equals(method)) {
			log("doErrorResponse(): handling response to OPTIONS request");
			log("doErrorResponse(): SipServletResponse.getHeader('Allow'): " + resp.getHeader("Allow"));
            
			SipFactory factory = (SipFactory)getServletContext().getAttribute(SipFactory.class.getName());
			SipApplicationSession session = resp.getApplicationSession();
			session.setAttribute(METHOD, INVITE);
			SipServletRequest request = factory.createRequest(session, "INVITE", resp.getTo(), resp.getFrom());
			request.send();
            
			log("doErrorResponse(): Sent INVITE request to: " + resp.getFrom());
		} else if (INVITE.equals(method)) {
			log("doErrorResponse(): handling response to INVITE request");
			resp.createAck().send();
			log("doErrorResponse(): successfully sent ACK");
		}
	}

    
	/**
	 *
	 */
	protected void doSuccessResponse(SipServletResponse resp) throws IOException {
		log("doSuccessResponse() called");  
		log("doSuccessResponse(): received response from " + resp.getRemoteAddr());

		String method = (String)resp.getRequest().getMethod();
        
		if (OPTIONS.equals(method)) {
			log("doSuccessResponse(): handling response to OPTIONS request");
			log("doSuccessResponse(): SipServletResponse.getHeader('Allow'): " + resp.getHeader("Allow"));
		}else if (INVITE.equals(method)) {
			log("doSuccessResponse(): handling response to INVITE request");
			resp.createAck().send();
			log("doSuccessResponse(): successfully sent ACK");

			ServletContext context = getServletContext();
			
			TimerService timerService = (TimerService)getServletContext().getAttribute(TimerService.class.getName());
			SipApplicationSession appSession = resp.getApplicationSession();
			
			timerService.createTimer(appSession, callDuration, false, "BYE");
			
			log("doSuccessResponse(): registered a timer listener...");
		} else if ("BYE".equals(method)) {
			resp.getApplicationSession().invalidate();
			log("doSuccessResponse(): Invalidated the Application Session...");
			successCalls++;
			log("NUMBER OF CALLS completed successfully :::"+successCalls);
		}
	}
    
    
	/**
	 *
	 */
	public void timeout(javax.servlet.sip.ServletTimer servletTimer) {
		log("timeout() called on the UAC Servlet...");
		if(servletTimer.getInfo() != null){
			if("INVITE".equals(servletTimer.getInfo())){
				this.createInviteRequest(servletTimer);
			}else{
				this.createNonInviteRequest(servletTimer);
			}
		}
	}

		
	public void createNonInviteRequest(ServletTimer servletTimer){
		try{
			SipApplicationSession appSession = servletTimer.getApplicationSession();
			String method = ""+servletTimer.getInfo();
			Iterator iterator = appSession.getSessions();
			if(iterator.hasNext()){
				log("Creating request... :"+ method);
				SipSession sipSession = (SipSession) iterator.next();
				SipServletRequest req = sipSession.createRequest(method);
				
				log("Sending the request...:"+method);
				req.send();
			}
		}catch(Exception e){
			log(e.getMessage(), e);
		}
	}
	public void createInviteRequest(ServletTimer servletTimer){
		if(totalCalls > 0){
			//This will keep the initial timer appsession for next 10 minutes.
			servletTimer.getApplicationSession().setExpires(10);
		}else{
			log(" Already completed the total calls. So returning without doing anything");
			servletTimer.getApplicationSession().invalidate();
			return;
		}
		for(int i=0;i<burstSize;i++){
			totalCalls--;
			try{
				ServletContext context = getServletContext();
				SipFactory factory = (SipFactory)context.getAttribute(SipFactory.class.getName());
				SipApplicationSession appSession = factory.createApplicationSession();
				SipServletRequest request = factory.createRequest(appSession, method, fromURI, toURI);
		                    
				log("init(): preparing to send " + method + " request to " + toURI);                        
		        
				if (content == null) {
					request.setContent("This is a test string...", "text/plain");
				} else {
					Object obj = Class.forName(content).newInstance();
					request.setContent(obj, "text/*");
				}                        
				request.send();
		        
				log("init(): successfully sent " + method + " request");                        
			}catch(Exception e){
				log(e.getMessage(), e);
			}
		}
	}
}
