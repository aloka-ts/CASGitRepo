package com.agnity.map.datatypes;

import com.agnity.map.enumdata.DefaultCallHandlingMapEnum;
import com.agnity.map.enumdata.OBcsmTriggerDetectionPointMapEnum;

public class OBcsmCamelTDPDataMap {
	
	private OBcsmTriggerDetectionPointMapEnum oBcsmTdp;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddress;
	private DefaultCallHandlingMapEnum defaultCallHandling;
	//TODO: extension container
	
	/**
	 * 
	 */
	public OBcsmCamelTDPDataMap() {
	}
	
	
	/**
	 * @return the oBcsmTdp
	 */
	public OBcsmTriggerDetectionPointMapEnum getoBcsmTdp() {
		return oBcsmTdp;
	}
	/**
	 * @param oBcsmTdp the oBcsmTdp to set
	 */
	public void setoBcsmTdp(OBcsmTriggerDetectionPointMapEnum oBcsmTdp) {
		this.oBcsmTdp = oBcsmTdp;
	}
	/**
	 * @return the serviceKey
	 */
	public ServiceKeyMap getServiceKey() {
		return serviceKey;
	}
	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(ServiceKeyMap serviceKey) {
		this.serviceKey = serviceKey;
	}
	/**
	 * @return the gsmScfAddress
	 */
	public ISDNAddressStringMap getGsmScfAddress() {
		return gsmScfAddress;
	}
	/**
	 * @param gsmScfAddress the gsmScfAddress to set
	 */
	public void setGsmScfAddress(ISDNAddressStringMap gsmScfAddress) {
		this.gsmScfAddress = gsmScfAddress;
	}
	/**
	 * @return the defaultCallHandling
	 */
	public DefaultCallHandlingMapEnum getDefaultCallHandling() {
		return defaultCallHandling;
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
		return "OBcsmCamelTDPDataMap [oBcsmTdp=" + oBcsmTdp + ", serviceKey="
				+ serviceKey + ", gsmScfAddress=" + gsmScfAddress
				+ ", defaultCallHandling=" + defaultCallHandling + "]";
	}
	
	


}
