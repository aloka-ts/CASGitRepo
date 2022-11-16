package com.baypackets.ase.ra.diameter.rf;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;


/**
 * This class defines all the events that can be generated during 
 * offline charging.
 *
 * @author Prshant Kumar
 */
public class RfResourceEvent extends ResourceEvent {

	public static final String TIMEOUT_EVENT = "TIMEOUT_EVENT";
	public static final String REQUEST_FAIL_EVENT = "REQUEST_FAIL_EVENT";
	public static final String RESPONSE_FAIL_EVENT = "RESPONSE_FAIL_EVENT";
	public static final String ERROR_MSG_RECEIVED = "ERROR_MSG_RECEIVED";
	
    /**
	 *  When peer diameter node (CDF) wants to disconnect with the local DFN, It sends 
	 * diameter DPR message(Disconnect Peer Request).
     * The Sh Client will invoke this API to intimate the Application about this event.
	 * After this event sh client inteshace application should not send/invoke any new
	 * request/transactions to the stack.
     */
    public static final String RF_NOTIFY_DISCONNECT_PEER_REQUEST = "RF_NOTIFY_DISCONNECT_PEER_REQUEST";

    /** 
	 * The response to the DPR from peer diameter node is intimated to the application
	 * using this API. After this event, application should not initiate any new requests
	 * to the peer node
     */
    public static final String RF_DISCONNECT_PEER_RESPONSE = "RF_NOTIFY_DISCONNECT_PEER_RESPONSE";

    /**
	 *  When there is transport connection failure with DFN, either due to the DFN crash 
	 * or DFN is not responding to health check messages the event is notified to the Sh
	 * application through this API.
     */
    public static final String RF_NOTIFY_DFN_DOWN = "RF_NOTIFY_DFN_DOWN";
    
	/**
	 *  When a primary/Active peer is down, this API notifies the application about the peer.
    */
    public static final String RF_NOTIFY_PEER_DOWN = "RF_NOTIFY_PEER_DOWN";

    /**
     * Whenever down DFN becomes active, the event is notified to application
     * through this API.
     */

    public static final String RF_NOTIFY_DFN_UP = "RF_NOTIFY_DFN_UP";

    /** 
	 * When a diameter peer node comes up this event is notified to the Sh application 
	 * through this API.
	 */
    public static final String RF_NOTIFY_PEER_UP = "RF_NOTIFY_PEER_UP";

    /** 
	 * When there is an unexpected message recieved from the peer in particular 
	 * State/Transaction this event is notified to the application by invoking this API.
     */
    public static final String RF_NOTIFY_UNEXPECTED_MESSAGE = "RF_NOTIFY_UNEXPECTED_MESSAGE";

    /**
     * When stack receives a redirect notification, then this event is generated.
     */
    public static final String RF_REDIRECT_NOTIFICATION = "RF_REDIRECT_NOTIFICATION";

    public static final String RF_NOTIFY_FAILOVER = "RF_NOTIFY_FAILOVER";

	public static final int NO_ERROR = 0;
		
	private int errorCode = NO_ERROR;
	private Object data;	
	private RfMessage message;

	public RfResourceEvent( Object source, 
							String type, 
							SipApplicationSession appSession) {
		super(source, type, appSession);
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
	
    public void setData(Object eventData) {
        this.data = eventData;
    }

    public Object getData() {
        return this.data;
    }

    public void setMessage(RfMessage msg) {
        this.message = msg;
    }

    public RfMessage getMessage() {
        return this.message;
    }
}
