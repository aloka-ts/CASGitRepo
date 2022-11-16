package com.agnity.map.datatypes;

public class ExtForwFeatureMap {

	// Mandatory attribute
	private ExtSsStatusMap ssStatus;
	
	// Optional Attribute
	private ExtBasicServiceCodeMap basicService;
	private ISDNAddressStringMap forwardedToNumber;
	private ISDNAddressStringMap forwardedToSubaddress;  //This needs to be Subaddressstring
	private ExtForwOptionsMap forwardingOptions;
	private Integer noReplyConditionTime;
	private FtnAddressStringMap longForwardedToNumber;
	
	public ExtForwFeatureMap(ExtSsStatusMap ssStatus) {
		this.ssStatus = ssStatus;
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
	 * @return the ssStatus
	 */
	public ExtSsStatusMap getSsStatus() {
		return ssStatus;
	}
	/**
	 * @return the forwardedToNumber
	 */
	public ISDNAddressStringMap getForwardedToNumber() {
		return forwardedToNumber;
	}
	/**
	 * @return the forwardedToSubaddress
	 */
	public ISDNAddressStringMap getForwardedToSubaddress() {
		return forwardedToSubaddress;
	}
	/**
	 * @return the forwardingOptions
	 */
	public ExtForwOptionsMap getForwardingOptions() {
		return forwardingOptions;
	}
	/**
	 * @return the noReplyConditionTime
	 */
	public Integer getNoReplyConditionTime() {
		return noReplyConditionTime;
	}
	/**
	 * @return the longForwardedToNumber
	 */
	public FtnAddressStringMap getLongForwardedToNumber() {
		return longForwardedToNumber;
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
	public void setForwardedToNumber(ISDNAddressStringMap forwardedToNumber) {
		this.forwardedToNumber = forwardedToNumber;
	}
	/**
	 * @param forwardedToSubaddress the forwardedToSubaddress to set
	 */
	public void setForwardedToSubaddress(ISDNAddressStringMap forwardedToSubaddress) {
		this.forwardedToSubaddress = forwardedToSubaddress;
	}
	/**
	 * @param forwardingOptions the forwardingOptions to set
	 */
	public void setForwardingOptions(ExtForwOptionsMap forwardingOptions) {
		this.forwardingOptions = forwardingOptions;
	}
	/**
	 * @param noReplyConditionTime the noReplyConditionTime to set
	 */
	public void setNoReplyConditionTime(Integer noReplyConditionTime) {
		this.noReplyConditionTime = noReplyConditionTime;
	}
	/**
	 * @param longForwardedToNumber the longForwardedToNumber to set
	 */
	public void setLongForwardedToNumber(FtnAddressStringMap longForwardedToNumber) {
		this.longForwardedToNumber = longForwardedToNumber;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtForwFeatureMap [ssStatus=" + ssStatus
				+ ", forwardedToNumber=" + forwardedToNumber
				+ ", forwardedToSubaddress=" + forwardedToSubaddress
				+ ", forwardingOptions=" + forwardingOptions
				+ ", noReplyConditionTime=" + noReplyConditionTime
				+ ", longForwardedToNumber=" + longForwardedToNumber + "]";
	}
	
}
