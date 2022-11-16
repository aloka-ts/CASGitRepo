package com.camel.enumData.bearerCapabilty;
/**
 * This enum represents Intermediate Rate.
 * @author vgoel
 */
public enum IntermediateRateEnum {
	
	/**
	 * 0- Not used
	 * 1- 8 kbit/s
	 * 2- 16 kbit/s
	 * 3- 32 kbit/s
	 */
	NOT_USED(0), KBITS_8(1), KBITS_16(2), KBITS_32(3);
	
	private int code;

	private IntermediateRateEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static IntermediateRateEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	KBITS_8	; }
		case 2: { return 	KBITS_16	; }
		case 3: { return 	KBITS_32	; }
		default: { return null; }
		}
	}
}
