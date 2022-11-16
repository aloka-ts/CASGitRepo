package com.camel.enumData.bearerCapabilty;
/**
 * This enum represents Number of data bits excluding parity bits if present.
 * @author vgoel
 */
public enum DataBitsEnum {
	
	/**
	 * 0- Not used
	 * 1- 5 bit
	 * 2- 7 bits
	 * 3- 8 bits
	 */
	NOT_USED(0), BIT_5(1), BIT_7(2), BIT_8(3);
	
	private int code;

	private DataBitsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static DataBitsEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	BIT_5	; }
		case 2: { return 	BIT_7	; }
		case 3: { return 	BIT_8	; }
		default: { return null; }
		}
	}
}
