package com.agnity.map.datatypes;

public class SsForBSCodeMap {
	// Mandatory 
	private SsCodeMap sscode;
	
	// optional parameter
	private BasicServiceCodeMap basicServiceCode;
	
	/**
	 * 
	 */
	public SsForBSCodeMap(SsCodeMap sscode) {
		this.sscode = sscode;
	}

	/**
	 * @return the sscode
	 */
	public SsCodeMap getSscode() {
		return sscode;
	}

	/**
	 * @param sscode the sscode to set
	 */
	public void setSscode(SsCodeMap sscode) {
		this.sscode = sscode;
	}

	/**
	 * @return the basicServiceCode
	 */
	public BasicServiceCodeMap getBasicServiceCode() {
		return basicServiceCode;
	}

	/**
	 * @param basicServiceCode the basicServiceCode to set
	 */
	public void setBasicServiceCode(BasicServiceCodeMap basicServiceCode) {
		this.basicServiceCode = basicServiceCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SsForBSCodeMap [sscode=" + sscode + ", basicServiceCode="
				+ basicServiceCode + "]";
	}
	
	
	
}
