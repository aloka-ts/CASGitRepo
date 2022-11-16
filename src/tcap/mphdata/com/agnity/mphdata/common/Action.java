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

import java.io.Serializable;

public class Action implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8137299546758193620L;

	public enum ActionType {
		ACTION_CONNECT_MS, ACTION_DISCONNECT_MS, ACTION_PLAY, ACTION_PLAY_COLLECT, ACTION_PLAY_RECORD, 
		ACTION_CONNECT, ACTION_REDIRECT, ACTION_DISCONNECT, ACTION_END_CALL, 
		ACTION_PROCESS_CALLS, ACTION_RESYNC_CALL, ACTION_HOLD_CALL, ACTION_LS_CMD, 
		ACTION_HTTP_REQ, ACTION_CONTINUE, ACTION_CHRG, ACTION_CALL_HB, ACTION_HTTP_RES
	}
	
	
	public static enum CONNECTIONMODE {
		NONE, ASSIST, B2BUA, REDIRECTION, REROUTING, PORTROUTING,CONTINUE
	}
	

	public static enum ERB_TYPE {
		ERB_ROUTESELECTFAILURE, ERB_BUSY, ERB_NO_ANSWER, ERB_ANSWER, ERB_DISCONNECT, ERB_ABANDON,ERB_TERMSEIZED
	}
	
	/*
	 * Send mode is added to check if outgoing inap message will go in END or continue dialog
	 */
	public static enum SEND_MODE {
		END,CONTINUE,NONE
	}


	public static enum DROP_CALL_MODE {
		NONE, RELEASE_CALL, INITIAL_ERROR, USER_ABORT, USER_REJECT, NULL_END,NULL_END_PREARRANGED
	}
	
	public static enum CONTINUE_MODE {
		NONE, INITIAL_ERROR, USER_REJECT, ENTITY_RELEASE
	}
	

	public Action(ActionType actionType) {
		this.actionType = actionType;
	}

	private ActionType	actionType;

	private String		leg;

	private Protocol	protocol;
	
	private DROP_CALL_MODE	dropCallMode		= DROP_CALL_MODE.NONE;
	
	private CONNECTIONMODE	connectionMode		= CONNECTIONMODE.NONE;
	
	private CONTINUE_MODE	continueMode	= CONTINUE_MODE.NONE;
	
	private SEND_MODE	inapSendMode	= SEND_MODE.NONE;
	
	public SEND_MODE getSendMode() {
		return inapSendMode;
	}

	public void setSendMode(SEND_MODE sendMode) {
		this.inapSendMode = sendMode;
	}

	/*
	 * releaseCauseValue: INAP/ISUP Release Cause Value returned by services to
	 * protocol handler
	 */
	private int				releaseCauseValue;
	

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
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
	
	public void setDropCallMode(DROP_CALL_MODE value) {
		dropCallMode = value;
	}

	public DROP_CALL_MODE getDropCallMode() {
		return dropCallMode;
	}
	
	public void setConnectionMode(CONNECTIONMODE connectionMode) {
		this.connectionMode = connectionMode;
	}

	public CONNECTIONMODE getConnectionMode() {
		return connectionMode;
	}

	public void setContinueMode(CONTINUE_MODE continueMode) {
		this.continueMode = continueMode;
	}

	public CONTINUE_MODE getContinueMode() {
		return continueMode;
	}
	
	public int getReleaseCauseValue() {
		return releaseCauseValue;
	}

	public void setReleaseCauseValue(int releaseCauseValue) {
		this.releaseCauseValue = releaseCauseValue;
	}
	
	@Override
	public String toString() {
		return "Action [actionType=" + actionType + ", leg=" + leg + ", protocol=" + protocol + "]";
	}

}
