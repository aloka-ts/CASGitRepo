package com.agnity.map.datatypes;

import java.util.Collection;

import org.apache.log4j.*;

public class AnyTimeSubscriptionInterrogationResMap {
	
	// Optional attributes
	private CallForwardingDataMap callForwardingData;
	private CallBarringDataMap callBarringData;
	private ODBInfoMap odbInfo;
	private CamelSubscriptionInfoMap camelSubscriptionInfo;
	private SupportedCamelPhasesMap supportedVlrCamelPhases;
	private SupportedCamelPhasesMap supportedSgsnCamelPhases;
	private OfferedCamel4CsiMap offeredCamel4CsisInVLR;
	private OfferedCamel4CsiMap offeredCamel4CsisInSgsn;
	private Collection<MsIsdnBsMap> msisdnBsList;
	
	/**
	 * TODO: CsgSubscriptionDataList, ExtensionContainer
	 */
	
	
	/**
	 * @return the callForwardingData
	 */
	public CallForwardingDataMap getCallForwardingData() {
		return callForwardingData;
	}
	/**
	 * @return the callBarringData
	 */
	public CallBarringDataMap getCallBarringData() {
		return callBarringData;
	}
	/**
	 * @return the odbInfo
	 */
	public ODBInfoMap getodbInfo() {
		return odbInfo;
	}
	/**
	 * @return the camelSubscriptionInfo
	 */
	public CamelSubscriptionInfoMap getCamelSubscriptionInfo() {
		return camelSubscriptionInfo;
	}
	/**
	 * @return the supportedVlrCamelPhases
	 */
	public SupportedCamelPhasesMap getSupportedVlrCamelPhases() {
		return supportedVlrCamelPhases;
	}
	/**
	 * @return the supportedSgsnCamelPhases
	 */
	public SupportedCamelPhasesMap getSupportedSgsnCamelPhases() {
		return supportedSgsnCamelPhases;
	}
	/**
	 * @return the offeredCamel4CsisInVLR
	 */
	public OfferedCamel4CsiMap getOfferedCamel4CsisInVLR() {
		return offeredCamel4CsisInVLR;
	}
	/**
	 * @return the offeredCamel4CsisInSgsn
	 */
	public OfferedCamel4CsiMap getOfferedCamel4CsisInSgsn() {
		return offeredCamel4CsisInSgsn;
	}
	/**
	 * @return the msisdnBsList
	 */
	public Collection<MsIsdnBsMap> getMsisdnBsList() {
		return msisdnBsList;
	}
	/**
	 * @param callForwardingData the callForwardingData to set
	 */
	public void setCallForwardingData(CallForwardingDataMap callForwardingData) {
		this.callForwardingData = callForwardingData;
	}
	/**
	 * @param callBarringData the callBarringData to set
	 */
	public void setCallBarringData(CallBarringDataMap callBarringData) {
		this.callBarringData = callBarringData;
	}
	/**
	 * @param odbInfo the odbInfo to set
	 */
	public void setodbInfo(ODBInfoMap odbInfo) {
		this.odbInfo = odbInfo;
	}
	/**
	 * @param camelSubscriptionInfo the camelSubscriptionInfo to set
	 */
	public void setCamelSubscriptionInfo(
			CamelSubscriptionInfoMap camelSubscriptionInfo) {
		this.camelSubscriptionInfo = camelSubscriptionInfo;
	}
	/**
	 * @param supportedVlrCamelPhases the supportedVlrCamelPhases to set
	 */
	public void setSupportedVlrCamelPhases(
			SupportedCamelPhasesMap supportedVlrCamelPhases) {
		this.supportedVlrCamelPhases = supportedVlrCamelPhases;
	}
	/**
	 * @param supportedSgsnCamelPhases the supportedSgsnCamelPhases to set
	 */
	public void setSupportedSgsnCamelPhases(
			SupportedCamelPhasesMap supportedSgsnCamelPhases) {
		this.supportedSgsnCamelPhases = supportedSgsnCamelPhases;
	}
	/**
	 * @param offeredCamel4CsisInVLR the offeredCamel4CsisInVLR to set
	 */
	public void setOfferedCamel4CsisInVLR(OfferedCamel4CsiMap offeredCamel4CsisInVLR) {
		this.offeredCamel4CsisInVLR = offeredCamel4CsisInVLR;
	}
	/**
	 * @param offeredCamel4CsisInSgsn the offeredCamel4CsisInSgsn to set
	 */
	public void setOfferedCamel4CsisInSgsn(
			OfferedCamel4CsiMap offeredCamel4CsisInSgsn) {
		this.offeredCamel4CsisInSgsn = offeredCamel4CsisInSgsn;
	}
	/**
	 * @param msisdnBsList the msisdnBsList to set
	 */
	public void setMsisdnBsList(Collection<MsIsdnBsMap> msisdnBsList) {
		this.msisdnBsList = msisdnBsList;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeSubscriptionInterrogationResMap [callForwardingData="
				+ callForwardingData + ", callBarringData=" + callBarringData
				+ ", odbInfo=" + odbInfo + ", camelSubscriptionInfo="
				+ camelSubscriptionInfo + ", supportedVlrCamelPhases="
				+ supportedVlrCamelPhases + ", supportedSgsnCamelPhases="
				+ supportedSgsnCamelPhases + ", offeredCamel4CsisInVLR="
				+ offeredCamel4CsisInVLR + ", offeredCamel4CsisInSgsn="
				+ offeredCamel4CsisInSgsn + ", msisdnBsList=" + msisdnBsList
				+ "]";
	}
	
	// TODO: CSG-SubscriptionDataList
	
	
	
}
