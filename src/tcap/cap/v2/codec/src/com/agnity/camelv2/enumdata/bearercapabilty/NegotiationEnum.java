package com.agnity.camelv2.enumdata.bearercapabilty;
/**
 * This enum represents Negotiation.
 * @author vgoel
 */
public enum NegotiationEnum {
	
	/**
	 * 0-In-band negotiation not possible
	 * 1-In-band negotiation possible
	 */
	INBAND_NOT_POSSIBLE(0), INBAND_POSSIBLE(1);
	
	private int code;

	private NegotiationEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NegotiationEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	INBAND_NOT_POSSIBLE	; }
		case 1: { return 	INBAND_POSSIBLE	; }
		default: { return null; }
		}
	}
}
