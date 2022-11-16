/*
 * Created on Dec 22, 2004
 *
 */
package com.baypackets.testapps;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.sip.*;

public class UasServlet extends SipServlet implements TimerListener{                

	private boolean support100Rel = false;
	private int callDuration = 3000;
	private int finalResponse = 200;
	private String provisionalContent;
	private String finalContent;
	
	
   	/**
	 *
	*/
	public void init(ServletConfig config) throws ServletException {        
		super.init(config);        
		log("init() called");  
		String str100Rel = config.getInitParameter("100rel");
		support100Rel = str100Rel != null && str100Rel.equalsIgnoreCase("true");
	
		String strcallDuration = config.getInitParameter("callDuration");
		try{
			callDuration = Integer.parseInt(strcallDuration != null ? strcallDuration : "");
		}catch(NumberFormatException e){
			log(e.getMessage(), e);
		}

		String strRespCode = config.getInitParameter("finalResponse");
		try{
			finalResponse = Integer.parseInt(strRespCode != null ? strRespCode : "");
		}catch(NumberFormatException e){
			log(e.getMessage(), e);
		}
		
		provisionalContent =  config.getInitParameter("provisionalContent");
		finalContent = config.getInitParameter("finalContent");
	}

    
	/**
	 *
	 */
	protected void doOptions(SipServletRequest req) throws IOException, ServletException {
		log("doOptions() called");
	}
    
    
	/**
	 *
	 */
	protected void doInvite(SipServletRequest req) throws IOException, ServletException {
		log("doInvite() called");  
        
		if (req.isInitial()) {
			log("doInvite(): request is initial");
		} else {
			log("doInvite(): request is not initial");
		}
        
		try {
			if(support100Rel){
				SipServletResponse response = req.createResponse(180);
				if(provisionalContent != null && !provisionalContent.trim().equals("")){
					response.setContent(provisionalContent, "text/sdp");
				}
				response.sendReliably();
			}
            
			TimerService timerService = (TimerService)getServletContext().getAttribute(TimerService.class.getName());
			SipApplicationSession appSession = req.getApplicationSession();
			timerService.createTimer(appSession, callDuration, false, (Serializable)req);
			
			log("doSuccessResponse(): registered a timer listener...");
	
			log("doInvite(): successfully sent 200 OK response");
		} catch (Exception e) {
			log (e.toString(), e);
			throw new ServletException(e.toString());
		}
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

	protected void doPrack(SipServletRequest req) throws IOException, ServletException {
		log("doPrack() called");
		try {
			SipServletResponse response = req.createResponse(200);
			response.send();
            
			log("doPrack(): successfully sent 200 OK response");
		} catch (Exception e) {
			log (e.toString(), e);
			throw new ServletException(e.toString());
		}
	}

	/**
	 *
	 */
	protected void doBye(SipServletRequest req) throws IOException, ServletException {
		log("doBye() called");
		try {
			SipServletResponse response = req.createResponse(200);
			response.send();
            
			log("doBye(): successfully sent 200 OK response");
		} catch (Exception e) {
			log (e.toString(), e);
			throw new ServletException(e.toString());
		}
	}


	/**
	 *
	 */
	protected void doProvisionalResponse(SipServletResponse resp) {
		log("doProvisionalResponse() called");
	}

    
	/**
	 *
	 */
	protected void doErrorResponse(SipServletResponse resp) {
		log("doErrorResponse() called");
	}

    
	/**
	 *
	 */
	protected void doSuccessResponse(SipServletResponse resp) {
		log("doSuccessResponse() called");
	}

	/**
	 *
	 */
	public void timeout(javax.servlet.sip.ServletTimer servletTimer) {
		log("timeout() called on the UAS Servlet...");
		try{
			if(servletTimer.getInfo() != null){
				SipServletRequest req = (SipServletRequest) servletTimer.getInfo();
				SipServletResponse resp = req.createResponse(finalResponse);
				if(finalContent != null && !finalContent.trim().equals("")){
					resp.setContent(finalContent, "text/sdp");
				}
				resp.send();
			}else{
				log("INFO object is NULL in the timer...");
			}
		}catch(Exception e){
			log(e.getMessage(), e);
		}
	}

}
