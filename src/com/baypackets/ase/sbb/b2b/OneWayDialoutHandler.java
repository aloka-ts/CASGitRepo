/*
 * @(#)OneWayDialout.java	1.0 8 July 2005
 */

package com.baypackets.ase.sbb.b2b;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.mediaserverstatistics.MediaServerStatisticsManager;
import com.baypackets.ase.sbb.timer.TimerInfo;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTimerInfo;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

/**
 * Implementation of the dialout handler.
 * This class is responsible for handling dialout operation. 
 * OneWayDialout operation handles the signalling level details 
 * for connecting two parties to each other into a back-to-back user
 * agent session. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class OneWayDialoutHandler extends BasicSBBOperation {
	
	private static final long serialVersionUID = -434204380140970331L;
    private transient SipServletRequest requestOut = null;
    private transient SipApplicationSession  appSession  = null;
    private transient SipSession requestInSession = null;
    private transient SipServletRequest reInvite = null;
    private transient SipServletRequest update = null;
    private boolean receivedFinalRespForInvite = false;
    private boolean receivedFinalRespForReInvite = false;
    private boolean receivedNon2XXFinalRespForReInvite = false;
	private transient ServletTimer noAnsTimerForInvite = null;
	
	
	private boolean received2XXFinalRespForInvite = false;
	private boolean received2XXFinalRespForReInvite = false;
	// No-Ans timeout in seconds
	private int noAnswerTimeout = 60;
  
    public static final int NO_REINVITE  = 1;
    public static final int REINVITE_ON_2XX = 2;
    public static final int REINVITE_ON_1XX = 3;

    public static final int PARTY_UNDEFINED = -1;
    public static final int PARTY_A = 0;
    public static final int PARTY_B = 1;
	private static final String TIMER = "timer";

    public static String NO_INVITE_WITHOUT_SDP = "false";
    // require it to differentiate whether to send an ack now or later.
	private boolean rtp_but_no_1xx = false ;

    /** Logger element */
    private static Logger logger = Logger.getLogger(OneWayDialoutHandler.class.getName());

	/** Address of the Sender **/
	private Address fromAddress = null;
		
	/** Address of party to be dialed out */
	private Address dialoutPartyAddr = null;
	
	/** dialout party is party-A or party-B */
	private int partyId = PARTY_UNDEFINED;

	/* RTP tunneling flag */
	private int isRTPTunnelingEnabled = 0;	
	
	private int reInviteType = REINVITE_ON_2XX;
	
	private static ConfigRepository m_configRepository  = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
	private static String mediaStatsEnable = (String) m_configRepository.getValue(com.baypackets.ase.util.Constants.MEDIA_STATS_DB_STORE_ENABLE);
	
	private String minSEValue=BaseContext.getConfigRepository().getValue(Constants.MIN_SE_TIMEOUT);
	private byte[] rel_isup = {(byte)0x0c, (byte)0x02, (byte)0x00, (byte)0x02, (byte)0x83, (byte)0xbf};
    private static int releaseCause=Integer.parseInt(BaseContext.getConfigRepository().getValue(Constants.SESSION_EXPIRES_DEFAULT_ISUP_RELEASE_CAUSE));
	private int dialogState = -1;
	
	private static String requestPendingTimerVal = (String) m_configRepository.getValue(Constants.REQUEST_PENDING_TIMER);
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public OneWayDialoutHandler() {
		super();
	}
	
	public OneWayDialoutHandler(Address dialoutAddr) {
		this(null, dialoutAddr);
	}

	/**
     	 * 	Constructor for creating instance of OneWayDialoutHandler.
	 *	@param dialoutPartyAddr: Address of party to be dialed out.		
	 *	@param partyId: 
	 *
         */
	public OneWayDialoutHandler(Address from, Address dialoutPartyAddr) {
		this.fromAddress = from;
		this.dialoutPartyAddr = dialoutPartyAddr;
	}

	public void setReInviteType(int type){
		this.reInviteType = type;
	}

	public int getReInviteType(){
		return this.reInviteType;
	}


	/**
     * This method will be invoked to start dialout operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     *
     */
	public void start() throws ProcessMessageException, IllegalStateException {
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB>entered start() ");
		}

		SBB sbb = (SBB)getOperationContext();
		// basic validations 
		if (sbb.getA() == null && sbb.getB() == null) {
			logger.error("Error: No associated party found ");
			throw new IllegalStateException("No associated party found");
		}
		
		if (sbb.getA() != null) {
			requestInSession = sbb.getA();
			dialogState = ((Integer)requestInSession.getAttribute(
								 Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

			//If dialog state is early then need to send update request in place of 
			//reinvite
			if (dialogState != Constants.STATE_CONFIRMED && dialogState != Constants.STATE_EARLY) {
				logger.error("Invalid Dialog state for dialout operation");
				throw new IllegalStateException("Invalid Dialog state for dialout operation");
			}
            		partyId = PARTY_A;
            		try {
            			if (logger.isDebugEnabled()) {
            				logger.debug("Setting SBBServlet as handler on RequestIn Session");
            			}
            			requestInSession.setHandler("SBBServlet");
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						logger.error("error in setting handler as SBBServlet for dialout " +e);
					}
        } else {
			requestInSession = sbb.getB();
			dialogState = ((Integer)requestInSession.getAttribute(
	                          Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			//If dialog state is early then need to send update request in place of 
			//reinvite
			if (dialogState != Constants.STATE_CONFIRMED && dialogState != Constants.STATE_EARLY) {
	        	logger.error("Invalid Dialog state for dialout operation");
	                		throw new IllegalStateException("Invalid Dialog state for dialout operation");
        	}
        	partyId = PARTY_B;
        	try {
        		if (logger.isDebugEnabled()) {
        			logger.debug("Setting SBBServlet as handler on RequestIn Session");
        		}
    			requestInSession.setHandler("SBBServlet");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("error in setting handler as SBBServlet for dialout " +e);
			}
        }
        appSession = requestInSession.getApplicationSession();

		//Identify the re-INVITE type...
		Integer rtpTunneling = (Integer)sbb.getAttribute(SBB.RTP_TUNNELLING);
		if(rtpTunneling != null && rtpTunneling.intValue() == Constants.RTP_TUNNELING_ENABLED){
			this.reInviteType = REINVITE_ON_1XX;
		}
		if (logger.isInfoEnabled()) {
			logger.info("<SBB> RE-INVITE Type:: "+ this.reInviteType);
		}

		try {
			SipFactory factory = (SipFactory)sbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			
			if(fromAddress == null){
				String strUri = requestInSession.getRemoteParty().toString();
				fromAddress = factory.createAddress(strUri);
			}
			Iterator itor = fromAddress.getParameterNames();
			if(itor != null) {
				while(itor.hasNext()) {
					fromAddress.removeParameter(itor.next().toString());
				}
			} else {
				if(logger.isDebugEnabled()) {
					logger.debug("<SBB> No Parameter Present in from Address");
				}
			}
			
			requestOut = factory.createRequest(appSession,Constants.METHOD_INVITE,fromAddress,dialoutPartyAddr);
			if (logger.isDebugEnabled()) {
				logger.debug("<<<<<<<<<<GETTING ATTRIB SEND_SDP_IN_INVITE_DIALOUT >>>>>>>>>>SDP = "+ (String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP));
			}
			//sending SDP in INVITE to B party in case of dialout| changes for sending SDP in Re-invite | start
			NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
			
			
			String msInviteWithoutSdp=(String)sbb.getAttribute(
					MsSessionController.MS_INVITE_WITHOUT_SDP);
			
			// if we want to send SDP in invite in case of Dialout scenario then
			// this flag needs to set in the servletcontext.This flag can be set
			// from SCE by checking the NO invite without SDP from project
			// properties.This flag is set in sip.xml file in the sar file
			// deployed on server.
			//sending SDP in INVITE to B party in case of dialout| changes for sending SDP in Re-invite | End
			if (partyId == PARTY_A) {
				// retrieving initial INVITE request from A from application session.
				SipServletRequest initReq = (SipServletRequest)sbb.getApplicationSession().getAttribute(Constants.REQUEST_FROM_A);
				if (logger.isDebugEnabled()) {
					logger.debug("[..........] Setting Headers...!!!");
				}
				if(initReq!=null){
					this.setHeadersOnOutRequest(initReq, requestOut);
				}else {
					if (logger.isDebugEnabled()) {
						logger.debug("[..........] Problem in getting the attribute...");
					}
				}
				sbb.addB(requestOut.getSession());
				
				//requestOut.setContent(appSession.getAttribute(arg0), "application/sdp");
				// sending SDP in INVITE to B party in case of dialout| changes for sending SDP in Re-invite | start
				//if the flag is not null and is selected then we append the SDP in the request 
				if(null!=NO_INVITE_WITHOUT_SDP && AseStrings.TRUE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP)|| (msInviteWithoutSdp == null || msInviteWithoutSdp.isEmpty())){
					if (logger.isDebugEnabled()) {
						logger.debug("<<<<<<<<<<setting SDP of A in the request>>>>>>>>>>SDP ="+sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP));
					}
				//	logger.debug("<<<SDP TYPE="+(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString());
					boolean isDialOutCall = false;
					if(appSession.getAttribute("P_DIALOUT") != null){
						isDialOutCall = (Boolean) appSession.getAttribute("P_DIALOUT");
					}
					/**
					 * In case of Dialout call when we need to connect B party to media server
					 * MS sbb wouldn't have SDP_CONTENT type of B, so explicitly setting it.
					 */
					if(isDialOutCall && sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE) == null){
						sbb.getA().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE, "application/sdp");
					}
					requestOut.setContent(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) ,
	                        (sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString() );
					// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end		
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("[.....] Oneway Dialing-Out to Party-A");
				}
				sbb.addA(requestOut.getSession());
				// sending SDP in INVITE to B party in case of dialout | start
				//if the flag is not null and is selected then we append the SDP in the request
				if(null!=NO_INVITE_WITHOUT_SDP && AseStrings.TRUE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP)){
					if (logger.isDebugEnabled()) {
						logger.debug("<<<<<<<<<<setting SDP of B in the request>>>>>>>>>>SDP ="+sbb.getB().getAttribute(SBBOperationContext.ATTRIBUTE_SDP));
					}
				//	logger.debug("<<<SDP TYPE="+(sbb.getB().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString());
					requestOut.setContent(sbb.getB().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) ,
	                        (sbb.getB().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString() );
				}// sending SDP in INVITE to B party in case of dialout | end
			}
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Sending INVITE request to dialout party :: "+requestOut);
			}
			requestOut.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST, requestOut);
			requestOut.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT, requestOut);
			

			//For Media Server Stats
			if(mediaStatsEnable.equals(AseStrings.TRUE_SMALL)){
				try{
					SipURI uri =  (SipURI)requestOut.getRequestURI();
					String voiceXmlPath  = uri.getParameter("voicexml");
					if(voiceXmlPath != null){
						//Getting voiceXmlPath path without encoding 
						voiceXmlPath =  voiceXmlPath.substring(0, voiceXmlPath.indexOf("%"));

						MediaServerStatisticsManager mediaServerStatisticsManager = MediaServerStatisticsManager.getInstance();
						//passing MsOperationSpec as null,play duration as zero and voiceXmlPath
						mediaServerStatisticsManager.setStaticsInfo(null,0,voiceXmlPath+",");

					}
				}catch (Exception e) {
					logger.error("Exception in start() while setting statistics info : "+e);
				}
			}
			// END 
			
			//BPInd18612
			long timeout = noAnswerTimeout*1000;
			if( timeout > 0){
				if(logger.isDebugEnabled()) {
					logger.debug("<SBB> start(): Creating a no-ans timer to fire in "+timeout/1000 +" seconds");
				}
				TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
				this.noAnsTimerForInvite = timerService.createTimer(appSession,timeout,false,this);
			}
			//----add session expires header value in request-----------------
			//String supportedHeaderValue =requestOut.getHeader(Constants.HDR_SUPPORTED);
			String supportedHeaderValue = null;
			boolean timerSupportedPresent = false;
			Iterator valueList = requestOut.getHeaders(Constants.HDR_SUPPORTED);
			while(valueList!=null && valueList.hasNext()){
				supportedHeaderValue = (String) valueList.next();
				if(supportedHeaderValue!=null && supportedHeaderValue.contains(TIMER)){
					timerSupportedPresent = true;
					break;
				}
			}
			// reeta only sending session -expiry if minSE is configured in ase.properties
				
		if(minSEValue!=null && !minSEValue.equals("")){
			
			if(!timerSupportedPresent)
				requestOut.addHeader(Constants.HDR_SUPPORTED, TIMER);				
			
			String newRefresher = "refresher=uas";
			String sessionExpiryValue = minSEValue + ";" + newRefresher;
			
			if(requestOut.getHeader(Constants.HDR_MIN_SE)!=null){
			     requestOut.setHeader(Constants.HDR_MIN_SE, minSEValue);
			     sbb.getA().setAttribute("MIN_SE",requestOut.getHeader(Constants.HDR_MIN_SE));
				 sbb.getB().setAttribute("MIN_SE",this.minSEValue);
			}else{
			    requestOut.addHeader(Constants.HDR_MIN_SE,minSEValue);
			    sbb.getA().setAttribute("MIN_SE",this.minSEValue);
				sbb.getB().setAttribute("MIN_SE",this.minSEValue);
			}
			
			if(logger.isDebugEnabled()){
		        logger.debug("Session Expiry to be set in b request :" + sessionExpiryValue);
			}
			requestOut.setHeader(Constants.HDR_SESSION_EXPIRES, sessionExpiryValue);
		}
			
//			if (valueList == null){
//				
//			}else {
//				while(valueList!=null && valueList.hasNext()){
//					supportedHeaderValue = (String) valueList.next();
//					if(supportedHeaderValue!=null && supportedHeaderValue.contains(TIMER)){
//						String newRefresher = "refresher=uas";
//						String sessionExpiryValue = minSEValue + ";" + newRefresher;
//						if(requestOut.getHeader(Constants.HDR_MIN_SE)!=null){
//						     requestOut.setHeader(Constants.HDR_MIN_SE, minSEValue);
//						     sbb.getA().setAttribute("MIN_SE",requestOut.getHeader(Constants.HDR_MIN_SE));
//							 sbb.getB().setAttribute("MIN_SE",this.minSEValue);
//						}else{
//						    requestOut.addHeader(Constants.HDR_MIN_SE,minSEValue);
//						    sbb.getA().setAttribute("MIN_SE",this.minSEValue);
//							sbb.getB().setAttribute("MIN_SE",this.minSEValue);
//						}
//						if(logger.isDebugEnabled()){
//					        logger.debug("Session Expiry to be set in b request :" + sessionExpiryValue);
//						}
//						requestOut.setHeader(Constants.HDR_SESSION_EXPIRES, sessionExpiryValue);
//					    break;
//			     	}
//				}
//			}
			
		
			
			
			
			//---------------------------
			//if X-ISC-SVC atf header is present then its must be remove
			if(requestOut.getHeader("X-ISC-SVC")!=null){
				requestOut.removeHeader("X-ISC-SVC");
			}

				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> DISABLE_OUTBOUND_PROXY..");
				}
				
				disableObgwproxy(sbb.getApplicationSession(),
						requestOut.getSession(), requestOut);

			//requestOut.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, AseStrings.BLANK_STRING);
			sendRequest(requestOut);
		
		}catch(Exception exp) {
			logger.error(exp.getMessage(),exp);	
			throw new ProcessMessageException(exp.getMessage());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB>exited start() ");
		}
    	}


	public void ackTimedout(SipSession session) {
    	}

	public void prackTimedout(SipSession session) {
    	}


	public void handleRequest(SipServletRequest request) {

		logger.error("<SBB> iReceived unexpected request :: "+request);
	}


	/**
     	*  This method  handles all response. Since this is a dialout to both parties
     	*  response can come from any party
     	*
     	* @response - Response from party.
     	*/
    	public void handleResponse(SipServletResponse response) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("<SBB> entered handleResponse with following response with <"+
               		response.getStatus()+","+response.getMethod()+">");
    		}

        	// handle INVITE responsed
        	if (response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {
            		handleInviteResponse(response);
        	}else {
            		handleNonInviteResponse(response);
        	}
        	
        	//This is done to send the connect failed event to Service in case of non 2xx response
        	//received for reinvite sent to Party-A as this is not happening when we are sending
        	//BYE to Media Server.
        	if (response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE) && receivedNon2XXFinalRespForReInvite) {
        		fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE, response);
        	}
        	if (logger.isDebugEnabled()) {
        		logger.debug("<SBB> exited handleResponse");
        	}
    	}
	


	/**
	 *Since this is dialout handler this methods can get responses from both the parties
	 */	
	private void handleInviteResponse(SipServletResponse response)	{

		/*
         	 *  Folowing respons are expected respose to INVITE request
		 *
         	 *  1.  100 TRYING      :  Log and ignore      
         	 *  2.  1xx             :  Log and ignore
		 *  3.  1xx  REL        :  Extract the SDP & send PRACK.
		 *		If REINVITE_ON_1XX flag is ON initiate a reINVITE to other party.										
		 *  4.  2xx from 		:  Extract the SDP & initiate a reINVITE to 
		 *		dialout party	   other party if RTP tunneling is ON, otherwise
		 *						   just ignore the message as reINVITE is already
		 *						   initiated.	
		 *  5.	2xx from other
		 *		party			:	send ack & fire CONNECTED.
		 *	
         	 *  6.  Non 2xx Final
		 *      from party 
		 *      dialout,
	     	 *      for INVITE		:  fire DIAL_OUT_FAILED	  
		 *  7.  Non 2xx Final
		 *      from other 
		 * 		party B			:  send ACK, BYE to dialout party 
		 *							 & fire DIAL_OUT_FAILED
		 *                                      
         	 */
		int contentLength = response.getContentLength();
		SBB sbb = (SBB)getOperationContext();
		try {

			// 100 Trying from anywhere
			if (SBBResponseUtil.is100Trying(response)) {
				if (logger.isInfoEnabled()) {
					logger.info("<SBB>Received 100 Trying ");
				}
				return;
			}

			// Provisional responses except 100 from dialed out party only
			if (SBBResponseUtil.isProvisionalResponse(response) &&
				response.getRequest() == requestOut) {

				if (reInviteType == REINVITE_ON_1XX) {
					
					//If it is the first 1xx response,
					//then send it upstream with a re-INVITE (OR) handle it locally.
					if(reInvite == null){
						this.send1xxUpstream(response);
					}else{
						this.handle1xxLocally(response);
					}
				}else {
					this.handle1xxLocally(response);
				}
				return;
			}

			// Already received a 2xx. This is a subsequent 2xx responses from dialed out party
			if (SBBResponseUtil.is2xxFinalResponse(response) && 
				response.getRequest() == requestOut && receivedFinalRespForInvite) {
				
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received another 2xx response from downstream");
				}

			    	//This is the case of the MFRs.
			    	//if we already got a 200 Ok, terminate this leg.
			    	//So we need to ack and bye this response 
			    	
			    	//Create an ACK and send out....
			    	SipServletRequest ackOut = response.createAck();
			    	try{
			    		this.sendRequest(ackOut);
			    	}catch(IOException e){
			    		logger.error(e.getMessage(), e);
			    	}
			    	
			    	//Create a BYE and send out...
			    	SipServletRequest byeOut = response.getSession().createRequest(Constants.METHOD_BYE);
			    	try{
			    		this.sendRequest(byeOut);
			    	}catch(IOException e){
			    		logger.error(e.getMessage(), e);
			    	}
			    	return;
			}
			
			// First 2xx response from dialed out party
			if (SBBResponseUtil.is2xxFinalResponse(response) && 
				(response.getRequest() == requestOut && !receivedFinalRespForInvite)){
	            
				// cancel the timer if not NULL
				if(this.noAnsTimerForInvite != null) {
					this.noAnsTimerForInvite.cancel();
				}

				//other leg is terminated but we received 200ok for invite so both leg must be clean
				if ((!(requestInSession.isValid()) || requestInSession.getState() == State.TERMINATED)) {

					logger.error("get 2xx FOR invite, dialout party is terminated.");
					if (response.getSession().isValid()
							&& response.getSession().getState() != State.TERMINATED) {
						logger.error("Cleaning media server leg");
						response.getSession().setAttribute(
								SBBOperationContext.ATTRIBUTE_INV_RESP,
								response);
						response.getSession().setAttribute(Constants.IS_OTHER_LEG_TERMINATED, AseStrings.TRUE_SMALL);
						//failing media party as other leg is terminated
						this.failDialoutParty(response.getSession(), response);
					}
					return;
				}
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received first 2xx response from downstream");
				}
				
				
				SipServletRequest pendingAckRequest = (SipServletRequest)appSession.getAttribute("P_PENDING_ACK");
				if(logger.isDebugEnabled()){
					logger.debug("sending pending Ack for completing offer-answer in case of dialout");
				}
				if(pendingAckRequest != null){
					pendingAckRequest.setContent(response.getContent(), response.getContentType());
					appSession.removeAttribute("P_PENDING_ACK");
					pendingAckRequest.send();
				}
				//start session-expires timer
				
				if (minSEValue != null && !minSEValue.equals("")) {
					this.startSessionExpiryTimer(response,
							Constants.TIMER_FOR_MS,
							Constants.SESSION_EXPIRY_TIMER_FOR_MS);
				}
				//Set the finalResponse Received Flag....
				receivedFinalRespForInvite = true;

				
				//In case of MFRs, if we get the 200 Ok from another session,
				//then swap the original session with the newly created session...
				//This will be the case, if we already got a 1xx from the original session but not the 2xx
				if(response.getSession() != requestOut.getSession()){
					SipSession dialedParty = requestOut.getSession();
					if(sbb.getA() == dialedParty){
    						sbb.removeA();
    						response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
    						sbb.addA(response.getSession());
    					}else if(sbb.getB() == dialedParty){
    						sbb.removeB();
    						response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
    						sbb.addB(response.getSession());
    					}
				}
				
				// extracting party B SDP and storing in session
				contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
				}
				else {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> No content associated with response request");
					}
				}
                
				//preserve the 200 OK response from dialed out party,
				//so that an ack can be created later on
				//sbb.getB().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP,response);
				response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP,response);
			

				//  Send reINVITE to connected party in case RTP-Tunneling is not supported
				if(logger.isDebugEnabled()){
					logger.debug("Re-INVITE type ::"+ reInviteType);
				}
                if (reInviteType == REINVITE_ON_2XX && reInvite == null && dialogState == Constants.STATE_CONFIRMED) {
                	if (logger.isInfoEnabled()) {
                		logger.info("<SBB> Sending reINVITE to connected party with dialout SDP");
                	}
                	reInvite = requestInSession.createRequest(Constants.METHOD_INVITE);
					if (contentLength > 0) {
                    				reInvite.setContent(response.getContent(),response.getContentType());
                    				reInvite.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
					}
					if (minSEValue != null && !minSEValue.equals("")) {
						reInvite.setHeader(Constants.HDR_SUPPORTED, TIMER);
						reInvite.setHeader(Constants.HDR_SESSION_EXPIRES,
								minSEValue + ";refresher=uas");
						if (logger.isDebugEnabled()) {
							logger.debug("Default value of Session Expires set in Invite request :"
									+ minSEValue);
						}
					}
					sendRequest(reInvite);
                }else if(reInviteType == REINVITE_ON_1XX && reInvite == null && dialogState == Constants.STATE_CONFIRMED) {
                	if (logger.isInfoEnabled()) {
                		logger.info("<SBB> [.....] Though RTP_TUNNELLING was enabled, still received a 2xx response and no re-invite has been sent yet.");
                		logger.info("So now sending reINVITE to connected party with dialout SDP");
                	}
                	rtp_but_no_1xx = true ;
                    reInvite = requestInSession.createRequest(Constants.METHOD_INVITE);
                    if (contentLength > 0) {
                       	reInvite.setContent(response.getContent(),response.getContentType());
                       	reInvite.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
                    }
					if (minSEValue != null && !minSEValue.equals("")) {
						reInvite.setHeader(Constants.HDR_SUPPORTED, TIMER);
						reInvite.setHeader(Constants.HDR_SESSION_EXPIRES,
								minSEValue + ";refresher=uas");
						if (logger.isDebugEnabled()) {
							logger.debug("Default value of Session Expires set in Invite request :"
									+ minSEValue);
						}
					}
                    sendRequest(reInvite);
				}else if(dialogState == Constants.STATE_EARLY){
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Sending UPDATE to connected party with dialout SDP as connected party is in early state");
					}
                	update = requestInSession.createRequest(Constants.METHOD_UPDATE);
                	 if (contentLength > 0) {
                		 update.setContent(response.getContent(),response.getContentType());
                		 update.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
                     }
                	sendRequest(update);
				}
                //This is done in case disconnect happens after the dialout operation as in disconnect
                //we retrieve the initial invite sent to the Party-A
                if (reInvite != null)
                	requestInSession.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,reInvite);
                else
                	requestInSession.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,requestInSession.getAttribute(com.baypackets.ase.util.Constants.ORIG_REQUEST));
				
                //Incase of no Re-INVITE, the reInvite would be NULL...
				//So send out an ACK and raise a CONNECTED event to the application....
                if(!rtp_but_no_1xx && reInviteType == REINVITE_ON_1XX && reInvite != null) {
                	if (logger.isInfoEnabled()) {
                		logger.info("<SBB> [.....] Sent Re-INVITE to A, Received 200 from B, so sending ACK");
                	}
					SipServletRequest ackToDialoutParty = response.createAck();
					// not send SDP in ACK to B party in case of dialout | changes for sending SDP in Re-invite| start
        			NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
        			if (logger.isDebugEnabled()) {
        				logger.debug("<<<<<<<<<< VLAUUE OF NO_INVITE_WITHOUT_SDP IN handleInviteResponse>>>>>>>>>>SDP = "+ NO_INVITE_WITHOUT_SDP);
        			}
        			if(null==NO_INVITE_WITHOUT_SDP || AseStrings.FALSE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))						
        			    ackToDialoutParty.setContent(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) ,
        			(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString() );
        			// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end
        					
					sendRequest(ackToDialoutParty);
					synchronized (this){
						received2XXFinalRespForInvite = true;
						if (received2XXFinalRespForReInvite){
							setCompleted(true);
							fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
						}
					}
					
					//setCompleted(true);
					// fire CONNECTED event
					//fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
				}else if (reInviteType == NO_REINVITE && reInvite == null && update == null){
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Skipping reINVITE, Sending ACK and completing.... ");
					}
					SipServletRequest ackToDialoutParty = response.createAck();
					// not send SDP in ACK to B party in case of dialout | changes for sending SDP in Re-invite| start
        			NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
        			if (logger.isDebugEnabled()) {
        				logger.debug("<<<<<<<<<< VLAUUE OF NO_INVITE_WITHOUT_SDP IN handleInviteResponse>>>>>>>>>>SDP = "+ NO_INVITE_WITHOUT_SDP);
        			}
        			if(null==NO_INVITE_WITHOUT_SDP || AseStrings.FALSE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))						
        			    ackToDialoutParty.setContent(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) ,
        			(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString() );
        			// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end
        			received2XXFinalRespForInvite = true;		
					sendRequest(ackToDialoutParty);
					setCompleted(true);
					// fire CONNECTED event
					fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
				}
                received2XXFinalRespForInvite = true;
				return;
            }
			// Non-2xx final response from dialed out party
			if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
				response.getRequest() == requestOut) {

				// Non 2xx final responses  (i.e. 3xx,4xx,5xx and 6xx)
				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received a Non-2xx final response from dialed out party"+
							", Firing connect failed event");
				}
				
				if(response.getStatus() == 422){
					
					SipServletRequest initReq = (SipServletRequest)sbb.getApplicationSession().getAttribute(Constants.REQUEST_FROM_A);
					if(logger.isDebugEnabled()){
					    logger.debug("get 422 response of "+response.getMethod());
					}
					String deltaSec = response.getHeader(Constants.HDR_MIN_SE);
					if(Integer.valueOf(deltaSec)>Integer.valueOf(this.minSEValue)){
						if(logger.isDebugEnabled()){
						    logger.debug("min-se value receive in 422 is greater then default value set in ase.properties");
						}
						response.getSession().setAttribute("MIN_SE", deltaSec);
					}else{
						deltaSec = this.minSEValue;
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
					this.setHeadersOnOutRequest(initReq, requestOut);
					requestOut.setHeader(Constants.HDR_SESSION_EXPIRES, sessionExpHeadVal);
					if( requestOut.getHeader(Constants.HDR_MIN_SE)!=null){
						requestOut.setHeader(Constants.HDR_MIN_SE, deltaSec);
					}else{
						requestOut.addHeader(Constants.HDR_MIN_SE, deltaSec);
					}
					
					if(initReq.getContentLength()>0){
						requestOut.setContent(initReq.getContent(), initReq.getContentType());
					}
					sendRequest(requestOut);
					if(logger.isDebugEnabled()){
					    logger.debug("invite request send Min-SE header added");
					}
					return;
				}
				//-------------------
				
				//Set the finalResponse Received Flag....
				receivedFinalRespForInvite = true;

				// cancel the timer if not NULL
				if(this.noAnsTimerForInvite != null) {
					this.noAnsTimerForInvite.cancel();
				}

				
				this.failOriginatingParty(response);

				int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE, response);
				
				
				
				// Bug 10511
				if(retValue == SBBEventListener.CONTINUE) {
					if (logger.isInfoEnabled()) {
						logger.info(" Received CONTINUE from application so clearing the sip session of Party A");
					}
					try 
					{
						if( !(requestInSession.isValid()) || requestInSession.getState()==State.TERMINATED){
							if (logger.isDebugEnabled()) {
								logger.debug("get 3xx or 4xx, dialout party is terminated.");
							}
							setCompleted(true);
							return;
						}
						
						int state = ((Integer)requestInSession.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

						byte[] rel_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.REL_ISUP);
						//for SIP-T calls
						if(rel_isup != null) {
							//Getting IllegalStateExcetion in the case if control goes to else block.In order to resolve it, 
							//creating response inside if block.

							Multipart mp = new MimeMultipart();
							if (logger.isInfoEnabled()) {
								logger.info("setting REL to Party A");
							}
							String contentType = getIsupContentTypeForA(); 
							if (contentType == null)
								SBBResponseUtil.formMultiPartMessage(mp, rel_isup, Constants.ISUP_CONTENT_TYPE,null);
							else
								SBBResponseUtil.formMultiPartMessage(mp, rel_isup, contentType, null);

							String reasonHdr = (String) sbb.getAttribute(MsSessionController.PARTY_A_REASON_HDR);
							
							if(state == Constants.STATE_EARLY)  {
								SipServletResponse resOut = ((AseSipSession)requestInSession).getOrigRequest().createResponse(Constants.RESP_IVR_NOT_AVAILABLE);
								resOut.setContent(mp, mp.getContentType());
								//FIXME::reason header service should provide??
				            	if (reasonHdr != null){
				            		resOut.addHeader(Constants.HDR_REASON, reasonHdr);
				            	}else{
				            		//resOut.addHeader(Constants.HDR_REASON,Constants.REASON_HDR_ISUP_VAL);	
				            	}
								sendResponse(resOut, false);
							} else if(state == Constants.STATE_CONFIRMED){
								SipServletRequest reqOut = requestInSession.createRequest(Constants.METHOD_BYE);
								reqOut.setContent(mp, mp.getContentType());
								//FIXME::reason header required; service should provide??
								if (reasonHdr != null){
									reqOut.addHeader(Constants.HDR_REASON, reasonHdr);
								}else{
									//reqOut.addHeader(Constants.HDR_REASON,Constants.REASON_HDR_ISUP_VAL);
								}
								sendRequest(reqOut);
							} 
						}else {
							if(state == Constants.STATE_EARLY)  {
								sendResponse(((AseSipSession)requestInSession).getOrigRequest().createResponse(Constants.RESP_DEFAULT_4XX),false);
							} else if(state == Constants.STATE_CONFIRMED) {
								sendRequest(requestInSession.createRequest(Constants.METHOD_BYE));
							}
						}
						setCompleted(true);
					}
					catch(Rel100Exception r100e) 
					{
						logger.error("cann't handle ",r100e);
					} catch (MessagingException e) {
						logger.error("<SBB> Could not add MIME to response", e);	
					}
				} else {
					setCompleted(true);
				}
				return;
			}


			// 2xx response to reINVITE from connected party
			if ((SBBResponseUtil.is2xxFinalResponse(response) && 
						response.getRequest() == reInvite)) {
				//---start session expiry timer for A_party-
			    this.startSessionExpiryTimer(response, Constants.TIMER_FOR_A_PARTY, Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
				//TODO: Need to make changes here for sessin expiry
			    if (logger.isInfoEnabled()) {
			    	logger.info("<SBB> Received 2xx response to reINVITE from connected party");
			    }
				
				receivedFinalRespForReInvite = true;
				// store the latest SDP in session and also pass this SDP to dialout party
			  	// as ACK content.
				contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
				}
				else {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> No content associated with response request");
					}
				}
				
				SipSession dialedSession = (sbb.getA() == response.getSession()) ? sbb.getB() : sbb.getA();
				if (logger.isDebugEnabled()) {
					logger.debug("Dialed Session ID :" + dialedSession.getId());
				}
				SipServletResponse previousOkResponse = (SipServletResponse)
							dialedSession.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				/* Modifying this piece of code to incorporate the case when RTP_TUNNELLING is enabled
				 * (which has been made a default behavior for B2BUA routing). Here this segment of 
				 * code was being executed even on receiving a 200OK for reInvite sent to party A. 
				 * This caused an exception while trying to generate an ACK request, since reInvite
				 * is initiated as soon as 183 is received from downstream.
				 */
				SipServletRequest ackToDialoutParty = null ;
				if(reInviteType==REINVITE_ON_2XX || rtp_but_no_1xx )
				{   // remove the attribute from session before sending ACK
					received2XXFinalRespForReInvite = true;
					dialedSession.removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
					ackToDialoutParty = previousOkResponse.createAck();
					if (contentLength > 0) {
						// sending SDP in INVITE to B party in case of dialout | changes for sending SDP in Re-invite| start
						NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
						if (logger.isDebugEnabled()) {
							logger.debug( "<<<<<<<<<< VLAUUE OF NO_INVITE_WITHOUT_SDP IN handleInviteResponse>>>>>>>>>>SDP = "+ NO_INVITE_WITHOUT_SDP);
						}
						if(null==NO_INVITE_WITHOUT_SDP || AseStrings.FALSE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))						
						ackToDialoutParty.setContent(response.getContent(),response.getContentType());
						// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end
					}
					sendRequest(response.createAck());
					sendRequest(ackToDialoutParty);
					setCompleted(true);
	
					// fire CONNECTED event
					fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
				}
				else{
					sendRequest(response.createAck());
					
					// fire CONNECTED event
					//There is a contention situation in case of when reinvite type is REINVITE_ON_1XX
					// and reinvite is sent to A when provisional response (with SDP) is received (or 
					//we can say the variable rtp_but_no_1xx is false.
					//Now in this case there is a possibility that 200 ok for reinvite and 200 OK from
					//Party-B can be received in any following order:
					//1) 200 OK for reinvite comes first
					//2) 200 OK from Party-B comes first
					//3) 200 OK from Party-B and reinvite comes at the same time
					//Now in order to handle these case we need to add the proper synchronized handling
					//to ensure that the completed is not set to true in the case where other 200 OK 
					//is yet to be processed, other wise other message would be stray.
					synchronized (this) {
						received2XXFinalRespForReInvite = true;
						if (received2XXFinalRespForInvite){
							setCompleted(true);
							fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
						}
					}
				}
					
				return;
			}
          
			// Non 2xx response to reINVITE from connected party
			if ((SBBResponseUtil.isNon2xxFinalResponse(response) &&
					response.getRequest() == reInvite)) {
				
				logger.error("<SBB> Received a Non-2xx final response for reINVITE from connected party" + 
						response.getStatus());
				receivedFinalRespForReInvite = true;
				
				
				//Handling for the race condition:
				//
				if (response.getStatus() == SipServletResponse.SC_REQUEST_PENDING){
					
					Double timerDuration = Double.valueOf(requestPendingTimerVal);
					timerDuration = timerDuration*1000;
					TimerService ts=(TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
					//ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration, false, new TimerInfo(servletTimer,this));
					//--changes for ft---------
					AseTimerInfo aseTimerInfo = new AseTimerInfo();
					aseTimerInfo.setSbbName(sbb.getName());
					ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration.longValue(), false, aseTimerInfo);
					TimerInfo timerInfo = new TimerInfo(Constants.TIMER_FOR_REQ_PEND,this);
					((SBBImpl)sbb).setServletTimer(timer.getId(), timerInfo);
					logger.error("Timer Created for pending request to be answered");
					return;
				}
				//This is after the handling for 419 as we need to fail the dialout party
				//nor we need to raise any error event to the service
				receivedNon2XXFinalRespForReInvite = true;
				//CONNECT_FAILED will be handled by the response for the BYE request.
				SipSession dialedSession = (sbb.getA() == response.getSession()) ? sbb.getB() : sbb.getA();
				this.failDialoutParty(dialedSession,response);
			}
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
		}
	}



	private void handleNonInviteResponse(SipServletResponse response) {


		/**
		 * Following responses are expected for Non-Invite requests
		 *
		 * 1. 200 for PRACK			 
		 *	  from dialed out 
		 *	  party					:	Log & Ignore
		 *
		 * 2. Non-200 for PRACK		
		 *	  from dialed out  party:  Fire CONNECT_FAILED 
		 *
		 * 3. 200 for BYE			:   Log & Ignore
		 * 
		 */	
		
		SBB sbb = (SBB)getOperationContext();
		SipSession dialoutParty = (this.partyId == PARTY_A) ? sbb.getA() : sbb.getB();

		// 2xx for PRACK from dialed out 
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) && 
			SBBResponseUtil.is200Ok(response) &&
		 	response.getSession().getId().equalsIgnoreCase(requestOut.getSession().getId())) {
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Received 200 OK for PRACK from dialed out party");
			}
		}


		// Non 2xx final for PRACK from dialed out  party 
		else  if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) &&
			SBBResponseUtil.isNon2xxFinalResponse(response) &&
            response.getSession().getId().equalsIgnoreCase(requestOut.getSession().getId())) {

			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Received Non-2xx final response from dialed out party"+
					",firing <CONNECT_FAILED> event ");
			}
			
			setCompleted(true);
            fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
                     SBBEvent.REASON_CODE_ERROR_RESPONSE,response);

		}


		// response for BYE from dialed out party
		else  if (response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE) &&
            response.getSession().equals(dialoutParty)) {
			
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Received 200 for BYE from dialed out party. ");
			}
			setCompleted(true);
            fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
                     SBBEvent.REASON_CODE_CANCELLED_BY_APPLICATION,response);
		}else if(response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE)  && response.getSession().getAttribute(Constants.IS_OTHER_LEG_TERMINATED)!=null){
			logger.error("Received 200 for BYE from Media Server in case A party is terminated. ");
			setCompleted(true);
			fireEvent(SBBEvent.EVENT_DISCONNECTED,
                     SBBEvent.REASON_CODE_CANCELLED_BY_ENDPOINT,response);
			response.getSession().removeAttribute(Constants.IS_OTHER_LEG_TERMINATED);
		}
		// response for UPDATE from dialed out party
		else if(response.getMethod().equalsIgnoreCase("UPDATE") && 
				SBBResponseUtil.is200Ok(response) && response.getRequest() == update){
			if (logger.isInfoEnabled()) {
				logger.info("<SBB> Received 200 for UPDATE from connected party. ");
			}
			try{
				int contentLength = response.getContentLength();
				String contentType = response.getContentType();
				if (contentLength >  0)	{	
					response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
					response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
				}else {
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> No content associated with response request");
					}
				}
				
				SipSession dialedSession = (sbb.getA() == response.getSession()) ? sbb.getB() : sbb.getA();
				if (logger.isDebugEnabled()) {
					logger.debug("Dialed Session ID :" + dialedSession.getId());
				}
				SipServletResponse previousOkResponse = (SipServletResponse) dialedSession.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				SipServletRequest ackToDialoutParty = null;
				dialedSession.removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				ackToDialoutParty = previousOkResponse.createAck();
				if (contentLength > 0) {
					// sending SDP in INVITE to B party in case of dialout | changes for sending SDP in Re-invite| start
					NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
					if (logger.isDebugEnabled()) {
						logger.debug( "<<<<<<<<<< VLAUUE OF NO_INVITE_WITHOUT_SDP IN handleInviteResponse>>>>>>>>>>SDP = "+ NO_INVITE_WITHOUT_SDP);
					}
					if(null==NO_INVITE_WITHOUT_SDP || AseStrings.FALSE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))						
						ackToDialoutParty.setContent(response.getContent(),response.getContentType());
					// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end
				}
				//ACK can be generated for an initial INVITE txn only.
				//sendRequest(response.createAck());
				sendRequest(ackToDialoutParty);
			}catch(IOException exp) {
				logger.error(exp.getMessage(),exp);
			}
			setCompleted(true);
			// fire CONNECTED event
			fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,requestOut);
		} else  // Non 2xx response to UPDATE from connected party
		
			if ((SBBResponseUtil.isNon2xxFinalResponse(response) &&
				response.getRequest() == update)) {

				if (logger.isInfoEnabled()) {
					logger.info("<SBB> Received a Non-2xx final response for UPDATE from connected party");
				}
			
			//CONNECT_FAILED will be handled by the response for the BYE request.
			SipSession dialedSession = (sbb.getA() == response.getSession()) ? sbb.getB() : sbb.getA();
			this.failDialoutParty(dialedSession,response);
		}
		
	}
	private String getDeltaSeconds(String sessionExpHeadVal){
		int deltaSecIndex = sessionExpHeadVal.indexOf(AseStrings.SEMI_COLON, 0);
		String deltaSeconds = null;
		if (deltaSecIndex != -1)
			deltaSeconds = sessionExpHeadVal.substring(0, deltaSecIndex);
		else
			deltaSeconds = sessionExpHeadVal;
		if(logger.isDebugEnabled()){
		    logger.debug("delta Second:"+deltaSeconds);
		}
		return deltaSeconds.trim();
	}
	
	private void startSessionExpiryTimer(SipServletResponse response,String servletTimer,String timerInSession){
		if(response.getHeader("Session-Expires")!=null){
			if(logger.isDebugEnabled()){
			    logger.debug("SDP attributes are set in sip_session");
			}
			 SBB sbb = (SBB)getOperationContext();
			 if(logger.isDebugEnabled()){
			 logger.debug("handle 2xx responce and Session-Expires:"+response.getHeader(Constants.HDR_SESSION_EXPIRES));
			logger.debug("handle 2xx responce "+timerInSession);
		     }
			String deltaSecondsUAS = getDeltaSeconds(response.getHeader(Constants.HDR_SESSION_EXPIRES));
			long timerDuration = Long.valueOf(deltaSecondsUAS);
			timerDuration = timerDuration*1000;
			TimerService ts=(TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
			//ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration, true, new TimerInfo(servletTimer,this));
			//--changes for ft---------
			AseTimerInfo aseTimerInfo = new AseTimerInfo();
			aseTimerInfo.setSbbName(sbb.getName());
			ServletTimer timer=ts.createTimer(sbb.getApplicationSession(), timerDuration, true, aseTimerInfo);
			TimerInfo timerInfo = new TimerInfo(servletTimer,Constants.SESSION_REFRESH_TIMER);
			((SBBImpl)sbb).setServletTimer(timer.getId(), timerInfo);	
			//----------------------
			
			response.getSession().setAttribute(timerInSession, timer.getId());
			response.getSession().setAttribute("SESSION_EXPIRES", deltaSecondsUAS);
			if(logger.isDebugEnabled()){
			   logger.debug("timer created "+timerInSession);
			}
			
		}else{
			if(logger.isDebugEnabled()){
			   logger.debug("handle 2xx responce and Session-Expires is null");
			}
		}
	}
	
	public void postSessionExpiry(String timerType){
		
		SBB sbb = (SBB)this.getOperationContext();
		/*
		 * session refresh post session expires handle by NetworkMessageHandler.  
		 */
		//sbb.getApplicationSession().setAttribute(Constants.SESSION_EXPIRED_OF, timerType);
		/*this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
		this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);*/
		
		/*if(timerType.equals(Constants.TIMER_FOR_A_PARTY)){
			if(logger.isDebugEnabled()){
			    logger.debug("sending bye to A and B party");
			}
			sbb.getA().removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
			if(logger.isDebugEnabled()){
				logger.debug("attribute removed from sip session");
			}
			this.cancelSessionExpiryTimer(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
			try{
				if(sbb.getA()!=null){
	                sbb.getA().setAttribute(Constants.SESSION_EXPIRED_OF, Constants.TIMER_FOR_A_PARTY);
		    		SipServletRequest aByeOut = sbb.getA().createRequest(Constants.METHOD_BYE);
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
		    	}
		    	if(sbb.getB()!=null){
		    		 check for any media playing 
					 * if stopPlay() retuen true ms disconnected from MediaServerInfoHAndler
					 * when received final INFO request.
					 
					if(this.stopPlay()){
						return;
					}
		    		SipServletRequest bByeOut = sbb.getB().createRequest(Constants.METHOD_BYE);
		    		this.sendRequest(bByeOut);
		    	}	
		    }catch(MessagingException me){
    			logger.error(me.getMessage(),me);
    		}
			catch(IOException e){
				logger.error("exception in sending bye a party"+e.getMessage());
			}
			catch(Exception e){
				logger.error(e.getMessage(),e);
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
		    try{
		    	if(sbb.getB()!=null){
		    		sbb.getB().setAttribute(Constants.SESSION_EXPIRED_OF, Constants.TIMER_FOR_MS);
		    		SipServletRequest bByeOut = sbb.getB().createRequest(Constants.METHOD_BYE);
		    		this.sendRequest(bByeOut);
		    	}	
		    }catch(IOException e){
		    	logger.error(e.getMessage(), e);
		    }
		    SBBEvent event = new SBBEvent(SBBEvent.EVENT_MS_SESSION_EXPIRED);
            SBBOperationContext context = (SBBOperationContext)sbb;
            int responseCode = context.fireEvent(event);
            if(logger.isDebugEnabled()){
                  logger.debug("event fired for ms_session_expired..");
            }
		
		} else*/
		if(timerType.equals(Constants.TIMER_FOR_REQ_PEND)){
		    logger.error("Timer fires for Request pending scenario");
			SipServletRequest tempReInvite = null;
			try{
                if (reInvite != null){
                	tempReInvite = reInvite.getSession().createRequest(Constants.METHOD_INVITE);
                    if (reInvite.getContentLength() > 0) {
                    	tempReInvite.setContent(reInvite.getContent(),reInvite.getContentType());
                    	tempReInvite.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
                    }
                	reInvite = tempReInvite;
                    sendRequest(reInvite);
                    requestInSession.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,reInvite);
                	logger.error("Re-Invite again sent for 491 received");
                } else if(requestInSession != null){
                	reInvite = (SipServletRequest) requestInSession.getAttribute(Constants.ATTRIBUTE_INIT_REQUEST);
                	if (reInvite != null){
                    	tempReInvite = reInvite.getSession().createRequest(Constants.METHOD_INVITE);
                        if (reInvite.getContentLength() > 0) {
                        	tempReInvite.setContent(reInvite.getContent(),reInvite.getContentType());
                        	tempReInvite.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
                        }
                    	reInvite = tempReInvite;
                		sendRequest(reInvite);
                		//This is done for a check in SBBServlet which sets the dialog state
                		//depending upon whether the request for the response received
                		//is equivalent to the request set corresponding to this attributew\
                		requestInSession.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,reInvite);
                		logger.error("Re-Invite again sent for 491 received");
                	}
                }
		    }catch(IOException e){
		    	logger.error(e.getMessage(), e);
		    }
		}else{
				logger.error(timerType+" timer not expected.");	
		}
    }
	
	private void cancelSessionExpiryTimer(String str){
		SBB sbb = (SBB)this.getOperationContext();
		//ServletTimer timer = (ServletTimer) sbb.getApplicationSession().getAttribute(str);
		ServletTimer timer = null;
		SipSession session = null;
		String timerId;
		if(str.equals(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY)){
			session =  sbb.getA();
			timerId = (String) session.getAttribute(str);
		}else{
			session = sbb.getB();
			timerId = (String) session.getAttribute(str);
		}
			timer = session.getApplicationSession().getTimer(timerId); 
			if(timer!=null){
				timer.cancel();
				if(logger.isDebugEnabled()){
					logger.debug("session Expires timer cancel"+str);
				}
				if(str.equals(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY)){
					 sbb.getA().removeAttribute(str);
					 if (logger.isDebugEnabled()) {
						 logger.debug("session expires timer attribute remove for A-party");
					 }
				}else{
					sbb.getB().removeAttribute(str);
					if (logger.isDebugEnabled()) {
						logger.debug("session expires timer attribute remove for B-party");
					}
				}
			}	
	}
	
	protected String getIsupContentTypeForA(){
		String contentType = null; 
		SBB sbb = (SBB)this.getOperationContext();
		SipServletRequest requestIn = (SipServletRequest) sbb.getApplicationSession().getAttribute(Constants.REQUEST_FROM_A);
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
	//--------------------------------------

	private void failDialoutParty(SipSession session, SipServletResponse response){
		//Extract the downstream session state.
		int sessionState = session == null ? -1 : 
			((Integer)session.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
				//Handle the downstream session.
		switch(sessionState){
			case Constants.STATE_UNDEFINED: 
			case Constants.STATE_EARLY:
				logger.error("<SBB> Downstream Dialog is in INITIAL/EARLY state, sending a CANCEL request");
				SipServletRequest cancelOut = requestOut.createCancel();
				// Adding Reason Header in CANCEL request
				if(response != null) {
					this.addReasonHeader(response, cancelOut);
				}
				try{
					this.sendRequest(cancelOut);
				}catch(IOException e){
					logger.error(e.getMessage(), e);
				}
				break;
			case Constants.STATE_CONFIRMED: 
				logger.error("<SBB> Downstream Dialog is in CONFIRMED state, so sending a BYE request");
				SipServletResponse previous200FromB = (SipServletResponse)session.getAttribute(
						SBBOperationContext.ATTRIBUTE_INV_RESP);
				
				//Send an ACK if the ACK was not sent earlier.
				if(previous200FromB != null){
					logger.error("<SBB> Downstream Dialog is in CONFIRMED state, Sending the ACK to the terminating end.");
					SipServletRequest ackOut = previous200FromB.createAck();
					try{
						this.sendRequest(ackOut);
					}catch(IOException e){
						logger.error(e.getMessage(), e);
					}
				}
				//In Alestra if we receive non 2xx reponse from A for
				//reinvite w/o sdp then we send ACK to B as per above logic.
				//As we have received error response then we will not be having the SDP of A,
				//which B expects in ACK. Thus ACK is sent without SDP then B will respond with BYE
				//and we are sending BYE which ultimately leads to inconsistent state
				//Thus we are adding the check for cases where reinvite is sent without SDP
				//Wrap the below code in this if (requestOut.getContent() != null){ statement
				//Send BYE to the terminating side.
				try{
					if (logger.isInfoEnabled()) {
						logger.info("<SBB> Sending BYE to party Session");
					}
					SipServletRequest byeOut = session.createRequest(Constants.METHOD_BYE);
					// Adding Reason Header in CANCEL request
					if(response != null) {
						this.addReasonHeader(response, byeOut);
					}
					sendRequest(byeOut);
					logger.error("<SBB> Firing <CONNECT_FAILED> to application ");
				}catch(IOException e){
					logger.error(e.getMessage(), e);
				}
				break;
			case Constants.STATE_TERMINATED:
				logger.error("<SBB> Downstream Dialog is in TERMINATED state, so not doing anything");
				break;
			default:
				logger.error("<SBB> Unknown downstream dialog state. So ignoring....");
		}
	}
	

	private void addReasonHeader(SipServletResponse response, SipServletRequest request) {
		if(logger.isDebugEnabled()) {
			logger.debug("<SBB> addReasonHeader(): Adding Reason header in "+request.getMethod());
		}
		String reasonHdrStr = "SIP;cause="+
						response.getStatus()+";text=\""+
						response.getReasonPhrase()+"\"";
		request.addHeader(Constants.HDR_REASON,reasonHdrStr);
	}
		
	
	public void failOriginatingParty(SipServletResponse response){
		try{
			logger.error("Failing originating party");
			if(this.reInvite != null && !this.receivedFinalRespForReInvite){
				SipServletRequest cancelForReInvite = this.reInvite.createCancel();
				this.addReasonHeader(response, cancelForReInvite);
				logger.error("Sending CANCEL");
				this.sendRequest(cancelForReInvite);
			}
		}catch(IOException e){
			logger.error(e.getMessage(), e);
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.dialoutPartyAddr = (Address) in.readObject();
		this.reInviteType = in.readInt();
		this.partyId = in.readInt();
		this.noAnswerTimeout = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(this.dialoutPartyAddr);
		out.writeInt(this.reInviteType);
		out.writeInt(this.partyId);
		out.writeInt(noAnswerTimeout);
	}

	public void cancel() {
		this.failDialoutParty(this.requestOut.getSession(), null);
	}
	
	protected void send1xxUpstream(SipServletResponse response){
		if(!SBBResponseUtil.isProvisionalResponse(response)){
			if (logger.isDebugEnabled()) {
				logger.debug("send1xxUpstream ==> Response not provisional. So ignoring it....");
			}
			return;
		}
		
		if(reInvite != null){
			if (logger.isDebugEnabled()) {
				logger.debug("The re-INVITE was already sent. So ignoring this provisional response ");
			}
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Sending the provisional response upstream as re-INVITE:" + response.getStatus());
		}
	
		try{
			// After getting a reliable provisional response, the PRACK needs to be generated 
			// This handling was present only while handling the responses locally.
			boolean reliable = SBBResponseUtil.isReliable(response);
			if(reliable) {
				if (logger.isDebugEnabled()) {
					logger.debug("[..........] Generating the PRACK locally for the reliable 183 received from downstream");
					logger.debug("Since we need to re-INVITE party A");
				}
				//String rSeqHdr = response.getHeader(Constants.HDR_RSEQ);
		        //String cSeqHdr = response.getHeader(Constants.HDR_CSEQ);
				
		        //SipSession partyB = response.getSession();
		        SipServletRequest prackToB = response.createPrack();
		        //Create Prack API is used which will take care adding the RACK header
		        //As rack header is immutable and also AseWrapper expects it to be part
		        //Prack Request before sending the request to stack
      //        SipServletRequest prackToB = partyB.createRequest(Constants.METHOD_PRACK);
		        
//		        String rackHdr = rSeqHdr + Constants.SPACE + cSeqHdr;
//		        logger.debug("<SBB> Created RAck header in PRACK <"+rackHdr+">");
//		        prackToB.addHeader(Constants.HDR_RACK,rackHdr); 
		        if (logger.isDebugEnabled()) {
		        	logger.debug("<SBB> Created prack for B "+prackToB);
		        }
		        sendRequest(prackToB);
			}
			//Create the provisional response to send to the upstream...
			if(response.getContentLength() > 0){
				if (logger.isDebugEnabled()) {
					logger.debug("[.....] The provisional response contains SDP, hence creating the re-INVITE for A.");
				}
				reInvite = requestInSession.createRequest(Constants.METHOD_INVITE);
				reInvite.setContent(response.getContent(), response.getContentType());
				this.sendRequest(reInvite);
			}else {
				if (logger.isDebugEnabled()) {
					logger.debug("[.....] The provisional response contains NO SDP, hence the re-INVITE for A can not be created.");
				}
			}
            //This is done in case disconnect happens after the dialout operation as in disconnect
            //we retrieve the initial invite sent to the Party-A
            if (reInvite != null)
            	requestInSession.setAttribute(Constants.ATTRIBUTE_INIT_REQUEST,reInvite);
		}catch(IOException e){
			logger.error(e.getMessage(), e);
		}catch (Rel100Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected void handle1xxLocally(SipServletResponse response){
		
		if(!SBBResponseUtil.isProvisionalResponse(response)){
			if (logger.isDebugEnabled()) {
				logger.debug("handle1xxLocally ==> Response not provisional. So ignoring it....");
			}
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Handling the provisional response locally :" + response.getStatus());
		}
		
		//Check whether the response is reliable or not.
		boolean reliable = SBBResponseUtil.isReliable(response);
		if(!reliable){
			if (logger.isDebugEnabled()) {
				logger.debug("Provisional response is not reliable, so just ignoring it....");
			}
			return;
		}
		try{
			if (logger.isDebugEnabled()) {
				logger.debug("Generating the PRACK locally for the reliable 183 received from downstream");
			}

			String rSeqHdr = response.getHeader(Constants.HDR_RSEQ);
	        String cSeqHdr = response.getHeader(Constants.HDR_CSEQ);
			
	        SipSession partyB = response.getSession();
	        SipServletRequest prackToB = response.createPrack();
	        //Create Prack API is used which will take care adding the RACK header
	        //As rack header is immutable and also AseWrapper expects it to be part
	        //Prack Request before sending the request to stack
	        //SipServletRequest prackToB = partyB.createRequest(Constants.METHOD_PRACK);
//	        String rackHdr = rSeqHdr + Constants.SPACE + cSeqHdr;
//	        logger.debug("<SBB> Created RAck header in PRACK <"+rackHdr+">");
//	        prackToB.addHeader(Constants.HDR_RACK,rackHdr); 
	        logger.debug("<SBB> Created prack for B "+prackToB);

			//	        SipServletRequest prackToB = partyB.createRequest(Constants.METHOD_PRACK);
			//	        String rackHdr = rSeqHdr + Constants.SPACE + cSeqHdr;
			//	        logger.debug("<SBB> Created RAck header in PRACK <"+rackHdr+">");
			//	        prackToB.addHeader(Constants.HDR_RACK,rackHdr); 

			sendRequest(prackToB);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	public synchronized void timerExpired(ServletTimer timer){
		if (logger.isDebugEnabled()) {
			logger.debug("timerExpired() called...");
		}

		if(! receivedFinalRespForInvite) {
			if(requestOut != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> Sending CANCEL to Out Party");
				}
				try {
					SipServletRequest cancel = requestOut.createCancel();
					sendRequest(cancel);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				logger.error("timerExpired() : requestOut is NULL");
			}
		}
	}

	public void setNoAnswerTimeout(int timeout) {
		noAnswerTimeout = timeout;
	}
	
	private void setHeadersOnOutRequest(SipServletRequest request,
			SipServletRequest outReq) {
		String where = "setHeadersOnOutRequest()";
		if (logger.isDebugEnabled()) {
			logger.debug( "[.....] Entering....."+where);
		}
		Iterator itr = request.getHeaderNames();

		while (itr.hasNext()) {
			String name = (String) itr.next();

			if (!name.equals(Constants.HDR_FROM) && !name.equals(Constants.HDR_TO)
					&& !name.equals(Constants.HDR_VIA) && !name.equalsIgnoreCase(Constants.HDR_CSEQ)
					&& !name.equals(Constants.HDR_ROUTE) && !name.equalsIgnoreCase(Constants.HDR_CALL_ID)
					&& !name.equals(Constants.HDR_RECORD_ROUTE) && !name.equals(Constants.HDR_CONTACT)
					&& !name.equals(Constants.HDR_ALLOW)&&!name.equalsIgnoreCase(Constants.HDR_CONTENT_LENGTH)
					&& !name.equalsIgnoreCase(Constants.HDR_CONTENT_TYPE)) {

				ListIterator pai = request.getHeaders(name);
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> Getting Headers for name " + name);
				}
				while (pai != null && pai.hasNext()) {
					String value = (String) pai.next();
					if (logger.isDebugEnabled()) {
						logger.debug("Setting Sip Header " + name + " to : " + value);
					}
					outReq.addHeader(name, value);
				}	
			}	
		}
	}
	/* Commented out for testing...
	protected void failOperation(String reasonCode){
		this.failOperation(reasonCode, null);
	}
	
	protected void failOperation(String reasonCode, Throwable t){
		
		failDialoutParty(requestOut.getSession(), null);
		failOriginatingParty(null);
	}*/

}
