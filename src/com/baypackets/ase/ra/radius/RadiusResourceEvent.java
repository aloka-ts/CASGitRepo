package com.baypackets.ase.ra.radius;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;


/**
 * This class defines all the events that can be generated during 
 * radius authentication and accounting.
 *
 * @author Amit Baxi
 */
public class RadiusResourceEvent extends ResourceEvent {

	private static final long serialVersionUID = 1L;
	
	public static final String TIMEOUT_EVENT = "TIMEOUT_EVENT";
	public static final String REQUEST_FAIL_EVENT = "REQUEST_FAIL_EVENT";
	public static final String RESPONSE_FAIL_EVENT = "RESPONSE_FAIL_EVENT";
	public static final String ERROR_MSG_RECEIVED = "ERROR_MSG_RECEIVED";
	

	
	public static final int NO_ERROR = 0;
		
	private int errorCode = NO_ERROR;
	private Object data;	
	private RadiusMessage message;

	public RadiusResourceEvent( Object source, 
							String type, 
							SipApplicationSession appSession) {
		super(source, type, appSession);
	}
	
	/**
	 * This method sets error code for this RadiusResourceEvent.
	 * @param errorCode
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * This method returns error code for this RadiusResourceEvent.
	 * @return
	 */
	public int getErrorCode() {
		return this.errorCode;
	}
	
	
    /**
     * This method sets data for this RadiusResourceEvent.
     * @param eventData
     */
    public void setData(Object eventData) {
        this.data = eventData;
    }

    /**
     * This method returns data for this RadiusResourceEvent.
     * @return
     */
    public Object getData() {
        return this.data;
    }

    /**
     * This method sets message for this RadiusResourceEvent.
     * @param msg
     */
    public void setMessage(RadiusMessage msg) {
        this.message = msg;
    }

    /**
     * This method returns message for this RadiusResourceEvent.
     * @return
     */
    public RadiusMessage getMessage() {
        return this.message;
    }
    
	public String toString() {
		return new String("RadiusResourceEvent:- Type : " + this.getType() +
						", Error Code : " + this.errorCode +
						", App Session : " + this.getApplicationSession() +
						", Data : " + this.data);
	}
}
