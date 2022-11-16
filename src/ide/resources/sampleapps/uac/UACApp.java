/*
 * Created on 26 Jan., 2006
 */
package com.baypackets.sampleapps.uac;

import javax.servlet.*;
import javax.servlet.sip.*;
import java.io.IOException;

/**
 * This class is a BayPackets Servlet. You are free to customize and use
 * this class as you see fit.
 *
 *
 * This class can be used as a base class to develop a client user agent.
 *
 */

public class UACApp extends SipServlet 
{
	

	/* 	SIP factory (SipFactory) is a factory class to create SIP Servlet-specific objects necessary for application execution */
	SipFactory sipFactory = null;

	/*	This is From header value. User can overwrite it */
	private String fromAddress = "sip:From@192.168.1.87:7061";

	/*	This is To header value. User can overwrite it */
	private String toAddress = "sip:To@192.168.1.87:5060";

	public void init() throws ServletException
	{
		sipFactory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
		
		/*The SipApplicationSession  interface acts as a store for application data and provides access to contained protocol sessions	*/
		SipApplicationSession appSession = sipFactory.createApplicationSession();
		
		//User can create From adddress URI as he requires
		URI fromAddressURI = sipFactory.createURI(fromAddress);
		
		//User can create to adddress URI as he requires
		URI toAddressURI = sipFactory.createURI(toAddress);
		
		
		//User can send any request inplace of INVITE
		SipServletRequest request = sipFactory.createRequest(appSession,"INVITE",fromAddressURI,toAddressURI);
		
		try
		{
			request.send();
		}
		catch(IOException e)
		{
			log("Exception occurred while sending initial Invite Request", e);
		}
		
		
	}

	public void doMessage(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming MESSAGE Request.
		 User should write the aspired logic */
	
	}

	public void doNotify(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming NOTIFY Request.
		 User should write the aspired logic */
	
	}

	public void doOptions(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming OPTIONS Request.
		 User should write the aspired logic */
	
	}

	public void doCancel(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming CANCEL Request.
		 User should write the aspired logic */
	
	}

	public void doInfo(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming INFO Request.
		 User should write the aspired logic */
	
	}

	public void doBye(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming BYE Request.
		 User should write the aspired logic */
		
		SipServletResponse response200 = request.createResponse(200);
		
		response200.send();
	
	}

	public void doInvite(SipServletRequest request) throws ServletException, IOException
	{
	
	/*Invoked by the Server to handle incoming INVITE Request.
	 User should write the aspired logic */
	}

	public void doAck(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming ACK Request.
		 User should write the aspired logic */
		
		
	
	}

	public void doPrack(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming PRACK Request.
		 User should write the aspired logic */
	
	}

	public void doRegister(SipServletRequest request) throws ServletException, IOException
	{
	
	/*Invoked by the Server to handle incoming REGISTER Request.
	 User should write the aspired logic */
	
	}

	public void doRequest(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked to handle incoming Requests.
		 This method dispatches requests to doXXX request methods.
		 User should write the aspired logic */
	
	}

	public void doSubscribe(SipServletRequest request) throws ServletException, IOException
	{
	
		/*Invoked by the Server to handle incoming SUBSCRIBE Request.
		 User should write the aspired logic */
	
	}

	public void doErrorResponse(SipServletResponse response) throws ServletException, IOException
	{
	
		/*Invoked by the server to handle incoming 4XX-6XX responses.
		 User should write the aspired logic */ 
	
	}

	public void doProvisionalResponse(SipServletResponse response) throws ServletException, IOException
	{
	
		/*Invoked by the server to handle incoming 1XX responses.
		 User should write the aspired logic.*/
	
	}

	public void doRedirectResponse(SipServletResponse response) throws ServletException, IOException
	{
	
		/*Invoked by the server to notify the servlet of incoming 3XX class responses.
		 User should write the aspired logic.*/
	
	}

	public void doResponse(SipServletResponse response) throws ServletException, IOException
	{
	
		/*Invoked to handle incoming responses.
		 This method dispatches responses to one of the doXXXResponse method.
		 User should write the aspired logic.*/
		
		
		SipServletRequest requestACK = response.createAck();
		
		requestACK.send();
	
	}
}

