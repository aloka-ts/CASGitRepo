/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */


package com.baypackets.ase.testapps.sbb.mscontroller;


import java.util.Hashtable;
import javax.servlet.*;
import java.io.IOException;
import javax.servlet.sip.*;
import com.baypackets.ase.sbb.*;
import com.baypackets.ase.util.Constants;

import javax.sql.*;
import java.sql.*;
//import com.baypackets.ase.jndi_jdbc.ds.*;
//import com.baypackets.ase.jndi_jdbc.util.*;
import javax.naming.*;

/*
 * Created on Aug 17, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author ruchirs
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments 
 */
public class MsSessionCtrl extends SipServlet 
{

	public static SipFactory sipFactory; 
	private static String calleeIP ;
	private static int calleePort ;
	private static String callerIP ;
	private static int callerPort ;
	private static MsSessionController msController;
	private static SBBEventListener listener = null;
	public static String check;
	public static SipSession session;
	private ServletContext ctx;

	public void init () throws ServletException
	{
		
		try 
		{
			ctx = (ServletContext) getServletContext ();
			sipFactory = (SipFactory) ctx.getAttribute (SIP_FACTORY);
		}
		catch (Exception e)
		{
			log ("Exception while initializing the Servlet: " + e);
		}
	}
	public void doInvite (SipServletRequest req) throws ServletException, IOException 
	{
		log("doInvite() called");  
		SipServletRequest initialInv = req;
    check = getServletConfig().getInitParameter("check");
    SipApplicationSession appSession = req.getApplicationSession();
    appSession.setAttribute("check",check);
  	log("check is : "  + check);
    session = req.getSession();
    session.setAttribute("request",req);
		String legAId = req.getSession().getId();
		log("Party-A id = " + legAId);
		int capabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION |
			MediaServer.CAPABILITY_VAR_ANNOUNCEMENT |
			MediaServer.CAPABILITY_AUDIO_RECORDING;
		
		MediaServerSelector msSelector = (MediaServerSelector) getServletContext().getAttribute(MediaServerSelector.class.getName());
		log("msSelector obj is ");
		log(msSelector.toString());
		MediaServer _mserver = msSelector.selectByCapabilities(capabilities);
		log("checking mserver");
		if(_mserver == null)
		{
			//This would select any available Media Server.
			_mserver = msSelector.selectByCapabilities(0);
		}
		SBBFactory sbbFactory = (SBBFactory) req.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
		if(sbbFactory == null) {
			log("ERROR: SBBFactory is null.");
			return;
		}
		msController =  
			(MsSessionController)sbbFactory.getSBB("com.baypackets.ase.sbb.MsSessionController","msController", req.getApplicationSession(), ctx);
		SBBEventListener mslistener = new MsListener();
		msController.setEventListener(mslistener);
		try
		{
      msController.setAttribute(SBB.RTP_TUNNELLING,"true");
			msController.connect(req,_mserver);
		}
		catch(Exception e)
		{
			log("Exception is ", e);
		}
		/*
		try
		{	
			Hashtable env=new Hashtable();
      log("performing jndi/jdbc");
			
      env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
      env.put(Context.PROVIDER_URL,"file:/home/sit/SAS5.3B03/ASESubsystem/jndiprovider/fileserver/");
      
      PreparedStatement pstmt; 
      Context ctx=new InitialContext(env);
			DataSourceImpl datasourceimpl=new DataSourceImpl("");
      
      DataSourceImpl datasource=null;
      log("Here i am going to make lookup");
      datasource=(DataSourceImpl)ctx.lookup("SASDB");
      log("Successfully loooked up");
			log("creating connection 1");
			Connection conn1=datasource.getConnection();
      Statement stmt1 = conn1.createStatement();
      stmt1.executeUpdate("create table test " + "(Name varchar(20),Age int)");
      stmt1.executeUpdate("insert into test values ('ruchir', 24)");
      log("creating connection 2 and dropping table");
			Connection conn2=datasource.getConnection();
      log("Busy Connections===> "+datasource.getBusyConnections());
      log("Free Connections===> "+datasource.getFreeConnections());
      Statement stmt2 = conn2.createStatement();
      stmt1.executeUpdate("drop table test");
      log("closing connections");
      conn1.close();
      conn2.close();
    }
			catch(Exception eeq)
			{
				//log(eeq.toString(),eeq);
				log("Error Occurred in Lookup",eeq);
			}
	*/
			
    	log("doInvite exit");	
	}

	protected void doAck(SipServletRequest ack) throws ServletException, IOException 
	{
		log("doAck()  "+ ack);
	}


	public void doBye(SipServletRequest a_Request) throws ServletException, IOException
	{
		log("BYE: " + a_Request);
		System.out.println("BYE: " + a_Request);
		log("sending 200 OK response for BYE and invalidating the session");
		a_Request.createResponse(SipServletResponse.SC_OK).send();
		a_Request.getSession().invalidate();
	  }

	  public void doProvisionalResponse(SipServletResponse a_Response) throws ServletException, IOException{
		log("Provisional response: " + a_Response);
		System.out.println("Provisional response: " + a_Response);
	  }

	  public void doSuccessResponse(SipServletResponse a_Response) throws ServletException, IOException{
		log("Success response: " + a_Response);
		System.out.println("Success response: " + a_Response);
	  }
}
