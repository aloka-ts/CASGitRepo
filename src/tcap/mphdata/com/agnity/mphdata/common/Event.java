/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.mphdata.common;

public class Event {
	public enum EventType {
		EVENT_INITIAL, EVENT_SUCCESS, EVENT_FAILURE, EVENT_DISCONNECT, EVENT_RESYNC_SUCCESS, EVENT_SESSION_EXPIRED, 
		EVENT_DTMF, EVENT_MS_SUCCESS, EVENT_MS_FAILURE, EVENT_PLAY_SUCCESS, EVENT_PLAY_FAILURE, EVENT_PNC_SUCCESS, 
		EVENT_PNC_FAILURE, EVENT_PNR_SUCCESS, EVENT_PNR_FAILURE, EVENT_MS_DISCONNECT, EVENT_CALL_DROPPED, EVENT_FAILOVER,
		EVENT_OPR_FAIL, EVENT_OPR_SEND_FAIL, EVENT_CHRG, EVENT_CALL_HB_SUCCESS, EVENT_CALL_HB_FAIL,EVENT_REDIRECT,EVENT_HTTP_REQ
	}

	public Event(EventType eventType, Protocol protocol, String leg) {
		this.eventType = eventType;
		this.protocol = protocol;
		this.leg = leg;
	}

	private EventType		eventType;

	private String			leg;

	private Protocol		protocol;

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getLeg() {
		return leg;
	}

	public void setLeg(String leg) {
		this.leg = leg;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return "Event [eventType=" + eventType + ", leg=" + leg
						+ ", protocol=" + protocol + "]";
	}

}
