package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class TCsiMap {
	// Mandatory attribute
	private Collection<TBcsmCamelTDPDataMap> tBcsmCamelTDPDataList;
	
	// optional attribute
	private CamelCapabilityHandlingMapEnum camelCapabilityHandling;
	/**
	 * @param tBcsmCamelTDPDataList
	 */
	public TCsiMap(Collection<TBcsmCamelTDPDataMap> tBcsmCamelTDPDataList) {
		this.tBcsmCamelTDPDataList = tBcsmCamelTDPDataList;
	}
	private boolean csiActive;
	private boolean notificationToCse;
	
	/**
	 * @return the csiActive
	 */
	public boolean isCsiActive() {
		return csiActive;
	}
	/**
	 * @return the notificationToCse
	 */
	public boolean isNotificationToCse() {
		return notificationToCse;
	}
	/**
	 * @param csiActive the csiActive to set
	 */
	public void setCsiActive(boolean csiActive) {
		this.csiActive = csiActive;
	}
	/**
	 * @param notificationToCse the notificationToCse to set
	 */
	public void setNotificationToCse(boolean notificationToCse) {
		this.notificationToCse = notificationToCse;
	}
	//TODO: extension container
	/**
	 * @return the tBcsmCamelTDPDataList
	 */
	public Collection<TBcsmCamelTDPDataMap> gettBcsmCamelTDPDataList() {
		return tBcsmCamelTDPDataList;
	}
	/**
	 * @return the camelCapabilityHandling
	 */
	public CamelCapabilityHandlingMapEnum getCamelCapabilityHandling() {
		return camelCapabilityHandling;
	}
	/**
	 * @param tBcsmCamelTDPDataList the tBcsmCamelTDPDataList to set
	 */
	public void settBcsmCamelTDPDataList(
			Collection<TBcsmCamelTDPDataMap> tBcsmCamelTDPDataList) {
		this.tBcsmCamelTDPDataList = tBcsmCamelTDPDataList;
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
		return "TCsiMap [tBcsmCamelTDPDataList=" + tBcsmCamelTDPDataList
				+ ", camelCapabilityHandling=" + camelCapabilityHandling
				+ ", csiActive=" + csiActive + ", notificationToCse="
				+ notificationToCse + "]";
	}
	
}
