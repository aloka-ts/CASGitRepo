package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ModificationInstructionMapEnum;

public class ModificationRequestForCfInfoMap {
	
	// Mandatory parameter 
	private SsCodeMap sscode;
	
	// optional parameter
	private ExtBasicServiceCodeMap basicService;
	private ExtSsStatusMap ssStatus;
	private AddressStringMap forwardedToNumber;
	private AddressStringMap forwardedToSubaddress;
	private Integer noReplyConditionTime;
	private ModificationInstructionMapEnum modifyNotificationToCSE;
	// TODO:
	// private ExtensionContainerMap 
	
	
	/**
	 * @param sscode
	 */
	public ModificationRequestForCfInfoMap(SsCodeMap sscode) {
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
	 * @return the forwardedToNumber
	 */
	public AddressStringMap getForwardedToNumber() {
		return forwardedToNumber;
	}
	/**
	 * @return the forwardedToSubaddress
	 */
	public AddressStringMap getForwardedToSubaddress() {
		return forwardedToSubaddress;
	}
	/**
	 * @return the noReplyConditionTime
	 */
	public Integer getNoReplyConditionTime() {
		return noReplyConditionTime;
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
	 * @param forwardedToNumber the forwardedToNumber to set
	 */
	public void setForwardedToNumber(AddressStringMap forwardedToNumber) {
		this.forwardedToNumber = forwardedToNumber;
	}
	/**
	 * @param forwardedToSubaddress the forwardedToSubaddress to set
	 */
	public void setForwardedToSubaddress(AddressStringMap forwardedToSubaddress) {
		this.forwardedToSubaddress = forwardedToSubaddress;
	}
	/**
	 * @param noReplyConditionTime the noReplyConditionTime to set
	 */
	public void setNoReplyConditionTime(Integer noReplyConditionTime) {
		this.noReplyConditionTime = noReplyConditionTime;
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
		return "ModificationRequestForCfInfoMap [sscode=" + sscode
				+ ", ssStatus=" + ssStatus 
				+ ", basicService=" + basicService + ", forwardedToNumber="
				+ forwardedToNumber + ", forwardedToSubaddress="
				+ forwardedToSubaddress + ", noReplyConditionTime="
				+ noReplyConditionTime + ", modifyNotificationToCSE="
				+ modifyNotificationToCSE + "]";
	}
	
	

}
