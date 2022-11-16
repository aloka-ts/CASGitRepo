/*
 * @(#)OneWayDisconnectHandler.java	1.0 11 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipSession.State;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;



import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import com.traffix.openblox.core.fsm.stack.StackEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.timer.TimerInfo;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sipconnector.AseSipSession;


/**
 * Implementation of the disconnect handler.
 * This class is responsible for handling disconnect operation. 
 * Disconnect operation handles the signalling level details 
 * for disconnecting a  party from a connected call. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class OneWayDisconnectHandler extends BasicSBBOperation {

	private static final long serialVersionUID = -334204388243342970L;
	public static final int PARTY_UNDEFINED = -1;
	public static final int PARTY_A = 0;
	public static final int PARTY_B = 1;


	private int partyTobeDisconnected = PARTY_UNDEFINED;



	 /** Logger element */
    private static Logger logger = Logger.getLogger(OneWayDisconnectHandler.class.getName());

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public OneWayDisconnectHandler() {
		super();
	}

	/**
     *  This method defined party to be disconnected from a connected B2BUA session.
     */
    public void setDisconnectParty(int  party) throws IllegalArgumentException {
        if (party != PARTY_A && party != PARTY_B) {
            logger.error("<SBB> Invalid party specified");
            throw new IllegalArgumentException("Invalid party specified");
        }
        partyTobeDisconnected = party;
    }


	/**
     *  This method will retuen the disconnect party set by user.
     */
	public int getDisconnectParty() {
		return partyTobeDisconnected;
	}

	

	/**
     * This method will be invoked to start disconnect operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     */
	public void start() throws ProcessMessageException {
		if(logger.isDebugEnabled())
			logger.debug("<SBB>entered start() ");

        if (partyTobeDisconnected == PARTY_UNDEFINED) {
            logger.error("<SBB> No party assiciated with handler.");
            throw new ProcessMessageException("No party assiciated with handler.");
        }

		SBB sbb = (SBB)getOperationContext();
		SipSession session = null;
		if ( partyTobeDisconnected == PARTY_A) {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received a request to disconnect party-A");
			session = sbb.getA();
			State sipSessionState = session.getState();
			if(sipSessionState == State.TERMINATED) {
				if(logger.isDebugEnabled()) {
					logger.debug("Party A already got disconnected, so returning");
				}
				return;
			}
		}
		else {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received a request to disconnect party-B");
			session = sbb.getB();
		}	
		
		try {
			/*
			 *  Check the dialog state
			 *	if dialog state is CONFIRMED send BYE.
			 *  if dialog state is EARLY send CANCEL.
			 *	otherwise throw IllegalStateException 
			 */
			
			//int dialogState = ((Integer)session.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

         SipServletRequest request = null;
         
         State sipSessionState = session.getState();
         SipServletRequest iniRequest = (SipServletRequest)session.getAttribute(Constants.ATTRIBUTE_INIT_REQUEST);	
         //if ((dialogState == Constants.STATE_UNDEFINED) || (dialogState == Constants.STATE_EARLY)) {
         if ((sipSessionState == State.EARLY) || (sipSessionState == State.INITIAL)) {
				if(logger.isInfoEnabled())
					logger.info("<SBB> Dialog is in EARLY state so creating a CANCEL request instead of BYE");	
	            if (iniRequest == null) {
	               throw new IllegalArgumentException("Unable to get initial request from session");
	            }
	            if(partyTobeDisconnected == PARTY_A){
	            	//Need to send the response code received from Service
	            	Integer responseCode = (Integer) sbb.getAttribute(MsSessionController.PARTY_A_EARLY_RESP_CODE);
	            	SipServletResponse resOut = null;
	            	if (responseCode != null){
	            		resOut = iniRequest.createResponse(responseCode);
	            	}else{
	            		resOut = iniRequest.createResponse(487);	
	            	}
	            	String reasonHdr = (String) sbb.getAttribute(MsSessionController.PARTY_A_REASON_HDR);
	            	
	            	if (reasonHdr != null){
	            		resOut.addHeader(Constants.HDR_REASON, reasonHdr);
	            	}
	            	byte[] rel_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.REL_ISUP);
					//for SIP-T calls
					if(rel_isup != null) {
						Multipart mp = new MimeMultipart();
						if(logger.isDebugEnabled())
							logger.debug(" setting REL in 487 to Party A");								
						String isupContentType = getIsupContentTypeForA(iniRequest); 
						if (isupContentType == null)
							SBBResponseUtil.formMultiPartMessage(mp, rel_isup, Constants.ISUP_CONTENT_TYPE, null);
						else
							SBBResponseUtil.formMultiPartMessage(mp, rel_isup, isupContentType, null);
						resOut.setContent(mp, mp.getContentType());
					}
					String pCdrInfoHeaderVal = (String) this.getOperationContext().getSBB().getAttribute(MsSessionController.CDR_INFO_HEADER);
					if (pCdrInfoHeaderVal != null){
						if(logger.isDebugEnabled())
							logger.debug(" setting P-CDR-INFO in 487 to Party A");
						resOut.addHeader(MsSessionController.CDR_INFO_HEADER,pCdrInfoHeaderVal);
					}
					sendResponse(resOut, false);
					//This is done as we disconnect Party-A after Party-B and we will not get any 
					//response/request further so raising the disconnected event
					setCompleted(true);	
					if(logger.isInfoEnabled())
						logger.info("<SBB> firing <DISCONNECT> event");
		            fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_SUCCESS,
		            		resOut);
					return;
	            }
	            else
	            	request = iniRequest.createCancel(); 
			}
         else if (sipSessionState == State.CONFIRMED) {	
         //else if (dialogState == Constants.STATE_CONFIRMED) {
				//if 200Ok recieved but ack not sends then first sends ack then bye
				// INVITE-2XX ACK
				if(((AseSipSession)session).isAckOutstanding()){
					SipServletResponse resp2xx = (SipServletResponse) session.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
					if(resp2xx!=null){
						SipServletRequest ackRequest = resp2xx.createAck();
					    sendRequest(ackRequest);
					    session.removeAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
						if(logger.isDebugEnabled())
							logger.debug("Ack send to b-party now sending bye.");	
					}
				}
				//---
				if(logger.isInfoEnabled())
					logger.info("<SBB> Dialog is in CONFIRMED state so creating a BYE request");	
				if(partyTobeDisconnected == PARTY_A){
					request = session.createRequest(Constants.METHOD_BYE);
					byte[] rel_isup = (byte[])this.getOperationContext().getSBB().getAttribute(MsSessionController.REL_ISUP);
					//for SIP-T calls
					if(rel_isup != null) {
						if(logger.isInfoEnabled())
							logger.info(" setting REL in BYE to Party A");								
						Multipart mp = new MimeMultipart();
						//This is done as ATTRIBUTE_INIT_REQUEST is removed from the session on receiving 200 OK 
						if (iniRequest == null)
							iniRequest = (SipServletRequest)session.getAttribute(Constants.ATTRIBUTE_INIT_REQUEST_FOR_CONTENT);
						
						String isupContentType = getIsupContentTypeForA(iniRequest); 
						if (isupContentType == null)
							SBBResponseUtil.formMultiPartMessage(mp, rel_isup, Constants.ISUP_CONTENT_TYPE,null);
						else
							SBBResponseUtil.formMultiPartMessage(mp, rel_isup, isupContentType, null);
						
						String reasonHdr = (String) sbb.getAttribute(MsSessionController.PARTY_A_REASON_HDR);
						if (reasonHdr != null){
							request.addHeader(Constants.HDR_REASON, reasonHdr);
						}else{
							//request.addHeader(Constants.HDR_REASON,Constants.REASON_HDR_ISUP_VAL);
						}
						request.setContent(mp, mp.getContentType());
					}
				}else {
					
					if(logger.isDebugEnabled()){
						logger.debug("create BYE for party B i.e. MS");
					}
					request = session.createRequest(Constants.METHOD_BYE);	
					
					String reasonHdr = (String) sbb.getAttribute(MsSessionController.PARTY_A_REASON_HDR);
					if (reasonHdr != null){
						if(logger.isDebugEnabled()){
							logger.debug("add reason hdr to bye");
						}
						request.addHeader(Constants.HDR_REASON, reasonHdr);
					}
					//---cancel session expiry timer 
					//NULL Check provided as in case of FT Service will always call disconnect Media Server
					//and at that time we don't have A session.
					ServletTimer forA_party = null;
					String timerId=null;
					if (sbb.getA() != null){
						timerId=(String) sbb.getA().getAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
						forA_party =sbb.getA().getApplicationSession().getTimer(timerId);
					}
					timerId=(String) sbb.getB().getAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
					ServletTimer forMS =sbb.getB().getApplicationSession().getTimer(timerId);  
						if(forA_party!=null){
							forA_party.cancel();
							if(logger.isDebugEnabled()){
								logger.debug("timer cancelled for A_PARTY");
							}
							sbb.getA().removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_A_PARTY);
							 if(logger.isDebugEnabled())
								logger.debug("session expires timer attribute remove for A-party");
						}
					 
						if(forMS!=null){
							forMS.cancel();
							if(logger.isDebugEnabled()){
								logger.debug("timer cancel for media_server");
							}
							sbb.getB().removeAttribute(Constants.SESSION_EXPIRY_TIMER_FOR_MS);
							if(logger.isDebugEnabled())
								logger.debug("session expires timer attribute remove for B-party");
						}
				}
			}
			else {
				logger.error("<SBB> Invalid Dialog state :: <"+sipSessionState+">");
				throw new IllegalStateException("Invalid Dialog state:terminated or initial");
	
			}
			
		    if(sbb.getClass().getName().equals("com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")
		    		&& partyTobeDisconnected == PARTY_B){
				if(logger.isInfoEnabled())	
					logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
				request.setAttribute("DISABLE_OUTBOUND_PROXY", "");
			}
			sendRequest(request);
			if(logger.isInfoEnabled())
				logger.info("<SBB> Sending a <" + request.getMethod() +"> request");
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
			throw new ProcessMessageException(exp.getMessage());	
		} catch (MessagingException e) {
			logger.error(e.getMessage(),e);
			throw new ProcessMessageException(e.getMessage());	
		} catch (Rel100Exception e) {
			logger.error(e.getMessage(),e);
			throw new ProcessMessageException(e.getMessage());
		}
		if(logger.isDebugEnabled())
			logger.debug("<SBB>exited start() ");
    }


	public void ackTimedout(SipSession session) {
    }

	public void prackTimedout(SipSession session) {
    }


	public void handleRequest(SipServletRequest request) {
		logger.error("<SBB> Error: Received a request on disconnect handler, ");
	}


	/**
     * This method  handles all response from party issued a BYE request
     *
     * @response - Response .
     */
    public void handleResponse(SipServletResponse response) {
			handleResponse(response,true);
	}


	/**
     * This method  handles all response from party issued a BYE request 
     *
     * @response - Response .
     */
	protected void handleResponse(SipServletResponse response,boolean isCompleted) {
		if(logger.isDebugEnabled())
			logger.debug("<SBB> entered handleResponse with following response <"+
				response.getStatus() +","+response.getMethod()+
				"> & iscompleted is "+isCompleted);

		boolean opResult=false;

		// This response can be in response to BYE or CANCEL request

		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_BYE)) {
			handleBYEResponse(response,isCompleted);
		}
		else if (response.getMethod().equalsIgnoreCase(Constants.METHOD_CANCEL)) {
			handleCANCELResponse(response,isCompleted);
		}
	}

	/**
	 *	This method will take care all possible response from UA is start() method
	 *  has send a BYE request.
	 *	OneWayDisconnectHandler will set with isCompleted to 'true' whereas Disconect
	 *  handler should call it with 'false' first time as 'true' second time. In case 
	 *  of error setCompleted will be called with true and DISCONNECTED should
	 *  be fired withot checking isCompleted flag and method should return with 'true'. 

	 */		
	private void  handleBYEResponse(SipServletResponse response,boolean isCompleted) {
		if(logger.isDebugEnabled())
			logger.debug("<SBB> Entered handleBYEResponse with isCompleted ="+isCompleted );
		
		/*
         *  Only possibility here is that a BYE was sent to either A or B, one of
         *  following two responses are possble.
         * 
         *  1. party responded 200 OK   :   fire disconnected event
         *  2. party responsed with
         *     error response           :   fire disconnected event
         */ 

		//  200 OK for BYE
		if(SBBResponseUtil.is200Ok(response) ) {		
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received 200 OK for BYE");
		}
		 else if (SBBResponseUtil.isNon2xxFinalResponse(response)) {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received Non-2xx final response for BYE, "+
                                        "firing <DISCONNECTED> event");
		}

		if ( partyTobeDisconnected == PARTY_A) {
           ((SBB)getOperationContext()).removeA();
        }
        else {
             ((SBB)getOperationContext()).removeB();
        }

		if (isCompleted) {
			setCompleted(true);	
			if(logger.isInfoEnabled())
				logger.info("<SBB> firing <DISCONNECT> event");
            fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_SUCCESS,
                                response);

		}
    }



	/**
	 *	This method will take care all possible response from UA is start() method
	 *  has send a CANCEL request.
	 */		

	private void handleCANCELResponse(SipServletResponse response,boolean isCompleted) {

		/**
		 *	A UA can respond with following responses to a CANCEL requests
		 *	
		 *	1.	481 Tx doesn't exist  :	Original INVITE request has been responsed 
		 *								with 2xx response so can't cancel the request
		 *								There is a rare possibility of this as 2xx 
		 *								response as received just before checking for
		 *								dialog state in start() method. 
		 *
		 *	2.	200 OK				  :	CANCEL request accepted by UAS. After canceling 
		 *								the request, UAS will send 487 Request Terminated
		 *								but this response will be handler by Original Req
		 *								handler (ConnectHandler)  & not by as 487 is in 
		 *								response to original (INVITE in most cases) req.
		 */


		if (SBBResponseUtil.is200Ok(response) ) {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received 200 OK for CANCEL, firing <DISCONNECTED> event");
			if (isCompleted) {
            	setCompleted(true);
            	int retValue = fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_SUCCESS,response);
            	if ( retValue==SBBEventListener.CONTINUE )
            	{
    				if ( partyTobeDisconnected == PARTY_A) {
                        ((SBB)getOperationContext()).removeA();
                    }
                    else {
                        ((SBB)getOperationContext()).removeB();
                    }
					if(logger.isInfoEnabled())
						logger.info("<SBB> Disconnect successful");
            	}
            	
            	if( retValue==SBBEventListener.NOOP )
            	{
            	     return;
            	}
            		

			}
        }
		else if (SBBResponseUtil.isNon2xxFinalResponse(response)) {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received Non-2xx final <"+ response.getStatus()+
									"> response for CANCEL, "+
                                    " firing <DISCONNECTED> event");
            setCompleted(true);
            fireEvent(SBBEvent.EVENT_DISCONNECTED,
                    SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
			if(logger.isInfoEnabled())		
				logger.info("<SBB> Disconnect unsuccessful");
        }
        else {
            // Should not come here as disconnect handler is invoked on response of
            // BYE only.
            logger.error("<SBB> Illegal invocation of disconnect handler by SBBServlet");
        }
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.partyTobeDisconnected = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.partyTobeDisconnected);
	}
	
	protected String getIsupContentTypeForA(SipServletRequest iniRequest){
		String contentType = null; 
		try{
			if (iniRequest.getContentType().startsWith(Constants.SDP_MULTIPART_MIXED)) {
				MimeMultipart mimeMultipart = (MimeMultipart) iniRequest.getContent();
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
}
