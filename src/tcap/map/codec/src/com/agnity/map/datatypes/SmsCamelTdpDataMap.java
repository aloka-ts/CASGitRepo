package com.agnity.map.datatypes;

import com.agnity.map.enumdata.DefaultSmsHandlingMapEnum;
import com.agnity.map.enumdata.SmsTriggerDetectionPointMapEnum;

public class SmsCamelTdpDataMap {
	private SmsTriggerDetectionPointMapEnum smsTriggerDetectionPoint;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddr;
	private DefaultSmsHandlingMapEnum defaultSMSHandling;
	// TODO: ExtensionContainer
	/**
	 * @return the smsTriggerDetectionPoint
	 */
	public SmsTriggerDetectionPointMapEnum getSmsTriggerDetectionPoint() {
		return smsTriggerDetectionPoint;
	}
	/**
	 * @return the serviceKey
	 */
	public ServiceKeyMap getServiceKey() {
		return serviceKey;
	}
	/**
	 * @return the gsmScfAddr
	 */
	public ISDNAddressStringMap getGsmScfAddr() {
		return gsmScfAddr;
	}
	/**
	 * @return the defaultSMSHandling
	 */
	public DefaultSmsHandlingMapEnum getDefaultSMSHandling() {
		return defaultSMSHandling;
	}
	/**
	 * @param smsTriggerDetectionPoint the smsTriggerDetectionPoint to set
	 */
	public void setSmsTriggerDetectionPoint(
			SmsTriggerDetectionPointMapEnum smsTriggerDetectionPoint) {
		this.smsTriggerDetectionPoint = smsTriggerDetectionPoint;
	}
	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(ServiceKeyMap serviceKey) {
		this.serviceKey = serviceKey;
	}
	/**
	 * @param gsmScfAddr the gsmScfAddr to set
	 */
	public void setGsmScfAddr(ISDNAddressStringMap gsmScfAddr) {
		this.gsmScfAddr = gsmScfAddr;
	}
	/**
	 * @param defaultSMSHandling the defaultSMSHandling to set
	 */
	public void setDefaultSMSHandling(DefaultSmsHandlingMapEnum defaultSMSHandling) {
		this.defaultSMSHandling = defaultSMSHandling;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SmsCamelTdpDataMap [smsTriggerDetectionPoint="
				+ smsTriggerDetectionPoint + ", serviceKey=" + serviceKey
				+ ", gsmScfAddr=" + gsmScfAddr + ", defaultSMSHandling="
				+ defaultSMSHandling + "]";
	}
	
	
	
}
