/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */


package com.baypackets.ase.testapps.sbb.mscontroller;

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

import java.io.*;
import java.util.Iterator;

import javax.servlet.sip.*;
import org.apache.log4j.Logger;
import com.baypackets.ase.sbb.*;
import com.baypackets.ase.util.Constants;



public class MsListener implements  SBBEventListener,Serializable
{                
	private static final long serialVersionUID = 387692818894347L;
	public int timer = 0;  
	public int oper = 3;   
	public Address partyA= null;   
	public Address partyB= null;   
	public String user ;
	public String domain;
	public int  dialout;
	private static String digit="";
  private  MsSessionController msCtrl;
  private B2bSessionController b2bSbb;
  private static SipSession _session;
  private String localAddr;
	private static Logger logger= Logger.getLogger(MsListener.class);

	public MsListener() 
	{
		super();
	}
	public void activate()
	{
		
	}

	public int handleEvent(SBB sbb,SBBEvent event) 
	{
	if (logger.isDebugEnabled()) 
	logger.debug("<APP> handle event called with sbb = "+sbb + 
					 "event "+event.getEventId());
    String sbbName = sbb.getName();
    String check = (sbb.getApplicationSession().getAttribute("check")).toString();
		if (logger.isDebugEnabled()) 
		logger.debug("check is: " + MsSessionCtrl.check);
		if(sbb instanceof MsSessionController)
    {
      msCtrl = (MsSessionController)sbb;  
    }
    try
		{
			if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED))
			{
					if(sbbName.equals("msController"))
          {
		  if (logger.isDebugEnabled()) 
          logger.debug("Connected to MediaServer");
					MsPlaySpec playSpec = new MsPlaySpec();
					
					//playSpec.addAnnouncementURI(new java.net.URI("file://mnt/192.168.3.119/1/dial_destn1.wav1"));
					  playSpec.addAnnouncementURI(new java.net.URI("file://mnt/192.168.8.172/dial_destn1.wav"));
					
					
					MsCollectSpec collectSpec = new MsCollectSpec();
					if (logger.isDebugEnabled()) 
					logger.debug("Setting clear digit buffer false");
					collectSpec.setClearDigitBuffer(false);
					/*logger.debug("digit timer values prior to setting");
					logger.debug("First digitTimer set is  " + collectSpec.getFirstDigitTimer());
					logger.debug("Inter digitTimer set is  " + collectSpec.getInterDigitTimer());
					logger.debug("Extra digitTimer set is  " + collectSpec.getExtraDigitTimer());
					logger.debug("Length digits is  " + collectSpec.getLengthDigits());
					logger.debug("Max digits is  " + collectSpec.getMaxDigits());
					logger.debug("Min digits is  " + collectSpec.getMinDigits());
					logger.debug("Termination key set is  " + collectSpec.getTerminationKey());
					logger.debug("Clear digit buffer is  " + collectSpec.isClearDigitBuffer());
					
					logger.debug("Media server is: " + msCtrl.getMediaServer());
					logger.debug("barge is :"  + playSpec.isBarge());
					logger.debug("Time out is: " + msCtrl.getTimeout());
					logger.debug("play spec interval is: " + playSpec.getInterval());
					logger.debug("play spec language is: " + playSpec.getLanguage()); 
					logger.debug("play spec iteration is: " + playSpec.getIterations());
					logger.debug("play spec Clear digit buffer is  " + playSpec.isClearDigitBuffer());
					*/
					if (logger.isDebugEnabled()) {
					logger.debug("");
					
					logger.debug("Parameters being set");
					}
					//Valid values
					//if(MsSessionCtrl.check.equals("601"))
					//{
						collectSpec.setFirstDigitTimer(10000);
						collectSpec.setInterDigitTimer(5000);
						collectSpec.setExtraDigitTimer(5000);
						
						collectSpec.applyPattern(4);
						collectSpec.applyPattern(1,5,"9");
						collectSpec.setClearDigitBuffer(true);
						playSpec.setBarge(true);
					//}
					// Invalid timers
					/*
					if(MsSessionCtrl.check.equals("602")) 
					{
						logger.debug("Setting first digit timer to 0");
						collectSpec.setFirstDigitTimer(0);
						logger.debug("Setting inter digit timer to 0");
						collectSpec.setInterDigitTimer(0);
						logger.debug("Setting extra digit timer to 0");
						collectSpec.setExtraDigitTimer(0);
					}
					//string for Id
					if(MsSessionCtrl.check.equals("603"))
					{
					//id removed
          
          }
					//For ApplyPattern api
					if(MsSessionCtrl.check.equals("604"))
					{
						logger.debug("Setting pattern as 0,0,9");
						collectSpec.applyPattern(4);			
					}
					//For 1st timer
					if(MsSessionCtrl.check.equals("605"))
					{
						logger.debug("Setting first timer as -1");
						collectSpec.setFirstDigitTimer(-1);
					}
					//For inter digit timer
					if(MsSessionCtrl.check.equals("606"))
					{
						logger.debug("Setting inter digit timer as -1");
						collectSpec.setInterDigitTimer(-1);
					}
					//For inter digit timer
					if(MsSessionCtrl.check.equals("607"))
					{
						logger.debug("Setting extra digit timer as -1");
						collectSpec.setExtraDigitTimer(-1);
					}
					//For applyPattern(length)
					if(MsSessionCtrl.check.equals("608"))
					{
						logger.debug("Setting pattern length as 0");
						collectSpec.applyPattern(0);					
					}
					
					//For applyPattern(length)
					if(MsSessionCtrl.check.equals("609"))
					{
						logger.debug("Setting pattern length as -1");
						collectSpec.applyPattern(-1);
					}
					
					//For timeout
					if(MsSessionCtrl.check.equals("610"))
					{
						logger.debug("Timeout set as 1");
						msCtrl.setTimeout(1);
						msCtrl.play(playSpec);
					
					}
					//For play spec language
					//if(MsSessionCtrl.check.equals("611"))
					{
						logger.debug("Set play spec language as English");
						playSpec.setLanguage("English");
					}
					//For play spec iterations
					if(MsSessionCtrl.check.equals("612"))
					{
						logger.debug("Set play spec iterations as 2");
						playSpec.setIterations(2);
					}
					//For play spec digit buffer
					if(MsSessionCtrl.check.equals("613"))
					{
						logger.debug("Set play spec digit buffer as false");
						playSpec.setClearDigitBuffer(false);
					}
					//For play 
					if(MsSessionCtrl.check.equals("614"))
					{
						logger.debug("play only ");
						msCtrl.play(playSpec);
					}
					//For iterations as -1
					if(MsSessionCtrl.check.equals("615"))
					{
						logger.debug("play spec iterations ");
						playSpec.setIterations(-1);
					}
					//For language as 123 
					if(MsSessionCtrl.check.equals("616"))
					{
						logger.debug("setting language as 123");
						playSpec.setLanguage("123");
					}
					//For setinterval as -5
					if(MsSessionCtrl.check.equals("617"))
					{
						logger.debug("setting interval as -5");
						playSpec.setInterval(-5);
					}
					//For setinterval as 5
					if(MsSessionCtrl.check.equals("618"))
					{
						logger.debug("setting interval as 5");
						playSpec.setInterval(5);
					}
					

					logger.debug("digit timer values after setting");
					logger.debug("First digitTimer set is  " + collectSpec.getFirstDigitTimer());
					logger.debug("Inter digitTimer set is  " + collectSpec.getInterDigitTimer());
					logger.debug("Extra digitTimer set is  " + collectSpec.getExtraDigitTimer());
					logger.debug("Length digits is  " + collectSpec.getLengthDigits());
					logger.debug("Max digits is  " + collectSpec.getMaxDigits());
					logger.debug("Min digits is  " + collectSpec.getMinDigits());
					logger.debug("Termination key set is  " + collectSpec.getTerminationKey());
					logger.debug("Clear digit buffer is  " + collectSpec.isClearDigitBuffer());
					
					logger.debug("barge is :"  + playSpec.isBarge());
					logger.debug("Time out is: " + msCtrl.getTimeout());
					logger.debug("Media server is: " + msCtrl.getMediaServer());
					logger.debug("play spec interval is: " + playSpec.getInterval());
					logger.debug("play spec language is: " + playSpec.getLanguage()); 
					logger.debug("play spec iteration is: " + playSpec.getIterations()); 
					logger.debug("play spec Clear digit buffer is  " + playSpec.isClearDigitBuffer());
					*/
					if(!((MsSessionCtrl.check.equals("610"))||(MsSessionCtrl.check.equals("614"))))
					{
						if (logger.isDebugEnabled()) 
						logger.debug("going to play and collect");
						((MsSessionController)sbb).playCollect(playSpec,collectSpec);
					}
					if (logger.isDebugEnabled()) 
					logger.debug("digit is " + digit);
					}
          if(sbb.getName().equals("B2B"))
          {
			if (logger.isDebugEnabled()) 
            logger.debug("Connected Event for B2b and getting remote Addr");
          }
			}
			if(event.getEventId().equals(SBBEvent.EVENT_DISCONNECTED))
			{
        if(sbb.getName().equals("msController"))
        {
			if (logger.isDebugEnabled()) 
          logger.debug("Disconnected from MediaServer");  
        }
        if(sbb.getName().equals("B2B"))
        {
			if (logger.isDebugEnabled()) 
          logger.debug("DisConnected Event for B2b");

	/*
          //SipServletRequest req = (SipServletRequest)sbb.getA().getAttribute("request");
          SipServletMessage req = event.getMessage();
          //if ((message instanceof SipServletRequest) )
          {
          //SipServletRequest req = (SipServletRequest)message;
          logger.debug("Writing CDR");
          CDR cdrObject = (CDR)sbb.getA().getAttribute("com.baypackets.ase.sbb.CDR");
          logger.debug("Using set method");
          cdrObject.set("FIELD_CALL_END_TIMESTAMP ", new Long(System.currentTimeMillis()));
          cdrObject.set("FIELD_CALL_DURATION_MSECS ", new Integer(123456));
          cdrObject.set(CDR.BILL_TO_NUMBER, new Integer(1));
          cdrObject.set(CDR.CALL_COMPLETION_STATUS_CODE, new Integer(2));
          cdrObject.set(CDR.CALL_START_TIMESTAMP, new Integer(3));
          cdrObject.set(CDR.CORRELATION_ID, new String("pass"));
          cdrObject.set(CDR.ORIGINATING_NUMBER, new String("caller"));
          cdrObject.set(CDR.SESSION_ID,_session);
          cdrObject.set(CDR.TERMINATING_NUMBER, new String("192.168.8.69"));
          cdrObject.set("ASACCESSNUMBER",new String("600"));
          cdrObject.set("UAS",new String("192.168.8.69"));
          logger.debug("AccessNumber is "+ cdrObject.get("TERMINATING_NUMBER"));
          logger.debug("Write count before writing is: " + cdrObject.getWriteCount());
          //Writing the CDR 
          try
          {
            cdrObject.write();
          }
          catch(CDRWriteFailedException e)
          {
            logger.debug("CDRWriteFailedException encountered " + e);
          }
          Iterator _list = cdrObject.getFields();
          logger.debug("Fields are ");
          while(_list.hasNext())
          {
            logger.debug(_list.next().toString());
          }
          }
	*/
        }
			}
 			if(event.getEventId().equals(SBBEvent.EVENT_SIG_IN_PROGRESS))
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Signalling in progress");
				return SBBEventListener.CONTINUE;	
			}
			if(event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED) && sbb instanceof MsSessionController)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Connect failed to MediaServer");
			}
			if(event.getEventId().equals(SBBEvent.EVENT_DISCONNECT_FAILED) && sbb instanceof MsSessionController)
			{
				if (logger.isDebugEnabled()) 
				logger.debug("disconnect failed to MediaServer");
			}
			if(event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_COMPLETED))
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Play collect Completed");
				MsOperationResult result = ((MsSessionController)sbb).getResult();
				digit = result.getAttribute(MsOperationResult.COLLECTED_DIGITS).toString()  ;
				boolean _success = result.isSuccessfull();
				if (logger.isDebugEnabled()) 
				logger.debug("Is Successful ? : " + _success);
				
				Iterator list = result.getAttributeNames();
				if (logger.isDebugEnabled()) 
				logger.debug("Attribtue names are ");
				while(list.hasNext())
				{
					String name = list.next().toString();
					if (logger.isDebugEnabled()) {
					logger.debug("Name " + name);
					logger.debug("Value is : " + result.getAttribute(name));
					}
				}
        //Verification for performing the dialout
				if(digit.equals("1234"))
				{
					if (logger.isDebugEnabled()) 
					logger.debug("digit is " + digit);
					SBBFactory sbbFactory = (SBBFactory) sbb.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
					if(sbbFactory == null) {
						logger.error("ERROR: SBBFactory is null.");
						return SBBEventListener.NOOP;
					}
					B2bSessionController b2b = (B2bSessionController)sbbFactory.getSBB("com.baypackets.ase.sbb.B2bSessionController", "B2B",sbb.getApplicationSession(),sbb.getServletContext());
					if (logger.isDebugEnabled()) 
					logger.debug("adding A");
					_session = ((MsSessionController)sbb).getA();
					
					//This gives error when used with invalidate
					 _session = ((MsSessionController)sbb).removeA();
					
					//msCtrl.invalidate(); 
					b2b.addA(_session);
					if (logger.isDebugEnabled()) 
					logger.debug("setting b2b eventlistener");
					b2b.setEventListener(new MsListener());
					if (logger.isDebugEnabled()) 
					logger.debug("dialing out to 192.168.13.24");
					try
					{
					if (logger.isDebugEnabled()) 
					logger.debug("disconnect media server");
					((MsSessionController)sbb).disconnectMediaServer();
          b2b.dialOut(null, MsSessionCtrl.sipFactory.createAddress("sip:123456@192.168.13.24:5064"));
					}
          catch(Exception e)
					{
						if (logger.isDebugEnabled()) 
						logger.debug("Exception is ",e);
					}
				}
			}
      if(event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_FAILED))
      {
		if (logger.isDebugEnabled()) 
        logger.debug("Play collect Failed");
        try
        {
          msCtrl.disconnectMediaServer();  
        }
        catch(Exception e)
        {
			if (logger.isDebugEnabled()) 
          logger.debug("Exception is " ,e );
        }
      }
			if(event.getEventId().equals(MsSessionController.EVENT_PLAY_COMPLETED))
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Play Completed");
			}
			if(event.getEventId().equals(MsSessionController.EVENT_PLAY_FAILED))
			{
				if (logger.isDebugEnabled()) {
				logger.debug("Play failed");
				logger.debug("Disconnecting media server");
				}
				msCtrl.disconnect();
			}
			if(event.getEventId().equals(MsSessionController.EVENT_PLAY_RECORD_COMPLETED))
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Play record completed");
			}
			if(event.getEventId().equals(MsSessionController.EVENT_PLAY_RECORD_FAILED))
			{
				if (logger.isDebugEnabled()) 
				logger.debug("Play Record failed");
			}
			
		}
		catch(Exception e)
		{
			if (logger.isDebugEnabled()) 
			logger.debug("Exception is " , e );
		}
	if (logger.isDebugEnabled()) 		
    logger.debug("returning NOOP");
		return SBBEventListener.NOOP;
	}
public void activate(SBB sbb)
{
  
}
		
}
