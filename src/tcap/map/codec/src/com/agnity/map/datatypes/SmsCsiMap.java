package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class SmsCsiMap {
	
	// optional attributes
	private Collection<SmsCamelTdpDataMap> smsCamelTdpDataList;
	private CamelCapabilityHandlingMapEnum camelCapabilityHandling;
	private boolean notificationToCse;
	private boolean csiActive;
	
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
	 * @return the smsCamelTdpDataList
	 */
	public Collection<SmsCamelTdpDataMap> getSmsCamelTdpDataList() {
		return smsCamelTdpDataList;
	}
	/**
	 * @return the camelCapabilityHandling
	 */
	public CamelCapabilityHandlingMapEnum getCamelCapabilityHandling() {
		return camelCapabilityHandling;
	}
	/**
	 * @param smsCamelTdpDataList the smsCamelTdpDataList to set
	 */
	public void setSmsCamelTdpDataList(
			Collection<SmsCamelTdpDataMap> smsCamelTdpDataList) {
		this.smsCamelTdpDataList = smsCamelTdpDataList;
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
		return "SmsCsiMap [smsCamelTdpDataList=" + smsCamelTdpDataList
				+ ", camelCapabilityHandling=" + camelCapabilityHandling
				+ ", notificationToCse=" + notificationToCse + ", csiActive="
				+ csiActive + "]";
	}

	
}
