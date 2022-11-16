package com.agnity.map.datatypes;

import com.agnity.map.enumdata.AddlRequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.ModificationInstructionMapEnum;
import com.agnity.map.enumdata.RequestedCAMELSubscriptionInfoMapEnum;

public class ModificationRequestForCsiInfoMap {
	
	// Mandatory attribute
	private RequestedCAMELSubscriptionInfoMapEnum requestedCamelSubscriptionInfo;
	
	// Optional attribute
	//TODO: ExtensionContainer
	private ModificationInstructionMapEnum modifyNotificationToCSE;
	private ModificationInstructionMapEnum modifyCSIState;
	private AddlRequestedCAMELSubscriptionInfoMapEnum addlRequestedCAMELSubscriptionInfo;
	
	/**
	 * @param requestedCamelSubscriptionInfo
	 */
	public ModificationRequestForCsiInfoMap(
			RequestedCAMELSubscriptionInfoMapEnum requestedCamelSubscriptionInfo) {
		this.requestedCamelSubscriptionInfo = requestedCamelSubscriptionInfo;
	}
	/**
	 * @return the requestedCamelSubscriptionInfo
	 */
	public RequestedCAMELSubscriptionInfoMapEnum getRequestedCamelSubscriptionInfo() {
		return requestedCamelSubscriptionInfo;
	}
	/**
	 * @return the modifyNotificationToCSE
	 */
	public ModificationInstructionMapEnum getModifyNotificationToCSE() {
		return modifyNotificationToCSE;
	}
	/**
	 * @return the modifyCSIState
	 */
	public ModificationInstructionMapEnum getModifyCSIState() {
		return modifyCSIState;
	}
	/**
	 * @return the addlRequestedCAMELSubscriptionInfo
	 */
	public AddlRequestedCAMELSubscriptionInfoMapEnum getAddlRequestedCAMELSubscriptionInfo() {
		return addlRequestedCAMELSubscriptionInfo;
	}
	/**
	 * @param requestedCamelSubscriptionInfo the requestedCamelSubscriptionInfo to set
	 */
	public void setRequestedCamelSubscriptionInfo(
			RequestedCAMELSubscriptionInfoMapEnum requestedCamelSubscriptionInfo) {
		this.requestedCamelSubscriptionInfo = requestedCamelSubscriptionInfo;
	}
	/**
	 * @param modifyNotificationToCSE the modifyNotificationToCSE to set
	 */
	public void setModifyNotificationToCSE(
			ModificationInstructionMapEnum modifyNotificationToCSE) {
		this.modifyNotificationToCSE = modifyNotificationToCSE;
	}
	/**
	 * @param modifyCSIState the modifyCSIState to set
	 */
	public void setModifyCSIState(ModificationInstructionMapEnum modifyCSIState) {
		this.modifyCSIState = modifyCSIState;
	}
	/**
	 * @param addlRequestedCAMELSubscriptionInfo the addlRequestedCAMELSubscriptionInfo to set
	 */
	public void setAddlRequestedCAMELSubscriptionInfo(
			AddlRequestedCAMELSubscriptionInfoMapEnum addlRequestedCAMELSubscriptionInfo) {
		this.addlRequestedCAMELSubscriptionInfo = addlRequestedCAMELSubscriptionInfo;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModificationRequestForCsiInfoMap [requestedCamelSubscriptionInfo="
				+ requestedCamelSubscriptionInfo
				+ ", modifyNotificationToCSE="
				+ modifyNotificationToCSE
				+ ", modifyCSIState="
				+ modifyCSIState
				+ ", addlRequestedCAMELSubscriptionInfo="
				+ addlRequestedCAMELSubscriptionInfo + "]";
	}
	

}
