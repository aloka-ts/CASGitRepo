package com.agnity.map.datatypes;

public class ODBInfoMap {
	
	// Mandatory attribute
	private ODBDataMap odbData;
	
	// optional attribute
	/**
	 * TODO: 
	 * 1. notificationToCSE NULL OPTIONAL
	 * 2. ExtensionContainer
	 */

	/**
	 * @param odbData
	 */
	public ODBInfoMap(ODBDataMap odbData) {
		this.odbData = odbData;
	}

	public ODBInfoMap() {
	}

	/**
	 * @return the odbData
	 */
	public ODBDataMap getOdbData() {
		return odbData;
	}

	/**
	 * @param odbData the odbData to set
	 */
	public void setOdbData(ODBDataMap odbData) {
		this.odbData = odbData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ODBInfoMap [odbData=" + odbData + "]";
	}
}
