package com.agnity.map.datatypes;

public class ODBDataMap {
	// Mandatory attribute
	private ODBGeneralDataMap odbGeneralData;
	
	// optional attribute
	private ODBHplmnDataMap odbHplmnData;
	//TODO:
	//private ExtensionContainerMap extContainer;
	
	/**
	 * 
	 */
	public ODBDataMap(ODBGeneralDataMap odbGeneralData) {
		this.odbGeneralData = odbGeneralData;
	}

	/**
	 * @return the odbGeneralData
	 */
	public ODBGeneralDataMap getOdbGeneralData() {
		return odbGeneralData;
	}

	/**
	 * @param odbGeneralData the odbGeneralData to set
	 */
	public void setOdbGeneralData(ODBGeneralDataMap odbGeneralData) {
		this.odbGeneralData = odbGeneralData;
	}

	/**
	 * @return the odbHplmnData
	 */
	public ODBHplmnDataMap getOdbHplmnData() {
		return odbHplmnData;
	}

	/**
	 * @param odbHplmnData the odbHplmnData to set
	 */
	public void setOdbHplmnData(ODBHplmnDataMap odbHplmnData) {
		this.odbHplmnData = odbHplmnData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ODBDataMap [odbGeneralData=" + odbGeneralData
				+ ", odbHplmnData=" + odbHplmnData + "]";
	}
	
}
