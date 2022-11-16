package com.genband.inap.enumdata;

/**
 * Enum for Interworking indicator
 * @author vgoel
 *
 */

public enum InterNwIndEnum {
	
	/**
	 * 0-no interworking encountered (No. 7 signalling all the way)
	 * 1-interworking encountered	 
	 */
	NO_INTER_NW_ENC(0), INTER_NW_ENC(1);
	
	private int code;

	private InterNwIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InterNwIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_INTER_NW_ENC; }
			case 1: { return INTER_NW_ENC; }
			default: { return null; }
		}
	}
	

}
