package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class OCsiMap {

	// Mandatory attribute
	private Collection<OBcsmCamelTDPDataMap> oBcsmCamelTdpDataList;
	
	// optional attribute
	private CamelCapabilityHandlingMapEnum camelCapabilityHandling;
	private boolean notificationToCse;
	private boolean csiActive;
	
	/**
	 * @param oBcsmCamelTdpDataList
	 */
	public OCsiMap(Collection<OBcsmCamelTDPDataMap> oBcsmCamelTdpDataList) {
		this.oBcsmCamelTdpDataList = oBcsmCamelTdpDataList;
	}
	//TODO: extension container
	
	/**
	 * @return the notificationToCse
	 */
	public boolean isNotificationToCse() {
		return notificationToCse;
	}
	/**
	 * @return the csiActive
	 */
	public boolean isCsiActive() {
		return csiActive;
	}
	/**
	 * @param notificationToCse the notificationToCse to set
	 */
	public void setNotificationToCse(boolean notificationToCse) {
		this.notificationToCse = notificationToCse;
	}
	/**
	 * @param csiActive the csiActive to set
	 */
	public void setCsiActive(boolean csiActive) {
		this.csiActive = csiActive;
	}
	/**
	 * @return the oBcsmCamelTdpDataList
	 */
	public Collection<OBcsmCamelTDPDataMap> getoBcsmCamelTdpDataList() {
		return oBcsmCamelTdpDataList;
	}
	/**
	 * @return the camelCapabilityHandling
	 */
	public CamelCapabilityHandlingMapEnum getCamelCapabilityHandling() {
		return camelCapabilityHandling;
	}
	/**
	 * @param oBcsmCamelTdpDataList the oBcsmCamelTdpDataList to set
	 */
	public void setoBcsmCamelTdpDataList(
			Collection<OBcsmCamelTDPDataMap> oBcsmCamelTdpDataList) {
		this.oBcsmCamelTdpDataList = oBcsmCamelTdpDataList;
	}
	/**
	 * @param camelCapabilityHandling the camelCapabilityHandling to set
	 */
	public void setCamelCapabilityHandling(
			CamelCapabilityHandlingMapEnum camelCapabilityHandling) {
		this.camelCapabilityHandling = camelCapabilityHandling;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OCsiMap [oBcsmCamelTdpDataList=" + oBcsmCamelTdpDataList
				+ ", camelCapabilityHandling=" + camelCapabilityHandling
				+ ", notificationToCse=" + notificationToCse + ", csiActive="
				+ csiActive + "]";
	}

}
