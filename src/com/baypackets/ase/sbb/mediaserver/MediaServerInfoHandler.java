package com.baypackets.ase.sbb.mediaserver;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.msadaptor.convedia.MsmlResult;
import com.baypackets.ase.sbb.*;
import com.baypackets.ase.sbb.b2b.NetworkMessageHandler;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.mediaserverstatistics.MediaServerStatisticsManager;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.*;
import javax.servlet.sip.SipSession.State;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

@DefaultSerializer(ExternalizableSerializer.class)
public class MediaServerInfoHandler extends BasicSBBOperation implements Serializable {
	
	private static final long serialVersionUID = -3243808471478354703L;
	private static final Logger logger = Logger.getLogger(MediaServerInfoHandler.class);
	public static final int MS_OPERATION_PLAY = 1;
	public static final int MS_OPERATION_PLAY_COLLECT = 2;
	public static final int MS_OPERATION_PLAY_RECORD = 3;
	public static final int MS_OPERATION_RECORD = 11;
	public static final int MS_OPERATION_PLAY_VXML = 4;	
	public static final int MS_OPERATION_STOP_RECORD = 5;
	public static final int MS_OPERATION_AUDIT = 6;
	public static final int MS_OPERATION_DIALOG_END_PLAY=7;	
	public static final int MS_OPERATION_DIALOG_END_PLAY_COLLECT=8;
	public static final int MS_OPERATION_DIALOG_END_PLAY_RECORD=9;
	public static final int MS_OPERATION_DIALOG_END_RECORD=10;
	
	public static final String MSML_DIALOG_EXIT="msml.dialog.exit";
	public static final String RECORD_END_ATTRIB="record.end";
	public static final String RECORD_TERMINATE_VALUE="record.terminate";
	
	public static HashMap<Integer,String> MS_OPERATION_STRING = new HashMap<Integer,String>();
	static{
		MS_OPERATION_STRING.put(1, "PLAY");
		MS_OPERATION_STRING.put(2, "PLAY_COLLECT");
		MS_OPERATION_STRING.put(3, "PLAY_RECORD");
		MS_OPERATION_STRING.put(11, "RECORD");
	}
	
	private int operation = -1;
	private transient MsOperationSpec operationSpec = null;
	private MsCollectSpec collectSpec=null;

	private boolean responseReceived = false;
	private boolean requestReceived = false;

	private transient ServletTimer timer = null;
	private transient MsSessionControllerImpl msSession = null;
	private transient URL vxml =null;
	private MsRecordSpec recordSpec;

	private static ConfigRepository m_configRepository  = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
	private static String mediaStatsEnable = (String) m_configRepository.getValue(com.baypackets.ase.util.Constants.MEDIA_STATS_DB_STORE_ENABLE);
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public MediaServerInfoHandler() {
		super();
	}

	public MediaServerInfoHandler(MsSessionControllerImpl msSession, int operation, MsOperationSpec operationSpec) {
		this.operation = operation;
		this.operationSpec = operationSpec;
		this.msSession = msSession;
		if(logger.isDebugEnabled())
			logger.debug("Inside constructor of MediaServerInfoHandler with opertaion:"+this.operation);
	}
	public MediaServerInfoHandler(MsSessionControllerImpl msSession, int operation, URL vxml) {
		this.operation = operation;
		this.msSession = msSession;
		this.vxml =vxml;
	}


	public synchronized void handleRequest(SipServletRequest request) {
		boolean loggerEnabled = logger.isDebugEnabled();

		this.requestReceived = true;

		//Cancel the timer if NOT NULL.
		if(this.timer != null){
			this.timer.cancel();
			this.timer=null;
		}

		try {
			if (loggerEnabled) {
				logger.debug("handleRequest(): Sending 200 response to media server...");
			}
			SipServletResponse response = request.createResponse(200);
			this.sendResponse(response, false);
		} catch (Exception e) {
			String msg = "Error occurred while sending response to media server: " + e.getMessage();
			logger.error(msg, e);
		}

		//MsOperationResult result = (MsOperationResult)request.getAttribute(Constants.MS_RESULT);
		MsOperationResult result = (MsOperationResult)((AseSipServletRequest)request).getMsResult();
		boolean success = result.isSuccessfull();

		//for Media Server Stats
		if(mediaStatsEnable.equals(AseStrings.TRUE_SMALL)){
			try{
				String playDurationString = (String)result.getAttribute(result.PLAY_DURATION);
				Float playDuration = 0f;
				if(playDurationString != null && !playDurationString.trim().equals(AseStrings.BLANK_STRING)){
					playDuration = Float.parseFloat(playDurationString);	
				}
				MediaServerStatisticsManager mediaServerStatisticsManager = MediaServerStatisticsManager.getInstance();
				mediaServerStatisticsManager.setStaticsInfo((this.operationSpec),playDuration,null);
			}catch (Exception e) {
				logger.error("Exception handleRequestwhile setting statistics info : "+e.getMessage());
			}

		}
		//END

		if (loggerEnabled) {
			if (success) {
				logger.debug("handleRequest(): Media server returned a successfull dialog result.");
			} else {
				logger.debug("handleRequest(): Media server returned an un-successful dialog result.");
			}
		}

		
		if(AseStrings.TRUE_CAPS.equals(this.getMsSBB().getAttribute(
				com.baypackets.ase.sbb.util.Constants.ATTRIBUTE_OPERATION_STOPPED))){
			this.getMsSBB().setAttribute(
					com.baypackets.ase.sbb.util.Constants.ATTRIBUTE_OPERATION_STOPPED,null);
			
			if(logger.isDebugEnabled()){
				logger.debug("handleRequest() set reason as stopped ");
				}
				
			result.setAttribute("reason" ,"stopped");
			result.setAttribute(MsOperationResult.COLLECTED_DIGITS,"");
		}
		
		this.getMsSBB().setResult(result);

		SBBEvent event = new MsEvent(result);
		String dialogEvent=(String)result.getAttribute(MsOperationResult.MSML_EVENT_NAME);
		
	
		if(logger.isDebugEnabled()){
		logger.debug("handleRequest() Current Object is:"+this+" Operation is:"+this.operation+" DIALOG_EVENT is:"+dialogEvent);
		}
		
		if (dialogEvent != null && !dialogEvent.equals(MSML_DIALOG_EXIT)) {
			
			/**
			 * on stopping a recording done event is received with attribute record.end=record.terminate so operation here will be 5 but done veent will be for
			 * record playing so needed nelow check here
			 */
			if (operation == MS_OPERATION_STOP_RECORD) {
				String recordEnd = (String) result.getAttribute(RECORD_END_ATTRIB);

				if (recordEnd != null && recordEnd.equals(RECORD_TERMINATE_VALUE)) {
					event.setEventId("PLAY_RECORD_" + dialogEvent.toUpperCase());
				}
			} else {
				event.setEventId(MS_OPERATION_STRING.get(this.operation) + "_"
						+ dialogEvent.toUpperCase());
			}
			this.getMsSBB().fireEvent(event);	
		}
		else{
		switch (this.operation){
		case MS_OPERATION_PLAY:
			if(success){
				event.setEventId(MsSessionController.EVENT_PLAY_COMPLETED);
			}else{
				event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
			}
			break;
		case MS_OPERATION_PLAY_COLLECT:
			if(success){
				
				String timerId = (String) this.getMsSBB().getAttribute(
						Constants.ATTRIBUTE_OPERATION_STOPPED);
				
					if (timerId != null) {

						if (logger.isDebugEnabled()) {
							logger.debug("Play collect operation  was set to be stopped on timer :"
									+ timerId);
						}

						ServletTimer timer = request.getApplicationSession()
								.getTimer(timerId);

						if (timer != null) {

							if (logger.isDebugEnabled()) {
								logger.debug("cancel timer started for stopping play collect operation");
							}
							timer.cancel();
							
							this.getMsSBB().setAttribute(
									Constants.ATTRIBUTE_OPERATION_STOPPED,null);
						}

						event.setEventId(MsSessionController.EVENT_STOP_RECORD_COMPLETED);

					} else {
						event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_COMPLETED);
					}
			}else{
				event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
			}
			break;
		case MS_OPERATION_PLAY_RECORD:
			if (success) {
				String timerId = (String) this.getMsSBB().getAttribute(
						Constants.ATTRIBUTE_OPERATION_STOPPED);
				
					if (timerId != null) {

						if (logger.isDebugEnabled()) {
							logger.debug("Play record operation  was set to be stopped on timer :"
									+ timerId);
						}

						ServletTimer timer = request.getApplicationSession()
								.getTimer(timerId);

						if (timer != null) {

							if (logger.isDebugEnabled()) {
								logger.debug("cancel timer started for stopping play record operation");
							}
							timer.cancel();
							
							this.getMsSBB().setAttribute(
									Constants.ATTRIBUTE_OPERATION_STOPPED,null);
						}

						event.setEventId(MsSessionController.EVENT_STOP_RECORD_COMPLETED);

					} else {
					    event.setEventId(MsSessionController.EVENT_PLAY_RECORD_COMPLETED);
				    }
			} else {
				event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
			}
			break;
			
		case MS_OPERATION_RECORD:
			if(success){
				event.setEventId(MsSessionController.EVENT_RECORD_COMPLETED);
			}else{
				event.setEventId(MsSessionController.EVENT_RECORD_FAILED);
			}
			break;
		case MS_OPERATION_STOP_RECORD:
			
			  if(success){
				event.setEventId(MsSessionController.EVENT_STOP_RECORD_COMPLETED);
				//sends re-invite to ms for sdp update
				Object prevHandler = ((GroupedMsSessionControllerImpl)(this.getOperationContext().getSBB())).getPrevHandler();
				if(prevHandler!=null && prevHandler instanceof NetworkMessageHandler){
					((NetworkMessageHandler)prevHandler).sendReInviteRequest();
					 ((GroupedMsSessionControllerImpl)(this.getOperationContext().getSBB())).removePrevHandler();
					break;
				}
			}else{
				event.setEventId(MsSessionController.EVENT_STOP_RECORD_FAILED);
			}

			if (null == this.getOperationContext().getSBB().getAttribute(MsSessionController.STOP_ANNOUNCEMENT)){
				try {
					this.getMsSBB().disconnectMediaServer();
				} catch (Exception e) {
					logger.error("error in disconnecting the media server " + e);
				}
			}else{
				this.getOperationContext().getSBB().setAttribute(MsSessionController.STOP_ANNOUNCEMENT,null);
			}
			break;	
		}

		this.checkCompleted(request);
		
		if (operation == MS_OPERATION_STOP_RECORD && isCompleted() != true) {
			
			if (logger.isDebugEnabled()) {

				logger.debug(" Stop operation has not completed yet so not firing event to apps..!!!");
			}
		} else {
			this.getMsSBB().fireEvent(event);
		}
		}				
	}

	public synchronized void handleResponse(SipServletResponse response) {
		this.responseReceived = true;
		int responseCode = response.getStatus();
		logger.debug("handleResponse() Current Object is:"+this+" Current Operation is:"+this.operation);
	
		SBB sbb = (SBB)getOperationContext();
		MsAdaptor msAdaptor = ((MsSessionControllerImpl)sbb).getMsAdaptor();
		MsSessionControllerImpl msSessionControllerImpl = (MsSessionControllerImpl)sbb;
		
		if(responseCode >= 300 && responseCode <=700) {
			SipServletRequest request = response.getRequest();
//			MsOperationResult result = (MsOperationResult)request.getAttribute(Constants.MS_RESULT);
			MsOperationResult result = (MsOperationResult)((AseSipServletRequest)request).getMsResult();
			SBBEvent event = new MsEvent(result);
			String reason = "RECEIVED "+responseCode+" RESPONSE:";
			event.setReasonCode(SBBEvent.REASON_CODE_ERROR_RESPONSE);
			event.setReason(reason);
			event.setMessage(response);			
                       	msSessionControllerImpl.setResult(result); 
			switch (this.operation){
			case MS_OPERATION_PLAY:
				event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
				break;
			case MS_OPERATION_PLAY_COLLECT:
				event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
				break;
			case MS_OPERATION_PLAY_RECORD:
				event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
				break;
			case MS_OPERATION_RECORD:
				event.setEventId(MsSessionController.EVENT_RECORD_FAILED);
				break;	
			case MS_OPERATION_STOP_RECORD:
				event.setEventId(MsSessionController.EVENT_STOP_RECORD_FAILED);
				break;
			case MS_OPERATION_AUDIT:
				event.setEventId(MsSessionController.EVENT_AUDIT_FAILED);
				break;
			case MS_OPERATION_DIALOG_END_PLAY:
				event.setEventId(MsSessionController.EVENT_END_PLAY_DIALOG_FAILED);
				break;
			case MS_OPERATION_DIALOG_END_RECORD:
				event.setEventId(MsSessionController.EVENT_END_RECORD_DIALOG_FAILED);
				break;	
			case MS_OPERATION_DIALOG_END_PLAY_COLLECT:
				event.setEventId(MsSessionController.EVENT_END_PLAY_COLLECT_DIALOG_FAILED);
				break;
			case MS_OPERATION_DIALOG_END_PLAY_RECORD:
				event.setEventId(MsSessionController.EVENT_END_PLAY_RECORD_DIALOG_FAILED);
				break;		
			}
			this.getMsSBB().fireEvent(event);
		}
		else {
			
		
			if(this.operation>=MS_OPERATION_AUDIT && this.operation<=MS_OPERATION_DIALOG_END_RECORD){
				//Cancel the timer if NOT NULL as we will not wait for INFO.
				if(this.timer != null){
					this.timer.cancel();
					logger.debug("Timer canceled for Object:"+this);
				}
			}
			
			try {
				MsOperationResult result = msAdaptor.parseMessage(response);
				if(result!=null){
					if ( ! result.isSuccessfull()) {
						SBBEvent event=new SBBEvent();
						switch (this.operation){
						case MS_OPERATION_PLAY:
							event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
							break;
						case MS_OPERATION_PLAY_COLLECT:
							event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
							break;
						case MS_OPERATION_PLAY_RECORD:
							event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
							break;
						case MS_OPERATION_RECORD:
							event.setEventId(MsSessionController.EVENT_RECORD_FAILED);
							break;	
						case MS_OPERATION_STOP_RECORD:
							event.setEventId(MsSessionController.EVENT_STOP_RECORD_FAILED);
							break;
						case MS_OPERATION_AUDIT:
							event.setEventId(MsSessionController.EVENT_AUDIT_FAILED);
							break;
						case MS_OPERATION_DIALOG_END_PLAY:
							event.setEventId(MsSessionController.EVENT_END_PLAY_DIALOG_FAILED);
							break;
						case MS_OPERATION_DIALOG_END_RECORD:
							event.setEventId(MsSessionController.EVENT_END_RECORD_DIALOG_FAILED);
							break;	
						case MS_OPERATION_DIALOG_END_PLAY_COLLECT:
							event.setEventId(MsSessionController.EVENT_END_PLAY_COLLECT_DIALOG_FAILED);
							break;
						case MS_OPERATION_DIALOG_END_PLAY_RECORD:
							event.setEventId(MsSessionController.EVENT_END_PLAY_RECORD_DIALOG_FAILED);
							break;	
						}

						event.setMessage(response);	
						msSessionControllerImpl.setResult(result);
						msSessionControllerImpl.fireEvent(event);

					}
					else{
						// result successful case
						if(this.operation>=MS_OPERATION_AUDIT && this.operation<=MS_OPERATION_DIALOG_END_RECORD){
							SBBEvent event=new SBBEvent();
							switch (this.operation){
							case MS_OPERATION_AUDIT:
								event.setEventId(MsSessionController.EVENT_AUDIT_COMPLETED);
								break;
							case MS_OPERATION_DIALOG_END_PLAY:
								event.setEventId(MsSessionController.EVENT_END_PLAY_DIALOG_COMPLETED);
								break;
							case MS_OPERATION_DIALOG_END_RECORD:
								event.setEventId(MsSessionController.EVENT_END_RECORD_DIALOG_COMPLETED);
								break;	
							case MS_OPERATION_DIALOG_END_PLAY_COLLECT:
								event.setEventId(MsSessionController.EVENT_END_PLAY_COLLECT_DIALOG_COMPLETED);
								break;
							case MS_OPERATION_DIALOG_END_PLAY_RECORD:
								event.setEventId(MsSessionController.EVENT_END_PLAY_RECORD_DIALOG_COMPLETED);
								break;	
							}					
							event.setMessage(response);
							msSessionControllerImpl.setResult(result);
							msSessionControllerImpl.fireEvent(event);

						}
					}
					// Set complted true if operation>=MS_OPERATION_AUDIT && operation<=MS_OPERATION_DIALOG_END_RECORD weather result success or fail
					if(this.operation>=MS_OPERATION_AUDIT && this.operation<=MS_OPERATION_DIALOG_END_RECORD){
						this.setCompleted(true);
					}
					
					
					if(this.operation == MS_OPERATION_PLAY){
						if(logger.isDebugEnabled()){
							logger.debug("checking for infinite announcement");
						}
						MsDialogSpec msDialogSpec = (MsDialogSpec) this.operationSpec;
						Iterator<Object> iterator = msDialogSpec.getSpecs();
						MsPlaySpec msPlaySpec = null;
						if(iterator.hasNext()){
							msPlaySpec = (MsPlaySpec) iterator.next();
						}
						
						if(logger.isDebugEnabled()){
							logger.debug("msPlaySpec :: " + msPlaySpec);
						}
						
						if(msPlaySpec.isInfiniteAnn()){
							if(logger.isDebugEnabled()){
								logger.debug("Announcement is being played infinitely, so firing event PLAY_IN_PROGRESS");
							}
						}
						SBBEvent event = new SBBEvent();
						event.setEventId(MsSessionController.EVENT_PLAY_IN_PROGRESS);
						event.setMessage(response);
						msSessionControllerImpl.setResult(result);
						msSessionControllerImpl.fireEvent(event);
					}
				}else{
					if(logger.isDebugEnabled()){
						logger.debug("200 ok received with no content.....");
					}
				}
				
				
				
				}catch(MediaServerException exp) {
					logger.error("<SBB> Error in parsing result from media server",exp);
				}
				
		}
		
		this.checkCompleted(response);
		
		/**
		 * handling done for improper seq of 200 ok for INFO request sent and INFO request received from MS issue came in call queuing feature with XMS
		 */
		if (this.operation == MS_OPERATION_STOP_RECORD && isCompleted() == true) {

			if (logger.isDebugEnabled()) {
				logger.debug("fire stop  completed to app as this operation is already completed i.e. INFO request has already receached before 200 ok response.....");
			}
			SBBEvent event = new SBBEvent();
			event.setEventId(MsSessionController.EVENT_STOP_RECORD_COMPLETED);
			event.setMessage(response);
			msSessionControllerImpl.fireEvent(event);
		}
	}


	private void checkCompleted(SipServletMessage message) {
		if ((message instanceof SipServletRequest && this.responseReceived) ||
				(message instanceof SipServletResponse && this.requestReceived)) {
			this.setCompleted(true);
		}	
	}


	public boolean isMatching(SipServletMessage message) {
		if(!message.getMethod().equals(AseStrings.INFO))
			return false;

		boolean matching = false;
		if(message instanceof SipServletRequest){
			try{
				SipServletRequest req = (SipServletRequest)message;
				//MsOperationResult result = (MsOperationResult)req.getAttribute(Constants.MS_RESULT);
				MsOperationResult result = (MsOperationResult)((AseSipServletRequest)req).getMsResult();
				MsAdaptor adaptor = this.getMsSBB().getMsAdaptor(); 
				if(result == null){
					result = adaptor.parseMessage(req, operationSpec);
				}
				if(result != null){
					//req.setAttribute(Constants.MS_RESULT, result);
					((AseSipServletRequest) req).setMsResult(result);
					String connectionId=this.operationSpec.getConnectionId();
					String id=this.operationSpec.getId();
					for (int i = 0; i < MsOperationResult.MSML_Dialog_Events.length; i++) {
						String eventName = MsOperationResult.MSML_Dialog_Events[i];
						matching = adaptor.isMatchingResult(eventName,connectionId,id,result);
						if (matching)
							break;
					}					
				}
			}catch(MediaServerException e){
				this.getMsSBB().getServletContext().log(e.getMessage(), e);	
			}
		}else {
			matching = super.isMatching(message);
		}
		return matching;
	}

	public void start() throws ProcessMessageException {
		
		if(logger.isDebugEnabled())
			logger.debug("Inside start() for object:"+this +"  MsSBB is "+ getMsSBB());
		
		if(!(getMsSBB() instanceof ConferenceController) && getMsSBB().getA() == null){
			throw new IllegalStateException("Party A is not associated with this SBB yet.");
		}

		if(getMsSBB().getB() == null){
			throw new IllegalStateException("Media Server is not associated with this SBB yet.");
		}

		if(getMsSBB().getA() != null){
			int state = ((Integer)getMsSBB().getA().getAttribute(
					Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			//We can do the media server operations either in the
			//CONFIRMED state or in the EARLY state.
			if(!(state == Constants.STATE_CONFIRMED ||
					state == Constants.STATE_EARLY || 
					((state == Constants.STATE_TERMINATED) && (this.operation == MS_OPERATION_STOP_RECORD)))){
				throw new IllegalStateException("Party A is not in a valid state to perform this operation.");
			}
		}

		if(getMsSBB().getB() != null){
			int state = ((Integer)getMsSBB().getB().getAttribute(
					Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			if(state != Constants.STATE_CONFIRMED){
				throw new IllegalStateException("Party B is not connected.");
			}
		}

		try{
			//if CPG is present, set CPG
			//this will be set by service 
	//		SipServletRequest request = (SipServletRequest) getMsSBB().getA().getAttribute(com.baypackets.ase.util.Constants.ORIG_REQUEST);
	//		State sipSessionState = request.getSession().getState();
			byte[] cpg_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.CPG_ISUP);	
			if(cpg_isup != null) {
				if(logger.isDebugEnabled()){
					logger.debug("cpg_isup attribute is not null");
				}
				//this will be set by service 
				SipServletRequest request = (SipServletRequest) getMsSBB().getA().getAttribute(com.baypackets.ase.util.Constants.ORIG_REQUEST);
				State sipSessionState = request.getSession().getState();
				//int dialogState = ((Integer)request.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();	
				if(sipSessionState == State.EARLY || sipSessionState == State.INITIAL){
					
					SipServletResponse responseOut = null ;
					responseOut = request.createResponse(183);
					logger.debug("Sending 18x with CPG before sending info::cpg");
					Multipart mp = new MimeMultipart();
					String contentType = getIsupContentTypeForA(request); 
					if (contentType == null)
						SBBResponseUtil.formMultiPartMessage(mp, cpg_isup,Constants.ISUP_CONTENT_TYPE,null);
					else
						SBBResponseUtil.formMultiPartMessage(mp, cpg_isup, contentType,null);
					
					this.getOperationContext().getSBB().setAttribute(MsSessionController.CPG_ISUP, null);
					
					responseOut.setContent(mp, mp.getContentType());
					request.getSession().setAttribute(Constants.CPG_PLAY_SEND, AseStrings.TRUE_SMALL);
					if(SBBResponseUtil.supports100Rel(request)){
						sendResponse(responseOut, true);
					}else{
						sendResponse(responseOut, false);
					}
					
					if(logger.isDebugEnabled()){
						logger.debug("state early, 183 reliable response send to A-party before sending INFO to ms::cpg");
					}
				}else{
					if(logger.isDebugEnabled()){
						logger.debug("dialog state is not early:: sending INFO without sending 183");
					}
					
				}
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("cpg_isup attribute is not set, sending INFO without sending 183");
				}
			}
			
			MsSessionControllerImpl  msController=getMsSBB();
			//chnages for level3 scenario in which play was connected in early media and there after playcollect should happen in late media
			 SipSession partyA=  msController.getA();
			if (partyA!=null && (partyA.getState() == State.EARLY
					|| partyA.getState() == State.INITIAL)) {

				if (logger.isDebugEnabled()) {
					logger.debug("currently leg A is connected in not in confirmed state and we are performing media operation check if late media is required here or not");
				}
				Object earlyMedia = msController.getAttribute(SBB.EARLY_MEDIA);

				if (earlyMedia != null && earlyMedia.equals(AseStrings.FALSE_SMALL)) {
					
					SipServletRequest request = (SipServletRequest) msController.getA().getAttribute(com.baypackets.ase.util.Constants.ORIG_REQUEST);
					if (logger.isDebugEnabled()) {
						logger.debug("late media is required on A leg, send 200 ok on A leg to bring it in confirmed state");
					}

					Object sdp = getMsSBB().getB().getAttribute(
							SBBOperationContext.ATTRIBUTE_SDP);
					Object sdpContentType = getMsSBB().getB().getAttribute(
							SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);

					SipServletResponse aSuccessResponse = request
							.createResponse(SipServletResponse.SC_OK);
					if (sdp != null && sdpContentType != null) {
						aSuccessResponse.setContent(sdp,
								(String) sdpContentType);
					}

					aSuccessResponse.send();
				}
			}
			
			 SipServletRequest request = msController.getB().createRequest(AseStrings.INFO);

			if(this.operation==MS_OPERATION_PLAY_VXML){

				SipURI  sipuri= (SipURI) request.getRequestURI();
				sipuri.setParameter("voicexml", this.vxml.toString());
			}else {

				MsAdaptor adaptor = msController.getMsAdaptor();
				adaptor.generateMessage(request, new MsOperationSpec[]{this.operationSpec});
			}   	

			if(((SBB)getOperationContext()).getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
				if(logger.isInfoEnabled())
					logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
				request.setAttribute("DISABLE_OUTBOUND_PROXY", AseStrings.BLANK_STRING);
			}
			this.sendRequest(request);

			//For Media Server Stats
			if(mediaStatsEnable.equals(AseStrings.TRUE_SMALL)){
				if(this.operation != MS_OPERATION_STOP_RECORD){
					try{
						// In case of playSubseqVoiceXmlWithINFO,this.operation would be MS_OPERATION_PLAY_VXML
						if(this.operation==MS_OPERATION_PLAY_VXML){
							SipURI uri =  (SipURI)request.getRequestURI();
							String voiceXmlPath  = uri.getParameter("voicexml");
							if(voiceXmlPath != null){
								//getting voiceXmlPath with encoding
								voiceXmlPath =  voiceXmlPath.substring(0, voiceXmlPath.indexOf(AseStrings.PERCENT));
								MediaServerStatisticsManager mediaServerStatisticsManager = MediaServerStatisticsManager.getInstance();
								//passing MsOperationSpec as null,play duration as zero and voiceXmlPath
								mediaServerStatisticsManager.setStaticsInfo(null,0,voiceXmlPath+",");
							}
						}else{
							// In case of play,playCollect
							MediaServerStatisticsManager mediaServerStatisticsManager = MediaServerStatisticsManager.getInstance();
							//passing MsOperationSpec,play duration as zero and voiceXmlPath as null
							mediaServerStatisticsManager.setStaticsInfo((this.operationSpec),0,null);
						}
					}catch (Exception e) {
						logger.error("Exception in start() while setting statistics info : "+e);
					}
				}
			}
			// END 

			long timeout = getMsSBB().getTimeout() * 1000; 

			if( timeout > 0){
				if (logger.isDebugEnabled()) {
					logger.debug("start(): Creating timer to fire in " + timeout + " seconds...");
				}
				TimerService timerService = (TimerService)msController.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
				this.timer = timerService.createTimer(msController.getB().getApplicationSession(), timeout, false, this);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ProcessMessageException(e.getMessage(), e);
		}
	}

	private MsSessionControllerImpl getMsSBB(){
		if(msSession == null){
			this.msSession = (MsSessionControllerImpl)
			this.getOperationContext().getSBB();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getMsSBB return..." +msSession);
		}
		return this.msSession;
	}

	public synchronized void timerExpired(ServletTimer timer){
		if (logger.isDebugEnabled()) {
			logger.debug("timerExpired() called...");
		}

		//If this operation is already completed, then no need to do anything.
		if(this.isCompleted())
			return;
		
		
		MsDialogSpec msDialogSpec = (MsDialogSpec) this.operationSpec;
		Iterator<Object> iterator = msDialogSpec.getSpecs();
		MsPlaySpec msPlaySpec = null;
		if(iterator.hasNext()){
			msPlaySpec = (MsPlaySpec) iterator.next();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("msPlaySpec :: " + msPlaySpec);
		}
		
		if(msPlaySpec.isInfiniteAnn()|| msPlaySpec.getIterations()>1){
			
		// Added check to avoid race condition where timer is expired at the time of call ended and B party session is NULL in MS-SBB
				if (this.getMsSBB().getMediaServer().getState() == ExternalDevice.STATE_ACTIVE){						
					if(this.getMsSBB().getB()!=null){
						if (logger.isDebugEnabled()) {
							logger.debug("timerExpired() called...media server is active so re creating the timer again");
						}
						long timeout = getMsSBB().getTimeout() * 1000;
						TimerService timerService = (TimerService)getMsSBB().getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
						this.timer = timerService.createTimer(getMsSBB().getB().getApplicationSession(), timeout, false, this);
					}else{
						if (logger.isDebugEnabled()) {
							logger.debug("timerExpired() called...B-Party is NULL so not creating timer again as call completed");
						}
					}
					return;
				}
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Announcement is being played finitely, so firing event failure as ms operation timer expired");
			}
		
		}
				
		SBBEvent event = new SBBEvent();

		switch (this.operation){
		case MS_OPERATION_PLAY:
			event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
			break;
		case MS_OPERATION_PLAY_COLLECT:
			event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
			break;
		case MS_OPERATION_PLAY_RECORD:
			event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
			break;
		case MS_OPERATION_RECORD:
			event.setEventId(MsSessionController.EVENT_RECORD_FAILED);
			break;	
		case MS_OPERATION_STOP_RECORD:
			event.setEventId(MsSessionController.EVENT_STOP_RECORD_FAILED);
			break;
		case MS_OPERATION_DIALOG_END_PLAY:
			event.setEventId(MsSessionController.EVENT_END_PLAY_DIALOG_FAILED);
			break;
		case MS_OPERATION_DIALOG_END_RECORD:
			event.setEventId(MsSessionController.EVENT_END_RECORD_DIALOG_FAILED);
			break;	
		case MS_OPERATION_DIALOG_END_PLAY_RECORD:			
			event.setEventId(MsSessionController.EVENT_END_PLAY_RECORD_DIALOG_FAILED);
			break;
		case MS_OPERATION_DIALOG_END_PLAY_COLLECT:
			event.setEventId(MsSessionController.EVENT_END_PLAY_COLLECT_DIALOG_FAILED);	
			break;				
		}

		if (logger.isDebugEnabled()) {
			logger.debug("timerExpired(): Firing an event of type: " + event.getEventId());
		}

		int action = this.getMsSBB().fireEvent(event);
		// Not DisConnecting: Its Apps responsibility to disConnect both MediaServer and Party A
		/*
		if (action == SBBEventListener.CONTINUE) {
			try {
				this.getMsSBB().disconnect();
			} catch (Exception e) {
				String msg = "Error occurred while disconnecting the media server session: " + e.getMessage();
				logger.error(msg, e);
				throw new RuntimeException(msg);
			}
		}
		 */
		this.setCompleted(true);
	}
   
	protected String getIsupContentTypeForA(SipServletRequest requestIn){
		String contentType = null; 
		try{
			if (requestIn.getContentType().startsWith(Constants.SDP_MULTIPART_MIXED)) {
				MimeMultipart mimeMultipart = (MimeMultipart) requestIn.getContent();
				for (int indx = 0; indx < mimeMultipart.getCount(); indx++) {
					BodyPart bodyPart = mimeMultipart.getBodyPart(indx);
					if (bodyPart.getContentType().startsWith(Constants.ISUP_CONTENT_TYPE)) {
						contentType = bodyPart.getContentType();
					}
				}
			}			
		}catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}catch (IOException e) {
			logger.error(e.getMessage(),e);
		}catch (MessagingException e) {
			logger.error(e.getMessage(),e);
		}
		return contentType;
	}
	
	//------------
	
	public int getOperation(){
		return this.operation;
	}
	
	public MsCollectSpec getCollectSpec() {
		return collectSpec;
	}

	public void setCollectSpec(MsCollectSpec collectSpec) {
		this.collectSpec = collectSpec;
	}


	public MsOperationSpec getOperationSpec() {
		return operationSpec;
	}

	public void setOperationSpec(MsOperationSpec operationSpec) {
		this.operationSpec = operationSpec;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.operation = in.readInt();
		this.operationSpec = (MsOperationSpec)in.readObject();
		this.requestReceived = in.readBoolean();
		this.responseReceived = in.readBoolean();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.operation);
		out.writeObject(this.operationSpec);
		out.writeBoolean(this.requestReceived);
		out.writeBoolean(this.responseReceived);
	}

	public void setRecordSpec(MsRecordSpec recordSpec) {
		this.recordSpec=recordSpec;	
	}
	
	public MsRecordSpec getRecordSpec() {
		return recordSpec;
	}
}
