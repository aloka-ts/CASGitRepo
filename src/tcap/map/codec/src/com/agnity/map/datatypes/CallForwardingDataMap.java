package com.agnity.map.datatypes;

import java.util.Collection;

public class CallForwardingDataMap {
	
	// Mandatory attribute
	private Collection<ExtForwFeatureMap> forwardingFeatureList;
	
	/**
	 *  TODO: 
	 *
	 * 1. notificationToCSE NULL OPTIONAL,
	 * 2. ExtensionContainer
	 */ 


	/**
	 * @return the forwardingFeatureList
	 */
	
	public Collection<ExtForwFeatureMap> getForwardingFeatureList() {
		return forwardingFeatureList;
	}

	/**
	 * @param forwardingFeatureList
	 */
	public CallForwardingDataMap(
			Collection<ExtForwFeatureMap> forwardingFeatureList) {
		this.forwardingFeatureList = forwardingFeatureList;
	}

	/**
	 * @param forwardingFeatureList the forwardingFeatureList to set
	 */
	public void setForwardingFeatureList(Collection<ExtForwFeatureMap> forwardingFeatureList) {
		this.forwardingFeatureList = forwardingFeatureList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CallForwardingDataMap [forwardingFeatureList="
				+ forwardingFeatureList + "]";
	}
}
