package com.agnity.cap.v2.datatypes.enumType;

public enum DuplexModeCapV2Enum {

	/**
	 * 0- Half duplex
	 * 1- Full duplex
	 */
	HALF_DUPLEX(0), FULL_DUPLEX(1);
	
	private int code;

	private DuplexModeCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static DuplexModeCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	HALF_DUPLEX	; }
		case 2: { return 	FULL_DUPLEX	; }		
		default: { return null; }
		}
	}
}
