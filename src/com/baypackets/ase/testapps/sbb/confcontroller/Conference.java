
/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */



package com.baypackets.ase.testapps.sbb.confcontroller;

import javax.servlet.ServletException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.URI;

import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.ConferenceInfo;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.ConferenceRegistry;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.MsConferenceSpec;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.util.Constants;

/**
 * Simple application illustrating how to write
 * a Conferencing Application.
 */
 public class Conference extends SipServlet
        implements SipApplicationSessionListener {
        
        
  public static final String CONF_ID = "12345";
  public static final String CONF_CONTROLLER = "CONTROLLER";
  public static final int START_TIMER = 10*1000;
  public static int PARTICIPANT_INDEX = 0;

/*public void timeout(ServletTimer timer) {
    try{
      log("Inside the TIMEOUT method .....");
      if(!"INIT".equals(""+timer.getInfo())){
        log("Not initalization timer. So ignoring it.....");
      }

      log("Inside the INIT method .....");
      //Select a Media Server
      MediaServerSelector msSelector= (MediaServerSelector)
          getServletContext().getAttribute(MediaServerSelector.class.getName());
      MediaServer ms = msSelector.selectByCapabilities(0);

      if (ms == null) {
        log("<APP> No active media server present");
        return;
      }

      //Create a Conference Controller SBB.
      SBBFactory sbbFactory = SBBFactory.instance();
      SBB sbb = sbbFactory.getSBB(ConferenceController.class.getName(),
                  CONF_CONTROLLER,timer.getApplicationSession(),getServletContext());
      log("<APP> ConferenceController = "+sbb);

      //Connect the Conference Controller SBB.
      MsConferenceSpec confSpec = new MsConferenceSpec();
      confSpec.setMaxActiveSpeakers(2);
      log("setting max active speakers to: " + confSpec.getMaxActiveSpeakers());
      log("setting notify active speaker to true");
      confSpec.setNotifyActiveSpeaker(true);
      timer.getApplicationSession().setAttribute("confSpec",confSpec);
      confSpec.setConferenceId(CONF_ID);
      log("conf id is " + confSpec.getConferenceId());
      ((ConferenceController)sbb).setEventListener(new ConferenceListener());
      ((ConferenceController)sbb).connect(confSpec, ms);
      log("Setting timeout to 5 minutes, default timeout is " + ((ConferenceController)sbb).getTimeout());
      ((ConferenceController)sbb).setTimeout(900);
    }catch(Exception e){
      log(e.getMessage(), e);
    }
  } */
  
  public void init() throws ServletException {
    super.init();
    log("Inside the INIT method .....");
    //Create an Application Session.
    SipFactory sipFactory = (SipFactory)
            getServletContext().getAttribute(SipFactory.class.getName());
    SipApplicationSession appSession = sipFactory.createApplicationSession();
    /*TimerService timerService = (TimerService)
          getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
    timerService.createTimer(appSession, START_TIMER , false, "INIT");*/
  }
  public void doInvite(SipServletRequest request) {

    if(!request.isInitial()){
      log("Request is not initial, so not handling it.");
      return;
    }

    try{
    
    String _flag = "true";
    String _uri = ((SipURI)request.getRequestURI()).getUser();
    if(_uri.equals("123"))
    {
        log("sending the conf request.....");
        //Select a Media Server
        MediaServerSelector msSelector= (MediaServerSelector)
            getServletContext().getAttribute(MediaServerSelector.class.getName());
        MediaServer ms = msSelector.selectByCapabilities(0);
        if (ms == null) 
        {
          log("<APP> No active media server present");
          return;
        }
        //Create a Conference Controller SBB.
        SBBFactory sbbFactory = (SBBFactory) request.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
        if(sbbFactory == null) {
        	log("ERROR: SBBFactory is null.");
        	return;
        }
        SBB sbb = sbbFactory.getSBB(ConferenceController.class.getName(),
                    CONF_CONTROLLER,request.getApplicationSession(),getServletContext());
        log("<APP> ConferenceController = "+sbb);
        //Connect the Conference Controller SBB.
        MsConferenceSpec confSpec = new MsConferenceSpec();
        confSpec.setMaxActiveSpeakers(2);
        log("setting max active speakers to: " + confSpec.getMaxActiveSpeakers());
        log("setting notify active speaker to true");
        confSpec.setNotifyActiveSpeaker(true);
        request.getApplicationSession().setAttribute("confSpec",confSpec);
        confSpec.setConferenceId(CONF_ID);
        log("conf id is " + confSpec.getConferenceId());
        ((ConferenceController)sbb).setEventListener(new ConferenceListener());
        ((ConferenceController)sbb).connect(confSpec, ms);
        log("Setting timeout to 5 minutes, default timeout is " + ((ConferenceController)sbb).getTimeout());
        ((ConferenceController)sbb).setTimeout(900);
        request.createResponse(503).send();
      }
      
      else
      {
        log("Inside initial INVITE request....");
      //Create a 100 trying response and send it.
        request.createResponse(100).send();
  
        //Get the conference info object
        URI requestUri = request.getRequestURI();
        String conferenceId =
              requestUri.isSipURI() ? ((SipURI)requestUri).getUser() : "UNKNOWN";
        ConferenceRegistry registry = (ConferenceRegistry)
          getServletContext().getAttribute(ConferenceRegistry.class.getName());
        ConferenceInfo info = (ConferenceInfo) registry.findByConferenceID(conferenceId);
        if(info == null){
          log("Not able to get the Conference with ID :" + CONF_ID);
          request.createResponse(503).send();
          return;
        }
  
        boolean matching = registry.isMatchingRequest(conferenceId, request);
        if(matching){
  
          //Get the media server used by the conference controller.
          ConferenceController controller = (ConferenceController)
                  request.getApplicationSession().getAttribute(CONF_CONTROLLER);
          MediaServer ms = controller.getMediaServer();
  
          //Increment the Conference Participant Index.
          //Create an instance of Conference Participant SBB.
          PARTICIPANT_INDEX++;
          
          request.getApplicationSession().setAttribute("flag","false");
          SBBFactory sbbFactory = (SBBFactory) request.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
          if(sbbFactory == null) {
          	log("ERROR: SBBFactory is null.");
          	return;
          }         
          ConferenceParticipant confParticipant = (ConferenceParticipant)
              sbbFactory.getSBB(ConferenceParticipant.class.getName(),
              "participant" + PARTICIPANT_INDEX,
              request.getApplicationSession(), getServletContext());
          confParticipant.setEventListener(new ConferenceListener());
          log("Got the Conference Participant ....");
         //Call connect on the Conference Participant.
         if(PARTICIPANT_INDEX == 1)
          { 
            request.getApplicationSession().setAttribute("confParticipant",confParticipant);
          }
         if(PARTICIPANT_INDEX == 2)
          { 
            request.getApplicationSession().setAttribute("flag","true");
          }
            confParticipant.connect(request, ms);
            log("Called connect on the Conference Participant Controller ....");
        }
        else
        {
          //Get the conference URI and set it in the contact header of
          //a 302 response and send it.
          String uri = registry.getConferenceURI(conferenceId);
          SipServletResponse response = request.createResponse(302);
          response.setHeader("Contact", uri);
          response.send();
        }
      }
    }catch(Exception e){
      this.log(e.getMessage(), e);
    }
  }

  public void sessionCreated(SipApplicationSessionEvent arg0) {
  log("session created");
  }

  public void sessionDestroyed(SipApplicationSessionEvent arg0) {
  log("session destroyed ");
    /*try{
      ConferenceController controller = (ConferenceController)
              arg0.getApplicationSession().getAttribute(CONF_CONTROLLER);
      if(controller != null){
      log("disconnecting conf controller");
        controller.disconect();
      }
    }catch(Exception e){
      log(e.getMessage(), e);
    }*/
  }

  public void sessionExpired(SipApplicationSessionEvent evt) {
  log("session expired event");
    try{
      ConferenceController controller = (ConferenceController)
              evt.getApplicationSession().getAttribute(CONF_CONTROLLER);
      if(controller != null){
      log("disconnecting conf controller");
        controller.disconect();
      ConferenceRegistry registry = (ConferenceRegistry)
          getServletContext().getAttribute(ConferenceRegistry.class.getName());
		  log("Not Invoking unregisterConference() on registry");
          //registry.unregisterConference("12345");
      }
    }catch(Exception e){
      log(e.getMessage(), e);
    }
  }
public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
	// TODO Auto-generated method stub
	
}
}
