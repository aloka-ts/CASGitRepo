package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class SsCsiMap {
	// Mandatory attr
	private SsCamelDataMap ssCamelData;
	
	//optional attribute
	private boolean notificationToCse;
	private boolean csiActive;

	/**
	 * @param ssCamelData
	 */
	public SsCsiMap(SsCamelDataMap ssCamelData) {
		this.ssCamelData = ssCamelData;
	}

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
	 * @return the ssCamelData
	 */
	public SsCamelDataMap getSsCamelData() {
		return ssCamelData;
	}

	/**
	 * @param ssCamelData the ssCamelData to set
	 */
	public void setSsCamelData(SsCamelDataMap ssCamelData) {
		this.ssCamelData = ssCamelData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SsCsiMap [ssCamelData=" + ssCamelData + ", notificationToCse="
				+ notificationToCse + ", csiActive=" + csiActive + "]";
	}


}
