package com.agnity.sasapp.common;

import java.io.Serializable;

public class Action implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4721292140190665119L;

	public static enum ACTION {
		NONE, CONNECT_IVR, CONNECT_TERM_IVR, DISCONNECT_IVR, DISCONNECT_TERM_IVR, DISCONNECT_TERM,PLAY_ANNOUNCEMENT, PLAY_AND_COLLECT, 
		CONNECT_TERM, STOP_MS_OPERATION, DROP_CALL, PLAY_RECORD_ANNOUNCEMNET, COPY_ANNOUNCEMENT, LS_EXEC_CMD, RESYNC_CALL, DROP_AND_PROCESS_NEXT, 
		SEND_HTTP_REQUEST, CONTINUE_INAP,PROCESS_NEXT
	}

	public static enum CONNECTIONMODE {
		NONE, ASSIST, B2BUA, REDIRECTION, REROUTING, PORTROUTING
	}

	public static enum DROP_CALL_MODE {
		NONE, RELEASE_CALL, INITIAL_ERROR, USER_ABORT, USER_REJECT, NULL_END,NULL_END_PREARRANGED
	}
	
	/**
	 * Added for win
	 * @author reeta
	 *
	 */
//	public static enum LOW_BALANCE_NOT_MODE {
//		LOW_BAL_NOT,LOW_BAL_DISCONNECT
//	}
//	private LOW_BALANCE_NOT_MODE	lowBalanceMode		= LOW_BALANCE_NOT_MODE.LOW_BAL_NOT;
//	
//	public void setLowBalanceMode(LOW_BALANCE_NOT_MODE lowBalMode) {
//		this.lowBalanceMode = lowBalMode;
//	}
//
//	public LOW_BALANCE_NOT_MODE getLowBalanceMode() {
//		return lowBalanceMode;
//	}
	//*/
	
	
	
	//saneja@10406[
	public static enum CONTINUE_INAP_MODE {
		NONE, INITIAL_ERROR, USER_REJECT, ENTITY_RELEASE
	}
	private static String CONTINUE_INAP_MODE_STR=" CONTINUE Inap Mode = ";
	private CONTINUE_INAP_MODE	continueMode		= CONTINUE_INAP_MODE.NONE;
	
	public void setContinueMode(CONTINUE_INAP_MODE continueMode) {
		this.continueMode = continueMode;
	}

	public CONTINUE_INAP_MODE getContinueMode() {
		return continueMode;
	}
	
	//]10406@saneja
	
	private static String	DROP_CALL_MODE_STR	= " Drop Call Mode = ";
	private static String	CONNECTION_MODE_STR	= " Connection Mode = ";
	private static String	ACTION_STR			= " Action = ";
	  

	private CONNECTIONMODE	connectionMode		= CONNECTIONMODE.NONE;
	private boolean			handoffRequired;
	private boolean 		mustExecuteLsReq;
	private DROP_CALL_MODE	dropCallMode		= DROP_CALL_MODE.NONE;
	private int				ivrOpConnectionType	= ServiceInterface.IVR_CONNECTION_TYPE;

	private ACTION			actionToBePerformed	= ACTION.NONE;
	private AnnSpec			playSpec;
	/*
	 * releaseCause : SIP Error response returned by services to protocol
	 * handler
	 */
	private int				releaseCause;
	/*
	 * releaseCauseValue: INAP/ISUP Release Cause Value returned by services to
	 * protocol handler
	 */
	private int				releaseCauseValue;

	private boolean			busyFlag;
	private boolean			noAnswerFlag;
	private boolean  		callQueuingInitialCdrFlag;
	private int				noAnswerTimer;

	private String			recAnnPath;
	private String			recAnnTempPath;
	private String			recAnnFileReplacePath;
	private String			httpUrl;
	private String			httpHeader;

	//saneja @bug 10406 [
	//added for reject poblrm type
	private int				problemType;

	/**
	 * @param problemType
	 *            the problemType to set
	 */
	public void setProblemType(int problemType) {
		this.problemType = problemType;
	}

	/**
	 * @return the problemType
	 */
	public int getProblemType() {
		return problemType;
	}

	//]saneja@bug 10406 

	public Action() {

	}

	public Action(ACTION action) {
		actionToBePerformed = action;
	}

	public int getIvrOpConnectionType() {
		return ivrOpConnectionType;
	}

	public void setIvrOpConnectionType(int value) {
		ivrOpConnectionType = value;
	}

	public int getReleaseCause() {
		return releaseCause;
	}

	public void setReleaseCause(int releaseCause) {
		this.releaseCause = releaseCause;
	}

	public AnnSpec getPlaySpec() {
		return playSpec;
	}

	public void setPlaySpec(AnnSpec playSpec) {
		this.playSpec = playSpec;
	}

	public ACTION getActionToBePerformed() {
		return actionToBePerformed;
	}

	public void setActionToBePerformed(ACTION actionToBePerformed) {
		this.actionToBePerformed = actionToBePerformed;
	}

	public int getReleaseCauseValue() {
		return releaseCauseValue;
	}

	public void setReleaseCauseValue(int releaseCauseValue) {
		this.releaseCauseValue = releaseCauseValue;
	}

	public void setConnectionMode(CONNECTIONMODE connectionMode) {
		this.connectionMode = connectionMode;
	}

	public CONNECTIONMODE getConnectionMode() {
		return connectionMode;
	}

	public void setDropCallMode(DROP_CALL_MODE value) {
		dropCallMode = value;
	}

	public DROP_CALL_MODE getDropCallMode() {
		return dropCallMode;
	}

	public void setHandoffRequired(boolean isHandoffRequired) {
		this.handoffRequired = isHandoffRequired;
	}

	public boolean isHandoffRequired() {
		return handoffRequired;
	}

	public int getNoAnswerTimer() {
		return noAnswerTimer;
	}

	public void setNoAnswerTimer(int nat) {
		noAnswerTimer = nat;
	}
	
	public boolean getMustExecuteLsReqFlag() {
		return mustExecuteLsReq;
	}

	public void setMustExecuteLsReqFlag(boolean mustExecuteLsReq) {
		this.mustExecuteLsReq = mustExecuteLsReq;
	}

	public boolean getBusyFlag() {
		return busyFlag;
	}

	public void setBusyFlag(boolean flag) {
		busyFlag = flag;
	}

	public boolean getNoAnswerFlag() {
		return noAnswerFlag;
	}

	public void setNoAnswerFlag(boolean flag) {
		noAnswerFlag = flag;
	}

	public boolean isCallQueuingInitialCdrFlag() {
		return callQueuingInitialCdrFlag;
	}

	public void setCallQueuingInitialCdrFlag(boolean callQueuingInitialCdrFlag) {
		this.callQueuingInitialCdrFlag = callQueuingInitialCdrFlag;
	}
	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String value) {
		httpUrl = value;
	}

	/**
	 * @return the httpHeader
	 */
	public String getHttpHeader() {
		return httpHeader;
	}

	/**
	 * @param httpHeader
	 *            the httpHeader to set
	 */
	public void setHttpHeader(String httpHeader) {
		this.httpHeader = httpHeader;
	}

	/**
	 * @return the recAnnPath
	 */
	public String getRecAnnPath() {
		return recAnnPath;
	}

	/**
	 * @param recAnnPath
	 *            the recAnnPath to set
	 */
	public void setRecAnnPath(String recAnnPath) {
		this.recAnnPath = recAnnPath;
	}

	/**
	 * @return the recAnnTempPath
	 */
	public String getRecAnnTempPath() {
		return recAnnTempPath;
	}

	/**
	 * @param recAnnTempPath
	 *            the recAnnTempPath to set
	 */
	public void setRecAnnTempPath(String recAnnTempPath) {
		this.recAnnTempPath = recAnnTempPath;
	}

	/*--public String toString() {
		String str = ACTION_STR + actionToBePerformed.toString() + CONNECTION_MODE_STR
						+ connectionMode.toString() + DROP_CALL_MODE_STR + dropCallMode.toString()
						+ CONTINUE_INAP_MODE_STR + continueMode.toString();
		return str;
	}*/

	public String getRecAnnFileReplacePath() {
		return recAnnFileReplacePath;
	}

	public void setRecAnnFileReplacePath(String recAnnFileReplacePath) {
		this.recAnnFileReplacePath = recAnnFileReplacePath;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Action [actionToBePerformed=");
		builder.append(actionToBePerformed);
		builder.append(", busyFlag=");
		builder.append(busyFlag);
		builder.append(", connectionMode=");
		builder.append(connectionMode);
		builder.append(", continueMode=");
		builder.append(continueMode);
		builder.append(", dropCallMode=");
		builder.append(dropCallMode);
		builder.append(", handoffRequired=");
		builder.append(handoffRequired);
		builder.append(", httpHeader=");
		builder.append(httpHeader);
		builder.append(", httpUrl=");
		builder.append(httpUrl);
		builder.append(", ivrOpConnectionType=");
		builder.append(ivrOpConnectionType);
		builder.append(", noAnswerFlag=");
		builder.append(noAnswerFlag);
		builder.append(", noAnswerTimer=");
		builder.append(noAnswerTimer);
		builder.append(", playSpec=");
		builder.append(playSpec);
		builder.append(", problemType=");
		builder.append(problemType);
		builder.append(", recAnnFileReplacePath=");
		builder.append(recAnnFileReplacePath);
		builder.append(", recAnnPath=");
		builder.append(recAnnPath);
		builder.append(", recAnnTempPath=");
		builder.append(recAnnTempPath);
		builder.append(", releaseCause=");
		builder.append(releaseCause);
		builder.append(", releaseCauseValue=");
		builder.append(releaseCauseValue);
		builder.append("]");
		return builder.toString();
	}
}
	
