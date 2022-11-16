package com.agnity.map.datatypes;

public class ExtCallBarringFeatureMap {
	
	// Mandatory attribute
	private ExtSsStatusMap ssStatus;
	
	// Optional attribute
	private ExtBasicServiceCodeMap basicService;
	//TODO: ExtensionContainer

	public ExtCallBarringFeatureMap(ExtSsStatusMap ssStatus) {
		this.ssStatus = ssStatus;
	}
	
	/**
	 * @return the ssStatus
	 */
	public ExtSsStatusMap getSsStatus() {
		return ssStatus;
	}
	/**
	 * @return the basicService
	 */
	public ExtBasicServiceCodeMap getBasicService() {
		return basicService;
	}
	/**
	 * @param ssStatus the ssStatus to set
	 */
	public void setSsStatus(ExtSsStatusMap ssStatus) {
		this.ssStatus = ssStatus;
	}
	/**
	 * @param basicService the basicService to set
	 */
	public void setBasicService(ExtBasicServiceCodeMap basicService) {
		this.basicService = basicService;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtCallBarringFeatureMap [ssStatus=" + ssStatus
				+ ", basicService=" + basicService + "]";
	}
}
