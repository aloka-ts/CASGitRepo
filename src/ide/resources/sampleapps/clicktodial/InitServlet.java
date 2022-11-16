/*
 * InitServlet.java
 *
 * Created on June 30, 2004, 12:20 PM
 */
package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.sip.SipServlet;


/**
 * This Servlet is loaded upon application startup to perform all
 * initialization for the "Click To Dial" application.
 */
public class InitServlet extends SipServlet implements Constants {
            
    /**
     * Invoked by the container when this Servlet is first loaded.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);  
        
        this.log("init() called...");        
        
        // Instantiate the data access object used to find and persist call
        // information to and from the backing store and register it with the 
        // ServletContext.
        ServletContext context = config.getServletContext();        
        CallDAO dao = new FileBasedCallDAO(context.getRealPath("/db"));        
        context.setAttribute(CALL_DAO, dao);
    }
            
}

