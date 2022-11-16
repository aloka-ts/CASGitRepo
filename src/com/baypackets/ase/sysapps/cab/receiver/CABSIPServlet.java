package com.baypackets.ase.sysapps.cab.receiver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.cab.manager.CABManager;

public class CABSIPServlet extends SipServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 18888L;
	private static Logger logger = Logger.getLogger(CABSIPServlet.class);
	CABManager cabManager;
	 private static SipFactory factory;
	 
	 public static SipFactory getSipFactory() {
         return factory;
 }
	public void init() throws ServletException {
			logger.debug("[CAB] init method called on CABSIPServlet.");
			cabManager=CABManager.getInstance();		
			 ServletContext ctx=this.getServletContext();
             factory = (SipFactory)ctx.getAttribute(SIP_FACTORY);
	}
	
}