/*
 * TestServlet.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.ase.container.tests;

import javax.servlet.sip.SipServlet;
import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;


/**
 * Sample servlet loaded by the AseWrapperTest class.
 *
 * @see com.baypackets.ase.container.tests.AseWrapperTest
 *
 * @author Zoltan Medveczky
 */
public class TestServlet extends SipServlet {
    
    private static Logger _logger = Logger.getLogger(TestServlet.class);
    
    /**
     * Called when this Servlet is first loaded.
     *
     */
    public void init(ServletConfig config) {
        _logger.debug("init() called...");
        
        _logger.debug("servlet name: " + config.getServletName());
    }
    
}
