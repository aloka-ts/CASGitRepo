package com.agnity.map.datatypes;

import org.apache.log4j.*;

public class AnyTimeModificationArgMap {
	
	// Mandatory parameters
	private SubscriberIdentityMap subIdentity;
	private ISDNAddressStringMap gsmScfAddr;

	// Optional parameters
	private ModificationRequestForCfInfoMap modRequestForCfInfo;
	private ModificationRequestForCbInfoMap modRequestForCbInfo;
	private ModificationRequestForCsiInfoMap modRequestForCSI;
	private ModificationRequestForOdbDataMap modRequestForOdbData;
	private ModificationRequestForIpSmGwDataMap modRequestForIpSmGwData;
	private RequestedServingNodeMap actRequestForUEReachability;
	private ModificationRequestForCsgMap modRequestForCSG;
	
	/**
	 * @param subIdentity
	 * @param gsmScfAddr
	 */
	public AnyTimeModificationArgMap(SubscriberIdentityMap subIdentity,
			ISDNAddressStringMap gsmScfAddr) {
		this.subIdentity = subIdentity;
		this.gsmScfAddr = gsmScfAddr;
	}
	/**
	 * @return the subIdentity
	 */
	public SubscriberIdentityMap getSubIdentity() {
		return subIdentity;
	}
	/**
	 * @return the gsmScfAddr
	 */
	public ISDNAddressStringMap getGsmScfAddr() {
		return gsmScfAddr;
	}
	/**
	 * @return the modRequestForCfInfo
	 */
	public ModificationRequestForCfInfoMap getModRequestForCfInfo() {
		return modRequestForCfInfo;
	}
	/**
	 * @return the modRequestForCbInfo
	 */
	public ModificationRequestForCbInfoMap getModRequestForCbInfo() {
		return modRequestForCbInfo;
	}
	/**
	 * @return the modRequestForCSI
	 */
	public ModificationRequestForCsiInfoMap getModRequestForCSI() {
		return modRequestForCSI;
	}
	/**
	 * @return the modRequestForOdbData
	 */
	public ModificationRequestForOdbDataMap getModRequestForOdbData() {
		return modRequestForOdbData;
	}
	/**
	 * @return the modRequestForIpSmGwData
	 */
	public ModificationRequestForIpSmGwDataMap getModRequestForIpSmGwData() {
		return modRequestForIpSmGwData;
	}
	/**
	 * @return the actRequestForUEReachability
	 */
	public RequestedServingNodeMap getActRequestForUEReachability() {
		return actRequestForUEReachability;
	}
	/**
	 * @return the modRequestForCSG
	 */
	public ModificationRequestForCsgMap getModRequestForCSG() {
		return modRequestForCSG;
	}
	/**
	 * @param subIdentity the subIdentity to set
	 */
	public void setSubIdentity(SubscriberIdentityMap subIdentity) {
		this.subIdentity = subIdentity;
	}
	/**
	 * @param gsmScfAddr the gsmScfAddr to set
	 */
	public void setGsmScfAddr(ISDNAddressStringMap gsmScfAddr) {
		this.gsmScfAddr = gsmScfAddr;
	}
	/**
	 * @param modRequestForCfInfo the modRequestForCfInfo to set
	 */
	public void setModRequestForCfInfo(
			ModificationRequestForCfInfoMap modRequestForCfInfo) {
		this.modRequestForCfInfo = modRequestForCfInfo;
	}
	/**
	 * @param modRequestForCbInfo the modRequestForCbInfo to set
	 */
	public void setModRequestForCbInfo(
			ModificationRequestForCbInfoMap modRequestForCbInfo) {
		this.modRequestForCbInfo = modRequestForCbInfo;
	}
	/**
	 * @param modRequestForCSI the modRequestForCSI to set
	 */
	public void setModRequestForCSI(
			ModificationRequestForCsiInfoMap modRequestForCSI) {
		this.modRequestForCSI = modRequestForCSI;
	}
	/**
	 * @param modRequestForOdbData the modRequestForOdbData to set
	 */
	public void setModRequestForOdbData(
			ModificationRequestForOdbDataMap modRequestForOdbData) {
		this.modRequestForOdbData = modRequestForOdbData;
	}
	/**
	 * @param modRequestForIpSmGwData the modRequestForIpSmGwData to set
	 */
	public void setModRequestForIpSmGwData(
			ModificationRequestForIpSmGwDataMap modRequestForIpSmGwData) {
		this.modRequestForIpSmGwData = modRequestForIpSmGwData;
	}
	/**
	 * @param actRequestForUEReachability the actRequestForUEReachability to set
	 */
	public void setActRequestForUEReachability(
			RequestedServingNodeMap actRequestForUEReachability) {
		this.actRequestForUEReachability = actRequestForUEReachability;
	}
	/**
	 * @param modRequestForCSG the modRequestForCSG to set
	 */
	public void setModRequestForCSG(ModificationRequestForCsgMap modRequestForCSG) {
		this.modRequestForCSG = modRequestForCSG;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnyTimeModificationArgMap [subIdentity=" + subIdentity
				+ ", gsmScfAddr=" + gsmScfAddr + ", modRequestForCfInfo="
				+ modRequestForCfInfo + ", modRequestForCbInfo="
				+ modRequestForCbInfo + ", modRequestForCSI="
				+ modRequestForCSI + ", modRequestForOdbData="
				+ modRequestForOdbData + ", modRequestForIpSmGwData="
				+ modRequestForIpSmGwData + ", actRequestForUEReachability="
				+ actRequestForUEReachability + ", modRequestForCSG="
				+ modRequestForCSG + "]";
	}
	
	
	
	
}
