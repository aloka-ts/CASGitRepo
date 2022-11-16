package com.baypackets.ase.ra.ro;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;

public class RoResourceEvent extends ResourceEvent {

	/**
	 * This event is fired when response to an RO request is not received within
	 * stipulated time period.
	 */
	public static final String RO_TX_TIMEROUT_EVENT = "RO_TX_TIMEROUT_EVENT";

	/**
	 * This event is generated when application fails to send update interogation
	 * request before interim interval has elapsed.
	 */
	public static final String RO_INTERIM_TIMEOUT_EVENT = "INTERIM_TIMEOUT_EVENT";

	/**
	 * When peer diameter node (CDF) wants to disconnect with the local DFN. It
	 * sends diameter DPR message (Disconnect Peer Request).
	 * <p/>
	 * The Ro Client will invoke this API to intimate Ro RA about this event.
	 * After this event Ro RA should not send/invoke any new request/
	 * transactions on the stack.
	 */
	public static final String RO_NOTIFY_DISCONNECT_PEER_REQUEST = "RO_NOTIFY_DISCONNECT_PEER_REQUEST";

	/**
	 * The response to the DPR from peer diameter node is intimated to the
	 * application using this API. After this event, application should not
	 * initiate any new requests to the peer node.
	 */
	public static final String RO_DISCONNECT_PEER_RESPONSE = "RO_NOTIFY_DISCONNECT_PEER_RESPONSE";

	/**
	 * When there is transport connection failure with DFN, either due to the
	 * DFN crash or DFN is not responding to health check messages the event
	 * is notified to the Ro application through this API.
	 */
	public static final String RO_NOTIFY_DFN_DOWN = "RO_NOTIFY_DFN_DOWN";
		
	/**
	 * Whenever down DFN becomes active, the event is notified to application
	 * through this API.
	 */
	
	public static final String RO_NOTIFY_DFN_UP = "RO_NOTIFY_DFN_UP";

	/**
	 * When a primary/Active peer is down, this API notifies the application
	 * about the peer.
	 */
	public static final String RO_NOTIFY_PEER_DOWN = "RO_NOTIFY_PEER_DOWN";

	/**
	 * When a diameter peer node comes up this event is notified to the Ro
	 * application through this API.
	 */
	public static final String RO_NOTIFY_PEER_UP = "RO_NOTIFY_PEER_UP";

	/**
	 * When there is an unexpected message recieved from the peer in particular
	 * State/Transaction this event is notified to the application by invoking
	 * this API.
	 */
	public static final String RO_NOTIFY_UNEXPECTED_MESSAGE = "RO_NOTIFY_UNEXPECTED_MESSAGE";

	/**
	 * When there is an error in processing a response or Re-Authorization-Request, then this
	 * event is fired with detailed error-information.
	 */
	public static final String RO_HB_HID_ERROR = "RO_HB_HID_ERROR";

	/**
	 * When there is an error in processing a response or Re-Authorization-Request, then this
	 * event is fired with detailed error-information.
	 */
	public static final String RO_ETE_ID_ERROR = "RO_ETE_ID_ERROR";

	/**
	 * When there is an invalid session string in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String RO_SESSION_STR_ERRROR = "RO_SESSION_STR_ERRROR";

	/**
	 * When there is an invalid AAA XML data in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String RO_AAA_XML_VALIDATION_ERROR = "RO_AAA_XML_VALIDATION_ERROR";

	/**
	 * When there is an invalid AAA AVP value in a response or Re-Authorization-Request, then
	 * this event is fired with detailed error-information.
	 */
	public static final String RO_AAA_INVALID_AVP_VALUE = "RO_AAA_INVALID_AVP_VALUE";

	/**
	 * When stack receives a message with unexpected Request-Type, this event is fired.
	 */
	public static final String RO_UNKNOWN_REQ_TYPE = "RO_UNKNOWN_REQ_TYPE";
	
	/**
	 * When stack receives a message with Requested-Action set to unexpected value, this
	 * event is generated.
	 */
	public static final String RO_UNEXPECTED_REQUESTED_ACTION = "RO_UNEXPECTED_REQUESTED_ACTION";

	/**
	 * When stack receives a redirect notification, then this event is generated.
	 */
	public static final String RO_REDIRECT_NOTIFICATION = "RO_REDIRECT_NOTIFICATION";

	/**
	 * This event indicates that server has permitted grant of service to the
	 * specified user.
	 */
	public static final String RO_GRANT_ENDUSER_SERVICE = "RO_GRANT_ENDUSER_SERVICE";

	/**
	 * This event indicates that server has refused grant of service to the
	 * specified user.
	 */
	public static final String RO_TERMINATE_ENDUSER_SERVICE = "RO_TERMINATE_ENDUSER_SERVICE";

	////////////////////////////// Implementation ///////////////////////////////////
	private int errorCode = 0;
	private Object data;
	private RoMessage message;

	public RoResourceEvent(	Object source,
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

	public void setMessage(RoMessage msg) {
		this.message = msg;
	}

	public RoMessage getMessage() {
		return this.message;
	}

	public String toString() {
		return new String("RoResourceEvent:- Type : " + this.getType() +
						", Error Code : " + this.errorCode +
						", App Session : " + this.getApplicationSession() +
						", Data : " + this.data);
	}
}

