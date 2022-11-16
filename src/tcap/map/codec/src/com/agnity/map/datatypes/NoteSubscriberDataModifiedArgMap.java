package com.agnity.map.datatypes;

/**
 * Refer: ETSI TS 129 002 V9.4.0 
 * Class to represent the argument to be sent for NSDM operation
 * 
 * The user of the class should check for null-ability for any object 
 * obtained through the getter method.
 * 
 * @author sanjay
 *
 */

public class NoteSubscriberDataModifiedArgMap {
	
	// Mandatory parameters
	private ImsiDataType imsi; 
	private ISDNAddressStringMap msisdn;

	// Optional parameters
	private ExtForwardingInfoForCSEMap forwardingInfoForCSE;
	private ExtCallBarringInfoForCSEMap callBarringInfoForCSE;
	private ODBInfoMap odbInfo;
	private CamelSubscriptionInfoMap csi;
	private RequestedNodesMap ueReachable;
	
	/**
	 * TODO:
	 *
	 * allInformationSent ASN NULL object
	 * Extension Container
	 * CSG-SubscriptionDataList
	 */
	
	public NoteSubscriberDataModifiedArgMap(ImsiDataType imsi, 
			ISDNAddressStringMap msisdn) {
		this.imsi = imsi;
		this.msisdn = msisdn;
	}
	
	/**
	 * @return the imsi
	 */
	public ImsiDataType getImsi() {
		return imsi;
	}
	/**
	 * @return the msisdn
	 */
	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}
	/**
	 * @return the forwardingInfoForCSE
	 */
	public ExtForwardingInfoForCSEMap getForwardingInfoForCSE() {
		return forwardingInfoForCSE;
	}
	/**
	 * @return the callBarringInfoForCSE
	 */
	public ExtCallBarringInfoForCSEMap getCallBarringInfoForCSE() {
		return callBarringInfoForCSE;
	}
	/**
	 * @return the odbInfo
	 */
	public ODBInfoMap getOdbInfo() {
		return odbInfo;
	}
	/**
	 * @return the csi
	 */
	public CamelSubscriptionInfoMap getCsi() {
		return csi;
	}
	/**
	 * @return the unReachable
	 */
	public RequestedNodesMap getUeReachable() {
		return ueReachable;
	}
	/**
	 * @param imsi the imsi to set
	 */
	public void setImsi(ImsiDataType imsi) {
		this.imsi = imsi;
	}
	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}
	/**
	 * @param forwardingInfoForCSE the forwardingInfoForCSE to set
	 */
	public void setForwardingInfoForCSE(
			ExtForwardingInfoForCSEMap forwardingInfoForCSE) {
		this.forwardingInfoForCSE = forwardingInfoForCSE;
	}
	/**
	 * @param callBarringInfoForCSE the callBarringInfoForCSE to set
	 */
	public void setCallBarringInfoForCSE(
			ExtCallBarringInfoForCSEMap callBarringInfoForCSE) {
		this.callBarringInfoForCSE = callBarringInfoForCSE;
	}
	/**
	 * @param odbInfo the odbInfo to set
	 */
	public void setOdbInfo(ODBInfoMap odbInfo) {
		this.odbInfo = odbInfo;
	}
	/**
	 * @param csi the csi to set
	 */
	public void setCsi(CamelSubscriptionInfoMap csi) {
		this.csi = csi;
	}
	/**
	 * @param unReachable the unReachable to set
	 */
	public void setUeReachable(RequestedNodesMap ueReachable) {
		this.ueReachable = ueReachable;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NoteSubscriberDataModifiedArgMap [imsi=" + imsi + ", msisdn="
				+ msisdn + ", forwardingInfoForCSE=" + forwardingInfoForCSE
				+ ", callBarringInfoForCSE=" + callBarringInfoForCSE
				+ ", odbInfo=" + odbInfo + ", csi=" + csi + ", unReachable="
				+ ueReachable + "]";
	}
}
