/*
 * @(#)ConnectHandler.java	1.0 8 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
//import com.baypackets.ase.sbb.timer.TimerInfo;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;

import org.apache.log4j.Logger;

import com.baypackets.ase.ari.AriSipServletRequest;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.mediaserverstatistics.MediaServerStatisticsManager;
import com.baypackets.ase.sbb.timer.TimerInfo;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sipconnector.AseSipSessionState;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTimerInfo;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
/**
 * Implementation of the connect handler.
 * This class is responsible for handling connect operation. 
 * Connect operation handles the signalling level details 
 * for connecting two parties to each other into a back-to-back user
 * agent session. 
 */
public class ConnectHandler extends BasicSBBOperation implements java.io.Serializable {

	 /** Logger element */
    private static Logger logger = Logger.getLogger(ConnectHandler.class.getName());
    private static final long serialVersionUID= -324334297033147L;
	private boolean cancelRecvFromA = false;
    private transient Address bParty= null;
	private transient Address from = null;
	protected transient SipServletRequest requestIn = null;
	protected transient SipServletRequest requestOut = null;
	protected transient SipServletResponse response1xx = null;
	protected transient SipServletResponse response2xx = null;
	
	private transient String aPartyUpdateSDPContentType = null;
	private transient Object aPartyUpdateSDPContent = null;
	
	protected transient SipServletRequest ackToB = null ;
	
	private transient SipServletRequest update = null ; 
	private transient SipServletResponse responseToA = null ;
	
	private int isRTPTunnelingEnabled = 0;
	private boolean preconditionProvResSent = false;
	//private String minSEValue= BaseContext.getConfigRepository().getValue(Constants.MIN_SE_TIMEOUT);
	private boolean isSessionRefreshReInviteReceived = false;
	private boolean initInvWithoutSDP = false;
	private int dialogState;
	private transient SipSession peerSession;
	private byte[] rel_isup = {(byte)0x0c, (byte)0x02, (byte)0x00, (byte)0x02, (byte)0x83, (byte)0xbf};
	private static int releaseCause=Integer.parseInt(BaseContext.getConfigRepository().getValue(Constants.SESSION_EXPIRES_DEFAULT_ISUP_RELEASE_CAUSE));

	private static String DUMMY_SDP = "v=0\r\no=- 6 2 IN IP4 0.0.0.0\r\ns=CounterPath X-Lite 3.0\r\nc=IN IP4 0.0.0.0\r\nt=0 0\r\nm=audio 5092 RTP/AVP 107 119 100 106 0 105 98 8 101\r\na=alt:1 1 : j99lqLZN eSGTc2CA 10.32.4.184 5092\r\na=fmtp:101 0-15\r\na=rtpmap:107 BV32/16000\r\na=rtpmap:119 BV32-FEC/16000\r\na=rtpmap:100 SPEEX/16000\r\na=rtpmap:106 SPEEX-FEC/16000\r\na=rtpmap:105 SPEEX-FEC/8000\r\na=rtpmap:98 iLBC/8000\r\na=rtpmap:101 telephone-event/8000\r\na=sendrecv";
	
   private static ConfigRepository m_configRepository  = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
   private static String minSEValue= m_configRepository.getValue(Constants.MIN_SE_TIMEOUT);
   private static String makeSASMSRefresher= m_configRepository.getValue(Constants.MS_ENABLE_REFRESHER_UAC);
   private static String mediaStatsEnable = (String) m_configRepository.getValue(com.baypackets.ase.util.Constants.MEDIA_STATS_DB_STORE_ENABLE);
   

    public ConnectHandler() {
		super();
	}

	public ConnectHandler(SipServletRequest request, Address partyB) {
		requestIn = request;
		bParty = partyB;
	}

	public ConnectHandler(SipServletRequest request,Address partyB, Address from) {
		requestIn = request;
		bParty= partyB;
		this.from = from;
	}

	/**
     * This method will be invoked to start connect operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     *
     */
	public void start() throws ProcessMessageException{
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB>entered start() ");
		}
		SBB sbb = (SBB)getOperationContext();
		SipSession legB = null ;
		// add A party
		sbb.addA(requestIn.getSession());
		int contentLength = 0;
		try {
			
			// setting request's SDP into session
			String contentType = null ;
			contentLength = requestIn.getContentLength();
			if (logger.isInfoEnabled()) {
				logger.info("[.....]setting sdp for request " + contentLength + "   <--->  "
								+ requestIn.getContentLength());
			}
			contentType=requestIn.getContentType();
			byte[] termSDP = (byte[]) this.getOperationContext().getSBB().getAttribute(MsSessionController.TERM_SDP);
			boolean servSetSDP = false;
			if (termSDP != null && termSDP.length !=0){
				if (logger.isInfoEnabled()) {
					logger.info("[***] setting SDP provided by service in session");
				}
				requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,termSDP);
				requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,Constants.SDP_CONTENT_TYPE);
				this.getOperationContext().getSBB().setAttribute(MsSessionController.TERM_SDP, null);
				servSetSDP = true;
			} else{
			if (contentLength >  0)	{	
				try{
					if(contentType.startsWith(Constants.SDP_MULTIPART)){
						int sdpBpIndx=-1;
						MimeMultipart multipart = null ;
						multipart=(MimeMultipart)requestIn.getContent();
						int count=multipart.getCount();
						for(int i=0;i<count;i++){
							if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
						      
						    requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,bp);
							requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
									multipart.getBodyPart(sdpBpIndx).getContentType());
							servSetSDP = true;
						    
						}else {
							this.initInvWithoutSDP = true;
							if (logger.isInfoEnabled()) {
								logger.info("<SBB> No SDP content associated with  multipart "+requestIn.getMethod()+" request");
							}
						}
						
					}else{
						if (logger.isInfoEnabled()) {
							logger.info("[***] setting SDP in session");
						}
							requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,
									requestIn.getContent());
							requestIn.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
									requestIn.getContentType());
							servSetSDP = true;
					}
				}catch(MessagingException e){
					this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
					if (logger.isInfoEnabled()) {
						logger.info("Exception in saving SDP "+e);
					}
				}catch(Exception e){
					this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
					if (logger.isInfoEnabled()) {
						logger.info("Exception in saving SDP "+e);
					}
				}
			}
			else {
				this.initInvWithoutSDP = true;
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> No content associated with this request/response");
				}
			}					
		}
         try {
            isRTPTunnelingEnabled = ((Integer)sbb.getAttribute(SBB.RTP_TUNNELLING)).intValue();
         }
         catch(Exception exp) {
        	 if (logger.isInfoEnabled()) {
        		 logger.info("RTP-tunneling attribute not present, defaulting to false");
        	 }
            isRTPTunnelingEnabled = 0;
         }
         
         if (logger.isInfoEnabled()) {
        	 logger.info("<SBB> RTP tummeling state :: "+isRTPTunnelingEnabled);
         }
 
			// add B party
        	SipFactory factory =  
				(SipFactory)sbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);;

			if (this.from == null && this.bParty == null) {
				requestOut = factory.createRequest(requestIn,true);
			}else if (this.from == null) {
				requestOut = factory.createRequest(requestIn.getApplicationSession(), requestIn.getMethod(), requestIn.getFrom(), this.bParty);
			}else {
				requestOut = factory.createRequest(requestIn.getApplicationSession(), requestIn.getMethod(), this.from, this.bParty);
			}
			
			this.setHeadersOnOutRequest(requestIn, requestOut);

			// For the second two creates we have to explicitly tell the container that is a contination request.
			((AriSipServletRequest)requestOut).setRoutingDirective(SipApplicationRoutingDirective.CONTINUE,requestIn);

			//setting requestUri of bParty
			requestOut.setRequestURI(bParty.getURI());
			// updating with party-A SDP

			if (logger.isInfoEnabled()) {
				logger.info("The SBB name is....."+sbb.getClass().getName());
			}
			
			if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
				if (logger.isInfoEnabled()) {
					logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
				}
				disableObgwproxy(sbb.getApplicationSession(),
						null, requestOut);
			
				/* When the above if condition is true, the party to which connect is called to is
				 * the media server. Hence, we set the contents to the application/sdp part
				 * of the complete content if the initial invite was sip-t
				 */
				//If MS_INVITE_WITHOUT_SDP attribute is set by Service, this means that service wants to send the INVITE
				//without SDP to the Media Server even though there is a SDP in the incoming request
				
				//sumit:: set the SDP in request if servSetSDP is true and service as not set the attribute MS_INVITE_WITHOUT_SDP
				//modified if condition here to simpler form
				//check on content length is not good option as in case of SIPT content length>0 and sdp can be 0
				String invWithoutSDP = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.MS_INVITE_WITHOUT_SDP);
				/*
				 * if (contentLength > 0
				 * ) {
				 * requestOut.setContent(requestIn.getSession().getAttribute(SBBOperationContext.
				 * ATTRIBUTE_SDP),
				 * (requestIn.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE
				 * )).toString());
				 * } else
				 */
				if(servSetSDP && (invWithoutSDP == null || invWithoutSDP.length() == 0)){
					//This is the case where service sets the terminating party's SDP and this needs to be set
					//even if content type length is zero.
					//The control will only come here when service sets the attribute TERM_SDP and content length is zero
					//There is an error scenario where in service sets both attributes (MS_INVITE_WITHOUT_SDP and TERM_SDP)
					requestOut.setContent(requestIn.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP),
							  (requestIn.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString());
				}else{
					this.initInvWithoutSDP = true;
					if(logger.isDebugEnabled())
						logger.debug("servSetSDP not set or service set invwithoutSDP....");
				}
			}else if (contentLength >  0) {
				/* If the connect is not being called for the media server, then the content
				 * has to be directly set.
				 */
				requestOut.setContent(requestIn.getContent(),requestIn.getContentType());	
				
			}
			legB = requestOut.getSession();
			if (logger.isInfoEnabled()) {
				logger.info("[.....] Adding INVITE request sent to B in B's session");
			}
			legB.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestOut);
			if (logger.isInfoEnabled()) {
				logger.info("Disabling Outbound Proxy on Session : " + legB);
			}
			
			disableObgwproxy(sbb.getApplicationSession(),
					legB, null);
			sbb.addB(legB);
			
			//sbb.addB(requestOut.getSession());
			//requestOut.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestOut); 
			if (logger.isInfoEnabled()) {
				logger.info("[.....] Adding INVITE request received from A in A's session");
			}
			
			requestIn.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestIn);
			//Atrribute name used to store the initial request to determine the content type for Sending either SIP or SIP-T in case of disconnect
			//As INIT_REQUEST gets removed at the time of receiving 200 OK for INVITE
			requestIn.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT,requestIn);
			
			if (logger.isInfoEnabled()) {
				logger.info("[.....] Adding INVITE from A in app session, as it may be needed if one-way dailout is required later on..");
			}
			requestIn.getApplicationSession().setAttribute(Constants.REQUEST_FROM_A, requestIn);
			// Storing this request to the session for CANCELING it later
			requestOut.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST, requestOut);
			requestOut.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT, requestOut);
			
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Sending INVITE to party-B");
				logger.info("The SBB name is....."+sbb.getClass().getName());
			}
			
			if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
				if (logger.isInfoEnabled()) {
					logger.info("The SBB is MediaSessionController so Disabling the OBGW Proxy for this request");
				}
				//requestOut.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
				disableObgwproxy(sbb.getApplicationSession(),
						null, requestOut);
			}
			//-* check for session expires header filed ,  set MIN-SE 
			// and Session-Expires header field in request
			//String supportedHeaderValue =requestIn.getHeader(Constants.HDR_SUPPORTED);
			String supportedHeaderValue = null;
			Iterator valueList = requestIn.getHeaders(Constants.HDR_SUPPORTED);
			while(valueList!=null && valueList.hasNext()){
				supportedHeaderValue = (String) valueList.next();
				if(supportedHeaderValue!=null && supportedHeaderValue.contains("timer")){
					String newRefresher = "refresher=uas";
					
					if(requestIn.getHeader(Constants.HDR_MIN_SE)!=null){
						requestOut.setHeader(Constants.HDR_MIN_SE, minSEValue);
						//set min-se value for both session
						requestIn.getSession().setAttribute("MIN_SE",requestIn.getHeader(Constants.HDR_MIN_SE));
						sbb.getB().setAttribute("MIN_SE",minSEValue);
					}else{
						requestOut.addHeader(Constants.HDR_MIN_SE,minSEValue);
						//set min-se value for both session
						sbb.getA().setAttribute("MIN_SE",minSEValue);
						sbb.getB().setAttribute("MIN_SE",minSEValue);
					}
					String sessionExpiryValue = minSEValue+ ";" + newRefresher;
					if(logger.isDebugEnabled()){
						logger.debug("Session Expires set in b request :" + sessionExpiryValue);
					}   
					requestOut.setHeader(Constants.HDR_SESSION_EXPIRES, sessionExpiryValue);
					break;
				
			}
				
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("Check if session refresh to be enabled on ms");
			}
			
			/**
			 * making SAS as refresher so that if any port gets blocked on ms side that is cleared as when ms donot get session refresh from SAs it will send
			 * bye itself 
			 */
			if ((sbb.getClass().getName()
							.equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl"))
					&& AseStrings.TRUE_SMALL
							.equalsIgnoreCase(makeSASMSRefresher)) {

				
				if (logger.isDebugEnabled()) {
					logger.debug("Enable Session refresh on MS");
				}
				String newRefresher = "refresher=uac";

				requestOut.setHeader(Constants.HDR_MIN_SE, minSEValue);

				sbb.getB().setAttribute("MIN_SE", minSEValue);

				String sessionExpiryValue = minSEValue + ";" + newRefresher;
				if (logger.isDebugEnabled()) {
					logger.debug("Session Expires set in b request :"
							+ sessionExpiryValue);
				}
				requestOut.setHeader(Constants.HDR_SUPPORTED, "timer");
				requestOut.setHeader(Constants.HDR_SESSION_EXPIRES,
						sessionExpiryValue);
			}
			
			
			//if X-ISC-SVC atf header is present then its must be remove
			if(requestOut.getHeader("X-ISC-SVC")!=null){
				requestOut.removeHeader("X-ISC-SVC");
			}
			//--------
			sendRequest(requestOut);
			//For Media Server Stats
			if(mediaStatsEnable.equals(AseStrings.TRUE_SMALL)){
				try{
					SipURI uri =  (SipURI)requestOut.getRequestURI();
					String voiceXmlPath  = uri.getParameter("voicexml");
					if(voiceXmlPath != null){
						//Getting voiceXmlPath path without encoding 
						voiceXmlPath =  voiceXmlPath.substring(0, voiceXmlPath.indexOf(AseStrings.PERCENT));
						MediaServerStatisticsManager mediaServerStatisticsManager = MediaServerStatisticsManager.getInstance();
						//passing MsOperationSpec as null,play duration as zero and voiceXmlPath
						mediaServerStatisticsManager.setStaticsInfo(null,0,voiceXmlPath+AseStrings.COMMA);

					}
				}catch (Exception e) {
					logger.error("Exception in start while setting statistics info : "+e);
				}
			}
			// END 
		}catch(IOException exp) {
			logger.error(exp.getMessage(),exp);	
			//changes for bug 50330 --4 lines
			SipSession session = requestIn.getSession();
				
			if( requestIn.isInitial() && 
			  ( (session.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE) == null) || 
			  ( ((Integer) session.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE) == Constants.STATE_EARLY) ||
			  	((Integer) session.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE) == Constants.STATE_INITIAL) ) )
			) {
				if (logger.isDebugEnabled()) {
					logger.debug("sending 503 response");
				}
					try {
						requestIn.createResponse(503).send();
					}catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
			}
				throw new ProcessMessageException(exp.getMessage());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB>exited start() ");
		}
    }


	public void ackTimedout(SipSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> Entered ackTimedout");
		}
		this.failOperation(SBBEvent.REASON_CODE_ACK_TIMEOUT);
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> Exited ackTimedout");
		}
    }


	public void prackTimedout(SipSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> Entered prackTimedout");
		}
		this.failOperation(SBBEvent.REASON_CODE_PRACK_TIMEOUT);
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> Exited prackTimedout");
		}
    }


	public void handleRequest(SipServletRequest request) {


		logger.debug("<SBB> entered handleRequest :: ");
		/**
		 *	Following responses  are expected  from party-A during 
		 *  connect operation
		 *
		 *	1. ACK		:
		 *
		 *	scenario	:		--> INVITE
		 *						<-- 200 OK
		 *						--> ACK
		 *	Action		:		relay ACK to party-B.
		 *
		 *
		 *  2. PRACK	:
		 *
		 *	scenario	:		--> INVITE
		 *						<--	1xx REL
		 *						--> PRACK
		 *
		 * 3. PRACK scenario 2
		 * 						INVITE ->
		 * 									INVITE ->
		 * 									<-- 200
		 * 						<-- UPDATE
		 * 						200 update -->
		 * 						<-- 1xx REL
		 * 						PRACK -->
		 *
		 * 4. CANCEL from party A.
		 */

		SBB sbb = (SBB)getOperationContext();
		SipSession partyB = sbb.getB();
		dialogState = ((Integer)requestIn.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		//String sessionExpired = (String) request.getApplicationSession().getAttribute(Constants.SESSION_EXPIRED_OF);
		//--------
		try {

			// ACK request
			if (request.getMethod().equalsIgnoreCase(Constants.METHOD_ACK)) {
				//Storing SDP as it might happen that we don't get the SDP from
				//requestIn at the time of connect rather gets the SDP from ACK
				this.setSDP(request);
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received ACK from party-A");
				}
				if(this.isSessionRefreshReInviteReceived==true){
				  	 this.isSessionRefreshReInviteReceived=false;
				   	 if(logger.isDebugEnabled()){
				   	       logger.debug("ack received for session refresh re-invite");
				   	 }
				   	 setCompleted(true);
				   	 return;
				}
				
				setCompleted(true);
				//This is necessary in case when there is null INVITE then offer
				//is sent in 200 OK received from Party B and Answer will be sent 
				//by Party A in ACK request
				
				/* In case of almost all the flows, the ACK for 200 OK from party-B is
				 * generated as soon as 200 OK is received. Hence, we need to send the 
				 * ACK to party-A only if it has not been sent already. 
				 */
				if(ackToB==null){
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Relying ACK to party-B");
					}
					//SipServletRequest ackOut = response2xx.createAck();
					ackToB = response2xx.createAck();

					//IN UPDATE_NEEDED case we need to send the Party-A SDP received in 200 OK
					//to media server in ACK as there is no surity that SDP will be received in 
					//ACK
					if(sbb.getAttribute(Constants.UPDATE_NEEDED)!=null &&
							   sbb.getAttribute(Constants.UPDATE_NEEDED).toString().equalsIgnoreCase(AseStrings.TRUE_CAPS) 
							   && this.aPartyUpdateSDPContent != null){
						ackToB.setContent(aPartyUpdateSDPContent, aPartyUpdateSDPContentType);
					}else if (request.getContentLength() > 0){
						ackToB.setContent(request.getContent(), request.getContentType());
					}else if (request.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) != null
							&& initInvWithoutSDP){
						if (logger.isInfoEnabled()) {
							logger.info("Recently Done fix");
						}
						ackToB.setContent(request.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP), (String) request.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE));
					}
					sendRequest(ackToB);
					fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
					//ack sends to media server no need of this attribute
                    response2xx.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
                    if (logger.isInfoEnabled()) {
                    	logger.info("<SBB> Connected successfully");	//BPInd13388 (Log level is set info from error)
                    }
				}else{
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> ACK to party-B has already been sent. Hence doing nothing.");
						logger.info("<SBB> Firing EVENT_CONNECTED.");
					}
					fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Connected successfully");	//BPInd13388 (Log level is set info from error)
					}
				}
				
			}

			 // PRACK request 
			else if (request.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK)){
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received PRACK from party-A, Relying PACK to party-B");
				}

				this.setSDP(request);
				if (response1xx == null) {
	            	if(responseToA!=null && response2xx!=null)
	            	{
	            		if (logger.isDebugEnabled()) {
		            		logger.debug("[***] Received PRACK for 183 sent to A upon receiving 200 from B.");
		            		logger.debug("[***] Need to send 200 for this PRACK and 200 for INVITE.");
	            		}
	            		SipServletResponse prackOk = request.createResponse(200);
	            		try{
	            			if (logger.isDebugEnabled()) {
	            				logger.debug("[***] Replying to PRACK");
	            			}
	            			sendResponse(prackOk,false);
	            			if(sbb.getAttribute(Constants.TIMEOUT_REQUIRED)!=null){
	            				int timeout = Integer.parseInt(sbb.getAttribute(Constants.TIMEOUT_REQUIRED).toString());
	            				TimerService ts=(TimerService) sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
	            				ServletTimer timer=ts.createTimer(requestIn.getApplicationSession(), timeout, false, new TimerInfo("TIMEOUT_REQUIRED",this));
	            			}else{
	            				createAndSendSuccessResponse(partyB,requestIn);
	            			}
	            			return ;
	            		}catch(Rel100Exception e){
	            			logger.error("[.....] Can't send 200 ok for PRACK :: "+e);
	            		}catch(IOException e){
	            			logger.error("[.....] Can't send 200 ok for PRACK :: "+e);
	            		}
	            	}else{
				   logger.warn("<SBB> This is a PRACK which is locally generated.. not Relying to Party-B");
               return;
            }
	            }
				
				SipServletRequest prackToB = null;
				if (response1xx != null) {
					prackToB = response1xx.createPrack();

				}
			//	SipServletRequest prackToB = partyB.createRequest(Constants.METHOD_PRACK);
				if(request.getContentLength() > 0){
					prackToB.setContent(request.getContent(), request.getContentType());
				}
				// Inserting a RAck header into 
//				String rSeqHdr = (String)partyB.getAttribute(
//												Constants.ATTRIBUTE_RSEQ_1XX_REL);
//                String cSeqHdr = (String)partyB.getAttribute(
//												Constants.ATTRIBUTE_CSEQ_1XX_REL);
//				String rackHdr = rSeqHdr + Constants.SPACE + cSeqHdr;
//				if (logger.isDebugEnabled()) {
//					logger.debug("<SBB> Created RAck header in PRACK <"+rackHdr+AseStrings.ANGLE_BRACKET_CLOSE);
//				}
//				// removing  attributes from session
//				partyB.removeAttribute(Constants.ATTRIBUTE_RSEQ_1XX_REL);	
//				partyB.removeAttribute(Constants.ATTRIBUTE_CSEQ_1XX_REL);	
//				prackToB.addHeader(Constants.HDR_RACK,rackHdr);	

				//  store the original prack from A as attribute, so that it can be used later
				// on when 200 for PRACK will be created.
				prackToB.setAttribute(SBBOperationContext.ORIG_PRACK_FROM_PARTY_A,request);
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> Created prack for B ");
				}
				
				if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
					if (logger.isInfoEnabled()) {
						logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
					}
					//prackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
					disableObgwproxy(sbb.getApplicationSession(),
							null, prackToB);
				}
				
				sendRequest(prackToB);
			}
			else if (request.getMethod().equalsIgnoreCase(AseStrings.CANCEL)){
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received CANCEL request from party-A.");
				}
				cancelRecvFromA = true;
				this.failOperation(SBBEvent.REASON_CODE_CANCELLED_BY_ENDPOINT);
			}
			//handle re-invte in case of early state of session for session-expires and for update sdp
			else if(dialogState==Constants.STATE_EARLY && request.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)){
				String newSDP=null;
	        	String oldSDP=null;
	        	boolean isSameSdp = false;
	        	try{
	        		byte[] newSDPByteValue=(byte[])request.getContent();
	            	byte[] oldSDPByteValue=(byte[])request.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
	            	if(newSDPByteValue!=null&&oldSDPByteValue!=null){
	            			newSDP = new String(newSDPByteValue).trim();
	              		    oldSDP = new String(oldSDPByteValue).trim();
	              		  isSameSdp = newSDP.equals(oldSDP);
	            	}
	        		
	        	}catch(IOException e){
	        		logger.error("error: "+e.getMessage());
	        	}
	        	if(logger.isDebugEnabled()){
	        	    logger.debug("is sdp's are same? "+ isSameSdp);
	        	}
            	if(isSameSdp || (newSDP==null)){	
            		//for session expires
            		this.isSessionRefreshReInviteReceived=true;
                	this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
            		try{
            			//creating response 200 ok 
                		SipServletResponse resp =  request.createResponse(200);
                		//add require header
                		resp.addHeader(Constants.HDR_REQUIRE, "timer");
                		//add supported header.. 
                		if(request.getHeader(Constants.HDR_SUPPORTED)!=null){
                			resp.setHeader(Constants.HDR_SUPPORTED, request.getHeader(Constants.HDR_SUPPORTED));
                		}else{
                			resp.setHeader(Constants.HDR_SUPPORTED, "timer");
                		}
                		// add session-expires header filed in 200ok of re-invite for session refresh
                		if(request.getHeader(Constants.HDR_SESSION_EXPIRES)!=null ){
                			String value = this.getDeltaSeconds(request.getHeader(Constants.HDR_SESSION_EXPIRES))+";refresher=uac";
                			resp.addHeader(Constants.HDR_SESSION_EXPIRES, value);
               
                		}
                		    
                			resp.setContent(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP),
                					           (String) sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE));
                			if(logger.isDebugEnabled()){
                			         logger.debug("old sdp set in 200ok..");
                			}
                		
                    	sendResponse(resp, false);
                    	if(logger.isDebugEnabled()){
                    	   logger.debug("200 send for re-invite of session-refresh");
                    	}
                        this.startSessionExpiryTimer(request,Constants.TIMER_FOR_MS,Constants.SESSION_EXPIRY_TIMER_FOR_MS);
            	  
        	       }catch(Exception e){
        		      logger.error("exception in handle re-invite "+e.getMessage());
        	          }
		
		        }
            	/*
            	 * Reeta commented for health care clinet issue . client sends session refresh with null sdp
            	 */
//            	else if(newSDP==null){
//	        		//send response 488 (not acceptable) and return  
//		        	//only in case of ms
//	        		try{
//	        			SipServletResponse errorResp = request.createResponse(488);
//	            		sendResponse(errorResp, false);
//	            		if(logger.isDebugEnabled()){
//	            		          logger.debug("488 sends....");
//	            		}
//	            		return;
//	        		}catch (Rel100Exception e) {
//	        			logger.error(e.getMessage(),e);
//					}
//	        		catch(IOException e){
//	        			logger.error(e.getMessage(),e);
//	        		}
//	        		
//	        	}
            	else{
            		if(logger.isDebugEnabled()){
			                  logger.debug("re-invite received in early state but not for session refresh");
            		}
		          }
	        
			
			}
			//---------------
			
			else {
				logger.error("<SBB> Unknown request received ");
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("<SBB> exited handleRequest ");
			}
		}
		catch(IOException exp) {
			logger.error("Exception thrown ..." +exp.getMessage(),exp);
		}catch(Rel100Exception e) {
			logger.error("Exception thrown...." + e.getMessage(),e);
		}
    }

	public void postTimerProcessing(){
		createAndSendSuccessResponse(((SBB)getOperationContext()).getB(), requestIn);
	}
	
	protected void createAndSendSuccessResponse(SipSession partyB , SipServletRequest request){
		try{
		SipServletResponse inviteOk = request.createResponse(200);
		Object content = partyB.getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
		String contentType = (String)partyB.getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
		byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);
		String contentDispHeader=(String)partyB.getAttribute(Constants.HDR_CONTENT_DISPOSITION);
        if(anm_isup != null) {
        	Multipart mp = new MimeMultipart();				
			if(content != null && contentType != null){
				SBBResponseUtil.formMultiPartMessage(mp, (byte[])content, (String)contentType,contentDispHeader);				
			}
			String isupContentType = getIsupContentTypeForA(); 
			if (isupContentType == null)
				SBBResponseUtil.formMultiPartMessage(mp, anm_isup, Constants.ISUP_CONTENT_TYPE,null);
			else
				SBBResponseUtil.formMultiPartMessage(mp, anm_isup, isupContentType,null);
        	inviteOk.setContent(mp, mp.getContentType());
        	this.getOperationContext().getSBB().setAttribute(MsSessionController.ANM_ISUP, null);
        }else if(content != null && contentType != null){
        	inviteOk.setContent(content, contentType);
        	if(contentDispHeader!=null){
        		inviteOk.setHeader(Constants.HDR_CONTENT_DISPOSITION, contentDispHeader);
        	}else{
        		inviteOk.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
        	}
		}
    	String pCdrInfoHeaderVal = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.CDR_INFO_HEADER);
		if (pCdrInfoHeaderVal != null){
			if(logger.isDebugEnabled())
				logger.debug(" setting P-CDR-INFO in 200 OK to Party A");
			inviteOk.addHeader(MsSessionController.CDR_INFO_HEADER,pCdrInfoHeaderVal);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("[.....] Sending 200 ok for INVITE.");
		}
        if(logger.isDebugEnabled()){
		      logger.debug("[.....] Sending 200 ok for INVITE.");
        }
		//adding session-expires header filed
		if(request.getHeader(Constants.HDR_SESSION_EXPIRES)!=null){
			inviteOk.addHeader(Constants.HDR_SESSION_EXPIRES, this.getDeltaSeconds(requestIn.getHeader(Constants.HDR_SESSION_EXPIRES))+";refresher=uac");
			 //add require header 
			inviteOk.addHeader(Constants.HDR_REQUIRE, "timer");
		}
		sendResponse(inviteOk,false);
		//start session expiry timer for A_party------
		 this.startSessionExpiryTimer(request,Constants.TIMER_FOR_A_PARTY,Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
		//------------------
		//This is removed as completed should be sent after ack is received.
		//this.setCompleted(true, SBBEvent.EVENT_CONNECTED, SBBEvent.REASON_CODE_SUCCESS);
		}catch(Rel100Exception re){
			logger.error(re.getMessage(),re);
		}catch(MessagingException me){
			logger.error(me.getMessage(),me);
		}catch(IOException ioe){
			logger.error(ioe.getMessage(),ioe);
		}/*catch(MessageEncodingException mee){
			logger.error(mee.getMessage(),mee);
		}*/catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
     * This method  handles all response from party-B. 
     * request to "party B" with an SDP indicating that media sent from party
     * B to A can resume.
     *
     * @response - Response from B-party.
     */
	public void handleResponse(SipServletResponse response) {
		super.handleResponse(response);
		
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> entered handleResponse with following response :: ");
		}

		  // handle INVITE responses
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {		
			handleInviteResponse(response);		
		}
		else {
			handleNonInviteResponse(response);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> exited handleResponse");
		}
    }

	public void setBParty(Address bParty) {
		this.bParty = bParty;
	}


	private void handleInviteResponse(SipServletResponse response)	{

		/*
         *  Folowing respons are extected from B-party in respose to
         *  INVITE request
         *  1.  100 TRYING      :  Log and ignore      
         *  2.  1xx             :  pass-them to A-party         
         *  3.  1xx  REL        :  pass them to A-party 
         *  4.  2xx             :  pass them to A-party
         *  5.  487 Req Term.   :  Log & ignore
         *  6.  Non 2xx Final   :  pass 4xx to A-party      
         *                                      
         */
		try {
			SBB sbb = (SBB)getOperationContext();
			// 100 Trying
			if (SBBResponseUtil.is100Trying(response)) {
				if (logger.isInfoEnabled()) {
					logger.info("<SBB>Received 100 Trying from party-B");
				}
			}
			// Provisional responses except 100
			else if (SBBResponseUtil.isProvisionalResponse(response)) {
				//This check is introduced to avoid race condition when cancel is received from Party-A
				//before receiving 1XX/200 OK from Media Server
				State sipSessionState = requestIn.getSession().getState();
				
				if(sipSessionState == State.TERMINATED){
					this.failOperation(SBBEvent.REASON_CODE_ERROR);
					return;
				}

				if (isRTPTunnelingEnabled == Constants.RTP_TUNNELING_ENABLED) {
					
					//If it is the first 1xx response OR 
					//1xx from the Same Session from which the 1xx was already sent upstream,
					//then send it upstream OR handle it locally.
					if(response1xx == null 
							|| response.getSession().getId().equals(response1xx.getSession().getId())){
						this.send1xxUpstream(response);
					}else{
						this.handle1xxLocally(response);
					}
				}else {
					this.handle1xxLocally(response);
				}
			}

			// 2xx responses
			else if (SBBResponseUtil.is2xxFinalResponse(response)) {
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received 2xx response from party-B");
				}
				
			    SipSession partyB = sbb.getB();
			   
			    boolean aSupports100Rel = SBBResponseUtil.supports100Rel(this.requestIn);
				
				Boolean relay2xxResponse = (Boolean) sbb.getAttribute(SBB.RELAY_2XX_IN_EARLY_MEDIA);
				relay2xxResponse = relay2xxResponse != null ? relay2xxResponse : false;
				
				String strEarlyMedia = (String) this.getOperationContext().getSBB().getAttribute(SBB.EARLY_MEDIA);
				boolean earlyMedia = strEarlyMedia != null && strEarlyMedia.equalsIgnoreCase(AseStrings.TRUE_SMALL); 
				
				boolean needsToSend2xxInEarlyMedia = aSupports100Rel && relay2xxResponse && earlyMedia;
			    //This is the case of the MFRs.
			    //if we already got a 200 Ok, terminate this leg.
			    //So we need to ack and bye this response to 
			    if(response2xx != null && !needsToSend2xxInEarlyMedia){
 			    	//Create an ACK and send out....
			    	if (logger.isDebugEnabled()) {
			    		logger.debug("creating Ack");
			    	}
			    	ackToB = response.createAck();
			    	if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
			    		if (logger.isInfoEnabled()) {
			    			logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
			    		}
						//ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
			    		disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);
			    		
					}
			    	try{
			    		this.sendRequest(ackToB);
			    	}catch(IOException e){
			    		logger.error(e.getMessage(), e);
			    	}
			    	//Create a BYE and send out...
			    	SipServletRequest byeOut = response.getSession().createRequest("BYE");
			    	if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
			    		if (logger.isInfoEnabled()) {
			    			logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
			    		}
						//ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
			    		disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);
					}
			    	try{
			    		this.sendRequest(byeOut);
			    	}catch(IOException e){
			    		logger.error(e.getMessage(), e);
			    	}
			    	return;
			    }
			    this.startSessionExpiryTimer(response, Constants.TIMER_FOR_MS, Constants.SESSION_EXPIRY_TIMER_FOR_MS);
			    //-------------
			    //response used when ms disconnect() called and 200ok recevied but ack not send yet.
			    //then using this response 1st sending ack to ms then send bye [INVITE-2XX ACK] 
			    response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP, response);
			    
			    //TODO: Need to create the timer for the delta seconds received in the session-expires 
			    //header in 200 OK from TERM party(UAS)
			    
			    //In case of MFRs, if we get the 200 Ok from another session,
			    //then swap the original session with the newly created session...
			    //This will be the case, if we already got a 1xx from the original session but not the 2xx
			    if(response.getSession() != requestOut.getSession()){
			    	sbb.removeB();
			        response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
			        sbb.addB(response.getSession());
			    }
			    
				//We have to preserve the 200 OK response from party-B, so that
				// an ack can be created once an ACK is received from party-A.
				// so storing "200 OK" from B as attribute in B session
				this.response2xx = response;
			    
				//This check is introduced to avoid race condition when cancel is received from Party-A
				//before receiving 200 OK from Media Server
				State sipSessionState = requestIn.getSession().getState();
				if(sipSessionState == State.TERMINATED){
					this.failOperation(SBBEvent.REASON_CODE_ERROR);
					return;
				}

				// extracting party B SDP and storing in session
				int contentLength = 0 ;
				String contentType = null ;
				contentLength = response.getContentLength();
				if (logger.isInfoEnabled()) {
					logger.info("[.....]setting sdp for response"+contentLength);
				}
				contentType=response.getContentType();
				if (contentLength >  0)	{	
					/*try{
						if(contentType.startsWith(Constants.SDP_MULTIPART)){
							int sdpBpIndx=-1;
							MimeMultipart multipart = null ;
							multipart=(MimeMultipart)response.getContent();
							int count=multipart.getCount();
							for(int i=0;i<count;i++){
								if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
								logger.info("<SBB> No SDP content associated with  multipart "+response.getStatus()+" response");
							}
							
						}else{
							
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
							
						}
					}catch(MessagingException e){
						this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						logger.info("Exception in saving SDP "+e);
					}catch(Exception e){
						this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						logger.info("Exception in saving SDP "+e);
					}*/
					//if response content with multipart then first parse sdp content then set into the session
					this.setContentInSession(response);
				}
				else {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> No content associated with this request/response");
					}
				}		
				
				int retVal = fireEvent(SBBEvent.EVENT_CONNECT_PROGRESS,SBBEvent.REASON_CODE_SUCCESS,requestOut);
				if(retVal == SBBOperation.ACTION_NOOP){
					//handle a special call flow in that SBB sends ACK to B-party and Media server when
					//event fired "EVENT_CONNECT_PROGRESS" and service return NOOP.
					
					if(sbb.getAttribute(MsSessionController.B_TAKEOVER)!=null){
						SipServletResponse termSuccessResponse = (SipServletResponse) this.getOperationContext().getSBB().getAttribute(MsSessionController.B_TAKEOVER);
			            //send ack to b-party
						SipServletRequest termAck = termSuccessResponse.createAck();
                        if (response2xx.getContent()!= null) {
                          termAck.setContent(response2xx.getContent(), response2xx.getContentType());
                          if (logger.isDebugEnabled()) {
                              logger.debug("Set msSuccessRespSdpContentType as ACK Content");
                            }
                         }
                      //if response content with multipart then first parse sdp content then set into the session
                        this.setContentInSession(termSuccessResponse);
                       
                	    termAck.send();
                	    if (logger.isDebugEnabled()) {
                            logger.debug("ack sends to b-party...");
                          }
                	    
                	    this.startSessionExpiryTimer(termSuccessResponse, Constants.TIMER_FOR_A_PARTY, Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
                      
                  
                      //send ACK to MS
                      SipServletRequest ackToMS = response2xx.createAck();
                      if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
                    	  	if (logger.isInfoEnabled()) {
                    	  		logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
                    	  	}
						//	ackToMS.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
							disableObgwproxy(sbb.getApplicationSession(),
									null, ackToMS);
						}
                      if (null != this.getOperationContext().getSBB().getA()){
							this.getOperationContext().getSBB().getA().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_CONFIRMED));
							if (logger.isInfoEnabled()) {
								logger.info("A Party Dialog state " + this.getOperationContext().getSBB().getA().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE));
							}
						}
                      sendRequest(ackToMS);
                      //ack sends to media server no need of this attribute
                      response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
                      if (logger.isDebugEnabled()) {
                          logger.debug("ack sends to media-server...");
                       }
                      fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
                      if (logger.isDebugEnabled()) {
                       logger.debug("fire event for EVENT_CONNECTED");
                       }
                      setCompleted(true);
					}
					else{
						//do not send 200 Ok to party-A,
						//send ACK to party-B
						if (logger.isInfoEnabled()) {
							logger.info("<SBB> Sending ACK to party-B");
						}
						ackToB = response2xx.createAck();
						if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
							if (logger.isInfoEnabled()) {
								logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
							}
						//	ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
							
							disableObgwproxy(sbb.getApplicationSession(),
									null, ackToB);
							
						}
						if (null != this.getOperationContext().getSBB().getA()){
							this.getOperationContext().getSBB().getA().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_EARLY));
							if (logger.isInfoEnabled()) {
								logger.info("A Party Dialog state " + this.getOperationContext().getSBB().getA().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE));
							}
						}
						
						sendRequest(ackToB);
						fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
						//ack sends to media server no need of this attribute
	                     response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
					}
					
					
				}
				else {
					
					/* response1xx is not null ONLY when we send a provisional response upstream.
					 * So it being null means that no response has been sent upstream. 
					 * And since requestIn supports 100Rel, we generate and send 183 upstream first.
					 * Added by Reshu Chaudhary
					 */
					
					/*String strEarlyMedia = (String) this.getOperationContext().getSBB().getAttribute(SBB.EARLY_MEDIA);
					boolean earlyMedia = strEarlyMedia != null && strEarlyMedia.equalsIgnoreCase(AseStrings.TRUE_SMALL); */
					
					String send1XX = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.SEND_1XX);
					if(earlyMedia&& response1xx==null && SBBResponseUtil.supports100Rel(requestIn) && (send1XX == null || send1XX.toLowerCase().equals(AseStrings.TRUE_SMALL)) )
					{
						if (logger.isDebugEnabled()) {
							logger.debug("INVITE supports 100rel, and we received 200 from B before a 183."+
								     " So sending 183/180 to A before relaying this 200.");
						}
						/* In certain flows, we may need to send an UPDATE to party A before sending any
						 * provisional response. So this check needs to be added here..
						 */
						if(sbb.getAttribute(Constants.UPDATE_NEEDED)!=null &&
						   sbb.getAttribute(Constants.UPDATE_NEEDED).toString().equalsIgnoreCase(AseStrings.TRUE_CAPS)){
							
							SipSession partyA = sbb.getA();
				        	SipServletRequest updateA = partyA.createRequest(Constants.METHOD_UPDATE);
				        	if(response.getContentLength() > 0){
								try{
									updateA.setContent(response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP),
													  (String)response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE));
								}catch(IOException e){
									logger.error("<SBB> Could not copy the contents to UPDATE for party-A ", e);	
								}
							}
				        	try{
								sendRequest(updateA);
							}catch(IOException e){
								logger.error("<SBB> Could not send UPDATE to party-A ", e);	
							}
						}else{
							// WE DONT NEED TO SEND AN update, SO WE DIRECTLY SEND THE 180/183 RESPONSE
							
							/* Whether we need to create 183 or 180, is to be decided by the APP.
							 * Hence checking the attribute PROVISIONAL_RESPONSE_CODE before generating the response.
							 */
							responseToA = this.generateProvisionalResponse(response);
							if(responseToA == null){
								if(logger.isDebugEnabled()){
									logger.debug("got null provisional reposne");
								}
								this.failOperation(SBBEvent.REASON_CODE_ERROR);
								return;
							}
							try {	
								sendResponse(responseToA,true);
							}catch(Rel100Exception exp) {
								if (logger.isDebugEnabled()) {
									logger.debug("[.....] Cann't send reliably :: ",exp);
								}
					    	}
						}
					}else{
						
						// doing ACK of 200 OK to B
						try{
							//TODO: We need to refrain from sending ACK to party B and will be send only in case 
							//when we receive ACK from Party A
							if (null == this.getOperationContext().getSBB().getAttribute(MsSessionController.NO_ACK)) {
								ackToB = response.createAck();
								sendRequest(ackToB);
								//ack sends to media server no need of this attribute
			                      response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
							}else{
								if (logger.isInfoEnabled()) {
									logger.info("Service has set the NO_ACK Attribute for NULL INVITE Flow");
								}
								this.getOperationContext().getSBB().setAttribute(MsSessionController.NO_ACK,null);
							}
							if (logger.isInfoEnabled()) {
								logger.info("<SBB> Relay 2xx response from party-B to party-A");
							}
							SipServletResponse okResp = requestIn.createResponse(response.getStatus());
							
							/*String sessExp = response.getHeader("Session-Expires");
							if(sessExp != null) {
								okResp.addHeader("Session-Expires", sessExp);
							}*/

							Object content = 	response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP);//epartyB.getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
							contentType = (String)response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
							
							byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);
				            if(anm_isup != null) {
				            	Multipart mp = new MimeMultipart();				
				    			if(content != null && contentType != null){
				    				SBBResponseUtil.formMultiPartMessage(mp, (byte[])content, (String)contentType, 
				    						              (String)response.getHeader(Constants.HDR_CONTENT_DISPOSITION));				
				    			}
								String isupContentType = getIsupContentTypeForA(); 
								if (isupContentType == null)
									SBBResponseUtil.formMultiPartMessage(mp, anm_isup, Constants.ISUP_CONTENT_TYPE, null);
								else
									SBBResponseUtil.formMultiPartMessage(mp, anm_isup, isupContentType, null);
								
				            	okResp.setContent(mp, mp.getContentType());
				            	this.getOperationContext().getSBB().setAttribute(MsSessionController.ANM_ISUP, null);
				            }else if(content != null && contentType != null){
				            	okResp.setContent(content, contentType);
				            	if(response.getHeader(Constants.HDR_CONTENT_DISPOSITION)!=null){
									okResp.setHeader(Constants.HDR_CONTENT_DISPOSITION, response.getHeader(Constants.HDR_CONTENT_DISPOSITION));
								}else{
									okResp.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
								}
							}
				            if(requestIn.getHeader(Constants.HDR_SESSION_EXPIRES)!=null){
				    			okResp.addHeader(Constants.HDR_SESSION_EXPIRES, this.getDeltaSeconds(requestIn.getHeader(Constants.HDR_SESSION_EXPIRES))+";refresher=uac");
				    			//add Require header with value timer
				    			okResp.addHeader(Constants.HDR_REQUIRE, "timer");  
				            }
				            //----
				        	String pCdrInfoHeaderVal = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.CDR_INFO_HEADER);
							if (pCdrInfoHeaderVal != null){
								if(logger.isDebugEnabled())
									logger.debug(" setting P-CDR-INFO in 200 OK to Party A");
								okResp.addHeader(MsSessionController.CDR_INFO_HEADER,pCdrInfoHeaderVal);
							}
							SipApplicationSession appSession = sbb.getApplicationSession();
							String pChargingVector = (String) appSession.getAttribute("P_CHARGE_VECTOR_SUCCESS");
							if(pChargingVector != null) {
								if(logger.isDebugEnabled()) {
									logger.debug("setting P-Charging-Vector in 200 Ok to party A");
								}
								okResp.setHeader(MsSessionController.P_CHARGE_VECTOR, pChargingVector);
							}
							
							
				            sendResponse(okResp,false);
								//---start seesion expiry timer for A_party---------
								this.startSessionExpiryTimer(requestIn, Constants.TIMER_FOR_A_PARTY, Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
								
								//----------------
						}catch(Rel100Exception exp) {
		                	// Ignoring this exception as response is sent unreliably.
							// Should not happen
		            	}catch(MessagingException me){
		        			logger.error(me.getMessage(),me);
		        		}catch(IOException ioe){
		        			logger.error(ioe.getMessage(),ioe);
		        		}/*catch(MessageEncodingException mee){
		        			logger.error(mee.getMessage(),mee);
		        		}*/catch(Exception e){
		        			logger.error(e.getMessage(),e);
		        		}
					}					
				}
				//response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
			}

			// Its possible to get 487 as result of CANCEL sent to B.
			else if (response.getStatus() == 487) {
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Connect operation completed");
				}
				setCompleted(true);	
				fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE,	
								response);

				// No response should be created here as leg B's transaction is already over
				// because ACK for non-200 final response is created and delivered by container
				// before forwarding these messages to application
			}
			
			else if(response.getStatus() == 422){
				if(logger.isDebugEnabled()){
				        logger.debug("get 422 response of "+response.getMethod());
				}
				String deltaSec = response.getHeader(Constants.HDR_MIN_SE);
				if(Integer.valueOf(deltaSec)>Integer.valueOf(minSEValue)){
					response.getSession().setAttribute("MIN_SE", deltaSec);
					if(logger.isDebugEnabled()){
					        logger.debug("min-se value receive in 422 is greater then default value set in ase.properties");
					}
				}else{
					deltaSec = minSEValue;  
				}
				if(logger.isDebugEnabled()){
				           logger.debug("set minimum session expiry:"+deltaSec);
				}
				String newRefresher = "refresher=uas";
				String sessionExpHeadVal = deltaSec + ";" + newRefresher;
				if(logger.isDebugEnabled()){
					logger.debug("Session Expiry to be set in b request :" + sessionExpHeadVal);
				}
				requestOut= response.getSession().createRequest(Constants.METHOD_INVITE);
				this.setHeadersOnOutRequest(requestIn, requestOut);
				requestOut.setHeader(Constants.HDR_SESSION_EXPIRES, sessionExpHeadVal);
				if(requestOut.getHeader(Constants.HDR_MIN_SE)!=null){
					requestOut.setHeader(Constants.HDR_MIN_SE, deltaSec);
				}else{
					requestOut.addHeader(Constants.HDR_MIN_SE, deltaSec);
				}
				if(requestIn.getContent()!=null){
					requestOut.setContent(requestIn.getContent(), requestIn.getContentType());
				}
				sendRequest(requestOut);
				if(logger.isDebugEnabled()){
				        logger.debug("new invite request send with Min-SE header");
				}
			}

			// Non 2xx final response i.e. 3xx,4xx,5xx,6xx
			else {
				// 3xx support not available
				// Non 2xx final responses  (i.e. 3xx,4xx,5xx and 6xx)
				if (logger.isInfoEnabled()) {
					logger.info("<SBB>Received Non 2xx final responses");
				}


				// Here an ACK should be send to party-B in response to Non-2xx 
				// final response to terminate the transaction. Since SAS does
				// this, so no need to send ACK.

				// fire connect fail
                                //ashish
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Firing connect failed event");
				}
               
                
				int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE,	
								response);
				if(retValue == SBBEventListener.CONTINUE)
				{
					if (logger.isInfoEnabled()) {
						logger.info(" Received CONTINUE from application so Sending 503 to Party A");
					}
					try 
					{
							
							byte[] rel_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.REL_ISUP);
							//for SIP-T calls
							if(rel_isup != null) {
								//Getting IllegalStateExcetion in the case if control goes to else block.In order to resolve it, 
								//creating response inside if block.
								SipServletResponse resOut = requestIn.createResponse(Constants.RESP_IVR_NOT_AVAILABLE); 
								Multipart mp = new MimeMultipart();
								logger.info("setting REL in 503 to Party A");								
								String contentType = getIsupContentTypeForA(); 
								if (contentType == null)
									SBBResponseUtil.formMultiPartMessage(mp, rel_isup, Constants.ISUP_CONTENT_TYPE,null);
								else
									SBBResponseUtil.formMultiPartMessage(mp, rel_isup, contentType,null);
							resOut.setContent(mp, mp.getContentType());
							//FIXME::reason header service should provide??
	    	            	String reasonHdr = (String) sbb.getAttribute(MsSessionController.PARTY_A_REASON_HDR);
	    	            	
	    	            	if (reasonHdr != null){
	    	            		resOut.addHeader(Constants.HDR_REASON, reasonHdr);
	    	            	}
//	    	            	else{
//	    	            		resOut.addHeader(Constants.HDR_REASON,Constants.REASON_HDR_ISUP_VAL);	
//	    	            	}
							String pCdrInfoHeaderVal = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.CDR_INFO_HEADER);
							if (pCdrInfoHeaderVal != null){
								if(logger.isDebugEnabled())
									logger.debug(" setting P-CDR-INFO in 503 to Party A - NON 2xx final response");
								resOut.addHeader(MsSessionController.CDR_INFO_HEADER,pCdrInfoHeaderVal);
							}
							sendResponse(resOut, false);
							}else{
								sendResponse(requestIn.createResponse(Constants.RESP_DEFAULT_4XX),false);
							}
							setCompleted(true);
							requestIn.getApplicationSession().invalidate();
					}
					catch(Rel100Exception r100e) 
					{
						if (logger.isInfoEnabled()) {
							logger.info("cann't handle ",r100e);
						}
					} catch (MessagingException e) {
						logger.error("<SBB> Could not add MIME to response", e);	
					}
					
				}
				else
                {
                    setCompleted(true);
					return;
				}
			}
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
		}	
	}

	protected String getDeltaSeconds(String sessionExpHeadVal){
		int deltaSecIndex = sessionExpHeadVal.indexOf(AseStrings.SEMI_COLON, 0);
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
				ServletTimer timer=ts.createTimer(requestIn.getApplicationSession(), timerDuration, true,aseTimerInfo);
				TimerInfo timerInfo = new TimerInfo(servletTimer,Constants.SESSION_REFRESH_TIMER);
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
			
			TimerInfo timerInfo = new TimerInfo(servletTimer,Constants.SESSION_REFRESH_TIMER);
			((SBBImpl)sbb).setServletTimer(timer.getId(), timerInfo);	
			//sbb.getApplicationSession().setAttribute(timerInSession, timer);
			sbb.getB().setAttribute(timerInSession, timer.getId());
			if(logger.isDebugEnabled()){
			        logger.debug("timer created for "+timerInSession+"and delta sec is "+deltaSecondsUAS);
			}
		}
	}
		
	public void postSessionExpiry(String timerType){
			 logger.error(timerType+ "timer not expected here");
		
		 /*
			 * session refresh post session expires handle by NetworkMessageHandler.  
			 */
		
		/*
		
		//requestIn.getApplicationSession().setAttribute(Constants.SESSION_EXPIRED_OF, timerType);
		//its already timeout no need to cancel 
		this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
		this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
		SBB sbb = this.getOperationContext().getSBB();
		SipSession aPartySess = sbb.getA();
		
		 * (session refresh timer expired), handle by new handler object need to set requestIn from sipSession
		 
		if(requestIn==null){
			requestIn = (SipServletRequest) aPartySess.getAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT);
			logger.debug("initil request"+requestIn);
		}
		if(timerType.equals(Constants.TIMER_FOR_A_PARTY)){
			if(logger.isDebugEnabled()){
			   logger.debug("session-expires timer timeout for A party");
			   logger.debug("call in confirm state");
			}
			aPartySess.removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
			if(logger.isDebugEnabled()){
				logger.debug("attribute removed from sip session");
			}
			this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
			//this attribute checks at 200ok received for bye
			aPartySess.setAttribute(Constants.SESSION_EXPIRED_OF, Constants.TIMER_FOR_A_PARTY);
			
				try{
					SipServletRequest aByeOut = aPartySess.createRequest("BYE");	
					//creating rel...
					byte relCauseByte = (byte) ((1 << 7) | releaseCause);
					rel_isup[((rel_isup.length)-1)]= relCauseByte;
					Multipart mp = new MimeMultipart();
					if (logger.isInfoEnabled()) {
						logger.info("setting REL in bye to Party A");
					}
					String contentType = getIsupContentTypeForA(); 
					if (contentType == null){
						SBBResponseUtil.formMultiPartMessage(mp, rel_isup, Constants.ISUP_CONTENT_TYPE,null);
					}
					else{
						SBBResponseUtil.formMultiPartMessage(mp, rel_isup, contentType,null);
					}
				    aByeOut.setContent(mp, mp.getContentType());

	            	aByeOut.addHeader(Constants.HDR_REASON,"Q.850;cause="+releaseCause);
					this.sendRequest(aByeOut);
				}catch(MessagingException me){
        			logger.error(me.getMessage(),me);
        		}
				catch(IOException e){
					logger.error("exception in sending bye a party"+e.getMessage());
				}
				catch(Exception e){
					logger.error(e.getMessage(),e);
				}
				
				 check for any media playing 
				 * if stopPlay() retuen true ms disconnected from MediaServerInfoHAndler
				 * when received final INFO request.
				 
				if(this.stopPlay()){
					return;
				}
				SipServletRequest bByeOut = sbb.getB().createRequest(Constants.METHOD_BYE);
				try{
		    		this.sendRequest(bByeOut);
		    	}catch(IOException e){
		    		logger.error("exception in sending bye to b party"+e.getMessage());
		    	}
			
		}else if(timerType.equals(Constants.TIMER_FOR_MS)){
			if(logger.isDebugEnabled()){
			    logger.debug("session-expires timer timeout for ms");
			}
			sbb.getB().removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
			if(logger.isDebugEnabled()){
				logger.debug("attribute removed from sip session");
			}
			this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
			sbb.getB().setAttribute(Constants.SESSION_EXPIRED_OF, Constants.TIMER_FOR_MS);   
				SipServletRequest byeOut = sbb.getB().createRequest(Constants.METHOD_BYE);
				try{
		    		this.sendRequest(byeOut);
		    	}catch(IOException e){
		    		logger.error("exception in sending bye to b party"+e.getMessage());
		    	}
		    	SBBEvent event = new SBBEvent(SBBEvent.EVENT_MS_SESSION_EXPIRED);
                SBBOperationContext context = (SBBOperationContext)sbb;
                int responseCode = context.fireEvent(event);
                if(logger.isDebugEnabled()){
                      logger.debug("event fired for ms_session_expired..");
                }
			
		}
		
		
    */}
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
			if(str.equals(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY)) {
				 sbb.getA().removeAttribute(str);
				 if (logger.isDebugEnabled()) {
					 logger.debug("session expires timer attribute remove for A-party");
				 }
			} else {
				sbb.getB().removeAttribute(str);
				if (logger.isDebugEnabled()) {
					logger.debug("session expires timer attribute remove for B-party");
				}
			}
			return true;
		}
		return false;
	}
	//in case of sdp with multipart, 1st parse sdp then set in session  
	private void setContentInSession(SipServletResponse response){
		int contentLength = 0 ;
		String contentType = null ;
		contentLength = response.getContentLength();
		if (logger.isInfoEnabled()) {
			logger.info("[.....]setting sdp for response"+contentLength);
		}
		contentType=response.getContentType();
		if (contentLength >  0)	{	
			try{
				if(contentType.startsWith(Constants.SDP_MULTIPART)){
					int sdpBpIndx=-1;
					MimeMultipart multipart = null ;
					multipart=(MimeMultipart)response.getContent();
					int count=multipart.getCount();
					for(int i=0;i<count;i++){
						if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
						if (logger.isInfoEnabled()) {
							logger.info("<SBB> No SDP content associated with  multipart "+response.getStatus()+" response");
						}
					}
					
				}else{
					
					response.getSession().setAttribute(
							SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
					response.getSession().setAttribute(
							SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
					if(response.getHeader(Constants.HDR_CONTENT_DISPOSITION)!=null){
						response.getSession().setAttribute(Constants.HDR_CONTENT_DISPOSITION, response.getHeader(Constants.HDR_CONTENT_DISPOSITION));	
					}
				}
			}catch(MessagingException e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				if (logger.isInfoEnabled()) {
					logger.info("Exception in saving SDP "+e);
				}
			}catch(Exception e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				if (logger.isInfoEnabled()) {
					logger.info("Exception in saving SDP "+e);
				}
			}
		}
	}
	//--------------------
	protected SipServletResponse generateProvisionalResponse(SipServletResponse response){
		SBB sbb = (SBB)this.getOperationContext();
		SipServletResponse responseOut = null ;
		
		SipSession inSession = requestIn.getSession();
		//added check on dialog to handle race condition;
		//if cancel is recieved form a party and from b party successs resp is recvied at same time
		//a party session will be termibated; so this check is introduced
		Object dialogStateObj =inSession.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE);
		if(dialogStateObj!=null){
			int dialogState = ((Integer)dialogStateObj).intValue();
		
			if (dialogState == Constants.STATE_TERMINATED){
   		 		//return null as A party dialog is already terminated
				return null;
			}
		}
		
		responseOut = requestIn.createResponse(183);
		
		/* Now, once the response has been created, we may need to send either ACM or CPG
		 * in it, depending upon the fact that which of the two has been set by the application.
		 */
		try{
			byte[] acm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ACM_ISUP);
			String contentDispHeader=response.getHeader(Constants.HDR_CONTENT_DISPOSITION);
			if(acm_isup != null) {
				Multipart mp = new MimeMultipart();
				if(response.getContentLength() > 0){						
					SBBResponseUtil.formMultiPartMessage(mp, (byte[])response.getContent(), response.getContentType(),contentDispHeader );						
				}
				String contentType = getIsupContentTypeForA(); 
				if (contentType == null)
					SBBResponseUtil.formMultiPartMessage(mp, acm_isup, Constants.ISUP_CONTENT_TYPE,null);
				else
					SBBResponseUtil.formMultiPartMessage(mp, acm_isup, contentType, null);
				
				this.getOperationContext().getSBB().setAttribute(MsSessionController.ACM_ISUP, null);
				responseOut.setContent(mp, mp.getContentType());
			}else{
				//if CPG is present, set CPG
				byte[] cpg_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.CPG_ISUP);	
				if(cpg_isup != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Sending 18x with CPG");
					}
					Multipart mp = new MimeMultipart();
					if(response.getContentLength() > 0){						
						SBBResponseUtil.formMultiPartMessage(mp, (byte[])response.getContent(), response.getContentType(),contentDispHeader);						
					}
					String contentType = getIsupContentTypeForA(); 
					if (contentType == null)
						SBBResponseUtil.formMultiPartMessage(mp, cpg_isup,Constants.ISUP_CONTENT_TYPE, null);
					else
						SBBResponseUtil.formMultiPartMessage(mp, cpg_isup, contentType, null);
					
					this.getOperationContext().getSBB().setAttribute(MsSessionController.CPG_ISUP, null);
					
					responseOut.setContent(mp, mp.getContentType());
				}else if(response.getContentLength() > 0){
					// no ACM and no CPG means that it is a plain SIP call.
					responseOut.setContent(response.getContent(),response.getContentType());
					if(contentDispHeader!=null){
						responseOut.setHeader(Constants.HDR_CONTENT_DISPOSITION,contentDispHeader);
					}else{
						responseOut.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
					}
				}
			}	
			
			/*if(response.getContentLength() > 0){
				responseToA.setContent(response.getContent(),response.getContentType());
			}*/
		}catch(MessagingException me){
			logger.error(me.getMessage(),me);
		}catch(IOException ioe){
			logger.error(ioe.getMessage(),ioe);
		}/*catch(MessageEncodingException mee){
			logger.error(mee.getMessage(),mee);
		}*/catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return responseOut ;
	}
	
	private void handleNonInviteResponse(SipServletResponse response) {

		/**
		 * Following responses are expected from party-B for Non-Invite requests
		 *
		 * 1.
		 * 200 for PRACK		:	INVITE -> 
		 *							<-- 100 TRYING
 		 *							<-- 1xx REL
		 *							PRACK -->	
		 *                          <--  200 for PRACK
		 * 	Action				: 	relay 200 for PRACK to party-A
		 *
		 *
		 * 2.
		 * Non-200 for PRACK    :   INVITE ->
         *                          <-- 100 TRYING
         *                          <-- 1xx REL
         *                          PRACK -->
         *                          <--  Non-200 for PRACK
         * Action 				:   relay Non-200 for PRACK to party-A
		 *							fire connect failed event and 
		 *
		 * 3. 200 for BYE		: 	On ACK timeout connect handler will send ACK followed by BYE to 
		 *							B-Party. So a 200 for BYE is expected from B.
		 *
		 * 4. 
		 * 200 OK for UPDATE	:	INVITE ->
		 * 										INVITE ->
		 * 										<-- 200 OK
		 * 								{found UPDATE_NEEDED to be true}
		 * 							<-- UPDATE
		 * 							200 for UPDATE ->
		 * 								{need to send a 183/183 to A, and then a 200 for INVITE
		 *  
		 *  5.
		 *  Non-200 for UPDATE	:	INVITE ->
		 * 										INVITE ->
		 * 										<-- 200 OK
		 * 								{found UPDATE_NEEDED to be true}
		 * 							<-- UPDATE
		 * 							Non-200 for UPDATE ->
		 * 										send ACK to B
		 * 										send BYE to B
		 * 										fire connect failed event
		 *  
		 *
		 */	

		SBB sbb = (SBB)this.getOperationContext();

		// PRACK's response
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK)) {
			
			/*
             *  Here we need to get the initail PRACK request from party-A because
             *  "200 for PRACK" response should be created on this request. Current
             *  response can't be created on requestIn, if we do that it will became
             *  "200 for INVITE"
             *  
             */
            SipServletRequest prackToB = response.getRequest();
            
            /* In the special case scenario of PRACK-UPDATE flow, if we handle the reliable 183 from
	         * party-B locally, then upon getting 200 Ok for the PRACK, we need to send an UPDATE 
	         * message to party-A. Hence, the attribute "RESPONSE_1XX_FROM_B" is set to the 183 response
	         * ONLY when the response is handled locally, AND the response is reliable. So if this
	         * attribute is present, we know that an UPDATE is to be sent to the party-A
	         * Added by Reshu Chaudhary
	         */
	        if(sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW) != null && 
	     		   sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW).equals(AseStrings.TRUE_SMALL)){
	        	if (logger.isDebugEnabled()) {
	        		logger.debug("[.....] Received response to PRACK from party-B...");
	        	}
	        	SipServletResponse responseFromB = (SipServletResponse)prackToB.getAttribute(Constants.RESPONSE_1XX_FROM_B);	
		        if(update== null) {
		        	if (logger.isDebugEnabled()) {
		        		logger.debug("[.....] No UPDATE has been sent yet.");
		        	}
		        	if(SBBResponseUtil.is200Ok(response)) 
		        	{
		        		if (logger.isDebugEnabled()) {
		        			logger.debug("[.....] Received 2xx response to PRACK. So sending UPDATE request to party-A");
		        		}
			        	SipSession partyA = sbb.getA();
			        	update = partyA.createRequest(Constants.METHOD_UPDATE);
						if(responseFromB.getContentLength() > 0){
							try{
								update.setContent(responseFromB.getContent(), responseFromB.getContentType());
							}catch(IOException e)
							{
								logger.error("<SBB> Could not copy the contents to UPDATE for party-A ", e);	
							}
						}
						prackToB.removeAttribute(Constants.RESPONSE_1XX_FROM_B); // since it is no longer needed
						
						if (logger.isDebugEnabled()) {
							logger.debug("<SBB> [.....] Sending UPDATE to A ");
						}
						try{
							sendRequest(update);
						}catch(IOException e){
							logger.error("<SBB> Could not send UPDATE to party-A ", e);	
						}
		        	}
		        	else {
		        		if (logger.isDebugEnabled()) {
		        			logger.debug("[.....] Non-200 response for 183-PRACK's received...");
		        		}
		        	}
	        	}
		        else {
		        	if (logger.isDebugEnabled()) {
		        		logger.debug("[.....] UPDATE already sent, so doing nothing.");
		        	}
		        }
	        }
	        else {
	        	if (logger.isDebugEnabled()) {
	        		logger.debug("[.....] Default Handling");
	        	}
            SipServletRequest prackFromA = (SipServletRequest)prackToB.
                                getAttribute(SBBOperationContext.ORIG_PRACK_FROM_PARTY_A);
	
            SipServletResponse responseOut = prackFromA.createResponse(response.getStatus());
            if(response.getContentLength() > 0){
            	try{
            		responseOut.setContent(response.getContent(), response.getContentType());
            	}catch(IOException e){
            		logger.error("<SBB> Could not copy the contents to party-A", e);		
        		}
            }

            	if (SBBResponseUtil.is200Ok(response)) {
            		if (logger.isInfoEnabled()) {
            			logger.info("<SBB> Received 200 ok for PRACK");
            		}
				try {
					sendResponse(responseOut,false);
				}
				catch(Rel100Exception exp) {
					// Should not happen as response is sent unreliably
				}
				catch(IOException ioe) {
					logger.error("<SBB> Could not relay 200 Ok for prack to party-A");		
				}
			}
			else {
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received Non-2xx <"+response.getStatus());
					logger.info("<SBB> Firing connect failed event to Application");
				}
				
				int retValue=fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
						SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
				if ( retValue == SBBEventListener.CONTINUE) {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> sending non 2XX response for PRACK to party A");
					}
					try {
	                    sendResponse(responseOut,false);
	                }
	                catch(Rel100Exception exp) {
	                	if (logger.isInfoEnabled()) {
	                		logger.info("<SBB> error in sending reliable response ",exp);
	                	}
	                	// Should not happen as response is sent unreliably
	                }
					catch(IOException ioe) {        
	                    logger.error("<SBB> Could not relay Non-200 response <"+
							response.getStatus()+"> to prack to party-A");     
	                }
					
					setCompleted(true);
						
				}
			        else	
                                {
					setCompleted(true);
					return;
				}
			}
	        }
		}
		else if (response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE)) {

			 //	200 for BYE from B. This will happen when ACK-timeout occurs.
			//This will also happen when session got expired
			//This will also happen when CANCEL is received from A and as a result of which BYE is 
			//being sent to Party-B and Party-B responded with 200 OK response
			
			//only in early state 200 ok of bye handled by ConnectHandler otherwise its handled by NetworkMessageHandler
			 //String sessionExpired = (String) response.getApplicationSession().getAttribute(Constants.SESSION_EXPIRED_OF);
			String sessionExpired = (String) sbb.getB().getAttribute(Constants.SESSION_EXPIRED_OF);
			 if(sessionExpired!=null && sessionExpired.equals(Constants.TIMER_FOR_MS)){
				 if(logger.isDebugEnabled()){
            	       logger.debug("200ok received from media server");
				 }
        	     sbb.getB().removeAttribute(Constants.SESSION_EXPIRED_OF); 
        	   //In case, if ms already down then sbb not going to receive 200ok for bye.
        	     //now ms session expired event fired when sbb send bye to ms.
        	     /*SBBEvent event = new SBBEvent(SBBEvent.EVENT_MS_SESSION_EXPIRED);
                 SBBOperationContext context = (SBBOperationContext)sbb;
                 //this.lastFiredEvent = SBBEvent.EVENT_MS_SESSION_EXPIRED;
                 int responseCode = context.fireEvent(event);
                 if(logger.isDebugEnabled()){
                       logger.debug("event fired for ms_session_expired..");
                 }*/
                 setCompleted(true);
                 return;
             }
            //----------------
			if (logger.isInfoEnabled()) { 
				logger.info("Received <"+response.getStatus()+"> for BYE");
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("<SBB> Connect operation completed");
			}
			setCompleted(true);
		
			if (cancelRecvFromA){
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> firing <DISCONNECT> event");
				}
				fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_SUCCESS,response);
			}
		}
		/* Added for specific MS flow of NGIN.
		 * Reshu Chaudhary 
		 */
		else if (response.getMethod().equalsIgnoreCase(Constants.METHOD_UPDATE)){
			if (logger.isInfoEnabled()) {
				logger.info("Received <"+response.getStatus()+"> for UPDATE");
			}
			if (!SBBResponseUtil.is200Ok(response)) {
				this.failOperation(SBBEvent.REASON_CODE_ERROR_RESPONSE);
				return;
			}
			if(sbb.getAttribute(Constants.UPDATE_NEEDED)!=null &&
					sbb.getAttribute(Constants.UPDATE_NEEDED).toString().equalsIgnoreCase(AseStrings.TRUE_CAPS)){
				if (logger.isInfoEnabled()) {
					logger
						.info("[***] This UPADTE was sent ahead of 183+200, due to a requirement of NGIN call flow..");
					logger
						.info("[***] So now we need to send the provisional response, and the success response to A");
				}
				byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);	
				//Storing the PartyA-SDP in the variable so that it can be sent after wards in ACK to Media Server 
				try{
					if (response.getContentLength() > 0){
						this.aPartyUpdateSDPContent = response.getContent();
						this.aPartyUpdateSDPContentType = response.getContentType();
					}
				}catch(IOException ioe) {        
                    logger.error("<SBB> Could not Store Party-A SDP");     
                }
				
				if(anm_isup == null) {
					if (logger.isInfoEnabled()) {
						logger.info("[***] anm_isup is null, it means that we have to send the ACK for 200 to party B right away..");
					}
					ackToB = response2xx.createAck();
					
					
					if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}
					//	ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, AseStrings.BLANK_STRING);
						
						disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);
					}
					try {
						if (response.getContentLength() > 0)
							ackToB.setContent(response.getContent(), response.getContentType());
						sendRequest(ackToB);
	                }
	                //catch(Rel100Exception exp) {
	                  //       logger.info("<SBB> error in sending reliable response ",exp); 
				// Should not happen as response is sent unreliably
	               // }
					catch(IOException ioe) {        
	                    logger.error("<SBB> Could not relay ACK to Party-B");     
	                }
					
				}
				int contentLength = 0 ;
				String contentType = null ;
				
				contentLength = response.getContentLength();
				if (logger.isInfoEnabled()) {
					logger.info("[.....]setting sdp for response"+contentLength);
				}
				contentType=response.getContentType();
				
				if (contentLength >  0)	{	
					try{
						if(contentType.startsWith(Constants.SDP_MULTIPART)){
							int sdpBpIndx=-1;
							MimeMultipart multipart = null ;
							multipart=(MimeMultipart)response.getContent();
							int count=multipart.getCount();
							for(int i=0;i<count;i++){
								if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
								if (logger.isInfoEnabled()) {
									logger.info("<SBB> No SDP content associated with  multipart "+response.getStatus()+" response");
								}
							}
							
						}else{
							
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
							
						}
					}catch(MessagingException e){
						this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						if (logger.isInfoEnabled()) {
							logger.info("Exception in saving SDP "+e);
						}
					}catch(Exception e){
						this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						if (logger.isInfoEnabled()) {
							logger.info("Exception in saving SDP "+e);
						}
					}
				}
				else {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> No content associated with this request/response");
					}
				}		
				responseToA = this.generateProvisionalResponse(response2xx);
				if(responseToA == null){
					if(logger.isDebugEnabled()){
						logger.debug("got null provisional reposne");
					}
					this.failOperation(SBBEvent.REASON_CODE_ERROR);
					return;
				}
				try {	
					sendResponse(responseToA,true);
				}catch(Rel100Exception exp) {
					if (logger.isDebugEnabled()) {
						logger.debug("[***] Cann't send reliably :: ",exp);
					}
		    	}catch(IOException ioe) {        
	                    logger.error("<SBB> Could not relay Non-200 response <"+
							response.getStatus()+"> to prack to party-A");     
	            }
		    	
			}
		}
	}
	
	
	protected void failOperation(String reasonCode){
		this.failOperation(reasonCode, null);
	}
	
	protected void failOperation(String reasonCode, Throwable t){
	
		/*
		 * For the upstream dialog,
		 * if the dialog is not confirmed, container himself respond 
		 * to UAC with 200 OK for CANCEL and then 478 for INVITE
		 * before delegating this CANCEL event to application and
		 * update dialog state to TERMINATED.
		 * 
		 * So we need to only take care of handling the upstream session,
		 * only if the dialog state is INITIAL or EARLY
	     *
		 * There are two valid scenarios for downstream session
		 *
		 * 1. Final response already received from party B.
		 *	  In this case send an ACK followed by BYE.
		 *
		 * 2. Final response not yet receicved from B.
		 *	  In this case send CANCEL to B. B will respond 
	 	 *    with 200 OK for CANCEL followed by 487 for
		 *    INVITE.			
		 */
		
		SBB sbb = (SBB)this.getOperationContext();
		SipSession partyA =  sbb.getA();
		SipSession partyB = sbb.getB();
        // Process upstream if not CANCEL
        if (reasonCode != SBBEvent.REASON_CODE_CANCELLED_BY_ENDPOINT) {
		//Extract the upstream session state.
		//int upstreamState = partyA == null ? -1 : 
			//((Integer)partyA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
        State upstreamState = partyA == null ? State.TERMINATED : partyA.getState();
		//Handle the upstream session.
		switch(upstreamState){
			case INITIAL: 
			case EARLY:
				if (null == this.getOperationContext().getSBB().getAttribute(MsSessionController.EARLY_DIALOUT)){
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Upstream Dialog is in INITIAL/EARLY state, sending error response");
					}
					SipServletResponse respOut = null;
					try {
						String ctype = requestIn.getContentType();
						if (ctype.startsWith(Constants.SDP_MULTIPART_MIXED)) {
							if (logger.isInfoEnabled()) {
								logger.info("<SBB> ISUP Using 5xx error response");
							}
							respOut = requestIn.createResponse(Constants.RESP_IVR_NOT_AVAILABLE);
							// sending REL for SIP-T calls
							Multipart mp = new MimeMultipart();
							String contentType = getIsupContentTypeForA(); 
							if (contentType == null)
								SBBResponseUtil.formMultiPartMessage(mp,Constants.REL_ISUP_CONTENT,Constants.ISUP_CONTENT_TYPE,null);
							else
								SBBResponseUtil.formMultiPartMessage(mp,Constants.REL_ISUP_CONTENT,contentType,null);
							respOut.setContent(mp, mp.getContentType());
							respOut.addHeader(Constants.HDR_REASON,Constants.REASON_HDR_ISUP_VAL);
						}else{
							if (logger.isInfoEnabled()) {
								logger.info("ISUP Using 4xx error response");
							}
							respOut = requestIn.createResponse(Constants.RESP_DEFAULT_4XX);
						}
				    	String pCdrInfoHeaderVal = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.CDR_INFO_HEADER);
						if (pCdrInfoHeaderVal != null){
							if(logger.isDebugEnabled())
								logger.debug(" setting P-CDR-INFO in 480 to Party A - Fail Operation");
							respOut.addHeader(MsSessionController.CDR_INFO_HEADER,pCdrInfoHeaderVal);
						}
						sendResponse(respOut, false);
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					} catch (Rel100Exception e) {
						logger.error(e.getMessage(), e);
					} catch (MessagingException e) {
						logger.error(e.getMessage(), e);
					}
					break;
				}else{
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Upstream Dialog is in INITIAL/EARLY state and EARLY Dialout support needed");
					}
					this.getOperationContext().getSBB().setAttribute(MsSessionController.EARLY_DIALOUT,null);
					break;
				}
			case CONFIRMED: 
//				 if (reasonCode != SBBEvent.REASON_CODE_PEER_SESSION_NOT_REPLICATED){
//					 logger.info("<SBB> UpStream Dialog is in CONFIRMED state, so sending a BYE request");
//					
//					//Send BYE to the originating side.
//					try{
//						logger.info("<SBB> Sending BYE to party A");
//						SipServletRequest byeOut = partyA.createRequest(Constants.METHOD_BYE);
//						
//						if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
//							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
//							byeOut.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
//						}
//						
//						sendRequest(byeOut);
//					}catch(IOException e){
//						logger.error(e.getMessage(), e);
//					}
//			    }else {
//			    }
			case TERMINATED:
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Upstream Dialog is in CONFIRMED/TERMINATED state, so not doing anything");
				}
				break;
			default:
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Unknown upstream dialog state. So ignoring....");
				}
            }
		}

		//Extract the downstream session state.
        State downstreamState = partyB == null ? State.TERMINATED : partyB.getState();
//        int downstreamState = partyB == null ? -1 : 
//			((Integer)partyB.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		
		//Handle the downstream session.
		switch(downstreamState){
			case INITIAL: 
			case EARLY:
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Downstream Dialog is in INITIAL/EARLY state, sending a CANCEL request");
				}
				SipServletRequest cancelOut = requestOut.createCancel();
				
				if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
					if (logger.isInfoEnabled()) {
						logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
					}
			//		cancelOut.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");	
					disableObgwproxy(sbb.getApplicationSession(),
							null, cancelOut);
				}
				
				try{
					this.sendRequest(cancelOut);
				}catch(IOException e){
					logger.error(e.getMessage(), e);
				}
				//Set Completed will be called when the 487 is received, due to this CANCEL.
				this.fireEvent(SBBEvent.EVENT_CONNECT_FAILED, reasonCode, cancelOut);
				break;
			case CONFIRMED: 
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Downstream Dialog is in CONFIRMED state, so sending a BYE request");
				}
				//Send an ACK if the ACK was not sent earlier.
				if(response2xx != null && ackToB == null){
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Downstream Dialog is in CONFIRMED state, Sending the ACK to the terminating end.");
					}
					ackToB = response2xx.createAck();
					
					if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}	
						//ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
						disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);		
					}
					
					try{
						// ABAXI: This block is added for situations where invite sent to MS without SDP and cancel received from 
						//A Party and SBB sends Ack without SDP to MS. Due to this call stucked at MS side so sending dummy sdp to
						// avoid issue with MS.
						if(this.initInvWithoutSDP){	
							if (logger.isInfoEnabled()) {
								logger.info("The SBB is invite sent without SDP so sending dummy SDP in ACK to avoid MS stuck issues");
							}	
							ackToB.setContent(DUMMY_SDP.getBytes(), Constants.SDP_CONTENT_TYPE);
						}
						
						this.sendRequest(ackToB);
					}catch(IOException e){
						logger.error(e.getMessage(), e);
					}
				}

				//Send BYE to the terminating side.
				try{
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Sending BYE to party B");
					}
					SipServletRequest byeOut = partyB.createRequest(Constants.METHOD_BYE);
					
					if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}
					//	byeOut.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
						disableObgwproxy(sbb.getApplicationSession(),
								null, byeOut);
						
						
					}
					this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
					sendRequest(byeOut);
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Firing <SIG_IN_PROGRESS> to application ");
					}
				}catch(IOException e){
					logger.error(e.getMessage(), e);
				}
				// delayed setComplete to 200 for BYE otherwise a new Netwok handler will be 
				// created when 200 for OK received.
	        	this.fireEvent(SBBEvent.EVENT_SIG_IN_PROGRESS,reasonCode, null, t);
				break;
			case TERMINATED:
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Downstream Dialog is in TERMINATED state, so sending SIG_IN_PROGRESS event");
				}
				//Bug:20999 P-CDR Info not present in 487 Error response when CCC number dial and hungup immediately
				//There was a race condition when CANCEL received from Pary-A at the same time when SBB is trying to
				//send 183, but could not send as request is alread cancelled and resulting in an exception thrown 
				//by container to SBB. This resulted in the trigger of error case where SBB tries to clean both legs 
				//by sending 503 and BYE respectively. Now when CANCEL is being handled by SBB (connect handler because it
				//already tried sending 183), both legs have been cleaned and flow ends up in this section. Therefore, need to 
				//send SIG IN PROGRESS EVENT to service so that it should clean the PArty-A as in SBTM 487 is not being sent 
				//by service in some cases.
				this.fireEvent(SBBEvent.EVENT_SIG_IN_PROGRESS,reasonCode, null, t);
				break;
			default:
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Unknown downstream dialog state. So ignoring....");
				}
		}
	}

	public void cancel() {
		this.failOperation(SBBEvent.REASON_CODE_CANCELLED_BY_APPLICATION);
	}
	
	protected void send1xxUpstream(SipServletResponse response){
		SBB sbb = (SBB)getOperationContext();
		if(!SBBResponseUtil.isProvisionalResponse(response)){
			if (logger.isDebugEnabled()) {
				logger.debug("send1xxUpstream ==> Response not provisional. So ignoring it....");
			}
			return;
		}
		
		/* In case of PRACk_UPDATE_FLOW, we do not relay the 1xx responses upstream.
		 * So we handle the responses locally.
		 * Modified by Reshu Chaudhary
		 */
		if(sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW) != null && 
		   sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW).equals(AseStrings.TRUE_SMALL)){
			if (logger.isDebugEnabled()) {
				logger.debug("[.....] PRACK_UPDATE_FLOW is true, so we do not send 183 to party-A");
				logger.debug("Instead an UPDATE is sent to party-A");
			}
			/* Adding this line since in the PRACK-UPDATE flow, we send a UPDATE to A, so
			 * response1xx stays null. Hence on receiving the 200 for INVITE, it might attempt
			 * to send a 183 to A since it sees that no 1xx response has been sent to A even
			 * when it supports 100rel. Hence it needs to be set here.
			 * Reshu Chaudhary
			 */
			this.response1xx = response;
			
			handle1xxLocally(response);
		}
		else{
		if(logger.isDebugEnabled()){
			logger.debug("Sending the provisional response upstream :" + response.getStatus());
		}
	
		//Check whether the response is reliable or not.
		boolean reliable = SBBResponseUtil.isReliable(response);
	
		try{
				SipSession partyB = sbb.getB();
				/* Adding this piece of code because when a PRACK from upstream is received, we try and
				 * generate a PRACK for downstream using these two attributes, BUT they are found to be null.
				 * So we save these here.
				 * Added by Reshu Chaudhary
				 */
				//if(response.getStatus()==183) // for sending PRACK for 183 as well as 180
				//{
					partyB.setAttribute(Constants.ATTRIBUTE_RSEQ_1XX_REL,response.getHeader(Constants.HDR_RSEQ));
					partyB.setAttribute(Constants.ATTRIBUTE_CSEQ_1XX_REL,response.getHeader(Constants.HDR_CSEQ));
				//}
					
			//Create the provisional response to send to the upstream...
			SipServletResponse provisionalResp = null;		
			/*String responseCode = (String) sbb.getAttribute(SBB.RTP_TUNNELLING_18X_CODE);
			int responseStatus = 0;
			if(responseCode != null && !responseCode.equals(Constants.DEFAULT)) {
				provisionalResp = requestIn.createResponse(Integer.parseInt(responseCode));
				responseStatus = Integer.parseInt(responseCode);
			}else {
				 provisionalResp = requestIn.createResponse(response.getStatus());
				 responseStatus = response.getStatus();
			}*/
			provisionalResp = requestIn.createResponse(response.getStatus());
			if(response.getContentLength() > 0){
				provisionalResp.setContent(response.getContent(), response.getContentType());
			}

			
	    	ListIterator<String> requires=response.getHeaders(Constants.HDR_REQUIRE);
			SipServletResponse preconditionProvRes = null;
			boolean preConditionReqReceived = false;
	    	while(requires.hasNext()){	
	    		String require=requires.next().trim();
	    		if(require.contains("precondition")){
	    			
//	    			if (logger.isDebugEnabled()) {
//						logger.debug("<SBB> [.....] setHeader "+Constants.HDR_SUPPORTED+" to "+require);
//					}
	    			//provisionalResp.setHeader(Constants.HDR_SUPPORTED, require);
	    			
	    			if(logger.isDebugEnabled()) {
    					logger.debug("setting precondition in REQUIRE header..");
    				}
	    			
	    			provisionalResp.addHeader(Constants.HDR_REQUIRE, require);
	    			preConditionReqReceived = true;
				}
	    	}
	    	
	    	SipApplicationSession appSession = sbb.getApplicationSession();
	    	if(preConditionReqReceived && !preconditionProvResSent) {
	    		if(logger.isDebugEnabled()) {
    				logger.debug("precondition required received, creating provisional response");
    			}
    			preconditionProvRes = requestIn.createResponse(response.getStatus());
    			String pChargingVector = (String) appSession.getAttribute(MsSessionController.P_CHARGE_VECTOR);
    			if(pChargingVector != null) {
    				if(logger.isDebugEnabled()) {
    					logger.debug("setting P-Charging-Vector in 1xx reponse to party A");
    				}
    				preconditionProvRes.setHeader(MsSessionController.P_CHARGE_VECTOR, pChargingVector);
    				if(logger.isDebugEnabled()) {
    					logger.debug("sending preconditional provisional response non-reliable");
    				}
    				preconditionProvResSent = true;
    				
    				boolean isRelSentToA = false; 
    				if(preconditionProvRes.getSession().getAttribute(Constants.IS_REL_RESP_SENT_ALREADY_SENT) != null){
    					isRelSentToA = (Boolean)preconditionProvRes.getSession().getAttribute(Constants.IS_REL_RESP_SENT_ALREADY_SENT);
    					
    					if(logger.isDebugEnabled()) {
        					logger.debug("reliable response alrady sent to A so not sending unreliable resonse");
        				}
    				}
    				
    				if(!isRelSentToA){
    					
    					sendResponse(preconditionProvRes, false);
    				}
    			}
	    	}

	    	sendResponse(provisionalResp, reliable);
			this.response1xx = response;
		}catch(IOException e){
			logger.error(e.getMessage(), e);
		}catch(Rel100Exception e){
		   logger.error("<SBB> Couldn't send as Party-A do not support 100rel", e);
		   if (logger.isInfoEnabled()) {
			   logger.info("<SBB> Canceling invite request to party-B, sending cancel to B");
		   }
		   try{
			   SipServletRequest cancel = response.getRequest().createCancel();
			   sendRequest(cancel);
		   }catch(IOException ex){
			   logger.error(ex.getMessage(), ex);
		   }
		   /*
		   * 	According to JSR 116 response of cancel will not be passed to Application, so
		   *	firing a <CONNECT_FAILED> here and not after getting 200 OK for cancel.
		   *  Also since INVITE transaction will be completed only after a 487 is receive from
		   *  B, setCompleted() should be delayed till 487 is received.	
		   */
		   if (logger.isInfoEnabled()) {
			   logger.info("<SBB> Firing <CONNECT_FAILED> to application");
		   }
		   //fire connect failed event
		   fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
               SBBEvent.REASON_CODE_A_REL100_NOT_SUPPORTED,response);
		}
	}
	}
	
	protected void handle1xxLocally(SipServletResponse response){
		
		SBB sbb = (SBB)this.getOperationContext();
		
		try{
		
		if(!SBBResponseUtil.isProvisionalResponse(response)){
			logger.debug("handle1xxLocally ==> Response not provisional. So ignoring it....");
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Handling the provisional response locally :" + response.getStatus());
		}
		
		
		//Check whether the response is reliable or not.
		
		boolean reliable = SBBResponseUtil.isReliable(response);
			if(reliable){
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> [.....] Generating the PRACK locally for the reliable "+response.getStatus()+" received from downstream");
				}
				
			//String rSeqHdr = response.getHeader(Constants.HDR_RSEQ);
	       // String cSeqHdr = response.getHeader(Constants.HDR_CSEQ);
	       // SipSession partyB = response.getSession();
	        //SipServletRequest prackToB = partyB.createRequest(Constants.METHOD_PRACK);
			/*Create Prack API is used which will take care adding the RACK header
		      As rack header is immutable and also AseWrapper expects it to be part
		      Prack Request before sending the request to stack */
	        SipServletRequest prackToB = response.createPrack();
	        
//	    	ListIterator<String> requires=response.getHeaders(Constants.HDR_REQUIRE);
			
//	    	while(requires.hasNext()){	
//	    		String require=requires.next().trim();
//	    		if(require.contains("precondition")|| require.contains(Constants.VALUE_100REL)){
//	    			
//	    			if (logger.isDebugEnabled()) {
//						logger.debug("<SBB> [.....] setHeader "+Constants.HDR_SUPPORTED+" to "+require);
//					}
//					prackToB.setHeader(Constants.HDR_SUPPORTED, require);
//					
//					if(response.getSession().equals(sbb.getB())){
//						
//						if (logger.isDebugEnabled()) {
//							logger.debug("<SBB> [.....] set SDP on PRACK as precodnition received ");
//						}
//						Object content=sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
//						String contentType=(String)sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
//						prackToB.setContent(content, contentType);
//					}
//					
//				}
//	    	}	
		
	        /*String rackHdr = rSeqHdr + Constants.SPACE + cSeqHdr;
	        if (logger.isDebugEnabled()) {
	        	logger.debug("<SBB> Created RAck header in PRACK <"+rackHdr+">");
	        }
	        prackToB.addHeader(Constants.HDR_RACK,rackHdr); */
				
				/* Adding this condition, since handle1xxResponse() is being called in both the
				 * situations when RTP_TUNNELLING == 0 or 1. So deciding whether to send an UPDATE
				 * based on that parameter is very tricky. Hence this attribute will help in 
				 * determining the UPDATE flow. Also, for creating the UPDATE, the SDP present in this
				 * 183 response will be needed, so storing it as attribute here serves both these
				 * purposes.
				 * Added by Reshu Chaudhary
				 */
				if(sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW) != null 
										&& 
				sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW).equals(AseStrings.TRUE_SMALL))
				{
					if (logger.isDebugEnabled()) {
						logger.debug("[.....] Setting RESPONSE_1XX_FROM_B attribute in PRACK being sent to B : "
								      +response.getStatus());
					}
					prackToB.setAttribute(Constants.RESPONSE_1XX_FROM_B, response);
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> Created PRACK for B ");
				}
				sendRequest(prackToB);
			}else{
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> [.....] Received non-reliable "+response.getStatus()+" from downstream");
				}
				if(response.getStatus() == 183){
					//logger.debug("<SBB> [.....] Looking up PRACK_UPDATE_FLOW");
					if(sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW) != null 
													&& 
					   sbb.getApplicationSession().getAttribute(Constants.PRACK_UPDATE_FLOW).equals(AseStrings.TRUE_SMALL))
					{
						if (logger.isDebugEnabled()) {
							logger.debug("<SBB> [.....] Found PRACK_UPDATE_FLOW to be true... So sending an UPDATE to A");
						}
						SipSession partyA = sbb.getA();
						update = partyA.createRequest(Constants.METHOD_UPDATE);
						if(response.getContentLength() > 0)
							update.setContent(response.getContent(), response.getContentType());
						
						//updateA.setAttribute(SBBOperationContext.ORIG_PRACK_FROM_PARTY_A,response);
						if (logger.isDebugEnabled()) {
							logger.debug("<SBB> [.....] Created and send UPDATE to A ");
						}
						sendRequest(update);
					}
					else
					{
						if (logger.isDebugEnabled()) {
							logger.debug("<SBB> Hence returning.. ");
						}
						return;
			}	}	}
		}catch(IOException e){
			logger.error(e.getMessage(), e);
		}catch(Rel100Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	
	private void setHeadersOnOutRequest(SipServletRequest request,
			SipServletRequest outReq) {

		String where = "setHeadersOnECSInviteRequest()";
		if (logger.isDebugEnabled()) {
			logger.debug( "Entering....."+where);
		}
		Iterator itr = request.getHeaderNames();

		while (itr.hasNext()) {
			String name = (String) itr.next();

			//Content type check applied as it will be updated afterwards depending upon whether
			//content is present in the incoming request or sdp set by the service
			if (!name.equals(AseStrings.HDR_FROM) && !name.equals(AseStrings.HDR_TO)
					&& !name.equals(AseStrings.HDR_VIA) && !name.equalsIgnoreCase(AseStrings.HDR_CSEQ)
					&& !name.equals(AseStrings.HDR_ROUTE) && !name.equalsIgnoreCase(AseStrings.HDR_CALL_ID)
					&& !name.equals(AseStrings.HDR_RECORD_ROUTE) && !name.equals(AseStrings.HDR_CONTACT)
					&& !name.equals(AseStrings.HDR_ALLOW)&& !name.equalsIgnoreCase(AseStrings.HDR_CONTENT_LENGTH)
					&& !name.equalsIgnoreCase(AseStrings.HDR_CONTENT_TYPE)) {

				
				
				/* Do not set system headers, Allow is also set by the IMX so
				 * not setting it
				 */
				ListIterator pai = request.getHeaders(name);
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> getting Headers for name " + name);
				}

				while (pai != null && pai.hasNext()) {
					String value = (String) pai.next();
					if (logger.isDebugEnabled()) {
						logger.debug("Setting Sip Header Value for Header Name " + name
									+ " is..." + value);
					}
					outReq.addHeader(name, value);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "Leaving....."+where);
		}
	}	
	
	/*protected void saveSDP(SipServletRequest request , SipServletResponse response){
		
		int contentLength = 0 ;
		String contentType = null ;
		if(request != null){
			contentLength = request.getContentLength();
			logger.info("[.....]setting sdp for request "+contentLength+"   <--->  "+request.getContentLength());
			contentType=request.getContentType();
		}else if(response != null){
			contentLength = response.getContentLength();
			logger.info("[.....]setting sdp for response"+contentLength);
			contentType=response.getContentType();
		}else{
			logger.info("[***] saveSDP called with null request and null response..");
			return ;
		}
		if (contentLength >  0)	{	
			try{
				if(contentType.startsWith(Constants.SDP_MULTIPART)){
					int sdpBpIndx=-1;
					MimeMultipart multipart = null ;
					if(request!=null)
						multipart=(MimeMultipart)request.getContent();
					else
						multipart=(MimeMultipart)response.getContent();
					int count=multipart.getCount();
					for(int i=0;i<count;i++){
						if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
					      
					    if(request!=null){  
						    request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,bp);
							request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
									multipart.getBodyPart(sdpBpIndx).getContentType());
					    }else{
					    	response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,bp);
						    response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
						    								   multipart.getBodyPart(sdpBpIndx).getContentType());
					    }
					}else {
						if(request!=null)
							logger.info("<SBB> No SDP content associated with  multipart "+request.getMethod()+" request");
						else
							logger.info("<SBB> No SDP content associated with  multipart "+response.getStatus()+" response");
					}
					
				}else{
					if(request!=null){
						logger.info("[***] setting SDP in session");
						request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,
								request.getContent());
						request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
								request.getContentType());
					}else{
						response.getSession().setAttribute(
								SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
						response.getSession().setAttribute(
								SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
					}
				}
			}catch(MessagingException e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				logger.info("Exception in saving SDP "+e);
			}catch(Exception e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				logger.info("Exception in saving SDP "+e);
			}
		}
		else {
			logger.info("<SBB> No content associated with this request/response");
		}		
	}*/
	
	protected String getIsupContentTypeForA(){
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
	
	//save sdp in session
	private void setSDP(SipServletRequest request){
		String contentType = null ;
		int contentLength = request.getContentLength();
		if (contentLength > 0){
			contentType = request.getContentType();
			try{
				if(contentType.startsWith(Constants.SDP_MULTIPART)){
					int sdpBpIndx=-1;
					MimeMultipart multipart = null ;
					multipart=(MimeMultipart)request.getContent();
					int count=multipart.getCount();
					for(int i=0;i<count;i++){
						if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
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
					      
					    request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,bp);
					    request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
								multipart.getBodyPart(sdpBpIndx).getContentType());
					}else {
						if (logger.isInfoEnabled()) {
							logger.info("<SBB> No SDP content associated with  multipart "+request.getMethod()+" request");
						}
					}
					
				}else{
						if (logger.isInfoEnabled()) {
							logger.info("[***] setting SDP in session");
						}	
						request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,
								request.getContent());
						request.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,
								request.getContentType());
				}
			}catch(MessagingException e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				if (logger.isInfoEnabled()) {
					logger.info("Exception in saving SDP "+e);
				}
			}catch(Exception e){
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				if (logger.isInfoEnabled()) {
					logger.info("Exception in saving SDP "+e);
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> No content associated with this request/response");
			}
		}	
	}
	
	
	public boolean isInviteWithoutSDP(){
	    return this.initInvWithoutSDP;
	}
	
	

	
}
