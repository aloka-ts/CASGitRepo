package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ModificationInstructionMapEnum;

public class ModificationRequestForCsgMap {
	private ModificationInstructionMapEnum modifyNotificationToCSE;

	/**
	 * @return the modifyNotificationToCSE
	 */
	public ModificationInstructionMapEnum getModifyNotificationToCSE() {
		return modifyNotificationToCSE;
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
		return "ModificationRequestForCsgMap [getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	//TODO: ExtensionContainer
	
	
}
