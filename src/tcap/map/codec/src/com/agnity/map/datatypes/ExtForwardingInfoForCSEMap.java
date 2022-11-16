package com.agnity.map.datatypes;

import java.util.Collection;

public class ExtForwardingInfoForCSEMap {
	// Mandatory attributes
	private SsCodeMap sscode;
	private Collection<ExtForwFeatureMap> forwardingFeatureList;
	
	// Optional attributes
	//TODO: ExtensionContainer
	
	/**
	 * @param sscode
	 * @param forwardingFeatureList
	 */
	public ExtForwardingInfoForCSEMap(SsCodeMap sscode,
			Collection<ExtForwFeatureMap> forwardingFeatureList) {
		this.sscode = sscode;
		this.forwardingFeatureList = forwardingFeatureList;
	}
	/**
	 * @return the sscode
	 */
	public SsCodeMap getSscode() {
		return sscode;
	}
	/**
	 * @return the forwardingFeatureList
	 */
	public Collection<ExtForwFeatureMap> getForwardingFeatureList() {
		return forwardingFeatureList;
	}
	/**
	 * @param sscode the sscode to set
	 */
	public void setSscode(SsCodeMap sscode) {
		this.sscode = sscode;
	}
	/**
	 * @param forwardingFeatureList the forwardingFeatureList to set
	 */
	public void setForwardingFeatureList(
			Collection<ExtForwFeatureMap> forwardingFeatureList) {
		this.forwardingFeatureList = forwardingFeatureList;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtForwardingInfoForCSEMap [sscode=" + sscode
				+ ", forwardingFeatureList=" + forwardingFeatureList + "]";
	}
	
	
}
