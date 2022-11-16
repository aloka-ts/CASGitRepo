package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ModificationInstructionMapEnum;

public class ModificationRequestForIpSmGwDataMap {
	// Optional attributes
	private ModificationInstructionMapEnum modifyRegistrationStatus;
	//TODO: ExtensionContainer

	/**
	 * @return the modifyRegistrationStatus
	 */
	public ModificationInstructionMapEnum getModifyRegistrationStatus() {
		return modifyRegistrationStatus;
	}

	/**
	 * @param modifyRegistrationStatus the modifyRegistrationStatus to set
	 */
	public void setModifyRegistrationStatus(
			ModificationInstructionMapEnum modifyRegistrationStatus) {
		this.modifyRegistrationStatus = modifyRegistrationStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModificationRequestForIpSmGwDataMap [modifyRegistrationStatus="
				+ modifyRegistrationStatus + "]";
	}

}
