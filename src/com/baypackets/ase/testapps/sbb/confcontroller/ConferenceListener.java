/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */



package com.baypackets.ase.testapps.sbb.confcontroller;


import com.baypackets.ase.sbb.MsConferenceSpec;
import com.baypackets.ase.sbb.SBBFactory;
import java.util.Iterator;
import javax.servlet.ServletContext;

import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsVarAnnouncement;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.util.Constants;

import org.apache.log4j.Logger;

public class ConferenceListener implements SBBEventListener{

  private static final long serialVersionUID = 243547432418894347L;
  public int handleEvent(SBB sbb, SBBEvent event){
    ServletContext ctx = sbb.getServletContext();
    ctx.log("handleEvent event is: " + event.getEventId() + "for SBB: " + sbb);
    ConferenceController controller =null;
    try{
      if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("handleEvent(): Calling 'joinParticipant' on  " +
            "ConferenceController object during CONNECTED");
        //ConferenceController controller = (ConferenceController)
          //sbb.getApplicationSession().getAttribute(Conference.CONF_CONTROLLER);
        SBBFactory sbbFactory = (SBBFactory) sbb.getApplicationSession().getAttribute(Constants.SBB_FACTORY);
        if(sbbFactory == null) {
        	ctx.log("ERROR: SBBFactory is null.");
        	return SBBEventListener.CONTINUE;
        }
        controller = (ConferenceController) sbbFactory.getSBB(ConferenceController.class.getName(),
                  Conference.CONF_CONTROLLER,sbb.getApplicationSession(),sbb.getServletContext());
        MsConferenceSpec confSpec = (MsConferenceSpec)sbb.getApplicationSession().getAttribute("confSpec");
        ctx.log("conf id is : " + confSpec.getConferenceId());
        ctx.log("controller is " + controller);
        String flag = sbb.getApplicationSession().getAttribute("flag").toString();
        if(flag.equals("true"))
        {
        ctx.log("MODE_LISTEN_AND_TALK for this participant");
        controller.join(new ConferenceParticipant[] {(ConferenceParticipant)sbb},
                new String[] {ConferenceController.MODE_LISTEN_AND_TALK});
        ctx.log("now unjoining the first party");       
        ConferenceParticipant confParticipant = (ConferenceParticipant)sbb.getApplicationSession().getAttribute("confParticipant");
        //controller.unjoin(new ConferenceParticipant[] {confParticipant});
        }
        if(flag.equals("false"))
        {
          ctx.log("MODE_LISTEN_AND_TALK for this participant"); 
          controller.join(new ConferenceParticipant[] {(ConferenceParticipant)sbb},
                new String[] {ConferenceController.MODE_LISTEN_AND_TALK});
        }
        confSpec.setMaxActiveSpeakers(6);
        controller.updateConference(confSpec);
        ctx.log("max speakers after setting as 6 : " + confSpec.getMaxActiveSpeakers());
        
     /*
      * ctx.log("getting participant");
        Iterator _iter = controller.getParticipants();
        
        while(_iter.hasNext())
        {
          int tmp= 0;
          ConferenceParticipant _party = (ConferenceParticipant)_iter.next();
          ctx.log("participants are: " + _party.toString());
          ctx.log("checking is the above speaker active speaker" + controller.isActiveSpeaker(_party));
          tmp++;
          /*(if(tmp == 2)
          { 
            ctx.log("unjoining the 2nd participant");
            controller.unjoin(new ConferenceParticipant [] {_party});
          }
        }*/
        ctx.log("conf info: " + controller.getConferenceInfo().toString());
        ctx.log("timeout: " + controller.getTimeout()); 
        //Play an Announcement to a user.
        MsPlaySpec playSpec = new MsPlaySpec();
        /*MsVarAnnouncement announcement = new MsVarAnnouncement();
        announcement.setType("digits");
        announcement.setSubType("gen");
        announcement.setValue("123456789");
        playSpec.addVariableAnnouncement(announcement);
        playSpec.setInterval(2000);
        playSpec.setIterations(2);*/
        playSpec.addAnnouncementURI(new java.net.URI("file://mnt/192.168.8.19/dial_destn1.wav"));
        playSpec.setInterval(2000);
        playSpec.setIterations(2);
        
        controller.play(playSpec);
      }
      if(event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED)&&
          sbb instanceof ConferenceParticipant)
      {
        ctx.log("connect failed, disconnecting media server");
        controller.disconect();
      }
      if(event.getEventId().equals(SBBEvent.EVENT_CONF_JOIN_FAILED)&&
          sbb instanceof ConferenceParticipant)
      {
        ctx.log("join failed, disconnecting media server");
        controller.disconect();
      }
      if(event.getEventId().equals(SBBEvent.EVENT_CONF_JOINED)&&
          sbb instanceof ConferenceParticipant)
      {
        ctx.log("join completed");
        ctx.log("getting participant");
        Iterator _iter = controller.getParticipants();
        
        while(_iter.hasNext())
        {
          //int tmp= 0;
          ConferenceParticipant _party = (ConferenceParticipant)_iter.next();
          ctx.log("participants are: " + _party.toString());
          ctx.log("checking is the above speaker active speaker" + controller.isActiveSpeaker(_party));
        }
        MsConferenceSpec confSpec = (MsConferenceSpec)sbb.getApplicationSession().getAttribute("confSpec");
        ctx.log("max active speakers " + confSpec.getMaxActiveSpeakers()); 
        //if(flag.equals("true"))
        { 
          ctx.log("setting max active speakers to 5");
          confSpec.setMaxActiveSpeakers(5);  
        }
        controller.updateConference(confSpec);
        ctx.log("conference info " + controller.getConferenceInfo().toString());
        ctx.log("max active speakers after setting " + confSpec.getMaxActiveSpeakers()); 
      }
      if(event.getEventId().equals(SBBEvent.EVENT_DISCONNECTED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("disconnected event");
    }
     if(event.getEventId().equals(SBBEvent.EVENT_CONF_UNJOINED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("conf unjoined");
    }
    if(event.getEventId().equals(SBBEvent.EVENT_CONF_UNJOIN_FAILED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("conf unjoin failed");
        controller.disconect();
    }
    if(event.getEventId().equals(SBBEvent.EVENT_CONF_UPDATED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("conf updated");
    }
    if(event.getEventId().equals(SBBEvent.EVENT_CONF_UPDATE_FAILED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("conf update failed");
    }
    }
    catch(Exception e)
    {
      ctx.log(e.getMessage(), e);
    }
    return SBBEventListener.CONTINUE;
  }

  public void activate(SBB sbb){
    sbb.getServletContext().log("activate called on Event Listener");
  }
}
