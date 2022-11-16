package com.baypackets.ase.ra.diameter.sh;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;

public class ShResourceEvent extends ResourceEvent {

	public static final String TIMEOUT_EVENT = "timeout_event";
	public static final String REQUEST_FAIL_EVENT = "request_fail_event";
	public static final String RESPONSE_FAIL_EVENT = "response_fail_event";
	public static final String ERROR_MSG_RECEIVED = "ERROR_MSG_RECEIVED";
	
    /**
	 *  When peer diameter node (CDF) wants to disconnect with the local DFN, It sends 
	 * diameter DPR message(Disconnect Peer Request).
     * The Sh Client will invoke this API to intimate the Application about this event.
	 * After this event sh client inteshace application should not send/invoke any new
	 * request/transactions to the stack.
     */
    public static final String SH_NOTIFY_DISCONNECT_PEER_REQUEST = "SH_NOTIFY_DISCONNECT_PEER_REQUEST";

    /** 
	 * The response to the DPR from peer diameter node is intimated to the application
	 * using this API. After this event, application should not initiate any new requests
	 * to the peer node
     */
    public static final String SH_DISCONNECT_PEER_RESPONSE = "SH_NOTIFY_DISCONNECT_PEER_RESPONSE";

    /**
	 *  When there is transport connection failure with DFN, either due to the DFN crash 
	 * or DFN is not responding to health check messages the event is notified to the Sh
	 * application through this API.
     */
    public static final String SH_NOTIFY_DFN_DOWN = "SH_NOTIFY_DFN_DOWN";
    
	/**
	 *  When a primary/Active peer is down, this API notifies the application about the peer.
    */
    public static final String SH_NOTIFY_PEER_DOWN = "SH_NOTIFY_PEER_DOWN";

    /**
     * Whenever down DFN becomes active, the event is notified to application
     * through this API.
     */

    public static final String SH_NOTIFY_DFN_UP = "SH_NOTIFY_DFN_UP";

    /** 
	 * When a diameter peer node comes up this event is notified to the Sh application 
	 * through this API.
	 */
    public static final String SH_NOTIFY_PEER_UP = "SH_NOTIFY_PEER_UP";

    /** 
	 * When there is an unexpected message recieved from the peer in particular 
	 * State/Transaction this event is notified to the application by invoking this API.
     */
    public static final String SH_NOTIFY_UNEXPECTED_MESSAGE = "SH_NOTIFY_UNEXPECTED_MESSAGE";

    /**
     * When stack receives a redirect notification, then this event is generated.
     */
    public static final String SH_REDIRECT_NOTIFICATION = "SH_REDIRECT_NOTIFICATION";

    public static final String SH_NOTIFY_FAILOVER = "SH_NOTIFY_FAILOVER";

	public static final int NO_ERROR = 0;
		
	private int errorCode = NO_ERROR;
	private Object data;	
	private ShMessage message;

	public ShResourceEvent( Object source, 
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

    public void setMessage(ShMessage msg) {
        this.message = msg;
    }

    public ShMessage getMessage() {
        return this.message;
    }
}
