package com.agnity.simulator.domainobjects;

public class SimulatorConfig {

	private int initialCps;
	
	private int maxCps;
	
	private int cpsIncrementFreq;
	
	private int cpsIncremntValue;
	
	private int totalCalls;
	
	private int publishingTime=0;
	
	private String inviteRespTcapMessage;
	
	private String infoTcapMessage;
	
	private String remotePc;
	
	private String remoteSsn;
	
	private String localPc;
	
	private String localSsn;
	
	private String[] serviceKey;
	
	private int protocolVariant;
	
	private String sdp;
	
	//private String definfocontent;
	
	private boolean enableInc;
	
	private boolean enableB2b;
			
	private String callFlowFileNames;
	
	private String activityTestTimeout;
	
	private String rsnRsaTimeout;
		
	private String tcapSessionTimeout;
	
	private boolean isSendSccpConfigWithInfo;
	
	private String delaySccpConfigWithInfo;
	
	private String activityTestResponse;
	
	private String uabortInfoFixedPart;
	
	private String applicationContextName;
	
	
	
	/**
	 * @return the activityTestResponse
	 */
	public String getActivityTestResponse() {
		return activityTestResponse;
	}

	/**
	 * @param activityTestResponse the activityTestResponse to set
	 */
	public void setActivityTestResponse(String activityTestResponse) {
		this.activityTestResponse = activityTestResponse;
	}

	/**
	 * @return the remotePc
	 */
	public String getRemotePc() {
		return remotePc;
	}

	/**
	 * @param remotePc the remotePc to set
	 */
	public void setRemotePc(String remotePc) {
		this.remotePc = remotePc;
	}

	/**
	 * @return the remoteSsn
	 */
	public String getRemoteSsn() {
		return remoteSsn;
	}

	/**
	 * @param remoteSsn the remoteSsn to set
	 */
	public void setRemoteSsn(String remoteSsn) {
		this.remoteSsn = remoteSsn;
	}

	/**
	 * @return the localPc
	 */
	public String getLocalPc() {
		return localPc;
	}

	/**
	 * @param localPc the localPc to set
	 */
	public void setLocalPc(String localPc) {
		this.localPc = localPc;
	}

	/**
	 * @return the localSsn
	 */
	public String getLocalSsn() {
		return localSsn;
	}

	/**
	 * @param localSsn the localSsn to set
	 */
	public void setLocalSsn(String localSsn) {
		this.localSsn = localSsn;
	}

	/**
	 * @return the initialCps
	 */
	public int getInitialCps() {
		return initialCps;
	}

	/**
	 * @param initialCps the initialCps to set
	 */
	public void setInitialCps(int initialCps) {
		this.initialCps = initialCps;
	}

	/**
	 * @return the maxCps
	 */
	public int getMaxCps() {
		return maxCps;
	}

	/**
	 * @param maxCps the maxCps to set
	 */
	public void setMaxCps(int maxCps) {
		this.maxCps = maxCps;
	}

	/**
	 * @return the cpsIncrementFreq
	 */
	public int getCpsIncrementFreq() {
		return cpsIncrementFreq;
	}

	/**
	 * @param cpsIncrementFreq the cpsIncrementFreq to set
	 */
	public void setCpsIncrementFreq(int cpsIncrementFreq) {
		this.cpsIncrementFreq = cpsIncrementFreq;
	}

	/**
	 * @return the cpsIncremntValue
	 */
	public int getCpsIncremntValue() {
		return cpsIncremntValue;
	}

	/**
	 * @param cpsIncremntValue the cpsIncremntValue to set
	 */
	public void setCpsIncremntValue(int cpsIncremntValue) {
		this.cpsIncremntValue = cpsIncremntValue;
	}

	/**
	 * @return the totalCalls
	 */
	public int getTotalCalls() {
		return totalCalls;
	}

	/**
	 * @param totalCalls the totalCalls to set
	 */
	public void setTotalCalls(int totalCalls) {
		this.totalCalls = totalCalls;
	}

	/**
	 * @return the inviteRespTcapmessage
	 */
	public String getInviteRespTcapMessage() {
		return inviteRespTcapMessage;
	}

	/**
	 * @param inviteRespTcapmessage the inviteRespTcapmessage to set
	 */
	public void setInviteRespTcapMessage(String inviteRespTcapMessage) {
		this.inviteRespTcapMessage = inviteRespTcapMessage;
	}

	/**
	 * @return the infoTcapMessage
	 */
	public String getInfoTcapMessage() {
		return infoTcapMessage;
	}

	/**
	 * @param infoTcapMessage the infoTcapMessage to set
	 */
	public void setInfoTcapMessage(String infoTcapMessage) {
		this.infoTcapMessage = infoTcapMessage;
	}

	/**
	 * @return the serviceKey
	 */
	public String[] getServiceKey() {
		return serviceKey;
	}

	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(String[] serviceKey) {
		this.serviceKey = serviceKey;
	}

	/**
	 * @param protocolVariant the protocolVariant to set
	 */
	public void setProtocolVariant(int protocolVariant) {
		this.protocolVariant = protocolVariant;
	}

	/**
	 * @return the protocolVariant
	 */
	public int getProtocolVariant() {
		return protocolVariant;
	}

	/**
	 * @param sdp the sdp to set
	 */
	public void setSdp(String sdp) {
		this.sdp = sdp;
	}

	/**
	 * @return the sdp
	 */
	public String getSdp() {
		return sdp;
	}
	
	/**
	 * @param sdp the sdp to set
	 */
	/*public void setDefInfoContent(String definfocontent) {
		this.definfocontent = definfocontent;
	}
*/
	/**
	 * @return the sdp
	 */
	/*public String getDefInfoContent() {
		return definfocontent;
	}
*/
	/**
	 * @param enableInc the enableInc to set
	 */
	public void setEnableInc(boolean enableInc) {
		this.enableInc = enableInc;
	}

	/**
	 * @return the enableInc
	 */
	public boolean isEnableInc() {
		return enableInc;
	}
	
	/**
	 * @param enableB2b the enableB2b to set
	 */
	public void setEnableB2b(boolean enableB2b) {
		this.enableB2b = enableB2b;
	}

	/**
	 * @return the enableB2b
	 */
	public boolean isEnableB2b() {
		return enableB2b;
	}


	/**
	 * @param callFlowFileNames the callFlowFileNames to set
	 */
	public void setCallFlowFileNames(String callFlowFileNames) {
		this.callFlowFileNames = callFlowFileNames;
	}

	/**
	 * @return the callFlowFileNames
	 */
	public String getCallFlowFileNames() {
		return callFlowFileNames;
	}

	/**
	 * @param activityTestTimeout the activityTestTimeout to set
	 */
	public void setActivityTestTimeout(String activityTestTimeout) {
		this.activityTestTimeout = activityTestTimeout;
	}

	/**
	 * @return the activityTestTimeout
	 */
	public String getActivityTestTimeout() {
		return activityTestTimeout;
	}

	/**
	 * @param tcapSessionTimeout the tcapSessionTimeout to set
	 */
	public void setTcapSessionTimeout(String tcapSessionTimeout) {
		this.tcapSessionTimeout = tcapSessionTimeout;
	}

	/**
	 * @return the tcapSessionTimeout
	 */
	public String getTcapSessionTimeout() {
		return tcapSessionTimeout;
	}

	/**
	 * @param isSendSccpConfigWithInfo the isSendSccpConfigWithInfo to set
	 */
	public void setSendSccpConfigWithInfo(boolean isSendSccpConfigWithInfo) {
		this.isSendSccpConfigWithInfo = isSendSccpConfigWithInfo;
	}

	/**
	 * @return the isSendSccpConfigWithInfo
	 */
	public boolean isSendSccpConfigWithInfo() {
		return isSendSccpConfigWithInfo;
	}

	/**
	 * @param delaySccpConfigWithInfo the delaySccpConfigWithInfo to set
	 */
	public void setDelaySccpConfigWithInfo(String delaySccpConfigWithInfo) {
		this.delaySccpConfigWithInfo = delaySccpConfigWithInfo;
	}

	/**
	 * @return the delaySccpConfigWithInfo
	 */
	public String getDelaySccpConfigWithInfo() {
		return delaySccpConfigWithInfo;
	}
	
	/**
	 * @return the rsnRsaTimeout
	 */
	public String getRsnRsaTimeout() {
		return rsnRsaTimeout;
	}

	/**
	 * @param rsnRsaTimeout to set
	 */
	public void setRsnRsaTimeout(String rsnRsaTimeout) {
		this.rsnRsaTimeout = rsnRsaTimeout;
	}

	/**
	 * @param uabortInfoFixedPart the uabortInfoFixedPart to set
	 */
	public void setUabortInfoFixedPart(String uabortInfoFixedPart) {
		this.uabortInfoFixedPart = uabortInfoFixedPart;
	}

	/**
	 * @return the uabortInfoFixedPart
	 */
	public String getUabortInfoFixedPart() {
		return uabortInfoFixedPart;
	}
	
	/**
	 * @return the publishingTime
	 */
	public int getPublishingTime() {
		return publishingTime;
	}

	/**
	 * @param publishingTime the publishingTime to set
	 */
	public void setPublishingTime(int publishingTime) {
		this.publishingTime = publishingTime;
	}

	/**
	 * @param applicationContextName the applicationContextName to set
	 */
	public void setApplicationContextName(String applicationContextName) {
		this.applicationContextName = applicationContextName;
	}

	/**
	 * @return the applicationContextName
	 */
	public String getApplicationContextName() {
		return applicationContextName;
	}

		
}
