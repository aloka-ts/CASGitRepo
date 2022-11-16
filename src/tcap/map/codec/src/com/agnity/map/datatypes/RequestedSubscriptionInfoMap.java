package com.agnity.map.datatypes;

import com.agnity.map.enumdata.AddlRequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.RequestedCAMELSubscriptionInfoMapEnum;

public class RequestedSubscriptionInfoMap {

	private SsForBSCodeMap ssforBSCode;
	private RequestedCAMELSubscriptionInfoMapEnum reqCAMELSubsInfo;
	private AddlRequestedCAMELSubscriptionInfoMapEnum addlReqCAMELSubsInfo;
	
	
	public RequestedSubscriptionInfoMap() {
		
	}


	/**
	 * @return the ssforBSCode
	 */
	public SsForBSCodeMap getSsforBSCode() {
		return ssforBSCode;
	}


	/**
	 * @param ssforBSCode the ssforBSCode to set
	 */
	public void setSsforBSCode(SsForBSCodeMap ssforBSCode) {
		this.ssforBSCode = ssforBSCode;
	}


	/**
	 * @return the reqCAMELSubsInfo
	 */
	public RequestedCAMELSubscriptionInfoMapEnum getReqCAMELSubsInfo() {
		return reqCAMELSubsInfo;
	}


	/**
	 * @param reqCAMELSubsInfo the reqCAMELSubsInfo to set
	 */
	public void setReqCAMELSubsInfo(
			RequestedCAMELSubscriptionInfoMapEnum reqCAMELSubsInfo) {
		this.reqCAMELSubsInfo = reqCAMELSubsInfo;
	}


	/**
	 * @return the addlReqCAMELSubsInfo
	 */
	public AddlRequestedCAMELSubscriptionInfoMapEnum getAddlReqCAMELSubsInfo() {
		return addlReqCAMELSubsInfo;
	}


	/**
	 * @param addlReqCAMELSubsInfo the addlReqCAMELSubsInfo to set
	 */
	public void setAddlReqCAMELSubsInfo(
			AddlRequestedCAMELSubscriptionInfoMapEnum addlReqCAMELSubsInfo) {
		this.addlReqCAMELSubsInfo = addlReqCAMELSubsInfo;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestedSubscriptionInfoMap [ssforBSCode=" + ssforBSCode
				+ ", reqCAMELSubsInfo=" + reqCAMELSubsInfo
				+ ", addlReqCAMELSubsInfo=" + addlReqCAMELSubsInfo + "]";
	}
	
	
}
