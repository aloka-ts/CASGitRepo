/*
 * @(#)DialoutHandler.java	1.0 8 July 2005
 */

package com.baypackets.ase.sbb.b2b;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.TimerService;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.util.AseStrings;

/**
 * Implementation of the dialout handler.
 * This class is responsible for handling dialout operation. 
 * DialoutHandler operation handles the signalling level details 
 * for connecting two parties to each other into a back-to-back user
 * agent session. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class DialoutHandler extends BasicSBBOperation {

	private static final long serialVersionUID = -338014097033147L;
	private transient SipServletRequest requestA = null;
	private transient SipServletRequest reInviteToA = null;
    private transient SipServletRequest requestB = null;
    private transient Address fromAddress = null;
    private transient Address addrPartyA= null;
    private transient Address addrPartyB= null;
    
    private boolean receivedFinalRespForA = false;
    private boolean receivedFinalRespForB = false;
		private boolean m_autoResponseMode = false;

	public static final int PARTY_UNDEFINED = -1;
    public static final int PARTY_A = 0;
    public static final int PARTY_B = 1;
    
    private static final String ATT_CPA_CHECK = "ATT_CPA_CHECK";  
    private static final String ORIG_INITIAL_REQUEST = "ORIG_INITIAL_REQUEST";
    private static final String ATT_CANCEL_RECIEVED = "ATT_CANCEL_RECIEVED";
    
    private transient ServletTimer noAnsTimerA = null;
    private transient ServletTimer noAnsTimerReInviteA = null;
    private transient ServletTimer noAnsTimerB = null;
    private boolean byeToPartyA = false;
    
    public static String NO_INVITE_WITHOUT_SDP = AseStrings.FALSE_SMALL;
    
    

	 /** Logger element */
    private static Logger logger = Logger.getLogger(DialoutHandler.class.getName());
		

	/* RTP tunneling flag */
	private int isRTPTunnelingEnabled = 0;	

	// No-Answer timeout in seconds
	private int noAnswerTimeout = 60;

	//Default SDP
	private String m_noMediaSDP="v=0\n" +
   "o=sas 1000 1000 IN IP4 127.0.0.0\n" +
   "s=SIP Media Capabilities\n" + 
   "c=IN IP4 0.0.0.0\n" + 
   "t=0 0\n"+
   "m=audio 16444 RTP/AVP 8 \r\n" ;

	private int m_sdpVersionNumber = 1000;

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public DialoutHandler() {
		super();
	}
	
	/**
     * 	Constructor for creating instance of DialoutHandler.
	 *	@param party-A: Address of party-A to be dialed out.		
	 *	@param party-B: Address of party-B to be dialed out.		
	 * 	@param autoResponseMode automata flag.
	 *
   */
	public DialoutHandler(Address from, Address partyA,Address partyB , boolean autoResponseMode ) {
		fromAddress = from;
		addrPartyA = partyA;
		addrPartyB = partyB;
		m_autoResponseMode = autoResponseMode;
	}


	/**
     * 	Constructor for creating instance of DialoutHandler.
	 *	@param party-A: Address of party-A to be dialed out.		
	 *	@param party-B: Address of party-B to be dialed out.		
	 *
   */
	public DialoutHandler(Address from, Address partyA,Address partyB ) {
		fromAddress = from;
		addrPartyA = partyA;
		addrPartyB = partyB;
		m_autoResponseMode = true;
	}

	/**
     * This method will be invoked to start dialout operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     *
     */
	public void start() throws ProcessMessageException, IllegalStateException{
		if (logger.isDebugEnabled()) {
		logger.debug("<SBB>entered start() ");
		
		logger.debug("<SBB> Party A <"+addrPartyA+"> and  Party B <"+addrPartyB+">");
		}
		SBB sbb = (SBB)getOperationContext();
		
		NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
		
		if(null!=NO_INVITE_WITHOUT_SDP && AseStrings.TRUE_SMALL.equalsIgnoreCase(NO_INVITE_WITHOUT_SDP)){
			m_autoResponseMode=false;
		}
		
		//isRTPTunnelingEnabled = ((Integer)sbb.getAttribute(SBB.RTP_TUNNELLING)).intValue();

		try {
			SipFactory factory =
                		(SipFactory)sbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			fromAddress = (fromAddress == null) ? factory.createAddress("SAS <sip:sas@gb.com>") : fromAddress;
			
			// creating request for party A
			SipApplicationSession appSession = ((SBB)getOperationContext()).getApplicationSession();
			requestA = factory.createRequest(appSession,Constants.METHOD_INVITE,fromAddress,addrPartyA);
			if ( m_autoResponseMode == false ) {
				if (logger.isInfoEnabled())
				logger.info("<SBB> Sending invite request (with SDP) to party A");
				requestA.setContent(m_noMediaSDP.getBytes(), "application/sdp" );
			} else {
				if (logger.isInfoEnabled())
				logger.info("<SBB> Sending invite request (w/o SDP) to party A");
			}	
			sbb.addA(requestA.getSession());
			if(logger.isDebugEnabled()) 
			logger.debug("request to party A ="+requestA);	
			requestA.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST, requestA);
			requestA.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT, requestA);
			
			// moving timer above before sendRequest() as caused issue in jail flow of lock on Invocation context
			// as response arrives before timer goes to take lock 
			//BPInd18612 changes starts here
			long timeout = noAnswerTimeout*1000;
			if( timeout > 0){
				if(logger.isDebugEnabled()) {
					logger.debug("<SBB> start(): Creating a no-ans timer to fire in "+timeout/1000 +" seconds");
				}
				TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
				this.noAnsTimerA = timerService.createTimer(appSession,timeout,false,this);
			}
			sendRequest(requestA);
			
		}catch(Exception exp) {
			logger.error(exp.getMessage(),exp);	
			throw new ProcessMessageException(exp.getMessage());
		}
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB>exited start() ");
    }


	public void ackTimedout(SipSession session) {
    }

	public void prackTimedout(SipSession session) {
    }

	/**
     *	This method will not get called as there is no request sent by endpoints.
     */
	public void handleRequest(SipServletRequest request) {

		logger.error("<SBB> entered handleRequest with following request :: "+request);
		// If request is non initial Invite request from A 
		// and A has sent INVITE to B but hasn't received final response
		//then send "491 request pending" to A.
		if ( request.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE) && 
			!request.isInitial() && request.getSession() == requestA.getSession() ) {
			if(logger.isDebugEnabled()) 
			logger.debug("re-INVITE received from A, while INVITE request pending with B");
			try {
				SipServletResponse response = request.createResponse(491);	
				response.send();
			} catch (Exception exp) {
				logger.error("Exception in sending 491" , exp);
			}
		}

	}

	/**
     *	This method  handles all response. Since this is a dialout to both parties
	 *	response can come from any party
     *
     * @response - Response from party.
     */
	public void handleResponse(SipServletResponse response) {
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> entered handleResponse with following response with <"+
				response.getStatus()+","+response.getMethod()+">");

		// handle INVITE responsed
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {		
			if ( m_autoResponseMode == false ) { 
				handleInviteResponseNoAutomata(response);		
			} else {
				handleInviteResponse(response);
			}
		} else {
			handleNonInviteResponse(response);
		}
			if(logger.isDebugEnabled()) 
			logger.debug("<SBB> exited handleResponse");
    }


	/**
	 *	Since this is dialout handler this methods can get responses from both the parties
	 */	
	private void handleInviteResponse(SipServletResponse response)	{

		/*
         *  Folowing respons are expected respose to INVITE request
		 *
         *  1.  100 TRYING      :  Log and ignore      
         *  2.  1xx             :  Log & Ignore
		 *  3.  1xx  REL        :  Log & Ignore
		 *  4.  2xx from A		:  Extract the SDP & initiate a INVITE to 
		 *						   other partyr.
		 *	5. 	1xx from B		:  Log & ignore
		 *	6. 	1xx REL from B	:  Log & ignore
		 *	7. 	2xx 			:  Extract the SDP, send ACK to B with A's SDP,
		 *						   send ACK to A.	
         *  6.  Non 2xx Final
		 *      from A 			:  fire <CONNECT_FAILED>  	
		 *  7.  Non 2xx Final
		 * 		party B			:  send ACK, BYE to A
		 *						   & fire <CONNECT_FAILED>.
		 *						   Ack to for Non-2xx final response will be generated by
		 *						   container.
         */
		
		int contentLength = response.getContentLength();
		SBB sbb = (SBB)getOperationContext();
		
	//Adding variable to add code for CPA call flows.
		String attCPACheck = AseStrings.FALSE_SMALL;
		SipApplicationSession appsession  = sbb.getA().getApplicationSession();
		//getting value of ATT_CPA_CHECK set in RouteCallAction.java 
		if(appsession.getAttribute(ATT_CPA_CHECK) != null){
			attCPACheck = (String)appsession.getAttribute(ATT_CPA_CHECK);
		}
		//Added for ATT CPA Call flow. To indicate that whether we need to do operation for cancel dialout or normal
		SipServletRequest origRequest = null ;
		String attCancelRecvd ="false";
		if(attCPACheck.equalsIgnoreCase("true")){ 		
		//Retrieving orignal request from appsession.
			origRequest = (SipServletRequest)appsession.getAttribute(ORIG_INITIAL_REQUEST);
			//This piece of code is only specific to ATT CPX call flow when three party are invploved in call 
			//and a party sends Cancel before B and IVR dialout. Then this attribute is set by application in Sbb.
			//Here it is used to check while sending response to A party in this scenario.
			
			if (origRequest.getApplicationSession().getAttribute(ATT_CANCEL_RECIEVED) != null) {
				attCancelRecvd = (String) origRequest.getApplicationSession().getAttribute(ATT_CANCEL_RECIEVED);
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("attcpacherck is :::::" +attCPACheck );
			logger.debug("Orignal request from sbb is ::" + origRequest);
			logger.debug("Is Cancel dialout in progress :::"+ attCancelRecvd);
		}
		
		
		try {

			// 100 Trying from anywhere
			if (SBBResponseUtil.is100Trying(response)) {
				if(logger.isInfoEnabled()) 
				logger.info("<SBB>Received 100 Trying ");
			}

			// Provisional responses except 100 from A (1xx & 1xx REL)
			else if (SBBResponseUtil.isProvisionalResponse(response) &&
				response.getRequest() == requestA) {
				//Added code for CPA call Flow(ATT GOvt Project)
				//Sending the 1xx received from B party in CPA call to A party.
				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Sending 180 upstream when automata response true");
					}
					SipServletResponse responseToInitialA =  origRequest.createResponse(response.getStatus());
					try{
						//throw new Exception("Dummy exception to test the flow");
						sendResponse(responseToInitialA, false);
					}catch(Exception e){
						logger.error("Exception will sending 180 response to Orignal A.");
						int stateA = ((Integer)sbb.getA().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
						//If it is in state early then send cancel otherwise send Bye.
						if(stateA != Constants.STATE_CONFIRMED){
							if(logger.isEnabledFor(Priority.ERROR)){
								logger.error("Sending Cancel to A party in dialout");
							}
							requestA.createCancel().send();
						}
						else {
							SipServletRequest byeOutA = response.getSession().createRequest(Constants.METHOD_BYE);
							if(logger.isEnabledFor(Priority.ERROR)){
								logger.error("Sending Bye to A party in Dilaout ");
							}
							try{
					    		this.sendRequest(byeOutA);
					    	}catch(IOException excep){
					    		logger.error(excep.getMessage(), excep);
					    	}
						}
						setCompleted(true);
						fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR,	
										response);
					}
				}
				if(logger.isInfoEnabled()) 
				logger.info("<SBB>Received <"+response.getStatus()+"> provisional response from A");
			}


			// 2xx responses from A
			else if (SBBResponseUtil.is2xxFinalResponse(response) && 
				response.getRequest() == requestA) {
				if(logger.isInfoEnabled()) 
				logger.info("<SBB> Received 2xx response from dialed out party");

			    //This is the case of the MFRs.
			    //if we already got a 200 Ok, terminate this leg.
			    //So we need to ack and bye this response 
			    if(this.receivedFinalRespForA){

			    	
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

			    //Set the finalResponse Received Flag....
			    receivedFinalRespForA = true;
				
				// cancel the timer if not NULL
				if(this.noAnsTimerA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerA.cancel();
				}

			    //In case of MFRs, if we get the 200 Ok from another session,
			    //then swap the original session with the newly created session...
			    //This will be the case, if we already got a 1xx from the original session but not the 2xx
			    if(response.getSession() != requestA.getSession()){
		    		sbb.removeA();
					response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
					sbb.addA(response.getSession());
    			}
				
				// extracting party B SDP and storing in session
			   // int contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
			    	try {
			    		if(this.getOperationContext().getSBB() instanceof MsSessionControllerImpl ){
			    		((MsSessionControllerImpl)this.getOperationContext().getSBB()).setSupportedMediaTypes(response);
			    		}
			    	} catch (MediaServerException e) {
			    		logger.error("Exception occured in setSupportedMediaTypes() : "+e);
			    	}

				}
				else {
					if(logger.isInfoEnabled()) 
					logger.info("<SBB> No content associated with response request");
				}
                 
				//  Send INVITE to connected party in case RTP-Tunneling is not supported

				SipFactory factory = (SipFactory)((SBB)getOperationContext()).getServletContext().getAttribute(
																		SipServlet.SIP_FACTORY);
				
				// TODO : remove user name and domain hardcoding
				requestB = factory.createRequest(response.getApplicationSession(),Constants.METHOD_INVITE,fromAddress,addrPartyB);
				((SBB)getOperationContext()).addB(requestB.getSession());
				if(contentLength != 0 ) {
                	requestB.setContent(response.getContent(),response.getContentType());
				}
				if(logger.isInfoEnabled()) 
				logger.info("<SBB> Sending request to party B with A's SDP");
				if(logger.isDebugEnabled()) 	
				logger.debug("<SBB> Sending request to party B with A's SDP ::"+requestB);
				requestB.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST, requestB);
				requestB.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT, requestB);
				// moving timer above before sendRequest() as caused issue in jail flow of lock on Invocation context
				// as response arrives before timer goes to take lock 
				//BPInd18612 changes starts here
				long timeout = noAnswerTimeout*1000;
				if( timeout > 0){
					if(logger.isDebugEnabled()) {
						logger.debug("<SBB> start(): Creating a no-ans timer to fire in "+timeout/1000 +" seconds");
					}
					TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
					this.noAnsTimerB = timerService.createTimer(response.getApplicationSession(),timeout,false,this);
				}
				
                 //  We have to preserve the 200 OK response from A, so that an ack can be created later on
                response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP,response);
                sendRequest(requestB);

			}

			// Non-2xx final response from A
			else if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
					response.getRequest() == requestA) {

				// Non 2xx final responses  (i.e. 3xx,4xx,5xx and 6xx)
				if(logger.isInfoEnabled()) 
				logger.info("<SBB> Received a Non-2xx final response from A"+
							", Firing connect failed event");
                
			    //Set the finalResponse Received Flag....
			    receivedFinalRespForA = true;
			    
			  //Mukesh Sending non 2xx response received from B party to A party in CPA call flow.
				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL) && attCancelRecvd.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Recived non 2xx response from A party(i.e Party B in call)");
					}
					SipServletResponse responseToInitialA =  origRequest.createResponse(response.getStatus());
					try{
						sendResponse(responseToInitialA, false);
					}catch(Exception e){
						logger.error("Exception will sending non 2XX response to Orignal A.");
					}
				}
				
				// Received final response cancel the timer if not NULL
				if(this.noAnsTimerA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerA.cancel();
				}

				
				setCompleted(true);
				fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE,	
								response);
			}
			

			// Provisional responses except 100 from B (1xx & 1xx REL)
            else if (SBBResponseUtil.isProvisionalResponse(response) &&
                response.getRequest() == requestB) {
				if(logger.isInfoEnabled()) 
                logger.info("<SBB>Received <"+response.getStatus()+"> provisional response from B");
            }

			// 2xx response to INVITE from B
			else if (SBBResponseUtil.is2xxFinalResponse(response) &&
					response.getRequest() == requestB) {
				
				  sbb.getB().setAttribute("TO_TAG" ,response.getTo().getParameter("tag"));
				if(logger.isInfoEnabled()) 
				logger.info("<SBB> Received 2xx response INVITE from B");
				
				//This is the case of the MFRs.
			    //if we already got a 200 Ok, terminate this leg.
			    //So we need to ack and bye this response 
			    if(this.receivedFinalRespForB){
			    	
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

			    //Set the finalResponse Received Flag....
			    receivedFinalRespForB = true;
			    sbb.getB().setAttribute("TO_TAG" ,response.getTo().getParameter("tag"));
			    response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP,response);
				// cancel the timer if not NULL
				if(this.noAnsTimerB != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for B: Cancelling NO-ANS timer");
					}
					this.noAnsTimerB.cancel();
				}
				
			    //In case of MFRs, if we get the 200 Ok from another session,
			    //then swap the original session with the newly created session...
			    //This will be the case, if we already got a 1xx from the original session but not the 2xx
			    if(response.getSession() != requestB.getSession()){
		    		sbb.removeB();
					response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
					sbb.addB(response.getSession());
    			}
				
				// store the latest SDP in session and also pass this SDP to  A  as ACK content.
			  //  int contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
				}
				else {
					if(logger.isInfoEnabled()) 
					logger.info("<SBB> No content associated with response request");
				}
				
				SipServletResponse okFromA = (SipServletResponse)requestA.
						getSession().getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				SipServletRequest ackToA  = okFromA.createAck();
				if (contentLength != 0) {
					ackToA.setContent(response.getContent(),response.getContentType());
				}

				// remove the attribute from session before sending ack
				requestA.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				
				sendRequest(ackToA);
				sendRequest(response.createAck());
                    
				setCompleted(true);	
				//fire Event connected
				int retValue = fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,response);
			}


			// Non 2xx response to INVITE from B
			else if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
					response.getRequest() == requestB) {
				if(logger.isInfoEnabled()) 
				logger.info("<SBB>  Received a Non-2xx final response for INVITE from B");

				//Set the finalResponse Received Flag....
				receivedFinalRespForB = true;
				
				//Received final response. cancel the timer if not NULL
				if(this.noAnsTimerB != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for B: Cancelling NO-ANS timer");
					}
					this.noAnsTimerB.cancel();
				}
				
				setCompleted(true);
    				
    				SipServletResponse previousOkResponse = (SipServletResponse)
					requestA.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);

	    			// remove the attribute from session before sending ack
       		             	requestA.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
							if(logger.isInfoEnabled()) 
                    		logger.info("<SBB> Sending ACK to party A");
                    		// send ACK to A & initiate a BYE
    				sendRequest(previousOkResponse.createAck());

    				// initiating a BYE to dialed out party 
					// Adding Reason Header in BYE
//					SipServletRequest byeRequest = requestA.getSession().createRequest(Constants.METHOD_BYE);
//					String reasonHdrStr = "SIP;cause="+
//											response.getStatus()+";text=\""+
//											response.getReasonPhrase()+"\"";
//					byeRequest.addHeader(Constants.HDR_REASON,reasonHdrStr);
//    				sendRequest(byeRequest);
    				
    				//Mukesh Sending CANCEL request to A party in CPA call flow when ivr send non 2xx response to B Party in CPA Call flow.
     				
    				//Reeta commenting below because we have added Grouped ms functinality so if ms sends error we will try on another ms so we can not send 487 toA her
//     				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL) && attCancelRecvd.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
//    					if(logger.isDebugEnabled()){
//    						logger.debug("Get non -2xx from Media server(B in Dialout ) and sending 487 to originating A party not A in dialout ");
//    					}
//     					SipServletResponse responseToInitialA =  origRequest.createResponse(487);
//    					try{
//    						sendResponse(responseToInitialA,false);
//    					}catch(Rel100Exception e){
//    						logger.error("Exception will sending 487 to Orignal A.(Not A in Dialout)");
//    					}
//    				}
    				
    				// fire CONNECTION_FAILED
                		int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
							SBBEvent.REASON_CODE_ERROR_RESPONSE,response); //reeta changed from requestA->response
						if(logger.isInfoEnabled()) 	
    		       		logger.info("<SBB>Received from application :"+retValue);	    

			}

			// No needs to ACK Non-2xx final response to A as container will do it.
			
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
		}	
	}


	/**
	 *	Since this is dialout handler this methods can get responses from both the parties
	 */	
	private void handleInviteResponseNoAutomata(SipServletResponse response)	{

		/*
         *  Folowing respons are expected respose to INVITE request
		 *
		 *  1.  100 TRYING      :  Log and ignore      
		 *  2.  1xx             :  Log & Ignore
		 *  3.  1xx  REL        :  Log & Ignore
		 *  4.  2xx for Initial 
		 *			INVITE from A		:  Send Ack to A. Initiate a INVITE w/o SDP to 
		 *												 other party B.
		 *	5. 	1xx from B			:  Log & ignore
		 *	6. 	1xx REL from B	:  Log & ignore
		 *	7. 	2xx from B 			:  Extract the SDP, make new SDP and send INVITE to party A.
		 *	8.	2xx for 
		 *			re-INVITE from A:	Extract SDP from response and Sends Ack to party B with 
		 *												that SDP. Sends Ack to party A.
     *  4.  Non 2xx Final from
		 *      A(Initial INVITE):  fire <CONNECT_FAILED>  	
		 *  7.  Non 2xx Final
		 * 			from party B		:  BYE to A & fire <CONNECT_FAILED>.Ack to for Non-2xx 
		 *												 final response will be generated by container.
		 *	8.Non 2xx Final from		
     *      A(re-INVITE)		:	 Send ACK, BYE to B & fire <CONNECT_FAILED>.
     *               Ack to for Non-2xx final response will be generated by
     *               container.
		
		 */
		int contentLength = response.getContentLength();
		SBB sbb = (SBB)getOperationContext();
		
		//Adding variable to add code for CPA call flows.
		String attCPACheck = "false";
		SipApplicationSession appsession  = sbb.getA().getApplicationSession();
		//Retrieving value of variable from appsession.
		if(appsession.getAttribute(ATT_CPA_CHECK) != null){
			attCPACheck = (String)appsession.getAttribute(ATT_CPA_CHECK);
		}
		
		//This orig request is intial INVITE request from party A after which we are dialing out B and IVR in CPX call flow for ATT Govt Project.
		SipServletRequest origRequest = (SipServletRequest)appsession.getAttribute(ORIG_INITIAL_REQUEST);
		//This piece of code is only specific to ATT CPX call flow when three party are invploved in call 
		//and a party sends Cancel before B and IVR dialout. Then this attribute is set by application in Sbb.
		//Here it is used to check while sending response to A party in this scenario.
		String attCancelRecvd ="false";
		if (origRequest.getApplicationSession().getAttribute(ATT_CANCEL_RECIEVED) != null) {
			attCancelRecvd = (String) origRequest.getApplicationSession().getAttribute(ATT_CANCEL_RECIEVED);
		}
		
		
		if(logger.isDebugEnabled()){
			logger.debug("attcpacherck is :::::" +attCPACheck );
			logger.debug("Orignal request from sbb is ::" + origRequest);
			logger.debug("Is cancel dialout operation in progress::"+attCancelRecvd);
		}
		
		try {

			// 100 Trying from anywhere
			if (SBBResponseUtil.is100Trying(response)) {
				if(logger.isInfoEnabled()) 
				logger.info("<SBB>Received 100 Trying ");
			}

			// Provisional responses except 100 from A (1xx & 1xx REL)
			else if (SBBResponseUtil.isProvisionalResponse(response) &&
				response.getRequest() == requestA) {
				//Adding code for CPA call flow
				//Sending 1xx response received from B Party to A party in CPA call flow.
				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Sending 180 upstream...");
					}
					SipServletResponse responseToInitialA =  origRequest.createResponse(response.getStatus());
					try{
						//throw new Exception ("handleInviteResponseNoAutomata : Dummy Exception");
						sendResponse(responseToInitialA, false);
					}catch(Exception e){
						logger.error("Exception while sending 180 response to Orignal A.");
						int stateA = ((Integer)sbb.getA().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
						//If it is in state early then send cancel otherwise send Bye.
						if(stateA != Constants.STATE_CONFIRMED){
							if(logger.isEnabledFor(Priority.ERROR)){
								logger.error(origRequest.getCallId()+"Sending Cancel to A party in dialout");
							}
							requestA.createCancel().send();
						}
						else {
							SipServletRequest byeOutA = response.getSession().createRequest(Constants.METHOD_BYE);
							if(logger.isEnabledFor(Priority.ERROR)){
								logger.error("Sending Bye to A party in Dilaout ");
							}
							try{
					    		this.sendRequest(byeOutA);
					    	}catch(IOException excep){
					    		logger.error(excep.getMessage(), excep);
					    	}
						}
						setCompleted(true);
						fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR,	
										response);
					}
				}
				if(logger.isInfoEnabled()) 
				logger.info("<SBB>Received <"+response.getStatus()+"> provisional response from A");
			}


			// 2xx responses from A
			else if (SBBResponseUtil.is2xxFinalResponse(response) && 
				response.getRequest() == requestA) {
				if(logger.isInfoEnabled()) 
				logger.info("<SBB> Received 2xx response from dialed out party");

			    //This is the case of the MFRs.
			    //if we already got a 200 Ok, terminate this leg.
			    //So we need to ack and bye this response 
			    if(this.receivedFinalRespForA){

			    	
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

			    //Set the finalResponse Received Flag....
			    receivedFinalRespForA = true;
				
				// cancel the timer if not NULL
				if(this.noAnsTimerA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerA.cancel();
				}

				//In case of MFRs, if we get the 200 Ok from another session,
				//then swap the original session with the newly created session...
				//This will be the case, if we already got a 1xx from the original session but not the 2xx
		    if(response.getSession() != requestA.getSession()){
	    		sbb.removeA();
					response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
					sbb.addA(response.getSession());
   			}
				//Sending Ack to party A
				sendRequest(response.createAck());

				// extracting party B SDP and storing in session
				contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
					try {
						if(this.getOperationContext().getSBB() instanceof MsSessionControllerImpl ){
						((MsSessionControllerImpl)this.getOperationContext().getSBB()).setSupportedMediaTypes(response);
						}
					} catch (MediaServerException e) {
						logger.error("Exception occured in setSupportedMediaTypes() : "+e);
					}
				}
				else {
					if(logger.isInfoEnabled()) 
					logger.info("<SBB> No content associated with response request");
				}
                 
				//  Send INVITE to connected party 
				SipFactory factory = (SipFactory)((SBB)getOperationContext()).getServletContext().getAttribute(
																		SipServlet.SIP_FACTORY);
				requestB = factory.createRequest(response.getApplicationSession(),Constants.METHOD_INVITE,fromAddress,addrPartyB);
				((SBB)getOperationContext()).addB(requestB.getSession());
				if(logger.isDebugEnabled()) 
				logger.debug("<SBB> Sending request to party B w/o SDP ::"+requestB);
				requestB.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST, requestB);
				requestB.getSession().setAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT, requestB);
				/*
				 * Reeta 
				 * in case of auto response code false which is black holed flow as per rfc 3725 sdp should go
				 * in invite to B not in ack to B to sending sdp to B in invite here 
				 */
					if(logger.isDebugEnabled()) 
					logger.log(Level.DEBUG, "<<<<<<<<<<setting SDP of A in the request>>>>>>>>>>SDP ="+sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP));
					requestB.setContent(sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP) ,
	                        (sbb.getA().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE)).toString() );
					// sending SDP in INVITE to B party in case of dialout |changes for sending SDP in Re-invite| end				
					/*
					 * change ends here 
					 */
				
				// moving timer before send Request as it caused AseIC lock issue in jail flow as response comes before timer is created
				//which tries to take lock on same IC
					
				//Creating timer 
				long timeout = noAnswerTimeout*1000;
				if( timeout > 0){
					if(logger.isDebugEnabled()) {
						logger.debug("<SBB> start(): Creating a no-ans timer to fire in "+timeout/1000 +" seconds");
					}
					TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
					this.noAnsTimerB = timerService.createTimer(response.getApplicationSession(),timeout,false,this);
				}
				sendRequest(requestB);
				sbb.getB().setAttribute("ReceivedFinalResponseFromB" , new Boolean(false)); 
			}

			// Non-2xx final response from A
			else if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
					response.getRequest() == requestA) {

				// Non 2xx final responses  (i.e. 3xx,4xx,5xx and 6xx)
				if (logger.isInfoEnabled())
				logger.info("<SBB> Received a Non-2xx final response from A"+
							", Firing connect failed event");
                
				//Sending non 2xx response received from B party(A party in Dialout)upstream i.e to A party in CPA Call Flow. 
				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL)&& attCancelRecvd.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Recived non 2xx response from A party(i.e Party B in call)");
					}
					SipServletResponse responseToInitialA =  origRequest.createResponse(response.getStatus());
					try{
						sendResponse(responseToInitialA, false);
					}catch(Exception e){
						logger.error("Exception will sending non 2XX response to Orignal A.");
					}
				}
				
			    //Set the finalResponse Received Flag....
			    receivedFinalRespForA = true;
				
				// Received final response cancel the timer if not NULL
				if(this.noAnsTimerA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerA.cancel();
				}

				
				setCompleted(true);
				fireEvent(SBBEvent.EVENT_CONNECT_FAILED,SBBEvent.REASON_CODE_ERROR_RESPONSE,	
								response);
			}
			

			// Provisional responses except 100 from B (1xx & 1xx REL)
			else if (SBBResponseUtil.isProvisionalResponse(response) &&
				response.getRequest() == requestB) {
				if (logger.isInfoEnabled())
        logger.info("<SBB>Received <"+response.getStatus()+"> provisional response from B");
			}

			// 2xx response to INVITE from B
			else if (SBBResponseUtil.is2xxFinalResponse(response) &&
					response.getRequest() == requestB) {
				
				 sbb.getB().setAttribute("TO_TAG" ,response.getTo().getParameter("tag"));
				if (logger.isInfoEnabled())
				logger.info("<SBB> Received 2xx response INVITE from B");
				
				//This is the case of the MFRs.
			  //if we already got a 200 Ok, terminate this leg.
			  //So we need to ack and bye this response 
				if(this.receivedFinalRespForB){
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

				//Set the finalResponse Received Flag....
				receivedFinalRespForB = true;
				sbb.getB().setAttribute("ReceivedFinalResponseFromB" , new Boolean(true));	
				sbb.getB().setAttribute("TO_TAG" ,response.getTo().getParameter("tag"));
				// cancel the timer if not NULL
				if(this.noAnsTimerB != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for B: Cancelling NO-ANS timer");
					}
					this.noAnsTimerB.cancel();
				}
				
				//In case of MFRs, if we get the 200 Ok from another session,
				//then swap the original session with the newly created session...
				//This will be the case, if we already got a 1xx from the original session but not the 2xx
			  if(response.getSession() != requestB.getSession()){
		   		sbb.removeB();
					response.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
					sbb.addB(response.getSession());
    		}
				
			//  int contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
				}
				else {
					logger.error("Received SDP with 0 length in 2XX response from B");
					//First send Ack for 2XX response then  send BYE to A and BYE to B
					sendRequest(response.createAck());
					sendRequest(requestA.getSession().createRequest(Constants.METHOD_BYE));
					sendRequest(requestB.getSession().createRequest(Constants.METHOD_BYE));
					// fire CONNECTION_FAILED
					setCompleted(true);
					int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
					SBBEvent.REASON_CODE_ERROR_RESPONSE,response); //reeta changed from request A to response
					if (logger.isInfoEnabled())
					logger.info("<SBB>Received from application :"+retValue);
					return;
				}

				//  We have to preserve the 200 OK response from B, so that an ack can be created later on
				response.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP,response);	
				//Now generate re-INVITE and send it to Party A with the modified SDP
				try {
					//get PartyA sip session
      		// creating request for party A
      		reInviteToA  = sbb.getA().createRequest(Constants.METHOD_INVITE); 
					contentType = response.getContentType();
					if ( contentType.indexOf("application/sdp") == -1 ) {
						logger.error( contentType + " is not supported in platform");
						logger.error(" so not modifying SDP. Sending re-INViTE to PartA without modifying SDP");
						 reInviteToA.setContent(response.getContent() , contentType);
					} else {
						//modifies the offer2 recived from party B in 2XX response.
          	reInviteToA.setContent(this.modifySDP(response.getRawContent()) ,contentType);
						if (logger.isInfoEnabled())
						logger.info("<SBB> Sending re-Invite request (with newSDP) to party A");
						if (logger.isDebugEnabled())	
						logger.debug("re-INVITE request to party A ="+reInviteToA );
					}
          
					// moving timer before send Request as it caused AseIC lock issue in jail flow as response comes before timer is created
					//which tries to take lock on same IC
					//create timer for re-INVITE
					long timeout = noAnswerTimeout*1000;
					if( timeout > 0){
						if(logger.isDebugEnabled()) {
							logger.debug("<SBB> start(): Creating a no-ans timer to fire in "+timeout/1000 +" seconds");
						}
						TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
						this.noAnsTimerReInviteA = timerService.createTimer(response.getApplicationSession(),timeout,false,this);
					}
					sendRequest(reInviteToA );
					
    		}catch(Exception exp) {
      		logger.error(exp.getMessage(),exp);
    		}

			}

			// Non 2xx response to INVITE from B
			else if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
					response.getRequest() == requestB) {
				if (logger.isInfoEnabled())
				logger.info("<SBB>  Received a Non-2xx final response for INVITE from B");

				//Set the finalResponse Received Flag....
				receivedFinalRespForB = true;
				
				//Received final response. cancel the timer if not NULL
				if(this.noAnsTimerB != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for B: Cancelling NO-ANS timer");
					}
					this.noAnsTimerB.cancel();
				}
				
				setCompleted(true);
    				
				// remove the attribute from session before sending BYE
				requestA.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
 				// initiating a BYE to dialed out party 
				// Adding Reason Header in BYE
				if (!byeToPartyA){
					//BYE will only be sent in case BYE is not already sent at the time application called
					//cancel dialout
					SipServletRequest byeRequest = requestA.getSession().createRequest(Constants.METHOD_BYE);
					String reasonHdrStr = "SIP;cause="+
												response.getStatus()+";text=\""+
												response.getReasonPhrase()+"\"";
				//	byeRequest.addHeader(Constants.HDR_REASON,reasonHdrStr);
	 				sendRequest(byeRequest);
				}
 				//Mukesh 
 				//Get non 2xx from Media server(B in Dialout ) and sending 487 to originating A party in CPA call Flow.
 				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL)&& attCancelRecvd.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Get non -2xx from Media server(B in Dialout ) and sending 487 to originating A party not A in dialout ");
					}
 					SipServletResponse responseToInitialA =  origRequest.createResponse(487);
					try{
						sendResponse(responseToInitialA,false);
					}catch(Rel100Exception e){
						logger.error("Exception will sending 487 to Orignal A.(Not A in Dialout)");
					}
				}
 				// fire CONNECTION_FAILED
     		int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
							SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
			if (logger.isInfoEnabled())
     		logger.info("<SBB>Received from application :"+retValue);	    

			}

			else if (SBBResponseUtil.isProvisionalResponse(response) &&
        response.getRequest() == reInviteToA ) {
				if (logger.isInfoEnabled())
				logger.info("<SBB>Received <"+response.getStatus()+"> provisional response from reInvite to A");
      }

			else if (SBBResponseUtil.is2xxFinalResponse(response) &&
        response.getRequest() == reInviteToA ) {

				//Received final response for reInvite. cancel the timer if not NULL
				if(this.noAnsTimerReInviteA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for re-Invite to A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerReInviteA.cancel();
				}
				
				/*
				 * Reeta:resetting SDP attribute in Session of B(its B in jail flow) otherwise SDP set may be with "a" attribute inactive e.g in jail flow
				 * in marcatel the B(its B in jail flow) party sends  blacked holed sdp or with a field inactive in sdp on receiving black holed sdp. so when we will resynch A and B
				 *  then A will get this black holed sdp so need to update latest SDP received from B when B and IVR gets connected on 
				 *  200 for reinvite to B.
				 */
				//int contentLength = response.getContentLength();
				String contentType=response.getContentType();
				if (contentLength >  0)	{	
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP,response.getContent());
							response.getSession().setAttribute(
									SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE,response.getContentType());
							try {
								if(this.getOperationContext().getSBB() instanceof MsSessionControllerImpl ){
									((MsSessionControllerImpl)this.getOperationContext().getSBB()).setSupportedMediaTypes(response);	
								}
					    	} catch (MediaServerException e) {
					    		logger.error("Exception occured in setSupportedMediaTypes() : "+e);
					    	}
				}
				else {
					if (logger.isInfoEnabled())
					logger.info("<SBB> No content associated with response request");
				}
				
				//send ACK to partyB with modified SDP of 2XX response from 
				//reInvite to A.
				SipServletResponse okFromB = (SipServletResponse)requestB.
            getSession().getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
				SipServletRequest ackToB  = okFromB.createAck();
        if (contentLength != 0) {
        	/*
			 * Reeta 
			 * in case of auto response code false which is black holed flow as per rfc 3725 sdp should go
			 * in invite to B not in ack to B to commenting sending sdp to ack to B here 
			 */
        			
				// not sending SDP in ACK to B party in case of dialout |changes for sending SDP in Re-invite| end				
			
//					String contentType = response.getContentType();
//					if ( contentType.indexOf("application/sdp") == -1 ) {
//						logger.error( contentType + " is not supported in platform");
//						logger.error(" so not modifying SDP. Sending Ack to PartB without modifying SDP");
//						 ackToB.setContent(response.getContent() , contentType);
//					} else {
//          	            ackToB.setContent(this.modifySDP(response.getRawContent()) , contentType);
//					}
        	/*
        	 * commenting ends here
        	 */
        } else {
					logger.error("Received null SDP in 2XX resoponse for reInvite to A");
					//First send Ack for 2XX response then  send BYE to A and BYE to B
					sendRequest(response.createAck());
					sendRequest(ackToB);
					sendRequest(requestA.getSession().createRequest(Constants.METHOD_BYE));
					sendRequest(requestB.getSession().createRequest(Constants.METHOD_BYE));
					setCompleted(true);
					// fire CONNECTION_FAILED
					int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
					SBBEvent.REASON_CODE_ERROR_RESPONSE,response); //reeta changed from requestA ->response
					if (logger.isInfoEnabled())
					logger.info("<SBB>Received from application :"+retValue);
					return;
				}
                                                                                                                         
        // remove the attribute from session before sending ack
        requestA.getSession().removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
                                                                                                                         
        sendRequest(ackToB);
				//send Ack to Party A
        sendRequest(response.createAck());
                                                                                                                         
        setCompleted(true);
        //fire Event connected
        int retValue = fireEvent(SBBEvent.EVENT_CONNECTED,SBBEvent.REASON_CODE_SUCCESS,response);
			}
		
			else if (SBBResponseUtil.isNon2xxFinalResponse(response) &&
        response.getRequest() == reInviteToA && isCompleted() == false) {

				//Received final response for reInvite. cancel the timer if not NULL
				if(this.noAnsTimerReInviteA != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Received final response for re-Invite to A: Cancelling NO-ANS timer");
					}
					this.noAnsTimerReInviteA.cancel();
				}

				//Received non 2XX final response from re-INVITE to A
				//Now terminate the dialog.
				//send ACK to partyB
				SipServletResponse okFromB = (SipServletResponse)requestB.
            getSession().getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
        SipServletRequest ackToB  = okFromB.createAck();
				sendRequest(ackToB);

				//Create BYE request and send it to party B
				sendRequest(requestB.getSession().createRequest(Constants.METHOD_BYE));
				sendRequest(requestA.getSession().createRequest(Constants.METHOD_BYE));
				//Mukesh
				//Sending NOn 2xx response to A party in CPA call flow.
				if(attCPACheck.equalsIgnoreCase(AseStrings.TRUE_SMALL)&& attCancelRecvd.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
					if(logger.isDebugEnabled()){
						logger.debug("Get non -2xx for reInvite to A(A in Dialout ) and sending non 2xx to originating A party (not A in dialout) ");
					}
 					SipServletResponse responseToInitialA =  origRequest.createResponse(response.getStatus());
					try{
						sendResponse(responseToInitialA,false);
					}catch(Exception e){
						logger.error("Exception will sending non 2xx response to Orignal A after reinvite in Dialout.");
					}
				}
				setCompleted(true);
				// fire CONNECTION_FAILED
				int retValue = fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
				SBBEvent.REASON_CODE_ERROR_RESPONSE,response); //reeta changed from requesta->response
				if (logger.isInfoEnabled())
				logger.info("<SBB>Received from application :"+retValue);
			}

			// No needs to ACK Non-2xx final response to A as container will do it.
			
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
		}	
	}


	private void handleNonInviteResponse(SipServletResponse response) {


		/**
		 * Following responses are expected for Non-Invite requests
		 *
		 * 1. 200 for PRACK	A 		:  Log & Ignore	 
		 *
		 * 2. Non-200 for PRACK		
		 *	  from A 				:  Fire CONNECT_FAILED 
		 *
		 * 3. 200 for BYE from A	:   Log & Ignore
		 *
		 * 4. 200 for PRACK from B	:   Log & Ignore
		 * 
		 */	


		// 2xx for PRACK from A
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) && 
			SBBResponseUtil.is200Ok(response) &&
		 	response.getSession().getId().equalsIgnoreCase(requestA.getSession().getId())) {
			if (logger.isInfoEnabled())
			logger.info("<SBB> Received 200 OK for PRACK from A");
		}


		// Non 2xx final for PRACK from A
		else  if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) &&
			SBBResponseUtil.isNon2xxFinalResponse(response) &&
            response.getSession().getId().equalsIgnoreCase(requestA.getSession().getId())) {
			if (logger.isInfoEnabled())
			logger.info("<SBB> Received Non-2xx final response from A"+
					",firing <CONNECT_FAILED> event ");
			
			setCompleted(true);
            fireEvent(SBBEvent.EVENT_CONNECT_FAILED,
                     SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
		}


		// 200 for BYE from dialed out party
		else  if (response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE) &&
            SBBResponseUtil.is200Ok(response) &&
            response.getSession().getId().equalsIgnoreCase(requestA.getSession().getId())) {
			if (logger.isInfoEnabled())
			logger.info("<SBB> Received 200 for BYE from dialed out party. ");
		}

		// 200 OK for PRACK from B
		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) &&
            SBBResponseUtil.is200Ok(response) &&
            response.getSession().getId().equalsIgnoreCase(requestB.getSession().getId())) {
			if (logger.isInfoEnabled())
            logger.info("<SBB> Received 200 OK for PRACK from A");
        }


	}
	
	//This method modifies the origin line of sdp. Infact it increases the 
	//version in origin line by 1
	private byte[] modifySDP(byte[] content) {
    int i=0;
		String versionNumber = Integer.toString(++m_sdpVersionNumber);
		byte[] ver = versionNumber.getBytes();

		//find the index of origin line
    for (i =0 ; i<content.length ;i++ ) {
      if ( content[i] =='o' && content[i+1]=='='){
        System.out.println(i);
        break;
      }
    }
		//Example origin line : 'o=sas 12   1212 IN 127.0.0.0'

		//move past o= in origin line
    i=i+2;
	
    //moved past o=sas
    while( content[i] != ' ' && content[i] !='\t' ) {
      i++;
    }
    
		//Ignoring more than one white spaces
    while ( content[i] == ' ' || content[i] =='\t' ) {
      i++;
    }
		
		//reading not white space after sas
    while( content[i] != ' ' &&  content[i] !='\t' ) {
      i++;
      System.out.println(i);
    }
		//Ignoring more than one white spaces
    while ( content[i] == ' ' || content[i] =='\t' ) {
      i++;
    }
                                                                                                                         
    int startIndex=i;
    while ( content[i] != ' ' &&  content[i] !='\t' ) {
      i++;
    }
    //i has gone one beyond
		
    int lengthDiff = ver.length-(i-startIndex);
		//length  of version number in content and new version number is same so 
		//no need to create new byte aray. Modifying orinial byte arary
    if ( (i-startIndex) == ver.length ) {
      for (int m=0 ;startIndex<i;startIndex++) {
        content[startIndex]= ver[m++];
      }
			if (logger.isDebugEnabled())	
			logger.debug("New SDP created is :" + new String(content));
			return content;
    } else {
			//creating new byte Array
    	byte[] newContent = new byte[lengthDiff+content.length];
      int m=0;
      for (m=0; m<startIndex ; m++ ) {
        newContent[m] = content[m];
      }
      for (int n =0; n<ver.length ; m++,n++ ) {
        newContent[m] = ver[n];
      }
      for (;i<content.length ; i++,m++) {
        newContent[m] = content[i];
      }
			if (logger.isDebugEnabled())	
			logger.debug("New SDP created is :" + new String(newContent));
			return newContent;
    }
	}
	
	public synchronized void timerExpired(ServletTimer timer){
		if(timer.equals(noAnsTimerA) && !receivedFinalRespForA) {
			if(requestA != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("timerExpired() called: Sending CANCEL to Party A");
				}
				try {
					SipServletRequest cancel = requestA.createCancel();
					sendRequest(cancel);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				logger.error("timerExpired() : requestA is NULL");
			}
		} else if(timer.equals(noAnsTimerB) && !receivedFinalRespForB) {
			if(requestB != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("timerExpired() called: Sending CANCEL to Party B");
				}
				try {
					SipServletRequest cancel = requestB.createCancel();
					sendRequest(cancel);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				logger.error("timerExpired() : requestB is NULL");
			}
		}
	}

	public void setNoAnswerTimeout(int timeout) {
		this.noAnswerTimeout = timeout;
	}
	
	public void cancel() {
		if (this.requestA != null && this.requestB != null){
			this.failDialoutParties(this.requestA.getSession(),this.requestB.getSession(), null);	
		}else if (this.requestA != null && this.requestB == null){
			this.failDialoutParties(this.requestA.getSession(),null, null);
		} else {
			logger.error("No dailout operation is in progrees thus no need to cancel");
		}
		
	}
	
	private void failDialoutParties(SipSession upStreamSession,SipSession downStreamSession,SipServletResponse response) {
		// Extract the Upstream session state.
		int upStreamsessionState = upStreamSession == null ? -1 : ((Integer) upStreamSession.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		//Handle the UpStream Session
		switch (upStreamsessionState) {
		case Constants.STATE_UNDEFINED:
		case Constants.STATE_EARLY:
			if (logger.isInfoEnabled())
			logger.info("<SBB> UpStream Dialog is in INITIAL/EARLY state, sending a CANCEL request");
			SipServletRequest cancelOut = requestA.createCancel();
			// Adding Reason Header in CANCEL request
			if (response != null) {
				this.addReasonHeader(response, cancelOut);
			}
			try {
				this.sendRequest(cancelOut);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			break;
		case Constants.STATE_CONFIRMED:
			if (logger.isInfoEnabled())
			logger.info("<SBB> UpStream Dialog is in CONFIRMED state, so sending a BYE request");
			SipServletResponse previous200FromA = (SipServletResponse) upStreamSession.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);

			// Send an ACK if the ACK was not sent earlier.
			if (previous200FromA != null) {
				if (logger.isInfoEnabled())
				logger.info("<SBB> UpStream Dialog is in CONFIRMED state, Sending the ACK to the originating end.");
				SipServletRequest ackOut = previous200FromA.createAck();
				try {
					this.sendRequest(ackOut);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			try {
				if (logger.isInfoEnabled())
				logger.info("<SBB> Sending BYE to party Session");
				SipServletRequest byeOut = upStreamSession.createRequest(Constants.METHOD_BYE);
				// Adding Reason Header in CANCEL request
				if (response != null) {
					this.addReasonHeader(response, byeOut);
				}
				sendRequest(byeOut);
				byeToPartyA = true;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			break;
		case Constants.STATE_TERMINATED:
			if (logger.isInfoEnabled())
			logger.info("<SBB> UpStream Dialog is in TERMINATED state, so not doing anything");
			break;
		default:
			if (logger.isInfoEnabled())
			logger.info("<SBB> Unknown UpStream dialog state. So ignoring....");
		}

		// Extract the DownStream session state.
		int downStreamsessionState = downStreamSession == null ? -1 : ((Integer) downStreamSession.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		// Handle the downstream session.
		switch (downStreamsessionState) {
		case Constants.STATE_UNDEFINED:
		case Constants.STATE_EARLY:
			if (logger.isInfoEnabled())
			logger.info("<SBB> Downstream Dialog is in INITIAL/EARLY state, sending a CANCEL request");
			SipServletRequest cancelOut = requestB.createCancel();
			// Adding Reason Header in CANCEL request
			if (response != null) {
				this.addReasonHeader(response, cancelOut);
			}
			try {
				this.sendRequest(cancelOut);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			break;
		case Constants.STATE_CONFIRMED:
			if (logger.isInfoEnabled())
			logger.info("<SBB> Downstream Dialog is in CONFIRMED state, so sending a BYE request");
			SipServletResponse previous200FromB = (SipServletResponse) downStreamSession.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);

			// Send an ACK if the ACK was not sent earlier.
			if (previous200FromB != null) {
				if (logger.isInfoEnabled())
				logger.info("<SBB> Downstream Dialog is in CONFIRMED state, Sending the ACK to the terminating end.");
				SipServletRequest ackOut = previous200FromB.createAck();
				try {
					this.sendRequest(ackOut);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			try {
				if (logger.isInfoEnabled())
				logger.info("<SBB> Sending BYE to party Session");
				SipServletRequest byeOut = downStreamSession.createRequest(Constants.METHOD_BYE);
				// Adding Reason Header in CANCEL request
				if (response != null) {
					this.addReasonHeader(response, byeOut);
				}
				sendRequest(byeOut);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			break;
		case Constants.STATE_TERMINATED:
			if (logger.isInfoEnabled())
			logger.info("<SBB> Downstream Dialog is in TERMINATED state, so not doing anything");
			break;
		default:
			if (logger.isInfoEnabled())
			logger.info("<SBB> Unknown downstream dialog state. So ignoring....");
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

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.isRTPTunnelingEnabled = in.readInt();
		this.noAnswerTimeout = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.isRTPTunnelingEnabled);
		out.writeInt(noAnswerTimeout);
	}
		
}
