/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MessageFilter;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl;
import com.baypackets.ase.sbb.mediaserver.MediaServerInfoHandler;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipSessionState;
import com.baypackets.ase.util.AseStrings;

/**
 * Default implementation of the SBBOperation Interface.
 * This class implements the matching of the incoming messages with the
 * in-progress operations using the CSeq header.
 * 
 * If the operation wants to do it differently, then it has to over-ride
 * the isMatching() method on this class. 
 * 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class BasicSBBOperation implements SBBOperation, Externalizable 
{

	private static final long serialVersionUID= -30882811454709843L;
	/** Logger element */
	private static Logger logger = Logger.getLogger(BasicSBBOperation.class.getName());
	
	private SBBOperationContext ctx;
	static final int STATE_INITIAL = 0;
    static final int STATE_UNDEFINED = 0;
    /**
     * Early Dialog state
     */
    static final int STATE_EARLY = 1;

    /**
     * Confirmed Dialog state
     */
    static final int STATE_CONFIRMED = 2;

    /**
     * Terminated Dialog state
     */
    static final int STATE_TERMINATED = 3;
	private boolean completed = false;
	
	private int operation = -1;
	
	private final int PLAY = 1;
    private final int PLAY_COLLECT = 2;
    private final int PLAY_RECORD  = 3;
	
	/* Array of request headers */
	private ArrayList associatedRequestList  = new ArrayList();

	/*  Array of cseq numbers, will be used to associate a ACK,PRACK and CANCEL */
	private ArrayList associatedResponseList = new ArrayList();
	
	public BasicSBBOperation() {
    }
	
	public void setOperationContext(SBBOperationContext ctx)
	{
		this.ctx = ctx;
	}
	
	public SBBOperationContext getOperationContext()
	{
		return this.ctx;
	} 


	
	/**
	*This method will be used to query the state of operation.
	*/
	public boolean isCompleted() 
	{
		return completed;
	}


	/**
	*  This method will set the state of operation.
	*/
	public void setCompleted(boolean b) 
	{
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> setComplete called with "+b);
		
		synchronized (this) {
			completed = b;
		}

			if (this.completed && this.ctx != null) {
				this.ctx.removeSBBOperation(this);
			}
		
	}
	

	/**
	*	This method will fire an event to Application.
	*  Normally this methods will be called by operation handlers to convey final 
	*  result of operation to application.	
	*/	
	protected int fireEvent(String eventId,String reasonCode, SipServletMessage msg,Throwable t) 
	{
		SBBEvent event = new SBBEvent();
		event.setEventId(eventId);
		event.setReasonCode(reasonCode);
		event.setError(t);
		event.setMessage(msg);
		return ctx.fireEvent(event);
	}


	/**
	*  This method will fire an event to Application.
	*  Normally this methods will be called by operation handlers to convey final
	*  result of operation to application.
	*/
	protected int fireEvent(String eventId,String reasonCode, SipServletMessage msg) 
	{
		SBBEvent event = new SBBEvent();
		event.setEventId(eventId);
		event.setReasonCode(reasonCode);
		event.setMessage(msg);
		return ctx.fireEvent(event);
	}

	/*
	*  This method will set the state of operation and fire an event to Application.
	*  Normally this methods will be called by operation handlers to convey final
	*  result of operation to application. 
	*/ 

	public void setCompleted(boolean b, String eventId, String reasonCode) 
	{
		this.setCompleted(b);
		if (b ) 
		{
			SBBEvent event = new SBBEvent(eventId,reasonCode);
			ctx.fireEvent(event);
		}        
	}



	/*
	*  This method will set the state of operation and fire an event to Application.
	*  Normally this methods will be called by operation handlers to convey final
	*  result of operation to application. 
	*/ 
	public void setCompleted(boolean b, String eventId, Throwable t) 
	{
		this.setCompleted(b);
		if (b) 
		{
			SBBEvent event = new SBBEvent(eventId);
			event.setError(t);	 			
			ctx.fireEvent(event);
		}        
	}



	
	/**
	 * Sub classes will override this method.
	 */
	public  void start() throws ProcessMessageException{}
	
	/**
	 * Sub classes will override this method.
	 */
	public void ackTimedout(SipSession session) 
	{
	}

	/**
	 * <pre>
	 * Checks the CSeq header from the specified message object.
	 * If the message is a response, 
	 * then checks whether the CSeq header matches with atleast one of the CSeq headers available.
	 * If the message is a request,
	 * Checks whether the numeric part of the CSeq header matches with the any of the CSeq headers in the list.
	 * </pre> 
	 */
	public boolean isMatching(SipServletMessage message) 
	{
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> entered isMatching");
		boolean flag= false;
		
//		if (message instanceof SipServletResponse &&associatedResponseList.size()==1)
//		{
//			if(logger.isDebugEnabled()) 
//				logger.debug("<SBB> only one response handler is present, so returning true");
//			return true;
//		}	

		if (message instanceof SipServletRequest) 
		{

			/*
			*	Now this can be a ACK, PRACK or a CANCEL.
			*  if its not ACK, PRACK or CANCEL, it can be either
			* 1. INVITE which means its actually a ACK TIMEOUT or
			* 2. PRACK TIMEOUT
			*/	
			if (message.getMethod().equalsIgnoreCase(Constants.METHOD_ACK) ||
					message.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK) ||
					message.getMethod().equalsIgnoreCase(Constants.METHOD_CANCEL) ||
					message.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) 
			{  
				
				Object assoResp[] = associatedResponseList.toArray();
				
				int cseq = -1;
				// ACK or ACK time out
				if (message.getMethod().equalsIgnoreCase(Constants.METHOD_ACK) || 
					message.getMethod().equalsIgnoreCase(Constants.METHOD_CANCEL) ||
					message.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {
					cseq = getCseqNumber(message);
				}
				// PRACK
				else if (message.getMethod().equalsIgnoreCase(Constants.METHOD_PRACK)) {
					cseq = getCseqNumberFromRAck(message);
				}	
				if(logger.isDebugEnabled()) 
				logger.debug("<SBB> Message CSeq = "+cseq);

				for (int i=0;i<assoResp.length;i++) {
					int value =((Integer)assoResp[i]).intValue();
                	if (value == cseq) {
                    	flag = true;
                    	break;
                	}
            	}
			}
		}
		else {

			Object assoRequest[] = associatedRequestList.toArray();

			//	Extract the request from response & compare reference 
			SipServletRequest origRequest = ((SipServletResponse)message).getRequest();
			for (int i=0;i<assoRequest.length;i++) {
				if (assoRequest[i] == origRequest) {
					flag = true;
					break;
				}
			}
		}
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> exited isMatching with return value :: "+flag);
		return flag;
	}

	/**
	 * Sub classes need to override this method.
	 */
	public void prackTimedout(SipSession session) {
	}

	/**
	 * <pre>
	 * 1. Gets the CSeq header from the request and add it to the CSeq header list.
	 * 2. Gets the Message Modifier from the SBB and invokes the modifyMessage on it.
	 * 3. Send the request OUT using the send() method.
	 * </pre>
	 */
	public void sendRequest(SipServletRequest request) throws IOException
	{
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("<SBB> DISABLE_OUTBOUND_PROXY..");
//		}
	//	request.setAttribute("DISABLE_OUTBOUND_PROXY", AseStrings.BLANK_STRING);
		
		if (request.getAttribute("DISABLE_OUTBOUND_PROXY") == null) {
			disableObgwproxy(request.getApplicationSession(), null, request);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("<SBB> Sending the following request OUT:\n" + request);
		}	
		if(request!=null && request.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE) ==null) {
                	request.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_UNDEFINED));
                }

		SBBSubscriptionState sbbSubscriptionState = null;
		if(logger.isDebugEnabled()) 
		logger.debug("entered sendRequest");

		associateRequest(request);
		if(request.getSession().getAttribute(com.baypackets.ase.util.Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB)!=null) {
			sbbSubscriptionState = (SBBSubscriptionState)request.getSession().getAttribute(com.baypackets.ase.util.Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB);
		} else {
			sbbSubscriptionState = new SBBSubscriptionState();
			request.getSession().setAttribute(com.baypackets.ase.util.Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB,sbbSubscriptionState);
		}


		//****	Making SBB Platform independent
		if(request.getMethod().equals("BYE")) {
			sbbSubscriptionState.byeRecieved();
		}

		request.getSession().setAttribute(com.baypackets.ase.util.Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB,sbbSubscriptionState);
		if(sbbSubscriptionState.isListEmpty() && sbbSubscriptionState.isByeRecieved()){
			request.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_TERMINATED));
		}


	//try {
	// Invoke the message filter	
	
		MessageFilter  msgFilter = ((SBB)ctx).getMessageFilter();
		if (msgFilter!= null) {
			msgFilter.doFilter(request);
		}
		request.send();
		//UAT-745
		if (request.getMethod().equals("INVITE") && !request.isInitial()){
			if(logger.isDebugEnabled()) 
				logger.debug("Pending request attribute is set to true");
			request.getSession().setAttribute(Constants.PENDING_REQUEST, true);
		}


	//	} catch(IOException exp) {
	//		logger.error("Not able to send request",exp);
	//	}
		if(logger.isDebugEnabled()) 
		logger.debug("exited sendRequest");
	}

	/**
	 * <pre>
	 * 1. Gets the CSeq header from the request and add it to the CSeq header list.
	 * 2. Gets the Message Modifier from the SBB and invokes the modifyMessage on it.
	 * 3. Sends the response OUT using the send() or sendReliable() methods based on the flag.
	 * </pre>
	 */
	public void sendResponse(SipServletResponse response, boolean reliable) throws Rel100Exception,IOException  
	{
		if(logger.isDebugEnabled()){
			logger.debug("<SBB> Sending the following response OUT:\n" + response);
		}	

		if(response!=null)
                {
                        if((response.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)) ==null)
                        {
                        	response.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_UNDEFINED));
                        }
                }

		if (response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) 
		{
			// Extract the CSeq Header
			int cSeq = getCseqNumber(response);
			// add CSeq in response list
			associatedResponseList.add(new Integer(cSeq));
		}

		if(response.getStatus() > 199){
			if(response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE))
			{
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> [.....] Sending "+ response.getStatus()+" for INVITE. Hence removing INVITE from session.");
				}
				response.getSession().removeAttribute(Constants.ATTRIBUTE_INIT_REQUEST);
			}else{
				if (logger.isDebugEnabled()) {
					logger.debug("<SBB> [.....] Sending "+response.getStatus()+" for "+response.getMethod()+". Hence not removing INVITE from session.");
				}
			}
		}

		//***** Added for Making SBB Platform Independent

		if((response.getStatus()==408)||(response.getStatus()==481))
		{
			response.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_TERMINATED));
		}
		else
		{
			if((response.getStatus()>100)&&(response.getStatus()<200)&&(response.getRequest().isInitial()))
			{
				response.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(AseSipSessionState.STATE_EARLY));
			}
			if((response.getStatus()>=200)&&(response.getStatus()<300))
 {
				if (response.getRequest().isInitial()) {
					if ((response.getRequest().equals("MESSAGE"))
							|| (response.getRequest().equals("PUBLISH"))
							|| (response.getRequest().equals("OPTIONS"))) {
						;
					} else {
						int sessionState = ((Integer) (response.getSession()
								.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)))
								.intValue();
						if ((sessionState == (AseSipSessionState.STATE_EARLY))
								|| (sessionState == (AseSipSessionState.STATE_UNDEFINED))) {
							response.getSession()
									.setAttribute(
											Constants.ATTRIBUTE_DIALOG_STATE,
											new Integer(
													AseSipSessionState.STATE_CONFIRMED));
						}
					}
				}
			}
			if ((response.getStatus() >= 300) && (response.getStatus() <= 600)) {

				int st = ((Integer) response.getSession().getAttribute(
						Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

				if (st == AseSipSessionState.STATE_EARLY) {
					response.getSession().setAttribute(
							Constants.ATTRIBUTE_DIALOG_STATE,
							new Integer(AseSipSessionState.STATE_TERMINATED));
				}
			}
		}
		MessageFilter  msgFilter = ((SBB)ctx).getMessageFilter();
		if (msgFilter != null) {
			msgFilter.doFilter(response);
		}

		try {
			if (reliable) {
				if(logger.isDebugEnabled()) {
					logger.debug("<SBB> setting attribute for rel already sent..");
				}
				response.getSession().setAttribute(Constants.IS_REL_RESP_SENT_ALREADY_SENT, new Boolean(reliable));
				response.sendReliably();
			} else {
				response.send();
			}
		} catch (IOException exp) {
			logger.error("Not able to send response", exp);
		}
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> exited sendResponse");
	}
	
	/**
	 * Sub classes need to over-ride this method.
	 */
	public void handleRequest(SipServletRequest request) {
	}
	
	/**
	 * Sub classes need to override this method.
	 */
	public void handleResponse(SipServletResponse response) {
		if(response.getStatus() > 199){
			/*
			 * We need to remove the initial INVITE request from the session,
			 * ONLY when a 200 OK for the INVITE has been received and not
			 * otherwise. As it is quite possible to receive a 200 OK for a
			 * PRACK for reliably delivered 183 before getting 200 OK for
			 * INVITE.
			 */
			if (response.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {
				if(logger.isDebugEnabled()) 
				logger.debug("[.....] Recieved "+response.getStatus()+" for INVITE, hence removing INIT_REQUEST from app session.");
				response.getSession().removeAttribute(Constants.ATTRIBUTE_INIT_REQUEST);
			} else {
				if(logger.isDebugEnabled()) 
				logger.debug("[.....] Recieved "+response.getStatus()+" for non-INVITE request, hence not removing INIT_REQUEST from app session.");
			}
		}
	}

	public void associateRequest(SipServletRequest  req) {
		associatedRequestList.add(req);	
	}


	/**
     * Sub classes need to override this method.
     */
	public void timerExpired(ServletTimer timer) {
	}	


	private int getCseqNumber(SipServletMessage message) {
		int seq = -1;
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> Entered getCseqNumber");
		String cSeqHdr = message.getHeader(Constants.HDR_CSEQ);
		if(logger.isDebugEnabled()) 
        logger.debug("<SBB> cSeq header ="+cSeqHdr);
        StringTokenizer tokenizer = new StringTokenizer(cSeqHdr," ");
        String cSeq = null;
        if (tokenizer.hasMoreTokens()) {
              cSeq = tokenizer.nextToken();
        }	
		try {
			seq = Integer.parseInt(cSeq);
		}
		catch(NumberFormatException exp) {
			// should not happen
			logger.error("Problem in parsing CSeq number",exp);
		}
		if(logger.isDebugEnabled()) 
		logger.debug("<SBB> Exited getCseqNumber with cSeq <"+seq+">");
		return seq;
	}


	private int getCseqNumberFromRAck(SipServletMessage message) {
        int seq = -1;
		if(logger.isDebugEnabled()) 
        logger.debug("<SBB> Entered getCseqNumberFromRAck");
        String rackHdr = message.getHeader(Constants.HDR_RACK);

        StringTokenizer tokenizer = new StringTokenizer(rackHdr," ");
        String cSeq = null;
        if (tokenizer.hasMoreTokens()) {
              cSeq = tokenizer.nextToken();
              cSeq = tokenizer.nextToken();
        }
        try {
            seq = Integer.parseInt(cSeq);
        }
        catch(NumberFormatException exp) {
            // should not happen
            logger.error("Problem in parsing CSeq number",exp);
        }
		if(logger.isDebugEnabled()) 
        logger.debug("<SBB> Exited getCseqNumberFromRAck  with cSeq <"+seq+">");
        return seq;
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.completed = in.readBoolean();
		this.associatedRequestList = (ArrayList)in.readObject();
		this.associatedResponseList = (ArrayList)in.readObject();
		
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(this.completed);
		out.writeObject(this.associatedRequestList);
		out.writeObject(this.associatedResponseList);
	}

	public void cancel() 
	{
		throw new IllegalStateException("CANCEL Operation Not supported");
	}
	
	/* For the Dialogic Media Server INFO is not received for the PLAY operation after sending BYE so
	 * only stop INFO request is sent.
	 * For PLAY COLLECT and PLAY RECORD INFO is received both for playcollect or playrecord 
	 * request and also for the stop request afterwards so BYE operation is handled after receiving
	 * final INFO request.
	 * The stop operation is not handled for the PLAY_VXML operation.
	 * stopPlay() return true if any media play and its perform stop operation otherwise return false
	 */
	
	protected boolean stopPlay(){
		SBB sbb = this.getOperationContext().getSBB();
		if(sbb instanceof MsSessionControllerImpl || sbb instanceof GroupedMsSessionControllerImpl){
			try{
				Iterator itr = ((SBBImpl)sbb).getOperations().iterator();
				while(itr.hasNext()) {
					SBBOperation oper = (SBBOperation)itr.next();
					if(oper instanceof MediaServerInfoHandler){
						this.operation = ((MediaServerInfoHandler)oper).getOperation();
						if((this.operation == PLAY) ||(this.operation == PLAY_COLLECT) || (this.operation == PLAY_RECORD)){
							
							if(logger.isDebugEnabled()){
								logger.debug("<SBB> stopMediaOperations()called");
							}
							((MsSessionControllerImpl)sbb).stopMediaOperations();
							return true;
						}
					}
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			if(logger.isDebugEnabled()){
				logger.debug("<SBB> currently no media is playing .");
			}
		}
		return false;
	}
	
	/**
	 * This method is used to disable obgw proxy
	 * @param sas
	 * @param session
	 * @param request
	 */
	protected void disableObgwproxy(SipApplicationSession sas,
			SipSession session, SipServletRequest request) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> disableObgwproxy called DISABLE_OUTBOUND_PROXY..");
		}
		Boolean isPrivate = (Boolean) sas
				.getAttribute(com.baypackets.ase.util.Constants.RECEIVED_PRIVATE_IF);
		
		if( isPrivate==null){
			isPrivate=false;
		}
		
		if (!isPrivate.booleanValue()) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("<SBB> disableObgwproxy Proxy is disabled");
			}
			if (session != null) {
				session.setAttribute(
						com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY,
						AseStrings.BLANK_STRING);
			} else if (request != null) {
				request.setAttribute(
						com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY,
						AseStrings.BLANK_STRING);
			}
		}else{
			
			if (logger.isDebugEnabled()) {
				logger.debug("<SBB> disableObgwproxy Proxy is not diabled as interface is private");
			}
		}
	}
}
