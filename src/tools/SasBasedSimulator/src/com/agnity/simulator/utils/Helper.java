package com.agnity.simulator.utils;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.sccp.management.NStateReqEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ComponentConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.dialogue.BeginIndEvent;
import jain.protocol.ss7.tcap.dialogue.BeginReqEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueReqEvent;
import jain.protocol.ss7.tcap.dialogue.DialogueConstants;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;
import jain.protocol.ss7.tcap.dialogue.NoticeIndEvent;
import jain.protocol.ss7.tcap.dialogue.ProviderAbortIndEvent;
import jain.protocol.ss7.tcap.dialogue.UnidirectionalReqEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortReqEvent;

import java.io.IOException;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.handlers.factory.HandlerFactory;
import com.agnity.simulator.handlers.impl.ActivityTestHandler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.tasks.ActivityTestTimerTask;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.sbb.CDRWriteFailedException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.util.Util;
import com.genband.tcap.parser.TcapParser;
import com.genband.tcap.provider.TcapSession;

public class Helper {

	private static Logger logger = Logger.getLogger(Helper.class);

	private static AtomicInteger dialogId;

	private static int callId;
	
	static {
		dialogId = new AtomicInteger(1000000);
		callId = 100;
		}


	public static int generateDialogId(){
		return dialogId.incrementAndGet();
	}

	//in case of win, there are multiple dialogue id's per call.So a callId is required to identify a call.
	public static int getCallId(){
		return callId;
	}
	
	public static DialogueReqEvent createDialogReqEvent(Object source, String dialogAs,
			SimCallProcessingBuffer simCpb) {

		//creating dialog checking type
		if(dialogAs.equals(Constants.DIALOG_BEGIN)){
			return createBeginDialogReqEvent(source, simCpb);
		}else if(dialogAs.equals(Constants.DIALOG_CONTINUE)){
			return createContinueDialogReqEvent(source, simCpb);
		}if(dialogAs.equals(Constants.DIALOG_END)){
			return createEndDialogReqEvent(source, simCpb);
		}if(dialogAs.equals(Constants.DIALOG_U_ABORT)){
			return createUAbortDialogReqEvent(source, simCpb);
		}if(dialogAs.equals(Constants.DIALOG_P_ABORT)){
			return createPAbortDialogReqEvent(source, simCpb);
		}if(dialogAs.equals(Constants.DIALOG_UNIDIR)){
			return createUnidirectionalDialogReqEvent(source, simCpb);
		}
		return null;

	}



	public static BeginReqEvent createBeginDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating BEGIN dialog");
		
		BeginReqEvent beginReqEvent = new BeginReqEvent(source, simCpb.getDialogId(),
					simCpb.getOriginatingAddress() , simCpb.getDestinationAddress());

		
		//will be false
		if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, beginReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}
		
		if(InapIsupSimServlet.getInstance().getFlowType().equalsIgnoreCase("win")){
			if(simCpb.getCurrentMessage().equals(Constants.ANLYZD)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.ODISCONNECT)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.TDISCONNECT)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.ORREQ)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.SEIZERES)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}			
			if(simCpb.getCurrentMessage().equals(Constants.CALLCONTROLDIRREQ)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.TBUSY)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.TNOANSWER)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}if(simCpb.getCurrentMessage().equals(Constants.ONOANSWER)){
				beginReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			
			
			beginReqEvent.setAllowedPermission(true);			
		}
		return beginReqEvent;
	}


	public static ContinueReqEvent createContinueDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating Continue dialog");
		ContinueReqEvent continueReqEvent=new ContinueReqEvent(source, simCpb.getDialogId());

		//will be false
		if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, continueReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}
		continueReqEvent.setOriginatingAddress(simCpb.getOriginatingAddress());
		
		if(InapIsupSimServlet.getInstance().getFlowType().equalsIgnoreCase("win")){
			
			if(simCpb.getCurrentMessage().equals(Constants.SEIZERESRESP)){
				continueReqEvent.setAllowedPermission(false);	
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.CONNRES)){
				continueReqEvent.setAllowedPermission(false);	
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.INSTRUCTIONREQ)){
				continueReqEvent.setAllowedPermission(false);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.SRFDIRECTIVE)){
				continueReqEvent.setAllowedPermission(false);
				simCpb.setCurrentMessage("none");
			}
			if(simCpb.getCurrentMessage().equals(Constants.SRFDIRECTIVE_RET_RES)){
				continueReqEvent.setAllowedPermission(true);
				simCpb.setCurrentMessage("none");
			}
			
			
			continueReqEvent.setAllowedPermission(true);	
		}
		return continueReqEvent;
	}

	//sends Tc-End basic termination
	public static EndReqEvent createEndDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating End dialog");
		EndReqEvent endReqEvent=new EndReqEvent(source, simCpb.getDialogId());
		endReqEvent.setTermination(DialogueConstants.TC_BASIC_END);
		
	
		//will be false
		if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, endReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}
		
//		simCpb.setTcapTerminationMessageExchanged(true);
		return endReqEvent;
	}

	public static UserAbortReqEvent createUAbortDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating Uabort dialog");
		UserAbortReqEvent uAbortReqEvent=new UserAbortReqEvent(source, simCpb.getDialogId());

		//will be false
		if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, uAbortReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}
//		simCpb.setTcapTerminationMessageExchanged(true);
		return uAbortReqEvent;
	}

	public static UserAbortReqEvent createPAbortDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating uabort dialog instead of pabort");
		UserAbortReqEvent uAbortReqEvent=new UserAbortReqEvent(source, simCpb.getDialogId());

		//will be false
		if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, uAbortReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}
//		simCpb.setTcapTerminationMessageExchanged(true);
		return uAbortReqEvent;
	}
	
	public static UnidirectionalReqEvent createUnidirectionalDialogReqEvent(Object source,SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info( " creating Unidirectional dialog");
		UnidirectionalReqEvent unidirReqEvent=new UnidirectionalReqEvent( source,InapIsupSimServlet.getInstance().getLocalAddrs().get(0) , InapIsupSimServlet.getInstance().getRemoteAddr());
		if((simCpb!=null)&&(simCpb.getDialogId()!=0))
			unidirReqEvent.setDialogueId(simCpb.getDialogId());
		else
			unidirReqEvent.setDialogueId(Constants.dialogueIdRSN);
		//will be false
	/*	if(simCpb.isDialoguePortionPresent()){
			setDialoguePortion(simCpb, source, unidirReqEvent);
			simCpb.setDialoguePortionPresent(false) ;
		}*/
//		simCpb.setTcapTerminationMessageExchanged(true);
		return unidirReqEvent;
	}
	
	private static void setDialoguePortion(SimCallProcessingBuffer simCpb, Object source,DialogueReqEvent dlgEvent){
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId()) + "::: setDialoguePortion:Making the DialoguePortion");
		DialoguePortion dlgPortion = new DialoguePortion(source);

		if(simCpb.getAppContextIdentifier() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:appContextIdentifier in the DialoguePortion:"+ simCpb.getAppContextIdentifier());
			dlgPortion.setAppContextIdentifier(simCpb.getAppContextIdentifier());
		}
		if(simCpb.getAppContextName() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:appContextName in the DialoguePortion:"+ simCpb.getAppContextName());
			dlgPortion.setAppContextName(simCpb.getAppContextName());
		}
		if(simCpb.getUserInfo() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:userInfo in the DialoguePortion:"+ simCpb.getUserInfo());
			dlgPortion.setUserInformation(simCpb.getUserInfo());
		}
		if(simCpb.getSecurityContextIdentifier() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:securityContextIdentifier in the DialoguePortion:"+ simCpb.getSecurityContextIdentifier());
			dlgPortion.setSecurityContextIdentifier(simCpb.getSecurityContextIdentifier());
		}
		if(simCpb.getSecurityContextInfo() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:securityContextInfo in the DialoguePortion:"+ simCpb.getSecurityContextInfo());
			dlgPortion.setSecurityContextInformation(simCpb.getSecurityContextInfo());
		}
		if(simCpb.getProtocolVersion() != null){
			if(logger.isInfoEnabled())
				logger.info(Util.toString(simCpb.getDialogId()) + 
						"::: connect:protocolVersion in the DialoguePortion:"+ simCpb.getProtocolVersion());
			dlgPortion.setProtocolVersion(simCpb.getProtocolVersion());
		}
		dlgEvent.setDialoguePortion(dlgPortion);
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId()) + "::: setDialoguePortion:Exit");
	}


	public static void processRcvdDialogue(DialogueIndEvent event,
			SimCallProcessingBuffer simCpb) throws ParameterNotSetException {
		

		int dlgId = 0;
		dlgId = simCpb.getDialogId() ;
		if(logger.isInfoEnabled())
			logger.info(Util.toString(dlgId)+ "::: processDialogue:indication is Dialogue");
		try{
			storeDialogPortion(dlgId,event, simCpb);
		}catch(ParameterNotSetException e){
			if(logger.isDebugEnabled())
				logger.debug("Parameter not set exception in store dialog",e);
		}
		
		DialogueIndEvent dilgEvent = (DialogueIndEvent)event ;
		//		simCpb.setLastDialoguePrimitive(dilgEvent.getPrimitiveType());
		switch (dilgEvent.getPrimitiveType()) {
		case TcapConstants.PRIMITIVE_BEGIN : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue begin indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_BEGIN) ;
			BeginIndEvent begin = (BeginIndEvent)event ;
			simCpb.setOriginatingAddress(begin.getOriginatingAddress());
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Origin Address: " + begin.getOriginatingAddress());
			simCpb.setDestinationAddress(begin.getDestinationAddress());
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Destination Address: " + begin.getDestinationAddress());
			break ;
		}
		case TcapConstants.PRIMITIVE_CONTINUE : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue continue indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_CONTINUE) ;
			break ;
		}
		case TcapConstants.PRIMITIVE_END : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue end indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_END) ;
			EndIndEvent endIndEvent = (EndIndEvent)event ;
			simCpb.setTcapTerminationMessageExchanged(true);
			if(! endIndEvent.isComponentsPresent()){
				if(logger.isDebugEnabled())
					logger.debug(Util.toString(dlgId) + "::: processDialogue: NO component in TC_END:"+endIndEvent.isComponentsPresent());
				//pass message to tcEndHandler
				callMessageHandler(endIndEvent, simCpb);
				//cleaning up as tcEnd is recieved
				cleanUpResources(simCpb, false);
			}
			//in case component present cleanup happens after last component
			break ;
		}
		case TcapConstants.PRIMITIVE_PROVIDER_ABORT : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue provider abort indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_PROVIDER_ABORT) ;
			ProviderAbortIndEvent provdAbort = (ProviderAbortIndEvent)event ;
			simCpb.setTcapTerminationMessageExchanged(true);
			cleanUpResources(simCpb,false);
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: Provider Abort Reason:" + provdAbort.getPAbort());

			break ;
		}
		case TcapConstants.PRIMITIVE_USER_ABORT : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue user abort indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_USER_ABORT) ;
			//XXX
			simCpb.setTcapTerminationMessageExchanged(true);
			callMessageHandler(event,simCpb);
			cleanUpResources(simCpb,false);
			break ;
		}
		case TcapConstants.PRIMITIVE_NOTICE : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue notice indication recieved");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_NOTICE) ;
			NoticeIndEvent noticeIndEvent = (NoticeIndEvent)event;
			if(noticeIndEvent.isReportCausePresent()){
				if(logger.isDebugEnabled())
					logger.debug(Util.toString(dlgId) + "::: processDialogue: Current state:" + Util.formatBytes(noticeIndEvent.getReportCause()));
			}
			break ;
		}
		case TcapConstants.PRIMITIVE_UNIDIRECTIONAL : {
			
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue Unidirectional Primitive");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_UNIDIRECTIONAL) ;
			if(dlgId==10001){
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processDialogue: dialogue Unidirectional Primitive(RSN Handling)");
			simCpb.setLastDialoguePrimitive(TcapConstants.PRIMITIVE_UNIDIRECTIONAL) ;
			//send tc_End preArranged
			EndReqEvent endReqEvent= createEndDialogReqEvent(InapIsupSimServlet.getInstance(), simCpb);
			endReqEvent.setTermination(DialogueConstants.TC_PRE_ARRANGED_END);
			try {
				Helper.sendDialogue(endReqEvent, simCpb);
			} catch (IOException e) {
				logger.error(Util.toString(dlgId) + "::: IOException excpetion sending Dialog on RSN handling::"+Constants.DIALOG_END,e);
			}
			}
							
			break ;

		}
		default : {
			logger.error(Util.toString(dlgId) + "::: processDialogue: dialogue NOT RECOGNIZED recieved: "+ dilgEvent.getPrimitiveType());
		}
		}

	}


	public static void cleanUpResources(SimCallProcessingBuffer simCpb, boolean cleanIsup) {
		if(logger.isInfoEnabled())
			logger.info("cleanUpResources() called");
		
		if(logger.isDebugEnabled())
			logger.debug("Attempt to write CDRS" );
		//writeCdR
		try{
			writeCdr(simCpb);
		}catch(Throwable th){
			logger.error("error Writing cdrs ",th);
		}
		if(simCpb.isTcap()){
			int dlgId=simCpb.getDialogId();
			InapIsupSimServlet.getInstance().getTcapCallData().remove(dlgId);
			InapIsupSimServlet.getInstance().getTcapCallData().remove(simCpb.getCallId());
			try {
				InapIsupSimServlet.getInstance().getTcapProvider().releaseDialogueId(dlgId);
			} catch (IdNotAvailableException e) {
				if(logger.isInfoEnabled())
					logger.info("DialogID release Failed",e);
			}

			if(! (simCpb.isTcapTerminationMessageExchanged() ) ){
				sendUserAbort(InapIsupSimServlet.getInstance(), simCpb, DialogueConstants.ABORT_REASON_USER_SPECIFIC);
				if(simCpb.isCallSuccess()){
					if(logger.isDebugEnabled())
						logger.debug(Util.toString(dlgId)+"   Incomplete Flow in Call Flow XML, sent U-Abort" );
				}
			}
		}

		String callId = simCpb.getCallId();
		SipApplicationSession appSession = simCpb.getSipAppSession();
		if(cleanIsup ){
			if(callId!=null){
				InapIsupSimServlet.getInstance().getSipCallData().remove(callId);
			}
			if(appSession!=null){
				InapIsupSimServlet.getInstance().getAppSessionIdCallData().remove(appSession.getId());
				appSession.invalidate();
			}
			simCpb.setCleaned(true);
		}

	}


	private static void writeCdr(SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("Inside writeCdr" );
		long startTime= simCpb.getCallStartTime();
		long endTime= System.currentTimeMillis();
		simCpb.setCallStartTime(endTime);
		Date startDate= new Date(startTime);
		Date endDate = new Date(endTime);
		StringBuilder cdrString= new StringBuilder();
		cdrString.append("Dialog Id::");
		cdrString.append(simCpb.getDialogId());
		cdrString.append(";Call Id::");
		cdrString.append(simCpb.getCallId());
		cdrString.append(";Start Time::");
		cdrString.append(startDate);
		cdrString.append(";End Time::");
		cdrString.append(endDate);
		cdrString.append(";Duration(ms)::");
		cdrString.append((endTime-startTime));
		cdrString.append(";Status::");
		cdrString.append(simCpb.isCallSuccess());

		CDR cdr= null;
		
		//FIXME vidhu this was incorrect; i changed to better approach
		
		SipServletMessage sipMsg = simCpb.getLastSipMessageLeg1();
		if(sipMsg == null){
			sipMsg = simCpb.getLastSipMessageLeg2();
			if(sipMsg == null){
				sipMsg = simCpb.getLastSipMessage();
			}
		}
			
		
		if(sipMsg != null){
			if(logger.isDebugEnabled())
				logger.debug("Inside writeCdr-->sipmsg found, callid::"+
						simCpb.getCallId()+"   dialogID::"+simCpb.getDialogId() );
			cdr = (CDR) sipMsg.getSession().getAttribute(CDR.class.getName());
		}else{
			//check tPG	
			if(logger.isDebugEnabled())
				logger.debug("Inside writeCdr-->sipmsg not present attempting tcapsession, callid::"+
						simCpb.getCallId()+"   dialogID::"+simCpb.getDialogId() );
			int dlgId= simCpb.getDialogId();
			TcapSession tcapSession =InapIsupSimServlet.getInstance().getTcapProvider().getTcapSession(dlgId);
			if(tcapSession != null){
				if(logger.isDebugEnabled())
					logger.debug("Inside writeCdr-->tcapsession found, callid::"+
							simCpb.getCallId()+"   dialogID::"+simCpb.getDialogId() );
				cdr = (CDR) tcapSession.getAttribute(CDR.class.getName());
			}
		}

		
		if(cdr == null){
			if(logger.isDebugEnabled())
				logger.debug("Inside writeCdr-->CDR is still null;return, callid::"+
						simCpb.getCallId()+"   dialogID::"+simCpb.getDialogId() );
			return;
		}else{
			String[] cdrs = new String[]{cdrString.toString()};
			try {
				cdr.write(cdrs);
			} catch (CDRWriteFailedException e) {
				if(logger.isDebugEnabled())
					logger.debug("Inside writeCdr-->CDRWriteFailedException writinfg CDRs, callid::"+
							simCpb.getCallId()+"   dialogID::"+simCpb.getDialogId(),e );
			}
		}
	}


	private static void storeDialogPortion(int dlgId, DialogueIndEvent event, SimCallProcessingBuffer simCpb) throws ParameterNotSetException {
		simCpb.setDialoguePortionPresent(false) ;

		//Changes for dlgPortion
		if(event.isDialoguePortionPresent()){
			logger.error("DialoguePortionPresent for dlgId:" + dlgId);
			simCpb.setDialoguePortionPresent(true);
			DialoguePortion dlgPortion = event.getDialoguePortion();

			if(dlgPortion.isAppContextIdentifierPresent()){
				logger.error("isAppContextIdentifierPresent for dlgId:" + dlgId);
				simCpb.setAppContextIdentifier(dlgPortion.getAppContextIdentifier());
			}
			if(dlgPortion.isAppContextNamePresent()){
				logger.error("isAppContextNamePresent for dlgId:" + dlgId);
				simCpb.setAppContextName(dlgPortion.getAppContextName());
			}
			if(dlgPortion.isProtocolVersionPresent()){
				logger.error("isProtocolVersionPresent for dlgId:" + dlgId);
				simCpb.setProtocolVersion(dlgPortion.getProtocolVersion());
			}
			if(dlgPortion.isSecurityContextIdentifierPresent()){
				logger.error("isSecurityContextIdentifierPresent for dlgId:" + dlgId);
				simCpb.setSecurityContextIdentifier(dlgPortion.getSecurityContextIdentifier());
			}
			if(dlgPortion.isSecurityContextInformationPresent()){
				logger.error("isSecurityContextInformationPresent for dlgId:" + dlgId);
				simCpb.setSecurityContextInfo(dlgPortion.getSecurityContextInformation());
			}
			if(dlgPortion.isUserInformationPresent()){
				logger.error("isUserInformationPresent for dlgId:" + dlgId);
				simCpb.setUserInfo(dlgPortion.getUserInformation());
			}
		}
		if(logger.isInfoEnabled())
			logger.info("Dilog portion for "+Util.toString(dlgId)+"  appContextIdentifier:" + simCpb.getAppContextIdentifier() + 
					" ,appContextName:" + simCpb.getAppContextName() +" ,protocolVersion:"+simCpb.getProtocolVersion() + 
					" ,securityContextInfo:"+simCpb.getSecurityContextInfo() +" ,securityContextIdentifier:"+ 
					simCpb.getSecurityContextIdentifier()+ " ,userInfo"+simCpb.getUserInfo());

	}


	public static void processRcvdComponent(ComponentIndEvent event,
			SimCallProcessingBuffer simCpb) throws ParameterNotSetException {

		int dlgId = simCpb.getDialogId() ;
		if(logger.isInfoEnabled())
			logger.info(Util.toString(dlgId) + "::: processComponent:Enter");

		ComponentIndEvent cmpIndEvent = (ComponentIndEvent)event ;
		if(logger.isDebugEnabled())
			logger.debug(Util.toString(dlgId) + "::: processComponent:Received component primitive:" + cmpIndEvent.getPrimitiveType());

		switch (cmpIndEvent.getPrimitiveType()) {
		case TcapConstants.PRIMITIVE_INVOKE : {				
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_INVOKE");
			// cast to an Invoke Indication Event
			byte [] opCode = ((InvokeIndEvent) event).getOperation().getOperationCode();
			if (opCode[0] == InapOpCodes.ACTIVITY_TEST_BYTE){
				if(logger.isDebugEnabled())
					logger.debug(Util.toString(dlgId) + "::: processComponent:Activity Test Message");
				Handler activityTestHandler = ActivityTestHandler.getInstance();
				activityTestHandler.recieveMessage(null, simCpb, event);
			}else {
				callMessageHandler(event,simCpb);	
			}
			break ;
		}//end of invoke component
		case TcapConstants.PRIMITIVE_RESULT : {
			if(logger.isInfoEnabled())
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT");

			//casting to result indication event
			ResultIndEvent resultIndEvent = (ResultIndEvent)event ;
			if(resultIndEvent.isLastResultEvent()){
				if(logger.isInfoEnabled())
					logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT last result component");
				Operation op = null;
				byte[] opCode = null;
				if(resultIndEvent.isOperationPresent()){
					if(logger.isInfoEnabled())
						logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT operation present");
					op = resultIndEvent.getOperation();
					opCode = op.getOperationCode();
				}else{
					if(resultIndEvent.isInvokeIdPresent()){
						int invokeId = resultIndEvent.getInvokeId();
						if(logger.isInfoEnabled())
							logger.info(Util.toString(dlgId) + "::: processComponent:invoke Id present:" + invokeId);
						Byte opCd = simCpb.getInvokeIdOpCodeMap().get(invokeId);
						if(opCd != null){
							opCode = new byte[1];
							opCode[0] = opCd ;
						}
					}						
				}

				if(opCode != null){
					String opCodeStr = Util.formatBytes(opCode);
					if(logger.isInfoEnabled())
						logger.info(Util.toString(dlgId) + "::: received opCodes:::"+opCodeStr);
					if(opCodeStr.equalsIgnoreCase(Constants.ACTIVITY_TEST_RESULT)){
						if(logger.isInfoEnabled())
							logger.info(Util.toString(dlgId) + "::: processComponent:Result of ACTIVITY_TEST got it.");
						simCpb.getActivityTestTimerTask().cancel();
					}
					callMessageHandler(event,simCpb);
				}else{
					if(logger.isInfoEnabled())
						logger.info(Util.toString(dlgId) + "::: opcode is null");
				}
				
			}else {
				if(logger.isInfoEnabled())
					logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_RESULT Not a last result component");
			}
			break ;
		}
		case TcapConstants.PRIMITIVE_ERROR : {
			if(logger.isInfoEnabled())
				logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_ERROR");
			//casting to ErrorIndEvent for future purpose
			ErrorIndEvent errorInd = (ErrorIndEvent)cmpIndEvent;
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processComponent: Error Type :" + errorInd.getErrorType());
			byte[] error = errorInd.getErrorCode();
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processComponent: Error :" + Util.formatBytes(error));

			// process recieved message
			callMessageHandler(event,simCpb);

			break ;
		}
		case TcapConstants.PRIMITIVE_REJECT : {
			logger.info(Util.toString(dlgId) + "::: processComponent:PRIMITIVE_REJECT");
			//casting to RejectIndEvent for future purpose
			RejectIndEvent rejectInd = (RejectIndEvent)cmpIndEvent ;
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem Type :" + rejectInd.getProblemType());
			logger.debug(Util.toString(dlgId) + "::: processComponent: Problem :" + rejectInd.getProblem());
			
			// process recieved message
			callMessageHandler(event,simCpb);

			break ;
		}
		default : {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(dlgId) + "::: processComponent: Component NOT RECOGNIZED recieved: " + 
						cmpIndEvent.getPrimitiveType());
		}
		}


		if(event.isLastComponent() && simCpb.getLastDialoguePrimitive()==TcapConstants.PRIMITIVE_END && !(InapIsupSimServlet.getInstance().getFlowType().equalsIgnoreCase("win") )){
			if(logger.isDebugEnabled())
				logger.debug("Last component of Tc-end processed calling cleanup");
			cleanUpResources(simCpb,false);
		}

	}


	public static void callMessageHandler(Object message,
			SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("callMessageHandler DialogId::"+simCpb.getDialogId()+ "  callId::"+simCpb.getCallId() );
		InapIsupSimServlet instance =InapIsupSimServlet.getInstance();
		Node nextNode = instance.getNodeManager().getNextNode(simCpb.getCurrNode());
		if(nextNode==null){
			if(logger.isDebugEnabled())
				logger.debug("next node not found...return and clean");
			Counters.getInstance().incrementUnExpectedMessages();
			SuiteLogger.getInstance().log("UNEXPECTED MESSAGE-->FileName::["+instance.getCurrentFileName()+
			"] Next node not found. Clean and return");
			Helper.cleanUpResources(simCpb,true);
			return;
		}
		Handler handler = HandlerFactory.getHandler(nextNode);
		if(handler== null){
			logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
			//			Counters.getInstance().incrementUnExpectedMessages();
			Counters.getInstance().incrementUnHandledNode();
			return;
		}
		handler.recieveMessage(nextNode, simCpb, message);

	}

	public static void performActivityTest(InapIsupSimServlet source,
			SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId()) + "::: Helper.performActivityTest() enter");

		byte[] activityOpCode =  { Constants.ACTIVITY_TEST_OP_CODE };
		Operation activityOp = new Operation(Operation.OPERATIONTYPE_LOCAL, activityOpCode);
		InvokeReqEvent ireContinue = new InvokeReqEvent(source, simCpb.getDialogId(), activityOp);			
		ireContinue.setClassType(ComponentConstants.CLASS_3);
		int invokeId=simCpb.incrementAndGetInvokeId();
		ireContinue.setInvokeId(invokeId);

		//creating dialog
		ContinueReqEvent continueReqEvent = createContinueDialogReqEvent(source, simCpb);
		if(logger.isDebugEnabled())
			logger.debug(Util.toString(simCpb.getDialogId()) + "::: Activity test component and continue dialog crerated..start sending");

		try {
			Helper.sendComponent(ireContinue, simCpb);
			Helper.sendDialogue(continueReqEvent, simCpb);
			simCpb.addInvokeIdOpCodeVal(invokeId, Constants.ACTIVITY_TEST_OP_CODE);
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(simCpb.getDialogId()) + "::: Activity test component and continue dialog sent; starting Timer");
			//start ActivityTestTimer

			String waitTimeActivityTestResult = source.getConfigData().getActivityTestTimeout();
			long waitTime=Constants.DEFAULT_AT_TIMEOUT;
			if(waitTimeActivityTestResult != null){
				waitTime = Long.parseLong(waitTimeActivityTestResult);
			}

			//getting timer
			Timer timer = source.getTimeoutTimer();
			if(timer ==null ){
				if(logger.isInfoEnabled())
					logger.info("Helper performActivityTest() creating new timer");
				timer = new Timer();
				InapIsupSimServlet.getInstance().setTimeoutTimer(timer);
			}

			//getting ACtivityTest task
			ActivityTestTimerTask timertaskAT = new ActivityTestTimerTask(timer, simCpb.getDialogId());

			//scheduling task in failure proof way
			try{
				timer.schedule(timertaskAT ,waitTime*1000);
				simCpb.setActivityTestTimerTask(timertaskAT);
			}catch(Exception e){
				logger.error("Timer creation FAiled once...recreating::"+e.getMessage());
				timer = new Timer();
				source.setTimeoutTimer(timer);
				try{
					timer.schedule(timertaskAT, waitTime*1000);
					simCpb.setActivityTestTimerTask(timertaskAT);
				}catch (Exception e1) {
					logger.error("Timer creation FAiled again::"+e.getMessage());
					InapIsupSimServlet.getInstance().setTimeoutTimer(null);
				}
			}

		} catch (ParameterNotSetException e) {
			logger.error(Util.toString(simCpb.getDialogId()) + "::: ParameterNotSetException sending Activity Test component/continue dialog",e);
		} catch (IOException e) {
			logger.error(Util.toString(simCpb.getDialogId()) + "::: IOException excpetion sending Activity Test component/continue dialog",e);
		}

		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId()) + "::: Helper.performActivityTest() Completed");

	}

	public static void sendUserAbort(InapIsupSimServlet instance,
			SimCallProcessingBuffer simCpb, int reason) {
		UserAbortReqEvent uAbortReqevent= createUAbortDialogReqEvent(instance, simCpb);
		if(reason !=-1)
			uAbortReqevent.setAbortReason(reason);
		//		uAbortReqevent.setUserAbortInformation(userAbortInformation);
		try {
			Helper.sendDialogue(uAbortReqevent, simCpb);
		} catch (IOException e) {
			logger.error(Util.toString(simCpb.getDialogId()) + "::: IOException " +
					"sending Dialog UAbort on AT::",e);
		} catch (MandatoryParameterNotSetException e) {
			logger.error(Util.toString(simCpb.getDialogId()) + "::: MandatoryParameterNotSetException " +
					"sending UAbort on AT::",e);

		}

	}

	public static void  sendComponent(ComponentReqEvent compReqEvent, SimCallProcessingBuffer simCpb) throws ParameterNotSetException {
		
		if(logger.isInfoEnabled())
			logger.info("Dilaogue Id:" + compReqEvent.getDialogueId()+ "::sendComponent:Enter");
		//sim cpb taken input for future use
		InapIsupSimServlet.getInstance().getTcapProvider().sendComponentReqEvent(compReqEvent);
		if(logger.isDebugEnabled())
			logger.debug("Dilaogue Id:" + compReqEvent.getDialogueId()+ "::Successfully sent component:"+ compReqEvent.getInvokeId());
	}



	public static void sendDialogue(DialogueReqEvent dialogReqEvent,SimCallProcessingBuffer simCpb) throws MandatoryParameterNotSetException, IOException{
		////in case of RSN we are not maintaining SimCallProcessingBuffer,so simCpb=null in this case
		if(simCpb==null)
		{
		if(logger.isInfoEnabled())
			logger.info("Dilaogue Id:" +dialogReqEvent.getDialogueId()+ "::sendDialogue:Enter");

		if(logger.isDebugEnabled())
			logger.debug("Dilaogue Id:" + dialogReqEvent.getDialogueId() + "::Sending dialogue-->" + dialogReqEvent.toString());
		}
		int primitiveType= dialogReqEvent.getPrimitiveType();
		InapIsupSimServlet.getInstance().getTcapProvider().sendDialogueReqEvent(dialogReqEvent);
		
		switch (primitiveType) {
		case TcapConstants.PRIMITIVE_END:
			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + dialogReqEvent.getDialogueId() + "::Primitive End; mark tcap endmessage exchanged");
			simCpb.setTcapTerminationMessageExchanged(true);
			break;
		case TcapConstants.PRIMITIVE_USER_ABORT:
			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + dialogReqEvent.getDialogueId() + "::Primitive User Abort; mark tcap end message exchanged");
			simCpb.setTcapTerminationMessageExchanged(true);
			break;
		case TcapConstants.PRIMITIVE_PROVIDER_ABORT:
			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + dialogReqEvent.getDialogueId() + "::Primitive Provider Abort; mark tcap end message exchanged");
			simCpb.setTcapTerminationMessageExchanged(true);
			break;

		default:
			break;
		}
		
		if(logger.isInfoEnabled())
			logger.info("::sendDialogue:Exit");
		if(simCpb!=null)
		{
		if(logger.isInfoEnabled())
			logger.info("Dilaogue Id:" + simCpb.getDialogId() + "::sendDialogue:Exit");
		}
	}



	public static  void formMultiPartMessage(Multipart mp, byte[] content, String contentType) throws MessagingException {
		if(mp == null)
			mp = new MimeMultipart();

		MimeBodyPart mb = new MimeBodyPart();
		ByteArrayDataSource ds = new ByteArrayDataSource(content, contentType);
		mb.setDataHandler(new DataHandler(ds));
		mb.setHeader("Content-Type", contentType);
		mp.addBodyPart(mb);
	}

	public static final boolean supports100Rel(SipServletRequest request){
		boolean supports = false;

		String strRequire = request.getHeader(Constants.HDR_REQUIRE);
		supports = strRequire != null && strRequire.indexOf(Constants.VALUE_100REL) != -1;

		if(!supports){
			String strSupported = request.getHeader(Constants.HDR_SUPPORTED);
			supports = strSupported != null && strSupported.indexOf(Constants.VALUE_100REL) != -1;	
		}
		return supports;
	}

	// check if response is send reliably
	public static final boolean isReliable(SipServletResponse resp) {
		// if response has RSeq header returns true, false otherwise
		if (resp.getHeader(Constants.HDR_RSEQ) != null) {
			return true;
		}
		return false;
	}


	public static String getValueForHedaerUri(String value,
			Map<String, Variable> varMap) {
		String userInfo = null;
		String host = null;
		String port =null;
		final String sipIdentifier = "sip:".intern();

		StringBuilder actualUri=new StringBuilder();

		if(value.toLowerCase().startsWith(sipIdentifier)){
			value =value.substring(sipIdentifier.length());
			actualUri.append(sipIdentifier);
		}

		String [] uri=value.split("@");
		userInfo= uri[0];
		String[] urlPart = uri[1].split(":"); 
		host = urlPart[0];

		String[] portPart = urlPart[1].split(";", 2);
		port = portPart[0];



		if(userInfo.startsWith("$")){
			String varName = userInfo.substring(1);
			Variable variable=varMap.get(varName);
			actualUri.append(variable.getVarValue());
		}else{		
			actualUri.append(userInfo);
		}
		actualUri.append("@");

		if(host.startsWith("$")){
			String varName = host.substring(1);
			Variable variable=varMap.get(varName);
			actualUri.append(variable.getVarValue());
		}else{		
			actualUri.append(host);
		}
		actualUri.append(":");
		if(port.startsWith("$")){
			String varName = port.substring(1);
			Variable variable=varMap.get(varName);
			actualUri.append(variable.getVarValue());
		}else{		
			actualUri.append(port);
		}
		if(portPart.length >1){
			actualUri.append(";");
			actualUri.append(portPart[1]);
		}

		return actualUri.toString();

	}	

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[(i / 2)] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	
	public static String byteArrayToHexString(byte[] data) {
		char output[] = new char[5 * (data.length)];
		int top = 0;

		for (int i = 0; i < data.length; i++) {
			output[top++] = Util.hexcodes[(data[i] >> 4) & 0xf];
			output[top++] = Util.hexcodes[data[i] & 0xf];
		}
		return (new String(output).trim());
	}

	public static byte[] getInviteByteArray(SccpUserAddress userAddr) {
		NStateReqEvent event = new NStateReqEvent(new Object()); 
		event.setAffectedUser(userAddr);
		event.setUserStatus(1);

		byte[] encode = null;
		try {
			encode = TcapParser.encodeSCCPMgmtMsg(event, 11);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("MandatoryParameterNotSetException encoding invite sccp message",e);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException encoding invite sccp message",e);
		}

		if(encode ==null){
			if(logger.isDebugEnabled())
				logger.debug("Encoded msg null returning default msg");
			return inviteSccpMsg;
		}
		//pv
		inviteSccpMsg[8] = encode[10];
		//pc
		inviteSccpMsg[10] = encode[14];
		inviteSccpMsg[11] = encode[15];
		inviteSccpMsg[12] = encode[16];
		inviteSccpMsg[13] = encode[17];
		//ssn
		inviteSccpMsg[15] = encode[12];

		if(logger.isDebugEnabled())
			logger.debug("Got byte array for INVIte::"+com.genband.tcap.parser.Util.formatBytes(inviteSccpMsg));
		return inviteSccpMsg;
	}

	public static byte[] getInfoByteArray(SccpUserAddress affectedUser, SccpUserAddress ownAddress, boolean isActive) {

		//affected user
		NStateReqEvent event1 = new NStateReqEvent(new Object()); 
		event1.setAffectedUser(affectedUser);
		event1.setUserStatus(1);
		byte[] encode = null;
		try {
			encode = TcapParser.encodeSCCPMgmtMsg(event1, 11);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("MandatoryParameterNotSetException encoding info sccp message",e);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException encoding info sccp message",e);
		}

		if(encode ==null){
			if(logger.isDebugEnabled())
				logger.debug("Encoded msg null returning default msg");
			return infoSccpMsg;
		}
		//pv
		infoSccpMsg[10] = encode[10];
		//pc
		infoSccpMsg[12] = encode[14];
		infoSccpMsg[13] = encode[15];
		infoSccpMsg[14] = encode[16];
		infoSccpMsg[15] = encode[17];
		//ssn
		infoSccpMsg[17] = encode[12];



		// own addresss
		NStateReqEvent event2 = new NStateReqEvent(new Object()); 
		event2.setAffectedUser(ownAddress);
		event2.setUserStatus(1);
		encode = null;
		try {
			encode = TcapParser.encodeSCCPMgmtMsg(event2, 11);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("MandatoryParameterNotSetException encoding info sccp message",e);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException encoding info sccp message",e);
		}

		if(encode ==null){
			if(logger.isDebugEnabled())
				logger.debug("Encoded msg null returning default msg");
			return infoSccpMsg;
		}
		//pv
		infoSccpMsg[23] = encode[10];
		//pc
		infoSccpMsg[25] = encode[14];
		infoSccpMsg[26] = encode[15];
		infoSccpMsg[27] = encode[16];
		infoSccpMsg[28] = encode[17];
		//ssn
		infoSccpMsg[30] = encode[12];

		//status
		if(isActive){
			if(logger.isDebugEnabled())
				logger.debug("active PC");
			infoSccpMsg[4] = (byte)0x01;
		}

		if(logger.isDebugEnabled())
			logger.debug("Got byte array for INVIte::"+com.genband.tcap.parser.Util.formatBytes(infoSccpMsg));
		return infoSccpMsg;
	}

	private static byte[] inviteSccpMsg ={(byte)0x35,(byte)0x0e,(byte)0x0d,(byte)0x04,(byte)0x0b,(byte)0x06,(byte)0x01,
		(byte)0x03,(byte)0x01,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x07,(byte)0x92, 
		(byte)0x0d, (byte)0x0a};

	private static byte[] infoSccpMsg ={(byte)0x34,(byte)0x1d,(byte)0x0b,(byte)0x33, (byte)0x00, (byte)0x04,(byte)0x0b,
		(byte)0x06,(byte)0x01,(byte)0x03,(byte)0x01,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,
		(byte)0x07,(byte)0x92,(byte)0x05,(byte)0x0b,(byte)0x06,(byte)0x01,(byte)0x03,(byte)0x01,(byte)0x08,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x07,(byte)0x92, (byte)0x0d, (byte)0x0a};

	public static int systemCall(String command) {
		//not initializing with 0 as it is the value returned on normal termination
		int terminationVal = -1;
		
		String s = System.getProperty("os.name");
		String [] val = s.split(" ");
		
		logger.debug("os is >> " + val[0]);
		
		String [] commandToExecute = {"cmd","/c",command};
				
		Runtime r = Runtime.getRuntime();
		        try {
		              /*
		               * Here we are executing the command we are getting as an argument 
		               */
		        	  Process p;
		        	  if(val[0].equalsIgnoreCase("windows"))
		        		   p = r.exec(commandToExecute);
		        	  else
		        		   p = r.exec(command);
		              		        	  
		              // Check for command failure
		              try {
		            	  terminationVal = p.waitFor();
		                  if (terminationVal != 0) {
		                      logger.debug("exit value = " + p.exitValue());
		                  }
		              } catch (InterruptedException e) {
		                  logger.debug(e);
		              } 
		          } catch (IOException e) {
		              logger.debug(e.getMessage());
		          }
		return terminationVal;
	}
}
