/*
 * CallTransferHandler.java
 *
 * Created on Oct 26, 2005
 */

package com.baypackets.ase.sbb.calltransfer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.io.Serializable;
import java.util.StringTokenizer;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.Address;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;


public class CallTransferHandler extends BasicSBBOperation implements Serializable {

	private static final long serialVersionUID = 201197038547703314L;
	/** Logger element */
	private static Logger logger = Logger.getLogger(CallTransferHandler.class.getName());

	private static final String STR_REFER					= "REFER";
	private static final String STR_SUBSCRIBE				= "SUBSCRIBE";
	private static final String STR_Event					= "Event";
	private static final String STR_Refer_To				= "Refer-To";
	private static final String STR_Subscription_State		= "Subscription-State";
	private static final String STR_Expires					= "Expires";
 	private static final String STR_REFERRED_BY                             = "Referred-By".intern();
	private static final String STR_SIPFRAG					= "message/sipfrag".intern();

	private static final int TIMER_FIRST_NOTIFY_TIMEOUT		= 1;
	private static final int TIMER_SUBSCRIPTION_TIMEOUT		= 2;

	/** associated <code>ServletTimer</code> object */
	private transient ServletTimer m_timer = null;

	private int m_timerType = 0;
	
	private Address addressB = null;
	
	/**
	* Public Default Constructor used for Externalizing this Object
	*/
	public CallTransferHandler() 
	{
		super();
	}

	public CallTransferHandler(Address addressB)
	{
		this.addressB = addressB;
	}
		

	/**
	* This method will be invoked to start CallTransferHandler operation.
	*/

	public void start() throws ProcessMessageException
	{
		if (logger.isDebugEnabled())
			logger.debug("start(): enter");

		SBB sbb = this.getOperationContext().getSBB();

		// Create and send a REFER request to partyA after adding Event and Refer-To headers
		SipServletRequest referReq = this._createRefer();
		if (logger.isDebugEnabled())
			logger.debug("start: Sending REFER request to A-party");
		try 
		{
			this.sendRequest(referReq);
		} catch(IOException ioe) 
		{
			throw new ProcessMessageException(ioe.getMessage());
		}

		// Create a timer of 32 secs to wait for first NOTIFY
		_createNotifyTimer();
		if (logger.isDebugEnabled())
			logger.debug("start(): exit");
    }

	/**
	 * This is a utility method, which creates a REFER request on <code>sessionA</code>
	 * and sets Refer-To and Event headers from <code>sessionA</code> details.
	 * @return created REFER request
	 */
	private SipServletRequest _createRefer() 
	{
		if (logger.isDebugEnabled())
			logger.debug("_createRefer(): enter");

		// Get A and B parties session objects
		SBB sbb = (SBB)getOperationContext();
		SipSession sessionA = sbb.getA();

		//
		// Create a REFER request for sessionA with AoR of B-party in 'Refer-To' header
		//

		// Create REFER request
		
		SipServletRequest referReq = sessionA.createRequest(STR_REFER);
		// Set Event header
		referReq.setHeader(STR_Event, "refer");

		// Set 'Refer-To' header
		String referToHdrValue = addressB.getURI().toString();
		if(logger.isDebugEnabled()) {
			logger.debug("_createRefer: Refer-To header value is: " + referToHdrValue);
		}
		referReq.setHeader(STR_Refer_To, referToHdrValue);

		//Create the Referred-By Header....
                String referredBy = referReq.getHeader(STR_REFERRED_BY);
                if(referredBy == null){
                        referredBy = sessionA.getLocalParty().getURI().toString();
                        referReq.setHeader(STR_REFERRED_BY, referredBy);
                }
		if (logger.isDebugEnabled())
			logger.debug("_createRefer(): exit");
		return referReq;
	}

	/**
	 * This is a utility method, which creates a SUBSCRIBE request on <code>sessionA</code>
	 * and sets Expires and Event headers from <code>sessionA</code> details.
	 *
	 * @param subDuration Duration of subscription in seconds
	 * @return created SUBSCRIBE request
	 */
	private SipServletRequest _createSubscribe(int subDuration) 
	{
		if (logger.isDebugEnabled())
			logger.debug("_createSubscribe(): enter");

		// Get A and B parties session objects
		SBB sbb = (SBB)getOperationContext();
		SipSession sessionA = sbb.getA();

		// Create SUBSCRIBE request
		
		SipServletRequest subscribeReq = sessionA.createRequest(STR_SUBSCRIBE);
		// Set Event header
		subscribeReq.setHeader(STR_Event, "refer");
		// Set Expires to 60 secs
		subscribeReq.setHeader(STR_Expires, Integer.toString(subDuration));
		if (logger.isDebugEnabled())
			logger.debug("_createSubscribe(): exit");
		return subscribeReq;
	}

	/**
	* This method will invoked by SBB servlet to handle incoming responses.
	* @param response incoming response object
	*/
	public void handleResponse(SipServletResponse response) 
	{
		if (logger.isDebugEnabled())
			logger.debug("handleResponse: entered");

		SipSession sessionA = null;
		int stateA = -1;

		String respMethod = response.getMethod();

		SBB sbb = (SBB)getOperationContext();

		int respStatus = response.getStatus();

		if(respMethod.equals(STR_REFER)) {

			//
			// REFER Response
			//

			if(respStatus>=300) {
				// Non-success final response to REFER, terminate dialog
				sessionA = sbb.getA();
				stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

				if(stateA != Constants.STATE_TERMINATED) {
					SipServletRequest non2xxReq = sessionA.createRequest("BYE");
					if (logger.isDebugEnabled())
						logger.debug("handleResponse: Sending BYE request to A-party");
					try {
						this.sendRequest(non2xxReq);
					} catch(IOException ioe) {
						logger.error("sending request", ioe); 
					}
				}

				if(m_timer != null) {
					// Cancel NOTIFY or subscription timer
					if(logger.isDebugEnabled()) {
						logger.debug("handleResponse: Cancelling timer " + m_timerType);
					}
					m_timer.cancel();
				}

				// Notify application
				this.fireEvent( SBBEvent.EVENT_CALL_TRANSFER_FAILED,
								SBBEvent.REASON_CODE_ERROR_RESPONSE,
								response);
			}
		} else if(respMethod.equals(STR_SUBSCRIBE)) {

			//
			// SUBSCRIBE Response
			//

			sessionA = sbb.getA();
			stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

			if(respStatus == 423) {
				// Refresh interval is too small
			} else if(respStatus >= 300) {
				// Non-success final response to SUBSCRIBE
				if(stateA == Constants.STATE_TERMINATED) {
					sessionA = sbb.getA();
					stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();

					if(m_timer != null) {
						// Cancel NOTIFY or subscription timer
						if(logger.isDebugEnabled()) {
							logger.debug("handleResponse: Cancelling timer " + m_timerType);
						}
						m_timer.cancel();
					}

					// Notify application
					this.fireEvent( SBBEvent.EVENT_CALL_TRANSFER_FAILED,
									SBBEvent.REASON_CODE_ERROR_RESPONSE,
									response);
				}
			} else if(respStatus>=200) {
				// 2xx response to SUBSCRIBE, create a new subscription timer from Expires
				// For now we are refreshing subscription
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("handleResponse: exit");
		return;
	}


	/**
	 * This method will invoked by SBB servlet to handle incoming requests.
	 * @param request incoming request object
	 */
	public void handleRequest(SipServletRequest request) {
		if (logger.isDebugEnabled())
			logger.debug("handleRequest: entered");
		SBB sbb = (SBB)getOperationContext();
		SipServletResponse resp = null;
		
		if(request.getMethod().equals("NOTIFY")) {

			// cancel any existing NOTIFY timeout timer
               		if(m_timer != null) {
				// Cancel NOTIFY or subscription timer
				if(logger.isDebugEnabled()) {
					logger.debug("handleRequest: Cancelling timer " + m_timerType);
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
					content = (content == null) ? "" : content;
				} catch(IOException ioe) {
					logger.error("handleRequest: getting content", ioe);
				}
				refStatus = SBBResponseUtil.parseNumber(content, " ");
			}
			if(logger.isDebugEnabled()) {
				logger.debug("refStatus is : " + refStatus);
			}

			// Get Subscription-State header value
			String subState = request.getHeader(STR_Subscription_State);
			subState = (subState == null) ? "" : subState.trim();

			// If NOTIFY body did not contain valid status line, then send "400
			// Bad Request" response.
			if(subState.equals("") && (refStatus < 100 || 699 < refStatus)) {
				resp = request.createResponse(400);
				try {
					resp.setContent("Invalid NOTIFY body", "text/plain");
				} catch(UnsupportedEncodingException uee) {
					logger.error("handleRequest: Setting NOTIFY response body", uee);
				}

				try {
					this.sendResponse(resp, false);
				} catch(Rel100Exception r100e) {
					logger.error("handleRequest: sending response", r100e);
				} catch(IOException ioe) {
					logger.error("handleRequest: sending response", ioe);
				}
				if (logger.isDebugEnabled())
					logger.debug("handleRequest: exit");
				return;
			}

			// Respond to NOTIFY with 200
			resp = request.createResponse(200);
			try {
				this.sendResponse(resp, false);
			} catch(Rel100Exception r100e) {
				logger.error("handleRequest: sending response", r100e);
			} catch(IOException ioe) {
				logger.error("handleRequest: sending response", ioe);
			}


			if(200 > refStatus && refStatus >= 100) {
				// If status of referred is 1xx 
				// Update timer for subscription timeout
				// Create subscription timer as per expires param in Subscription-State header
				_createSubscriptionTimer(request);
				if (logger.isDebugEnabled())
					logger.debug("handleRequest: exit");
				return;
			}

			// If NOTIFY request has 2xx response status line its body, 
			//fire CALL_TRANSFER_SUCCESSFUL event
			//If NOTIFY request has NON- 2xx final response status line in its body, 
			//fire CALL_TRANSFER_FAILED event and cancels any existing NOTIFY timeout timer

			String eventId = 200 <= refStatus && refStatus < 300 ?
					SBBEvent.EVENT_CALL_TRANSFER_SUCCESSFUL : 
					SBBEvent.EVENT_CALL_TRANSFER_FAILED;
			String reason = 200 <= refStatus && refStatus < 300 ?
				SBBEvent.REASON_CODE_SUCCESS : SBBEvent.REASON_CODE_ERROR_RESPONSE;
			this.setCompleted(true);
			this.fireEvent( eventId, reason, resp);
		} else { // if not NOTIFY
			// First of all respond to request (NOTIFY or BYE) with 200
			resp = request.createResponse(200);

			try {
				this.sendResponse(resp, false);
			} catch(Rel100Exception r100e) {
				logger.error("handleRequest: sending response", r100e);
			} catch(IOException ioe) {
				logger.error("handleRequest: sending response", ioe);
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("handleRequest: exit");
	}

	public void timerExpired(ServletTimer timer) {	
		if (logger.isDebugEnabled())
			logger.debug("timerExpired: enter");

		m_timer = null;

		if(this.isCompleted()) {
			if (logger.isDebugEnabled()) {
				logger.debug("timerExpired: operation already completed");
				logger.debug("timerExpired: exit");
			}
			return;
		}

		if(m_timerType == TIMER_FIRST_NOTIFY_TIMEOUT || m_timerType == TIMER_SUBSCRIPTION_TIMEOUT) {
			m_timerType = 0;

			// Send a SUBSCRIBE to terminate subscription
			SipServletRequest subscribeReq = this._createSubscribe(0);
			if (logger.isDebugEnabled())
				logger.debug("timerExpired: Sending SUBSCRIBE request to terminate subscription");
			try {
				this.sendRequest(subscribeReq);
			} catch(IOException ioe) {
				logger.error("timerExpired: sending SUBSCRIBE (Expires: 0)", ioe);
			}
		} else {
			logger.error("timerExpired: unknown timeout condition");
		}
		if (logger.isDebugEnabled())
			logger.debug("timerExpired: exit");
	}


	/**
	 * Checks if method is NOTIFY or REFER, it is matched with this handler.
	 * @return true if method belongs to A-party
	 */
	public boolean isMatching(SipServletMessage message) {
		if (logger.isDebugEnabled())
			logger.debug("isMatching(SipServletMessage): enter");

		SBB sbb = this.getOperationContext().getSBB();
		if(message.getSession() == sbb.getA() && 
			message instanceof SipServletRequest &&
			message.getMethod().equals("NOTIFY")) {
			return true;
		}

		return super.isMatching(message);
	}

		
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException 
	{
		super.readExternal(in);
		this.m_timerType = in.readInt();
		this.addressB = (Address)in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException 
	{
		super.writeExternal(out);
		out.writeInt(this.m_timerType);
		out.writeObject(this.addressB);
	}

	private void _createNotifyTimer() {
		int timeout = 32; // 32 seconds
		SBB sbb = this.getOperationContext().getSBB();

		if(logger.isDebugEnabled()) {
			logger.debug("Creating NOTIFY_TIMEOUT timer with period (secs): " + timeout);
		}

		m_timerType = TIMER_FIRST_NOTIFY_TIMEOUT;
		TimerService timerService = (TimerService)
			sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
		m_timer = timerService.createTimer(sbb.getA().getApplicationSession(),timeout*1000, true,this);
	}

	private int _createSubscriptionTimer(SipServletRequest request) {
		if (logger.isDebugEnabled())
			logger.debug("Creating subscription timer from request's Subscription-State header");

		String subState = request.getHeader(STR_Subscription_State);
		subState = (subState == null) ? "" : subState;
		int timeout = SBBResponseUtil.parseNumber(subState, "expires=");
		timeout = (timeout > 0) ? timeout : 32; // set default to 32 seconds

		SBB sbb = this.getOperationContext().getSBB();

		if(logger.isDebugEnabled()) {
			logger.debug("Creating SUBSCRIPTION_TIMEOUT timer with period (secs): " + timeout);
		}

		m_timerType = TIMER_SUBSCRIPTION_TIMEOUT;
		TimerService timerService = (TimerService)
			sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
		m_timer = timerService.createTimer(sbb.getA().getApplicationSession(),timeout*1000, true, this);
		return timeout;
	}

	/**
	 * This is not being used at the moment but should be retained for future use - Neeraj Jain
	 */
	private int _createSubscriptionTimer(SipServletResponse response) {
		if (logger.isDebugEnabled())
			logger.debug("Creating subscription timer from response's Expires header");

		int timeout = response.getExpires();

		if(timeout <=  0) 
			return timeout;
		SBB sbb = this.getOperationContext().getSBB();

		if(logger.isDebugEnabled()) {
			logger.debug("Creating SUBSCRIPTION_TIMEOUT timer with period (secs): " + timeout);
		}

		m_timerType = TIMER_SUBSCRIPTION_TIMEOUT;
		TimerService timerService = (TimerService)
			sbb.getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
		m_timer = timerService.createTimer(sbb.getA().getApplicationSession(), timeout*1000, true, this);

		return timeout;
	}
}

