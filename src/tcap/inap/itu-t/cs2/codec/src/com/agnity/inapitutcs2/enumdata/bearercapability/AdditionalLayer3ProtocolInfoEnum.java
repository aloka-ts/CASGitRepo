package com.agnity.inapitutcs2.enumdata.bearercapability;
/**
 * This enum represents additional layer 3 protocol information.
 * @author Mriganka
 */
public enum AdditionalLayer3ProtocolInfoEnum {
	
	/**
	 * 12-Internet protocol
	 * 15-Point-to-point protocol
	 */
	INTERNET_PROTOCOL(12), POINT_TO_POINT_PROTOCOL(15);
	
	private int code;

	private AdditionalLayer3ProtocolInfoEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdditionalLayer3ProtocolInfoEnum fromInt(int num) {
		switch (num) {
		case 12: { return 	INTERNET_PROTOCOL	; }
		case 15: { return 	POINT_TO_POINT_PROTOCOL	; }
		default: { return null; }
		}
	}
}
