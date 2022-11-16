

/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */





package com.baypackets.ase.testapps.sbb.b2b;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.sip.*;
import com.baypackets.ase.sbb.*;
import com.baypackets.ase.util.Constants;

/**
 * Simple application illustrating how to write a back-to-back user agent (B2BUA).
 */
public class B2bServlet extends SipServlet {
	private SipFactory sipFactory;
  	private Address addr;
	private ServletContext sc;	

public void init () throws ServletException
        {
                try
                {
                sc = (ServletContext) getServletContext ();
                sipFactory = (SipFactory) sc.getAttribute (SIP_FACTORY);
                }
                catch (Exception e)
                {
                log ("Exception while initializing the Servlet: " + e);
                }
        }


  public void doInvite (SipServletRequest req) throws ServletException, IOException {

    	log ("b2bua doInvite: " + req.getRequestURI ());
	 String check = ((SipURI)req.getRequestURI()).getUser();
	String ip = ((SipURI)req.getRequestURI()).getHost();    
	log("check is: " + check);
    	SipSession _session = req.getSession();
    	String checkAttrib = "checkAttrib";
    	_session.setAttribute(checkAttrib,check);
	
    	//Handle only the initial requests.
    	if(!req.isInitial())
			return;
	B2bSessionController b2bController = null;
		
    	try {
			log("Handling the initial INVITE for session :"+req.getSession().getId());
			
			//This is an Initial INVITE message.
			SBBFactory sbbFactory = (SBBFactory) req.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
			b2bController = 
					(B2bSessionController)sbbFactory.getSBB(B2bSessionController.class.getName(), "test", req.getApplicationSession(), getServletContext());
			 b2bController.setEventListener(new MySBBEventListener());
		        _session.setAttribute("b2b",b2bController);

        		addr = sipFactory.createAddress("sip:935533@192.168.9.203:5060");
			/*
			if(ip.equals("192.168.13.31"))
        			addr = sipFactory.createAddress("sip:raj@192.168.13.24:7060");

			if(ip.equals("192.168.13.32"))
        			addr = sipFactory.createAddress("sip:raj@192.168.13.24:7061");
			*/
			//Enable RTP TUNNELING
			b2bController.setAttribute(SBB.RTP_TUNNELLING,new Integer(1));
			b2bController.connect(req, addr);
			//wait for some time and then invoke mute()
			
		} catch (Exception e) {
      		log ("Exception in doInvite: " + e);
    	}
	/*
	//wait for some time and then invoke mute()
	try	{
		Thread.sleep(10000);
	}catch(Exception exp)	{

	}
	b2bController.mute();
	//wait for some time and then invoke disconnect()
        try     {
                Thread.sleep(10000);
		b2bController.disconnect();
        }catch(Exception exp)   {
                                     
        }
	*/



  }
}
