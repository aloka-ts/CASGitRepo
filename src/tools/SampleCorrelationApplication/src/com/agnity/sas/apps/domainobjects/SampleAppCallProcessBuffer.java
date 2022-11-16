package com.agnity.sas.apps.domainobjects;

import jain.protocol.ss7.SccpUserAddress;

import java.io.Serializable;
import java.util.Timer;

import javax.servlet.sip.SipApplicationSession;





/**
 * This class contains parameters required
 * for Inap call.
 * Insatnce of this class will float between INAP messages 
 * @author saneja
 *
 */
public class SampleAppCallProcessBuffer implements Serializable {

	/**
	 * Sbb will use this invokeId.This Parameter will be incremented
	 * corresponding to each call.It should be in sync so it is put in buffer.
	 */
	private int invokeId = 0 ;
	
	private Integer dlgId;
	private boolean dialoguePortionPresent;
	private Integer appContextIdentifier;
	private byte[] appContextName;
	private Integer protocolVersion;
	private Integer securityContextIdentifier;
	private byte[] securityContextInfo;
	private byte[] userInfo;
	private int lastDialoguePrimitive;
	private SampleAppCallStateInfo stateInfo = new SampleAppCallStateInfo();
	private StringBuilder cdr =new StringBuilder();
	private int dpCount = 0 ;
	private ParsedIdp idpContent;
	private int corrId;
	private String assistingSspIp;
	private Timer etcTimer;
	private SipApplicationSession appSession;
	private String destinationRoutingAddress;
	private int sipTcorrId;
	private Timer conTimer;
	private SccpUserAddress originatingAddress;
	private SccpUserAddress destinationAddress;
	/**
	 * @param dlgId the dlgId to set
	 */
	public void setDlgId(Integer dlgId) {
		this.dlgId = dlgId;
	}

	/**
	 * @return the dlgId
	 */
	public Integer getDlgId() {
		return dlgId;
	}

	/**
	 * @param dialoguePortionPresent the dialoguePortionPresent to set
	 */
	public void setDialoguePortionPresent(boolean dialoguePortionPresent) {
		this.dialoguePortionPresent = dialoguePortionPresent;
	}

	/**
	 * @return the dialoguePortionPresent
	 */
	public boolean isDialoguePortionPresent() {
		return dialoguePortionPresent;
	}

	/**
	 * @param appContextIdentifier the appContextIdentifier to set
	 */
	public void setAppContextIdentifier(Integer appContextIdentifier) {
		this.appContextIdentifier = appContextIdentifier;
	}

	/**
	 * @return the appContextIdentifier
	 */
	public Integer getAppContextIdentifier() {
		return appContextIdentifier;
	}

	/**
	 * @param appContextName the appContextName to set
	 */
	public void setAppContextName(byte[] appContextName) {
		this.appContextName = appContextName;
	}

	/**
	 * @return the appContextName
	 */
	public byte[] getAppContextName() {
		return appContextName;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	public void setProtocolVersion(Integer protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the protocolVersion
	 */
	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param securityContextIdentifier the securityContextIdentifier to set
	 */
	public void setSecurityContextIdentifier(Integer securityContextIdentifier) {
		this.securityContextIdentifier = securityContextIdentifier;
	}

	/**
	 * @return the securityContextIdentifier
	 */
	public Integer getSecurityContextIdentifier() {
		return securityContextIdentifier;
	}

	/**
	 * @param securityContextInfo the securityContextInfo to set
	 */
	public void setSecurityContextInfo(byte[] securityContextInfo) {
		this.securityContextInfo = securityContextInfo;
	}

	/**
	 * @return the securityContextInfo
	 */
	public byte[] getSecurityContextInfo() {
		return securityContextInfo;
	}

	/**
	 * @param userInfo the userInfo to set
	 */
	public void setUserInfo(byte[] userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * @return the userInfo
	 */
	public byte[] getUserInfo() {
		return userInfo;
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
	 * @param stateInfo the stateInfo to set
	 */
	public void setStateInfo(SampleAppCallStateInfo stateInfo) {
		this.stateInfo = stateInfo;
	}

	/**
	 * @return the stateInfo
	 */
	public SampleAppCallStateInfo getStateInfo() {
		return stateInfo;
	}

	/**
	 * @param cdr the cdr to set
	 */
	public void setCdr(StringBuilder cdr) {
		this.cdr = cdr;
	}

	/**
	 * @return the cdr
	 */
	public StringBuilder getCdr() {
		return cdr;
	}

	/**
	 * @param dpCount the dpCount to set
	 */
	public void setDpCount(int dpCount) {
		this.dpCount = dpCount;
	}

	/**
	 * @return the dpCount
	 */
	public int getDpCount() {
		return dpCount;
	}

	/**
	 * @param idpContent the idpContent to set
	 */
	public void setIdpContent(ParsedIdp idpContent) {
		this.idpContent = idpContent;
	}

	/**
	 * @return the idpContent
	 */
	public ParsedIdp getIdpContent() {
		return idpContent;
	}

	/**
	 * @param corrId the corrId to set
	 */
	public void setCorrId(int corrId) {
		this.corrId = corrId;
	}

	/**
	 * @return the corrId
	 */
	public int getCorrId() {
		return corrId;
	}

	/**
	 * @param assistingSspIp the assistingSspIp to set
	 */
	public void setAssistingSspIp(String assistingSspIp) {
		this.assistingSspIp = assistingSspIp;
	}

	/**
	 * @return the assistingSspIp
	 */
	public String getAssistingSspIp() {
		return assistingSspIp;
	}

	/**
	 * @param invokeId the invokeId to set
	 */
	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}

	/**
	 * @return the invokeId
	 */
	public int getInvokeId() {
		return invokeId;
	}

	public void incrementInvokeId() {
		invokeId++;		
	}

	/**
	 * @param etcTimer the etcTimer to set
	 */
	public void setEtcTimer(Timer etcTimer) {
		this.etcTimer = etcTimer;
	}

	/**
	 * @return the etcTimer
	 */
	public Timer getEtcTimer() {
		return etcTimer;
	}

	/**
	 * @param appSession the appSession to set
	 */
	public void setAppSession(SipApplicationSession appSession) {
		this.appSession = appSession;
	}

	/**
	 * @return the appSession
	 */
	public SipApplicationSession getAppSession() {
		return appSession;
	}

	/**
	 * @param destinationRoutingAddress the destinationRoutingAddress to set
	 */
	public void setDestinationRoutingAddress(String destinationRoutingAddress) {
		this.destinationRoutingAddress = destinationRoutingAddress;
	}

	/**
	 * @return the destinationRoutingAddress
	 */
	public String getDestinationRoutingAddress() {
		return destinationRoutingAddress;
	}

	/**
	 * @param sipTcorrId the sipTcorrId to set
	 */
	public void setSipTcorrId(int sipTcorrId) {
		this.sipTcorrId = sipTcorrId;
	}

	/**
	 * @return the sipTcorrId
	 */
	public int getSipTcorrId() {
		return sipTcorrId;
	}

	/**
	 * @param conTimer the conTimer to set
	 */
	public void setConTimer(Timer conTimer) {
		this.conTimer = conTimer;
	}

	/**
	 * @return the conTimer
	 */
	public Timer getConTimer() {
		return conTimer;
	}

	/**
	 * @param originatingAddress the originatingAddress to set
	 */
	public void setOriginatingAddress(SccpUserAddress originatingAddress) {
		this.originatingAddress = originatingAddress;
	}

	/**
	 * @return the originatingAddress
	 */
	public SccpUserAddress getOriginatingAddress() {
		return originatingAddress;
	}

	/**
	 * @param destinationAddress the destinationAddress to set
	 */
	public void setDestinationAddress(SccpUserAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	/**
	 * @return the destinationAddress
	 */
	public SccpUserAddress getDestinationAddress() {
		return destinationAddress;
	}

	





	






	
}
