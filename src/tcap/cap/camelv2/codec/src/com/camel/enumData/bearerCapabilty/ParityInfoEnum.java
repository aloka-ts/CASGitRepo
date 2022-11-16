package com.camel.enumData.bearerCapabilty;
/**
 * This enum represents Parity Information.
 * @author vgoel
 */
public enum ParityInfoEnum {
	
	/**
	 * 0- Odd
	 * 2- Even
	 * 3- NONE
	 * 4- Forced to 0
	 * 5- Forced to 1
	 */
	ODD(0), EVEN(2), NONE(3), FORCED_0(4), FORCED_1(5);
	
	private int code;

	private ParityInfoEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ParityInfoEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	ODD	; }
		case 2: { return 	EVEN	; }
		case 3: { return 	NONE	; }
		case 4: { return 	FORCED_0	; }
		case 5: { return 	FORCED_1	; }		
		default: { return null; }
		}
	}
}
