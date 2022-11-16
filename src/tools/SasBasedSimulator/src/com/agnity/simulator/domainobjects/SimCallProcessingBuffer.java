package com.agnity.simulator.domainobjects;

import jain.protocol.ss7.SccpUserAddress;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.agnity.simulator.callflowadaptor.element.Node;

/**
 * This class contains parameters required
 * for Inap/ISUP call by Simulator.
 * Insatnce of this class will float between TCAP/SIPT messages 
 * @author saneja
 *
 */
public class SimCallProcessingBuffer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334305780546473310L;

	public SimCallProcessingBuffer() {
		variableMap = new HashMap<String, Variable>();
		winReqDialogId = new HashMap<String, Integer>();
		winReqInvokeId = new HashMap<String, Integer>();
		invokeIdOpCodeMap = new HashMap<Integer, Byte>();
		invokeId = 0;
		dialogId =0;
		dialoguePortionPresent= false;
		isCleaned = false;
		isCallSuccess=false;
		isTcapTerminationMessageExchanged=false;
		setTcap(false);
		b2bCall = false;
	}
	
	public Map<String, Variable> getVariableMap(){
		return variableMap;
	}
	
	
	/**
	 * @param Variable adds the variable to set
	 */
	public void addVariable(Variable variable) {
		variableMap.put(variable.getVarName(),variable);
	}
	
	/**
	 * @param currNode the currNode to set
	 */
	public void setCurrNode(Node currNode) {
		this.currNode = currNode;
	}


	/**
	 * @return the currNode
	 */
	public Node getCurrNode() {
		return currNode;
	}


	/**
	 * @param expectedNode the expectedNode to set
	 */
	public void setExpectedNode(Node expectedNode) {
		this.expectedNode = expectedNode;
	}


	/**
	 * @return the expectedNode
	 */
	public Node getExpectedNode() {
		return expectedNode;
	}

	/**
	 * @param dialogId the dialogId to set
	 */
	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
		setTcap(true);
	}

	/**
	 * @return the dialogId
	 */
	public int getDialogId() {
		return dialogId;
	}

	/**
	 * @return the invokeId
	 */
	public int incrementAndGetInvokeId() {
		++invokeId;
		return invokeId;
	}


	/**
	 * @param invokeId the invokeId to set
	 */
	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}

	/**
	 * @return the dialoguePortionPresent
	 */
	public boolean isDialoguePortionPresent() {
		return dialoguePortionPresent;
	}

	/**
	 * @param dialoguePortionPresent the dialoguePortionPresent to set
	 */
	public void setDialoguePortionPresent(boolean dialoguePortionPresent) {
		this.dialoguePortionPresent = dialoguePortionPresent;
	}

	/**
	 * @return the appContextIdentifier
	 */
	public Integer getAppContextIdentifier() {
		return appContextIdentifier;
	}

	/**
	 * @param appContextIdentifier the appContextIdentifier to set
	 */
	public void setAppContextIdentifier(Integer appContextIdentifier) {
		this.appContextIdentifier = appContextIdentifier;
	}

	/**
	 * @return the appContextName
	 */
	public byte[] getAppContextName() {
		return appContextName;
	}

	/**
	 * @param appContextName the appContextName to set
	 */
	public void setAppContextName(byte[] appContextName) {
		this.appContextName = appContextName;
	}

	/**
	 * @return the protocolVersion
	 */
	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	public void setProtocolVersion(Integer protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the securityContextIdentifier
	 */
	public Integer getSecurityContextIdentifier() {
		return securityContextIdentifier;
	}

	/**
	 * @param securityContextIdentifier the securityContextIdentifier to set
	 */
	public void setSecurityContextIdentifier(Integer securityContextIdentifier) {
		this.securityContextIdentifier = securityContextIdentifier;
	}

	/**
	 * @return the securityContextInfo
	 */
	public byte[] getSecurityContextInfo() {
		return securityContextInfo;
	}

	/**
	 * @param securityContextInfo the securityContextInfo to set
	 */
	public void setSecurityContextInfo(byte[] securityContextInfo) {
		this.securityContextInfo = securityContextInfo;
	}

	/**
	 * @return the userInfo
	 */
	public byte[] getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo the userInfo to set
	 */
	public void setUserInfo(byte[] userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * @return the originatingAddress
	 */
	public SccpUserAddress getOriginatingAddress() {
		return originatingAddress;
	}

	/**
	 * @param originatingAddress the originatingAddress to set
	 */
	public void setOriginatingAddress(SccpUserAddress originatingAddress) {
		this.originatingAddress = originatingAddress;
	}

	/**
	 * @return the destinationAddress
	 */
	public SccpUserAddress getDestinationAddress() {
		return destinationAddress;
	}

	/**
	 * @param destinationAddress the destinationAddress to set
	 */
	public void setDestinationAddress(SccpUserAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}


	/**
	 * @param lastDialoguePrimitive the lastDialoguePrimitive to set
	 */
	public void setLastDialoguePrimitive(int lastDialoguePrimitive) {
		this.lastDialoguePrimitive = lastDialoguePrimitive;
	}

	/**
	 * @return the lastDialoguePrimitive
	 */
	public int getLastDialoguePrimitive() {
		return lastDialoguePrimitive;
	}


	/**
	 * @param callId the callId to set
	 */
	public void setCallId(String callId) {
		this.callId = callId;
	}

	/**
	 * @return the callId
	 */
	public String getCallId() {
		return callId;
	}
	
	/**
	 * @param lastSipMessage the lastSipMessage to set
	 */
	public void setLastSipMessage(SipServletMessage lastSipMessage) {
		this.lastSipMessage = lastSipMessage;
	}

	/**
	 * @param lastSipMessageLeg1 the lastSipMessageLeg1 to set
	 */
	public void setLastSipMessageLeg1(SipServletMessage lastSipMessage) {
		this.lastSipMessageLeg1 = lastSipMessage;
	}

	/**
	 * @param lastSipMessageLeg2 the lastSipMessageLeg2 to set
	 */
	public void setLastSipMessageLeg2(SipServletMessage lastSipMessage) {
		this.lastSipMessageLeg2 = lastSipMessage;
	}
	
	/**
	 * @return the lastSipMessage
	 */
	public SipServletMessage getLastSipMessage() {
		return lastSipMessage;
	}
	
	/**
	 * @return the lastSipMessageLeg1
	 */
	public SipServletMessage getLastSipMessageLeg1() {
		return lastSipMessageLeg1;
	}
	
	/**
	 * @return the lastSipMessageLeg2
	 */
	public SipServletMessage getLastSipMessageLeg2() {
		return lastSipMessageLeg2;
	}

	/**
	 * @param sipAppSession the sipAppSession to set
	 */
	public void setSipAppSession(SipApplicationSession sipAppSession) {
		this.sipAppSession = sipAppSession;
	}

	/**
	 * @return the sipAppSession
	 */
	public SipApplicationSession getSipAppSession() {
		return sipAppSession;
	}
	
	/**
	 * @param isCleaned the isCleaned to set
	 */
	public void setCleaned(boolean isCleaned) {
		this.isCleaned = isCleaned;
	}

	/**
	 * @return the isCleaned
	 */
	public boolean isCleaned() {
		return isCleaned;
	}



	/**
	 * @param supportsReliable the supportsReliable to set
	 */
	public void setSupportsReliable(boolean supportsReliable) {
		this.supportsReliable = supportsReliable;
	}
	
	/**
	 * @param supportsReliableLeg1 the supportsReliableleg1 to set
	 */
	public void setSupportsReliableLeg1(boolean supportsReliable) {
		this.supportsReliableLeg1 = supportsReliable;
	}
	
	/**
	 * @param supportsReliableLeg2 the supportsReliableleg2 to set
	 */
	public void setSupportsReliableLeg2(boolean supportsReliable) {
		this.supportsReliableLeg2 = supportsReliable;
	}
	
	/**
	 * @return the supportsReliable
	 */
	public boolean isSupportsReliable() {
		return supportsReliable;
	}

	/**
	 * @return the supportsReliableLeg1
	 */
	public boolean isSupportsReliableLeg1() {
		return supportsReliableLeg1;
	}
	
	/**
	 * @return the supportsReliableLeg2
	 */
	public boolean isSupportsReliableLeg2() {
		return supportsReliableLeg2;
	}
	
	
	
	/**
	 * @param origInviteRequest the origInviteRequest to set
	 */
	public void setOrigInviteRequest(SipServletRequest origInviteRequest) {
		this.origInviteRequest = origInviteRequest;
	}

	/**
	 * @param origInviteRequestLeg1 the origInviteRequestLeg1 to set
	 */
	public void setOrigInviteRequestLeg1(SipServletRequest origInviteRequest) {
		this.origInviteRequestLeg1 = origInviteRequest;
	}
	
	/**
	 * @param origInviteRequestLeg2 the origInviteRequestLeg2 to set
	 */
	public void setOrigInviteRequestLeg2(SipServletRequest origInviteRequest) {
		this.origInviteRequestLeg2 = origInviteRequest;
	}

	/**
	 * @return the origInviteRequest
	 */
	public SipServletRequest getOrigInviteRequest() {
		return origInviteRequest;
	}

	
	/**
	 * @return the origInviteRequestLeg1
	 */
	public SipServletRequest getOrigInviteRequestLeg1() {
		return origInviteRequestLeg1;
	}

	/**
	 * @return the origInviteRequestLeg2
	 */
	public SipServletRequest getOrigInviteRequestLeg2() {
		return origInviteRequestLeg2;
	}

	/**
	 * @param lastInvokeTime the lastInvokeTime to set
	 */
	public void setLastInvokeTime(long lastInvokeTime) {
		this.lastInvokeTime = lastInvokeTime;
	}

	/**
	 * @return the lastInvokeTime
	 */
	public long getLastInvokeTime() {
		return lastInvokeTime;
	}


	/**
	 * @param activityTestTimerTask the activityTestTimerTask to set
	 */
	public void setActivityTestTimerTask(TimerTask activityTestTimerTask) {
		this.activityTestTimerTask = activityTestTimerTask;
	}

	/**
	 * @return the activityTestTimerTask
	 */
	public TimerTask getActivityTestTimerTask() {
		return activityTestTimerTask;
	}

	/**
	 * @param invokeIdOpCodeMap the invokeIdOpCodeMap to set
	 */
	public void addInvokeIdOpCodeVal(Integer invokeId, Byte opCode) {
		this.invokeIdOpCodeMap.put(invokeId, opCode);
	}

	/**
	 * @return the invokeIdOpCodeMap
	 */
	public Map<Integer, Byte> getInvokeIdOpCodeMap() {
		return invokeIdOpCodeMap;
	}

	/**
	 * @param callSuccess the callSuccess to set
	 */
	public void setCallSuccess(boolean isCallSuccess) {
		this.isCallSuccess = isCallSuccess;
	}

	/**
	 * @return the callSuccess
	 */
	public boolean isCallSuccess() {
		return isCallSuccess;
	}

	/**
	 * @param terminationMessageSent the terminationMessageSent to set
	 */
	public void setTcapTerminationMessageExchanged(boolean isTcapTerminationMessageExchanged) {
		this.isTcapTerminationMessageExchanged = isTcapTerminationMessageExchanged;
	}

	/**
	 * @return the terminationMessageSent
	 */
	public boolean isTcapTerminationMessageExchanged() {
		return isTcapTerminationMessageExchanged;
	}


	/**
	 * @param isTcap the isTcap to set
	 */
	public void setTcap(boolean isTcap) {
		this.isTcap = isTcap;
	}

	/**
	 * @return the isTcap
	 */
	public boolean isTcap() {
		return isTcap;
	}



	/**
	 * @param callStartTime the callStartTime to set
	 */
	public void setCallStartTime(long callStartTime) {
		this.callStartTime = callStartTime;
	}

	/**
	 * @return the callStartTime
	 */
	public long getCallStartTime() {
		return callStartTime;
	}

	
	public boolean isB2bCall() {
		return b2bCall;
	}

	public void setB2bCall(boolean b2bCall) {
		this.b2bCall = b2bCall;
	}

	public SipSession getLeg1() {
		return leg1;
	}

	public void setLeg1(SipSession leg1) {
		this.leg1 = leg1;
	}

	public SipSession getLeg2() {
		return leg2;
	}

	public void setLeg2(SipSession leg2) {
		this.leg2 = leg2;
	}
	
	public String getLastSdpContent() {
		return lastSdpContent;
	}

	public void setLastSdpContent(String lastSdpContent) {
		this.lastSdpContent = lastSdpContent;
	}
	
	public String getLastSdpContentLeg1() {
		return lastSdpContentLeg1;
	}

	public void setLastSdpContentLeg1(String lastSdpContent) {
		this.lastSdpContentLeg1 = lastSdpContent;
	}
	
	public String getLastSdpContentLeg2() {
		return lastSdpContentLeg2;
	}

	public void setLastSdpContentLeg2(String lastSdpContent) {
		this.lastSdpContentLeg2 = lastSdpContent;
	}
	
	public String getLastInfoContent() {
		return lastInfoContent;
	}

	public void setLastInfoContent(String lastInfoContent) {
		this.lastInfoContent = lastInfoContent;
	}
	
	public String getLastInfoContentLeg1() {
		return lastInfoContentLeg1;
	}

	public void setLastInfoContentLeg1(String lastInfoContent) {
		this.lastInfoContentLeg1 = lastInfoContent;
	}
	
	public String getLastInfoContentLeg2() {
		return lastInfoContentLeg2;
	}

	public void setLastInfoContentLeg2(String lastInfoContent) {
		this.lastInfoContentLeg2 = lastInfoContent;
	}
	
	public String getLastContentType() {
		return lastContentType;
	}

	public void setLastContentType(String lastContentType) {
		this.lastContentType = lastContentType;
	}
	
	public String getLastContentTypeLeg1() {
		return lastContentTypeLeg1;
	}

	public void setLastContentTypeLeg1(String lastContentType) {
		this.lastContentTypeLeg1 = lastContentType;
	}
	
	public String getLastContentTypeLeg2() {
		return lastContentTypeLeg2;
	}

	public void setLastContentTypeLeg2(String lastContentType) {
		this.lastContentTypeLeg2 = lastContentType;
	}
	
	/**
	 * @return the winReqDialogId
	 */
	public int getWinReqDialogId(String message) {
		return winReqDialogId.get(message);
	}

	/**
	 * @param winReqDialogId the winReqDialogId to set
	 */
	public void setWinReqDialogId(String message,int dialogueId) {
			this.winReqDialogId.put(message, dialogueId);
	}
	
	/**
	 * @return the winReqInvokeId
	 */
	public int getWinReqInvokeId(String message) {
		return winReqInvokeId.get(message);
	}

	/**
	 * @param winReqInvokeId the winReqInvokeId to set
	 */
	public void setWinReqInvokeId(String message,int invokeId) {
		this.winReqInvokeId.put(message, invokeId);
	}

	//to remove dialogid after successful receive of response
	public void removeDialogId(String message){
		this.winReqDialogId.remove(message);
	}
	
	//to remove invokeid after successful receive of response
	public void removeInvokeId(String message){
		this.winReqInvokeId.remove(message);
	}
	/**
	 * @return the currentMessage
	 */
	public String getCurrentMessage() {
		return currentMessage;
	}

	/**
	 * @param currentMessage the currentMessage to set
	 */
	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}
	
	
	private Map<String,Variable> variableMap;
	private Node currNode;
	private Node expectedNode;
	private int dialogId;
	private Map<String,Integer> winReqDialogId;
	private Map<String,Integer> winReqInvokeId;
	private int invokeId;
	private boolean dialoguePortionPresent;
	private Integer appContextIdentifier;
	private byte[] appContextName;
	private Integer protocolVersion;
	private Integer securityContextIdentifier;
	private byte[] securityContextInfo;
	private byte[] userInfo;
	private SccpUserAddress originatingAddress;
	private SccpUserAddress destinationAddress;
	private int lastDialoguePrimitive;
	private String callId;
	private String lastSdpContent;
	private String lastSdpContentLeg1;
	private String lastSdpContentLeg2;
	private String lastInfoContent;
	private String lastInfoContentLeg1;
	private String lastInfoContentLeg2;
	private String lastContentType;

	private String lastContentTypeLeg1;
	private String lastContentTypeLeg2;
	private SipServletMessage lastSipMessage;
	private SipServletMessage lastSipMessageLeg1;
	private SipServletMessage lastSipMessageLeg2;
	private SipApplicationSession sipAppSession;
	private SipServletRequest origInviteRequest;
	private SipServletRequest origInviteRequestLeg1;
	private SipServletRequest origInviteRequestLeg2;
	private boolean isCleaned;
	private boolean supportsReliable;
	private boolean supportsReliableLeg1;
	private boolean supportsReliableLeg2;	
	private long lastInvokeTime;
	private Map<Integer, Byte> invokeIdOpCodeMap;
	private TimerTask activityTestTimerTask;
	private boolean isCallSuccess;
	private boolean isTcapTerminationMessageExchanged;
	private boolean isTcap;
	private long callStartTime;
	
	private boolean b2bCall;
	private SipSession leg1;
	private SipSession leg2;

	private String currentMessage = "none";

		
	
	
	
}
