/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/*
 * Created on 26 Jan., 2006
 */
package com.baypackets.sampleapps.uas;

import java.io.IOException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import javax.servlet.sip.Proxy;

/**
 * This class is a BayPackets Servlet. You are free to customize and use
 * this class as you see fit.
 *
 *
 * This class can be used as a base class to develop a server user agent. 
 *
 */

public class UASApp extends SipServlet 
{

    //====== REQUEST METHODS ======

    /**
     * Method for processing a SIP INVITE request.
     * @param request A SIP request object.
     * @throws IOException In case a low-level communication or file operation fails.
     * @throws ServletException Denotes a general error. 
     */
	public void doInvite(SipServletRequest a_Request) throws ServletException, IOException
	{
        	// TODO: Add your own code to process payload...

        	// create and send a response.....
        	SipServletResponse response = a_Request.createResponse(200); 
        	// TODO: Add your own code to add a payload to the response, e.g. sdp 
		
		response.send();
	}

    /**
     * Method processing for a SIP ACK request.
     * @param a_Request A SIP request object.
     * @throws IOException In case a low-level communication or file operation fails.
     * @throws ServletException Denotes a general error. 
     */
	public void doAck(SipServletRequest a_Request) throws ServletException, IOException
	{
        	// Session is now established so we swallow the ack!!

		log("ACK is recieved");
        	// TODO: Add your own code to process late sdp        
	}

    /**
     * Method processing for a SIP BYE request. It sends back 200-OK and then sends BYE
     * to all other sessions for this call.
     * @param a_Request A SIP request object.
     * @throws IOException In case a low-level communication or file operation fails.
     * @throws ServletException Denotes a general error. 
     */
	public void doBye(SipServletRequest a_Request) throws ServletException, IOException
	{

        	// TODO: Add your own code to acknowledge termination of the dialog        
        	SipServletResponse response = a_Request.createResponse(200); 
        	// update the session state
		log("200 Respose is sent to the Originating Party");
		
		response.send();
	}

}
