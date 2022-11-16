
/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
 */


package com.baypackets.ase.testapps.sbb.conf;

import javax.servlet.ServletContext;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsVarAnnouncement;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;

public class ConferenceParticipantHandler implements SBBEventListener{

  /**
	 * 
	 */
	private static final long serialVersionUID = 2354365486487161764L;

public int handleEvent(SBB sbb, SBBEvent event){
    ServletContext ctx = sbb.getServletContext();
    try{
      if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED) &&
          sbb instanceof ConferenceParticipant){
        ctx.log("handleEvent(): Calling 'joinParticipant' on  " +
            "ConferenceController object during CONNECTED");
        ConferenceController controller = (ConferenceController)
          sbb.getApplicationSession().getAttribute(ConferenceServlet.CONF_CONTROLLER);

        controller.join(new ConferenceParticipant[] {(ConferenceParticipant)sbb},
                new String[] {ConferenceController.MODE_LISTEN_AND_TALK});

        //Play an Announcement to a user.
        MsPlaySpec playSpec = new MsPlaySpec();
        MsVarAnnouncement announcement = new MsVarAnnouncement();
        announcement.setType("digits");
        announcement.setSubType("gen");
        announcement.setValue("123456789");
        playSpec.addVariableAnnouncement(announcement);
        playSpec.setInterval(2000);
        playSpec.setIterations(2);

        controller.play(playSpec);
      }
    }catch(Exception e){
      ctx.log(e.getMessage(), e);
    }
    return SBBEventListener.CONTINUE;
  }

  public void activate(SBB sbb){
    sbb.getServletContext().log("activate called on Event Listener");
  }
}
