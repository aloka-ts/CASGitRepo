package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;
import com.agnity.map.enumdata.GprsTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.MmCodeMapEnum;

public class GprsCsiMap {

	// optional attributes
	private Collection<GprsCamelTdpDataMap> gprsCamelTDPDataList;
	private CamelCapabilityHandlingMapEnum camelCapabilityHandling;
	private boolean csiActive;
	private boolean notificationToCse;
	/**
	 * @return the gprsCamelTDPDataList
	 */
	public Collection<GprsCamelTdpDataMap> getGprsCamelTDPDataList() {
		return gprsCamelTDPDataList;
	}
	/**
	 * @return the camelCapabilityHandling
	 */
	public CamelCapabilityHandlingMapEnum getCamelCapabilityHandling() {
		return camelCapabilityHandling;
	}
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
	 * @param gprsCamelTDPDataList the gprsCamelTDPDataList to set
	 */
	public void setGprsCamelTDPDataList(
			Collection<GprsCamelTdpDataMap> gprsCamelTDPDataList) {
		this.gprsCamelTDPDataList = gprsCamelTDPDataList;
	}
	/**
	 * @param camelCapabilityHandling the camelCapabilityHandling to set
	 */
	public void setCamelCapabilityHandling(
			CamelCapabilityHandlingMapEnum camelCapabilityHandling) {
		this.camelCapabilityHandling = camelCapabilityHandling;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GprsCsiMap [gprsCamelTDPDataList=" + gprsCamelTDPDataList
				+ ", camelCapabilityHandling=" + camelCapabilityHandling
				+ ", csiActive=" + csiActive + ", notificationToCse="
				+ notificationToCse + "]";
	}
}
