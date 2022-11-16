package com.agnity.map.datatypes;

import org.apache.log4j.*;

public class AnyTimeModificationResMap {
	
	// Optional attributes
	private ExtSsInfoForCseMap ssInfoForCse;
	private CamelSubscriptionInfoMap camelSubscriptionInfo;
	private ODBInfoMap odbInfo;

	
	/**
	 * TODO:
	 * ExtensionContainer 
	 */
	
	/**
	 * @return the ssInfoForCse
	 */
	public ExtSsInfoForCseMap getSsInfoForCse() {
		return ssInfoForCse;
	}
	/**
	 * @param ssInfoForCse the ssInfoForCse to set
	 */
	public void setSsInfoForCse(ExtSsInfoForCseMap ssInfoForCse) {
		this.ssInfoForCse = ssInfoForCse;
	}
	
	
	/**
	 * @return the ssInfoForCSE
	 */
	//public ExtSsInfoForCseMap getSsInfoForCSE() {
	//	return ssInfoForCSE;
	//}
	/**
	 * @return the camelSubscriptionInfo
	 */
	public CamelSubscriptionInfoMap getCamelSubscriptionInfo() {
		return camelSubscriptionInfo;
	}
	/**
	 * @return the odbInfo
	 */
	public ODBInfoMap getOdbInfo() {
		return odbInfo;
	}
	/**
	 * @param ssInfoForCSE the ssInfoForCSE to set
	 */
	//public void setSsInfoForCSE(ExtSsInfoForCseMap ssInfoForCSE) {
	//	this.ssInfoForCSE = ssInfoForCSE;
	//}
	/**
	 * @param camelSubscriptionInfo the camelSubscriptionInfo to set
	 */
	public void setCamelSubscriptionInfo(
			CamelSubscriptionInfoMap camelSubscriptionInfo) {
		this.camelSubscriptionInfo = camelSubscriptionInfo;
	}
	/**
	 * @param odbInfo the odbInfo to set
	 */
	public void setOdbInfo(ODBInfoMap odbInfo) {
		this.odbInfo = odbInfo;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeModificationResMap [ssInfoForCse=" + ssInfoForCse
				+ ", camelSubscriptionInfo=" + camelSubscriptionInfo
				+ ", odbInfo=" + odbInfo + "]";
	}
}
