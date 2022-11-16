package com.agnity.map.datatypes;

import com.agnity.map.enumdata.DefaultCallHandlingMapEnum;
import com.agnity.map.enumdata.GprsTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.DefaultGPRSHandlingMapEnum;

public class GprsCamelTdpDataMap {
	
	//Mandatory attributes
	private GprsTriggerDetectionPointMapEnum gprsTriggerDetectionPoint;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddress;
	private DefaultGPRSHandlingMapEnum defaultSessionHandling;
	/**
	 * @param gprsTriggerDetectionPoint
	 * @param serviceKey
	 * @param gsmScfAddress
	 * @param defaultSessionHandling
	 */
	public GprsCamelTdpDataMap(
			GprsTriggerDetectionPointMapEnum gprsTriggerDetectionPoint,
			ServiceKeyMap serviceKey, ISDNAddressStringMap gsmScfAddress,
			DefaultGPRSHandlingMapEnum defaultSessionHandling) {
		this.gprsTriggerDetectionPoint = gprsTriggerDetectionPoint;
		this.serviceKey = serviceKey;
		this.gsmScfAddress = gsmScfAddress;
		this.defaultSessionHandling = defaultSessionHandling;
	}
	/**
	 * @return the gprsTriggerDetectionPoint
	 */
	public GprsTriggerDetectionPointMapEnum getGprsTriggerDetectionPoint() {
		return gprsTriggerDetectionPoint;
	}
	/**
	 * @return the serviceKey
	 */
	public ServiceKeyMap getServiceKey() {
		return serviceKey;
	}
	/**
	 * @return the gsmScfAddress
	 */
	public ISDNAddressStringMap getGsmScfAddress() {
		return gsmScfAddress;
	}
	/**
	 * @return the defaultSessionHandling
	 */
	public DefaultGPRSHandlingMapEnum getDefaultSessionHandling() {
		return defaultSessionHandling;
	}
	/**
	 * @param gprsTriggerDetectionPoint the gprsTriggerDetectionPoint to set
	 */
	public void setGprsTriggerDetectionPoint(
			GprsTriggerDetectionPointMapEnum gprsTriggerDetectionPoint) {
		this.gprsTriggerDetectionPoint = gprsTriggerDetectionPoint;
	}
	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(ServiceKeyMap serviceKey) {
		this.serviceKey = serviceKey;
	}
	/**
	 * @param gsmScfAddress the gsmScfAddress to set
	 */
	public void setGsmScfAddress(ISDNAddressStringMap gsmScfAddress) {
		this.gsmScfAddress = gsmScfAddress;
	}
	/**
	 * @param defaultSessionHandling the defaultSessionHandling to set
	 */
	public void setDefaultSessionHandling(
			DefaultGPRSHandlingMapEnum defaultSessionHandling) {
		this.defaultSessionHandling = defaultSessionHandling;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GprsCamelTdpDataMap [gprsTriggerDetectionPoint="
				+ gprsTriggerDetectionPoint + ", serviceKey=" + serviceKey
				+ ", gsmScfAddress=" + gsmScfAddress
				+ ", defaultSessionHandling=" + defaultSessionHandling + "]";
	}
}
