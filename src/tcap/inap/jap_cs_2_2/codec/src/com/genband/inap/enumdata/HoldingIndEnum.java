package com.genband.inap.enumdata;

/**
 * Enum for Holding Indicators
 * @author vgoel
 *
 */
public enum HoldingIndEnum {

	/**
	 *  0-Holding not requested
	 *  1-Holding requested
	 */
	
	HOLDING_NOT_REQUESTED(0), HOLDING_REQUESTED(1);
	 
	private int code;

	private HoldingIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static HoldingIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	HOLDING_NOT_REQUESTED	; }
		case 1: { return 	HOLDING_REQUESTED	; }
		default: { return null; }
		}
	}
}
