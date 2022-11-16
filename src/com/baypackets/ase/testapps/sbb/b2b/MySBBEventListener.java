
/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */



/*
 * Created on Dec 22, 2004
 *
 */
package com.baypackets.ase.testapps.sbb.b2b;

import java.io.*;

import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.log4j.Logger;
import com.baypackets.ase.sbb.*;

public class MySBBEventListener implements  SBBEventListener,Serializable 
{                
	private static final long serialVersionUID = 248114143918894347L;
	public int timer = 0;  
	public int oper = 3;   
	public Address partyA= null;   
	public Address partyB= null;   
	public String user ;
	public String domain;
	public int  dialout;


	private static Logger logger= Logger.getLogger(MySBBEventListener.class);

	public MySBBEventListener() 
	{
		super();
	}
	public void activate ()
	{
		
	}
	public int handleEvent(SBB sbb,SBBEvent event) 
	{
		if (logger.isDebugEnabled()) 
		logger.debug("<APP> handle event called with sbb = "+sbb +
					 "event "+event.getEventId());
		String check = null;
		TimerService timerService = null;
		try 
		{
			ServletContext ctx= sbb.getServletContext();
			timerService = (TimerService)ctx.getAttribute(TimerService.class.getName());
		}
		catch(Exception exp) 
		{
			exp.printStackTrace();	
		}
		B2bSessionController b2bCtrl = (B2bSessionController)sbb;
		if (logger.isDebugEnabled()) 
		logger.debug("<APP> Received <"+ event.getEventId()+">");
		String eventId = event.getEventId();
		if (eventId.equalsIgnoreCase(SBBEvent.EVENT_SIG_IN_PROGRESS)) 
		{
			//logger.debug("calling connect in doBye");
			//return SBBEventListener.NOOP;
			if (logger.isDebugEnabled()) 
			logger.debug("<APP> Signalling in progress");
			return SBBEventListener.CONTINUE;
		}
		
		// CONNECTED event
		else  if (eventId.equalsIgnoreCase(SBBEvent.EVENT_CONNECTED))
		{
			if (logger.isDebugEnabled()) {
			logger.debug("<APP> Received connected event from SBB");
        
			logger.debug("disconnect B");
			}
			try
			{
				b2bCtrl.disconnectB();	
			}
			catch(DisconnectException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Exception", exp);
			}
			catch(IllegalStateException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Exception", exp);
			}
		}
        
        	//Muted Event
		if (eventId.equalsIgnoreCase(SBBEvent.EVENT_HOLD_COMPLETE)){
			if (logger.isDebugEnabled()) {
			logger.debug("<APP> Signalling in progress");
			logger.debug("HOLD_COMPLETED event reached");
			
	        logger.debug("<APP> Signalling in progress");
			logger.debug("muted event reached");
			}
            try{
            	b2bCtrl.hold();
            }catch(IllegalStateException ex){
				if (logger.isDebugEnabled()) 
                logger.debug("Exception", ex);
            }
		}
//event umnuted

		//Event Disconnected
		if (eventId.equalsIgnoreCase(SBBEvent.EVENT_DISCONNECTED))
		{
			if (logger.isDebugEnabled()) 
			logger.debug("Disconnected Event");
			try
			{
				if (logger.isDebugEnabled()) 
				logger.debug("disconnecting again");
				b2bCtrl.disconnect();
			}
			catch(Exception e)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Exception", e);
			}
			try
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Disconnect B again");
				b2bCtrl.disconnectB();
			}
			catch(DisconnectException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("in disconnect" , exp);
			}
			catch(IllegalStateException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("in illegalState" , exp);
			}
			try
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Disconnect A again");
				b2bCtrl.disconnectA();
			}
			catch(DisconnectException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("in disconnect" , exp);
			}
			catch(IllegalStateException exp)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("in illegalState" , exp);
				
			}
		}
	
		return SBBEventListener.NOOP;
	}

	public void activate(SBB sbb){
    
	}
		
}
