/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.impl;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.Address;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ConferenceRegistry;
import com.baypackets.ase.sbb.DefaultConferenceRegistry;
import com.baypackets.ase.sbb.b2b.NetworkMessageHandler;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.IncomingMessageListener;


/**
 *
 * The SBB Servlet Implementation. When the application want to use the SBBs.
 */
public class SBBServlet extends SipServlet implements SipErrorListener, TimerListener
{
	/** Logger element */
	private static Logger logger = Logger.getLogger(SBBServlet.class.getName());

	public SBBServlet() 
	{
		super();
	}

	public void init() throws ServletException 
	{
		if(logger.isDebugEnabled())
		{
	     		logger.debug("<SBB> init method called on the SBB Servlet...");
		}
	
		//Create the Conference Manager object and associate with the Servlet Context
		try
		{
			
 			ConferenceRegistry registry = (ConferenceRegistry)getServletContext().getAttribute(ConferenceRegistry.class.getName());
			if(registry == null)
			{
				registry = new DefaultConferenceRegistry();
				getServletContext().setAttribute(ConferenceRegistry.class.getName(), registry);
			}
		}	
		catch(Exception e)
		{
			getServletContext().log(e.getMessage(), e);
		}
	}


	/* (non-Javadoc)
	* @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	*/
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException 
	{
		if(logger.isDebugEnabled()){
			if(req != null)
				logger.debug("<SBB> Received request  :: \n "+req );
			else if (resp != null)
				logger.debug("<SBB> Received response  :: \n "+resp );
				
		}

		SBBSubscriptionState sbbSubscriptionState = null;
		SipServletRequest request = (SipServletRequest)req;
		SipServletResponse response = (SipServletResponse)resp;

		//Invoking the Incoming Message Listener if its not NULL
		if(req != null && resp == null) {
			SBB sbb = getSBBOperationContext(request).getSBB();
			IncomingMessageListener listener = sbb.getIncomingMessageListener();
			if(listener != null) {
				try {
					listener.handleIncomingMessage(request);
				} catch(Exception exp) {
					logger.error("Exception in IncomingMessageListener", exp);
				}
			}
		} else if(req == null && resp != null) {
			SBB sbb = getSBBOperationContext(response).getSBB();
			IncomingMessageListener listener = sbb.getIncomingMessageListener();
			if(listener != null) {
				try {
					listener.handleIncomingMessage(response);
				} catch(Exception exp) {
					logger.error("Exception in IncomingMessageListener", exp);
				}
			}
		}	
		
		if(req!=null)
		{

			if((request.getSession().getAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB))==null)
			{
				sbbSubscriptionState = new SBBSubscriptionState();

				request.getSession().setAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB, sbbSubscriptionState);
				if(logger.isDebugEnabled()) 
					logger.debug("Set the ATTRIBUTE_SUBSCRIPTION_STATE_SBB , Request === > "+request);
			}
			else
			{
				sbbSubscriptionState =(SBBSubscriptionState)(request.getSession().getAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB));
			}
				
		}

		if(resp!=null)
		{
			if((response.getSession().getAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB))==null)
			{
				sbbSubscriptionState = new SBBSubscriptionState();

				response.getSession().setAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB, sbbSubscriptionState);
				
			}
			else
			{
				sbbSubscriptionState =(SBBSubscriptionState)(response.getSession().getAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB));
			}
		}


		SBBOperation oper = getOperationHandler((SipServletRequest)req,(SipServletResponse)resp);
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Operation handler returned by getOperationHandler :: "+oper);

		//*** Making SBB Platform Independent Start
		if(request!=null) {
			String method = request.getMethod();
			boolean isInitialReq = request.isInitial();
		
			if(isInitialReq){
				request.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(Constants.STATE_EARLY));
				if(logger.isDebugEnabled()) 
					logger.debug("Set the EARLY STATE for DIALOG_STATE ");
			} else if(method.equals("NOTIFY")) {
				String event = ((SipServletRequest)req).getHeader("Event");
				event = (event == null) ? "" : event;
				String subscriptionState = ((SipServletRequest)req).getHeader("Subscription-State");
				subscriptionState = (subscriptionState == null) ? "" : subscriptionState;
				if(logger.isDebugEnabled()){
					logger.debug("Event :" + event + ",Subscription State :"+subscriptionState);
				}
				if(subscriptionState.indexOf("active")>=0 || subscriptionState.indexOf("pending")>=0) {
					sbbSubscriptionState.addToList(event);
				}else {
					sbbSubscriptionState.removeFromList(event);
				}
			}else if(method.equals("BYE")) {
				sbbSubscriptionState.byeRecieved();
			}	
			if(sbbSubscriptionState.isListEmpty() && sbbSubscriptionState.isByeRecieved()){
				request.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, new Integer(Constants.STATE_TERMINATED));
			}
			request.getSession().setAttribute(Constants.ATTRIBUTE_SUBSCRIPTION_STATE_SBB,sbbSubscriptionState);
			if (method.equals("INVITE") && !isInitialReq){
				Object pendingRequestObj = request.getSession().getAttribute(Constants.PENDING_REQUEST);
				if(pendingRequestObj != null && ((Boolean)pendingRequestObj).booleanValue()){
					logger.error("<SBB> Request is still pending so sending 491");
					SipServletResponse pendingResponse = request.createResponse(SipServletResponse.SC_REQUEST_PENDING);
					pendingResponse.send();
				        pendingResponse.getSession().setAttribute(Constants.SEND_491, true);	
                                       return;
				}
			}
	                //if property propogate.failure.resp.ack is true, ack for 4xx handled by container  
                        if (request.getMethod().equalsIgnoreCase(Constants.METHOD_ACK) && 
                        		(request.getSession().getAttribute(Constants.SEND_491)!=null)){
                        	if(logger.isDebugEnabled()){
                    			logger.debug("ACK received for 491");
                    		}
                    		request.getSession().removeAttribute(Constants.SEND_491);
                    		return;
                        }	

                    }


		//Need to take care of the session state for the Downstream as well.
		//So fixed it by changing the following block of code.
		if(response!=null) {
			int status = response.getStatus();
			String method = response.getRequest().getMethod();
			boolean isInitialReq = response.getRequest().isInitial();
		
			//Fix to a IBM's WAS specific problem. In WAS, the isInitial() method
			//returns FALSE, if the initial INVITE request was created using SipFactory.
			//The following block is a temporary workaround for the same.
			//It may not work in all the cases.
			if(!isInitialReq){
				//UAT-745
				if(method.equals("INVITE") && status > 199){
					if(logger.isDebugEnabled()){
						logger.debug("<SBB> No Pending request now");
					}
					response.getSession().setAttribute(Constants.PENDING_REQUEST, false);
				}
				Object storedReq = response.getSession().getAttribute(Constants.ATTRIBUTE_INIT_REQUEST);
				isInitialReq = (storedReq == response.getRequest());
			}
			
			Integer State = null;
			if( status ==408 || status ==481) {
				State = new Integer(Constants.STATE_TERMINATED);
			}

			if(isInitialReq && (method.equals("INVITE") || method.equals("SUBSCRIBE") || method.equals("REFER"))){
					
				if(status >=300 && status <=600){
					State = new Integer(Constants.STATE_TERMINATED);
				}
				if(status >=100 && status <=199){
                                        State = new Integer(Constants.STATE_EARLY);
                                }
				if(status >=200 && status <=299){
                                        State = new Integer(Constants.STATE_CONFIRMED);
                                }
			}
			
			if(State != null){
				response.getSession().setAttribute(Constants.ATTRIBUTE_DIALOG_STATE, State);
			}
		}
			

        	if (request != null) {
			if(logger.isDebugEnabled()){
				logger.debug("<SBB> Session State :::: " + request.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE));
			}
			oper.handleRequest(request);
		} else if (resp != null) {
			if(logger.isDebugEnabled()){
				logger.debug("<SBB> Session State :::: " + response.getSession().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE));
			}
			oper.handleResponse((SipServletResponse)resp);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipErrorListener#noAckReceived(javax.servlet.sip.SipErrorEvent)
	 */
	public void noAckReceived(SipErrorEvent errEvent) 
	{
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> entered noAckReceived ");
		SipServletRequest req = errEvent.getRequest();
		SipServletResponse resp = errEvent.getResponse();
		SBBOperation oper = getOperationHandler(req,resp);
		if (oper == null) 
		{
			logger.error("operation is null");
			return;
		}
		if (req == null) 
		{
			logger.error("request is null");
			return;
		} 
		oper.ackTimedout(req.getSession());
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Exited noAckReceived");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipErrorListener#noPrackReceived(javax.servlet.sip.SipErrorEvent)
	 */
	public void noPrackReceived(SipErrorEvent errEvent) 
	{
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> PRACK  Timed out ");
		SipServletRequest req = errEvent.getRequest();
		SipServletResponse resp = errEvent.getResponse();
		SBBOperation oper = getOperationHandler(req,resp);
		if (oper == null) 
		{
			logger.error("operation is null");
			return;
		}
		if (req == null) 
		{
			logger.error("request is null");
			return;
		}
		oper.prackTimedout(req.getSession());
	}
	
	protected SBBOperationContext getSBBOperationContext(SipServletMessage msg)
	{
		boolean loggerEnabled = logger.isDebugEnabled();
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB>Entered getSBBOperationContext(SipServletMessage)");
		
		SBBOperationContext operContext = null;
		String sbbName = (String)msg.getSession().getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		
		if (loggerEnabled) 
		{
			logger.debug("getSBBOperationContext(): App session ID is: " + msg.getApplicationSession().getId());
			logger.debug("getSBBOperationContext(): SBB assiciated with current app session is :: "+sbbName);
		}

		SipApplicationSession appSession = msg.getApplicationSession();
		SBBOperationContext operCtx = (SBBOperationContext)appSession.getAttribute(sbbName);
		
		if(operCtx != null && operCtx.getSBB().getServletContext()==null)
			operCtx.getSBB().setServletContext(getServletContext());
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> exited getSBBOperationContext(SipServletMessage) with operCtx ="+operCtx); 	
		return operCtx;
	}

	/*
	*  This method returns the Operation handler associated with the current request/response.
	*/
	private SBBOperation getOperationHandler(SipServletRequest request,SipServletResponse response) 
	{
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB>getOperationHandler called");
		SBBOperationContext operCtx = null;
		SipApplicationSession appSession = null;
		SBBOperation oper = null;
		if (request != null && response == null) 
		{
			// This can be a ACK, PRACK, or a new network request
			operCtx = getSBBOperationContext(request);
			oper = operCtx.getMatchingSBBOperation(request);
			//  No operation matches, must be a new network request
			if (oper == null) 
			{
				// create a new network operation handler
				if(logger.isInfoEnabled()) 
					logger.info("<SBB>External Network request");
				oper = new NetworkMessageHandler(request);
				operCtx.addSBBOperation(oper);
			}
		}
		else if (request == null && response != null)
		{
			// extract the sbb assiciated with the session
			operCtx = getSBBOperationContext(response);
			oper = operCtx.getMatchingSBBOperation(response);
			if (oper == null) 
			{
				// create a new network operation handler
				if(logger.isInfoEnabled()) 
					logger.info("<SBB>External Network response");
				oper = new NetworkMessageHandler(response);
				operCtx.addSBBOperation(oper);
			}
		}

		// In ACK-timeout or PRACK-timeout
		else if (request != null && response != null) 
		{
			if(logger.isDebugEnabled()) 
				logger.debug("<SBB> ACK/PRACK timeout ");
			// Extract the matching operation by using response
			// In ACK timeout request = INVITE & response = 200 OK
			// In PRACK timeout request = INVITE & response = 180 REL
			operCtx = getSBBOperationContext(request);
			if (operCtx !=null)
				oper = operCtx.getMatchingSBBOperation(request);
		}
		return oper;
	}


	public void timeout(ServletTimer timer) 
	{
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB>timeout called on SBBServlet ");
		Object oper = timer.getInfo();
		if(oper instanceof SBBOperation)
		{
			((SBBOperation)oper).timerExpired(timer);
		}
	}
}
