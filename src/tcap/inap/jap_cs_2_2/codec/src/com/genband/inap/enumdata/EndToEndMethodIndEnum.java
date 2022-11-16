package com.genband.inap.enumdata;

/**
 * Enum for End to End Method Indicator 
 * @author vgoel
 *
 */

public enum EndToEndMethodIndEnum {

	/**
	 * 0-no end-to-end method available (only link-by-link method available)
	 * 1-pass-along method available (national use)
	 * 2-SCCP method available
	 * 3-pass-along and SCCP methods available (national use)
	 */
	NO_END_METHOD(0), PASS_ALONG_METHOD(1), SCCP_METHOD(2), PASS_ALONG_SCCP_METHOD(3);
	
	private int code;

	private EndToEndMethodIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EndToEndMethodIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_END_METHOD; }
			case 1: { return PASS_ALONG_METHOD; }
			case 2: { return SCCP_METHOD; }
			case 3: { return PASS_ALONG_SCCP_METHOD; }
			default: { return null; }
		}
	}
	
}
