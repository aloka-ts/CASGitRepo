package com.baypackets.ase.ra.rf;

import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.resource.ResourceEvent;

/**
 * This clas defines all the events that can be generated during 
 * offline charging.
 *
 * @author Neeraj Jadaun
 */
public class RfResourceEvent extends ResourceEvent 
{
	public static final String TIMEOUT_EVENT = "timeout_event";
	public static final String INTERIM_TIMEOUT_EVENT = "interim_timeout_event";
	public static final String REQUEST_FAIL_EVENT = "request_fail_event";
	public static final String RESPONSE_FAIL_EVENT = "response_fail_event";
	private static Logger logger = Logger.getLogger(RfResourceEvent.class);

	/**	When peer diameter node (CDF) wants to disconnect with the local DFN.It sends diameter DPR message(Disconnect Peer Request).
	*	The Rf Client will invoke this API to intimate the Application about this event. After this event rf client interface 
	*	application should not send/invoke any new request/transactions to the stack.
	*/
	public static final String RF_NOTIFY_DISCONNECT_PEER_REQUEST = "RF_NOTIFY_DISCONNECT_PEER_REQUEST";
	
	/**	The response to the DPR from peer diameter node is intimated to the application using this API. After this event,
	*	application should not initiate any new requests to the peer node	
	*/
	public static final String RF_DISCONNECT_PEER_RESPONSE = "RF_NOTIFY_DISCONNECT_PEER_RESPONSE";

	/**	When there is transport connection failure with DFN, either due to the DFN crash or DFN is not responding to health check 
	*	messages the event is notified to the Rf application through this API.
	*/
	public static final String RF_NOTIFY_DFN_DOWN = "RF_NOTIFY_DFN_DOWN";

	/**	When a primary/Active peer is down, this API notifies the application about the peer.
	*/
	public static final String RF_NOTIFY_PEER_DOWN = "RF_NOTIFY_PEER_DOWN";

	/**	When a diameter peer node comes up this event is notified to the Rf application through this API
	*/
	public static final String RF_NOTIFY_PEER_UP = "RF_NOTIFY_PEER_UP";

	/**	When there is an unexpected message recieved from the peer in particular State/Transaction this event is notified to the
	*	application by invoking this API
	*/
	public static final String RF_NOTIFY_UNEXPECTED_MESSAGE = "RF_NOTIFY_UNEXPECTED_MESSAGE";

	public static final String RF_NOTIFY_FAILOVER = "RF_NOTIFY_FAILOVER";

	public static final int NO_ERROR = 0;
	
	private int errorCode = NO_ERROR;
	
	public RfResourceEvent(Object source, String type, SipApplicationSession appSession) 
	{
		super(source, type, appSession);
	}
	
	public void setErrorCode(int errorCode) 
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setErrorCode() called.");
		}
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() 
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getErrorCode() called.");
		}
		return this.errorCode;
	}
}
