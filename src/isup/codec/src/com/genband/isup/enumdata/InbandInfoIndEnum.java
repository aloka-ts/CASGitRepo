package com.genband.isup.enumdata;

/**
 * Enum for In-band infornation Indicators
 * @author vgoel
 *
 */
public enum InbandInfoIndEnum {

	/**
	 *  0-no indication
	 *  1-in-band information or an appropriate pattern is now available
	 */
	
	NO_INDICATION(0), INBAND_INFO(1);
	 
	private int code;

	private InbandInfoIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InbandInfoIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	INBAND_INFO	; }
		default: { return null; }
		}
	}
}
