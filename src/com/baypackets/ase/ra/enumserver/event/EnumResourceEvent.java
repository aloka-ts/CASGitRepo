package com.baypackets.ase.ra.enumserver.event;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceEvent;

public class EnumResourceEvent extends ResourceEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Constant REQUEST_FAIL_EVENT. */
	public static final String RESPONSE_FAIL_EVENT = "RESPONSE_FAIL_EVENT";
	
	/** The Constant QUEUE_FULL. */
	public static final String QUEUE_FULL = "QUEUE_FULL";
	
	/** The Constant RA_DOWN. */
	public static final String RA_DOWN = "RA_DOWN";
	
	/** The Constant RA_UP. */
	public static final String RA_UP = "RA_UP";
	
    /** The Constant NO_ERROR. */
    public static final int NO_ERROR = 0;
		
	/** The error code. */
	private int errorCode = NO_ERROR;
	
	/** The data. */
	private Object data;	
	
	/** The message. */
	private EnumMessage message;


	public EnumResourceEvent(Object source, 
			                 String type,
			           SipApplicationSession appSession) {
		super(source, type, appSession);
	}

	
	/**
	 * Sets the error code.
	 *
	 * @param errorCode the new error code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public int getErrorCode() {
		return this.errorCode;
	}
	
    /**
     * Sets the data.
     *
     * @param eventData the new data
     */
    public void setData(Object eventData) {
        this.data = eventData;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Object getData() {
        return this.data;
    }

    /**
     * Sets the message.
     *
     * @param msg the new message
     */
    public void setMessage(Message msg) {
        this.message = (EnumMessage)msg;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public EnumMessage getMessage() {
        return this.message;
    }


}
