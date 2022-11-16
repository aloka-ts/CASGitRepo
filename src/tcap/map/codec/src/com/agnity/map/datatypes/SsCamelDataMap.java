package com.agnity.map.datatypes;

import java.util.Collection;

public class SsCamelDataMap {
	private Collection<SsCodeMap> ssEventList;
	private ISDNAddressStringMap gsmScfAddress;
	//TODO: ExtensionContainer
	/**
	 * @param ssEventList
	 * @param gsmScfAddress
	 */
	public SsCamelDataMap(Collection<SsCodeMap> ssEventList,
			ISDNAddressStringMap gsmScfAddress) {
		this.ssEventList = ssEventList;
		this.gsmScfAddress = gsmScfAddress;
	}
	/**
	 * @return the ssEventList
	 */
	public Collection<SsCodeMap> getSsEventList() {
		return ssEventList;
	}
	/**
	 * @return the gsmScfAddress
	 */
	public ISDNAddressStringMap getGsmScfAddress() {
		return gsmScfAddress;
	}
	/**
	 * @param ssEventList the ssEventList to set
	 */
	public void setSsEventList(Collection<SsCodeMap> ssEventList) {
		this.ssEventList = ssEventList;
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
		return "SsCamelDataMap [ssEventList=" + ssEventList
				+ ", gsmScfAddress=" + gsmScfAddress + "]";
	}
	
	
}
