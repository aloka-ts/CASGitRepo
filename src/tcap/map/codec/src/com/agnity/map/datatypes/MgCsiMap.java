package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.MmCodeMapEnum;

public class MgCsiMap {

	// Mandatory attributes
	private Collection<MmCodeMap> mobilityTriggers;
	private ServiceKeyMap serviceKey;
	private ISDNAddressStringMap gsmScfAddress;
	
	// optional attributes
	private boolean notificationToCse;
	private boolean csiActive;
	
	/**
	 * @param mobilityTriggers
	 * @param serviceKey
	 * @param gsmScfAddress
	 */
	public MgCsiMap(Collection<MmCodeMap> mobilityTriggers,
			ServiceKeyMap serviceKey, ISDNAddressStringMap gsmScfAddress) {
		this.mobilityTriggers = mobilityTriggers;
		this.serviceKey = serviceKey;
		this.gsmScfAddress = gsmScfAddress;
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
	// TODO: ExtensionContainer
	/**
	 * @return the mobilityTriggers
	 */
	public Collection<MmCodeMap> getMobilityTriggers() {
		return mobilityTriggers;
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
	 * @param mobilityTriggers the mobilityTriggers to set
	 */
	public void setMobilityTriggers(Collection<MmCodeMap> mobilityTriggers) {
		this.mobilityTriggers = mobilityTriggers;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MgCsiMap [mobilityTriggers=" + mobilityTriggers
				+ ", serviceKey=" + serviceKey + ", gsmScfAddress="
				+ gsmScfAddress + ", notificationToCse=" + notificationToCse
				+ ", csiActive=" + csiActive + "]";
	}

}
