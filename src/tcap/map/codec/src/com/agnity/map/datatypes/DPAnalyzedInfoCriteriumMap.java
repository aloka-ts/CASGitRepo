package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;
import com.agnity.map.enumdata.DefaultCallHandlingMapEnum;

public class DPAnalyzedInfoCriteriumMap {

	//Mandatory attributes
	private ISDNAddressStringMap dialledNumber;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddress;
	private DefaultCallHandlingMapEnum defaultCallHandling;
	//TODO: extensionContainer
	
	/**
	 * @return the dialledNumber
	 */
	public ISDNAddressStringMap getDialledNumber() {
		return dialledNumber;
	}
	/**
	 * @param dialledNumber
	 * @param serviceKey
	 * @param gsmScfAddress
	 * @param defaultCallHandling
	 */
	public DPAnalyzedInfoCriteriumMap(ISDNAddressStringMap dialledNumber,
			ServiceKeyMap serviceKey, ISDNAddressStringMap gsmScfAddress,
			DefaultCallHandlingMapEnum defaultCallHandling) {
		this.dialledNumber = dialledNumber;
		this.serviceKey = serviceKey;
		this.gsmScfAddress = gsmScfAddress;
		this.defaultCallHandling = defaultCallHandling;
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
	 * @return the defaultCallHandling
	 */
	public DefaultCallHandlingMapEnum getDefaultCallHandling() {
		return defaultCallHandling;
	}
	/**
	 * @param dialledNumber the dialledNumber to set
	 */
	public void setDialledNumber(ISDNAddressStringMap dialledNumber) {
		this.dialledNumber = dialledNumber;
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
	 * @param defaultCallHandling the defaultCallHandling to set
	 */
	public void setDefaultCallHandling(
			DefaultCallHandlingMapEnum defaultCallHandling) {
		this.defaultCallHandling = defaultCallHandling;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DPAnalyzedInfoCriteriumMap [dialledNumber=" + dialledNumber
				+ ", serviceKey=" + serviceKey + ", gsmScfAddress="
				+ gsmScfAddress + ", defaultCallHandling="
				+ defaultCallHandling + "]";
	}
	
	
	
}
