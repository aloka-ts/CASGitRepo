package com.agnity.map.datatypes;

import com.agnity.map.enumdata.DefaultCallHandlingMapEnum;
import com.agnity.map.enumdata.OBcsmTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.TBcsmTriggerDetectionPointMapEnum;

public class TBcsmCamelTDPDataMap {
	
	private TBcsmTriggerDetectionPointMapEnum tBcsmTdp;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddress;
	private DefaultCallHandlingMapEnum defaultCallHandling;
	//TODO: extension container
	/**
	 * @return the tBcsmTdp
	 */
	public TBcsmTriggerDetectionPointMapEnum gettBcsmTdp() {
		return tBcsmTdp;
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
	 * @param tBcsmTdp the tBcsmTdp to set
	 */
	public void settBcsmTdp(TBcsmTriggerDetectionPointMapEnum tBcsmTdp) {
		this.tBcsmTdp = tBcsmTdp;
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
		return "TBcsmCamelTDPDataMap [tBcsmTdp=" + tBcsmTdp + ", serviceKey="
				+ serviceKey + ", gsmScfAddress=" + gsmScfAddress
				+ ", defaultCallHandling=" + defaultCallHandling + "]";
	}

	

}
