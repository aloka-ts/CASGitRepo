package com.sas.cap;
import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.tcap.component.ComponentConstants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.camel.CAPMsg.CAPSbb;
import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapCallStateEnum;
import com.camel.CAPMsg.SasCapMsgsToSend;


public class MediaSbbListner implements SBBEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MediaSbbListner.class);
	private CAPServlet capServlet ;
	private CAPSbb capSbb ;

	public MediaSbbListner() {

	}

	public void activate(SBB arg0) {
		logger.info("activate called.");

	}

	public int handleEvent(SBB sbb, SBBEvent event) {
		logger.info("handleEvent:Enter:EventID:" + event.getEventId());
		Properties camelProperties = CAPServlet.getCamelAppProperty();
		Hashtable<Integer, SasCapCallProcessBuffer> tcapCallData = CAPServlet.getTcapCallData();
		Integer dlgId = (Integer)sbb.getServletContext().getAttribute(Constants.DLG_ID);
		logger.debug("handleEvent: dlgId:" + dlgId);
		SasCapCallProcessBuffer buffer = tcapCallData.get(dlgId);
		logger.debug("handleEvent: buffer:" + buffer);
		capServlet = (CAPServlet)sbb.getApplicationSession().getAttribute("CAPServlet");
		logger.debug("handleEvent:CAPServlet instance:" + capServlet);
		capSbb = capServlet.getCapSbb();
		MsPlaySpec playSpec = new MsPlaySpec();

		if (event.getEventId().equals(SBBEvent.EVENT_CONNECTED)) {						
			MsSessionController mscontroller = (MsSessionController)sbb;
			buffer.stateInfo.setPrevState(buffer.stateInfo.getCurrState());
			buffer.stateInfo.setCurrState(SasCapCallStateEnum.IVR_CONNECTED);
			
			if(camelProperties == null){
				return SBBEventListener.NOOP ;
			}
			String annUri = camelProperties.getProperty("announcementURI");
			logger.debug("handleEvent:Announcment URI:" + annUri);
			try {
				playSpec.addAnnouncementURI(new java.net.URI(annUri));
			} catch (URISyntaxException e1) {
				logger.error("handleEvent:Exception is: " , e1);

			}
			playSpec.setClearDigitBuffer(true);
			playSpec.setBarge(true);

			try {
				mscontroller.play(playSpec);
			} catch (IllegalStateException e) {
				logger.error("handleEvent:Exception is:" , e);
			} catch (MediaServerException e) {
				logger.error("handleEvent:Exception is:" , e);
			}
			return SBBEventListener.CONTINUE ;
		}
		if (event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED)) {
			logger.info("handleEvent:EVENT_CONNECT_FAILED");
			SasCapMsgsToSend msgs = new SasCapMsgsToSend() ;
			try {
				buffer.problemType = ComponentConstants.PROBLEM_TYPE_TRANSACTION ;
				buffer.problemCode = ComponentConstants.PROBLEM_CODE_RESOURCE_UNAVAILABLE ;
				capSbb.rejectRequest(capSbb, buffer, msgs);
				capServlet.sendMsgs(msgs, buffer);
			} catch (Exception e) {
				logger.error("Exceptio is:" , e);
				handleFail(capServlet, buffer);
			}
			return SBBEventListener.CONTINUE ;
		}
		if (event.getEventId().equals(SBBEvent.EVENT_HOLD_COMPLETE)) {
			logger.info("handleEvent:EVENT_HOLD_COMPLETE");
		}
		if (event.getEventId().equals(SBBEvent.EVENT_HOLD_FAILED)) {
			logger.info("handleEvent:EVENT_HOLD_FAILED");
		}
		if (event.getEventId().equals(SBBEvent.EVENT_RESYNC_COMPLETED)) {
			logger.info("handleEvent:EVENT_RESYNC_COMPLETED");
		}
		if (event.getEventId().equals(SBBEvent.EVENT_RESYNC_FAILED)) {
			logger.info("handleEvent:EVENT_RESYNC_FAILED");
		}
		if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COMPLETED)) {
			logger.info("handleEvent:EVENT_PLAY_COMPLETED");
			buffer.stateInfo.setPrevState(buffer.stateInfo.getCurrState());
			buffer.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
			MsSessionController mscontroller = (MsSessionController)sbb;
			MsCollectSpec collectSpec = new MsCollectSpec();
			String firstDigitTime = camelProperties.getProperty("firstDigitTimer");
			String interDigitTime = camelProperties.getProperty("interDigitTimer");
			String terminateKey = camelProperties.getProperty("terminateKey");
			String minDigits = camelProperties.getProperty("minimumNbOfDigits");
			String maxDigits = camelProperties.getProperty("maximumNbOfDigits");
			if(logger.isDebugEnabled()){
				logger.debug("handleEvent:firstDigitTime:" + firstDigitTime);
				logger.debug("handleEvent:terminateKey:" + terminateKey);
				logger.debug("handleEvent:interDigitTime:" + interDigitTime);
				logger.debug("handleEvent:minDigits:" + minDigits);
				logger.debug("handleEvent:maxDigits:" + maxDigits);
			}
			collectSpec.setFirstDigitTimer(Integer.parseInt(firstDigitTime));
			collectSpec.setInterDigitTimer(Integer.parseInt(interDigitTime));

			collectSpec.applyPattern(Integer.parseInt(minDigits),Integer.parseInt(maxDigits),terminateKey);
			try {
				mscontroller.playCollect(playSpec, collectSpec);
			} catch (IllegalStateException e) {
				logger.error("handleEvent:Exception is:" , e);
			} catch (MediaServerException e) {
				logger.error("handleEvent:Exception is:" , e);
			}
			return SBBEventListener.CONTINUE ;
		}
		if (event.getEventId().equals(MsSessionController.EVENT_PLAY_FAILED)) {
			logger.info("handleEvent:EVENT_PLAY_FAILED");
			return handleFail(capServlet , buffer) ;
		}
		if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_COMPLETED)) {
			logger.info("handleEvent:EVENT_PLAY_COLLECT_COMPLETED");
			MsOperationResult result =((MsSessionController)sbb).getResult();
			String digits = (String) result.getAttribute(MsOperationResult.COLLECTED_DIGITS);
			logger.debug("handleEvent:EVENT_PLAY_COLLECT_COMPLETED :Collected digits: "+digits);
			if(buffer == null){
				logger.info("handleEvent: buffer is null.So returning NOOP");
				return SBBEventListener.NOOP ;
			}
			buffer.stateInfo.setPrevState(buffer.stateInfo.getCurrState());
			buffer.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
			if(logger.isDebugEnabled()){
				logger.debug("handleEvent:Current state:" + buffer.stateInfo.getPrevState());
				logger.debug("handleEvent:Next state:" + buffer.stateInfo.getCurrState());
			}
			SasCapMsgsToSend msgs = new SasCapMsgsToSend() ;
			try {
				capSbb.disconnectIvr(capSbb, buffer, msgs);
				capServlet.sendMsgs(msgs, buffer);
			} catch (Exception e) {
				logger.error("Exceptio is:" , e);
				handleFail(capServlet, buffer);
				return SBBEventListener.NOOP ;
			}
			Timer timer = new Timer();
			//timertask will be executed after 20 sec and succescive excecution will be after 30 sec.
			MsSessionController msSessionController = (MsSessionController)sbb ;
			//timer.scheduleAtFixedRate(new Timertask(timer,msSessionController,true),20000,30000);
			buffer.timer = timer ;
			return SBBEventListener.CONTINUE ;
		}
		if (event.getEventId().equals(MsSessionController.EVENT_PLAY_COLLECT_FAILED)) {
			logger.info("handleEvent:EVENT_PLAY_COLLECT_COMPLETED");			
			return handleFail(capServlet, buffer) ;
		}

		if (event.getEventId().equals(SBBEvent.EVENT_DISCONNECTED)) {
			logger.info("handleEvent:EVENT_DISCONNECTED");
			if(buffer.isTimerPresent()){
				buffer.timer.cancel();
			}
		}

		return SBBEventListener.CONTINUE;
	}

	private int handleFail(CAPServlet capServlet, SasCapCallProcessBuffer buffer){
		logger.info("handleFail:Enter");	
		SasCapMsgsToSend msgs = new SasCapMsgsToSend();						
		if(buffer == null){
			logger.info("handleFail: returning noop.");
			return SBBEventListener.NOOP ;
		}
		try {
			capServlet.sendingUserAbort(msgs, buffer);
			capServlet.cleanUpResources(buffer);

		} catch (MandatoryParameterNotSetException e1) {
			logger.error("handleFail:MandatoryParameterNotSetException is:" ,e1);
		} catch (IOException e1) {
			logger.error("handleFail:IOException is:" ,e1);
		} catch (IdNotAvailableException e1) {
			logger.error("handleFail:IdNotAvailableException is:" ,e1);
		}catch(Exception e){
			logger.error("handleFail:Exception is:" ,e);
		}
		logger.info("handleFail:Exit");	
		return SBBEventListener.CONTINUE ;
	}

}
