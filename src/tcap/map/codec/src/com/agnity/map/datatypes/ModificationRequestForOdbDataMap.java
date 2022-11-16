package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ModificationInstructionMapEnum;

public class ModificationRequestForOdbDataMap {

	// Optional attributes
	
	//TODO: Extensioncontainer
	private ODBDataMap odbData;
	private ModificationInstructionMapEnum modifyNotificationToCSE;
	/**
	 * @return the odbData
	 */
	public ODBDataMap getOdbData() {
		return odbData;
	}
	/**
	 * @return the modifyNotificationToCSE
	 */
	public ModificationInstructionMapEnum getModifyNotificationToCSE() {
		return modifyNotificationToCSE;
	}
	/**
	 * @param odbData the odbData to set
	 */
	public void setOdbData(ODBDataMap odbData) {
		this.odbData = odbData;
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
		return "ModificationRequestForOdbDataMap [odbData=" + odbData
				+ ", modifyNotificationToCSE=" + modifyNotificationToCSE + "]";
	}
	
	
}
