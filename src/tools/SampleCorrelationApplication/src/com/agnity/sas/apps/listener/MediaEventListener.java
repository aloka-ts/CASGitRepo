package com.agnity.sas.apps.listener;

import java.io.IOException;
import java.util.Map;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.sas.apps.SampleTestApp;
import com.agnity.sas.apps.domainobjects.SampleAppCallProcessBuffer;
import com.agnity.sas.apps.exceptions.MessageCreationFailedException;
import com.agnity.sas.apps.util.Constants;
import com.agnity.sas.apps.util.InapIsupParser;
import com.agnity.sas.apps.util.SampleAppCallStateEnum;
import com.baypackets.ase.sbb.GroupedMsEvent;
import com.baypackets.ase.sbb.GroupedMsSessionController;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.genband.tcap.provider.TcapSession;

public class MediaEventListener implements SBBEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2169759543024715437L;
	private static Logger logger = Logger.getLogger(MediaEventListener.class);
			
	public void activate(SBB sbb) {
		
	}

	public int handleEvent(SBB sbb, SBBEvent event) {	
		
		logger.debug("handleEvent() app enter");
		
		SipApplicationSession appSession = sbb.getApplicationSession();

		if(event.getMessage() instanceof SipServletRequest){
			SipServletRequest req = (SipServletRequest)event.getMessage();
			logger.info("TEST::Request received on media session ::"+req.getMethod()+"\n Contact :: "+req.getHeader("Contact"));
		} else {
			SipServletResponse response = (SipServletResponse)event.getMessage();
			logger.info("TEST::Response received on media session ::"+response);
			if(response != null){
				logger.info("TEST::Response status code  ::"+response.getStatus());
				if (response.getRequest().getMethod().equalsIgnoreCase("INVITE") && response.getStatus() == 200) {
					//sendAck
					SipServletRequest ack = response.createAck();
					try {
						ack.send();
					} catch (IOException e) {
						logger.debug("Exception in sending ACK "+e);
					}
					//SipServletResponse resp = (SipServletResponse)appSession.getAttribute(INVITE_2XX_RESP);
				}
			}
		}
		
		try {
			if(event.getEventId().equals(SBBEvent.EVENT_EARLY_MEDIA_CONNECT_PROGRESS)){
				logger.info("######## EARLY MEDIA CONNECT EVENT ########");
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				//set ACM
				byte[] acm = InapIsupParser.createACM();
//				byte[] acm = {(byte)0x41, (byte)0x45, (byte)0x32, (byte)0x37, (byte)0x39, (byte)0x43};
				ms.setAttribute(MsSessionController.ACM_ISUP, acm);
				
			
			}else if(event.getEventId().equals(SBBEvent.EVENT_EARLY_MEDIA)){
				logger.info("######## EARLY MEDIA EVENT ########");
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				//set ANM
				byte[] anm = InapIsupParser.createANM();
				ms.setAttribute(MsSessionController.ANM_ISUP, anm);
				
			}else if(event.getEventId().equals(SBBEvent.EVENT_CONNECT_PROGRESS)){
				logger.info("######## CONNECT PROGRESS EVENT ########");
				//return SBBEventListener.NOOP;
			}
			else if(event.getEventId().equals(SBBEvent.EVENT_SIG_IN_PROGRESS)){
				logger.info("######## IVR EVENT_SIG_IN_PROGRESS ########");
				
			}
			else if (event.getEventId().equals(SBBEvent.EVENT_CONNECTED)) {
				logger.info("######## IVR connected ########" + ((GroupedMsEvent)event).getConnectedMediaServer());
				
				//stop timer
				TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
				int dialogId=tcapSession.getDialogueId();
				SampleAppCallProcessBuffer cpb=SampleTestApp.getTcapCallData().get(dialogId);
				cpb.getEtcTimer().cancel();
				
				//remove correlation
				Integer correlation=null;
				SipServletRequest request = (SipServletRequest) appSession.getAttribute(Constants.ORIG_REQ);
				Map<Integer, Object> map = (Map<Integer, Object>)request.getSession().getServletContext().getAttribute(Constants.CORR_MAP_ATTR);
				logger.error("Dilaogue Id: tcapSession" + tcapSession);
				if(tcapSession!=null){
					correlation=(Integer) tcapSession.getAttribute(Constants.CORR_ID_ATTR);
					tcapSession.removeAttribute(Constants.CORR_ID_ATTR);
				}
				map.remove(correlation);
			
				//pc
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				
				MsPlaySpec playSpec = new MsPlaySpec();
				playSpec.addAnnouncementURI(new java.net.URI("file:/anns/test.wav"));
				playSpec.setClearDigitBuffer(true);
				playSpec.setBarge(true);
				
				MsCollectSpec collectSpec = new MsCollectSpec();
				collectSpec.setFirstDigitTimer(3000);
				collectSpec.setInterDigitTimer(4000);
				collectSpec.applyPattern(1,1,"#");
				ms.playCollect(playSpec, collectSpec);
				logger.info("######## connect completed ########");									
				
			} else if (event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED)) {
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				byte[] rel = InapIsupParser.createREL();
				ms.setAttribute(MsSessionController.REL_ISUP, rel);
				logger.info("Failed to connect to IVR ");
			}  else if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_COMPLETED)) {
				
				logger.info("playe collect completed successfully");					
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				MsOperationResult result = ms.getResult();
				String digits=result.getAttribute(MsOperationResult.COLLECTED_DIGITS).toString();
				logger.info("######## play collect completed ########" + digits);
				
				appSession.setAttribute(Constants.COLLECTED_DIGITS, digits);
				//XXX send DFC in iNAP
				SampleTestApp sipServlet=(SampleTestApp) appSession.getAttribute(Constants.SERVLET);
				
				TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
				int dlgId=tcapSession.getDialogueId();
				SampleAppCallProcessBuffer cpb=SampleTestApp.getTcapCallData().get(dlgId);
				cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.MEDIA_OPERATION_COMPLETED);
				sipServlet.performBusinessLogic(cpb,null);
//				return SBBEventListener.NOOP;
				
			}else if (event.getEventId().equals(SBBEvent.EVENT_DISCONNECTED)) {
				logger.info("######## IVR disconnected ########");
				
				TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
				int dlgId=tcapSession.getDialogueId();
				SampleAppCallProcessBuffer cpb=SampleTestApp.getTcapCallData().get(dlgId);
				cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.MEDIA_DISCONNECTED);
//				return SBBEventListener.NOOP;
				
				
			}else if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COMPLETED)) 
			{
				
			} else if (event.getEventId().equals(SBBEvent.EVENT_DISCONNECT_FAILED)) {
				logger.info("Failed to disconnect the IVR");
			} else if (event.getEventId().equals(MsSessionController.EVENT_PLAY_RECORD_COMPLETED)) 
			{
			} else if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_FAILED)) {
				logger.info("Play and collect failed");
			} else if (event.getEventId().equals(MsSessionController.EVENT_PLAY_FAILED)) {
				logger.info("Play announcemnet failed");
			} else {
				logger.warn("Unexpected event recieved." + event.getEventId());
			}
		}catch (MessageCreationFailedException ex) {
			logger.warn("MessageCreationFailedException in handleEvent", ex);
		} 
		catch (Exception ex) {
			logger.warn("Error in handleEvent", ex);
		}
		return SBBEventListener.CONTINUE;
	}
}

