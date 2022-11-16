package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents In-band/out-band negotiation.
 * @author vgoel
 */
public enum InbandOutbandNegotiationEnum {
	
	/**
	 * 0-Negotiation is done with user information messages on a temporary signaling connection
	 * 1-Negotiation is done in-band using logical link zero
	 */
	USER_INFO(0), IN_BAND(1);
	
	private int code;

	private InbandOutbandNegotiationEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InbandOutbandNegotiationEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	USER_INFO	; }
		case 1: { return 	IN_BAND	; }
		default: { return null; }
		}
	}
}
