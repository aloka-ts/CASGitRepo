package com.baypackets.ase.ra.diameter.gy;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;

public class GyResourceEvent extends ResourceEvent {

	public static final String TIMEOUT_EVENT = "TIMEOUT_EVENT";
	public static final String REQUEST_FAIL_EVENT = "REQUEST_FAIL_EVENT";
	public static final String RESPONSE_FAIL_EVENT = "RESPONSE_FAIL_EVENT";
	public static final String ERROR_MSG_RECEIVED = "ERROR_MSG_RECEIVED";
	
	/**
	 * This event is generated when application fails to send update interogation
	 * request before interim interval has elapsed.
	 */
	public static final String GY_INTERIM_TIMEOUT_EVENT = "INTERIM_TIMEOUT_EVENT";
	
    /**
	 *  When peer diameter node (CDF) wants to disconnect with the local DFN, It sends 
	 * diameter DPR message(Disconnect Peer Request).
     * The Sh Client will invoke this API to intimate the Application about this event.
	 * After this event sh client inteshace application should not send/invoke any new
	 * request/transactions to the stack.
     */
    public static final String GY_NOTIFY_DISCONNECT_PEER_REQUEST = "GY_NOTIFY_DISCONNECT_PEER_REQUEST";

    /** 
	 * The response to the DPR from peer diameter node is intimated to the application
	 * using this API. After this event, application should not initiate any new requests
	 * to the peer node
     */
    public static final String GY_DISCONNECT_PEER_RESPONSE = "GY_NOTIFY_DISCONNECT_PEER_RESPONSE";

	/**
	 *  When a primary/Active peer is down, this API notifies the application about the peer.
    */
    public static final String GY_NOTIFY_PEER_DOWN = "GY_NOTIFY_PEER_DOWN";

    /** 
	 * When a diameter peer node comes up this event is notified to the Sh application 
	 * through this API.
	 */
    public static final String GY_NOTIFY_PEER_UP = "GY_NOTIFY_PEER_UP";

    /** 
	 * When there is an unexpected message received from the peer in particular 
	 * State/Transaction this event is notified to the application by invoking this API.
     */
    public static final String GY_NOTIFY_UNEXPECTED_MESSAGE = "GY_NOTIFY_UNEXPECTED_MESSAGE";

    /**
     * When stack receives a redirect notification, then this event is generated.
     */
    public static final String GY_REDIRECT_NOTIFICATION = "GY_REDIRECT_NOTIFICATION";

    public static final String GY_NOTIFY_FAILOVER = "GY_NOTIFY_FAILOVER";

    
	/**
	 * When there is an error in processing a response or Re-Authorization-Request, then this
	 * event is fired with detailed error-information.
	 */
	public static final String GY_HB_HID_ERROR = "GY_HB_HID_ERROR";

	/**
	 * When there is an error in processing a response or Re-Authorization-Request, then this
	 * event is fired with detailed error-information.
	 */
	public static final String GY_ETE_ID_ERROR = "GY_ETE_ID_ERROR";

	/**
	 * When there is an invalid session string in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String GY_SESSION_STR_ERRROR = "GY_SESSION_STR_ERRROR";

	/**
	 * When there is an invalid AAA XML data in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String GY_AAA_XML_VALIDATION_ERROR = "GY_AAA_XML_VALIDATION_ERROR";

	/**
	 * When there is an invalid AAA AVP value in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String GY_AAA_INVALID_AVP_VALUE = "GY_AAA_INVALID_AVP_VALUE";

	/**
	 * When stack receives a message with unexpected Request-Type, this event is fired.
	 */
	public static final String GY_UNKNOWN_REQ_TYPE = "GY_UNKNOWN_REQ_TYPE";
	
	/**
	 * When stack receives a message with Requested-Action set to unexpected value, this
	 * event is generated.
	 */
	public static final String GY_UNEXPECTED_REQUESTED_ACTION = "GY_UNEXPECTED_REQUESTED_ACTION";

	/**
	 * This event indicates that server has permitted grant of service to the
	 * specified user.
	 */
	public static final String GY_GRANT_ENDUSER_SERVICE = "GY_GRANT_ENDUSER_SERVICE";

	/**
	 * This event indicates that server has refused grant of service to the
	 * specified user.
	 */
	public static final String GY_TERMINATE_ENDUSER_SERVICE = "GY_TERMINATE_ENDUSER_SERVICE";
	
	public static final int NO_ERROR = 0;
		
	private int errorCode = NO_ERROR;
	private Object data;	
	private GyMessage message;

	public GyResourceEvent( Object source, 
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

    public void setMessage(GyMessage msg) {
        this.message = msg;
    }

    public GyMessage getMessage() {
        return this.message;
    }
    
	public String toString() {
		return new String("GyResourceEvent:- Type : " + this.getType() +
						", Error Code : " + this.errorCode +
						", App Session : " + this.getApplicationSession() +
						", Data : " + this.data);
	}
}
