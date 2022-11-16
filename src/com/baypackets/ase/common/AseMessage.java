/*
 * Created on Aug 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.common;

import java.util.EventObject;

import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.AseStrings;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AseMessage {
	
	//Message type constants	
	public static final int MESSAGE = 1;
	//public static final int RESPONSE = 2;
	public static final int EVENT = 3;
	public static final int OTHER = 4;

	//Status type constants.
	public static final int PROCESSED = 1;
	public static final int NO_DESTINATION_FOUND = 2;
	public static final int LOOP_DETECTED = 3;
	public static final int OTHER_ERROR = 4;
	public static final int LOOPBACK_SYNC = 5;
	public static final int LOOPBACK_ASYNC = 6;
	
	private int messageType;
	private boolean bPriorityMsg = false;
	private int status;
	
	private SasMessage message;
	private EventObject event;
	private AseEventListener listener;
	private Object data;
	
	private int workQueue = -1;
	
	private boolean isInapMessage = false;
	private boolean isInitial = false;
	private boolean isPrepaidMessage = false;

	public boolean isPrepaidMessage() {
		return isPrepaidMessage;
	}

	public void setPrepaidMessage(boolean isPrepaidMessage) {
		this.isPrepaidMessage = isPrepaidMessage;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	public boolean isInapMessage() {
		return isInapMessage;
	}

	public void setInapMessage(boolean isInapMessage) {
		this.isInapMessage = isInapMessage;
	}

	public AseMessage(SasMessage request){
		this.setMessage(request);
		this.setMessageType(MESSAGE);
	}

   public AseMessage(AseEvent event){
        this.setEvent(event);
        this.setMessageType(EVENT);
    }

	
	public AseMessage(SasMessage request,boolean priorityMsg){
		this.setMessage(request);
		message.setMessagePriority(priorityMsg);
		this.setMessageType(MESSAGE);
		bPriorityMsg = priorityMsg;
	}
	public AseMessage(EventObject event, AseEventListener listener){
		this.setMessageType(EVENT);
		this.setEvent(event);
		this.setListener(listener);
	}
		

	public Object getData() {
		return data;
	}

	public EventObject getEvent() {
		return event;
	}

	public int getMessageType() {
		return messageType;
	}

	public SasMessage getMessage() {
		return message;
	}

	public boolean isPriorityMessage()	{
		return bPriorityMsg;
	}

	public void setData(Object object) {
		data = object;
	}

	public void setEvent(EventObject event) {
		this.event = event;
	}

	public void setMessageType(int i) {
		messageType = i;
	}

	public void setMessage(SasMessage message) {
		this.message = message;
	}

	public int getWorkQueue() {
		return workQueue;
	}

	public void setWorkQueue(int i) {
		workQueue = i;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int i) {
		status = i;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("AseMessage [");
		buffer.append("type=");
		buffer.append(this.messageType);
		buffer.append(", message=");
		buffer.append(this.message);
		buffer.append(", event =");
		buffer.append(this.event);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

	public AseEventListener getListener() {
		return listener;
	}

	public void setListener(AseEventListener listener) {
		this.listener = listener;
	}
}
