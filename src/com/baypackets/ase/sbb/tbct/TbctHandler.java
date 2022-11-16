/*
 * Filename: TbctHandler.java
 * Created on Aug 17, 2005
 *
 */

package com.baypackets.ase.sbb.tbct;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.util.AseStrings;


/**
 * Implementation of the TBCT handler.
 * This class is responsible for handling TBCT operation. 
 * TBCT operation handles the signalling level details 
 * for connecting two existing call-legs to each other into a TBCT fashion.
 *
 * It is application's responsibility to ensure that both parties are terminated
 * on same gateway, a pre-condition to TBCT, and that gateway supports TBCT.
 */

public class TbctHandler extends BasicSBBOperation implements java.io.Serializable {
	
	private static final long serialVersionUID = -391297033147L;
	private static final String STR_REFER					= "REFER";
	private static final String STR_SUBSCRIBE				= "SUBSCRIBE";
	private static final String STR_Event					= "Event";
	private static final String STR_Refer_To				= "Refer-To";
	private static final String STR_Subscription_State			= "Subscription-State";
	private static final String STR_Expires					= "Expires";
	private static final String STR_REFERRED_BY				= "Referred-By".intern();
	private static final String STR_SIPFRAG					= "message/sipfrag".intern();
	private static final String STR_Replaces        			= "Replaces";
        private static final String STR_tag                     		= "tag";


	private static final int TIMER_NOTIFY_TIMEOUT			= 1;
	private static final int TIMER_SUBSCRIPTION_EXPIRED		= 2;

	/** Log4j Logger element */
    private static Logger m_logger = Logger.getLogger(TbctHandler.class);

	/** associated <code>ServletTimer</code> object */
	private transient ServletTimer m_timer = null;

	private int m_timerType = 0;

	private boolean expectsBye = false;

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public TbctHandler() {
		super();
	}

	/**
     * This method will be invoked to start TBCT operation.
     */
	public void start()
		throws ProcessMessageException {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("start(): enter");

		SBB sbb = this.getOperationContext().getSBB();
		// Create and send a REFER request on sessionB
		SipServletRequest referReq = this.createRefer();
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("start: Sending REFER request to B-party");
		try {
			this.sendRequest(referReq);
		} catch(IOException ioe) {
			throw new ProcessMessageException(ioe.getMessage());
		}

		// Create a timer of 10 secs to wait for first NOTIFY
		int timeout = 10; // 10 seconds
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("start: Creating NOTIFY_TIMEOUT timer with period (secs): " + timeout);
		}

		m_timerType = TIMER_NOTIFY_TIMEOUT;
		TimerService timerService = (TimerService)sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
		m_timer = timerService.createTimer(	sbb.getB().getApplicationSession(),
												timeout*1000,
												true,
												this);
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("start(): exit");
    }

	/**
	 * This method will invoked by SBB servlet to handle incoming requests.
	 * @param request incoming request object
	 */
	public void handleRequest(SipServletRequest request) {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("handleRequest: entered");

		// First of all respond to request (NOTIFY or BYE) with 200
		SBB sbb = (SBB)getOperationContext();
		SipServletResponse resp = request.createResponse(200);
		try {
			this.sendResponse(resp, false);
		} catch(Rel100Exception r100e) {
			m_logger.error("handleRequest: sending response", r100e);
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("handleRequest: exit");
			return;
		} catch(IOException ioe) {
			m_logger.error("handleRequest: sending response", ioe);
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("handleRequest: exit");
			return;
		}

		//Received BYE from B... Send a BYE to the originating side and raise TBCT_SUCCESS...
		if(request.getMethod().equals(Constants.METHOD_BYE) && request.getSession() == sbb.getB()) {
			
			SipServletRequest byeToA = sbb.getA().createRequest(Constants.METHOD_BYE);
			try{
				this.sendRequest(byeToA);
			} catch(IOException ioe) {
				m_logger.error("timerExpired: sending SUBSCRIBE (Expires: 0)", ioe);
			}

			// Fire event TBCT successful
			this.fireEvent(	SBBEvent.EVENT_TBCT_SUCCESSFUL, SBBEvent.REASON_CODE_SUCCESS, null);
			
			this.setCompleted(true);
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("handleRequest: exit");
			return;
		} // BYE requests

		if(request.getMethod().equals(Constants.METHOD_NOTIFY)) {
			if(m_timer != null) {
				// Cancel NOTIFY or subscription timer
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("handleRequest: Cancelling timer " + m_timerType);
				}
				m_timer.cancel();
			}
			
			// Parse the Content and get the status received from the resource
                        int refStatus = -1;
                        String contentType = request.getContentType();
                        if(request.getContentLength() > 0 && contentType != null &&
                                        contentType.equals(STR_SIPFRAG)){
                                String content = null;
                                try {
                                        content = new String(request.getRawContent());
                                        content = (content == null) ? AseStrings.BLANK_STRING : content;
                                } catch(IOException ioe) {
                                        m_logger.error("handleRequest: getting content", ioe);
                                }

				refStatus = SBBResponseUtil.parseNumber(content, AseStrings.SPACE);
                        }
                        if(m_logger.isDebugEnabled()) {
                                m_logger.debug("refStatus is : " + refStatus);
                        }
			
			// Get Subscription-State header value
			String subState = request.getHeader(STR_Subscription_State);
			subState = (subState == null) ? AseStrings.BLANK_STRING : subState;
			if(subState.startsWith("terminated") || refStatus >= 300) {
				// This handler terminates here
				this.setCompleted(true);

				// Fire event TBCT FAILED
				this.fireEvent(	SBBEvent.EVENT_TBCT_FAILED, SBBEvent.REASON_CODE_ERROR, request);
				return;
			}
		
			//Now we can expect a BYE if a successful TBCT happens.
			//So set this flag ON
			this.expectsBye = true;

			//Also create a timer for the subscription expiry.
			int timeout = SBBResponseUtil.parseNumber(subState, "expires=");
			timeout = (timeout <= 0 ) ? 60 : timeout; // 60 seconds

			// Create timer for subscription expiry
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("handleRequest: Creating SUBSCRIPTION_EXPIRED timer with period in secs: " + timeout);
			}

			TimerService timerService = (TimerService)
					sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
			m_timerType = TIMER_SUBSCRIPTION_EXPIRED;
			m_timer = timerService.createTimer(sbb.getB().getApplicationSession(), 
								timeout*1000, true,this);
		} // NOTIFY requests
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("handleRequest: exit");
    	}

	/**
	 * This method will invoked by SBB servlet to handle incoming responses.
	 * @param response incoming response object
     */
	public void handleResponse(SipServletResponse response) {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("handleResponse: entered");

		int respStatus = response.getStatus();

		if(response.getMethod().equals(Constants.METHOD_BYE)){
			this.setCompleted(true);
			return;
		}

		// If 1xx or 2xx response to REFER/SUBSCRIBE, return
		if(respStatus < 300) {
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("handleResponse: exit");
			return;
		}

		// This SBB operation is finished if it is:
		// a failure response to REFER
		// 408/481 response to any request
		if(response.getMethod().equals(STR_REFER) || (respStatus == 408) || (respStatus == 481)) {
			if(m_timer != null) {
				// Cancel NOTIFY or subscription timer
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("handleResponse: Cancelling timer " + m_timerType);
				}
				m_timer.cancel();
			}

			// This handler terminates here, call super class.setCompleted()
			super.setCompleted(true);

			this.fireEvent(	SBBEvent.EVENT_TBCT_FAILED,
							SBBEvent.REASON_CODE_ERROR_RESPONSE,
							response);
		}
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("handleResponse: exit");
    }	

	/**
	 * This is a utility method, which creates a REFER request on <code>sessionB</code>
	 * and sets Refer-To and Event headers from <code>sessionA</code> details.
	 * @return created REFER request
	 */
	private SipServletRequest createRefer() {
		SipServletRequest referReq = null;
		try{
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("createRefer(): enter");

		// Get A and B parties session objects
		SBB sbb = (SBB)getOperationContext();
		SipSession sessionA = sbb.getA();
		SipSession sessionB = sbb.getB();

		//
		// Create a REFER request for sessionB with AoR of A-party in 'Refer-To' header
		//

		// Create REFER request
		referReq = sessionA.createRequest(STR_REFER);

		// Set Event header
		referReq.setHeader(STR_Event, "refer");

		//// Create the 'Refer-To' header value
		String referToURI = sessionB.getLocalParty().getURI().toString();
		referToURI = referToURI != null && referToURI.startsWith(AseStrings.ANGLE_BRACKET_OPEN) && referToURI.endsWith(AseStrings.ANGLE_BRACKET_CLOSE) ?
				referToURI.substring(1, referToURI.length()) : referToURI;
		int pos = referToURI.indexOf(AseStrings.SEMI_COLON);
		//referToURI = pos > 0 ? referToURI.substring(0, pos) : referToURI;

		//Create the Replaces header....
	        String replacesHdrValue = sessionB.getCallId() +
					 ";to-tag=" + sessionB.getRemoteParty().getParameter(STR_tag) +
                                         ";from-tag=" + sessionB.getLocalParty().getParameter(STR_tag);
		replacesHdrValue = java.net.URLEncoder.encode(replacesHdrValue, AseStrings.XML_ENCODING_UTF8);
		referToURI = AseStrings.ANGLE_BRACKET_OPEN + referToURI + AseStrings.QUESTION_MARK + STR_Replaces + AseStrings.EQUALS + replacesHdrValue + AseStrings.ANGLE_BRACKET_CLOSE;
		referReq.setHeader(STR_Refer_To, referToURI);

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("createRefer: Refer-To header value is: " + referToURI);
		}


		//Create the Referred-By Header....
		String referredBy = referReq.getHeader(STR_REFERRED_BY);
		if(referredBy == null){
			referReq.setHeader(STR_REFERRED_BY, sessionB.getLocalParty().getURI().toString());
		}
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("createRefer(): exit");
		}catch(Exception e){
			m_logger.error(e.getMessage(), e);
		}
		return referReq;
    }

	/**
	 * This is a utility method, which creates a SUBSCRIBE request on <code>sessionB</code>
	 * @return created SUBSCRIBE request
	 */
	private SipServletRequest createSubscribe() {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("createSubscribe(): enter");

		// Get A and B parties session objects
		SBB sbb = (SBB)getOperationContext();
		SipSession sessionA = sbb.getB();

		// Create SUBSCRIBE request
		SipServletRequest subsReq = sessionA.createRequest(STR_SUBSCRIBE);

		// Set Event header
		subsReq.setHeader(STR_Event, "refer");
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("createSubscribe(): exit");
		return subsReq;
    }

	public void timerExpired(ServletTimer timer) {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("timerExpired: enter");

		m_timer = null;

		if(this.isCompleted()) {
			if(m_logger.isDebugEnabled()) {
			m_logger.debug("timerExpired: operation already completed");
			m_logger.debug("timerExpired: exit");
			}
			return;
		}

		if(m_timerType == TIMER_NOTIFY_TIMEOUT) {
			m_timerType = 0;

			// Send a REFER to terminate subscription
			SipServletRequest subscribeReq = this.createSubscribe();
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("timerExpired: Sending SUBSCRIBE request to terminate subscription");

			// Set Expires header with 0
			subscribeReq.setHeader(STR_Expires, "0");
			try {
				this.sendRequest(subscribeReq);
			} catch(IOException ioe) {
				m_logger.error("timerExpired: sending SUBSCRIBE (Expires: 0)", ioe);
			}
		} else if(m_timerType == TIMER_SUBSCRIPTION_EXPIRED) {
			m_timerType = 0;

			// This handler terminates here
			this.setCompleted(true);

			this.fireEvent(	SBBEvent.EVENT_TBCT_FAILED,
							SBBEvent.REASON_CODE_SUBSCRIPTION_EXPIRED,
							null);
		} else {
			m_logger.error("timerExpired: unknown timeout condition");
		}
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("timerExpired: exit");
	}

	/**
	 * Checks if method is NOTIFY or REFER, it is matched with this handler.
	 * @return true if session associated with message matches with A or B party session
	 */
	public boolean isMatching(SipServletMessage message) {
		SBB sbb = this.getOperationContext().getSBB();
		String method = message.getMethod();
		if(message instanceof SipServletRequest && 
			(message.getSession() == sbb.getA() && method.equals(Constants.METHOD_NOTIFY) ||
		 	 this.expectsBye && message.getSession() == sbb.getB() && method.equals(Constants.METHOD_BYE))) {
			return true;
		}

		return super.isMatching(message);
	}

	/**
	 * This method check if dialogs corresponding to both the parties, if present are
	 * TERMINATED. If so, it removes this handler from SBB.
	 * <p>
	 * If this handler needs to be removed irrespective of dialogs' states,
	 * same method of super class should be called.
	 */
	public void setCompleted(boolean b) {
		SBB sbb = this.getOperationContext().getSBB();
		SipSession sessionA = sbb.getA();
		SipSession sessionB = sbb.getB();

		int stateA = Constants.STATE_TERMINATED;
		int stateB = Constants.STATE_TERMINATED;

		if(sessionA != null) {
			stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		}

		if(sessionB != null) {
			stateB = ((Integer)sessionB.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		}

		if(stateA == Constants.STATE_TERMINATED
		&& stateB == Constants.STATE_TERMINATED) {
			// Both dialog's are terminated, set complete operation
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("handleRequest: Calling setCompleted()");
			super.setCompleted(b);
		}
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.m_timerType = in.readInt();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.m_timerType);
	}

}

