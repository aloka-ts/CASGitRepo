package com.agnity.map.datatypes;

import java.util.Collection;

public class MsIsdnBsMap {
	// Mandatory attribute
	private ISDNAddressStringMap msisdn;
	
	// optional attribute
	private Collection<ExtBasicServiceCodeMap> basicServiceList;
	//TODO: ExtensionContainer

	/**
	 * @return the msisdn
	 */
	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn
	 */
	public MsIsdnBsMap(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return the basicServiceList
	 */
	public Collection<ExtBasicServiceCodeMap> getBasicServiceList() {
		return basicServiceList;
	}

	/**
	 * @param basicServiceList the basicServiceList to set
	 */
	public void setBasicServiceList(
			Collection<ExtBasicServiceCodeMap> basicServiceList) {
		this.basicServiceList = basicServiceList;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MsIsdnBsMap [msisdn=" + msisdn + ", basicServiceList="
				+ basicServiceList + "]";
	}
}
