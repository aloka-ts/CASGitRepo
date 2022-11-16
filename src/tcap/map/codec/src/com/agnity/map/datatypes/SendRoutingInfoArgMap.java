package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ForwardingReasonMapEnum;
import com.agnity.map.enumdata.InterrogationTypeEnumMap;
import com.agnity.map.enumdata.IstSupportIndicatorMapEnum;
import com.agnity.map.enumdata.SuppressMtssMapEnum;

public class SendRoutingInfoArgMap {
	// Mandatory attributes
	private ISDNAddressStringMap msisdn;
	private InterrogationTypeEnumMap interrogationType;
	private ISDNAddressStringMap gmscOrGsmScfAddress;
	
	// Optional attributes
	// TODO: CUG-CheckInfo
	// TODO: or-Interrogation 
	private Integer NumberOfForwarding; // min value: 1, max value 5
	private Integer orCapability; // MAX value 79
    // TODO: ccbs-Call is null
	// TODO: CallReferenceNumber
	private ForwardingReasonMapEnum forwardingReason;
	private ExtBasicServiceCodeMap basicServiceGroup;
	// TODO: ExternalSignalInfo
	// TODO: CamelInfo
	// TODO: SuppressionOfAnnouncement as its null
	private AlertingPatternDataType alertingPattern;
	// TODO: ccbs-Call as its null type
	private Integer callingPriority;
	private Integer supportedCCBSPhase;
	// TODO: Ext-ExternalSignalInfo
	private IstSupportIndicatorMapEnum istSupportIndicator;
	// TODO: pre-pagingSupported null type
	// TODO: CallDiversionTreatmentIndicator
	// TODO: longFTN-Supported is null type
	// TODO: suppress-VT-CSI
	// TODO: suppressIncomingCallBarring
	// TODO: gsmSCF-InitiatedCall
	private ExtBasicServiceCodeMap basicServiceGroup2;
	// TODO: ExternalSignalInfo
	private SuppressMtssMap suppressMTSS;
	// TODO: mtRoamingRetrySupported due to bn bug
	

	/**
	 * @return the interrogationType
	 */
	public InterrogationTypeEnumMap getInterrogationType() {
		return interrogationType;
	}

	/**
	 * @return the gmscOrGsmScfAddress
	 */
	public ISDNAddressStringMap getGmscOrGsmScfAddress() {
		return gmscOrGsmScfAddress;
	}

	/**
	 * @param msisdn
	 * @param interrogationType
	 * @param gmscOrGsmScfAddress
	 */
	public SendRoutingInfoArgMap(ISDNAddressStringMap msisdn,
			InterrogationTypeEnumMap interrogationType,
			ISDNAddressStringMap gmscOrGsmScfAddress) {
		this.msisdn = msisdn;
		this.interrogationType = interrogationType;
		this.gmscOrGsmScfAddress = gmscOrGsmScfAddress;
	}

	/**
	 * @param interrogationType the interrogationType to set
	 */
	public void setInterrogationType(InterrogationTypeEnumMap interrogationType) {
		this.interrogationType = interrogationType;
	}

	/**
	 * @param gmscOrGsmScfAddress the gmscOrGsmScfAddress to set
	 */
	public void setGmscOrGsmScfAddress(ISDNAddressStringMap gmscOrGsmScfAddress) {
		this.gmscOrGsmScfAddress = gmscOrGsmScfAddress;
	}

	/**
	 * @return the msisdn
	 */
	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SendRoutingInfoArgMap [msisdn=" + msisdn + "\n" 
				+ ", interrogationType=" + interrogationType + "\n" 
				+ ", gmscOrGsmScfAddress=" + gmscOrGsmScfAddress + "\n" 
				+ ", NumberOfForwarding=" + NumberOfForwarding + "\n" 
				+ ", orCapability=" + orCapability + "\n, gmscOrGsmScfaddress=" + "\n" 
				+ forwardingReason + ",\n basicServiceGroup=" + basicServiceGroup + "\n" 
				+ ", alertingPattern=" + alertingPattern + "\n, callingPriority=" + "\n" 
				+ callingPriority + "\n, supportedCCBSPhase=" + "\n" 
				+ supportedCCBSPhase + "\n, istSupportIndicator=" + "\n" 
				+ istSupportIndicator + "\n, basicServiceGroup2=" + "\n" 
				+ basicServiceGroup2 + "\n, suppressMTSS=" + suppressMTSS + "]";
	}

	/**
	 * @return the numberOfForwarding
	 */
	public Integer getNumberOfForwarding() {
		return NumberOfForwarding;
	}

	/**
	 * @return the orCapability
	 */
	public Integer getOrCapability() {
		return orCapability;
	}

	/**
	 * @return the forwardingReason
	 */
	public ForwardingReasonMapEnum getForwardingReason() {
		return forwardingReason;
	}

	/**
	 * @return the basicServiceGroup
	 */
	public ExtBasicServiceCodeMap getBasicServiceGroup() {
		return basicServiceGroup;
	}

	/**
	 * @return the alertingPattern
	 */
	public AlertingPatternDataType getAlertingPattern() {
		return alertingPattern;
	}

	/**
	 * @return the callingPriority
	 */
	public Integer getCallingPriority() {
		return callingPriority;
	}

	/**
	 * @return the supportedCCBSPhase
	 */
	public Integer getSupportedCCBSPhase() {
		return supportedCCBSPhase;
	}

	/**
	 * @return the istSupportIndicator
	 */
	public IstSupportIndicatorMapEnum getIstSupportIndicator() {
		return istSupportIndicator;
	}

	/**
	 * @return the basicServiceGroup2
	 */
	public ExtBasicServiceCodeMap getBasicServiceGroup2() {
		return basicServiceGroup2;
	}

	/**
	 * @return the suppressMTSS
	 */
	public SuppressMtssMap getSuppressMTSS() {
		return suppressMTSS;
	}

	/**
	 * @param numberOfForwarding the numberOfForwarding to set
	 */
	public void setNumberOfForwarding(Integer numberOfForwarding) {
		NumberOfForwarding = numberOfForwarding;
	}

	/**
	 * @param orCapability the orCapability to set
	 */
	public void setOrCapability(Integer orCapability) {
		this.orCapability = orCapability;
	}

	/**
	 * @param forwardingReason the forwardingReason to set
	 */
	public void setForwardingReason(ForwardingReasonMapEnum forwardingReason) {
		this.forwardingReason = forwardingReason;
	}

	/**
	 * @param basicServiceGroup the basicServiceGroup to set
	 */
	public void setBasicServiceGroup(ExtBasicServiceCodeMap basicServiceGroup) {
		this.basicServiceGroup = basicServiceGroup;
	}

	/**
	 * @param alertingPattern the alertingPattern to set
	 */
	public void setAlertingPattern(AlertingPatternDataType alertingPattern) {
		this.alertingPattern = alertingPattern;
	}

	/**
	 * @param callingPriority the callingPriority to set
	 */
	public void setCallingPriority(Integer callingPriority) {
		this.callingPriority = callingPriority;
	}

	/**
	 * @param supportedCCBSPhase the supportedCCBSPhase to set
	 */
	public void setSupportedCCBSPhase(Integer supportedCCBSPhase) {
		this.supportedCCBSPhase = supportedCCBSPhase;
	}

	/**
	 * @param istSupportIndicator the istSupportIndicator to set
	 */
	public void setIstSupportIndicator(
			IstSupportIndicatorMapEnum istSupportIndicator) {
		this.istSupportIndicator = istSupportIndicator;
	}

	/**
	 * @param basicServiceGroup2 the basicServiceGroup2 to set
	 */
	public void setBasicServiceGroup2(ExtBasicServiceCodeMap basicServiceGroup2) {
		this.basicServiceGroup2 = basicServiceGroup2;
	}

	/**
	 * @param suppressMTSS the suppressMTSS to set
	 */
	public void setSuppressMTSS(SuppressMtssMap suppressMTSS) {
		this.suppressMTSS = suppressMTSS;
	}
	
}
