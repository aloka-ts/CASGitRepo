package com.genband.inap.enumdata;

/**
 * Enum for End to End Information Indicator 
 * @author vgoel
 *
 */

public enum EndToEndInfoIndEnum {

	/**
	 * 0-no end-to-end information available
	 * 1-end-to-end information available
	 */
	NO_END_INFO_AVAILABLE(0), END_INFO_AVAILABLE(1);
	
	private int code;

	private EndToEndInfoIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EndToEndInfoIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_END_INFO_AVAILABLE; }
			case 1: { return END_INFO_AVAILABLE; }
			default: { return null; }
		}
	}
	
}
