package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ModificationInstructionMapEnum;

public class ModificationRequestForCbInfoMap {
	
	// Mandatory attribute
	private SsCodeMap sscode;
	
	// Optional attribute
	//TODO: ExtensionContainer
	private ExtBasicServiceCodeMap basicService;
	private ExtSsStatusMap ssStatus;
	private String password;
	private Integer wrongPasswordAttemptsCounter;
	private ModificationInstructionMapEnum modifyNotificationToCSE;
	/**
	 * @param sscode
	 */
	public ModificationRequestForCbInfoMap(SsCodeMap sscode) {
		this.sscode = sscode;
	}
	
	
	/**
	 * @return the basicService
	 */
	public ExtBasicServiceCodeMap getBasicService() {
		return basicService;
	}


	/**
	 * @param basicService the basicService to set
	 */
	public void setBasicService(ExtBasicServiceCodeMap basicService) {
		this.basicService = basicService;
	}


	/**
	 * @return the sscode
	 */
	public SsCodeMap getSscode() {
		return sscode;
	}
	/**
	 * @return the ssStatus
	 */
	public ExtSsStatusMap getSsStatus() {
		return ssStatus;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the wrongPasswordAttemptsCounter
	 */
	public Integer getWrongPasswordAttemptsCounter() {
		return wrongPasswordAttemptsCounter;
	}
	/**
	 * @return the modifyNotificationToCSE
	 */
	public ModificationInstructionMapEnum getModifyNotificationToCSE() {
		return modifyNotificationToCSE;
	}
	/**
	 * @param sscode the sscode to set
	 */
	public void setSscode(SsCodeMap sscode) {
		this.sscode = sscode;
	}
	/**
	 * @param ssStatus the ssStatus to set
	 */
	public void setSsStatus(ExtSsStatusMap ssStatus) {
		this.ssStatus = ssStatus;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @param wrongPasswordAttemptsCounter the wrongPasswordAttemptsCounter to set
	 */
	public void setWrongPasswordAttemptsCounter(Integer wrongPasswordAttemptsCounter) {
		this.wrongPasswordAttemptsCounter = wrongPasswordAttemptsCounter;
	}
	/**
	 * @param modifyNotificationToCSE the modifyNotificationToCSE to set
	 */
	public void setModifyNotificationToCSE(
			ModificationInstructionMapEnum modifyNotificationToCSE) {
		this.modifyNotificationToCSE = modifyNotificationToCSE;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModificationRequestForCbInfoMap [sscode=" + sscode
				+ ", basicService = "+basicService 
				+ ", ssStatus=" + ssStatus + ", password=" + password
				+ ", wrongPasswordAttemptsCounter="
				+ wrongPasswordAttemptsCounter + ", modifyNotificationToCSE="
				+ modifyNotificationToCSE + "]";
	}
	
	
	
}
