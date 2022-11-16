package com.genband.inap.enumdata.bearercapability;
/**
 * This enum represents Number of stop bits.
 * @author vgoel
 */
public enum StopBitsEnum {
	
	/**
	 * 0- Not used
	 * 1- 1 bit
	 * 2- 1.5 bits
	 * 3- 2 bits
	 */
	NOT_USED(0), BIT_1(1), BIT_1_5(2), BIT_2(3);
	
	private int code;

	private StopBitsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static StopBitsEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	BIT_1	; }
		case 2: { return 	BIT_1_5	; }
		case 3: { return 	BIT_2	; }
		default: { return null; }
		}
	}
}
