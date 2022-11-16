package com.agnity.camelv2.enumdata.bearercapabilty;
/**
 * This enum represents Logical link identifier negotiation.
 * @author vgoel
 */
public enum LLINegotiationEnum {
	
	/**
	 * 0-Default LLI=256 only
	 * 1-Full protocol negotiation
	 */
	DEFAULT(0), FULL(1);
	
	private int code;

	private LLINegotiationEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static LLINegotiationEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	DEFAULT	; }
		case 1: { return 	FULL	; }
		default: { return null; }
		}
	}
}
