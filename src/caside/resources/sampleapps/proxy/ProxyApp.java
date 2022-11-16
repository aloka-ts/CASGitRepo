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

package com.baypackets.sampleapps.proxy;

import javax.servlet.*;
import javax.servlet.sip.*;
import java.io.IOException;


/**
 * This class is a BayPackets Servlet. You are free to customize and use
 * this class as you see fit.
 *
 *
 * This class can be used as a base class to develop a full fledged Proxy Application
 *
 */


public class ProxyApp extends SipServlet
{

	/* SIP factory (SipFactory) is a factory class to create SIP Servlet-specific objects necessary for application execution */
	private SipFactory factory = null;
	
	/* The SipApplicationSession  interface acts as a store for application data and provides access to contained protocol sessions  */
	private SipApplicationSession appSession = null;

	/* Represents point-to-point SIP relationships.*/
	private SipSession sipSession = null;

	private SipServletRequest initialInvite = null;

	/*************************** Methods start *******************************/

	// Initialize
	public void init() throws ServletException
	{
		log("init: enter");
		super.init();
		factory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
		log("init: exit");
	} // init()

	// Requests
	protected void doAck(SipServletRequest req) throws ServletException, IOException
	{
		log("doAck: called");
	}

	protected void doBye(SipServletRequest req) throws ServletException, IOException
	{
		log("doBye: called");

			
	}

	protected void doCancel(SipServletRequest req) throws ServletException, IOException
	{
		log("doCancel: called");
	}

	protected void doInfo(SipServletRequest req) throws ServletException, IOException
	{
		log("doInfo: called");
	}

	protected void doInvite(SipServletRequest req) throws ServletException, IOException
	{
		log("doInvite: called");

		// Proxy invite
		Proxy proxy = null;

		log("getProxy() calling");
		proxy = req.getProxy();
		/*proxy.setRecordRoute(true);*/ //User can set the RecordRoute option
		log("calling proxyTo()");

		/*proxy.proxyTo(factory.createURI("sip:proxytarget@192.168.13.27:7061"));*/ // User can use any
		/*proxy.proxyTo(factory.createURI(req.getTo().toString()));*/

		proxy.proxyTo(req.getRequestURI());

		log("doInvite: exit");
	}

	protected void doMessage(SipServletRequest req) throws ServletException, IOException
	{
		log("doMessage: called");
	}

	protected void doNotify(SipServletRequest req) throws ServletException, IOException
	{
		log("doNotify: called");
	}

	protected void doOptions(SipServletRequest req) throws ServletException, IOException
	{
		log("doOptions: called");
	}

	protected void doPrack(SipServletRequest req) throws ServletException, IOException
	{
		log("doPrack: called");
	}

	protected void doRegister(SipServletRequest req) throws ServletException, IOException
	{
		log("doRegister: called");
	}

	protected void doSubscribe(SipServletRequest req) throws ServletException, IOException
	{
		log("doSubscribe: called");
	}

	// Responses
	protected void doErrorResponse(SipServletResponse resp) throws ServletException, IOException
	{
		log("doErrorResponse: called");
		log("Error response method [" + resp.getMethod() + "] code [" + resp.getStatus() + "]");
	}

	protected void doProvisionalResponse(SipServletResponse resp) throws ServletException, IOException
	{
		log("doProvisionalResponse: enter");

		log("doProvisionalResponse: exit");
	}

	protected void doRedirectResponse(SipServletResponse resp) throws ServletException, IOException
	{
		log("doRedirectResponse: called");
	}

	protected void doSuccessResponse(SipServletResponse resp) throws ServletException, IOException
	{
		log("doSuccessResponse: called");
	}
}

