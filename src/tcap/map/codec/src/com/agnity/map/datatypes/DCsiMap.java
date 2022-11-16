package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class DCsiMap {
	private Collection<DPAnalyzedInfoCriteriumMap> dpAnalysedInfoCriteriaList;
	private CamelCapabilityHandlingMapEnum camelCapabilityHandling;
	private boolean notificationToCse;
	private boolean csiActive;
	
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
	 * @return the dpAnalysedInfoCriteriaList
	 */
	public Collection<DPAnalyzedInfoCriteriumMap> getDpAnalysedInfoCriteriaList() {
		return dpAnalysedInfoCriteriaList;
	}
	/**
	 * @return the camelCapabilityHandling
	 */
	public CamelCapabilityHandlingMapEnum getCamelCapabilityHandling() {
		return camelCapabilityHandling;
	}
	/**
	 * @param dpAnalysedInfoCriteriaList the dpAnalysedInfoCriteriaList to set
	 */
	public void setDpAnalysedInfoCriteriaList(
			Collection<DPAnalyzedInfoCriteriumMap> dpAnalysedInfoCriteriaList) {
		this.dpAnalysedInfoCriteriaList = dpAnalysedInfoCriteriaList;
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
		return "DCsiMap [dpAnalysedInfoCriteriaList="
				+ dpAnalysedInfoCriteriaList + ", camelCapabilityHandling="
				+ camelCapabilityHandling + ", notificationToCse="
				+ notificationToCse + ", csiActive=" + csiActive + "]";
	}

}
