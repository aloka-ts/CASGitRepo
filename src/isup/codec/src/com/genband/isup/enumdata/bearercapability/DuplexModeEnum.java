package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Mode duplex.
 * @author vgoel
 */
public enum DuplexModeEnum {
	
	/**
	 * 0- Half duplex
	 * 1- Full duplex
	 */
	HALF_DUPLEX(0), FULL_DUPLEX(1);
	
	private int code;

	private DuplexModeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static DuplexModeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	HALF_DUPLEX	; }
		case 2: { return 	FULL_DUPLEX	; }		
		default: { return null; }
		}
	}
}
