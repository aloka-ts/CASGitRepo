package com.baypackets.ase.sbb.b2b;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.lang.Exception;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.TimerService;

import com.baypackets.ase.sbb.timer.TimerInfo;

import javax.servlet.sip.ServletTimer;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.b2b.ConnectHandler;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.util.AseStrings;

@DefaultSerializer(ExternalizableSerializer.class)
public class EarlyMediaConnectHandler extends ConnectHandler implements EarlyMediaCallback {
	
	private static final long serialVersionUID = 407142438024334297L;
	private static final Logger logger = Logger.getLogger(EarlyMediaConnectHandler.class);
	
	private transient SBBImpl mySbb = null;
	protected boolean earlyMedia = false;

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public EarlyMediaConnectHandler() {
		super();
	}

	public EarlyMediaConnectHandler(SipServletRequest request, Address partyB, Address from) {
		super(request, partyB, from);
		// TODO Auto-generated constructor stub
	}

	public EarlyMediaConnectHandler(SipServletRequest incomingReq, Address addressB) {
		super(incomingReq, addressB);
	}

	public void start() throws ProcessMessageException {
		String strEarlyMedia = (String) this.getSBB().getAttribute(SBB.EARLY_MEDIA);
		this.earlyMedia = strEarlyMedia != null && strEarlyMedia.equalsIgnoreCase(AseStrings.TRUE_SMALL); 
		super.start();
	}
	
	public void handleResponse(SipServletResponse response) {
		SBB sbb = (SBB)getOperationContext();
		int responseCode = response.getStatus();
		if(responseCode >=200 && responseCode < 299 && response.getMethod().equals(Constants.METHOD_INVITE)){
			
			if(earlyMedia){
				try{
					//this.startSessionExpiryTimerAsUAC(response);
					this.startSessionExpiryTimer(response, Constants.TIMER_FOR_MS, Constants.SESSION_EXPIRY_TIMER_FOR_MS);
					//-------------------
					if (logger.isInfoEnabled()) {
						logger.info("Going to do early media handling for this response....");
						logger.info("[.....] Setting PRACK_UPDATE_FLOW to true in app session,");
						logger
							.info("since earlyMedia is local to the MediaSBB, hence becomes null when handling goes to any other SBB.");
					}
					this.getSBB().getApplicationSession().setAttribute(Constants.PRACK_UPDATE_FLOW,AseStrings.TRUE_SMALL);
					//extracting party B SDP and storing in session
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
	
					//for SIP-T ... firing EARLY_MEDIA_CONNECT_PROGRESS EVENT
					if (logger.isInfoEnabled()) {
						logger.info("<SBB>Mid call signalling, firing EARLY_MEDIA_CONNECT_PROGRESS EVENT ");
					}
					/* When this event is fired, it is expected that the application will set the ACM-CPG-ANM-TIMEOUT
					 * attributes in the sbb, so that the flow is executed properly.
					 */
					this.fireEvent(SBBEvent.EVENT_EARLY_MEDIA_CONNECT_PROGRESS,SBBEvent.REASON_CODE_SUCCESS, response.getRequest());
					boolean aSupports100Rel = SBBResponseUtil.supports100Rel(this.requestIn);
					
					Boolean relay2xxResponse = (Boolean) sbb.getAttribute(SBB.RELAY_2XX_IN_EARLY_MEDIA);
					relay2xxResponse = relay2xxResponse != null ? relay2xxResponse : false;
					if(sbb.getAttribute(Constants.UPDATE_NEEDED)!=null &&
					   sbb.getAttribute(Constants.UPDATE_NEEDED).toString().equalsIgnoreCase(AseStrings.TRUE_CAPS)){
						
						//setting the response2xx variable value of ConnectHandler
						//This is done as response to UPDATE request comes to Connect Handler 
						//and response2XX is needed to generate the ACK
						this.response2xx = response;
						
						SipSession partyA = sbb.getA();
					   	SipServletRequest updateA = partyA.createRequest("UPDATE");
					   	if(response.getContentLength() > 0){
					   		try{
								updateA.setContent(response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP),
										  (String)response.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE));
							    if(response.getHeader(Constants.HDR_CONTENT_DISPOSITION)!=null){
							    	updateA.setHeader(Constants.HDR_CONTENT_DISPOSITION,response.getHeader(Constants.HDR_CONTENT_DISPOSITION));
							    }else{
							    	updateA.setHeader(Constants.HDR_CONTENT_DISPOSITION,Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
							    }
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
						
						//if sdp not present in invite then hold this ack 
						//or If received Invite is not reliable 
						//or if need to relay 2xx response to A in which ack will be sent to B
						//once we received it from A party
						if(!this.isInviteWithoutSDP() /*&& !aSupports100Rel && !relay2xxResponse*/){
							if(logger.isDebugEnabled()) {
								logger.debug("Sending ack to B party");
							}
							this.ackToB = response.createAck();
							if(((SBB)getOperationContext()).getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
								if (logger.isInfoEnabled()) {
									logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
								}
								//ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
								disableObgwproxy(sbb.getApplicationSession(),
										null, ackToB);
							}
							this.sendRequest(ackToB);
							
						}else{
							response.setAttribute(Constants.IS_ACK_PENDING, AseStrings.TRUE_SMALL);
						}
						// sending ACK to party B before doing any further processing..
						/*this.ackToB = response.createAck();
						if(((SBB)getOperationContext()).getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
							ackToB.setAttribute("com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY", "");
						}
						this.sendRequest(ackToB);*/
						
						
						SipServletResponse responseOut = super.generateProvisionalResponse(response);
						if(responseOut == null){
							if(logger.isDebugEnabled()){
								logger.debug("got null provisional reposne");
							}
							this.failOperation(SBBEvent.REASON_CODE_ERROR);
							return;
						}
					
						//Check whether the party-A supports 100rel or NOT....
						if(logger.isDebugEnabled()){
							logger.debug("Party Supports 100 Rel :::" + aSupports100Rel);
						}
						SipApplicationSession appSession = sbb.getApplicationSession();
				    	String pChargingVector = (String) appSession.getAttribute(MsSessionController.P_CHARGE_VECTOR);
						if(aSupports100Rel){
							//setting the response2xx variable value of ConnectHandler
							this.response2xx = response;
							//send 183 and then ACK					
							//Send the response reliably to the party-A.
							//The EARLY_MEDIA will be generated when PRACK is received from party-A.
							
							byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);
							if (anm_isup != null) {
								
								if(logger.isDebugEnabled()){
									logger.debug("ANM is set by the application it seems application want to send ANM here raise early connected as well :::");
								}		
									this.connect();	
									this.fireEvent(SBBEvent.EVENT_EARLY_MEDIA,SBBEvent.REASON_CODE_SUCCESS, null);
							
							} else {
								if(pChargingVector != null) {
									if(logger.isDebugEnabled()) {
										logger.debug("setting P-Charging-Vector in 1xx reponse to party A");
									}
									responseOut.setHeader(MsSessionController.P_CHARGE_VECTOR, pChargingVector);
								}
								this.sendResponse(responseOut, true);
							}
						}else{
							//Send the not reliably.
							//Since we will not get the PRACK in this case,
							//Send ACK to the Media Server and raise the EARLY_MEDIA event to the application.
							
							if(pChargingVector != null) {
								if(logger.isDebugEnabled()) {
									logger.debug("setting P-Charging-Vector in 1xx reponse to party A");
								}
								responseOut.setHeader(MsSessionController.P_CHARGE_VECTOR, pChargingVector);
							}
							this.sendResponse(responseOut, false);
							//this.generateEarlyMediaEvent();
							if(!relay2xxResponse)
								this.generateEarlyMediaEvent(null, response);
						}
					}
					if(relay2xxResponse && !aSupports100Rel) {
						if(logger.isDebugEnabled()) {
							logger.debug("relay 2xx to A-party in early media");
						}
						super.handleResponse(response);
					}
				}catch(Rel100Exception e){
					this.failOperation(SBBEvent.REASON_CODE_A_REL100_NOT_SUPPORTED, e);
				}catch(IOException e){
					this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
				}
				
				
				return;
			}
		}
		
		//Do the default handling as done in the base class.
		super.handleResponse(response);
	}
		
	public void handleRequest(SipServletRequest request) {
		SBB sbb = (SBB) this.getOperationContext();
		SipSession partyA = sbb.getA();
		if(earlyMedia && request.getMethod().equals("PRACK")){
			if(request.getSession().getAttribute(Constants.CPG_PLAY_SEND)!=null){
				if(logger.isDebugEnabled()){
					logger.debug("Prack received for 183 progress before sending info ::cpg");
				}
				try{
					SipServletResponse resp = request.createResponse(200);
					sendResponse(resp, false);
					if(logger.isDebugEnabled()){
						logger.debug("cpg::200Ok sends for PRACK");
					}
				}catch(Rel100Exception e){
					logger.error(e.getMessage(),e);
				}
				catch(IOException e){
					logger.error(e.getMessage(),e);
				}
				return;
			}
			try{
				SipSession partyB = this.getSBB().getB();
				
				//Send a 200 OK upstream.
				if(logger.isDebugEnabled()){
					logger.debug("Sending a 200 response for PRACK");
				}
				//if initial invite doesn't contain sdp
				//ADDED is ack pending to isolate form update needed and without sdp occuring together.
				if (this.isInviteWithoutSDP()
						&& response2xx.getAttribute(Constants.IS_ACK_PENDING) != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("sdp null in invite so send ack after prack");
					}
					SipServletRequest ackToB = this.response2xx.createAck();
					ContentType ct = getSDP(request);
					if (ct != null) {

						ackToB.setContent(ct.getContent(), ct.getContentType());
					}
					if (sbb.getClass().getName()
							.equals(
									"com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")) {
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}
					//	ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
						
						disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);
					}
					response2xx.removeAttribute(Constants.IS_ACK_PENDING);
					this.sendRequest(ackToB);
				}
				
				SipServletResponse respOut = request.createResponse(200);
				this.sendResponse(respOut, false);
				/*boolean aSupports100Rel = SBBResponseUtil.supports100Rel(this.requestIn);
				Boolean relay2xxResponse = (Boolean) sbb.getAttribute(SBB.RELAY_2XX_IN_EARLY_MEDIA);
				relay2xxResponse = relay2xxResponse != null ? relay2xxResponse : false;
				if(AseStrings.TRUE_SMALL.equals(this.response2xx.getAttribute(Constants.IS_ACK_PENDING))
						&& !relay2xxResponse && aSupports100Rel && !this.isInviteWithoutSDP()) {
					this.ackToB = this.response2xx.createAck();
					
					if(((SBB)getOperationContext()).getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}
						//ackToB.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
						disableObgwproxy(sbb.getApplicationSession(),
								null, ackToB);
					}
					this.sendRequest(ackToB);
				}*/
				/*//Send the ACK downstream.
				if(logger.isDebugEnabled()){
					logger.debug("Sending the ACK to the Media Server");
				}
				SipServletResponse previous200FromB = (SipServletResponse)partyB.getAttribute(
						SBBOperationContext.ATTRIBUTE_INV_RESP);
				SipServletRequest ackOut = previous200FromB.createAck();
				if(request.getContentLength() > 0){
					ackOut.setContent(request.getContent(), request.getContentType());
				}
				this.sendRequest(ackOut);
				partyB.removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);*/
				Boolean relay2xxResponse = (Boolean) sbb.getAttribute(SBB.RELAY_2XX_IN_EARLY_MEDIA);
				if(relay2xxResponse) {
					if(logger.isDebugEnabled()) {
						logger.debug("relay 2xx to A-party in early media");
					}
					super.handleResponse(response2xx);
				}else {
					this.generateEarlyMediaEvent(request,null);
				}
				
			}catch(Rel100Exception e){
				logger.error(e.getMessage(), e);
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
			}catch(IOException e){
				logger.error(e.getMessage(), e);
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
			//} catch (MessagingException e) {
				//logger.error(e.getMessage(), e);
				//this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
			}
		}else if(earlyMedia && request.getMethod().equals("ACK") && request.getSession().getId().equalsIgnoreCase(partyA.getId())){
			
			/*try {
				if(ackToB == null) {
					if(logger.isDebugEnabled()) {
						logger.debug("sending ACK to media server");
					}
					
					ackToB = response2xx.createAck();
					
					if(request.getContentLength() > 0) {
						ackToB.setContent(request.getContent(), request.getContentType());
					}
					this.sendRequest(ackToB);
				}
			}catch(IOException exp) {
				logger.error("Exception thrown ..." +exp.getMessage(),exp);
			}*/
			
			//Added a check to see whether we got the ACK from Party-A
			//This is done as in case of session refresh from media server in early media
			//ACK from Media Server coming here and hence confirming the dialog, which
			//is not true. Another side effect for ACK coming here is Party-A decides
			//to cancel the request then CANCEL was coming on the NetworkMessageHandler.
			if(logger.isDebugEnabled()){
				logger.debug("Received ACK. Going to create CONNECTED event");
			}
			this.setCompleted(true, SBBEvent.EVENT_CONNECTED, SBBEvent.REASON_CODE_SUCCESS);
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Received request. Going to call super.handleRequest()");
			}
			super.handleRequest(request);
		}
	}

	public void connect() {
		SBB sbb = (SBB)getOperationContext();
		if(logger.isDebugEnabled()){
			logger.debug("Entered the EarlyMediaCallback.connect() method...");
		}
		try{
			
			if(sbb.getAttribute(Constants.TIMEOUT_REQUIRED)!=null){
				int timeout = Integer.parseInt(sbb.getAttribute(Constants.TIMEOUT_REQUIRED).toString());
				//***************************************************************************************************
				//                              PUT CODE FOR TIMER HERE
				//**************************************************************************************************
				TimerService ts=(TimerService) sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
				ServletTimer timer=ts.createTimer(requestIn.getApplicationSession(), timeout, false, new TimerInfo("TIMEOUT_REQUIRED",this));
			}else{
				SipSession partyB = this.getSBB().getB();
				createAndSendSuccessResponse(partyB, requestIn);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
		}
	}
	
	public void postTimerProcessing(){
		SipSession partyB = this.getSBB().getB();
		createAndSendSuccessResponse(partyB, requestIn);
	}
	
	public void disconnect() {
		if(logger.isDebugEnabled()){
			logger.debug("Entered the EarlyMediaCallback.disconnect() method...");
		}
		this.getSBB().unregisterCallback(EarlyMediaCallback.class.getName());
		this.failOperation(SBBEvent.REASON_CODE_CANCELLED_BY_APPLICATION);
	}

	public void redirect(int responseCode, String[] addressess) throws IllegalArgumentException {
		this.redirect(responseCode, null, addressess);
	}
	
	public void redirect(int responseCode, String reasonPhrase, String[] addressess) throws IllegalArgumentException {
		if(logger.isDebugEnabled()){
			logger.debug("Entered the EarlyMediaCallback.redirect() method...");
		}

		if(responseCode < 300 || responseCode > 399){
			throw new IllegalArgumentException("Response Code should have a value between 300 - 399");
		}
	
		//Added by NJADAUN as a part of bug BPInd13545
		if(addressess == null){
			throw new IllegalArgumentException("Addressess should not be null");
		}
		//ADD complete
		
		//Construct the address list
		ArrayList addressList = new ArrayList();
		try {
			SipFactory factory = (SipFactory)this.getSBB().getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			for(int i=0; i<addressess.length;i++){
				if(addressess[i] == null || addressess[i].trim().equals(""))
					continue;
				addressList.add(factory.createAddress(addressess[i]));
			}
		}catch(ServletParseException e){
			throw new IllegalArgumentException("Invalid Address :" +e.getMessage() );
		}
		if(addressList.isEmpty()){
			throw new IllegalArgumentException("There should be atleast one redirect Address available");
		}
		
		try{
			this.getSBB().unregisterCallback(EarlyMediaCallback.class.getName());
			SipServletResponse response = null;
			if(reasonPhrase != null) {
				response = requestIn.createResponse(responseCode,reasonPhrase);
			} else {
				response = requestIn.createResponse(responseCode);
			}
			
			for(int i=0;i<addressList.size();i++){
				response.addAddressHeader("Contact", (Address)addressList.get(i), false);
			}
			
			this.sendResponse(response, false);
			this.failOperation(SBBEvent.REASON_CODE_CANCELLED_BY_APPLICATION);
		}catch(Rel100Exception e){
			logger.error(e.getMessage(), e);
			this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
		}catch(IOException e){
			logger.error(e.getMessage(), e);
			this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
		}
	}
	
	protected void generateEarlyMediaEvent(){
		//Register an EARLY_MEDIA event to the application.
		if(logger.isDebugEnabled()){
			logger.debug("Raising the EARLY_MEDIA event to the application.");
		}
		this.getSBB().registerCallback(EarlyMediaCallback.class.getName(), this);
		int retValue = this.fireEvent(SBBEvent.EVENT_EARLY_MEDIA,SBBEvent.REASON_CODE_SUCCESS, null);
		if(logger.isDebugEnabled()){
			logger.debug("Application return value :" + retValue);
		}
		switch(retValue){
			case SBBEventListener.CONTINUE:
				this.connect();
				break;
			case SBBEventListener.NOOP:
				break;
		}
	}

	/* Adding this method since a special handling is required under the "EarlyMedia" connect
	 * operation, when PRACK_UPDATE_FLOW is required. Hence, here in this function, which is called 
	 * upon receiving a non-reliable 200OK from party-B, or PRACK in response to 183 sent to party-A
	 * is received. Here we trigger the CONNECTED event, so that the IVR announcements are played 
	 * to the orig party.
	 * Added by Reshu Chaudhary  
	 */
	protected void generateEarlyMediaEvent(SipServletRequest request ,SipServletResponse response ){
		//Register an EARLY_MEDIA event to the application.
		if(logger.isDebugEnabled()){
			logger.debug("Raising the EARLY_MEDIA event to the application.");
		}
		this.getSBB().registerCallback(EarlyMediaCallback.class.getName(), this);
		int retValue = this.fireEvent(SBBEvent.EVENT_EARLY_MEDIA,SBBEvent.REASON_CODE_SUCCESS, null);
		if(logger.isDebugEnabled()){
			logger.debug("Application return value :" + retValue);
		}
		SipSession partyB = this.getSBB().getB();
		SipServletResponse responseOut = null;
		switch(retValue){
			case SBBEventListener.CONTINUE:
				if(response != null)
				{
					if(logger.isDebugEnabled())
						logger.debug("[.....] Called for non-reliable "+response.getStatus()+", so setting CONNECTED state");
					
					/* When we trigger the EARLY_MEDIA event to the application, and it returns 
					 * CONTINUE, then if the application has set the ANM in the session, then it means that
					 * we need to send the 200 OK with this ANM to Party-A, otherwise not.*/
					byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);	
					//183 CPG needs to be send if set by the service, this is done to support prepaid call flows
					byte[] cpg_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.CPG_ISUP);
					if(cpg_isup != null) {
						responseOut = this.generateProvRespWithCPG(cpg_isup,response);
						try{
							this.sendResponse(responseOut, true);	
						}catch(Rel100Exception e){
							this.failOperation(SBBEvent.REASON_CODE_A_REL100_NOT_SUPPORTED, e);
						}catch(IOException e){
							this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						}
						break;
					}else if(anm_isup != null){
						this.connect();
					}
					//this.setCompleted(true, SBBEvent.EVENT_CONNECTED, SBBEvent.REASON_CODE_SUCCESS);
				}
				else if(request != null && request.getMethod().equals("PRACK")){
					/* When we trigger the EARLY_MEDIA event to the application, and it returns 
					 * CONTINUE, then if the application has set the ANM in the session, then it means that
					 * we need to send the 200 OK with this ANM to Party-A, otherwise not.*/
					byte[] anm_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.ANM_ISUP);	
					//183 CPG needs to be send if set by the service, this is done to support prepaid call flows
					byte[] cpg_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.CPG_ISUP);
					if(cpg_isup != null) {
						responseOut = this.generateProvRespWithCPG(cpg_isup,response);
						try{
							this.sendResponse(responseOut, true);	
						}catch(Rel100Exception e){
							this.failOperation(SBBEvent.REASON_CODE_A_REL100_NOT_SUPPORTED, e);
						}catch(IOException e){
							this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
						}
						//This provisional response will always be reliable thus not firing
						//the completed event and thus defering it till the time prack request
						//for this response is received.
						break;
					}else if(anm_isup != null){
						this.connect();
					}
					/*if(logger.isDebugEnabled())
						logger.debug("[.....] Called for "+request.getMethod()+", so setting CONNECTED state");
					this.setCompleted(true, SBBEvent.EVENT_CONNECTED, SBBEvent.REASON_CODE_SUCCESS);*/
				}
				else{
					if(logger.isDebugEnabled())
						logger.debug("[.....] Some error condition... look up in EarlyMediaConnectHandler's handleResponse()/handleRequest()..");
				}
				break;
				
			case SBBEventListener.NOOP:
				break;
		}
	}
	
	protected SBBImpl getSBB(){
		if(mySbb == null){
			this.mySbb = (SBBImpl) this.getOperationContext().getSBB();
		}
		return this.mySbb;
	}
	
	
	public void sessionActivated(SipSession session){
		if(logger.isDebugEnabled()){
			logger.debug("Session Activated called on the EarlyMediaConnectHandler....");
		}
		try{
			SipSession partyA = this.getSBB().getA();
			SipSession partyB = this.getSBB().getB();
			int state = ((Integer)session.getAttribute(
					com.baypackets.ase.sbb.util.Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			if(this.earlyMedia &&
					partyA == null && partyB != null && partyB.equals(session) &&
					state == com.baypackets.ase.sbb.util.Constants.STATE_CONFIRMED){
				if(logger.isDebugEnabled()){
					logger.debug("Sending a BYE request to the Media Server....");
				}		
				SipServletRequest request = session.createRequest("BYE");
				if(((SBB)getOperationContext()).getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")){
					if (logger.isInfoEnabled()) {
						logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
					}
				//	request.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "");
					disableObgwproxy(session.getApplicationSession(),
							null, request);
				}
				this.sendRequest(request);
			}
		}catch(IOException e){
			logger.error(e.getMessage(), e);
		}finally{
			this.failOperation(SBBEvent.REASON_CODE_PEER_SESSION_NOT_REPLICATED);
		}
	}
	
	private SipServletResponse generateProvRespWithCPG(byte[] cpg_isup,SipServletResponse response){
		SipServletResponse responseOut = null ;
		
		if(logger.isDebugEnabled()){
			logger.debug("Sending 18x with CPG");
		}
		responseOut = requestIn.createResponse(183);
		Multipart mp = new MimeMultipart();
		
		try{
			if(response != null && response.getContentLength() > 0){						
				SBBResponseUtil.formMultiPartMessage(mp, (byte[])response.getContent(), response.getContentType(),
						                                         response.getHeader(Constants.HDR_CONTENT_DISPOSITION));						
			}
			String contentType = getIsupContentTypeForA(); 
			
			if (contentType == null)
				SBBResponseUtil.formMultiPartMessage(mp, cpg_isup, Constants.ISUP_CONTENT_TYPE,null);
			else
				SBBResponseUtil.formMultiPartMessage(mp, cpg_isup, contentType, null);
			
			this.getOperationContext().getSBB().setAttribute(MsSessionController.CPG_ISUP, null);
			responseOut.setContent(mp, mp.getContentType());
		}catch(MessagingException me){
			logger.error(me.getMessage(),me);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return responseOut;
	}
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.earlyMedia = in.readBoolean();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeBoolean(this.earlyMedia);
	}
	
	//in case of multipart content type 
	protected ContentType getSDP(SipServletRequest request) throws UnsupportedEncodingException, IOException{
		byte[] bp=null;
		Object content = request.getContent();
		String contentType = request.getContentType();
		
		if (request.getContentLength()<1 || contentType ==null){
			return null;
		}else if(contentType.startsWith(Constants.SDP_CONTENT_TYPE)){
			if(content instanceof byte[]){
				return new ContentType((byte[]) content,contentType);
			}
			if(content instanceof String){
				bp = ((String)content).getBytes();
				return new ContentType(bp,contentType);
			}
		}else if(contentType.startsWith("multipart")){
		// in case of multipart
			try{
				int sdpBpIndx=-1;
				MimeMultipart multipart = null ;
				multipart=(MimeMultipart)content;
				int count=multipart.getCount();
				for(int i=0;i<count;i++){
					if(multipart.getBodyPart(i).getContentType().startsWith(Constants.SDP_CONTENT_TYPE)){
						sdpBpIndx=i;
						break;
					}
				}
				if(sdpBpIndx!=-1){
					
				    ByteArrayInputStream bis=(ByteArrayInputStream) multipart.getBodyPart(sdpBpIndx).getContent(); 
				    int bytes=bis.available();
				    bp=new byte[bytes];
				    bis.read(bp,0,bytes);
				    
				    return new ContentType(bp,contentType); 
				}
				
			}catch(MessagingException e){
				logger.error("Exception in returning SDP "+e);
			}catch(Exception e){
				logger.error("Exception in returning SDP "+e);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("sdp not found in multipart message");
			}
		}//end if
		
		return null;
	}
	
	protected class ContentType{
		byte[] content;
		String contentType;
		
		ContentType(byte[] content,	String contentType){
			this.content =content;
			this.contentType = contentType;
		}
		
		public String getContentType() {
			return contentType;
		}
		
		public byte[] getContent() {
			return content;
		}
		
	}
}
