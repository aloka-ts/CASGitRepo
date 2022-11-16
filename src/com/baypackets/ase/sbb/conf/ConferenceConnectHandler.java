package com.baypackets.ase.sbb.conf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.container.SasApplicationSession;

import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsConferenceSpec;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;

import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.timer.TimerInfo;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.util.AseTimerInfo;

public class ConferenceConnectHandler extends BasicSBBOperation {


         /** Logger element */
    private static Logger logger = Logger.getLogger(ConferenceConnectHandler.class.getName());
    private static final long serialVersionUID = -39635624543333637L;
        
        private transient Address mediaServerAddr = null;
        private transient SipServletRequest requestOut = null;
        private transient MediaServer mediaServer = null;
        private transient MsConferenceSpec msConferenceSpec = null;

        /**
         * Public Default Constructor used for Externalizing this Object
         */
        public ConferenceConnectHandler() {
                super();
        }

        public ConferenceConnectHandler(Address addrB, MsConferenceSpec spec){
                msConferenceSpec = spec;
                mediaServerAddr = addrB;
        }

        public void setMediaServer(MediaServer ms) {
                this.mediaServer = ms;
        }



        public void start() throws ProcessMessageException {

		if(logger.isDebugEnabled())
	                logger.debug("<SBB> Entered start() ");

                ConferenceControllerImpl sbb = (ConferenceControllerImpl)getOperationContext();
                // Connecting with media server by sending INVITE
                SipFactory factory =
                (SipFactory)sbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
                // TODO: remove user and domain name hard coding
        Address from = factory.createAddress(factory.createSipURI("sas","bay.com"));
                SipApplicationSession appSession = sbb.getApplicationSession();
        requestOut = factory.createRequest(appSession,"INVITE",from,mediaServerAddr);

                //Creating and registering the Conference Info with the Conference Registry.
                ConferenceInfoImpl tempInfo = new ConferenceInfoImpl();
                tempInfo.setConferenceId(sbb.getConferenceId());
                tempInfo.setApplicationSessionId(((SasApplicationSession)sbb.getApplicationSession()).getAppSessionId());
                tempInfo.setHostName(requestOut.getLocalAddr());
                tempInfo.setPort(requestOut.getLocalPort());
                sbb.setConferenceInfo(tempInfo);
                
                try {
                        // set the SDP for control message.
                MsAdaptor msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
                msAdaptor.generateControlMessage(requestOut);
			if(logger.isInfoEnabled())
	                        logger.info("<SBB> Sending INVITE request to media server:: "+requestOut);
			
			            // add media server as B party
                        sbb.addB(requestOut.getSession());
                        
                        sendRequest(requestOut);
                        
                        //string INVITE sdp for session refresh mechanism
                        
                        requestOut.getSession().setAttribute(Constants.CONTROLLER_SDP_CONTENT, requestOut.getContent());
                        requestOut.getSession().setAttribute(Constants.CONTROLLER_SDP_CONTENT_TYPE, requestOut.getContentType());
                        
                      

                }
                catch(UnsupportedEncodingException exp) {
                        logger.error(exp.getMessage(),exp);
                        throw new ProcessMessageException(exp.getMessage());
                }
                catch (IOException exp) {
                        logger.error(exp.getMessage(),exp);
                        throw new ProcessMessageException(exp.getMessage());
                }
                catch(MediaServerException exp) {
                        logger.error(exp.getMessage(),exp);
            throw new ProcessMessageException(exp.getMessage());

                }
		if(logger.isDebugEnabled())
                	logger.debug("<SBB> Exited start() ");
        }



    /**
     * This method  handles all response from media server.
     * @response - Response from media-server.
     */
    public void handleResponse(SipServletResponse response) {
		if(logger.isDebugEnabled())
	                logger.debug("<SBB> entered handleResponse with <"+
                response.getStatus()+","+response.getMethod()+">");
                if (! response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {
                        logger.error("<SBB> Error: Non-INVITE method "); 
                        return;
                }

                // 100 Trying
                if (SBBResponseUtil.isProvisionalResponse(response)) {
		if(logger.isInfoEnabled())
                	logger.info("<SBB>Received 100 Trying from media server, no-op");
        }

                // 2xx response
                else if (SBBResponseUtil.is2xxFinalResponse(response)) {
		if(logger.isInfoEnabled()){
	                logger.info("<SBB> Received 2xx response from media server");
        	        logger.info("<SBB> Sending ACK to media server");
		}
                        try {
                                sendRequest(response.createAck());
				if(logger.isInfoEnabled())
                                logger.info("<SBB> Connect media server operation completed");
                                setCompleted(true);
				if(logger.isInfoEnabled())
                                logger.info("<SBB> Media server connected successfully");
                                
                                //handling for session refresh
                                
                                  this.startSessionExpiryTimer(response, Constants.TIMER_FOR_MS, Constants.SESSION_EXPIRY_TIMER_FOR_MS);
                                                             
                                // extracting party B SDP and storing in session
                                        int contentLength = 0 ;
                                        contentLength = response.getContentLength();
					if(logger.isInfoEnabled())
        	                                logger.info("[.....]setting sdp for response"+contentLength);
                                        if (contentLength >  0) {
                                                this.setContentInSession(response);
                                        }
                                        else {
                                               	if(logger.isInfoEnabled())
							logger.info("<SBB> No content associated with this request/response");
                                        }       
                                  
                                
                                // Now create a conference in media server
                                MsConfSpec confSpec = new MsConfSpec();
                                confSpec.setId(msConferenceSpec.getConferenceId());
                                confSpec.setOperation(MsConfSpec.OP_CODE_CREATE_CONF);
                                confSpec.setDeleteConfFlag(msConferenceSpec.getDeleteConfFlag());
                                confSpec.setMaxActiveSpeakers(msConferenceSpec.getMaxActiveSpeakers());
                                confSpec.setNotifyActiveSpeaker(msConferenceSpec.isNotifyActiveSpeaker());
                                confSpec.setNotificationInterval(msConferenceSpec.getNotificationInterval());
                                confSpec.setActiveSpeakerThreashold(msConferenceSpec.getActiveSpeakerThreashold());
                                confSpec.setAudiomixId(msConferenceSpec.getAudiomixId());
                                confSpec.setAudiomixSampleRate(msConferenceSpec.getAudiomixSampleRate());
                                //9114
                                confSpec.setConferenceType(msConferenceSpec.getConferenceType());
                                confSpec.setMsVideoConferenceSpec(msConferenceSpec.getMsVideoConferenceSpec());
                                confSpec.setTerm(msConferenceSpec.isTerm());
                                confSpec.setMark(msConferenceSpec.getMark());
                                ConferenceCommandHandler confCommandHandler = new ConferenceCommandHandler(confSpec);
                                SBBOperationContext operCtx = getOperationContext();
                                operCtx.addSBBOperation(confCommandHandler);
                                confCommandHandler.start();
                        }
                        catch(IOException exp) {
                                logger.error("<SBB> Couldn't send ACK to media server",exp);
                        }
                        catch(ProcessMessageException exp) {
                                logger.error("<SBB> Couldn't create the conference",exp);
                        }
                }

                // Non-2xx response
                else {
                        if(logger.isInfoEnabled())
				logger.info("<SBB> Received Non-2xx final response from media server");
                        setCompleted(true);
                        fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE,
                                response);
                        if(logger.isInfoEnabled())
				logger.info("<SBB> Fired <CONNECT_FAILED> event to application");

                }

                                if(logger.isDebugEnabled())
					logger.debug("<SBB> exited handleResponse");
    }


        public void handleRequest(SipServletRequest request) {
            if(logger.isDebugEnabled())
		logger.debug("<SBB> entered handleRequest with <"+
                request.getMethod()+">");
                
                if(!request.isInitial()){
                        
                        Object sdpInvite;
                        try {
                                sdpInvite = request.getContent();
                                
                                Object oldMSSDP=request.getSession().getAttribute(Constants.CONTROLLER_SDP_CONTENT);
                                if(sdpInvite.equals(oldMSSDP)){
                                        //cancelling the timer
                                        cancelSessionRefreshTimer(request.getApplicationSession());
                                        
                                        String sesionExpHeader=request.getHeader(Constants.HDR_SESSION_EXPIRES);
                                        //again starting the timer
                                        String refreshingTime=sesionExpHeader.split(";")[0];
                                        int deltaTime=0;
                                        if(refreshingTime!=null)
                                        {
                                                deltaTime=Integer.parseInt(refreshingTime.trim());
                                                
                                        }
                                        
                                        SipServletResponse response=request.createResponse(200);
                                        response.setContent(request.getSession().getAttribute(Constants.CONTROLLER_SDP_CONTENT),(String)request.getSession().getAttribute(Constants.CONTROLLER_SDP_CONTENT_TYPE));
                                        response.send();
                                        if(deltaTime>0){
                                                startSessionRefreshTimer(request.getApplicationSession(), deltaTime);
                                        }
                                        
                                }
                        } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        
                
                }
                
                logger.error("<SBB> Invalid call to handleRequest");
             if(logger.isDebugEnabled())   
                logger.debug("<SBB> Exiteed handleRquest");

        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
                super.readExternal(in);
        }

        public void writeExternal(ObjectOutput out) throws IOException {
                super.writeExternal(out);
        }
        
        /**
         * This method will start session refresh timer for given time.
         * @param appSession SipApplicationSession for which timer will be started
         * @param timerLegId SipApplicationSession attribute name for setting timer id.
         * @param durationSeconds session expire duration in seconds for session refresh
         */
        public void startSessionRefreshTimer(SipApplicationSession appSession,int deltaSeconds){
                if(logger.isDebugEnabled())
                        logger.debug("Inside startSessionRefreshTimer() for deltaSeconds:"+deltaSeconds);
                if(appSession!=null ){
                        SBB sbb = (SBB)getOperationContext();
                        TimerService ts=(TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
                        long timerDuration = deltaSeconds*1000;
                        ServletTimer timer=ts.createTimer(appSession, timerDuration, true, Constants.SESSION_EXPIRY_TIMER_FOR_MS);
                        appSession.setAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS, timer.getId());
                        if(logger.isDebugEnabled())
                                logger.debug("startSessionRefreshTimer(): timer started with duration[MS]:"+timerDuration);
                }else{
                        if(logger.isDebugEnabled())
                                logger.debug("startSessionRefreshTimer() called with NULL values so not doing anything");
                }
        }
        
        /**
         * This method will cancel session refresh timer for given timer leg id.
         * @param appSession SipApplicationSession for which timer will be canceled
         * @param timerLegId SipApplicationSession attribute name for getting timer id.
         */
        public void cancelSessionRefreshTimer(SipApplicationSession appSession){
                if(logger.isDebugEnabled())
                        logger.debug("Inside cancelSessionRefreshTimer()");
                if(appSession!=null ){
                        String timerId=(String) appSession.getAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
                        if(timerId!=null){
                                ServletTimer timer=appSession.getTimer(timerId);
                                if(timer!=null){
                                        timer.cancel();
                                        appSession.removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
                                }else{
                                        if(logger.isDebugEnabled())
                                                logger.debug("Timer id present but Timer not found:");
                                }
                        }else{
                                logger.debug("Timer not found:");
                        }
                }else{
                        if(logger.isDebugEnabled())
                                logger.debug("cancelSessionRefreshTimer() called with NULL values so not doing anything");
                }
        }
        
        /**
     * Sub classes need to override this method.
     */
        public void timerExpired(ServletTimer timer) {
                if(logger.isDebugEnabled())
                        logger.debug("timerExpired() entered .....");

                SipApplicationSession appSession=timer.getApplicationSession();

                if(timer.getId().equals((String)appSession.getAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS))){
                        SBB sbb = (SBB)getOperationContext();
                        ConferenceControllerImpl impl=(ConferenceControllerImpl)sbb;
                        try{
                                impl.disconnectMediaServer();
                        }catch (Exception e) {
                                logger.error("Exception in disconnectMediaServer()",e);
                        }
                }

                if(logger.isDebugEnabled())
                        logger.debug("timerExpired() exitting ....");
        }
        
        protected void startSessionExpiryTimer(SipServletMessage msg,String servletTimer,String timerInSession){
                SBB sbb = (SBB)getOperationContext();
                if(msg!=null){
                        if(msg.getHeader("Session-Expires")!=null){
                                if(logger.isDebugEnabled()){
                                       logger.debug("SDP attributes are set in sip_session");
                                }
                                 
                                 if(logger.isDebugEnabled()){
                                       logger.debug("header vale of Session-Expires: "+msg.getHeader(Constants.HDR_SESSION_EXPIRES));
                                 }
                                String deltaSecondsUAS = getDeltaSeconds(msg.getHeader(Constants.HDR_SESSION_EXPIRES));
                                long timerDuration = Long.valueOf(deltaSecondsUAS);
                                timerDuration = timerDuration*1000;
                                TimerService ts=(TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
                                //TODO
                                /*TimerInfo timer = new TimerInfo(servletTimer,this);
                                this.getOperationContext().getSBB().
                                ServletTimer timer=ts.createTimer(requestIn.getApplicationSession(), timerDuration, false, "TIMERID1");*/
                                //TODO
                                //ServletTimer timer=ts.createTimer(requestIn.getApplicationSession(), timerDuration, true, new TimerInfo(servletTimer,this));
                                //--changes---
                                AseTimerInfo aseTimerInfo = new AseTimerInfo();
                                aseTimerInfo.setSbbName(sbb.getName());
                                ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration, true,aseTimerInfo);
                                TimerInfo timerInfo = new TimerInfo(servletTimer,this);
                                ((SBBImpl)sbb).setServletTimer(timer.getId(), timerInfo);                       
                                //--------------
                                msg.getSession().setAttribute(timerInSession, timer.getId());
                                msg.getSession().setAttribute("SESSION_EXPIRES", deltaSecondsUAS);
                                if(logger.isDebugEnabled()){
                                        logger.debug("timer created for "+timerInSession);
                                }
                                
                        }else{
                                if(logger.isDebugEnabled()){
                                    logger.debug("Session-Expires is null");
                                }
                        
                }
                }else{
                        String deltaSecondsUAS = (String) sbb.getB().getAttribute("SESSION_EXPIRES");
                        long timerDuration = Long.valueOf(deltaSecondsUAS);
                        timerDuration = timerDuration*1000;
                        
                        TimerService ts=(TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
                        AseTimerInfo aseTimerInfo = new AseTimerInfo();
                        aseTimerInfo.setSbbName(sbb.getName());
                        ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration, true, aseTimerInfo);
                        
                        TimerInfo timerInfo = new TimerInfo(servletTimer,this);
                        ((SBBImpl)sbb).setServletTimer(timer.getId(), timerInfo);       
                        //sbb.getApplicationSession().setAttribute(timerInSession, timer);
                        sbb.getB().setAttribute(timerInSession, timer.getId());
                        if(logger.isDebugEnabled()){
                                logger.debug("timer created for "+timerInSession+"and delta sec is "+deltaSecondsUAS);
                        }
                }
        }
        
        //ravi----------------
        protected String getDeltaSeconds(String sessionExpHeadVal){
                int deltaSecIndex = sessionExpHeadVal.indexOf(";", 0);
                String deltaSeconds = null;
                if (deltaSecIndex != -1){
                        deltaSeconds = sessionExpHeadVal.substring(0, deltaSecIndex);
                }else{
                        deltaSeconds = sessionExpHeadVal;
                }
                if(logger.isDebugEnabled()){
                   logger.debug("delta Second:"+deltaSeconds);
                }
                return deltaSeconds.trim();
        }
        
        //in case of sdp with multipart, 1st parse sdp then set in session  
        private void setContentInSession(SipServletResponse response){
                int contentLength = 0 ;
                String contentType = null ;
                contentLength = response.getContentLength();
                if(logger.isInfoEnabled())
			logger.info("[.....]setting sdp for response"+contentLength);
                contentType=response.getContentType();
                if (contentLength >  0) {       
                        try{
                                if(contentType.startsWith("multipart/")){
                                        int sdpBpIndx=-1;
                                        MimeMultipart multipart = null ;
                                        multipart=(MimeMultipart)response.getContent();
                                        int count=multipart.getCount();
                                        for(int i=0;i<count;i++){
                                                if(multipart.getBodyPart(i).getContentType().startsWith("application/sdp")){
                                                        sdpBpIndx=i;
                                                        break;
                                                }
                                        }
                                        if(sdpBpIndx!=-1){
                                                byte[] bp;
                                            ByteArrayInputStream bis=(ByteArrayInputStream) multipart.getBodyPart(sdpBpIndx).getContent(); 
                                            int bytes=bis.available();
                                            bp=new byte[bytes];
                                            bis.read(bp,0,bytes);
                                            
                                            
                                                response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,bp);
                                                response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
                                                                                                                   multipart.getBodyPart(sdpBpIndx).getContentType());
                                            
                                        }else {
                                                if(logger.isInfoEnabled())
							logger.info("<SBB> No SDP content associated with  multipart "+response.getStatus()+" response");
                                        }
                                        
                                }else{
                                        
                                        response.getSession().setAttribute(
                                                        SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
                                        response.getSession().setAttribute(
                                                        SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
                                        
                                }
                        }catch(MessagingException e){
                                //this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
                                if(logger.isInfoEnabled())
					logger.info("Exception in saving SDP "+e);
                        }catch(Exception e){
                                //this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
                                if(logger.isInfoEnabled())
					logger.info("Exception in saving SDP "+e);
                        }
                }
        }
        
        public void postSessionExpiry(String timerType){

        	SBB sbb = (SBB)this.getOperationContext();
        	//sbb.getApplicationSession().setAttribute(Constants.SESSION_EXPIRED_OF, timerType);
        	/*this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
                this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);*/

        	if(timerType.equals(Constants.TIMER_FOR_MS)){
        		if(logger.isDebugEnabled()){
        			logger.debug("session-expires timer timeout for ms");
        		}
        		sbb.getB().removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
        		if(logger.isDebugEnabled()){
        			logger.debug("attribute removed from sip session");
        		}
        		this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
        		sbb.getB().setAttribute(Constants.SESSION_EXPIRED_OF, Constants.TIMER_FOR_MS);
        		SipServletRequest bByeOut = sbb.getB().createRequest("BYE");
        		try{
        			this.sendRequest(bByeOut);
        			if(logger.isDebugEnabled()){
        				logger.debug("bye sends to ms.....");
        			}
        		}catch(IOException e){
        			logger.error(e.getMessage(),e);
        		}
        	}

        }

        private boolean cancelSessionExpiryTimer(String str){
        	SBB sbb = (SBB)this.getOperationContext();
        	//ServletTimer timer = (ServletTimer) sbb.getApplicationSession().getAttribute(str);
        	ServletTimer timer = null;
        	if(str.equals(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY)){
        		String timerId=(String ) sbb.getA().getAttribute(str);
        		timer =  sbb.getA().getApplicationSession().getTimer(timerId);
        	}else{
        		String timerId=(String ) sbb.getB().getAttribute(str);
        		timer =  sbb.getB().getApplicationSession().getTimer(timerId);
        	}
        	if(timer!=null){
        		timer.cancel();
        		if(logger.isDebugEnabled()){
        			logger.debug("session Expires timer cancel "+str);
        		}
        		if(str.equals(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY)){
        			sbb.getA().removeAttribute(str);
        			logger.debug("session expires timer attribute remove for A-party");
        		}else{
        			sbb.getB().removeAttribute(str);
        			logger.debug("session expires timer attribute remove for B-party");
        		}
        		return true;
        	}
        	return false;
        }
}
